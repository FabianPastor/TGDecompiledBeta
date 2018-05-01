package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzv;
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

public final class zzo
{
  private Context zzair;
  private SharedPreferences zzige;
  
  public zzo(Context paramContext)
  {
    this(paramContext, "com.google.android.gms.appid");
  }
  
  private zzo(Context paramContext, String paramString)
  {
    this.zzair = paramContext;
    this.zzige = paramContext.getSharedPreferences(paramString, 0);
    paramContext = String.valueOf(paramString);
    paramString = String.valueOf("-no-backup");
    if (paramString.length() != 0) {
      paramContext = paramContext.concat(paramString);
    }
    for (;;)
    {
      paramContext = new File(zzv.getNoBackupFilesDir(this.zzair), paramContext);
      if (!paramContext.exists()) {}
      try
      {
        if ((paramContext.createNewFile()) && (!isEmpty()))
        {
          Log.i("InstanceID/Store", "App restored, clearing state");
          InstanceIDListenerService.zza(this.zzair, this);
        }
        return;
        paramContext = new String(paramContext);
      }
      catch (IOException paramContext)
      {
        while (!Log.isLoggable("InstanceID/Store", 3)) {}
        paramContext = String.valueOf(paramContext.getMessage());
        if (paramContext.length() == 0) {}
      }
    }
    for (paramContext = "Error creating file in no backup dir: ".concat(paramContext);; paramContext = new String("Error creating file in no backup dir: "))
    {
      Log.d("InstanceID/Store", paramContext);
      return;
    }
  }
  
  private final void zza(SharedPreferences.Editor paramEditor, String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramEditor.putString(String.valueOf(paramString1).length() + String.valueOf("|S|").length() + String.valueOf(paramString2).length() + paramString1 + "|S|" + paramString2, paramString3);
      return;
    }
    finally
    {
      paramEditor = finally;
      throw paramEditor;
    }
  }
  
  private static String zzd(String paramString1, String paramString2, String paramString3)
  {
    return String.valueOf(paramString1).length() + 1 + String.valueOf("|T|").length() + String.valueOf(paramString2).length() + String.valueOf(paramString3).length() + paramString1 + "|T|" + paramString2 + "|" + paramString3;
  }
  
  final String get(String paramString)
  {
    try
    {
      paramString = this.zzige.getString(paramString, null);
      return paramString;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  final String get(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = this.zzige.getString(String.valueOf(paramString1).length() + String.valueOf("|S|").length() + String.valueOf(paramString2).length() + paramString1 + "|S|" + paramString2, null);
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public final boolean isEmpty()
  {
    return this.zzige.getAll().isEmpty();
  }
  
  public final void zza(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      paramString1 = zzd(paramString1, paramString2, paramString3);
      paramString2 = this.zzige.edit();
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
  
  public final void zzavj()
  {
    try
    {
      this.zzige.edit().clear().commit();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final KeyPair zzc(String paramString, long paramLong)
  {
    try
    {
      KeyPair localKeyPair = zza.zzavc();
      SharedPreferences.Editor localEditor = this.zzige.edit();
      zza(localEditor, paramString, "|P|", InstanceID.zzo(localKeyPair.getPublic().getEncoded()));
      zza(localEditor, paramString, "|K|", InstanceID.zzo(localKeyPair.getPrivate().getEncoded()));
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
  
  public final String zze(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zzd(paramString1, paramString2, paramString3);
      paramString1 = this.zzige.getString(paramString1, null);
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public final void zzhz(String paramString)
  {
    try
    {
      SharedPreferences.Editor localEditor = this.zzige.edit();
      Iterator localIterator = this.zzige.getAll().keySet().iterator();
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
  
  public final void zzia(String paramString)
  {
    zzhz(String.valueOf(paramString).concat("|T|"));
  }
  
  final KeyPair zzib(String paramString)
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
      InstanceIDListenerService.zza(this.zzair, this);
      return null;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */