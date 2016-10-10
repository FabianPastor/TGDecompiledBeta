package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Map;

public class FirebaseInstanceId
{
  private static Map<String, FirebaseInstanceId> afS = new ArrayMap();
  private static zze bho;
  private final FirebaseApp bhp;
  private final zzd bhq;
  private final String bhr;
  
  private FirebaseInstanceId(FirebaseApp paramFirebaseApp, zzd paramzzd)
  {
    this.bhp = paramFirebaseApp;
    this.bhq = paramzzd;
    this.bhr = B();
    if (this.bhr == null) {
      throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
    }
    FirebaseInstanceIdService.zza(this.bhp.getApplicationContext(), this);
  }
  
  public static FirebaseInstanceId getInstance()
  {
    return getInstance(FirebaseApp.getInstance());
  }
  
  @Keep
  public static FirebaseInstanceId getInstance(@NonNull FirebaseApp paramFirebaseApp)
  {
    try
    {
      FirebaseInstanceId localFirebaseInstanceId = (FirebaseInstanceId)afS.get(paramFirebaseApp.getOptions().getApplicationId());
      Object localObject = localFirebaseInstanceId;
      if (localFirebaseInstanceId == null)
      {
        localObject = zzd.zzb(paramFirebaseApp.getApplicationContext(), null);
        if (bho == null) {
          bho = new zze(((zzd)localObject).H());
        }
        localObject = new FirebaseInstanceId(paramFirebaseApp, (zzd)localObject);
        afS.put(paramFirebaseApp.getOptions().getApplicationId(), localObject);
      }
      return (FirebaseInstanceId)localObject;
    }
    finally {}
  }
  
  static String zza(KeyPair paramKeyPair)
  {
    paramKeyPair = paramKeyPair.getPublic().getEncoded();
    try
    {
      paramKeyPair = MessageDigest.getInstance("SHA1").digest(paramKeyPair);
      paramKeyPair[0] = ((byte)((paramKeyPair[0] & 0xF) + 112 & 0xFF));
      paramKeyPair = Base64.encodeToString(paramKeyPair, 0, 8, 11);
      return paramKeyPair;
    }
    catch (NoSuchAlgorithmException paramKeyPair)
    {
      Log.w("FirebaseInstanceId", "Unexpected error, device missing required alghorithms");
    }
    return null;
  }
  
  static void zza(Context paramContext, zzg paramzzg)
  {
    paramzzg.zzbow();
    paramzzg = new Intent();
    paramzzg.putExtra("CMD", "RST");
    paramContext.sendBroadcast(FirebaseInstanceIdInternalReceiver.zzg(paramContext, paramzzg));
  }
  
  static int zzdg(Context paramContext)
  {
    try
    {
      int i = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("FirebaseInstanceId", String.valueOf(paramContext).length() + 38 + "Never happens: can't find own package " + paramContext);
    }
    return 0;
  }
  
  static String zzdh(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("FirebaseInstanceId", String.valueOf(paramContext).length() + 38 + "Never happens: can't find own package " + paramContext);
    }
    return null;
  }
  
  static void zzdi(Context paramContext)
  {
    Intent localIntent = new Intent();
    localIntent.setPackage(paramContext.getPackageName());
    localIntent.putExtra("CMD", "SYNC");
    paramContext.sendBroadcast(FirebaseInstanceIdInternalReceiver.zzg(paramContext, localIntent));
  }
  
  static String zzes(Context paramContext)
  {
    return getInstance().bhp.getOptions().getApplicationId();
  }
  
  static String zzu(byte[] paramArrayOfByte)
  {
    return Base64.encodeToString(paramArrayOfByte, 11);
  }
  
  String B()
  {
    Object localObject = this.bhp.getOptions().getGcmSenderId();
    if (localObject != null) {}
    String str;
    do
    {
      do
      {
        return (String)localObject;
        str = this.bhp.getOptions().getApplicationId();
        localObject = str;
      } while (!str.startsWith("1:"));
      localObject = str.split(":");
      if (localObject.length < 2) {
        return null;
      }
      str = localObject[1];
      localObject = str;
    } while (!str.isEmpty());
    return null;
  }
  
  @Nullable
  zzg.zza C()
  {
    return this.bhq.H().zzq("", this.bhr, "*");
  }
  
  String D()
    throws IOException
  {
    return getToken(this.bhr, "*");
  }
  
  zze E()
  {
    return bho;
  }
  
  public void deleteInstanceId()
    throws IOException
  {
    this.bhq.zzb("*", "*", null);
    this.bhq.zzboq();
  }
  
  @WorkerThread
  public void deleteToken(String paramString1, String paramString2)
    throws IOException
  {
    this.bhq.zzb(paramString1, paramString2, null);
  }
  
  public long getCreationTime()
  {
    return this.bhq.getCreationTime();
  }
  
  public String getId()
  {
    return zza(this.bhq.zzbop());
  }
  
  @Nullable
  public String getToken()
  {
    zzg.zza localzza = C();
    if ((localzza == null) || (localzza.zztz(zzd.afY))) {
      FirebaseInstanceIdService.zzet(this.bhp.getApplicationContext());
    }
    if (localzza != null) {
      return localzza.auj;
    }
    return null;
  }
  
  @WorkerThread
  public String getToken(String paramString1, String paramString2)
    throws IOException
  {
    return this.bhq.getToken(paramString1, paramString2, null);
  }
  
  public void zztr(String paramString)
  {
    bho.zztr(paramString);
    FirebaseInstanceIdService.zzet(this.bhp.getApplicationContext());
  }
  
  void zzts(String paramString)
    throws IOException
  {
    if (getToken() == null) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    Object localObject = String.valueOf("/topics/");
    String str1 = String.valueOf(paramString);
    String str2;
    if (str1.length() != 0)
    {
      localObject = ((String)localObject).concat(str1);
      localBundle.putString("gcm.topic", (String)localObject);
      localObject = this.bhq;
      str1 = getToken();
      str2 = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label122;
      }
    }
    label122:
    for (paramString = str2.concat(paramString);; paramString = new String(str2))
    {
      ((zzd)localObject).getToken(str1, paramString, localBundle);
      return;
      localObject = new String((String)localObject);
      break;
    }
  }
  
  void zztt(String paramString)
    throws IOException
  {
    if (getToken() == null) {
      throw new IOException("token not available");
    }
    Bundle localBundle = new Bundle();
    Object localObject = String.valueOf("/topics/");
    String str1 = String.valueOf(paramString);
    String str2;
    if (str1.length() != 0)
    {
      localObject = ((String)localObject).concat(str1);
      localBundle.putString("gcm.topic", (String)localObject);
      localObject = this.bhq;
      str1 = getToken();
      str2 = String.valueOf("/topics/");
      paramString = String.valueOf(paramString);
      if (paramString.length() == 0) {
        break label121;
      }
    }
    label121:
    for (paramString = str2.concat(paramString);; paramString = new String(str2))
    {
      ((zzd)localObject).zzb(str1, paramString, localBundle);
      return;
      localObject = new String((String)localObject);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */