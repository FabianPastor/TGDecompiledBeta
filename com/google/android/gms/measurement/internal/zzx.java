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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvl;
import com.google.android.gms.internal.zzvm;
import com.google.android.gms.internal.zzvm.zzb;
import com.google.android.gms.internal.zzvm.zzc;
import com.google.android.gms.internal.zzvm.zzd;
import com.google.android.gms.internal.zzvm.zzg;
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
    private final zze zzapy;

    private class zza implements zzb {
        final /* synthetic */ zzx arp;
        zzvm.zze arq;
        List<Long> arr;
        long ars;
        List<zzb> zzamv;

        private zza(zzx com_google_android_gms_measurement_internal_zzx) {
            this.arp = com_google_android_gms_measurement_internal_zzx;
        }

        private long zza(zzb com_google_android_gms_internal_zzvm_zzb) {
            return ((com_google_android_gms_internal_zzvm_zzb.atp.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zzamv == null || this.zzamv.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzvm_zzb) {
            zzac.zzy(com_google_android_gms_internal_zzvm_zzb);
            if (this.zzamv == null) {
                this.zzamv = new ArrayList();
            }
            if (this.arr == null) {
                this.arr = new ArrayList();
            }
            if (this.zzamv.size() > 0 && zza((zzb) this.zzamv.get(0)) != zza(com_google_android_gms_internal_zzvm_zzb)) {
                return false;
            }
            long db = this.ars + ((long) com_google_android_gms_internal_zzvm_zzb.db());
            if (db >= ((long) this.arp.zzbvi().zzbuh())) {
                return false;
            }
            this.ars = db;
            this.zzamv.add(com_google_android_gms_internal_zzvm_zzb);
            this.arr.add(Long.valueOf(j));
            return this.zzamv.size() < this.arp.zzbvi().zzbui();
        }

        public void zzb(zzvm.zze com_google_android_gms_internal_zzvm_zze) {
            zzac.zzy(com_google_android_gms_internal_zzvm_zze);
            this.arq = com_google_android_gms_internal_zzvm_zze;
        }
    }

    zzx(zzab com_google_android_gms_measurement_internal_zzab) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzab);
        this.mContext = com_google_android_gms_measurement_internal_zzab.mContext;
        this.zzapy = com_google_android_gms_measurement_internal_zzab.zzm(this);
        this.aqQ = com_google_android_gms_measurement_internal_zzab.zza(this);
        zzt zzb = com_google_android_gms_measurement_internal_zzab.zzb(this);
        zzb.initialize();
        this.aqR = zzb;
        zzp zzc = com_google_android_gms_measurement_internal_zzab.zzc(this);
        zzc.initialize();
        this.aqS = zzc;
        zzbvg().zzbwh().zzj("App measurement is starting up, version", Long.valueOf(zzbvi().zzbsy()));
        zzbvg().zzbwh().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzbvg().zzbwi().log("Debug-level message logging enabled");
        zzbvg().zzbwi().zzj("AppMeasurement singleton hash", Integer.valueOf(System.identityHashCode(this)));
        this.aqY = com_google_android_gms_measurement_internal_zzab.zzj(this);
        zzg zzo = com_google_android_gms_measurement_internal_zzab.zzo(this);
        zzo.initialize();
        this.arc = zzo;
        zzn zzp = com_google_android_gms_measurement_internal_zzab.zzp(this);
        zzp.initialize();
        this.are = zzp;
        String zzti = zzp.zzti();
        if (zzbvc().zzni(zzti)) {
            zzbvg().zzbwh().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop firebase.analytics.debug-mode .none.");
        } else {
            com.google.android.gms.measurement.internal.zzp.zza zzbwh = zzbvg().zzbwh();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop firebase.analytics.debug-mode ";
            zzti = String.valueOf(zzti);
            zzbwh.log(zzti.length() != 0 ? str.concat(zzti) : new String(str));
        }
        zze zzk = com_google_android_gms_measurement_internal_zzab.zzk(this);
        zzk.initialize();
        this.aqZ = zzk;
        zzc zzs = com_google_android_gms_measurement_internal_zzab.zzs(this);
        zzs.initialize();
        this.arh = zzs;
        zzq zzl = com_google_android_gms_measurement_internal_zzab.zzl(this);
        zzl.initialize();
        this.ara = zzl;
        zzad zzn = com_google_android_gms_measurement_internal_zzab.zzn(this);
        zzn.initialize();
        this.arb = zzn;
        zzac zzi = com_google_android_gms_measurement_internal_zzab.zzi(this);
        zzi.initialize();
        this.ard = zzi;
        zzai zzr = com_google_android_gms_measurement_internal_zzab.zzr(this);
        zzr.initialize();
        this.arg = zzr;
        this.arf = com_google_android_gms_measurement_internal_zzab.zzq(this);
        this.aqW = com_google_android_gms_measurement_internal_zzab.zzh(this);
        this.aqX = com_google_android_gms_measurement_internal_zzab.zzg(this);
        zzaf zze = com_google_android_gms_measurement_internal_zzab.zze(this);
        zze.initialize();
        this.aqU = zze;
        zzv zzf = com_google_android_gms_measurement_internal_zzab.zzf(this);
        zzf.initialize();
        this.aqV = zzf;
        zzw zzd = com_google_android_gms_measurement_internal_zzab.zzd(this);
        zzd.initialize();
        this.aqT = zzd;
        if (this.arn != this.aro) {
            zzbvg().zzbwc().zze("Not all components initialized", Integer.valueOf(this.arn), Integer.valueOf(this.aro));
        }
        this.aJ = true;
        if (!(this.aqQ.zzact() || zzbxg())) {
            if (!(this.mContext.getApplicationContext() instanceof Application)) {
                zzbvg().zzbwe().log("Application context is not an Application");
            } else if (VERSION.SDK_INT >= 14) {
                zzbux().zzbxv();
            } else {
                zzbvg().zzbwi().log("Not tracking deep linking pre-ICS");
            }
        }
        this.aqT.zzm(new Runnable(this) {
            final /* synthetic */ zzx arp;

            {
                this.arp = r1;
            }

            public void run() {
                this.arp.start();
            }
        });
    }

    @WorkerThread
    private void zza(int i, Throwable th, byte[] bArr) {
        int i2 = 0;
        zzyl();
        zzaax();
        if (bArr == null) {
            bArr = new byte[0];
        }
        List<Long> list = this.arm;
        this.arm = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            zzbvh().apQ.set(zzaan().currentTimeMillis());
            zzbvh().apR.set(0);
            zzbxm();
            zzbvg().zzbwj().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
            zzbvb().beginTransaction();
            try {
                for (Long longValue : list) {
                    zzbvb().zzbk(longValue.longValue());
                }
                zzbvb().setTransactionSuccessful();
                if (zzbxa().zzafa() && zzbxl()) {
                    zzbxk();
                } else {
                    zzbxm();
                }
            } finally {
                zzbvb().endTransaction();
            }
        } else {
            zzbvg().zzbwj().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzbvh().apR.set(zzaan().currentTimeMillis());
            if (i == 503 || i == 429) {
                i2 = 1;
            }
            if (i2 != 0) {
                zzbvh().apS.set(zzaan().currentTimeMillis());
            }
            zzbxm();
        }
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
        if (com_google_android_gms_measurement_internal_zzh.aos == null) {
            return false;
        }
        Iterator it = com_google_android_gms_measurement_internal_zzh.aos.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzbvd().zzay(com_google_android_gms_measurement_internal_zzh.zzcpe, com_google_android_gms_measurement_internal_zzh.mName) && zzbvb().zza(zzbxh(), com_google_android_gms_measurement_internal_zzh.zzcpe, false, false, false, false, false).aoj < ((long) zzbvi().zzlq(com_google_android_gms_measurement_internal_zzh.zzcpe));
    }

    private com.google.android.gms.internal.zzvm.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzvm_zzgArr, zzb[] com_google_android_gms_internal_zzvm_zzbArr) {
        zzac.zzhz(str);
        return zzbuw().zza(str, com_google_android_gms_internal_zzvm_zzbArr, com_google_android_gms_internal_zzvm_zzgArr);
    }

    private void zzag(List<Long> list) {
        zzac.zzbs(!list.isEmpty());
        if (this.arm != null) {
            zzbvg().zzbwc().log("Set uploading progress before finishing the previous upload");
        } else {
            this.arm = new ArrayList(list);
        }
    }

    @WorkerThread
    private boolean zzbxj() {
        zzyl();
        return this.arm != null;
    }

    private boolean zzbxl() {
        zzyl();
        zzaax();
        return zzbvb().zzbvp() || !TextUtils.isEmpty(zzbvb().zzbvj());
    }

    @WorkerThread
    private void zzbxm() {
        zzyl();
        zzaax();
        if (!zzbxq()) {
            return;
        }
        if (zzbwv() && zzbxl()) {
            long zzbxn = zzbxn();
            if (zzbxn == 0) {
                zzbxb().unregister();
                zzbxc().cancel();
                return;
            } else if (zzbxa().zzafa()) {
                long j = zzbvh().apS.get();
                long zzbul = zzbvi().zzbul();
                if (!zzbvc().zzg(j, zzbul)) {
                    zzbxn = Math.max(zzbxn, j + zzbul);
                }
                zzbxb().unregister();
                zzbxn -= zzaan().currentTimeMillis();
                if (zzbxn <= 0) {
                    zzbxn = zzbvi().zzbuo();
                }
                zzbvg().zzbwj().zzj("Upload scheduled in approximately ms", Long.valueOf(zzbxn));
                zzbxc().zzx(zzbxn);
                return;
            } else {
                zzbxb().zzaex();
                zzbxc().cancel();
                return;
            }
        }
        zzbxb().unregister();
        zzbxc().cancel();
    }

    private long zzbxn() {
        long currentTimeMillis = zzaan().currentTimeMillis();
        long zzbur = zzbvi().zzbur();
        Object obj = (zzbvb().zzbvq() || zzbvb().zzbvk()) ? 1 : null;
        long zzbun = obj != null ? zzbvi().zzbun() : zzbvi().zzbum();
        long j = zzbvh().apQ.get();
        long j2 = zzbvh().apR.get();
        long max = Math.max(zzbvb().zzbvn(), zzbvb().zzbvo());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzbur;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzbun;
        }
        if (!zzbvc().zzg(j, zzbun)) {
            currentTimeMillis = j + zzbun;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzbvi().zzbut(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzbvi().zzbus();
            if (currentTimeMillis > j2) {
                return currentTimeMillis;
            }
        }
        return 0;
    }

    public static zzx zzdt(Context context) {
        zzac.zzy(context);
        zzac.zzy(context.getApplicationContext());
        if (aqP == null) {
            synchronized (zzx.class) {
                if (aqP == null) {
                    aqP = new zzab(context).zzbxu();
                }
            }
        }
        return aqP;
    }

    @WorkerThread
    private void zze(AppMetadata appMetadata) {
        Object obj = 1;
        zzyl();
        zzaax();
        zzac.zzy(appMetadata);
        zzac.zzhz(appMetadata.packageName);
        zza zzlz = zzbvb().zzlz(appMetadata.packageName);
        String zzmm = zzbvh().zzmm(appMetadata.packageName);
        Object obj2 = null;
        if (zzlz == null) {
            zza com_google_android_gms_measurement_internal_zza = new zza(this, appMetadata.packageName);
            com_google_android_gms_measurement_internal_zza.zzlj(zzbvh().zzbwm());
            com_google_android_gms_measurement_internal_zza.zzll(zzmm);
            zzlz = com_google_android_gms_measurement_internal_zza;
            obj2 = 1;
        } else if (!zzmm.equals(zzlz.zzbss())) {
            zzlz.zzll(zzmm);
            zzlz.zzlj(zzbvh().zzbwm());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.anQ) || appMetadata.anQ.equals(zzlz.zzbsr()))) {
            zzlz.zzlk(appMetadata.anQ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.anY) || appMetadata.anY.equals(zzlz.zzbst()))) {
            zzlz.zzlm(appMetadata.anY);
            obj2 = 1;
        }
        if (!(appMetadata.anS == 0 || appMetadata.anS == zzlz.zzbsy())) {
            zzlz.zzaz(appMetadata.anS);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.afY) || appMetadata.afY.equals(zzlz.zzyt()))) {
            zzlz.setAppVersion(appMetadata.afY);
            obj2 = 1;
        }
        if (appMetadata.anX != zzlz.zzbsw()) {
            zzlz.zzay(appMetadata.anX);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(appMetadata.anR) || appMetadata.anR.equals(zzlz.zzbsx()))) {
            zzlz.zzln(appMetadata.anR);
            obj2 = 1;
        }
        if (appMetadata.anT != zzlz.zzbsz()) {
            zzlz.zzba(appMetadata.anT);
            obj2 = 1;
        }
        if (appMetadata.anV != zzlz.zzbta()) {
            zzlz.setMeasurementEnabled(appMetadata.anV);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzbvb().zza(zzlz);
        }
    }

    private boolean zzj(String str, long j) {
        zzbvb().beginTransaction();
        try {
            zzx com_google_android_gms_measurement_internal_zzx = this;
            zza com_google_android_gms_measurement_internal_zzx_zza = new zza();
            zzbvb().zza(str, j, (zzb) com_google_android_gms_measurement_internal_zzx_zza);
            if (com_google_android_gms_measurement_internal_zzx_zza.isEmpty()) {
                zzbvb().setTransactionSuccessful();
                zzbvb().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzvm.zze com_google_android_gms_internal_zzvm_zze = com_google_android_gms_measurement_internal_zzx_zza.arq;
            com_google_android_gms_internal_zzvm_zze.atw = new zzb[com_google_android_gms_measurement_internal_zzx_zza.zzamv.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_measurement_internal_zzx_zza.zzamv.size()) {
                boolean z2;
                if (zzbvd().zzax(com_google_android_gms_measurement_internal_zzx_zza.arq.zzck, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name)) {
                    zzbvg().zzbwe().zzj("Dropping blacklisted raw event", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name);
                    zzbvc().zza(11, "_ev", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name, 0);
                    i = i2;
                    z2 = z;
                } else {
                    int i4;
                    boolean z3;
                    if (zzbvd().zzay(com_google_android_gms_measurement_internal_zzx_zza.arq.zzck, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzvm_zzcArr;
                        zzc com_google_android_gms_internal_zzvm_zzc;
                        zzb com_google_android_gms_internal_zzvm_zzb;
                        Object obj = null;
                        Object obj2 = null;
                        if (((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato == null) {
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzvm_zzcArr2 = ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato;
                        int length = com_google_android_gms_internal_zzvm_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            Object obj3;
                            zzc com_google_android_gms_internal_zzvm_zzc2 = com_google_android_gms_internal_zzvm_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzvm_zzc2.name)) {
                                com_google_android_gms_internal_zzvm_zzc2.ats = Long.valueOf(1);
                                obj = 1;
                                obj3 = obj2;
                            } else if ("_r".equals(com_google_android_gms_internal_zzvm_zzc2.name)) {
                                com_google_android_gms_internal_zzvm_zzc2.ats = Long.valueOf(1);
                                obj3 = 1;
                            } else {
                                obj3 = obj2;
                            }
                            i5++;
                            obj2 = obj3;
                        }
                        if (obj == null) {
                            zzbvg().zzbwj().zzj("Marking event as conversion", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name);
                            com_google_android_gms_internal_zzvm_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato.length + 1);
                            com_google_android_gms_internal_zzvm_zzc = new zzc();
                            com_google_android_gms_internal_zzvm_zzc.name = "_c";
                            com_google_android_gms_internal_zzvm_zzc.ats = Long.valueOf(1);
                            com_google_android_gms_internal_zzvm_zzcArr[com_google_android_gms_internal_zzvm_zzcArr.length - 1] = com_google_android_gms_internal_zzvm_zzc;
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato = com_google_android_gms_internal_zzvm_zzcArr;
                        }
                        if (obj2 == null) {
                            zzbvg().zzbwj().zzj("Marking event as real-time", ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name);
                            com_google_android_gms_internal_zzvm_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato, ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato.length + 1);
                            com_google_android_gms_internal_zzvm_zzc = new zzc();
                            com_google_android_gms_internal_zzvm_zzc.name = "_r";
                            com_google_android_gms_internal_zzvm_zzc.ats = Long.valueOf(1);
                            com_google_android_gms_internal_zzvm_zzcArr[com_google_android_gms_internal_zzvm_zzcArr.length - 1] = com_google_android_gms_internal_zzvm_zzc;
                            ((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).ato = com_google_android_gms_internal_zzvm_zzcArr;
                        }
                        boolean zzmx = zzal.zzmx(((zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3)).name);
                        if (zzbvb().zza(zzbxh(), com_google_android_gms_measurement_internal_zzx_zza.arq.zzck, false, false, false, false, true).aoj > ((long) zzbvi().zzlq(com_google_android_gms_measurement_internal_zzx_zza.arq.zzck))) {
                            com_google_android_gms_internal_zzvm_zzb = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzvm_zzb.ato.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzvm_zzb.ato[i4].name)) {
                                    obj2 = new zzc[(com_google_android_gms_internal_zzvm_zzb.ato.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzvm_zzb.ato, 0, obj2, 0, i4);
                                    }
                                    if (i4 < obj2.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzvm_zzb.ato, i4 + 1, obj2, i4, obj2.length - i4);
                                    }
                                    com_google_android_gms_internal_zzvm_zzb.ato = obj2;
                                } else {
                                    i4++;
                                }
                            }
                        } else {
                            z = true;
                        }
                        if (zzmx && zzbvb().zza(zzbxh(), com_google_android_gms_measurement_internal_zzx_zza.arq.zzck, false, false, true, false, false).aoh > ((long) zzbvi().zzlp(com_google_android_gms_measurement_internal_zzx_zza.arq.zzck))) {
                            zzbvg().zzbwe().log("Too many conversions. Not logging as conversion.");
                            com_google_android_gms_internal_zzvm_zzb = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3);
                            Object obj4 = null;
                            zzc com_google_android_gms_internal_zzvm_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzvm_zzcArr3 = com_google_android_gms_internal_zzvm_zzb.ato;
                            int length2 = com_google_android_gms_internal_zzvm_zzcArr3.length;
                            int i6 = 0;
                            while (i6 < length2) {
                                com_google_android_gms_internal_zzvm_zzc = com_google_android_gms_internal_zzvm_zzcArr3[i6];
                                if ("_c".equals(com_google_android_gms_internal_zzvm_zzc.name)) {
                                    obj2 = obj4;
                                } else if ("_err".equals(com_google_android_gms_internal_zzvm_zzc.name)) {
                                    zzc com_google_android_gms_internal_zzvm_zzc4 = com_google_android_gms_internal_zzvm_zzc3;
                                    int i7 = 1;
                                    com_google_android_gms_internal_zzvm_zzc = com_google_android_gms_internal_zzvm_zzc4;
                                } else {
                                    com_google_android_gms_internal_zzvm_zzc = com_google_android_gms_internal_zzvm_zzc3;
                                    obj2 = obj4;
                                }
                                i6++;
                                obj4 = obj2;
                                com_google_android_gms_internal_zzvm_zzc3 = com_google_android_gms_internal_zzvm_zzc;
                            }
                            if (obj4 != null && com_google_android_gms_internal_zzvm_zzc3 != null) {
                                com_google_android_gms_internal_zzvm_zzcArr3 = new zzc[(com_google_android_gms_internal_zzvm_zzb.ato.length - 1)];
                                i5 = 0;
                                zzc[] com_google_android_gms_internal_zzvm_zzcArr4 = com_google_android_gms_internal_zzvm_zzb.ato;
                                int length3 = com_google_android_gms_internal_zzvm_zzcArr4.length;
                                i6 = 0;
                                while (i6 < length3) {
                                    zzc com_google_android_gms_internal_zzvm_zzc5 = com_google_android_gms_internal_zzvm_zzcArr4[i6];
                                    if (com_google_android_gms_internal_zzvm_zzc5 != com_google_android_gms_internal_zzvm_zzc3) {
                                        i4 = i5 + 1;
                                        com_google_android_gms_internal_zzvm_zzcArr3[i5] = com_google_android_gms_internal_zzvm_zzc5;
                                    } else {
                                        i4 = i5;
                                    }
                                    i6++;
                                    i5 = i4;
                                }
                                com_google_android_gms_internal_zzvm_zzb.ato = com_google_android_gms_internal_zzvm_zzcArr3;
                                z3 = z;
                                i4 = i2 + 1;
                                com_google_android_gms_internal_zzvm_zze.atw[i2] = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3);
                                i = i4;
                                z2 = z3;
                            } else if (com_google_android_gms_internal_zzvm_zzc3 != null) {
                                com_google_android_gms_internal_zzvm_zzc3.name = "_err";
                                com_google_android_gms_internal_zzvm_zzc3.ats = Long.valueOf(10);
                                z3 = z;
                                i4 = i2 + 1;
                                com_google_android_gms_internal_zzvm_zze.atw[i2] = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3);
                                i = i4;
                                z2 = z3;
                            } else {
                                zzbvg().zzbwc().log("Did not find conversion parameter. Error not tracked");
                            }
                        }
                    }
                    z3 = z;
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzvm_zze.atw[i2] = (zzb) com_google_android_gms_measurement_internal_zzx_zza.zzamv.get(i3);
                    i = i4;
                    z2 = z3;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_measurement_internal_zzx_zza.zzamv.size()) {
                com_google_android_gms_internal_zzvm_zze.atw = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzvm_zze.atw, i2);
            }
            com_google_android_gms_internal_zzvm_zze.atP = zza(com_google_android_gms_measurement_internal_zzx_zza.arq.zzck, com_google_android_gms_measurement_internal_zzx_zza.arq.atx, com_google_android_gms_internal_zzvm_zze.atw);
            com_google_android_gms_internal_zzvm_zze.atz = com_google_android_gms_internal_zzvm_zze.atw[0].atp;
            com_google_android_gms_internal_zzvm_zze.atA = com_google_android_gms_internal_zzvm_zze.atw[0].atp;
            for (i = 1; i < com_google_android_gms_internal_zzvm_zze.atw.length; i++) {
                zzb com_google_android_gms_internal_zzvm_zzb2 = com_google_android_gms_internal_zzvm_zze.atw[i];
                if (com_google_android_gms_internal_zzvm_zzb2.atp.longValue() < com_google_android_gms_internal_zzvm_zze.atz.longValue()) {
                    com_google_android_gms_internal_zzvm_zze.atz = com_google_android_gms_internal_zzvm_zzb2.atp;
                }
                if (com_google_android_gms_internal_zzvm_zzb2.atp.longValue() > com_google_android_gms_internal_zzvm_zze.atA.longValue()) {
                    com_google_android_gms_internal_zzvm_zze.atA = com_google_android_gms_internal_zzvm_zzb2.atp;
                }
            }
            String str2 = com_google_android_gms_measurement_internal_zzx_zza.arq.zzck;
            zza zzlz = zzbvb().zzlz(str2);
            if (zzlz == null) {
                zzbvg().zzbwc().log("Bundling raw events w/o app info");
            } else {
                long zzbsv = zzlz.zzbsv();
                com_google_android_gms_internal_zzvm_zze.atC = zzbsv != 0 ? Long.valueOf(zzbsv) : null;
                long zzbsu = zzlz.zzbsu();
                if (zzbsu != 0) {
                    zzbsv = zzbsu;
                }
                com_google_android_gms_internal_zzvm_zze.atB = zzbsv != 0 ? Long.valueOf(zzbsv) : null;
                zzlz.zzbte();
                com_google_android_gms_internal_zzvm_zze.atN = Integer.valueOf((int) zzlz.zzbtb());
                zzlz.zzaw(com_google_android_gms_internal_zzvm_zze.atz.longValue());
                zzlz.zzax(com_google_android_gms_internal_zzvm_zze.atA.longValue());
                zzbvb().zza(zzlz);
            }
            com_google_android_gms_internal_zzvm_zze.anU = zzbvg().zzbwk();
            zzbvb().zza(com_google_android_gms_internal_zzvm_zze, z);
            zzbvb().zzaf(com_google_android_gms_measurement_internal_zzx_zza.arr);
            zzbvb().zzmg(str2);
            zzbvb().setTransactionSuccessful();
            return true;
        } finally {
            zzbvb().endTransaction();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    @WorkerThread
    public boolean isEnabled() {
        boolean z = false;
        zzyl();
        zzaax();
        if (zzbvi().zzbuc()) {
            return false;
        }
        Boolean zzbud = zzbvi().zzbud();
        if (zzbud != null) {
            z = zzbud.booleanValue();
        } else if (!zzbvi().zzasm()) {
            z = true;
        }
        return zzbvh().zzcg(z);
    }

    @WorkerThread
    protected void start() {
        zzyl();
        if (!zzbxg() || (this.aqT.isInitialized() && !this.aqT.zzbxt())) {
            zzbvb().zzbvl();
            if (zzbvh().apQ.get() == 0) {
                zzbvh().apQ.set(zzaan().currentTimeMillis());
            }
            if (zzbwv()) {
                if (!(zzbvi().zzact() || TextUtils.isEmpty(zzbuy().zzbsr()))) {
                    String zzbwp = zzbvh().zzbwp();
                    if (zzbwp == null) {
                        zzbvh().zzmn(zzbuy().zzbsr());
                    } else if (!zzbwp.equals(zzbuy().zzbsr())) {
                        zzbvg().zzbwh().log("Rechecking which service to use due to a GMP App Id change");
                        zzbvh().zzbwr();
                        this.arb.disconnect();
                        this.arb.zzabz();
                        zzbvh().zzmn(zzbuy().zzbsr());
                    }
                }
                if (!(zzbvi().zzact() || zzbxg() || TextUtils.isEmpty(zzbuy().zzbsr()))) {
                    zzbux().zzbxw();
                }
            } else if (isEnabled()) {
                if (!zzbvc().zzew("android.permission.INTERNET")) {
                    zzbvg().zzbwc().log("App is missing INTERNET permission");
                }
                if (!zzbvc().zzew("android.permission.ACCESS_NETWORK_STATE")) {
                    zzbvg().zzbwc().log("App is missing ACCESS_NETWORK_STATE permission");
                }
                if (!zzbvi().zzact()) {
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
            zzbxm();
            return;
        }
        zzbvg().zzbwc().log("Scheduler shutting down before Scion.start() called");
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzyl();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzbvg().zzbwc().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzbvg().zzbwe().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzbvg().zzbwc().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    @WorkerThread
    void zza(AppMetadata appMetadata, long j) {
        zza zzlz = zzbvb().zzlz(appMetadata.packageName);
        if (!(zzlz == null || zzlz.zzbsr() == null || zzlz.zzbsr().equals(appMetadata.anQ))) {
            zzbvg().zzbwe().log("New GMP App Id passed in. Removing cached database data.");
            zzbvb().zzme(zzlz.zzti());
            zzlz = null;
        }
        if (zzlz != null && zzlz.zzyt() != null && !zzlz.zzyt().equals(appMetadata.afY)) {
            Bundle bundle = new Bundle();
            bundle.putString("_pv", zzlz.zzyt());
            zzb(new EventParcel("_au", new EventParams(bundle), "auto", j), appMetadata);
        }
    }

    void zza(zzh com_google_android_gms_measurement_internal_zzh, AppMetadata appMetadata) {
        zzyl();
        zzaax();
        zzac.zzy(com_google_android_gms_measurement_internal_zzh);
        zzac.zzy(appMetadata);
        zzac.zzhz(com_google_android_gms_measurement_internal_zzh.zzcpe);
        zzac.zzbs(com_google_android_gms_measurement_internal_zzh.zzcpe.equals(appMetadata.packageName));
        zzvm.zze com_google_android_gms_internal_zzvm_zze = new zzvm.zze();
        com_google_android_gms_internal_zzvm_zze.atv = Integer.valueOf(1);
        com_google_android_gms_internal_zzvm_zze.atD = "android";
        com_google_android_gms_internal_zzvm_zze.zzck = appMetadata.packageName;
        com_google_android_gms_internal_zzvm_zze.anR = appMetadata.anR;
        com_google_android_gms_internal_zzvm_zze.afY = appMetadata.afY;
        com_google_android_gms_internal_zzvm_zze.atQ = Integer.valueOf((int) appMetadata.anX);
        com_google_android_gms_internal_zzvm_zze.atH = Long.valueOf(appMetadata.anS);
        com_google_android_gms_internal_zzvm_zze.anQ = appMetadata.anQ;
        com_google_android_gms_internal_zzvm_zze.atM = appMetadata.anT == 0 ? null : Long.valueOf(appMetadata.anT);
        Pair zzml = zzbvh().zzml(appMetadata.packageName);
        if (zzml != null && !TextUtils.isEmpty((CharSequence) zzml.first)) {
            com_google_android_gms_internal_zzvm_zze.atJ = (String) zzml.first;
            com_google_android_gms_internal_zzvm_zze.atK = (Boolean) zzml.second;
        } else if (!zzbuz().zzds(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzbvg().zzbwe().log("null secure ID");
                string = "null";
            } else if (string.isEmpty()) {
                zzbvg().zzbwe().log("empty secure ID");
            }
            com_google_android_gms_internal_zzvm_zze.atT = string;
        }
        com_google_android_gms_internal_zzvm_zze.atE = zzbuz().zzuj();
        com_google_android_gms_internal_zzvm_zze.zzct = zzbuz().zzbvv();
        com_google_android_gms_internal_zzvm_zze.atG = Integer.valueOf((int) zzbuz().zzbvw());
        com_google_android_gms_internal_zzvm_zze.atF = zzbuz().zzbvx();
        com_google_android_gms_internal_zzvm_zze.atI = null;
        com_google_android_gms_internal_zzvm_zze.aty = null;
        com_google_android_gms_internal_zzvm_zze.atz = null;
        com_google_android_gms_internal_zzvm_zze.atA = null;
        zza zzlz = zzbvb().zzlz(appMetadata.packageName);
        if (zzlz == null) {
            zzlz = new zza(this, appMetadata.packageName);
            zzlz.zzlj(zzbvh().zzbwm());
            zzlz.zzlm(appMetadata.anY);
            zzlz.zzlk(appMetadata.anQ);
            zzlz.zzll(zzbvh().zzmm(appMetadata.packageName));
            zzlz.zzbb(0);
            zzlz.zzaw(0);
            zzlz.zzax(0);
            zzlz.setAppVersion(appMetadata.afY);
            zzlz.zzay(appMetadata.anX);
            zzlz.zzln(appMetadata.anR);
            zzlz.zzaz(appMetadata.anS);
            zzlz.zzba(appMetadata.anT);
            zzlz.setMeasurementEnabled(appMetadata.anV);
            zzbvb().zza(zzlz);
        }
        com_google_android_gms_internal_zzvm_zze.atL = zzlz.zzayn();
        com_google_android_gms_internal_zzvm_zze.anY = zzlz.zzbst();
        List zzly = zzbvb().zzly(appMetadata.packageName);
        com_google_android_gms_internal_zzvm_zze.atx = new zzg[zzly.size()];
        for (int i = 0; i < zzly.size(); i++) {
            zzg com_google_android_gms_internal_zzvm_zzg = new zzg();
            com_google_android_gms_internal_zzvm_zze.atx[i] = com_google_android_gms_internal_zzvm_zzg;
            com_google_android_gms_internal_zzvm_zzg.name = ((zzak) zzly.get(i)).mName;
            com_google_android_gms_internal_zzvm_zzg.atX = Long.valueOf(((zzak) zzly.get(i)).asy);
            zzbvc().zza(com_google_android_gms_internal_zzvm_zzg, ((zzak) zzly.get(i)).zzctv);
        }
        try {
            zzbvb().zza(com_google_android_gms_measurement_internal_zzh, zzbvb().zza(com_google_android_gms_internal_zzvm_zze), zza(com_google_android_gms_measurement_internal_zzh));
        } catch (IOException e) {
            zzbvg().zzbwc().zzj("Data loss. Failed to insert raw event metadata", e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzyl();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzbvg().zzbwc().log("Bad chanel to read from");
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
            zzbvg().zzbwc().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzbvg().zzbwc().zzj("Failed to write to channel", e);
            return false;
        }
    }

    @WorkerThread
    public byte[] zza(@NonNull EventParcel eventParcel, @Size(min = 1) String str) {
        zzaax();
        zzyl();
        zzbxi();
        zzac.zzy(eventParcel);
        zzac.zzhz(str);
        zzd com_google_android_gms_internal_zzvm_zzd = new zzd();
        zzbvb().beginTransaction();
        try {
            zza zzlz = zzbvb().zzlz(str);
            byte[] bArr;
            if (zzlz == null) {
                zzbvg().zzbwi().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzlz.zzbta()) {
                long j;
                zzvm.zze com_google_android_gms_internal_zzvm_zze = new zzvm.zze();
                com_google_android_gms_internal_zzvm_zzd.att = new zzvm.zze[]{com_google_android_gms_internal_zzvm_zze};
                com_google_android_gms_internal_zzvm_zze.atv = Integer.valueOf(1);
                com_google_android_gms_internal_zzvm_zze.atD = "android";
                com_google_android_gms_internal_zzvm_zze.zzck = zzlz.zzti();
                com_google_android_gms_internal_zzvm_zze.anR = zzlz.zzbsx();
                com_google_android_gms_internal_zzvm_zze.afY = zzlz.zzyt();
                com_google_android_gms_internal_zzvm_zze.atQ = Integer.valueOf((int) zzlz.zzbsw());
                com_google_android_gms_internal_zzvm_zze.atH = Long.valueOf(zzlz.zzbsy());
                com_google_android_gms_internal_zzvm_zze.anQ = zzlz.zzbsr();
                com_google_android_gms_internal_zzvm_zze.atM = Long.valueOf(zzlz.zzbsz());
                Pair zzml = zzbvh().zzml(zzlz.zzti());
                if (!(zzml == null || TextUtils.isEmpty((CharSequence) zzml.first))) {
                    com_google_android_gms_internal_zzvm_zze.atJ = (String) zzml.first;
                    com_google_android_gms_internal_zzvm_zze.atK = (Boolean) zzml.second;
                }
                com_google_android_gms_internal_zzvm_zze.atE = zzbuz().zzuj();
                com_google_android_gms_internal_zzvm_zze.zzct = zzbuz().zzbvv();
                com_google_android_gms_internal_zzvm_zze.atG = Integer.valueOf((int) zzbuz().zzbvw());
                com_google_android_gms_internal_zzvm_zze.atF = zzbuz().zzbvx();
                com_google_android_gms_internal_zzvm_zze.atL = zzlz.zzayn();
                com_google_android_gms_internal_zzvm_zze.anY = zzlz.zzbst();
                List zzly = zzbvb().zzly(zzlz.zzti());
                com_google_android_gms_internal_zzvm_zze.atx = new zzg[zzly.size()];
                for (int i = 0; i < zzly.size(); i++) {
                    zzg com_google_android_gms_internal_zzvm_zzg = new zzg();
                    com_google_android_gms_internal_zzvm_zze.atx[i] = com_google_android_gms_internal_zzvm_zzg;
                    com_google_android_gms_internal_zzvm_zzg.name = ((zzak) zzly.get(i)).mName;
                    com_google_android_gms_internal_zzvm_zzg.atX = Long.valueOf(((zzak) zzly.get(i)).asy);
                    zzbvc().zza(com_google_android_gms_internal_zzvm_zzg, ((zzak) zzly.get(i)).zzctv);
                }
                Bundle zzbvz = eventParcel.aoz.zzbvz();
                if ("_iap".equals(eventParcel.name)) {
                    zzbvz.putLong("_c", 1);
                    zzbvg().zzbwi().log("Marking in-app purchase as real-time");
                    zzbvz.putLong("_r", 1);
                }
                zzbvz.putString("_o", eventParcel.aoA);
                if (zzbvc().zzni(com_google_android_gms_internal_zzvm_zze.zzck)) {
                    zzbvc().zza(zzbvz, "_dbg", Long.valueOf(1));
                    zzbvc().zza(zzbvz, "_r", Long.valueOf(1));
                }
                zzi zzaq = zzbvb().zzaq(str, eventParcel.name);
                if (zzaq == null) {
                    zzbvb().zza(new zzi(str, eventParcel.name, 1, 0, eventParcel.aoB));
                    j = 0;
                } else {
                    j = zzaq.aov;
                    zzbvb().zza(zzaq.zzbm(eventParcel.aoB).zzbvy());
                }
                zzh com_google_android_gms_measurement_internal_zzh = new zzh(this, eventParcel.aoA, str, eventParcel.name, eventParcel.aoB, j, zzbvz);
                zzb com_google_android_gms_internal_zzvm_zzb = new zzb();
                com_google_android_gms_internal_zzvm_zze.atw = new zzb[]{com_google_android_gms_internal_zzvm_zzb};
                com_google_android_gms_internal_zzvm_zzb.atp = Long.valueOf(com_google_android_gms_measurement_internal_zzh.tr);
                com_google_android_gms_internal_zzvm_zzb.name = com_google_android_gms_measurement_internal_zzh.mName;
                com_google_android_gms_internal_zzvm_zzb.atq = Long.valueOf(com_google_android_gms_measurement_internal_zzh.aor);
                com_google_android_gms_internal_zzvm_zzb.ato = new zzc[com_google_android_gms_measurement_internal_zzh.aos.size()];
                Iterator it = com_google_android_gms_measurement_internal_zzh.aos.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzvm_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzvm_zzb.ato[i2] = com_google_android_gms_internal_zzvm_zzc;
                    com_google_android_gms_internal_zzvm_zzc.name = str2;
                    zzbvc().zza(com_google_android_gms_internal_zzvm_zzc, com_google_android_gms_measurement_internal_zzh.aos.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzvm_zze.atP = zza(zzlz.zzti(), com_google_android_gms_internal_zzvm_zze.atx, com_google_android_gms_internal_zzvm_zze.atw);
                com_google_android_gms_internal_zzvm_zze.atz = com_google_android_gms_internal_zzvm_zzb.atp;
                com_google_android_gms_internal_zzvm_zze.atA = com_google_android_gms_internal_zzvm_zzb.atp;
                long zzbsv = zzlz.zzbsv();
                com_google_android_gms_internal_zzvm_zze.atC = zzbsv != 0 ? Long.valueOf(zzbsv) : null;
                long zzbsu = zzlz.zzbsu();
                if (zzbsu != 0) {
                    zzbsv = zzbsu;
                }
                com_google_android_gms_internal_zzvm_zze.atB = zzbsv != 0 ? Long.valueOf(zzbsv) : null;
                zzlz.zzbte();
                com_google_android_gms_internal_zzvm_zze.atN = Integer.valueOf((int) zzlz.zzbtb());
                com_google_android_gms_internal_zzvm_zze.atI = Long.valueOf(zzbvi().zzbsy());
                com_google_android_gms_internal_zzvm_zze.aty = Long.valueOf(zzaan().currentTimeMillis());
                com_google_android_gms_internal_zzvm_zze.atO = Boolean.TRUE;
                zzlz.zzaw(com_google_android_gms_internal_zzvm_zze.atz.longValue());
                zzlz.zzax(com_google_android_gms_internal_zzvm_zze.atA.longValue());
                zzbvb().zza(zzlz);
                zzbvb().setTransactionSuccessful();
                zzbvb().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzvm_zzd.db()];
                    zzard zzbe = zzard.zzbe(bArr);
                    com_google_android_gms_internal_zzvm_zzd.zza(zzbe);
                    zzbe.cO();
                    return zzbvc().zzj(bArr);
                } catch (IOException e) {
                    zzbvg().zzbwc().zzj("Data loss. Failed to bundle and serialize", e);
                    return null;
                }
            } else {
                zzbvg().zzbwi().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzbvb().endTransaction();
                return bArr;
            }
        } finally {
            zzbvb().endTransaction();
        }
    }

    void zzaam() {
        if (zzbvi().zzact()) {
            throw new IllegalStateException("Unexpected call on package side");
        }
    }

    public zze zzaan() {
        return this.zzapy;
    }

    void zzaax() {
        if (!this.aJ) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    public void zzav(boolean z) {
        zzbxm();
    }

    @WorkerThread
    void zzb(AppMetadata appMetadata, long j) {
        zzyl();
        zzaax();
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        bundle.putLong("_uwa", 0);
        bundle.putLong("_pfo", 0);
        bundle.putLong("_sys", 0);
        bundle.putLong("_sysu", 0);
        PackageManager packageManager = getContext().getPackageManager();
        if (packageManager == null) {
            zzbvg().zzbwc().log("PackageManager is null, first open report might be inaccurate");
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = packageManager.getPackageInfo(appMetadata.packageName, 0);
            } catch (NameNotFoundException e) {
                zzbvg().zzbwc().zzj("Package info is null, first open report might be inaccurate", e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = packageManager.getApplicationInfo(appMetadata.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzbvg().zzbwc().zzj("Application info is null, first open report might be inaccurate", e2);
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
        long zzmf = zzbvb().zzmf(appMetadata.packageName);
        if (zzmf != 0) {
            bundle.putLong("_pfo", zzmf);
        }
        zzb(new EventParcel("_f", new EventParams(bundle), "auto", j), appMetadata);
    }

    @WorkerThread
    void zzb(EventParcel eventParcel, AppMetadata appMetadata) {
        long nanoTime = System.nanoTime();
        zzyl();
        zzaax();
        String str = appMetadata.packageName;
        zzac.zzhz(str);
        if (!TextUtils.isEmpty(appMetadata.anQ)) {
            if (!appMetadata.anV) {
                zze(appMetadata);
            } else if (zzbvd().zzax(str, eventParcel.name)) {
                zzbvg().zzbwe().zzj("Dropping blacklisted event", eventParcel.name);
                zzbvc().zza(11, "_ev", eventParcel.name, 0);
            } else {
                if (zzbvg().zzbf(2)) {
                    zzbvg().zzbwj().zzj("Logging event", eventParcel);
                }
                zzbvb().beginTransaction();
                try {
                    Bundle zzbvz = eventParcel.aoz.zzbvz();
                    zze(appMetadata);
                    if ("_iap".equals(eventParcel.name) || Event.ECOMMERCE_PURCHASE.equals(eventParcel.name)) {
                        long round;
                        Object string = zzbvz.getString(Param.CURRENCY);
                        if (Event.ECOMMERCE_PURCHASE.equals(eventParcel.name)) {
                            double d = zzbvz.getDouble(Param.VALUE) * 1000000.0d;
                            if (d == 0.0d) {
                                d = ((double) zzbvz.getLong(Param.VALUE)) * 1000000.0d;
                            }
                            if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                                zzbvg().zzbwe().zzj("Data lost. Currency value is too big", Double.valueOf(d));
                                zzbvb().setTransactionSuccessful();
                                zzbvb().endTransaction();
                                return;
                            }
                            round = Math.round(d);
                        } else {
                            round = zzbvz.getLong(Param.VALUE);
                        }
                        if (!TextUtils.isEmpty(string)) {
                            String toUpperCase = string.toUpperCase(Locale.US);
                            if (toUpperCase.matches("[A-Z]{3}")) {
                                zzak com_google_android_gms_measurement_internal_zzak;
                                String valueOf = String.valueOf("_ltv_");
                                toUpperCase = String.valueOf(toUpperCase);
                                String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                                zzak zzas = zzbvb().zzas(str, concat);
                                if (zzas == null || !(zzas.zzctv instanceof Long)) {
                                    zzbvb().zzz(str, zzbvi().zzls(str) - 1);
                                    com_google_android_gms_measurement_internal_zzak = new zzak(str, concat, zzaan().currentTimeMillis(), Long.valueOf(round));
                                } else {
                                    com_google_android_gms_measurement_internal_zzak = new zzak(str, concat, zzaan().currentTimeMillis(), Long.valueOf(round + ((Long) zzas.zzctv).longValue()));
                                }
                                if (!zzbvb().zza(com_google_android_gms_measurement_internal_zzak)) {
                                    zzbvg().zzbwc().zze("Too many unique user properties are set. Ignoring user property.", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzctv);
                                    zzbvc().zza(9, null, null, 0);
                                }
                            }
                        }
                    }
                    boolean zzmx = zzal.zzmx(eventParcel.name);
                    boolean equals = "_err".equals(eventParcel.name);
                    com.google.android.gms.measurement.internal.zze.zza zza = zzbvb().zza(zzbxh(), str, true, zzmx, false, equals, false);
                    long zzbtv = zza.aog - zzbvi().zzbtv();
                    if (zzbtv > 0) {
                        if (zzbtv % 1000 == 1) {
                            zzbvg().zzbwc().zzj("Data loss. Too many events logged. count", Long.valueOf(zza.aog));
                        }
                        zzbvc().zza(16, "_ev", eventParcel.name, 0);
                        zzbvb().setTransactionSuccessful();
                        return;
                    }
                    zzi zzbm;
                    if (zzmx) {
                        zzbtv = zza.aof - zzbvi().zzbtw();
                        if (zzbtv > 0) {
                            if (zzbtv % 1000 == 1) {
                                zzbvg().zzbwc().zzj("Data loss. Too many public events logged. count", Long.valueOf(zza.aof));
                            }
                            zzbvc().zza(16, "_ev", eventParcel.name, 0);
                            zzbvb().setTransactionSuccessful();
                            zzbvb().endTransaction();
                            return;
                        }
                    }
                    if (equals) {
                        zzbtv = zza.aoi - ((long) zzbvi().zzlo(appMetadata.packageName));
                        if (zzbtv > 0) {
                            if (zzbtv == 1) {
                                zzbvg().zzbwc().zzj("Too many error events logged. count", Long.valueOf(zza.aoi));
                            }
                            zzbvb().setTransactionSuccessful();
                            zzbvb().endTransaction();
                            return;
                        }
                    }
                    zzbvc().zza(zzbvz, "_o", eventParcel.aoA);
                    if (zzbvc().zzni(str)) {
                        zzbvc().zza(zzbvz, "_dbg", Long.valueOf(1));
                        zzbvc().zza(zzbvz, "_r", Long.valueOf(1));
                    }
                    long zzma = zzbvb().zzma(str);
                    if (zzma > 0) {
                        zzbvg().zzbwe().zzj("Data lost. Too many events stored on disk, deleted", Long.valueOf(zzma));
                    }
                    zzh com_google_android_gms_measurement_internal_zzh = new zzh(this, eventParcel.aoA, str, eventParcel.name, eventParcel.aoB, 0, zzbvz);
                    zzi zzaq = zzbvb().zzaq(str, com_google_android_gms_measurement_internal_zzh.mName);
                    if (zzaq != null) {
                        com_google_android_gms_measurement_internal_zzh = com_google_android_gms_measurement_internal_zzh.zza(this, zzaq.aov);
                        zzbm = zzaq.zzbm(com_google_android_gms_measurement_internal_zzh.tr);
                    } else if (zzbvb().zzmh(str) >= ((long) zzbvi().zzbtu())) {
                        zzbvg().zzbwc().zze("Too many event names used, ignoring event. name, supported count", com_google_android_gms_measurement_internal_zzh.mName, Integer.valueOf(zzbvi().zzbtu()));
                        zzbvc().zza(8, null, null, 0);
                        zzbvb().endTransaction();
                        return;
                    } else {
                        zzbm = new zzi(str, com_google_android_gms_measurement_internal_zzh.mName, 0, 0, com_google_android_gms_measurement_internal_zzh.tr);
                    }
                    zzbvb().zza(zzbm);
                    zza(com_google_android_gms_measurement_internal_zzh, appMetadata);
                    zzbvb().setTransactionSuccessful();
                    if (zzbvg().zzbf(2)) {
                        zzbvg().zzbwj().zzj("Event recorded", com_google_android_gms_measurement_internal_zzh);
                    }
                    zzbvb().endTransaction();
                    zzbxm();
                    zzbvg().zzbwj().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
                } finally {
                    zzbvb().endTransaction();
                }
            }
        }
    }

    @WorkerThread
    void zzb(EventParcel eventParcel, String str) {
        zza zzlz = zzbvb().zzlz(str);
        if (zzlz == null || TextUtils.isEmpty(zzlz.zzyt())) {
            zzbvg().zzbwi().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = getContext().getPackageManager().getPackageInfo(str, 0).versionName;
            if (!(zzlz.zzyt() == null || zzlz.zzyt().equals(str2))) {
                zzbvg().zzbwe().zzj("App version does not match; dropping event", str);
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(eventParcel.name)) {
                zzbvg().zzbwe().zzj("Could not find package", str);
            }
        }
        EventParcel eventParcel2 = eventParcel;
        zzb(eventParcel2, new AppMetadata(str, zzlz.zzbsr(), zzlz.zzyt(), zzlz.zzbsw(), zzlz.zzbsx(), zzlz.zzbsy(), zzlz.zzbsz(), null, zzlz.zzbta(), false, zzlz.zzbst()));
    }

    @WorkerThread
    void zzb(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) {
        int i = 0;
        zzyl();
        zzaax();
        if (!TextUtils.isEmpty(appMetadata.anQ)) {
            if (appMetadata.anV) {
                int zznb = zzbvc().zznb(userAttributeParcel.name);
                String zza;
                if (zznb != 0) {
                    zza = zzbvc().zza(userAttributeParcel.name, zzbvi().zzbto(), true);
                    if (userAttributeParcel.name != null) {
                        i = userAttributeParcel.name.length();
                    }
                    zzbvc().zza(zznb, "_ev", zza, i);
                    return;
                }
                zznb = zzbvc().zzm(userAttributeParcel.name, userAttributeParcel.getValue());
                if (zznb != 0) {
                    zza = zzbvc().zza(userAttributeParcel.name, zzbvi().zzbto(), true);
                    Object value = userAttributeParcel.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzbvc().zza(zznb, "_ev", zza, i);
                    return;
                }
                Object zzn = zzbvc().zzn(userAttributeParcel.name, userAttributeParcel.getValue());
                if (zzn != null) {
                    zzak com_google_android_gms_measurement_internal_zzak = new zzak(appMetadata.packageName, userAttributeParcel.name, userAttributeParcel.asu, zzn);
                    zzbvg().zzbwi().zze("Setting user property", com_google_android_gms_measurement_internal_zzak.mName, zzn);
                    zzbvb().beginTransaction();
                    try {
                        zze(appMetadata);
                        boolean zza2 = zzbvb().zza(com_google_android_gms_measurement_internal_zzak);
                        zzbvb().setTransactionSuccessful();
                        if (zza2) {
                            zzbvg().zzbwi().zze("User property set", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzctv);
                        } else {
                            zzbvg().zzbwc().zze("Too many unique user properties are set. Ignoring user property.", com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.zzctv);
                            zzbvc().zza(9, null, null, 0);
                        }
                        zzbvb().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzbvb().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zze(appMetadata);
        }
    }

    void zzb(zzaa com_google_android_gms_measurement_internal_zzaa) {
        this.arn++;
    }

    @WorkerThread
    void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        int i2 = 0;
        zzyl();
        zzaax();
        zzac.zzhz(str);
        if (bArr == null) {
            bArr = new byte[0];
        }
        zzbvb().beginTransaction();
        try {
            zza zzlz = zzbvb().zzlz(str);
            int i3 = ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null) ? 1 : 0;
            if (zzlz == null) {
                zzbvg().zzbwe().zzj("App does not exist in onConfigFetched", str);
            } else if (i3 != 0 || i == 404) {
                List list = map != null ? (List) map.get("Last-Modified") : null;
                String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
                if (i == 404 || i == 304) {
                    if (zzbvd().zzmp(str) == null && !zzbvd().zzb(str, null, null)) {
                        zzbvb().endTransaction();
                        return;
                    }
                } else if (!zzbvd().zzb(str, bArr, str2)) {
                    zzbvb().endTransaction();
                    return;
                }
                zzlz.zzbc(zzaan().currentTimeMillis());
                zzbvb().zza(zzlz);
                if (i == 404) {
                    zzbvg().zzbwe().log("Config not found. Using empty config");
                } else {
                    zzbvg().zzbwj().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzbxa().zzafa() && zzbxl()) {
                    zzbxk();
                } else {
                    zzbxm();
                }
            } else {
                zzlz.zzbd(zzaan().currentTimeMillis());
                zzbvb().zza(zzlz);
                zzbvg().zzbwj().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzbvd().zzmr(str);
                zzbvh().apR.set(zzaan().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzbvh().apS.set(zzaan().currentTimeMillis());
                }
                zzbxm();
            }
            zzbvb().setTransactionSuccessful();
        } finally {
            zzbvb().endTransaction();
        }
    }

    boolean zzbo(long j) {
        return zzj(null, j);
    }

    public zzc zzbuw() {
        zza(this.arh);
        return this.arh;
    }

    public zzac zzbux() {
        zza(this.ard);
        return this.ard;
    }

    public zzn zzbuy() {
        zza(this.are);
        return this.are;
    }

    public zzg zzbuz() {
        zza(this.arc);
        return this.arc;
    }

    public zzad zzbva() {
        zza(this.arb);
        return this.arb;
    }

    public zze zzbvb() {
        zza(this.aqZ);
        return this.aqZ;
    }

    public zzal zzbvc() {
        zza(this.aqY);
        return this.aqY;
    }

    public zzv zzbvd() {
        zza(this.aqV);
        return this.aqV;
    }

    public zzaf zzbve() {
        zza(this.aqU);
        return this.aqU;
    }

    public zzw zzbvf() {
        zza(this.aqT);
        return this.aqT;
    }

    public zzp zzbvg() {
        zza(this.aqS);
        return this.aqS;
    }

    public zzt zzbvh() {
        zza(this.aqR);
        return this.aqR;
    }

    public zzd zzbvi() {
        return this.aqQ;
    }

    @WorkerThread
    protected boolean zzbwv() {
        boolean z = true;
        zzaax();
        zzyl();
        if (this.arj == null) {
            if (zzbvi().zzact()) {
                this.arj = Boolean.valueOf(true);
                return true;
            }
            if (!(zzbvc().zzew("android.permission.INTERNET") && zzbvc().zzew("android.permission.ACCESS_NETWORK_STATE") && zzu.zzh(getContext(), false) && zzae.zzi(getContext(), false))) {
                z = false;
            }
            this.arj = Boolean.valueOf(z);
            if (this.arj.booleanValue()) {
                this.arj = Boolean.valueOf(zzbvc().zzne(zzbuy().zzbsr()));
            }
        }
        return this.arj.booleanValue();
    }

    public zzp zzbww() {
        return (this.aqS == null || !this.aqS.isInitialized()) ? null : this.aqS;
    }

    zzw zzbwx() {
        return this.aqT;
    }

    public AppMeasurement zzbwy() {
        return this.aqW;
    }

    public FirebaseAnalytics zzbwz() {
        return this.aqX;
    }

    public zzq zzbxa() {
        zza(this.ara);
        return this.ara;
    }

    public zzr zzbxb() {
        if (this.arf != null) {
            return this.arf;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public zzai zzbxc() {
        zza(this.arg);
        return this.arg;
    }

    FileChannel zzbxd() {
        return this.arl;
    }

    @WorkerThread
    void zzbxe() {
        zzyl();
        zzaax();
        if (zzbxq() && zzbxf()) {
            zzu(zza(zzbxd()), zzbuy().zzbwa());
        }
    }

    @WorkerThread
    boolean zzbxf() {
        zzyl();
        try {
            this.arl = new RandomAccessFile(new File(getContext().getFilesDir(), this.aqZ.zzabs()), "rw").getChannel();
            this.ark = this.arl.tryLock();
            if (this.ark != null) {
                zzbvg().zzbwj().log("Storage concurrent access okay");
                return true;
            }
            zzbvg().zzbwc().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzbvg().zzbwc().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Failed to access storage lock file", e2);
        }
    }

    public boolean zzbxg() {
        return false;
    }

    long zzbxh() {
        return ((((zzaan().currentTimeMillis() + zzbvh().zzbwn()) / 1000) / 60) / 60) / 24;
    }

    void zzbxi() {
        if (!zzbvi().zzact()) {
            throw new IllegalStateException("Unexpected call on client side");
        }
    }

    @WorkerThread
    public void zzbxk() {
        Map map = null;
        int i = 0;
        zzyl();
        zzaax();
        if (!zzbvi().zzact()) {
            Boolean zzbwq = zzbvh().zzbwq();
            if (zzbwq == null) {
                zzbvg().zzbwe().log("Upload data called on the client side before use of service was decided");
                return;
            } else if (zzbwq.booleanValue()) {
                zzbvg().zzbwc().log("Upload called in the client side when service should be used");
                return;
            }
        }
        if (zzbxj()) {
            zzbvg().zzbwe().log("Uploading requested multiple times");
        } else if (zzbxa().zzafa()) {
            long currentTimeMillis = zzaan().currentTimeMillis();
            zzbo(currentTimeMillis - zzbvi().zzbuk());
            long j = zzbvh().apQ.get();
            if (j != 0) {
                zzbvg().zzbwi().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzbvj = zzbvb().zzbvj();
            if (TextUtils.isEmpty(zzbvj)) {
                String zzbl = zzbvb().zzbl(currentTimeMillis - zzbvi().zzbuk());
                if (!TextUtils.isEmpty(zzbl)) {
                    zza zzlz = zzbvb().zzlz(zzbl);
                    if (zzlz != null) {
                        String zzap = zzbvi().zzap(zzlz.zzbsr(), zzlz.zzayn());
                        try {
                            URL url = new URL(zzap);
                            zzbvg().zzbwj().zzj("Fetching remote configuration", zzlz.zzti());
                            zzvl.zzb zzmp = zzbvd().zzmp(zzlz.zzti());
                            CharSequence zzmq = zzbvd().zzmq(zzlz.zzti());
                            if (!(zzmp == null || TextUtils.isEmpty(zzmq))) {
                                map = new ArrayMap();
                                map.put("If-Modified-Since", zzmq);
                            }
                            zzbxa().zza(zzbl, url, map, new zza(this) {
                                final /* synthetic */ zzx arp;

                                {
                                    this.arp = r1;
                                }

                                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                                    this.arp.zzb(str, i, th, bArr, map);
                                }
                            });
                            return;
                        } catch (MalformedURLException e) {
                            zzbvg().zzbwc().zzj("Failed to parse config URL. Not fetching", zzap);
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            List<Pair> zzn = zzbvb().zzn(zzbvj, zzbvi().zzlv(zzbvj), zzbvi().zzlw(zzbvj));
            if (!zzn.isEmpty()) {
                zzvm.zze com_google_android_gms_internal_zzvm_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzvm_zze = (zzvm.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzvm_zze.atJ)) {
                        obj = com_google_android_gms_internal_zzvm_zze.atJ;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzvm_zze = (zzvm.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzvm_zze.atJ) && !com_google_android_gms_internal_zzvm_zze.atJ.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzvm_zzd = new zzd();
                com_google_android_gms_internal_zzvm_zzd.att = new zzvm.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzvm_zzd.att.length) {
                    com_google_android_gms_internal_zzvm_zzd.att[i] = (zzvm.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzvm_zzd.att[i].atI = Long.valueOf(zzbvi().zzbsy());
                    com_google_android_gms_internal_zzvm_zzd.att[i].aty = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzvm_zzd.att[i].atO = Boolean.valueOf(zzbvi().zzact());
                    i++;
                }
                Object zzb = zzbvg().zzbf(2) ? zzal.zzb(com_google_android_gms_internal_zzvm_zzd) : null;
                byte[] zza = zzbvc().zza(com_google_android_gms_internal_zzvm_zzd);
                String zzbuj = zzbvi().zzbuj();
                try {
                    URL url2 = new URL(zzbuj);
                    zzag(arrayList);
                    zzbvh().apR.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzvm_zzd.att.length > 0) {
                        obj2 = com_google_android_gms_internal_zzvm_zzd.att[0].zzck;
                    }
                    zzbvg().zzbwj().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzb);
                    zzbxa().zza(zzbvj, url2, zza, null, new zza(this) {
                        final /* synthetic */ zzx arp;

                        {
                            this.arp = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.arp.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e2) {
                    zzbvg().zzbwc().zzj("Failed to parse upload URL. Not uploading", zzbuj);
                }
            }
        } else {
            zzbvg().zzbwe().log("Network not connected, ignoring upload request");
            zzbxm();
        }
    }

    void zzbxo() {
        this.aro++;
    }

    @WorkerThread
    void zzbxp() {
        zzyl();
        zzaax();
        if (!this.ari) {
            zzbvg().zzbwh().log("This instance being marked as an uploader");
            zzbxe();
        }
        this.ari = true;
    }

    @WorkerThread
    boolean zzbxq() {
        zzyl();
        zzaax();
        return this.ari || zzbxg();
    }

    void zzc(AppMetadata appMetadata) {
        zzyl();
        zzaax();
        zzac.zzhz(appMetadata.packageName);
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
        zzyl();
        zzaax();
        if (!TextUtils.isEmpty(appMetadata.anQ)) {
            if (appMetadata.anV) {
                zzbvg().zzbwi().zzj("Removing user property", userAttributeParcel.name);
                zzbvb().beginTransaction();
                try {
                    zze(appMetadata);
                    zzbvb().zzar(appMetadata.packageName, userAttributeParcel.name);
                    zzbvb().setTransactionSuccessful();
                    zzbvg().zzbwi().zzj("User property removed", userAttributeParcel.name);
                } finally {
                    zzbvb().endTransaction();
                }
            } else {
                zze(appMetadata);
            }
        }
    }

    @WorkerThread
    public void zzd(AppMetadata appMetadata) {
        zzyl();
        zzaax();
        zzac.zzy(appMetadata);
        zzac.zzhz(appMetadata.packageName);
        if (!TextUtils.isEmpty(appMetadata.anQ)) {
            if (appMetadata.anV) {
                long currentTimeMillis = zzaan().currentTimeMillis();
                zzbvb().beginTransaction();
                try {
                    zza(appMetadata, currentTimeMillis);
                    zze(appMetadata);
                    if (zzbvb().zzaq(appMetadata.packageName, "_f") == null) {
                        zzb(new UserAttributeParcel("_fot", currentTimeMillis, Long.valueOf((1 + (currentTimeMillis / 3600000)) * 3600000), "auto"), appMetadata);
                        zzb(appMetadata, currentTimeMillis);
                        zzc(appMetadata, currentTimeMillis);
                    } else if (appMetadata.anW) {
                        zzd(appMetadata, currentTimeMillis);
                    }
                    zzbvb().setTransactionSuccessful();
                } finally {
                    zzbvb().endTransaction();
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
    boolean zzu(int i, int i2) {
        zzyl();
        if (i > i2) {
            zzbvg().zzbwc().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzbxd())) {
                zzbvg().zzbwj().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzbvg().zzbwc().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }

    @WorkerThread
    public void zzyl() {
        zzbvf().zzyl();
    }
}
