package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

public final class zzclq
  extends zzcjl
{
  private static String[] zzjjn = { "firebase_" };
  private SecureRandom zzjjo;
  private final AtomicLong zzjjp = new AtomicLong(0L);
  private int zzjjq;
  
  zzclq(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final int zza(String paramString, Object paramObject, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i;
      if ((paramObject instanceof Parcelable[]))
      {
        i = ((Parcelable[])paramObject).length;
        if (i <= 1000) {
          break label82;
        }
        zzawy().zzazf().zzd("Parameter array is too long; discarded. Value kind, name, array length", "param", paramString, Integer.valueOf(i));
        i = 0;
      }
      for (;;)
      {
        if (i != 0) {
          break label88;
        }
        return 17;
        if ((paramObject instanceof ArrayList))
        {
          i = ((ArrayList)paramObject).size();
          break;
        }
        i = 1;
        continue;
        label82:
        i = 1;
      }
    }
    label88:
    if (zzki(paramString)) {}
    for (paramBoolean = zza("param", paramString, 256, paramObject, paramBoolean); paramBoolean; paramBoolean = zza("param", paramString, 100, paramObject, paramBoolean)) {
      return 0;
    }
    return 4;
  }
  
  private static Object zza(int paramInt, Object paramObject, boolean paramBoolean)
  {
    Object localObject;
    if (paramObject == null) {
      localObject = null;
    }
    do
    {
      do
      {
        return localObject;
        localObject = paramObject;
      } while ((paramObject instanceof Long));
      localObject = paramObject;
    } while ((paramObject instanceof Double));
    if ((paramObject instanceof Integer)) {
      return Long.valueOf(((Integer)paramObject).intValue());
    }
    if ((paramObject instanceof Byte)) {
      return Long.valueOf(((Byte)paramObject).byteValue());
    }
    if ((paramObject instanceof Short)) {
      return Long.valueOf(((Short)paramObject).shortValue());
    }
    if ((paramObject instanceof Boolean))
    {
      if (((Boolean)paramObject).booleanValue()) {}
      for (long l = 1L;; l = 0L) {
        return Long.valueOf(l);
      }
    }
    if ((paramObject instanceof Float)) {
      return Double.valueOf(((Float)paramObject).doubleValue());
    }
    if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence))) {
      return zza(String.valueOf(paramObject), paramInt, paramBoolean);
    }
    return null;
  }
  
  public static Object zza(zzcmb paramzzcmb, String paramString)
  {
    paramzzcmb = paramzzcmb.zzjlh;
    int j = paramzzcmb.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramzzcmb[i];
      if (((zzcmc)localObject).name.equals(paramString))
      {
        if (((zzcmc)localObject).zzgcc != null) {
          return ((zzcmc)localObject).zzgcc;
        }
        if (((zzcmc)localObject).zzjll != null) {
          return ((zzcmc)localObject).zzjll;
        }
        if (((zzcmc)localObject).zzjjl != null) {
          return ((zzcmc)localObject).zzjjl;
        }
      }
      i += 1;
    }
    return null;
  }
  
  public static String zza(String paramString, int paramInt, boolean paramBoolean)
  {
    String str = paramString;
    if (paramString.codePointCount(0, paramString.length()) > paramInt)
    {
      if (paramBoolean) {
        str = String.valueOf(paramString.substring(0, paramString.offsetByCodePoints(0, paramInt))).concat("...");
      }
    }
    else {
      return str;
    }
    return null;
  }
  
  public static String zza(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    zzbq.checkNotNull(paramArrayOfString1);
    zzbq.checkNotNull(paramArrayOfString2);
    int j = Math.min(paramArrayOfString1.length, paramArrayOfString2.length);
    int i = 0;
    while (i < j)
    {
      if (zzas(paramString, paramArrayOfString1[i])) {
        return paramArrayOfString2[i];
      }
      i += 1;
    }
    return null;
  }
  
  private final boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null) {}
    do
    {
      int i;
      while (paramInt >= i)
      {
        for (;;)
        {
          return true;
          if ((!(paramObject instanceof Long)) && (!(paramObject instanceof Float)) && (!(paramObject instanceof Integer)) && (!(paramObject instanceof Byte)) && (!(paramObject instanceof Short)) && (!(paramObject instanceof Boolean)) && (!(paramObject instanceof Double))) {
            if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence)))
            {
              paramObject = String.valueOf(paramObject);
              if (((String)paramObject).codePointCount(0, ((String)paramObject).length()) > paramInt)
              {
                zzawy().zzazf().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
                return false;
              }
            }
            else if ((!(paramObject instanceof Bundle)) || (!paramBoolean))
            {
              if ((!(paramObject instanceof Parcelable[])) || (!paramBoolean)) {
                break;
              }
              paramString1 = (Parcelable[])paramObject;
              i = paramString1.length;
              paramInt = 0;
              while (paramInt < i)
              {
                paramObject = paramString1[paramInt];
                if (!(paramObject instanceof Bundle))
                {
                  zzawy().zzazf().zze("All Parcelable[] elements must be of type Bundle. Value type, name", paramObject.getClass(), paramString2);
                  return false;
                }
                paramInt += 1;
              }
            }
          }
        }
        if ((!(paramObject instanceof ArrayList)) || (!paramBoolean)) {
          break;
        }
        paramString1 = (ArrayList)paramObject;
        i = paramString1.size();
        paramInt = 0;
      }
      paramObject = paramString1.get(paramInt);
      paramInt += 1;
    } while ((paramObject instanceof Bundle));
    zzawy().zzazf().zze("All ArrayList elements must be of type Bundle. Value type, name", paramObject.getClass(), paramString2);
    return false;
    return false;
  }
  
  private final boolean zza(String paramString1, String[] paramArrayOfString, String paramString2)
  {
    if (paramString2 == null)
    {
      zzawy().zzazd().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    zzbq.checkNotNull(paramString2);
    int i = 0;
    if (i < zzjjn.length) {
      if (!paramString2.startsWith(zzjjn[i])) {}
    }
    for (i = 1;; i = 0)
    {
      if (i == 0) {
        break label88;
      }
      zzawy().zzazd().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
      return false;
      i += 1;
      break;
    }
    label88:
    if (paramArrayOfString != null)
    {
      zzbq.checkNotNull(paramArrayOfString);
      i = 0;
      if (i < paramArrayOfString.length) {
        if (!zzas(paramString2, paramArrayOfString[i])) {}
      }
      for (i = 1;; i = 0)
      {
        if (i == 0) {
          break label157;
        }
        zzawy().zzazd().zze("Name is reserved. Type, name", paramString1, paramString2);
        return false;
        i += 1;
        break;
      }
    }
    label157:
    return true;
  }
  
  public static boolean zza(long[] paramArrayOfLong, int paramInt)
  {
    if (paramInt >= paramArrayOfLong.length << 6) {}
    while ((paramArrayOfLong[(paramInt / 64)] & 1L << paramInt % 64) == 0L) {
      return false;
    }
    return true;
  }
  
  static byte[] zza(Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      paramParcelable.writeToParcel(localParcel, 0);
      paramParcelable = localParcel.marshall();
      return paramParcelable;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public static long[] zza(BitSet paramBitSet)
  {
    int k = (paramBitSet.length() + 63) / 64;
    long[] arrayOfLong = new long[k];
    int i = 0;
    while (i < k)
    {
      arrayOfLong[i] = 0L;
      int j = 0;
      while ((j < 64) && ((i << 6) + j < paramBitSet.length()))
      {
        if (paramBitSet.get((i << 6) + j)) {
          arrayOfLong[i] |= 1L << j;
        }
        j += 1;
      }
      i += 1;
    }
    return arrayOfLong;
  }
  
  static zzcmc[] zza(zzcmc[] paramArrayOfzzcmc, String paramString, Object paramObject)
  {
    int j = paramArrayOfzzcmc.length;
    int i = 0;
    while (i < j)
    {
      localObject = paramArrayOfzzcmc[i];
      if (Objects.equals(((zzcmc)localObject).name, paramString))
      {
        ((zzcmc)localObject).zzjll = null;
        ((zzcmc)localObject).zzgcc = null;
        ((zzcmc)localObject).zzjjl = null;
        if ((paramObject instanceof Long)) {
          ((zzcmc)localObject).zzjll = ((Long)paramObject);
        }
        do
        {
          return paramArrayOfzzcmc;
          if ((paramObject instanceof String))
          {
            ((zzcmc)localObject).zzgcc = ((String)paramObject);
            return paramArrayOfzzcmc;
          }
        } while (!(paramObject instanceof Double));
        ((zzcmc)localObject).zzjjl = ((Double)paramObject);
        return paramArrayOfzzcmc;
      }
      i += 1;
    }
    Object localObject = new zzcmc[paramArrayOfzzcmc.length + 1];
    System.arraycopy(paramArrayOfzzcmc, 0, localObject, 0, paramArrayOfzzcmc.length);
    zzcmc localzzcmc = new zzcmc();
    localzzcmc.name = paramString;
    if ((paramObject instanceof Long)) {
      localzzcmc.zzjll = ((Long)paramObject);
    }
    for (;;)
    {
      localObject[paramArrayOfzzcmc.length] = localzzcmc;
      return (zzcmc[])localObject;
      if ((paramObject instanceof String)) {
        localzzcmc.zzgcc = ((String)paramObject);
      } else if ((paramObject instanceof Double)) {
        localzzcmc.zzjjl = ((Double)paramObject);
      }
    }
  }
  
  public static Bundle[] zzaf(Object paramObject)
  {
    if ((paramObject instanceof Bundle)) {
      return new Bundle[] { (Bundle)paramObject };
    }
    if ((paramObject instanceof Parcelable[])) {
      return (Bundle[])Arrays.copyOf((Parcelable[])paramObject, ((Parcelable[])paramObject).length, Bundle[].class);
    }
    if ((paramObject instanceof ArrayList))
    {
      paramObject = (ArrayList)paramObject;
      return (Bundle[])((ArrayList)paramObject).toArray(new Bundle[((ArrayList)paramObject).size()]);
    }
    return null;
  }
  
  /* Error */
  public static Object zzag(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: new 291	java/io/ByteArrayOutputStream
    //   9: dup
    //   10: invokespecial 292	java/io/ByteArrayOutputStream:<init>	()V
    //   13: astore_1
    //   14: new 294	java/io/ObjectOutputStream
    //   17: dup
    //   18: aload_1
    //   19: invokespecial 297	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_2
    //   23: aload_2
    //   24: aload_0
    //   25: invokevirtual 301	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   28: aload_2
    //   29: invokevirtual 304	java/io/ObjectOutputStream:flush	()V
    //   32: new 306	java/io/ObjectInputStream
    //   35: dup
    //   36: new 308	java/io/ByteArrayInputStream
    //   39: dup
    //   40: aload_1
    //   41: invokevirtual 311	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   44: invokespecial 314	java/io/ByteArrayInputStream:<init>	([B)V
    //   47: invokespecial 317	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   50: astore_1
    //   51: aload_1
    //   52: invokevirtual 321	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   55: astore_0
    //   56: aload_2
    //   57: invokevirtual 324	java/io/ObjectOutputStream:close	()V
    //   60: aload_1
    //   61: invokevirtual 325	java/io/ObjectInputStream:close	()V
    //   64: aload_0
    //   65: areturn
    //   66: aload_2
    //   67: ifnull +7 -> 74
    //   70: aload_2
    //   71: invokevirtual 324	java/io/ObjectOutputStream:close	()V
    //   74: aload_1
    //   75: ifnull +7 -> 82
    //   78: aload_1
    //   79: invokevirtual 325	java/io/ObjectInputStream:close	()V
    //   82: aload_0
    //   83: athrow
    //   84: astore_0
    //   85: aconst_null
    //   86: areturn
    //   87: astore_0
    //   88: aconst_null
    //   89: areturn
    //   90: astore_0
    //   91: aconst_null
    //   92: astore_1
    //   93: goto -27 -> 66
    //   96: astore_0
    //   97: goto -31 -> 66
    //   100: astore_0
    //   101: aconst_null
    //   102: astore_1
    //   103: aconst_null
    //   104: astore_2
    //   105: goto -39 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	paramObject	Object
    //   13	90	1	localObject	Object
    //   22	83	2	localObjectOutputStream	java.io.ObjectOutputStream
    // Exception table:
    //   from	to	target	type
    //   56	64	84	java/io/IOException
    //   70	74	84	java/io/IOException
    //   78	82	84	java/io/IOException
    //   82	84	84	java/io/IOException
    //   56	64	87	java/lang/ClassNotFoundException
    //   70	74	87	java/lang/ClassNotFoundException
    //   78	82	87	java/lang/ClassNotFoundException
    //   82	84	87	java/lang/ClassNotFoundException
    //   23	51	90	finally
    //   51	56	96	finally
    //   6	23	100	finally
  }
  
  private final boolean zzag(Context paramContext, String paramString)
  {
    X500Principal localX500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
    try
    {
      paramContext = zzbhf.zzdb(paramContext).getPackageInfo(paramString, 64);
      if ((paramContext != null) && (paramContext.signatures != null) && (paramContext.signatures.length > 0))
      {
        paramContext = paramContext.signatures[0];
        boolean bool = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramContext.toByteArray()))).getSubjectX500Principal().equals(localX500Principal);
        return bool;
      }
    }
    catch (CertificateException paramContext)
    {
      zzawy().zzazd().zzj("Error obtaining certificate", paramContext);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        zzawy().zzazd().zzj("Package name not found", paramContext);
      }
    }
  }
  
  private final boolean zzaq(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzawy().zzazd().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzawy().zzazd().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if (!Character.isLetter(i))
    {
      zzawy().zzazd().zze("Name must start with a letter. Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzawy().zzazd().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  private final boolean zzar(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzawy().zzazd().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzawy().zzazd().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if ((!Character.isLetter(i)) && (i != 95))
    {
      zzawy().zzazd().zze("Name must start with a letter or _ (underscore). Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzawy().zzazd().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  public static boolean zzas(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return true;
    }
    if (paramString1 == null) {
      return false;
    }
    return paramString1.equals(paramString2);
  }
  
  private static void zzb(Bundle paramBundle, Object paramObject)
  {
    zzbq.checkNotNull(paramBundle);
    if ((paramObject != null) && (((paramObject instanceof String)) || ((paramObject instanceof CharSequence)))) {
      paramBundle.putLong("_el", String.valueOf(paramObject).length());
    }
  }
  
  private final boolean zzb(String paramString1, int paramInt, String paramString2)
  {
    if (paramString2 == null)
    {
      zzawy().zzazd().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.codePointCount(0, paramString2.length()) > paramInt)
    {
      zzawy().zzazd().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      return false;
    }
    return true;
  }
  
  private static boolean zzd(Bundle paramBundle, int paramInt)
  {
    if (paramBundle.getLong("_err") == 0L)
    {
      paramBundle.putLong("_err", paramInt);
      return true;
    }
    return false;
  }
  
  static boolean zzd(zzcha paramzzcha, zzcgi paramzzcgi)
  {
    zzbq.checkNotNull(paramzzcha);
    zzbq.checkNotNull(paramzzcgi);
    return !TextUtils.isEmpty(paramzzcgi.zzixs);
  }
  
  static MessageDigest zzek(String paramString)
  {
    int i = 0;
    while (i < 2) {
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
        if (localMessageDigest != null) {
          return localMessageDigest;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        i += 1;
      }
    }
    return null;
  }
  
  static boolean zzjz(String paramString)
  {
    boolean bool = false;
    zzbq.zzgm(paramString);
    if ((paramString.charAt(0) != '_') || (paramString.equals("_ep"))) {
      bool = true;
    }
    return bool;
  }
  
  private final int zzke(String paramString)
  {
    if (!zzaq("event param", paramString)) {}
    do
    {
      return 3;
      if (!zza("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", 40, paramString));
    return 0;
  }
  
  private final int zzkf(String paramString)
  {
    if (!zzar("event param", paramString)) {}
    do
    {
      return 3;
      if (!zza("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", 40, paramString));
    return 0;
  }
  
  private static int zzkh(String paramString)
  {
    if ("_ldl".equals(paramString)) {
      return 2048;
    }
    return 36;
  }
  
  public static boolean zzki(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"));
  }
  
  static boolean zzkk(String paramString)
  {
    return (paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310);
  }
  
  static boolean zzkn(String paramString)
  {
    boolean bool = true;
    zzbq.zzgm(paramString);
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        bool = false;
      }
      return bool;
      if (paramString.equals("_in"))
      {
        i = 0;
        continue;
        if (paramString.equals("_ui"))
        {
          i = 1;
          continue;
          if (paramString.equals("_ug")) {
            i = 2;
          }
        }
      }
    }
  }
  
  public static boolean zzo(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("android.intent.extra.REFERRER_NAME");
    return ("android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(paramIntent)) || ("https://www.google.com".equals(paramIntent)) || ("android-app://com.google.appcrawler".equals(paramIntent));
  }
  
  static long zzs(byte[] paramArrayOfByte)
  {
    int j = 0;
    zzbq.checkNotNull(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      zzbq.checkState(bool);
      l = 0L;
      int i = paramArrayOfByte.length - 1;
      while ((i >= 0) && (i >= paramArrayOfByte.length - 8))
      {
        l += ((paramArrayOfByte[i] & 0xFF) << j);
        j += 8;
        i -= 1;
      }
    }
    return l;
  }
  
  public static boolean zzt(Context paramContext, String paramString)
  {
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getServiceInfo(new ComponentName(paramContext, paramString), 4);
      if (paramContext != null)
      {
        boolean bool = paramContext.enabled;
        if (bool) {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public final Bundle zza(String paramString, Bundle paramBundle, List<String> paramList, boolean paramBoolean1, boolean paramBoolean2)
  {
    Bundle localBundle = null;
    if (paramBundle != null)
    {
      localBundle = new Bundle(paramBundle);
      Iterator localIterator = paramBundle.keySet().iterator();
      int i = 0;
      while (localIterator.hasNext())
      {
        String str1 = (String)localIterator.next();
        int j = 0;
        int k = 0;
        if ((paramList == null) || (!paramList.contains(str1)))
        {
          if (paramBoolean1) {
            k = zzke(str1);
          }
          j = k;
          if (k == 0) {
            j = zzkf(str1);
          }
        }
        if (j != 0)
        {
          if (zzd(localBundle, j))
          {
            localBundle.putString("_ev", zza(str1, 40, true));
            if (j == 3) {
              zzb(localBundle, str1);
            }
          }
          localBundle.remove(str1);
        }
        else
        {
          j = zza(str1, paramBundle.get(str1), paramBoolean2);
          if ((j != 0) && (!"_ev".equals(str1)))
          {
            if (zzd(localBundle, j))
            {
              localBundle.putString("_ev", zza(str1, 40, true));
              zzb(localBundle, paramBundle.get(str1));
            }
            localBundle.remove(str1);
          }
          else
          {
            j = i;
            if (zzjz(str1))
            {
              i += 1;
              j = i;
              if (i > 25)
              {
                String str2 = 48 + "Event can't contain more then 25 params";
                zzawy().zzazd().zze(str2, zzawt().zzjh(paramString), zzawt().zzx(paramBundle));
                zzd(localBundle, 5);
                localBundle.remove(str1);
                continue;
              }
            }
            i = j;
          }
        }
      }
    }
    return localBundle;
  }
  
  final zzcha zza(String paramString1, Bundle paramBundle, String paramString2, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return null;
    }
    if (zzkb(paramString1) != 0)
    {
      zzawy().zzazd().zzj("Invalid conditional property event name", zzawt().zzjj(paramString1));
      throw new IllegalArgumentException();
    }
    if (paramBundle != null) {}
    for (paramBundle = new Bundle(paramBundle);; paramBundle = new Bundle())
    {
      paramBundle.putString("_o", paramString2);
      return new zzcha(paramString1, new zzcgx(zzy(zza(paramString1, paramBundle, Collections.singletonList("_o"), false, false))), paramString2, paramLong);
    }
  }
  
  public final void zza(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    zza(null, paramInt1, paramString1, paramString2, paramInt2);
  }
  
  public final void zza(Bundle paramBundle, String paramString, Object paramObject)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      if ((paramObject instanceof Long))
      {
        paramBundle.putLong(paramString, ((Long)paramObject).longValue());
        return;
      }
      if ((paramObject instanceof String))
      {
        paramBundle.putString(paramString, String.valueOf(paramObject));
        return;
      }
      if ((paramObject instanceof Double))
      {
        paramBundle.putDouble(paramString, ((Double)paramObject).doubleValue());
        return;
      }
    } while (paramString == null);
    if (paramObject != null) {}
    for (paramBundle = paramObject.getClass().getSimpleName();; paramBundle = null)
    {
      zzawy().zzazg().zze("Not putting event parameter. Invalid value type. name, type", zzawt().zzji(paramString), paramBundle);
      return;
    }
  }
  
  public final void zza(zzcmc paramzzcmc, Object paramObject)
  {
    zzbq.checkNotNull(paramObject);
    paramzzcmc.zzgcc = null;
    paramzzcmc.zzjll = null;
    paramzzcmc.zzjjl = null;
    if ((paramObject instanceof String))
    {
      paramzzcmc.zzgcc = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzcmc.zzjll = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzcmc.zzjjl = ((Double)paramObject);
      return;
    }
    zzawy().zzazd().zzj("Ignoring invalid (type) event param value", paramObject);
  }
  
  public final void zza(zzcmg paramzzcmg, Object paramObject)
  {
    zzbq.checkNotNull(paramObject);
    paramzzcmg.zzgcc = null;
    paramzzcmg.zzjll = null;
    paramzzcmg.zzjjl = null;
    if ((paramObject instanceof String))
    {
      paramzzcmg.zzgcc = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzcmg.zzjll = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzcmg.zzjjl = ((Double)paramObject);
      return;
    }
    zzawy().zzazd().zzj("Ignoring invalid (type) user attribute value", paramObject);
  }
  
  public final void zza(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
  {
    paramString1 = new Bundle();
    zzd(paramString1, paramInt1);
    if (!TextUtils.isEmpty(paramString2)) {
      paramString1.putString(paramString2, paramString3);
    }
    if ((paramInt1 == 6) || (paramInt1 == 7) || (paramInt1 == 2)) {
      paramString1.putLong("_el", paramInt2);
    }
    this.zziwf.zzawm().zzc("auto", "_err", paramString1);
  }
  
  final long zzaf(Context paramContext, String paramString)
  {
    zzve();
    zzbq.checkNotNull(paramContext);
    zzbq.zzgm(paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    MessageDigest localMessageDigest = zzek("MD5");
    if (localMessageDigest == null)
    {
      zzawy().zzazd().log("Could not get MD5 instance");
      return -1L;
    }
    if (localPackageManager != null) {
      try
      {
        if (!zzag(paramContext, paramString))
        {
          paramContext = zzbhf.zzdb(paramContext).getPackageInfo(getContext().getPackageName(), 64);
          if ((paramContext.signatures != null) && (paramContext.signatures.length > 0)) {
            return zzs(localMessageDigest.digest(paramContext.signatures[0].toByteArray()));
          }
          zzawy().zzazf().log("Could not get signatures");
          return -1L;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        zzawy().zzazd().zzj("Package name not found", paramContext);
      }
    }
    return 0L;
  }
  
  protected final boolean zzaxz()
  {
    return true;
  }
  
  protected final void zzayy()
  {
    zzve();
    SecureRandom localSecureRandom = new SecureRandom();
    long l2 = localSecureRandom.nextLong();
    long l1 = l2;
    if (l2 == 0L)
    {
      l2 = localSecureRandom.nextLong();
      l1 = l2;
      if (l2 == 0L)
      {
        zzawy().zzazf().log("Utils falling back to Random for random id");
        l1 = l2;
      }
    }
    this.zzjjp.set(l1);
  }
  
  final <T extends Parcelable> T zzb(byte[] paramArrayOfByte, Parcelable.Creator<T> paramCreator)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      localParcel.unmarshall(paramArrayOfByte, 0, paramArrayOfByte.length);
      localParcel.setDataPosition(0);
      paramArrayOfByte = (Parcelable)paramCreator.createFromParcel(localParcel);
      return paramArrayOfByte;
    }
    catch (zzbfo paramArrayOfByte)
    {
      zzawy().zzazd().log("Failed to load parcelable from buffer");
      return null;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public final byte[] zzb(zzcmd paramzzcmd)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzcmd.zzho()];
      zzfjk localzzfjk = zzfjk.zzo(arrayOfByte, 0, arrayOfByte.length);
      paramzzcmd.zza(localzzfjk);
      localzzfjk.zzcwt();
      return arrayOfByte;
    }
    catch (IOException paramzzcmd)
    {
      zzawy().zzazd().zzj("Data loss. Failed to serialize batch", paramzzcmd);
    }
    return null;
  }
  
  public final long zzbay()
  {
    long l1;
    if (this.zzjjp.get() == 0L) {
      synchronized (this.zzjjp)
      {
        l1 = new Random(System.nanoTime() ^ zzws().currentTimeMillis()).nextLong();
        int i = this.zzjjq + 1;
        this.zzjjq = i;
        long l2 = i;
        return l1 + l2;
      }
    }
    synchronized (this.zzjjp)
    {
      this.zzjjp.compareAndSet(-1L, 1L);
      l1 = this.zzjjp.getAndIncrement();
      return l1;
    }
  }
  
  final SecureRandom zzbaz()
  {
    zzve();
    if (this.zzjjo == null) {
      this.zzjjo = new SecureRandom();
    }
    return this.zzjjo;
  }
  
  public final boolean zzeb(String paramString)
  {
    zzve();
    if (zzbhf.zzdb(getContext()).checkCallingOrSelfPermission(paramString) == 0) {
      return true;
    }
    zzawy().zzazi().zzj("Permission not granted", paramString);
    return false;
  }
  
  public final boolean zzf(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) || (paramLong2 <= 0L)) {}
    while (Math.abs(zzws().currentTimeMillis() - paramLong1) > paramLong2) {
      return true;
    }
    return false;
  }
  
  public final Object zzk(String paramString, Object paramObject)
  {
    int i = 256;
    if ("_ev".equals(paramString)) {
      return zza(256, paramObject, true);
    }
    if (zzki(paramString)) {}
    for (;;)
    {
      return zza(i, paramObject, false);
      i = 100;
    }
  }
  
  public final int zzka(String paramString)
  {
    if (!zzaq("event", paramString)) {}
    do
    {
      return 2;
      if (!zza("event", AppMeasurement.Event.zziwg, paramString)) {
        return 13;
      }
    } while (!zzb("event", 40, paramString));
    return 0;
  }
  
  public final int zzkb(String paramString)
  {
    if (!zzar("event", paramString)) {}
    do
    {
      return 2;
      if (!zza("event", AppMeasurement.Event.zziwg, paramString)) {
        return 13;
      }
    } while (!zzb("event", 40, paramString));
    return 0;
  }
  
  public final int zzkc(String paramString)
  {
    if (!zzaq("user property", paramString)) {}
    do
    {
      return 6;
      if (!zza("user property", AppMeasurement.UserProperty.zziwn, paramString)) {
        return 15;
      }
    } while (!zzb("user property", 24, paramString));
    return 0;
  }
  
  public final int zzkd(String paramString)
  {
    if (!zzar("user property", paramString)) {}
    do
    {
      return 6;
      if (!zza("user property", AppMeasurement.UserProperty.zziwn, paramString)) {
        return 15;
      }
    } while (!zzb("user property", 24, paramString));
    return 0;
  }
  
  public final boolean zzkg(String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      zzawy().zzazd().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
      return false;
    }
    zzbq.checkNotNull(paramString);
    if (!paramString.matches("^1:\\d+:android:[a-f0-9]+$"))
    {
      zzawy().zzazd().zzj("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", paramString);
      return false;
    }
    return true;
  }
  
  public final boolean zzkj(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    return zzaxa().zzayd().equals(paramString);
  }
  
  final boolean zzkl(String paramString)
  {
    return "1".equals(zzawv().zzam(paramString, "measurement.upload.blacklist_internal"));
  }
  
  final boolean zzkm(String paramString)
  {
    return "1".equals(zzawv().zzam(paramString, "measurement.upload.blacklist_public"));
  }
  
  public final int zzl(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (boolean bool = zza("user property referrer", paramString, zzkh(paramString), paramObject, false); bool; bool = zza("user property", paramString, zzkh(paramString), paramObject, false)) {
      return 0;
    }
    return 7;
  }
  
  public final Object zzm(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {
      return zza(zzkh(paramString), paramObject, true);
    }
    return zza(zzkh(paramString), paramObject, false);
  }
  
  public final Bundle zzp(Uri paramUri)
  {
    Object localObject = null;
    if (paramUri == null) {
      return (Bundle)localObject;
    }
    for (;;)
    {
      try
      {
        if (paramUri.isHierarchical())
        {
          str4 = paramUri.getQueryParameter("utm_campaign");
          str3 = paramUri.getQueryParameter("utm_source");
          str2 = paramUri.getQueryParameter("utm_medium");
          str1 = paramUri.getQueryParameter("gclid");
          if ((TextUtils.isEmpty(str4)) && (TextUtils.isEmpty(str3)) && (TextUtils.isEmpty(str2)) && (TextUtils.isEmpty(str1))) {
            break;
          }
          Bundle localBundle = new Bundle();
          if (!TextUtils.isEmpty(str4)) {
            localBundle.putString("campaign", str4);
          }
          if (!TextUtils.isEmpty(str3)) {
            localBundle.putString("source", str3);
          }
          if (!TextUtils.isEmpty(str2)) {
            localBundle.putString("medium", str2);
          }
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("gclid", str1);
          }
          str1 = paramUri.getQueryParameter("utm_term");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("term", str1);
          }
          str1 = paramUri.getQueryParameter("utm_content");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("content", str1);
          }
          str1 = paramUri.getQueryParameter("aclid");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("aclid", str1);
          }
          str1 = paramUri.getQueryParameter("cp1");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("cp1", str1);
          }
          paramUri = paramUri.getQueryParameter("anid");
          localObject = localBundle;
          if (TextUtils.isEmpty(paramUri)) {
            break;
          }
          localBundle.putString("anid", paramUri);
          return localBundle;
        }
      }
      catch (UnsupportedOperationException paramUri)
      {
        zzawy().zzazf().zzj("Install referrer url isn't a hierarchical URI", paramUri);
        return null;
      }
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
    }
  }
  
  public final byte[] zzq(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramArrayOfByte);
      localGZIPOutputStream.close();
      localByteArrayOutputStream.close();
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzawy().zzazd().zzj("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public final byte[] zzr(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream;
    try
    {
      paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
      GZIPInputStream localGZIPInputStream = new GZIPInputStream(paramArrayOfByte);
      localByteArrayOutputStream = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        int i = localGZIPInputStream.read(arrayOfByte);
        if (i <= 0) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localGZIPInputStream.close();
    }
    catch (IOException paramArrayOfByte)
    {
      zzawy().zzazd().zzj("Failed to ungzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
    paramArrayOfByte.close();
    paramArrayOfByte = localByteArrayOutputStream.toByteArray();
    return paramArrayOfByte;
  }
  
  final Bundle zzy(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = zzk(str, paramBundle.get(str));
        if (localObject == null) {
          zzawy().zzazf().zzj("Param value can't be null", zzawt().zzji(str));
        } else {
          zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */