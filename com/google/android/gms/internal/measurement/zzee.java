package com.google.android.gms.internal.measurement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.Preconditions;
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

final class zzee
  extends zzhk
{
  zzee(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final Boolean zza(double paramDouble, zzkb paramzzkb)
  {
    try
    {
      BigDecimal localBigDecimal = new java/math/BigDecimal;
      localBigDecimal.<init>(paramDouble);
      paramzzkb = zza(localBigDecimal, paramzzkb, Math.ulp(paramDouble));
      return paramzzkb;
    }
    catch (NumberFormatException paramzzkb)
    {
      for (;;)
      {
        paramzzkb = null;
      }
    }
  }
  
  private final Boolean zza(long paramLong, zzkb paramzzkb)
  {
    try
    {
      BigDecimal localBigDecimal = new java/math/BigDecimal;
      localBigDecimal.<init>(paramLong);
      paramzzkb = zza(localBigDecimal, paramzzkb, 0.0D);
      return paramzzkb;
    }
    catch (NumberFormatException paramzzkb)
    {
      for (;;)
      {
        paramzzkb = null;
      }
    }
  }
  
  private static Boolean zza(Boolean paramBoolean, boolean paramBoolean1)
  {
    if (paramBoolean == null) {}
    for (paramBoolean = null;; paramBoolean = Boolean.valueOf(paramBoolean.booleanValue() ^ paramBoolean1)) {
      return paramBoolean;
    }
  }
  
  private final Boolean zza(String paramString1, int paramInt, boolean paramBoolean, String paramString2, List<String> paramList, String paramString3)
  {
    if (paramString1 == null) {
      paramString1 = null;
    }
    for (;;)
    {
      label6:
      return paramString1;
      if (paramInt == 6)
      {
        if ((paramList == null) || (paramList.size() == 0)) {
          paramString1 = null;
        }
      }
      else if (paramString2 == null)
      {
        paramString1 = null;
        continue;
      }
      String str = paramString1;
      if (!paramBoolean) {
        if (paramInt != 1) {
          break label105;
        }
      }
      label105:
      for (str = paramString1;; str = paramString1.toUpperCase(Locale.ENGLISH)) {
        switch (paramInt)
        {
        default: 
          paramString1 = null;
          break label6;
        }
      }
      if (paramBoolean) {}
      for (paramInt = 0;; paramInt = 66) {
        try
        {
          paramBoolean = Pattern.compile(paramString3, paramInt).matcher(str).matches();
          paramString1 = Boolean.valueOf(paramBoolean);
        }
        catch (PatternSyntaxException paramString1)
        {
          zzgg().zzin().zzg("Invalid regular expression in REGEXP audience filter. expression", paramString3);
          paramString1 = null;
        }
      }
      continue;
      paramString1 = Boolean.valueOf(str.startsWith(paramString2));
      continue;
      paramString1 = Boolean.valueOf(str.endsWith(paramString2));
      continue;
      paramString1 = Boolean.valueOf(str.contains(paramString2));
      continue;
      paramString1 = Boolean.valueOf(str.equals(paramString2));
      continue;
      paramString1 = Boolean.valueOf(paramList.contains(str));
    }
  }
  
  private final Boolean zza(String paramString, zzkb paramzzkb)
  {
    Object localObject = null;
    if (!zzjv.zzcd(paramString)) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      try
      {
        BigDecimal localBigDecimal = new java/math/BigDecimal;
        localBigDecimal.<init>(paramString);
        paramString = zza(localBigDecimal, paramzzkb, 0.0D);
      }
      catch (NumberFormatException paramString)
      {
        paramString = (String)localObject;
      }
    }
  }
  
  private final Boolean zza(String paramString, zzkd paramzzkd)
  {
    int i = 0;
    Object localObject1 = null;
    ArrayList localArrayList = null;
    Preconditions.checkNotNull(paramzzkd);
    Object localObject2;
    if (paramString == null) {
      localObject2 = localArrayList;
    }
    do
    {
      do
      {
        do
        {
          do
          {
            return (Boolean)localObject2;
            localObject2 = localArrayList;
          } while (paramzzkd.zzasc == null);
          localObject2 = localArrayList;
        } while (paramzzkd.zzasc.intValue() == 0);
        if (paramzzkd.zzasc.intValue() != 6) {
          break;
        }
        localObject2 = localArrayList;
      } while (paramzzkd.zzasf == null);
      localObject2 = localArrayList;
    } while (paramzzkd.zzasf.length == 0);
    label84:
    int j = paramzzkd.zzasc.intValue();
    boolean bool;
    if ((paramzzkd.zzase != null) && (paramzzkd.zzase.booleanValue()))
    {
      bool = true;
      label113:
      if ((!bool) && (j != 1) && (j != 6)) {
        break label195;
      }
      localObject2 = paramzzkd.zzasd;
      label137:
      if (paramzzkd.zzasf != null) {
        break label210;
      }
    }
    label195:
    label210:
    String[] arrayOfString;
    for (paramzzkd = null;; paramzzkd = Arrays.asList(arrayOfString))
    {
      if (j == 1) {
        localObject1 = localObject2;
      }
      localObject2 = zza(paramString, j, bool, (String)localObject2, paramzzkd, (String)localObject1);
      break;
      if (paramzzkd.zzasd != null) {
        break label84;
      }
      localObject2 = localArrayList;
      break;
      bool = false;
      break label113;
      localObject2 = paramzzkd.zzasd.toUpperCase(Locale.ENGLISH);
      break label137;
      arrayOfString = paramzzkd.zzasf;
      if (!bool) {
        break label230;
      }
    }
    label230:
    localArrayList = new ArrayList();
    int k = arrayOfString.length;
    for (;;)
    {
      paramzzkd = localArrayList;
      if (i >= k) {
        break;
      }
      localArrayList.add(arrayOfString[i].toUpperCase(Locale.ENGLISH));
      i++;
    }
  }
  
  private static Boolean zza(BigDecimal paramBigDecimal, zzkb paramzzkb, double paramDouble)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    Preconditions.checkNotNull(paramzzkb);
    if ((paramzzkb.zzaru == null) || (paramzzkb.zzaru.intValue() == 0)) {
      paramBigDecimal = null;
    }
    BigDecimal localBigDecimal1;
    BigDecimal localBigDecimal3;
    for (;;)
    {
      return paramBigDecimal;
      if (paramzzkb.zzaru.intValue() == 4)
      {
        if ((paramzzkb.zzarx == null) || (paramzzkb.zzary == null)) {
          paramBigDecimal = null;
        }
      }
      else if (paramzzkb.zzarw == null)
      {
        paramBigDecimal = null;
        continue;
      }
      int i = paramzzkb.zzaru.intValue();
      if (paramzzkb.zzaru.intValue() == 4) {
        if ((!zzjv.zzcd(paramzzkb.zzarx)) || (!zzjv.zzcd(paramzzkb.zzary)))
        {
          paramBigDecimal = null;
          continue;
        }
      }
      for (;;)
      {
        try
        {
          localBigDecimal1 = new java/math/BigDecimal;
          localBigDecimal1.<init>(paramzzkb.zzarx);
          BigDecimal localBigDecimal2 = new java/math/BigDecimal;
          localBigDecimal2.<init>(paramzzkb.zzary);
          localBigDecimal3 = null;
          paramzzkb = localBigDecimal2;
          if (i != 4) {
            break label226;
          }
          if (localBigDecimal1 != null) {
            break label231;
          }
          paramBigDecimal = null;
        }
        catch (NumberFormatException paramBigDecimal)
        {
          paramBigDecimal = null;
        }
        break;
        if (!zzjv.zzcd(paramzzkb.zzarw))
        {
          paramBigDecimal = null;
          break;
        }
        try
        {
          localBigDecimal3 = new BigDecimal(paramzzkb.zzarw);
          paramzzkb = null;
          localBigDecimal1 = null;
        }
        catch (NumberFormatException paramBigDecimal)
        {
          paramBigDecimal = null;
        }
      }
      continue;
      label226:
      if (localBigDecimal3 != null) {}
      switch (i)
      {
      default: 
        label231:
        paramBigDecimal = null;
      }
    }
    if (paramBigDecimal.compareTo(localBigDecimal3) == -1) {}
    for (;;)
    {
      paramBigDecimal = Boolean.valueOf(bool5);
      break;
      bool5 = false;
    }
    if (paramBigDecimal.compareTo(localBigDecimal3) == 1) {}
    for (bool5 = bool1;; bool5 = false)
    {
      paramBigDecimal = Boolean.valueOf(bool5);
      break;
    }
    if (paramDouble != 0.0D)
    {
      if ((paramBigDecimal.compareTo(localBigDecimal3.subtract(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == 1) && (paramBigDecimal.compareTo(localBigDecimal3.add(new BigDecimal(paramDouble).multiply(new BigDecimal(2)))) == -1)) {}
      for (bool5 = bool2;; bool5 = false)
      {
        paramBigDecimal = Boolean.valueOf(bool5);
        break;
      }
    }
    if (paramBigDecimal.compareTo(localBigDecimal3) == 0) {}
    for (bool5 = bool3;; bool5 = false)
    {
      paramBigDecimal = Boolean.valueOf(bool5);
      break;
    }
    if ((paramBigDecimal.compareTo(localBigDecimal1) != -1) && (paramBigDecimal.compareTo(paramzzkb) != 1)) {}
    for (bool5 = bool4;; bool5 = false)
    {
      paramBigDecimal = Boolean.valueOf(bool5);
      break;
    }
  }
  
  final zzkh[] zza(String paramString, zzki[] paramArrayOfzzki, zzkn[] paramArrayOfzzkn)
  {
    Preconditions.checkNotEmpty(paramString);
    HashSet localHashSet = new HashSet();
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    Object localObject1 = zzga().zzba(paramString);
    Object localObject3;
    int i;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    Object localObject7;
    int j;
    if (localObject1 != null)
    {
      localObject3 = ((Map)localObject1).keySet().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        i = ((Integer)((Iterator)localObject3).next()).intValue();
        localObject4 = (zzkm)((Map)localObject1).get(Integer.valueOf(i));
        localObject5 = (BitSet)localArrayMap2.get(Integer.valueOf(i));
        localObject6 = (BitSet)localArrayMap3.get(Integer.valueOf(i));
        localObject7 = localObject5;
        if (localObject5 == null)
        {
          localObject7 = new BitSet();
          localArrayMap2.put(Integer.valueOf(i), localObject7);
          localObject6 = new BitSet();
          localArrayMap3.put(Integer.valueOf(i), localObject6);
        }
        for (j = 0; j < ((zzkm)localObject4).zzauf.length << 6; j++) {
          if (zzjv.zza(((zzkm)localObject4).zzauf, j))
          {
            zzgg().zzir().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(i), Integer.valueOf(j));
            ((BitSet)localObject6).set(j);
            if (zzjv.zza(((zzkm)localObject4).zzaug, j)) {
              ((BitSet)localObject7).set(j);
            }
          }
        }
        localObject5 = new zzkh();
        localArrayMap1.put(Integer.valueOf(i), localObject5);
        ((zzkh)localObject5).zzast = Boolean.valueOf(false);
        ((zzkh)localObject5).zzass = ((zzkm)localObject4);
        ((zzkh)localObject5).zzasr = new zzkm();
        ((zzkh)localObject5).zzasr.zzaug = zzjv.zza((BitSet)localObject7);
        ((zzkh)localObject5).zzasr.zzauf = zzjv.zza((BitSet)localObject6);
      }
    }
    long l1;
    Object localObject8;
    label466:
    int m;
    label485:
    label551:
    label557:
    Object localObject9;
    if (paramArrayOfzzki != null)
    {
      localObject6 = null;
      l1 = 0L;
      localObject5 = null;
      localObject8 = new ArrayMap();
      int k = paramArrayOfzzki.length;
      i = 0;
      if (i < k)
      {
        localObject4 = paramArrayOfzzki[i];
        localObject3 = ((zzki)localObject4).name;
        localObject7 = ((zzki)localObject4).zzasv;
        if (!zzgi().zzd(paramString, zzew.zzahy)) {
          break label4103;
        }
        zzgc();
        localObject1 = (Long)zzjv.zzb((zzki)localObject4, "_eid");
        if (localObject1 != null)
        {
          j = 1;
          if ((j == 0) || (!((String)localObject3).equals("_ep"))) {
            break label551;
          }
          m = 1;
          if (m == 0) {
            break label1211;
          }
          zzgc();
          localObject3 = (String)zzjv.zzb((zzki)localObject4, "_en");
          if (!TextUtils.isEmpty((CharSequence)localObject3)) {
            break label557;
          }
          zzgg().zzil().zzg("Extra parameter without an event name. eventId", localObject1);
        }
        for (localObject7 = localObject6;; localObject7 = localObject6)
        {
          i++;
          localObject6 = localObject7;
          break;
          j = 0;
          break label466;
          m = 0;
          break label485;
          if ((localObject6 != null) && (localObject5 != null) && (((Long)localObject1).longValue() == ((Long)localObject5).longValue())) {
            break label4126;
          }
          localObject9 = zzga().zza(paramString, (Long)localObject1);
          if ((localObject9 != null) && (((Pair)localObject9).first != null)) {
            break label630;
          }
          zzgg().zzil().zze("Extra parameter without existing main event. eventName, eventId", localObject3, localObject1);
        }
        label630:
        localObject6 = (zzki)((Pair)localObject9).first;
        l1 = ((Long)((Pair)localObject9).second).longValue();
        zzgc();
        localObject5 = (Long)zzjv.zzb((zzki)localObject6, "_eid");
        l1 -= 1L;
        if (l1 <= 0L)
        {
          localObject9 = zzga();
          ((zzhj)localObject9).zzab();
          ((zzhj)localObject9).zzgg().zzir().zzg("Clearing complex main event info. appId", paramString);
        }
      }
    }
    label856:
    label927:
    label1002:
    label1097:
    label1211:
    label1308:
    label1346:
    label1726:
    label1889:
    label1967:
    label2025:
    label2666:
    label2672:
    label2681:
    label2688:
    label3289:
    label3659:
    label3665:
    label3699:
    label4097:
    label4100:
    label4103:
    label4126:
    label4143:
    label4146:
    for (;;)
    {
      Object localObject10;
      int n;
      int i1;
      try
      {
        ((zzei)localObject9).getWritableDatabase().execSQL("delete from main_event_params where app_id=?", new String[] { paramString });
        localObject1 = new zzkj[((zzki)localObject6).zzasv.length + localObject7.length];
        j = 0;
        localObject10 = ((zzki)localObject6).zzasv;
        n = localObject10.length;
        m = 0;
        if (m >= n) {
          break label856;
        }
        localObject9 = localObject10[m];
        zzgc();
        if (zzjv.zza((zzki)localObject4, ((zzkj)localObject9).name) != null) {
          break label4146;
        }
        i1 = j + 1;
        localObject1[j] = localObject9;
        j = i1;
        m++;
        continue;
      }
      catch (SQLiteException localSQLiteException)
      {
        ((zzhj)localObject9).zzgg().zzil().zzg("Error clearing complex main event", localSQLiteException);
        continue;
      }
      zzga().zza(paramString, localSQLiteException, l1, (zzki)localObject6);
      continue;
      Object localObject2;
      long l2;
      if (j > 0)
      {
        i1 = localObject7.length;
        m = 0;
        while (m < i1)
        {
          localSQLiteException[j] = localObject7[m];
          m++;
          j++;
        }
        if (j == localSQLiteException.length)
        {
          localObject7 = localSQLiteException;
          localObject2 = localObject5;
          localObject5 = localObject6;
          localObject6 = localObject2;
          localObject2 = localObject3;
          localObject3 = localObject7;
          localObject7 = zzga().zze(paramString, ((zzki)localObject4).name);
          if (localObject7 != null) {
            break label1346;
          }
          zzgg().zzin().zze("Event aggregate wasn't created during raw event logging. appId, event", zzfg.zzbh(paramString), zzgb().zzbe((String)localObject2));
          localObject7 = new zzeq(paramString, ((zzki)localObject4).name, 1L, 1L, ((zzki)localObject4).zzasw.longValue(), 0L, null, null, null);
          zzga().zza((zzeq)localObject7);
          l2 = ((zzeq)localObject7).zzafp;
          localObject4 = (Map)((Map)localObject8).get(localObject2);
          if (localObject4 != null) {
            break label4143;
          }
          localObject4 = zzga().zzj(paramString, (String)localObject2);
          localObject7 = localObject4;
          if (localObject4 == null) {
            localObject7 = new ArrayMap();
          }
          ((Map)localObject8).put(localObject2, localObject7);
          localObject4 = localObject7;
        }
      }
      for (;;)
      {
        Iterator localIterator1 = ((Map)localObject4).keySet().iterator();
        for (;;)
        {
          if (localIterator1.hasNext())
          {
            i1 = ((Integer)localIterator1.next()).intValue();
            if (localHashSet.contains(Integer.valueOf(i1)))
            {
              zzgg().zzir().zzg("Skipping failed audience ID", Integer.valueOf(i1));
              continue;
              localObject7 = (zzkj[])Arrays.copyOf((Object[])localObject2, j);
              break;
              zzgg().zzin().zzg("No unique parameters in main event. eventName", localObject3);
              localObject2 = localObject3;
              localObject9 = localObject6;
              localObject3 = localObject7;
              localObject6 = localObject5;
              localObject5 = localObject9;
              break label927;
              if (j == 0) {
                break label4103;
              }
              zzgc();
              localObject6 = Long.valueOf(0L);
              localObject5 = zzjv.zzb((zzki)localObject4, "_epc");
              if (localObject5 == null) {}
              for (;;)
              {
                l1 = ((Long)localObject6).longValue();
                if (l1 > 0L) {
                  break label1308;
                }
                zzgg().zzin().zzg("Complex event with zero extra param count. eventName", localObject3);
                localObject6 = localObject3;
                localObject9 = localObject2;
                localObject5 = localObject4;
                localObject3 = localObject7;
                localObject2 = localObject6;
                localObject6 = localObject9;
                break;
                localObject6 = localObject5;
              }
              zzga().zza(paramString, (Long)localObject2, l1, (zzki)localObject4);
              localObject9 = localObject3;
              localObject6 = localObject2;
              localObject5 = localObject4;
              localObject3 = localObject7;
              localObject2 = localObject9;
              break label927;
              localObject7 = ((zzeq)localObject7).zzie();
              break label1002;
            }
            localObject7 = (zzkh)localArrayMap1.get(Integer.valueOf(i1));
            localObject9 = (BitSet)localArrayMap2.get(Integer.valueOf(i1));
            localObject10 = (BitSet)localArrayMap3.get(Integer.valueOf(i1));
            if (localObject7 == null)
            {
              localObject7 = new zzkh();
              localArrayMap1.put(Integer.valueOf(i1), localObject7);
              ((zzkh)localObject7).zzast = Boolean.valueOf(true);
              localObject9 = new BitSet();
              localArrayMap2.put(Integer.valueOf(i1), localObject9);
              localObject10 = new BitSet();
              localArrayMap3.put(Integer.valueOf(i1), localObject10);
            }
            for (;;)
            {
              Iterator localIterator2 = ((List)((Map)localObject4).get(Integer.valueOf(i1))).iterator();
              Object localObject12;
              while (localIterator2.hasNext())
              {
                zzjz localzzjz = (zzjz)localIterator2.next();
                if (zzgg().isLoggable(2))
                {
                  zzgg().zzir().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(i1), localzzjz.zzark, zzgb().zzbe(localzzjz.zzarl));
                  zzgg().zzir().zzg("Filter definition", zzgb().zza(localzzjz));
                }
                if ((localzzjz.zzark == null) || (localzzjz.zzark.intValue() > 256))
                {
                  zzgg().zzin().zze("Invalid event filter ID. appId, id", zzfg.zzbh(paramString), String.valueOf(localzzjz.zzark));
                }
                else if (((BitSet)localObject9).get(localzzjz.zzark.intValue()))
                {
                  zzgg().zzir().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(i1), localzzjz.zzark);
                }
                else
                {
                  Object localObject11;
                  if (localzzjz.zzaro != null)
                  {
                    localObject7 = zza(l2, localzzjz.zzaro);
                    if (localObject7 == null)
                    {
                      localObject7 = null;
                      localObject11 = zzgg().zzir();
                      if (localObject7 != null) {
                        break label2681;
                      }
                    }
                  }
                  for (localObject12 = "null";; localObject12 = localObject7)
                  {
                    ((zzfi)localObject11).zzg("Event filter result", localObject12);
                    if (localObject7 != null) {
                      break label2688;
                    }
                    localHashSet.add(Integer.valueOf(i1));
                    break;
                    if (!((Boolean)localObject7).booleanValue())
                    {
                      localObject7 = Boolean.valueOf(false);
                      break label1726;
                    }
                    localObject7 = new HashSet();
                    localObject12 = localzzjz.zzarm;
                    m = localObject12.length;
                    for (j = 0;; j++)
                    {
                      if (j >= m) {
                        break label1889;
                      }
                      localObject11 = localObject12[j];
                      if (TextUtils.isEmpty(((zzka)localObject11).zzart))
                      {
                        zzgg().zzin().zzg("null or empty param name in filter. event", zzgb().zzbe((String)localObject2));
                        localObject7 = null;
                        break;
                      }
                      ((Set)localObject7).add(((zzka)localObject11).zzart);
                    }
                    localObject12 = new ArrayMap();
                    m = localObject3.length;
                    j = 0;
                    if (j < m)
                    {
                      localObject11 = localObject3[j];
                      if (((Set)localObject7).contains(((zzkj)localObject11).name))
                      {
                        if (((zzkj)localObject11).zzasz == null) {
                          break label1967;
                        }
                        ((Map)localObject12).put(((zzkj)localObject11).name, ((zzkj)localObject11).zzasz);
                      }
                      for (;;)
                      {
                        j++;
                        break;
                        if (((zzkj)localObject11).zzaqx != null)
                        {
                          ((Map)localObject12).put(((zzkj)localObject11).name, ((zzkj)localObject11).zzaqx);
                        }
                        else
                        {
                          if (((zzkj)localObject11).zzajf == null) {
                            break label2025;
                          }
                          ((Map)localObject12).put(((zzkj)localObject11).name, ((zzkj)localObject11).zzajf);
                        }
                      }
                      zzgg().zzin().zze("Unknown value for param. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(((zzkj)localObject11).name));
                      localObject7 = null;
                      break label1726;
                    }
                    localObject11 = localzzjz.zzarm;
                    n = localObject11.length;
                    for (j = 0;; j++)
                    {
                      if (j >= n) {
                        break label2672;
                      }
                      Object localObject13 = localObject11[j];
                      int i2 = Boolean.TRUE.equals(((zzka)localObject13).zzars);
                      String str = ((zzka)localObject13).zzart;
                      if (TextUtils.isEmpty(str))
                      {
                        zzgg().zzin().zzg("Event has empty param name. event", zzgb().zzbe((String)localObject2));
                        localObject7 = null;
                        break;
                      }
                      localObject7 = ((Map)localObject12).get(str);
                      if ((localObject7 instanceof Long))
                      {
                        if (((zzka)localObject13).zzarr == null)
                        {
                          zzgg().zzin().zze("No number filter for long param. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                          localObject7 = null;
                          break;
                        }
                        localObject7 = zza(((Long)localObject7).longValue(), ((zzka)localObject13).zzarr);
                        if (localObject7 == null)
                        {
                          localObject7 = null;
                          break;
                        }
                        if (!((Boolean)localObject7).booleanValue()) {}
                        for (m = 1;; m = 0)
                        {
                          if ((m ^ i2) == 0) {
                            break label2666;
                          }
                          localObject7 = Boolean.valueOf(false);
                          break;
                        }
                      }
                      if ((localObject7 instanceof Double))
                      {
                        if (((zzka)localObject13).zzarr == null)
                        {
                          zzgg().zzin().zze("No number filter for double param. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                          localObject7 = null;
                          break;
                        }
                        localObject7 = zza(((Double)localObject7).doubleValue(), ((zzka)localObject13).zzarr);
                        if (localObject7 == null)
                        {
                          localObject7 = null;
                          break;
                        }
                        if (!((Boolean)localObject7).booleanValue()) {}
                        for (m = 1;; m = 0)
                        {
                          if ((m ^ i2) == 0) {
                            break label2666;
                          }
                          localObject7 = Boolean.valueOf(false);
                          break;
                        }
                      }
                      if ((localObject7 instanceof String))
                      {
                        if (((zzka)localObject13).zzarq != null) {
                          localObject7 = zza((String)localObject7, ((zzka)localObject13).zzarq);
                        }
                        for (;;)
                        {
                          if (localObject7 == null)
                          {
                            localObject7 = null;
                            break;
                            if (((zzka)localObject13).zzarr != null)
                            {
                              if (zzjv.zzcd((String)localObject7))
                              {
                                localObject7 = zza((String)localObject7, ((zzka)localObject13).zzarr);
                                continue;
                              }
                              zzgg().zzin().zze("Invalid param value for number filter. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                              localObject7 = null;
                              break;
                            }
                            zzgg().zzin().zze("No filter for String param. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                            localObject7 = null;
                            break;
                          }
                        }
                        if (!((Boolean)localObject7).booleanValue()) {}
                        for (m = 1;; m = 0)
                        {
                          if ((m ^ i2) == 0) {
                            break label2666;
                          }
                          localObject7 = Boolean.valueOf(false);
                          break;
                        }
                      }
                      if (localObject7 == null)
                      {
                        zzgg().zzir().zze("Missing param for filter. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                        localObject7 = Boolean.valueOf(false);
                        break;
                      }
                      zzgg().zzin().zze("Unknown param type. event, param", zzgb().zzbe((String)localObject2), zzgb().zzbf(str));
                      localObject7 = null;
                      break;
                    }
                    localObject7 = Boolean.valueOf(true);
                    break label1726;
                  }
                  ((BitSet)localObject10).set(localzzjz.zzark.intValue());
                  if (((Boolean)localObject7).booleanValue()) {
                    ((BitSet)localObject9).set(localzzjz.zzark.intValue());
                  }
                }
              }
              break label1097;
              if (paramArrayOfzzkn != null)
              {
                localObject4 = new ArrayMap();
                i = paramArrayOfzzkn.length;
                j = 0;
                if (j < i)
                {
                  localObject2 = paramArrayOfzzkn[j];
                  localObject7 = (Map)((Map)localObject4).get(((zzkn)localObject2).name);
                  if (localObject7 != null) {
                    break label4100;
                  }
                  localObject7 = zzga().zzk(paramString, ((zzkn)localObject2).name);
                  paramArrayOfzzki = (zzki[])localObject7;
                  if (localObject7 == null) {
                    paramArrayOfzzki = new ArrayMap();
                  }
                  ((Map)localObject4).put(((zzkn)localObject2).name, paramArrayOfzzki);
                  localObject7 = paramArrayOfzzki;
                }
              }
              for (;;)
              {
                localObject9 = ((Map)localObject7).keySet().iterator();
                while (((Iterator)localObject9).hasNext())
                {
                  m = ((Integer)((Iterator)localObject9).next()).intValue();
                  if (localHashSet.contains(Integer.valueOf(m)))
                  {
                    zzgg().zzir().zzg("Skipping failed audience ID", Integer.valueOf(m));
                  }
                  else
                  {
                    paramArrayOfzzki = (zzkh)localArrayMap1.get(Integer.valueOf(m));
                    localObject6 = (BitSet)localArrayMap2.get(Integer.valueOf(m));
                    localObject5 = (BitSet)localArrayMap3.get(Integer.valueOf(m));
                    if (paramArrayOfzzki == null)
                    {
                      paramArrayOfzzki = new zzkh();
                      localArrayMap1.put(Integer.valueOf(m), paramArrayOfzzki);
                      paramArrayOfzzki.zzast = Boolean.valueOf(true);
                      localObject6 = new BitSet();
                      localArrayMap2.put(Integer.valueOf(m), localObject6);
                      localObject5 = new BitSet();
                      localArrayMap3.put(Integer.valueOf(m), localObject5);
                    }
                    localObject12 = ((List)((Map)localObject7).get(Integer.valueOf(m))).iterator();
                    for (;;)
                    {
                      if (!((Iterator)localObject12).hasNext()) {
                        break label3699;
                      }
                      localObject10 = (zzkc)((Iterator)localObject12).next();
                      if (zzgg().isLoggable(2))
                      {
                        zzgg().zzir().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(m), ((zzkc)localObject10).zzark, zzgb().zzbg(((zzkc)localObject10).zzasa));
                        zzgg().zzir().zzg("Filter definition", zzgb().zza((zzkc)localObject10));
                      }
                      if ((((zzkc)localObject10).zzark == null) || (((zzkc)localObject10).zzark.intValue() > 256))
                      {
                        zzgg().zzin().zze("Invalid property filter ID. appId, id", zzfg.zzbh(paramString), String.valueOf(((zzkc)localObject10).zzark));
                        localHashSet.add(Integer.valueOf(m));
                        break;
                      }
                      if (((BitSet)localObject6).get(((zzkc)localObject10).zzark.intValue()))
                      {
                        zzgg().zzir().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(m), ((zzkc)localObject10).zzark);
                      }
                      else
                      {
                        paramArrayOfzzki = ((zzkc)localObject10).zzasb;
                        if (paramArrayOfzzki == null)
                        {
                          zzgg().zzin().zzg("Missing property filter. property", zzgb().zzbg(((zzkn)localObject2).name));
                          paramArrayOfzzki = null;
                          localObject8 = zzgg().zzir();
                          if (paramArrayOfzzki != null) {
                            break label3659;
                          }
                        }
                        for (localObject3 = "null";; localObject3 = paramArrayOfzzki)
                        {
                          ((zzfi)localObject8).zzg("Property filter result", localObject3);
                          if (paramArrayOfzzki != null) {
                            break label3665;
                          }
                          localHashSet.add(Integer.valueOf(m));
                          break;
                          boolean bool = Boolean.TRUE.equals(paramArrayOfzzki.zzars);
                          if (((zzkn)localObject2).zzasz != null)
                          {
                            if (paramArrayOfzzki.zzarr == null)
                            {
                              zzgg().zzin().zzg("No number filter for long property. property", zzgb().zzbg(((zzkn)localObject2).name));
                              paramArrayOfzzki = null;
                              break label3289;
                            }
                            paramArrayOfzzki = zza(zza(((zzkn)localObject2).zzasz.longValue(), paramArrayOfzzki.zzarr), bool);
                            break label3289;
                          }
                          if (((zzkn)localObject2).zzaqx != null)
                          {
                            if (paramArrayOfzzki.zzarr == null)
                            {
                              zzgg().zzin().zzg("No number filter for double property. property", zzgb().zzbg(((zzkn)localObject2).name));
                              paramArrayOfzzki = null;
                              break label3289;
                            }
                            paramArrayOfzzki = zza(zza(((zzkn)localObject2).zzaqx.doubleValue(), paramArrayOfzzki.zzarr), bool);
                            break label3289;
                          }
                          if (((zzkn)localObject2).zzajf != null)
                          {
                            if (paramArrayOfzzki.zzarq == null)
                            {
                              if (paramArrayOfzzki.zzarr == null) {
                                zzgg().zzin().zzg("No string or number filter defined. property", zzgb().zzbg(((zzkn)localObject2).name));
                              }
                              for (;;)
                              {
                                paramArrayOfzzki = null;
                                break;
                                if (zzjv.zzcd(((zzkn)localObject2).zzajf))
                                {
                                  paramArrayOfzzki = zza(zza(((zzkn)localObject2).zzajf, paramArrayOfzzki.zzarr), bool);
                                  break;
                                }
                                zzgg().zzin().zze("Invalid user property value for Numeric number filter. property, value", zzgb().zzbg(((zzkn)localObject2).name), ((zzkn)localObject2).zzajf);
                              }
                            }
                            paramArrayOfzzki = zza(zza(((zzkn)localObject2).zzajf, paramArrayOfzzki.zzarq), bool);
                            break label3289;
                          }
                          zzgg().zzin().zzg("User property has no value, property", zzgb().zzbg(((zzkn)localObject2).name));
                          paramArrayOfzzki = null;
                          break label3289;
                        }
                        ((BitSet)localObject5).set(((zzkc)localObject10).zzark.intValue());
                        if (paramArrayOfzzki.booleanValue()) {
                          ((BitSet)localObject6).set(((zzkc)localObject10).zzark.intValue());
                        }
                      }
                    }
                  }
                }
                j++;
                break;
                paramArrayOfzzkn = new zzkh[localArrayMap2.size()];
                localObject7 = localArrayMap2.keySet().iterator();
                j = 0;
                while (((Iterator)localObject7).hasNext())
                {
                  m = ((Integer)((Iterator)localObject7).next()).intValue();
                  if (!localHashSet.contains(Integer.valueOf(m)))
                  {
                    paramArrayOfzzki = (zzkh)localArrayMap1.get(Integer.valueOf(m));
                    if (paramArrayOfzzki != null) {
                      break label4097;
                    }
                    paramArrayOfzzki = new zzkh();
                  }
                }
                for (;;)
                {
                  i = j + 1;
                  paramArrayOfzzkn[j] = paramArrayOfzzki;
                  paramArrayOfzzki.zzarg = Integer.valueOf(m);
                  paramArrayOfzzki.zzasr = new zzkm();
                  paramArrayOfzzki.zzasr.zzaug = zzjv.zza((BitSet)localArrayMap2.get(Integer.valueOf(m)));
                  paramArrayOfzzki.zzasr.zzauf = zzjv.zza((BitSet)localArrayMap3.get(Integer.valueOf(m)));
                  localObject6 = zzga();
                  localObject5 = paramArrayOfzzki.zzasr;
                  ((zzhk)localObject6).zzch();
                  ((zzhj)localObject6).zzab();
                  Preconditions.checkNotEmpty(paramString);
                  Preconditions.checkNotNull(localObject5);
                  try
                  {
                    paramArrayOfzzki = new byte[((zzabj)localObject5).zzwg()];
                    localObject3 = zzabb.zzb(paramArrayOfzzki, 0, paramArrayOfzzki.length);
                    ((zzabj)localObject5).zza((zzabb)localObject3);
                    ((zzabb)localObject3).zzvy();
                    localObject5 = new ContentValues();
                    ((ContentValues)localObject5).put("app_id", paramString);
                    ((ContentValues)localObject5).put("audience_id", Integer.valueOf(m));
                    ((ContentValues)localObject5).put("current_results", paramArrayOfzzki);
                  }
                  catch (IOException paramArrayOfzzki)
                  {
                    try
                    {
                      if (((zzei)localObject6).getWritableDatabase().insertWithOnConflict("audience_filter_values", null, (ContentValues)localObject5, 5) == -1L) {
                        ((zzhj)localObject6).zzgg().zzil().zzg("Failed to insert filter results (got -1). appId", zzfg.zzbh(paramString));
                      }
                      j = i;
                    }
                    catch (SQLiteException paramArrayOfzzki)
                    {
                      ((zzhj)localObject6).zzgg().zzil().zze("Error storing filter results. appId", zzfg.zzbh(paramString), paramArrayOfzzki);
                      j = i;
                    }
                    paramArrayOfzzki = paramArrayOfzzki;
                    ((zzhj)localObject6).zzgg().zzil().zze("Configuration loss. Failed to serialize filter results. appId", zzfg.zzbh(paramString), paramArrayOfzzki);
                    j = i;
                  }
                  break;
                  break;
                  return (zzkh[])Arrays.copyOf(paramArrayOfzzkn, j);
                }
              }
              localObject2 = localObject3;
              localObject9 = localObject6;
              localObject3 = localObject7;
              localObject6 = localObject5;
              localObject5 = localObject9;
              break label927;
              break;
            }
          }
        }
        localObject7 = localObject5;
        localObject5 = localObject6;
        break;
      }
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzee.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */