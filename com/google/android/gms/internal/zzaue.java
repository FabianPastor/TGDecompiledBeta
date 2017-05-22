package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteException;
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
import com.google.android.gms.internal.zzauw.zzb;
import com.google.android.gms.internal.zzauw.zzc;
import com.google.android.gms.internal.zzauw.zzd;
import com.google.android.gms.internal.zzauw.zzg;
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

public class zzaue {
    private static volatile zzaue zzbub;
    private final Context mContext;
    private final boolean zzadP;
    private FileLock zzbuA;
    private FileChannel zzbuB;
    private List<Long> zzbuC;
    private int zzbuD;
    private int zzbuE;
    private long zzbuF = -1;
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

    private class zza implements zzb {
        final /* synthetic */ zzaue zzbuI;
        zzauw.zze zzbuJ;
        List<Long> zzbuK;
        long zzbuL;
        List<zzb> zzth;

        private zza(zzaue com_google_android_gms_internal_zzaue) {
            this.zzbuI = com_google_android_gms_internal_zzaue;
        }

        private long zza(zzb com_google_android_gms_internal_zzauw_zzb) {
            return ((com_google_android_gms_internal_zzauw_zzb.zzbxc.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zzth == null || this.zzth.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzauw_zzb) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zzb);
            if (this.zzth == null) {
                this.zzth = new ArrayList();
            }
            if (this.zzbuK == null) {
                this.zzbuK = new ArrayList();
            }
            if (this.zzth.size() > 0 && zza((zzb) this.zzth.get(0)) != zza(com_google_android_gms_internal_zzauw_zzb)) {
                return false;
            }
            long zzafB = this.zzbuL + ((long) com_google_android_gms_internal_zzauw_zzb.zzafB());
            if (zzafB >= ((long) this.zzbuI.zzKn().zzLo())) {
                return false;
            }
            this.zzbuL = zzafB;
            this.zzth.add(com_google_android_gms_internal_zzauw_zzb);
            this.zzbuK.add(Long.valueOf(j));
            return this.zzth.size() < this.zzbuI.zzKn().zzLp();
        }

        public void zzb(zzauw.zze com_google_android_gms_internal_zzauw_zze) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zze);
            this.zzbuJ = com_google_android_gms_internal_zzauw_zze;
        }
    }

    zzaue(zzaui com_google_android_gms_internal_zzaui) {
        zzac.zzw(com_google_android_gms_internal_zzaui);
        this.mContext = com_google_android_gms_internal_zzaui.mContext;
        this.zzuP = com_google_android_gms_internal_zzaui.zzn(this);
        this.zzbuH = this.zzuP.currentTimeMillis();
        this.zzbuc = com_google_android_gms_internal_zzaui.zza(this);
        zzaua zzb = com_google_android_gms_internal_zzaui.zzb(this);
        zzb.initialize();
        this.zzbud = zzb;
        zzatx zzc = com_google_android_gms_internal_zzaui.zzc(this);
        zzc.initialize();
        this.zzbue = zzc;
        zzKl().zzMd().zzj("App measurement is starting up, version", Long.valueOf(zzKn().zzKv()));
        zzKn().zzLh();
        zzKl().zzMd().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzaut zzj = com_google_android_gms_internal_zzaui.zzj(this);
        zzj.initialize();
        this.zzbuk = zzj;
        zzatl zzq = com_google_android_gms_internal_zzaui.zzq(this);
        zzq.initialize();
        this.zzbuq = zzq;
        zzatu zzr = com_google_android_gms_internal_zzaui.zzr(this);
        zzr.initialize();
        this.zzbus = zzr;
        zzKn().zzLh();
        String zzke = zzr.zzke();
        if (zzKh().zzge(zzke)) {
            zzKl().zzMd().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
        } else {
            com.google.android.gms.internal.zzatx.zza zzMd = zzKl().zzMd();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzke = String.valueOf(zzke);
            zzMd.log(zzke.length() != 0 ? str.concat(zzke) : new String(str));
        }
        zzKl().zzMe().log("Debug-level message logging enabled");
        zzatj zzk = com_google_android_gms_internal_zzaui.zzk(this);
        zzk.initialize();
        this.zzbul = zzk;
        zzatv zzl = com_google_android_gms_internal_zzaui.zzl(this);
        zzl.initialize();
        this.zzbum = zzl;
        zzatf zzu = com_google_android_gms_internal_zzaui.zzu(this);
        zzu.initialize();
        this.zzbuv = zzu;
        this.zzbuw = com_google_android_gms_internal_zzaui.zzv(this);
        zzaty zzm = com_google_android_gms_internal_zzaui.zzm(this);
        zzm.initialize();
        this.zzbun = zzm;
        zzauk zzo = com_google_android_gms_internal_zzaui.zzo(this);
        zzo.initialize();
        this.zzbuo = zzo;
        zzaul zzp = com_google_android_gms_internal_zzaui.zzp(this);
        zzp.initialize();
        this.zzbup = zzp;
        zzauj zzi = com_google_android_gms_internal_zzaui.zzi(this);
        zzi.initialize();
        this.zzbur = zzi;
        zzaup zzt = com_google_android_gms_internal_zzaui.zzt(this);
        zzt.initialize();
        this.zzbuu = zzt;
        this.zzbut = com_google_android_gms_internal_zzaui.zzs(this);
        this.zzbui = com_google_android_gms_internal_zzaui.zzh(this);
        this.zzbuj = com_google_android_gms_internal_zzaui.zzg(this);
        zzaun zze = com_google_android_gms_internal_zzaui.zze(this);
        zze.initialize();
        this.zzbug = zze;
        zzauc zzf = com_google_android_gms_internal_zzaui.zzf(this);
        zzf.initialize();
        this.zzbuh = zzf;
        zzaud zzd = com_google_android_gms_internal_zzaui.zzd(this);
        zzd.initialize();
        this.zzbuf = zzd;
        if (this.zzbuD != this.zzbuE) {
            zzKl().zzLZ().zze("Not all components initialized", Integer.valueOf(this.zzbuD), Integer.valueOf(this.zzbuE));
        }
        this.zzadP = true;
        this.zzbuc.zzLh();
        if (this.mContext.getApplicationContext() instanceof Application) {
            int i = VERSION.SDK_INT;
            zzKa().zzMS();
        } else {
            zzKl().zzMb().log("Application context is not an Application");
        }
        this.zzbuf.zzm(new Runnable(this) {
            final /* synthetic */ zzaue zzbuI;

            {
                this.zzbuI = r1;
            }

            public void run() {
                this.zzbuI.start();
            }
        });
    }

    private boolean zzMJ() {
        zzmR();
        zzob();
        return zzKg().zzLK() || !TextUtils.isEmpty(zzKg().zzLE());
    }

    @WorkerThread
    private void zzMK() {
        zzmR();
        zzob();
        if (zzMO()) {
            long abs;
            if (this.zzbuG > 0) {
                abs = 3600000 - Math.abs(zznR().elapsedRealtime() - this.zzbuG);
                if (abs > 0) {
                    zzKl().zzMf().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    zzMA().unregister();
                    zzMB().cancel();
                    return;
                }
                this.zzbuG = 0;
            }
            if (zzMu() && zzMJ()) {
                abs = zzML();
                if (abs == 0) {
                    zzMA().unregister();
                    zzMB().cancel();
                    return;
                } else if (zzMz().zzqa()) {
                    long j = zzKm().zzbtd.get();
                    long zzLt = zzKn().zzLt();
                    if (!zzKh().zzh(j, zzLt)) {
                        abs = Math.max(abs, j + zzLt);
                    }
                    zzMA().unregister();
                    abs -= zznR().currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzKn().zzLx();
                        zzKm().zzbtb.set(zznR().currentTimeMillis());
                    }
                    zzKl().zzMf().zzj("Upload scheduled in approximately ms", Long.valueOf(abs));
                    zzMB().zzy(abs);
                    return;
                } else {
                    zzMA().zzpX();
                    zzMB().cancel();
                    return;
                }
            }
            zzMA().unregister();
            zzMB().cancel();
        }
    }

    private long zzML() {
        long zzLv;
        long currentTimeMillis = zznR().currentTimeMillis();
        long zzLA = zzKn().zzLA();
        Object obj = (zzKg().zzLL() || zzKg().zzLF()) ? 1 : null;
        if (obj != null) {
            CharSequence zzLD = zzKn().zzLD();
            zzLv = (TextUtils.isEmpty(zzLD) || ".none.".equals(zzLD)) ? zzKn().zzLv() : zzKn().zzLw();
        } else {
            zzLv = zzKn().zzLu();
        }
        long j = zzKm().zzbtb.get();
        long j2 = zzKm().zzbtc.get();
        long max = Math.max(zzKg().zzLI(), zzKg().zzLJ());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzLA;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzLv;
        }
        if (!zzKh().zzh(j, zzLv)) {
            currentTimeMillis = j + zzLv;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzKn().zzLC(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzKn().zzLB();
            if (currentTimeMillis > j2) {
                return currentTimeMillis;
            }
        }
        return 0;
    }

    private void zza(zzaug com_google_android_gms_internal_zzaug) {
        if (com_google_android_gms_internal_zzaug == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private void zza(zzauh com_google_android_gms_internal_zzauh) {
        if (com_google_android_gms_internal_zzauh == null) {
            throw new IllegalStateException("Component not created");
        } else if (!com_google_android_gms_internal_zzauh.isInitialized()) {
            throw new IllegalStateException("Component not initialized");
        }
    }

    private boolean zza(zzatm com_google_android_gms_internal_zzatm) {
        if (com_google_android_gms_internal_zzatm.zzbrA == null) {
            return false;
        }
        Iterator it = com_google_android_gms_internal_zzatm.zzbrA.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzKi().zzab(com_google_android_gms_internal_zzatm.mAppId, com_google_android_gms_internal_zzatm.mName) && zzKg().zza(zzMG(), com_google_android_gms_internal_zzatm.mAppId, false, false, false, false, false).zzbrs < ((long) zzKn().zzfl(com_google_android_gms_internal_zzatm.mAppId));
    }

    private com.google.android.gms.internal.zzauw.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzauw_zzgArr, zzb[] com_google_android_gms_internal_zzauw_zzbArr) {
        zzac.zzdr(str);
        return zzJZ().zza(str, com_google_android_gms_internal_zzauw_zzbArr, com_google_android_gms_internal_zzauw_zzgArr);
    }

    public static zzaue zzbM(Context context) {
        zzac.zzw(context);
        zzac.zzw(context.getApplicationContext());
        if (zzbub == null) {
            synchronized (zzaue.class) {
                if (zzbub == null) {
                    zzbub = new zzaui(context).zzMR();
                }
            }
        }
        return zzbub;
    }

    @WorkerThread
    private void zzf(zzatd com_google_android_gms_internal_zzatd) {
        Object obj = 1;
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        String zzfH = zzKm().zzfH(com_google_android_gms_internal_zzatd.packageName);
        Object obj2 = null;
        if (zzfu == null) {
            zzatc com_google_android_gms_internal_zzatc = new zzatc(this, com_google_android_gms_internal_zzatd.packageName);
            com_google_android_gms_internal_zzatc.zzfd(zzKm().zzMi());
            com_google_android_gms_internal_zzatc.zzff(zzfH);
            zzfu = com_google_android_gms_internal_zzatc;
            obj2 = 1;
        } else if (!zzfH.equals(zzfu.zzKp())) {
            zzfu.zzff(zzfH);
            zzfu.zzfd(zzKm().zzMi());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK) || com_google_android_gms_internal_zzatd.zzbqK.equals(zzfu.getGmpAppId()))) {
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqK);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqS) || com_google_android_gms_internal_zzatd.zzbqS.equals(zzfu.zzKq()))) {
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqS);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqM == 0 || com_google_android_gms_internal_zzatd.zzbqM == zzfu.zzKv())) {
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqM);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbhN) || com_google_android_gms_internal_zzatd.zzbhN.equals(zzfu.zzmZ()))) {
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhN);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqR != zzfu.zzKt()) {
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqR);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqL == null || com_google_android_gms_internal_zzatd.zzbqL.equals(zzfu.zzKu()))) {
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqL);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqN != zzfu.zzKw()) {
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqN);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqP != zzfu.zzKx()) {
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqP);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqO) || com_google_android_gms_internal_zzatd.zzbqO.equals(zzfu.zzKI()))) {
            zzfu.zzfi(com_google_android_gms_internal_zzatd.zzbqO);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqT != zzfu.zzuW()) {
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqT);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzKg().zza(zzfu);
        }
    }

    private boolean zzl(String str, long j) {
        zzKg().beginTransaction();
        try {
            zzaue com_google_android_gms_internal_zzaue = this;
            zza com_google_android_gms_internal_zzaue_zza = new zza();
            zzKg().zza(str, j, this.zzbuF, com_google_android_gms_internal_zzaue_zza);
            if (com_google_android_gms_internal_zzaue_zza.isEmpty()) {
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzauw.zze com_google_android_gms_internal_zzauw_zze = com_google_android_gms_internal_zzaue_zza.zzbuJ;
            com_google_android_gms_internal_zzauw_zze.zzbxj = new zzb[com_google_android_gms_internal_zzaue_zza.zzth.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                boolean z2;
                Object obj;
                if (zzKi().zzaa(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                    zzKl().zzMb().zze("Dropping blacklisted raw event. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS), ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    obj = (zzKh().zzgg(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS) || zzKh().zzgh(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS)) ? 1 : null;
                    if (obj != null || "_err".equals(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                        i = i2;
                        z2 = z;
                    } else {
                        zzKh().zza(11, "_ev", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name, 0);
                        i = i2;
                        z2 = z;
                    }
                } else {
                    int i4;
                    boolean z3;
                    boolean zzab = zzKi().zzab(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    if (zzab || zzKh().zzgi(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr;
                        zzc com_google_android_gms_internal_zzauw_zzc;
                        zzb com_google_android_gms_internal_zzauw_zzb;
                        Object obj2 = null;
                        Object obj3 = null;
                        if (((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb == null) {
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr2 = ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb;
                        int length = com_google_android_gms_internal_zzauw_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            zzc com_google_android_gms_internal_zzauw_zzc2 = com_google_android_gms_internal_zzauw_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxf = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxf = Long.valueOf(1);
                                obj = 1;
                            } else {
                                obj = obj3;
                            }
                            i5++;
                            obj3 = obj;
                        }
                        if (obj2 == null && zzab) {
                            zzKl().zzMf().zzj("Marking event as conversion", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_c";
                            com_google_android_gms_internal_zzauw_zzc.zzbxf = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        if (obj3 == null) {
                            zzKl().zzMf().zzj("Marking event as real-time", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_r";
                            com_google_android_gms_internal_zzauw_zzc.zzbxf = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxb = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        boolean z4 = true;
                        if (zzKg().zza(zzMG(), com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS, false, false, false, false, true).zzbrs > ((long) zzKn().zzfl(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS))) {
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzauw_zzb.zzbxb.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzauw_zzb.zzbxb[i4].name)) {
                                    obj3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbxb.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbxb, 0, obj3, 0, i4);
                                    }
                                    if (i4 < obj3.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbxb, i4 + 1, obj3, i4, obj3.length - i4);
                                    }
                                    com_google_android_gms_internal_zzauw_zzb.zzbxb = obj3;
                                    z4 = z;
                                } else {
                                    i4++;
                                }
                            }
                            z4 = z;
                        }
                        if (zzaut.zzfT(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name) && zzab && zzKg().zza(zzMG(), com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS, false, false, true, false, false).zzbrq > ((long) zzKn().zzfk(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS))) {
                            zzKl().zzMb().zzj("Too many conversions. Not logging as conversion. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS));
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            Object obj4 = null;
                            zzc com_google_android_gms_internal_zzauw_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzauw_zzcArr3 = com_google_android_gms_internal_zzauw_zzb.zzbxb;
                            int length2 = com_google_android_gms_internal_zzauw_zzcArr3.length;
                            int i6 = 0;
                            while (i6 < length2) {
                                com_google_android_gms_internal_zzauw_zzc = com_google_android_gms_internal_zzauw_zzcArr3[i6];
                                if ("_c".equals(com_google_android_gms_internal_zzauw_zzc.name)) {
                                    obj3 = obj4;
                                } else if ("_err".equals(com_google_android_gms_internal_zzauw_zzc.name)) {
                                    zzc com_google_android_gms_internal_zzauw_zzc4 = com_google_android_gms_internal_zzauw_zzc3;
                                    int i7 = 1;
                                    com_google_android_gms_internal_zzauw_zzc = com_google_android_gms_internal_zzauw_zzc4;
                                } else {
                                    com_google_android_gms_internal_zzauw_zzc = com_google_android_gms_internal_zzauw_zzc3;
                                    obj3 = obj4;
                                }
                                i6++;
                                obj4 = obj3;
                                com_google_android_gms_internal_zzauw_zzc3 = com_google_android_gms_internal_zzauw_zzc;
                            }
                            if (obj4 != null && com_google_android_gms_internal_zzauw_zzc3 != null) {
                                com_google_android_gms_internal_zzauw_zzcArr3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbxb.length - 1)];
                                int i8 = 0;
                                zzc[] com_google_android_gms_internal_zzauw_zzcArr4 = com_google_android_gms_internal_zzauw_zzb.zzbxb;
                                int length3 = com_google_android_gms_internal_zzauw_zzcArr4.length;
                                i6 = 0;
                                while (i6 < length3) {
                                    zzc com_google_android_gms_internal_zzauw_zzc5 = com_google_android_gms_internal_zzauw_zzcArr4[i6];
                                    if (com_google_android_gms_internal_zzauw_zzc5 != com_google_android_gms_internal_zzauw_zzc3) {
                                        i4 = i8 + 1;
                                        com_google_android_gms_internal_zzauw_zzcArr3[i8] = com_google_android_gms_internal_zzauw_zzc5;
                                    } else {
                                        i4 = i8;
                                    }
                                    i6++;
                                    i8 = i4;
                                }
                                com_google_android_gms_internal_zzauw_zzb.zzbxb = com_google_android_gms_internal_zzauw_zzcArr3;
                                z3 = z4;
                            } else if (com_google_android_gms_internal_zzauw_zzc3 != null) {
                                com_google_android_gms_internal_zzauw_zzc3.name = "_err";
                                com_google_android_gms_internal_zzauw_zzc3.zzbxf = Long.valueOf(10);
                                z3 = z4;
                            } else {
                                zzKl().zzLZ().zzj("Did not find conversion parameter. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS));
                            }
                        }
                        z3 = z4;
                    } else {
                        z3 = z;
                    }
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zze.zzbxj[i2] = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                    i = i4;
                    z2 = z3;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                com_google_android_gms_internal_zzauw_zze.zzbxj = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzauw_zze.zzbxj, i2);
            }
            com_google_android_gms_internal_zzauw_zze.zzbxC = zza(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS, com_google_android_gms_internal_zzaue_zza.zzbuJ.zzbxk, com_google_android_gms_internal_zzauw_zze.zzbxj);
            com_google_android_gms_internal_zzauw_zze.zzbxm = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzauw_zze.zzbxn = Long.valueOf(Long.MIN_VALUE);
            for (zzb com_google_android_gms_internal_zzauw_zzb2 : com_google_android_gms_internal_zzauw_zze.zzbxj) {
                if (com_google_android_gms_internal_zzauw_zzb2.zzbxc.longValue() < com_google_android_gms_internal_zzauw_zze.zzbxm.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxm = com_google_android_gms_internal_zzauw_zzb2.zzbxc;
                }
                if (com_google_android_gms_internal_zzauw_zzb2.zzbxc.longValue() > com_google_android_gms_internal_zzauw_zze.zzbxn.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxn = com_google_android_gms_internal_zzauw_zzb2.zzbxc;
                }
            }
            String str2 = com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS;
            zzatc zzfu = zzKg().zzfu(str2);
            if (zzfu == null) {
                zzKl().zzLZ().zzj("Bundling raw events w/o app info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS));
            } else if (com_google_android_gms_internal_zzauw_zze.zzbxj.length > 0) {
                long zzKs = zzfu.zzKs();
                com_google_android_gms_internal_zzauw_zze.zzbxp = zzKs != 0 ? Long.valueOf(zzKs) : null;
                long zzKr = zzfu.zzKr();
                if (zzKr != 0) {
                    zzKs = zzKr;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxo = zzKs != 0 ? Long.valueOf(zzKs) : null;
                zzfu.zzKB();
                com_google_android_gms_internal_zzauw_zze.zzbxA = Integer.valueOf((int) zzfu.zzKy());
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxm.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxn.longValue());
                com_google_android_gms_internal_zzauw_zze.zzbqO = zzfu.zzKJ();
                zzKg().zza(zzfu);
            }
            if (com_google_android_gms_internal_zzauw_zze.zzbxj.length > 0) {
                zzKn().zzLh();
                zzauv.zzb zzfL = zzKi().zzfL(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS);
                if (zzfL != null && zzfL.zzbwQ != null) {
                    com_google_android_gms_internal_zzauw_zze.zzbxH = zzfL.zzbwQ;
                } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzbqK)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxH = Long.valueOf(-1);
                } else {
                    zzKl().zzMb().zzj("Did not find measurement config or missing version info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuJ.zzaS));
                }
                zzKg().zza(com_google_android_gms_internal_zzauw_zze, z);
            }
            zzKg().zzJ(com_google_android_gms_internal_zzaue_zza.zzbuK);
            zzKg().zzfB(str2);
            zzKg().setTransactionSuccessful();
            boolean z5 = com_google_android_gms_internal_zzauw_zze.zzbxj.length > 0;
            zzKg().endTransaction();
            return z5;
        } catch (Throwable th) {
            zzKg().endTransaction();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    @WorkerThread
    public boolean isEnabled() {
        boolean z = false;
        zzmR();
        zzob();
        if (zzKn().zzLi()) {
            return false;
        }
        Boolean zzLj = zzKn().zzLj();
        if (zzLj != null) {
            z = zzLj.booleanValue();
        } else if (!zzKn().zzwR()) {
            z = true;
        }
        return zzKm().zzaK(z);
    }

    @WorkerThread
    protected void start() {
        zzmR();
        zzKg().zzLG();
        if (zzKm().zzbtb.get() == 0) {
            zzKm().zzbtb.set(zznR().currentTimeMillis());
        }
        if (zzMu()) {
            zzKn().zzLh();
            if (!TextUtils.isEmpty(zzKb().getGmpAppId())) {
                String zzMl = zzKm().zzMl();
                if (zzMl == null) {
                    zzKm().zzfI(zzKb().getGmpAppId());
                } else if (!zzMl.equals(zzKb().getGmpAppId())) {
                    zzKl().zzMd().log("Rechecking which service to use due to a GMP App Id change");
                    zzKm().zzMo();
                    this.zzbup.disconnect();
                    this.zzbup.zzoD();
                    zzKm().zzfI(zzKb().getGmpAppId());
                }
            }
            zzKn().zzLh();
            if (!TextUtils.isEmpty(zzKb().getGmpAppId())) {
                zzKa().zzMT();
            }
        } else if (isEnabled()) {
            if (!zzKh().zzbW("android.permission.INTERNET")) {
                zzKl().zzLZ().log("App is missing INTERNET permission");
            }
            if (!zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE")) {
                zzKl().zzLZ().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzKn().zzLh();
            if (!zzadg.zzbi(getContext()).zzzx()) {
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
    }

    void zzJV() {
        zzKn().zzLh();
        throw new IllegalStateException("Unexpected call on client side");
    }

    void zzJW() {
        zzKn().zzLh();
    }

    public zzatb zzJY() {
        zza(this.zzbuw);
        return this.zzbuw;
    }

    public zzatf zzJZ() {
        zza(this.zzbuv);
        return this.zzbuv;
    }

    protected void zzK(List<Long> list) {
        zzac.zzaw(!list.isEmpty());
        if (this.zzbuC != null) {
            zzKl().zzLZ().log("Set uploading progress before finishing the previous upload");
        } else {
            this.zzbuC = new ArrayList(list);
        }
    }

    public zzauj zzKa() {
        zza(this.zzbur);
        return this.zzbur;
    }

    public zzatu zzKb() {
        zza(this.zzbus);
        return this.zzbus;
    }

    public zzatl zzKc() {
        zza(this.zzbuq);
        return this.zzbuq;
    }

    public zzaul zzKd() {
        zza(this.zzbup);
        return this.zzbup;
    }

    public zzauk zzKe() {
        zza(this.zzbuo);
        return this.zzbuo;
    }

    public zzatv zzKf() {
        zza(this.zzbum);
        return this.zzbum;
    }

    public zzatj zzKg() {
        zza(this.zzbul);
        return this.zzbul;
    }

    public zzaut zzKh() {
        zza(this.zzbuk);
        return this.zzbuk;
    }

    public zzauc zzKi() {
        zza(this.zzbuh);
        return this.zzbuh;
    }

    public zzaun zzKj() {
        zza(this.zzbug);
        return this.zzbug;
    }

    public zzaud zzKk() {
        zza(this.zzbuf);
        return this.zzbuf;
    }

    public zzatx zzKl() {
        zza(this.zzbue);
        return this.zzbue;
    }

    public zzaua zzKm() {
        zza(this.zzbud);
        return this.zzbud;
    }

    public zzati zzKn() {
        return this.zzbuc;
    }

    public zzatz zzMA() {
        if (this.zzbut != null) {
            return this.zzbut;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public zzaup zzMB() {
        zza(this.zzbuu);
        return this.zzbuu;
    }

    FileChannel zzMC() {
        return this.zzbuB;
    }

    @WorkerThread
    void zzMD() {
        zzmR();
        zzob();
        if (zzMO() && zzME()) {
            zzy(zza(zzMC()), zzKb().zzLX());
        }
    }

    @WorkerThread
    boolean zzME() {
        zzmR();
        try {
            this.zzbuB = new RandomAccessFile(new File(getContext().getFilesDir(), this.zzbul.zzow()), "rw").getChannel();
            this.zzbuA = this.zzbuB.tryLock();
            if (this.zzbuA != null) {
                zzKl().zzMf().log("Storage concurrent access okay");
                return true;
            }
            zzKl().zzLZ().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzKl().zzLZ().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzKl().zzLZ().zzj("Failed to access storage lock file", e2);
        }
    }

    long zzMF() {
        return this.zzbuH;
    }

    long zzMG() {
        return ((((zznR().currentTimeMillis() + zzKm().zzMj()) / 1000) / 60) / 60) / 24;
    }

    @WorkerThread
    protected boolean zzMH() {
        zzmR();
        return this.zzbuC != null;
    }

    @WorkerThread
    public void zzMI() {
        int i = 0;
        zzmR();
        zzob();
        zzKn().zzLh();
        Boolean zzMn = zzKm().zzMn();
        if (zzMn == null) {
            zzKl().zzMb().log("Upload data called on the client side before use of service was decided");
        } else if (zzMn.booleanValue()) {
            zzKl().zzLZ().log("Upload called in the client side when service should be used");
        } else if (this.zzbuG > 0) {
            zzMK();
        } else if (zzMH()) {
            zzKl().zzMb().log("Uploading requested multiple times");
        } else if (zzMz().zzqa()) {
            long currentTimeMillis = zznR().currentTimeMillis();
            zzaq(currentTimeMillis - zzKn().zzLs());
            long j = zzKm().zzbtb.get();
            if (j != 0) {
                zzKl().zzMe().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzLE = zzKg().zzLE();
            Object zzao;
            if (TextUtils.isEmpty(zzLE)) {
                this.zzbuF = -1;
                zzao = zzKg().zzao(currentTimeMillis - zzKn().zzLs());
                if (!TextUtils.isEmpty(zzao)) {
                    zzatc zzfu = zzKg().zzfu(zzao);
                    if (zzfu != null) {
                        zzb(zzfu);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.zzbuF == -1) {
                this.zzbuF = zzKg().zzLM();
            }
            List<Pair> zzn = zzKg().zzn(zzLE, zzKn().zzfq(zzLE), zzKn().zzfr(zzLE));
            if (!zzn.isEmpty()) {
                zzauw.zze com_google_android_gms_internal_zzauw_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzauw_zze = (zzauw.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxw)) {
                        obj = com_google_android_gms_internal_zzauw_zze.zzbxw;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzauw_zze = (zzauw.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxw) && !com_google_android_gms_internal_zzauw_zze.zzbxw.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzauw_zzd = new zzd();
                com_google_android_gms_internal_zzauw_zzd.zzbxg = new zzauw.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzauw_zzd.zzbxg.length) {
                    com_google_android_gms_internal_zzauw_zzd.zzbxg[i] = (zzauw.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzauw_zzd.zzbxg[i].zzbxv = Long.valueOf(zzKn().zzKv());
                    com_google_android_gms_internal_zzauw_zzd.zzbxg[i].zzbxl = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzauw_zzd.zzbxg[i].zzbxB = Boolean.valueOf(zzKn().zzLh());
                    i++;
                }
                zzao = zzKl().zzak(2) ? zzaut.zzb(com_google_android_gms_internal_zzauw_zzd) : null;
                byte[] zza = zzKh().zza(com_google_android_gms_internal_zzauw_zzd);
                String zzLr = zzKn().zzLr();
                try {
                    URL url = new URL(zzLr);
                    zzK(arrayList);
                    zzKm().zzbtc.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzauw_zzd.zzbxg.length > 0) {
                        obj2 = com_google_android_gms_internal_zzauw_zzd.zzbxg[0].zzaS;
                    }
                    zzKl().zzMf().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzao);
                    zzMz().zza(zzLE, url, zza, null, new zza(this) {
                        final /* synthetic */ zzaue zzbuI;

                        {
                            this.zzbuI = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.zzbuI.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e) {
                    zzKl().zzLZ().zze("Failed to parse upload URL. Not uploading. appId", zzatx.zzfE(zzLE), zzLr);
                }
            }
        } else {
            zzKl().zzMb().log("Network not connected, ignoring upload request");
            zzMK();
        }
    }

    void zzMM() {
        this.zzbuE++;
    }

    @WorkerThread
    void zzMN() {
        zzmR();
        zzob();
        if (!this.zzbux) {
            zzKl().zzMd().log("This instance being marked as an uploader");
            zzMD();
        }
        this.zzbux = true;
    }

    @WorkerThread
    boolean zzMO() {
        zzmR();
        zzob();
        return this.zzbux;
    }

    @WorkerThread
    protected boolean zzMu() {
        boolean z = false;
        zzob();
        zzmR();
        if (this.zzbuy == null || this.zzbuz == 0 || !(this.zzbuy == null || this.zzbuy.booleanValue() || Math.abs(zznR().elapsedRealtime() - this.zzbuz) <= 1000)) {
            this.zzbuz = zznR().elapsedRealtime();
            zzKn().zzLh();
            if (zzKh().zzbW("android.permission.INTERNET") && zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE") && (zzadg.zzbi(getContext()).zzzx() || (zzaub.zzi(getContext(), false) && zzaum.zzj(getContext(), false)))) {
                z = true;
            }
            this.zzbuy = Boolean.valueOf(z);
            if (this.zzbuy.booleanValue()) {
                this.zzbuy = Boolean.valueOf(zzKh().zzga(zzKb().getGmpAppId()));
            }
        }
        return this.zzbuy.booleanValue();
    }

    public zzatx zzMv() {
        return (this.zzbue == null || !this.zzbue.isInitialized()) ? null : this.zzbue;
    }

    zzaud zzMw() {
        return this.zzbuf;
    }

    public AppMeasurement zzMx() {
        return this.zzbui;
    }

    public FirebaseAnalytics zzMy() {
        return this.zzbuj;
    }

    public zzaty zzMz() {
        zza(this.zzbun);
        return this.zzbun;
    }

    public void zzV(boolean z) {
        zzMK();
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKl().zzLZ().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzKl().zzMb().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzKl().zzLZ().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    @WorkerThread
    protected void zza(int i, Throwable th, byte[] bArr) {
        int i2 = 0;
        zzmR();
        zzob();
        if (bArr == null) {
            bArr = new byte[0];
        }
        List<Long> list = this.zzbuC;
        this.zzbuC = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            try {
                zzKm().zzbtb.set(zznR().currentTimeMillis());
                zzKm().zzbtc.set(0);
                zzMK();
                zzKl().zzMf().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzKg().beginTransaction();
                for (Long longValue : list) {
                    zzKg().zzan(longValue.longValue());
                }
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                if (zzMz().zzqa() && zzMJ()) {
                    zzMI();
                } else {
                    this.zzbuF = -1;
                    zzMK();
                }
                this.zzbuG = 0;
                return;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zzj("Database error while trying to delete uploaded bundles", e);
                this.zzbuG = zznR().elapsedRealtime();
                zzKl().zzMf().zzj("Disable upload, time", Long.valueOf(this.zzbuG));
                return;
            } catch (Throwable th2) {
                zzKg().endTransaction();
            }
        }
        zzKl().zzMf().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
        zzKm().zzbtc.set(zznR().currentTimeMillis());
        if (i == 503 || i == 429) {
            i2 = 1;
        }
        if (i2 != 0) {
            zzKm().zzbtd.set(zznR().currentTimeMillis());
        }
        zzMK();
    }

    @WorkerThread
    void zza(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || zzfu.getGmpAppId() == null || zzfu.getGmpAppId().equals(com_google_android_gms_internal_zzatd.zzbqK))) {
            zzKl().zzMb().zzj("New GMP App Id passed in. Removing cached database data. appId", zzatx.zzfE(zzfu.zzke()));
            zzKg().zzfz(zzfu.zzke());
            zzfu = null;
        }
        if (zzfu != null && zzfu.zzmZ() != null && !zzfu.zzmZ().equals(com_google_android_gms_internal_zzatd.zzbhN)) {
            Bundle bundle = new Bundle();
            bundle.putString("_pv", zzfu.zzmZ());
            zzb(new zzatq("_au", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
        }
    }

    void zza(zzatm com_google_android_gms_internal_zzatm, zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzatm);
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatm.mAppId);
        zzac.zzaw(com_google_android_gms_internal_zzatm.mAppId.equals(com_google_android_gms_internal_zzatd.packageName));
        zzauw.zze com_google_android_gms_internal_zzauw_zze = new zzauw.zze();
        com_google_android_gms_internal_zzauw_zze.zzbxi = Integer.valueOf(1);
        com_google_android_gms_internal_zzauw_zze.zzbxq = "android";
        com_google_android_gms_internal_zzauw_zze.zzaS = com_google_android_gms_internal_zzatd.packageName;
        com_google_android_gms_internal_zzauw_zze.zzbqL = com_google_android_gms_internal_zzatd.zzbqL;
        com_google_android_gms_internal_zzauw_zze.zzbhN = com_google_android_gms_internal_zzatd.zzbhN;
        com_google_android_gms_internal_zzauw_zze.zzbxD = Integer.valueOf((int) com_google_android_gms_internal_zzatd.zzbqR);
        com_google_android_gms_internal_zzauw_zze.zzbxu = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqM);
        com_google_android_gms_internal_zzauw_zze.zzbqK = com_google_android_gms_internal_zzatd.zzbqK;
        com_google_android_gms_internal_zzauw_zze.zzbxz = com_google_android_gms_internal_zzatd.zzbqN == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzatd.zzbqN);
        Pair zzfG = zzKm().zzfG(com_google_android_gms_internal_zzatd.packageName);
        if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
            com_google_android_gms_internal_zzauw_zze.zzbxw = (String) zzfG.first;
            com_google_android_gms_internal_zzauw_zze.zzbxx = (Boolean) zzfG.second;
        } else if (!zzKc().zzbL(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzKl().zzMb().zzj("null secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
                string = "null";
            } else if (string.isEmpty()) {
                zzKl().zzMb().zzj("empty secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
            }
            com_google_android_gms_internal_zzauw_zze.zzbxG = string;
        }
        com_google_android_gms_internal_zzauw_zze.zzbxr = zzKc().zzkN();
        com_google_android_gms_internal_zzauw_zze.zzbb = zzKc().zzLS();
        com_google_android_gms_internal_zzauw_zze.zzbxt = Integer.valueOf((int) zzKc().zzLT());
        com_google_android_gms_internal_zzauw_zze.zzbxs = zzKc().zzLU();
        com_google_android_gms_internal_zzauw_zze.zzbxv = null;
        com_google_android_gms_internal_zzauw_zze.zzbxl = null;
        com_google_android_gms_internal_zzauw_zze.zzbxm = null;
        com_google_android_gms_internal_zzauw_zze.zzbxn = null;
        com_google_android_gms_internal_zzauw_zze.zzbxI = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqT);
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (zzfu == null) {
            zzfu = new zzatc(this, com_google_android_gms_internal_zzatd.packageName);
            zzfu.zzfd(zzKm().zzMi());
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqS);
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqK);
            zzfu.zzff(zzKm().zzfH(com_google_android_gms_internal_zzatd.packageName));
            zzfu.zzad(0);
            zzfu.zzY(0);
            zzfu.zzZ(0);
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhN);
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqR);
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqL);
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqM);
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqN);
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqP);
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqT);
            zzKg().zza(zzfu);
        }
        com_google_android_gms_internal_zzauw_zze.zzbxy = zzfu.getAppInstanceId();
        com_google_android_gms_internal_zzauw_zze.zzbqS = zzfu.zzKq();
        List zzft = zzKg().zzft(com_google_android_gms_internal_zzatd.packageName);
        com_google_android_gms_internal_zzauw_zze.zzbxk = new zzg[zzft.size()];
        for (int i = 0; i < zzft.size(); i++) {
            zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
            com_google_android_gms_internal_zzauw_zze.zzbxk[i] = com_google_android_gms_internal_zzauw_zzg;
            com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
            com_google_android_gms_internal_zzauw_zzg.zzbxM = Long.valueOf(((zzaus) zzft.get(i)).zzbwj);
            zzKh().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
        }
        try {
            if (zzKg().zza(com_google_android_gms_internal_zzatm, zzKg().zza(com_google_android_gms_internal_zzauw_zze), zza(com_google_android_gms_internal_zzatm))) {
                this.zzbuG = 0;
            }
        } catch (IOException e) {
            zzKl().zzLZ().zze("Data loss. Failed to insert raw event metadata. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKl().zzLZ().log("Bad chanel to read from");
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
            zzKl().zzLZ().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzKl().zzLZ().zzj("Failed to write to channel", e);
            return false;
        }
    }

    @WorkerThread
    public byte[] zza(@NonNull zzatq com_google_android_gms_internal_zzatq, @Size(min = 1) String str) {
        zzob();
        zzmR();
        zzJV();
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzac.zzdr(str);
        zzd com_google_android_gms_internal_zzauw_zzd = new zzd();
        zzKg().beginTransaction();
        try {
            zzatc zzfu = zzKg().zzfu(str);
            byte[] bArr;
            if (zzfu == null) {
                zzKl().zzMe().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzfu.zzKx()) {
                long j;
                zzauw.zze com_google_android_gms_internal_zzauw_zze = new zzauw.zze();
                com_google_android_gms_internal_zzauw_zzd.zzbxg = new zzauw.zze[]{com_google_android_gms_internal_zzauw_zze};
                com_google_android_gms_internal_zzauw_zze.zzbxi = Integer.valueOf(1);
                com_google_android_gms_internal_zzauw_zze.zzbxq = "android";
                com_google_android_gms_internal_zzauw_zze.zzaS = zzfu.zzke();
                com_google_android_gms_internal_zzauw_zze.zzbqL = zzfu.zzKu();
                com_google_android_gms_internal_zzauw_zze.zzbhN = zzfu.zzmZ();
                com_google_android_gms_internal_zzauw_zze.zzbxD = Integer.valueOf((int) zzfu.zzKt());
                com_google_android_gms_internal_zzauw_zze.zzbxu = Long.valueOf(zzfu.zzKv());
                com_google_android_gms_internal_zzauw_zze.zzbqK = zzfu.getGmpAppId();
                com_google_android_gms_internal_zzauw_zze.zzbxz = Long.valueOf(zzfu.zzKw());
                Pair zzfG = zzKm().zzfG(zzfu.zzke());
                if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxw = (String) zzfG.first;
                    com_google_android_gms_internal_zzauw_zze.zzbxx = (Boolean) zzfG.second;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxr = zzKc().zzkN();
                com_google_android_gms_internal_zzauw_zze.zzbb = zzKc().zzLS();
                com_google_android_gms_internal_zzauw_zze.zzbxt = Integer.valueOf((int) zzKc().zzLT());
                com_google_android_gms_internal_zzauw_zze.zzbxs = zzKc().zzLU();
                com_google_android_gms_internal_zzauw_zze.zzbxy = zzfu.getAppInstanceId();
                com_google_android_gms_internal_zzauw_zze.zzbqS = zzfu.zzKq();
                List zzft = zzKg().zzft(zzfu.zzke());
                com_google_android_gms_internal_zzauw_zze.zzbxk = new zzg[zzft.size()];
                for (int i = 0; i < zzft.size(); i++) {
                    zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
                    com_google_android_gms_internal_zzauw_zze.zzbxk[i] = com_google_android_gms_internal_zzauw_zzg;
                    com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
                    com_google_android_gms_internal_zzauw_zzg.zzbxM = Long.valueOf(((zzaus) zzft.get(i)).zzbwj);
                    zzKh().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
                }
                Bundle zzLW = com_google_android_gms_internal_zzatq.zzbrH.zzLW();
                if ("_iap".equals(com_google_android_gms_internal_zzatq.name)) {
                    zzLW.putLong("_c", 1);
                    zzKl().zzMe().log("Marking in-app purchase as real-time");
                    zzLW.putLong("_r", 1);
                }
                zzLW.putString("_o", com_google_android_gms_internal_zzatq.zzbqW);
                if (zzKh().zzge(com_google_android_gms_internal_zzauw_zze.zzaS)) {
                    zzKh().zza(zzLW, "_dbg", Long.valueOf(1));
                    zzKh().zza(zzLW, "_r", Long.valueOf(1));
                }
                zzatn zzQ = zzKg().zzQ(str, com_google_android_gms_internal_zzatq.name);
                if (zzQ == null) {
                    zzKg().zza(new zzatn(str, com_google_android_gms_internal_zzatq.name, 1, 0, com_google_android_gms_internal_zzatq.zzbrI));
                    j = 0;
                } else {
                    j = zzQ.zzbrD;
                    zzKg().zza(zzQ.zzap(com_google_android_gms_internal_zzatq.zzbrI).zzLV());
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqW, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrI, j, zzLW);
                zzb com_google_android_gms_internal_zzauw_zzb = new zzb();
                com_google_android_gms_internal_zzauw_zze.zzbxj = new zzb[]{com_google_android_gms_internal_zzauw_zzb};
                com_google_android_gms_internal_zzauw_zzb.zzbxc = Long.valueOf(com_google_android_gms_internal_zzatm.zzaxb);
                com_google_android_gms_internal_zzauw_zzb.name = com_google_android_gms_internal_zzatm.mName;
                com_google_android_gms_internal_zzauw_zzb.zzbxd = Long.valueOf(com_google_android_gms_internal_zzatm.zzbrz);
                com_google_android_gms_internal_zzauw_zzb.zzbxb = new zzc[com_google_android_gms_internal_zzatm.zzbrA.size()];
                Iterator it = com_google_android_gms_internal_zzatm.zzbrA.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzauw_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zzb.zzbxb[i2] = com_google_android_gms_internal_zzauw_zzc;
                    com_google_android_gms_internal_zzauw_zzc.name = str2;
                    zzKh().zza(com_google_android_gms_internal_zzauw_zzc, com_google_android_gms_internal_zzatm.zzbrA.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxC = zza(zzfu.zzke(), com_google_android_gms_internal_zzauw_zze.zzbxk, com_google_android_gms_internal_zzauw_zze.zzbxj);
                com_google_android_gms_internal_zzauw_zze.zzbxm = com_google_android_gms_internal_zzauw_zzb.zzbxc;
                com_google_android_gms_internal_zzauw_zze.zzbxn = com_google_android_gms_internal_zzauw_zzb.zzbxc;
                long zzKs = zzfu.zzKs();
                com_google_android_gms_internal_zzauw_zze.zzbxp = zzKs != 0 ? Long.valueOf(zzKs) : null;
                long zzKr = zzfu.zzKr();
                if (zzKr != 0) {
                    zzKs = zzKr;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxo = zzKs != 0 ? Long.valueOf(zzKs) : null;
                zzfu.zzKB();
                com_google_android_gms_internal_zzauw_zze.zzbxA = Integer.valueOf((int) zzfu.zzKy());
                com_google_android_gms_internal_zzauw_zze.zzbxv = Long.valueOf(zzKn().zzKv());
                com_google_android_gms_internal_zzauw_zze.zzbxl = Long.valueOf(zznR().currentTimeMillis());
                com_google_android_gms_internal_zzauw_zze.zzbxB = Boolean.TRUE;
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxm.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxn.longValue());
                zzKg().zza(zzfu);
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzauw_zzd.zzafB()];
                    zzbyc zzah = zzbyc.zzah(bArr);
                    com_google_android_gms_internal_zzauw_zzd.zza(zzah);
                    zzah.zzafo();
                    return zzKh().zzk(bArr);
                } catch (IOException e) {
                    zzKl().zzLZ().zze("Data loss. Failed to bundle and serialize. appId", zzatx.zzfE(str), e);
                    return null;
                }
            } else {
                zzKl().zzMe().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzKg().endTransaction();
                return bArr;
            }
        } finally {
            zzKg().endTransaction();
        }
    }

    boolean zzaq(long j) {
        return zzl(null, j);
    }

    void zzb(zzatc com_google_android_gms_internal_zzatc) {
        Map map = null;
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzatc.getGmpAppId())) {
            zzb(com_google_android_gms_internal_zzatc.zzke(), 204, null, null, null);
            return;
        }
        String zzP = zzKn().zzP(com_google_android_gms_internal_zzatc.getGmpAppId(), com_google_android_gms_internal_zzatc.getAppInstanceId());
        try {
            URL url = new URL(zzP);
            zzKl().zzMf().zzj("Fetching remote configuration", com_google_android_gms_internal_zzatc.zzke());
            zzauv.zzb zzfL = zzKi().zzfL(com_google_android_gms_internal_zzatc.zzke());
            CharSequence zzfM = zzKi().zzfM(com_google_android_gms_internal_zzatc.zzke());
            if (!(zzfL == null || TextUtils.isEmpty(zzfM))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzfM);
            }
            zzMz().zza(com_google_android_gms_internal_zzatc.zzke(), url, map, new zza(this) {
                final /* synthetic */ zzaue zzbuI;

                {
                    this.zzbuI = r1;
                }

                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                    this.zzbuI.zzb(str, i, th, bArr, map);
                }
            });
        } catch (MalformedURLException e) {
            zzKl().zzLZ().zze("Failed to parse config URL. Not fetching. appId", zzatx.zzfE(com_google_android_gms_internal_zzatc.zzke()), zzP);
        }
    }

    @WorkerThread
    void zzb(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzmR();
        zzob();
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        zzb(new zzatq("_v", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzb(zzatg com_google_android_gms_internal_zzatg, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzdr(com_google_android_gms_internal_zzatg.packageName);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqW);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqX);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbqX.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK)) {
            if (com_google_android_gms_internal_zzatd.zzbqP) {
                zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
                zzKg().beginTransaction();
                try {
                    Object obj;
                    zzatg zzT = zzKg().zzT(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqX.name);
                    if (zzT != null && zzT.zzbqZ) {
                        com_google_android_gms_internal_zzatg2.zzbqW = zzT.zzbqW;
                        com_google_android_gms_internal_zzatg2.zzbqY = zzT.zzbqY;
                        com_google_android_gms_internal_zzatg2.zzbra = zzT.zzbra;
                        com_google_android_gms_internal_zzatg2.zzbrd = zzT.zzbrd;
                        obj = null;
                    } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzatg2.zzbra)) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg2.zzbqX;
                        com_google_android_gms_internal_zzatg2.zzbqX = new zzauq(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzatg2.zzbqY, com_google_android_gms_internal_zzauq.getValue(), com_google_android_gms_internal_zzauq.zzbqW);
                        com_google_android_gms_internal_zzatg2.zzbqZ = true;
                        int i = 1;
                    } else {
                        obj = null;
                    }
                    if (com_google_android_gms_internal_zzatg2.zzbqZ) {
                        zzauq com_google_android_gms_internal_zzauq2 = com_google_android_gms_internal_zzatg2.zzbqX;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqW, com_google_android_gms_internal_zzauq2.name, com_google_android_gms_internal_zzauq2.zzbwf, com_google_android_gms_internal_zzauq2.getValue());
                        if (zzKg().zza(com_google_android_gms_internal_zzaus)) {
                            zzKl().zzMe().zzd("User property updated immediately", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLZ().zzd("(2)Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (!(obj == null || com_google_android_gms_internal_zzatg2.zzbrd == null)) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg2.zzbrd, com_google_android_gms_internal_zzatg2.zzbqY), com_google_android_gms_internal_zzatd);
                        }
                    }
                    if (zzKg().zza(com_google_android_gms_internal_zzatg2)) {
                        zzKl().zzMe().zzd("Conditional property added", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqX.name, com_google_android_gms_internal_zzatg2.zzbqX.getValue());
                    } else {
                        zzKl().zzLZ().zzd("Too many conditional properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzatg2.zzbqX.name, com_google_android_gms_internal_zzatg2.zzbqX.getValue());
                    }
                    zzKg().setTransactionSuccessful();
                } finally {
                    zzKg().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzatd);
            }
        }
    }

    @WorkerThread
    void zzb(zzatq com_google_android_gms_internal_zzatq, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        zzmR();
        zzob();
        String str = com_google_android_gms_internal_zzatd.packageName;
        long j = com_google_android_gms_internal_zzatq.zzbrI;
        if (!zzKh().zzd(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd)) {
            return;
        }
        if (com_google_android_gms_internal_zzatd.zzbqP) {
            zzKg().beginTransaction();
            try {
                for (zzatg com_google_android_gms_internal_zzatg : zzKg().zzh(str, j)) {
                    if (com_google_android_gms_internal_zzatg != null) {
                        zzKl().zzMe().zzd("User property timed out", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name, com_google_android_gms_internal_zzatg.zzbqX.getValue());
                        if (com_google_android_gms_internal_zzatg.zzbrb != null) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg.zzbrb, j), com_google_android_gms_internal_zzatd);
                        }
                        zzKg().zzU(str, com_google_android_gms_internal_zzatg.zzbqX.name);
                    }
                }
                List<zzatg> zzi = zzKg().zzi(str, j);
                List<zzatq> arrayList = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg2 : zzi) {
                    if (com_google_android_gms_internal_zzatg2 != null) {
                        zzKl().zzMe().zzd("User property expired", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqX.name, com_google_android_gms_internal_zzatg2.zzbqX.getValue());
                        zzKg().zzR(str, com_google_android_gms_internal_zzatg2.zzbqX.name);
                        if (com_google_android_gms_internal_zzatg2.zzbrf != null) {
                            arrayList.add(com_google_android_gms_internal_zzatg2.zzbrf);
                        }
                        zzKg().zzU(str, com_google_android_gms_internal_zzatg2.zzbqX.name);
                    }
                }
                for (zzatq com_google_android_gms_internal_zzatq2 : arrayList) {
                    zzc(new zzatq(com_google_android_gms_internal_zzatq2, j), com_google_android_gms_internal_zzatd);
                }
                zzi = zzKg().zzc(str, com_google_android_gms_internal_zzatq.name, j);
                List<zzatq> arrayList2 = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg3 : zzi) {
                    if (com_google_android_gms_internal_zzatg3 != null) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg3.zzbqX;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzatg3.zzbqW, com_google_android_gms_internal_zzauq.name, j, com_google_android_gms_internal_zzauq.getValue());
                        if (zzKg().zza(com_google_android_gms_internal_zzaus)) {
                            zzKl().zzMe().zzd("User property triggered", com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLZ().zzd("Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg3.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (com_google_android_gms_internal_zzatg3.zzbrd != null) {
                            arrayList2.add(com_google_android_gms_internal_zzatg3.zzbrd);
                        }
                        com_google_android_gms_internal_zzatg3.zzbqX = new zzauq(com_google_android_gms_internal_zzaus);
                        com_google_android_gms_internal_zzatg3.zzbqZ = true;
                        zzKg().zza(com_google_android_gms_internal_zzatg3);
                    }
                }
                zzc(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd);
                for (zzatq com_google_android_gms_internal_zzatq22 : arrayList2) {
                    zzc(new zzatq(com_google_android_gms_internal_zzatq22, j), com_google_android_gms_internal_zzatd);
                }
                zzKg().setTransactionSuccessful();
            } finally {
                zzKg().endTransaction();
            }
        } else {
            zzf(com_google_android_gms_internal_zzatd);
        }
    }

    @WorkerThread
    void zzb(zzatq com_google_android_gms_internal_zzatq, String str) {
        zzatc zzfu = zzKg().zzfu(str);
        if (zzfu == null || TextUtils.isEmpty(zzfu.zzmZ())) {
            zzKl().zzMe().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKl().zzMb().zzj("App version does not match; dropping event. appId", zzatx.zzfE(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKl().zzMb().zzj("Could not find package. appId", zzatx.zzfE(str));
            }
        }
        zzatq com_google_android_gms_internal_zzatq2 = com_google_android_gms_internal_zzatq;
        zzb(com_google_android_gms_internal_zzatq2, new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), zzfu.zzKw(), null, zzfu.zzKx(), false, zzfu.zzKq(), zzfu.zzuW(), 0, 0));
    }

    void zzb(zzauh com_google_android_gms_internal_zzauh) {
        this.zzbuD++;
    }

    @WorkerThread
    void zzb(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        int i = 0;
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK)) {
            if (com_google_android_gms_internal_zzatd.zzbqP) {
                int zzfX = zzKh().zzfX(com_google_android_gms_internal_zzauq.name);
                String zza;
                if (zzfX != 0) {
                    zza = zzKh().zza(com_google_android_gms_internal_zzauq.name, zzKn().zzKN(), true);
                    if (com_google_android_gms_internal_zzauq.name != null) {
                        i = com_google_android_gms_internal_zzauq.name.length();
                    }
                    zzKh().zza(zzfX, "_ev", zza, i);
                    return;
                }
                zzfX = zzKh().zzl(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzfX != 0) {
                    zza = zzKh().zza(com_google_android_gms_internal_zzauq.name, zzKn().zzKN(), true);
                    Object value = com_google_android_gms_internal_zzauq.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzKh().zza(zzfX, "_ev", zza, i);
                    return;
                }
                Object zzm = zzKh().zzm(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzm != null) {
                    zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.zzbqW, com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.zzbwf, zzm);
                    zzKl().zzMe().zze("Setting user property", com_google_android_gms_internal_zzaus.mName, zzm);
                    zzKg().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzatd);
                        boolean zza2 = zzKg().zza(com_google_android_gms_internal_zzaus);
                        zzKg().setTransactionSuccessful();
                        if (zza2) {
                            zzKl().zzMe().zze("User property set", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLZ().zze("Too many unique user properties are set. Ignoring user property", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                            zzKh().zza(9, null, null, 0);
                        }
                        zzKg().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzKg().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zzf(com_google_android_gms_internal_zzatd);
        }
    }

    @WorkerThread
    void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        int i2 = 0;
        zzmR();
        zzob();
        zzac.zzdr(str);
        if (bArr == null) {
            bArr = new byte[0];
        }
        zzKg().beginTransaction();
        try {
            zzatc zzfu = zzKg().zzfu(str);
            int i3 = ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null) ? 1 : 0;
            if (zzfu == null) {
                zzKl().zzMb().zzj("App does not exist in onConfigFetched. appId", zzatx.zzfE(str));
            } else if (i3 != 0 || i == 404) {
                List list = map != null ? (List) map.get("Last-Modified") : null;
                String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
                if (i == 404 || i == 304) {
                    if (zzKi().zzfL(str) == null && !zzKi().zzb(str, null, null)) {
                        zzKg().endTransaction();
                        return;
                    }
                } else if (!zzKi().zzb(str, bArr, str2)) {
                    zzKg().endTransaction();
                    return;
                }
                zzfu.zzae(zznR().currentTimeMillis());
                zzKg().zza(zzfu);
                if (i == 404) {
                    zzKl().zzMc().zzj("Config not found. Using empty config. appId", str);
                } else {
                    zzKl().zzMf().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzMz().zzqa() && zzMJ()) {
                    zzMI();
                } else {
                    zzMK();
                }
            } else {
                zzfu.zzaf(zznR().currentTimeMillis());
                zzKg().zza(zzfu);
                zzKl().zzMf().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzKi().zzfN(str);
                zzKm().zzbtc.set(zznR().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzKm().zzbtd.set(zznR().currentTimeMillis());
                }
                zzMK();
            }
            zzKg().setTransactionSuccessful();
        } finally {
            zzKg().endTransaction();
        }
    }

    @WorkerThread
    void zzc(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzmR();
        zzob();
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || !TextUtils.isEmpty(zzfu.getGmpAppId()) || com_google_android_gms_internal_zzatd == null || TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK))) {
            zzfu.zzae(0);
            zzKg().zza(zzfu);
        }
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        bundle.putLong("_uwa", 0);
        bundle.putLong("_pfo", 0);
        bundle.putLong("_sys", 0);
        bundle.putLong("_sysu", 0);
        if (getContext().getPackageManager() == null) {
            zzKl().zzLZ().zzj("PackageManager is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName));
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = zzadg.zzbi(getContext()).getPackageInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e) {
                zzKl().zzLZ().zze("Package info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzKl().zzLZ().zze("Application info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e2);
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
        long zzfA = zzKg().zzfA(com_google_android_gms_internal_zzatd.packageName);
        if (zzfA >= 0) {
            bundle.putLong("_pfo", zzfA);
        }
        zzb(new zzatq("_f", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzc(zzatg com_google_android_gms_internal_zzatg, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzdr(com_google_android_gms_internal_zzatg.packageName);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqX);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbqX.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK)) {
            if (com_google_android_gms_internal_zzatd.zzbqP) {
                zzKg().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzatg zzT = zzKg().zzT(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name);
                    if (zzT != null) {
                        zzKl().zzMe().zze("Removing conditional user property", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name);
                        zzKg().zzU(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name);
                        if (zzT.zzbqZ) {
                            zzKg().zzR(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name);
                        }
                        if (com_google_android_gms_internal_zzatg.zzbrf != null) {
                            Bundle bundle = null;
                            if (com_google_android_gms_internal_zzatg.zzbrf.zzbrH != null) {
                                bundle = com_google_android_gms_internal_zzatg.zzbrf.zzbrH.zzLW();
                            }
                            zzc(zzKh().zza(com_google_android_gms_internal_zzatg.zzbrf.name, bundle, zzT.zzbqW, com_google_android_gms_internal_zzatg.zzbrf.zzbrI, true, false), com_google_android_gms_internal_zzatd);
                        }
                    } else {
                        zzKl().zzMb().zze("Conditional user property doesn't exist", zzatx.zzfE(com_google_android_gms_internal_zzatg.packageName), com_google_android_gms_internal_zzatg.zzbqX.name);
                    }
                    zzKg().setTransactionSuccessful();
                } finally {
                    zzKg().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzatd);
            }
        }
    }

    @WorkerThread
    void zzc(zzatq com_google_android_gms_internal_zzatq, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        long nanoTime = System.nanoTime();
        zzmR();
        zzob();
        String str = com_google_android_gms_internal_zzatd.packageName;
        if (!zzKh().zzd(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd)) {
            return;
        }
        if (!com_google_android_gms_internal_zzatd.zzbqP) {
            zzf(com_google_android_gms_internal_zzatd);
        } else if (zzKi().zzaa(str, com_google_android_gms_internal_zzatq.name)) {
            zzKl().zzMb().zze("Dropping blacklisted event. appId", zzatx.zzfE(str), com_google_android_gms_internal_zzatq.name);
            r2 = (zzKh().zzgg(str) || zzKh().zzgh(str)) ? 1 : null;
            if (r2 == null && !"_err".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKh().zza(11, "_ev", com_google_android_gms_internal_zzatq.name, 0);
            }
            if (r2 != null) {
                zzatc zzfu = zzKg().zzfu(str);
                if (zzfu != null) {
                    if (Math.abs(zznR().currentTimeMillis() - Math.max(zzfu.zzKA(), zzfu.zzKz())) > zzKn().zzLm()) {
                        zzKl().zzMe().log("Fetching config for blacklisted app");
                        zzb(zzfu);
                    }
                }
            }
        } else {
            if (zzKl().zzak(2)) {
                zzKl().zzMf().zzj("Logging event", com_google_android_gms_internal_zzatq);
            }
            zzKg().beginTransaction();
            try {
                Bundle zzLW = com_google_android_gms_internal_zzatq.zzbrH.zzLW();
                zzf(com_google_android_gms_internal_zzatd);
                if ("_iap".equals(com_google_android_gms_internal_zzatq.name) || Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatq.name)) {
                    long round;
                    r2 = zzLW.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatq.name)) {
                        double d = zzLW.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzLW.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzKl().zzMb().zze("Data lost. Currency value is too big. appId", zzatx.zzfE(str), Double.valueOf(d));
                            zzKg().setTransactionSuccessful();
                            zzKg().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzLW.getLong(Param.VALUE);
                    }
                    if (!TextUtils.isEmpty(r2)) {
                        String toUpperCase = r2.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzaus zzS = zzKg().zzS(str, concat);
                            if (zzS == null || !(zzS.mValue instanceof Long)) {
                                zzKg().zzz(str, zzKn().zzfn(str) - 1);
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqW, concat, zznR().currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqW, concat, zznR().currentTimeMillis(), Long.valueOf(round + ((Long) zzS.mValue).longValue()));
                            }
                            if (!zzKg().zza(zzS)) {
                                zzKl().zzLZ().zzd("Too many unique user properties are set. Ignoring user property. appId", zzatx.zzfE(str), zzS.mName, zzS.mValue);
                                zzKh().zza(9, null, null, 0);
                            }
                        }
                    }
                }
                boolean zzfT = zzaut.zzfT(com_google_android_gms_internal_zzatq.name);
                boolean equals = "_err".equals(com_google_android_gms_internal_zzatq.name);
                com.google.android.gms.internal.zzatj.zza zza = zzKg().zza(zzMG(), str, true, zzfT, false, equals, false);
                long zzKV = zza.zzbrp - zzKn().zzKV();
                if (zzKV > 0) {
                    if (zzKV % 1000 == 1) {
                        zzKl().zzLZ().zze("Data loss. Too many events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrp));
                    }
                    zzKh().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                    zzKg().setTransactionSuccessful();
                    return;
                }
                zzatn com_google_android_gms_internal_zzatn;
                if (zzfT) {
                    zzKV = zza.zzbro - zzKn().zzKW();
                    if (zzKV > 0) {
                        if (zzKV % 1000 == 1) {
                            zzKl().zzLZ().zze("Data loss. Too many public events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbro));
                        }
                        zzKh().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                        zzKg().setTransactionSuccessful();
                        zzKg().endTransaction();
                        return;
                    }
                }
                if (equals) {
                    zzKV = zza.zzbrr - ((long) zzKn().zzfj(com_google_android_gms_internal_zzatd.packageName));
                    if (zzKV > 0) {
                        if (zzKV == 1) {
                            zzKl().zzLZ().zze("Too many error events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrr));
                        }
                        zzKg().setTransactionSuccessful();
                        zzKg().endTransaction();
                        return;
                    }
                }
                zzKh().zza(zzLW, "_o", com_google_android_gms_internal_zzatq.zzbqW);
                if (zzKh().zzge(str)) {
                    zzKh().zza(zzLW, "_dbg", Long.valueOf(1));
                    zzKh().zza(zzLW, "_r", Long.valueOf(1));
                }
                zzKV = zzKg().zzfv(str);
                if (zzKV > 0) {
                    zzKl().zzMb().zze("Data lost. Too many events stored on disk, deleted. appId", zzatx.zzfE(str), Long.valueOf(zzKV));
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqW, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrI, 0, zzLW);
                zzatn zzQ = zzKg().zzQ(str, com_google_android_gms_internal_zzatm.mName);
                if (zzQ == null) {
                    long zzfC = zzKg().zzfC(str);
                    zzKn().zzKU();
                    if (zzfC >= 500) {
                        zzKl().zzLZ().zzd("Too many event names used, ignoring event. appId, name, supported count", zzatx.zzfE(str), com_google_android_gms_internal_zzatm.mName, Integer.valueOf(zzKn().zzKU()));
                        zzKh().zza(8, null, null, 0);
                        zzKg().endTransaction();
                        return;
                    }
                    com_google_android_gms_internal_zzatn = new zzatn(str, com_google_android_gms_internal_zzatm.mName, 0, 0, com_google_android_gms_internal_zzatm.zzaxb);
                } else {
                    com_google_android_gms_internal_zzatm = com_google_android_gms_internal_zzatm.zza(this, zzQ.zzbrD);
                    com_google_android_gms_internal_zzatn = zzQ.zzap(com_google_android_gms_internal_zzatm.zzaxb);
                }
                zzKg().zza(com_google_android_gms_internal_zzatn);
                zza(com_google_android_gms_internal_zzatm, com_google_android_gms_internal_zzatd);
                zzKg().setTransactionSuccessful();
                if (zzKl().zzak(2)) {
                    zzKl().zzMf().zzj("Event recorded", com_google_android_gms_internal_zzatm);
                }
                zzKg().endTransaction();
                zzMK();
                zzKl().zzMf().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
            } finally {
                zzKg().endTransaction();
            }
        }
    }

    @WorkerThread
    void zzc(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK)) {
            if (com_google_android_gms_internal_zzatd.zzbqP) {
                zzKl().zzMe().zzj("Removing user property", com_google_android_gms_internal_zzauq.name);
                zzKg().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzKg().zzR(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.name);
                    zzKg().setTransactionSuccessful();
                    zzKl().zzMe().zzj("User property removed", com_google_android_gms_internal_zzauq.name);
                } finally {
                    zzKg().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzatd);
            }
        }
    }

    void zzd(zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        zzf(com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzd(zzatd com_google_android_gms_internal_zzatd, long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("_et", 1);
        zzb(new zzatq("_e", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzd(zzatg com_google_android_gms_internal_zzatg) {
        zzatd zzfO = zzfO(com_google_android_gms_internal_zzatg.packageName);
        if (zzfO != null) {
            zzb(com_google_android_gms_internal_zzatg, zzfO);
        }
    }

    @WorkerThread
    public void zze(zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqK)) {
            if (com_google_android_gms_internal_zzatd.zzbqP) {
                long j = com_google_android_gms_internal_zzatd.zzbqU;
                if (j == 0) {
                    j = zznR().currentTimeMillis();
                }
                int i = com_google_android_gms_internal_zzatd.zzbqV;
                if (!(i == 0 || i == 1)) {
                    zzKl().zzMb().zze("Incorrect app type, assuming installed app. appId, appType", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), Integer.valueOf(i));
                    i = 0;
                }
                zzKg().beginTransaction();
                try {
                    zza(com_google_android_gms_internal_zzatd, j);
                    zzf(com_google_android_gms_internal_zzatd);
                    zzatn com_google_android_gms_internal_zzatn = null;
                    if (i == 0) {
                        com_google_android_gms_internal_zzatn = zzKg().zzQ(com_google_android_gms_internal_zzatd.packageName, "_f");
                    } else if (i == 1) {
                        com_google_android_gms_internal_zzatn = zzKg().zzQ(com_google_android_gms_internal_zzatd.packageName, "_v");
                    }
                    if (com_google_android_gms_internal_zzatn == null) {
                        long j2 = (1 + (j / 3600000)) * 3600000;
                        if (i == 0) {
                            zzb(new zzauq("_fot", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzatd);
                            zzc(com_google_android_gms_internal_zzatd, j);
                        } else if (i == 1) {
                            zzb(new zzauq("_fvt", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzatd);
                            zzb(com_google_android_gms_internal_zzatd, j);
                        }
                        zzd(com_google_android_gms_internal_zzatd, j);
                    } else if (com_google_android_gms_internal_zzatd.zzbqQ) {
                        zze(com_google_android_gms_internal_zzatd, j);
                    }
                    zzKg().setTransactionSuccessful();
                } finally {
                    zzKg().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzatd);
            }
        }
    }

    @WorkerThread
    void zze(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzb(new zzatq("_cd", new zzato(new Bundle()), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zze(zzatg com_google_android_gms_internal_zzatg) {
        zzatd zzfO = zzfO(com_google_android_gms_internal_zzatg.packageName);
        if (zzfO != null) {
            zzc(com_google_android_gms_internal_zzatg, zzfO);
        }
    }

    @WorkerThread
    zzatd zzfO(String str) {
        zzatc zzfu = zzKg().zzfu(str);
        if (zzfu == null || TextUtils.isEmpty(zzfu.zzmZ())) {
            zzKl().zzMe().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKl().zzMb().zzj("App version does not match; dropping. appId", zzatx.zzfE(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), zzfu.zzKw(), null, zzfu.zzKx(), false, zzfu.zzKq(), zzfu.zzuW(), 0, 0);
    }

    public String zzfP(final String str) {
        Object e;
        try {
            return (String) zzKk().zzd(new Callable<String>(this) {
                final /* synthetic */ zzaue zzbuI;

                public /* synthetic */ Object call() throws Exception {
                    return zzbY();
                }

                public String zzbY() throws Exception {
                    zzatc zzfu = this.zzbuI.zzKg().zzfu(str);
                    if (zzfu != null) {
                        return zzfu.getAppInstanceId();
                    }
                    this.zzbuI.zzKl().zzMb().log("App info was null when attempting to get app instance id");
                    return null;
                }
            }).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzKl().zzLZ().zze("Failed to get app instance id. appId", zzatx.zzfE(str), e);
        return null;
    }

    @WorkerThread
    public void zzmR() {
        zzKk().zzmR();
    }

    public zze zznR() {
        return this.zzuP;
    }

    void zzob() {
        if (!this.zzadP) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    @WorkerThread
    boolean zzy(int i, int i2) {
        zzmR();
        if (i > i2) {
            zzKl().zzLZ().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzMC())) {
                zzKl().zzMf().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzKl().zzLZ().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }
}
