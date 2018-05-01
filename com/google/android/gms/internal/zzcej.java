package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import java.io.IOException;
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
import java.util.regex.PatternSyntaxException;

final class zzcej
  extends zzchj
{
  zzcej(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  private final Boolean zza(double paramDouble, zzcjp paramzzcjp)
  {
    try
    {
      paramzzcjp = zza(new BigDecimal(paramDouble), paramzzcjp, Math.ulp(paramDouble));
      return paramzzcjp;
    }
    catch (NumberFormatException paramzzcjp) {}
    return null;
  }
  
  private final Boolean zza(long paramLong, zzcjp paramzzcjp)
  {
    try
    {
      paramzzcjp = zza(new BigDecimal(paramLong), paramzzcjp, 0.0D);
      return paramzzcjp;
    }
    catch (NumberFormatException paramzzcjp) {}
    return null;
  }
  
  private final Boolean zza(zzcjn paramzzcjn, zzcjw paramzzcjw, long paramLong)
  {
    if (paramzzcjn.zzbuQ != null)
    {
      localObject1 = zza(paramLong, paramzzcjn.zzbuQ);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzcjn.zzbuO;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzcjo)localObject3).zzbuV))
      {
        zzwF().zzyz().zzj("null or empty param name in filter. event", zzwA().zzdW(paramzzcjw.name));
        return null;
      }
      ((Set)localObject2).add(((zzcjo)localObject3).zzbuV);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzcjw.zzbvw;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzcjx)localObject4).name))
      {
        if (((zzcjx)localObject4).zzbvA == null) {
          break label220;
        }
        ((Map)localObject1).put(((zzcjx)localObject4).name, ((zzcjx)localObject4).zzbvA);
      }
      for (;;)
      {
        i += 1;
        break;
        label220:
        if (((zzcjx)localObject4).zzbuB != null)
        {
          ((Map)localObject1).put(((zzcjx)localObject4).name, ((zzcjx)localObject4).zzbuB);
        }
        else
        {
          if (((zzcjx)localObject4).zzaIF == null) {
            break label278;
          }
          ((Map)localObject1).put(((zzcjx)localObject4).name, ((zzcjx)localObject4).zzaIF);
        }
      }
      label278:
      zzwF().zzyz().zze("Unknown value for param. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX(((zzcjx)localObject4).name));
      return null;
    }
    localObject2 = paramzzcjn.zzbuO;
    int k = localObject2.length;
    i = 0;
    while (i < k)
    {
      paramzzcjn = localObject2[i];
      int m = Boolean.TRUE.equals(paramzzcjn.zzbuU);
      localObject3 = paramzzcjn.zzbuV;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzwF().zzyz().zzj("Event has empty param name. event", zzwA().zzdW(paramzzcjw.name));
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (paramzzcjn.zzbuT == null)
        {
          zzwF().zzyz().zze("No number filter for long param. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
          return null;
        }
        paramzzcjn = zza(((Long)localObject4).longValue(), paramzzcjn.zzbuT);
        if (paramzzcjn == null) {
          return null;
        }
        if (!paramzzcjn.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof Double))
      {
        if (paramzzcjn.zzbuT == null)
        {
          zzwF().zzyz().zze("No number filter for double param. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
          return null;
        }
        paramzzcjn = zza(((Double)localObject4).doubleValue(), paramzzcjn.zzbuT);
        if (paramzzcjn == null) {
          return null;
        }
        if (!paramzzcjn.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof String))
      {
        if (paramzzcjn.zzbuS != null) {
          paramzzcjn = zza((String)localObject4, paramzzcjn.zzbuS);
        }
        while (paramzzcjn == null)
        {
          return null;
          if (paramzzcjn.zzbuT != null)
          {
            if (zzcjl.zzez((String)localObject4))
            {
              paramzzcjn = zza((String)localObject4, paramzzcjn.zzbuT);
            }
            else
            {
              zzwF().zzyz().zze("Invalid param value for number filter. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
              return null;
            }
          }
          else
          {
            zzwF().zzyz().zze("No filter for String param. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
            return null;
          }
        }
        if (!paramzzcjn.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if (localObject4 == null)
      {
        zzwF().zzyD().zze("Missing param for filter. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
        return Boolean.valueOf(false);
      }
      zzwF().zzyz().zze("Unknown param type. event, param", zzwA().zzdW(paramzzcjw.name), zzwA().zzdX((String)localObject3));
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private static Boolean zza(Boolean paramBoolean, boolean paramBoolean1)
  {
    if (paramBoolean == null) {
      return null;
    }
    return Boolean.valueOf(paramBoolean.booleanValue() ^ paramBoolean1);
  }
  
  private final Boolean zza(String paramString1, int paramInt, boolean paramBoolean, String paramString2, List<String> paramList, String paramString3)
  {
    if (paramString1 == null) {
      return null;
    }
    if (paramInt == 6)
    {
      if ((paramList == null) || (paramList.size() == 0)) {
        return null;
      }
    }
    else if (paramString2 == null) {
      return null;
    }
    String str = paramString1;
    if (!paramBoolean) {
      if (paramInt != 1) {
        break label94;
      }
    }
    label94:
    for (str = paramString1;; str = paramString1.toUpperCase(Locale.ENGLISH)) {
      switch (paramInt)
      {
      default: 
        return null;
      }
    }
    if (paramBoolean) {}
    for (paramInt = 0;; paramInt = 66) {
      try
      {
        paramBoolean = Pattern.compile(paramString3, paramInt).matcher(str).matches();
        return Boolean.valueOf(paramBoolean);
      }
      catch (PatternSyntaxException paramString1)
      {
        zzwF().zzyz().zzj("Invalid regular expression in REGEXP audience filter. expression", paramString3);
        return null;
      }
    }
    return Boolean.valueOf(str.startsWith(paramString2));
    return Boolean.valueOf(str.endsWith(paramString2));
    return Boolean.valueOf(str.contains(paramString2));
    return Boolean.valueOf(str.equals(paramString2));
    return Boolean.valueOf(paramList.contains(str));
  }
  
  private final Boolean zza(String paramString, zzcjp paramzzcjp)
  {
    if (!zzcjl.zzez(paramString)) {
      return null;
    }
    try
    {
      paramString = zza(new BigDecimal(paramString), paramzzcjp, 0.0D);
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
  }
  
  private final Boolean zza(String paramString, zzcjr paramzzcjr)
  {
    int i = 0;
    Object localObject = null;
    zzbo.zzu(paramzzcjr);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzcjr.zzbve == null) || (paramzzcjr.zzbve.intValue() == 0));
      if (paramzzcjr.zzbve.intValue() != 6) {
        break;
      }
    } while ((paramzzcjr.zzbvh == null) || (paramzzcjr.zzbvh.length == 0));
    int j = paramzzcjr.zzbve.intValue();
    boolean bool;
    label89:
    String str;
    if ((paramzzcjr.zzbvg != null) && (paramzzcjr.zzbvg.booleanValue()))
    {
      bool = true;
      if ((!bool) && (j != 1) && (j != 6)) {
        break label162;
      }
      str = paramzzcjr.zzbvf;
      label113:
      if (paramzzcjr.zzbvh != null) {
        break label177;
      }
    }
    label162:
    label177:
    String[] arrayOfString;
    for (paramzzcjr = null;; paramzzcjr = Arrays.asList(arrayOfString))
    {
      if (j == 1) {
        localObject = str;
      }
      return zza(paramString, j, bool, str, paramzzcjr, (String)localObject);
      if (paramzzcjr.zzbvf != null) {
        break;
      }
      return null;
      bool = false;
      break label89;
      str = paramzzcjr.zzbvf.toUpperCase(Locale.ENGLISH);
      break label113;
      arrayOfString = paramzzcjr.zzbvh;
      if (!bool) {
        break label197;
      }
    }
    label197:
    ArrayList localArrayList = new ArrayList();
    int k = arrayOfString.length;
    for (;;)
    {
      paramzzcjr = localArrayList;
      if (i >= k) {
        break;
      }
      localArrayList.add(arrayOfString[i].toUpperCase(Locale.ENGLISH));
      i += 1;
    }
  }
  
  private static Boolean zza(BigDecimal paramBigDecimal, zzcjp paramzzcjp, double paramDouble)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool1 = true;
    zzbo.zzu(paramzzcjp);
    if ((paramzzcjp.zzbuW == null) || (paramzzcjp.zzbuW.intValue() == 0)) {
      return null;
    }
    if (paramzzcjp.zzbuW.intValue() == 4)
    {
      if ((paramzzcjp.zzbuZ == null) || (paramzzcjp.zzbva == null)) {
        return null;
      }
    }
    else if (paramzzcjp.zzbuY == null) {
      return null;
    }
    int i = paramzzcjp.zzbuW.intValue();
    if (paramzzcjp.zzbuW.intValue() == 4) {
      if ((!zzcjl.zzez(paramzzcjp.zzbuZ)) || (!zzcjl.zzez(paramzzcjp.zzbva))) {
        return null;
      }
    }
    BigDecimal localBigDecimal1;
    BigDecimal localBigDecimal2;
    for (;;)
    {
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzcjp.zzbuZ);
        paramzzcjp = new BigDecimal(paramzzcjp.zzbva);
        localBigDecimal2 = null;
        if (i != 4) {
          break;
        }
        if (localBigDecimal1 != null) {
          break label202;
        }
        return null;
      }
      catch (NumberFormatException paramBigDecimal)
      {
        return null;
      }
      if (!zzcjl.zzez(paramzzcjp.zzbuY)) {
        return null;
      }
      try
      {
        localBigDecimal2 = new BigDecimal(paramzzcjp.zzbuY);
        localBigDecimal1 = null;
        paramzzcjp = null;
      }
      catch (NumberFormatException paramBigDecimal)
      {
        return null;
      }
    }
    if (localBigDecimal2 != null) {}
    switch (i)
    {
    default: 
      return null;
    case 1: 
      if (paramBigDecimal.compareTo(localBigDecimal2) == -1) {}
      for (;;)
      {
        return Boolean.valueOf(bool1);
        bool1 = false;
      }
    case 2: 
      if (paramBigDecimal.compareTo(localBigDecimal2) == 1) {}
      for (bool1 = bool2;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    case 3: 
      label202:
      if (paramDouble != 0.0D)
      {
        if ((paramBigDecimal.compareTo(localBigDecimal2.subtract(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == 1) && (paramBigDecimal.compareTo(localBigDecimal2.add(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == -1)) {}
        for (bool1 = bool3;; bool1 = false) {
          return Boolean.valueOf(bool1);
        }
      }
      if (paramBigDecimal.compareTo(localBigDecimal2) == 0) {}
      for (bool1 = bool4;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    }
    if ((paramBigDecimal.compareTo(localBigDecimal1) != -1) && (paramBigDecimal.compareTo(paramzzcjp) != 1)) {}
    for (bool1 = bool5;; bool1 = false) {
      return Boolean.valueOf(bool1);
    }
  }
  
  @WorkerThread
  final zzcjv[] zza(String paramString, zzcjw[] paramArrayOfzzcjw, zzckb[] paramArrayOfzzckb)
  {
    zzbo.zzcF(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzwz().zzdT(paramString);
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
        localObject6 = (zzcka)((Map)localObject4).get(Integer.valueOf(j));
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
        while (i < ((zzcka)localObject6).zzbwe.length << 6)
        {
          if (zzcjl.zza(((zzcka)localObject6).zzbwe, i))
          {
            zzwF().zzyD().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzcjl.zza(((zzcka)localObject6).zzbwf, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzcjv();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzcjv)localObject3).zzbvu = Boolean.valueOf(false);
        ((zzcjv)localObject3).zzbvt = ((zzcka)localObject6);
        ((zzcjv)localObject3).zzbvs = new zzcka();
        ((zzcjv)localObject3).zzbvs.zzbwf = zzcjl.zza((BitSet)localObject1);
        ((zzcjv)localObject3).zzbvs.zzbwe = zzcjl.zza((BitSet)localObject2);
      }
    }
    Object localObject7;
    long l;
    if (paramArrayOfzzcjw != null)
    {
      localObject6 = new ArrayMap();
      j = paramArrayOfzzcjw.length;
      i = 0;
      if (i < j)
      {
        localObject7 = paramArrayOfzzcjw[i];
        localObject1 = zzwz().zzE(paramString, ((zzcjw)localObject7).name);
        if (localObject1 == null)
        {
          zzwF().zzyz().zze("Event aggregate wasn't created during raw event logging. appId, event", zzcfl.zzdZ(paramString), zzwA().zzdW(((zzcjw)localObject7).name));
          localObject1 = new zzcev(paramString, ((zzcjw)localObject7).name, 1L, 1L, ((zzcjw)localObject7).zzbvx.longValue());
          zzwz().zza((zzcev)localObject1);
          l = ((zzcev)localObject1).zzbpG;
          localObject1 = (Map)((Map)localObject6).get(((zzcjw)localObject7).name);
          if (localObject1 != null) {
            break label2488;
          }
          localObject2 = zzwz().zzJ(paramString, ((zzcjw)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzcjw)localObject7).name, localObject1);
        }
      }
    }
    label1061:
    label1098:
    label1671:
    label2041:
    label2047:
    label2081:
    label2482:
    label2485:
    label2488:
    for (;;)
    {
      Iterator localIterator = ((Map)localObject1).keySet().iterator();
      int k;
      Object localObject8;
      Object localObject9;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label1098;
        }
        k = ((Integer)localIterator.next()).intValue();
        if (localHashSet.contains(Integer.valueOf(k)))
        {
          zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzcev)localObject1).zzys();
          break;
        }
        localObject4 = (zzcjv)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzcjv();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzcjv)localObject2).zzbvu = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzcjn)((Iterator)localObject8).next();
          if (zzwF().zzz(2))
          {
            zzwF().zzyD().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzcjn)localObject9).zzbuM, zzwA().zzdW(((zzcjn)localObject9).zzbuN));
            zzwF().zzyD().zzj("Filter definition", zzwA().zza((zzcjn)localObject9));
          }
          if ((((zzcjn)localObject9).zzbuM == null) || (((zzcjn)localObject9).zzbuM.intValue() > 256))
          {
            zzwF().zzyz().zze("Invalid event filter ID. appId, id", zzcfl.zzdZ(paramString), String.valueOf(((zzcjn)localObject9).zzbuM));
          }
          else if (((BitSet)localObject2).get(((zzcjn)localObject9).zzbuM.intValue()))
          {
            zzwF().zzyD().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzcjn)localObject9).zzbuM);
          }
          else
          {
            localObject5 = zza((zzcjn)localObject9, (zzcjw)localObject7, l);
            zzcfn localzzcfn = zzwF().zzyD();
            if (localObject5 == null) {}
            for (localObject4 = "null";; localObject4 = localObject5)
            {
              localzzcfn.zzj("Event filter result", localObject4);
              if (localObject5 != null) {
                break label1061;
              }
              localHashSet.add(Integer.valueOf(k));
              break;
            }
            ((BitSet)localObject3).set(((zzcjn)localObject9).zzbuM.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzcjn)localObject9).zzbuM.intValue());
            }
          }
        }
      }
      i += 1;
      break;
      if (paramArrayOfzzckb != null)
      {
        localObject5 = new ArrayMap();
        j = paramArrayOfzzckb.length;
        i = 0;
        if (i < j)
        {
          localObject6 = paramArrayOfzzckb[i];
          localObject1 = (Map)((Map)localObject5).get(((zzckb)localObject6).name);
          if (localObject1 != null) {
            break label2485;
          }
          localObject1 = zzwz().zzK(paramString, ((zzckb)localObject6).name);
          paramArrayOfzzcjw = (zzcjw[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzcjw = new ArrayMap();
          }
          ((Map)localObject5).put(((zzckb)localObject6).name, paramArrayOfzzcjw);
          localObject1 = paramArrayOfzzcjw;
        }
      }
      for (;;)
      {
        localObject7 = ((Map)localObject1).keySet().iterator();
        while (((Iterator)localObject7).hasNext())
        {
          k = ((Integer)((Iterator)localObject7).next()).intValue();
          if (localHashSet.contains(Integer.valueOf(k)))
          {
            zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            paramArrayOfzzcjw = (zzcjv)localArrayMap1.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (paramArrayOfzzcjw == null)
            {
              paramArrayOfzzcjw = new zzcjv();
              localArrayMap1.put(Integer.valueOf(k), paramArrayOfzzcjw);
              paramArrayOfzzcjw.zzbvu = Boolean.valueOf(true);
              localObject2 = new BitSet();
              localArrayMap2.put(Integer.valueOf(k), localObject2);
              localObject3 = new BitSet();
              localArrayMap3.put(Integer.valueOf(k), localObject3);
            }
            localIterator = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                break label2081;
              }
              localObject8 = (zzcjq)localIterator.next();
              if (zzwF().zzz(2))
              {
                zzwF().zzyD().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzcjq)localObject8).zzbuM, zzwA().zzdY(((zzcjq)localObject8).zzbvc));
                zzwF().zzyD().zzj("Filter definition", zzwA().zza((zzcjq)localObject8));
              }
              if ((((zzcjq)localObject8).zzbuM == null) || (((zzcjq)localObject8).zzbuM.intValue() > 256))
              {
                zzwF().zzyz().zze("Invalid property filter ID. appId, id", zzcfl.zzdZ(paramString), String.valueOf(((zzcjq)localObject8).zzbuM));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject2).get(((zzcjq)localObject8).zzbuM.intValue()))
              {
                zzwF().zzyD().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzcjq)localObject8).zzbuM);
              }
              else
              {
                paramArrayOfzzcjw = ((zzcjq)localObject8).zzbvd;
                if (paramArrayOfzzcjw == null)
                {
                  zzwF().zzyz().zzj("Missing property filter. property", zzwA().zzdY(((zzckb)localObject6).name));
                  paramArrayOfzzcjw = null;
                  localObject9 = zzwF().zzyD();
                  if (paramArrayOfzzcjw != null) {
                    break label2041;
                  }
                }
                for (localObject4 = "null";; localObject4 = paramArrayOfzzcjw)
                {
                  ((zzcfn)localObject9).zzj("Property filter result", localObject4);
                  if (paramArrayOfzzcjw != null) {
                    break label2047;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                  boolean bool = Boolean.TRUE.equals(paramArrayOfzzcjw.zzbuU);
                  if (((zzckb)localObject6).zzbvA != null)
                  {
                    if (paramArrayOfzzcjw.zzbuT == null)
                    {
                      zzwF().zzyz().zzj("No number filter for long property. property", zzwA().zzdY(((zzckb)localObject6).name));
                      paramArrayOfzzcjw = null;
                      break label1671;
                    }
                    paramArrayOfzzcjw = zza(zza(((zzckb)localObject6).zzbvA.longValue(), paramArrayOfzzcjw.zzbuT), bool);
                    break label1671;
                  }
                  if (((zzckb)localObject6).zzbuB != null)
                  {
                    if (paramArrayOfzzcjw.zzbuT == null)
                    {
                      zzwF().zzyz().zzj("No number filter for double property. property", zzwA().zzdY(((zzckb)localObject6).name));
                      paramArrayOfzzcjw = null;
                      break label1671;
                    }
                    paramArrayOfzzcjw = zza(zza(((zzckb)localObject6).zzbuB.doubleValue(), paramArrayOfzzcjw.zzbuT), bool);
                    break label1671;
                  }
                  if (((zzckb)localObject6).zzaIF != null)
                  {
                    if (paramArrayOfzzcjw.zzbuS == null)
                    {
                      if (paramArrayOfzzcjw.zzbuT == null) {
                        zzwF().zzyz().zzj("No string or number filter defined. property", zzwA().zzdY(((zzckb)localObject6).name));
                      }
                      for (;;)
                      {
                        paramArrayOfzzcjw = null;
                        break;
                        if (zzcjl.zzez(((zzckb)localObject6).zzaIF))
                        {
                          paramArrayOfzzcjw = zza(zza(((zzckb)localObject6).zzaIF, paramArrayOfzzcjw.zzbuT), bool);
                          break;
                        }
                        zzwF().zzyz().zze("Invalid user property value for Numeric number filter. property, value", zzwA().zzdY(((zzckb)localObject6).name), ((zzckb)localObject6).zzaIF);
                      }
                    }
                    paramArrayOfzzcjw = zza(zza(((zzckb)localObject6).zzaIF, paramArrayOfzzcjw.zzbuS), bool);
                    break label1671;
                  }
                  zzwF().zzyz().zzj("User property has no value, property", zzwA().zzdY(((zzckb)localObject6).name));
                  paramArrayOfzzcjw = null;
                  break label1671;
                }
                ((BitSet)localObject3).set(((zzcjq)localObject8).zzbuM.intValue());
                if (paramArrayOfzzcjw.booleanValue()) {
                  ((BitSet)localObject2).set(((zzcjq)localObject8).zzbuM.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzckb = new zzcjv[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          k = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(k)))
          {
            paramArrayOfzzcjw = (zzcjv)localArrayMap1.get(Integer.valueOf(k));
            if (paramArrayOfzzcjw != null) {
              break label2482;
            }
            paramArrayOfzzcjw = new zzcjv();
          }
        }
        for (;;)
        {
          j = i + 1;
          paramArrayOfzzckb[i] = paramArrayOfzzcjw;
          paramArrayOfzzcjw.zzbuI = Integer.valueOf(k);
          paramArrayOfzzcjw.zzbvs = new zzcka();
          paramArrayOfzzcjw.zzbvs.zzbwf = zzcjl.zza((BitSet)localArrayMap2.get(Integer.valueOf(k)));
          paramArrayOfzzcjw.zzbvs.zzbwe = zzcjl.zza((BitSet)localArrayMap3.get(Integer.valueOf(k)));
          localObject2 = zzwz();
          localObject3 = paramArrayOfzzcjw.zzbvs;
          ((zzcen)localObject2).zzkD();
          ((zzcen)localObject2).zzjC();
          zzbo.zzcF(paramString);
          zzbo.zzu(localObject3);
          try
          {
            paramArrayOfzzcjw = new byte[((zzcka)localObject3).zzLV()];
            localObject4 = adh.zzc(paramArrayOfzzcjw, 0, paramArrayOfzzcjw.length);
            ((zzcka)localObject3).zza((adh)localObject4);
            ((adh)localObject4).zzLM();
            localObject3 = new ContentValues();
            ((ContentValues)localObject3).put("app_id", paramString);
            ((ContentValues)localObject3).put("audience_id", Integer.valueOf(k));
            ((ContentValues)localObject3).put("current_results", paramArrayOfzzcjw);
          }
          catch (IOException paramArrayOfzzcjw)
          {
            try
            {
              if (((zzcen)localObject2).getWritableDatabase().insertWithOnConflict("audience_filter_values", null, (ContentValues)localObject3, 5) == -1L) {
                ((zzcen)localObject2).zzwF().zzyx().zzj("Failed to insert filter results (got -1). appId", zzcfl.zzdZ(paramString));
              }
              i = j;
            }
            catch (SQLiteException paramArrayOfzzcjw)
            {
              ((zzcen)localObject2).zzwF().zzyx().zze("Error storing filter results. appId", zzcfl.zzdZ(paramString), paramArrayOfzzcjw);
              i = j;
            }
            paramArrayOfzzcjw = paramArrayOfzzcjw;
            ((zzcen)localObject2).zzwF().zzyx().zze("Configuration loss. Failed to serialize filter results. appId", zzcfl.zzdZ(paramString), paramArrayOfzzcjw);
            i = j;
          }
          break;
          break;
          return (zzcjv[])Arrays.copyOf(paramArrayOfzzckb, i);
        }
      }
    }
  }
  
  protected final void zzjD() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcej.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */