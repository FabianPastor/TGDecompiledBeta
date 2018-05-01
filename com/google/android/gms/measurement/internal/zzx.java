package com.google.android.gms.measurement.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwb.zzb;
import com.google.android.gms.internal.zzwc.zza;
import com.google.android.gms.internal.zzwc.zzb;
import com.google.android.gms.internal.zzwc.zzc;
import com.google.android.gms.internal.zzwc.zzd;
import com.google.android.gms.internal.zzwc.zze;
import com.google.android.gms.internal.zzwc.zzg;
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

public class zzx
{
  private static volatile zzx atW;
  private final zzd atX;
  private final zzt atY;
  private final zzq atZ;
  private final zzw aua;
  private final zzag aub;
  private final zzv auc;
  private final AppMeasurement aud;
  private final FirebaseAnalytics aue;
  private final zzal auf;
  private final zze aug;
  private final zzo auh;
  private final zzr aui;
  private final zzad auj;
  private final zzae auk;
  private final zzg aul;
  private final zzac aum;
  private final zzn aun;
  private final zzs auo;
  private final zzai aup;
  private final zzc auq;
  private boolean aur;
  private Boolean aus;
  private long aut;
  private FileLock auu;
  private FileChannel auv;
  private List<Long> auw;
  private int aux;
  private int auy;
  private long auz;
  private final boolean cR;
  private final Context mContext;
  private final com.google.android.gms.common.util.zze zzaql;
  
  zzx(zzab paramzzab)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramzzab);
    this.mContext = paramzzab.mContext;
    this.auz = -1L;
    this.zzaql = paramzzab.zzn(this);
    this.atX = paramzzab.zza(this);
    Object localObject = paramzzab.zzb(this);
    ((zzt)localObject).initialize();
    this.atY = ((zzt)localObject);
    localObject = paramzzab.zzc(this);
    ((zzq)localObject).initialize();
    this.atZ = ((zzq)localObject);
    zzbwb().zzbxc().zzj("App measurement is starting up, version", Long.valueOf(zzbwd().zzbto()));
    zzbwb().zzbxc().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
    zzbwb().zzbxd().log("Debug-level message logging enabled");
    zzbwb().zzbxd().zzj("AppMeasurement singleton hash", Integer.valueOf(System.identityHashCode(this)));
    this.auf = paramzzab.zzj(this);
    localObject = paramzzab.zzq(this);
    ((zzg)localObject).initialize();
    this.aul = ((zzg)localObject);
    localObject = paramzzab.zzr(this);
    ((zzn)localObject).initialize();
    this.aun = ((zzn)localObject);
    if (!zzbwd().zzayi())
    {
      localObject = ((zzn)localObject).zzup();
      if (zzbvx().zznf((String)localObject)) {
        zzbwb().zzbxc().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop firebase.analytics.debug-mode .none.");
      }
    }
    else
    {
      localObject = paramzzab.zzk(this);
      ((zze)localObject).initialize();
      this.aug = ((zze)localObject);
      localObject = paramzzab.zzl(this);
      ((zzo)localObject).initialize();
      this.auh = ((zzo)localObject);
      localObject = paramzzab.zzu(this);
      ((zzc)localObject).initialize();
      this.auq = ((zzc)localObject);
      localObject = paramzzab.zzm(this);
      ((zzr)localObject).initialize();
      this.aui = ((zzr)localObject);
      localObject = paramzzab.zzo(this);
      ((zzad)localObject).initialize();
      this.auj = ((zzad)localObject);
      localObject = paramzzab.zzp(this);
      ((zzae)localObject).initialize();
      this.auk = ((zzae)localObject);
      localObject = paramzzab.zzi(this);
      ((zzac)localObject).initialize();
      this.aum = ((zzac)localObject);
      localObject = paramzzab.zzt(this);
      ((zzai)localObject).initialize();
      this.aup = ((zzai)localObject);
      this.auo = paramzzab.zzs(this);
      this.aud = paramzzab.zzh(this);
      this.aue = paramzzab.zzg(this);
      localObject = paramzzab.zze(this);
      ((zzag)localObject).initialize();
      this.aub = ((zzag)localObject);
      localObject = paramzzab.zzf(this);
      ((zzv)localObject).initialize();
      this.auc = ((zzv)localObject);
      paramzzab = paramzzab.zzd(this);
      paramzzab.initialize();
      this.aua = paramzzab;
      if (this.aux != this.auy) {
        zzbwb().zzbwy().zze("Not all components initialized", Integer.valueOf(this.aux), Integer.valueOf(this.auy));
      }
      this.cR = true;
      if (!this.atX.zzayi())
      {
        if (!(this.mContext.getApplicationContext() instanceof Application)) {
          break label569;
        }
        if (Build.VERSION.SDK_INT < 14) {
          break label553;
        }
        zzbvq().zzbyp();
      }
    }
    for (;;)
    {
      this.aua.zzm(new Runnable()
      {
        public void run()
        {
          zzx.this.start();
        }
      });
      return;
      zzq.zza localzza = zzbwb().zzbxc();
      localObject = String.valueOf(localObject);
      if (((String)localObject).length() != 0) {}
      for (localObject = "To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode ".concat((String)localObject);; localObject = new String("To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode "))
      {
        localzza.log((String)localObject);
        break;
      }
      label553:
      zzbwb().zzbxd().log("Not tracking deep linking pre-ICS");
      continue;
      label569:
      zzbwb().zzbxa().log("Application context is not an Application");
    }
  }
  
  private void zza(zzaa paramzzaa)
  {
    if (paramzzaa == null) {
      throw new IllegalStateException("Component not created");
    }
    if (!paramzzaa.isInitialized()) {
      throw new IllegalStateException("Component not initialized");
    }
  }
  
  private void zza(zzz paramzzz)
  {
    if (paramzzz == null) {
      throw new IllegalStateException("Component not created");
    }
  }
  
  private boolean zza(zzh paramzzh)
  {
    if (paramzzh.arC == null) {}
    Object localObject;
    boolean bool;
    do
    {
      return false;
      localObject = paramzzh.arC.iterator();
      while (((Iterator)localObject).hasNext()) {
        if ("_r".equals((String)((Iterator)localObject).next())) {
          return true;
        }
      }
      bool = zzbvy().zzay(paramzzh.zzctj, paramzzh.mName);
      localObject = zzbvw().zza(zzbyb(), paramzzh.zzctj, false, false, false, false, false);
    } while ((!bool) || (((zze.zza)localObject).art >= zzbwd().zzlq(paramzzh.zzctj)));
    return true;
  }
  
  private zzwc.zza[] zza(String paramString, zzwc.zzg[] paramArrayOfzzg, zzwc.zzb[] paramArrayOfzzb)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    return zzbvp().zza(paramString, paramArrayOfzzb, paramArrayOfzzg);
  }
  
  private boolean zzbyf()
  {
    zzzx();
    zzacj();
    return (zzbvw().zzbwk()) || (!TextUtils.isEmpty(zzbvw().zzbwe()));
  }
  
  @WorkerThread
  private void zzbyg()
  {
    zzzx();
    zzacj();
    if (!zzbyk()) {
      return;
    }
    if ((!zzbxq()) || (!zzbyf()))
    {
      zzbxw().unregister();
      zzbxx().cancel();
      return;
    }
    long l2 = zzbyh();
    if (l2 == 0L)
    {
      zzbxw().unregister();
      zzbxx().cancel();
      return;
    }
    if (!zzbxv().zzagk())
    {
      zzbxw().zzagh();
      zzbxx().cancel();
      return;
    }
    long l3 = zzbwc().atb.get();
    long l4 = zzbwd().zzbve();
    long l1 = l2;
    if (!zzbvx().zzf(l3, l4)) {
      l1 = Math.max(l2, l3 + l4);
    }
    zzbxw().unregister();
    l2 = l1 - zzabz().currentTimeMillis();
    l1 = l2;
    if (l2 <= 0L)
    {
      l1 = zzbwd().zzbvh();
      zzbwc().asZ.set(zzabz().currentTimeMillis());
    }
    zzbwb().zzbxe().zzj("Upload scheduled in approximately ms", Long.valueOf(l1));
    zzbxx().zzx(l1);
  }
  
  private long zzbyh()
  {
    long l3 = zzabz().currentTimeMillis();
    long l1 = zzbwd().zzbvk();
    long l2;
    label54:
    long l6;
    long l5;
    long l4;
    if ((zzbvw().zzbwl()) || (zzbvw().zzbwf()))
    {
      i = 1;
      if (i == 0) {
        break label115;
      }
      l2 = zzbwd().zzbvg();
      l6 = zzbwc().asZ.get();
      l5 = zzbwc().ata.get();
      l4 = Math.max(zzbvw().zzbwi(), zzbvw().zzbwj());
      if (l4 != 0L) {
        break label127;
      }
      l2 = 0L;
    }
    label115:
    label127:
    do
    {
      do
      {
        return l2;
        i = 0;
        break;
        l2 = zzbwd().zzbvf();
        break label54;
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
        if (!zzbvx().zzf(l6, l2)) {
          l1 = l6 + l2;
        }
        l2 = l1;
      } while (l5 == 0L);
      l2 = l1;
    } while (l5 < l4);
    int i = 0;
    for (;;)
    {
      if (i >= zzbwd().zzbvm()) {
        break label295;
      }
      l1 += (1 << i) * zzbwd().zzbvl();
      l2 = l1;
      if (l1 > l5) {
        break;
      }
      i += 1;
    }
    label295:
    return 0L;
  }
  
  public static zzx zzdq(Context paramContext)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramContext);
    com.google.android.gms.common.internal.zzaa.zzy(paramContext.getApplicationContext());
    if (atW == null) {}
    try
    {
      if (atW == null) {
        atW = new zzab(paramContext).zzbyo();
      }
      return atW;
    }
    finally {}
  }
  
  @WorkerThread
  private void zze(AppMetadata paramAppMetadata)
  {
    int k = 1;
    zzzx();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzy(paramAppMetadata);
    com.google.android.gms.common.internal.zzaa.zzib(paramAppMetadata.packageName);
    zza localzza2 = zzbvw().zzlz(paramAppMetadata.packageName);
    String str = zzbwc().zzml(paramAppMetadata.packageName);
    int i = 0;
    zza localzza1;
    int j;
    if (localzza2 == null)
    {
      localzza1 = new zza(this, paramAppMetadata.packageName);
      localzza1.zzlj(zzbwc().zzbxh());
      localzza1.zzll(str);
      i = 1;
      j = i;
      if (!TextUtils.isEmpty(paramAppMetadata.aqZ))
      {
        j = i;
        if (!paramAppMetadata.aqZ.equals(localzza1.zzbth()))
        {
          localzza1.zzlk(paramAppMetadata.aqZ);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramAppMetadata.arh))
      {
        i = j;
        if (!paramAppMetadata.arh.equals(localzza1.zzbtj()))
        {
          localzza1.zzlm(paramAppMetadata.arh);
          i = 1;
        }
      }
      j = i;
      if (paramAppMetadata.arb != 0L)
      {
        j = i;
        if (paramAppMetadata.arb != localzza1.zzbto())
        {
          localzza1.zzay(paramAppMetadata.arb);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramAppMetadata.aii))
      {
        i = j;
        if (!paramAppMetadata.aii.equals(localzza1.zzaaf()))
        {
          localzza1.setAppVersion(paramAppMetadata.aii);
          i = 1;
        }
      }
      if (paramAppMetadata.arg != localzza1.zzbtm())
      {
        localzza1.zzax(paramAppMetadata.arg);
        i = 1;
      }
      j = i;
      if (!TextUtils.isEmpty(paramAppMetadata.ara))
      {
        j = i;
        if (!paramAppMetadata.ara.equals(localzza1.zzbtn()))
        {
          localzza1.zzln(paramAppMetadata.ara);
          j = 1;
        }
      }
      if (paramAppMetadata.arc != localzza1.zzbtp())
      {
        localzza1.zzaz(paramAppMetadata.arc);
        j = 1;
      }
      if (paramAppMetadata.are == localzza1.zzbtq()) {
        break label420;
      }
      localzza1.setMeasurementEnabled(paramAppMetadata.are);
    }
    label420:
    for (i = k;; i = j)
    {
      if (i != 0) {
        zzbvw().zza(localzza1);
      }
      return;
      localzza1 = localzza2;
      if (str.equals(localzza2.zzbti())) {
        break;
      }
      localzza2.zzll(str);
      localzza2.zzlj(zzbwc().zzbxh());
      i = 1;
      localzza1 = localzza2;
      break;
    }
  }
  
  private boolean zzh(String paramString, long paramLong)
  {
    zzbvw().beginTransaction();
    for (;;)
    {
      zzwc.zze localzze;
      int i;
      int k;
      int n;
      int i1;
      int m;
      Object localObject1;
      zzwc.zzc[] arrayOfzzc;
      long l;
      try
      {
        zza localzza = new zza(null);
        zzbvw().zza(paramString, paramLong, this.auz, localzza);
        if (localzza.isEmpty()) {
          break label1673;
        }
        bool1 = false;
        localzze = localzza.auB;
        localzze.awV = new zzwc.zzb[localzza.zzani.size()];
        i = 0;
        k = 0;
        if (k < localzza.zzani.size())
        {
          if (zzbvy().zzax(localzza.auB.zzcs, ((zzwc.zzb)localzza.zzani.get(k)).name))
          {
            zzbwb().zzbxa().zzj("Dropping blacklisted raw event", ((zzwc.zzb)localzza.zzani.get(k)).name);
            if (zzbvx().zznh(localzza.auB.zzcs)) {
              break label1710;
            }
            if (!zzbvx().zzni(localzza.auB.zzcs)) {
              break label1725;
            }
            break label1710;
            if ((j != 0) || ("_err".equals(((zzwc.zzb)localzza.zzani.get(k)).name))) {
              break label1707;
            }
            zzbvx().zza(11, "_ev", ((zzwc.zzb)localzza.zzani.get(k)).name, 0);
            break label1716;
          }
          if (!zzbvy().zzay(localzza.auB.zzcs, ((zzwc.zzb)localzza.zzani.get(k)).name)) {
            break label1704;
          }
          n = 0;
          j = 0;
          if (((zzwc.zzb)localzza.zzani.get(k)).awN == null) {
            ((zzwc.zzb)localzza.zzani.get(k)).awN = new zzwc.zzc[0];
          }
          paramString = ((zzwc.zzb)localzza.zzani.get(k)).awN;
          i1 = paramString.length;
          m = 0;
          if (m < i1)
          {
            localObject1 = paramString[m];
            if ("_c".equals(((zzwc.zzc)localObject1).name))
            {
              ((zzwc.zzc)localObject1).awR = Long.valueOf(1L);
              n = 1;
              break label1731;
            }
            if (!"_r".equals(((zzwc.zzc)localObject1).name)) {
              break label1701;
            }
            ((zzwc.zzc)localObject1).awR = Long.valueOf(1L);
            j = 1;
            break label1731;
          }
          if (n == 0)
          {
            zzbwb().zzbxe().zzj("Marking event as conversion", ((zzwc.zzb)localzza.zzani.get(k)).name);
            paramString = (zzwc.zzc[])Arrays.copyOf(((zzwc.zzb)localzza.zzani.get(k)).awN, ((zzwc.zzb)localzza.zzani.get(k)).awN.length + 1);
            localObject1 = new zzwc.zzc();
            ((zzwc.zzc)localObject1).name = "_c";
            ((zzwc.zzc)localObject1).awR = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzwc.zzb)localzza.zzani.get(k)).awN = paramString;
          }
          if (j == 0)
          {
            zzbwb().zzbxe().zzj("Marking event as real-time", ((zzwc.zzb)localzza.zzani.get(k)).name);
            paramString = (zzwc.zzc[])Arrays.copyOf(((zzwc.zzb)localzza.zzani.get(k)).awN, ((zzwc.zzb)localzza.zzani.get(k)).awN.length + 1);
            localObject1 = new zzwc.zzc();
            ((zzwc.zzc)localObject1).name = "_r";
            ((zzwc.zzc)localObject1).awR = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzwc.zzb)localzza.zzani.get(k)).awN = paramString;
          }
          bool2 = true;
          boolean bool3 = zzal.zzmu(((zzwc.zzb)localzza.zzani.get(k)).name);
          if (zzbvw().zza(zzbyb(), localzza.auB.zzcs, false, false, false, false, true).art > zzbwd().zzlq(localzza.auB.zzcs))
          {
            paramString = (zzwc.zzb)localzza.zzani.get(k);
            j = 0;
            if (j >= paramString.awN.length) {
              break label1740;
            }
            if (!"_r".equals(paramString.awN[j].name)) {
              break label1756;
            }
            localObject1 = new zzwc.zzc[paramString.awN.length - 1];
            if (j > 0) {
              System.arraycopy(paramString.awN, 0, localObject1, 0, j);
            }
            if (j < localObject1.length) {
              System.arraycopy(paramString.awN, j + 1, localObject1, j, localObject1.length - j);
            }
            paramString.awN = ((zzwc.zzc[])localObject1);
            break label1740;
          }
          if ((!bool3) || (zzbvw().zza(zzbyb(), localzza.auB.zzcs, false, false, true, false, false).arr <= zzbwd().zzlp(localzza.auB.zzcs))) {
            break label1811;
          }
          zzbwb().zzbxa().log("Too many conversions. Not logging as conversion.");
          localObject2 = (zzwc.zzb)localzza.zzani.get(k);
          j = 0;
          paramString = null;
          arrayOfzzc = ((zzwc.zzb)localObject2).awN;
          n = arrayOfzzc.length;
          m = 0;
          if (m < n)
          {
            localObject1 = arrayOfzzc[m];
            if ("_c".equals(((zzwc.zzc)localObject1).name))
            {
              paramString = (String)localObject1;
              break label1747;
            }
            if (!"_err".equals(((zzwc.zzc)localObject1).name)) {
              break label1698;
            }
            j = 1;
            break label1747;
          }
          if ((j != 0) && (paramString != null))
          {
            localObject1 = new zzwc.zzc[((zzwc.zzb)localObject2).awN.length - 1];
            j = 0;
            arrayOfzzc = ((zzwc.zzb)localObject2).awN;
            i1 = arrayOfzzc.length;
            m = 0;
            break label1765;
            ((zzwc.zzb)localObject2).awN = ((zzwc.zzc[])localObject1);
            bool1 = bool2;
            localzze.awV[i] = ((zzwc.zzb)localzza.zzani.get(k));
            i += 1;
            break label1716;
          }
          if (paramString != null)
          {
            paramString.name = "_err";
            paramString.awR = Long.valueOf(10L);
            bool1 = bool2;
            continue;
          }
          zzbwb().zzbwy().log("Did not find conversion parameter. Error not tracked");
          break label1811;
        }
        if (i < localzza.zzani.size()) {
          localzze.awV = ((zzwc.zzb[])Arrays.copyOf(localzze.awV, i));
        }
        localzze.axo = zza(localzza.auB.zzcs, localzza.auB.awW, localzze.awV);
        localzze.awY = Long.valueOf(Long.MAX_VALUE);
        localzze.awZ = Long.valueOf(Long.MIN_VALUE);
        i = 0;
        if (i < localzze.awV.length)
        {
          paramString = localzze.awV[i];
          if (paramString.awO.longValue() < localzze.awY.longValue()) {
            localzze.awY = paramString.awO;
          }
          if (paramString.awO.longValue() <= localzze.awZ.longValue()) {
            break label1818;
          }
          localzze.awZ = paramString.awO;
          break label1818;
        }
        localObject1 = localzza.auB.zzcs;
        Object localObject2 = zzbvw().zzlz((String)localObject1);
        if (localObject2 == null)
        {
          zzbwb().zzbwy().log("Bundling raw events w/o app info");
          if (localzze.awV.length > 0)
          {
            localzze.ard = zzbwb().zzbxf();
            paramString = zzbvy().zzmo(localzza.auB.zzcs);
            if ((paramString != null) && (paramString.awC != null)) {
              break label1661;
            }
            zzbwb().zzbxa().log("Did not find measurement config or missing version info");
            zzbvw().zza(localzze, bool1);
          }
          zzbvw().zzaf(localzza.auC);
          zzbvw().zzmg((String)localObject1);
          zzbvw().setTransactionSuccessful();
          i = localzze.awV.length;
          if (i <= 0) {
            break label1827;
          }
          bool1 = true;
          return bool1;
        }
        if (localzze.awV.length <= 0) {
          continue;
        }
        paramLong = ((zza)localObject2).zzbtl();
        if (paramLong != 0L)
        {
          paramString = Long.valueOf(paramLong);
          localzze.axb = paramString;
          l = ((zza)localObject2).zzbtk();
          if (l != 0L) {
            break label1689;
          }
          if (paramLong == 0L) {
            break label1656;
          }
          paramString = Long.valueOf(paramLong);
          localzze.axa = paramString;
          ((zza)localObject2).zzbtu();
          localzze.axm = Integer.valueOf((int)((zza)localObject2).zzbtr());
          ((zza)localObject2).zzav(localzze.awY.longValue());
          ((zza)localObject2).zzaw(localzze.awZ.longValue());
          zzbvw().zza((zza)localObject2);
          continue;
        }
        paramString = null;
      }
      finally
      {
        zzbvw().endTransaction();
      }
      continue;
      label1656:
      paramString = null;
      continue;
      label1661:
      localzze.axt = paramString.awC;
      continue;
      label1673:
      zzbvw().setTransactionSuccessful();
      zzbvw().endTransaction();
      return false;
      label1689:
      paramLong = l;
      continue;
      break label1802;
      label1698:
      break label1747;
      label1701:
      break label1731;
      label1704:
      continue;
      label1707:
      break label1716;
      label1710:
      int j = 1;
      continue;
      label1716:
      k += 1;
      continue;
      label1725:
      j = 0;
      continue;
      label1731:
      m += 1;
      continue;
      label1740:
      boolean bool2 = bool1;
      continue;
      label1747:
      m += 1;
      continue;
      label1756:
      j += 1;
      continue;
      for (;;)
      {
        label1765:
        if (m >= i1) {
          break label1809;
        }
        zzwc.zzc localzzc = arrayOfzzc[m];
        if (localzzc == paramString) {
          break;
        }
        n = j + 1;
        localObject1[j] = localzzc;
        j = n;
        label1802:
        m += 1;
      }
      label1809:
      continue;
      label1811:
      boolean bool1 = bool2;
      continue;
      label1818:
      i += 1;
      continue;
      label1827:
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
    zzzx();
    zzacj();
    if (zzbwd().zzbut()) {
      return false;
    }
    Boolean localBoolean = zzbwd().zzbuu();
    if (localBoolean != null) {
      bool = localBoolean.booleanValue();
    }
    for (;;)
    {
      return zzbwc().zzch(bool);
      if (!zzbwd().zzatu()) {
        bool = true;
      }
    }
  }
  
  @WorkerThread
  protected void start()
  {
    zzzx();
    zzbvw().zzbwg();
    if (zzbwc().asZ.get() == 0L) {
      zzbwc().asZ.set(zzabz().currentTimeMillis());
    }
    if (!zzbxq()) {
      if (isEnabled())
      {
        if (!zzbvx().zzez("android.permission.INTERNET")) {
          zzbwb().zzbwy().log("App is missing INTERNET permission");
        }
        if (!zzbvx().zzez("android.permission.ACCESS_NETWORK_STATE")) {
          zzbwb().zzbwy().log("App is missing ACCESS_NETWORK_STATE permission");
        }
        if (!zzbwd().zzayi())
        {
          if (!zzu.zzh(getContext(), false)) {
            zzbwb().zzbwy().log("AppMeasurementReceiver not registered/enabled");
          }
          if (!zzaf.zzi(getContext(), false)) {
            zzbwb().zzbwy().log("AppMeasurementService not registered/enabled");
          }
        }
        zzbwb().zzbwy().log("Uploading is not possible. App measurement disabled");
      }
    }
    label269:
    label332:
    for (;;)
    {
      zzbyg();
      return;
      String str;
      if ((!zzbwd().zzayi()) && (!TextUtils.isEmpty(zzbvr().zzbth())))
      {
        str = zzbwc().zzbxk();
        if (str != null) {
          break label269;
        }
        zzbwc().zzmm(zzbvr().zzbth());
      }
      for (;;)
      {
        if ((zzbwd().zzayi()) || (TextUtils.isEmpty(zzbvr().zzbth()))) {
          break label332;
        }
        zzbvq().zzbyq();
        break;
        if (!str.equals(zzbvr().zzbth()))
        {
          zzbwb().zzbxc().log("Rechecking which service to use due to a GMP App Id change");
          zzbwc().zzbxm();
          this.auk.disconnect();
          this.auk.zzadl();
          zzbwc().zzmm(zzbvr().zzbth());
        }
      }
    }
  }
  
  @WorkerThread
  int zza(FileChannel paramFileChannel)
  {
    zzzx();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen())) {
      zzbwb().zzbwy().log("Bad chanel to read from");
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
          zzbwb().zzbxa().zzj("Unexpected data length. Bytes read", Integer.valueOf(i));
          return 0;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzbwb().zzbwy().zzj("Failed to read from channel", paramFileChannel);
        return 0;
      }
    }
    localByteBuffer.flip();
    int i = localByteBuffer.getInt();
    return i;
  }
  
  @WorkerThread
  protected void zza(int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte)
  {
    int i = 0;
    zzzx();
    zzacj();
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    paramArrayOfByte = this.auw;
    this.auw = null;
    if (((paramInt == 200) || (paramInt == 204)) && (paramThrowable == null))
    {
      zzbwc().asZ.set(zzabz().currentTimeMillis());
      zzbwc().ata.set(0L);
      zzbyg();
      zzbwb().zzbxe().zze("Successful upload. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      zzbvw().beginTransaction();
      try
      {
        paramThrowable = paramArrayOfByte.iterator();
        while (paramThrowable.hasNext())
        {
          paramArrayOfByte = (Long)paramThrowable.next();
          zzbvw().zzbj(paramArrayOfByte.longValue());
        }
      }
      finally
      {
        zzbvw().endTransaction();
      }
      zzbvw().endTransaction();
      if ((zzbxv().zzagk()) && (zzbyf()))
      {
        zzbye();
        return;
      }
      this.auz = -1L;
      zzbyg();
      return;
    }
    zzbwb().zzbxe().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzbwc().ata.set(zzabz().currentTimeMillis());
    if ((paramInt == 503) || (paramInt == 429)) {
      i = 1;
    }
    if (i != 0) {
      zzbwc().atb.set(zzabz().currentTimeMillis());
    }
    zzbyg();
  }
  
  @WorkerThread
  void zza(AppMetadata paramAppMetadata, long paramLong)
  {
    Object localObject2 = zzbvw().zzlz(paramAppMetadata.packageName);
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((zza)localObject2).zzbth() != null)
      {
        localObject1 = localObject2;
        if (!((zza)localObject2).zzbth().equals(paramAppMetadata.aqZ))
        {
          zzbwb().zzbxa().log("New GMP App Id passed in. Removing cached database data.");
          zzbvw().zzme(((zza)localObject2).zzup());
          localObject1 = null;
        }
      }
    }
    if ((localObject1 != null) && (((zza)localObject1).zzaaf() != null) && (!((zza)localObject1).zzaaf().equals(paramAppMetadata.aii)))
    {
      localObject2 = new Bundle();
      ((Bundle)localObject2).putString("_pv", ((zza)localObject1).zzaaf());
      zzb(new EventParcel("_au", new EventParams((Bundle)localObject2), "auto", paramLong), paramAppMetadata);
    }
  }
  
  void zza(zzh paramzzh, AppMetadata paramAppMetadata)
  {
    zzzx();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzy(paramzzh);
    com.google.android.gms.common.internal.zzaa.zzy(paramAppMetadata);
    com.google.android.gms.common.internal.zzaa.zzib(paramzzh.zzctj);
    com.google.android.gms.common.internal.zzaa.zzbt(paramzzh.zzctj.equals(paramAppMetadata.packageName));
    zzwc.zze localzze = new zzwc.zze();
    localzze.awU = Integer.valueOf(1);
    localzze.axc = "android";
    localzze.zzcs = paramAppMetadata.packageName;
    localzze.ara = paramAppMetadata.ara;
    localzze.aii = paramAppMetadata.aii;
    localzze.axp = Integer.valueOf((int)paramAppMetadata.arg);
    localzze.axg = Long.valueOf(paramAppMetadata.arb);
    localzze.aqZ = paramAppMetadata.aqZ;
    Object localObject1;
    if (paramAppMetadata.arc == 0L)
    {
      localObject1 = null;
      localzze.axl = ((Long)localObject1);
      localObject1 = zzbwc().zzmk(paramAppMetadata.packageName);
      if ((localObject1 == null) || (TextUtils.isEmpty((CharSequence)((Pair)localObject1).first))) {
        break label599;
      }
      localzze.axi = ((String)((Pair)localObject1).first);
      localzze.axj = ((Boolean)((Pair)localObject1).second);
    }
    label599:
    while (zzbvs().zzdp(this.mContext))
    {
      localzze.axd = zzbvs().zzvt();
      localzze.zzdb = zzbvs().zzbws();
      localzze.axf = Integer.valueOf((int)zzbvs().zzbwt());
      localzze.axe = zzbvs().zzbwu();
      localzze.axh = null;
      localzze.awX = null;
      localzze.awY = null;
      localzze.awZ = null;
      localObject2 = zzbvw().zzlz(paramAppMetadata.packageName);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new zza(this, paramAppMetadata.packageName);
        ((zza)localObject1).zzlj(zzbwc().zzbxh());
        ((zza)localObject1).zzlm(paramAppMetadata.arh);
        ((zza)localObject1).zzlk(paramAppMetadata.aqZ);
        ((zza)localObject1).zzll(zzbwc().zzml(paramAppMetadata.packageName));
        ((zza)localObject1).zzba(0L);
        ((zza)localObject1).zzav(0L);
        ((zza)localObject1).zzaw(0L);
        ((zza)localObject1).setAppVersion(paramAppMetadata.aii);
        ((zza)localObject1).zzax(paramAppMetadata.arg);
        ((zza)localObject1).zzln(paramAppMetadata.ara);
        ((zza)localObject1).zzay(paramAppMetadata.arb);
        ((zza)localObject1).zzaz(paramAppMetadata.arc);
        ((zza)localObject1).setMeasurementEnabled(paramAppMetadata.are);
        zzbvw().zza((zza)localObject1);
      }
      localzze.axk = ((zza)localObject1).zzazn();
      localzze.arh = ((zza)localObject1).zzbtj();
      paramAppMetadata = zzbvw().zzly(paramAppMetadata.packageName);
      localzze.awW = new zzwc.zzg[paramAppMetadata.size()];
      int i = 0;
      while (i < paramAppMetadata.size())
      {
        localObject1 = new zzwc.zzg();
        localzze.awW[i] = localObject1;
        ((zzwc.zzg)localObject1).name = ((zzak)paramAppMetadata.get(i)).mName;
        ((zzwc.zzg)localObject1).axx = Long.valueOf(((zzak)paramAppMetadata.get(i)).avX);
        zzbvx().zza((zzwc.zzg)localObject1, ((zzak)paramAppMetadata.get(i)).zzcyd);
        i += 1;
      }
      localObject1 = Long.valueOf(paramAppMetadata.arc);
      break;
    }
    Object localObject2 = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
    if (localObject2 == null)
    {
      zzbwb().zzbxa().log("null secure ID");
      localObject1 = "null";
    }
    for (;;)
    {
      localzze.axs = ((String)localObject1);
      break;
      localObject1 = localObject2;
      if (((String)localObject2).isEmpty())
      {
        zzbwb().zzbxa().log("empty secure ID");
        localObject1 = localObject2;
      }
    }
    try
    {
      long l = zzbvw().zza(localzze);
      zzbvw().zza(paramzzh, l, zza(paramzzh));
      return;
    }
    catch (IOException paramzzh)
    {
      zzbwb().zzbwy().zzj("Data loss. Failed to insert raw event metadata", paramzzh);
    }
  }
  
  @WorkerThread
  boolean zza(int paramInt, FileChannel paramFileChannel)
  {
    boolean bool = true;
    zzzx();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen()))
    {
      zzbwb().zzbwy().log("Bad chanel to read from");
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
          zzbwb().zzbwy().zzj("Error writing to channel. Bytes written", Long.valueOf(paramFileChannel.size()));
          return true;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzbwb().zzbwy().zzj("Failed to write to channel", paramFileChannel);
      }
    }
    return false;
  }
  
  @WorkerThread
  public byte[] zza(@NonNull EventParcel paramEventParcel, @Size(min=1L) String paramString)
  {
    zzacj();
    zzzx();
    zzbyc();
    com.google.android.gms.common.internal.zzaa.zzy(paramEventParcel);
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzwc.zzd localzzd = new zzwc.zzd();
    zzbvw().beginTransaction();
    zza localzza;
    zzwc.zze localzze;
    try
    {
      localzza = zzbvw().zzlz(paramString);
      if (localzza == null)
      {
        zzbwb().zzbxd().zzj("Log and bundle not available. package_name", paramString);
        return new byte[0];
      }
      if (!localzza.zzbtq())
      {
        zzbwb().zzbxd().zzj("Log and bundle disabled. package_name", paramString);
        return new byte[0];
      }
      localzze = new zzwc.zze();
      localzzd.awS = new zzwc.zze[] { localzze };
      localzze.awU = Integer.valueOf(1);
      localzze.axc = "android";
      localzze.zzcs = localzza.zzup();
      localzze.ara = localzza.zzbtn();
      localzze.aii = localzza.zzaaf();
      localzze.axp = Integer.valueOf((int)localzza.zzbtm());
      localzze.axg = Long.valueOf(localzza.zzbto());
      localzze.aqZ = localzza.zzbth();
      localzze.axl = Long.valueOf(localzza.zzbtp());
      Object localObject1 = zzbwc().zzmk(localzza.zzup());
      if ((localObject1 != null) && (!TextUtils.isEmpty((CharSequence)((Pair)localObject1).first)))
      {
        localzze.axi = ((String)((Pair)localObject1).first);
        localzze.axj = ((Boolean)((Pair)localObject1).second);
      }
      localzze.axd = zzbvs().zzvt();
      localzze.zzdb = zzbvs().zzbws();
      localzze.axf = Integer.valueOf((int)zzbvs().zzbwt());
      localzze.axe = zzbvs().zzbwu();
      localzze.axk = localzza.zzazn();
      localzze.arh = localzza.zzbtj();
      localObject1 = zzbvw().zzly(localzza.zzup());
      localzze.awW = new zzwc.zzg[((List)localObject1).size()];
      int i = 0;
      while (i < ((List)localObject1).size())
      {
        localObject2 = new zzwc.zzg();
        localzze.awW[i] = localObject2;
        ((zzwc.zzg)localObject2).name = ((zzak)((List)localObject1).get(i)).mName;
        ((zzwc.zzg)localObject2).axx = Long.valueOf(((zzak)((List)localObject1).get(i)).avX);
        zzbvx().zza((zzwc.zzg)localObject2, ((zzak)((List)localObject1).get(i)).zzcyd);
        i += 1;
      }
      localObject1 = paramEventParcel.arJ.zzbww();
      if ("_iap".equals(paramEventParcel.name))
      {
        ((Bundle)localObject1).putLong("_c", 1L);
        zzbwb().zzbxd().log("Marking in-app purchase as real-time");
        ((Bundle)localObject1).putLong("_r", 1L);
      }
      ((Bundle)localObject1).putString("_o", paramEventParcel.arK);
      if (zzbvx().zznf(localzze.zzcs))
      {
        zzbvx().zza((Bundle)localObject1, "_dbg", Long.valueOf(1L));
        zzbvx().zza((Bundle)localObject1, "_r", Long.valueOf(1L));
      }
      Object localObject2 = zzbvw().zzap(paramString, paramEventParcel.name);
      if (localObject2 == null)
      {
        localObject2 = new zzi(paramString, paramEventParcel.name, 1L, 0L, paramEventParcel.arL);
        zzbvw().zza((zzi)localObject2);
        l1 = 0L;
      }
      for (;;)
      {
        paramEventParcel = new zzh(this, paramEventParcel.arK, paramString, paramEventParcel.name, paramEventParcel.arL, l1, (Bundle)localObject1);
        paramString = new zzwc.zzb();
        localzze.awV = new zzwc.zzb[] { paramString };
        paramString.awO = Long.valueOf(paramEventParcel.vO);
        paramString.name = paramEventParcel.mName;
        paramString.awP = Long.valueOf(paramEventParcel.arB);
        paramString.awN = new zzwc.zzc[paramEventParcel.arC.size()];
        localObject1 = paramEventParcel.arC.iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject3 = (String)((Iterator)localObject1).next();
          localObject2 = new zzwc.zzc();
          paramString.awN[i] = localObject2;
          ((zzwc.zzc)localObject2).name = ((String)localObject3);
          localObject3 = paramEventParcel.arC.get((String)localObject3);
          zzbvx().zza((zzwc.zzc)localObject2, localObject3);
          i += 1;
        }
        l1 = ((zzi)localObject2).arF;
        localObject2 = ((zzi)localObject2).zzbl(paramEventParcel.arL).zzbwv();
        zzbvw().zza((zzi)localObject2);
      }
      localzze.axo = zza(localzza.zzup(), localzze.awW, localzze.awV);
    }
    finally
    {
      zzbvw().endTransaction();
    }
    localzze.awY = paramString.awO;
    localzze.awZ = paramString.awO;
    long l1 = localzza.zzbtl();
    long l2;
    if (l1 != 0L)
    {
      paramEventParcel = Long.valueOf(l1);
      localzze.axb = paramEventParcel;
      l2 = localzza.zzbtk();
      if (l2 != 0L) {
        break label1146;
      }
    }
    for (;;)
    {
      if (l1 != 0L) {}
      for (paramEventParcel = Long.valueOf(l1);; paramEventParcel = null)
      {
        localzze.axa = paramEventParcel;
        localzza.zzbtu();
        localzze.axm = Integer.valueOf((int)localzza.zzbtr());
        localzze.axh = Long.valueOf(zzbwd().zzbto());
        localzze.awX = Long.valueOf(zzabz().currentTimeMillis());
        localzze.axn = Boolean.TRUE;
        localzza.zzav(localzze.awY.longValue());
        localzza.zzaw(localzze.awZ.longValue());
        zzbvw().zza(localzza);
        zzbvw().setTransactionSuccessful();
        zzbvw().endTransaction();
        try
        {
          paramEventParcel = new byte[localzzd.cz()];
          paramString = zzart.zzbe(paramEventParcel);
          localzzd.zza(paramString);
          paramString.cm();
          paramEventParcel = zzbvx().zzk(paramEventParcel);
          return paramEventParcel;
        }
        catch (IOException paramEventParcel)
        {
          zzbwb().zzbwy().zzj("Data loss. Failed to bundle and serialize", paramEventParcel);
          return null;
        }
        paramEventParcel = null;
        break;
      }
      label1146:
      l1 = l2;
    }
  }
  
  void zzaby()
  {
    zzbwd().zzayi();
  }
  
  public com.google.android.gms.common.util.zze zzabz()
  {
    return this.zzaql;
  }
  
  void zzacj()
  {
    if (!this.cR) {
      throw new IllegalStateException("AppMeasurement is not initialized");
    }
  }
  
  protected void zzag(List<Long> paramList)
  {
    if (!paramList.isEmpty()) {}
    for (boolean bool = true;; bool = false)
    {
      com.google.android.gms.common.internal.zzaa.zzbt(bool);
      if (this.auw == null) {
        break;
      }
      zzbwb().zzbwy().log("Set uploading progress before finishing the previous upload");
      return;
    }
    this.auw = new ArrayList(paramList);
  }
  
  public void zzaw(boolean paramBoolean)
  {
    zzbyg();
  }
  
  @WorkerThread
  void zzb(AppMetadata paramAppMetadata, long paramLong)
  {
    zzzx();
    zzacj();
    Bundle localBundle = new Bundle();
    localBundle.putLong("_c", 1L);
    localBundle.putLong("_r", 1L);
    localBundle.putLong("_uwa", 0L);
    localBundle.putLong("_pfo", 0L);
    localBundle.putLong("_sys", 0L);
    localBundle.putLong("_sysu", 0L);
    PackageManager localPackageManager = getContext().getPackageManager();
    if (localPackageManager == null) {
      zzbwb().zzbwy().log("PackageManager is null, first open report might be inaccurate");
    }
    for (;;)
    {
      long l = zzbvw().zzmf(paramAppMetadata.packageName);
      if (l >= 0L) {
        localBundle.putLong("_pfo", l);
      }
      zzb(new EventParcel("_f", new EventParams(localBundle), "auto", paramLong), paramAppMetadata);
      return;
      try
      {
        localObject1 = localPackageManager.getPackageInfo(paramAppMetadata.packageName, 0);
        if ((localObject1 != null) && (((PackageInfo)localObject1).firstInstallTime != 0L) && (((PackageInfo)localObject1).firstInstallTime != ((PackageInfo)localObject1).lastUpdateTime)) {
          localBundle.putLong("_uwa", 1L);
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        try
        {
          Object localObject1 = localPackageManager.getApplicationInfo(paramAppMetadata.packageName, 0);
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
          zzbwb().zzbwy().zzj("Package info is null, first open report might be inaccurate", localNameNotFoundException1);
          Object localObject2 = null;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          for (;;)
          {
            zzbwb().zzbwy().zzj("Application info is null, first open report might be inaccurate", localNameNotFoundException2);
            Object localObject3 = null;
          }
        }
      }
    }
  }
  
  @WorkerThread
  void zzb(EventParcel paramEventParcel, AppMetadata paramAppMetadata)
  {
    long l2 = System.nanoTime();
    zzzx();
    zzacj();
    String str = paramAppMetadata.packageName;
    com.google.android.gms.common.internal.zzaa.zzib(str);
    if (!zzal.zzc(paramEventParcel, paramAppMetadata)) {
      return;
    }
    if ((!paramAppMetadata.are) && (!"_in".equals(paramEventParcel.name)))
    {
      zze(paramAppMetadata);
      return;
    }
    if (zzbvy().zzax(str, paramEventParcel.name))
    {
      zzbwb().zzbxa().zzj("Dropping blacklisted event", paramEventParcel.name);
      if ((zzbvx().zznh(str)) || (zzbvx().zzni(str))) {}
      for (int i = 1;; i = 0)
      {
        if ((i == 0) && (!"_err".equals(paramEventParcel.name))) {
          zzbvx().zza(11, "_ev", paramEventParcel.name, 0);
        }
        if (i == 0) {
          break;
        }
        paramEventParcel = zzbvw().zzlz(str);
        if (paramEventParcel == null) {
          break;
        }
        l1 = Math.max(paramEventParcel.zzbtt(), paramEventParcel.zzbts());
        if (Math.abs(zzabz().currentTimeMillis() - l1) <= zzbwd().zzbux()) {
          break;
        }
        zzbwb().zzbxd().log("Fetching config for blacklisted app");
        zzb(paramEventParcel);
        return;
      }
    }
    if (zzbwb().zzbi(2)) {
      zzbwb().zzbxe().zzj("Logging event", paramEventParcel);
    }
    zzbvw().beginTransaction();
    Bundle localBundle;
    boolean bool1;
    boolean bool2;
    for (;;)
    {
      try
      {
        localBundle = paramEventParcel.arJ.zzbww();
        zze(paramAppMetadata);
        double d1;
        Object localObject2;
        if (("_iap".equals(paramEventParcel.name)) || ("ecommerce_purchase".equals(paramEventParcel.name)))
        {
          localObject1 = localBundle.getString("currency");
          if (!"ecommerce_purchase".equals(paramEventParcel.name)) {
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
              localObject2 = zzbvw().zzar(str, (String)localObject1);
              if ((localObject2 != null) && ((((zzak)localObject2).zzcyd instanceof Long))) {
                break label775;
              }
              zzbvw().zzz(str, zzbwd().zzls(str) - 1);
              localObject1 = new zzak(str, (String)localObject1, zzabz().currentTimeMillis(), Long.valueOf(l1));
              if (!zzbvw().zza((zzak)localObject1))
              {
                zzbwb().zzbwy().zze("Too many unique user properties are set. Ignoring user property.", ((zzak)localObject1).mName, ((zzak)localObject1).zzcyd);
                zzbvx().zza(9, null, null, 0);
              }
            }
          }
        }
        bool1 = zzal.zzmu(paramEventParcel.name);
        bool2 = "_err".equals(paramEventParcel.name);
        localObject1 = zzbvw().zza(zzbyb(), str, true, bool1, false, bool2, false);
        l1 = ((zze.zza)localObject1).arq - zzbwd().zzbul();
        if (l1 <= 0L) {
          break;
        }
        if (l1 % 1000L == 1L) {
          zzbwb().zzbwy().zzj("Data loss. Too many events logged. count", Long.valueOf(((zze.zza)localObject1).arq));
        }
        zzbvx().zza(16, "_ev", paramEventParcel.name, 0);
        zzbvw().setTransactionSuccessful();
        return;
        zzbwb().zzbxa().zzj("Data lost. Currency value is too big", Double.valueOf(d1));
        zzbvw().setTransactionSuccessful();
        return;
        l1 = localBundle.getLong("value");
        continue;
        localObject1 = new String((String)localObject1);
        continue;
        l3 = ((Long)((zzak)localObject2).zzcyd).longValue();
      }
      finally
      {
        zzbvw().endTransaction();
      }
      label775:
      long l3;
      localObject1 = new zzak(str, (String)localObject1, zzabz().currentTimeMillis(), Long.valueOf(l1 + l3));
    }
    if (bool1)
    {
      l1 = ((zze.zza)localObject1).arp - zzbwd().zzbum();
      if (l1 > 0L)
      {
        if (l1 % 1000L == 1L) {
          zzbwb().zzbwy().zzj("Data loss. Too many public events logged. count", Long.valueOf(((zze.zza)localObject1).arp));
        }
        zzbvx().zza(16, "_ev", paramEventParcel.name, 0);
        zzbvw().setTransactionSuccessful();
        zzbvw().endTransaction();
        return;
      }
    }
    if (bool2)
    {
      l1 = ((zze.zza)localObject1).ars - zzbwd().zzlo(paramAppMetadata.packageName);
      if (l1 > 0L)
      {
        if (l1 == 1L) {
          zzbwb().zzbwy().zzj("Too many error events logged. count", Long.valueOf(((zze.zza)localObject1).ars));
        }
        zzbvw().setTransactionSuccessful();
        zzbvw().endTransaction();
        return;
      }
    }
    zzbvx().zza(localBundle, "_o", paramEventParcel.arK);
    if (zzbvx().zznf(str))
    {
      zzbvx().zza(localBundle, "_dbg", Long.valueOf(1L));
      zzbvx().zza(localBundle, "_r", Long.valueOf(1L));
    }
    long l1 = zzbvw().zzma(str);
    if (l1 > 0L) {
      zzbwb().zzbxa().zzj("Data lost. Too many events stored on disk, deleted", Long.valueOf(l1));
    }
    paramEventParcel = new zzh(this, paramEventParcel.arK, str, paramEventParcel.name, paramEventParcel.arL, 0L, localBundle);
    Object localObject1 = zzbvw().zzap(str, paramEventParcel.mName);
    if (localObject1 == null)
    {
      l1 = zzbvw().zzmh(str);
      zzbwd().zzbuk();
      if (l1 >= 500L)
      {
        zzbwb().zzbwy().zze("Too many event names used, ignoring event. name, supported count", paramEventParcel.mName, Integer.valueOf(zzbwd().zzbuk()));
        zzbvx().zza(8, null, null, 0);
        zzbvw().endTransaction();
        return;
      }
    }
    for (localObject1 = new zzi(str, paramEventParcel.mName, 0L, 0L, paramEventParcel.vO);; localObject1 = ((zzi)localObject1).zzbl(paramEventParcel.vO))
    {
      zzbvw().zza((zzi)localObject1);
      zza(paramEventParcel, paramAppMetadata);
      zzbvw().setTransactionSuccessful();
      if (zzbwb().zzbi(2)) {
        zzbwb().zzbxe().zzj("Event recorded", paramEventParcel);
      }
      zzbvw().endTransaction();
      zzbyg();
      zzbwb().zzbxe().zzj("Background event processing time, ms", Long.valueOf((System.nanoTime() - l2 + 500000L) / 1000000L));
      return;
      paramEventParcel = paramEventParcel.zza(this, ((zzi)localObject1).arF);
    }
  }
  
  @WorkerThread
  void zzb(EventParcel paramEventParcel, String paramString)
  {
    zza localzza = zzbvw().zzlz(paramString);
    if ((localzza == null) || (TextUtils.isEmpty(localzza.zzaaf())))
    {
      zzbwb().zzbxd().zzj("No app data available; dropping event", paramString);
      return;
    }
    try
    {
      String str = getContext().getPackageManager().getPackageInfo(paramString, 0).versionName;
      if ((localzza.zzaaf() != null) && (!localzza.zzaaf().equals(str)))
      {
        zzbwb().zzbxa().zzj("App version does not match; dropping event", paramString);
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (!"_ui".equals(paramEventParcel.name)) {
        zzbwb().zzbxa().zzj("Could not find package", paramString);
      }
      zzb(paramEventParcel, new AppMetadata(paramString, localzza.zzbth(), localzza.zzaaf(), localzza.zzbtm(), localzza.zzbtn(), localzza.zzbto(), localzza.zzbtp(), null, localzza.zzbtq(), false, localzza.zzbtj()));
    }
  }
  
  /* Error */
  @WorkerThread
  void zzb(UserAttributeParcel paramUserAttributeParcel, AppMetadata paramAppMetadata)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_0
    //   4: istore_3
    //   5: aload_0
    //   6: invokevirtual 500	com/google/android/gms/measurement/internal/zzx:zzzx	()V
    //   9: aload_0
    //   10: invokevirtual 503	com/google/android/gms/measurement/internal/zzx:zzacj	()V
    //   13: aload_2
    //   14: getfield 685	com/google/android/gms/measurement/internal/AppMetadata:aqZ	Ljava/lang/String;
    //   17: invokestatic 515	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   20: ifeq +4 -> 24
    //   23: return
    //   24: aload_2
    //   25: getfield 745	com/google/android/gms/measurement/internal/AppMetadata:are	Z
    //   28: ifne +9 -> 37
    //   31: aload_0
    //   32: aload_2
    //   33: invokespecial 1479	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   41: aload_1
    //   42: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   45: invokevirtual 1644	com/google/android/gms/measurement/internal/zzal:zzmy	(Ljava/lang/String;)I
    //   48: istore 5
    //   50: iload 5
    //   52: ifeq +53 -> 105
    //   55: aload_0
    //   56: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   59: aload_1
    //   60: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   63: aload_0
    //   64: invokevirtual 142	com/google/android/gms/measurement/internal/zzx:zzbwd	()Lcom/google/android/gms/measurement/internal/zzd;
    //   67: invokevirtual 1647	com/google/android/gms/measurement/internal/zzd:zzbue	()I
    //   70: iconst_1
    //   71: invokevirtual 1650	com/google/android/gms/measurement/internal/zzal:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   74: astore_2
    //   75: aload_1
    //   76: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   79: ifnull +11 -> 90
    //   82: aload_1
    //   83: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   86: invokevirtual 393	java/lang/String:length	()I
    //   89: istore_3
    //   90: aload_0
    //   91: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   94: iload 5
    //   96: ldc_w 813
    //   99: aload_2
    //   100: iload_3
    //   101: invokevirtual 816	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   104: return
    //   105: aload_0
    //   106: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   109: aload_1
    //   110: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   113: aload_1
    //   114: invokevirtual 1653	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   117: invokevirtual 1656	com/google/android/gms/measurement/internal/zzal:zzm	(Ljava/lang/String;Ljava/lang/Object;)I
    //   120: istore 5
    //   122: iload 5
    //   124: ifeq +75 -> 199
    //   127: aload_0
    //   128: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   131: aload_1
    //   132: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   135: aload_0
    //   136: invokevirtual 142	com/google/android/gms/measurement/internal/zzx:zzbwd	()Lcom/google/android/gms/measurement/internal/zzd;
    //   139: invokevirtual 1647	com/google/android/gms/measurement/internal/zzd:zzbue	()I
    //   142: iconst_1
    //   143: invokevirtual 1650	com/google/android/gms/measurement/internal/zzal:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   146: astore_2
    //   147: aload_1
    //   148: invokevirtual 1653	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   151: astore_1
    //   152: iload 4
    //   154: istore_3
    //   155: aload_1
    //   156: ifnull +28 -> 184
    //   159: aload_1
    //   160: instanceof 386
    //   163: ifne +13 -> 176
    //   166: iload 4
    //   168: istore_3
    //   169: aload_1
    //   170: instanceof 1155
    //   173: ifeq +11 -> 184
    //   176: aload_1
    //   177: invokestatic 389	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   180: invokevirtual 393	java/lang/String:length	()I
    //   183: istore_3
    //   184: aload_0
    //   185: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   188: iload 5
    //   190: ldc_w 813
    //   193: aload_2
    //   194: iload_3
    //   195: invokevirtual 816	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   198: return
    //   199: aload_0
    //   200: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   203: aload_1
    //   204: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   207: aload_1
    //   208: invokevirtual 1653	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   211: invokevirtual 1659	com/google/android/gms/measurement/internal/zzal:zzn	(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    //   214: astore 7
    //   216: aload 7
    //   218: ifnull -195 -> 23
    //   221: new 1217	com/google/android/gms/measurement/internal/zzak
    //   224: dup
    //   225: aload_2
    //   226: getfield 661	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   229: aload_1
    //   230: getfield 1641	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   233: aload_1
    //   234: getfield 1662	com/google/android/gms/measurement/internal/UserAttributeParcel:avT	J
    //   237: aload 7
    //   239: invokespecial 1556	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   242: astore_1
    //   243: aload_0
    //   244: invokevirtual 132	com/google/android/gms/measurement/internal/zzx:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   247: invokevirtual 169	com/google/android/gms/measurement/internal/zzq:zzbxd	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   250: ldc_w 1664
    //   253: aload_1
    //   254: getfield 1218	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   257: aload 7
    //   259: invokevirtual 356	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   262: aload_0
    //   263: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   266: invokevirtual 762	com/google/android/gms/measurement/internal/zze:beginTransaction	()V
    //   269: aload_0
    //   270: aload_2
    //   271: invokespecial 1479	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   274: aload_0
    //   275: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   278: aload_1
    //   279: invokevirtual 1559	com/google/android/gms/measurement/internal/zze:zza	(Lcom/google/android/gms/measurement/internal/zzak;)Z
    //   282: istore 6
    //   284: aload_0
    //   285: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   288: invokevirtual 922	com/google/android/gms/measurement/internal/zze:setTransactionSuccessful	()V
    //   291: iload 6
    //   293: ifeq +32 -> 325
    //   296: aload_0
    //   297: invokevirtual 132	com/google/android/gms/measurement/internal/zzx:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   300: invokevirtual 169	com/google/android/gms/measurement/internal/zzq:zzbxd	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   303: ldc_w 1666
    //   306: aload_1
    //   307: getfield 1218	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   310: aload_1
    //   311: getfield 1228	com/google/android/gms/measurement/internal/zzak:zzcyd	Ljava/lang/Object;
    //   314: invokevirtual 356	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload_0
    //   318: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   321: invokevirtual 925	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   324: return
    //   325: aload_0
    //   326: invokevirtual 132	com/google/android/gms/measurement/internal/zzx:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   329: invokevirtual 351	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   332: ldc_w 1561
    //   335: aload_1
    //   336: getfield 1218	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   339: aload_1
    //   340: getfield 1228	com/google/android/gms/measurement/internal/zzak:zzcyd	Ljava/lang/Object;
    //   343: invokevirtual 356	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   346: aload_0
    //   347: invokevirtual 219	com/google/android/gms/measurement/internal/zzx:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   350: bipush 9
    //   352: aconst_null
    //   353: aconst_null
    //   354: iconst_0
    //   355: invokevirtual 816	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   358: goto -41 -> 317
    //   361: astore_1
    //   362: aload_0
    //   363: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   366: invokevirtual 925	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   369: aload_1
    //   370: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	371	0	this	zzx
    //   0	371	1	paramUserAttributeParcel	UserAttributeParcel
    //   0	371	2	paramAppMetadata	AppMetadata
    //   4	191	3	i	int
    //   1	166	4	j	int
    //   48	141	5	k	int
    //   282	10	6	bool	boolean
    //   214	44	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   269	291	361	finally
    //   296	317	361	finally
    //   325	358	361	finally
  }
  
  void zzb(zza paramzza)
  {
    String str1 = zzbwd().zzao(paramzza.zzbth(), paramzza.zzazn());
    try
    {
      URL localURL = new URL(str1);
      zzbwb().zzbxe().zzj("Fetching remote configuration", paramzza.zzup());
      zzwb.zzb localzzb = zzbvy().zzmo(paramzza.zzup());
      Object localObject2 = null;
      String str2 = zzbvy().zzmp(paramzza.zzup());
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
      zzbxv().zza(paramzza.zzup(), localURL, (Map)localObject1, new zzr.zza()
      {
        public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
        {
          zzx.this.zzb(paramAnonymousString, paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte, paramAnonymousMap);
        }
      });
      return;
    }
    catch (MalformedURLException paramzza)
    {
      zzbwb().zzbwy().zzj("Failed to parse config URL. Not fetching", str1);
    }
  }
  
  void zzb(zzaa paramzzaa)
  {
    this.aux += 1;
  }
  
  @WorkerThread
  void zzb(String paramString, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    int j = 0;
    zzzx();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    zzbvw().beginTransaction();
    label90:
    label105:
    int i;
    for (;;)
    {
      try
      {
        paramArrayOfByte = zzbvw().zzlz(paramString);
        if ((paramInt == 200) || (paramInt == 204)) {
          break label471;
        }
        if (paramInt == 304)
        {
          break label471;
          if (paramArrayOfByte == null)
          {
            zzbwb().zzbxa().zzj("App does not exist in onConfigFetched", paramString);
            zzbvw().setTransactionSuccessful();
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
            break label481;
            label169:
            if (zzbvy().zzmo(paramString) != null) {
              continue;
            }
            bool = zzbvy().zzb(paramString, null, null);
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
        break label481;
        label215:
        boolean bool = zzbvy().zzb(paramString, arrayOfByte, paramThrowable);
        if (!bool) {
          return;
        }
        paramArrayOfByte.zzbb(zzabz().currentTimeMillis());
        zzbvw().zza(paramArrayOfByte);
        if (paramInt == 404)
        {
          zzbwb().zzbxa().log("Config not found. Using empty config");
          if ((!zzbxv().zzagk()) || (!zzbyf())) {
            break label344;
          }
          zzbye();
          continue;
        }
        zzbwb().zzbxe().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      }
      finally
      {
        zzbvw().endTransaction();
      }
      continue;
      label344:
      zzbyg();
    }
    paramArrayOfByte.zzbc(zzabz().currentTimeMillis());
    zzbvw().zza(paramArrayOfByte);
    zzbwb().zzbxe().zze("Fetching config failed. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzbvy().zzmq(paramString);
    zzbwc().ata.set(zzabz().currentTimeMillis());
    if (paramInt != 503)
    {
      i = j;
      if (paramInt == 429) {}
    }
    for (;;)
    {
      if (i != 0) {
        zzbwc().atb.set(zzabz().currentTimeMillis());
      }
      zzbyg();
      break label90;
      label471:
      if (paramThrowable != null) {
        break label105;
      }
      i = 1;
      break;
      label481:
      if (paramInt == 404) {
        break label169;
      }
      if (paramInt != 304) {
        break label215;
      }
      break label169;
      i = 1;
    }
  }
  
  boolean zzbm(long paramLong)
  {
    return zzh(null, paramLong);
  }
  
  public zzc zzbvp()
  {
    zza(this.auq);
    return this.auq;
  }
  
  public zzac zzbvq()
  {
    zza(this.aum);
    return this.aum;
  }
  
  public zzn zzbvr()
  {
    zza(this.aun);
    return this.aun;
  }
  
  public zzg zzbvs()
  {
    zza(this.aul);
    return this.aul;
  }
  
  public zzae zzbvt()
  {
    zza(this.auk);
    return this.auk;
  }
  
  public zzad zzbvu()
  {
    zza(this.auj);
    return this.auj;
  }
  
  public zzo zzbvv()
  {
    zza(this.auh);
    return this.auh;
  }
  
  public zze zzbvw()
  {
    zza(this.aug);
    return this.aug;
  }
  
  public zzal zzbvx()
  {
    zza(this.auf);
    return this.auf;
  }
  
  public zzv zzbvy()
  {
    zza(this.auc);
    return this.auc;
  }
  
  public zzag zzbvz()
  {
    zza(this.aub);
    return this.aub;
  }
  
  public zzw zzbwa()
  {
    zza(this.aua);
    return this.aua;
  }
  
  public zzq zzbwb()
  {
    zza(this.atZ);
    return this.atZ;
  }
  
  public zzt zzbwc()
  {
    zza(this.atY);
    return this.atY;
  }
  
  public zzd zzbwd()
  {
    return this.atX;
  }
  
  @WorkerThread
  protected boolean zzbxq()
  {
    boolean bool2 = false;
    zzacj();
    zzzx();
    if ((this.aus == null) || (this.aut == 0L) || ((this.aus != null) && (!this.aus.booleanValue()) && (Math.abs(zzabz().elapsedRealtime() - this.aut) > 1000L)))
    {
      this.aut = zzabz().elapsedRealtime();
      zzbwd().zzayi();
      boolean bool1 = bool2;
      if (zzbvx().zzez("android.permission.INTERNET"))
      {
        bool1 = bool2;
        if (zzbvx().zzez("android.permission.ACCESS_NETWORK_STATE"))
        {
          bool1 = bool2;
          if (zzu.zzh(getContext(), false))
          {
            bool1 = bool2;
            if (zzaf.zzi(getContext(), false)) {
              bool1 = true;
            }
          }
        }
      }
      this.aus = Boolean.valueOf(bool1);
      if (this.aus.booleanValue()) {
        this.aus = Boolean.valueOf(zzbvx().zznb(zzbvr().zzbth()));
      }
    }
    return this.aus.booleanValue();
  }
  
  public zzq zzbxr()
  {
    if ((this.atZ != null) && (this.atZ.isInitialized())) {
      return this.atZ;
    }
    return null;
  }
  
  zzw zzbxs()
  {
    return this.aua;
  }
  
  public AppMeasurement zzbxt()
  {
    return this.aud;
  }
  
  public FirebaseAnalytics zzbxu()
  {
    return this.aue;
  }
  
  public zzr zzbxv()
  {
    zza(this.aui);
    return this.aui;
  }
  
  public zzs zzbxw()
  {
    if (this.auo == null) {
      throw new IllegalStateException("Network broadcast receiver not created");
    }
    return this.auo;
  }
  
  public zzai zzbxx()
  {
    zza(this.aup);
    return this.aup;
  }
  
  FileChannel zzbxy()
  {
    return this.auv;
  }
  
  @WorkerThread
  void zzbxz()
  {
    zzzx();
    zzacj();
    if ((zzbyk()) && (zzbya())) {
      zzv(zza(zzbxy()), zzbvr().zzbwx());
    }
  }
  
  @WorkerThread
  boolean zzbya()
  {
    zzzx();
    Object localObject = this.aug.zzade();
    localObject = new File(getContext().getFilesDir(), (String)localObject);
    try
    {
      this.auv = new RandomAccessFile((File)localObject, "rw").getChannel();
      this.auu = this.auv.tryLock();
      if (this.auu != null)
      {
        zzbwb().zzbxe().log("Storage concurrent access okay");
        return true;
      }
      zzbwb().zzbwy().log("Storage concurrent data access panic");
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Failed to acquire storage lock", localFileNotFoundException);
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Failed to access storage lock file", localIOException);
      }
    }
    return false;
  }
  
  long zzbyb()
  {
    return (zzabz().currentTimeMillis() + zzbwc().zzbxi()) / 1000L / 60L / 60L / 24L;
  }
  
  void zzbyc()
  {
    if (!zzbwd().zzayi()) {
      throw new IllegalStateException("Unexpected call on client side");
    }
  }
  
  @WorkerThread
  protected boolean zzbyd()
  {
    zzzx();
    return this.auw != null;
  }
  
  @WorkerThread
  public void zzbye()
  {
    int j = 0;
    zzzx();
    zzacj();
    if (!zzbwd().zzayi())
    {
      localObject1 = zzbwc().zzbxl();
      if (localObject1 == null) {
        zzbwb().zzbxa().log("Upload data called on the client side before use of service was decided");
      }
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
        zzbwb().zzbwy().log("Upload called in the client side when service should be used");
        return;
      }
      if (zzbyd())
      {
        zzbwb().zzbxa().log("Uploading requested multiple times");
        return;
      }
      if (!zzbxv().zzagk())
      {
        zzbwb().zzbxa().log("Network not connected, ignoring upload request");
        zzbyg();
        return;
      }
      l1 = zzabz().currentTimeMillis();
      zzbm(l1 - zzbwd().zzbvd());
      long l2 = zzbwc().asZ.get();
      if (l2 != 0L) {
        zzbwb().zzbxd().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(l1 - l2)));
      }
      str1 = zzbvw().zzbwe();
      if (TextUtils.isEmpty(str1)) {
        break;
      }
      if (this.auz == -1L) {
        this.auz = zzbvw().zzbwm();
      }
      i = zzbwd().zzlv(str1);
      int k = zzbwd().zzlw(str1);
      localObject3 = zzbvw().zzn(str1, i, k);
    } while (((List)localObject3).isEmpty());
    Object localObject1 = ((List)localObject3).iterator();
    Object localObject4;
    do
    {
      if (!((Iterator)localObject1).hasNext()) {
        break;
      }
      localObject4 = (zzwc.zze)((Pair)((Iterator)localObject1).next()).first;
    } while (TextUtils.isEmpty(((zzwc.zze)localObject4).axi));
    Object localObject2;
    for (localObject1 = ((zzwc.zze)localObject4).axi;; localObject2 = null)
    {
      if (localObject1 != null)
      {
        i = 0;
        if (i < ((List)localObject3).size())
        {
          localObject4 = (zzwc.zze)((Pair)((List)localObject3).get(i)).first;
          if (TextUtils.isEmpty(((zzwc.zze)localObject4).axi)) {}
          while (((zzwc.zze)localObject4).axi.equals(localObject1))
          {
            i += 1;
            break;
          }
        }
      }
      for (localObject1 = ((List)localObject3).subList(0, i);; localObject2 = localObject3)
      {
        localObject4 = new zzwc.zzd();
        ((zzwc.zzd)localObject4).awS = new zzwc.zze[((List)localObject1).size()];
        localObject3 = new ArrayList(((List)localObject1).size());
        i = j;
        while (i < ((zzwc.zzd)localObject4).awS.length)
        {
          ((zzwc.zzd)localObject4).awS[i] = ((zzwc.zze)((Pair)((List)localObject1).get(i)).first);
          ((List)localObject3).add((Long)((Pair)((List)localObject1).get(i)).second);
          localObject4.awS[i].axh = Long.valueOf(zzbwd().zzbto());
          localObject4.awS[i].awX = Long.valueOf(l1);
          localObject4.awS[i].axn = Boolean.valueOf(zzbwd().zzayi());
          i += 1;
        }
        if (zzbwb().zzbi(2)) {}
        for (localObject1 = zzal.zzb((zzwc.zzd)localObject4);; localObject2 = null)
        {
          byte[] arrayOfByte = zzbvx().zza((zzwc.zzd)localObject4);
          String str2 = zzbwd().zzbvc();
          try
          {
            URL localURL = new URL(str2);
            zzag((List)localObject3);
            zzbwc().ata.set(l1);
            localObject3 = "?";
            if (((zzwc.zzd)localObject4).awS.length > 0) {
              localObject3 = localObject4.awS[0].zzcs;
            }
            zzbwb().zzbxe().zzd("Uploading data. app, uncompressed size, data", localObject3, Integer.valueOf(arrayOfByte.length), localObject1);
            zzbxv().zza(str1, localURL, arrayOfByte, null, new zzr.zza()
            {
              public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
              {
                zzx.this.zza(paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte);
              }
            });
            return;
          }
          catch (MalformedURLException localMalformedURLException)
          {
            zzbwb().zzbwy().zzj("Failed to parse upload URL. Not uploading", str2);
            return;
          }
          this.auz = -1L;
          localObject2 = zzbvw().zzbk(l1 - zzbwd().zzbvd());
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            break;
          }
          localObject2 = zzbvw().zzlz((String)localObject2);
          if (localObject2 == null) {
            break;
          }
          zzb((zza)localObject2);
          return;
        }
      }
    }
  }
  
  void zzbyi()
  {
    this.auy += 1;
  }
  
  @WorkerThread
  void zzbyj()
  {
    zzzx();
    zzacj();
    if (!this.aur)
    {
      zzbwb().zzbxc().log("This instance being marked as an uploader");
      zzbxz();
    }
    this.aur = true;
  }
  
  @WorkerThread
  boolean zzbyk()
  {
    zzzx();
    zzacj();
    return this.aur;
  }
  
  void zzc(AppMetadata paramAppMetadata)
  {
    zzzx();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzib(paramAppMetadata.packageName);
    zze(paramAppMetadata);
  }
  
  @WorkerThread
  void zzc(AppMetadata paramAppMetadata, long paramLong)
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", 1L);
    zzb(new EventParcel("_e", new EventParams(localBundle), "auto", paramLong), paramAppMetadata);
  }
  
  @WorkerThread
  void zzc(UserAttributeParcel paramUserAttributeParcel, AppMetadata paramAppMetadata)
  {
    zzzx();
    zzacj();
    if (TextUtils.isEmpty(paramAppMetadata.aqZ)) {
      return;
    }
    if (!paramAppMetadata.are)
    {
      zze(paramAppMetadata);
      return;
    }
    zzbwb().zzbxd().zzj("Removing user property", paramUserAttributeParcel.name);
    zzbvw().beginTransaction();
    try
    {
      zze(paramAppMetadata);
      zzbvw().zzaq(paramAppMetadata.packageName, paramUserAttributeParcel.name);
      zzbvw().setTransactionSuccessful();
      zzbwb().zzbxd().zzj("User property removed", paramUserAttributeParcel.name);
      return;
    }
    finally
    {
      zzbvw().endTransaction();
    }
  }
  
  /* Error */
  @WorkerThread
  public void zzd(AppMetadata paramAppMetadata)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 500	com/google/android/gms/measurement/internal/zzx:zzzx	()V
    //   4: aload_0
    //   5: invokevirtual 503	com/google/android/gms/measurement/internal/zzx:zzacj	()V
    //   8: aload_1
    //   9: invokestatic 88	com/google/android/gms/common/internal/zzaa:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 661	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   17: invokestatic 489	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: getfield 685	com/google/android/gms/measurement/internal/AppMetadata:aqZ	Ljava/lang/String;
    //   25: invokestatic 515	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   28: ifeq +4 -> 32
    //   31: return
    //   32: aload_1
    //   33: getfield 745	com/google/android/gms/measurement/internal/AppMetadata:are	Z
    //   36: ifne +9 -> 45
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 1479	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   44: return
    //   45: aload_0
    //   46: invokevirtual 583	com/google/android/gms/measurement/internal/zzx:zzabz	()Lcom/google/android/gms/common/util/zze;
    //   49: invokeinterface 588 1 0
    //   54: lstore_2
    //   55: aload_0
    //   56: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   59: invokevirtual 762	com/google/android/gms/measurement/internal/zze:beginTransaction	()V
    //   62: aload_0
    //   63: aload_1
    //   64: lload_2
    //   65: invokevirtual 1919	com/google/android/gms/measurement/internal/zzx:zza	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   68: aload_0
    //   69: aload_1
    //   70: invokespecial 1479	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   73: aload_0
    //   74: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   77: aload_1
    //   78: getfield 661	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   81: ldc_w 1442
    //   84: invokevirtual 1337	com/google/android/gms/measurement/internal/zze:zzap	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/measurement/internal/zzi;
    //   87: ifnonnull +63 -> 150
    //   90: aload_0
    //   91: new 1640	com/google/android/gms/measurement/internal/UserAttributeParcel
    //   94: dup
    //   95: ldc_w 1921
    //   98: lload_2
    //   99: lconst_1
    //   100: lload_2
    //   101: ldc2_w 1922
    //   104: ldiv
    //   105: ladd
    //   106: ldc2_w 1922
    //   109: lmul
    //   110: invokestatic 154	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   113: ldc_w 1112
    //   116: invokespecial 1926	com/google/android/gms/measurement/internal/UserAttributeParcel:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   119: aload_1
    //   120: invokevirtual 1928	com/google/android/gms/measurement/internal/zzx:zzb	(Lcom/google/android/gms/measurement/internal/UserAttributeParcel;Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   123: aload_0
    //   124: aload_1
    //   125: lload_2
    //   126: invokevirtual 1930	com/google/android/gms/measurement/internal/zzx:zzb	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   129: aload_0
    //   130: aload_1
    //   131: lload_2
    //   132: invokevirtual 1932	com/google/android/gms/measurement/internal/zzx:zzc	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   135: aload_0
    //   136: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   139: invokevirtual 922	com/google/android/gms/measurement/internal/zze:setTransactionSuccessful	()V
    //   142: aload_0
    //   143: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   146: invokevirtual 925	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   149: return
    //   150: aload_1
    //   151: getfield 1935	com/google/android/gms/measurement/internal/AppMetadata:arf	Z
    //   154: ifeq -19 -> 135
    //   157: aload_0
    //   158: aload_1
    //   159: lload_2
    //   160: invokevirtual 1937	com/google/android/gms/measurement/internal/zzx:zzd	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   163: goto -28 -> 135
    //   166: astore_1
    //   167: aload_0
    //   168: invokevirtual 470	com/google/android/gms/measurement/internal/zzx:zzbvw	()Lcom/google/android/gms/measurement/internal/zze;
    //   171: invokevirtual 925	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   174: aload_1
    //   175: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzx
    //   0	176	1	paramAppMetadata	AppMetadata
    //   54	106	2	l	long
    // Exception table:
    //   from	to	target	type
    //   62	135	166	finally
    //   135	142	166	finally
    //   150	163	166	finally
  }
  
  @WorkerThread
  void zzd(AppMetadata paramAppMetadata, long paramLong)
  {
    zzb(new EventParcel("_cd", new EventParams(new Bundle()), "auto", paramLong), paramAppMetadata);
  }
  
  @WorkerThread
  boolean zzv(int paramInt1, int paramInt2)
  {
    zzzx();
    if (paramInt1 > paramInt2)
    {
      zzbwb().zzbwy().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      return false;
    }
    if (paramInt1 < paramInt2)
    {
      if (zza(paramInt2, zzbxy())) {
        zzbwb().zzbxe().zze("Storage version upgraded. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      }
    }
    else {
      return true;
    }
    zzbwb().zzbwy().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    return false;
  }
  
  @WorkerThread
  public void zzzx()
  {
    zzbwa().zzzx();
  }
  
  private class zza
    implements zze.zzb
  {
    zzwc.zze auB;
    List<Long> auC;
    long auD;
    List<zzwc.zzb> zzani;
    
    private zza() {}
    
    private long zza(zzwc.zzb paramzzb)
    {
      return paramzzb.awO.longValue() / 1000L / 60L / 60L;
    }
    
    boolean isEmpty()
    {
      return (this.zzani == null) || (this.zzani.isEmpty());
    }
    
    public boolean zza(long paramLong, zzwc.zzb paramzzb)
    {
      com.google.android.gms.common.internal.zzaa.zzy(paramzzb);
      if (this.zzani == null) {
        this.zzani = new ArrayList();
      }
      if (this.auC == null) {
        this.auC = new ArrayList();
      }
      if ((this.zzani.size() > 0) && (zza((zzwc.zzb)this.zzani.get(0)) != zza(paramzzb))) {
        return false;
      }
      long l = this.auD + paramzzb.cz();
      if (l >= zzx.this.zzbwd().zzbuz()) {
        return false;
      }
      this.auD = l;
      this.zzani.add(paramzzb);
      this.auC.add(Long.valueOf(paramLong));
      return this.zzani.size() < zzx.this.zzbwd().zzbva();
    }
    
    public void zzb(zzwc.zze paramzze)
    {
      com.google.android.gms.common.internal.zzaa.zzy(paramzze);
      this.auB = paramzze;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */