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

class zzass
  extends zzats
{
  zzass(zzatp paramzzatp)
  {
    super(paramzzatp);
  }
  
  private Boolean zza(zzauf.zzb paramzzb, zzauh.zzb paramzzb1, long paramLong)
  {
    if (paramzzb.zzbvp != null)
    {
      localObject1 = zza(paramLong, paramzzb.zzbvp);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzb.zzbvn;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzauf.zzc)localObject3).zzbvu))
      {
        zzJt().zzLc().zzj("null or empty param name in filter. event", paramzzb1.name);
        return null;
      }
      ((Set)localObject2).add(((zzauf.zzc)localObject3).zzbvu);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzb1.zzbvV;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzauh.zzc)localObject4).name))
      {
        if (((zzauh.zzc)localObject4).zzbvZ == null) {
          break label213;
        }
        ((Map)localObject1).put(((zzauh.zzc)localObject4).name, ((zzauh.zzc)localObject4).zzbvZ);
      }
      for (;;)
      {
        i += 1;
        break;
        label213:
        if (((zzauh.zzc)localObject4).zzbvc != null)
        {
          ((Map)localObject1).put(((zzauh.zzc)localObject4).name, ((zzauh.zzc)localObject4).zzbvc);
        }
        else
        {
          if (((zzauh.zzc)localObject4).zzaFy == null) {
            break label271;
          }
          ((Map)localObject1).put(((zzauh.zzc)localObject4).name, ((zzauh.zzc)localObject4).zzaFy);
        }
      }
      label271:
      zzJt().zzLc().zze("Unknown value for param. event, param", paramzzb1.name, ((zzauh.zzc)localObject4).name);
      return null;
    }
    paramzzb = paramzzb.zzbvn;
    int k = paramzzb.length;
    i = 0;
    while (i < k)
    {
      localObject2 = paramzzb[i];
      int m = Boolean.TRUE.equals(((zzauf.zzc)localObject2).zzbvt);
      localObject3 = ((zzauf.zzc)localObject2).zzbvu;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzJt().zzLc().zzj("Event has empty param name. event", paramzzb1.name);
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (((zzauf.zzc)localObject2).zzbvs == null)
        {
          zzJt().zzLc().zze("No number filter for long param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza(((Long)localObject4).longValue(), ((zzauf.zzc)localObject2).zzbvs);
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
        if (((zzauf.zzc)localObject2).zzbvs == null)
        {
          zzJt().zzLc().zze("No number filter for double param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza(((Double)localObject4).doubleValue(), ((zzauf.zzc)localObject2).zzbvs);
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
        if (((zzauf.zzc)localObject2).zzbvr == null)
        {
          zzJt().zzLc().zze("No string filter for String param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = zza((String)localObject4, ((zzauf.zzc)localObject2).zzbvr);
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
        zzJt().zzLg().zze("Missing param for filter. event, param", paramzzb1.name, localObject3);
        return Boolean.valueOf(false);
      }
      zzJt().zzLc().zze("Unknown param type. event, param", paramzzb1.name, localObject3);
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private Boolean zza(zzauf.zze paramzze, zzauh.zzg paramzzg)
  {
    paramzze = paramzze.zzbvC;
    if (paramzze == null)
    {
      zzJt().zzLc().zzj("Missing property filter. property", paramzzg.name);
      return null;
    }
    boolean bool = Boolean.TRUE.equals(paramzze.zzbvt);
    if (paramzzg.zzbvZ != null)
    {
      if (paramzze.zzbvs == null)
      {
        zzJt().zzLc().zzj("No number filter for long property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbvZ.longValue(), paramzze.zzbvs), bool);
    }
    if (paramzzg.zzbvc != null)
    {
      if (paramzze.zzbvs == null)
      {
        zzJt().zzLc().zzj("No number filter for double property. property", paramzzg.name);
        return null;
      }
      return zza(zza(paramzzg.zzbvc.doubleValue(), paramzze.zzbvs), bool);
    }
    if (paramzzg.zzaFy != null)
    {
      if (paramzze.zzbvr == null)
      {
        if (paramzze.zzbvs == null)
        {
          zzJt().zzLc().zzj("No string or number filter defined. property", paramzzg.name);
          return null;
        }
        if (zzaue.zzgi(paramzzg.zzaFy)) {
          return zza(zza(paramzzg.zzaFy, paramzze.zzbvs), bool);
        }
        zzJt().zzLc().zze("Invalid user property value for Numeric number filter. property, value", paramzzg.name, paramzzg.zzaFy);
        return null;
      }
      return zza(zza(paramzzg.zzaFy, paramzze.zzbvr), bool);
    }
    zzJt().zzLc().zzj("User property has no value, property", paramzzg.name);
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
  
  public Boolean zza(double paramDouble, zzauf.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramDouble), paramzzd, Math.ulp(paramDouble));
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(long paramLong, zzauf.zzd paramzzd)
  {
    try
    {
      paramzzd = zza(new BigDecimal(paramLong), paramzzd, 0.0D);
      return paramzzd;
    }
    catch (NumberFormatException paramzzd) {}
    return null;
  }
  
  public Boolean zza(String paramString, zzauf.zzd paramzzd)
  {
    if (!zzaue.zzgi(paramString)) {
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
  
  Boolean zza(String paramString, zzauf.zzf paramzzf)
  {
    Object localObject = null;
    zzac.zzw(paramzzf);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzf.zzbvD == null) || (paramzzf.zzbvD.intValue() == 0));
      if (paramzzf.zzbvD.intValue() != 6) {
        break;
      }
    } while ((paramzzf.zzbvG == null) || (paramzzf.zzbvG.length == 0));
    int i = paramzzf.zzbvD.intValue();
    boolean bool;
    label86:
    String str;
    if ((paramzzf.zzbvF != null) && (paramzzf.zzbvF.booleanValue()))
    {
      bool = true;
      if ((!bool) && (i != 1) && (i != 6)) {
        break label155;
      }
      str = paramzzf.zzbvE;
      label108:
      if (paramzzf.zzbvG != null) {
        break label170;
      }
    }
    label155:
    label170:
    for (paramzzf = null;; paramzzf = zza(paramzzf.zzbvG, bool))
    {
      if (i == 1) {
        localObject = str;
      }
      return zza(paramString, i, bool, str, paramzzf, (String)localObject);
      if (paramzzf.zzbvE != null) {
        break;
      }
      return null;
      bool = false;
      break label86;
      str = paramzzf.zzbvE.toUpperCase(Locale.ENGLISH);
      break label108;
    }
  }
  
  Boolean zza(BigDecimal paramBigDecimal, zzauf.zzd paramzzd, double paramDouble)
  {
    zzac.zzw(paramzzd);
    if ((paramzzd.zzbvv == null) || (paramzzd.zzbvv.intValue() == 0)) {}
    label49:
    int i;
    do
    {
      do
      {
        return null;
        if (paramzzd.zzbvv.intValue() != 4) {
          break;
        }
      } while ((paramzzd.zzbvy == null) || (paramzzd.zzbvz == null));
      i = paramzzd.zzbvv.intValue();
    } while ((paramzzd.zzbvv.intValue() == 4) && ((!zzaue.zzgi(paramzzd.zzbvy)) || (!zzaue.zzgi(paramzzd.zzbvz))));
    for (;;)
    {
      BigDecimal localBigDecimal1;
      BigDecimal localBigDecimal2;
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzd.zzbvy);
        localBigDecimal2 = new BigDecimal(paramzzd.zzbvz);
        paramzzd = null;
        return zza(paramBigDecimal, i, paramzzd, localBigDecimal1, localBigDecimal2, paramDouble);
      }
      catch (NumberFormatException paramBigDecimal) {}
      if (paramzzd.zzbvx != null) {
        break label49;
      }
      return null;
      if (!zzaue.zzgi(paramzzd.zzbvx)) {
        break;
      }
      try
      {
        paramzzd = new BigDecimal(paramzzd.zzbvx);
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
  void zza(String paramString, zzauf.zza[] paramArrayOfzza)
  {
    zzac.zzw(paramArrayOfzza);
    int m = paramArrayOfzza.length;
    int i = 0;
    while (i < m)
    {
      Object localObject1 = paramArrayOfzza[i];
      zzauf.zzb[] arrayOfzzb = ((zzauf.zza)localObject1).zzbvj;
      int n = arrayOfzzb.length;
      int j = 0;
      Object localObject2;
      while (j < n)
      {
        localObject2 = arrayOfzzb[j];
        String str1 = (String)AppMeasurement.zza.zzbpx.get(((zzauf.zzb)localObject2).zzbvm);
        if (str1 != null) {
          ((zzauf.zzb)localObject2).zzbvm = str1;
        }
        localObject2 = ((zzauf.zzb)localObject2).zzbvn;
        int i1 = localObject2.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject2[k];
          String str2 = (String)AppMeasurement.zze.zzbpy.get(str1.zzbvu);
          if (str2 != null) {
            str1.zzbvu = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject1 = ((zzauf.zza)localObject1).zzbvi;
      int k = localObject1.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzb = localObject1[j];
        localObject2 = (String)AppMeasurement.zzg.zzbpC.get(arrayOfzzb.zzbvB);
        if (localObject2 != null) {
          arrayOfzzb.zzbvB = ((String)localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    zzJo().zzb(paramString, paramArrayOfzza);
  }
  
  @WorkerThread
  zzauh.zza[] zza(String paramString, zzauh.zzb[] paramArrayOfzzb, zzauh.zzg[] paramArrayOfzzg)
  {
    zzac.zzdv(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzJo().zzfC(paramString);
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
        localObject6 = (zzauh.zzf)((Map)localObject4).get(Integer.valueOf(j));
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
        while (i < ((zzauh.zzf)localObject6).zzbwC.length * 64)
        {
          if (zzaue.zza(((zzauh.zzf)localObject6).zzbwC, i))
          {
            zzJt().zzLg().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzaue.zza(((zzauh.zzf)localObject6).zzbwD, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzauh.zza();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzauh.zza)localObject3).zzbvT = Boolean.valueOf(false);
        ((zzauh.zza)localObject3).zzbvS = ((zzauh.zzf)localObject6);
        ((zzauh.zza)localObject3).zzbvR = new zzauh.zzf();
        ((zzauh.zza)localObject3).zzbvR.zzbwD = zzaue.zza((BitSet)localObject1);
        ((zzauh.zza)localObject3).zzbvR.zzbwC = zzaue.zza((BitSet)localObject2);
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
        localObject1 = zzJo().zzP(paramString, ((zzauh.zzb)localObject7).name);
        if (localObject1 == null)
        {
          zzJt().zzLc().zze("Event aggregate wasn't created during raw event logging. appId, event", zzati.zzfI(paramString), ((zzauh.zzb)localObject7).name);
          localObject1 = new zzasy(paramString, ((zzauh.zzb)localObject7).name, 1L, 1L, ((zzauh.zzb)localObject7).zzbvW.longValue());
          zzJo().zza((zzasy)localObject1);
          l = ((zzasy)localObject1).zzbqJ;
          localObject1 = (Map)((Map)localObject6).get(((zzauh.zzb)localObject7).name);
          if (localObject1 != null) {
            break label1926;
          }
          localObject2 = zzJo().zzS(paramString, ((zzauh.zzb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzauh.zzb)localObject7).name, localObject1);
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
          zzJt().zzLg().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzasy)localObject1).zzKX();
          break;
        }
        localObject4 = (zzauh.zza)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzauh.zza();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzauh.zza)localObject2).zzbvT = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzauf.zzb)((Iterator)localObject8).next();
          if (zzJt().zzai(2))
          {
            zzJt().zzLg().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzauf.zzb)localObject9).zzbvl, ((zzauf.zzb)localObject9).zzbvm);
            zzJt().zzLg().zzj("Filter definition", zzaue.zza((zzauf.zzb)localObject9));
          }
          if ((((zzauf.zzb)localObject9).zzbvl == null) || (((zzauf.zzb)localObject9).zzbvl.intValue() > 256))
          {
            zzJt().zzLc().zze("Invalid event filter ID. appId, id", zzati.zzfI(paramString), String.valueOf(((zzauf.zzb)localObject9).zzbvl));
          }
          else if (((BitSet)localObject2).get(((zzauf.zzb)localObject9).zzbvl.intValue()))
          {
            zzJt().zzLg().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzauf.zzb)localObject9).zzbvl);
          }
          else
          {
            localObject5 = zza((zzauf.zzb)localObject9, (zzauh.zzb)localObject7, l);
            zzati.zza localzza = zzJt().zzLg();
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
            ((BitSet)localObject3).set(((zzauf.zzb)localObject9).zzbvl.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzauf.zzb)localObject9).zzbvl.intValue());
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
          paramArrayOfzzb = (Map)((Map)localObject5).get(((zzauh.zzg)localObject6).name);
          if (paramArrayOfzzb != null) {
            break label1923;
          }
          localObject1 = zzJo().zzT(paramString, ((zzauh.zzg)localObject6).name);
          paramArrayOfzzb = (zzauh.zzb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzauh.zzg)localObject6).name, paramArrayOfzzb);
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
            zzJt().zzLg().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            localObject3 = (zzauh.zza)localArrayMap1.get(Integer.valueOf(k));
            localObject1 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (localObject3 == null)
            {
              localObject1 = new zzauh.zza();
              localArrayMap1.put(Integer.valueOf(k), localObject1);
              ((zzauh.zza)localObject1).zzbvT = Boolean.valueOf(true);
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
              localObject8 = (zzauf.zze)localIterator.next();
              if (zzJt().zzai(2))
              {
                zzJt().zzLg().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzauf.zze)localObject8).zzbvl, ((zzauf.zze)localObject8).zzbvB);
                zzJt().zzLg().zzj("Filter definition", zzaue.zza((zzauf.zze)localObject8));
              }
              if ((((zzauf.zze)localObject8).zzbvl == null) || (((zzauf.zze)localObject8).zzbvl.intValue() > 256))
              {
                zzJt().zzLc().zze("Invalid property filter ID. appId, id", zzati.zzfI(paramString), String.valueOf(((zzauf.zze)localObject8).zzbvl));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject1).get(((zzauf.zze)localObject8).zzbvl.intValue()))
              {
                zzJt().zzLg().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzauf.zze)localObject8).zzbvl);
              }
              else
              {
                localObject4 = zza((zzauf.zze)localObject8, (zzauh.zzg)localObject6);
                localObject9 = zzJt().zzLg();
                if (localObject4 == null) {}
                for (localObject3 = "null";; localObject3 = localObject4)
                {
                  ((zzati.zza)localObject9).zzj("Property filter result", localObject3);
                  if (localObject4 != null) {
                    break label1670;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                }
                ((BitSet)localObject2).set(((zzauf.zze)localObject8).zzbvl.intValue());
                if (((Boolean)localObject4).booleanValue()) {
                  ((BitSet)localObject1).set(((zzauf.zze)localObject8).zzbvl.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzg = new zzauh.zza[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          j = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(j)))
          {
            paramArrayOfzzb = (zzauh.zza)localArrayMap1.get(Integer.valueOf(j));
            if (paramArrayOfzzb != null) {
              break label1920;
            }
            paramArrayOfzzb = new zzauh.zza();
          }
        }
        for (;;)
        {
          paramArrayOfzzg[i] = paramArrayOfzzb;
          paramArrayOfzzb.zzbvh = Integer.valueOf(j);
          paramArrayOfzzb.zzbvR = new zzauh.zzf();
          paramArrayOfzzb.zzbvR.zzbwD = zzaue.zza((BitSet)localArrayMap2.get(Integer.valueOf(j)));
          paramArrayOfzzb.zzbvR.zzbwC = zzaue.zza((BitSet)localArrayMap3.get(Integer.valueOf(j)));
          zzJo().zza(paramString, j, paramArrayOfzzb.zzbvR);
          i += 1;
          break;
          return (zzauh.zza[])Arrays.copyOf(paramArrayOfzzg, i);
        }
      }
    }
  }
  
  protected void zzmr() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */