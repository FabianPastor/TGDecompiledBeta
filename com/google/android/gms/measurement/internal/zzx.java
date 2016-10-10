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
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvm.zza;
import com.google.android.gms.internal.zzvm.zzb;
import com.google.android.gms.internal.zzvm.zzc;
import com.google.android.gms.internal.zzvm.zzd;
import com.google.android.gms.internal.zzvm.zze;
import com.google.android.gms.internal.zzvm.zzg;
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
  private static volatile zzx aqP;
  private final boolean aJ;
  private final zzd aqQ;
  private final zzt aqR;
  private final zzp aqS;
  private final zzw aqT;
  private final zzaf aqU;
  private final zzv aqV;
  private final AppMeasurement aqW;
  private final FirebaseAnalytics aqX;
  private final zzal aqY;
  private final zze aqZ;
  private final zzq ara;
  private final zzad arb;
  private final zzg arc;
  private final zzac ard;
  private final zzn are;
  private final zzr arf;
  private final zzai arg;
  private final zzc arh;
  private boolean ari;
  private Boolean arj;
  private FileLock ark;
  private FileChannel arl;
  private List<Long> arm;
  private int arn;
  private int aro;
  private final Context mContext;
  private final com.google.android.gms.common.util.zze zzapy;
  
  zzx(zzab paramzzab)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramzzab);
    this.mContext = paramzzab.mContext;
    this.zzapy = paramzzab.zzm(this);
    this.aqQ = paramzzab.zza(this);
    Object localObject = paramzzab.zzb(this);
    ((zzt)localObject).initialize();
    this.aqR = ((zzt)localObject);
    localObject = paramzzab.zzc(this);
    ((zzp)localObject).initialize();
    this.aqS = ((zzp)localObject);
    zzbvg().zzbwh().zzj("App measurement is starting up, version", Long.valueOf(zzbvi().zzbsy()));
    zzbvg().zzbwh().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
    zzbvg().zzbwi().log("Debug-level message logging enabled");
    zzbvg().zzbwi().zzj("AppMeasurement singleton hash", Integer.valueOf(System.identityHashCode(this)));
    this.aqY = paramzzab.zzj(this);
    localObject = paramzzab.zzo(this);
    ((zzg)localObject).initialize();
    this.arc = ((zzg)localObject);
    localObject = paramzzab.zzp(this);
    ((zzn)localObject).initialize();
    this.are = ((zzn)localObject);
    localObject = ((zzn)localObject).zzti();
    if (zzbvc().zzni((String)localObject))
    {
      zzbvg().zzbwh().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop firebase.analytics.debug-mode .none.");
      localObject = paramzzab.zzk(this);
      ((zze)localObject).initialize();
      this.aqZ = ((zze)localObject);
      localObject = paramzzab.zzs(this);
      ((zzc)localObject).initialize();
      this.arh = ((zzc)localObject);
      localObject = paramzzab.zzl(this);
      ((zzq)localObject).initialize();
      this.ara = ((zzq)localObject);
      localObject = paramzzab.zzn(this);
      ((zzad)localObject).initialize();
      this.arb = ((zzad)localObject);
      localObject = paramzzab.zzi(this);
      ((zzac)localObject).initialize();
      this.ard = ((zzac)localObject);
      localObject = paramzzab.zzr(this);
      ((zzai)localObject).initialize();
      this.arg = ((zzai)localObject);
      this.arf = paramzzab.zzq(this);
      this.aqW = paramzzab.zzh(this);
      this.aqX = paramzzab.zzg(this);
      localObject = paramzzab.zze(this);
      ((zzaf)localObject).initialize();
      this.aqU = ((zzaf)localObject);
      localObject = paramzzab.zzf(this);
      ((zzv)localObject).initialize();
      this.aqV = ((zzv)localObject);
      paramzzab = paramzzab.zzd(this);
      paramzzab.initialize();
      this.aqT = paramzzab;
      if (this.arn != this.aro) {
        zzbvg().zzbwc().zze("Not all components initialized", Integer.valueOf(this.arn), Integer.valueOf(this.aro));
      }
      this.aJ = true;
      if ((!this.aqQ.zzact()) && (!zzbxg()))
      {
        if (!(this.mContext.getApplicationContext() instanceof Application)) {
          break label529;
        }
        if (Build.VERSION.SDK_INT < 14) {
          break label513;
        }
        zzbux().zzbxv();
      }
    }
    for (;;)
    {
      this.aqT.zzm(new Runnable()
      {
        public void run()
        {
          zzx.this.start();
        }
      });
      return;
      zzp.zza localzza = zzbvg().zzbwh();
      localObject = String.valueOf(localObject);
      if (((String)localObject).length() != 0) {}
      for (localObject = "To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode ".concat((String)localObject);; localObject = new String("To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode "))
      {
        localzza.log((String)localObject);
        break;
      }
      label513:
      zzbvg().zzbwi().log("Not tracking deep linking pre-ICS");
      continue;
      label529:
      zzbvg().zzbwe().log("Application context is not an Application");
    }
  }
  
  @WorkerThread
  private void zza(int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte)
  {
    int i = 0;
    zzyl();
    zzaax();
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    paramArrayOfByte = this.arm;
    this.arm = null;
    if (((paramInt == 200) || (paramInt == 204)) && (paramThrowable == null))
    {
      zzbvh().apQ.set(zzaan().currentTimeMillis());
      zzbvh().apR.set(0L);
      zzbxm();
      zzbvg().zzbwj().zze("Successful upload. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      zzbvb().beginTransaction();
      try
      {
        paramThrowable = paramArrayOfByte.iterator();
        while (paramThrowable.hasNext())
        {
          paramArrayOfByte = (Long)paramThrowable.next();
          zzbvb().zzbk(paramArrayOfByte.longValue());
        }
      }
      finally
      {
        zzbvb().endTransaction();
      }
      zzbvb().endTransaction();
      if ((zzbxa().zzafa()) && (zzbxl()))
      {
        zzbxk();
        return;
      }
      zzbxm();
      return;
    }
    zzbvg().zzbwj().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzbvh().apR.set(zzaan().currentTimeMillis());
    if ((paramInt == 503) || (paramInt == 429)) {
      i = 1;
    }
    if (i != 0) {
      zzbvh().apS.set(zzaan().currentTimeMillis());
    }
    zzbxm();
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
    if (paramzzh.aos == null) {}
    Object localObject;
    boolean bool;
    do
    {
      return false;
      localObject = paramzzh.aos.iterator();
      while (((Iterator)localObject).hasNext()) {
        if ("_r".equals((String)((Iterator)localObject).next())) {
          return true;
        }
      }
      bool = zzbvd().zzay(paramzzh.zzcpe, paramzzh.mName);
      localObject = zzbvb().zza(zzbxh(), paramzzh.zzcpe, false, false, false, false, false);
    } while ((!bool) || (((zze.zza)localObject).aoj >= zzbvi().zzlq(paramzzh.zzcpe)));
    return true;
  }
  
  private zzvm.zza[] zza(String paramString, zzvm.zzg[] paramArrayOfzzg, zzvm.zzb[] paramArrayOfzzb)
  {
    com.google.android.gms.common.internal.zzac.zzhz(paramString);
    return zzbuw().zza(paramString, paramArrayOfzzb, paramArrayOfzzg);
  }
  
  private void zzag(List<Long> paramList)
  {
    if (!paramList.isEmpty()) {}
    for (boolean bool = true;; bool = false)
    {
      com.google.android.gms.common.internal.zzac.zzbs(bool);
      if (this.arm == null) {
        break;
      }
      zzbvg().zzbwc().log("Set uploading progress before finishing the previous upload");
      return;
    }
    this.arm = new ArrayList(paramList);
  }
  
  @WorkerThread
  private boolean zzbxj()
  {
    zzyl();
    return this.arm != null;
  }
  
  private boolean zzbxl()
  {
    zzyl();
    zzaax();
    return (zzbvb().zzbvp()) || (!TextUtils.isEmpty(zzbvb().zzbvj()));
  }
  
  @WorkerThread
  private void zzbxm()
  {
    zzyl();
    zzaax();
    if (!zzbxq()) {
      return;
    }
    if ((!zzbwv()) || (!zzbxl()))
    {
      zzbxb().unregister();
      zzbxc().cancel();
      return;
    }
    long l2 = zzbxn();
    if (l2 == 0L)
    {
      zzbxb().unregister();
      zzbxc().cancel();
      return;
    }
    if (!zzbxa().zzafa())
    {
      zzbxb().zzaex();
      zzbxc().cancel();
      return;
    }
    long l3 = zzbvh().apS.get();
    long l4 = zzbvi().zzbul();
    long l1 = l2;
    if (!zzbvc().zzg(l3, l4)) {
      l1 = Math.max(l2, l3 + l4);
    }
    zzbxb().unregister();
    l2 = l1 - zzaan().currentTimeMillis();
    l1 = l2;
    if (l2 <= 0L) {
      l1 = zzbvi().zzbuo();
    }
    zzbvg().zzbwj().zzj("Upload scheduled in approximately ms", Long.valueOf(l1));
    zzbxc().zzx(l1);
  }
  
  private long zzbxn()
  {
    long l3 = zzaan().currentTimeMillis();
    long l1 = zzbvi().zzbur();
    long l2;
    label54:
    long l6;
    long l5;
    long l4;
    if ((zzbvb().zzbvq()) || (zzbvb().zzbvk()))
    {
      i = 1;
      if (i == 0) {
        break label115;
      }
      l2 = zzbvi().zzbun();
      l6 = zzbvh().apQ.get();
      l5 = zzbvh().apR.get();
      l4 = Math.max(zzbvb().zzbvn(), zzbvb().zzbvo());
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
        l2 = zzbvi().zzbum();
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
        if (!zzbvc().zzg(l6, l2)) {
          l1 = l6 + l2;
        }
        l2 = l1;
      } while (l5 == 0L);
      l2 = l1;
    } while (l5 < l4);
    int i = 0;
    for (;;)
    {
      if (i >= zzbvi().zzbut()) {
        break label295;
      }
      l1 += (1 << i) * zzbvi().zzbus();
      l2 = l1;
      if (l1 > l5) {
        break;
      }
      i += 1;
    }
    label295:
    return 0L;
  }
  
  public static zzx zzdt(Context paramContext)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramContext);
    com.google.android.gms.common.internal.zzac.zzy(paramContext.getApplicationContext());
    if (aqP == null) {}
    try
    {
      if (aqP == null) {
        aqP = new zzab(paramContext).zzbxu();
      }
      return aqP;
    }
    finally {}
  }
  
  @WorkerThread
  private void zze(AppMetadata paramAppMetadata)
  {
    int k = 1;
    zzyl();
    zzaax();
    com.google.android.gms.common.internal.zzac.zzy(paramAppMetadata);
    com.google.android.gms.common.internal.zzac.zzhz(paramAppMetadata.packageName);
    zza localzza2 = zzbvb().zzlz(paramAppMetadata.packageName);
    String str = zzbvh().zzmm(paramAppMetadata.packageName);
    int i = 0;
    zza localzza1;
    int j;
    if (localzza2 == null)
    {
      localzza1 = new zza(this, paramAppMetadata.packageName);
      localzza1.zzlj(zzbvh().zzbwm());
      localzza1.zzll(str);
      i = 1;
      j = i;
      if (!TextUtils.isEmpty(paramAppMetadata.anQ))
      {
        j = i;
        if (!paramAppMetadata.anQ.equals(localzza1.zzbsr()))
        {
          localzza1.zzlk(paramAppMetadata.anQ);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramAppMetadata.anY))
      {
        i = j;
        if (!paramAppMetadata.anY.equals(localzza1.zzbst()))
        {
          localzza1.zzlm(paramAppMetadata.anY);
          i = 1;
        }
      }
      j = i;
      if (paramAppMetadata.anS != 0L)
      {
        j = i;
        if (paramAppMetadata.anS != localzza1.zzbsy())
        {
          localzza1.zzaz(paramAppMetadata.anS);
          j = 1;
        }
      }
      i = j;
      if (!TextUtils.isEmpty(paramAppMetadata.afY))
      {
        i = j;
        if (!paramAppMetadata.afY.equals(localzza1.zzyt()))
        {
          localzza1.setAppVersion(paramAppMetadata.afY);
          i = 1;
        }
      }
      if (paramAppMetadata.anX != localzza1.zzbsw())
      {
        localzza1.zzay(paramAppMetadata.anX);
        i = 1;
      }
      j = i;
      if (!TextUtils.isEmpty(paramAppMetadata.anR))
      {
        j = i;
        if (!paramAppMetadata.anR.equals(localzza1.zzbsx()))
        {
          localzza1.zzln(paramAppMetadata.anR);
          j = 1;
        }
      }
      if (paramAppMetadata.anT != localzza1.zzbsz())
      {
        localzza1.zzba(paramAppMetadata.anT);
        j = 1;
      }
      if (paramAppMetadata.anV == localzza1.zzbta()) {
        break label420;
      }
      localzza1.setMeasurementEnabled(paramAppMetadata.anV);
    }
    label420:
    for (i = k;; i = j)
    {
      if (i != 0) {
        zzbvb().zza(localzza1);
      }
      return;
      localzza1 = localzza2;
      if (str.equals(localzza2.zzbss())) {
        break;
      }
      localzza2.zzll(str);
      localzza2.zzlj(zzbvh().zzbwm());
      i = 1;
      localzza1 = localzza2;
      break;
    }
  }
  
  private boolean zzj(String paramString, long paramLong)
  {
    zzbvb().beginTransaction();
    for (;;)
    {
      int k;
      int i;
      boolean bool3;
      int n;
      int i1;
      int m;
      Object localObject1;
      zzvm.zzc[] arrayOfzzc;
      long l;
      try
      {
        zza localzza = new zza(null);
        zzbvb().zza(paramString, paramLong, localzza);
        if (localzza.isEmpty()) {
          break label1541;
        }
        bool1 = false;
        zzvm.zze localzze = localzza.arq;
        localzze.atw = new zzvm.zzb[localzza.zzamv.size()];
        j = 0;
        k = 0;
        if (k < localzza.zzamv.size())
        {
          if (zzbvd().zzax(localzza.arq.zzck, ((zzvm.zzb)localzza.zzamv.get(k)).name))
          {
            zzbvg().zzbwe().zzj("Dropping blacklisted raw event", ((zzvm.zzb)localzza.zzamv.get(k)).name);
            zzbvc().zza(11, "_ev", ((zzvm.zzb)localzza.zzamv.get(k)).name, 0);
            i = j;
            break label1578;
          }
          bool3 = bool1;
          if (!zzbvd().zzay(localzza.arq.zzck, ((zzvm.zzb)localzza.zzamv.get(k)).name)) {
            break label1664;
          }
          n = 0;
          i = 0;
          if (((zzvm.zzb)localzza.zzamv.get(k)).ato == null) {
            ((zzvm.zzb)localzza.zzamv.get(k)).ato = new zzvm.zzc[0];
          }
          paramString = ((zzvm.zzb)localzza.zzamv.get(k)).ato;
          i1 = paramString.length;
          m = 0;
          if (m < i1)
          {
            localObject1 = paramString[m];
            if ("_c".equals(((zzvm.zzc)localObject1).name))
            {
              ((zzvm.zzc)localObject1).ats = Long.valueOf(1L);
              n = 1;
              break label1591;
            }
            if (!"_r".equals(((zzvm.zzc)localObject1).name)) {
              break label1575;
            }
            ((zzvm.zzc)localObject1).ats = Long.valueOf(1L);
            i = 1;
            break label1591;
          }
          if (n == 0)
          {
            zzbvg().zzbwj().zzj("Marking event as conversion", ((zzvm.zzb)localzza.zzamv.get(k)).name);
            paramString = (zzvm.zzc[])Arrays.copyOf(((zzvm.zzb)localzza.zzamv.get(k)).ato, ((zzvm.zzb)localzza.zzamv.get(k)).ato.length + 1);
            localObject1 = new zzvm.zzc();
            ((zzvm.zzc)localObject1).name = "_c";
            ((zzvm.zzc)localObject1).ats = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzvm.zzb)localzza.zzamv.get(k)).ato = paramString;
          }
          if (i == 0)
          {
            zzbvg().zzbwj().zzj("Marking event as real-time", ((zzvm.zzb)localzza.zzamv.get(k)).name);
            paramString = (zzvm.zzc[])Arrays.copyOf(((zzvm.zzb)localzza.zzamv.get(k)).ato, ((zzvm.zzb)localzza.zzamv.get(k)).ato.length + 1);
            localObject1 = new zzvm.zzc();
            ((zzvm.zzc)localObject1).name = "_r";
            ((zzvm.zzc)localObject1).ats = Long.valueOf(1L);
            paramString[(paramString.length - 1)] = localObject1;
            ((zzvm.zzb)localzza.zzamv.get(k)).ato = paramString;
          }
          boolean bool4 = zzal.zzmx(((zzvm.zzb)localzza.zzamv.get(k)).name);
          if (zzbvb().zza(zzbxh(), localzza.arq.zzck, false, false, false, false, true).aoj <= zzbvi().zzlq(localzza.arq.zzck)) {
            break label1569;
          }
          paramString = (zzvm.zzb)localzza.zzamv.get(k);
          i = 0;
          bool2 = bool1;
          if (i < paramString.ato.length)
          {
            if (!"_r".equals(paramString.ato[i].name)) {
              break label1609;
            }
            localObject1 = new zzvm.zzc[paramString.ato.length - 1];
            if (i > 0) {
              System.arraycopy(paramString.ato, 0, localObject1, 0, i);
            }
            if (i < localObject1.length) {
              System.arraycopy(paramString.ato, i + 1, localObject1, i, localObject1.length - i);
            }
            paramString.ato = ((zzvm.zzc[])localObject1);
            bool2 = bool1;
          }
          bool3 = bool2;
          if (!bool4) {
            break label1664;
          }
          bool3 = bool2;
          if (zzbvb().zza(zzbxh(), localzza.arq.zzck, false, false, true, false, false).aoh <= zzbvi().zzlp(localzza.arq.zzck)) {
            break label1664;
          }
          zzbvg().zzbwe().log("Too many conversions. Not logging as conversion.");
          localObject2 = (zzvm.zzb)localzza.zzamv.get(k);
          i = 0;
          paramString = null;
          arrayOfzzc = ((zzvm.zzb)localObject2).ato;
          n = arrayOfzzc.length;
          m = 0;
          if (m < n)
          {
            localObject1 = arrayOfzzc[m];
            if ("_c".equals(((zzvm.zzc)localObject1).name))
            {
              paramString = (String)localObject1;
              break label1600;
            }
            if (!"_err".equals(((zzvm.zzc)localObject1).name)) {
              break label1566;
            }
            i = 1;
            break label1600;
          }
          if ((i != 0) && (paramString != null))
          {
            localObject1 = new zzvm.zzc[((zzvm.zzb)localObject2).ato.length - 1];
            i = 0;
            arrayOfzzc = ((zzvm.zzb)localObject2).ato;
            i1 = arrayOfzzc.length;
            m = 0;
            break label1618;
            ((zzvm.zzb)localObject2).ato = ((zzvm.zzc[])localObject1);
            bool1 = bool2;
            localzze.atw[j] = ((zzvm.zzb)localzza.zzamv.get(k));
            i = j + 1;
            break label1578;
          }
          if (paramString != null)
          {
            paramString.name = "_err";
            paramString.ats = Long.valueOf(10L);
            bool1 = bool2;
            continue;
          }
          zzbvg().zzbwc().log("Did not find conversion parameter. Error not tracked");
          bool3 = bool2;
          break label1664;
        }
        if (j < localzza.zzamv.size()) {
          localzze.atw = ((zzvm.zzb[])Arrays.copyOf(localzze.atw, j));
        }
        localzze.atP = zza(localzza.arq.zzck, localzza.arq.atx, localzze.atw);
        localzze.atz = localzze.atw[0].atp;
        localzze.atA = localzze.atw[0].atp;
        i = 1;
        if (i < localzze.atw.length)
        {
          paramString = localzze.atw[i];
          if (paramString.atp.longValue() < localzze.atz.longValue()) {
            localzze.atz = paramString.atp;
          }
          if (paramString.atp.longValue() <= localzze.atA.longValue()) {
            break label1671;
          }
          localzze.atA = paramString.atp;
          break label1671;
        }
        localObject1 = localzza.arq.zzck;
        Object localObject2 = zzbvb().zzlz((String)localObject1);
        if (localObject2 == null)
        {
          zzbvg().zzbwc().log("Bundling raw events w/o app info");
          localzze.anU = zzbvg().zzbwk();
          zzbvb().zza(localzze, bool1);
          zzbvb().zzaf(localzza.arr);
          zzbvb().zzmg((String)localObject1);
          zzbvb().setTransactionSuccessful();
          return true;
        }
        paramLong = ((zza)localObject2).zzbsv();
        if (paramLong != 0L)
        {
          paramString = Long.valueOf(paramLong);
          localzze.atC = paramString;
          l = ((zza)localObject2).zzbsu();
          if (l != 0L) {
            break label1557;
          }
          if (paramLong == 0L) {
            break label1536;
          }
          paramString = Long.valueOf(paramLong);
          localzze.atB = paramString;
          ((zza)localObject2).zzbte();
          localzze.atN = Integer.valueOf((int)((zza)localObject2).zzbtb());
          ((zza)localObject2).zzaw(localzze.atz.longValue());
          ((zza)localObject2).zzax(localzze.atA.longValue());
          zzbvb().zza((zza)localObject2);
          continue;
        }
        paramString = null;
      }
      finally
      {
        zzbvb().endTransaction();
      }
      continue;
      label1536:
      paramString = null;
      continue;
      label1541:
      zzbvb().setTransactionSuccessful();
      zzbvb().endTransaction();
      return false;
      label1557:
      paramLong = l;
      continue;
      break label1655;
      label1566:
      break label1600;
      label1569:
      boolean bool2 = true;
      continue;
      label1575:
      break label1591;
      label1578:
      k += 1;
      int j = i;
      continue;
      label1591:
      m += 1;
      continue;
      label1600:
      m += 1;
      continue;
      label1609:
      i += 1;
      continue;
      for (;;)
      {
        label1618:
        if (m >= i1) {
          break label1662;
        }
        zzvm.zzc localzzc = arrayOfzzc[m];
        if (localzzc == paramString) {
          break;
        }
        n = i + 1;
        localObject1[i] = localzzc;
        i = n;
        label1655:
        m += 1;
      }
      label1662:
      continue;
      label1664:
      boolean bool1 = bool3;
      continue;
      label1671:
      i += 1;
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
    zzyl();
    zzaax();
    if (zzbvi().zzbuc()) {
      return false;
    }
    Boolean localBoolean = zzbvi().zzbud();
    if (localBoolean != null) {
      bool = localBoolean.booleanValue();
    }
    for (;;)
    {
      return zzbvh().zzcg(bool);
      if (!zzbvi().zzasm()) {
        bool = true;
      }
    }
  }
  
  @WorkerThread
  protected void start()
  {
    zzyl();
    if ((zzbxg()) && ((!this.aqT.isInitialized()) || (this.aqT.zzbxt())))
    {
      zzbvg().zzbwc().log("Scheduler shutting down before Scion.start() called");
      return;
    }
    zzbvb().zzbvl();
    if (zzbvh().apQ.get() == 0L) {
      zzbvh().apQ.set(zzaan().currentTimeMillis());
    }
    if (!zzbwv()) {
      if (isEnabled())
      {
        if (!zzbvc().zzew("android.permission.INTERNET")) {
          zzbvg().zzbwc().log("App is missing INTERNET permission");
        }
        if (!zzbvc().zzew("android.permission.ACCESS_NETWORK_STATE")) {
          zzbvg().zzbwc().log("App is missing ACCESS_NETWORK_STATE permission");
        }
        if (!zzbvi().zzact())
        {
          if (!zzu.zzh(getContext(), false)) {
            zzbvg().zzbwc().log("AppMeasurementReceiver not registered/enabled");
          }
          if (!zzae.zzi(getContext(), false)) {
            zzbvg().zzbwc().log("AppMeasurementService not registered/enabled");
          }
        }
        if (!zzbxg()) {
          zzbvg().zzbwc().log("Uploading is not possible. App measurement disabled");
        }
      }
    }
    label324:
    label387:
    for (;;)
    {
      zzbxm();
      return;
      String str;
      if ((!zzbvi().zzact()) && (!TextUtils.isEmpty(zzbuy().zzbsr())))
      {
        str = zzbvh().zzbwp();
        if (str != null) {
          break label324;
        }
        zzbvh().zzmn(zzbuy().zzbsr());
      }
      for (;;)
      {
        if ((zzbvi().zzact()) || (zzbxg()) || (TextUtils.isEmpty(zzbuy().zzbsr()))) {
          break label387;
        }
        zzbux().zzbxw();
        break;
        if (!str.equals(zzbuy().zzbsr()))
        {
          zzbvg().zzbwh().log("Rechecking which service to use due to a GMP App Id change");
          zzbvh().zzbwr();
          this.arb.disconnect();
          this.arb.zzabz();
          zzbvh().zzmn(zzbuy().zzbsr());
        }
      }
    }
  }
  
  @WorkerThread
  int zza(FileChannel paramFileChannel)
  {
    zzyl();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen())) {
      zzbvg().zzbwc().log("Bad chanel to read from");
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
          zzbvg().zzbwe().zzj("Unexpected data length. Bytes read", Integer.valueOf(i));
          return 0;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzbvg().zzbwc().zzj("Failed to read from channel", paramFileChannel);
        return 0;
      }
    }
    localByteBuffer.flip();
    int i = localByteBuffer.getInt();
    return i;
  }
  
  @WorkerThread
  void zza(AppMetadata paramAppMetadata, long paramLong)
  {
    Object localObject2 = zzbvb().zzlz(paramAppMetadata.packageName);
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((zza)localObject2).zzbsr() != null)
      {
        localObject1 = localObject2;
        if (!((zza)localObject2).zzbsr().equals(paramAppMetadata.anQ))
        {
          zzbvg().zzbwe().log("New GMP App Id passed in. Removing cached database data.");
          zzbvb().zzme(((zza)localObject2).zzti());
          localObject1 = null;
        }
      }
    }
    if ((localObject1 != null) && (((zza)localObject1).zzyt() != null) && (!((zza)localObject1).zzyt().equals(paramAppMetadata.afY)))
    {
      localObject2 = new Bundle();
      ((Bundle)localObject2).putString("_pv", ((zza)localObject1).zzyt());
      zzb(new EventParcel("_au", new EventParams((Bundle)localObject2), "auto", paramLong), paramAppMetadata);
    }
  }
  
  void zza(zzh paramzzh, AppMetadata paramAppMetadata)
  {
    zzyl();
    zzaax();
    com.google.android.gms.common.internal.zzac.zzy(paramzzh);
    com.google.android.gms.common.internal.zzac.zzy(paramAppMetadata);
    com.google.android.gms.common.internal.zzac.zzhz(paramzzh.zzcpe);
    com.google.android.gms.common.internal.zzac.zzbs(paramzzh.zzcpe.equals(paramAppMetadata.packageName));
    zzvm.zze localzze = new zzvm.zze();
    localzze.atv = Integer.valueOf(1);
    localzze.atD = "android";
    localzze.zzck = paramAppMetadata.packageName;
    localzze.anR = paramAppMetadata.anR;
    localzze.afY = paramAppMetadata.afY;
    localzze.atQ = Integer.valueOf((int)paramAppMetadata.anX);
    localzze.atH = Long.valueOf(paramAppMetadata.anS);
    localzze.anQ = paramAppMetadata.anQ;
    Object localObject1;
    if (paramAppMetadata.anT == 0L)
    {
      localObject1 = null;
      localzze.atM = ((Long)localObject1);
      localObject1 = zzbvh().zzml(paramAppMetadata.packageName);
      if ((localObject1 == null) || (TextUtils.isEmpty((CharSequence)((Pair)localObject1).first))) {
        break label599;
      }
      localzze.atJ = ((String)((Pair)localObject1).first);
      localzze.atK = ((Boolean)((Pair)localObject1).second);
    }
    label599:
    while (zzbuz().zzds(this.mContext))
    {
      localzze.atE = zzbuz().zzuj();
      localzze.zzct = zzbuz().zzbvv();
      localzze.atG = Integer.valueOf((int)zzbuz().zzbvw());
      localzze.atF = zzbuz().zzbvx();
      localzze.atI = null;
      localzze.aty = null;
      localzze.atz = null;
      localzze.atA = null;
      localObject2 = zzbvb().zzlz(paramAppMetadata.packageName);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new zza(this, paramAppMetadata.packageName);
        ((zza)localObject1).zzlj(zzbvh().zzbwm());
        ((zza)localObject1).zzlm(paramAppMetadata.anY);
        ((zza)localObject1).zzlk(paramAppMetadata.anQ);
        ((zza)localObject1).zzll(zzbvh().zzmm(paramAppMetadata.packageName));
        ((zza)localObject1).zzbb(0L);
        ((zza)localObject1).zzaw(0L);
        ((zza)localObject1).zzax(0L);
        ((zza)localObject1).setAppVersion(paramAppMetadata.afY);
        ((zza)localObject1).zzay(paramAppMetadata.anX);
        ((zza)localObject1).zzln(paramAppMetadata.anR);
        ((zza)localObject1).zzaz(paramAppMetadata.anS);
        ((zza)localObject1).zzba(paramAppMetadata.anT);
        ((zza)localObject1).setMeasurementEnabled(paramAppMetadata.anV);
        zzbvb().zza((zza)localObject1);
      }
      localzze.atL = ((zza)localObject1).zzayn();
      localzze.anY = ((zza)localObject1).zzbst();
      paramAppMetadata = zzbvb().zzly(paramAppMetadata.packageName);
      localzze.atx = new zzvm.zzg[paramAppMetadata.size()];
      int i = 0;
      while (i < paramAppMetadata.size())
      {
        localObject1 = new zzvm.zzg();
        localzze.atx[i] = localObject1;
        ((zzvm.zzg)localObject1).name = ((zzak)paramAppMetadata.get(i)).mName;
        ((zzvm.zzg)localObject1).atX = Long.valueOf(((zzak)paramAppMetadata.get(i)).asy);
        zzbvc().zza((zzvm.zzg)localObject1, ((zzak)paramAppMetadata.get(i)).zzctv);
        i += 1;
      }
      localObject1 = Long.valueOf(paramAppMetadata.anT);
      break;
    }
    Object localObject2 = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
    if (localObject2 == null)
    {
      zzbvg().zzbwe().log("null secure ID");
      localObject1 = "null";
    }
    for (;;)
    {
      localzze.atT = ((String)localObject1);
      break;
      localObject1 = localObject2;
      if (((String)localObject2).isEmpty())
      {
        zzbvg().zzbwe().log("empty secure ID");
        localObject1 = localObject2;
      }
    }
    try
    {
      long l = zzbvb().zza(localzze);
      zzbvb().zza(paramzzh, l, zza(paramzzh));
      return;
    }
    catch (IOException paramzzh)
    {
      zzbvg().zzbwc().zzj("Data loss. Failed to insert raw event metadata", paramzzh);
    }
  }
  
  @WorkerThread
  boolean zza(int paramInt, FileChannel paramFileChannel)
  {
    boolean bool = true;
    zzyl();
    if ((paramFileChannel == null) || (!paramFileChannel.isOpen()))
    {
      zzbvg().zzbwc().log("Bad chanel to read from");
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
          zzbvg().zzbwc().zzj("Error writing to channel. Bytes written", Long.valueOf(paramFileChannel.size()));
          return true;
        }
      }
      catch (IOException paramFileChannel)
      {
        zzbvg().zzbwc().zzj("Failed to write to channel", paramFileChannel);
      }
    }
    return false;
  }
  
  @WorkerThread
  public byte[] zza(@NonNull EventParcel paramEventParcel, @Size(min=1L) String paramString)
  {
    zzaax();
    zzyl();
    zzbxi();
    com.google.android.gms.common.internal.zzac.zzy(paramEventParcel);
    com.google.android.gms.common.internal.zzac.zzhz(paramString);
    zzvm.zzd localzzd = new zzvm.zzd();
    zzbvb().beginTransaction();
    zza localzza;
    zzvm.zze localzze;
    try
    {
      localzza = zzbvb().zzlz(paramString);
      if (localzza == null)
      {
        zzbvg().zzbwi().zzj("Log and bundle not available. package_name", paramString);
        return new byte[0];
      }
      if (!localzza.zzbta())
      {
        zzbvg().zzbwi().zzj("Log and bundle disabled. package_name", paramString);
        return new byte[0];
      }
      localzze = new zzvm.zze();
      localzzd.att = new zzvm.zze[] { localzze };
      localzze.atv = Integer.valueOf(1);
      localzze.atD = "android";
      localzze.zzck = localzza.zzti();
      localzze.anR = localzza.zzbsx();
      localzze.afY = localzza.zzyt();
      localzze.atQ = Integer.valueOf((int)localzza.zzbsw());
      localzze.atH = Long.valueOf(localzza.zzbsy());
      localzze.anQ = localzza.zzbsr();
      localzze.atM = Long.valueOf(localzza.zzbsz());
      Object localObject1 = zzbvh().zzml(localzza.zzti());
      if ((localObject1 != null) && (!TextUtils.isEmpty((CharSequence)((Pair)localObject1).first)))
      {
        localzze.atJ = ((String)((Pair)localObject1).first);
        localzze.atK = ((Boolean)((Pair)localObject1).second);
      }
      localzze.atE = zzbuz().zzuj();
      localzze.zzct = zzbuz().zzbvv();
      localzze.atG = Integer.valueOf((int)zzbuz().zzbvw());
      localzze.atF = zzbuz().zzbvx();
      localzze.atL = localzza.zzayn();
      localzze.anY = localzza.zzbst();
      localObject1 = zzbvb().zzly(localzza.zzti());
      localzze.atx = new zzvm.zzg[((List)localObject1).size()];
      int i = 0;
      while (i < ((List)localObject1).size())
      {
        localObject2 = new zzvm.zzg();
        localzze.atx[i] = localObject2;
        ((zzvm.zzg)localObject2).name = ((zzak)((List)localObject1).get(i)).mName;
        ((zzvm.zzg)localObject2).atX = Long.valueOf(((zzak)((List)localObject1).get(i)).asy);
        zzbvc().zza((zzvm.zzg)localObject2, ((zzak)((List)localObject1).get(i)).zzctv);
        i += 1;
      }
      localObject1 = paramEventParcel.aoz.zzbvz();
      if ("_iap".equals(paramEventParcel.name))
      {
        ((Bundle)localObject1).putLong("_c", 1L);
        zzbvg().zzbwi().log("Marking in-app purchase as real-time");
        ((Bundle)localObject1).putLong("_r", 1L);
      }
      ((Bundle)localObject1).putString("_o", paramEventParcel.aoA);
      if (zzbvc().zzni(localzze.zzck))
      {
        zzbvc().zza((Bundle)localObject1, "_dbg", Long.valueOf(1L));
        zzbvc().zza((Bundle)localObject1, "_r", Long.valueOf(1L));
      }
      Object localObject2 = zzbvb().zzaq(paramString, paramEventParcel.name);
      if (localObject2 == null)
      {
        localObject2 = new zzi(paramString, paramEventParcel.name, 1L, 0L, paramEventParcel.aoB);
        zzbvb().zza((zzi)localObject2);
        l1 = 0L;
      }
      for (;;)
      {
        paramEventParcel = new zzh(this, paramEventParcel.aoA, paramString, paramEventParcel.name, paramEventParcel.aoB, l1, (Bundle)localObject1);
        paramString = new zzvm.zzb();
        localzze.atw = new zzvm.zzb[] { paramString };
        paramString.atp = Long.valueOf(paramEventParcel.tr);
        paramString.name = paramEventParcel.mName;
        paramString.atq = Long.valueOf(paramEventParcel.aor);
        paramString.ato = new zzvm.zzc[paramEventParcel.aos.size()];
        localObject1 = paramEventParcel.aos.iterator();
        i = 0;
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject3 = (String)((Iterator)localObject1).next();
          localObject2 = new zzvm.zzc();
          paramString.ato[i] = localObject2;
          ((zzvm.zzc)localObject2).name = ((String)localObject3);
          localObject3 = paramEventParcel.aos.get((String)localObject3);
          zzbvc().zza((zzvm.zzc)localObject2, localObject3);
          i += 1;
        }
        l1 = ((zzi)localObject2).aov;
        localObject2 = ((zzi)localObject2).zzbm(paramEventParcel.aoB).zzbvy();
        zzbvb().zza((zzi)localObject2);
      }
      localzze.atP = zza(localzza.zzti(), localzze.atx, localzze.atw);
    }
    finally
    {
      zzbvb().endTransaction();
    }
    localzze.atz = paramString.atp;
    localzze.atA = paramString.atp;
    long l1 = localzza.zzbsv();
    long l2;
    if (l1 != 0L)
    {
      paramEventParcel = Long.valueOf(l1);
      localzze.atC = paramEventParcel;
      l2 = localzza.zzbsu();
      if (l2 != 0L) {
        break label1146;
      }
    }
    for (;;)
    {
      if (l1 != 0L) {}
      for (paramEventParcel = Long.valueOf(l1);; paramEventParcel = null)
      {
        localzze.atB = paramEventParcel;
        localzza.zzbte();
        localzze.atN = Integer.valueOf((int)localzza.zzbtb());
        localzze.atI = Long.valueOf(zzbvi().zzbsy());
        localzze.aty = Long.valueOf(zzaan().currentTimeMillis());
        localzze.atO = Boolean.TRUE;
        localzza.zzaw(localzze.atz.longValue());
        localzza.zzax(localzze.atA.longValue());
        zzbvb().zza(localzza);
        zzbvb().setTransactionSuccessful();
        zzbvb().endTransaction();
        try
        {
          paramEventParcel = new byte[localzzd.db()];
          paramString = zzard.zzbe(paramEventParcel);
          localzzd.zza(paramString);
          paramString.cO();
          paramEventParcel = zzbvc().zzj(paramEventParcel);
          return paramEventParcel;
        }
        catch (IOException paramEventParcel)
        {
          zzbvg().zzbwc().zzj("Data loss. Failed to bundle and serialize", paramEventParcel);
          return null;
        }
        paramEventParcel = null;
        break;
      }
      label1146:
      l1 = l2;
    }
  }
  
  void zzaam()
  {
    if (zzbvi().zzact()) {
      throw new IllegalStateException("Unexpected call on package side");
    }
  }
  
  public com.google.android.gms.common.util.zze zzaan()
  {
    return this.zzapy;
  }
  
  void zzaax()
  {
    if (!this.aJ) {
      throw new IllegalStateException("AppMeasurement is not initialized");
    }
  }
  
  public void zzav(boolean paramBoolean)
  {
    zzbxm();
  }
  
  @WorkerThread
  void zzb(AppMetadata paramAppMetadata, long paramLong)
  {
    zzyl();
    zzaax();
    Bundle localBundle = new Bundle();
    localBundle.putLong("_c", 1L);
    localBundle.putLong("_r", 1L);
    localBundle.putLong("_uwa", 0L);
    localBundle.putLong("_pfo", 0L);
    localBundle.putLong("_sys", 0L);
    localBundle.putLong("_sysu", 0L);
    PackageManager localPackageManager = getContext().getPackageManager();
    if (localPackageManager == null) {
      zzbvg().zzbwc().log("PackageManager is null, first open report might be inaccurate");
    }
    for (;;)
    {
      long l = zzbvb().zzmf(paramAppMetadata.packageName);
      if (l != 0L) {
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
          zzbvg().zzbwc().zzj("Package info is null, first open report might be inaccurate", localNameNotFoundException1);
          Object localObject2 = null;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          for (;;)
          {
            zzbvg().zzbwc().zzj("Application info is null, first open report might be inaccurate", localNameNotFoundException2);
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
    zzyl();
    zzaax();
    String str = paramAppMetadata.packageName;
    com.google.android.gms.common.internal.zzac.zzhz(str);
    if (TextUtils.isEmpty(paramAppMetadata.anQ)) {
      return;
    }
    if (!paramAppMetadata.anV)
    {
      zze(paramAppMetadata);
      return;
    }
    if (zzbvd().zzax(str, paramEventParcel.name))
    {
      zzbvg().zzbwe().zzj("Dropping blacklisted event", paramEventParcel.name);
      zzbvc().zza(11, "_ev", paramEventParcel.name, 0);
      return;
    }
    if (zzbvg().zzbf(2)) {
      zzbvg().zzbwj().zzj("Logging event", paramEventParcel);
    }
    zzbvb().beginTransaction();
    Bundle localBundle;
    boolean bool1;
    boolean bool2;
    for (;;)
    {
      try
      {
        localBundle = paramEventParcel.aoz.zzbvz();
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
              localObject2 = zzbvb().zzas(str, (String)localObject1);
              if ((localObject2 != null) && ((((zzak)localObject2).zzctv instanceof Long))) {
                break label637;
              }
              zzbvb().zzz(str, zzbvi().zzls(str) - 1);
              localObject1 = new zzak(str, (String)localObject1, zzaan().currentTimeMillis(), Long.valueOf(l1));
              if (!zzbvb().zza((zzak)localObject1))
              {
                zzbvg().zzbwc().zze("Too many unique user properties are set. Ignoring user property.", ((zzak)localObject1).mName, ((zzak)localObject1).zzctv);
                zzbvc().zza(9, null, null, 0);
              }
            }
          }
        }
        bool1 = zzal.zzmx(paramEventParcel.name);
        bool2 = "_err".equals(paramEventParcel.name);
        localObject1 = zzbvb().zza(zzbxh(), str, true, bool1, false, bool2, false);
        l1 = ((zze.zza)localObject1).aog - zzbvi().zzbtv();
        if (l1 <= 0L) {
          break;
        }
        if (l1 % 1000L == 1L) {
          zzbvg().zzbwc().zzj("Data loss. Too many events logged. count", Long.valueOf(((zze.zza)localObject1).aog));
        }
        zzbvc().zza(16, "_ev", paramEventParcel.name, 0);
        zzbvb().setTransactionSuccessful();
        return;
        zzbvg().zzbwe().zzj("Data lost. Currency value is too big", Double.valueOf(d1));
        zzbvb().setTransactionSuccessful();
        return;
        l1 = localBundle.getLong("value");
        continue;
        localObject1 = new String((String)localObject1);
        continue;
        l3 = ((Long)((zzak)localObject2).zzctv).longValue();
      }
      finally
      {
        zzbvb().endTransaction();
      }
      label637:
      long l3;
      localObject1 = new zzak(str, (String)localObject1, zzaan().currentTimeMillis(), Long.valueOf(l1 + l3));
    }
    if (bool1)
    {
      l1 = ((zze.zza)localObject1).aof - zzbvi().zzbtw();
      if (l1 > 0L)
      {
        if (l1 % 1000L == 1L) {
          zzbvg().zzbwc().zzj("Data loss. Too many public events logged. count", Long.valueOf(((zze.zza)localObject1).aof));
        }
        zzbvc().zza(16, "_ev", paramEventParcel.name, 0);
        zzbvb().setTransactionSuccessful();
        zzbvb().endTransaction();
        return;
      }
    }
    if (bool2)
    {
      l1 = ((zze.zza)localObject1).aoi - zzbvi().zzlo(paramAppMetadata.packageName);
      if (l1 > 0L)
      {
        if (l1 == 1L) {
          zzbvg().zzbwc().zzj("Too many error events logged. count", Long.valueOf(((zze.zza)localObject1).aoi));
        }
        zzbvb().setTransactionSuccessful();
        zzbvb().endTransaction();
        return;
      }
    }
    zzbvc().zza(localBundle, "_o", paramEventParcel.aoA);
    if (zzbvc().zzni(str))
    {
      zzbvc().zza(localBundle, "_dbg", Long.valueOf(1L));
      zzbvc().zza(localBundle, "_r", Long.valueOf(1L));
    }
    long l1 = zzbvb().zzma(str);
    if (l1 > 0L) {
      zzbvg().zzbwe().zzj("Data lost. Too many events stored on disk, deleted", Long.valueOf(l1));
    }
    paramEventParcel = new zzh(this, paramEventParcel.aoA, str, paramEventParcel.name, paramEventParcel.aoB, 0L, localBundle);
    Object localObject1 = zzbvb().zzaq(str, paramEventParcel.mName);
    if (localObject1 == null) {
      if (zzbvb().zzmh(str) >= zzbvi().zzbtu())
      {
        zzbvg().zzbwc().zze("Too many event names used, ignoring event. name, supported count", paramEventParcel.mName, Integer.valueOf(zzbvi().zzbtu()));
        zzbvc().zza(8, null, null, 0);
        zzbvb().endTransaction();
        return;
      }
    }
    for (localObject1 = new zzi(str, paramEventParcel.mName, 0L, 0L, paramEventParcel.tr);; localObject1 = ((zzi)localObject1).zzbm(paramEventParcel.tr))
    {
      zzbvb().zza((zzi)localObject1);
      zza(paramEventParcel, paramAppMetadata);
      zzbvb().setTransactionSuccessful();
      if (zzbvg().zzbf(2)) {
        zzbvg().zzbwj().zzj("Event recorded", paramEventParcel);
      }
      zzbvb().endTransaction();
      zzbxm();
      zzbvg().zzbwj().zzj("Background event processing time, ms", Long.valueOf((System.nanoTime() - l2 + 500000L) / 1000000L));
      return;
      paramEventParcel = paramEventParcel.zza(this, ((zzi)localObject1).aov);
    }
  }
  
  @WorkerThread
  void zzb(EventParcel paramEventParcel, String paramString)
  {
    zza localzza = zzbvb().zzlz(paramString);
    if ((localzza == null) || (TextUtils.isEmpty(localzza.zzyt())))
    {
      zzbvg().zzbwi().zzj("No app data available; dropping event", paramString);
      return;
    }
    try
    {
      String str = getContext().getPackageManager().getPackageInfo(paramString, 0).versionName;
      if ((localzza.zzyt() != null) && (!localzza.zzyt().equals(str)))
      {
        zzbvg().zzbwe().zzj("App version does not match; dropping event", paramString);
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (!"_ui".equals(paramEventParcel.name)) {
        zzbvg().zzbwe().zzj("Could not find package", paramString);
      }
      zzb(paramEventParcel, new AppMetadata(paramString, localzza.zzbsr(), localzza.zzyt(), localzza.zzbsw(), localzza.zzbsx(), localzza.zzbsy(), localzza.zzbsz(), null, localzza.zzbta(), false, localzza.zzbst()));
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
    //   6: invokevirtual 388	com/google/android/gms/measurement/internal/zzx:zzyl	()V
    //   9: aload_0
    //   10: invokevirtual 391	com/google/android/gms/measurement/internal/zzx:zzaax	()V
    //   13: aload_2
    //   14: getfield 711	com/google/android/gms/measurement/internal/AppMetadata:anQ	Ljava/lang/String;
    //   17: invokestatic 585	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   20: ifeq +4 -> 24
    //   23: return
    //   24: aload_2
    //   25: getfield 771	com/google/android/gms/measurement/internal/AppMetadata:anV	Z
    //   28: ifne +9 -> 37
    //   31: aload_0
    //   32: aload_2
    //   33: invokespecial 1438	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   41: aload_1
    //   42: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   45: invokevirtual 1588	com/google/android/gms/measurement/internal/zzal:zznb	(Ljava/lang/String;)I
    //   48: istore 5
    //   50: iload 5
    //   52: ifeq +53 -> 105
    //   55: aload_0
    //   56: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   59: aload_1
    //   60: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   63: aload_0
    //   64: invokevirtual 131	com/google/android/gms/measurement/internal/zzx:zzbvi	()Lcom/google/android/gms/measurement/internal/zzd;
    //   67: invokevirtual 1591	com/google/android/gms/measurement/internal/zzd:zzbto	()I
    //   70: iconst_1
    //   71: invokevirtual 1594	com/google/android/gms/measurement/internal/zzal:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   74: astore_2
    //   75: aload_1
    //   76: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   79: ifnull +11 -> 90
    //   82: aload_1
    //   83: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   86: invokevirtual 367	java/lang/String:length	()I
    //   89: istore_3
    //   90: aload_0
    //   91: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   94: iload 5
    //   96: ldc_w 825
    //   99: aload_2
    //   100: iload_3
    //   101: invokevirtual 828	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   104: return
    //   105: aload_0
    //   106: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   109: aload_1
    //   110: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   113: aload_1
    //   114: invokevirtual 1597	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   117: invokevirtual 1600	com/google/android/gms/measurement/internal/zzal:zzm	(Ljava/lang/String;Ljava/lang/Object;)I
    //   120: istore 5
    //   122: iload 5
    //   124: ifeq +75 -> 199
    //   127: aload_0
    //   128: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   131: aload_1
    //   132: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   135: aload_0
    //   136: invokevirtual 131	com/google/android/gms/measurement/internal/zzx:zzbvi	()Lcom/google/android/gms/measurement/internal/zzd;
    //   139: invokevirtual 1591	com/google/android/gms/measurement/internal/zzd:zzbto	()I
    //   142: iconst_1
    //   143: invokevirtual 1594	com/google/android/gms/measurement/internal/zzal:zza	(Ljava/lang/String;IZ)Ljava/lang/String;
    //   146: astore_2
    //   147: aload_1
    //   148: invokevirtual 1597	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   151: astore_1
    //   152: iload 4
    //   154: istore_3
    //   155: aload_1
    //   156: ifnull +28 -> 184
    //   159: aload_1
    //   160: instanceof 360
    //   163: ifne +13 -> 176
    //   166: iload 4
    //   168: istore_3
    //   169: aload_1
    //   170: instanceof 1127
    //   173: ifeq +11 -> 184
    //   176: aload_1
    //   177: invokestatic 363	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   180: invokevirtual 367	java/lang/String:length	()I
    //   183: istore_3
    //   184: aload_0
    //   185: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   188: iload 5
    //   190: ldc_w 825
    //   193: aload_2
    //   194: iload_3
    //   195: invokevirtual 828	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   198: return
    //   199: aload_0
    //   200: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   203: aload_1
    //   204: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   207: aload_1
    //   208: invokevirtual 1597	com/google/android/gms/measurement/internal/UserAttributeParcel:getValue	()Ljava/lang/Object;
    //   211: invokevirtual 1603	com/google/android/gms/measurement/internal/zzal:zzn	(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    //   214: astore 7
    //   216: aload 7
    //   218: ifnull -195 -> 23
    //   221: new 1189	com/google/android/gms/measurement/internal/zzak
    //   224: dup
    //   225: aload_2
    //   226: getfield 687	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   229: aload_1
    //   230: getfield 1585	com/google/android/gms/measurement/internal/UserAttributeParcel:name	Ljava/lang/String;
    //   233: aload_1
    //   234: getfield 1606	com/google/android/gms/measurement/internal/UserAttributeParcel:asu	J
    //   237: aload 7
    //   239: invokespecial 1502	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   242: astore_1
    //   243: aload_0
    //   244: invokevirtual 121	com/google/android/gms/measurement/internal/zzx:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   247: invokevirtual 158	com/google/android/gms/measurement/internal/zzp:zzbwi	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   250: ldc_w 1608
    //   253: aload_1
    //   254: getfield 1190	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   257: aload 7
    //   259: invokevirtual 323	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   262: aload_0
    //   263: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   266: invokevirtual 434	com/google/android/gms/measurement/internal/zze:beginTransaction	()V
    //   269: aload_0
    //   270: aload_2
    //   271: invokespecial 1438	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   274: aload_0
    //   275: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   278: aload_1
    //   279: invokevirtual 1505	com/google/android/gms/measurement/internal/zze:zza	(Lcom/google/android/gms/measurement/internal/zzak;)Z
    //   282: istore 6
    //   284: aload_0
    //   285: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   288: invokevirtual 461	com/google/android/gms/measurement/internal/zze:setTransactionSuccessful	()V
    //   291: iload 6
    //   293: ifeq +32 -> 325
    //   296: aload_0
    //   297: invokevirtual 121	com/google/android/gms/measurement/internal/zzx:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   300: invokevirtual 158	com/google/android/gms/measurement/internal/zzp:zzbwi	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   303: ldc_w 1610
    //   306: aload_1
    //   307: getfield 1190	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   310: aload_1
    //   311: getfield 1200	com/google/android/gms/measurement/internal/zzak:zzctv	Ljava/lang/Object;
    //   314: invokevirtual 323	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload_0
    //   318: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   321: invokevirtual 458	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   324: return
    //   325: aload_0
    //   326: invokevirtual 121	com/google/android/gms/measurement/internal/zzx:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   329: invokevirtual 318	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   332: ldc_w 1507
    //   335: aload_1
    //   336: getfield 1190	com/google/android/gms/measurement/internal/zzak:mName	Ljava/lang/String;
    //   339: aload_1
    //   340: getfield 1200	com/google/android/gms/measurement/internal/zzak:zzctv	Ljava/lang/Object;
    //   343: invokevirtual 323	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   346: aload_0
    //   347: invokevirtual 204	com/google/android/gms/measurement/internal/zzx:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   350: bipush 9
    //   352: aconst_null
    //   353: aconst_null
    //   354: iconst_0
    //   355: invokevirtual 828	com/google/android/gms/measurement/internal/zzal:zza	(ILjava/lang/String;Ljava/lang/String;I)V
    //   358: goto -41 -> 317
    //   361: astore_1
    //   362: aload_0
    //   363: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   366: invokevirtual 458	com/google/android/gms/measurement/internal/zze:endTransaction	()V
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
  
  void zzb(zzaa paramzzaa)
  {
    this.arn += 1;
  }
  
  @WorkerThread
  void zzb(String paramString, int paramInt, Throwable paramThrowable, byte[] paramArrayOfByte, Map<String, List<String>> paramMap)
  {
    int j = 0;
    zzyl();
    zzaax();
    com.google.android.gms.common.internal.zzac.zzhz(paramString);
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    zzbvb().beginTransaction();
    label90:
    label105:
    int i;
    for (;;)
    {
      try
      {
        paramArrayOfByte = zzbvb().zzlz(paramString);
        if ((paramInt == 200) || (paramInt == 204)) {
          break label471;
        }
        if (paramInt == 304)
        {
          break label471;
          if (paramArrayOfByte == null)
          {
            zzbvg().zzbwe().zzj("App does not exist in onConfigFetched", paramString);
            zzbvb().setTransactionSuccessful();
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
            if (zzbvd().zzmp(paramString) != null) {
              continue;
            }
            bool = zzbvd().zzb(paramString, null, null);
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
        boolean bool = zzbvd().zzb(paramString, arrayOfByte, paramThrowable);
        if (!bool) {
          return;
        }
        paramArrayOfByte.zzbc(zzaan().currentTimeMillis());
        zzbvb().zza(paramArrayOfByte);
        if (paramInt == 404)
        {
          zzbvg().zzbwe().log("Config not found. Using empty config");
          if ((!zzbxa().zzafa()) || (!zzbxl())) {
            break label344;
          }
          zzbxk();
          continue;
        }
        zzbvg().zzbwj().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(paramInt), Integer.valueOf(arrayOfByte.length));
      }
      finally
      {
        zzbvb().endTransaction();
      }
      continue;
      label344:
      zzbxm();
    }
    paramArrayOfByte.zzbd(zzaan().currentTimeMillis());
    zzbvb().zza(paramArrayOfByte);
    zzbvg().zzbwj().zze("Fetching config failed. code, error", Integer.valueOf(paramInt), paramThrowable);
    zzbvd().zzmr(paramString);
    zzbvh().apR.set(zzaan().currentTimeMillis());
    if (paramInt != 503)
    {
      i = j;
      if (paramInt == 429) {}
    }
    for (;;)
    {
      if (i != 0) {
        zzbvh().apS.set(zzaan().currentTimeMillis());
      }
      zzbxm();
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
  
  boolean zzbo(long paramLong)
  {
    return zzj(null, paramLong);
  }
  
  public zzc zzbuw()
  {
    zza(this.arh);
    return this.arh;
  }
  
  public zzac zzbux()
  {
    zza(this.ard);
    return this.ard;
  }
  
  public zzn zzbuy()
  {
    zza(this.are);
    return this.are;
  }
  
  public zzg zzbuz()
  {
    zza(this.arc);
    return this.arc;
  }
  
  public zzad zzbva()
  {
    zza(this.arb);
    return this.arb;
  }
  
  public zze zzbvb()
  {
    zza(this.aqZ);
    return this.aqZ;
  }
  
  public zzal zzbvc()
  {
    zza(this.aqY);
    return this.aqY;
  }
  
  public zzv zzbvd()
  {
    zza(this.aqV);
    return this.aqV;
  }
  
  public zzaf zzbve()
  {
    zza(this.aqU);
    return this.aqU;
  }
  
  public zzw zzbvf()
  {
    zza(this.aqT);
    return this.aqT;
  }
  
  public zzp zzbvg()
  {
    zza(this.aqS);
    return this.aqS;
  }
  
  public zzt zzbvh()
  {
    zza(this.aqR);
    return this.aqR;
  }
  
  public zzd zzbvi()
  {
    return this.aqQ;
  }
  
  @WorkerThread
  protected boolean zzbwv()
  {
    boolean bool = true;
    zzaax();
    zzyl();
    if (this.arj == null)
    {
      if (zzbvi().zzact())
      {
        this.arj = Boolean.valueOf(true);
        return true;
      }
      if ((!zzbvc().zzew("android.permission.INTERNET")) || (!zzbvc().zzew("android.permission.ACCESS_NETWORK_STATE")) || (!zzu.zzh(getContext(), false)) || (!zzae.zzi(getContext(), false))) {
        break label132;
      }
    }
    for (;;)
    {
      this.arj = Boolean.valueOf(bool);
      if (this.arj.booleanValue()) {
        this.arj = Boolean.valueOf(zzbvc().zzne(zzbuy().zzbsr()));
      }
      return this.arj.booleanValue();
      label132:
      bool = false;
    }
  }
  
  public zzp zzbww()
  {
    if ((this.aqS != null) && (this.aqS.isInitialized())) {
      return this.aqS;
    }
    return null;
  }
  
  zzw zzbwx()
  {
    return this.aqT;
  }
  
  public AppMeasurement zzbwy()
  {
    return this.aqW;
  }
  
  public FirebaseAnalytics zzbwz()
  {
    return this.aqX;
  }
  
  public zzq zzbxa()
  {
    zza(this.ara);
    return this.ara;
  }
  
  public zzr zzbxb()
  {
    if (this.arf == null) {
      throw new IllegalStateException("Network broadcast receiver not created");
    }
    return this.arf;
  }
  
  public zzai zzbxc()
  {
    zza(this.arg);
    return this.arg;
  }
  
  FileChannel zzbxd()
  {
    return this.arl;
  }
  
  @WorkerThread
  void zzbxe()
  {
    zzyl();
    zzaax();
    if ((zzbxq()) && (zzbxf())) {
      zzu(zza(zzbxd()), zzbuy().zzbwa());
    }
  }
  
  @WorkerThread
  boolean zzbxf()
  {
    zzyl();
    Object localObject = this.aqZ.zzabs();
    localObject = new File(getContext().getFilesDir(), (String)localObject);
    try
    {
      this.arl = new RandomAccessFile((File)localObject, "rw").getChannel();
      this.ark = this.arl.tryLock();
      if (this.ark != null)
      {
        zzbvg().zzbwj().log("Storage concurrent access okay");
        return true;
      }
      zzbvg().zzbwc().log("Storage concurrent data access panic");
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Failed to acquire storage lock", localFileNotFoundException);
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Failed to access storage lock file", localIOException);
      }
    }
    return false;
  }
  
  public boolean zzbxg()
  {
    return false;
  }
  
  long zzbxh()
  {
    return (zzaan().currentTimeMillis() + zzbvh().zzbwn()) / 1000L / 60L / 60L / 24L;
  }
  
  void zzbxi()
  {
    if (!zzbvi().zzact()) {
      throw new IllegalStateException("Unexpected call on client side");
    }
  }
  
  @WorkerThread
  public void zzbxk()
  {
    Object localObject4 = null;
    int j = 0;
    zzyl();
    zzaax();
    if (!zzbvi().zzact())
    {
      localObject1 = zzbvh().zzbwq();
      if (localObject1 == null) {
        zzbvg().zzbwe().log("Upload data called on the client side before use of service was decided");
      }
    }
    long l1;
    String str;
    int i;
    do
    {
      return;
      if (((Boolean)localObject1).booleanValue())
      {
        zzbvg().zzbwc().log("Upload called in the client side when service should be used");
        return;
      }
      if (zzbxj())
      {
        zzbvg().zzbwe().log("Uploading requested multiple times");
        return;
      }
      if (!zzbxa().zzafa())
      {
        zzbvg().zzbwe().log("Network not connected, ignoring upload request");
        zzbxm();
        return;
      }
      l1 = zzaan().currentTimeMillis();
      zzbo(l1 - zzbvi().zzbuk());
      long l2 = zzbvh().apQ.get();
      if (l2 != 0L) {
        zzbvg().zzbwi().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(l1 - l2)));
      }
      str = zzbvb().zzbvj();
      if (TextUtils.isEmpty(str)) {
        break;
      }
      i = zzbvi().zzlv(str);
      int k = zzbvi().zzlw(str);
      localObject4 = zzbvb().zzn(str, i, k);
    } while (((List)localObject4).isEmpty());
    Object localObject1 = ((List)localObject4).iterator();
    Object localObject5;
    do
    {
      if (!((Iterator)localObject1).hasNext()) {
        break;
      }
      localObject5 = (zzvm.zze)((Pair)((Iterator)localObject1).next()).first;
    } while (TextUtils.isEmpty(((zzvm.zze)localObject5).atJ));
    Object localObject3;
    for (localObject1 = ((zzvm.zze)localObject5).atJ;; localObject3 = null)
    {
      if (localObject1 != null)
      {
        i = 0;
        if (i < ((List)localObject4).size())
        {
          localObject5 = (zzvm.zze)((Pair)((List)localObject4).get(i)).first;
          if (TextUtils.isEmpty(((zzvm.zze)localObject5).atJ)) {}
          while (((zzvm.zze)localObject5).atJ.equals(localObject1))
          {
            i += 1;
            break;
          }
        }
      }
      for (localObject1 = ((List)localObject4).subList(0, i);; localObject3 = localObject4)
      {
        localObject5 = new zzvm.zzd();
        ((zzvm.zzd)localObject5).att = new zzvm.zze[((List)localObject1).size()];
        localObject4 = new ArrayList(((List)localObject1).size());
        i = j;
        while (i < ((zzvm.zzd)localObject5).att.length)
        {
          ((zzvm.zzd)localObject5).att[i] = ((zzvm.zze)((Pair)((List)localObject1).get(i)).first);
          ((List)localObject4).add((Long)((Pair)((List)localObject1).get(i)).second);
          localObject5.att[i].atI = Long.valueOf(zzbvi().zzbsy());
          localObject5.att[i].aty = Long.valueOf(l1);
          localObject5.att[i].atO = Boolean.valueOf(zzbvi().zzact());
          i += 1;
        }
        if (zzbvg().zzbf(2)) {}
        for (localObject1 = zzal.zzb((zzvm.zzd)localObject5);; localObject3 = null)
        {
          Object localObject7 = zzbvc().zza((zzvm.zzd)localObject5);
          Object localObject6 = zzbvi().zzbuj();
          Object localObject8;
          try
          {
            localObject8 = new URL((String)localObject6);
            zzag((List)localObject4);
            zzbvh().apR.set(l1);
            localObject4 = "?";
            if (((zzvm.zzd)localObject5).att.length > 0) {
              localObject4 = localObject5.att[0].zzck;
            }
            zzbvg().zzbwj().zzd("Uploading data. app, uncompressed size, data", localObject4, Integer.valueOf(localObject7.length), localObject1);
            zzbxa().zza(str, (URL)localObject8, (byte[])localObject7, null, new zzq.zza()
            {
              public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
              {
                zzx.zza(zzx.this, paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte);
              }
            });
            return;
          }
          catch (MalformedURLException localMalformedURLException1)
          {
            zzbvg().zzbwc().zzj("Failed to parse upload URL. Not uploading", localObject6);
            return;
          }
          localObject5 = zzbvb().zzbl(l1 - zzbvi().zzbuk());
          if (TextUtils.isEmpty((CharSequence)localObject5)) {
            break;
          }
          Object localObject2 = zzbvb().zzlz((String)localObject5);
          if (localObject2 == null) {
            break;
          }
          str = zzbvi().zzap(((zza)localObject2).zzbsr(), ((zza)localObject2).zzayn());
          try
          {
            localObject6 = new URL(str);
            zzbvg().zzbwj().zzj("Fetching remote configuration", ((zza)localObject2).zzti());
            localObject7 = zzbvd().zzmp(((zza)localObject2).zzti());
            localObject8 = zzbvd().zzmq(((zza)localObject2).zzti());
            localObject2 = localObject4;
            if (localObject7 != null)
            {
              localObject2 = localObject4;
              if (!TextUtils.isEmpty((CharSequence)localObject8))
              {
                localObject2 = new ArrayMap();
                ((Map)localObject2).put("If-Modified-Since", localObject8);
              }
            }
            zzbxa().zza((String)localObject5, (URL)localObject6, (Map)localObject2, new zzq.zza()
            {
              public void zza(String paramAnonymousString, int paramAnonymousInt, Throwable paramAnonymousThrowable, byte[] paramAnonymousArrayOfByte, Map<String, List<String>> paramAnonymousMap)
              {
                zzx.this.zzb(paramAnonymousString, paramAnonymousInt, paramAnonymousThrowable, paramAnonymousArrayOfByte, paramAnonymousMap);
              }
            });
            return;
          }
          catch (MalformedURLException localMalformedURLException2)
          {
            zzbvg().zzbwc().zzj("Failed to parse config URL. Not fetching", str);
            return;
          }
        }
      }
    }
  }
  
  void zzbxo()
  {
    this.aro += 1;
  }
  
  @WorkerThread
  void zzbxp()
  {
    zzyl();
    zzaax();
    if (!this.ari)
    {
      zzbvg().zzbwh().log("This instance being marked as an uploader");
      zzbxe();
    }
    this.ari = true;
  }
  
  @WorkerThread
  boolean zzbxq()
  {
    zzyl();
    zzaax();
    return (this.ari) || (zzbxg());
  }
  
  void zzc(AppMetadata paramAppMetadata)
  {
    zzyl();
    zzaax();
    com.google.android.gms.common.internal.zzac.zzhz(paramAppMetadata.packageName);
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
    zzyl();
    zzaax();
    if (TextUtils.isEmpty(paramAppMetadata.anQ)) {
      return;
    }
    if (!paramAppMetadata.anV)
    {
      zze(paramAppMetadata);
      return;
    }
    zzbvg().zzbwi().zzj("Removing user property", paramUserAttributeParcel.name);
    zzbvb().beginTransaction();
    try
    {
      zze(paramAppMetadata);
      zzbvb().zzar(paramAppMetadata.packageName, paramUserAttributeParcel.name);
      zzbvb().setTransactionSuccessful();
      zzbvg().zzbwi().zzj("User property removed", paramUserAttributeParcel.name);
      return;
    }
    finally
    {
      zzbvb().endTransaction();
    }
  }
  
  /* Error */
  @WorkerThread
  public void zzd(AppMetadata paramAppMetadata)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 388	com/google/android/gms/measurement/internal/zzx:zzyl	()V
    //   4: aload_0
    //   5: invokevirtual 391	com/google/android/gms/measurement/internal/zzx:zzaax	()V
    //   8: aload_1
    //   9: invokestatic 81	com/google/android/gms/common/internal/zzac:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 687	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   17: invokestatic 548	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: getfield 711	com/google/android/gms/measurement/internal/AppMetadata:anQ	Ljava/lang/String;
    //   25: invokestatic 585	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   28: ifeq +4 -> 32
    //   31: return
    //   32: aload_1
    //   33: getfield 771	com/google/android/gms/measurement/internal/AppMetadata:anV	Z
    //   36: ifne +9 -> 45
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 1438	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   44: return
    //   45: aload_0
    //   46: invokevirtual 405	com/google/android/gms/measurement/internal/zzx:zzaan	()Lcom/google/android/gms/common/util/zze;
    //   49: invokeinterface 410 1 0
    //   54: lstore_2
    //   55: aload_0
    //   56: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   59: invokevirtual 434	com/google/android/gms/measurement/internal/zze:beginTransaction	()V
    //   62: aload_0
    //   63: aload_1
    //   64: lload_2
    //   65: invokevirtual 1854	com/google/android/gms/measurement/internal/zzx:zza	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   68: aload_0
    //   69: aload_1
    //   70: invokespecial 1438	com/google/android/gms/measurement/internal/zzx:zze	(Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   73: aload_0
    //   74: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   77: aload_1
    //   78: getfield 687	com/google/android/gms/measurement/internal/AppMetadata:packageName	Ljava/lang/String;
    //   81: ldc_w 1406
    //   84: invokevirtual 1309	com/google/android/gms/measurement/internal/zze:zzaq	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/measurement/internal/zzi;
    //   87: ifnonnull +63 -> 150
    //   90: aload_0
    //   91: new 1584	com/google/android/gms/measurement/internal/UserAttributeParcel
    //   94: dup
    //   95: ldc_w 1856
    //   98: lload_2
    //   99: lconst_1
    //   100: lload_2
    //   101: ldc2_w 1857
    //   104: ldiv
    //   105: ladd
    //   106: ldc2_w 1857
    //   109: lmul
    //   110: invokestatic 143	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   113: ldc_w 1087
    //   116: invokespecial 1861	com/google/android/gms/measurement/internal/UserAttributeParcel:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   119: aload_1
    //   120: invokevirtual 1863	com/google/android/gms/measurement/internal/zzx:zzb	(Lcom/google/android/gms/measurement/internal/UserAttributeParcel;Lcom/google/android/gms/measurement/internal/AppMetadata;)V
    //   123: aload_0
    //   124: aload_1
    //   125: lload_2
    //   126: invokevirtual 1865	com/google/android/gms/measurement/internal/zzx:zzb	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   129: aload_0
    //   130: aload_1
    //   131: lload_2
    //   132: invokevirtual 1867	com/google/android/gms/measurement/internal/zzx:zzc	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   135: aload_0
    //   136: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   139: invokevirtual 461	com/google/android/gms/measurement/internal/zze:setTransactionSuccessful	()V
    //   142: aload_0
    //   143: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   146: invokevirtual 458	com/google/android/gms/measurement/internal/zze:endTransaction	()V
    //   149: return
    //   150: aload_1
    //   151: getfield 1870	com/google/android/gms/measurement/internal/AppMetadata:anW	Z
    //   154: ifeq -19 -> 135
    //   157: aload_0
    //   158: aload_1
    //   159: lload_2
    //   160: invokevirtual 1872	com/google/android/gms/measurement/internal/zzx:zzd	(Lcom/google/android/gms/measurement/internal/AppMetadata;J)V
    //   163: goto -28 -> 135
    //   166: astore_1
    //   167: aload_0
    //   168: invokevirtual 431	com/google/android/gms/measurement/internal/zzx:zzbvb	()Lcom/google/android/gms/measurement/internal/zze;
    //   171: invokevirtual 458	com/google/android/gms/measurement/internal/zze:endTransaction	()V
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
  boolean zzu(int paramInt1, int paramInt2)
  {
    zzyl();
    if (paramInt1 > paramInt2)
    {
      zzbvg().zzbwc().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      return false;
    }
    if (paramInt1 < paramInt2)
    {
      if (zza(paramInt2, zzbxd())) {
        zzbvg().zzbwj().zze("Storage version upgraded. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      }
    }
    else {
      return true;
    }
    zzbvg().zzbwc().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    return false;
  }
  
  @WorkerThread
  public void zzyl()
  {
    zzbvf().zzyl();
  }
  
  private class zza
    implements zze.zzb
  {
    zzvm.zze arq;
    List<Long> arr;
    long ars;
    List<zzvm.zzb> zzamv;
    
    private zza() {}
    
    private long zza(zzvm.zzb paramzzb)
    {
      return paramzzb.atp.longValue() / 1000L / 60L / 60L;
    }
    
    boolean isEmpty()
    {
      return (this.zzamv == null) || (this.zzamv.isEmpty());
    }
    
    public boolean zza(long paramLong, zzvm.zzb paramzzb)
    {
      com.google.android.gms.common.internal.zzac.zzy(paramzzb);
      if (this.zzamv == null) {
        this.zzamv = new ArrayList();
      }
      if (this.arr == null) {
        this.arr = new ArrayList();
      }
      if ((this.zzamv.size() > 0) && (zza((zzvm.zzb)this.zzamv.get(0)) != zza(paramzzb))) {
        return false;
      }
      long l = this.ars + paramzzb.db();
      if (l >= zzx.this.zzbvi().zzbuh()) {
        return false;
      }
      this.ars = l;
      this.zzamv.add(paramzzb);
      this.arr.add(Long.valueOf(paramLong));
      return this.zzamv.size() < zzx.this.zzbvi().zzbui();
    }
    
    public void zzb(zzvm.zze paramzze)
    {
      com.google.android.gms.common.internal.zzac.zzy(paramzze);
      this.arq = paramzze;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */