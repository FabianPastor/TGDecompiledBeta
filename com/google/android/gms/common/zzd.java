package com.google.android.gms.common;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.common.internal.zzt.zza;
import com.google.android.gms.common.internal.zzw;
import com.google.android.gms.common.internal.zzw.zza;
import com.google.android.gms.common.util.zzm;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.internal.zzsu;
import com.google.android.gms.internal.zzsu.zza;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class zzd
{
  private static zzw uO;
  private static Context uP;
  private static Set<zzt> uQ;
  private static Set<zzt> uR;
  
  private static Set<zzt> zza(IBinder[] paramArrayOfIBinder)
    throws RemoteException
  {
    int j = paramArrayOfIBinder.length;
    HashSet localHashSet = new HashSet(j);
    int i = 0;
    if (i < j)
    {
      zzt localzzt = zzt.zza.zzdt(paramArrayOfIBinder[i]);
      if (localzzt == null) {
        Log.e("GoogleCertificates", "iCertData is null, skipping");
      }
      for (;;)
      {
        i += 1;
        break;
        localHashSet.add(localzzt);
      }
    }
    return localHashSet;
  }
  
  private static boolean zzape()
  {
    zzac.zzy(uP);
    if (uO == null) {}
    String str;
    try
    {
      localObject = zzsu.zza(uP, zzsu.OC, "com.google.android.gms.googlecertificates");
      Log.d("GoogleCertificates", "com.google.android.gms.googlecertificates module is loaded");
      uO = zzw.zza.zzdw(((zzsu)localObject).zzjd("com.google.android.gms.common.GoogleCertificatesImpl"));
      return true;
    }
    catch (zzsu.zza localzza)
    {
      localObject = String.valueOf("Failed to load com.google.android.gms.googlecertificates: ");
      str = String.valueOf(localzza.getMessage());
      if (str.length() == 0) {}
    }
    for (Object localObject = ((String)localObject).concat(str);; localObject = new String((String)localObject))
    {
      Log.e("GoogleCertificates", (String)localObject);
      return false;
    }
  }
  
  /* Error */
  static Set<zzt> zzapf()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   6: ifnull +12 -> 18
    //   9: getstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   12: astore_1
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_1
    //   17: areturn
    //   18: getstatic 77	com/google/android/gms/common/zzd:uO	Lcom/google/android/gms/common/internal/zzw;
    //   21: ifnonnull +16 -> 37
    //   24: invokestatic 134	com/google/android/gms/common/zzd:zzape	()Z
    //   27: ifne +10 -> 37
    //   30: getstatic 139	java/util/Collections:EMPTY_SET	Ljava/util/Set;
    //   33: astore_1
    //   34: goto -21 -> 13
    //   37: getstatic 77	com/google/android/gms/common/zzd:uO	Lcom/google/android/gms/common/internal/zzw;
    //   40: invokeinterface 145 1 0
    //   45: astore_1
    //   46: aload_1
    //   47: ifnonnull +18 -> 65
    //   50: ldc 45
    //   52: ldc -109
    //   54: invokestatic 53	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   57: pop
    //   58: getstatic 139	java/util/Collections:EMPTY_SET	Ljava/util/Set;
    //   61: astore_1
    //   62: goto -49 -> 13
    //   65: aload_1
    //   66: invokestatic 153	com/google/android/gms/dynamic/zze:zzae	(Lcom/google/android/gms/dynamic/zzd;)Ljava/lang/Object;
    //   69: checkcast 155	[Landroid/os/IBinder;
    //   72: invokestatic 157	com/google/android/gms/common/zzd:zza	([Landroid/os/IBinder;)Ljava/util/Set;
    //   75: putstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   78: iconst_0
    //   79: istore_0
    //   80: iload_0
    //   81: getstatic 161	com/google/android/gms/common/zzd$zzd:uW	[Lcom/google/android/gms/common/zzd$zza;
    //   84: arraylength
    //   85: if_icmpge +24 -> 109
    //   88: getstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   91: getstatic 161	com/google/android/gms/common/zzd$zzd:uW	[Lcom/google/android/gms/common/zzd$zza;
    //   94: iload_0
    //   95: aaload
    //   96: invokeinterface 59 2 0
    //   101: pop
    //   102: iload_0
    //   103: iconst_1
    //   104: iadd
    //   105: istore_0
    //   106: goto -26 -> 80
    //   109: getstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   112: invokestatic 165	java/util/Collections:unmodifiableSet	(Ljava/util/Set;)Ljava/util/Set;
    //   115: putstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   118: getstatic 132	com/google/android/gms/common/zzd:uQ	Ljava/util/Set;
    //   121: astore_1
    //   122: goto -109 -> 13
    //   125: astore_1
    //   126: ldc 45
    //   128: ldc -89
    //   130: invokestatic 53	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   133: pop
    //   134: goto -16 -> 118
    //   137: astore_1
    //   138: ldc 2
    //   140: monitorexit
    //   141: aload_1
    //   142: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   79	27	0	i	int
    //   12	110	1	localObject1	Object
    //   125	1	1	localRemoteException	RemoteException
    //   137	5	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   37	46	125	android/os/RemoteException
    //   50	62	125	android/os/RemoteException
    //   65	78	125	android/os/RemoteException
    //   80	102	125	android/os/RemoteException
    //   109	118	125	android/os/RemoteException
    //   3	13	137	finally
    //   18	34	137	finally
    //   37	46	137	finally
    //   50	62	137	finally
    //   65	78	137	finally
    //   80	102	137	finally
    //   109	118	137	finally
    //   118	122	137	finally
    //   126	134	137	finally
  }
  
  /* Error */
  static Set<zzt> zzapg()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   6: ifnull +12 -> 18
    //   9: getstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: getstatic 77	com/google/android/gms/common/zzd:uO	Lcom/google/android/gms/common/internal/zzw;
    //   21: ifnonnull +16 -> 37
    //   24: invokestatic 134	com/google/android/gms/common/zzd:zzape	()Z
    //   27: ifne +10 -> 37
    //   30: getstatic 139	java/util/Collections:EMPTY_SET	Ljava/util/Set;
    //   33: astore_0
    //   34: goto -21 -> 13
    //   37: getstatic 77	com/google/android/gms/common/zzd:uO	Lcom/google/android/gms/common/internal/zzw;
    //   40: invokeinterface 174 1 0
    //   45: astore_0
    //   46: aload_0
    //   47: ifnonnull +18 -> 65
    //   50: ldc 45
    //   52: ldc -80
    //   54: invokestatic 93	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   57: pop
    //   58: getstatic 139	java/util/Collections:EMPTY_SET	Ljava/util/Set;
    //   61: astore_0
    //   62: goto -49 -> 13
    //   65: aload_0
    //   66: invokestatic 153	com/google/android/gms/dynamic/zze:zzae	(Lcom/google/android/gms/dynamic/zzd;)Ljava/lang/Object;
    //   69: checkcast 155	[Landroid/os/IBinder;
    //   72: invokestatic 157	com/google/android/gms/common/zzd:zza	([Landroid/os/IBinder;)Ljava/util/Set;
    //   75: putstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   78: getstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   81: getstatic 161	com/google/android/gms/common/zzd$zzd:uW	[Lcom/google/android/gms/common/zzd$zza;
    //   84: iconst_0
    //   85: aaload
    //   86: invokeinterface 59 2 0
    //   91: pop
    //   92: getstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   95: invokestatic 165	java/util/Collections:unmodifiableSet	(Ljava/util/Set;)Ljava/util/Set;
    //   98: putstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   101: getstatic 171	com/google/android/gms/common/zzd:uR	Ljava/util/Set;
    //   104: astore_0
    //   105: goto -92 -> 13
    //   108: astore_0
    //   109: ldc 45
    //   111: ldc -78
    //   113: invokestatic 53	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: goto -16 -> 101
    //   120: astore_0
    //   121: ldc 2
    //   123: monitorexit
    //   124: aload_0
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	93	0	localObject1	Object
    //   108	1	0	localRemoteException	RemoteException
    //   120	5	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   37	46	108	android/os/RemoteException
    //   50	62	108	android/os/RemoteException
    //   65	101	108	android/os/RemoteException
    //   3	13	120	finally
    //   18	34	120	finally
    //   37	46	120	finally
    //   50	62	120	finally
    //   65	101	120	finally
    //   101	105	120	finally
    //   109	117	120	finally
  }
  
  /* Error */
  static void zzbr(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 69	com/google/android/gms/common/zzd:uP	Landroid/content/Context;
    //   6: ifnonnull +18 -> 24
    //   9: aload_0
    //   10: ifnull +10 -> 20
    //   13: aload_0
    //   14: invokevirtual 186	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   17: putstatic 69	com/google/android/gms/common/zzd:uP	Landroid/content/Context;
    //   20: ldc 2
    //   22: monitorexit
    //   23: return
    //   24: ldc 45
    //   26: ldc -68
    //   28: invokestatic 191	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: goto -12 -> 20
    //   35: astore_0
    //   36: ldc 2
    //   38: monitorexit
    //   39: aload_0
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	35	finally
    //   13	20	35	finally
    //   24	32	35	finally
  }
  
  static abstract class zza
    extends zzt.zza
  {
    private int uS;
    
    protected zza(byte[] paramArrayOfByte)
    {
      Object localObject = paramArrayOfByte;
      if (paramArrayOfByte.length != 25)
      {
        int i = paramArrayOfByte.length;
        localObject = String.valueOf(zzm.zza(paramArrayOfByte, 0, paramArrayOfByte.length, false));
        Log.wtf("GoogleCertificates", String.valueOf(localObject).length() + 51 + "Cert hash data has incorrect length (" + i + "):\n" + (String)localObject, new Exception());
        localObject = Arrays.copyOfRange(paramArrayOfByte, 0, 25);
        if (localObject.length == 25) {
          bool = true;
        }
        i = localObject.length;
        zzac.zzb(bool, 55 + "cert hash data has incorrect length. length=" + i);
      }
      this.uS = Arrays.hashCode((byte[])localObject);
    }
    
    protected static byte[] zzhd(String paramString)
    {
      try
      {
        paramString = paramString.getBytes("ISO-8859-1");
        return paramString;
      }
      catch (UnsupportedEncodingException paramString)
      {
        throw new AssertionError(paramString);
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (!(paramObject instanceof zzt))) {
        return false;
      }
      try
      {
        paramObject = (zzt)paramObject;
        if (((zzt)paramObject).zzapi() != hashCode()) {
          return false;
        }
        paramObject = ((zzt)paramObject).zzaph();
        if (paramObject == null) {
          return false;
        }
        paramObject = (byte[])zze.zzae((com.google.android.gms.dynamic.zzd)paramObject);
        boolean bool = Arrays.equals(getBytes(), (byte[])paramObject);
        return bool;
      }
      catch (RemoteException paramObject)
      {
        Log.e("GoogleCertificates", "iCertData failed to retrive data from remote");
      }
      return false;
    }
    
    abstract byte[] getBytes();
    
    public int hashCode()
    {
      return this.uS;
    }
    
    public com.google.android.gms.dynamic.zzd zzaph()
    {
      return zze.zzac(getBytes());
    }
    
    public int zzapi()
    {
      return hashCode();
    }
  }
  
  static class zzb
    extends zzd.zza
  {
    private final byte[] uT;
    
    zzb(byte[] paramArrayOfByte)
    {
      super();
      this.uT = paramArrayOfByte;
    }
    
    byte[] getBytes()
    {
      return this.uT;
    }
  }
  
  static abstract class zzc
    extends zzd.zza
  {
    private static final WeakReference<byte[]> uV = new WeakReference(null);
    private WeakReference<byte[]> uU = uV;
    
    zzc(byte[] paramArrayOfByte)
    {
      super();
    }
    
    byte[] getBytes()
    {
      try
      {
        byte[] arrayOfByte2 = (byte[])this.uU.get();
        byte[] arrayOfByte1 = arrayOfByte2;
        if (arrayOfByte2 == null)
        {
          arrayOfByte1 = zzapj();
          this.uU = new WeakReference(arrayOfByte1);
        }
        return arrayOfByte1;
      }
      finally {}
    }
    
    protected abstract byte[] zzapj();
  }
  
  static final class zzd
  {
    static final zzd.zza[] uW = { new zzd.zzc(zzd.zza.zzhd("0\004C0\003+ \003\002\001\002\002\t\000ÂàFdJ00"))new zzd.zzc
    {
      protected byte[] zzapj()
      {
        return zzd.zza.zzhd("0\004C0\003+ \003\002\001\002\002\t\000ÂàFdJ00\r\006\t*H÷\r\001\001\004\005\0000t1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0240\022\006\003U\004\n\023\013Google Inc.1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android0\036\027\r080821231334Z\027\r360107231334Z0t1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0240\022\006\003U\004\n\023\013Google Inc.1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android0\001 0\r\006\t*H÷\r\001\001\001\005\000\003\001\r\0000\001\b\002\001\001\000«V.\000Ø;¢\b®\no\022N)Ú\021ò«VÐXâÌ©\023\003é·TÓrö@§\033\035Ë\023\tgbNFV§wj\031=²å¿·$©\036w\030\016jG¤;3Ù`w\0301EÌß{.XftÉáV[\037LjYU¿òQ¦=«ùÅ\\'\"\"Rèuäø\025Jd_qhÀ±¿Æ\022ê¿xWi»4ªyÜ~.¢vL®\007ØÁqT×î_d¥\032D¦\002ÂI\005AWÜ\002Í_\\\016Uûï\031ûã'ð±Q\026Å o\031ÑõÄÛÂÖ¹?hÌ)yÇ\016\030«k;ÕÛU*\016;LßXûíÁº5à\003Á´±\rÒD¨î$ÿý38r«R!^Ú°ü\r\013\024[j¡y\002\001\003£Ù0Ö0\035\006\003U\035\016\004\026\004\024Ç}Â!\027V%Óßkãä×¥0¦\006\003U\035#\0040\024Ç}Â!\027V%Óßkãä×¥¡x¤v0t1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0240\022\006\003U\004\n\023\013Google Inc.1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android\t\000ÂàFdJ00\f\006\003U\035\023\004\0050\003\001\001ÿ0\r\006\t*H÷\r\001\001\004\005\000\003\001\001\000mÒRÎï0,6\nªÎÏòÌ©\004»]z\026aø®F²B\004ÐÿJhÇí\032S\036ÄYZb<æ\007c±g)zzãW\022Ä\007ò\bðË\020)\022M{\020b\031ÀÊ>³ù­_¸qï&âñmDÈÙ l²ð\005»?âËD~s\020v­E³?`\tê\031Áaæ&Aª'\035ýR(ÅÅ]ÛE'XÖaöÌ\fÌ·5.BLÄ6\\R52÷2Q7Y<JãAôÛAíÚ\r\013\020q§Ä@ðþ \034¶'ÊgCiÐ½/Ù\021ÿ\006Í¿,ú\020Ü\017:ãWbHÇïÆLqD\027B÷\005ÉÞW:õ[9\r×ý¹A1]_u0\021&ÿb\024\020Ài0");
      }
    }, new zzd.zzc(zzd.zza.zzhd("0\004¨0\003 \003\002\001\002\002\t\000Õ¸l}ÓNõ0"))
    {
      protected byte[] zzapj()
      {
        return zzd.zza.zzhd("0\004¨0\003 \003\002\001\002\002\t\000Õ¸l}ÓNõ0\r\006\t*H÷\r\001\001\004\005\00001\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0200\016\006\003U\004\n\023\007Android1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android1\"0 \006\t*H÷\r\001\t\001\026\023android@android.com0\036\027\r080415233656Z\027\r350901233656Z01\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0200\016\006\003U\004\n\023\007Android1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android1\"0 \006\t*H÷\r\001\t\001\026\023android@android.com0\001 0\r\006\t*H÷\r\001\001\001\005\000\003\001\r\0000\001\b\002\001\001\000ÖÎ.\b\n¿â1MÑ³ÏÓ\030\\´=3ú\ftá½¶ÑÛ\023ö,\\9ßVøF=e¾ÀóÊBk\007Å¨íZ9ÁgçkÉ¹'K\013\"\000\031©)\025årÅm*0\033£oÅü\021:ÖËt5¡m#«}úîáeäß\037\n½§\nQlN\005\021Ê|\fU\027[ÃuùHÅj®\b¤O¦¤Ý}¿,\n5\"­\006¸Ì\030^±Uyîøm\b\013\035aÀù¯±ÂëÑ\007êE«Ûh£Ç^TÇlSÔ\013\022\035ç»Ó\016b\f\030áªaÛ¼Ý<d_/UóÔÃuì@p©?qQØ6pÁj\032¾^òÑ\030á¸®ó)ðf¿láD¬èm\034\033\017\002\001\003£ü0ù0\035\006\003U\035\016\004\026\004\024\034Å¾LC<a:\025°L¼\003òOà²0É\006\003U\035#\004Á0¾\024\034Å¾LC<a:\025°L¼\003òOà²¡¤01\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0200\016\006\003U\004\n\023\007Android1\0200\016\006\003U\004\013\023\007Android1\0200\016\006\003U\004\003\023\007Android1\"0 \006\t*H÷\r\001\t\001\026\023android@android.com\t\000Õ¸l}ÓNõ0\f\006\003U\035\023\004\0050\003\001\001ÿ0\r\006\t*H÷\r\001\001\004\005\000\003\001\001\000\031Ó\fñ\005ûx?L\r}Ò##=@zÏÎ\000\b\035[×ÆéÖí k\016\021 \006Al¢D\023ÒkJ àõ$ÊÒ»\\nL¡\001j\025n¡ì]ÉZ^:\001\0006ôHÕ\020¿.\036ag:;åm¯\013w±Â)ãÂUãèL]#ïº\tËñ; +NZ\"É2cHJ#Òü)ú\0319u3¯Øª\026\017BÂÐ\026>fCéÁ/ Á33[Àÿk\"ÞÑ­DB)¥9©Nï­«ÐeÎÒK>QåÝ{fx{ï\022þû¤Ä#ûOøÌIL\002ðõ\005\026\022ÿe)9>FêÅ»!òwÁQª_*¦'Ñè§\n¶\0035iÞ;¿ÿ|©Ú>\022Cö\013");
      }
    } };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */