package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class zzaa
{
  private final SharedPreferences zzbrz;
  private final Context zzqs;
  
  public zzaa(Context paramContext)
  {
    this(paramContext, "com.google.android.gms.appid");
  }
  
  private zzaa(Context paramContext, String paramString)
  {
    this.zzqs = paramContext;
    this.zzbrz = paramContext.getSharedPreferences(paramString, 0);
    paramContext = String.valueOf(paramString);
    paramString = String.valueOf("-no-backup");
    if (paramString.length() != 0) {
      paramContext = paramContext.concat(paramString);
    }
    for (;;)
    {
      paramContext = new File(ContextCompat.getNoBackupFilesDir(this.zzqs), paramContext);
      if (!paramContext.exists()) {}
      try
      {
        if ((paramContext.createNewFile()) && (!isEmpty()))
        {
          Log.i("FirebaseInstanceId", "App restored, clearing state");
          zztd();
          FirebaseInstanceId.getInstance().zzsk();
        }
        return;
        paramContext = new String(paramContext);
      }
      catch (IOException paramContext)
      {
        while (!Log.isLoggable("FirebaseInstanceId", 3)) {}
        paramContext = String.valueOf(paramContext.getMessage());
        if (paramContext.length() == 0) {}
      }
    }
    for (paramContext = "Error creating file in no backup dir: ".concat(paramContext);; paramContext = new String("Error creating file in no backup dir: "))
    {
      Log.d("FirebaseInstanceId", paramContext);
      break;
    }
  }
  
  private final boolean isEmpty()
  {
    try
    {
      boolean bool = this.zzbrz.getAll().isEmpty();
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private static String zzi(String paramString1, String paramString2, String paramString3)
  {
    return String.valueOf(paramString1).length() + 4 + String.valueOf(paramString2).length() + String.valueOf(paramString3).length() + paramString1 + "|T|" + paramString2 + "|" + paramString3;
  }
  
  private static String zzv(String paramString1, String paramString2)
  {
    return String.valueOf(paramString1).length() + 3 + String.valueOf(paramString2).length() + paramString1 + "|S|" + paramString2;
  }
  
  /* Error */
  public final void zza(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload 4
    //   4: aload 5
    //   6: invokestatic 145	java/lang/System:currentTimeMillis	()J
    //   9: invokestatic 150	com/google/firebase/iid/zzab:zza	(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;
    //   12: astore 5
    //   14: aload 5
    //   16: ifnonnull +6 -> 22
    //   19: aload_0
    //   20: monitorexit
    //   21: return
    //   22: aload_0
    //   23: getfield 31	com/google/firebase/iid/zzaa:zzbrz	Landroid/content/SharedPreferences;
    //   26: invokeinterface 154 1 0
    //   31: astore 4
    //   33: aload 4
    //   35: aload_1
    //   36: aload_2
    //   37: aload_3
    //   38: invokestatic 156	com/google/firebase/iid/zzaa:zzi	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   41: aload 5
    //   43: invokeinterface 162 3 0
    //   48: pop
    //   49: aload 4
    //   51: invokeinterface 165 1 0
    //   56: pop
    //   57: goto -38 -> 19
    //   60: astore_1
    //   61: aload_0
    //   62: monitorexit
    //   63: aload_1
    //   64: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	zzaa
    //   0	65	1	paramString1	String
    //   0	65	2	paramString2	String
    //   0	65	3	paramString3	String
    //   0	65	4	paramString4	String
    //   0	65	5	paramString5	String
    // Exception table:
    //   from	to	target	type
    //   2	14	60	finally
    //   22	57	60	finally
  }
  
  public final boolean zzez(String paramString)
  {
    for (;;)
    {
      try
      {
        String str1 = this.zzbrz.getString("topic_operaion_queue", "");
        String str2 = String.valueOf(",");
        String str3 = String.valueOf(paramString);
        if (str3.length() != 0)
        {
          str2 = str2.concat(str3);
          if (!str1.startsWith(str2)) {
            break label143;
          }
          str2 = String.valueOf(",");
          paramString = String.valueOf(paramString);
          if (paramString.length() != 0)
          {
            paramString = str2.concat(paramString);
            paramString = str1.substring(paramString.length());
            this.zzbrz.edit().putString("topic_operaion_queue", paramString).apply();
            bool = true;
            return bool;
          }
        }
        else
        {
          str2 = new String(str2);
          continue;
        }
        paramString = new String(str2);
      }
      finally {}
      continue;
      label143:
      boolean bool = false;
    }
  }
  
  final KeyPair zzfb(String paramString)
  {
    try
    {
      KeyPair localKeyPair = zza.zzsc();
      long l = System.currentTimeMillis();
      SharedPreferences.Editor localEditor = this.zzbrz.edit();
      localEditor.putString(zzv(paramString, "|P|"), Base64.encodeToString(localKeyPair.getPublic().getEncoded(), 11));
      localEditor.putString(zzv(paramString, "|K|"), Base64.encodeToString(localKeyPair.getPrivate().getEncoded(), 11));
      localEditor.putString(zzv(paramString, "cre"), Long.toString(l));
      localEditor.commit();
      return localKeyPair;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public final void zzfc(String paramString)
  {
    try
    {
      String str1 = String.valueOf(paramString).concat("|T|");
      SharedPreferences.Editor localEditor = this.zzbrz.edit();
      paramString = this.zzbrz.getAll().keySet().iterator();
      while (paramString.hasNext())
      {
        String str2 = (String)paramString.next();
        if (str2.startsWith(str1)) {
          localEditor.remove(str2);
        }
      }
      localEditor.commit();
    }
    finally {}
  }
  
  /* Error */
  public final KeyPair zzfd(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 31	com/google/firebase/iid/zzaa:zzbrz	Landroid/content/SharedPreferences;
    //   6: aload_1
    //   7: ldc -60
    //   9: invokestatic 198	com/google/firebase/iid/zzaa:zzv	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   12: aconst_null
    //   13: invokeinterface 174 3 0
    //   18: astore_2
    //   19: aload_0
    //   20: getfield 31	com/google/firebase/iid/zzaa:zzbrz	Landroid/content/SharedPreferences;
    //   23: aload_1
    //   24: ldc -38
    //   26: invokestatic 198	com/google/firebase/iid/zzaa:zzv	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   29: aconst_null
    //   30: invokeinterface 174 3 0
    //   35: astore_1
    //   36: aload_2
    //   37: ifnull +7 -> 44
    //   40: aload_1
    //   41: ifnonnull +9 -> 50
    //   44: aconst_null
    //   45: astore_1
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_1
    //   49: areturn
    //   50: aload_2
    //   51: bipush 8
    //   53: invokestatic 265	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   56: astore_2
    //   57: aload_1
    //   58: bipush 8
    //   60: invokestatic 265	android/util/Base64:decode	(Ljava/lang/String;I)[B
    //   63: astore_1
    //   64: ldc_w 267
    //   67: invokestatic 272	java/security/KeyFactory:getInstance	(Ljava/lang/String;)Ljava/security/KeyFactory;
    //   70: astore_3
    //   71: new 274	java/security/spec/X509EncodedKeySpec
    //   74: astore 4
    //   76: aload 4
    //   78: aload_2
    //   79: invokespecial 277	java/security/spec/X509EncodedKeySpec:<init>	([B)V
    //   82: aload_3
    //   83: aload 4
    //   85: invokevirtual 281	java/security/KeyFactory:generatePublic	(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;
    //   88: astore_2
    //   89: new 283	java/security/spec/PKCS8EncodedKeySpec
    //   92: astore 4
    //   94: aload 4
    //   96: aload_1
    //   97: invokespecial 284	java/security/spec/PKCS8EncodedKeySpec:<init>	([B)V
    //   100: aload_3
    //   101: aload 4
    //   103: invokevirtual 288	java/security/KeyFactory:generatePrivate	(Ljava/security/spec/KeySpec;)Ljava/security/PrivateKey;
    //   106: astore_3
    //   107: new 200	java/security/KeyPair
    //   110: astore_1
    //   111: aload_1
    //   112: aload_2
    //   113: aload_3
    //   114: invokespecial 291	java/security/KeyPair:<init>	(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
    //   117: goto -71 -> 46
    //   120: astore_1
    //   121: aload_1
    //   122: invokestatic 37	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   125: astore_1
    //   126: aload_1
    //   127: invokestatic 37	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   130: invokevirtual 43	java/lang/String:length	()I
    //   133: istore 5
    //   135: new 119	java/lang/StringBuilder
    //   138: astore_2
    //   139: aload_2
    //   140: iload 5
    //   142: bipush 19
    //   144: iadd
    //   145: invokespecial 122	java/lang/StringBuilder:<init>	(I)V
    //   148: ldc 70
    //   150: aload_2
    //   151: ldc_w 293
    //   154: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   157: aload_1
    //   158: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: invokestatic 296	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   167: pop
    //   168: invokestatic 87	com/google/firebase/iid/FirebaseInstanceId:getInstance	()Lcom/google/firebase/iid/FirebaseInstanceId;
    //   171: invokevirtual 90	com/google/firebase/iid/FirebaseInstanceId:zzsk	()V
    //   174: aconst_null
    //   175: astore_1
    //   176: goto -130 -> 46
    //   179: astore_1
    //   180: aload_0
    //   181: monitorexit
    //   182: aload_1
    //   183: athrow
    //   184: astore_1
    //   185: goto -64 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	188	0	this	zzaa
    //   0	188	1	paramString	String
    //   18	133	2	localObject1	Object
    //   70	44	3	localObject2	Object
    //   74	28	4	localObject3	Object
    //   133	12	5	i	int
    // Exception table:
    //   from	to	target	type
    //   50	117	120	java/security/spec/InvalidKeySpecException
    //   2	36	179	finally
    //   50	117	179	finally
    //   121	174	179	finally
    //   50	117	184	java/security/NoSuchAlgorithmException
  }
  
  public final zzab zzj(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      paramString1 = zzab.zzfe(this.zzbrz.getString(zzi(paramString1, paramString2, paramString3), null));
      return paramString1;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  public final String zztc()
  {
    Object localObject1 = null;
    try
    {
      Object localObject2 = this.zzbrz.getString("topic_operaion_queue", null);
      Object localObject3 = localObject1;
      if (localObject2 != null)
      {
        localObject2 = ((String)localObject2).split(",");
        localObject3 = localObject1;
        if (localObject2.length > 1)
        {
          localObject3 = localObject1;
          if (!TextUtils.isEmpty(localObject2[1])) {
            localObject3 = localObject2[1];
          }
        }
      }
      return (String)localObject3;
    }
    finally {}
  }
  
  public final void zztd()
  {
    try
    {
      this.zzbrz.edit().clear().commit();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */