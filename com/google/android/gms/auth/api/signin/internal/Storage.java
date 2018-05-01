package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import org.json.JSONException;

public class Storage
{
  private static final Lock zzaf = new ReentrantLock();
  @GuardedBy("sLk")
  private static Storage zzag;
  private final Lock zzah = new ReentrantLock();
  @GuardedBy("mLk")
  private final SharedPreferences zzai;
  
  private Storage(Context paramContext)
  {
    this.zzai = paramContext.getSharedPreferences("com.google.android.gms.signin", 0);
  }
  
  public static Storage getInstance(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    zzaf.lock();
    try
    {
      if (zzag == null)
      {
        Storage localStorage = new com/google/android/gms/auth/api/signin/internal/Storage;
        localStorage.<init>(paramContext.getApplicationContext());
        zzag = localStorage;
      }
      paramContext = zzag;
      return paramContext;
    }
    finally
    {
      zzaf.unlock();
    }
  }
  
  private static String zza(String paramString1, String paramString2)
  {
    return String.valueOf(paramString1).length() + 1 + String.valueOf(paramString2).length() + paramString1 + ":" + paramString2;
  }
  
  @Nullable
  private final GoogleSignInAccount zzb(String paramString)
  {
    Object localObject = null;
    if (TextUtils.isEmpty(paramString)) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      String str = getFromStore(zza("googleSignInAccount", paramString));
      paramString = (String)localObject;
      if (str != null) {
        try
        {
          paramString = GoogleSignInAccount.fromJsonString(str);
        }
        catch (JSONException paramString)
        {
          paramString = (String)localObject;
        }
      }
    }
  }
  
  @Nullable
  protected String getFromStore(String paramString)
  {
    this.zzah.lock();
    try
    {
      paramString = this.zzai.getString(paramString, null);
      return paramString;
    }
    finally
    {
      this.zzah.unlock();
    }
  }
  
  @Nullable
  public GoogleSignInAccount getSavedDefaultGoogleSignInAccount()
  {
    return zzb(getFromStore("defaultGoogleSignInAccount"));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/internal/Storage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */