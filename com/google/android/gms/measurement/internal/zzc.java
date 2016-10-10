package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zza;
import com.google.android.gms.internal.zzvk.zzb;
import com.google.android.gms.internal.zzvk.zzc;
import com.google.android.gms.internal.zzvk.zze;
import com.google.android.gms.internal.zzvm.zza;
import com.google.android.gms.internal.zzvm.zzb;
import com.google.android.gms.internal.zzvm.zzc;
import com.google.android.gms.internal.zzvm.zzf;
import com.google.android.gms.internal.zzvm.zzg;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import com.google.android.gms.measurement.AppMeasurement.zze;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class zzc
  extends zzaa
{
  zzc(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private Boolean zza(zzvk.zzb paramzzb, zzvm.zzb paramzzb1, long paramLong)
  {
    if (paramzzb.asI != null)
    {
      localObject1 = new zzs(paramzzb.asI).zzbn(paramLong);
      if (localObject1 == null) {
        return null;
      }
      if (!((Boolean)localObject1).booleanValue()) {
        return Boolean.valueOf(false);
      }
    }
    Object localObject2 = new HashSet();
    Object localObject1 = paramzzb.asG;
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject3 = localObject1[i];
      if (TextUtils.isEmpty(((zzvk.zzc)localObject3).asN))
      {
        zzbvg().zzbwe().zzj("null or empty param name in filter. event", paramzzb1.name);
        return null;
      }
      ((Set)localObject2).add(((zzvk.zzc)localObject3).asN);
      i += 1;
    }
    localObject1 = new ArrayMap();
    Object localObject3 = paramzzb1.ato;
    j = localObject3.length;
    i = 0;
    Object localObject4;
    if (i < j)
    {
      localObject4 = localObject3[i];
      if (((Set)localObject2).contains(((zzvm.zzc)localObject4).name))
      {
        if (((zzvm.zzc)localObject4).ats == null) {
          break label219;
        }
        ((Map)localObject1).put(((zzvm.zzc)localObject4).name, ((zzvm.zzc)localObject4).ats);
      }
      for (;;)
      {
        i += 1;
        break;
        label219:
        if (((zzvm.zzc)localObject4).asx != null)
        {
          ((Map)localObject1).put(((zzvm.zzc)localObject4).name, ((zzvm.zzc)localObject4).asx);
        }
        else
        {
          if (((zzvm.zzc)localObject4).Dr == null) {
            break label277;
          }
          ((Map)localObject1).put(((zzvm.zzc)localObject4).name, ((zzvm.zzc)localObject4).Dr);
        }
      }
      label277:
      zzbvg().zzbwe().zze("Unknown value for param. event, param", paramzzb1.name, ((zzvm.zzc)localObject4).name);
      return null;
    }
    paramzzb = paramzzb.asG;
    int k = paramzzb.length;
    i = 0;
    while (i < k)
    {
      localObject2 = paramzzb[i];
      int m = Boolean.TRUE.equals(((zzvk.zzc)localObject2).asM);
      localObject3 = ((zzvk.zzc)localObject2).asN;
      if (TextUtils.isEmpty((CharSequence)localObject3))
      {
        zzbvg().zzbwe().zzj("Event has empty param name. event", paramzzb1.name);
        return null;
      }
      localObject4 = ((Map)localObject1).get(localObject3);
      if ((localObject4 instanceof Long))
      {
        if (((zzvk.zzc)localObject2).asL == null)
        {
          zzbvg().zzbwe().zze("No number filter for long param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = new zzs(((zzvk.zzc)localObject2).asL).zzbn(((Long)localObject4).longValue());
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
        if (((zzvk.zzc)localObject2).asL == null)
        {
          zzbvg().zzbwe().zze("No number filter for double param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = new zzs(((zzvk.zzc)localObject2).asL).zzj(((Double)localObject4).doubleValue());
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
        if (((zzvk.zzc)localObject2).asK == null)
        {
          zzbvg().zzbwe().zze("No string filter for String param. event, param", paramzzb1.name, localObject3);
          return null;
        }
        localObject2 = new zzag(((zzvk.zzc)localObject2).asK).zzmw((String)localObject4);
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
        zzbvg().zzbwj().zze("Missing param for filter. event, param", paramzzb1.name, localObject3);
        return Boolean.valueOf(false);
      }
      zzbvg().zzbwe().zze("Unknown param type. event, param", paramzzb1.name, localObject3);
      return null;
      i += 1;
    }
    return Boolean.valueOf(true);
  }
  
  private Boolean zza(zzvk.zze paramzze, zzvm.zzg paramzzg)
  {
    paramzze = paramzze.asV;
    if (paramzze == null)
    {
      zzbvg().zzbwe().zzj("Missing property filter. property", paramzzg.name);
      return null;
    }
    boolean bool = Boolean.TRUE.equals(paramzze.asM);
    if (paramzzg.ats != null)
    {
      if (paramzze.asL == null)
      {
        zzbvg().zzbwe().zzj("No number filter for long property. property", paramzzg.name);
        return null;
      }
      return zza(new zzs(paramzze.asL).zzbn(paramzzg.ats.longValue()), bool);
    }
    if (paramzzg.asx != null)
    {
      if (paramzze.asL == null)
      {
        zzbvg().zzbwe().zzj("No number filter for double property. property", paramzzg.name);
        return null;
      }
      return zza(new zzs(paramzze.asL).zzj(paramzzg.asx.doubleValue()), bool);
    }
    if (paramzzg.Dr != null)
    {
      if (paramzze.asK == null)
      {
        if (paramzze.asL == null)
        {
          zzbvg().zzbwe().zzj("No string or number filter defined. property", paramzzg.name);
          return null;
        }
        paramzze = new zzs(paramzze.asL);
        if (zzal.zznj(paramzzg.Dr)) {
          return zza(paramzze.zzmk(paramzzg.Dr), bool);
        }
        zzbvg().zzbwe().zze("Invalid user property value for Numeric number filter. property, value", paramzzg.name, paramzzg.Dr);
        return null;
      }
      return zza(new zzag(paramzze.asK).zzmw(paramzzg.Dr), bool);
    }
    zzbvg().zzbwe().zzj("User property has no value, property", paramzzg.name);
    return null;
  }
  
  static Boolean zza(Boolean paramBoolean, boolean paramBoolean1)
  {
    if (paramBoolean == null) {
      return null;
    }
    return Boolean.valueOf(paramBoolean.booleanValue() ^ paramBoolean1);
  }
  
  @WorkerThread
  void zza(String paramString, zzvk.zza[] paramArrayOfzza)
  {
    zzac.zzy(paramArrayOfzza);
    int m = paramArrayOfzza.length;
    int i = 0;
    while (i < m)
    {
      Object localObject1 = paramArrayOfzza[i];
      zzvk.zzb[] arrayOfzzb = ((zzvk.zza)localObject1).asC;
      int n = arrayOfzzb.length;
      int j = 0;
      Object localObject2;
      while (j < n)
      {
        localObject2 = arrayOfzzb[j];
        String str1 = (String)AppMeasurement.zza.anr.get(((zzvk.zzb)localObject2).asF);
        if (str1 != null) {
          ((zzvk.zzb)localObject2).asF = str1;
        }
        localObject2 = ((zzvk.zzb)localObject2).asG;
        int i1 = localObject2.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject2[k];
          String str2 = (String)AppMeasurement.zzd.ans.get(str1.asN);
          if (str2 != null) {
            str1.asN = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject1 = ((zzvk.zza)localObject1).asB;
      int k = localObject1.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzb = localObject1[j];
        localObject2 = (String)AppMeasurement.zze.ant.get(arrayOfzzb.asU);
        if (localObject2 != null) {
          arrayOfzzb.asU = ((String)localObject2);
        }
        j += 1;
      }
      i += 1;
    }
    zzbvb().zzb(paramString, paramArrayOfzza);
  }
  
  @WorkerThread
  zzvm.zza[] zza(String paramString, zzvm.zzb[] paramArrayOfzzb, zzvm.zzg[] paramArrayOfzzg)
  {
    zzac.zzhz(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject4 = zzbvb().zzmd(paramString);
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
        localObject6 = (zzvm.zzf)((Map)localObject4).get(Integer.valueOf(j));
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
        while (i < ((zzvm.zzf)localObject6).atU.length * 64)
        {
          if (zzal.zza(((zzvm.zzf)localObject6).atU, i))
          {
            zzbvg().zzbwj().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(j), Integer.valueOf(i));
            ((BitSet)localObject2).set(i);
            if (zzal.zza(((zzvm.zzf)localObject6).atV, i)) {
              ((BitSet)localObject1).set(i);
            }
          }
          i += 1;
        }
        localObject3 = new zzvm.zza();
        localArrayMap1.put(Integer.valueOf(j), localObject3);
        ((zzvm.zza)localObject3).atm = Boolean.valueOf(false);
        ((zzvm.zza)localObject3).atl = ((zzvm.zzf)localObject6);
        ((zzvm.zza)localObject3).atk = new zzvm.zzf();
        ((zzvm.zza)localObject3).atk.atV = zzal.zza((BitSet)localObject1);
        ((zzvm.zza)localObject3).atk.atU = zzal.zza((BitSet)localObject2);
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
        localObject1 = zzbvb().zzaq(paramString, ((zzvm.zzb)localObject7).name);
        if (localObject1 == null)
        {
          zzbvg().zzbwe().zzj("Event aggregate wasn't created during raw event logging. event", ((zzvm.zzb)localObject7).name);
          localObject1 = new zzi(paramString, ((zzvm.zzb)localObject7).name, 1L, 1L, ((zzvm.zzb)localObject7).atp.longValue());
          zzbvb().zza((zzi)localObject1);
          l = ((zzi)localObject1).aot;
          localObject1 = (Map)((Map)localObject6).get(((zzvm.zzb)localObject7).name);
          if (localObject1 != null) {
            break label1914;
          }
          localObject2 = zzbvb().zzat(paramString, ((zzvm.zzb)localObject7).name);
          localObject1 = localObject2;
          if (localObject2 == null) {
            localObject1 = new ArrayMap();
          }
          ((Map)localObject6).put(((zzvm.zzb)localObject7).name, localObject1);
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
          zzbvg().zzbwj().zzj("Skipping failed audience ID", Integer.valueOf(k));
          continue;
          localObject1 = ((zzi)localObject1).zzbvy();
          break;
        }
        localObject4 = (zzvm.zza)localArrayMap1.get(Integer.valueOf(k));
        localObject2 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
        localObject3 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
        if (localObject4 == null)
        {
          localObject2 = new zzvm.zza();
          localArrayMap1.put(Integer.valueOf(k), localObject2);
          ((zzvm.zza)localObject2).atm = Boolean.valueOf(true);
          localObject2 = new BitSet();
          localArrayMap2.put(Integer.valueOf(k), localObject2);
          localObject3 = new BitSet();
          localArrayMap3.put(Integer.valueOf(k), localObject3);
        }
        localObject8 = ((List)((Map)localObject1).get(Integer.valueOf(k))).iterator();
        while (((Iterator)localObject8).hasNext())
        {
          localObject9 = (zzvk.zzb)((Iterator)localObject8).next();
          if (zzbvg().zzbf(2))
          {
            zzbvg().zzbwj().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(k), ((zzvk.zzb)localObject9).asE, ((zzvk.zzb)localObject9).asF);
            zzbvg().zzbwj().zzj("Filter definition", zzal.zza((zzvk.zzb)localObject9));
          }
          if ((((zzvk.zzb)localObject9).asE == null) || (((zzvk.zzb)localObject9).asE.intValue() > 256))
          {
            zzbvg().zzbwe().zzj("Invalid event filter ID. id", String.valueOf(((zzvk.zzb)localObject9).asE));
          }
          else if (((BitSet)localObject2).get(((zzvk.zzb)localObject9).asE.intValue()))
          {
            zzbvg().zzbwj().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzvk.zzb)localObject9).asE);
          }
          else
          {
            localObject5 = zza((zzvk.zzb)localObject9, (zzvm.zzb)localObject7, l);
            zzp.zza localzza = zzbvg().zzbwj();
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
            ((BitSet)localObject3).set(((zzvk.zzb)localObject9).asE.intValue());
            if (((Boolean)localObject5).booleanValue()) {
              ((BitSet)localObject2).set(((zzvk.zzb)localObject9).asE.intValue());
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
          paramArrayOfzzb = (Map)((Map)localObject5).get(((zzvm.zzg)localObject6).name);
          if (paramArrayOfzzb != null) {
            break label1911;
          }
          localObject1 = zzbvb().zzau(paramString, ((zzvm.zzg)localObject6).name);
          paramArrayOfzzb = (zzvm.zzb[])localObject1;
          if (localObject1 == null) {
            paramArrayOfzzb = new ArrayMap();
          }
          ((Map)localObject5).put(((zzvm.zzg)localObject6).name, paramArrayOfzzb);
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
            zzbvg().zzbwj().zzj("Skipping failed audience ID", Integer.valueOf(k));
          }
          else
          {
            localObject3 = (zzvm.zza)localArrayMap1.get(Integer.valueOf(k));
            localObject1 = (BitSet)localArrayMap2.get(Integer.valueOf(k));
            localObject2 = (BitSet)localArrayMap3.get(Integer.valueOf(k));
            if (localObject3 == null)
            {
              localObject1 = new zzvm.zza();
              localArrayMap1.put(Integer.valueOf(k), localObject1);
              ((zzvm.zza)localObject1).atm = Boolean.valueOf(true);
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
              localObject8 = (zzvk.zze)localIterator.next();
              if (zzbvg().zzbf(2))
              {
                zzbvg().zzbwj().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(k), ((zzvk.zze)localObject8).asE, ((zzvk.zze)localObject8).asU);
                zzbvg().zzbwj().zzj("Filter definition", zzal.zza((zzvk.zze)localObject8));
              }
              if ((((zzvk.zze)localObject8).asE == null) || (((zzvk.zze)localObject8).asE.intValue() > 256))
              {
                zzbvg().zzbwe().zzj("Invalid property filter ID. id", String.valueOf(((zzvk.zze)localObject8).asE));
                localHashSet.add(Integer.valueOf(k));
                break;
              }
              if (((BitSet)localObject1).get(((zzvk.zze)localObject8).asE.intValue()))
              {
                zzbvg().zzbwj().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(k), ((zzvk.zze)localObject8).asE);
              }
              else
              {
                localObject4 = zza((zzvk.zze)localObject8, (zzvm.zzg)localObject6);
                localObject9 = zzbvg().zzbwj();
                if (localObject4 == null) {}
                for (localObject3 = "null";; localObject3 = localObject4)
                {
                  ((zzp.zza)localObject9).zzj("Property filter result", localObject3);
                  if (localObject4 != null) {
                    break label1658;
                  }
                  localHashSet.add(Integer.valueOf(k));
                  break;
                }
                ((BitSet)localObject2).set(((zzvk.zze)localObject8).asE.intValue());
                if (((Boolean)localObject4).booleanValue()) {
                  ((BitSet)localObject1).set(((zzvk.zze)localObject8).asE.intValue());
                }
              }
            }
          }
        }
        i += 1;
        break;
        paramArrayOfzzg = new zzvm.zza[localArrayMap2.size()];
        localObject1 = localArrayMap2.keySet().iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          j = ((Integer)((Iterator)localObject1).next()).intValue();
          if (!localHashSet.contains(Integer.valueOf(j)))
          {
            paramArrayOfzzb = (zzvm.zza)localArrayMap1.get(Integer.valueOf(j));
            if (paramArrayOfzzb != null) {
              break label1908;
            }
            paramArrayOfzzb = new zzvm.zza();
          }
        }
        for (;;)
        {
          paramArrayOfzzg[i] = paramArrayOfzzb;
          paramArrayOfzzb.asA = Integer.valueOf(j);
          paramArrayOfzzb.atk = new zzvm.zzf();
          paramArrayOfzzb.atk.atV = zzal.zza((BitSet)localArrayMap2.get(Integer.valueOf(j)));
          paramArrayOfzzb.atk.atU = zzal.zza((BitSet)localArrayMap3.get(Integer.valueOf(j)));
          zzbvb().zza(paramString, j, paramArrayOfzzb.atk);
          i += 1;
          break;
          return (zzvm.zza[])Arrays.copyOf(paramArrayOfzzg, i);
        }
      }
    }
  }
  
  protected void zzym() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */