package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.internal.zzwa.zza;
import com.google.android.gms.internal.zzwa.zzb;
import com.google.android.gms.internal.zzwa.zzc;
import com.google.android.gms.internal.zzwa.zzd;
import com.google.android.gms.internal.zzwa.zze;
import com.google.android.gms.internal.zzwa.zzf;
import com.google.android.gms.internal.zzwc.zza;
import com.google.android.gms.internal.zzwc.zzb;
import com.google.android.gms.internal.zzwc.zzc;
import com.google.android.gms.internal.zzwc.zzf;
import com.google.android.gms.internal.zzwc.zzg;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zze;
import com.google.android.gms.measurement.AppMeasurement.zzg;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class zzc
  extends zzaa
{
  zzc(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private Boolean zza(zzwa.zzb paramzzb, zzwc.zzb paramzzb1, long paramLong)
  {
    if (paramzzb.awh != null)
    {
      localObject1 = zza(paramLong, paramzzb.awh);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzb.awf;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzwa.zzc)localObject3).awm))
      {
        zzbwb().zzbxa().zzj("null or empty param name in filter. event", paramzzb1.name);
        return null;
      }
      ((Set)localObject2).add(((zzwa.zzc)localObject3).awm);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzb1.awN;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzwc.zzc)localObject4).name))
      {
        if (((zzwc.zzc)localObject4).awR == null) {
          break label213;
        }
        ((Map)localObject1).put(((zzwc.zzc)localObject4).name, ((zzwc.zzc)localObject4).awR);
      }
      for (;;)
      {
        i += 1;
        break;
        label213:
        if (((zzwc.zzc)localObject4).avW != null)
        {
          ((Map)localObject1).put(((zzwc.zzc)localObject4).name, ((zzwc.zzc)localObject4).avW);
        }
        else
        {
          if (((zzwc.zzc)localObject4).Fe == null) {
            break label271;
          }
          ((Map)localObject1).put(((zzwc.zzc)localObject4).name, ((zzwc.zzc)localObject4).Fe);
        }
      }
      label271:
      zzbwb().zzbxa().zze("Unknown value for param. event, param", paramzzb1.name, ((zzwc.zzc)localObject4).name);
      return null;
    }
    paramzzb = paramzzb.awf;
    int k = paramzzb.length;
    i = 0;
    while (i < k)
    {
      localObject2 = paramzzb[i];
      int m = Boolean.TRUE.equals(((zzwa.zzc)localObject2).awl);
      localObject3 = ((zzwa.zzc)localObject2).awm;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzbwb().zzbxa().zzj("Event has empty param name. event", paramzzb1.name);
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (((zzwa.zzc)localObject2).awk == null)
        {
          zzbwb().zzbxa().zze("No number filter for long param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza(((Long)localObject4).longValue(), ((zzwa.zzc)localObject2).awk);
        if (localObject2 == null) {
          return null;
        }
        if (!((Boolean)localObject2).booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof Double))
      {
        if (((zzwa.zzc)localObject2).awk == null)
        {
          zzbwb().zzbxa().zze("No number filter for double param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza(((Double)localObject4).doubleValue(), ((zzwa.zzc)localObject2).awk);
        if (localObject2 == null) {
          return null;
        }
        if (!((Boolean)localObject2).booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof String))
      {
        if (((zzwa.zzc)localObject2).awj == null)
        {
          zzbwb().zzbxa().zze("No string filter for String param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza((String)localObject4, ((zzwa.zzc)localObject2).awj);
        if (localObject2 == null) {
          return null;
        }
        if (!((Boolean)localObject2).booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if (localObject4 == null)
      {
        zzbwb().zzbxe().zze("Missing param for filter. event, param", paramzzb1.name, localObject3);
        return Boolean.valueOf(false);
      }
      zzbwb().zzbxa().zze("Unknown param type. event, param", paramzzb1.name, localObject3);
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private Boolean zza(zzwa.zze paramzze, zzwc.zzg paramzzg)
  {
    paramzze = paramzze.awu;
    if (paramzze == null)
    {
      zzbwb().zzbxa().zzj("Missing property filter. property", paramzzg.name);
      return null;
    }
    boolean bool = Boolean.TRUE.equals(paramzze.awl);
    if (paramzzg.awR != null)
    {
      if (paramzze.awk == null)
      {
        zzbwb().zzbxa().zzj("No number filter for long property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.awR.longValue(), paramzze.awk), bool);
    }
    if (paramzzg.avW != null)
    {
      if (paramzze.awk == null)
      {
        zzbwb().zzbxa().zzj("No number filter for double property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.avW.doubleValue(), paramzze.awk), bool);
    }
    if (paramzzg.Fe != null)
    {
      if (paramzze.awj == null)
      {
        if (paramzze.awk == null)
        {
          zzbwb().zzbxa().zzj("No string or number filter defined. property", paramzzg.name);
          return null;
        }
        if (zzal.zzng(paramzzg.Fe)) {
          return zza(zza(paramzzg.Fe, paramzze.awk), bool);
        }
        zzbwb().zzbxa().zze("Invalid user property value for Numeric number filter. property, value", paramzzg.name, paramzzg.Fe);
        return null;
      }
      return zza(zza(paramzzg.Fe, paramzze.awj), bool);
    }
    zzbwb().zzbxa().zzj("User property has no value, property", paramzzg.name);
    return null;
  }
  
  static Boolean zza(Boolean paramBoolean, boolean paramBoolean1)
  {
    if (paramBoolean == null) {
      return null;
    }
    return Boolean.valueOf(paramBoolean.booleanValue() ^ paramBoolean1);
  }
  
  private Boolean zza(String paramString1, int paramInt, boolean paramBoolean, String paramString2, List<String> paramList, String paramString3)
  {
    if (paramString1 == null) {}
    do
    {
      return null;
      if (paramInt != 6) {
        break;
      }
    } while ((paramList == null) || (paramList.size() == 0));
    String str = paramString1;
    if (!paramBoolean)
    {
      if (paramInt != 1) {
        break label113;
      }
      str = paramString1;
    }
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      if (paramBoolean) {}
      for (paramInt = 0;; paramInt = 66)
      {
        return Boolean.valueOf(Pattern.compile(paramString3, paramInt).matcher(str).matches());
        if (paramString2 != null) {
          break;
        }
        return null;
        str = paramString1.toUpperCase(Locale.ENGLISH);
        break label42;
      }
    case 2: 
      return Boolean.valueOf(str.startsWith(paramString2));
    case 3: 
      return Boolean.valueOf(str.endsWith(paramString2));
    case 4: 
      return Boolean.valueOf(str.contains(paramString2));
    case 5: 
      label42:
      label113:
      return Boolean.valueOf(str.equals(paramString2));
    }
    return Boolean.valueOf(paramList.contains(str));
  }
  
  private Boolean zza(BigDecimal paramBigDecimal1, int paramInt, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, BigDecimal paramBigDecimal4, double paramDouble)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool1 = true;
    if (paramBigDecimal1 == null) {
      return null;
    }
    if (paramInt == 4)
    {
      if ((paramBigDecimal3 == null) || (paramBigDecimal4 == null)) {
        return null;
      }
    }
    else if (paramBigDecimal2 == null) {
      return null;
    }
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == -1) {}
      for (;;)
      {
        return Boolean.valueOf(bool1);
        bool1 = false;
      }
    case 2: 
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == 1) {}
      for (bool1 = bool2;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    case 3: 
      if (paramDouble != 0.0D)
      {
        if ((paramBigDecimal1.compareTo(paramBigDecimal2.subtract(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == 1) && (paramBigDecimal1.compareTo(paramBigDecimal2.add(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == -1)) {}
        for (bool1 = bool3;; bool1 = false) {
          return Boolean.valueOf(bool1);
        }
      }
      if (paramBigDecimal1.compareTo(paramBigDecimal2) == 0) {}
      for (bool1 = bool4;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    }
    if ((paramBigDecimal1.compareTo(paramBigDecimal3) != -1) && (paramBigDecimal1.compareTo(paramBigDecimal4) != 1)) {}
    for (bool1 = bool5;; bool1 = false) {
      return Boolean.valueOf(bool1);
    }
  }
  
  private List<String> zza(String[] paramArrayOfString, boolean paramBoolean)
  {
    Object localObject;
    if (paramBoolean)
    {
      localObject = Arrays.asList(paramArrayOfString);
      return (List<String>)localObject;
    }
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfString.length;
    int i = 0;
    for (;;)
    {
      localObject = localArrayList;
      if (i >= j) {
        break;
      }
      localArrayList.add(paramArrayOfString[i].toUpperCase(Locale.ENGLISH));
      i += 1;
    }
  }
  
  public Boolean zza(double paramDouble, zzwa.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramDouble), paramzzd, Math.ulp(paramDouble));
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(long paramLong, zzwa.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramLong), paramzzd, 0.0D);
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(String paramString, zzwa.zzd paramzzd)
  {
    if (!zzal.zzng(paramString)) {
      return null;
    }
    try
    {
      paramString = zza(new BigDecimal(paramString), paramzzd, 0.0D);
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
  }
  
  Boolean zza(String paramString, zzwa.zzf paramzzf)
  {
    Object localObject = null;
    com.google.android.gms.common.internal.zzaa.zzy(paramzzf);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzf.awv == null) || (paramzzf.awv.intValue() == 0));
      if (paramzzf.awv.intValue() != 6) {
        break;
      }
    } while ((paramzzf.awy == null) || (paramzzf.awy.length == 0));
    int i = paramzzf.awv.intValue();
    boolean bool;
    label86:
    String str;
    if ((paramzzf.awx != null) && (paramzzf.awx.booleanValue()))
    {
      bool = true;
      if ((!bool) && (i != 1) && (i != 6)) {
        break label155;
      }
      str = paramzzf.aww;
      label108:
      if (paramzzf.awy != null) {
        break label170;
      }
    }
    label155:
    label170:
    for (paramzzf = null;; paramzzf = zza(paramzzf.awy, bool))
    {
      if (i == 1) {
        localObject = str;
      }
      return zza(paramString, i, bool, str, paramzzf, (String)localObject);
      if (paramzzf.aww != null) {
        break;
      }
      return null;
      bool = false;
      break label86;
      str = paramzzf.aww.toUpperCase(Locale.ENGLISH);
      break label108;
    }
  }
  
  Boolean zza(BigDecimal paramBigDecimal, zzwa.zzd paramzzd, double paramDouble)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramzzd);
    if ((paramzzd.awn == null) || (paramzzd.awn.intValue() == 0)) {}
    label49:
    int i;
    do
    {
      do
      {
        return null;
        if (paramzzd.awn.intValue() != 4) {
          break;
        }
      } while ((paramzzd.awq == null) || (paramzzd.awr == null));
      i = paramzzd.awn.intValue();
    } while ((paramzzd.awn.intValue() == 4) && ((!zzal.zzng(paramzzd.awq)) || (!zzal.zzng(paramzzd.awr))));
    for (;;)
    {
      BigDecimal localBigDecimal1;
      BigDecimal localBigDecimal2;
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzd.awq);
        localBigDecimal2 = new BigDecimal(paramzzd.awr);
        paramzzd = null;
        return zza(paramBigDecimal, i, paramzzd, localBigDecimal1, localBigDecimal2, paramDouble);
      }
      catch (NumberFormatException paramBigDecimal) {}
      if (paramzzd.awp != null) {
        break label49;
      }
      return null;
      if (!zzal.zzng(paramzzd.awp)) {
        break;
      }
      try
      {
        paramzzd = new BigDecimal(paramzzd.awp);
        localBigDecimal2 = null;
        localBigDecimal1 = null;
      }
      catch (NumberFormatException paramBigDecimal)
      {
        return null;
      }
    }
    return null;
  }
  
  @WorkerThread
  void zza(String paramString, zzwa.zza[] paramArrayOfzza)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramArrayOfzza);
    int m = paramArrayOfzza.length;
    int i = 0;
    while (i < m)
    {
      Object localObject1 = paramArrayOfzza[i];
      zzwa.zzb[] arrayOfzzb = ((zzwa.zza)localObject1).awb;
      int n = arrayOfzzb.length;
      int j = 0;
      Object localObject2;
      while (j < n)
      {
        localObject2 = arrayOfzzb[j];
        String str1 = (String)AppMeasurement.zza.aqx.get(((zzwa.zzb)localObject2).awe);
        if (str1 != null) {
          ((zzwa.zzb)localObject2).awe = str1;
        }
        localObject2 = ((zzwa.zzb)localObject2).awf;
        int i1 = localObject2.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject2[k];
          String str2 = (String)AppMeasurement.zze.aqy.get(str1.awm);
          if (str2 != null) {
            str1.awm = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject1 = ((zzwa.zza)localObject1).awa;
      int k = localObject1.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzb = localObject1[j];
        localObject2 = (String)AppMeasurement.zzg.aqC.get(arrayOfzzb.awt);
        if (localObject2 != null) {
          arrayOfzzb.awt = ((String)localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    zzbvw().zzb(paramString, paramArrayOfzza);
  }
  
  @WorkerThread
  zzwc.zza[] zza(String paramString, zzwc.zzb[] paramArrayOfzzb, zzwc.zzg[] paramArrayOfzzg)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzbvw().zzmd(paramString);
    Object localObject5;
    int j;
    Object localObject6;
    Object localObject3;
    Object localObject2;
    Object localObject1;
    int i;
    if (localObject4 != null)
    {
      localObject5 = ((Map)localObject4).keySet().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        j = ((Integer)((Iterator)localObject5).next()).intValue();
        localObject6 = (zzwc.zzf)((Map)localObject4).get(Integer.valueOf(j));
        localObject3 = (BitSet)localArrayMap2.get(Integer.valueOf(j));
        localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(j));
        localObject1 = localObject3;
        if (localObject3 == null)
        {
          localObject1 = new BitSet();
          localArrayMap2.put(Integer.valueOf(j), localObject1);
          localObject2 = new BitSet();
          localArrayMap3.put(Integer.valueOf(j), localObject2);
        }
        i = 0;
        while (i < ((zzwc.zzf)localObject6).axu.length * 64)
        {
          if (zzal.zza(((zzwc.zzf)localObject6).axu, i))
          {
            zzbwb().zzbxe().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzal.zza(((zzwc.zzf)localObject6).axv, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzwc.zza();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzwc.zza)localObject3).awL = Boolean.valueOf(false);
        ((zzwc.zza)localObject3).awK = ((zzwc.zzf)localObject6);
        ((zzwc.zza)localObject3).awJ = new zzwc.zzf();
        ((zzwc.zza)localObject3).awJ.axv = zzal.zza((BitSet)localObject1);
        ((zzwc.zza)localObject3).awJ.axu = zzal.zza((BitSet)localObject2);
      }
    }
    Object localObject7;
    long l;
    if (paramArrayOfzzb != null)
    {
      localObject6 = new ArrayMap();
      j = paramArrayOfzzb.length;
      i = 0;
      if (i < j)
      {
        localObject7 = paramArrayOfzzb[i];
        localObject1 = zzbvw().zzap(paramString, ((zzwc.zzb)localObject7).name);
        if (localObject1 == null)
        {
          zzbwb().zzbxa().zzj("Event aggregate wasn't created during raw event logging. event", ((zzwc.zzb)localObject7).name);
          localObject1 = new zzi(paramString, ((zzwc.zzb)localObject7).name, 1L, 1L, ((zzwc.zzb)localObject7).awO.longValue());
          zzbvw().zza((zzi)localObject1);
          l = ((zzi)localObject1).arD;
          localObject1 = (Map)((Map)localObject6).get(((zzwc.zzb)localObject7).name);
          if (localObject1 != null) {
            break label1914;
          }
          localObject2 = zzbvw().zzas(paramString, ((zzwc.zzb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzwc.zzb)localObject7).name, localObject1);
        }
      }
    }
    label1035:
    label1072:
    label1658:
    label1693:
    label1908:
    label1911:
    label1914:
    for (;;)
    {
      Iterator localIterator = ((Map)localObject1).keySet().iterator();
      int k;
      Object localObject8;
      Object localObject9;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label1072;
        }
        k = ((Integer)localIterator.next()).intValue();
        if (localHashSet.contains(Integer.valueOf(k)))
        {
          zzbwb().zzbxe().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzi)localObject1).zzbwv();
          break;
        }
        localObject4 = (zzwc.zza)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzwc.zza();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzwc.zza)localObject2).awL = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzwa.zzb)((Iterator)localObject8).next();
          if (zzbwb().zzbi(2))
          {
            zzbwb().zzbxe().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzwa.zzb)localObject9).awd, ((zzwa.zzb)localObject9).awe);
            zzbwb().zzbxe().zzj("Filter definition", zzal.zza((zzwa.zzb)localObject9));
          }
          if ((((zzwa.zzb)localObject9).awd == null) || (((zzwa.zzb)localObject9).awd.intValue() > 256))
          {
            zzbwb().zzbxa().zzj("Invalid event filter ID. id", String.valueOf(((zzwa.zzb)localObject9).awd));
          }
          else if (((BitSet)localObject2).get(((zzwa.zzb)localObject9).awd.intValue()))
          {
            zzbwb().zzbxe().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzwa.zzb)localObject9).awd);
          }
          else
          {
            localObject5 = zza((zzwa.zzb)localObject9, (zzwc.zzb)localObject7, l);
            zzq.zza localzza = zzbwb().zzbxe();
            if (localObject5 == null) {}
            for (localObject4 = "null";; localObject4 = localObject5)
            {
              localzza.zzj("Event filter result", localObject4);
              if (localObject5 != null) {
                break label1035;
              }
              localHashSet.add(Integer.valueOf(k));
              break;
            }
            ((BitSet)localObject3).set(((zzwa.zzb)localObject9).awd.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzwa.zzb)localObject9).awd.intValue());
            }
          }
        }
      }
      i += 1;
      break;
      if (paramArrayOfzzg != null)
      {
        localObject5 = new ArrayMap();
        j = paramArrayOfzzg.length;
        i = 0;
        if (i < j)
        {
          localObject6 = paramArrayOfzzg[i];
          paramArrayOfzzb = (Map)((Map)localObject5).get(((zzwc.zzg)localObject6).name);
          if (paramArrayOfzzb != null) {
            break label1911;
          }
          localObject1 = zzbvw().zzat(paramString, ((zzwc.zzg)localObject6).name);
          paramArrayOfzzb = (zzwc.zzb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzwc.zzg)localObject6).name, paramArrayOfzzb);
        }
      }
      for (;;)
      {
        localObject7 = paramArrayOfzzb.keySet().iterator();
        while (((Iterator)localObject7).hasNext())
        {
          k = ((Integer)((Iterator)localObject7).next()).intValue();
          if (localHashSet.contains(Integer.valueOf(k)))
          {
            zzbwb().zzbxe().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            localObject3 = (zzwc.zza)localArrayMap1.get(Integer.valueOf(k));
            localObject1 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (localObject3 == null)
            {
              localObject1 = new zzwc.zza();
              localArrayMap1.put(Integer.valueOf(k), localObject1);
              ((zzwc.zza)localObject1).awL = Boolean.valueOf(true);
              localObject1 = new BitSet();
              localArrayMap2.put(Integer.valueOf(k), localObject1);
              localObject2 = new BitSet();
              localArrayMap3.put(Integer.valueOf(k), localObject2);
            }
            localIterator = ((List)paramArrayOfzzb.get(Integer.valueOf(k))).iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                break label1693;
              }
              localObject8 = (zzwa.zze)localIterator.next();
              if (zzbwb().zzbi(2))
              {
                zzbwb().zzbxe().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzwa.zze)localObject8).awd, ((zzwa.zze)localObject8).awt);
                zzbwb().zzbxe().zzj("Filter definition", zzal.zza((zzwa.zze)localObject8));
              }
              if ((((zzwa.zze)localObject8).awd == null) || (((zzwa.zze)localObject8).awd.intValue() > 256))
              {
                zzbwb().zzbxa().zzj("Invalid property filter ID. id", String.valueOf(((zzwa.zze)localObject8).awd));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject1).get(((zzwa.zze)localObject8).awd.intValue()))
              {
                zzbwb().zzbxe().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzwa.zze)localObject8).awd);
              }
              else
              {
                localObject4 = zza((zzwa.zze)localObject8, (zzwc.zzg)localObject6);
                localObject9 = zzbwb().zzbxe();
                if (localObject4 == null) {}
                for (localObject3 = "null";; localObject3 = localObject4)
                {
                  ((zzq.zza)localObject9).zzj("Property filter result", localObject3);
                  if (localObject4 != null) {
                    break label1658;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                }
                ((BitSet)localObject2).set(((zzwa.zze)localObject8).awd.intValue());
                if (((Boolean)localObject4).booleanValue()) {
                  ((BitSet)localObject1).set(((zzwa.zze)localObject8).awd.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzg = new zzwc.zza[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          j = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(j)))
          {
            paramArrayOfzzb = (zzwc.zza)localArrayMap1.get(Integer.valueOf(j));
            if (paramArrayOfzzb != null) {
              break label1908;
            }
            paramArrayOfzzb = new zzwc.zza();
          }
        }
        for (;;)
        {
          paramArrayOfzzg[i] = paramArrayOfzzb;
          paramArrayOfzzb.avZ = Integer.valueOf(j);
          paramArrayOfzzb.awJ = new zzwc.zzf();
          paramArrayOfzzb.awJ.axv = zzal.zza((BitSet)localArrayMap2.get(Integer.valueOf(j)));
          paramArrayOfzzb.awJ.axu = zzal.zza((BitSet)localArrayMap3.get(Integer.valueOf(j)));
          zzbvw().zza(paramString, j, paramArrayOfzzb.awJ);
          i += 1;
          break;
          return (zzwc.zza[])Arrays.copyOf(paramArrayOfzzg, i);
        }
      }
    }
  }
  
  protected void zzzy() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */