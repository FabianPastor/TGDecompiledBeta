package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public final class zzz
{
  private static final Lock zzeiu = new ReentrantLock();
  private static zzz zzeiv;
  private final Lock zzeiw = new ReentrantLock();
  private final SharedPreferences zzeix;
  
  private zzz(Context paramContext)
  {
    this.zzeix = paramContext.getSharedPreferences("com.google.android.gms.signin", 0);
  }
  
  public static zzz zzbt(Context paramContext)
  {
    zzbq.checkNotNull(paramContext);
    zzeiu.lock();
    try
    {
      if (zzeiv == null) {
        zzeiv = new zzz(paramContext.getApplicationContext());
      }
      paramContext = zzeiv;
      return paramContext;
    }
    finally
    {
      zzeiu.unlock();
    }
  }
  
  private final GoogleSignInAccount zzex(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    do
    {
      return null;
      paramString = zzez(zzp("googleSignInAccount", paramString));
    } while (paramString == null);
    try
    {
      paramString = GoogleSignInAccount.zzeu(paramString);
      return paramString;
    }
    catch (JSONException paramString) {}
    return null;
  }
  
  private final String zzez(String paramString)
  {
    this.zzeiw.lock();
    try
    {
      paramString = this.zzeix.getString(paramString, null);
      return paramString;
    }
    finally
    {
      this.zzeiw.unlock();
    }
  }
  
  private static String zzp(String paramString1, String paramString2)
  {
    return String.valueOf(paramString1).length() + String.valueOf(":").length() + String.valueOf(paramString2).length() + paramString1 + ":" + paramString2;
  }
  
  public final GoogleSignInAccount zzabt()
  {
    return zzex(zzez("defaultGoogleSignInAccount"));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/internal/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */