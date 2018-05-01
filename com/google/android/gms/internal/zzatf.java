package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
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

class zzatf
  extends zzauh
{
  zzatf(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  private Boolean zza(zzauu.zzb paramzzb, zzauw.zzb paramzzb1, long paramLong)
  {
    if (paramzzb.zzbwv != null)
    {
      localObject1 = zza(paramLong, paramzzb.zzbwv);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzb.zzbwt;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzauu.zzc)localObject3).zzbwA))
      {
        zzKl().zzMb().zzj("null or empty param name in filter. event", paramzzb1.name);
        return null;
      }
      ((Set)localObject2).add(((zzauu.zzc)localObject3).zzbwA);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzb1.zzbxb;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzauw.zzc)localObject4).name))
      {
        if (((zzauw.zzc)localObject4).zzbxf == null) {
          break label213;
        }
        ((Map)localObject1).put(((zzauw.zzc)localObject4).name, ((zzauw.zzc)localObject4).zzbxf);
      }
      for (;;)
      {
        i += 1;
        break;
        label213:
        if (((zzauw.zzc)localObject4).zzbwi != null)
        {
          ((Map)localObject1).put(((zzauw.zzc)localObject4).name, ((zzauw.zzc)localObject4).zzbwi);
        }
        else
        {
          if (((zzauw.zzc)localObject4).zzaGV == null) {
            break label271;
          }
          ((Map)localObject1).put(((zzauw.zzc)localObject4).name, ((zzauw.zzc)localObject4).zzaGV);
        }
      }
      label271:
      zzKl().zzMb().zze("Unknown value for param. event, param", paramzzb1.name, ((zzauw.zzc)localObject4).name);
      return null;
    }
    localObject2 = paramzzb.zzbwt;
    int k = localObject2.length;
    i = 0;
    while (i < k)
    {
      paramzzb = localObject2[i];
      int m = Boolean.TRUE.equals(paramzzb.zzbwz);
      localObject3 = paramzzb.zzbwA;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzKl().zzMb().zzj("Event has empty param name. event", paramzzb1.name);
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (paramzzb.zzbwy == null)
        {
          zzKl().zzMb().zze("No number filter for long param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        paramzzb = zza(((Long)localObject4).longValue(), paramzzb.zzbwy);
        if (paramzzb == null) {
          return null;
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof Double))
      {
        if (paramzzb.zzbwy == null)
        {
          zzKl().zzMb().zze("No number filter for double param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        paramzzb = zza(((Double)localObject4).doubleValue(), paramzzb.zzbwy);
        if (paramzzb == null) {
          return null;
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof String))
      {
        if (paramzzb.zzbwx != null) {
          paramzzb = zza((String)localObject4, paramzzb.zzbwx);
        }
        while (paramzzb == null)
        {
          return null;
          if (paramzzb.zzbwy != null)
          {
            if (zzaut.zzgf((String)localObject4))
            {
              paramzzb = zza((String)localObject4, paramzzb.zzbwy);
            }
            else
            {
              zzKl().zzMb().zze("Invalid param value for number filter. event, param", paramzzb1.name, localObject3);
              return null;
            }
          }
          else
          {
            zzKl().zzMb().zze("No filter for String param. event, param", paramzzb1.name, localObject3);
            return null;
          }
        }
        if (!paramzzb.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if (localObject4 == null)
      {
        zzKl().zzMf().zze("Missing param for filter. event, param", paramzzb1.name, localObject3);
        return Boolean.valueOf(false);
      }
      zzKl().zzMb().zze("Unknown param type. event, param", paramzzb1.name, localObject3);
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private Boolean zza(zzauu.zze paramzze, zzauw.zzg paramzzg)
  {
    paramzze = paramzze.zzbwI;
    if (paramzze == null)
    {
      zzKl().zzMb().zzj("Missing property filter. property", paramzzg.name);
      return null;
    }
    boolean bool = Boolean.TRUE.equals(paramzze.zzbwz);
    if (paramzzg.zzbxf != null)
    {
      if (paramzze.zzbwy == null)
      {
        zzKl().zzMb().zzj("No number filter for long property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbxf.longValue(), paramzze.zzbwy), bool);
    }
    if (paramzzg.zzbwi != null)
    {
      if (paramzze.zzbwy == null)
      {
        zzKl().zzMb().zzj("No number filter for double property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbwi.doubleValue(), paramzze.zzbwy), bool);
    }
    if (paramzzg.zzaGV != null)
    {
      if (paramzze.zzbwx == null)
      {
        if (paramzze.zzbwy == null)
        {
          zzKl().zzMb().zzj("No string or number filter defined. property", paramzzg.name);
          return null;
        }
        if (zzaut.zzgf(paramzzg.zzaGV)) {
          return zza(zza(paramzzg.zzaGV, paramzze.zzbwy), bool);
        }
        zzKl().zzMb().zze("Invalid user property value for Numeric number filter. property, value", paramzzg.name, paramzzg.zzaGV);
        return null;
      }
      return zza(zza(paramzzg.zzaGV, paramzze.zzbwx), bool);
    }
    zzKl().zzMb().zzj("User property has no value, property", paramzzg.name);
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
  
  public Boolean zza(double paramDouble, zzauu.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramDouble), paramzzd, Math.ulp(paramDouble));
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(long paramLong, zzauu.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramLong), paramzzd, 0.0D);
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(String paramString, zzauu.zzd paramzzd)
  {
    if (!zzaut.zzgf(paramString)) {
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
  
  Boolean zza(String paramString, zzauu.zzf paramzzf)
  {
    Object localObject = null;
    zzac.zzw(paramzzf);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzf.zzbwJ == null) || (paramzzf.zzbwJ.intValue() == 0));
      if (paramzzf.zzbwJ.intValue() != 6) {
        break;
      }
    } while ((paramzzf.zzbwM == null) || (paramzzf.zzbwM.length == 0));
    int i = paramzzf.zzbwJ.intValue();
    boolean bool;
    label86:
    String str;
    if ((paramzzf.zzbwL != null) && (paramzzf.zzbwL.booleanValue()))
    {
      bool = true;
      if ((!bool) && (i != 1) && (i != 6)) {
        break label155;
      }
      str = paramzzf.zzbwK;
      label108:
      if (paramzzf.zzbwM != null) {
        break label170;
      }
    }
    label155:
    label170:
    for (paramzzf = null;; paramzzf = zza(paramzzf.zzbwM, bool))
    {
      if (i == 1) {
        localObject = str;
      }
      return zza(paramString, i, bool, str, paramzzf, (String)localObject);
      if (paramzzf.zzbwK != null) {
        break;
      }
      return null;
      bool = false;
      break label86;
      str = paramzzf.zzbwK.toUpperCase(Locale.ENGLISH);
      break label108;
    }
  }
  
  Boolean zza(BigDecimal paramBigDecimal, zzauu.zzd paramzzd, double paramDouble)
  {
    zzac.zzw(paramzzd);
    if ((paramzzd.zzbwB == null) || (paramzzd.zzbwB.intValue() == 0)) {}
    label49:
    int i;
    do
    {
      do
      {
        return null;
        if (paramzzd.zzbwB.intValue() != 4) {
          break;
        }
      } while ((paramzzd.zzbwE == null) || (paramzzd.zzbwF == null));
      i = paramzzd.zzbwB.intValue();
    } while ((paramzzd.zzbwB.intValue() == 4) && ((!zzaut.zzgf(paramzzd.zzbwE)) || (!zzaut.zzgf(paramzzd.zzbwF))));
    for (;;)
    {
      BigDecimal localBigDecimal1;
      BigDecimal localBigDecimal2;
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzd.zzbwE);
        localBigDecimal2 = new BigDecimal(paramzzd.zzbwF);
        paramzzd = null;
        return zza(paramBigDecimal, i, paramzzd, localBigDecimal1, localBigDecimal2, paramDouble);
      }
      catch (NumberFormatException paramBigDecimal) {}
      if (paramzzd.zzbwD != null) {
        break label49;
      }
      return null;
      if (!zzaut.zzgf(paramzzd.zzbwD)) {
        break;
      }
      try
      {
        paramzzd = new BigDecimal(paramzzd.zzbwD);
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
  void zza(String paramString, zzauu.zza[] paramArrayOfzza)
  {
    zzac.zzw(paramArrayOfzza);
    int m = paramArrayOfzza.length;
    int i = 0;
    while (i < m)
    {
      Object localObject1 = paramArrayOfzza[i];
      zzauu.zzb[] arrayOfzzb = ((zzauu.zza)localObject1).zzbwp;
      int n = arrayOfzzb.length;
      int j = 0;
      Object localObject2;
      while (j < n)
      {
        localObject2 = arrayOfzzb[j];
        String str1 = (String)AppMeasurement.zza.zzbqc.get(((zzauu.zzb)localObject2).zzbws);
        if (str1 != null) {
          ((zzauu.zzb)localObject2).zzbws = str1;
        }
        localObject2 = ((zzauu.zzb)localObject2).zzbwt;
        int i1 = localObject2.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject2[k];
          String str2 = (String)AppMeasurement.zze.zzbqd.get(str1.zzbwA);
          if (str2 != null) {
            str1.zzbwA = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject1 = ((zzauu.zza)localObject1).zzbwo;
      int k = localObject1.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzb = localObject1[j];
        localObject2 = (String)AppMeasurement.zzg.zzbqh.get(arrayOfzzb.zzbwH);
        if (localObject2 != null) {
          arrayOfzzb.zzbwH = ((String)localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    zzKg().zzb(paramString, paramArrayOfzza);
  }
  
  @WorkerThread
  zzauw.zza[] zza(String paramString, zzauw.zzb[] paramArrayOfzzb, zzauw.zzg[] paramArrayOfzzg)
  {
    zzac.zzdr(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzKg().zzfy(paramString);
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
        localObject6 = (zzauw.zzf)((Map)localObject4).get(Integer.valueOf(j));
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
        while (i < ((zzauw.zzf)localObject6).zzbxJ.length * 64)
        {
          if (zzaut.zza(((zzauw.zzf)localObject6).zzbxJ, i))
          {
            zzKl().zzMf().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzaut.zza(((zzauw.zzf)localObject6).zzbxK, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzauw.zza();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzauw.zza)localObject3).zzbwZ = Boolean.valueOf(false);
        ((zzauw.zza)localObject3).zzbwY = ((zzauw.zzf)localObject6);
        ((zzauw.zza)localObject3).zzbwX = new zzauw.zzf();
        ((zzauw.zza)localObject3).zzbwX.zzbxK = zzaut.zza((BitSet)localObject1);
        ((zzauw.zza)localObject3).zzbwX.zzbxJ = zzaut.zza((BitSet)localObject2);
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
        localObject1 = zzKg().zzQ(paramString, ((zzauw.zzb)localObject7).name);
        if (localObject1 == null)
        {
          zzKl().zzMb().zze("Event aggregate wasn't created during raw event logging. appId, event", zzatx.zzfE(paramString), ((zzauw.zzb)localObject7).name);
          localObject1 = new zzatn(paramString, ((zzauw.zzb)localObject7).name, 1L, 1L, ((zzauw.zzb)localObject7).zzbxc.longValue());
          zzKg().zza((zzatn)localObject1);
          l = ((zzatn)localObject1).zzbrB;
          localObject1 = (Map)((Map)localObject6).get(((zzauw.zzb)localObject7).name);
          if (localObject1 != null) {
            break label1926;
          }
          localObject2 = zzKg().zzV(paramString, ((zzauw.zzb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzauw.zzb)localObject7).name, localObject1);
        }
      }
    }
    label1043:
    label1080:
    label1670:
    label1705:
    label1920:
    label1923:
    label1926:
    for (;;)
    {
      Iterator localIterator = ((Map)localObject1).keySet().iterator();
      int k;
      Object localObject8;
      Object localObject9;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label1080;
        }
        k = ((Integer)localIterator.next()).intValue();
        if (localHashSet.contains(Integer.valueOf(k)))
        {
          zzKl().zzMf().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzatn)localObject1).zzLV();
          break;
        }
        localObject4 = (zzauw.zza)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzauw.zza();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzauw.zza)localObject2).zzbwZ = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzauu.zzb)((Iterator)localObject8).next();
          if (zzKl().zzak(2))
          {
            zzKl().zzMf().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzauu.zzb)localObject9).zzbwr, ((zzauu.zzb)localObject9).zzbws);
            zzKl().zzMf().zzj("Filter definition", zzaut.zza((zzauu.zzb)localObject9));
          }
          if ((((zzauu.zzb)localObject9).zzbwr == null) || (((zzauu.zzb)localObject9).zzbwr.intValue() > 256))
          {
            zzKl().zzMb().zze("Invalid event filter ID. appId, id", zzatx.zzfE(paramString), String.valueOf(((zzauu.zzb)localObject9).zzbwr));
          }
          else if (((BitSet)localObject2).get(((zzauu.zzb)localObject9).zzbwr.intValue()))
          {
            zzKl().zzMf().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzauu.zzb)localObject9).zzbwr);
          }
          else
          {
            localObject5 = zza((zzauu.zzb)localObject9, (zzauw.zzb)localObject7, l);
            zzatx.zza localzza = zzKl().zzMf();
            if (localObject5 == null) {}
            for (localObject4 = "null";; localObject4 = localObject5)
            {
              localzza.zzj("Event filter result", localObject4);
              if (localObject5 != null) {
                break label1043;
              }
              localHashSet.add(Integer.valueOf(k));
              break;
            }
            ((BitSet)localObject3).set(((zzauu.zzb)localObject9).zzbwr.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzauu.zzb)localObject9).zzbwr.intValue());
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
          paramArrayOfzzb = (Map)((Map)localObject5).get(((zzauw.zzg)localObject6).name);
          if (paramArrayOfzzb != null) {
            break label1923;
          }
          localObject1 = zzKg().zzW(paramString, ((zzauw.zzg)localObject6).name);
          paramArrayOfzzb = (zzauw.zzb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzauw.zzg)localObject6).name, paramArrayOfzzb);
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
            zzKl().zzMf().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            localObject3 = (zzauw.zza)localArrayMap1.get(Integer.valueOf(k));
            localObject1 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (localObject3 == null)
            {
              localObject1 = new zzauw.zza();
              localArrayMap1.put(Integer.valueOf(k), localObject1);
              ((zzauw.zza)localObject1).zzbwZ = Boolean.valueOf(true);
              localObject1 = new BitSet();
              localArrayMap2.put(Integer.valueOf(k), localObject1);
              localObject2 = new BitSet();
              localArrayMap3.put(Integer.valueOf(k), localObject2);
            }
            localIterator = ((List)paramArrayOfzzb.get(Integer.valueOf(k))).iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                break label1705;
              }
              localObject8 = (zzauu.zze)localIterator.next();
              if (zzKl().zzak(2))
              {
                zzKl().zzMf().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzauu.zze)localObject8).zzbwr, ((zzauu.zze)localObject8).zzbwH);
                zzKl().zzMf().zzj("Filter definition", zzaut.zza((zzauu.zze)localObject8));
              }
              if ((((zzauu.zze)localObject8).zzbwr == null) || (((zzauu.zze)localObject8).zzbwr.intValue() > 256))
              {
                zzKl().zzMb().zze("Invalid property filter ID. appId, id", zzatx.zzfE(paramString), String.valueOf(((zzauu.zze)localObject8).zzbwr));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject1).get(((zzauu.zze)localObject8).zzbwr.intValue()))
              {
                zzKl().zzMf().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzauu.zze)localObject8).zzbwr);
              }
              else
              {
                localObject4 = zza((zzauu.zze)localObject8, (zzauw.zzg)localObject6);
                localObject9 = zzKl().zzMf();
                if (localObject4 == null) {}
                for (localObject3 = "null";; localObject3 = localObject4)
                {
                  ((zzatx.zza)localObject9).zzj("Property filter result", localObject3);
                  if (localObject4 != null) {
                    break label1670;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                }
                ((BitSet)localObject2).set(((zzauu.zze)localObject8).zzbwr.intValue());
                if (((Boolean)localObject4).booleanValue()) {
                  ((BitSet)localObject1).set(((zzauu.zze)localObject8).zzbwr.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzg = new zzauw.zza[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          j = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(j)))
          {
            paramArrayOfzzb = (zzauw.zza)localArrayMap1.get(Integer.valueOf(j));
            if (paramArrayOfzzb != null) {
              break label1920;
            }
            paramArrayOfzzb = new zzauw.zza();
          }
        }
        for (;;)
        {
          paramArrayOfzzg[i] = paramArrayOfzzb;
          paramArrayOfzzb.zzbwn = Integer.valueOf(j);
          paramArrayOfzzb.zzbwX = new zzauw.zzf();
          paramArrayOfzzb.zzbwX.zzbxK = zzaut.zza((BitSet)localArrayMap2.get(Integer.valueOf(j)));
          paramArrayOfzzb.zzbwX.zzbxJ = zzaut.zza((BitSet)localArrayMap3.get(Integer.valueOf(j)));
          zzKg().zza(paramString, j, paramArrayOfzzb.zzbwX);
          i += 1;
          break;
          return (zzauw.zza[])Arrays.copyOf(paramArrayOfzzg, i);
        }
      }
    }
  }
  
  protected void zzmS() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */