package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzx;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzd
{
  SharedPreferences agw;
  Context zzahn;
  
  public zzd(Context paramContext)
  {
    this(paramContext, "com.google.android.gms.appid");
  }
  
  public zzd(Context paramContext, String paramString)
  {
    this.zzahn = paramContext;
    this.agw = paramContext.getSharedPreferences(paramString, 4);
    paramContext = String.valueOf(paramString);
    paramString = String.valueOf("-no-backup");
    if (paramString.length() != 0) {}
    for (paramContext = paramContext.concat(paramString);; paramContext = new String(paramContext))
    {
      zzkq(paramContext);
      return;
    }
  }
  
  private String zzg(String paramString1, String paramString2, String paramString3)
  {
    String str = String.valueOf("|T|");
    return String.valueOf(paramString1).length() + 1 + String.valueOf(str).length() + String.valueOf(paramString2).length() + String.valueOf(paramString3).length() + paramString1 + str + paramString2 + "|" + paramString3;
  }
  
  private void zzkq(String paramString)
  {
    paramString = new File(zzx.getNoBackupFilesDir(this.zzahn), paramString);
    if (paramString.exists()) {}
    do
    {
      for (;;)
      {
        return;
        try
        {
          if ((paramString.createNewFile()) && (!isEmpty()))
          {
            Log.i("InstanceID/Store", "App restored, clearing state");
            InstanceIDListenerService.zza(this.zzahn, this);
            return;
          }
        }
        catch (IOException paramString) {}
      }
    } while (!Log.isLoggable("InstanceID/Store", 3));
    paramString = String.valueOf(paramString.getMessage());
    if (paramString.length() != 0) {}
    for (paramString = "Error creating file in no backup dir: ".concat(paramString);; paramString = new String("Error creating file in no backup dir: "))
    {
      Log.d("InstanceID/Store", paramString);
      return;
    }
  }
  
  String get(String paramString)
  {
    try
    {
      paramString = this.agw.getString(paramString, null);
      return paramString;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  String get(String paramString1, String paramString2)
  {
    try
    {
      SharedPreferences localSharedPreferences = this.agw;
      String str = String.valueOf("|S|");
      paramString1 = localSharedPreferences.getString(String.valueOf(paramString1).length() + 0 + String.valueOf(str).length() + String.valueOf(paramString2).length() + paramString1 + str + paramString2, null);
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public boolean isEmpty()
  {
    return this.agw.getAll().isEmpty();
  }
  
  void zza(SharedPreferences.Editor paramEditor, String paramString1, String paramString2, String paramString3)
  {
    try
    {
      String str = String.valueOf("|S|");
      paramEditor.putString(String.valueOf(paramString1).length() + 0 + String.valueOf(str).length() + String.valueOf(paramString2).length() + paramString1 + str + paramString2, paramString3);
      return;
    }
    finally
    {
      paramEditor = finally;
      throw paramEditor;
    }
  }
  
  public void zza(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      paramString1 = zzg(paramString1, paramString2, paramString3);
      paramString2 = this.agw.edit();
      paramString2.putString(paramString1, paramString4);
      paramString2.putString("appVersion", paramString5);
      paramString2.putString("lastToken", Long.toString(System.currentTimeMillis() / 1000L));
      paramString2.commit();
      return;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public void zzbow()
  {
    try
    {
      this.agw.edit().clear().commit();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  KeyPair zze(String paramString, long paramLong)
  {
    try
    {
      KeyPair localKeyPair = zza.zzboo();
      SharedPreferences.Editor localEditor = this.agw.edit();
      zza(localEditor, paramString, "|P|", InstanceID.zzu(localKeyPair.getPublic().getEncoded()));
      zza(localEditor, paramString, "|K|", InstanceID.zzu(localKeyPair.getPrivate().getEncoded()));
      zza(localEditor, paramString, "cre", Long.toString(paramLong));
      localEditor.commit();
      return localKeyPair;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public String zzh(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zzg(paramString1, paramString2, paramString3);
      paramString1 = this.agw.getString(paramString1, null);
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public void zzi(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zzg(paramString1, paramString2, paramString3);
      paramString2 = this.agw.edit();
      paramString2.remove(paramString1);
      paramString2.commit();
      return;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public void zzkr(String paramString)
  {
    try
    {
      SharedPreferences.Editor localEditor = this.agw.edit();
      Iterator localIterator = this.agw.getAll().keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.startsWith(paramString)) {
          localEditor.remove(str);
        }
      }
      localEditor.commit();
    }
    finally {}
  }
  
  public KeyPair zzks(String paramString)
  {
    return zzkv(paramString);
  }
  
  void zzkt(String paramString)
  {
    zzkr(String.valueOf(paramString).concat("|"));
  }
  
  public void zzku(String paramString)
  {
    zzkr(String.valueOf(paramString).concat("|T|"));
  }
  
  KeyPair zzkv(String paramString)
  {
    Object localObject1 = get(paramString, "|P|");
    Object localObject2 = get(paramString, "|K|");
    if ((localObject1 == null) || (localObject2 == null)) {
      return null;
    }
    try
    {
      paramString = Base64.decode((String)localObject1, 8);
      localObject1 = Base64.decode((String)localObject2, 8);
      localObject2 = KeyFactory.getInstance("RSA");
      paramString = new KeyPair(((KeyFactory)localObject2).generatePublic(new X509EncodedKeySpec(paramString)), ((KeyFactory)localObject2).generatePrivate(new PKCS8EncodedKeySpec((byte[])localObject1)));
      return paramString;
    }
    catch (InvalidKeySpecException paramString)
    {
      paramString = String.valueOf(paramString);
      Log.w("InstanceID/Store", String.valueOf(paramString).length() + 19 + "Invalid key stored " + paramString);
      InstanceIDListenerService.zza(this.zzahn, this);
      return null;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */