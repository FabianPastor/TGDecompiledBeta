package com.google.android.gms.internal.measurement;

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
import android.text.TextUtils;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

public final class zzjv
  extends zzhk
{
  private static final String[] zzaqy = { "firebase_", "google_", "ga_" };
  private SecureRandom zzaqz;
  private final AtomicLong zzara = new AtomicLong(0L);
  private int zzarb;
  private Integer zzarc = null;
  
  zzjv(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  static MessageDigest getMessageDigest(String paramString)
  {
    int i = 0;
    if (i < 2) {}
    for (;;)
    {
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
        if (localMessageDigest != null) {
          return localMessageDigest;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        i++;
      }
      break;
      Object localObject = null;
    }
  }
  
  public static zzkj zza(zzki paramzzki, String paramString)
  {
    zzkj[] arrayOfzzkj = paramzzki.zzasv;
    int i = arrayOfzzkj.length;
    int j = 0;
    if (j < i)
    {
      paramzzki = arrayOfzzkj[j];
      if (!paramzzki.name.equals(paramString)) {}
    }
    for (;;)
    {
      return paramzzki;
      j++;
      break;
      paramzzki = null;
    }
  }
  
  private static Object zza(int paramInt, Object paramObject, boolean paramBoolean)
  {
    Object localObject;
    if (paramObject == null) {
      localObject = null;
    }
    for (;;)
    {
      return localObject;
      localObject = paramObject;
      if (!(paramObject instanceof Long))
      {
        localObject = paramObject;
        if (!(paramObject instanceof Double)) {
          if ((paramObject instanceof Integer))
          {
            localObject = Long.valueOf(((Integer)paramObject).intValue());
          }
          else if ((paramObject instanceof Byte))
          {
            localObject = Long.valueOf(((Byte)paramObject).byteValue());
          }
          else if ((paramObject instanceof Short))
          {
            localObject = Long.valueOf(((Short)paramObject).shortValue());
          }
          else
          {
            if ((paramObject instanceof Boolean))
            {
              if (((Boolean)paramObject).booleanValue()) {}
              for (long l = 1L;; l = 0L)
              {
                localObject = Long.valueOf(l);
                break;
              }
            }
            if ((paramObject instanceof Float)) {
              localObject = Double.valueOf(((Float)paramObject).doubleValue());
            } else if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence))) {
              localObject = zza(String.valueOf(paramObject), paramInt, paramBoolean);
            } else {
              localObject = null;
            }
          }
        }
      }
    }
  }
  
  public static String zza(String paramString, int paramInt, boolean paramBoolean)
  {
    String str = paramString;
    if (paramString.codePointCount(0, paramString.length()) > paramInt) {
      if (!paramBoolean) {
        break label41;
      }
    }
    label41:
    for (str = String.valueOf(paramString.substring(0, paramString.offsetByCodePoints(0, paramInt))).concat("...");; str = null) {
      return str;
    }
  }
  
  public static String zza(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    Preconditions.checkNotNull(paramArrayOfString1);
    Preconditions.checkNotNull(paramArrayOfString2);
    int i = Math.min(paramArrayOfString1.length, paramArrayOfString2.length);
    int j = 0;
    if (j < i) {
      if (!zzs(paramString, paramArrayOfString1[j])) {}
    }
    for (paramString = paramArrayOfString2[j];; paramString = null)
    {
      return paramString;
      j++;
      break;
    }
  }
  
  private static void zza(Bundle paramBundle, Object paramObject)
  {
    Preconditions.checkNotNull(paramBundle);
    if ((paramObject != null) && (((paramObject instanceof String)) || ((paramObject instanceof CharSequence)))) {
      paramBundle.putLong("_el", String.valueOf(paramObject).length());
    }
  }
  
  private static boolean zza(Bundle paramBundle, int paramInt)
  {
    if (paramBundle.getLong("_err") == 0L) {
      paramBundle.putLong("_err", paramInt);
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject, boolean paramBoolean)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramObject == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (!(paramObject instanceof Long))
      {
        bool2 = bool1;
        if (!(paramObject instanceof Float))
        {
          bool2 = bool1;
          if (!(paramObject instanceof Integer))
          {
            bool2 = bool1;
            if (!(paramObject instanceof Byte))
            {
              bool2 = bool1;
              if (!(paramObject instanceof Short))
              {
                bool2 = bool1;
                if (!(paramObject instanceof Boolean))
                {
                  bool2 = bool1;
                  if (!(paramObject instanceof Double)) {
                    if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence)))
                    {
                      paramObject = String.valueOf(paramObject);
                      bool2 = bool1;
                      if (((String)paramObject).codePointCount(0, ((String)paramObject).length()) > paramInt)
                      {
                        zzgg().zzin().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
                        bool2 = false;
                      }
                    }
                    else if ((paramObject instanceof Bundle))
                    {
                      bool2 = bool1;
                      if (paramBoolean) {}
                    }
                    else
                    {
                      int i;
                      if (((paramObject instanceof Parcelable[])) && (paramBoolean))
                      {
                        paramObject = (Parcelable[])paramObject;
                        i = paramObject.length;
                        for (paramInt = 0;; paramInt++)
                        {
                          bool2 = bool1;
                          if (paramInt >= i) {
                            break;
                          }
                          paramString1 = paramObject[paramInt];
                          if (!(paramString1 instanceof Bundle))
                          {
                            zzgg().zzin().zze("All Parcelable[] elements must be of type Bundle. Value type, name", paramString1.getClass(), paramString2);
                            bool2 = false;
                            break;
                          }
                        }
                      }
                      if (((paramObject instanceof ArrayList)) && (paramBoolean))
                      {
                        paramString1 = (ArrayList)paramObject;
                        i = paramString1.size();
                        paramInt = 0;
                        do
                        {
                          bool2 = bool1;
                          if (paramInt >= i) {
                            break;
                          }
                          paramObject = paramString1.get(paramInt);
                          paramInt++;
                        } while ((paramObject instanceof Bundle));
                        zzgg().zzin().zze("All ArrayList elements must be of type Bundle. Value type, name", paramObject.getClass(), paramString2);
                        bool2 = false;
                      }
                      else
                      {
                        bool2 = false;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public static boolean zza(long[] paramArrayOfLong, int paramInt)
  {
    boolean bool = false;
    if (paramInt >= paramArrayOfLong.length << 6) {}
    for (;;)
    {
      return bool;
      if ((paramArrayOfLong[(paramInt / 64)] & 1L << paramInt % 64) != 0L) {
        bool = true;
      }
    }
  }
  
  static byte[] zza(Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      paramParcelable = null;
    }
    for (;;)
    {
      return paramParcelable;
      Parcel localParcel = Parcel.obtain();
      try
      {
        paramParcelable.writeToParcel(localParcel, 0);
        paramParcelable = localParcel.marshall();
        localParcel.recycle();
      }
      finally
      {
        localParcel.recycle();
      }
    }
  }
  
  public static long[] zza(BitSet paramBitSet)
  {
    int i = (paramBitSet.length() + 63) / 64;
    long[] arrayOfLong = new long[i];
    for (int j = 0; j < i; j++)
    {
      arrayOfLong[j] = 0L;
      for (int k = 0; (k < 64) && ((j << 6) + k < paramBitSet.length()); k++) {
        if (paramBitSet.get((j << 6) + k)) {
          arrayOfLong[j] |= 1L << k;
        }
      }
    }
    return arrayOfLong;
  }
  
  static zzkj[] zza(zzkj[] paramArrayOfzzkj, String paramString, Object paramObject)
  {
    int i = paramArrayOfzzkj.length;
    for (int j = 0; j < i; j++)
    {
      localObject = paramArrayOfzzkj[j];
      if (paramString.equals(((zzkj)localObject).name))
      {
        ((zzkj)localObject).zzasz = null;
        ((zzkj)localObject).zzajf = null;
        ((zzkj)localObject).zzaqx = null;
        if ((paramObject instanceof Long))
        {
          ((zzkj)localObject).zzasz = ((Long)paramObject);
          paramString = paramArrayOfzzkj;
        }
        for (;;)
        {
          return paramString;
          if ((paramObject instanceof String))
          {
            ((zzkj)localObject).zzajf = ((String)paramObject);
            paramString = paramArrayOfzzkj;
          }
          else
          {
            paramString = paramArrayOfzzkj;
            if ((paramObject instanceof Double))
            {
              ((zzkj)localObject).zzaqx = ((Double)paramObject);
              paramString = paramArrayOfzzkj;
            }
          }
        }
      }
    }
    Object localObject = new zzkj[paramArrayOfzzkj.length + 1];
    System.arraycopy(paramArrayOfzzkj, 0, localObject, 0, paramArrayOfzzkj.length);
    zzkj localzzkj = new zzkj();
    localzzkj.name = paramString;
    if ((paramObject instanceof Long)) {
      localzzkj.zzasz = ((Long)paramObject);
    }
    for (;;)
    {
      localObject[paramArrayOfzzkj.length] = localzzkj;
      paramString = (String)localObject;
      break;
      if ((paramObject instanceof String)) {
        localzzkj.zzajf = ((String)paramObject);
      } else if ((paramObject instanceof Double)) {
        localzzkj.zzaqx = ((Double)paramObject);
      }
    }
  }
  
  public static Object zzb(zzki paramzzki, String paramString)
  {
    paramzzki = zza(paramzzki, paramString);
    if (paramzzki != null) {
      if (paramzzki.zzajf != null) {
        paramzzki = paramzzki.zzajf;
      }
    }
    for (;;)
    {
      return paramzzki;
      if (paramzzki.zzasz != null) {
        paramzzki = paramzzki.zzasz;
      } else if (paramzzki.zzaqx != null) {
        paramzzki = paramzzki.zzaqx;
      } else {
        paramzzki = null;
      }
    }
  }
  
  static boolean zzbv(String paramString)
  {
    boolean bool = false;
    Preconditions.checkNotEmpty(paramString);
    if ((paramString.charAt(0) != '_') || (paramString.equals("_ep"))) {
      bool = true;
    }
    return bool;
  }
  
  static long zzc(byte[] paramArrayOfByte)
  {
    int i = 0;
    Preconditions.checkNotNull(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      l = 0L;
      for (int j = paramArrayOfByte.length - 1; (j >= 0) && (j >= paramArrayOfByte.length - 8); j--)
      {
        l += ((paramArrayOfByte[j] & 0xFF) << i);
        i += 8;
      }
    }
    return l;
  }
  
  public static boolean zzc(Context paramContext, String paramString)
  {
    bool1 = false;
    for (;;)
    {
      try
      {
        localPackageManager = paramContext.getPackageManager();
        if (localPackageManager != null) {
          continue;
        }
        bool2 = bool1;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        PackageManager localPackageManager;
        ComponentName localComponentName;
        boolean bool3;
        boolean bool2 = bool1;
        continue;
      }
      return bool2;
      localComponentName = new android/content/ComponentName;
      localComponentName.<init>(paramContext, paramString);
      paramContext = localPackageManager.getServiceInfo(localComponentName, 0);
      bool2 = bool1;
      if (paramContext != null)
      {
        bool3 = paramContext.enabled;
        bool2 = bool1;
        if (bool3) {
          bool2 = true;
        }
      }
    }
  }
  
  private static int zzca(String paramString)
  {
    int i;
    if ("_ldl".equals(paramString)) {
      i = 2048;
    }
    for (;;)
    {
      return i;
      if ("_id".equals(paramString)) {
        i = 256;
      } else {
        i = 36;
      }
    }
  }
  
  public static boolean zzcb(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  static boolean zzcd(String paramString)
  {
    if ((paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  static boolean zzcg(String paramString)
  {
    boolean bool = true;
    Preconditions.checkNotEmpty(paramString);
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
  
  public static boolean zzd(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("android.intent.extra.REFERRER_NAME");
    if (("android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(paramIntent)) || ("https://www.google.com".equals(paramIntent)) || ("android-app://com.google.appcrawler".equals(paramIntent))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private final boolean zze(Context paramContext, String paramString)
  {
    X500Principal localX500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
    try
    {
      paramContext = Wrappers.packageManager(paramContext).getPackageInfo(paramString, 64);
      if ((paramContext == null) || (paramContext.signatures == null) || (paramContext.signatures.length <= 0)) {
        break label105;
      }
      paramContext = paramContext.signatures[0];
      paramString = CertificateFactory.getInstance("X.509");
      ByteArrayInputStream localByteArrayInputStream = new java/io/ByteArrayInputStream;
      localByteArrayInputStream.<init>(paramContext.toByteArray());
      bool = ((X509Certificate)paramString.generateCertificate(localByteArrayInputStream)).getSubjectX500Principal().equals(localX500Principal);
    }
    catch (CertificateException paramContext)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Error obtaining certificate", paramContext);
        boolean bool = true;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Package name not found", paramContext);
      }
    }
    return bool;
  }
  
  public static Bundle[] zze(Object paramObject)
  {
    if ((paramObject instanceof Bundle))
    {
      Bundle[] arrayOfBundle = new Bundle[1];
      arrayOfBundle[0] = ((Bundle)paramObject);
      paramObject = arrayOfBundle;
    }
    for (;;)
    {
      return (Bundle[])paramObject;
      if ((paramObject instanceof Parcelable[]))
      {
        paramObject = (Bundle[])Arrays.copyOf((Parcelable[])paramObject, ((Parcelable[])paramObject).length, Bundle[].class);
      }
      else if ((paramObject instanceof ArrayList))
      {
        paramObject = (ArrayList)paramObject;
        paramObject = (Bundle[])((ArrayList)paramObject).toArray(new Bundle[((ArrayList)paramObject).size()]);
      }
      else
      {
        paramObject = null;
      }
    }
  }
  
  /* Error */
  public static Object zzf(Object paramObject)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aload_0
    //   3: ifnonnull +7 -> 10
    //   6: aload_1
    //   7: astore_0
    //   8: aload_0
    //   9: areturn
    //   10: new 451	java/io/ByteArrayOutputStream
    //   13: astore_2
    //   14: aload_2
    //   15: invokespecial 452	java/io/ByteArrayOutputStream:<init>	()V
    //   18: new 454	java/io/ObjectOutputStream
    //   21: astore_3
    //   22: aload_3
    //   23: aload_2
    //   24: invokespecial 457	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   27: aload_3
    //   28: aload_0
    //   29: invokevirtual 461	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   32: aload_3
    //   33: invokevirtual 464	java/io/ObjectOutputStream:flush	()V
    //   36: new 466	java/io/ObjectInputStream
    //   39: astore 4
    //   41: new 401	java/io/ByteArrayInputStream
    //   44: astore_0
    //   45: aload_0
    //   46: aload_2
    //   47: invokevirtual 467	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   50: invokespecial 409	java/io/ByteArrayInputStream:<init>	([B)V
    //   53: aload 4
    //   55: aload_0
    //   56: invokespecial 470	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   59: aload 4
    //   61: invokevirtual 474	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   64: astore_0
    //   65: aload_3
    //   66: invokevirtual 477	java/io/ObjectOutputStream:close	()V
    //   69: aload 4
    //   71: invokevirtual 478	java/io/ObjectInputStream:close	()V
    //   74: goto -66 -> 8
    //   77: astore_0
    //   78: aconst_null
    //   79: astore 4
    //   81: aconst_null
    //   82: astore_3
    //   83: aload_3
    //   84: ifnull +7 -> 91
    //   87: aload_3
    //   88: invokevirtual 477	java/io/ObjectOutputStream:close	()V
    //   91: aload 4
    //   93: ifnull +8 -> 101
    //   96: aload 4
    //   98: invokevirtual 478	java/io/ObjectInputStream:close	()V
    //   101: aload_0
    //   102: athrow
    //   103: astore_0
    //   104: aload_1
    //   105: astore_0
    //   106: goto -98 -> 8
    //   109: astore_0
    //   110: aload_1
    //   111: astore_0
    //   112: goto -104 -> 8
    //   115: astore_0
    //   116: aconst_null
    //   117: astore 4
    //   119: goto -36 -> 83
    //   122: astore_0
    //   123: goto -40 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	paramObject	Object
    //   1	110	1	localObject	Object
    //   13	34	2	localByteArrayOutputStream	ByteArrayOutputStream
    //   21	67	3	localObjectOutputStream	java.io.ObjectOutputStream
    //   39	79	4	localObjectInputStream	java.io.ObjectInputStream
    // Exception table:
    //   from	to	target	type
    //   10	27	77	finally
    //   65	74	103	java/io/IOException
    //   87	91	103	java/io/IOException
    //   96	101	103	java/io/IOException
    //   101	103	103	java/io/IOException
    //   65	74	109	java/lang/ClassNotFoundException
    //   87	91	109	java/lang/ClassNotFoundException
    //   96	101	109	java/lang/ClassNotFoundException
    //   101	103	109	java/lang/ClassNotFoundException
    //   27	59	115	finally
    //   59	65	122	finally
  }
  
  private final boolean zzr(String paramString1, String paramString2)
  {
    boolean bool = false;
    if (paramString2 == null) {
      zzgg().zzil().zzg("Name is required and can't be null. Type", paramString1);
    }
    for (;;)
    {
      return bool;
      if (paramString2.length() == 0)
      {
        zzgg().zzil().zzg("Name is required and can't be empty. Type", paramString1);
      }
      else
      {
        int i = paramString2.codePointAt(0);
        if ((!Character.isLetter(i)) && (i != 95))
        {
          zzgg().zzil().zze("Name must start with a letter or _ (underscore). Type, name", paramString1, paramString2);
        }
        else
        {
          int j = paramString2.length();
          i = Character.charCount(i);
          for (;;)
          {
            if (i >= j) {
              break label160;
            }
            int k = paramString2.codePointAt(i);
            if ((k != 95) && (!Character.isLetterOrDigit(k)))
            {
              zzgg().zzil().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
              break;
            }
            i += Character.charCount(k);
          }
          label160:
          bool = true;
        }
      }
    }
  }
  
  public static boolean zzs(String paramString1, String paramString2)
  {
    boolean bool;
    if ((paramString1 == null) && (paramString2 == null)) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if (paramString1 == null) {
        bool = false;
      } else {
        bool = paramString1.equals(paramString2);
      }
    }
  }
  
  public final Bundle zza(Uri paramUri)
  {
    Object localObject = null;
    if (paramUri == null) {
      return (Bundle)localObject;
    }
    for (;;)
    {
      try
      {
        if (!paramUri.isHierarchical()) {
          break label307;
        }
        str1 = paramUri.getQueryParameter("utm_campaign");
        str2 = paramUri.getQueryParameter("utm_source");
        str3 = paramUri.getQueryParameter("utm_medium");
        str4 = paramUri.getQueryParameter("gclid");
        if ((TextUtils.isEmpty(str1)) && (TextUtils.isEmpty(str2)) && (TextUtils.isEmpty(str3)) && (TextUtils.isEmpty(str4))) {
          break;
        }
        Bundle localBundle = new Bundle();
        if (!TextUtils.isEmpty(str1)) {
          localBundle.putString("campaign", str1);
        }
        if (!TextUtils.isEmpty(str2)) {
          localBundle.putString("source", str2);
        }
        if (!TextUtils.isEmpty(str3)) {
          localBundle.putString("medium", str3);
        }
        if (!TextUtils.isEmpty(str4)) {
          localBundle.putString("gclid", str4);
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
        localObject = localBundle;
      }
      catch (UnsupportedOperationException paramUri)
      {
        zzgg().zzin().zzg("Install referrer url isn't a hierarchical URI", paramUri);
      }
      break;
      label307:
      String str4 = null;
      String str3 = null;
      String str2 = null;
      String str1 = null;
    }
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
        String str = (String)localIterator.next();
        int j = 0;
        int k = 0;
        if ((paramList == null) || (!paramList.contains(str)))
        {
          if (paramBoolean1)
          {
            if (zzq("event param", str)) {
              break label172;
            }
            k = 3;
          }
          label94:
          j = k;
          if (k == 0)
          {
            if (zzr("event param", str)) {
              break label218;
            }
            j = 3;
          }
        }
        for (;;)
        {
          if (j == 0) {
            break label264;
          }
          if (zza(localBundle, j))
          {
            localBundle.putString("_ev", zza(str, 40, true));
            if (j == 3) {
              zza(localBundle, str);
            }
          }
          localBundle.remove(str);
          break;
          label172:
          if (!zza("event param", null, str))
          {
            k = 14;
            break label94;
          }
          if (!zza("event param", 40, str))
          {
            k = 3;
            break label94;
          }
          k = 0;
          break label94;
          label218:
          if (!zza("event param", null, str)) {
            j = 14;
          } else if (!zza("event param", 40, str)) {
            j = 3;
          } else {
            j = 0;
          }
        }
        label264:
        Object localObject = paramBundle.get(str);
        zzab();
        if (paramBoolean2) {
          if ((localObject instanceof Parcelable[]))
          {
            j = ((Parcelable[])localObject).length;
            label297:
            if (j <= 1000) {
              break label430;
            }
            zzgg().zzin().zzd("Parameter array is too long; discarded. Value kind, name, array length", "param", str, Integer.valueOf(j));
            j = 0;
            label331:
            if (j != 0) {
              break label436;
            }
            j = 17;
          }
        }
        for (;;)
        {
          if ((j == 0) || ("_ev".equals(str))) {
            break label526;
          }
          if (zza(localBundle, j))
          {
            localBundle.putString("_ev", zza(str, 40, true));
            zza(localBundle, paramBundle.get(str));
          }
          localBundle.remove(str);
          break;
          if ((localObject instanceof ArrayList))
          {
            j = ((ArrayList)localObject).size();
            break label297;
          }
          j = 1;
          break label331;
          label430:
          j = 1;
          break label331;
          label436:
          if (((zzgi().zzd(zzfv().zzah(), zzew.zzahz)) && (zzcb(paramString))) || (zzcb(str))) {}
          for (boolean bool = zza("param", str, 256, localObject, paramBoolean2);; bool = zza("param", str, 100, localObject, paramBoolean2))
          {
            if (!bool) {
              break label520;
            }
            j = 0;
            break;
          }
          label520:
          j = 4;
        }
        label526:
        if (zzbv(str))
        {
          k = i + 1;
          j = k;
          if (k > 25)
          {
            localObject = 48 + "Event can't contain more than 25 params";
            zzgg().zzil().zze((String)localObject, zzgb().zzbe(paramString), zzgb().zzb(paramBundle));
            zza(localBundle, 5);
            localBundle.remove(str);
            i = k;
          }
        }
        else
        {
          j = i;
        }
        i = j;
      }
    }
    return localBundle;
  }
  
  final zzeu zza(String paramString1, Bundle paramBundle, String paramString2, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (TextUtils.isEmpty(paramString1))
    {
      paramString1 = null;
      return paramString1;
    }
    if (zzbw(paramString1) != 0)
    {
      zzgg().zzil().zzg("Invalid conditional property event name", zzgb().zzbg(paramString1));
      throw new IllegalArgumentException();
    }
    if (paramBundle != null) {}
    for (paramBundle = new Bundle(paramBundle);; paramBundle = new Bundle())
    {
      paramBundle.putString("_o", paramString2);
      paramString1 = new zzeu(paramString1, new zzer(zzd(zza(paramString1, paramBundle, CollectionUtils.listOf("_o"), false, false))), paramString2, paramLong);
      break;
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
      for (;;)
      {
        return;
        if ((paramObject instanceof Long))
        {
          paramBundle.putLong(paramString, ((Long)paramObject).longValue());
        }
        else if ((paramObject instanceof String))
        {
          paramBundle.putString(paramString, String.valueOf(paramObject));
        }
        else
        {
          if (!(paramObject instanceof Double)) {
            break;
          }
          paramBundle.putDouble(paramString, ((Double)paramObject).doubleValue());
        }
      }
    } while (paramString == null);
    if (paramObject != null) {}
    for (paramBundle = paramObject.getClass().getSimpleName();; paramBundle = null)
    {
      zzgg().zzio().zze("Not putting event parameter. Invalid value type. name, type", zzgb().zzbf(paramString), paramBundle);
      break;
    }
  }
  
  public final void zza(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
  {
    paramString1 = new Bundle();
    zza(paramString1, paramInt1);
    if (!TextUtils.isEmpty(paramString2)) {
      paramString1.putString(paramString2, paramString3);
    }
    if ((paramInt1 == 6) || (paramInt1 == 7) || (paramInt1 == 2)) {
      paramString1.putLong("_el", paramInt2);
    }
    this.zzacr.zzfu().zza("auto", "_err", paramString1);
  }
  
  public final boolean zza(long paramLong1, long paramLong2)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (paramLong1 != 0L)
    {
      if (paramLong2 > 0L) {
        break label26;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label26:
      bool2 = bool1;
      if (Math.abs(zzbt().currentTimeMillis() - paramLong1) <= paramLong2) {
        bool2 = false;
      }
    }
  }
  
  final boolean zza(String paramString1, int paramInt, String paramString2)
  {
    boolean bool = false;
    if (paramString2 == null) {
      zzgg().zzil().zzg("Name is required and can't be null. Type", paramString1);
    }
    for (;;)
    {
      return bool;
      if (paramString2.codePointCount(0, paramString2.length()) > paramInt) {
        zzgg().zzil().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      } else {
        bool = true;
      }
    }
  }
  
  final boolean zza(String paramString1, String[] paramArrayOfString, String paramString2)
  {
    boolean bool = false;
    if (paramString2 == null) {
      zzgg().zzil().zzg("Name is required and can't be null. Type", paramString1);
    }
    for (;;)
    {
      return bool;
      Preconditions.checkNotNull(paramString2);
      int i = 0;
      label32:
      if (i < zzaqy.length) {
        if (!paramString2.startsWith(zzaqy[i])) {}
      }
      for (i = 1;; i = 0)
      {
        if (i == 0) {
          break label92;
        }
        zzgg().zzil().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
        break;
        i++;
        break label32;
      }
      label92:
      if (paramArrayOfString != null)
      {
        Preconditions.checkNotNull(paramArrayOfString);
        i = 0;
        label104:
        if (i < paramArrayOfString.length) {
          if (!zzs(paramString2, paramArrayOfString[i])) {}
        }
        for (i = 1;; i = 0)
        {
          if (i == 0) {
            break label160;
          }
          zzgg().zzil().zze("Name is reserved. Type, name", paramString1, paramString2);
          break;
          i++;
          break label104;
        }
      }
      label160:
      bool = true;
    }
  }
  
  public final byte[] zza(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new java/io/ByteArrayOutputStream;
      localByteArrayOutputStream.<init>();
      GZIPOutputStream localGZIPOutputStream = new java/util/zip/GZIPOutputStream;
      localGZIPOutputStream.<init>(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramArrayOfByte);
      localGZIPOutputStream.close();
      localByteArrayOutputStream.close();
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzgg().zzil().zzg("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public final byte[] zzb(zzkk paramzzkk)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzkk.zzwg()];
      zzabb localzzabb = zzabb.zzb(arrayOfByte, 0, arrayOfByte.length);
      paramzzkk.zza(localzzabb);
      localzzabb.zzvy();
      paramzzkk = arrayOfByte;
    }
    catch (IOException paramzzkk)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Data loss. Failed to serialize batch", paramzzkk);
        paramzzkk = null;
      }
    }
    return paramzzkk;
  }
  
  public final byte[] zzb(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayInputStream localByteArrayInputStream;
    ByteArrayOutputStream localByteArrayOutputStream;
    try
    {
      localByteArrayInputStream = new java/io/ByteArrayInputStream;
      localByteArrayInputStream.<init>(paramArrayOfByte);
      paramArrayOfByte = new java/util/zip/GZIPInputStream;
      paramArrayOfByte.<init>(localByteArrayInputStream);
      localByteArrayOutputStream = new java/io/ByteArrayOutputStream;
      localByteArrayOutputStream.<init>();
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        int i = paramArrayOfByte.read(arrayOfByte);
        if (i <= 0) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      paramArrayOfByte.close();
    }
    catch (IOException paramArrayOfByte)
    {
      zzgg().zzil().zzg("Failed to ungzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
    localByteArrayInputStream.close();
    paramArrayOfByte = localByteArrayOutputStream.toByteArray();
    return paramArrayOfByte;
  }
  
  public final int zzbw(String paramString)
  {
    int i = 2;
    if (!zzr("event", paramString)) {}
    for (;;)
    {
      return i;
      if (!zza("event", AppMeasurement.Event.zzacs, paramString)) {
        i = 13;
      } else if (zza("event", 40, paramString)) {
        i = 0;
      }
    }
  }
  
  public final int zzbx(String paramString)
  {
    int i = 6;
    if (!zzq("user property", paramString)) {}
    for (;;)
    {
      return i;
      if (!zza("user property", AppMeasurement.UserProperty.zzacw, paramString)) {
        i = 15;
      } else if (zza("user property", 24, paramString)) {
        i = 0;
      }
    }
  }
  
  public final int zzby(String paramString)
  {
    int i = 6;
    if (!zzr("user property", paramString)) {}
    for (;;)
    {
      return i;
      if (!zza("user property", AppMeasurement.UserProperty.zzacw, paramString)) {
        i = 15;
      } else if (zza("user property", 24, paramString)) {
        i = 0;
      }
    }
  }
  
  public final boolean zzbz(String paramString)
  {
    boolean bool = false;
    if (TextUtils.isEmpty(paramString)) {
      zzgg().zzil().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
    }
    for (;;)
    {
      return bool;
      Preconditions.checkNotNull(paramString);
      if (!paramString.matches("^1:\\d+:android:[a-f0-9]+$")) {
        zzgg().zzil().zzg("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", paramString);
      } else {
        bool = true;
      }
    }
  }
  
  public final boolean zzcc(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    for (boolean bool = false;; bool = zzgi().zzhl().equals(paramString)) {
      return bool;
    }
  }
  
  final boolean zzce(String paramString)
  {
    return "1".equals(zzgd().zzm(paramString, "measurement.upload.blacklist_internal"));
  }
  
  final boolean zzcf(String paramString)
  {
    return "1".equals(zzgd().zzm(paramString, "measurement.upload.blacklist_public"));
  }
  
  final long zzd(Context paramContext, String paramString)
  {
    long l = -1L;
    zzab();
    Preconditions.checkNotNull(paramContext);
    Preconditions.checkNotEmpty(paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    MessageDigest localMessageDigest = getMessageDigest("MD5");
    if (localMessageDigest == null) {
      zzgg().zzil().log("Could not get MD5 instance");
    }
    for (;;)
    {
      return l;
      if (localPackageManager != null) {
        try
        {
          if (!zze(paramContext, paramString))
          {
            paramContext = Wrappers.packageManager(paramContext).getPackageInfo(getContext().getPackageName(), 64);
            if ((paramContext.signatures != null) && (paramContext.signatures.length > 0))
            {
              l = zzc(localMessageDigest.digest(paramContext.signatures[0].toByteArray()));
              continue;
            }
            zzgg().zzin().log("Could not get signatures");
          }
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          zzgg().zzil().zzg("Package name not found", paramContext);
        }
      } else {
        l = 0L;
      }
    }
  }
  
  final Bundle zzd(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = zzh(str, paramBundle.get(str));
        if (localObject == null) {
          zzgg().zzin().zzg("Param value can't be null", zzgb().zzbf(str));
        } else {
          zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
  
  public final Object zzh(String paramString, Object paramObject)
  {
    int i = 256;
    if ("_ev".equals(paramString))
    {
      paramString = zza(256, paramObject, true);
      return paramString;
    }
    if (zzcb(paramString)) {}
    for (;;)
    {
      paramString = zza(i, paramObject, false);
      break;
      i = 100;
    }
  }
  
  protected final boolean zzhh()
  {
    return true;
  }
  
  public final int zzi(String paramString, Object paramObject)
  {
    int i = 0;
    boolean bool;
    if ("_ldl".equals(paramString))
    {
      bool = zza("user property referrer", paramString, zzca(paramString), paramObject, false);
      if (!bool) {
        break label54;
      }
    }
    for (;;)
    {
      return i;
      bool = zza("user property", paramString, zzca(paramString), paramObject, false);
      break;
      label54:
      i = 7;
    }
  }
  
  protected final void zzig()
  {
    zzab();
    SecureRandom localSecureRandom = new SecureRandom();
    long l1 = localSecureRandom.nextLong();
    long l2 = l1;
    if (l1 == 0L)
    {
      l1 = localSecureRandom.nextLong();
      l2 = l1;
      if (l1 == 0L)
      {
        zzgg().zzin().log("Utils falling back to Random for random id");
        l2 = l1;
      }
    }
    this.zzara.set(l2);
  }
  
  public final Object zzj(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (paramString = zza(zzca(paramString), paramObject, true);; paramString = zza(zzca(paramString), paramObject, false)) {
      return paramString;
    }
  }
  
  public final long zzkt()
  {
    if (this.zzara.get() == 0L) {}
    for (;;)
    {
      long l;
      synchronized (this.zzara)
      {
        Random localRandom = new java/util/Random;
        localRandom.<init>(System.nanoTime() ^ zzbt().currentTimeMillis());
        l = localRandom.nextLong();
        int i = this.zzarb + 1;
        this.zzarb = i;
        l += i;
        return l;
      }
      synchronized (this.zzara)
      {
        this.zzara.compareAndSet(-1L, 1L);
        l = this.zzara.getAndIncrement();
      }
    }
  }
  
  final SecureRandom zzku()
  {
    zzab();
    if (this.zzaqz == null) {
      this.zzaqz = new SecureRandom();
    }
    return this.zzaqz;
  }
  
  public final int zzkv()
  {
    if (this.zzarc == null) {
      this.zzarc = Integer.valueOf(GoogleApiAvailabilityLight.getInstance().getApkVersion(getContext()) / 1000);
    }
    return this.zzarc.intValue();
  }
  
  final boolean zzq(String paramString1, String paramString2)
  {
    boolean bool = false;
    if (paramString2 == null) {
      zzgg().zzil().zzg("Name is required and can't be null. Type", paramString1);
    }
    for (;;)
    {
      return bool;
      if (paramString2.length() == 0)
      {
        zzgg().zzil().zzg("Name is required and can't be empty. Type", paramString1);
      }
      else
      {
        int i = paramString2.codePointAt(0);
        if (!Character.isLetter(i))
        {
          zzgg().zzil().zze("Name must start with a letter. Type, name", paramString1, paramString2);
        }
        else
        {
          int j = paramString2.length();
          i = Character.charCount(i);
          for (;;)
          {
            if (i >= j) {
              break label153;
            }
            int k = paramString2.codePointAt(i);
            if ((k != 95) && (!Character.isLetterOrDigit(k)))
            {
              zzgg().zzil().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
              break;
            }
            i += Character.charCount(k);
          }
          label153:
          bool = true;
        }
      }
    }
  }
  
  public final boolean zzx(String paramString)
  {
    zzab();
    if (Wrappers.packageManager(getContext()).checkCallingOrSelfPermission(paramString) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      return bool;
      zzgg().zziq().zzg("Permission not granted", paramString);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */