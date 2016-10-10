package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzx;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

class zzg
{
  SharedPreferences agw;
  Context zzahn;
  
  public zzg(Context paramContext)
  {
    this(paramContext, "com.google.android.gms.appid");
  }
  
  public zzg(Context paramContext, String paramString)
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
  
  private String zzbu(String paramString1, String paramString2)
  {
    String str = String.valueOf("|S|");
    return String.valueOf(paramString1).length() + 0 + String.valueOf(str).length() + String.valueOf(paramString2).length() + paramString1 + str + paramString2;
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
            FirebaseInstanceId.zza(this.zzahn, this);
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
  
  private void zzkr(String paramString)
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
  
  private String zzp(String paramString1, String paramString2, String paramString3)
  {
    String str = String.valueOf("|T|");
    return String.valueOf(paramString1).length() + 1 + String.valueOf(str).length() + String.valueOf(paramString2).length() + String.valueOf(paramString3).length() + paramString1 + str + paramString2 + "|" + paramString3;
  }
  
  public SharedPreferences K()
  {
    return this.agw;
  }
  
  public boolean isEmpty()
  {
    try
    {
      boolean bool = this.agw.getAll().isEmpty();
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void zza(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload 4
    //   4: aload 5
    //   6: invokestatic 182	java/lang/System:currentTimeMillis	()J
    //   9: invokestatic 186	com/google/firebase/iid/zzg$zza:zzc	(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;
    //   12: astore 4
    //   14: aload 4
    //   16: ifnonnull +6 -> 22
    //   19: aload_0
    //   20: monitorexit
    //   21: return
    //   22: aload_0
    //   23: getfield 32	com/google/firebase/iid/zzg:agw	Landroid/content/SharedPreferences;
    //   26: invokeinterface 128 1 0
    //   31: astore 5
    //   33: aload 5
    //   35: aload_0
    //   36: aload_1
    //   37: aload_2
    //   38: aload_3
    //   39: invokespecial 188	com/google/firebase/iid/zzg:zzp	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   42: aload 4
    //   44: invokeinterface 192 3 0
    //   49: pop
    //   50: aload 5
    //   52: invokeinterface 166 1 0
    //   57: pop
    //   58: goto -39 -> 19
    //   61: astore_1
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_1
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	zzg
    //   0	66	1	paramString1	String
    //   0	66	2	paramString2	String
    //   0	66	3	paramString3	String
    //   0	66	4	paramString4	String
    //   0	66	5	paramString5	String
    // Exception table:
    //   from	to	target	type
    //   2	14	61	finally
    //   22	58	61	finally
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
  
  public void zzi(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zzp(paramString1, paramString2, paramString3);
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
  
  /* Error */
  public KeyPair zzks(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 32	com/google/firebase/iid/zzg:agw	Landroid/content/SharedPreferences;
    //   6: aload_0
    //   7: aload_1
    //   8: ldc -50
    //   10: invokespecial 208	com/google/firebase/iid/zzg:zzbu	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   13: aconst_null
    //   14: invokeinterface 211 3 0
    //   19: astore_2
    //   20: aload_0
    //   21: getfield 32	com/google/firebase/iid/zzg:agw	Landroid/content/SharedPreferences;
    //   24: aload_0
    //   25: aload_1
    //   26: ldc -43
    //   28: invokespecial 208	com/google/firebase/iid/zzg:zzbu	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   31: aconst_null
    //   32: invokeinterface 211 3 0
    //   37: astore_3
    //   38: aload_2
    //   39: ifnull +7 -> 46
    //   42: aload_3
    //   43: ifnonnull +9 -> 52
    //   46: aconst_null
    //   47: astore_1
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_1
    //   51: areturn
    //   52: aload_2
    //   53: bipush 8
    //   55: invokestatic 219	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   58: astore_1
    //   59: aload_3
    //   60: bipush 8
    //   62: invokestatic 219	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   65: astore_2
    //   66: ldc -35
    //   68: invokestatic 227	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   71: astore_3
    //   72: new 229	java/security/KeyPair
    //   75: dup
    //   76: aload_3
    //   77: new 231	java/security/spec/X509EncodedKeySpec
    //   80: dup
    //   81: aload_1
    //   82: invokespecial 234	java/security/spec/X509EncodedKeySpec:<init>	([B)V
    //   85: invokevirtual 238	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   88: aload_3
    //   89: new 240	java/security/spec/PKCS8EncodedKeySpec
    //   92: dup
    //   93: aload_2
    //   94: invokespecial 241	java/security/spec/PKCS8EncodedKeySpec:<init>	([B)V
    //   97: invokevirtual 245	java/security/KeyFactory:generatePrivate	(Ljava/security/spec/KeySpec;)Ljava/security/PrivateKey;
    //   100: invokespecial 248	java/security/KeyPair:<init>	(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
    //   103: astore_1
    //   104: goto -56 -> 48
    //   107: astore_1
    //   108: aload_1
    //   109: invokestatic 38	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   112: astore_1
    //   113: ldc 96
    //   115: new 60	java/lang/StringBuilder
    //   118: dup
    //   119: aload_1
    //   120: invokestatic 38	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   123: invokevirtual 44	java/lang/String:length	()I
    //   126: bipush 19
    //   128: iadd
    //   129: invokespecial 63	java/lang/StringBuilder:<init>	(I)V
    //   132: ldc -6
    //   134: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: aload_1
    //   138: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: invokevirtual 71	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   144: invokestatic 253	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   147: pop
    //   148: aload_0
    //   149: getfield 24	com/google/firebase/iid/zzg:zzahn	Landroid/content/Context;
    //   152: aload_0
    //   153: invokestatic 109	com/google/firebase/iid/FirebaseInstanceId:zza	(Landroid/content/Context;Lcom/google/firebase/iid/zzg;)V
    //   156: aconst_null
    //   157: astore_1
    //   158: goto -110 -> 48
    //   161: astore_1
    //   162: aload_0
    //   163: monitorexit
    //   164: aload_1
    //   165: athrow
    //   166: astore_1
    //   167: goto -59 -> 108
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	zzg
    //   0	170	1	paramString	String
    //   19	75	2	localObject1	Object
    //   37	52	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   52	104	107	java/security/spec/InvalidKeySpecException
    //   2	38	161	finally
    //   52	104	161	finally
    //   108	156	161	finally
    //   52	104	166	java/security/NoSuchAlgorithmException
  }
  
  void zzkt(String paramString)
  {
    try
    {
      zzkr(String.valueOf(paramString).concat("|"));
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void zzku(String paramString)
  {
    try
    {
      zzkr(String.valueOf(paramString).concat("|T|"));
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public zza zzq(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zza.zzty(this.agw.getString(zzp(paramString1, paramString2, paramString3), null));
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  /* Error */
  public long zztw(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: ldc_w 269
    //   7: invokespecial 208	com/google/firebase/iid/zzg:zzbu	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   10: astore_1
    //   11: aload_0
    //   12: getfield 32	com/google/firebase/iid/zzg:agw	Landroid/content/SharedPreferences;
    //   15: aload_1
    //   16: aconst_null
    //   17: invokeinterface 211 3 0
    //   22: astore_1
    //   23: aload_1
    //   24: ifnull +13 -> 37
    //   27: aload_1
    //   28: invokestatic 274	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   31: lstore_2
    //   32: aload_0
    //   33: monitorexit
    //   34: lload_2
    //   35: lreturn
    //   36: astore_1
    //   37: lconst_0
    //   38: lstore_2
    //   39: goto -7 -> 32
    //   42: astore_1
    //   43: aload_0
    //   44: monitorexit
    //   45: aload_1
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	zzg
    //   0	47	1	paramString	String
    //   31	8	2	l	long
    // Exception table:
    //   from	to	target	type
    //   27	32	36	java/lang/NumberFormatException
    //   2	23	42	finally
    //   27	32	42	finally
  }
  
  KeyPair zztx(String paramString)
  {
    try
    {
      KeyPair localKeyPair = zza.zzboo();
      long l = System.currentTimeMillis();
      SharedPreferences.Editor localEditor = this.agw.edit();
      localEditor.putString(zzbu(paramString, "|P|"), FirebaseInstanceId.zzu(localKeyPair.getPublic().getEncoded()));
      localEditor.putString(zzbu(paramString, "|K|"), FirebaseInstanceId.zzu(localKeyPair.getPrivate().getEncoded()));
      localEditor.putString(zzbu(paramString, "cre"), Long.toString(l));
      localEditor.commit();
      return localKeyPair;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  static class zza
  {
    private static final long bhE = TimeUnit.DAYS.toMillis(7L);
    final String afY;
    final String auj;
    final long timestamp;
    
    private zza(String paramString1, String paramString2, long paramLong)
    {
      this.auj = paramString1;
      this.afY = paramString2;
      this.timestamp = paramLong;
    }
    
    static String zzc(String paramString1, String paramString2, long paramLong)
    {
      try
      {
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("token", paramString1);
        localJSONObject.put("appVersion", paramString2);
        localJSONObject.put("timestamp", paramLong);
        paramString1 = localJSONObject.toString();
        return paramString1;
      }
      catch (JSONException paramString1)
      {
        paramString1 = String.valueOf(paramString1);
        Log.w("InstanceID/Store", String.valueOf(paramString1).length() + 24 + "Failed to encode token: " + paramString1);
      }
      return null;
    }
    
    static zza zzty(String paramString)
    {
      if (TextUtils.isEmpty(paramString)) {
        return null;
      }
      if (paramString.startsWith("{")) {
        try
        {
          paramString = new JSONObject(paramString);
          paramString = new zza(paramString.getString("token"), paramString.getString("appVersion"), paramString.getLong("timestamp"));
          return paramString;
        }
        catch (JSONException paramString)
        {
          paramString = String.valueOf(paramString);
          Log.w("InstanceID/Store", String.valueOf(paramString).length() + 23 + "Failed to parse token: " + paramString);
          return null;
        }
      }
      return new zza(paramString, null, 0L);
    }
    
    boolean zztz(String paramString)
    {
      return (System.currentTimeMillis() > this.timestamp + bhE) || (!paramString.equals(this.afY));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */