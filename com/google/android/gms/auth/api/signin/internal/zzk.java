package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public class zzk
{
  private static final Lock hI = new ReentrantLock();
  private static zzk hJ;
  private final Lock hK = new ReentrantLock();
  private final SharedPreferences hL;
  
  zzk(Context paramContext)
  {
    this.hL = paramContext.getSharedPreferences("com.google.android.gms.signin", 0);
  }
  
  public static zzk zzbd(Context paramContext)
  {
    zzac.zzy(paramContext);
    hI.lock();
    try
    {
      if (hJ == null) {
        hJ = new zzk(paramContext.getApplicationContext());
      }
      paramContext = hJ;
      return paramContext;
    }
    finally
    {
      hI.unlock();
    }
  }
  
  private String zzy(String paramString1, String paramString2)
  {
    String str = String.valueOf(":");
    return String.valueOf(paramString1).length() + 0 + String.valueOf(str).length() + String.valueOf(paramString2).length() + paramString1 + str + paramString2;
  }
  
  void zza(GoogleSignInAccount paramGoogleSignInAccount, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzac.zzy(paramGoogleSignInAccount);
    zzac.zzy(paramGoogleSignInOptions);
    String str = paramGoogleSignInAccount.zzahf();
    zzx(zzy("googleSignInAccount", str), paramGoogleSignInAccount.zzahh());
    zzx(zzy("googleSignInOptions", str), paramGoogleSignInOptions.zzahg());
  }
  
  public GoogleSignInAccount zzaic()
  {
    return zzga(zzgc("defaultGoogleSignInAccount"));
  }
  
  public GoogleSignInOptions zzaid()
  {
    return zzgb(zzgc("defaultGoogleSignInAccount"));
  }
  
  public void zzaie()
  {
    String str = zzgc("defaultGoogleSignInAccount");
    zzge("defaultGoogleSignInAccount");
    zzgd(str);
  }
  
  public void zzb(GoogleSignInAccount paramGoogleSignInAccount, GoogleSignInOptions paramGoogleSignInOptions)
  {
    zzac.zzy(paramGoogleSignInAccount);
    zzac.zzy(paramGoogleSignInOptions);
    zzx("defaultGoogleSignInAccount", paramGoogleSignInAccount.zzahf());
    zza(paramGoogleSignInAccount, paramGoogleSignInOptions);
  }
  
  GoogleSignInAccount zzga(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    do
    {
      return null;
      paramString = zzgc(zzy("googleSignInAccount", paramString));
    } while (paramString == null);
    try
    {
      paramString = GoogleSignInAccount.zzfw(paramString);
      return paramString;
    }
    catch (JSONException paramString) {}
    return null;
  }
  
  GoogleSignInOptions zzgb(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    do
    {
      return null;
      paramString = zzgc(zzy("googleSignInOptions", paramString));
    } while (paramString == null);
    try
    {
      paramString = GoogleSignInOptions.zzfy(paramString);
      return paramString;
    }
    catch (JSONException paramString) {}
    return null;
  }
  
  protected String zzgc(String paramString)
  {
    this.hK.lock();
    try
    {
      paramString = this.hL.getString(paramString, null);
      return paramString;
    }
    finally
    {
      this.hK.unlock();
    }
  }
  
  void zzgd(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return;
    }
    zzge(zzy("googleSignInAccount", paramString));
    zzge(zzy("googleSignInOptions", paramString));
  }
  
  protected void zzge(String paramString)
  {
    this.hK.lock();
    try
    {
      this.hL.edit().remove(paramString).apply();
      return;
    }
    finally
    {
      this.hK.unlock();
    }
  }
  
  protected void zzx(String paramString1, String paramString2)
  {
    this.hK.lock();
    try
    {
      this.hL.edit().putString(paramString1, paramString2).apply();
      return;
    }
    finally
    {
      this.hK.unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/internal/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */