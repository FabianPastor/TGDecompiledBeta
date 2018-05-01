package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public final class zzy
{
  private static final Lock zzamD = new ReentrantLock();
  private static zzy zzamE;
  private final Lock zzamF = new ReentrantLock();
  private final SharedPreferences zzamG;
  
  private zzy(Context paramContext)
  {
    this.zzamG = paramContext.getSharedPreferences("com.google.android.gms.signin", 0);
  }
  
  public static zzy zzaj(Context paramContext)
  {
    zzbo.zzu(paramContext);
    zzamD.lock();
    try
    {
      if (zzamE == null) {
        zzamE = new zzy(paramContext.getApplicationContext());
      }
      paramContext = zzamE;
      return paramContext;
    }
    finally
    {
      zzamD.unlock();
    }
  }
  
  private final GoogleSignInAccount zzbS(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    do
    {
      return null;
      paramString = zzbU(zzs("googleSignInAccount", paramString));
    } while (paramString == null);
    try
    {
      paramString = GoogleSignInAccount.zzbP(paramString);
      return paramString;
    }
    catch (JSONException paramString) {}
    return null;
  }
  
  private final GoogleSignInOptions zzbT(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    do
    {
      return null;
      paramString = zzbU(zzs("googleSignInOptions", paramString));
    } while (paramString == null);
    try
    {
      paramString = GoogleSignInOptions.zzbQ(paramString);
      return paramString;
    }
    catch (JSONException paramString) {}
    return null;
  }
  
  private final String zzbU(String paramString)
  {
    this.zzamF.lock();
    try
    {
      paramString = this.zzamG.getString(paramString, null);
      return paramString;
    }
    finally
    {
      this.zzamF.unlock();
    }
  }
  
  private final void zzbV(String paramString)
  {
    this.zzamF.lock();
    try
    {
      this.zzamG.edit().remove(paramString).apply();
      return;
    }
    finally
    {
      this.zzamF.unlock();
    }
  }
  
  private final void zzr(String paramString1, String paramString2)
  {
    this.zzamF.lock();
    try
    {
      this.zzamG.edit().putString(paramString1, paramString2).apply();
      return;
    }
    finally
    {
      this.zzamF.unlock();
    }
  }
  
  private static String zzs(String paramString1, String paramString2)
  {
    String str = String.valueOf(":");
    return String.valueOf(paramString1).length() + String.valueOf(str).length() + String.valueOf(paramString2).length() + paramString1 + str + paramString2;
  }
  
  public final void zza(GoogleSignInAccount paramGoogleSignInAccount, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzbo.zzu(paramGoogleSignInAccount);
    zzbo.zzu(paramGoogleSignInOptions);
    zzr("defaultGoogleSignInAccount", paramGoogleSignInAccount.zzmx());
    zzbo.zzu(paramGoogleSignInAccount);
    zzbo.zzu(paramGoogleSignInOptions);
    String str = paramGoogleSignInAccount.zzmx();
    zzr(zzs("googleSignInAccount", str), paramGoogleSignInAccount.zzmy());
    zzr(zzs("googleSignInOptions", str), paramGoogleSignInOptions.zzmC());
  }
  
  public final GoogleSignInAccount zzmN()
  {
    return zzbS(zzbU("defaultGoogleSignInAccount"));
  }
  
  public final GoogleSignInOptions zzmO()
  {
    return zzbT(zzbU("defaultGoogleSignInAccount"));
  }
  
  public final void zzmP()
  {
    String str = zzbU("defaultGoogleSignInAccount");
    zzbV("defaultGoogleSignInAccount");
    if (!TextUtils.isEmpty(str))
    {
      zzbV(zzs("googleSignInAccount", str));
      zzbV(zzs("googleSignInOptions", str));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */