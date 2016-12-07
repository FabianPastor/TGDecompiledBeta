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
import com.google.android.gms.internal.zzauh.zzb;
import com.google.android.gms.internal.zzauh.zzc;
import com.google.android.gms.internal.zzauh.zzd;
import com.google.android.gms.internal.zzauh.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzatp {
    private static volatile zzatp zzbtc;
    private final Context mContext;
    private final boolean zzacO;
    private long zzbtA;
    private FileLock zzbtB;
    private FileChannel zzbtC;
    private List<Long> zzbtD;
    private int zzbtE;
    private int zzbtF;
    private long zzbtG = -1;
    private final zzast zzbtd;
    private final zzatl zzbte;
    private final zzati zzbtf;
    private final zzato zzbtg;
    private final zzaty zzbth;
    private final zzatn zzbti;
    private final AppMeasurement zzbtj;
    private final FirebaseAnalytics zzbtk;
    private final zzaue zzbtl;
    private final zzasu zzbtm;
    private final zzatg zzbtn;
    private final zzatj zzbto;
    private final zzatv zzbtp;
    private final zzatw zzbtq;
    private final zzasw zzbtr;
    private final zzatu zzbts;
    private final zzatf zzbtt;
    private final zzatk zzbtu;
    private final zzaua zzbtv;
    private final zzass zzbtw;
    private final zzaso zzbtx;
    private boolean zzbty;
    private Boolean zzbtz;
    private final zze zzuI;

    private class zza implements zzb {
        final /* synthetic */ zzatp zzbtH;
        zzauh.zze zzbtI;
        List<Long> zzbtJ;
        long zzbtK;
        List<zzb> zztf;

        private zza(zzatp com_google_android_gms_internal_zzatp) {
            this.zzbtH = com_google_android_gms_internal_zzatp;
        }

        private long zza(zzb com_google_android_gms_internal_zzauh_zzb) {
            return ((com_google_android_gms_internal_zzauh_zzb.zzbvW.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zztf == null || this.zztf.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzauh_zzb) {
            zzac.zzw(com_google_android_gms_internal_zzauh_zzb);
            if (this.zztf == null) {
                this.zztf = new ArrayList();
            }
            if (this.zzbtJ == null) {
                this.zzbtJ = new ArrayList();
            }
            if (this.zztf.size() > 0 && zza((zzb) this.zztf.get(0)) != zza(com_google_android_gms_internal_zzauh_zzb)) {
                return false;
            }
            long zzacZ = this.zzbtK + ((long) com_google_android_gms_internal_zzauh_zzb.zzacZ());
            if (zzacZ >= ((long) this.zzbtH.zzJv().zzKr())) {
                return false;
            }
            this.zzbtK = zzacZ;
            this.zztf.add(com_google_android_gms_internal_zzauh_zzb);
            this.zzbtJ.add(Long.valueOf(j));
            return this.zztf.size() < this.zzbtH.zzJv().zzKs();
        }

        public void zzb(zzauh.zze com_google_android_gms_internal_zzauh_zze) {
            zzac.zzw(com_google_android_gms_internal_zzauh_zze);
            this.zzbtI = com_google_android_gms_internal_zzauh_zze;
        }
    }

    zzatp(zzatt com_google_android_gms_internal_zzatt) {
        zzac.zzw(com_google_android_gms_internal_zzatt);
        this.mContext = com_google_android_gms_internal_zzatt.mContext;
        this.zzuI = com_google_android_gms_internal_zzatt.zzn(this);
        this.zzbtd = com_google_android_gms_internal_zzatt.zza(this);
        zzatl zzb = com_google_android_gms_internal_zzatt.zzb(this);
        zzb.initialize();
        this.zzbte = zzb;
        zzati zzc = com_google_android_gms_internal_zzatt.zzc(this);
        zzc.initialize();
        this.zzbtf = zzc;
        zzJt().zzLe().zzj("App measurement is starting up, version", Long.valueOf(zzJv().zzJD()));
        zzJt().zzLe().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzJt().zzLf().log("Debug-level message logging enabled");
        zzJt().zzLf().zzj("AppMeasurement singleton hash", Integer.valueOf(System.identityHashCode(this)));
        zzaue zzj = com_google_android_gms_internal_zzatt.zzj(this);
        zzj.initialize();
        this.zzbtl = zzj;
        zzasw zzq = com_google_android_gms_internal_zzatt.zzq(this);
        zzq.initialize();
        this.zzbtr = zzq;
        zzatf zzr = com_google_android_gms_internal_zzatt.zzr(this);
        zzr.initialize();
        this.zzbtt = zzr;
        zzJv().zzKk();
        String zzjI = zzr.zzjI();
        if (zzJp().zzgh(zzjI)) {
            zzJt().zzLe().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
        } else {
            com.google.android.gms.internal.zzati.zza zzLe = zzJt().zzLe();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzjI = String.valueOf(zzjI);
            zzLe.log(zzjI.length() != 0 ? str.concat(zzjI) : new String(str));
        }
        zzasu zzk = com_google_android_gms_internal_zzatt.zzk(this);
        zzk.initialize();
        this.zzbtm = zzk;
        zzatg zzl = com_google_android_gms_internal_zzatt.zzl(this);
        zzl.initialize();
        this.zzbtn = zzl;
        zzass zzu = com_google_android_gms_internal_zzatt.zzu(this);
        zzu.initialize();
        this.zzbtw = zzu;
        this.zzbtx = com_google_android_gms_internal_zzatt.zzv(this);
        zzatj zzm = com_google_android_gms_internal_zzatt.zzm(this);
        zzm.initialize();
        this.zzbto = zzm;
        zzatv zzo = com_google_android_gms_internal_zzatt.zzo(this);
        zzo.initialize();
        this.zzbtp = zzo;
        zzatw zzp = com_google_android_gms_internal_zzatt.zzp(this);
        zzp.initialize();
        this.zzbtq = zzp;
        zzatu zzi = com_google_android_gms_internal_zzatt.zzi(this);
        zzi.initialize();
        this.zzbts = zzi;
        zzaua zzt = com_google_android_gms_internal_zzatt.zzt(this);
        zzt.initialize();
        this.zzbtv = zzt;
        this.zzbtu = com_google_android_gms_internal_zzatt.zzs(this);
        this.zzbtj = com_google_android_gms_internal_zzatt.zzh(this);
        this.zzbtk = com_google_android_gms_internal_zzatt.zzg(this);
        zzaty zze = com_google_android_gms_internal_zzatt.zze(this);
        zze.initialize();
        this.zzbth = zze;
        zzatn zzf = com_google_android_gms_internal_zzatt.zzf(this);
        zzf.initialize();
        this.zzbti = zzf;
        zzato zzd = com_google_android_gms_internal_zzatt.zzd(this);
        zzd.initialize();
        this.zzbtg = zzd;
        if (this.zzbtE != this.zzbtF) {
            zzJt().zzLa().zze("Not all components initialized", Integer.valueOf(this.zzbtE), Integer.valueOf(this.zzbtF));
        }
        this.zzacO = true;
        this.zzbtd.zzKk();
        if (!(this.mContext.getApplicationContext() instanceof Application)) {
            zzJt().zzLc().log("Application context is not an Application");
        } else if (VERSION.SDK_INT >= 14) {
            zzJi().zzLQ();
        } else {
            zzJt().zzLf().log("Not tracking deep linking pre-ICS");
        }
        this.zzbtg.zzm(new Runnable(this) {
            final /* synthetic */ zzatp zzbtH;

            {
                this.zzbtH = r1;
            }

            public void run() {
                this.zzbtH.start();
            }
        });
    }

    private boolean zzLH() {
        zzmq();
        zznA();
        return zzJo().zzKM() || !TextUtils.isEmpty(zzJo().zzKG());
    }

    @WorkerThread
    private void zzLI() {
        zzmq();
        zznA();
        if (!zzLM()) {
            return;
        }
        if (zzLt() && zzLH()) {
            long zzLJ = zzLJ();
            if (zzLJ == 0) {
                zzLz().unregister();
                zzLA().cancel();
                return;
            } else if (zzLy().zzpA()) {
                long j = zzJu().zzbsi.get();
                long zzKw = zzJv().zzKw();
                if (!zzJp().zzf(j, zzKw)) {
                    zzLJ = Math.max(zzLJ, j + zzKw);
                }
                zzLz().unregister();
                zzLJ -= zznq().currentTimeMillis();
                if (zzLJ <= 0) {
                    zzLJ = zzJv().zzKz();
                    zzJu().zzbsg.set(zznq().currentTimeMillis());
                }
                zzJt().zzLg().zzj("Upload scheduled in approximately ms", Long.valueOf(zzLJ));
                zzLA().zzx(zzLJ);
                return;
            } else {
                zzLz().zzpx();
                zzLA().cancel();
                return;
            }
        }
        zzLz().unregister();
        zzLA().cancel();
    }

    private long zzLJ() {
        long currentTimeMillis = zznq().currentTimeMillis();
        long zzKC = zzJv().zzKC();
        Object obj = (zzJo().zzKN() || zzJo().zzKH()) ? 1 : null;
        long zzKy = obj != null ? zzJv().zzKy() : zzJv().zzKx();
        long j = zzJu().zzbsg.get();
        long j2 = zzJu().zzbsh.get();
        long max = Math.max(zzJo().zzKK(), zzJo().zzKL());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzKC;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzKy;
        }
        if (!zzJp().zzf(j, zzKy)) {
            currentTimeMillis = j + zzKy;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzJv().zzKE(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzJv().zzKD();
            if (currentTimeMillis > j2) {
                return currentTimeMillis;
            }
        }
        return 0;
    }

    private void zza(zzatr com_google_android_gms_internal_zzatr) {
        if (com_google_android_gms_internal_zzatr == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private void zza(zzats com_google_android_gms_internal_zzats) {
        if (com_google_android_gms_internal_zzats == null) {
            throw new IllegalStateException("Component not created");
        } else if (!com_google_android_gms_internal_zzats.isInitialized()) {
            throw new IllegalStateException("Component not initialized");
        }
    }

    private boolean zza(zzasx com_google_android_gms_internal_zzasx) {
        if (com_google_android_gms_internal_zzasx.zzbqI == null) {
            return false;
        }
        Iterator it = com_google_android_gms_internal_zzasx.zzbqI.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzJq().zzY(com_google_android_gms_internal_zzasx.zzVQ, com_google_android_gms_internal_zzasx.mName) && zzJo().zza(zzLE(), com_google_android_gms_internal_zzasx.zzVQ, false, false, false, false, false).zzbqz < ((long) zzJv().zzfp(com_google_android_gms_internal_zzasx.zzVQ));
    }

    private com.google.android.gms.internal.zzauh.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzauh_zzgArr, zzb[] com_google_android_gms_internal_zzauh_zzbArr) {
        zzac.zzdv(str);
        return zzJh().zza(str, com_google_android_gms_internal_zzauh_zzbArr, com_google_android_gms_internal_zzauh_zzgArr);
    }

    public static zzatp zzbu(Context context) {
        zzac.zzw(context);
        zzac.zzw(context.getApplicationContext());
        if (zzbtc == null) {
            synchronized (zzatp.class) {
                if (zzbtc == null) {
                    zzbtc = new zzatt(context).zzLP();
                }
            }
        }
        return zzbtc;
    }

    @WorkerThread
    private void zzf(zzasq com_google_android_gms_internal_zzasq) {
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzasq);
        zzac.zzdv(com_google_android_gms_internal_zzasq.packageName);
        zzasp zzfy = zzJo().zzfy(com_google_android_gms_internal_zzasq.packageName);
        String zzfL = zzJu().zzfL(com_google_android_gms_internal_zzasq.packageName);
        Object obj = null;
        if (zzfy == null) {
            zzasp com_google_android_gms_internal_zzasp = new zzasp(this, com_google_android_gms_internal_zzasq.packageName);
            com_google_android_gms_internal_zzasp.zzfh(zzJu().zzLj());
            com_google_android_gms_internal_zzasp.zzfj(zzfL);
            zzfy = com_google_android_gms_internal_zzasp;
            obj = 1;
        } else if (!zzfL.equals(zzfy.zzJx())) {
            zzfy.zzfj(zzfL);
            zzfy.zzfh(zzJu().zzLj());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqf) || com_google_android_gms_internal_zzasq.zzbqf.equals(zzfy.getGmpAppId()))) {
            zzfy.zzfi(com_google_android_gms_internal_zzasq.zzbqf);
            obj = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqn) || com_google_android_gms_internal_zzasq.zzbqn.equals(zzfy.zzJy()))) {
            zzfy.zzfk(com_google_android_gms_internal_zzasq.zzbqn);
            obj = 1;
        }
        if (!(com_google_android_gms_internal_zzasq.zzbqh == 0 || com_google_android_gms_internal_zzasq.zzbqh == zzfy.zzJD())) {
            zzfy.zzaa(com_google_android_gms_internal_zzasq.zzbqh);
            obj = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbhg) || com_google_android_gms_internal_zzasq.zzbhg.equals(zzfy.zzmy()))) {
            zzfy.setAppVersion(com_google_android_gms_internal_zzasq.zzbhg);
            obj = 1;
        }
        if (com_google_android_gms_internal_zzasq.zzbqm != zzfy.zzJB()) {
            zzfy.zzZ(com_google_android_gms_internal_zzasq.zzbqm);
            obj = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqg) || com_google_android_gms_internal_zzasq.zzbqg.equals(zzfy.zzJC()))) {
            zzfy.zzfl(com_google_android_gms_internal_zzasq.zzbqg);
            obj = 1;
        }
        if (com_google_android_gms_internal_zzasq.zzbqi != zzfy.zzJE()) {
            zzfy.zzab(com_google_android_gms_internal_zzasq.zzbqi);
            obj = 1;
        }
        if (com_google_android_gms_internal_zzasq.zzbqk != zzfy.zzJF()) {
            zzfy.setMeasurementEnabled(com_google_android_gms_internal_zzasq.zzbqk);
            obj = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqj) || com_google_android_gms_internal_zzasq.zzbqj.equals(zzfy.zzJQ()))) {
            zzfy.zzfm(com_google_android_gms_internal_zzasq.zzbqj);
            obj = 1;
        }
        if (obj != null) {
            zzJo().zza(zzfy);
        }
    }

    private boolean zzj(String str, long j) {
        zzJo().beginTransaction();
        try {
            zzatp com_google_android_gms_internal_zzatp = this;
            zza com_google_android_gms_internal_zzatp_zza = new zza();
            zzJo().zza(str, j, this.zzbtG, com_google_android_gms_internal_zzatp_zza);
            if (com_google_android_gms_internal_zzatp_zza.isEmpty()) {
                zzJo().setTransactionSuccessful();
                zzJo().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzauh.zze com_google_android_gms_internal_zzauh_zze = com_google_android_gms_internal_zzatp_zza.zzbtI;
            com_google_android_gms_internal_zzauh_zze.zzbwd = new zzb[com_google_android_gms_internal_zzatp_zza.zztf.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_internal_zzatp_zza.zztf.size()) {
                boolean z2;
                Object obj;
                if (zzJq().zzX(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR, ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name)) {
                    zzJt().zzLc().zze("Dropping blacklisted raw event. appId", zzati.zzfI(str), ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name);
                    obj = (zzJp().zzgj(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR) || zzJp().zzgk(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR)) ? 1 : null;
                    if (obj != null || "_err".equals(((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name)) {
                        i = i2;
                        z2 = z;
                    } else {
                        zzJp().zza(11, "_ev", ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name, 0);
                        i = i2;
                        z2 = z;
                    }
                } else {
                    boolean zzfW;
                    int i4;
                    if (zzJq().zzY(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR, ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzauh_zzcArr;
                        zzc com_google_android_gms_internal_zzauh_zzc;
                        zzb com_google_android_gms_internal_zzauh_zzb;
                        Object obj2 = null;
                        Object obj3 = null;
                        if (((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV == null) {
                            ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzauh_zzcArr2 = ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV;
                        int length = com_google_android_gms_internal_zzauh_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            zzc com_google_android_gms_internal_zzauh_zzc2 = com_google_android_gms_internal_zzauh_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzauh_zzc2.name)) {
                                com_google_android_gms_internal_zzauh_zzc2.zzbvZ = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzauh_zzc2.name)) {
                                com_google_android_gms_internal_zzauh_zzc2.zzbvZ = Long.valueOf(1);
                                obj = 1;
                            } else {
                                obj = obj3;
                            }
                            i5++;
                            obj3 = obj;
                        }
                        if (obj2 == null) {
                            zzJt().zzLg().zzj("Marking event as conversion", ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name);
                            com_google_android_gms_internal_zzauh_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV, ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV.length + 1);
                            com_google_android_gms_internal_zzauh_zzc = new zzc();
                            com_google_android_gms_internal_zzauh_zzc.name = "_c";
                            com_google_android_gms_internal_zzauh_zzc.zzbvZ = Long.valueOf(1);
                            com_google_android_gms_internal_zzauh_zzcArr[com_google_android_gms_internal_zzauh_zzcArr.length - 1] = com_google_android_gms_internal_zzauh_zzc;
                            ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV = com_google_android_gms_internal_zzauh_zzcArr;
                        }
                        if (obj3 == null) {
                            zzJt().zzLg().zzj("Marking event as real-time", ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name);
                            com_google_android_gms_internal_zzauh_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV, ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV.length + 1);
                            com_google_android_gms_internal_zzauh_zzc = new zzc();
                            com_google_android_gms_internal_zzauh_zzc.name = "_r";
                            com_google_android_gms_internal_zzauh_zzc.zzbvZ = Long.valueOf(1);
                            com_google_android_gms_internal_zzauh_zzcArr[com_google_android_gms_internal_zzauh_zzcArr.length - 1] = com_google_android_gms_internal_zzauh_zzc;
                            ((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).zzbvV = com_google_android_gms_internal_zzauh_zzcArr;
                        }
                        boolean z3 = true;
                        zzfW = zzaue.zzfW(((zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3)).name);
                        if (zzJo().zza(zzLE(), com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR, false, false, false, false, true).zzbqz > ((long) zzJv().zzfp(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR))) {
                            com_google_android_gms_internal_zzauh_zzb = (zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzauh_zzb.zzbvV.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzauh_zzb.zzbvV[i4].name)) {
                                    Object obj4 = new zzc[(com_google_android_gms_internal_zzauh_zzb.zzbvV.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzauh_zzb.zzbvV, 0, obj4, 0, i4);
                                    }
                                    if (i4 < obj4.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzauh_zzb.zzbvV, i4 + 1, obj4, i4, obj4.length - i4);
                                    }
                                    com_google_android_gms_internal_zzauh_zzb.zzbvV = obj4;
                                    z3 = z;
                                } else {
                                    i4++;
                                }
                            }
                            z3 = z;
                        }
                        if (zzfW && zzJo().zza(zzLE(), com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR, false, false, true, false, false).zzbqx > ((long) zzJv().zzfo(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR))) {
                            zzJt().zzLc().zzj("Too many conversions. Not logging as conversion. appId", zzati.zzfI(str));
                            com_google_android_gms_internal_zzauh_zzb = (zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3);
                            Object obj5 = null;
                            zzc com_google_android_gms_internal_zzauh_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzauh_zzcArr3 = com_google_android_gms_internal_zzauh_zzb.zzbvV;
                            int length2 = com_google_android_gms_internal_zzauh_zzcArr3.length;
                            int i6 = 0;
                            while (i6 < length2) {
                                com_google_android_gms_internal_zzauh_zzc = com_google_android_gms_internal_zzauh_zzcArr3[i6];
                                if ("_c".equals(com_google_android_gms_internal_zzauh_zzc.name)) {
                                    obj3 = obj5;
                                } else if ("_err".equals(com_google_android_gms_internal_zzauh_zzc.name)) {
                                    zzc com_google_android_gms_internal_zzauh_zzc4 = com_google_android_gms_internal_zzauh_zzc3;
                                    int i7 = 1;
                                    com_google_android_gms_internal_zzauh_zzc = com_google_android_gms_internal_zzauh_zzc4;
                                } else {
                                    com_google_android_gms_internal_zzauh_zzc = com_google_android_gms_internal_zzauh_zzc3;
                                    obj3 = obj5;
                                }
                                i6++;
                                obj5 = obj3;
                                com_google_android_gms_internal_zzauh_zzc3 = com_google_android_gms_internal_zzauh_zzc;
                            }
                            if (obj5 != null && com_google_android_gms_internal_zzauh_zzc3 != null) {
                                com_google_android_gms_internal_zzauh_zzcArr3 = new zzc[(com_google_android_gms_internal_zzauh_zzb.zzbvV.length - 1)];
                                int i8 = 0;
                                zzc[] com_google_android_gms_internal_zzauh_zzcArr4 = com_google_android_gms_internal_zzauh_zzb.zzbvV;
                                int length3 = com_google_android_gms_internal_zzauh_zzcArr4.length;
                                i6 = 0;
                                while (i6 < length3) {
                                    zzc com_google_android_gms_internal_zzauh_zzc5 = com_google_android_gms_internal_zzauh_zzcArr4[i6];
                                    if (com_google_android_gms_internal_zzauh_zzc5 != com_google_android_gms_internal_zzauh_zzc3) {
                                        i4 = i8 + 1;
                                        com_google_android_gms_internal_zzauh_zzcArr3[i8] = com_google_android_gms_internal_zzauh_zzc5;
                                    } else {
                                        i4 = i8;
                                    }
                                    i6++;
                                    i8 = i4;
                                }
                                com_google_android_gms_internal_zzauh_zzb.zzbvV = com_google_android_gms_internal_zzauh_zzcArr3;
                                zzfW = z3;
                            } else if (com_google_android_gms_internal_zzauh_zzc3 != null) {
                                com_google_android_gms_internal_zzauh_zzc3.name = "_err";
                                com_google_android_gms_internal_zzauh_zzc3.zzbvZ = Long.valueOf(10);
                                zzfW = z3;
                            } else {
                                zzJt().zzLa().zzj("Did not find conversion parameter. appId", zzati.zzfI(str));
                            }
                        }
                        zzfW = z3;
                    } else {
                        zzfW = z;
                    }
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzauh_zze.zzbwd[i2] = (zzb) com_google_android_gms_internal_zzatp_zza.zztf.get(i3);
                    i = i4;
                    z2 = zzfW;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_internal_zzatp_zza.zztf.size()) {
                com_google_android_gms_internal_zzauh_zze.zzbwd = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzauh_zze.zzbwd, i2);
            }
            com_google_android_gms_internal_zzauh_zze.zzbww = zza(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR, com_google_android_gms_internal_zzatp_zza.zzbtI.zzbwe, com_google_android_gms_internal_zzauh_zze.zzbwd);
            com_google_android_gms_internal_zzauh_zze.zzbwg = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzauh_zze.zzbwh = Long.valueOf(Long.MIN_VALUE);
            for (zzb com_google_android_gms_internal_zzauh_zzb2 : com_google_android_gms_internal_zzauh_zze.zzbwd) {
                if (com_google_android_gms_internal_zzauh_zzb2.zzbvW.longValue() < com_google_android_gms_internal_zzauh_zze.zzbwg.longValue()) {
                    com_google_android_gms_internal_zzauh_zze.zzbwg = com_google_android_gms_internal_zzauh_zzb2.zzbvW;
                }
                if (com_google_android_gms_internal_zzauh_zzb2.zzbvW.longValue() > com_google_android_gms_internal_zzauh_zze.zzbwh.longValue()) {
                    com_google_android_gms_internal_zzauh_zze.zzbwh = com_google_android_gms_internal_zzauh_zzb2.zzbvW;
                }
            }
            String str2 = com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR;
            zzasp zzfy = zzJo().zzfy(str2);
            if (zzfy == null) {
                zzJt().zzLa().zzj("Bundling raw events w/o app info. appId", zzati.zzfI(str));
            } else if (com_google_android_gms_internal_zzauh_zze.zzbwd.length > 0) {
                long zzJA = zzfy.zzJA();
                com_google_android_gms_internal_zzauh_zze.zzbwj = zzJA != 0 ? Long.valueOf(zzJA) : null;
                long zzJz = zzfy.zzJz();
                if (zzJz != 0) {
                    zzJA = zzJz;
                }
                com_google_android_gms_internal_zzauh_zze.zzbwi = zzJA != 0 ? Long.valueOf(zzJA) : null;
                zzfy.zzJJ();
                com_google_android_gms_internal_zzauh_zze.zzbwu = Integer.valueOf((int) zzfy.zzJG());
                zzfy.zzX(com_google_android_gms_internal_zzauh_zze.zzbwg.longValue());
                zzfy.zzY(com_google_android_gms_internal_zzauh_zze.zzbwh.longValue());
                com_google_android_gms_internal_zzauh_zze.zzbqj = zzfy.zzJR();
                zzJo().zza(zzfy);
            }
            if (com_google_android_gms_internal_zzauh_zze.zzbwd.length > 0) {
                zzJv().zzKk();
                zzaug.zzb zzfO = zzJq().zzfO(com_google_android_gms_internal_zzatp_zza.zzbtI.zzaR);
                if (zzfO == null || zzfO.zzbvK == null) {
                    zzJt().zzLc().zzj("Did not find measurement config or missing version info. appId", zzati.zzfI(str));
                } else {
                    com_google_android_gms_internal_zzauh_zze.zzbwB = zzfO.zzbvK;
                }
                zzJo().zza(com_google_android_gms_internal_zzauh_zze, z);
            }
            zzJo().zzG(com_google_android_gms_internal_zzatp_zza.zzbtJ);
            zzJo().zzfF(str2);
            zzJo().setTransactionSuccessful();
            boolean z4 = com_google_android_gms_internal_zzauh_zze.zzbwd.length > 0;
            zzJo().endTransaction();
            return z4;
        } catch (Throwable th) {
            zzJo().endTransaction();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public String getGmpAppIdOnPackageSide(final String str) {
        Object e;
        zzJd();
        try {
            return (String) zzJs().zze(new Callable<String>(this) {
                final /* synthetic */ zzatp zzbtH;

                public /* synthetic */ Object call() throws Exception {
                    return zzou();
                }

                public String zzou() throws Exception {
                    zzasp zzfy = this.zzbtH.zzJo().zzfy(str);
                    return zzfy == null ? null : zzfy.getGmpAppId();
                }
            }).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzJt().zzLa().zze("Failed to get gmp app id. appId", zzati.zzfI(str), e);
        return null;
    }

    @WorkerThread
    public boolean isEnabled() {
        boolean z = false;
        zzmq();
        zznA();
        if (zzJv().zzKl()) {
            return false;
        }
        Boolean zzKm = zzJv().zzKm();
        if (zzKm != null) {
            z = zzKm.booleanValue();
        } else if (!zzJv().zzwk()) {
            z = true;
        }
        return zzJu().zzaG(z);
    }

    @WorkerThread
    protected void start() {
        zzmq();
        zzJo().zzKI();
        if (zzJu().zzbsg.get() == 0) {
            zzJu().zzbsg.set(zznq().currentTimeMillis());
        }
        if (zzLt()) {
            zzJv().zzKk();
            if (!TextUtils.isEmpty(zzJj().getGmpAppId())) {
                String zzLm = zzJu().zzLm();
                if (zzLm == null) {
                    zzJu().zzfM(zzJj().getGmpAppId());
                } else if (!zzLm.equals(zzJj().getGmpAppId())) {
                    zzJt().zzLe().log("Rechecking which service to use due to a GMP App Id change");
                    zzJu().zzLo();
                    this.zzbtq.disconnect();
                    this.zzbtq.zzoc();
                    zzJu().zzfM(zzJj().getGmpAppId());
                }
            }
            zzJv().zzKk();
            if (!TextUtils.isEmpty(zzJj().getGmpAppId())) {
                zzJi().zzLR();
            }
        } else if (isEnabled()) {
            if (!zzJp().zzbV("android.permission.INTERNET")) {
                zzJt().zzLa().log("App is missing INTERNET permission");
            }
            if (!zzJp().zzbV("android.permission.ACCESS_NETWORK_STATE")) {
                zzJt().zzLa().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzJv().zzKk();
            zzacx.zzaQ(getContext());
            if (!zzatm.zzi(getContext(), false)) {
                zzJt().zzLa().log("AppMeasurementReceiver not registered/enabled");
            }
            if (!zzatx.zzj(getContext(), false)) {
                zzJt().zzLa().log("AppMeasurementService not registered/enabled");
            }
            zzJt().zzLa().log("Uploading is not possible. App measurement disabled");
        }
        zzLI();
    }

    protected void zzH(List<Long> list) {
        zzac.zzas(!list.isEmpty());
        if (this.zzbtD != null) {
            zzJt().zzLa().log("Set uploading progress before finishing the previous upload");
        } else {
            this.zzbtD = new ArrayList(list);
        }
    }

    void zzJd() {
        zzJv().zzKk();
        throw new IllegalStateException("Unexpected call on client side");
    }

    void zzJe() {
        zzJv().zzKk();
    }

    public zzaso zzJg() {
        zza(this.zzbtx);
        return this.zzbtx;
    }

    public zzass zzJh() {
        zza(this.zzbtw);
        return this.zzbtw;
    }

    public zzatu zzJi() {
        zza(this.zzbts);
        return this.zzbts;
    }

    public zzatf zzJj() {
        zza(this.zzbtt);
        return this.zzbtt;
    }

    public zzasw zzJk() {
        zza(this.zzbtr);
        return this.zzbtr;
    }

    public zzatw zzJl() {
        zza(this.zzbtq);
        return this.zzbtq;
    }

    public zzatv zzJm() {
        zza(this.zzbtp);
        return this.zzbtp;
    }

    public zzatg zzJn() {
        zza(this.zzbtn);
        return this.zzbtn;
    }

    public zzasu zzJo() {
        zza(this.zzbtm);
        return this.zzbtm;
    }

    public zzaue zzJp() {
        zza(this.zzbtl);
        return this.zzbtl;
    }

    public zzatn zzJq() {
        zza(this.zzbti);
        return this.zzbti;
    }

    public zzaty zzJr() {
        zza(this.zzbth);
        return this.zzbth;
    }

    public zzato zzJs() {
        zza(this.zzbtg);
        return this.zzbtg;
    }

    public zzati zzJt() {
        zza(this.zzbtf);
        return this.zzbtf;
    }

    public zzatl zzJu() {
        zza(this.zzbte);
        return this.zzbte;
    }

    public zzast zzJv() {
        return this.zzbtd;
    }

    public zzaua zzLA() {
        zza(this.zzbtv);
        return this.zzbtv;
    }

    FileChannel zzLB() {
        return this.zzbtC;
    }

    @WorkerThread
    void zzLC() {
        zzmq();
        zznA();
        if (zzLM() && zzLD()) {
            zzv(zza(zzLB()), zzJj().zzKZ());
        }
    }

    @WorkerThread
    boolean zzLD() {
        zzmq();
        try {
            this.zzbtC = new RandomAccessFile(new File(getContext().getFilesDir(), this.zzbtm.zznV()), "rw").getChannel();
            this.zzbtB = this.zzbtC.tryLock();
            if (this.zzbtB != null) {
                zzJt().zzLg().log("Storage concurrent access okay");
                return true;
            }
            zzJt().zzLa().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzJt().zzLa().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzJt().zzLa().zzj("Failed to access storage lock file", e2);
        }
    }

    long zzLE() {
        return ((((zznq().currentTimeMillis() + zzJu().zzLk()) / 1000) / 60) / 60) / 24;
    }

    @WorkerThread
    protected boolean zzLF() {
        zzmq();
        return this.zzbtD != null;
    }

    @WorkerThread
    public void zzLG() {
        int i = 0;
        zzmq();
        zznA();
        zzJv().zzKk();
        Boolean zzLn = zzJu().zzLn();
        if (zzLn == null) {
            zzJt().zzLc().log("Upload data called on the client side before use of service was decided");
        } else if (zzLn.booleanValue()) {
            zzJt().zzLa().log("Upload called in the client side when service should be used");
        } else if (zzLF()) {
            zzJt().zzLc().log("Uploading requested multiple times");
        } else if (zzLy().zzpA()) {
            long currentTimeMillis = zznq().currentTimeMillis();
            zzao(currentTimeMillis - zzJv().zzKv());
            long j = zzJu().zzbsg.get();
            if (j != 0) {
                zzJt().zzLf().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzKG = zzJo().zzKG();
            Object zzam;
            if (TextUtils.isEmpty(zzKG)) {
                this.zzbtG = -1;
                zzam = zzJo().zzam(currentTimeMillis - zzJv().zzKv());
                if (!TextUtils.isEmpty(zzam)) {
                    zzasp zzfy = zzJo().zzfy(zzam);
                    if (zzfy != null) {
                        zzb(zzfy);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.zzbtG == -1) {
                this.zzbtG = zzJo().zzKO();
            }
            List<Pair> zzn = zzJo().zzn(zzKG, zzJv().zzfu(zzKG), zzJv().zzfv(zzKG));
            if (!zzn.isEmpty()) {
                zzauh.zze com_google_android_gms_internal_zzauh_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzauh_zze = (zzauh.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauh_zze.zzbwq)) {
                        obj = com_google_android_gms_internal_zzauh_zze.zzbwq;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzauh_zze = (zzauh.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauh_zze.zzbwq) && !com_google_android_gms_internal_zzauh_zze.zzbwq.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzauh_zzd = new zzd();
                com_google_android_gms_internal_zzauh_zzd.zzbwa = new zzauh.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzauh_zzd.zzbwa.length) {
                    com_google_android_gms_internal_zzauh_zzd.zzbwa[i] = (zzauh.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzauh_zzd.zzbwa[i].zzbwp = Long.valueOf(zzJv().zzJD());
                    com_google_android_gms_internal_zzauh_zzd.zzbwa[i].zzbwf = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzauh_zzd.zzbwa[i].zzbwv = Boolean.valueOf(zzJv().zzKk());
                    i++;
                }
                zzam = zzJt().zzai(2) ? zzaue.zzb(com_google_android_gms_internal_zzauh_zzd) : null;
                byte[] zza = zzJp().zza(com_google_android_gms_internal_zzauh_zzd);
                String zzKu = zzJv().zzKu();
                try {
                    URL url = new URL(zzKu);
                    zzH(arrayList);
                    zzJu().zzbsh.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzauh_zzd.zzbwa.length > 0) {
                        obj2 = com_google_android_gms_internal_zzauh_zzd.zzbwa[0].zzaR;
                    }
                    zzJt().zzLg().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzam);
                    zzLy().zza(zzKG, url, zza, null, new zza(this) {
                        final /* synthetic */ zzatp zzbtH;

                        {
                            this.zzbtH = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.zzbtH.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e) {
                    zzJt().zzLa().zze("Failed to parse upload URL. Not uploading. appId", zzati.zzfI(zzKG), zzKu);
                }
            }
        } else {
            zzJt().zzLc().log("Network not connected, ignoring upload request");
            zzLI();
        }
    }

    void zzLK() {
        this.zzbtF++;
    }

    @WorkerThread
    void zzLL() {
        zzmq();
        zznA();
        if (!this.zzbty) {
            zzJt().zzLe().log("This instance being marked as an uploader");
            zzLC();
        }
        this.zzbty = true;
    }

    @WorkerThread
    boolean zzLM() {
        zzmq();
        zznA();
        return this.zzbty;
    }

    @WorkerThread
    protected boolean zzLt() {
        boolean z = false;
        zznA();
        zzmq();
        if (this.zzbtz == null || this.zzbtA == 0 || !(this.zzbtz == null || this.zzbtz.booleanValue() || Math.abs(zznq().elapsedRealtime() - this.zzbtA) <= 1000)) {
            this.zzbtA = zznq().elapsedRealtime();
            zzJv().zzKk();
            if (zzJp().zzbV("android.permission.INTERNET") && zzJp().zzbV("android.permission.ACCESS_NETWORK_STATE")) {
                zzacx.zzaQ(getContext());
                if (zzatm.zzi(getContext(), false) && zzatx.zzj(getContext(), false)) {
                    z = true;
                }
            }
            this.zzbtz = Boolean.valueOf(z);
            if (this.zzbtz.booleanValue()) {
                this.zzbtz = Boolean.valueOf(zzJp().zzgd(zzJj().getGmpAppId()));
            }
        }
        return this.zzbtz.booleanValue();
    }

    public zzati zzLu() {
        return (this.zzbtf == null || !this.zzbtf.isInitialized()) ? null : this.zzbtf;
    }

    zzato zzLv() {
        return this.zzbtg;
    }

    public AppMeasurement zzLw() {
        return this.zzbtj;
    }

    public FirebaseAnalytics zzLx() {
        return this.zzbtk;
    }

    public zzatj zzLy() {
        zza(this.zzbto);
        return this.zzbto;
    }

    public zzatk zzLz() {
        if (this.zzbtu != null) {
            return this.zzbtu;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public void zzV(boolean z) {
        zzLI();
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzmq();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzJt().zzLa().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzJt().zzLc().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzJt().zzLa().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    @WorkerThread
    protected void zza(int i, Throwable th, byte[] bArr) {
        int i2 = 0;
        zzmq();
        zznA();
        if (bArr == null) {
            bArr = new byte[0];
        }
        List<Long> list = this.zzbtD;
        this.zzbtD = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            zzJu().zzbsg.set(zznq().currentTimeMillis());
            zzJu().zzbsh.set(0);
            zzLI();
            zzJt().zzLg().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
            zzJo().beginTransaction();
            try {
                for (Long longValue : list) {
                    zzJo().zzal(longValue.longValue());
                }
                zzJo().setTransactionSuccessful();
                if (zzLy().zzpA() && zzLH()) {
                    zzLG();
                    return;
                }
                this.zzbtG = -1;
                zzLI();
            } finally {
                zzJo().endTransaction();
            }
        } else {
            zzJt().zzLg().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzJu().zzbsh.set(zznq().currentTimeMillis());
            if (i == 503 || i == 429) {
                i2 = 1;
            }
            if (i2 != 0) {
                zzJu().zzbsi.set(zznq().currentTimeMillis());
            }
            zzLI();
        }
    }

    @WorkerThread
    void zza(zzasq com_google_android_gms_internal_zzasq, long j) {
        zzasp zzfy = zzJo().zzfy(com_google_android_gms_internal_zzasq.packageName);
        if (!(zzfy == null || zzfy.getGmpAppId() == null || zzfy.getGmpAppId().equals(com_google_android_gms_internal_zzasq.zzbqf))) {
            zzJt().zzLc().zzj("New GMP App Id passed in. Removing cached database data. appId", zzati.zzfI(zzfy.zzjI()));
            zzJo().zzfD(zzfy.zzjI());
            zzfy = null;
        }
        if (zzfy != null && zzfy.zzmy() != null && !zzfy.zzmy().equals(com_google_android_gms_internal_zzasq.zzbhg)) {
            Bundle bundle = new Bundle();
            bundle.putString("_pv", zzfy.zzmy());
            zzb(new zzatb("_au", new zzasz(bundle), "auto", j), com_google_android_gms_internal_zzasq);
        }
    }

    void zza(zzasx com_google_android_gms_internal_zzasx, zzasq com_google_android_gms_internal_zzasq) {
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzasx);
        zzac.zzw(com_google_android_gms_internal_zzasq);
        zzac.zzdv(com_google_android_gms_internal_zzasx.zzVQ);
        zzac.zzas(com_google_android_gms_internal_zzasx.zzVQ.equals(com_google_android_gms_internal_zzasq.packageName));
        zzauh.zze com_google_android_gms_internal_zzauh_zze = new zzauh.zze();
        com_google_android_gms_internal_zzauh_zze.zzbwc = Integer.valueOf(1);
        com_google_android_gms_internal_zzauh_zze.zzbwk = "android";
        com_google_android_gms_internal_zzauh_zze.zzaR = com_google_android_gms_internal_zzasq.packageName;
        com_google_android_gms_internal_zzauh_zze.zzbqg = com_google_android_gms_internal_zzasq.zzbqg;
        com_google_android_gms_internal_zzauh_zze.zzbhg = com_google_android_gms_internal_zzasq.zzbhg;
        com_google_android_gms_internal_zzauh_zze.zzbwx = Integer.valueOf((int) com_google_android_gms_internal_zzasq.zzbqm);
        com_google_android_gms_internal_zzauh_zze.zzbwo = Long.valueOf(com_google_android_gms_internal_zzasq.zzbqh);
        com_google_android_gms_internal_zzauh_zze.zzbqf = com_google_android_gms_internal_zzasq.zzbqf;
        com_google_android_gms_internal_zzauh_zze.zzbwt = com_google_android_gms_internal_zzasq.zzbqi == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzasq.zzbqi);
        Pair zzfK = zzJu().zzfK(com_google_android_gms_internal_zzasq.packageName);
        if (!TextUtils.isEmpty((CharSequence) zzfK.first)) {
            com_google_android_gms_internal_zzauh_zze.zzbwq = (String) zzfK.first;
            com_google_android_gms_internal_zzauh_zze.zzbwr = (Boolean) zzfK.second;
        } else if (!zzJk().zzbt(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzJt().zzLc().zzj("null secure ID. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR));
                string = "null";
            } else if (string.isEmpty()) {
                zzJt().zzLc().zzj("empty secure ID. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR));
            }
            com_google_android_gms_internal_zzauh_zze.zzbwA = string;
        }
        com_google_android_gms_internal_zzauh_zze.zzbwl = zzJk().zzkm();
        com_google_android_gms_internal_zzauh_zze.zzba = zzJk().zzKU();
        com_google_android_gms_internal_zzauh_zze.zzbwn = Integer.valueOf((int) zzJk().zzKV());
        com_google_android_gms_internal_zzauh_zze.zzbwm = zzJk().zzKW();
        com_google_android_gms_internal_zzauh_zze.zzbwp = null;
        com_google_android_gms_internal_zzauh_zze.zzbwf = null;
        com_google_android_gms_internal_zzauh_zze.zzbwg = null;
        com_google_android_gms_internal_zzauh_zze.zzbwh = null;
        zzasp zzfy = zzJo().zzfy(com_google_android_gms_internal_zzasq.packageName);
        if (zzfy == null) {
            zzfy = new zzasp(this, com_google_android_gms_internal_zzasq.packageName);
            zzfy.zzfh(zzJu().zzLj());
            zzfy.zzfk(com_google_android_gms_internal_zzasq.zzbqn);
            zzfy.zzfi(com_google_android_gms_internal_zzasq.zzbqf);
            zzfy.zzfj(zzJu().zzfL(com_google_android_gms_internal_zzasq.packageName));
            zzfy.zzac(0);
            zzfy.zzX(0);
            zzfy.zzY(0);
            zzfy.setAppVersion(com_google_android_gms_internal_zzasq.zzbhg);
            zzfy.zzZ(com_google_android_gms_internal_zzasq.zzbqm);
            zzfy.zzfl(com_google_android_gms_internal_zzasq.zzbqg);
            zzfy.zzaa(com_google_android_gms_internal_zzasq.zzbqh);
            zzfy.zzab(com_google_android_gms_internal_zzasq.zzbqi);
            zzfy.setMeasurementEnabled(com_google_android_gms_internal_zzasq.zzbqk);
            zzJo().zza(zzfy);
        }
        com_google_android_gms_internal_zzauh_zze.zzbws = zzfy.getAppInstanceId();
        com_google_android_gms_internal_zzauh_zze.zzbqn = zzfy.zzJy();
        List zzfx = zzJo().zzfx(com_google_android_gms_internal_zzasq.packageName);
        com_google_android_gms_internal_zzauh_zze.zzbwe = new zzg[zzfx.size()];
        for (int i = 0; i < zzfx.size(); i++) {
            zzg com_google_android_gms_internal_zzauh_zzg = new zzg();
            com_google_android_gms_internal_zzauh_zze.zzbwe[i] = com_google_android_gms_internal_zzauh_zzg;
            com_google_android_gms_internal_zzauh_zzg.name = ((zzaud) zzfx.get(i)).mName;
            com_google_android_gms_internal_zzauh_zzg.zzbwF = Long.valueOf(((zzaud) zzfx.get(i)).zzbvd);
            zzJp().zza(com_google_android_gms_internal_zzauh_zzg, ((zzaud) zzfx.get(i)).zzYe);
        }
        try {
            zzJo().zza(com_google_android_gms_internal_zzasx, zzJo().zza(com_google_android_gms_internal_zzauh_zze), zza(com_google_android_gms_internal_zzasx));
        } catch (IOException e) {
            zzJt().zzLa().zze("Data loss. Failed to insert raw event metadata. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzmq();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzJt().zzLa().log("Bad chanel to read from");
            return false;
        }
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(i);
        allocate.flip();
        try {
            fileChannel.truncate(0);
            fileChannel.write(allocate);
            fileChannel.force(true);
            if (fileChannel.size() == 4) {
                return true;
            }
            zzJt().zzLa().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzJt().zzLa().zzj("Failed to write to channel", e);
            return false;
        }
    }

    @WorkerThread
    public byte[] zza(@NonNull zzatb com_google_android_gms_internal_zzatb, @Size(min = 1) String str) {
        zznA();
        zzmq();
        zzJd();
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzac.zzdv(str);
        zzd com_google_android_gms_internal_zzauh_zzd = new zzd();
        zzJo().beginTransaction();
        try {
            zzasp zzfy = zzJo().zzfy(str);
            byte[] bArr;
            if (zzfy == null) {
                zzJt().zzLf().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzfy.zzJF()) {
                long j;
                zzauh.zze com_google_android_gms_internal_zzauh_zze = new zzauh.zze();
                com_google_android_gms_internal_zzauh_zzd.zzbwa = new zzauh.zze[]{com_google_android_gms_internal_zzauh_zze};
                com_google_android_gms_internal_zzauh_zze.zzbwc = Integer.valueOf(1);
                com_google_android_gms_internal_zzauh_zze.zzbwk = "android";
                com_google_android_gms_internal_zzauh_zze.zzaR = zzfy.zzjI();
                com_google_android_gms_internal_zzauh_zze.zzbqg = zzfy.zzJC();
                com_google_android_gms_internal_zzauh_zze.zzbhg = zzfy.zzmy();
                com_google_android_gms_internal_zzauh_zze.zzbwx = Integer.valueOf((int) zzfy.zzJB());
                com_google_android_gms_internal_zzauh_zze.zzbwo = Long.valueOf(zzfy.zzJD());
                com_google_android_gms_internal_zzauh_zze.zzbqf = zzfy.getGmpAppId();
                com_google_android_gms_internal_zzauh_zze.zzbwt = Long.valueOf(zzfy.zzJE());
                Pair zzfK = zzJu().zzfK(zzfy.zzjI());
                if (!TextUtils.isEmpty((CharSequence) zzfK.first)) {
                    com_google_android_gms_internal_zzauh_zze.zzbwq = (String) zzfK.first;
                    com_google_android_gms_internal_zzauh_zze.zzbwr = (Boolean) zzfK.second;
                }
                com_google_android_gms_internal_zzauh_zze.zzbwl = zzJk().zzkm();
                com_google_android_gms_internal_zzauh_zze.zzba = zzJk().zzKU();
                com_google_android_gms_internal_zzauh_zze.zzbwn = Integer.valueOf((int) zzJk().zzKV());
                com_google_android_gms_internal_zzauh_zze.zzbwm = zzJk().zzKW();
                com_google_android_gms_internal_zzauh_zze.zzbws = zzfy.getAppInstanceId();
                com_google_android_gms_internal_zzauh_zze.zzbqn = zzfy.zzJy();
                List zzfx = zzJo().zzfx(zzfy.zzjI());
                com_google_android_gms_internal_zzauh_zze.zzbwe = new zzg[zzfx.size()];
                for (int i = 0; i < zzfx.size(); i++) {
                    zzg com_google_android_gms_internal_zzauh_zzg = new zzg();
                    com_google_android_gms_internal_zzauh_zze.zzbwe[i] = com_google_android_gms_internal_zzauh_zzg;
                    com_google_android_gms_internal_zzauh_zzg.name = ((zzaud) zzfx.get(i)).mName;
                    com_google_android_gms_internal_zzauh_zzg.zzbwF = Long.valueOf(((zzaud) zzfx.get(i)).zzbvd);
                    zzJp().zza(com_google_android_gms_internal_zzauh_zzg, ((zzaud) zzfx.get(i)).zzYe);
                }
                Bundle zzKY = com_google_android_gms_internal_zzatb.zzbqP.zzKY();
                if ("_iap".equals(com_google_android_gms_internal_zzatb.name)) {
                    zzKY.putLong("_c", 1);
                    zzJt().zzLf().log("Marking in-app purchase as real-time");
                    zzKY.putLong("_r", 1);
                }
                zzKY.putString("_o", com_google_android_gms_internal_zzatb.zzbqQ);
                if (zzJp().zzgh(com_google_android_gms_internal_zzauh_zze.zzaR)) {
                    zzJp().zza(zzKY, "_dbg", Long.valueOf(1));
                    zzJp().zza(zzKY, "_r", Long.valueOf(1));
                }
                zzasy zzP = zzJo().zzP(str, com_google_android_gms_internal_zzatb.name);
                if (zzP == null) {
                    zzJo().zza(new zzasy(str, com_google_android_gms_internal_zzatb.name, 1, 0, com_google_android_gms_internal_zzatb.zzbqR));
                    j = 0;
                } else {
                    j = zzP.zzbqL;
                    zzJo().zza(zzP.zzan(com_google_android_gms_internal_zzatb.zzbqR).zzKX());
                }
                zzasx com_google_android_gms_internal_zzasx = new zzasx(this, com_google_android_gms_internal_zzatb.zzbqQ, str, com_google_android_gms_internal_zzatb.name, com_google_android_gms_internal_zzatb.zzbqR, j, zzKY);
                zzb com_google_android_gms_internal_zzauh_zzb = new zzb();
                com_google_android_gms_internal_zzauh_zze.zzbwd = new zzb[]{com_google_android_gms_internal_zzauh_zzb};
                com_google_android_gms_internal_zzauh_zzb.zzbvW = Long.valueOf(com_google_android_gms_internal_zzasx.zzavX);
                com_google_android_gms_internal_zzauh_zzb.name = com_google_android_gms_internal_zzasx.mName;
                com_google_android_gms_internal_zzauh_zzb.zzbvX = Long.valueOf(com_google_android_gms_internal_zzasx.zzbqH);
                com_google_android_gms_internal_zzauh_zzb.zzbvV = new zzc[com_google_android_gms_internal_zzasx.zzbqI.size()];
                Iterator it = com_google_android_gms_internal_zzasx.zzbqI.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzauh_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzauh_zzb.zzbvV[i2] = com_google_android_gms_internal_zzauh_zzc;
                    com_google_android_gms_internal_zzauh_zzc.name = str2;
                    zzJp().zza(com_google_android_gms_internal_zzauh_zzc, com_google_android_gms_internal_zzasx.zzbqI.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzauh_zze.zzbww = zza(zzfy.zzjI(), com_google_android_gms_internal_zzauh_zze.zzbwe, com_google_android_gms_internal_zzauh_zze.zzbwd);
                com_google_android_gms_internal_zzauh_zze.zzbwg = com_google_android_gms_internal_zzauh_zzb.zzbvW;
                com_google_android_gms_internal_zzauh_zze.zzbwh = com_google_android_gms_internal_zzauh_zzb.zzbvW;
                long zzJA = zzfy.zzJA();
                com_google_android_gms_internal_zzauh_zze.zzbwj = zzJA != 0 ? Long.valueOf(zzJA) : null;
                long zzJz = zzfy.zzJz();
                if (zzJz != 0) {
                    zzJA = zzJz;
                }
                com_google_android_gms_internal_zzauh_zze.zzbwi = zzJA != 0 ? Long.valueOf(zzJA) : null;
                zzfy.zzJJ();
                com_google_android_gms_internal_zzauh_zze.zzbwu = Integer.valueOf((int) zzfy.zzJG());
                com_google_android_gms_internal_zzauh_zze.zzbwp = Long.valueOf(zzJv().zzJD());
                com_google_android_gms_internal_zzauh_zze.zzbwf = Long.valueOf(zznq().currentTimeMillis());
                com_google_android_gms_internal_zzauh_zze.zzbwv = Boolean.TRUE;
                zzfy.zzX(com_google_android_gms_internal_zzauh_zze.zzbwg.longValue());
                zzfy.zzY(com_google_android_gms_internal_zzauh_zze.zzbwh.longValue());
                zzJo().zza(zzfy);
                zzJo().setTransactionSuccessful();
                zzJo().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzauh_zzd.zzacZ()];
                    zzbum zzae = zzbum.zzae(bArr);
                    com_google_android_gms_internal_zzauh_zzd.zza(zzae);
                    zzae.zzacM();
                    return zzJp().zzk(bArr);
                } catch (IOException e) {
                    zzJt().zzLa().zze("Data loss. Failed to bundle and serialize. appId", zzati.zzfI(str), e);
                    return null;
                }
            } else {
                zzJt().zzLf().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzJo().endTransaction();
                return bArr;
            }
        } finally {
            zzJo().endTransaction();
        }
    }

    boolean zzao(long j) {
        return zzj(null, j);
    }

    void zzb(zzasp com_google_android_gms_internal_zzasp) {
        String zzO = zzJv().zzO(com_google_android_gms_internal_zzasp.getGmpAppId(), com_google_android_gms_internal_zzasp.getAppInstanceId());
        try {
            URL url = new URL(zzO);
            zzJt().zzLg().zzj("Fetching remote configuration", com_google_android_gms_internal_zzasp.zzjI());
            zzaug.zzb zzfO = zzJq().zzfO(com_google_android_gms_internal_zzasp.zzjI());
            Map map = null;
            CharSequence zzfP = zzJq().zzfP(com_google_android_gms_internal_zzasp.zzjI());
            if (!(zzfO == null || TextUtils.isEmpty(zzfP))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzfP);
            }
            zzLy().zza(com_google_android_gms_internal_zzasp.zzjI(), url, map, new zza(this) {
                final /* synthetic */ zzatp zzbtH;

                {
                    this.zzbtH = r1;
                }

                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                    this.zzbtH.zzb(str, i, th, bArr, map);
                }
            });
        } catch (MalformedURLException e) {
            zzJt().zzLa().zze("Failed to parse config URL. Not fetching. appId", zzati.zzfI(com_google_android_gms_internal_zzasp.zzjI()), zzO);
        }
    }

    @WorkerThread
    void zzb(zzasq com_google_android_gms_internal_zzasq, long j) {
        zzmq();
        zznA();
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        bundle.putLong("_uwa", 0);
        bundle.putLong("_pfo", 0);
        bundle.putLong("_sys", 0);
        bundle.putLong("_sysu", 0);
        if (getContext().getPackageManager() == null) {
            zzJt().zzLa().zzj("PackageManager is null, first open report might be inaccurate. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName));
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = zzacx.zzaQ(getContext()).getPackageInfo(com_google_android_gms_internal_zzasq.packageName, 0);
            } catch (NameNotFoundException e) {
                zzJt().zzLa().zze("Package info is null, first open report might be inaccurate. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = zzacx.zzaQ(getContext()).getApplicationInfo(com_google_android_gms_internal_zzasq.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzJt().zzLa().zze("Application info is null, first open report might be inaccurate. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e2);
                applicationInfo = null;
            }
            if (applicationInfo != null) {
                if ((applicationInfo.flags & 1) != 0) {
                    bundle.putLong("_sys", 1);
                }
                if ((applicationInfo.flags & 128) != 0) {
                    bundle.putLong("_sysu", 1);
                }
            }
        }
        long zzfE = zzJo().zzfE(com_google_android_gms_internal_zzasq.packageName);
        if (zzfE >= 0) {
            bundle.putLong("_pfo", zzfE);
        }
        zzb(new zzatb("_f", new zzasz(bundle), "auto", j), com_google_android_gms_internal_zzasq);
    }

    @WorkerThread
    void zzb(zzatb com_google_android_gms_internal_zzatb, zzasq com_google_android_gms_internal_zzasq) {
        long nanoTime = System.nanoTime();
        zzmq();
        zznA();
        String str = com_google_android_gms_internal_zzasq.packageName;
        zzac.zzdv(str);
        if (!zzaue.zzc(com_google_android_gms_internal_zzatb, com_google_android_gms_internal_zzasq)) {
            return;
        }
        if (!com_google_android_gms_internal_zzasq.zzbqk && !"_in".equals(com_google_android_gms_internal_zzatb.name)) {
            zzf(com_google_android_gms_internal_zzasq);
        } else if (zzJq().zzX(str, com_google_android_gms_internal_zzatb.name)) {
            zzJt().zzLc().zze("Dropping blacklisted event. appId", zzati.zzfI(str), com_google_android_gms_internal_zzatb.name);
            Object obj = (zzJp().zzgj(str) || zzJp().zzgk(str)) ? 1 : null;
            if (obj == null && !"_err".equals(com_google_android_gms_internal_zzatb.name)) {
                zzJp().zza(11, "_ev", com_google_android_gms_internal_zzatb.name, 0);
            }
            if (obj != null) {
                zzasp zzfy = zzJo().zzfy(str);
                if (zzfy != null) {
                    if (Math.abs(zznq().currentTimeMillis() - Math.max(zzfy.zzJI(), zzfy.zzJH())) > zzJv().zzKp()) {
                        zzJt().zzLf().log("Fetching config for blacklisted app");
                        zzb(zzfy);
                    }
                }
            }
        } else {
            if (zzJt().zzai(2)) {
                zzJt().zzLg().zzj("Logging event", com_google_android_gms_internal_zzatb);
            }
            zzJo().beginTransaction();
            try {
                Bundle zzKY = com_google_android_gms_internal_zzatb.zzbqP.zzKY();
                zzf(com_google_android_gms_internal_zzasq);
                if ("_iap".equals(com_google_android_gms_internal_zzatb.name) || Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatb.name)) {
                    long round;
                    Object string = zzKY.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatb.name)) {
                        double d = zzKY.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzKY.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzJt().zzLc().zze("Data lost. Currency value is too big. appId", zzati.zzfI(str), Double.valueOf(d));
                            zzJo().setTransactionSuccessful();
                            zzJo().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzKY.getLong(Param.VALUE);
                    }
                    if (!TextUtils.isEmpty(string)) {
                        String toUpperCase = string.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            zzaud com_google_android_gms_internal_zzaud;
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzaud zzR = zzJo().zzR(str, concat);
                            if (zzR == null || !(zzR.zzYe instanceof Long)) {
                                zzJo().zzz(str, zzJv().zzfr(str) - 1);
                                com_google_android_gms_internal_zzaud = new zzaud(str, concat, zznq().currentTimeMillis(), Long.valueOf(round));
                            } else {
                                com_google_android_gms_internal_zzaud = new zzaud(str, concat, zznq().currentTimeMillis(), Long.valueOf(round + ((Long) zzR.zzYe).longValue()));
                            }
                            if (!zzJo().zza(com_google_android_gms_internal_zzaud)) {
                                zzJt().zzLa().zzd("Too many unique user properties are set. Ignoring user property. appId", zzati.zzfI(str), com_google_android_gms_internal_zzaud.mName, com_google_android_gms_internal_zzaud.zzYe);
                                zzJp().zza(9, null, null, 0);
                            }
                        }
                    }
                }
                boolean zzfW = zzaue.zzfW(com_google_android_gms_internal_zzatb.name);
                boolean equals = "_err".equals(com_google_android_gms_internal_zzatb.name);
                com.google.android.gms.internal.zzasu.zza zza = zzJo().zza(zzLE(), str, true, zzfW, false, equals, false);
                long zzKc = zza.zzbqw - zzJv().zzKc();
                if (zzKc > 0) {
                    if (zzKc % 1000 == 1) {
                        zzJt().zzLa().zze("Data loss. Too many events logged. appId, count", zzati.zzfI(str), Long.valueOf(zza.zzbqw));
                    }
                    zzJp().zza(16, "_ev", com_google_android_gms_internal_zzatb.name, 0);
                    zzJo().setTransactionSuccessful();
                    return;
                }
                zzasy com_google_android_gms_internal_zzasy;
                if (zzfW) {
                    zzKc = zza.zzbqv - zzJv().zzKd();
                    if (zzKc > 0) {
                        if (zzKc % 1000 == 1) {
                            zzJt().zzLa().zze("Data loss. Too many public events logged. appId, count", zzati.zzfI(str), Long.valueOf(zza.zzbqv));
                        }
                        zzJp().zza(16, "_ev", com_google_android_gms_internal_zzatb.name, 0);
                        zzJo().setTransactionSuccessful();
                        zzJo().endTransaction();
                        return;
                    }
                }
                if (equals) {
                    zzKc = zza.zzbqy - ((long) zzJv().zzfn(com_google_android_gms_internal_zzasq.packageName));
                    if (zzKc > 0) {
                        if (zzKc == 1) {
                            zzJt().zzLa().zze("Too many error events logged. appId, count", zzati.zzfI(str), Long.valueOf(zza.zzbqy));
                        }
                        zzJo().setTransactionSuccessful();
                        zzJo().endTransaction();
                        return;
                    }
                }
                zzJp().zza(zzKY, "_o", com_google_android_gms_internal_zzatb.zzbqQ);
                if (zzJp().zzgh(str)) {
                    zzJp().zza(zzKY, "_dbg", Long.valueOf(1));
                    zzJp().zza(zzKY, "_r", Long.valueOf(1));
                }
                long zzfz = zzJo().zzfz(str);
                if (zzfz > 0) {
                    zzJt().zzLc().zze("Data lost. Too many events stored on disk, deleted. appId", zzati.zzfI(str), Long.valueOf(zzfz));
                }
                zzasx com_google_android_gms_internal_zzasx = new zzasx(this, com_google_android_gms_internal_zzatb.zzbqQ, str, com_google_android_gms_internal_zzatb.name, com_google_android_gms_internal_zzatb.zzbqR, 0, zzKY);
                zzasy zzP = zzJo().zzP(str, com_google_android_gms_internal_zzasx.mName);
                if (zzP == null) {
                    zzfz = zzJo().zzfG(str);
                    zzJv().zzKb();
                    if (zzfz >= 500) {
                        zzJt().zzLa().zzd("Too many event names used, ignoring event. appId, name, supported count", zzati.zzfI(str), com_google_android_gms_internal_zzasx.mName, Integer.valueOf(zzJv().zzKb()));
                        zzJp().zza(8, null, null, 0);
                        zzJo().endTransaction();
                        return;
                    }
                    com_google_android_gms_internal_zzasy = new zzasy(str, com_google_android_gms_internal_zzasx.mName, 0, 0, com_google_android_gms_internal_zzasx.zzavX);
                } else {
                    com_google_android_gms_internal_zzasx = com_google_android_gms_internal_zzasx.zza(this, zzP.zzbqL);
                    com_google_android_gms_internal_zzasy = zzP.zzan(com_google_android_gms_internal_zzasx.zzavX);
                }
                zzJo().zza(com_google_android_gms_internal_zzasy);
                zza(com_google_android_gms_internal_zzasx, com_google_android_gms_internal_zzasq);
                zzJo().setTransactionSuccessful();
                if (zzJt().zzai(2)) {
                    zzJt().zzLg().zzj("Event recorded", com_google_android_gms_internal_zzasx);
                }
                zzJo().endTransaction();
                zzLI();
                zzJt().zzLg().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
            } finally {
                zzJo().endTransaction();
            }
        }
    }

    @WorkerThread
    void zzb(zzatb com_google_android_gms_internal_zzatb, String str) {
        zzasp zzfy = zzJo().zzfy(str);
        if (zzfy == null || TextUtils.isEmpty(zzfy.zzmy())) {
            zzJt().zzLf().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzacx.zzaQ(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfy.zzmy() == null || zzfy.zzmy().equals(str2))) {
                zzJt().zzLc().zzj("App version does not match; dropping event. appId", zzati.zzfI(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzatb.name)) {
                zzJt().zzLc().zzj("Could not find package. appId", zzati.zzfI(str));
            }
        }
        zzatb com_google_android_gms_internal_zzatb2 = com_google_android_gms_internal_zzatb;
        zzb(com_google_android_gms_internal_zzatb2, new zzasq(str, zzfy.getGmpAppId(), zzfy.zzmy(), zzfy.zzJB(), zzfy.zzJC(), zzfy.zzJD(), zzfy.zzJE(), null, zzfy.zzJF(), false, zzfy.zzJy()));
    }

    void zzb(zzats com_google_android_gms_internal_zzats) {
        this.zzbtE++;
    }

    @WorkerThread
    void zzb(zzaub com_google_android_gms_internal_zzaub, zzasq com_google_android_gms_internal_zzasq) {
        int i = 0;
        zzmq();
        zznA();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqf)) {
            if (com_google_android_gms_internal_zzasq.zzbqk) {
                int zzga = zzJp().zzga(com_google_android_gms_internal_zzaub.name);
                String zza;
                if (zzga != 0) {
                    zza = zzJp().zza(com_google_android_gms_internal_zzaub.name, zzJv().zzJV(), true);
                    if (com_google_android_gms_internal_zzaub.name != null) {
                        i = com_google_android_gms_internal_zzaub.name.length();
                    }
                    zzJp().zza(zzga, "_ev", zza, i);
                    return;
                }
                zzga = zzJp().zzm(com_google_android_gms_internal_zzaub.name, com_google_android_gms_internal_zzaub.getValue());
                if (zzga != 0) {
                    zza = zzJp().zza(com_google_android_gms_internal_zzaub.name, zzJv().zzJV(), true);
                    Object value = com_google_android_gms_internal_zzaub.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzJp().zza(zzga, "_ev", zza, i);
                    return;
                }
                Object zzn = zzJp().zzn(com_google_android_gms_internal_zzaub.name, com_google_android_gms_internal_zzaub.getValue());
                if (zzn != null) {
                    zzaud com_google_android_gms_internal_zzaud = new zzaud(com_google_android_gms_internal_zzasq.packageName, com_google_android_gms_internal_zzaub.name, com_google_android_gms_internal_zzaub.zzbuZ, zzn);
                    zzJt().zzLf().zze("Setting user property", com_google_android_gms_internal_zzaud.mName, zzn);
                    zzJo().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzasq);
                        boolean zza2 = zzJo().zza(com_google_android_gms_internal_zzaud);
                        zzJo().setTransactionSuccessful();
                        if (zza2) {
                            zzJt().zzLf().zze("User property set", com_google_android_gms_internal_zzaud.mName, com_google_android_gms_internal_zzaud.zzYe);
                        } else {
                            zzJt().zzLa().zze("Too many unique user properties are set. Ignoring user property", com_google_android_gms_internal_zzaud.mName, com_google_android_gms_internal_zzaud.zzYe);
                            zzJp().zza(9, null, null, 0);
                        }
                        zzJo().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzJo().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zzf(com_google_android_gms_internal_zzasq);
        }
    }

    @WorkerThread
    void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        int i2 = 0;
        zzmq();
        zznA();
        zzac.zzdv(str);
        if (bArr == null) {
            bArr = new byte[0];
        }
        zzJo().beginTransaction();
        try {
            zzasp zzfy = zzJo().zzfy(str);
            int i3 = ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null) ? 1 : 0;
            if (zzfy == null) {
                zzJt().zzLc().zzj("App does not exist in onConfigFetched. appId", zzati.zzfI(str));
            } else if (i3 != 0 || i == 404) {
                List list = map != null ? (List) map.get("Last-Modified") : null;
                String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
                if (i == 404 || i == 304) {
                    if (zzJq().zzfO(str) == null && !zzJq().zzb(str, null, null)) {
                        zzJo().endTransaction();
                        return;
                    }
                } else if (!zzJq().zzb(str, bArr, str2)) {
                    zzJo().endTransaction();
                    return;
                }
                zzfy.zzad(zznq().currentTimeMillis());
                zzJo().zza(zzfy);
                if (i == 404) {
                    zzJt().zzLc().zzj("Config not found. Using empty config. appId", zzati.zzfI(str));
                } else {
                    zzJt().zzLg().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzLy().zzpA() && zzLH()) {
                    zzLG();
                } else {
                    zzLI();
                }
            } else {
                zzfy.zzae(zznq().currentTimeMillis());
                zzJo().zza(zzfy);
                zzJt().zzLg().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzJq().zzfQ(str);
                zzJu().zzbsh.set(zznq().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzJu().zzbsi.set(zznq().currentTimeMillis());
                }
                zzLI();
            }
            zzJo().setTransactionSuccessful();
        } finally {
            zzJo().endTransaction();
        }
    }

    @WorkerThread
    void zzc(zzasq com_google_android_gms_internal_zzasq, long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("_et", 1);
        zzb(new zzatb("_e", new zzasz(bundle), "auto", j), com_google_android_gms_internal_zzasq);
    }

    @WorkerThread
    void zzc(zzaub com_google_android_gms_internal_zzaub, zzasq com_google_android_gms_internal_zzasq) {
        zzmq();
        zznA();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqf)) {
            if (com_google_android_gms_internal_zzasq.zzbqk) {
                zzJt().zzLf().zzj("Removing user property", com_google_android_gms_internal_zzaub.name);
                zzJo().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzasq);
                    zzJo().zzQ(com_google_android_gms_internal_zzasq.packageName, com_google_android_gms_internal_zzaub.name);
                    zzJo().setTransactionSuccessful();
                    zzJt().zzLf().zzj("User property removed", com_google_android_gms_internal_zzaub.name);
                } finally {
                    zzJo().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzasq);
            }
        }
    }

    void zzd(zzasq com_google_android_gms_internal_zzasq) {
        zzmq();
        zznA();
        zzac.zzdv(com_google_android_gms_internal_zzasq.packageName);
        zzf(com_google_android_gms_internal_zzasq);
    }

    @WorkerThread
    void zzd(zzasq com_google_android_gms_internal_zzasq, long j) {
        zzb(new zzatb("_cd", new zzasz(new Bundle()), "auto", j), com_google_android_gms_internal_zzasq);
    }

    @WorkerThread
    public void zze(zzasq com_google_android_gms_internal_zzasq) {
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzasq);
        zzac.zzdv(com_google_android_gms_internal_zzasq.packageName);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqf)) {
            if (com_google_android_gms_internal_zzasq.zzbqk) {
                long currentTimeMillis = zznq().currentTimeMillis();
                zzJo().beginTransaction();
                try {
                    zza(com_google_android_gms_internal_zzasq, currentTimeMillis);
                    zzf(com_google_android_gms_internal_zzasq);
                    if (zzJo().zzP(com_google_android_gms_internal_zzasq.packageName, "_f") == null) {
                        zzb(new zzaub("_fot", currentTimeMillis, Long.valueOf((1 + (currentTimeMillis / 3600000)) * 3600000), "auto"), com_google_android_gms_internal_zzasq);
                        zzb(com_google_android_gms_internal_zzasq, currentTimeMillis);
                        zzc(com_google_android_gms_internal_zzasq, currentTimeMillis);
                    } else if (com_google_android_gms_internal_zzasq.zzbql) {
                        zzd(com_google_android_gms_internal_zzasq, currentTimeMillis);
                    }
                    zzJo().setTransactionSuccessful();
                } finally {
                    zzJo().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzasq);
            }
        }
    }

    public String zzfR(final String str) {
        Object e;
        try {
            return (String) zzJs().zzd(new Callable<String>(this) {
                final /* synthetic */ zzatp zzbtH;

                public /* synthetic */ Object call() throws Exception {
                    return zzou();
                }

                public String zzou() throws Exception {
                    zzasp zzfy = this.zzbtH.zzJo().zzfy(str);
                    return zzfy == null ? null : zzfy.getAppInstanceId();
                }
            }).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzJt().zzLa().zze("Failed to get app instance id. appId", zzati.zzfI(str), e);
        return null;
    }

    @WorkerThread
    public void zzmq() {
        zzJs().zzmq();
    }

    void zznA() {
        if (!this.zzacO) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    public zze zznq() {
        return this.zzuI;
    }

    @WorkerThread
    boolean zzv(int i, int i2) {
        zzmq();
        if (i > i2) {
            zzJt().zzLa().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzLB())) {
                zzJt().zzLg().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzJt().zzLa().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }
}
