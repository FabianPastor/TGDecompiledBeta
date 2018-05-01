package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class zzaue
{
  private static volatile zzaue zzbub;
  private final Context mContext;
  private final boolean zzadP;
  private FileLock zzbuA;
  private FileChannel zzbuB;
  private List<Long> zzbuC;
  private int zzbuD;
  private int zzbuE;
  private long zzbuF;
  protected long zzbuG;
  private final long zzbuH;
  private final zzati zzbuc;
  private final zzaua zzbud;
  private final zzatx zzbue;
  private final zzaud zzbuf;
  private final zzaun zzbug;
  private final zzauc zzbuh;
  private final AppMeasurement zzbui;
  private final FirebaseAnalytics zzbuj;
  private final zzaut zzbuk;
  private final zzatj zzbul;
  private final zzatv zzbum;
  private final zzaty zzbun;
  private final zzauk zzbuo;
  private final zzaul zzbup;
  private final zzatl zzbuq;
  private final zzauj zzbur;
  private final zzatu zzbus;
  private final zzatz zzbut;
  private final zzaup zzbuu;
  private final zzatf zzbuv;
  private final zzatb zzbuw;
  private boolean zzbux;
  private Boolean zzbuy;
  private long zzbuz;
  private final zze zzuP;
  
  zzaue(zzaui paramzzaui)
  {
    zzac.zzw(paramzzaui);
    this.mContext = paramzzaui.mContext;
    this.zzbuF = -1L;
    this.zzuP = paramzzaui.zzn(this);
    this.zzbuH = this.zzuP.currentTimeMillis();
    this.zzbuc = paramzzaui.zza(this);
    Object localObject = paramzzaui.zzb(this);
    ((zzaua)localObject).initialize();
    this.zzbud = ((zzaua)localObject);
    localObject = paramzzaui.zzc(this);
    ((zzatx)localObject).initialize();
    this.zzbue = ((zzatx)localObject);
    zzKl().zzMd().zzj("App measurement is starting up, version", Long.valueOf(zzKn().zzKv()));
    zzKn().zzLh();
    zzKl().zzMd().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
    localObject = paramzzaui.zzj(this);
    ((zzaut)localObject).initialize();
    this.zzbuk = ((zzaut)localObject);
    localObject = paramzzaui.zzq(this);
    ((zzatl)localObject).initialize();
    this.zzbuq = ((zzatl)localObject);
    localObject = paramzzaui.zzr(this);
    ((zzatu)localObject).initialize();
    this.zzbus = ((zzatu)localObject);
    zzKn().zzLh();
    localObject = ((zzatu)localObject).zzke();
    if (zzKh().zzge((String)localObject))
    {
      zzKl().zzMd().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
      zzKl().zzMe().log("Debug-level message logging enabled");
      localObject = paramzzaui.zzk(this);
      ((zzatj)localObject).initialize();
      this.zzbul = ((zzatj)localObject);
      localObject = paramzzaui.zzl(this);
      ((zzatv)localObject).initialize();
      this.zzbum = ((zzatv)localObject);
      localObject = paramzzaui.zzu(this);
      ((zzatf)localObject).initialize();
      this.zzbuv = ((zzatf)localObject);
      this.zzbuw = paramzzaui.zzv(this);
      localObject = paramzzaui.zzm(this);
      ((zzaty)localObject).initialize();
      this.zzbun = ((zzaty)localObject);
      localObject = paramzzaui.zzo(this);
      ((zzauk)localObject).initialize();
      this.zzbuo = ((zzauk)localObject);
      localObject = paramzzaui.zzp(this);
      ((zzaul)localObject).initialize();
      this.zzbup = ((zzaul)localObject);
      localObject = paramzzaui.zzi(this);
      ((zzauj)localObject).initialize();
      this.zzbur = ((zzauj)localObject);
      localObject = paramzzaui.zzt(this);
      ((zzaup)localObject).initialize();
      this.zzbuu = ((zzaup)localObject);
      this.zzbut = paramzzaui.zzs(this);
      this.zzbui = paramzzaui.zzh(this);
      this.zzbuj = paramzzaui.zzg(this);
      localObject = paramzzaui.zze(this);
      ((zzaun)localObject).initialize();
      this.zzbug = ((zzaun)localObject);
      localObject = paramzzaui.zzf(this);
      ((zzauc)localObject).initialize();
      this.zzbuh = ((zzauc)localObject);
      paramzzaui = paramzzaui.zzd(this);
      paramzzaui.initialize();
      this.zzbuf = paramzzaui;
      if (this.zzbuD != this.zzbuE) {
        zzKl().zzLZ().zze("Not all components initialized", Integer.valueOf(this.zzbuD), Integer.valueOf(this.zzbuE));
      }
      this.zzadP = true;
      this.zzbuc.zzLh();
      if (!(this.mContext.getApplicationContext() instanceof Application)) {
        break label564;
      }
      int i = Build.VERSION.SDK_INT;
      zzKa().zzMS();
    }
    for (;;)
    {
      this.zzbuf.zzm(new Runnable()
      {
        public void run()
        {
          zzaue.this.start();
        }
      });
      return;
      zzatx.zza localzza = zzKl().zzMd();
      localObject = String.valueOf(localObject);
      if (((String)localObject).length() != 0) {}
      for (localObject = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ".concat((String)localObject);; localObject = new String("To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app "))
      {
        localzza.log((String)localObject);
        break;
      }
      label564:
      zzKl().zzMb().log("Application context is not an Application");
    }
  }
  
  private boolean zzMJ()
  {
    zzmR();
    zzob();
    return (zzKg().zzLK()) || (!TextUtils.isEmpty(zzKg().zzLE()));
  }
  
  @WorkerThread
  private void zzMK()
  {
    zzmR();
    zzob();
    if (!zzMO()) {
      return;
    }
    if (this.zzbuG > 0L)
    {
      l1 = 3600000L - Math.abs(zznR().elapsedRealtime() - this.zzbuG);
      if (l1 > 0L)
      {
        zzKl().zzMf().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(l1));
        zzMA().unregister();
        zzMB().cancel();
        return;
      }
      this.zzbuG = 0L;
    }
    if ((!zzMu()) || (!zzMJ()))
    {
      zzMA().unregister();
      zzMB().cancel();
      return;
    }
    long l2 = zzML();
    if (l2 == 0L)
    {
      zzMA().unregister();
      zzMB().cancel();
      return;
    }
    if (!zzMz().zzqa())
    {
      zzMA().zzpX();
      zzMB().cancel();
      return;
    }
    long l3 = zzKm().zzbtd.get();
    long l4 = zzKn().zzLt();
    long l1 = l2;
    if (!zzKh().zzh(l3, l4)) {
      l1 = Math.max(l2, l3 + l4);
    }
    zzMA().unregister();
    l2 = l1 - zznR().currentTimeMillis();
    l1 = l2;
    if (l2 <= 0L)
    {
      l1 = zzKn().zzLx();
      zzKm().zzbtb.set(zznR().currentTimeMillis());
    }
    zzKl().zzMf().zzj("Upload scheduled in approximately ms", Long.valueOf(l1));
    zzMB().zzy(l1);
  }
  
  private long zzML()
  {
    long l3 = zznR().currentTimeMillis();
    long l1 = zzKn().zzLA();
    long l2;
    label82:
    long l6;
    long l5;
    long l4;
    if ((zzKg().zzLL()) || (zzKg().zzLF()))
    {
      i = 1;
      if (i == 0) {
        break label155;
      }
      String str = zzKn().zzLD();
      if ((TextUtils.isEmpty(str)) || (".none.".equals(str))) {
        break label143;
      }
      l2 = zzKn().zzLw();
      l6 = zzKm().zzbtb.get();
      l5 = zzKm().zzbtc.get();
      l4 = Math.max(zzKg().zzLI(), zzKg().zzLJ());
      if (l4 != 0L) {
        break label167;
      }
      l2 = 0L;
    }
    label143:
    label155:
    label167:
    do
    {
      do
      {
        return l2;
        i = 0;
        break;
        l2 = zzKn().zzLv();
        break label82;
        l2 = zzKn().zzLu();
        break label82;
        l4 = l3 - Math.abs(l4 - l3);
        l6 = Math.abs(l6 - l3);
        l5 = l3 - Math.abs(l5 - l3);
        l6 = Math.max(l3 - l6, l5);
        l3 = l4 + l1;
        l1 = l3;
        if (i != 0)
        {
          l1 = l3;
          if (l6 > 0L) {
            l1 = Math.min(l4, l6) + l2;
          }
        }
        if (!zzKh().zzh(l6, l2)) {
          l1 = l6 + l2;
        }
        l2 = l1;
      } while (l5 == 0L);
      l2 = l1;
    } while (l5 < l4);
    int i = 0;
    for (;;)
    {
      if (i >= zzKn().zzLC()) {
        break label335;
      }
      l1 += (1 << i) * zzKn().zzLB();
      l2 = l1;
      if (l1 > l5) {
        break;
      }
      i += 1;
    }
    label335:
    return 0L;
  }
  
  private void zza(zzaug paramzzaug)
  {
    if (paramzzaug == null) {
      throw new IllegalStateException("Component not created");
    }
  }
  
  private void zza(zzauh paramzzauh)
  {
    if (paramzzauh == null) {
      throw new IllegalStateException("Component not created");
    }
    if (!paramzzauh.isInitialized()) {
      throw new IllegalStateException("Component not initialized");
    }
  }
  
  private boolean zza(zzatm paramzzatm)
  {
    if (paramzzatm.zzbrA == null) {}
    Object localObject;
    boolean bool;
    do
    {
      return false;
      localObject = paramzzatm.zzbrA.iterator();
      while (((Iterator)localObject).hasNext()) {
        if ("_r".equals((String)((Iterator)localObject).next())) {
          return true;
        }
      }
      bool = zzKi().zzab(paramzzatm.mAppId, paramzzatm.mName);
      localObject = zzKg().zza(zzMG(), paramzzatm.mAppId, false, false, false, false, false);
    } while ((!bool) || (((zzatj.zza)localObject).zzbrs >= zzKn().zzfl(paramzzatm.mAppId)));
    return true;
  }
  
  private zzauw.zza[] zza(String paramString, zzauw.zzg[] paramArrayOfzzg, zzauw.zzb[] paramArrayOfzzb)
  {
    zzac.zzdr(paramString);
    return zzJZ().zza(paramString, paramArrayOfzzb, paramArrayOfzzg);
  }
  
  public static zzaue zzbM(Context paramContext)
  {
    zzac.zzw(paramContext);
    zzac.zzw(paramContext.getApplicationContext());
    if (zzbub == null) {}
    try
    {
      if (zzbub == null) {
        zzbub = new zzaui(paramContext).zzMR();
      }
      return zzbub;
    }
    finally {}
  }
  
  @WorkerThread
  private void zzf(zzatd paramzzatd)
  {
    int k = 1;
    zzmR();
    zzob();
    zzac.zzw(paramzzatd);
    zzac.zzdr(paramzzatd.packageName);
    zzatc localzzatc2 = zzKg().zzfu(paramzzatd.packageName);
    String str = zzKm().zzfH(paramzzatd.packageName);
    int i = 0;
    zzatc localzzatc1;
    int j;
    if (localzzatc2 == null)
    {
      localzzatc1 = new zzatc(this, paramzzatd.packageName);
      localzzatc1.zzfd(zzKm().zzMi());
      localzzatc1.zzff(str);
      i = 1;
      j = i;
      if (!TextUtils.isEmpty(paramzzatd.zzbqK))
      {
        j = i;
        if (!paramzzatd.zzbqK.equals(localzzatc1.getGmpAppId()))
        {
          localzzatc1.zzfe(paramzzatd.zzbqK);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramzzatd.zzbqS))
      {
        i = j;
        if (!paramzzatd.zzbqS.equals(localzzatc1.zzKq()))
        {
          localzzatc1.zzfg(paramzzatd.zzbqS);
          i = 1;
        }
      }
      j = i;
      if (paramzzatd.zzbqM != 0L)
      {
        j = i;
        if (paramzzatd.zzbqM != localzzatc1.zzKv())
        {
          localzzatc1.zzab(paramzzatd.zzbqM);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramzzatd.zzbhN))
      {
        i = j;
        if (!paramzzatd.zzbhN.equals(localzzatc1.zzmZ()))
        {
          localzzatc1.setAppVersion(paramzzatd.zzbhN);
          i = 1;
        }
      }
      if (paramzzatd.zzbqR != localzzatc1.zzKt())
      {
        localzzatc1.zzaa(paramzzatd.zzbqR);
        i = 1;
      }
      j = i;
      if (paramzzatd.zzbqL != null)
      {
        j = i;
        if (!paramzzatd.zzbqL.equals(localzzatc1.zzKu()))
        {
          localzzatc1.zzfh(paramzzatd.zzbqL);
          j = 1;
        }
      }
      i = j;
      if (paramzzatd.zzbqN != localzzatc1.zzKw())
      {
        localzzatc1.zzac(paramzzatd.zzbqN);
        i = 1;
      }
      if (paramzzatd.zzbqP != localzzatc1.zzKx())
      {
        localzzatc1.setMeasurementEnabled(paramzzatd.zzbqP);
        i = 1;
      }
      j = i;
      if (!TextUtils.isEmpty(paramzzatd.zzbqO))
      {
        j = i;
        if (!paramzzatd.zzbqO.equals(localzzatc1.zzKI()))
        {
          localzzatc1.zzfi(paramzzatd.zzbqO);
          j = 1;
        }
      }
      if (paramzzatd.zzbqT == localzzatc1.zzuW()) {
        break label483;
      }
      localzzatc1.zzam(paramzzatd.zzbqT);
    }
    label483:
    for (i = k;; i = j)
    {
      if (i != 0) {
        zzKg().zza(localzzatc1);
      }
      return;
      localzzatc1 = localzzatc2;
      if (str.equals(localzzatc2.zzKp())) {
        break;
      }
      localzzatc2.zzff(str);
      localzzatc2.zzfd(zzKm().zzMi());
      i = 1;
      localzzatc1 = localzzatc2;
      break;
    }
  }
  
  private boolean zzl(String paramString, long paramLong)
  {
    zzKg().beginTransaction();
    for (;;)
    {
      zza localzza;
      zzauw.zze localzze;
      int i;
      int k;
      int n;
      int i1;
      int m;
      Object localObject1;
      zzauw.zzc[] arrayOfzzc;
      long l;
      try
      {
        localzza = new zza(null);
        zzKg().zza(paramString, paramLong, this.zzbuF, localzza);
        if (localzza.isEmpty()) {
          break label1801;
        }
        bool1 = false;
        localzze = localzza.zzbuJ;
        localzze.zzbxj = new zzauw.zzb[localzza.zzth.size()];
        i = 0;
        k = 0;
        if (k < localzza.zzth.size())
        {
          if (zzKi().zzaa(localzza.zzbuJ.zzaS, ((zzauw.zzb)localzza.zzth.get(k)).name))
          {
            zzKl().zzMb().zze("Dropping blacklisted raw event. appId", zzatx.zzfE(localzza.zzbuJ.zzaS), ((zzauw.zzb)localzza.zzth.get(k)).name);
            if (zzKh().zzgg(localzza.zzbuJ.zzaS)) {
              break label1838;
            }
            if (!zzKh().zzgh(localzza.zzbuJ.zzaS)) {
              break label1853;
            }
            break label1838;
            if ((j != 0) || ("_err".equals(((zzauw.zzb)localzza.zzth.get(k)).name))) {
              break label1835;
            }
            zzKh().zza(11, "_ev", ((zzauw.zzb)localzza.zzth.get(k)).name, 0);
            break label1844;
          }
          boolean bool3 = zzKi().zzab(localzza.zzbuJ.zzaS, ((zzauw.zzb)localzza.zzth.get(k)).name);
          if ((!bool3) && (!zzKh().zzgi(((zzauw.zzb)localzza.zzth.get(k)).name))) {
            break label1832;
          }
          n = 0;
          j = 0;
          if (((zzauw.zzb)localzza.zzth.get(k)).zzbxb == null) {
            ((zzauw.zzb)localzza.zzth.get(k)).zzbxb = new zzauw.zzc[0];
          }
          paramString = ((zzauw.zzb)localzza.zzth.get(k)).zzbxb;
          i1 = paramString.length;
          m = 0;
          if (m < i1)
          {
            localObject1 = paramString[m];
            if ("_c".equals(((zzauw.zzc)localObject1).name))
            {
              ((zzauw.zzc)localObject1).zzbxf = Long.valueOf(1L);
              n = 1;
              break label1859;
            }
            if (!"_r".equals(((zzauw.zzc)localObject1).name)) {
              break label1829;
            }
            ((zzauw.zzc)localObject1).zzbxf = Long.valueOf(1L);
            j = 1;
            break label1859;
          }
          if ((n == 0) && (bool3))
          {
            zzKl().zzMf().zzj("Marking event as conversion", ((zzauw.zzb)localzza.zzth.get(k)).name);
            paramString = (zzauw.zzc[])Arrays.copyOf(((zzauw.zzb)localzza.zzth.get(k)).zzbxb, ((zzauw.zzb)localzza.zzth.get(k)).zzbxb.length + 1);
            localObject1 = new zzauw.zzc();
            ((zzauw.zzc)localObject1).name = "_c";
            ((zzauw.zzc)localObject1).zzbxf = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzauw.zzb)localzza.zzth.get(k)).zzbxb = paramString;
          }
          if (j == 0)
          {
            zzKl().zzMf().zzj("Marking event as real-time", ((zzauw.zzb)localzza.zzth.get(k)).name);
            paramString = (zzauw.zzc[])Arrays.copyOf(((zzauw.zzb)localzza.zzth.get(k)).zzbxb, ((zzauw.zzb)localzza.zzth.get(k)).zzbxb.length + 1);
            localObject1 = new zzauw.zzc();
            ((zzauw.zzc)localObject1).name = "_r";
            ((zzauw.zzc)localObject1).zzbxf = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzauw.zzb)localzza.zzth.get(k)).zzbxb = paramString;
          }
          bool2 = true;
          if (zzKg().zza(zzMG(), localzza.zzbuJ.zzaS, false, false, false, false, true).zzbrs > zzKn().zzfl(localzza.zzbuJ.zzaS))
          {
            paramString = (zzauw.zzb)localzza.zzth.get(k);
            j = 0;
            if (j >= paramString.zzbxb.length) {
              break label1868;
            }
            if (!"_r".equals(paramString.zzbxb[j].name)) {
              break label1884;
            }
            localObject1 = new zzauw.zzc[paramString.zzbxb.length - 1];
            if (j > 0) {
              System.arraycopy(paramString.zzbxb, 0, localObject1, 0, j);
            }
            if (j < localObject1.length) {
              System.arraycopy(paramString.zzbxb, j + 1, localObject1, j, localObject1.length - j);
            }
            paramString.zzbxb = ((zzauw.zzc[])localObject1);
            break label1868;
          }
          if ((!zzaut.zzfT(((zzauw.zzb)localzza.zzth.get(k)).name)) || (!bool3) || (zzKg().zza(zzMG(), localzza.zzbuJ.zzaS, false, false, true, false, false).zzbrq <= zzKn().zzfk(localzza.zzbuJ.zzaS))) {
            break label1939;
          }
          zzKl().zzMb().zzj("Too many conversions. Not logging as conversion. appId", zzatx.zzfE(localzza.zzbuJ.zzaS));
          localObject2 = (zzauw.zzb)localzza.zzth.get(k);
          j = 0;
          paramString = null;
          arrayOfzzc = ((zzauw.zzb)localObject2).zzbxb;
          n = arrayOfzzc.length;
          m = 0;
          if (m < n)
          {
            localObject1 = arrayOfzzc[m];
            if ("_c".equals(((zzauw.zzc)localObject1).name))
            {
              paramString = (String)localObject1;
              break label1875;
            }
            if (!"_err".equals(((zzauw.zzc)localObject1).name)) {
              break label1826;
            }
            j = 1;
            break label1875;
          }
          if ((j != 0) && (paramString != null))
          {
            localObject1 = new zzauw.zzc[((zzauw.zzb)localObject2).zzbxb.length - 1];
            j = 0;
            arrayOfzzc = ((zzauw.zzb)localObject2).zzbxb;
            i1 = arrayOfzzc.length;
            m = 0;
            break label1893;
            ((zzauw.zzb)localObject2).zzbxb = ((zzauw.zzc[])localObject1);
            bool1 = bool2;
            localzze.zzbxj[i] = ((zzauw.zzb)localzza.zzth.get(k));
            i += 1;
            break label1844;
          }
          if (paramString != null)
          {
            paramString.name = "_err";
            paramString.zzbxf = Long.valueOf(10L);
            bool1 = bool2;
            continue;
          }
          zzKl().zzLZ().zzj("Did not find conversion parameter. appId", zzatx.zzfE(localzza.zzbuJ.zzaS));
          break label1939;
        }
        if (i < localzza.zzth.size()) {
          localzze.zzbxj = ((zzauw.zzb[])Arrays.copyOf(localzze.zzbxj, i));
        }
        localzze.zzbxC = zza(localzza.zzbuJ.zzaS, localzza.zzbuJ.zzbxk, localzze.zzbxj);
        localzze.zzbxm = Long.valueOf(Long.MAX_VALUE);
        localzze.zzbxn = Long.valueOf(Long.MIN_VALUE);
        i = 0;
        if (i < localzze.zzbxj.length)
        {
          paramString = localzze.zzbxj[i];
          if (paramString.zzbxc.longValue() < localzze.zzbxm.longValue()) {
            localzze.zzbxm = paramString.zzbxc;
          }
          if (paramString.zzbxc.longValue() <= localzze.zzbxn.longValue()) {
            break label1946;
          }
          localzze.zzbxn = paramString.zzbxc;
          break label1946;
        }
        localObject1 = localzza.zzbuJ.zzaS;
        Object localObject2 = zzKg().zzfu((String)localObject1);
        if (localObject2 == null)
        {
          zzKl().zzLZ().zzj("Bundling raw events w/o app info. appId", zzatx.zzfE(localzza.zzbuJ.zzaS));
          if (localzze.zzbxj.length > 0)
          {
            zzKn().zzLh();
            paramString = zzKi().zzfL(localzza.zzbuJ.zzaS);
            if ((paramString != null) && (paramString.zzbwQ != null)) {
              break label1789;
            }
            if (!TextUtils.isEmpty(localzza.zzbuJ.zzbqK)) {
              break label1762;
            }
            localzze.zzbxH = Long.valueOf(-1L);
            zzKg().zza(localzze, bool1);
          }
          zzKg().zzJ(localzza.zzbuK);
          zzKg().zzfB((String)localObject1);
          zzKg().setTransactionSuccessful();
          i = localzze.zzbxj.length;
          if (i <= 0) {
            break label1955;
          }
          bool1 = true;
          return bool1;
        }
        if (localzze.zzbxj.length <= 0) {
          continue;
        }
        paramLong = ((zzatc)localObject2).zzKs();
        if (paramLong != 0L)
        {
          paramString = Long.valueOf(paramLong);
          localzze.zzbxp = paramString;
          l = ((zzatc)localObject2).zzKr();
          if (l != 0L) {
            break label1817;
          }
          if (paramLong == 0L) {
            break label1757;
          }
          paramString = Long.valueOf(paramLong);
          localzze.zzbxo = paramString;
          ((zzatc)localObject2).zzKB();
          localzze.zzbxA = Integer.valueOf((int)((zzatc)localObject2).zzKy());
          ((zzatc)localObject2).zzY(localzze.zzbxm.longValue());
          ((zzatc)localObject2).zzZ(localzze.zzbxn.longValue());
          localzze.zzbqO = ((zzatc)localObject2).zzKJ();
          zzKg().zza((zzatc)localObject2);
          continue;
        }
        paramString = null;
      }
      finally
      {
        zzKg().endTransaction();
      }
      continue;
      label1757:
      paramString = null;
      continue;
      label1762:
      zzKl().zzMb().zzj("Did not find measurement config or missing version info. appId", zzatx.zzfE(localzza.zzbuJ.zzaS));
      continue;
      label1789:
      localzze.zzbxH = paramString.zzbwQ;
      continue;
      label1801:
      zzKg().setTransactionSuccessful();
      zzKg().endTransaction();
      return false;
      label1817:
      paramLong = l;
      continue;
      break label1930;
      label1826:
      break label1875;
      label1829:
      break label1859;
      label1832:
      continue;
      label1835:
      break label1844;
      label1838:
      int j = 1;
      continue;
      label1844:
      k += 1;
      continue;
      label1853:
      j = 0;
      continue;
      label1859:
      m += 1;
      continue;
      label1868:
      boolean bool2 = bool1;
      continue;
      label1875:
      m += 1;
      continue;
      label1884:
      j += 1;
      continue;
      for (;;)
      {
        label1893:
        if (m >= i1) {
          break label1937;
        }
        zzauw.zzc localzzc = arrayOfzzc[m];
        if (localzzc == paramString) {
          break;
        }
        n = j + 1;
        localObject1[j] = localzzc;
        j = n;
        label1930:
        m += 1;
      }
      label1937:
      continue;
      label1939:
      boolean bool1 = bool2;
      continue;
      label1946:
      i += 1;
      continue;
      label1955:
      bool1 = false;
    }
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  @WorkerThread
  public boolean isEnabled()
  {
    boolean bool = false;
    zzmR();
    zzob();
    if (zzKn().zzLi()) {
      return false;
    }
    Boolean localBoolean = zzKn().zzLj();
    if (localBoolean != null) {
      bool = localBoolean.booleanValue();
    }
    for (;;)
    {
      return zzKm().zzaK(bool);
      if (!zzKn().zzwR()) {
        bool = true;
      }
    }
  }
  
  @WorkerThread
  protected void start()
  {
    zzmR();
    zzKg().zzLG();
    if (zzKm().zzbtb.get() == 0L) {
      zzKm().zzbtb.set(zznR().currentTimeMillis());
    }
    if (!zzMu())
    {
      if (isEnabled())
      {
        if (!zzKh().zzbW("android.permission.INTERNET")) {
          zzKl().zzLZ().log("App is missing INTERNET permission");
        }
        if (!zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE")) {
          zzKl().zzLZ().log("App is missing ACCESS_NETWORK_STATE permission");
        }
        zzKn().zzLh();
        if (!zzadg.zzbi(getContext()).zzzx())
        {
          if (!zzaub.zzi(getContext(), false)) {
            zzKl().zzLZ().log("AppMeasurementReceiver not registered/enabled");
          }
          if (!zzaum.zzj(getContext(), false)) {
            zzKl().zzLZ().log("AppMeasurementService not registered/enabled");
          }
        }
        zzKl().zzLZ().log("Uploading is not possible. App measurement disabled");
      }
      zzMK();
      return;
    }
    zzKn().zzLh();
    String str;
    if (!TextUtils.isEmpty(zzKb().getGmpAppId()))
    {
      str = zzKm().zzMl();
      if (str != null) {
        break label276;
      }
      zzKm().zzfI(zzKb().getGmpAppId());
    }
    for (;;)
    {
      zzKn().zzLh();
      if (TextUtils.isEmpty(zzKb().getGmpAppId())) {
        break;
      }
      zzKa().zzMT();
      break;
      label276:
      if (!str.equals(zzKb().getGmpAppId()))
      {
        zzKl().zzMd().log("Rechecking which service to use due to a GMP App Id change");
        zzKm().zzMo();
        this.zzbup.disconnect();
        this.zzbup.zzoD();
        zzKm().zzfI(zzKb().getGmpAppId());
      }
    }
  }
  
  void zzJV()
  {
    zzKn().zzLh();
    throw new IllegalStateException("Unexpected call on client side");
  }
  
  void zzJW()
  {
    zzKn().zzLh();
  }
  
  public zzatb zzJY()
  {
    zza(this.zzbuw);
    return this.zzbuw;
  }
  
  public zzatf zzJZ()
  {
    zza(this.zzbuv);
    return this.zzbuv;
  }
  
  protected void zzK(List<Long> paramList)
  {
    if (!paramList.isEmpty()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzaw(bool);
      if (this.zzbuC == null) {
        break;
      }
      zzKl().zzLZ().log("Set uploading progress before finishing the previous upload");
      return;
    }
    this.zzbuC = new ArrayList(paramList);
  }
  
  public zzauj zzKa()
  {
    zza(this.zzbur);
    return this.zzbur;
  }
  
  public zzatu zzKb()
  {
    zza(this.zzbus);
    return this.zzbus;
  }
  
  public zzatl zzKc()
  {
    zza(this.zzbuq);
    return this.zzbuq;
  }
  
  public zzaul zzKd()
  {
    zza(this.zzbup);
    return this.zzbup;
  }
  
  public zzauk zzKe()
  {
    zza(this.zzbuo);
    return this.zzbuo;
  }
  
  public zzatv zzKf()
  {
    zza(this.zzbum);
    return this.zzbum;
  }
  
  public zzatj zzKg()
  {
    zza(this.zzbul);
    return this.zzbul;
  }
  
  public zzaut zzKh()
  {
    zza(this.zzbuk);
    return this.zzbuk;
  }
  
  public zzauc zzKi()
  {
    zza(this.zzbuh);
    return this.zzbuh;
  }
  
  public zzaun zzKj()
  {
    zza(this.zzbug);
    return this.zzbug;
  }
  
  public zzaud zzKk()
  {
    zza(this.zzbuf);
    return this.zzbuf;
  }
  
  public zzatx zzKl()
  {
    zza(this.zzbue);
    return this.zzbue;
  }
  
  public zzaua zzKm()
  {
    zza(this.zzbud);
    return this.zzbud;
  }
  
  public zzati zzKn()
  {
    return this.zzbuc;
  }
  
  public zzatz zzMA()
  {
    if (this.zzbut == null) {
      throw new IllegalStateException("Network broadcast receiver not created");
    }
    return this.zzbut;
  }
  
  public zzaup zzMB()
  {
    zza(this.zzbuu);
    return this.zzbuu;
  }
  
  FileChannel zzMC()
  {
    return this.zzbuB;
  }
  
  @WorkerThread
  void zzMD()
  {
    zzmR();
    zzob();
    if ((zzMO()) && (zzME())) {
      zzy(zza(zzMC()), zzKb().zzLX());
    }
  }
  
  @WorkerThread
  boolean zzME()
  {
    zzmR();
    Object localObject = this.zzbul.zzow();
    localObject = new File(getContext().getFilesDir(), (String)localObject);
    try
    {
      this.zzbuB = new RandomAccessFile((File)localObject, "rw").getChannel();
      this.zzbuA = this.zzbuB.tryLock();
      if (this.zzbuA != null)
      {
        zzKl().zzMf().log("Storage concurrent access okay");
        return true;
      }
      zzKl().zzLZ().log("Storage concurrent data access panic");
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("Failed to acquire storage lock", localFileNotFoundException);
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("Failed to access storage lock file", localIOException);
      }
    }
    return false;
  }
  
  long zzMF()
  {
    return this.zzbuH;
  }
  
  long zzMG()
  {
    return (zznR().currentTimeMillis() + zzKm().zzMj()) / 1000L / 60L / 60L / 24L;
  }
  
  @WorkerThread
  protected boolean zzMH()
  {
    zzmR();
    return this.zzbuC != null;
  }
  
  @WorkerThread
  public void zzMI()
  {
    int j = 0;
    zzmR();
    zzob();
    zzKn().zzLh();
    Object localObject1 = zzKm().zzMn();
    if (localObject1 == null) {
      zzKl().zzMb().log("Upload data called on the client side before use of service was decided");
    }
    long l1;
    String str1;
    int i;
    Object localObject3;
    do
    {
      return;
      if (((Boolean)localObject1).booleanValue())
      {
        zzKl().zzLZ().log("Upload called in the client side when service should be used");
        return;
      }
      if (this.zzbuG > 0L)
      {
        zzMK();
        return;
      }
      if (zzMH())
      {
        zzKl().zzMb().log("Uploading requested multiple times");
        return;
      }
      if (!zzMz().zzqa())
      {
        zzKl().zzMb().log("Network not connected, ignoring upload request");
        zzMK();
        return;
      }
      l1 = zznR().currentTimeMillis();
      zzaq(l1 - zzKn().zzLs());
      long l2 = zzKm().zzbtb.get();
      if (l2 != 0L) {
        zzKl().zzMe().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(l1 - l2)));
      }
      str1 = zzKg().zzLE();
      if (TextUtils.isEmpty(str1)) {
        break;
      }
      if (this.zzbuF == -1L) {
        this.zzbuF = zzKg().zzLM();
      }
      i = zzKn().zzfq(str1);
      int k = zzKn().zzfr(str1);
      localObject3 = zzKg().zzn(str1, i, k);
    } while (((List)localObject3).isEmpty());
    localObject1 = ((List)localObject3).iterator();
    Object localObject4;
    do
    {
      if (!((Iterator)localObject1).hasNext()) {
        break;
      }
      localObject4 = (zzauw.zze)((Pair)((Iterator)localObject1).next()).first;
    } while (TextUtils.isEmpty(((zzauw.zze)localObject4).zzbxw));
    Object localObject2;
    for (localObject1 = ((zzauw.zze)localObject4).zzbxw;; localObject2 = null)
    {
      if (localObject1 != null)
      {
        i = 0;
        if (i < ((List)localObject3).size())
        {
          localObject4 = (zzauw.zze)((Pair)((List)localObject3).get(i)).first;
          if (TextUtils.isEmpty(((zzauw.zze)localObject4).zzbxw)) {}
          while (((zzauw.zze)localObject4).zzbxw.equals(localObject1))
          {
            i += 1;
            break;
          }
        }
      }
      for (localObject1 = ((List)localObject3).subList(0, i);; localObject2 = localObject3)
      {
        localObject4 = new zzauw.zzd();
        ((zzauw.zzd)localObject4).zzbxg = new zzauw.zze[((List)localObject1).size()];
        localObject3 = new ArrayList(((List)localObject1).size());
        i = j;
        while (i < ((zzauw.zzd)localObject4).zzbxg.length)
        {
          ((zzauw.zzd)localObject4).zzbxg[i] = ((zzauw.zze)((Pair)((List)localObject1).get(i)).first);
          ((List)localObject3).add((Long)((Pair)((List)localObject1).get(i)).second);
          localObject4.zzbxg[i].zzbxv = Long.valueOf(zzKn().zzKv());
          localObject4.zzbxg[i].zzbxl = Long.valueOf(l1);
          localObject4.zzbxg[i].zzbxB = Boolean.valueOf(zzKn().zzLh());
          i += 1;
        }
        if (zzKl().zzak(2)) {}
        for (localObject1 = zzaut.zzb((zzauw.zzd)localObject4);; localObject2 = null)
        {
          byte[] arrayOfByte = zzKh().zza((zzauw.zzd)localObject4);
          String str2 = zzKn().zzLr();
          try
          {
            URL localURL = new URL(str2);
            zzK((List)localObject3);
            zzKm().zzbtc.set(l1);
            localObject3 = "?";
            if (((zzauw.zzd)localObject4).zzbxg.length > 0) {
              localObject3 = localObject4.zzbxg[0].zzaS;
            }
            zzKl().zzMf().zzd("Uploading data. app, uncompressed size, data", localObject3, Integer.valueOf(arrayOfByte.length), localObject1);
            zzMz().zza(str1, localURL, arrayOfByte, null, new zzaty.zza()
            {
              public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
              {
                zzaue.this.zza(paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte);
              }
            });
            return;
          }
          catch (MalformedURLException localMalformedURLException)
          {
            zzKl().zzLZ().zze("Failed to parse upload URL. Not uploading. appId", zzatx.zzfE(str1), str2);
            return;
          }
          this.zzbuF = -1L;
          localObject2 = zzKg().zzao(l1 - zzKn().zzLs());
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            break;
          }
          localObject2 = zzKg().zzfu((String)localObject2);
          if (localObject2 == null) {
            break;
          }
          zzb((zzatc)localObject2);
          return;
        }
      }
    }
  }
  
  void zzMM()
  {
    this.zzbuE += 1;
  }
  
  @WorkerThread
  void zzMN()
  {
    zzmR();
    zzob();
    if (!this.zzbux)
    {
      zzKl().zzMd().log("This instance being marked as an uploader");
      zzMD();
    }
    this.zzbux = true;
  }
  
  @WorkerThread
  boolean zzMO()
  {
    zzmR();
    zzob();
    return this.zzbux;
  }
  
  @WorkerThread
  protected boolean zzMu()
  {
    boolean bool2 = false;
    zzob();
    zzmR();
    if ((this.zzbuy == null) || (this.zzbuz == 0L) || ((this.zzbuy != null) && (!this.zzbuy.booleanValue()) && (Math.abs(zznR().elapsedRealtime() - this.zzbuz) > 1000L)))
    {
      this.zzbuz = zznR().elapsedRealtime();
      zzKn().zzLh();
      boolean bool1 = bool2;
      if (zzKh().zzbW("android.permission.INTERNET"))
      {
        bool1 = bool2;
        if (zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE")) {
          if (!zzadg.zzbi(getContext()).zzzx())
          {
            bool1 = bool2;
            if (zzaub.zzi(getContext(), false))
            {
              bool1 = bool2;
              if (!zzaum.zzj(getContext(), false)) {}
            }
          }
          else
          {
            bool1 = true;
          }
        }
      }
      this.zzbuy = Boolean.valueOf(bool1);
      if (this.zzbuy.booleanValue()) {
        this.zzbuy = Boolean.valueOf(zzKh().zzga(zzKb().getGmpAppId()));
      }
    }
    return this.zzbuy.booleanValue();
  }
  
  public zzatx zzMv()
  {
    if ((this.zzbue != null) && (this.zzbue.isInitialized())) {
      return this.zzbue;
    }
    return null;
  }
  
  zzaud zzMw()
  {
    return this.zzbuf;
  }
  
  public AppMeasurement zzMx()
  {
    return this.zzbui;
  }
  
  public FirebaseAnalytics zzMy()
  {
    return this.zzbuj;
  }
  
  public zzaty zzMz()
  {
    zza(this.zzbun);
    return this.zzbun;
  }
  
  public void zzV(boolean paramBoolean)
  {
    zzMK();
  }
  
  @WorkerThread
  int zza(FileChannel paramFileChannel)
  {
    zzmR();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen())) {
      zzKl().zzLZ().log("Bad chanel to read from");
    }
    ByteBuffer localByteBuffer;
    for (;;)
    {
      return 0;
      localByteBuffer = ByteBuffer.allocate(4);
      try
      {
        paramFileChannel.position(0L);
        i = paramFileChannel.read(localByteBuffer);
        if (i != 4)
        {
          if (i == -1) {
            continue;
          }
          zzKl().zzMb().zzj("Unexpected data length. Bytes read", Integer.valueOf(i));
          return 0;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzKl().zzLZ().zzj("Failed to read from channel", paramFileChannel);
        return 0;
      }
    }
    localByteBuffer.flip();
    int i = localByteBuffer.getInt();
    return i;
  }
  
  /* Error */
  @WorkerThread
  protected void zza(int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_0
    //   4: invokevirtual 423	com/google/android/gms/internal/zzaue:zzmR	()V
    //   7: aload_0
    //   8: invokevirtual 426	com/google/android/gms/internal/zzaue:zzob	()V
    //   11: aload_3
    //   12: astore 5
    //   14: aload_3
    //   15: ifnonnull +8 -> 23
    //   18: iconst_0
    //   19: newarray <illegal type>
    //   21: astore 5
    //   23: aload_0
    //   24: getfield 1114	com/google/android/gms/internal/zzaue:zzbuC	Ljava/util/List;
    //   27: astore_3
    //   28: aload_0
    //   29: aconst_null
    //   30: putfield 1114	com/google/android/gms/internal/zzaue:zzbuC	Ljava/util/List;
    //   33: iload_1
    //   34: sipush 200
    //   37: if_icmpeq +10 -> 47
    //   40: iload_1
    //   41: sipush 204
    //   44: if_icmpne +225 -> 269
    //   47: aload_2
    //   48: ifnonnull +221 -> 269
    //   51: aload_0
    //   52: invokevirtual 507	com/google/android/gms/internal/zzaue:zzKm	()Lcom/google/android/gms/internal/zzaua;
    //   55: getfield 532	com/google/android/gms/internal/zzaua:zzbtb	Lcom/google/android/gms/internal/zzaua$zzb;
    //   58: aload_0
    //   59: invokevirtual 455	com/google/android/gms/internal/zzaue:zznR	()Lcom/google/android/gms/common/util/zze;
    //   62: invokeinterface 115 1 0
    //   67: invokevirtual 536	com/google/android/gms/internal/zzaua$zzb:set	(J)V
    //   70: aload_0
    //   71: invokevirtual 507	com/google/android/gms/internal/zzaue:zzKm	()Lcom/google/android/gms/internal/zzaua;
    //   74: getfield 566	com/google/android/gms/internal/zzaua:zzbtc	Lcom/google/android/gms/internal/zzaua$zzb;
    //   77: lconst_0
    //   78: invokevirtual 536	com/google/android/gms/internal/zzaua$zzb:set	(J)V
    //   81: aload_0
    //   82: invokespecial 1072	com/google/android/gms/internal/zzaue:zzMK	()V
    //   85: aload_0
    //   86: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   89: invokevirtual 467	com/google/android/gms/internal/zzatx:zzMf	()Lcom/google/android/gms/internal/zzatx$zza;
    //   92: ldc_w 1381
    //   95: iload_1
    //   96: invokestatic 365	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   99: aload 5
    //   101: arraylength
    //   102: invokestatic 365	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   105: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   108: aload_0
    //   109: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   112: invokevirtual 802	com/google/android/gms/internal/zzatj:beginTransaction	()V
    //   115: aload_3
    //   116: invokeinterface 1245 1 0
    //   121: astore_2
    //   122: aload_2
    //   123: invokeinterface 619 1 0
    //   128: ifeq +86 -> 214
    //   131: aload_2
    //   132: invokeinterface 625 1 0
    //   137: checkcast 163	java/lang/Long
    //   140: astore_3
    //   141: aload_0
    //   142: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   145: aload_3
    //   146: invokevirtual 936	java/lang/Long:longValue	()J
    //   149: invokevirtual 1384	com/google/android/gms/internal/zzatj:zzan	(J)V
    //   152: goto -30 -> 122
    //   155: astore_2
    //   156: aload_0
    //   157: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   160: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   163: aload_2
    //   164: athrow
    //   165: astore_2
    //   166: aload_0
    //   167: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   170: invokevirtual 358	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   173: ldc_w 1386
    //   176: aload_2
    //   177: invokevirtual 173	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   180: aload_0
    //   181: aload_0
    //   182: invokevirtual 455	com/google/android/gms/internal/zzaue:zznR	()Lcom/google/android/gms/common/util/zze;
    //   185: invokeinterface 458 1 0
    //   190: putfield 449	com/google/android/gms/internal/zzaue:zzbuG	J
    //   193: aload_0
    //   194: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   197: invokevirtual 467	com/google/android/gms/internal/zzatx:zzMf	()Lcom/google/android/gms/internal/zzatx$zza;
    //   200: ldc_w 1388
    //   203: aload_0
    //   204: getfield 449	com/google/android/gms/internal/zzaue:zzbuG	J
    //   207: invokestatic 167	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 173	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   213: return
    //   214: aload_0
    //   215: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   218: invokevirtual 967	com/google/android/gms/internal/zzatj:setTransactionSuccessful	()V
    //   221: aload_0
    //   222: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   225: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   228: aload_0
    //   229: invokevirtual 497	com/google/android/gms/internal/zzaue:zzMz	()Lcom/google/android/gms/internal/zzaty;
    //   232: invokevirtual 500	com/google/android/gms/internal/zzaty:zzqa	()Z
    //   235: ifeq +20 -> 255
    //   238: aload_0
    //   239: invokespecial 490	com/google/android/gms/internal/zzaue:zzMJ	()Z
    //   242: ifeq +13 -> 255
    //   245: aload_0
    //   246: invokevirtual 1390	com/google/android/gms/internal/zzaue:zzMI	()V
    //   249: aload_0
    //   250: lconst_0
    //   251: putfield 449	com/google/android/gms/internal/zzaue:zzbuG	J
    //   254: return
    //   255: aload_0
    //   256: ldc2_w 100
    //   259: putfield 103	com/google/android/gms/internal/zzaue:zzbuF	J
    //   262: aload_0
    //   263: invokespecial 1072	com/google/android/gms/internal/zzaue:zzMK	()V
    //   266: goto -17 -> 249
    //   269: aload_0
    //   270: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   273: invokevirtual 467	com/google/android/gms/internal/zzatx:zzMf	()Lcom/google/android/gms/internal/zzatx$zza;
    //   276: ldc_w 1392
    //   279: iload_1
    //   280: invokestatic 365	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   283: aload_2
    //   284: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   287: aload_0
    //   288: invokevirtual 507	com/google/android/gms/internal/zzaue:zzKm	()Lcom/google/android/gms/internal/zzaua;
    //   291: getfield 566	com/google/android/gms/internal/zzaua:zzbtc	Lcom/google/android/gms/internal/zzaua$zzb;
    //   294: aload_0
    //   295: invokevirtual 455	com/google/android/gms/internal/zzaue:zznR	()Lcom/google/android/gms/common/util/zze;
    //   298: invokeinterface 115 1 0
    //   303: invokevirtual 536	com/google/android/gms/internal/zzaua$zzb:set	(J)V
    //   306: iload_1
    //   307: sipush 503
    //   310: if_icmpeq +10 -> 320
    //   313: iload_1
    //   314: sipush 429
    //   317: if_icmpne +6 -> 323
    //   320: iconst_1
    //   321: istore 4
    //   323: iload 4
    //   325: ifeq +22 -> 347
    //   328: aload_0
    //   329: invokevirtual 507	com/google/android/gms/internal/zzaue:zzKm	()Lcom/google/android/gms/internal/zzaua;
    //   332: getfield 511	com/google/android/gms/internal/zzaua:zzbtd	Lcom/google/android/gms/internal/zzaua$zzb;
    //   335: aload_0
    //   336: invokevirtual 455	com/google/android/gms/internal/zzaue:zznR	()Lcom/google/android/gms/common/util/zze;
    //   339: invokeinterface 115 1 0
    //   344: invokevirtual 536	com/google/android/gms/internal/zzaua$zzb:set	(J)V
    //   347: aload_0
    //   348: invokespecial 1072	com/google/android/gms/internal/zzaue:zzMK	()V
    //   351: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	this	zzaue
    //   0	352	1	paramInt	int
    //   0	352	2	paramThrowable	Throwable
    //   0	352	3	paramArrayOfByte	byte[]
    //   1	323	4	i	int
    //   12	88	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   115	122	155	finally
    //   122	152	155	finally
    //   214	221	155	finally
    //   51	115	165	android/database/sqlite/SQLiteException
    //   156	165	165	android/database/sqlite/SQLiteException
    //   221	249	165	android/database/sqlite/SQLiteException
    //   249	254	165	android/database/sqlite/SQLiteException
    //   255	266	165	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zza(zzatd paramzzatd, long paramLong)
  {
    Object localObject2 = zzKg().zzfu(paramzzatd.packageName);
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((zzatc)localObject2).getGmpAppId() != null)
      {
        localObject1 = localObject2;
        if (!((zzatc)localObject2).getGmpAppId().equals(paramzzatd.zzbqK))
        {
          zzKl().zzMb().zzj("New GMP App Id passed in. Removing cached database data. appId", zzatx.zzfE(((zzatc)localObject2).zzke()));
          zzKg().zzfz(((zzatc)localObject2).zzke());
          localObject1 = null;
        }
      }
    }
    if ((localObject1 != null) && (((zzatc)localObject1).zzmZ() != null) && (!((zzatc)localObject1).zzmZ().equals(paramzzatd.zzbhN)))
    {
      localObject2 = new Bundle();
      ((Bundle)localObject2).putString("_pv", ((zzatc)localObject1).zzmZ());
      zzb(new zzatq("_au", new zzato((Bundle)localObject2), "auto", paramLong), paramzzatd);
    }
  }
  
  void zza(zzatm paramzzatm, zzatd paramzzatd)
  {
    zzmR();
    zzob();
    zzac.zzw(paramzzatm);
    zzac.zzw(paramzzatd);
    zzac.zzdr(paramzzatm.mAppId);
    zzac.zzaw(paramzzatm.mAppId.equals(paramzzatd.packageName));
    zzauw.zze localzze = new zzauw.zze();
    localzze.zzbxi = Integer.valueOf(1);
    localzze.zzbxq = "android";
    localzze.zzaS = paramzzatd.packageName;
    localzze.zzbqL = paramzzatd.zzbqL;
    localzze.zzbhN = paramzzatd.zzbhN;
    localzze.zzbxD = Integer.valueOf((int)paramzzatd.zzbqR);
    localzze.zzbxu = Long.valueOf(paramzzatd.zzbqM);
    localzze.zzbqK = paramzzatd.zzbqK;
    Object localObject1;
    if (paramzzatd.zzbqN == 0L)
    {
      localObject1 = null;
      localzze.zzbxz = ((Long)localObject1);
      localObject1 = zzKm().zzfG(paramzzatd.packageName);
      if (TextUtils.isEmpty((CharSequence)((Pair)localObject1).first)) {
        break label615;
      }
      localzze.zzbxw = ((String)((Pair)localObject1).first);
      localzze.zzbxx = ((Boolean)((Pair)localObject1).second);
    }
    label615:
    while (zzKc().zzbL(this.mContext))
    {
      localzze.zzbxr = zzKc().zzkN();
      localzze.zzbb = zzKc().zzLS();
      localzze.zzbxt = Integer.valueOf((int)zzKc().zzLT());
      localzze.zzbxs = zzKc().zzLU();
      localzze.zzbxv = null;
      localzze.zzbxl = null;
      localzze.zzbxm = null;
      localzze.zzbxn = null;
      localzze.zzbxI = Long.valueOf(paramzzatd.zzbqT);
      localObject2 = zzKg().zzfu(paramzzatd.packageName);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new zzatc(this, paramzzatd.packageName);
        ((zzatc)localObject1).zzfd(zzKm().zzMi());
        ((zzatc)localObject1).zzfg(paramzzatd.zzbqS);
        ((zzatc)localObject1).zzfe(paramzzatd.zzbqK);
        ((zzatc)localObject1).zzff(zzKm().zzfH(paramzzatd.packageName));
        ((zzatc)localObject1).zzad(0L);
        ((zzatc)localObject1).zzY(0L);
        ((zzatc)localObject1).zzZ(0L);
        ((zzatc)localObject1).setAppVersion(paramzzatd.zzbhN);
        ((zzatc)localObject1).zzaa(paramzzatd.zzbqR);
        ((zzatc)localObject1).zzfh(paramzzatd.zzbqL);
        ((zzatc)localObject1).zzab(paramzzatd.zzbqM);
        ((zzatc)localObject1).zzac(paramzzatd.zzbqN);
        ((zzatc)localObject1).setMeasurementEnabled(paramzzatd.zzbqP);
        ((zzatc)localObject1).zzam(paramzzatd.zzbqT);
        zzKg().zza((zzatc)localObject1);
      }
      localzze.zzbxy = ((zzatc)localObject1).getAppInstanceId();
      localzze.zzbqS = ((zzatc)localObject1).zzKq();
      paramzzatd = zzKg().zzft(paramzzatd.packageName);
      localzze.zzbxk = new zzauw.zzg[paramzzatd.size()];
      int i = 0;
      while (i < paramzzatd.size())
      {
        localObject1 = new zzauw.zzg();
        localzze.zzbxk[i] = localObject1;
        ((zzauw.zzg)localObject1).name = ((zzaus)paramzzatd.get(i)).mName;
        ((zzauw.zzg)localObject1).zzbxM = Long.valueOf(((zzaus)paramzzatd.get(i)).zzbwj);
        zzKh().zza((zzauw.zzg)localObject1, ((zzaus)paramzzatd.get(i)).mValue);
        i += 1;
      }
      localObject1 = Long.valueOf(paramzzatd.zzbqN);
      break;
    }
    Object localObject2 = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
    if (localObject2 == null)
    {
      zzKl().zzMb().zzj("null secure ID. appId", zzatx.zzfE(localzze.zzaS));
      localObject1 = "null";
    }
    for (;;)
    {
      localzze.zzbxG = ((String)localObject1);
      break;
      localObject1 = localObject2;
      if (((String)localObject2).isEmpty())
      {
        zzKl().zzMb().zzj("empty secure ID. appId", zzatx.zzfE(localzze.zzaS));
        localObject1 = localObject2;
      }
    }
    try
    {
      long l = zzKg().zza(localzze);
      if (zzKg().zza(paramzzatm, l, zza(paramzzatm))) {
        this.zzbuG = 0L;
      }
      return;
    }
    catch (IOException paramzzatm)
    {
      zzKl().zzLZ().zze("Data loss. Failed to insert raw event metadata. appId", zzatx.zzfE(localzze.zzaS), paramzzatm);
    }
  }
  
  @WorkerThread
  boolean zza(int paramInt, FileChannel paramFileChannel)
  {
    boolean bool = true;
    zzmR();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen()))
    {
      zzKl().zzLZ().log("Bad chanel to read from");
      bool = false;
    }
    for (;;)
    {
      return bool;
      ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
      localByteBuffer.putInt(paramInt);
      localByteBuffer.flip();
      try
      {
        paramFileChannel.truncate(0L);
        paramFileChannel.write(localByteBuffer);
        paramFileChannel.force(true);
        if (paramFileChannel.size() != 4L)
        {
          zzKl().zzLZ().zzj("Error writing to channel. Bytes written", Long.valueOf(paramFileChannel.size()));
          return true;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzKl().zzLZ().zzj("Failed to write to channel", paramFileChannel);
      }
    }
    return false;
  }
  
  @WorkerThread
  public byte[] zza(@NonNull zzatq paramzzatq, @Size(min=1L) String paramString)
  {
    zzob();
    zzmR();
    zzJV();
    zzac.zzw(paramzzatq);
    zzac.zzdr(paramString);
    zzauw.zzd localzzd = new zzauw.zzd();
    zzKg().beginTransaction();
    Object localObject1;
    zzauw.zze localzze;
    Object localObject2;
    try
    {
      localObject1 = zzKg().zzfu(paramString);
      if (localObject1 == null)
      {
        zzKl().zzMe().zzj("Log and bundle not available. package_name", paramString);
        return new byte[0];
      }
      if (!((zzatc)localObject1).zzKx())
      {
        zzKl().zzMe().zzj("Log and bundle disabled. package_name", paramString);
        return new byte[0];
      }
      localzze = new zzauw.zze();
      localzzd.zzbxg = new zzauw.zze[] { localzze };
      localzze.zzbxi = Integer.valueOf(1);
      localzze.zzbxq = "android";
      localzze.zzaS = ((zzatc)localObject1).zzke();
      localzze.zzbqL = ((zzatc)localObject1).zzKu();
      localzze.zzbhN = ((zzatc)localObject1).zzmZ();
      localzze.zzbxD = Integer.valueOf((int)((zzatc)localObject1).zzKt());
      localzze.zzbxu = Long.valueOf(((zzatc)localObject1).zzKv());
      localzze.zzbqK = ((zzatc)localObject1).getGmpAppId();
      localzze.zzbxz = Long.valueOf(((zzatc)localObject1).zzKw());
      localObject2 = zzKm().zzfG(((zzatc)localObject1).zzke());
      if (!TextUtils.isEmpty((CharSequence)((Pair)localObject2).first))
      {
        localzze.zzbxw = ((String)((Pair)localObject2).first);
        localzze.zzbxx = ((Boolean)((Pair)localObject2).second);
      }
      localzze.zzbxr = zzKc().zzkN();
      localzze.zzbb = zzKc().zzLS();
      localzze.zzbxt = Integer.valueOf((int)zzKc().zzLT());
      localzze.zzbxs = zzKc().zzLU();
      localzze.zzbxy = ((zzatc)localObject1).getAppInstanceId();
      localzze.zzbqS = ((zzatc)localObject1).zzKq();
      localObject2 = zzKg().zzft(((zzatc)localObject1).zzke());
      localzze.zzbxk = new zzauw.zzg[((List)localObject2).size()];
      int i = 0;
      while (i < ((List)localObject2).size())
      {
        localObject3 = new zzauw.zzg();
        localzze.zzbxk[i] = localObject3;
        ((zzauw.zzg)localObject3).name = ((zzaus)((List)localObject2).get(i)).mName;
        ((zzauw.zzg)localObject3).zzbxM = Long.valueOf(((zzaus)((List)localObject2).get(i)).zzbwj);
        zzKh().zza((zzauw.zzg)localObject3, ((zzaus)((List)localObject2).get(i)).mValue);
        i += 1;
      }
      localObject2 = paramzzatq.zzbrH.zzLW();
      if ("_iap".equals(paramzzatq.name))
      {
        ((Bundle)localObject2).putLong("_c", 1L);
        zzKl().zzMe().log("Marking in-app purchase as real-time");
        ((Bundle)localObject2).putLong("_r", 1L);
      }
      ((Bundle)localObject2).putString("_o", paramzzatq.zzbqW);
      if (zzKh().zzge(localzze.zzaS))
      {
        zzKh().zza((Bundle)localObject2, "_dbg", Long.valueOf(1L));
        zzKh().zza((Bundle)localObject2, "_r", Long.valueOf(1L));
      }
      Object localObject3 = zzKg().zzQ(paramString, paramzzatq.name);
      if (localObject3 == null)
      {
        localObject3 = new zzatn(paramString, paramzzatq.name, 1L, 0L, paramzzatq.zzbrI);
        zzKg().zza((zzatn)localObject3);
        l1 = 0L;
      }
      for (;;)
      {
        paramzzatq = new zzatm(this, paramzzatq.zzbqW, paramString, paramzzatq.name, paramzzatq.zzbrI, l1, (Bundle)localObject2);
        localObject2 = new zzauw.zzb();
        localzze.zzbxj = new zzauw.zzb[] { localObject2 };
        ((zzauw.zzb)localObject2).zzbxc = Long.valueOf(paramzzatq.zzaxb);
        ((zzauw.zzb)localObject2).name = paramzzatq.mName;
        ((zzauw.zzb)localObject2).zzbxd = Long.valueOf(paramzzatq.zzbrz);
        ((zzauw.zzb)localObject2).zzbxb = new zzauw.zzc[paramzzatq.zzbrA.size()];
        localObject3 = paramzzatq.zzbrA.iterator();
        i = 0;
        while (((Iterator)localObject3).hasNext())
        {
          Object localObject4 = (String)((Iterator)localObject3).next();
          zzauw.zzc localzzc = new zzauw.zzc();
          ((zzauw.zzb)localObject2).zzbxb[i] = localzzc;
          localzzc.name = ((String)localObject4);
          localObject4 = paramzzatq.zzbrA.get((String)localObject4);
          zzKh().zza(localzzc, localObject4);
          i += 1;
        }
        l1 = ((zzatn)localObject3).zzbrD;
        localObject3 = ((zzatn)localObject3).zzap(paramzzatq.zzbrI).zzLV();
        zzKg().zza((zzatn)localObject3);
      }
      localzze.zzbxC = zza(((zzatc)localObject1).zzke(), localzze.zzbxk, localzze.zzbxj);
    }
    finally
    {
      zzKg().endTransaction();
    }
    localzze.zzbxm = ((zzauw.zzb)localObject2).zzbxc;
    localzze.zzbxn = ((zzauw.zzb)localObject2).zzbxc;
    long l1 = ((zzatc)localObject1).zzKs();
    long l2;
    if (l1 != 0L)
    {
      paramzzatq = Long.valueOf(l1);
      localzze.zzbxp = paramzzatq;
      l2 = ((zzatc)localObject1).zzKr();
      if (l2 != 0L) {
        break label1157;
      }
    }
    for (;;)
    {
      if (l1 != 0L) {}
      for (paramzzatq = Long.valueOf(l1);; paramzzatq = null)
      {
        localzze.zzbxo = paramzzatq;
        ((zzatc)localObject1).zzKB();
        localzze.zzbxA = Integer.valueOf((int)((zzatc)localObject1).zzKy());
        localzze.zzbxv = Long.valueOf(zzKn().zzKv());
        localzze.zzbxl = Long.valueOf(zznR().currentTimeMillis());
        localzze.zzbxB = Boolean.TRUE;
        ((zzatc)localObject1).zzY(localzze.zzbxm.longValue());
        ((zzatc)localObject1).zzZ(localzze.zzbxn.longValue());
        zzKg().zza((zzatc)localObject1);
        zzKg().setTransactionSuccessful();
        zzKg().endTransaction();
        try
        {
          paramzzatq = new byte[localzzd.zzafB()];
          localObject1 = zzbyc.zzah(paramzzatq);
          localzzd.zza((zzbyc)localObject1);
          ((zzbyc)localObject1).zzafo();
          paramzzatq = zzKh().zzk(paramzzatq);
          return paramzzatq;
        }
        catch (IOException paramzzatq)
        {
          zzKl().zzLZ().zze("Data loss. Failed to bundle and serialize. appId", zzatx.zzfE(paramString), paramzzatq);
          return null;
        }
        paramzzatq = null;
        break;
      }
      label1157:
      l1 = l2;
    }
  }
  
  boolean zzaq(long paramLong)
  {
    return zzl(null, paramLong);
  }
  
  void zzb(zzatc paramzzatc)
  {
    Object localObject2 = null;
    if (TextUtils.isEmpty(paramzzatc.getGmpAppId()))
    {
      zzb(paramzzatc.zzke(), 204, null, null, null);
      return;
    }
    String str1 = zzKn().zzP(paramzzatc.getGmpAppId(), paramzzatc.getAppInstanceId());
    try
    {
      URL localURL = new URL(str1);
      zzKl().zzMf().zzj("Fetching remote configuration", paramzzatc.zzke());
      zzauv.zzb localzzb = zzKi().zzfL(paramzzatc.zzke());
      String str2 = zzKi().zzfM(paramzzatc.zzke());
      Object localObject1 = localObject2;
      if (localzzb != null)
      {
        localObject1 = localObject2;
        if (!TextUtils.isEmpty(str2))
        {
          localObject1 = new ArrayMap();
          ((Map)localObject1).put("If-Modified-Since", str2);
        }
      }
      zzMz().zza(paramzzatc.zzke(), localURL, (Map)localObject1, new zzaty.zza()
      {
        public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
        {
          zzaue.this.zzb(paramAnonymousString, paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte, paramAnonymousMap);
        }
      });
      return;
    }
    catch (MalformedURLException localMalformedURLException)
    {
      zzKl().zzLZ().zze("Failed to parse config URL. Not fetching. appId", zzatx.zzfE(paramzzatc.zzke()), str1);
    }
  }
  
  @WorkerThread
  void zzb(zzatd paramzzatd, long paramLong)
  {
    zzmR();
    zzob();
    Bundle localBundle = new Bundle();
    localBundle.putLong("_c", 1L);
    localBundle.putLong("_r", 1L);
    zzb(new zzatq("_v", new zzato(localBundle), "auto", paramLong), paramzzatd);
  }
  
  @WorkerThread
  void zzb(zzatg paramzzatg, zzatd paramzzatd)
  {
    zzac.zzw(paramzzatg);
    zzac.zzdr(paramzzatg.packageName);
    zzac.zzw(paramzzatg.zzbqW);
    zzac.zzw(paramzzatg.zzbqX);
    zzac.zzdr(paramzzatg.zzbqX.name);
    zzmR();
    zzob();
    if (TextUtils.isEmpty(paramzzatd.zzbqK)) {
      return;
    }
    if (!paramzzatd.zzbqP)
    {
      zzf(paramzzatd);
      return;
    }
    paramzzatg = new zzatg(paramzzatg);
    zzKg().beginTransaction();
    for (;;)
    {
      try
      {
        Object localObject = zzKg().zzT(paramzzatg.packageName, paramzzatg.zzbqX.name);
        if ((localObject != null) && (((zzatg)localObject).zzbqZ))
        {
          paramzzatg.zzbqW = ((zzatg)localObject).zzbqW;
          paramzzatg.zzbqY = ((zzatg)localObject).zzbqY;
          paramzzatg.zzbra = ((zzatg)localObject).zzbra;
          paramzzatg.zzbrd = ((zzatg)localObject).zzbrd;
          i = 0;
          if (paramzzatg.zzbqZ)
          {
            localObject = paramzzatg.zzbqX;
            localObject = new zzaus(paramzzatg.packageName, paramzzatg.zzbqW, ((zzauq)localObject).name, ((zzauq)localObject).zzbwf, ((zzauq)localObject).getValue());
            if (!zzKg().zza((zzaus)localObject)) {
              continue;
            }
            zzKl().zzMe().zzd("User property updated immediately", paramzzatg.packageName, ((zzaus)localObject).mName, ((zzaus)localObject).mValue);
            if ((i != 0) && (paramzzatg.zzbrd != null)) {
              zzc(new zzatq(paramzzatg.zzbrd, paramzzatg.zzbqY), paramzzatd);
            }
          }
          if (!zzKg().zza(paramzzatg)) {
            break label430;
          }
          zzKl().zzMe().zzd("Conditional property added", paramzzatg.packageName, paramzzatg.zzbqX.name, paramzzatg.zzbqX.getValue());
          zzKg().setTransactionSuccessful();
        }
        else
        {
          if (!TextUtils.isEmpty(paramzzatg.zzbra)) {
            break label467;
          }
          localObject = paramzzatg.zzbqX;
          paramzzatg.zzbqX = new zzauq(((zzauq)localObject).name, paramzzatg.zzbqY, ((zzauq)localObject).getValue(), ((zzauq)localObject).zzbqW);
          paramzzatg.zzbqZ = true;
          i = 1;
          continue;
        }
        zzKl().zzLZ().zzd("(2)Too many active user properties, ignoring", zzatx.zzfE(paramzzatg.packageName), ((zzaus)localObject).mName, ((zzaus)localObject).mValue);
        continue;
        zzKl().zzLZ().zzd("Too many conditional properties, ignoring", zzatx.zzfE(paramzzatg.packageName), paramzzatg.zzbqX.name, paramzzatg.zzbqX.getValue());
      }
      finally
      {
        zzKg().endTransaction();
      }
      label430:
      continue;
      label467:
      int i = 0;
    }
  }
  
  @WorkerThread
  void zzb(zzatq paramzzatq, zzatd paramzzatd)
  {
    zzac.zzw(paramzzatd);
    zzac.zzdr(paramzzatd.packageName);
    zzmR();
    zzob();
    Object localObject1 = paramzzatd.packageName;
    long l = paramzzatq.zzbrI;
    if (!zzKh().zzd(paramzzatq, paramzzatd)) {
      return;
    }
    if (!paramzzatd.zzbqP)
    {
      zzf(paramzzatd);
      return;
    }
    zzKg().beginTransaction();
    try
    {
      localObject2 = zzKg().zzh((String)localObject1, l).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (zzatg)((Iterator)localObject2).next();
        if (localObject3 != null)
        {
          zzKl().zzMe().zzd("User property timed out", ((zzatg)localObject3).packageName, ((zzatg)localObject3).zzbqX.name, ((zzatg)localObject3).zzbqX.getValue());
          if (((zzatg)localObject3).zzbrb != null) {
            zzc(new zzatq(((zzatg)localObject3).zzbrb, l), paramzzatd);
          }
          zzKg().zzU((String)localObject1, ((zzatg)localObject3).zzbqX.name);
        }
      }
      localObject3 = zzKg().zzi((String)localObject1, l);
    }
    finally
    {
      zzKg().endTransaction();
    }
    Object localObject2 = new ArrayList(((List)localObject3).size());
    Object localObject3 = ((List)localObject3).iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (zzatg)((Iterator)localObject3).next();
      if (localObject4 != null)
      {
        zzKl().zzMe().zzd("User property expired", ((zzatg)localObject4).packageName, ((zzatg)localObject4).zzbqX.name, ((zzatg)localObject4).zzbqX.getValue());
        zzKg().zzR((String)localObject1, ((zzatg)localObject4).zzbqX.name);
        if (((zzatg)localObject4).zzbrf != null) {
          ((List)localObject2).add(((zzatg)localObject4).zzbrf);
        }
        zzKg().zzU((String)localObject1, ((zzatg)localObject4).zzbqX.name);
      }
    }
    localObject2 = ((List)localObject2).iterator();
    while (((Iterator)localObject2).hasNext()) {
      zzc(new zzatq((zzatq)((Iterator)localObject2).next(), l), paramzzatd);
    }
    localObject2 = zzKg().zzc((String)localObject1, paramzzatq.name, l);
    localObject1 = new ArrayList(((List)localObject2).size());
    localObject2 = ((List)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (zzatg)((Iterator)localObject2).next();
      if (localObject3 != null)
      {
        localObject4 = ((zzatg)localObject3).zzbqX;
        localObject4 = new zzaus(((zzatg)localObject3).packageName, ((zzatg)localObject3).zzbqW, ((zzauq)localObject4).name, l, ((zzauq)localObject4).getValue());
        if (zzKg().zza((zzaus)localObject4)) {
          zzKl().zzMe().zzd("User property triggered", ((zzatg)localObject3).packageName, ((zzaus)localObject4).mName, ((zzaus)localObject4).mValue);
        }
        for (;;)
        {
          if (((zzatg)localObject3).zzbrd != null) {
            ((List)localObject1).add(((zzatg)localObject3).zzbrd);
          }
          ((zzatg)localObject3).zzbqX = new zzauq((zzaus)localObject4);
          ((zzatg)localObject3).zzbqZ = true;
          zzKg().zza((zzatg)localObject3);
          break;
          zzKl().zzLZ().zzd("Too many active user properties, ignoring", zzatx.zzfE(((zzatg)localObject3).packageName), ((zzaus)localObject4).mName, ((zzaus)localObject4).mValue);
        }
      }
    }
    zzc(paramzzatq, paramzzatd);
    paramzzatq = ((List)localObject1).iterator();
    while (paramzzatq.hasNext()) {
      zzc(new zzatq((zzatq)paramzzatq.next(), l), paramzzatd);
    }
    zzKg().setTransactionSuccessful();
    zzKg().endTransaction();
  }
  
  @WorkerThread
  void zzb(zzatq paramzzatq, String paramString)
  {
    zzatc localzzatc = zzKg().zzfu(paramString);
    if ((localzzatc == null) || (TextUtils.isEmpty(localzzatc.zzmZ())))
    {
      zzKl().zzMe().zzj("No app data available; dropping event", paramString);
      return;
    }
    try
    {
      String str = zzadg.zzbi(getContext()).getPackageInfo(paramString, 0).versionName;
      if ((localzzatc.zzmZ() != null) && (!localzzatc.zzmZ().equals(str)))
      {
        zzKl().zzMb().zzj("App version does not match; dropping event. appId", zzatx.zzfE(paramString));
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (!"_ui".equals(paramzzatq.name)) {
        zzKl().zzMb().zzj("Could not find package. appId", zzatx.zzfE(paramString));
      }
      zzb(paramzzatq, new zzatd(paramString, localzzatc.getGmpAppId(), localzzatc.zzmZ(), localzzatc.zzKt(), localzzatc.zzKu(), localzzatc.zzKv(), localzzatc.zzKw(), null, localzzatc.zzKx(), false, localzzatc.zzKq(), localzzatc.zzuW(), 0L, 0));
    }
  }
  
  void zzb(zzauh paramzzauh)
  {
    this.zzbuD += 1;
  }
  
  /* Error */
  @WorkerThread
  void zzb(zzauq paramzzauq, zzatd paramzzatd)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_0
    //   4: istore_3
    //   5: aload_0
    //   6: invokevirtual 423	com/google/android/gms/internal/zzaue:zzmR	()V
    //   9: aload_0
    //   10: invokevirtual 426	com/google/android/gms/internal/zzaue:zzob	()V
    //   13: aload_2
    //   14: getfield 707	com/google/android/gms/internal/zzatd:zzbqK	Ljava/lang/String;
    //   17: invokestatic 442	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   20: ifeq +4 -> 24
    //   23: return
    //   24: aload_2
    //   25: getfield 767	com/google/android/gms/internal/zzatd:zzbqP	Z
    //   28: ifne +9 -> 37
    //   31: aload_0
    //   32: aload_2
    //   33: invokespecial 1725	com/google/android/gms/internal/zzaue:zzf	(Lcom/google/android/gms/internal/zzatd;)V
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   41: aload_1
    //   42: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   45: invokevirtual 1839	com/google/android/gms/internal/zzaut:zzfX	(Ljava/lang/String;)I
    //   48: istore 5
    //   50: iload 5
    //   52: ifeq +53 -> 105
    //   55: aload_0
    //   56: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   59: aload_1
    //   60: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   63: aload_0
    //   64: invokevirtual 156	com/google/android/gms/internal/zzaue:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   67: invokevirtual 1842	com/google/android/gms/internal/zzati:zzKN	()I
    //   70: iconst_1
    //   71: invokevirtual 1845	com/google/android/gms/internal/zzaut:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   74: astore_2
    //   75: aload_1
    //   76: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   79: ifnull +11 -> 90
    //   82: aload_1
    //   83: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   86: invokevirtual 405	java/lang/String:length	()I
    //   89: istore_3
    //   90: aload_0
    //   91: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   94: iload 5
    //   96: ldc_w 857
    //   99: aload_2
    //   100: iload_3
    //   101: invokevirtual 860	com/google/android/gms/internal/zzaut:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   104: return
    //   105: aload_0
    //   106: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   109: aload_1
    //   110: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   113: aload_1
    //   114: invokevirtual 1751	com/google/android/gms/internal/zzauq:getValue	()Ljava/lang/Object;
    //   117: invokevirtual 1848	com/google/android/gms/internal/zzaut:zzl	(Ljava/lang/String;Ljava/lang/Object;)I
    //   120: istore 5
    //   122: iload 5
    //   124: ifeq +75 -> 199
    //   127: aload_0
    //   128: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   131: aload_1
    //   132: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   135: aload_0
    //   136: invokevirtual 156	com/google/android/gms/internal/zzaue:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   139: invokevirtual 1842	com/google/android/gms/internal/zzati:zzKN	()I
    //   142: iconst_1
    //   143: invokevirtual 1845	com/google/android/gms/internal/zzaut:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   146: astore_2
    //   147: aload_1
    //   148: invokevirtual 1751	com/google/android/gms/internal/zzauq:getValue	()Ljava/lang/Object;
    //   151: astore_1
    //   152: iload 4
    //   154: istore_3
    //   155: aload_1
    //   156: ifnull +28 -> 184
    //   159: aload_1
    //   160: instanceof 398
    //   163: ifne +13 -> 176
    //   166: iload 4
    //   168: istore_3
    //   169: aload_1
    //   170: instanceof 1450
    //   173: ifeq +11 -> 184
    //   176: aload_1
    //   177: invokestatic 401	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   180: invokevirtual 405	java/lang/String:length	()I
    //   183: istore_3
    //   184: aload_0
    //   185: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   188: iload 5
    //   190: ldc_w 857
    //   193: aload_2
    //   194: iload_3
    //   195: invokevirtual 860	com/google/android/gms/internal/zzaut:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   198: return
    //   199: aload_0
    //   200: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   203: aload_1
    //   204: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   207: aload_1
    //   208: invokevirtual 1751	com/google/android/gms/internal/zzauq:getValue	()Ljava/lang/Object;
    //   211: invokevirtual 1851	com/google/android/gms/internal/zzaut:zzm	(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    //   214: astore 7
    //   216: aload 7
    //   218: ifnull -195 -> 23
    //   221: new 1501	com/google/android/gms/internal/zzaus
    //   224: dup
    //   225: aload_2
    //   226: getfield 683	com/google/android/gms/internal/zzatd:packageName	Ljava/lang/String;
    //   229: aload_1
    //   230: getfield 1770	com/google/android/gms/internal/zzauq:zzbqW	Ljava/lang/String;
    //   233: aload_1
    //   234: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   237: aload_1
    //   238: getfield 1748	com/google/android/gms/internal/zzauq:zzbwf	J
    //   241: aload 7
    //   243: invokespecial 1754	com/google/android/gms/internal/zzaus:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   246: astore_1
    //   247: aload_0
    //   248: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   251: invokevirtual 226	com/google/android/gms/internal/zzatx:zzMe	()Lcom/google/android/gms/internal/zzatx$zza;
    //   254: ldc_w 1853
    //   257: aload_1
    //   258: getfield 1502	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   261: aload 7
    //   263: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   266: aload_0
    //   267: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   270: invokevirtual 802	com/google/android/gms/internal/zzatj:beginTransaction	()V
    //   273: aload_0
    //   274: aload_2
    //   275: invokespecial 1725	com/google/android/gms/internal/zzaue:zzf	(Lcom/google/android/gms/internal/zzatd;)V
    //   278: aload_0
    //   279: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   282: aload_1
    //   283: invokevirtual 1757	com/google/android/gms/internal/zzatj:zza	(Lcom/google/android/gms/internal/zzaus;)Z
    //   286: istore 6
    //   288: aload_0
    //   289: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   292: invokevirtual 967	com/google/android/gms/internal/zzatj:setTransactionSuccessful	()V
    //   295: iload 6
    //   297: ifeq +32 -> 329
    //   300: aload_0
    //   301: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   304: invokevirtual 226	com/google/android/gms/internal/zzatx:zzMe	()Lcom/google/android/gms/internal/zzatx$zza;
    //   307: ldc_w 1855
    //   310: aload_1
    //   311: getfield 1502	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   314: aload_1
    //   315: getfield 1512	com/google/android/gms/internal/zzaus:mValue	Ljava/lang/Object;
    //   318: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   321: aload_0
    //   322: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   325: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   328: return
    //   329: aload_0
    //   330: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   333: invokevirtual 358	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   336: ldc_w 1857
    //   339: aload_1
    //   340: getfield 1502	com/google/android/gms/internal/zzaus:mName	Ljava/lang/String;
    //   343: aload_1
    //   344: getfield 1512	com/google/android/gms/internal/zzaus:mValue	Ljava/lang/Object;
    //   347: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   350: aload_0
    //   351: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   354: bipush 9
    //   356: aconst_null
    //   357: aconst_null
    //   358: iconst_0
    //   359: invokevirtual 860	com/google/android/gms/internal/zzaut:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   362: goto -41 -> 321
    //   365: astore_1
    //   366: aload_0
    //   367: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   370: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   373: aload_1
    //   374: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	375	0	this	zzaue
    //   0	375	1	paramzzauq	zzauq
    //   0	375	2	paramzzatd	zzatd
    //   4	191	3	i	int
    //   1	166	4	j	int
    //   48	141	5	k	int
    //   286	10	6	bool	boolean
    //   214	48	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   273	295	365	finally
    //   300	321	365	finally
    //   329	362	365	finally
  }
  
  @WorkerThread
  void zzb(String paramString, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    int j = 0;
    zzmR();
    zzob();
    zzac.zzdr(paramString);
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    zzKg().beginTransaction();
    label93:
    label108:
    int i;
    for (;;)
    {
      try
      {
        paramArrayOfByte = zzKg().zzfu(paramString);
        if ((paramInt == 200) || (paramInt == 204)) {
          break label475;
        }
        if (paramInt == 304)
        {
          break label475;
          if (paramArrayOfByte == null)
          {
            zzKl().zzMb().zzj("App does not exist in onConfigFetched. appId", zzatx.zzfE(paramString));
            zzKg().setTransactionSuccessful();
          }
        }
        else
        {
          i = 0;
          continue;
        }
        if ((i == 0) && (paramInt != 404)) {
          break;
        }
        if (paramMap != null)
        {
          paramThrowable = (List)paramMap.get("Last-Modified");
          if ((paramThrowable != null) && (paramThrowable.size() > 0))
          {
            paramThrowable = (String)paramThrowable.get(0);
            break label485;
            label172:
            if (zzKi().zzfL(paramString) != null) {
              continue;
            }
            bool = zzKi().zzb(paramString, null, null);
            if (bool) {
              continue;
            }
          }
        }
        else
        {
          paramThrowable = null;
          continue;
        }
        paramThrowable = null;
        break label485;
        label218:
        boolean bool = zzKi().zzb(paramString, arrayOfByte, paramThrowable);
        if (!bool) {
          return;
        }
        paramArrayOfByte.zzae(zznR().currentTimeMillis());
        zzKg().zza(paramArrayOfByte);
        if (paramInt == 404)
        {
          zzKl().zzMc().zzj("Config not found. Using empty config. appId", paramString);
          if ((!zzMz().zzqa()) || (!zzMJ())) {
            break label348;
          }
          zzMI();
          continue;
        }
        zzKl().zzMf().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      }
      finally
      {
        zzKg().endTransaction();
      }
      continue;
      label348:
      zzMK();
    }
    paramArrayOfByte.zzaf(zznR().currentTimeMillis());
    zzKg().zza(paramArrayOfByte);
    zzKl().zzMf().zze("Fetching config failed. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzKi().zzfN(paramString);
    zzKm().zzbtc.set(zznR().currentTimeMillis());
    if (paramInt != 503)
    {
      i = j;
      if (paramInt == 429) {}
    }
    for (;;)
    {
      if (i != 0) {
        zzKm().zzbtd.set(zznR().currentTimeMillis());
      }
      zzMK();
      break label93;
      label475:
      if (paramThrowable != null) {
        break label108;
      }
      i = 1;
      break;
      label485:
      if (paramInt == 404) {
        break label172;
      }
      if (paramInt != 304) {
        break label218;
      }
      break label172;
      i = 1;
    }
  }
  
  @WorkerThread
  void zzc(zzatd paramzzatd, long paramLong)
  {
    zzmR();
    zzob();
    Object localObject1 = zzKg().zzfu(paramzzatd.packageName);
    if ((localObject1 != null) && (TextUtils.isEmpty(((zzatc)localObject1).getGmpAppId())) && (paramzzatd != null) && (!TextUtils.isEmpty(paramzzatd.zzbqK)))
    {
      ((zzatc)localObject1).zzae(0L);
      zzKg().zza((zzatc)localObject1);
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_c", 1L);
    localBundle.putLong("_r", 1L);
    localBundle.putLong("_uwa", 0L);
    localBundle.putLong("_pfo", 0L);
    localBundle.putLong("_sys", 0L);
    localBundle.putLong("_sysu", 0L);
    if (getContext().getPackageManager() == null) {
      zzKl().zzLZ().zzj("PackageManager is null, first open report might be inaccurate. appId", zzatx.zzfE(paramzzatd.packageName));
    }
    for (;;)
    {
      long l = zzKg().zzfA(paramzzatd.packageName);
      if (l >= 0L) {
        localBundle.putLong("_pfo", l);
      }
      zzb(new zzatq("_f", new zzato(localBundle), "auto", paramLong), paramzzatd);
      return;
      try
      {
        localObject1 = zzadg.zzbi(getContext()).getPackageInfo(paramzzatd.packageName, 0);
        if ((localObject1 != null) && (((PackageInfo)localObject1).firstInstallTime != 0L) && (((PackageInfo)localObject1).firstInstallTime != ((PackageInfo)localObject1).lastUpdateTime)) {
          localBundle.putLong("_uwa", 1L);
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        try
        {
          localObject1 = zzadg.zzbi(getContext()).getApplicationInfo(paramzzatd.packageName, 0);
          if (localObject1 == null) {
            continue;
          }
          if ((((ApplicationInfo)localObject1).flags & 0x1) != 0) {
            localBundle.putLong("_sys", 1L);
          }
          if ((((ApplicationInfo)localObject1).flags & 0x80) == 0) {
            continue;
          }
          localBundle.putLong("_sysu", 1L);
          continue;
          localNameNotFoundException1 = localNameNotFoundException1;
          zzKl().zzLZ().zze("Package info is null, first open report might be inaccurate. appId", zzatx.zzfE(paramzzatd.packageName), localNameNotFoundException1);
          Object localObject2 = null;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          for (;;)
          {
            zzKl().zzLZ().zze("Application info is null, first open report might be inaccurate. appId", zzatx.zzfE(paramzzatd.packageName), localNameNotFoundException2);
            Object localObject3 = null;
          }
        }
      }
    }
  }
  
  /* Error */
  @WorkerThread
  void zzc(zzatg paramzzatg, zzatd paramzzatd)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 94	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_1
    //   6: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   9: invokestatic 659	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_1
    //   14: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   17: invokestatic 94	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   20: pop
    //   21: aload_1
    //   22: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   25: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   28: invokestatic 659	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   31: pop
    //   32: aload_0
    //   33: invokevirtual 423	com/google/android/gms/internal/zzaue:zzmR	()V
    //   36: aload_0
    //   37: invokevirtual 426	com/google/android/gms/internal/zzaue:zzob	()V
    //   40: aload_2
    //   41: getfield 707	com/google/android/gms/internal/zzatd:zzbqK	Ljava/lang/String;
    //   44: invokestatic 442	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   47: ifeq +4 -> 51
    //   50: return
    //   51: aload_2
    //   52: getfield 767	com/google/android/gms/internal/zzatd:zzbqP	Z
    //   55: ifne +9 -> 64
    //   58: aload_0
    //   59: aload_2
    //   60: invokespecial 1725	com/google/android/gms/internal/zzaue:zzf	(Lcom/google/android/gms/internal/zzatd;)V
    //   63: return
    //   64: aload_0
    //   65: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   68: invokevirtual 802	com/google/android/gms/internal/zzatj:beginTransaction	()V
    //   71: aload_0
    //   72: aload_2
    //   73: invokespecial 1725	com/google/android/gms/internal/zzaue:zzf	(Lcom/google/android/gms/internal/zzatd;)V
    //   76: aload_0
    //   77: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   80: aload_1
    //   81: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   84: aload_1
    //   85: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   88: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   91: invokevirtual 1732	com/google/android/gms/internal/zzatj:zzT	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/internal/zzatg;
    //   94: astore 4
    //   96: aload 4
    //   98: ifnull +151 -> 249
    //   101: aload_0
    //   102: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   105: invokevirtual 226	com/google/android/gms/internal/zzatx:zzMe	()Lcom/google/android/gms/internal/zzatx$zza;
    //   108: ldc_w 1926
    //   111: aload_1
    //   112: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   115: aload_1
    //   116: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   119: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   122: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   125: aload_0
    //   126: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   129: aload_1
    //   130: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   133: aload_1
    //   134: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   137: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   140: invokevirtual 1792	com/google/android/gms/internal/zzatj:zzU	(Ljava/lang/String;Ljava/lang/String;)I
    //   143: pop
    //   144: aload 4
    //   146: getfield 1735	com/google/android/gms/internal/zzatg:zzbqZ	Z
    //   149: ifeq +21 -> 170
    //   152: aload_0
    //   153: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   156: aload_1
    //   157: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   160: aload_1
    //   161: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   164: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   167: invokevirtual 1799	com/google/android/gms/internal/zzatj:zzR	(Ljava/lang/String;Ljava/lang/String;)V
    //   170: aload_1
    //   171: getfield 1802	com/google/android/gms/internal/zzatg:zzbrf	Lcom/google/android/gms/internal/zzatq;
    //   174: ifnull +60 -> 234
    //   177: aconst_null
    //   178: astore_3
    //   179: aload_1
    //   180: getfield 1802	com/google/android/gms/internal/zzatg:zzbrf	Lcom/google/android/gms/internal/zzatq;
    //   183: getfield 1586	com/google/android/gms/internal/zzatq:zzbrH	Lcom/google/android/gms/internal/zzato;
    //   186: ifnull +14 -> 200
    //   189: aload_1
    //   190: getfield 1802	com/google/android/gms/internal/zzatg:zzbrf	Lcom/google/android/gms/internal/zzatq;
    //   193: getfield 1586	com/google/android/gms/internal/zzatq:zzbrH	Lcom/google/android/gms/internal/zzato;
    //   196: invokevirtual 1590	com/google/android/gms/internal/zzato:zzLW	()Landroid/os/Bundle;
    //   199: astore_3
    //   200: aload_0
    //   201: aload_0
    //   202: invokevirtual 217	com/google/android/gms/internal/zzaue:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   205: aload_1
    //   206: getfield 1802	com/google/android/gms/internal/zzatg:zzbrf	Lcom/google/android/gms/internal/zzatq;
    //   209: getfield 1593	com/google/android/gms/internal/zzatq:name	Ljava/lang/String;
    //   212: aload_3
    //   213: aload 4
    //   215: getfield 1716	com/google/android/gms/internal/zzatg:zzbqW	Ljava/lang/String;
    //   218: aload_1
    //   219: getfield 1802	com/google/android/gms/internal/zzatg:zzbrf	Lcom/google/android/gms/internal/zzatq;
    //   222: getfield 1618	com/google/android/gms/internal/zzatq:zzbrI	J
    //   225: iconst_1
    //   226: iconst_0
    //   227: invokevirtual 1929	com/google/android/gms/internal/zzaut:zza	(Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;JZZ)Lcom/google/android/gms/internal/zzatq;
    //   230: aload_2
    //   231: invokevirtual 1764	com/google/android/gms/internal/zzaue:zzc	(Lcom/google/android/gms/internal/zzatq;Lcom/google/android/gms/internal/zzatd;)V
    //   234: aload_0
    //   235: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   238: invokevirtual 967	com/google/android/gms/internal/zzatj:setTransactionSuccessful	()V
    //   241: aload_0
    //   242: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   245: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   248: return
    //   249: aload_0
    //   250: invokevirtual 146	com/google/android/gms/internal/zzaue:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   253: invokevirtual 416	com/google/android/gms/internal/zzatx:zzMb	()Lcom/google/android/gms/internal/zzatx$zza;
    //   256: ldc_w 1931
    //   259: aload_1
    //   260: getfield 1715	com/google/android/gms/internal/zzatg:packageName	Ljava/lang/String;
    //   263: invokestatic 847	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   266: aload_1
    //   267: getfield 1720	com/google/android/gms/internal/zzatg:zzbqX	Lcom/google/android/gms/internal/zzauq;
    //   270: getfield 1723	com/google/android/gms/internal/zzauq:name	Ljava/lang/String;
    //   273: invokevirtual 368	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   276: goto -42 -> 234
    //   279: astore_1
    //   280: aload_0
    //   281: invokevirtual 430	com/google/android/gms/internal/zzaue:zzKg	()Lcom/google/android/gms/internal/zzatj;
    //   284: invokevirtual 970	com/google/android/gms/internal/zzatj:endTransaction	()V
    //   287: aload_1
    //   288: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	289	0	this	zzaue
    //   0	289	1	paramzzatg	zzatg
    //   0	289	2	paramzzatd	zzatd
    //   178	35	3	localBundle	Bundle
    //   94	120	4	localzzatg	zzatg
    // Exception table:
    //   from	to	target	type
    //   71	96	279	finally
    //   101	170	279	finally
    //   170	177	279	finally
    //   179	200	279	finally
    //   200	234	279	finally
    //   234	241	279	finally
    //   249	276	279	finally
  }
  
  @WorkerThread
  void zzc(zzatq paramzzatq, zzatd paramzzatd)
  {
    zzac.zzw(paramzzatd);
    zzac.zzdr(paramzzatd.packageName);
    long l2 = System.nanoTime();
    zzmR();
    zzob();
    String str = paramzzatd.packageName;
    if (!zzKh().zzd(paramzzatq, paramzzatd)) {
      return;
    }
    if (!paramzzatd.zzbqP)
    {
      zzf(paramzzatd);
      return;
    }
    if (zzKi().zzaa(str, paramzzatq.name))
    {
      zzKl().zzMb().zze("Dropping blacklisted event. appId", zzatx.zzfE(str), paramzzatq.name);
      if ((zzKh().zzgg(str)) || (zzKh().zzgh(str))) {}
      for (int i = 1;; i = 0)
      {
        if ((i == 0) && (!"_err".equals(paramzzatq.name))) {
          zzKh().zza(11, "_ev", paramzzatq.name, 0);
        }
        if (i == 0) {
          break;
        }
        paramzzatq = zzKg().zzfu(str);
        if (paramzzatq == null) {
          break;
        }
        l1 = Math.max(paramzzatq.zzKA(), paramzzatq.zzKz());
        if (Math.abs(zznR().currentTimeMillis() - l1) <= zzKn().zzLm()) {
          break;
        }
        zzKl().zzMe().log("Fetching config for blacklisted app");
        zzb(paramzzatq);
        return;
      }
    }
    if (zzKl().zzak(2)) {
      zzKl().zzMf().zzj("Logging event", paramzzatq);
    }
    zzKg().beginTransaction();
    Bundle localBundle;
    boolean bool1;
    boolean bool2;
    for (;;)
    {
      try
      {
        localBundle = paramzzatq.zzbrH.zzLW();
        zzf(paramzzatd);
        double d1;
        Object localObject2;
        if (("_iap".equals(paramzzatq.name)) || ("ecommerce_purchase".equals(paramzzatq.name)))
        {
          localObject1 = localBundle.getString("currency");
          if (!"ecommerce_purchase".equals(paramzzatq.name)) {
            continue;
          }
          double d2 = localBundle.getDouble("value") * 1000000.0D;
          d1 = d2;
          if (d2 == 0.0D) {
            d1 = localBundle.getLong("value") * 1000000.0D;
          }
          if ((d1 > 9.223372036854776E18D) || (d1 < -9.223372036854776E18D)) {
            continue;
          }
          l1 = Math.round(d1);
          if (!TextUtils.isEmpty((CharSequence)localObject1))
          {
            localObject2 = ((String)localObject1).toUpperCase(Locale.US);
            if (((String)localObject2).matches("[A-Z]{3}"))
            {
              localObject1 = String.valueOf("_ltv_");
              localObject2 = String.valueOf(localObject2);
              if (((String)localObject2).length() == 0) {
                continue;
              }
              localObject1 = ((String)localObject1).concat((String)localObject2);
              localObject2 = zzKg().zzS(str, (String)localObject1);
              if ((localObject2 != null) && ((((zzaus)localObject2).mValue instanceof Long))) {
                break label797;
              }
              zzKg().zzz(str, zzKn().zzfn(str) - 1);
              localObject1 = new zzaus(str, paramzzatq.zzbqW, (String)localObject1, zznR().currentTimeMillis(), Long.valueOf(l1));
              if (!zzKg().zza((zzaus)localObject1))
              {
                zzKl().zzLZ().zzd("Too many unique user properties are set. Ignoring user property. appId", zzatx.zzfE(str), ((zzaus)localObject1).mName, ((zzaus)localObject1).mValue);
                zzKh().zza(9, null, null, 0);
              }
            }
          }
        }
        bool1 = zzaut.zzfT(paramzzatq.name);
        bool2 = "_err".equals(paramzzatq.name);
        localObject1 = zzKg().zza(zzMG(), str, true, bool1, false, bool2, false);
        l1 = ((zzatj.zza)localObject1).zzbrp - zzKn().zzKV();
        if (l1 <= 0L) {
          break;
        }
        if (l1 % 1000L == 1L) {
          zzKl().zzLZ().zze("Data loss. Too many events logged. appId, count", zzatx.zzfE(str), Long.valueOf(((zzatj.zza)localObject1).zzbrp));
        }
        zzKh().zza(16, "_ev", paramzzatq.name, 0);
        zzKg().setTransactionSuccessful();
        return;
        zzKl().zzMb().zze("Data lost. Currency value is too big. appId", zzatx.zzfE(str), Double.valueOf(d1));
        zzKg().setTransactionSuccessful();
        return;
        l1 = localBundle.getLong("value");
        continue;
        localObject1 = new String((String)localObject1);
        continue;
        l3 = ((Long)((zzaus)localObject2).mValue).longValue();
      }
      finally
      {
        zzKg().endTransaction();
      }
      label797:
      long l3;
      localObject1 = new zzaus(str, paramzzatq.zzbqW, (String)localObject1, zznR().currentTimeMillis(), Long.valueOf(l1 + l3));
    }
    if (bool1)
    {
      l1 = ((zzatj.zza)localObject1).zzbro - zzKn().zzKW();
      if (l1 > 0L)
      {
        if (l1 % 1000L == 1L) {
          zzKl().zzLZ().zze("Data loss. Too many public events logged. appId, count", zzatx.zzfE(str), Long.valueOf(((zzatj.zza)localObject1).zzbro));
        }
        zzKh().zza(16, "_ev", paramzzatq.name, 0);
        zzKg().setTransactionSuccessful();
        zzKg().endTransaction();
        return;
      }
    }
    if (bool2)
    {
      l1 = ((zzatj.zza)localObject1).zzbrr - zzKn().zzfj(paramzzatd.packageName);
      if (l1 > 0L)
      {
        if (l1 == 1L) {
          zzKl().zzLZ().zze("Too many error events logged. appId, count", zzatx.zzfE(str), Long.valueOf(((zzatj.zza)localObject1).zzbrr));
        }
        zzKg().setTransactionSuccessful();
        zzKg().endTransaction();
        return;
      }
    }
    zzKh().zza(localBundle, "_o", paramzzatq.zzbqW);
    if (zzKh().zzge(str))
    {
      zzKh().zza(localBundle, "_dbg", Long.valueOf(1L));
      zzKh().zza(localBundle, "_r", Long.valueOf(1L));
    }
    long l1 = zzKg().zzfv(str);
    if (l1 > 0L) {
      zzKl().zzMb().zze("Data lost. Too many events stored on disk, deleted. appId", zzatx.zzfE(str), Long.valueOf(l1));
    }
    paramzzatq = new zzatm(this, paramzzatq.zzbqW, str, paramzzatq.name, paramzzatq.zzbrI, 0L, localBundle);
    Object localObject1 = zzKg().zzQ(str, paramzzatq.mName);
    if (localObject1 == null)
    {
      l1 = zzKg().zzfC(str);
      zzKn().zzKU();
      if (l1 >= 500L)
      {
        zzKl().zzLZ().zzd("Too many event names used, ignoring event. appId, name, supported count", zzatx.zzfE(str), paramzzatq.mName, Integer.valueOf(zzKn().zzKU()));
        zzKh().zza(8, null, null, 0);
        zzKg().endTransaction();
        return;
      }
    }
    for (localObject1 = new zzatn(str, paramzzatq.mName, 0L, 0L, paramzzatq.zzaxb);; localObject1 = ((zzatn)localObject1).zzap(paramzzatq.zzaxb))
    {
      zzKg().zza((zzatn)localObject1);
      zza(paramzzatq, paramzzatd);
      zzKg().setTransactionSuccessful();
      if (zzKl().zzak(2)) {
        zzKl().zzMf().zzj("Event recorded", paramzzatq);
      }
      zzKg().endTransaction();
      zzMK();
      zzKl().zzMf().zzj("Background event processing time, ms", Long.valueOf((System.nanoTime() - l2 + 500000L) / 1000000L));
      return;
      paramzzatq = paramzzatq.zza(this, ((zzatn)localObject1).zzbrD);
    }
  }
  
  @WorkerThread
  void zzc(zzauq paramzzauq, zzatd paramzzatd)
  {
    zzmR();
    zzob();
    if (TextUtils.isEmpty(paramzzatd.zzbqK)) {
      return;
    }
    if (!paramzzatd.zzbqP)
    {
      zzf(paramzzatd);
      return;
    }
    zzKl().zzMe().zzj("Removing user property", paramzzauq.name);
    zzKg().beginTransaction();
    try
    {
      zzf(paramzzatd);
      zzKg().zzR(paramzzatd.packageName, paramzzauq.name);
      zzKg().setTransactionSuccessful();
      zzKl().zzMe().zzj("User property removed", paramzzauq.name);
      return;
    }
    finally
    {
      zzKg().endTransaction();
    }
  }
  
  void zzd(zzatd paramzzatd)
  {
    zzmR();
    zzob();
    zzac.zzdr(paramzzatd.packageName);
    zzf(paramzzatd);
  }
  
  @WorkerThread
  void zzd(zzatd paramzzatd, long paramLong)
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", 1L);
    zzb(new zzatq("_e", new zzato(localBundle), "auto", paramLong), paramzzatd);
  }
  
  @WorkerThread
  void zzd(zzatg paramzzatg)
  {
    zzatd localzzatd = zzfO(paramzzatg.packageName);
    if (localzzatd != null) {
      zzb(paramzzatg, localzzatd);
    }
  }
  
  @WorkerThread
  public void zze(zzatd paramzzatd)
  {
    zzmR();
    zzob();
    zzac.zzw(paramzzatd);
    zzac.zzdr(paramzzatd.packageName);
    if (TextUtils.isEmpty(paramzzatd.zzbqK)) {
      return;
    }
    if (!paramzzatd.zzbqP)
    {
      zzf(paramzzatd);
      return;
    }
    long l2 = paramzzatd.zzbqU;
    long l1 = l2;
    if (l2 == 0L) {
      l1 = zznR().currentTimeMillis();
    }
    int j = paramzzatd.zzbqV;
    int i = j;
    if (j != 0)
    {
      i = j;
      if (j != 1)
      {
        zzKl().zzMb().zze("Incorrect app type, assuming installed app. appId, appType", zzatx.zzfE(paramzzatd.packageName), Integer.valueOf(j));
        i = 0;
      }
    }
    zzKg().beginTransaction();
    for (;;)
    {
      try
      {
        zza(paramzzatd, l1);
        zzf(paramzzatd);
        zzatn localzzatn = null;
        if (i == 0)
        {
          localzzatn = zzKg().zzQ(paramzzatd.packageName, "_f");
          if (localzzatn != null) {
            break label310;
          }
          l2 = (1L + l1 / 3600000L) * 3600000L;
          if (i == 0)
          {
            zzb(new zzauq("_fot", l1, Long.valueOf(l2), "auto"), paramzzatd);
            zzc(paramzzatd, l1);
            zzd(paramzzatd, l1);
            zzKg().setTransactionSuccessful();
          }
        }
        else
        {
          if (i != 1) {
            continue;
          }
          localzzatn = zzKg().zzQ(paramzzatd.packageName, "_v");
          continue;
        }
        if (i != 1) {
          continue;
        }
        zzb(new zzauq("_fvt", l1, Long.valueOf(l2), "auto"), paramzzatd);
        zzb(paramzzatd, l1);
        continue;
        if (!paramzzatd.zzbqQ) {
          continue;
        }
      }
      finally
      {
        zzKg().endTransaction();
      }
      label310:
      zze(paramzzatd, l1);
    }
  }
  
  @WorkerThread
  void zze(zzatd paramzzatd, long paramLong)
  {
    zzb(new zzatq("_cd", new zzato(new Bundle()), "auto", paramLong), paramzzatd);
  }
  
  @WorkerThread
  void zze(zzatg paramzzatg)
  {
    zzatd localzzatd = zzfO(paramzzatg.packageName);
    if (localzzatd != null) {
      zzc(paramzzatg, localzzatd);
    }
  }
  
  @WorkerThread
  zzatd zzfO(String paramString)
  {
    zzatc localzzatc = zzKg().zzfu(paramString);
    if ((localzzatc == null) || (TextUtils.isEmpty(localzzatc.zzmZ())))
    {
      zzKl().zzMe().zzj("No app data available; dropping", paramString);
      return null;
    }
    try
    {
      String str = zzadg.zzbi(getContext()).getPackageInfo(paramString, 0).versionName;
      if ((localzzatc.zzmZ() != null) && (!localzzatc.zzmZ().equals(str)))
      {
        zzKl().zzMb().zzj("App version does not match; dropping. appId", zzatx.zzfE(paramString));
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return new zzatd(paramString, localzzatc.getGmpAppId(), localzzatc.zzmZ(), localzzatc.zzKt(), localzzatc.zzKu(), localzzatc.zzKv(), localzzatc.zzKw(), null, localzzatc.zzKx(), false, localzzatc.zzKq(), localzzatc.zzuW(), 0L, 0);
  }
  
  public String zzfP(final String paramString)
  {
    Object localObject = zzKk().zzd(new Callable()
    {
      public String zzbY()
        throws Exception
      {
        zzatc localzzatc = zzaue.this.zzKg().zzfu(paramString);
        if (localzzatc == null)
        {
          zzaue.this.zzKl().zzMb().log("App info was null when attempting to get app instance id");
          return null;
        }
        return localzzatc.getAppInstanceId();
      }
    });
    try
    {
      localObject = (String)((Future)localObject).get(30000L, TimeUnit.MILLISECONDS);
      return (String)localObject;
    }
    catch (InterruptedException localInterruptedException)
    {
      zzKl().zzLZ().zze("Failed to get app instance id. appId", zzatx.zzfE(paramString), localInterruptedException);
      return null;
    }
    catch (ExecutionException localExecutionException)
    {
      for (;;) {}
    }
    catch (TimeoutException localTimeoutException)
    {
      for (;;) {}
    }
  }
  
  @WorkerThread
  public void zzmR()
  {
    zzKk().zzmR();
  }
  
  public zze zznR()
  {
    return this.zzuP;
  }
  
  void zzob()
  {
    if (!this.zzadP) {
      throw new IllegalStateException("AppMeasurement is not initialized");
    }
  }
  
  @WorkerThread
  boolean zzy(int paramInt1, int paramInt2)
  {
    zzmR();
    if (paramInt1 > paramInt2)
    {
      zzKl().zzLZ().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      return false;
    }
    if (paramInt1 < paramInt2)
    {
      if (zza(paramInt2, zzMC())) {
        zzKl().zzMf().zze("Storage version upgraded. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      }
    }
    else {
      return true;
    }
    zzKl().zzLZ().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    return false;
  }
  
  private class zza
    implements zzatj.zzb
  {
    zzauw.zze zzbuJ;
    List<Long> zzbuK;
    long zzbuL;
    List<zzauw.zzb> zzth;
    
    private zza() {}
    
    private long zza(zzauw.zzb paramzzb)
    {
      return paramzzb.zzbxc.longValue() / 1000L / 60L / 60L;
    }
    
    boolean isEmpty()
    {
      return (this.zzth == null) || (this.zzth.isEmpty());
    }
    
    public boolean zza(long paramLong, zzauw.zzb paramzzb)
    {
      zzac.zzw(paramzzb);
      if (this.zzth == null) {
        this.zzth = new ArrayList();
      }
      if (this.zzbuK == null) {
        this.zzbuK = new ArrayList();
      }
      if ((this.zzth.size() > 0) && (zza((zzauw.zzb)this.zzth.get(0)) != zza(paramzzb))) {
        return false;
      }
      long l = this.zzbuL + paramzzb.zzafB();
      if (l >= zzaue.this.zzKn().zzLo()) {
        return false;
      }
      this.zzbuL = l;
      this.zzth.add(paramzzb);
      this.zzbuK.add(Long.valueOf(paramLong));
      return this.zzth.size() < zzaue.this.zzKn().zzLp();
    }
    
    public void zzb(zzauw.zze paramzze)
    {
      zzac.zzw(paramzze);
      this.zzbuJ = paramzze;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */