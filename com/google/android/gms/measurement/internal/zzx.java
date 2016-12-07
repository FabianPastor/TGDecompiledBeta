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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwb;
import com.google.android.gms.internal.zzwc;
import com.google.android.gms.internal.zzwc.zzb;
import com.google.android.gms.internal.zzwc.zzc;
import com.google.android.gms.internal.zzwc.zzd;
import com.google.android.gms.internal.zzwc.zzg;
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
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzx {
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
    private long auz = -1;
    private final boolean cR;
    private final Context mContext;
    private final zze zzaql;

    private class zza implements zzb {
        final /* synthetic */ zzx auA;
        zzwc.zze auB;
        List<Long> auC;
        long auD;
        List<zzb> zzani;

        private zza(zzx com_google_android_gms_measurement_internal_zzx) {
            this.auA = com_google_android_gms_measurement_internal_zzx;
        }

        private long zza(zzb com_google_android_gms_internal_zzwc_zzb) {
            return ((com_google_android_gms_internal_zzwc_zzb.awO.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zzani == null || this.zzani.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzwc_zzb) {
            zzaa.zzy(com_google_android_gms_internal_zzwc_zzb);
            if (this.zzani == null) {
                this.zzani = new ArrayList();
            }
            if (this.auC == null) {
                this.auC = new ArrayList();
            }
            if (this.zzani.size() > 0 && zza((zzb) this.zzani.get(0)) != zza(com_google_android_gms_internal_zzwc_zzb)) {
                return false;
            }
            long cz = this.auD + ((long) com_google_android_gms_internal_zzwc_zzb.cz());
            if (cz >= ((long) this.auA.zzbwd().zzbuz())) {
                return false;
            }
            this.auD = cz;
            this.zzani.add(com_google_android_gms_internal_zzwc_zzb);
            this.auC.add(Long.valueOf(j));
            return this.zzani.size() < this.auA.zzbwd().zzbva();
        }

        public void zzb(zzwc.zze com_google_android_gms_internal_zzwc_zze) {
            zzaa.zzy(com_google_android_gms_internal_zzwc_zze);
            this.auB = com_google_android_gms_internal_zzwc_zze;
        }
    }

    zzx(zzab com_google_android_gms_measurement_internal_zzab) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzab);
        this.mContext = com_google_android_gms_measurement_internal_zzab.mContext;
        this.zzaql = com_google_android_gms_measurement_internal_zzab.zzn(this);
        this.atX = com_google_android_gms_measurement_internal_zzab.zza(this);
        zzt zzb = com_google_android_gms_measurement_internal_zzab.zzb(this);
        zzb.initialize();
        this.atY = zzb;
        zzq zzc = com_google_android_gms_measurement_internal_zzab.zzc(this);
        zzc.initialize();
        this.atZ = zzc;
        zzbwb().zzbxc().zzj("App measurement is starting up, version", Long.valueOf(zzbwd().zzbto()));
        zzbwb().zzbxc().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzbwb().zzbxd().log("Debug-level message logging enabled");
        zzbwb().zzbxd().zzj("AppMeasurement singleton hash", Integer.valueOf(System.identityHashCode(this)));
        this.auf = com_google_android_gms_measurement_internal_zzab.zzj(this);
        zzg zzq = com_google_android_gms_measurement_internal_zzab.zzq(this);
        zzq.initialize();
        this.aul = zzq;
        zzn zzr = com_google_android_gms_measurement_internal_zzab.zzr(this);
        zzr.initialize();
        this.aun = zzr;
        if (!zzbwd().zzayi()) {
            String zzup = zzr.zzup();
            if (zzbvx().zznf(zzup)) {
                zzbwb().zzbxc().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop firebase.analytics.debug-mode .none.");
            } else {
                com.google.android.gms.measurement.internal.zzq.zza zzbxc = zzbwb().zzbxc();
                String str = "To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode ";
                zzup = String.valueOf(zzup);
                zzbxc.log(zzup.length() != 0 ? str.concat(zzup) : new String(str));
            }
        }
        zze zzk = com_google_android_gms_measurement_internal_zzab.zzk(this);
        zzk.initialize();
        this.aug = zzk;
        zzo zzl = com_google_android_gms_measurement_internal_zzab.zzl(this);
        zzl.initialize();
        this.auh = zzl;
        zzc zzu = com_google_android_gms_measurement_internal_zzab.zzu(this);
        zzu.initialize();
        this.auq = zzu;
        zzr zzm = com_google_android_gms_measurement_internal_zzab.zzm(this);
        zzm.initialize();
        this.aui = zzm;
        zzad zzo = com_google_android_gms_measurement_internal_zzab.zzo(this);
        zzo.initialize();
        this.auj = zzo;
        zzae zzp = com_google_android_gms_measurement_internal_zzab.zzp(this);
        zzp.initialize();
        this.auk = zzp;
        zzac zzi = com_google_android_gms_measurement_internal_zzab.zzi(this);
        zzi.initialize();
        this.aum = zzi;
        zzai zzt = com_google_android_gms_measurement_internal_zzab.zzt(this);
        zzt.initialize();
        this.aup = zzt;
        this.auo = com_google_android_gms_measurement_internal_zzab.zzs(this);
        this.aud = com_google_android_gms_measurement_internal_zzab.zzh(this);
        this.aue = com_google_android_gms_measurement_internal_zzab.zzg(this);
        zzag zze = com_google_android_gms_measurement_internal_zzab.zze(this);
        zze.initialize();
        this.aub = zze;
        zzv zzf = com_google_android_gms_measurement_internal_zzab.zzf(this);
        zzf.initialize();
        this.auc = zzf;
        zzw zzd = com_google_android_gms_measurement_internal_zzab.zzd(this);
        zzd.initialize();
        this.aua = zzd;
        if (this.aux != this.auy) {
            zzbwb().zzbwy().zze("Not all components initialized", Integer.valueOf(this.aux), Integer.valueOf(this.auy));
        }
        this.cR = true;
        if (!this.atX.zzayi()) {
            if (!(this.mContext.getApplicationContext() instanceof Application)) {
                zzbwb().zzbxa().log("Application context is not an Application");
            } else if (VERSION.SDK_INT >= 14) {
                zzbvq().zzbyp();
            } else {
                zzbwb().zzbxd().log("Not tracking deep linking pre-ICS");
            }
        }
        this.aua.zzm(new Runnable(this) {
            final /* synthetic */ zzx auA;

            {
                this.auA = r1;
            }

            public void run() {
                this.auA.start();
            }
        });
    }

    private void zza(zzaa com_google_android_gms_measurement_internal_zzaa) {
        if (com_google_android_gms_measurement_internal_zzaa == null) {
            throw new IllegalStateException("Component not created");
        } else if (!com_google_android_gms_measurement_internal_zzaa.isInitialized()) {
            throw new IllegalStateException("Component not initialized");
        }
    }

    private void zza(zzz com_google_android_gms_measurement_internal_zzz) {
        if (com_google_android_gms_measurement_internal_zzz == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private boolean zza(zzh com_google_android_gms_measurement_internal_zzh) {
        if (com_google_android_gms_measurement_internal_zzh.arC == null) {
            return false;
        }
        Iterator it = com_google_android_gms_measurement_internal_zzh.arC.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzbvy().zzay(com_google_android_gms_measurement_internal_zzh.zzctj, com_google_android_gms_measurement_internal_zzh.mName) && zzbvw().zza(zzbyb(), com_google_android_gms_measurement_internal_zzh.zzctj, false, false, false, false, false).art < ((long) zzbwd().zzlq(com_google_android_gms_measurement_internal_zzh.zzctj));
    }

    private com.google.android.gms.internal.zzwc.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzwc_zzgArr, zzb[] com_google_android_gms_internal_zzwc_zzbArr) {
        zzaa.zzib(str);
        return zzbvp().zza(str, com_google_android_gms_internal_zzwc_zzbArr, com_google_android_gms_internal_zzwc_zzgArr);
    }

    private boolean zzbyf() {
        zzzx();
        zzacj();
        return zzbvw().zzbwk() || !TextUtils.isEmpty(zzbvw().zzbwe());
    }

    @WorkerThread
    private void zzbyg() {
        zzzx();
        zzacj();
        if (!zzbyk()) {
            return;
        }
        if (zzbxq() && zzbyf()) {
            long zzbyh = zzbyh();
            if (zzbyh == 0) {
                zzbxw().unregister();
                zzbxx().cancel();
                return;
            } else if (zzbxv().zzagk()) {
                long j = zzbwc().atb.get();
                long zzbve = zzbwd().zzbve();
                if (!zzbvx().zzf(j, zzbve)) {
                    zzbyh = Math.max(zzbyh, j + zzbve);
                }
                zzbxw().unregister();
                zzbyh -= zzabz().currentTimeMillis();
                if (zzbyh <= 0) {
                    zzbyh = zzbwd().zzbvh();
                    zzbwc().asZ.set(zzabz().currentTimeMillis());
                }
                zzbwb().zzbxe().zzj("Upload scheduled in approximately ms", Long.valueOf(zzbyh));
                zzbxx().zzx(zzbyh);
                return;
            } else {
                zzbxw().zzagh();
                zzbxx().cancel();
                return;
            }
        }
        zzbxw().unregister();
        zzbxx().cancel();
    }

    private long zzbyh() {
        long currentTimeMillis = zzabz().currentTimeMillis();
        long zzbvk = zzbwd().zzbvk();
        Object obj = (zzbvw().zzbwl() || zzbvw().zzbwf()) ? 1 : null;
        long zzbvg = obj != null ? zzbwd().zzbvg() : zzbwd().zzbvf();
        long j = zzbwc().asZ.get();
        long j2 = zzbwc().ata.get();
        long max = Math.max(zzbvw().zzbwi(), zzbvw().zzbwj());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzbvk;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzbvg;
        }
        if (!zzbvx().zzf(j, zzbvg)) {
            currentTimeMillis = j + zzbvg;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzbwd().zzbvm(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzbwd().zzbvl();
            if (currentTimeMillis > j2) {
                return currentTimeMillis;
            }
        }
        return 0;
    }

    public static zzx zzdq(Context context) {
        zzaa.zzy(context);
        zzaa.zzy(context.getApplicationContext());
        if (atW == null) {
            synchronized (zzx.class) {
                if (atW == null) {
                    atW = new zzab(context).zzbyo();
                }
            }
        }
        return atW;
    }

    @WorkerThread
    private void zze(AppMetadata appMetadata) {
        Object obj = 1;
        zzzx();
        zzacj();
        zzaa.zzy(appMetadata);
        zzaa.zzib(appMetadata.packageName);
        zza zzlz = zzbvw().zzlz(appMetadata.packageName);
        String zzml = zzbwc().zzml(appMetadata.packageName);
        Object obj2 = null;
        if (zzlz == null) {
            zza com_google_android_gms_measurement_internal_zza = new zza(this, appMetadata.packageName);
            com_google_android_gms_measurement_internal_zza.zzlj(zzbwc().zzbxh());
            com_google_android_gms_measurement_internal_zza.zzll(zzml);
            zzlz = com_google_android_gms_measurement_internal_zza;
            obj2 = 1;
        } else if (!zzml.equals(zzlz.zzbti())) {
            zzlz.zzll(zzml);
            zzlz.zzlj(zzbwc().zzbxh());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.aqZ) || appMetadata.aqZ.equals(zzlz.zzbth()))) {
            zzlz.zzlk(appMetadata.aqZ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.arh) || appMetadata.arh.equals(zzlz.zzbtj()))) {
            zzlz.zzlm(appMetadata.arh);
            obj2 = 1;
        }
        if (!(appMetadata.arb == 0 || appMetadata.arb == zzlz.zzbto())) {
            zzlz.zzay(appMetadata.arb);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.aii) || appMetadata.aii.equals(zzlz.zzaaf()))) {
            zzlz.setAppVersion(appMetadata.aii);
            obj2 = 1;
        }
        if (appMetadata.arg != zzlz.zzbtm()) {
            zzlz.zzax(appMetadata.arg);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.ara) || appMetadata.ara.equals(zzlz.zzbtn()))) {
            zzlz.zzln(appMetadata.ara);
            obj2 = 1;
        }
        if (appMetadata.arc != zzlz.zzbtp()) {
            zzlz.zzaz(appMetadata.arc);
            obj2 = 1;
        }
        if (appMetadata.are != zzlz.zzbtq()) {
            zzlz.setMeasurementEnabled(appMetadata.are);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzbvw().zza(zzlz);
        }
    }

    private boolean zzh(String str, long j) {
        zzbvw().beginTransaction();
        try {
            zzx com_google_android_gms_measurement_internal_zzx = this;
            zza com_google_android_gms_measurement_internal_zzx_zza = new zza();
            zzbvw().zza(str, j, this.auz, com_google_android_gms_measurement_internal_zzx_zza);
            if (com_google_android_gms_measurement_internal_zzx_zza.isEmpty()) {
                zzbvw().setTransactionSuccessful();
                zzbvw().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzwc.zze com_google_android_gms_internal_zzwc_zze = com_google_android_gms_measurement_internal_zzx_zza.auB;
            com_google_android_gms_internal_zzwc_zze.awV = new zzb[com_google_android_gms_measurement_internal_zzx_zza.zzani.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_measurement_internal_zzx_zza.zzani.size()) {
                boolean z2;
                Object obj;
                if (zzbvy().zzax(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name)) {
                    zzbwb().zzbxa().zzj("Dropping blacklisted raw event", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name);
                    obj = (zzbvx().zznh(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs) || zzbvx().zzni(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs)) ? 1 : null;
                    if (obj != null || "_err".equals(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name)) {
                        i = i2;
                        z2 = z;
                    } else {
                        zzbvx().zza(11, "_ev", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name, 0);
                        i = i2;
                        z2 = z;
                    }
                } else {
                    boolean zzmu;
                    int i4;
                    if (zzbvy().zzay(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzwc_zzcArr;
                        zzc com_google_android_gms_internal_zzwc_zzc;
                        zzb com_google_android_gms_internal_zzwc_zzb;
                        Object obj2 = null;
                        Object obj3 = null;
                        if (((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN == null) {
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzwc_zzcArr2 = ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN;
                        int length = com_google_android_gms_internal_zzwc_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            zzc com_google_android_gms_internal_zzwc_zzc2 = com_google_android_gms_internal_zzwc_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzwc_zzc2.name)) {
                                com_google_android_gms_internal_zzwc_zzc2.awR = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzwc_zzc2.name)) {
                                com_google_android_gms_internal_zzwc_zzc2.awR = Long.valueOf(1);
                                obj = 1;
                            } else {
                                obj = obj3;
                            }
                            i5++;
                            obj3 = obj;
                        }
                        if (obj2 == null) {
                            zzbwb().zzbxe().zzj("Marking event as conversion", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name);
                            com_google_android_gms_internal_zzwc_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN.length + 1);
                            com_google_android_gms_internal_zzwc_zzc = new zzc();
                            com_google_android_gms_internal_zzwc_zzc.name = "_c";
                            com_google_android_gms_internal_zzwc_zzc.awR = Long.valueOf(1);
                            com_google_android_gms_internal_zzwc_zzcArr[com_google_android_gms_internal_zzwc_zzcArr.length - 1] = com_google_android_gms_internal_zzwc_zzc;
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN = com_google_android_gms_internal_zzwc_zzcArr;
                        }
                        if (obj3 == null) {
                            zzbwb().zzbxe().zzj("Marking event as real-time", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name);
                            com_google_android_gms_internal_zzwc_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN.length + 1);
                            com_google_android_gms_internal_zzwc_zzc = new zzc();
                            com_google_android_gms_internal_zzwc_zzc.name = "_r";
                            com_google_android_gms_internal_zzwc_zzc.awR = Long.valueOf(1);
                            com_google_android_gms_internal_zzwc_zzcArr[com_google_android_gms_internal_zzwc_zzcArr.length - 1] = com_google_android_gms_internal_zzwc_zzc;
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).awN = com_google_android_gms_internal_zzwc_zzcArr;
                        }
                        boolean z3 = true;
                        zzmu = zzal.zzmu(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3)).name);
                        if (zzbvw().zza(zzbyb(), com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs, false, false, false, false, true).art > ((long) zzbwd().zzlq(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs))) {
                            com_google_android_gms_internal_zzwc_zzb = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzwc_zzb.awN.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzwc_zzb.awN[i4].name)) {
                                    Object obj4 = new zzc[(com_google_android_gms_internal_zzwc_zzb.awN.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzwc_zzb.awN, 0, obj4, 0, i4);
                                    }
                                    if (i4 < obj4.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzwc_zzb.awN, i4 + 1, obj4, i4, obj4.length - i4);
                                    }
                                    com_google_android_gms_internal_zzwc_zzb.awN = obj4;
                                    z3 = z;
                                } else {
                                    i4++;
                                }
                            }
                            z3 = z;
                        }
                        if (zzmu && zzbvw().zza(zzbyb(), com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs, false, false, true, false, false).arr > ((long) zzbwd().zzlp(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs))) {
                            zzbwb().zzbxa().log("Too many conversions. Not logging as conversion.");
                            com_google_android_gms_internal_zzwc_zzb = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3);
                            Object obj5 = null;
                            zzc com_google_android_gms_internal_zzwc_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzwc_zzcArr3 = com_google_android_gms_internal_zzwc_zzb.awN;
                            int length2 = com_google_android_gms_internal_zzwc_zzcArr3.length;
                            int i6 = 0;
                            while (i6 < length2) {
                                com_google_android_gms_internal_zzwc_zzc = com_google_android_gms_internal_zzwc_zzcArr3[i6];
                                if ("_c".equals(com_google_android_gms_internal_zzwc_zzc.name)) {
                                    obj3 = obj5;
                                } else if ("_err".equals(com_google_android_gms_internal_zzwc_zzc.name)) {
                                    zzc com_google_android_gms_internal_zzwc_zzc4 = com_google_android_gms_internal_zzwc_zzc3;
                                    int i7 = 1;
                                    com_google_android_gms_internal_zzwc_zzc = com_google_android_gms_internal_zzwc_zzc4;
                                } else {
                                    com_google_android_gms_internal_zzwc_zzc = com_google_android_gms_internal_zzwc_zzc3;
                                    obj3 = obj5;
                                }
                                i6++;
                                obj5 = obj3;
                                com_google_android_gms_internal_zzwc_zzc3 = com_google_android_gms_internal_zzwc_zzc;
                            }
                            if (obj5 != null && com_google_android_gms_internal_zzwc_zzc3 != null) {
                                com_google_android_gms_internal_zzwc_zzcArr3 = new zzc[(com_google_android_gms_internal_zzwc_zzb.awN.length - 1)];
                                int i8 = 0;
                                zzc[] com_google_android_gms_internal_zzwc_zzcArr4 = com_google_android_gms_internal_zzwc_zzb.awN;
                                int length3 = com_google_android_gms_internal_zzwc_zzcArr4.length;
                                i6 = 0;
                                while (i6 < length3) {
                                    zzc com_google_android_gms_internal_zzwc_zzc5 = com_google_android_gms_internal_zzwc_zzcArr4[i6];
                                    if (com_google_android_gms_internal_zzwc_zzc5 != com_google_android_gms_internal_zzwc_zzc3) {
                                        i4 = i8 + 1;
                                        com_google_android_gms_internal_zzwc_zzcArr3[i8] = com_google_android_gms_internal_zzwc_zzc5;
                                    } else {
                                        i4 = i8;
                                    }
                                    i6++;
                                    i8 = i4;
                                }
                                com_google_android_gms_internal_zzwc_zzb.awN = com_google_android_gms_internal_zzwc_zzcArr3;
                                zzmu = z3;
                            } else if (com_google_android_gms_internal_zzwc_zzc3 != null) {
                                com_google_android_gms_internal_zzwc_zzc3.name = "_err";
                                com_google_android_gms_internal_zzwc_zzc3.awR = Long.valueOf(10);
                                zzmu = z3;
                            } else {
                                zzbwb().zzbwy().log("Did not find conversion parameter. Error not tracked");
                            }
                        }
                        zzmu = z3;
                    } else {
                        zzmu = z;
                    }
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzwc_zze.awV[i2] = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzani.get(i3);
                    i = i4;
                    z2 = zzmu;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_measurement_internal_zzx_zza.zzani.size()) {
                com_google_android_gms_internal_zzwc_zze.awV = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzwc_zze.awV, i2);
            }
            com_google_android_gms_internal_zzwc_zze.axo = zza(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs, com_google_android_gms_measurement_internal_zzx_zza.auB.awW, com_google_android_gms_internal_zzwc_zze.awV);
            com_google_android_gms_internal_zzwc_zze.awY = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzwc_zze.awZ = Long.valueOf(Long.MIN_VALUE);
            for (zzb com_google_android_gms_internal_zzwc_zzb2 : com_google_android_gms_internal_zzwc_zze.awV) {
                if (com_google_android_gms_internal_zzwc_zzb2.awO.longValue() < com_google_android_gms_internal_zzwc_zze.awY.longValue()) {
                    com_google_android_gms_internal_zzwc_zze.awY = com_google_android_gms_internal_zzwc_zzb2.awO;
                }
                if (com_google_android_gms_internal_zzwc_zzb2.awO.longValue() > com_google_android_gms_internal_zzwc_zze.awZ.longValue()) {
                    com_google_android_gms_internal_zzwc_zze.awZ = com_google_android_gms_internal_zzwc_zzb2.awO;
                }
            }
            String str2 = com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs;
            zza zzlz = zzbvw().zzlz(str2);
            if (zzlz == null) {
                zzbwb().zzbwy().log("Bundling raw events w/o app info");
            } else if (com_google_android_gms_internal_zzwc_zze.awV.length > 0) {
                long zzbtl = zzlz.zzbtl();
                com_google_android_gms_internal_zzwc_zze.axb = zzbtl != 0 ? Long.valueOf(zzbtl) : null;
                long zzbtk = zzlz.zzbtk();
                if (zzbtk != 0) {
                    zzbtl = zzbtk;
                }
                com_google_android_gms_internal_zzwc_zze.axa = zzbtl != 0 ? Long.valueOf(zzbtl) : null;
                zzlz.zzbtu();
                com_google_android_gms_internal_zzwc_zze.axm = Integer.valueOf((int) zzlz.zzbtr());
                zzlz.zzav(com_google_android_gms_internal_zzwc_zze.awY.longValue());
                zzlz.zzaw(com_google_android_gms_internal_zzwc_zze.awZ.longValue());
                zzbvw().zza(zzlz);
            }
            if (com_google_android_gms_internal_zzwc_zze.awV.length > 0) {
                com_google_android_gms_internal_zzwc_zze.ard = zzbwb().zzbxf();
                zzwb.zzb zzmo = zzbvy().zzmo(com_google_android_gms_measurement_internal_zzx_zza.auB.zzcs);
                if (zzmo == null || zzmo.awC == null) {
                    zzbwb().zzbxa().log("Did not find measurement config or missing version info");
                } else {
                    com_google_android_gms_internal_zzwc_zze.axt = zzmo.awC;
                }
                zzbvw().zza(com_google_android_gms_internal_zzwc_zze, z);
            }
            zzbvw().zzaf(com_google_android_gms_measurement_internal_zzx_zza.auC);
            zzbvw().zzmg(str2);
            zzbvw().setTransactionSuccessful();
            boolean z4 = com_google_android_gms_internal_zzwc_zze.awV.length > 0;
            zzbvw().endTransaction();
            return z4;
        } catch (Throwable th) {
            zzbvw().endTransaction();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    @WorkerThread
    public boolean isEnabled() {
        boolean z = false;
        zzzx();
        zzacj();
        if (zzbwd().zzbut()) {
            return false;
        }
        Boolean zzbuu = zzbwd().zzbuu();
        if (zzbuu != null) {
            z = zzbuu.booleanValue();
        } else if (!zzbwd().zzatu()) {
            z = true;
        }
        return zzbwc().zzch(z);
    }

    @WorkerThread
    protected void start() {
        zzzx();
        zzbvw().zzbwg();
        if (zzbwc().asZ.get() == 0) {
            zzbwc().asZ.set(zzabz().currentTimeMillis());
        }
        if (zzbxq()) {
            if (!(zzbwd().zzayi() || TextUtils.isEmpty(zzbvr().zzbth()))) {
                String zzbxk = zzbwc().zzbxk();
                if (zzbxk == null) {
                    zzbwc().zzmm(zzbvr().zzbth());
                } else if (!zzbxk.equals(zzbvr().zzbth())) {
                    zzbwb().zzbxc().log("Rechecking which service to use due to a GMP App Id change");
                    zzbwc().zzbxm();
                    this.auk.disconnect();
                    this.auk.zzadl();
                    zzbwc().zzmm(zzbvr().zzbth());
                }
            }
            if (!(zzbwd().zzayi() || TextUtils.isEmpty(zzbvr().zzbth()))) {
                zzbvq().zzbyq();
            }
        } else if (isEnabled()) {
            if (!zzbvx().zzez("android.permission.INTERNET")) {
                zzbwb().zzbwy().log("App is missing INTERNET permission");
            }
            if (!zzbvx().zzez("android.permission.ACCESS_NETWORK_STATE")) {
                zzbwb().zzbwy().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            if (!zzbwd().zzayi()) {
                if (!zzu.zzh(getContext(), false)) {
                    zzbwb().zzbwy().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzaf.zzi(getContext(), false)) {
                    zzbwb().zzbwy().log("AppMeasurementService not registered/enabled");
                }
            }
            zzbwb().zzbwy().log("Uploading is not possible. App measurement disabled");
        }
        zzbyg();
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzzx();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzbwb().zzbwy().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzbwb().zzbxa().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzbwb().zzbwy().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    @WorkerThread
    protected void zza(int i, Throwable th, byte[] bArr) {
        int i2 = 0;
        zzzx();
        zzacj();
        if (bArr == null) {
            bArr = new byte[0];
        }
        List<Long> list = this.auw;
        this.auw = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            zzbwc().asZ.set(zzabz().currentTimeMillis());
            zzbwc().ata.set(0);
            zzbyg();
            zzbwb().zzbxe().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
            zzbvw().beginTransaction();
            try {
                for (Long longValue : list) {
                    zzbvw().zzbj(longValue.longValue());
                }
                zzbvw().setTransactionSuccessful();
                if (zzbxv().zzagk() && zzbyf()) {
                    zzbye();
                    return;
                }
                this.auz = -1;
                zzbyg();
            } finally {
                zzbvw().endTransaction();
            }
        } else {
            zzbwb().zzbxe().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzbwc().ata.set(zzabz().currentTimeMillis());
            if (i == 503 || i == 429) {
                i2 = 1;
            }
            if (i2 != 0) {
                zzbwc().atb.set(zzabz().currentTimeMillis());
            }
            zzbyg();
        }
    }

    @WorkerThread
    void zza(AppMetadata appMetadata, long j) {
        zza zzlz = zzbvw().zzlz(appMetadata.packageName);
        if (!(zzlz == null || zzlz.zzbth() == null || zzlz.zzbth().equals(appMetadata.aqZ))) {
            zzbwb().zzbxa().log("New GMP App Id passed in. Removing cached database data.");
            zzbvw().zzme(zzlz.zzup());
            zzlz = null;
        }
        if (zzlz != null && zzlz.zzaaf() != null && !zzlz.zzaaf().equals(appMetadata.aii)) {
            Bundle bundle = new Bundle();
            bundle.putString("_pv", zzlz.zzaaf());
            zzb(new EventParcel("_au", new EventParams(bundle), "auto", j), appMetadata);
        }
    }

    void zza(zzh com_google_android_gms_measurement_internal_zzh, AppMetadata appMetadata) {
        zzzx();
        zzacj();
        zzaa.zzy(com_google_android_gms_measurement_internal_zzh);
        zzaa.zzy(appMetadata);
        zzaa.zzib(com_google_android_gms_measurement_internal_zzh.zzctj);
        zzaa.zzbt(com_google_android_gms_measurement_internal_zzh.zzctj.equals(appMetadata.packageName));
        zzwc.zze com_google_android_gms_internal_zzwc_zze = new zzwc.zze();
        com_google_android_gms_internal_zzwc_zze.awU = Integer.valueOf(1);
        com_google_android_gms_internal_zzwc_zze.axc = "android";
        com_google_android_gms_internal_zzwc_zze.zzcs = appMetadata.packageName;
        com_google_android_gms_internal_zzwc_zze.ara = appMetadata.ara;
        com_google_android_gms_internal_zzwc_zze.aii = appMetadata.aii;
        com_google_android_gms_internal_zzwc_zze.axp = Integer.valueOf((int) appMetadata.arg);
        com_google_android_gms_internal_zzwc_zze.axg = Long.valueOf(appMetadata.arb);
        com_google_android_gms_internal_zzwc_zze.aqZ = appMetadata.aqZ;
        com_google_android_gms_internal_zzwc_zze.axl = appMetadata.arc == 0 ? null : Long.valueOf(appMetadata.arc);
        Pair zzmk = zzbwc().zzmk(appMetadata.packageName);
        if (zzmk != null && !TextUtils.isEmpty((CharSequence) zzmk.first)) {
            com_google_android_gms_internal_zzwc_zze.axi = (String) zzmk.first;
            com_google_android_gms_internal_zzwc_zze.axj = (Boolean) zzmk.second;
        } else if (!zzbvs().zzdp(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzbwb().zzbxa().log("null secure ID");
                string = "null";
            } else if (string.isEmpty()) {
                zzbwb().zzbxa().log("empty secure ID");
            }
            com_google_android_gms_internal_zzwc_zze.axs = string;
        }
        com_google_android_gms_internal_zzwc_zze.axd = zzbvs().zzvt();
        com_google_android_gms_internal_zzwc_zze.zzdb = zzbvs().zzbws();
        com_google_android_gms_internal_zzwc_zze.axf = Integer.valueOf((int) zzbvs().zzbwt());
        com_google_android_gms_internal_zzwc_zze.axe = zzbvs().zzbwu();
        com_google_android_gms_internal_zzwc_zze.axh = null;
        com_google_android_gms_internal_zzwc_zze.awX = null;
        com_google_android_gms_internal_zzwc_zze.awY = null;
        com_google_android_gms_internal_zzwc_zze.awZ = null;
        zza zzlz = zzbvw().zzlz(appMetadata.packageName);
        if (zzlz == null) {
            zzlz = new zza(this, appMetadata.packageName);
            zzlz.zzlj(zzbwc().zzbxh());
            zzlz.zzlm(appMetadata.arh);
            zzlz.zzlk(appMetadata.aqZ);
            zzlz.zzll(zzbwc().zzml(appMetadata.packageName));
            zzlz.zzba(0);
            zzlz.zzav(0);
            zzlz.zzaw(0);
            zzlz.setAppVersion(appMetadata.aii);
            zzlz.zzax(appMetadata.arg);
            zzlz.zzln(appMetadata.ara);
            zzlz.zzay(appMetadata.arb);
            zzlz.zzaz(appMetadata.arc);
            zzlz.setMeasurementEnabled(appMetadata.are);
            zzbvw().zza(zzlz);
        }
        com_google_android_gms_internal_zzwc_zze.axk = zzlz.zzazn();
        com_google_android_gms_internal_zzwc_zze.arh = zzlz.zzbtj();
        List zzly = zzbvw().zzly(appMetadata.packageName);
        com_google_android_gms_internal_zzwc_zze.awW = new zzg[zzly.size()];
        for (int i = 0; i < zzly.size(); i++) {
            zzg com_google_android_gms_internal_zzwc_zzg = new zzg();
            com_google_android_gms_internal_zzwc_zze.awW[i] = com_google_android_gms_internal_zzwc_zzg;
            com_google_android_gms_internal_zzwc_zzg.name = ((zzak) zzly.get(i)).mName;
            com_google_android_gms_internal_zzwc_zzg.axx = Long.valueOf(((zzak) zzly.get(i)).avX);
            zzbvx().zza(com_google_android_gms_internal_zzwc_zzg, ((zzak) zzly.get(i)).zzcyd);
        }
        try {
            zzbvw().zza(com_google_android_gms_measurement_internal_zzh, zzbvw().zza(com_google_android_gms_internal_zzwc_zze), zza(com_google_android_gms_measurement_internal_zzh));
        } catch (IOException e) {
            zzbwb().zzbwy().zzj("Data loss. Failed to insert raw event metadata", e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzzx();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzbwb().zzbwy().log("Bad chanel to read from");
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
            zzbwb().zzbwy().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzbwb().zzbwy().zzj("Failed to write to channel", e);
            return false;
        }
    }

    @WorkerThread
    public byte[] zza(@NonNull EventParcel eventParcel, @Size(min = 1) String str) {
        zzacj();
        zzzx();
        zzbyc();
        zzaa.zzy(eventParcel);
        zzaa.zzib(str);
        zzd com_google_android_gms_internal_zzwc_zzd = new zzd();
        zzbvw().beginTransaction();
        try {
            zza zzlz = zzbvw().zzlz(str);
            byte[] bArr;
            if (zzlz == null) {
                zzbwb().zzbxd().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzlz.zzbtq()) {
                long j;
                zzwc.zze com_google_android_gms_internal_zzwc_zze = new zzwc.zze();
                com_google_android_gms_internal_zzwc_zzd.awS = new zzwc.zze[]{com_google_android_gms_internal_zzwc_zze};
                com_google_android_gms_internal_zzwc_zze.awU = Integer.valueOf(1);
                com_google_android_gms_internal_zzwc_zze.axc = "android";
                com_google_android_gms_internal_zzwc_zze.zzcs = zzlz.zzup();
                com_google_android_gms_internal_zzwc_zze.ara = zzlz.zzbtn();
                com_google_android_gms_internal_zzwc_zze.aii = zzlz.zzaaf();
                com_google_android_gms_internal_zzwc_zze.axp = Integer.valueOf((int) zzlz.zzbtm());
                com_google_android_gms_internal_zzwc_zze.axg = Long.valueOf(zzlz.zzbto());
                com_google_android_gms_internal_zzwc_zze.aqZ = zzlz.zzbth();
                com_google_android_gms_internal_zzwc_zze.axl = Long.valueOf(zzlz.zzbtp());
                Pair zzmk = zzbwc().zzmk(zzlz.zzup());
                if (!(zzmk == null || TextUtils.isEmpty((CharSequence) zzmk.first))) {
                    com_google_android_gms_internal_zzwc_zze.axi = (String) zzmk.first;
                    com_google_android_gms_internal_zzwc_zze.axj = (Boolean) zzmk.second;
                }
                com_google_android_gms_internal_zzwc_zze.axd = zzbvs().zzvt();
                com_google_android_gms_internal_zzwc_zze.zzdb = zzbvs().zzbws();
                com_google_android_gms_internal_zzwc_zze.axf = Integer.valueOf((int) zzbvs().zzbwt());
                com_google_android_gms_internal_zzwc_zze.axe = zzbvs().zzbwu();
                com_google_android_gms_internal_zzwc_zze.axk = zzlz.zzazn();
                com_google_android_gms_internal_zzwc_zze.arh = zzlz.zzbtj();
                List zzly = zzbvw().zzly(zzlz.zzup());
                com_google_android_gms_internal_zzwc_zze.awW = new zzg[zzly.size()];
                for (int i = 0; i < zzly.size(); i++) {
                    zzg com_google_android_gms_internal_zzwc_zzg = new zzg();
                    com_google_android_gms_internal_zzwc_zze.awW[i] = com_google_android_gms_internal_zzwc_zzg;
                    com_google_android_gms_internal_zzwc_zzg.name = ((zzak) zzly.get(i)).mName;
                    com_google_android_gms_internal_zzwc_zzg.axx = Long.valueOf(((zzak) zzly.get(i)).avX);
                    zzbvx().zza(com_google_android_gms_internal_zzwc_zzg, ((zzak) zzly.get(i)).zzcyd);
                }
                Bundle zzbww = eventParcel.arJ.zzbww();
                if ("_iap".equals(eventParcel.name)) {
                    zzbww.putLong("_c", 1);
                    zzbwb().zzbxd().log("Marking in-app purchase as real-time");
                    zzbww.putLong("_r", 1);
                }
                zzbww.putString("_o", eventParcel.arK);
                if (zzbvx().zznf(com_google_android_gms_internal_zzwc_zze.zzcs)) {
                    zzbvx().zza(zzbww, "_dbg", Long.valueOf(1));
                    zzbvx().zza(zzbww, "_r", Long.valueOf(1));
                }
                zzi zzap = zzbvw().zzap(str, eventParcel.name);
                if (zzap == null) {
                    zzbvw().zza(new zzi(str, eventParcel.name, 1, 0, eventParcel.arL));
                    j = 0;
                } else {
                    j = zzap.arF;
                    zzbvw().zza(zzap.zzbl(eventParcel.arL).zzbwv());
                }
                zzh com_google_android_gms_measurement_internal_zzh = new zzh(this, eventParcel.arK, str, eventParcel.name, eventParcel.arL, j, zzbww);
                zzb com_google_android_gms_internal_zzwc_zzb = new zzb();
                com_google_android_gms_internal_zzwc_zze.awV = new zzb[]{com_google_android_gms_internal_zzwc_zzb};
                com_google_android_gms_internal_zzwc_zzb.awO = Long.valueOf(com_google_android_gms_measurement_internal_zzh.vO);
                com_google_android_gms_internal_zzwc_zzb.name = com_google_android_gms_measurement_internal_zzh.mName;
                com_google_android_gms_internal_zzwc_zzb.awP = Long.valueOf(com_google_android_gms_measurement_internal_zzh.arB);
                com_google_android_gms_internal_zzwc_zzb.awN = new zzc[com_google_android_gms_measurement_internal_zzh.arC.size()];
                Iterator it = com_google_android_gms_measurement_internal_zzh.arC.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzwc_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzwc_zzb.awN[i2] = com_google_android_gms_internal_zzwc_zzc;
                    com_google_android_gms_internal_zzwc_zzc.name = str2;
                    zzbvx().zza(com_google_android_gms_internal_zzwc_zzc, com_google_android_gms_measurement_internal_zzh.arC.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzwc_zze.axo = zza(zzlz.zzup(), com_google_android_gms_internal_zzwc_zze.awW, com_google_android_gms_internal_zzwc_zze.awV);
                com_google_android_gms_internal_zzwc_zze.awY = com_google_android_gms_internal_zzwc_zzb.awO;
                com_google_android_gms_internal_zzwc_zze.awZ = com_google_android_gms_internal_zzwc_zzb.awO;
                long zzbtl = zzlz.zzbtl();
                com_google_android_gms_internal_zzwc_zze.axb = zzbtl != 0 ? Long.valueOf(zzbtl) : null;
                long zzbtk = zzlz.zzbtk();
                if (zzbtk != 0) {
                    zzbtl = zzbtk;
                }
                com_google_android_gms_internal_zzwc_zze.axa = zzbtl != 0 ? Long.valueOf(zzbtl) : null;
                zzlz.zzbtu();
                com_google_android_gms_internal_zzwc_zze.axm = Integer.valueOf((int) zzlz.zzbtr());
                com_google_android_gms_internal_zzwc_zze.axh = Long.valueOf(zzbwd().zzbto());
                com_google_android_gms_internal_zzwc_zze.awX = Long.valueOf(zzabz().currentTimeMillis());
                com_google_android_gms_internal_zzwc_zze.axn = Boolean.TRUE;
                zzlz.zzav(com_google_android_gms_internal_zzwc_zze.awY.longValue());
                zzlz.zzaw(com_google_android_gms_internal_zzwc_zze.awZ.longValue());
                zzbvw().zza(zzlz);
                zzbvw().setTransactionSuccessful();
                zzbvw().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzwc_zzd.cz()];
                    zzart zzbe = zzart.zzbe(bArr);
                    com_google_android_gms_internal_zzwc_zzd.zza(zzbe);
                    zzbe.cm();
                    return zzbvx().zzk(bArr);
                } catch (IOException e) {
                    zzbwb().zzbwy().zzj("Data loss. Failed to bundle and serialize", e);
                    return null;
                }
            } else {
                zzbwb().zzbxd().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzbvw().endTransaction();
                return bArr;
            }
        } finally {
            zzbvw().endTransaction();
        }
    }

    void zzaby() {
        zzbwd().zzayi();
    }

    public zze zzabz() {
        return this.zzaql;
    }

    void zzacj() {
        if (!this.cR) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    protected void zzag(List<Long> list) {
        zzaa.zzbt(!list.isEmpty());
        if (this.auw != null) {
            zzbwb().zzbwy().log("Set uploading progress before finishing the previous upload");
        } else {
            this.auw = new ArrayList(list);
        }
    }

    public void zzaw(boolean z) {
        zzbyg();
    }

    @WorkerThread
    void zzb(AppMetadata appMetadata, long j) {
        zzzx();
        zzacj();
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        bundle.putLong("_uwa", 0);
        bundle.putLong("_pfo", 0);
        bundle.putLong("_sys", 0);
        bundle.putLong("_sysu", 0);
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzbwb().zzbwy().log("PackageManager is null, first open report might be inaccurate");
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = packageManager.getPackageInfo(appMetadata.packageName, 0);
            } catch (NameNotFoundException e) {
                zzbwb().zzbwy().zzj("Package info is null, first open report might be inaccurate", e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = packageManager.getApplicationInfo(appMetadata.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzbwb().zzbwy().zzj("Application info is null, first open report might be inaccurate", e2);
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
        long zzmf = zzbvw().zzmf(appMetadata.packageName);
        if (zzmf >= 0) {
            bundle.putLong("_pfo", zzmf);
        }
        zzb(new EventParcel("_f", new EventParams(bundle), "auto", j), appMetadata);
    }

    @WorkerThread
    void zzb(EventParcel eventParcel, AppMetadata appMetadata) {
        long nanoTime = System.nanoTime();
        zzzx();
        zzacj();
        String str = appMetadata.packageName;
        zzaa.zzib(str);
        if (!zzal.zzc(eventParcel, appMetadata)) {
            return;
        }
        if (!appMetadata.are && !"_in".equals(eventParcel.name)) {
            zze(appMetadata);
        } else if (zzbvy().zzax(str, eventParcel.name)) {
            zzbwb().zzbxa().zzj("Dropping blacklisted event", eventParcel.name);
            Object obj = (zzbvx().zznh(str) || zzbvx().zzni(str)) ? 1 : null;
            if (obj == null && !"_err".equals(eventParcel.name)) {
                zzbvx().zza(11, "_ev", eventParcel.name, 0);
            }
            if (obj != null) {
                zza zzlz = zzbvw().zzlz(str);
                if (zzlz != null) {
                    if (Math.abs(zzabz().currentTimeMillis() - Math.max(zzlz.zzbtt(), zzlz.zzbts())) > zzbwd().zzbux()) {
                        zzbwb().zzbxd().log("Fetching config for blacklisted app");
                        zzb(zzlz);
                    }
                }
            }
        } else {
            if (zzbwb().zzbi(2)) {
                zzbwb().zzbxe().zzj("Logging event", eventParcel);
            }
            zzbvw().beginTransaction();
            try {
                Bundle zzbww = eventParcel.arJ.zzbww();
                zze(appMetadata);
                if ("_iap".equals(eventParcel.name) || Event.ECOMMERCE_PURCHASE.equals(eventParcel.name)) {
                    long round;
                    Object string = zzbww.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(eventParcel.name)) {
                        double d = zzbww.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzbww.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzbwb().zzbxa().zzj("Data lost. Currency value is too big", Double.valueOf(d));
                            zzbvw().setTransactionSuccessful();
                            zzbvw().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzbww.getLong(Param.VALUE);
                    }
                    if (!TextUtils.isEmpty(string)) {
                        String toUpperCase = string.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            zzak com_google_android_gms_measurement_internal_zzak;
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzak zzar = zzbvw().zzar(str, concat);
                            if (zzar == null || !(zzar.zzcyd instanceof Long)) {
                                zzbvw().zzz(str, zzbwd().zzls(str) - 1);
                                com_google_android_gms_measurement_internal_zzak = new zzak(str, concat, zzabz().currentTimeMillis(), Long.valueOf(round));
                            } else {
                                com_google_android_gms_measurement_internal_zzak = new zzak(str, concat, zzabz().currentTimeMillis(), Long.valueOf(round + ((Long) zzar.zzcyd).longValue()));
                            }
                            if (!zzbvw().zza(com_google_android_gms_measurement_internal_zzak)) {
                                zzbwb().zzbwy().zze("Too many unique user properties are set. Ignoring user property.", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzcyd);
                                zzbvx().zza(9, null, null, 0);
                            }
                        }
                    }
                }
                boolean zzmu = zzal.zzmu(eventParcel.name);
                boolean equals = "_err".equals(eventParcel.name);
                com.google.android.gms.measurement.internal.zze.zza zza = zzbvw().zza(zzbyb(), str, true, zzmu, false, equals, false);
                long zzbul = zza.arq - zzbwd().zzbul();
                if (zzbul > 0) {
                    if (zzbul % 1000 == 1) {
                        zzbwb().zzbwy().zzj("Data loss. Too many events logged. count", Long.valueOf(zza.arq));
                    }
                    zzbvx().zza(16, "_ev", eventParcel.name, 0);
                    zzbvw().setTransactionSuccessful();
                    return;
                }
                zzi com_google_android_gms_measurement_internal_zzi;
                if (zzmu) {
                    zzbul = zza.arp - zzbwd().zzbum();
                    if (zzbul > 0) {
                        if (zzbul % 1000 == 1) {
                            zzbwb().zzbwy().zzj("Data loss. Too many public events logged. count", Long.valueOf(zza.arp));
                        }
                        zzbvx().zza(16, "_ev", eventParcel.name, 0);
                        zzbvw().setTransactionSuccessful();
                        zzbvw().endTransaction();
                        return;
                    }
                }
                if (equals) {
                    zzbul = zza.ars - ((long) zzbwd().zzlo(appMetadata.packageName));
                    if (zzbul > 0) {
                        if (zzbul == 1) {
                            zzbwb().zzbwy().zzj("Too many error events logged. count", Long.valueOf(zza.ars));
                        }
                        zzbvw().setTransactionSuccessful();
                        zzbvw().endTransaction();
                        return;
                    }
                }
                zzbvx().zza(zzbww, "_o", eventParcel.arK);
                if (zzbvx().zznf(str)) {
                    zzbvx().zza(zzbww, "_dbg", Long.valueOf(1));
                    zzbvx().zza(zzbww, "_r", Long.valueOf(1));
                }
                long zzma = zzbvw().zzma(str);
                if (zzma > 0) {
                    zzbwb().zzbxa().zzj("Data lost. Too many events stored on disk, deleted", Long.valueOf(zzma));
                }
                zzh com_google_android_gms_measurement_internal_zzh = new zzh(this, eventParcel.arK, str, eventParcel.name, eventParcel.arL, 0, zzbww);
                zzi zzap = zzbvw().zzap(str, com_google_android_gms_measurement_internal_zzh.mName);
                if (zzap == null) {
                    zzma = zzbvw().zzmh(str);
                    zzbwd().zzbuk();
                    if (zzma >= 500) {
                        zzbwb().zzbwy().zze("Too many event names used, ignoring event. name, supported count", com_google_android_gms_measurement_internal_zzh.mName, Integer.valueOf(zzbwd().zzbuk()));
                        zzbvx().zza(8, null, null, 0);
                        zzbvw().endTransaction();
                        return;
                    }
                    com_google_android_gms_measurement_internal_zzi = new zzi(str, com_google_android_gms_measurement_internal_zzh.mName, 0, 0, com_google_android_gms_measurement_internal_zzh.vO);
                } else {
                    com_google_android_gms_measurement_internal_zzh = com_google_android_gms_measurement_internal_zzh.zza(this, zzap.arF);
                    com_google_android_gms_measurement_internal_zzi = zzap.zzbl(com_google_android_gms_measurement_internal_zzh.vO);
                }
                zzbvw().zza(com_google_android_gms_measurement_internal_zzi);
                zza(com_google_android_gms_measurement_internal_zzh, appMetadata);
                zzbvw().setTransactionSuccessful();
                if (zzbwb().zzbi(2)) {
                    zzbwb().zzbxe().zzj("Event recorded", com_google_android_gms_measurement_internal_zzh);
                }
                zzbvw().endTransaction();
                zzbyg();
                zzbwb().zzbxe().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
            } finally {
                zzbvw().endTransaction();
            }
        }
    }

    @WorkerThread
    void zzb(EventParcel eventParcel, String str) {
        zza zzlz = zzbvw().zzlz(str);
        if (zzlz == null || TextUtils.isEmpty(zzlz.zzaaf())) {
            zzbwb().zzbxd().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = getContext().getPackageManager().getPackageInfo(str, 0).versionName;
            if (!(zzlz.zzaaf() == null || zzlz.zzaaf().equals(str2))) {
                zzbwb().zzbxa().zzj("App version does not match; dropping event", str);
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(eventParcel.name)) {
                zzbwb().zzbxa().zzj("Could not find package", str);
            }
        }
        EventParcel eventParcel2 = eventParcel;
        zzb(eventParcel2, new AppMetadata(str, zzlz.zzbth(), zzlz.zzaaf(), zzlz.zzbtm(), zzlz.zzbtn(), zzlz.zzbto(), zzlz.zzbtp(), null, zzlz.zzbtq(), false, zzlz.zzbtj()));
    }

    @WorkerThread
    void zzb(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) {
        int i = 0;
        zzzx();
        zzacj();
        if (!TextUtils.isEmpty(appMetadata.aqZ)) {
            if (appMetadata.are) {
                int zzmy = zzbvx().zzmy(userAttributeParcel.name);
                String zza;
                if (zzmy != 0) {
                    zza = zzbvx().zza(userAttributeParcel.name, zzbwd().zzbue(), true);
                    if (userAttributeParcel.name != null) {
                        i = userAttributeParcel.name.length();
                    }
                    zzbvx().zza(zzmy, "_ev", zza, i);
                    return;
                }
                zzmy = zzbvx().zzm(userAttributeParcel.name, userAttributeParcel.getValue());
                if (zzmy != 0) {
                    zza = zzbvx().zza(userAttributeParcel.name, zzbwd().zzbue(), true);
                    Object value = userAttributeParcel.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzbvx().zza(zzmy, "_ev", zza, i);
                    return;
                }
                Object zzn = zzbvx().zzn(userAttributeParcel.name, userAttributeParcel.getValue());
                if (zzn != null) {
                    zzak com_google_android_gms_measurement_internal_zzak = new zzak(appMetadata.packageName, userAttributeParcel.name, userAttributeParcel.avT, zzn);
                    zzbwb().zzbxd().zze("Setting user property", com_google_android_gms_measurement_internal_zzak.mName, zzn);
                    zzbvw().beginTransaction();
                    try {
                        zze(appMetadata);
                        boolean zza2 = zzbvw().zza(com_google_android_gms_measurement_internal_zzak);
                        zzbvw().setTransactionSuccessful();
                        if (zza2) {
                            zzbwb().zzbxd().zze("User property set", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzcyd);
                        } else {
                            zzbwb().zzbwy().zze("Too many unique user properties are set. Ignoring user property.", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzcyd);
                            zzbvx().zza(9, null, null, 0);
                        }
                        zzbvw().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzbvw().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zze(appMetadata);
        }
    }

    void zzb(zza com_google_android_gms_measurement_internal_zza) {
        String zzao = zzbwd().zzao(com_google_android_gms_measurement_internal_zza.zzbth(), com_google_android_gms_measurement_internal_zza.zzazn());
        try {
            URL url = new URL(zzao);
            zzbwb().zzbxe().zzj("Fetching remote configuration", com_google_android_gms_measurement_internal_zza.zzup());
            zzwb.zzb zzmo = zzbvy().zzmo(com_google_android_gms_measurement_internal_zza.zzup());
            Map map = null;
            CharSequence zzmp = zzbvy().zzmp(com_google_android_gms_measurement_internal_zza.zzup());
            if (!(zzmo == null || TextUtils.isEmpty(zzmp))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzmp);
            }
            zzbxv().zza(com_google_android_gms_measurement_internal_zza.zzup(), url, map, new zza(this) {
                final /* synthetic */ zzx auA;

                {
                    this.auA = r1;
                }

                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                    this.auA.zzb(str, i, th, bArr, map);
                }
            });
        } catch (MalformedURLException e) {
            zzbwb().zzbwy().zzj("Failed to parse config URL. Not fetching", zzao);
        }
    }

    void zzb(zzaa com_google_android_gms_measurement_internal_zzaa) {
        this.aux++;
    }

    @WorkerThread
    void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        int i2 = 0;
        zzzx();
        zzacj();
        zzaa.zzib(str);
        if (bArr == null) {
            bArr = new byte[0];
        }
        zzbvw().beginTransaction();
        try {
            zza zzlz = zzbvw().zzlz(str);
            int i3 = ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null) ? 1 : 0;
            if (zzlz == null) {
                zzbwb().zzbxa().zzj("App does not exist in onConfigFetched", str);
            } else if (i3 != 0 || i == 404) {
                List list = map != null ? (List) map.get("Last-Modified") : null;
                String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
                if (i == 404 || i == 304) {
                    if (zzbvy().zzmo(str) == null && !zzbvy().zzb(str, null, null)) {
                        zzbvw().endTransaction();
                        return;
                    }
                } else if (!zzbvy().zzb(str, bArr, str2)) {
                    zzbvw().endTransaction();
                    return;
                }
                zzlz.zzbb(zzabz().currentTimeMillis());
                zzbvw().zza(zzlz);
                if (i == 404) {
                    zzbwb().zzbxa().log("Config not found. Using empty config");
                } else {
                    zzbwb().zzbxe().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzbxv().zzagk() && zzbyf()) {
                    zzbye();
                } else {
                    zzbyg();
                }
            } else {
                zzlz.zzbc(zzabz().currentTimeMillis());
                zzbvw().zza(zzlz);
                zzbwb().zzbxe().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzbvy().zzmq(str);
                zzbwc().ata.set(zzabz().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzbwc().atb.set(zzabz().currentTimeMillis());
                }
                zzbyg();
            }
            zzbvw().setTransactionSuccessful();
        } finally {
            zzbvw().endTransaction();
        }
    }

    boolean zzbm(long j) {
        return zzh(null, j);
    }

    public zzc zzbvp() {
        zza(this.auq);
        return this.auq;
    }

    public zzac zzbvq() {
        zza(this.aum);
        return this.aum;
    }

    public zzn zzbvr() {
        zza(this.aun);
        return this.aun;
    }

    public zzg zzbvs() {
        zza(this.aul);
        return this.aul;
    }

    public zzae zzbvt() {
        zza(this.auk);
        return this.auk;
    }

    public zzad zzbvu() {
        zza(this.auj);
        return this.auj;
    }

    public zzo zzbvv() {
        zza(this.auh);
        return this.auh;
    }

    public zze zzbvw() {
        zza(this.aug);
        return this.aug;
    }

    public zzal zzbvx() {
        zza(this.auf);
        return this.auf;
    }

    public zzv zzbvy() {
        zza(this.auc);
        return this.auc;
    }

    public zzag zzbvz() {
        zza(this.aub);
        return this.aub;
    }

    public zzw zzbwa() {
        zza(this.aua);
        return this.aua;
    }

    public zzq zzbwb() {
        zza(this.atZ);
        return this.atZ;
    }

    public zzt zzbwc() {
        zza(this.atY);
        return this.atY;
    }

    public zzd zzbwd() {
        return this.atX;
    }

    @WorkerThread
    protected boolean zzbxq() {
        boolean z = false;
        zzacj();
        zzzx();
        if (this.aus == null || this.aut == 0 || !(this.aus == null || this.aus.booleanValue() || Math.abs(zzabz().elapsedRealtime() - this.aut) <= 1000)) {
            this.aut = zzabz().elapsedRealtime();
            zzbwd().zzayi();
            if (zzbvx().zzez("android.permission.INTERNET") && zzbvx().zzez("android.permission.ACCESS_NETWORK_STATE") && zzu.zzh(getContext(), false) && zzaf.zzi(getContext(), false)) {
                z = true;
            }
            this.aus = Boolean.valueOf(z);
            if (this.aus.booleanValue()) {
                this.aus = Boolean.valueOf(zzbvx().zznb(zzbvr().zzbth()));
            }
        }
        return this.aus.booleanValue();
    }

    public zzq zzbxr() {
        return (this.atZ == null || !this.atZ.isInitialized()) ? null : this.atZ;
    }

    zzw zzbxs() {
        return this.aua;
    }

    public AppMeasurement zzbxt() {
        return this.aud;
    }

    public FirebaseAnalytics zzbxu() {
        return this.aue;
    }

    public zzr zzbxv() {
        zza(this.aui);
        return this.aui;
    }

    public zzs zzbxw() {
        if (this.auo != null) {
            return this.auo;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public zzai zzbxx() {
        zza(this.aup);
        return this.aup;
    }

    FileChannel zzbxy() {
        return this.auv;
    }

    @WorkerThread
    void zzbxz() {
        zzzx();
        zzacj();
        if (zzbyk() && zzbya()) {
            zzv(zza(zzbxy()), zzbvr().zzbwx());
        }
    }

    @WorkerThread
    boolean zzbya() {
        zzzx();
        try {
            this.auv = new RandomAccessFile(new File(getContext().getFilesDir(), this.aug.zzade()), "rw").getChannel();
            this.auu = this.auv.tryLock();
            if (this.auu != null) {
                zzbwb().zzbxe().log("Storage concurrent access okay");
                return true;
            }
            zzbwb().zzbwy().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzbwb().zzbwy().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Failed to access storage lock file", e2);
        }
    }

    long zzbyb() {
        return ((((zzabz().currentTimeMillis() + zzbwc().zzbxi()) / 1000) / 60) / 60) / 24;
    }

    void zzbyc() {
        if (!zzbwd().zzayi()) {
            throw new IllegalStateException("Unexpected call on client side");
        }
    }

    @WorkerThread
    protected boolean zzbyd() {
        zzzx();
        return this.auw != null;
    }

    @WorkerThread
    public void zzbye() {
        int i = 0;
        zzzx();
        zzacj();
        if (!zzbwd().zzayi()) {
            Boolean zzbxl = zzbwc().zzbxl();
            if (zzbxl == null) {
                zzbwb().zzbxa().log("Upload data called on the client side before use of service was decided");
                return;
            } else if (zzbxl.booleanValue()) {
                zzbwb().zzbwy().log("Upload called in the client side when service should be used");
                return;
            }
        }
        if (zzbyd()) {
            zzbwb().zzbxa().log("Uploading requested multiple times");
        } else if (zzbxv().zzagk()) {
            long currentTimeMillis = zzabz().currentTimeMillis();
            zzbm(currentTimeMillis - zzbwd().zzbvd());
            long j = zzbwc().asZ.get();
            if (j != 0) {
                zzbwb().zzbxd().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzbwe = zzbvw().zzbwe();
            Object zzbk;
            if (TextUtils.isEmpty(zzbwe)) {
                this.auz = -1;
                zzbk = zzbvw().zzbk(currentTimeMillis - zzbwd().zzbvd());
                if (!TextUtils.isEmpty(zzbk)) {
                    zza zzlz = zzbvw().zzlz(zzbk);
                    if (zzlz != null) {
                        zzb(zzlz);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.auz == -1) {
                this.auz = zzbvw().zzbwm();
            }
            List<Pair> zzn = zzbvw().zzn(zzbwe, zzbwd().zzlv(zzbwe), zzbwd().zzlw(zzbwe));
            if (!zzn.isEmpty()) {
                zzwc.zze com_google_android_gms_internal_zzwc_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzwc_zze = (zzwc.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzwc_zze.axi)) {
                        obj = com_google_android_gms_internal_zzwc_zze.axi;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzwc_zze = (zzwc.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzwc_zze.axi) && !com_google_android_gms_internal_zzwc_zze.axi.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzwc_zzd = new zzd();
                com_google_android_gms_internal_zzwc_zzd.awS = new zzwc.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzwc_zzd.awS.length) {
                    com_google_android_gms_internal_zzwc_zzd.awS[i] = (zzwc.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzwc_zzd.awS[i].axh = Long.valueOf(zzbwd().zzbto());
                    com_google_android_gms_internal_zzwc_zzd.awS[i].awX = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzwc_zzd.awS[i].axn = Boolean.valueOf(zzbwd().zzayi());
                    i++;
                }
                zzbk = zzbwb().zzbi(2) ? zzal.zzb(com_google_android_gms_internal_zzwc_zzd) : null;
                byte[] zza = zzbvx().zza(com_google_android_gms_internal_zzwc_zzd);
                String zzbvc = zzbwd().zzbvc();
                try {
                    URL url = new URL(zzbvc);
                    zzag(arrayList);
                    zzbwc().ata.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzwc_zzd.awS.length > 0) {
                        obj2 = com_google_android_gms_internal_zzwc_zzd.awS[0].zzcs;
                    }
                    zzbwb().zzbxe().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzbk);
                    zzbxv().zza(zzbwe, url, zza, null, new zza(this) {
                        final /* synthetic */ zzx auA;

                        {
                            this.auA = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.auA.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e) {
                    zzbwb().zzbwy().zzj("Failed to parse upload URL. Not uploading", zzbvc);
                }
            }
        } else {
            zzbwb().zzbxa().log("Network not connected, ignoring upload request");
            zzbyg();
        }
    }

    void zzbyi() {
        this.auy++;
    }

    @WorkerThread
    void zzbyj() {
        zzzx();
        zzacj();
        if (!this.aur) {
            zzbwb().zzbxc().log("This instance being marked as an uploader");
            zzbxz();
        }
        this.aur = true;
    }

    @WorkerThread
    boolean zzbyk() {
        zzzx();
        zzacj();
        return this.aur;
    }

    void zzc(AppMetadata appMetadata) {
        zzzx();
        zzacj();
        zzaa.zzib(appMetadata.packageName);
        zze(appMetadata);
    }

    @WorkerThread
    void zzc(AppMetadata appMetadata, long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("_et", 1);
        zzb(new EventParcel("_e", new EventParams(bundle), "auto", j), appMetadata);
    }

    @WorkerThread
    void zzc(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) {
        zzzx();
        zzacj();
        if (!TextUtils.isEmpty(appMetadata.aqZ)) {
            if (appMetadata.are) {
                zzbwb().zzbxd().zzj("Removing user property", userAttributeParcel.name);
                zzbvw().beginTransaction();
                try {
                    zze(appMetadata);
                    zzbvw().zzaq(appMetadata.packageName, userAttributeParcel.name);
                    zzbvw().setTransactionSuccessful();
                    zzbwb().zzbxd().zzj("User property removed", userAttributeParcel.name);
                } finally {
                    zzbvw().endTransaction();
                }
            } else {
                zze(appMetadata);
            }
        }
    }

    @WorkerThread
    public void zzd(AppMetadata appMetadata) {
        zzzx();
        zzacj();
        zzaa.zzy(appMetadata);
        zzaa.zzib(appMetadata.packageName);
        if (!TextUtils.isEmpty(appMetadata.aqZ)) {
            if (appMetadata.are) {
                long currentTimeMillis = zzabz().currentTimeMillis();
                zzbvw().beginTransaction();
                try {
                    zza(appMetadata, currentTimeMillis);
                    zze(appMetadata);
                    if (zzbvw().zzap(appMetadata.packageName, "_f") == null) {
                        zzb(new UserAttributeParcel("_fot", currentTimeMillis, Long.valueOf((1 + (currentTimeMillis / 3600000)) * 3600000), "auto"), appMetadata);
                        zzb(appMetadata, currentTimeMillis);
                        zzc(appMetadata, currentTimeMillis);
                    } else if (appMetadata.arf) {
                        zzd(appMetadata, currentTimeMillis);
                    }
                    zzbvw().setTransactionSuccessful();
                } finally {
                    zzbvw().endTransaction();
                }
            } else {
                zze(appMetadata);
            }
        }
    }

    @WorkerThread
    void zzd(AppMetadata appMetadata, long j) {
        zzb(new EventParcel("_cd", new EventParams(new Bundle()), "auto", j), appMetadata);
    }

    @WorkerThread
    boolean zzv(int i, int i2) {
        zzzx();
        if (i > i2) {
            zzbwb().zzbwy().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzbxy())) {
                zzbwb().zzbxe().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzbwb().zzbwy().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }

    @WorkerThread
    public void zzzx() {
        zzbwa().zzzx();
    }
}
