package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
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

final class zzcgk
  extends zzcjl
{
  zzcgk(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final Boolean zza(double paramDouble, zzclu paramzzclu)
  {
    try
    {
      paramzzclu = zza(new BigDecimal(paramDouble), paramzzclu, Math.ulp(paramDouble));
      return paramzzclu;
    }
    catch (NumberFormatException paramzzclu) {}
    return null;
  }
  
  private final Boolean zza(long paramLong, zzclu paramzzclu)
  {
    try
    {
      paramzzclu = zza(new BigDecimal(paramLong), paramzzclu, 0.0D);
      return paramzzclu;
    }
    catch (NumberFormatException paramzzclu) {}
    return null;
  }
  
  private final Boolean zza(zzcls paramzzcls, zzcmb paramzzcmb, long paramLong)
  {
    if (paramzzcls.zzjka != null)
    {
      localObject1 = zza(paramLong, paramzzcls.zzjka);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzcls.zzjjy;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzclt)localObject3).zzjkf))
      {
        zzawy().zzazf().zzj("null or empty param name in filter. event", zzawt().zzjh(paramzzcmb.name));
        return null;
      }
      ((Set)localObject2).add(((zzclt)localObject3).zzjkf);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzcmb.zzjlh;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzcmc)localObject4).name))
      {
        if (((zzcmc)localObject4).zzjll == null) {
          break label220;
        }
        ((Map)localObject1).put(((zzcmc)localObject4).name, ((zzcmc)localObject4).zzjll);
      }
      for (;;)
      {
        i += 1;
        break;
        label220:
        if (((zzcmc)localObject4).zzjjl != null)
        {
          ((Map)localObject1).put(((zzcmc)localObject4).name, ((zzcmc)localObject4).zzjjl);
        }
        else
        {
          if (((zzcmc)localObject4).zzgcc == null) {
            break label278;
          }
          ((Map)localObject1).put(((zzcmc)localObject4).name, ((zzcmc)localObject4).zzgcc);
        }
      }
      label278:
      zzawy().zzazf().zze("Unknown value for param. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji(((zzcmc)localObject4).name));
      return null;
    }
    localObject2 = paramzzcls.zzjjy;
    int k = localObject2.length;
    i = 0;
    while (i < k)
    {
      paramzzcls = localObject2[i];
      int m = Boolean.TRUE.equals(paramzzcls.zzjke);
      localObject3 = paramzzcls.zzjkf;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzawy().zzazf().zzj("Event has empty param name. event", zzawt().zzjh(paramzzcmb.name));
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (paramzzcls.zzjkd == null)
        {
          zzawy().zzazf().zze("No number filter for long param. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
          return null;
        }
        paramzzcls = zza(((Long)localObject4).longValue(), paramzzcls.zzjkd);
        if (paramzzcls == null) {
          return null;
        }
        if (!paramzzcls.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof Double))
      {
        if (paramzzcls.zzjkd == null)
        {
          zzawy().zzazf().zze("No number filter for double param. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
          return null;
        }
        paramzzcls = zza(((Double)localObject4).doubleValue(), paramzzcls.zzjkd);
        if (paramzzcls == null) {
          return null;
        }
        if (!paramzzcls.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if ((localObject4 instanceof String))
      {
        if (paramzzcls.zzjkc != null) {
          paramzzcls = zza((String)localObject4, paramzzcls.zzjkc);
        }
        while (paramzzcls == null)
        {
          return null;
          if (paramzzcls.zzjkd != null)
          {
            if (zzclq.zzkk((String)localObject4))
            {
              paramzzcls = zza((String)localObject4, paramzzcls.zzjkd);
            }
            else
            {
              zzawy().zzazf().zze("Invalid param value for number filter. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
              return null;
            }
          }
          else
          {
            zzawy().zzazf().zze("No filter for String param. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
            return null;
          }
        }
        if (!paramzzcls.booleanValue()) {}
        for (j = 1; (j ^ m) != 0; j = 0) {
          return Boolean.valueOf(false);
        }
      }
      if (localObject4 == null)
      {
        zzawy().zzazj().zze("Missing param for filter. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
        return Boolean.valueOf(false);
      }
      zzawy().zzazf().zze("Unknown param type. event, param", zzawt().zzjh(paramzzcmb.name), zzawt().zzji((String)localObject3));
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
        zzawy().zzazf().zzj("Invalid regular expression in REGEXP audience filter. expression", paramString3);
        return null;
      }
    }
    return Boolean.valueOf(str.startsWith(paramString2));
    return Boolean.valueOf(str.endsWith(paramString2));
    return Boolean.valueOf(str.contains(paramString2));
    return Boolean.valueOf(str.equals(paramString2));
    return Boolean.valueOf(paramList.contains(str));
  }
  
  private final Boolean zza(String paramString, zzclu paramzzclu)
  {
    if (!zzclq.zzkk(paramString)) {
      return null;
    }
    try
    {
      paramString = zza(new BigDecimal(paramString), paramzzclu, 0.0D);
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
  }
  
  private final Boolean zza(String paramString, zzclw paramzzclw)
  {
    int i = 0;
    Object localObject = null;
    zzbq.checkNotNull(paramzzclw);
    if (paramString == null) {}
    do
    {
      do
      {
        return null;
      } while ((paramzzclw.zzjko == null) || (paramzzclw.zzjko.intValue() == 0));
      if (paramzzclw.zzjko.intValue() != 6) {
        break;
      }
    } while ((paramzzclw.zzjkr == null) || (paramzzclw.zzjkr.length == 0));
    int j = paramzzclw.zzjko.intValue();
    boolean bool;
    label89:
    String str;
    if ((paramzzclw.zzjkq != null) && (paramzzclw.zzjkq.booleanValue()))
    {
      bool = true;
      if ((!bool) && (j != 1) && (j != 6)) {
        break label162;
      }
      str = paramzzclw.zzjkp;
      label113:
      if (paramzzclw.zzjkr != null) {
        break label177;
      }
    }
    label162:
    label177:
    String[] arrayOfString;
    for (paramzzclw = null;; paramzzclw = Arrays.asList(arrayOfString))
    {
      if (j == 1) {
        localObject = str;
      }
      return zza(paramString, j, bool, str, paramzzclw, (String)localObject);
      if (paramzzclw.zzjkp != null) {
        break;
      }
      return null;
      bool = false;
      break label89;
      str = paramzzclw.zzjkp.toUpperCase(Locale.ENGLISH);
      break label113;
      arrayOfString = paramzzclw.zzjkr;
      if (!bool) {
        break label197;
      }
    }
    label197:
    ArrayList localArrayList = new ArrayList();
    int k = arrayOfString.length;
    for (;;)
    {
      paramzzclw = localArrayList;
      if (i >= k) {
        break;
      }
      localArrayList.add(arrayOfString[i].toUpperCase(Locale.ENGLISH));
      i += 1;
    }
  }
  
  private static Boolean zza(BigDecimal paramBigDecimal, zzclu paramzzclu, double paramDouble)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool1 = true;
    zzbq.checkNotNull(paramzzclu);
    if ((paramzzclu.zzjkg == null) || (paramzzclu.zzjkg.intValue() == 0)) {
      return null;
    }
    if (paramzzclu.zzjkg.intValue() == 4)
    {
      if ((paramzzclu.zzjkj == null) || (paramzzclu.zzjkk == null)) {
        return null;
      }
    }
    else if (paramzzclu.zzjki == null) {
      return null;
    }
    int i = paramzzclu.zzjkg.intValue();
    if (paramzzclu.zzjkg.intValue() == 4) {
      if ((!zzclq.zzkk(paramzzclu.zzjkj)) || (!zzclq.zzkk(paramzzclu.zzjkk))) {
        return null;
      }
    }
    BigDecimal localBigDecimal1;
    BigDecimal localBigDecimal2;
    for (;;)
    {
      try
      {
        localBigDecimal1 = new BigDecimal(paramzzclu.zzjkj);
        paramzzclu = new BigDecimal(paramzzclu.zzjkk);
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
      if (!zzclq.zzkk(paramzzclu.zzjki)) {
        return null;
      }
      try
      {
        localBigDecimal2 = new BigDecimal(paramzzclu.zzjki);
        localBigDecimal1 = null;
        paramzzclu = null;
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
    if ((paramBigDecimal.compareTo(localBigDecimal1) != -1) && (paramBigDecimal.compareTo(paramzzclu) != 1)) {}
    for (bool1 = bool5;; bool1 = false) {
      return Boolean.valueOf(bool1);
    }
  }
  
  final zzcma[] zza(String paramString, zzcmb[] paramArrayOfzzcmb, zzcmg[] paramArrayOfzzcmg)
  {
    zzbq.zzgm(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzaws().zzje(paramString);
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
        localObject6 = (zzcmf)((Map)localObject4).get(Integer.valueOf(j));
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
        while (i < ((zzcmf)localObject6).zzjmp.length << 6)
        {
          if (zzclq.zza(((zzcmf)localObject6).zzjmp, i))
          {
            zzawy().zzazj().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzclq.zza(((zzcmf)localObject6).zzjmq, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzcma();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzcma)localObject3).zzjlf = Boolean.valueOf(false);
        ((zzcma)localObject3).zzjle = ((zzcmf)localObject6);
        ((zzcma)localObject3).zzjld = new zzcmf();
        ((zzcma)localObject3).zzjld.zzjmq = zzclq.zza((BitSet)localObject1);
        ((zzcma)localObject3).zzjld.zzjmp = zzclq.zza((BitSet)localObject2);
      }
    }
    Object localObject7;
    long l;
    if (paramArrayOfzzcmb != null)
    {
      localObject6 = new ArrayMap();
      j = paramArrayOfzzcmb.length;
      i = 0;
      if (i < j)
      {
        localObject7 = paramArrayOfzzcmb[i];
        localObject1 = zzaws().zzae(paramString, ((zzcmb)localObject7).name);
        if (localObject1 == null)
        {
          zzawy().zzazf().zze("Event aggregate wasn't created during raw event logging. appId, event", zzchm.zzjk(paramString), zzawt().zzjh(((zzcmb)localObject7).name));
          localObject1 = new zzcgw(paramString, ((zzcmb)localObject7).name, 1L, 1L, ((zzcmb)localObject7).zzjli.longValue(), 0L, null, null, null);
          zzaws().zza((zzcgw)localObject1);
          l = ((zzcgw)localObject1).zzizk;
          localObject1 = (Map)((Map)localObject6).get(((zzcmb)localObject7).name);
          if (localObject1 != null) {
            break label2492;
          }
          localObject2 = zzaws().zzaj(paramString, ((zzcmb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzcmb)localObject7).name, localObject1);
        }
      }
    }
    label1065:
    label1102:
    label1675:
    label2045:
    label2051:
    label2085:
    label2486:
    label2489:
    label2492:
    for (;;)
    {
      Iterator localIterator = ((Map)localObject1).keySet().iterator();
      int k;
      Object localObject8;
      Object localObject9;
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label1102;
        }
        k = ((Integer)localIterator.next()).intValue();
        if (localHashSet.contains(Integer.valueOf(k)))
        {
          zzawy().zzazj().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzcgw)localObject1).zzayw();
          break;
        }
        localObject4 = (zzcma)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzcma();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzcma)localObject2).zzjlf = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzcls)((Iterator)localObject8).next();
          if (zzawy().zzae(2))
          {
            zzawy().zzazj().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzcls)localObject9).zzjjw, zzawt().zzjh(((zzcls)localObject9).zzjjx));
            zzawy().zzazj().zzj("Filter definition", zzawt().zza((zzcls)localObject9));
          }
          if ((((zzcls)localObject9).zzjjw == null) || (((zzcls)localObject9).zzjjw.intValue() > 256))
          {
            zzawy().zzazf().zze("Invalid event filter ID. appId, id", zzchm.zzjk(paramString), String.valueOf(((zzcls)localObject9).zzjjw));
          }
          else if (((BitSet)localObject2).get(((zzcls)localObject9).zzjjw.intValue()))
          {
            zzawy().zzazj().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzcls)localObject9).zzjjw);
          }
          else
          {
            localObject5 = zza((zzcls)localObject9, (zzcmb)localObject7, l);
            zzcho localzzcho = zzawy().zzazj();
            if (localObject5 == null) {}
            for (localObject4 = "null";; localObject4 = localObject5)
            {
              localzzcho.zzj("Event filter result", localObject4);
              if (localObject5 != null) {
                break label1065;
              }
              localHashSet.add(Integer.valueOf(k));
              break;
            }
            ((BitSet)localObject3).set(((zzcls)localObject9).zzjjw.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzcls)localObject9).zzjjw.intValue());
            }
          }
        }
      }
      i += 1;
      break;
      if (paramArrayOfzzcmg != null)
      {
        localObject5 = new ArrayMap();
        j = paramArrayOfzzcmg.length;
        i = 0;
        if (i < j)
        {
          localObject6 = paramArrayOfzzcmg[i];
          localObject1 = (Map)((Map)localObject5).get(((zzcmg)localObject6).name);
          if (localObject1 != null) {
            break label2489;
          }
          localObject1 = zzaws().zzak(paramString, ((zzcmg)localObject6).name);
          paramArrayOfzzcmb = (zzcmb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzcmb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzcmg)localObject6).name, paramArrayOfzzcmb);
          localObject1 = paramArrayOfzzcmb;
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
            zzawy().zzazj().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            paramArrayOfzzcmb = (zzcma)localArrayMap1.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (paramArrayOfzzcmb == null)
            {
              paramArrayOfzzcmb = new zzcma();
              localArrayMap1.put(Integer.valueOf(k), paramArrayOfzzcmb);
              paramArrayOfzzcmb.zzjlf = Boolean.valueOf(true);
              localObject2 = new BitSet();
              localArrayMap2.put(Integer.valueOf(k), localObject2);
              localObject3 = new BitSet();
              localArrayMap3.put(Integer.valueOf(k), localObject3);
            }
            localIterator = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                break label2085;
              }
              localObject8 = (zzclv)localIterator.next();
              if (zzawy().zzae(2))
              {
                zzawy().zzazj().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzclv)localObject8).zzjjw, zzawt().zzjj(((zzclv)localObject8).zzjkm));
                zzawy().zzazj().zzj("Filter definition", zzawt().zza((zzclv)localObject8));
              }
              if ((((zzclv)localObject8).zzjjw == null) || (((zzclv)localObject8).zzjjw.intValue() > 256))
              {
                zzawy().zzazf().zze("Invalid property filter ID. appId, id", zzchm.zzjk(paramString), String.valueOf(((zzclv)localObject8).zzjjw));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject2).get(((zzclv)localObject8).zzjjw.intValue()))
              {
                zzawy().zzazj().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzclv)localObject8).zzjjw);
              }
              else
              {
                paramArrayOfzzcmb = ((zzclv)localObject8).zzjkn;
                if (paramArrayOfzzcmb == null)
                {
                  zzawy().zzazf().zzj("Missing property filter. property", zzawt().zzjj(((zzcmg)localObject6).name));
                  paramArrayOfzzcmb = null;
                  localObject9 = zzawy().zzazj();
                  if (paramArrayOfzzcmb != null) {
                    break label2045;
                  }
                }
                for (localObject4 = "null";; localObject4 = paramArrayOfzzcmb)
                {
                  ((zzcho)localObject9).zzj("Property filter result", localObject4);
                  if (paramArrayOfzzcmb != null) {
                    break label2051;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                  boolean bool = Boolean.TRUE.equals(paramArrayOfzzcmb.zzjke);
                  if (((zzcmg)localObject6).zzjll != null)
                  {
                    if (paramArrayOfzzcmb.zzjkd == null)
                    {
                      zzawy().zzazf().zzj("No number filter for long property. property", zzawt().zzjj(((zzcmg)localObject6).name));
                      paramArrayOfzzcmb = null;
                      break label1675;
                    }
                    paramArrayOfzzcmb = zza(zza(((zzcmg)localObject6).zzjll.longValue(), paramArrayOfzzcmb.zzjkd), bool);
                    break label1675;
                  }
                  if (((zzcmg)localObject6).zzjjl != null)
                  {
                    if (paramArrayOfzzcmb.zzjkd == null)
                    {
                      zzawy().zzazf().zzj("No number filter for double property. property", zzawt().zzjj(((zzcmg)localObject6).name));
                      paramArrayOfzzcmb = null;
                      break label1675;
                    }
                    paramArrayOfzzcmb = zza(zza(((zzcmg)localObject6).zzjjl.doubleValue(), paramArrayOfzzcmb.zzjkd), bool);
                    break label1675;
                  }
                  if (((zzcmg)localObject6).zzgcc != null)
                  {
                    if (paramArrayOfzzcmb.zzjkc == null)
                    {
                      if (paramArrayOfzzcmb.zzjkd == null) {
                        zzawy().zzazf().zzj("No string or number filter defined. property", zzawt().zzjj(((zzcmg)localObject6).name));
                      }
                      for (;;)
                      {
                        paramArrayOfzzcmb = null;
                        break;
                        if (zzclq.zzkk(((zzcmg)localObject6).zzgcc))
                        {
                          paramArrayOfzzcmb = zza(zza(((zzcmg)localObject6).zzgcc, paramArrayOfzzcmb.zzjkd), bool);
                          break;
                        }
                        zzawy().zzazf().zze("Invalid user property value for Numeric number filter. property, value", zzawt().zzjj(((zzcmg)localObject6).name), ((zzcmg)localObject6).zzgcc);
                      }
                    }
                    paramArrayOfzzcmb = zza(zza(((zzcmg)localObject6).zzgcc, paramArrayOfzzcmb.zzjkc), bool);
                    break label1675;
                  }
                  zzawy().zzazf().zzj("User property has no value, property", zzawt().zzjj(((zzcmg)localObject6).name));
                  paramArrayOfzzcmb = null;
                  break label1675;
                }
                ((BitSet)localObject3).set(((zzclv)localObject8).zzjjw.intValue());
                if (paramArrayOfzzcmb.booleanValue()) {
                  ((BitSet)localObject2).set(((zzclv)localObject8).zzjjw.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzcmg = new zzcma[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          k = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(k)))
          {
            paramArrayOfzzcmb = (zzcma)localArrayMap1.get(Integer.valueOf(k));
            if (paramArrayOfzzcmb != null) {
              break label2486;
            }
            paramArrayOfzzcmb = new zzcma();
          }
        }
        for (;;)
        {
          j = i + 1;
          paramArrayOfzzcmg[i] = paramArrayOfzzcmb;
          paramArrayOfzzcmb.zzjjs = Integer.valueOf(k);
          paramArrayOfzzcmb.zzjld = new zzcmf();
          paramArrayOfzzcmb.zzjld.zzjmq = zzclq.zza((BitSet)localArrayMap2.get(Integer.valueOf(k)));
          paramArrayOfzzcmb.zzjld.zzjmp = zzclq.zza((BitSet)localArrayMap3.get(Integer.valueOf(k)));
          localObject2 = zzaws();
          localObject3 = paramArrayOfzzcmb.zzjld;
          ((zzcjl)localObject2).zzxf();
          ((zzcjk)localObject2).zzve();
          zzbq.zzgm(paramString);
          zzbq.checkNotNull(localObject3);
          try
          {
            paramArrayOfzzcmb = new byte[((zzfjs)localObject3).zzho()];
            localObject4 = zzfjk.zzo(paramArrayOfzzcmb, 0, paramArrayOfzzcmb.length);
            ((zzfjs)localObject3).zza((zzfjk)localObject4);
            ((zzfjk)localObject4).zzcwt();
            localObject3 = new ContentValues();
            ((ContentValues)localObject3).put("app_id", paramString);
            ((ContentValues)localObject3).put("audience_id", Integer.valueOf(k));
            ((ContentValues)localObject3).put("current_results", paramArrayOfzzcmb);
          }
          catch (IOException paramArrayOfzzcmb)
          {
            try
            {
              if (((zzcgo)localObject2).getWritableDatabase().insertWithOnConflict("audience_filter_values", null, (ContentValues)localObject3, 5) == -1L) {
                ((zzcjk)localObject2).zzawy().zzazd().zzj("Failed to insert filter results (got -1). appId", zzchm.zzjk(paramString));
              }
              i = j;
            }
            catch (SQLiteException paramArrayOfzzcmb)
            {
              ((zzcjk)localObject2).zzawy().zzazd().zze("Error storing filter results. appId", zzchm.zzjk(paramString), paramArrayOfzzcmb);
              i = j;
            }
            paramArrayOfzzcmb = paramArrayOfzzcmb;
            ((zzcjk)localObject2).zzawy().zzazd().zze("Configuration loss. Failed to serialize filter results. appId", zzchm.zzjk(paramString), paramArrayOfzzcmb);
            i = j;
          }
          break;
          break;
          return (zzcma[])Arrays.copyOf(paramArrayOfzzcmg, i);
        }
      }
    }
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */