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
    private static volatile zzaue zzbtZ;
    private final Context mContext;
    private final boolean zzadP;
    private List<Long> zzbuA;
    private int zzbuB;
    private int zzbuC;
    private long zzbuD = -1;
    protected long zzbuE;
    private final zzati zzbua;
    private final zzaua zzbub;
    private final zzatx zzbuc;
    private final zzaud zzbud;
    private final zzaun zzbue;
    private final zzauc zzbuf;
    private final AppMeasurement zzbug;
    private final FirebaseAnalytics zzbuh;
    private final zzaut zzbui;
    private final zzatj zzbuj;
    private final zzatv zzbuk;
    private final zzaty zzbul;
    private final zzauk zzbum;
    private final zzaul zzbun;
    private final zzatl zzbuo;
    private final zzauj zzbup;
    private final zzatu zzbuq;
    private final zzatz zzbur;
    private final zzaup zzbus;
    private final zzatf zzbut;
    private final zzatb zzbuu;
    private boolean zzbuv;
    private Boolean zzbuw;
    private long zzbux;
    private FileLock zzbuy;
    private FileChannel zzbuz;
    private final zze zzuP;

    private class zza implements zzb {
        final /* synthetic */ zzaue zzbuF;
        zzauw.zze zzbuG;
        List<Long> zzbuH;
        long zzbuI;
        List<zzb> zzth;

        private zza(zzaue com_google_android_gms_internal_zzaue) {
            this.zzbuF = com_google_android_gms_internal_zzaue;
        }

        private long zza(zzb com_google_android_gms_internal_zzauw_zzb) {
            return ((com_google_android_gms_internal_zzauw_zzb.zzbwZ.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zzth == null || this.zzth.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzauw_zzb) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zzb);
            if (this.zzth == null) {
                this.zzth = new ArrayList();
            }
            if (this.zzbuH == null) {
                this.zzbuH = new ArrayList();
            }
            if (this.zzth.size() > 0 && zza((zzb) this.zzth.get(0)) != zza(com_google_android_gms_internal_zzauw_zzb)) {
                return false;
            }
            long zzaeT = this.zzbuI + ((long) com_google_android_gms_internal_zzauw_zzb.zzaeT());
            if (zzaeT >= ((long) this.zzbuF.zzKn().zzLn())) {
                return false;
            }
            this.zzbuI = zzaeT;
            this.zzth.add(com_google_android_gms_internal_zzauw_zzb);
            this.zzbuH.add(Long.valueOf(j));
            return this.zzth.size() < this.zzbuF.zzKn().zzLo();
        }

        public void zzb(zzauw.zze com_google_android_gms_internal_zzauw_zze) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zze);
            this.zzbuG = com_google_android_gms_internal_zzauw_zze;
        }
    }

    zzaue(zzaui com_google_android_gms_internal_zzaui) {
        zzac.zzw(com_google_android_gms_internal_zzaui);
        this.mContext = com_google_android_gms_internal_zzaui.mContext;
        this.zzuP = com_google_android_gms_internal_zzaui.zzn(this);
        this.zzbua = com_google_android_gms_internal_zzaui.zza(this);
        zzaua zzb = com_google_android_gms_internal_zzaui.zzb(this);
        zzb.initialize();
        this.zzbub = zzb;
        zzatx zzc = com_google_android_gms_internal_zzaui.zzc(this);
        zzc.initialize();
        this.zzbuc = zzc;
        zzKl().zzMc().zzj("App measurement is starting up, version", Long.valueOf(zzKn().zzKv()));
        zzKn().zzLg();
        zzKl().zzMc().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzaut zzj = com_google_android_gms_internal_zzaui.zzj(this);
        zzj.initialize();
        this.zzbui = zzj;
        zzatl zzq = com_google_android_gms_internal_zzaui.zzq(this);
        zzq.initialize();
        this.zzbuo = zzq;
        zzatu zzr = com_google_android_gms_internal_zzaui.zzr(this);
        zzr.initialize();
        this.zzbuq = zzr;
        zzKn().zzLg();
        String zzke = zzr.zzke();
        if (zzKh().zzge(zzke)) {
            zzKl().zzMc().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
        } else {
            com.google.android.gms.internal.zzatx.zza zzMc = zzKl().zzMc();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzke = String.valueOf(zzke);
            zzMc.log(zzke.length() != 0 ? str.concat(zzke) : new String(str));
        }
        zzKl().zzMd().log("Debug-level message logging enabled");
        zzatj zzk = com_google_android_gms_internal_zzaui.zzk(this);
        zzk.initialize();
        this.zzbuj = zzk;
        zzatv zzl = com_google_android_gms_internal_zzaui.zzl(this);
        zzl.initialize();
        this.zzbuk = zzl;
        zzatf zzu = com_google_android_gms_internal_zzaui.zzu(this);
        zzu.initialize();
        this.zzbut = zzu;
        this.zzbuu = com_google_android_gms_internal_zzaui.zzv(this);
        zzaty zzm = com_google_android_gms_internal_zzaui.zzm(this);
        zzm.initialize();
        this.zzbul = zzm;
        zzauk zzo = com_google_android_gms_internal_zzaui.zzo(this);
        zzo.initialize();
        this.zzbum = zzo;
        zzaul zzp = com_google_android_gms_internal_zzaui.zzp(this);
        zzp.initialize();
        this.zzbun = zzp;
        zzauj zzi = com_google_android_gms_internal_zzaui.zzi(this);
        zzi.initialize();
        this.zzbup = zzi;
        zzaup zzt = com_google_android_gms_internal_zzaui.zzt(this);
        zzt.initialize();
        this.zzbus = zzt;
        this.zzbur = com_google_android_gms_internal_zzaui.zzs(this);
        this.zzbug = com_google_android_gms_internal_zzaui.zzh(this);
        this.zzbuh = com_google_android_gms_internal_zzaui.zzg(this);
        zzaun zze = com_google_android_gms_internal_zzaui.zze(this);
        zze.initialize();
        this.zzbue = zze;
        zzauc zzf = com_google_android_gms_internal_zzaui.zzf(this);
        zzf.initialize();
        this.zzbuf = zzf;
        zzaud zzd = com_google_android_gms_internal_zzaui.zzd(this);
        zzd.initialize();
        this.zzbud = zzd;
        if (this.zzbuB != this.zzbuC) {
            zzKl().zzLY().zze("Not all components initialized", Integer.valueOf(this.zzbuB), Integer.valueOf(this.zzbuC));
        }
        this.zzadP = true;
        this.zzbua.zzLg();
        if (this.mContext.getApplicationContext() instanceof Application) {
            int i = VERSION.SDK_INT;
            zzKa().zzMQ();
        } else {
            zzKl().zzMa().log("Application context is not an Application");
        }
        this.zzbud.zzm(new Runnable(this) {
            final /* synthetic */ zzaue zzbuF;

            {
                this.zzbuF = r1;
            }

            public void run() {
                this.zzbuF.start();
            }
        });
    }

    private boolean zzMH() {
        zzmR();
        zzob();
        return zzKg().zzLJ() || !TextUtils.isEmpty(zzKg().zzLD());
    }

    @WorkerThread
    private void zzMI() {
        zzmR();
        zzob();
        if (zzMM()) {
            long abs;
            if (this.zzbuE > 0) {
                abs = 3600000 - Math.abs(zznR().elapsedRealtime() - this.zzbuE);
                if (abs > 0) {
                    zzKl().zzMe().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    zzMz().unregister();
                    zzMA().cancel();
                    return;
                }
                this.zzbuE = 0;
            }
            if (zzMt() && zzMH()) {
                abs = zzMJ();
                if (abs == 0) {
                    zzMz().unregister();
                    zzMA().cancel();
                    return;
                } else if (zzMy().zzqa()) {
                    long j = zzKm().zzbtb.get();
                    long zzLs = zzKn().zzLs();
                    if (!zzKh().zzh(j, zzLs)) {
                        abs = Math.max(abs, j + zzLs);
                    }
                    zzMz().unregister();
                    abs -= zznR().currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzKn().zzLw();
                        zzKm().zzbsZ.set(zznR().currentTimeMillis());
                    }
                    zzKl().zzMe().zzj("Upload scheduled in approximately ms", Long.valueOf(abs));
                    zzMA().zzy(abs);
                    return;
                } else {
                    zzMz().zzpX();
                    zzMA().cancel();
                    return;
                }
            }
            zzMz().unregister();
            zzMA().cancel();
        }
    }

    private long zzMJ() {
        long zzLu;
        long currentTimeMillis = zznR().currentTimeMillis();
        long zzLz = zzKn().zzLz();
        Object obj = (zzKg().zzLK() || zzKg().zzLE()) ? 1 : null;
        if (obj != null) {
            CharSequence zzLC = zzKn().zzLC();
            zzLu = (TextUtils.isEmpty(zzLC) || ".none.".equals(zzLC)) ? zzKn().zzLu() : zzKn().zzLv();
        } else {
            zzLu = zzKn().zzLt();
        }
        long j = zzKm().zzbsZ.get();
        long j2 = zzKm().zzbta.get();
        long max = Math.max(zzKg().zzLH(), zzKg().zzLI());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzLz;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzLu;
        }
        if (!zzKh().zzh(j, zzLu)) {
            currentTimeMillis = j + zzLu;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzKn().zzLB(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzKn().zzLA();
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
        if (com_google_android_gms_internal_zzatm.zzbrz == null) {
            return false;
        }
        Iterator it = com_google_android_gms_internal_zzatm.zzbrz.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzKi().zzab(com_google_android_gms_internal_zzatm.mAppId, com_google_android_gms_internal_zzatm.mName) && zzKg().zza(zzME(), com_google_android_gms_internal_zzatm.mAppId, false, false, false, false, false).zzbrr < ((long) zzKn().zzfl(com_google_android_gms_internal_zzatm.mAppId));
    }

    private com.google.android.gms.internal.zzauw.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzauw_zzgArr, zzb[] com_google_android_gms_internal_zzauw_zzbArr) {
        zzac.zzdr(str);
        return zzJZ().zza(str, com_google_android_gms_internal_zzauw_zzbArr, com_google_android_gms_internal_zzauw_zzgArr);
    }

    public static zzaue zzbM(Context context) {
        zzac.zzw(context);
        zzac.zzw(context.getApplicationContext());
        if (zzbtZ == null) {
            synchronized (zzaue.class) {
                if (zzbtZ == null) {
                    zzbtZ = new zzaui(context).zzMP();
                }
            }
        }
        return zzbtZ;
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
            com_google_android_gms_internal_zzatc.zzfd(zzKm().zzMh());
            com_google_android_gms_internal_zzatc.zzff(zzfH);
            zzfu = com_google_android_gms_internal_zzatc;
            obj2 = 1;
        } else if (!zzfH.equals(zzfu.zzKp())) {
            zzfu.zzff(zzfH);
            zzfu.zzfd(zzKm().zzMh());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL) || com_google_android_gms_internal_zzatd.zzbqL.equals(zzfu.getGmpAppId()))) {
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqL);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqT) || com_google_android_gms_internal_zzatd.zzbqT.equals(zzfu.zzKq()))) {
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqT);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqN == 0 || com_google_android_gms_internal_zzatd.zzbqN == zzfu.zzKv())) {
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqN);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbhN) || com_google_android_gms_internal_zzatd.zzbhN.equals(zzfu.zzmZ()))) {
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhN);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqS != zzfu.zzKt()) {
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqS);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqM == null || com_google_android_gms_internal_zzatd.zzbqM.equals(zzfu.zzKu()))) {
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqM);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqO != zzfu.zzKw()) {
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqO);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqQ != zzfu.zzKx()) {
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqQ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP) || com_google_android_gms_internal_zzatd.zzbqP.equals(zzfu.zzKI()))) {
            zzfu.zzfi(com_google_android_gms_internal_zzatd.zzbqP);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqU != zzfu.zzuW()) {
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqU);
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
            zzKg().zza(str, j, this.zzbuD, com_google_android_gms_internal_zzaue_zza);
            if (com_google_android_gms_internal_zzaue_zza.isEmpty()) {
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzauw.zze com_google_android_gms_internal_zzauw_zze = com_google_android_gms_internal_zzaue_zza.zzbuG;
            com_google_android_gms_internal_zzauw_zze.zzbxg = new zzb[com_google_android_gms_internal_zzaue_zza.zzth.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                boolean z2;
                Object obj;
                if (zzKi().zzaa(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                    zzKl().zzMa().zze("Dropping blacklisted raw event. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS), ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    obj = (zzKh().zzgg(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS) || zzKh().zzgh(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS)) ? 1 : null;
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
                    boolean zzab = zzKi().zzab(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    if (zzab || zzKh().zzgi(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr;
                        zzc com_google_android_gms_internal_zzauw_zzc;
                        zzb com_google_android_gms_internal_zzauw_zzb;
                        Object obj2 = null;
                        Object obj3 = null;
                        if (((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY == null) {
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr2 = ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY;
                        int length = com_google_android_gms_internal_zzauw_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            zzc com_google_android_gms_internal_zzauw_zzc2 = com_google_android_gms_internal_zzauw_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxc = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxc = Long.valueOf(1);
                                obj = 1;
                            } else {
                                obj = obj3;
                            }
                            i5++;
                            obj3 = obj;
                        }
                        if (obj2 == null && zzab) {
                            zzKl().zzMe().zzj("Marking event as conversion", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_c";
                            com_google_android_gms_internal_zzauw_zzc.zzbxc = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        if (obj3 == null) {
                            zzKl().zzMe().zzj("Marking event as real-time", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_r";
                            com_google_android_gms_internal_zzauw_zzc.zzbxc = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbwY = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        boolean z4 = true;
                        if (zzKg().zza(zzME(), com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS, false, false, false, false, true).zzbrr > ((long) zzKn().zzfl(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS))) {
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzauw_zzb.zzbwY.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzauw_zzb.zzbwY[i4].name)) {
                                    obj3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbwY.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbwY, 0, obj3, 0, i4);
                                    }
                                    if (i4 < obj3.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbwY, i4 + 1, obj3, i4, obj3.length - i4);
                                    }
                                    com_google_android_gms_internal_zzauw_zzb.zzbwY = obj3;
                                    z4 = z;
                                } else {
                                    i4++;
                                }
                            }
                            z4 = z;
                        }
                        if (zzaut.zzfT(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name) && zzab && zzKg().zza(zzME(), com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS, false, false, true, false, false).zzbrp > ((long) zzKn().zzfk(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS))) {
                            zzKl().zzMa().zzj("Too many conversions. Not logging as conversion. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS));
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            Object obj4 = null;
                            zzc com_google_android_gms_internal_zzauw_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzauw_zzcArr3 = com_google_android_gms_internal_zzauw_zzb.zzbwY;
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
                                com_google_android_gms_internal_zzauw_zzcArr3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbwY.length - 1)];
                                int i8 = 0;
                                zzc[] com_google_android_gms_internal_zzauw_zzcArr4 = com_google_android_gms_internal_zzauw_zzb.zzbwY;
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
                                com_google_android_gms_internal_zzauw_zzb.zzbwY = com_google_android_gms_internal_zzauw_zzcArr3;
                                z3 = z4;
                            } else if (com_google_android_gms_internal_zzauw_zzc3 != null) {
                                com_google_android_gms_internal_zzauw_zzc3.name = "_err";
                                com_google_android_gms_internal_zzauw_zzc3.zzbxc = Long.valueOf(10);
                                z3 = z4;
                            } else {
                                zzKl().zzLY().zzj("Did not find conversion parameter. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS));
                            }
                        }
                        z3 = z4;
                    } else {
                        z3 = z;
                    }
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zze.zzbxg[i2] = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                    i = i4;
                    z2 = z3;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                com_google_android_gms_internal_zzauw_zze.zzbxg = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzauw_zze.zzbxg, i2);
            }
            com_google_android_gms_internal_zzauw_zze.zzbxz = zza(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS, com_google_android_gms_internal_zzaue_zza.zzbuG.zzbxh, com_google_android_gms_internal_zzauw_zze.zzbxg);
            com_google_android_gms_internal_zzauw_zze.zzbxj = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzauw_zze.zzbxk = Long.valueOf(Long.MIN_VALUE);
            for (zzb com_google_android_gms_internal_zzauw_zzb2 : com_google_android_gms_internal_zzauw_zze.zzbxg) {
                if (com_google_android_gms_internal_zzauw_zzb2.zzbwZ.longValue() < com_google_android_gms_internal_zzauw_zze.zzbxj.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxj = com_google_android_gms_internal_zzauw_zzb2.zzbwZ;
                }
                if (com_google_android_gms_internal_zzauw_zzb2.zzbwZ.longValue() > com_google_android_gms_internal_zzauw_zze.zzbxk.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxk = com_google_android_gms_internal_zzauw_zzb2.zzbwZ;
                }
            }
            String str2 = com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS;
            zzatc zzfu = zzKg().zzfu(str2);
            if (zzfu == null) {
                zzKl().zzLY().zzj("Bundling raw events w/o app info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS));
            } else if (com_google_android_gms_internal_zzauw_zze.zzbxg.length > 0) {
                long zzKs = zzfu.zzKs();
                com_google_android_gms_internal_zzauw_zze.zzbxm = zzKs != 0 ? Long.valueOf(zzKs) : null;
                long zzKr = zzfu.zzKr();
                if (zzKr != 0) {
                    zzKs = zzKr;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxl = zzKs != 0 ? Long.valueOf(zzKs) : null;
                zzfu.zzKB();
                com_google_android_gms_internal_zzauw_zze.zzbxx = Integer.valueOf((int) zzfu.zzKy());
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxj.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxk.longValue());
                com_google_android_gms_internal_zzauw_zze.zzbqP = zzfu.zzKJ();
                zzKg().zza(zzfu);
            }
            if (com_google_android_gms_internal_zzauw_zze.zzbxg.length > 0) {
                zzKn().zzLg();
                zzauv.zzb zzfL = zzKi().zzfL(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS);
                if (zzfL != null && zzfL.zzbwN != null) {
                    com_google_android_gms_internal_zzauw_zze.zzbxE = zzfL.zzbwN;
                } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzaue_zza.zzbuG.zzbqL)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxE = Long.valueOf(-1);
                } else {
                    zzKl().zzMa().zzj("Did not find measurement config or missing version info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuG.zzaS));
                }
                zzKg().zza(com_google_android_gms_internal_zzauw_zze, z);
            }
            zzKg().zzJ(com_google_android_gms_internal_zzaue_zza.zzbuH);
            zzKg().zzfB(str2);
            zzKg().setTransactionSuccessful();
            boolean z5 = com_google_android_gms_internal_zzauw_zze.zzbxg.length > 0;
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
        if (zzKn().zzLh()) {
            return false;
        }
        Boolean zzLi = zzKn().zzLi();
        if (zzLi != null) {
            z = zzLi.booleanValue();
        } else if (!zzKn().zzwR()) {
            z = true;
        }
        return zzKm().zzaL(z);
    }

    @WorkerThread
    protected void start() {
        zzmR();
        zzKg().zzLF();
        if (zzKm().zzbsZ.get() == 0) {
            zzKm().zzbsZ.set(zznR().currentTimeMillis());
        }
        if (zzMt()) {
            zzKn().zzLg();
            if (!TextUtils.isEmpty(zzKb().getGmpAppId())) {
                String zzMk = zzKm().zzMk();
                if (zzMk == null) {
                    zzKm().zzfI(zzKb().getGmpAppId());
                } else if (!zzMk.equals(zzKb().getGmpAppId())) {
                    zzKl().zzMc().log("Rechecking which service to use due to a GMP App Id change");
                    zzKm().zzMn();
                    this.zzbun.disconnect();
                    this.zzbun.zzoD();
                    zzKm().zzfI(zzKb().getGmpAppId());
                }
            }
            zzKn().zzLg();
            if (!TextUtils.isEmpty(zzKb().getGmpAppId())) {
                zzKa().zzMR();
            }
        } else if (isEnabled()) {
            if (!zzKh().zzbW("android.permission.INTERNET")) {
                zzKl().zzLY().log("App is missing INTERNET permission");
            }
            if (!zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE")) {
                zzKl().zzLY().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzKn().zzLg();
            if (!zzadg.zzbi(getContext()).zzzx()) {
                if (!zzaub.zzi(getContext(), false)) {
                    zzKl().zzLY().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzaum.zzj(getContext(), false)) {
                    zzKl().zzLY().log("AppMeasurementService not registered/enabled");
                }
            }
            zzKl().zzLY().log("Uploading is not possible. App measurement disabled");
        }
        zzMI();
    }

    void zzJV() {
        zzKn().zzLg();
        throw new IllegalStateException("Unexpected call on client side");
    }

    void zzJW() {
        zzKn().zzLg();
    }

    public zzatb zzJY() {
        zza(this.zzbuu);
        return this.zzbuu;
    }

    public zzatf zzJZ() {
        zza(this.zzbut);
        return this.zzbut;
    }

    protected void zzK(List<Long> list) {
        zzac.zzax(!list.isEmpty());
        if (this.zzbuA != null) {
            zzKl().zzLY().log("Set uploading progress before finishing the previous upload");
        } else {
            this.zzbuA = new ArrayList(list);
        }
    }

    public zzauj zzKa() {
        zza(this.zzbup);
        return this.zzbup;
    }

    public zzatu zzKb() {
        zza(this.zzbuq);
        return this.zzbuq;
    }

    public zzatl zzKc() {
        zza(this.zzbuo);
        return this.zzbuo;
    }

    public zzaul zzKd() {
        zza(this.zzbun);
        return this.zzbun;
    }

    public zzauk zzKe() {
        zza(this.zzbum);
        return this.zzbum;
    }

    public zzatv zzKf() {
        zza(this.zzbuk);
        return this.zzbuk;
    }

    public zzatj zzKg() {
        zza(this.zzbuj);
        return this.zzbuj;
    }

    public zzaut zzKh() {
        zza(this.zzbui);
        return this.zzbui;
    }

    public zzauc zzKi() {
        zza(this.zzbuf);
        return this.zzbuf;
    }

    public zzaun zzKj() {
        zza(this.zzbue);
        return this.zzbue;
    }

    public zzaud zzKk() {
        zza(this.zzbud);
        return this.zzbud;
    }

    public zzatx zzKl() {
        zza(this.zzbuc);
        return this.zzbuc;
    }

    public zzaua zzKm() {
        zza(this.zzbub);
        return this.zzbub;
    }

    public zzati zzKn() {
        return this.zzbua;
    }

    public zzaup zzMA() {
        zza(this.zzbus);
        return this.zzbus;
    }

    FileChannel zzMB() {
        return this.zzbuz;
    }

    @WorkerThread
    void zzMC() {
        zzmR();
        zzob();
        if (zzMM() && zzMD()) {
            zzy(zza(zzMB()), zzKb().zzLX());
        }
    }

    @WorkerThread
    boolean zzMD() {
        zzmR();
        try {
            this.zzbuz = new RandomAccessFile(new File(getContext().getFilesDir(), this.zzbuj.zzow()), "rw").getChannel();
            this.zzbuy = this.zzbuz.tryLock();
            if (this.zzbuy != null) {
                zzKl().zzMe().log("Storage concurrent access okay");
                return true;
            }
            zzKl().zzLY().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzKl().zzLY().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzKl().zzLY().zzj("Failed to access storage lock file", e2);
        }
    }

    long zzME() {
        return ((((zznR().currentTimeMillis() + zzKm().zzMi()) / 1000) / 60) / 60) / 24;
    }

    @WorkerThread
    protected boolean zzMF() {
        zzmR();
        return this.zzbuA != null;
    }

    @WorkerThread
    public void zzMG() {
        int i = 0;
        zzmR();
        zzob();
        zzKn().zzLg();
        Boolean zzMm = zzKm().zzMm();
        if (zzMm == null) {
            zzKl().zzMa().log("Upload data called on the client side before use of service was decided");
        } else if (zzMm.booleanValue()) {
            zzKl().zzLY().log("Upload called in the client side when service should be used");
        } else if (this.zzbuE > 0) {
            zzMI();
        } else if (zzMF()) {
            zzKl().zzMa().log("Uploading requested multiple times");
        } else if (zzMy().zzqa()) {
            long currentTimeMillis = zznR().currentTimeMillis();
            zzaq(currentTimeMillis - zzKn().zzLr());
            long j = zzKm().zzbsZ.get();
            if (j != 0) {
                zzKl().zzMd().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzLD = zzKg().zzLD();
            Object zzao;
            if (TextUtils.isEmpty(zzLD)) {
                this.zzbuD = -1;
                zzao = zzKg().zzao(currentTimeMillis - zzKn().zzLr());
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
            if (this.zzbuD == -1) {
                this.zzbuD = zzKg().zzLL();
            }
            List<Pair> zzn = zzKg().zzn(zzLD, zzKn().zzfq(zzLD), zzKn().zzfr(zzLD));
            if (!zzn.isEmpty()) {
                zzauw.zze com_google_android_gms_internal_zzauw_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzauw_zze = (zzauw.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxt)) {
                        obj = com_google_android_gms_internal_zzauw_zze.zzbxt;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzauw_zze = (zzauw.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxt) && !com_google_android_gms_internal_zzauw_zze.zzbxt.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzauw_zzd = new zzd();
                com_google_android_gms_internal_zzauw_zzd.zzbxd = new zzauw.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzauw_zzd.zzbxd.length) {
                    com_google_android_gms_internal_zzauw_zzd.zzbxd[i] = (zzauw.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzauw_zzd.zzbxd[i].zzbxs = Long.valueOf(zzKn().zzKv());
                    com_google_android_gms_internal_zzauw_zzd.zzbxd[i].zzbxi = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzauw_zzd.zzbxd[i].zzbxy = Boolean.valueOf(zzKn().zzLg());
                    i++;
                }
                zzao = zzKl().zzak(2) ? zzaut.zzb(com_google_android_gms_internal_zzauw_zzd) : null;
                byte[] zza = zzKh().zza(com_google_android_gms_internal_zzauw_zzd);
                String zzLq = zzKn().zzLq();
                try {
                    URL url = new URL(zzLq);
                    zzK(arrayList);
                    zzKm().zzbta.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzauw_zzd.zzbxd.length > 0) {
                        obj2 = com_google_android_gms_internal_zzauw_zzd.zzbxd[0].zzaS;
                    }
                    zzKl().zzMe().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzao);
                    zzMy().zza(zzLD, url, zza, null, new zza(this) {
                        final /* synthetic */ zzaue zzbuF;

                        {
                            this.zzbuF = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.zzbuF.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e) {
                    zzKl().zzLY().zze("Failed to parse upload URL. Not uploading. appId", zzatx.zzfE(zzLD), zzLq);
                }
            }
        } else {
            zzKl().zzMa().log("Network not connected, ignoring upload request");
            zzMI();
        }
    }

    void zzMK() {
        this.zzbuC++;
    }

    @WorkerThread
    void zzML() {
        zzmR();
        zzob();
        if (!this.zzbuv) {
            zzKl().zzMc().log("This instance being marked as an uploader");
            zzMC();
        }
        this.zzbuv = true;
    }

    @WorkerThread
    boolean zzMM() {
        zzmR();
        zzob();
        return this.zzbuv;
    }

    @WorkerThread
    protected boolean zzMt() {
        boolean z = false;
        zzob();
        zzmR();
        if (this.zzbuw == null || this.zzbux == 0 || !(this.zzbuw == null || this.zzbuw.booleanValue() || Math.abs(zznR().elapsedRealtime() - this.zzbux) <= 1000)) {
            this.zzbux = zznR().elapsedRealtime();
            zzKn().zzLg();
            if (zzKh().zzbW("android.permission.INTERNET") && zzKh().zzbW("android.permission.ACCESS_NETWORK_STATE") && (zzadg.zzbi(getContext()).zzzx() || (zzaub.zzi(getContext(), false) && zzaum.zzj(getContext(), false)))) {
                z = true;
            }
            this.zzbuw = Boolean.valueOf(z);
            if (this.zzbuw.booleanValue()) {
                this.zzbuw = Boolean.valueOf(zzKh().zzga(zzKb().getGmpAppId()));
            }
        }
        return this.zzbuw.booleanValue();
    }

    public zzatx zzMu() {
        return (this.zzbuc == null || !this.zzbuc.isInitialized()) ? null : this.zzbuc;
    }

    zzaud zzMv() {
        return this.zzbud;
    }

    public AppMeasurement zzMw() {
        return this.zzbug;
    }

    public FirebaseAnalytics zzMx() {
        return this.zzbuh;
    }

    public zzaty zzMy() {
        zza(this.zzbul);
        return this.zzbul;
    }

    public zzatz zzMz() {
        if (this.zzbur != null) {
            return this.zzbur;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public void zzW(boolean z) {
        zzMI();
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKl().zzLY().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzKl().zzMa().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzKl().zzLY().zzj("Failed to read from channel", e);
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
        List<Long> list = this.zzbuA;
        this.zzbuA = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            try {
                zzKm().zzbsZ.set(zznR().currentTimeMillis());
                zzKm().zzbta.set(0);
                zzMI();
                zzKl().zzMe().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzKg().beginTransaction();
                for (Long longValue : list) {
                    zzKg().zzan(longValue.longValue());
                }
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                if (zzMy().zzqa() && zzMH()) {
                    zzMG();
                } else {
                    this.zzbuD = -1;
                    zzMI();
                }
                this.zzbuE = 0;
                return;
            } catch (SQLiteException e) {
                zzKl().zzLY().zzj("Database error while trying to delete uploaded bundles", e);
                this.zzbuE = zznR().elapsedRealtime();
                zzKl().zzMe().zzj("Disable upload, time", Long.valueOf(this.zzbuE));
                return;
            } catch (Throwable th2) {
                zzKg().endTransaction();
            }
        }
        zzKl().zzMe().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
        zzKm().zzbta.set(zznR().currentTimeMillis());
        if (i == 503 || i == 429) {
            i2 = 1;
        }
        if (i2 != 0) {
            zzKm().zzbtb.set(zznR().currentTimeMillis());
        }
        zzMI();
    }

    @WorkerThread
    void zza(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || zzfu.getGmpAppId() == null || zzfu.getGmpAppId().equals(com_google_android_gms_internal_zzatd.zzbqL))) {
            zzKl().zzMa().zzj("New GMP App Id passed in. Removing cached database data. appId", zzatx.zzfE(zzfu.zzke()));
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
        zzac.zzax(com_google_android_gms_internal_zzatm.mAppId.equals(com_google_android_gms_internal_zzatd.packageName));
        zzauw.zze com_google_android_gms_internal_zzauw_zze = new zzauw.zze();
        com_google_android_gms_internal_zzauw_zze.zzbxf = Integer.valueOf(1);
        com_google_android_gms_internal_zzauw_zze.zzbxn = "android";
        com_google_android_gms_internal_zzauw_zze.zzaS = com_google_android_gms_internal_zzatd.packageName;
        com_google_android_gms_internal_zzauw_zze.zzbqM = com_google_android_gms_internal_zzatd.zzbqM;
        com_google_android_gms_internal_zzauw_zze.zzbhN = com_google_android_gms_internal_zzatd.zzbhN;
        com_google_android_gms_internal_zzauw_zze.zzbxA = Integer.valueOf((int) com_google_android_gms_internal_zzatd.zzbqS);
        com_google_android_gms_internal_zzauw_zze.zzbxr = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqN);
        com_google_android_gms_internal_zzauw_zze.zzbqL = com_google_android_gms_internal_zzatd.zzbqL;
        com_google_android_gms_internal_zzauw_zze.zzbxw = com_google_android_gms_internal_zzatd.zzbqO == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzatd.zzbqO);
        Pair zzfG = zzKm().zzfG(com_google_android_gms_internal_zzatd.packageName);
        if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
            com_google_android_gms_internal_zzauw_zze.zzbxt = (String) zzfG.first;
            com_google_android_gms_internal_zzauw_zze.zzbxu = (Boolean) zzfG.second;
        } else if (!zzKc().zzbL(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzKl().zzMa().zzj("null secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
                string = "null";
            } else if (string.isEmpty()) {
                zzKl().zzMa().zzj("empty secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
            }
            com_google_android_gms_internal_zzauw_zze.zzbxD = string;
        }
        com_google_android_gms_internal_zzauw_zze.zzbxo = zzKc().zzkN();
        com_google_android_gms_internal_zzauw_zze.zzbb = zzKc().zzLS();
        com_google_android_gms_internal_zzauw_zze.zzbxq = Integer.valueOf((int) zzKc().zzLT());
        com_google_android_gms_internal_zzauw_zze.zzbxp = zzKc().zzLU();
        com_google_android_gms_internal_zzauw_zze.zzbxs = null;
        com_google_android_gms_internal_zzauw_zze.zzbxi = null;
        com_google_android_gms_internal_zzauw_zze.zzbxj = null;
        com_google_android_gms_internal_zzauw_zze.zzbxk = null;
        com_google_android_gms_internal_zzauw_zze.zzbxF = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqU);
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (zzfu == null) {
            zzfu = new zzatc(this, com_google_android_gms_internal_zzatd.packageName);
            zzfu.zzfd(zzKm().zzMh());
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqT);
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqL);
            zzfu.zzff(zzKm().zzfH(com_google_android_gms_internal_zzatd.packageName));
            zzfu.zzad(0);
            zzfu.zzY(0);
            zzfu.zzZ(0);
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhN);
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqS);
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqM);
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqN);
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqO);
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqQ);
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqU);
            zzKg().zza(zzfu);
        }
        com_google_android_gms_internal_zzauw_zze.zzbxv = zzfu.getAppInstanceId();
        com_google_android_gms_internal_zzauw_zze.zzbqT = zzfu.zzKq();
        List zzft = zzKg().zzft(com_google_android_gms_internal_zzatd.packageName);
        com_google_android_gms_internal_zzauw_zze.zzbxh = new zzg[zzft.size()];
        for (int i = 0; i < zzft.size(); i++) {
            zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
            com_google_android_gms_internal_zzauw_zze.zzbxh[i] = com_google_android_gms_internal_zzauw_zzg;
            com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
            com_google_android_gms_internal_zzauw_zzg.zzbxJ = Long.valueOf(((zzaus) zzft.get(i)).zzbwg);
            zzKh().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
        }
        try {
            if (zzKg().zza(com_google_android_gms_internal_zzatm, zzKg().zza(com_google_android_gms_internal_zzauw_zze), zza(com_google_android_gms_internal_zzatm))) {
                this.zzbuE = 0;
            }
        } catch (IOException e) {
            zzKl().zzLY().zze("Data loss. Failed to insert raw event metadata. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKl().zzLY().log("Bad chanel to read from");
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
            zzKl().zzLY().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzKl().zzLY().zzj("Failed to write to channel", e);
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
                zzKl().zzMd().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzfu.zzKx()) {
                long j;
                zzauw.zze com_google_android_gms_internal_zzauw_zze = new zzauw.zze();
                com_google_android_gms_internal_zzauw_zzd.zzbxd = new zzauw.zze[]{com_google_android_gms_internal_zzauw_zze};
                com_google_android_gms_internal_zzauw_zze.zzbxf = Integer.valueOf(1);
                com_google_android_gms_internal_zzauw_zze.zzbxn = "android";
                com_google_android_gms_internal_zzauw_zze.zzaS = zzfu.zzke();
                com_google_android_gms_internal_zzauw_zze.zzbqM = zzfu.zzKu();
                com_google_android_gms_internal_zzauw_zze.zzbhN = zzfu.zzmZ();
                com_google_android_gms_internal_zzauw_zze.zzbxA = Integer.valueOf((int) zzfu.zzKt());
                com_google_android_gms_internal_zzauw_zze.zzbxr = Long.valueOf(zzfu.zzKv());
                com_google_android_gms_internal_zzauw_zze.zzbqL = zzfu.getGmpAppId();
                com_google_android_gms_internal_zzauw_zze.zzbxw = Long.valueOf(zzfu.zzKw());
                Pair zzfG = zzKm().zzfG(zzfu.zzke());
                if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxt = (String) zzfG.first;
                    com_google_android_gms_internal_zzauw_zze.zzbxu = (Boolean) zzfG.second;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxo = zzKc().zzkN();
                com_google_android_gms_internal_zzauw_zze.zzbb = zzKc().zzLS();
                com_google_android_gms_internal_zzauw_zze.zzbxq = Integer.valueOf((int) zzKc().zzLT());
                com_google_android_gms_internal_zzauw_zze.zzbxp = zzKc().zzLU();
                com_google_android_gms_internal_zzauw_zze.zzbxv = zzfu.getAppInstanceId();
                com_google_android_gms_internal_zzauw_zze.zzbqT = zzfu.zzKq();
                List zzft = zzKg().zzft(zzfu.zzke());
                com_google_android_gms_internal_zzauw_zze.zzbxh = new zzg[zzft.size()];
                for (int i = 0; i < zzft.size(); i++) {
                    zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
                    com_google_android_gms_internal_zzauw_zze.zzbxh[i] = com_google_android_gms_internal_zzauw_zzg;
                    com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
                    com_google_android_gms_internal_zzauw_zzg.zzbxJ = Long.valueOf(((zzaus) zzft.get(i)).zzbwg);
                    zzKh().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
                }
                Bundle zzLW = com_google_android_gms_internal_zzatq.zzbrG.zzLW();
                if ("_iap".equals(com_google_android_gms_internal_zzatq.name)) {
                    zzLW.putLong("_c", 1);
                    zzKl().zzMd().log("Marking in-app purchase as real-time");
                    zzLW.putLong("_r", 1);
                }
                zzLW.putString("_o", com_google_android_gms_internal_zzatq.zzbqV);
                if (zzKh().zzge(com_google_android_gms_internal_zzauw_zze.zzaS)) {
                    zzKh().zza(zzLW, "_dbg", Long.valueOf(1));
                    zzKh().zza(zzLW, "_r", Long.valueOf(1));
                }
                zzatn zzQ = zzKg().zzQ(str, com_google_android_gms_internal_zzatq.name);
                if (zzQ == null) {
                    zzKg().zza(new zzatn(str, com_google_android_gms_internal_zzatq.name, 1, 0, com_google_android_gms_internal_zzatq.zzbrH));
                    j = 0;
                } else {
                    j = zzQ.zzbrC;
                    zzKg().zza(zzQ.zzap(com_google_android_gms_internal_zzatq.zzbrH).zzLV());
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqV, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrH, j, zzLW);
                zzb com_google_android_gms_internal_zzauw_zzb = new zzb();
                com_google_android_gms_internal_zzauw_zze.zzbxg = new zzb[]{com_google_android_gms_internal_zzauw_zzb};
                com_google_android_gms_internal_zzauw_zzb.zzbwZ = Long.valueOf(com_google_android_gms_internal_zzatm.zzaxb);
                com_google_android_gms_internal_zzauw_zzb.name = com_google_android_gms_internal_zzatm.mName;
                com_google_android_gms_internal_zzauw_zzb.zzbxa = Long.valueOf(com_google_android_gms_internal_zzatm.zzbry);
                com_google_android_gms_internal_zzauw_zzb.zzbwY = new zzc[com_google_android_gms_internal_zzatm.zzbrz.size()];
                Iterator it = com_google_android_gms_internal_zzatm.zzbrz.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzauw_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zzb.zzbwY[i2] = com_google_android_gms_internal_zzauw_zzc;
                    com_google_android_gms_internal_zzauw_zzc.name = str2;
                    zzKh().zza(com_google_android_gms_internal_zzauw_zzc, com_google_android_gms_internal_zzatm.zzbrz.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxz = zza(zzfu.zzke(), com_google_android_gms_internal_zzauw_zze.zzbxh, com_google_android_gms_internal_zzauw_zze.zzbxg);
                com_google_android_gms_internal_zzauw_zze.zzbxj = com_google_android_gms_internal_zzauw_zzb.zzbwZ;
                com_google_android_gms_internal_zzauw_zze.zzbxk = com_google_android_gms_internal_zzauw_zzb.zzbwZ;
                long zzKs = zzfu.zzKs();
                com_google_android_gms_internal_zzauw_zze.zzbxm = zzKs != 0 ? Long.valueOf(zzKs) : null;
                long zzKr = zzfu.zzKr();
                if (zzKr != 0) {
                    zzKs = zzKr;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxl = zzKs != 0 ? Long.valueOf(zzKs) : null;
                zzfu.zzKB();
                com_google_android_gms_internal_zzauw_zze.zzbxx = Integer.valueOf((int) zzfu.zzKy());
                com_google_android_gms_internal_zzauw_zze.zzbxs = Long.valueOf(zzKn().zzKv());
                com_google_android_gms_internal_zzauw_zze.zzbxi = Long.valueOf(zznR().currentTimeMillis());
                com_google_android_gms_internal_zzauw_zze.zzbxy = Boolean.TRUE;
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxj.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxk.longValue());
                zzKg().zza(zzfu);
                zzKg().setTransactionSuccessful();
                zzKg().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzauw_zzd.zzaeT()];
                    zzbxm zzag = zzbxm.zzag(bArr);
                    com_google_android_gms_internal_zzauw_zzd.zza(zzag);
                    zzag.zzaeG();
                    return zzKh().zzk(bArr);
                } catch (IOException e) {
                    zzKl().zzLY().zze("Data loss. Failed to bundle and serialize. appId", zzatx.zzfE(str), e);
                    return null;
                }
            } else {
                zzKl().zzMd().zzj("Log and bundle disabled. package_name", str);
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
            zzKl().zzMe().zzj("Fetching remote configuration", com_google_android_gms_internal_zzatc.zzke());
            zzauv.zzb zzfL = zzKi().zzfL(com_google_android_gms_internal_zzatc.zzke());
            CharSequence zzfM = zzKi().zzfM(com_google_android_gms_internal_zzatc.zzke());
            if (!(zzfL == null || TextUtils.isEmpty(zzfM))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzfM);
            }
            zzMy().zza(com_google_android_gms_internal_zzatc.zzke(), url, map, new zza(this) {
                final /* synthetic */ zzaue zzbuF;

                {
                    this.zzbuF = r1;
                }

                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                    this.zzbuF.zzb(str, i, th, bArr, map);
                }
            });
        } catch (MalformedURLException e) {
            zzKl().zzLY().zze("Failed to parse config URL. Not fetching. appId", zzatx.zzfE(com_google_android_gms_internal_zzatc.zzke()), zzP);
        }
    }

    @WorkerThread
    void zzb(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzmR();
        zzob();
        zzatc zzfu = zzKg().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || !TextUtils.isEmpty(zzfu.getGmpAppId()) || com_google_android_gms_internal_zzatd == null || TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL))) {
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
            zzKl().zzLY().zzj("PackageManager is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName));
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = zzadg.zzbi(getContext()).getPackageInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e) {
                zzKl().zzLY().zze("Package info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzKl().zzLY().zze("Application info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e2);
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
    void zzb(zzatg com_google_android_gms_internal_zzatg, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzdr(com_google_android_gms_internal_zzatg.packageName);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqV);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqW);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbqW.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            if (com_google_android_gms_internal_zzatd.zzbqQ) {
                zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
                zzKg().beginTransaction();
                try {
                    Object obj;
                    zzatg zzT = zzKg().zzT(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqW.name);
                    if (zzT != null && zzT.zzbqY) {
                        com_google_android_gms_internal_zzatg2.zzbqV = zzT.zzbqV;
                        com_google_android_gms_internal_zzatg2.zzbqX = zzT.zzbqX;
                        com_google_android_gms_internal_zzatg2.zzbqZ = zzT.zzbqZ;
                        com_google_android_gms_internal_zzatg2.zzbrc = zzT.zzbrc;
                        obj = null;
                    } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzatg2.zzbqZ)) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg2.zzbqW;
                        com_google_android_gms_internal_zzatg2.zzbqW = new zzauq(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzatg2.zzbqX, com_google_android_gms_internal_zzauq.getValue(), com_google_android_gms_internal_zzauq.zzbqV);
                        com_google_android_gms_internal_zzatg2.zzbqY = true;
                        int i = 1;
                    } else {
                        obj = null;
                    }
                    if (com_google_android_gms_internal_zzatg2.zzbqY) {
                        zzauq com_google_android_gms_internal_zzauq2 = com_google_android_gms_internal_zzatg2.zzbqW;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqV, com_google_android_gms_internal_zzauq2.name, com_google_android_gms_internal_zzauq2.zzbwc, com_google_android_gms_internal_zzauq2.getValue());
                        if (zzKg().zza(com_google_android_gms_internal_zzaus)) {
                            zzKl().zzMd().zzd("User property updated immediately", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLY().zzd("(2)Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (!(obj == null || com_google_android_gms_internal_zzatg2.zzbrc == null)) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg2.zzbrc, com_google_android_gms_internal_zzatg2.zzbqX), com_google_android_gms_internal_zzatd);
                        }
                    }
                    if (zzKg().zza(com_google_android_gms_internal_zzatg2)) {
                        zzKl().zzMd().zzd("Conditional property added", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqW.name, com_google_android_gms_internal_zzatg2.zzbqW.getValue());
                    } else {
                        zzKl().zzLY().zzd("Too many conditional properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzatg2.zzbqW.name, com_google_android_gms_internal_zzatg2.zzbqW.getValue());
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
        long j = com_google_android_gms_internal_zzatq.zzbrH;
        if (!zzKh().zzd(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd)) {
            return;
        }
        if (com_google_android_gms_internal_zzatd.zzbqQ) {
            zzKg().beginTransaction();
            try {
                for (zzatg com_google_android_gms_internal_zzatg : zzKg().zzh(str, j)) {
                    if (com_google_android_gms_internal_zzatg != null) {
                        zzKl().zzMd().zzd("User property timed out", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqW.name, com_google_android_gms_internal_zzatg.zzbqW.getValue());
                        if (com_google_android_gms_internal_zzatg.zzbra != null) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg.zzbra, j), com_google_android_gms_internal_zzatd);
                        }
                        zzKg().zzU(str, com_google_android_gms_internal_zzatg.zzbqW.name);
                    }
                }
                List<zzatg> zzi = zzKg().zzi(str, j);
                List<zzatq> arrayList = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg2 : zzi) {
                    if (com_google_android_gms_internal_zzatg2 != null) {
                        zzKl().zzMd().zzd("User property expired", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqW.name, com_google_android_gms_internal_zzatg2.zzbqW.getValue());
                        zzKg().zzR(str, com_google_android_gms_internal_zzatg2.zzbqW.name);
                        if (com_google_android_gms_internal_zzatg2.zzbre != null) {
                            arrayList.add(com_google_android_gms_internal_zzatg2.zzbre);
                        }
                        zzKg().zzU(str, com_google_android_gms_internal_zzatg2.zzbqW.name);
                    }
                }
                for (zzatq com_google_android_gms_internal_zzatq2 : arrayList) {
                    zzc(new zzatq(com_google_android_gms_internal_zzatq2, j), com_google_android_gms_internal_zzatd);
                }
                zzi = zzKg().zzc(str, com_google_android_gms_internal_zzatq.name, j);
                List<zzatq> arrayList2 = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg3 : zzi) {
                    if (com_google_android_gms_internal_zzatg3 != null) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg3.zzbqW;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzatg3.zzbqV, com_google_android_gms_internal_zzauq.name, j, com_google_android_gms_internal_zzauq.getValue());
                        if (zzKg().zza(com_google_android_gms_internal_zzaus)) {
                            zzKl().zzMd().zzd("User property triggered", com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLY().zzd("Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg3.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (com_google_android_gms_internal_zzatg3.zzbrc != null) {
                            arrayList2.add(com_google_android_gms_internal_zzatg3.zzbrc);
                        }
                        com_google_android_gms_internal_zzatg3.zzbqW = new zzauq(com_google_android_gms_internal_zzaus);
                        com_google_android_gms_internal_zzatg3.zzbqY = true;
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
            zzKl().zzMd().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKl().zzMa().zzj("App version does not match; dropping event. appId", zzatx.zzfE(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKl().zzMa().zzj("Could not find package. appId", zzatx.zzfE(str));
            }
        }
        zzatq com_google_android_gms_internal_zzatq2 = com_google_android_gms_internal_zzatq;
        zzb(com_google_android_gms_internal_zzatq2, new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), zzfu.zzKw(), null, zzfu.zzKx(), false, zzfu.zzKq(), zzfu.zzuW()));
    }

    void zzb(zzauh com_google_android_gms_internal_zzauh) {
        this.zzbuB++;
    }

    @WorkerThread
    void zzb(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        int i = 0;
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            if (com_google_android_gms_internal_zzatd.zzbqQ) {
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
                zzfX = zzKh().zzm(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzfX != 0) {
                    zza = zzKh().zza(com_google_android_gms_internal_zzauq.name, zzKn().zzKN(), true);
                    Object value = com_google_android_gms_internal_zzauq.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzKh().zza(zzfX, "_ev", zza, i);
                    return;
                }
                Object zzn = zzKh().zzn(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzn != null) {
                    zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.zzbqV, com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.zzbwc, zzn);
                    zzKl().zzMd().zze("Setting user property", com_google_android_gms_internal_zzaus.mName, zzn);
                    zzKg().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzatd);
                        boolean zza2 = zzKg().zza(com_google_android_gms_internal_zzaus);
                        zzKg().setTransactionSuccessful();
                        if (zza2) {
                            zzKl().zzMd().zze("User property set", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKl().zzLY().zze("Too many unique user properties are set. Ignoring user property", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
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
                zzKl().zzMa().zzj("App does not exist in onConfigFetched. appId", zzatx.zzfE(str));
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
                    zzKl().zzMb().zzj("Config not found. Using empty config. appId", str);
                } else {
                    zzKl().zzMe().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzMy().zzqa() && zzMH()) {
                    zzMG();
                } else {
                    zzMI();
                }
            } else {
                zzfu.zzaf(zznR().currentTimeMillis());
                zzKg().zza(zzfu);
                zzKl().zzMe().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzKi().zzfN(str);
                zzKm().zzbta.set(zznR().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzKm().zzbtb.set(zznR().currentTimeMillis());
                }
                zzMI();
            }
            zzKg().setTransactionSuccessful();
        } finally {
            zzKg().endTransaction();
        }
    }

    @WorkerThread
    void zzc(zzatd com_google_android_gms_internal_zzatd, long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("_et", 1);
        zzb(new zzatq("_e", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzc(zzatg com_google_android_gms_internal_zzatg, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzdr(com_google_android_gms_internal_zzatg.packageName);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqW);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbqW.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            if (com_google_android_gms_internal_zzatd.zzbqQ) {
                zzKg().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzatg zzT = zzKg().zzT(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqW.name);
                    if (zzT != null) {
                        zzKl().zzMd().zze("Removing conditional user property", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqW.name);
                        zzKg().zzU(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqW.name);
                        if (zzT.zzbqY) {
                            zzKg().zzR(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqW.name);
                        }
                        if (com_google_android_gms_internal_zzatg.zzbre != null) {
                            Bundle bundle = null;
                            if (com_google_android_gms_internal_zzatg.zzbre.zzbrG != null) {
                                bundle = com_google_android_gms_internal_zzatg.zzbre.zzbrG.zzLW();
                            }
                            zzc(zzKh().zza(com_google_android_gms_internal_zzatg.zzbre.name, bundle, zzT.zzbqV, com_google_android_gms_internal_zzatg.zzbre.zzbrH, true, false), com_google_android_gms_internal_zzatd);
                        }
                    } else {
                        zzKl().zzMa().zze("Conditional user property doesn't exist", zzatx.zzfE(com_google_android_gms_internal_zzatg.packageName), com_google_android_gms_internal_zzatg.zzbqW.name);
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
        if (!com_google_android_gms_internal_zzatd.zzbqQ) {
            zzf(com_google_android_gms_internal_zzatd);
        } else if (zzKi().zzaa(str, com_google_android_gms_internal_zzatq.name)) {
            zzKl().zzMa().zze("Dropping blacklisted event. appId", zzatx.zzfE(str), com_google_android_gms_internal_zzatq.name);
            r2 = (zzKh().zzgg(str) || zzKh().zzgh(str)) ? 1 : null;
            if (r2 == null && !"_err".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKh().zza(11, "_ev", com_google_android_gms_internal_zzatq.name, 0);
            }
            if (r2 != null) {
                zzatc zzfu = zzKg().zzfu(str);
                if (zzfu != null) {
                    if (Math.abs(zznR().currentTimeMillis() - Math.max(zzfu.zzKA(), zzfu.zzKz())) > zzKn().zzLl()) {
                        zzKl().zzMd().log("Fetching config for blacklisted app");
                        zzb(zzfu);
                    }
                }
            }
        } else {
            if (zzKl().zzak(2)) {
                zzKl().zzMe().zzj("Logging event", com_google_android_gms_internal_zzatq);
            }
            zzKg().beginTransaction();
            try {
                Bundle zzLW = com_google_android_gms_internal_zzatq.zzbrG.zzLW();
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
                            zzKl().zzMa().zze("Data lost. Currency value is too big. appId", zzatx.zzfE(str), Double.valueOf(d));
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
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqV, concat, zznR().currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqV, concat, zznR().currentTimeMillis(), Long.valueOf(round + ((Long) zzS.mValue).longValue()));
                            }
                            if (!zzKg().zza(zzS)) {
                                zzKl().zzLY().zzd("Too many unique user properties are set. Ignoring user property. appId", zzatx.zzfE(str), zzS.mName, zzS.mValue);
                                zzKh().zza(9, null, null, 0);
                            }
                        }
                    }
                }
                boolean zzfT = zzaut.zzfT(com_google_android_gms_internal_zzatq.name);
                boolean equals = "_err".equals(com_google_android_gms_internal_zzatq.name);
                com.google.android.gms.internal.zzatj.zza zza = zzKg().zza(zzME(), str, true, zzfT, false, equals, false);
                long zzKU = zza.zzbro - zzKn().zzKU();
                if (zzKU > 0) {
                    if (zzKU % 1000 == 1) {
                        zzKl().zzLY().zze("Data loss. Too many events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbro));
                    }
                    zzKh().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                    zzKg().setTransactionSuccessful();
                    return;
                }
                zzatn com_google_android_gms_internal_zzatn;
                if (zzfT) {
                    zzKU = zza.zzbrn - zzKn().zzKV();
                    if (zzKU > 0) {
                        if (zzKU % 1000 == 1) {
                            zzKl().zzLY().zze("Data loss. Too many public events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrn));
                        }
                        zzKh().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                        zzKg().setTransactionSuccessful();
                        zzKg().endTransaction();
                        return;
                    }
                }
                if (equals) {
                    zzKU = zza.zzbrq - ((long) zzKn().zzfj(com_google_android_gms_internal_zzatd.packageName));
                    if (zzKU > 0) {
                        if (zzKU == 1) {
                            zzKl().zzLY().zze("Too many error events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrq));
                        }
                        zzKg().setTransactionSuccessful();
                        zzKg().endTransaction();
                        return;
                    }
                }
                zzKh().zza(zzLW, "_o", com_google_android_gms_internal_zzatq.zzbqV);
                if (zzKh().zzge(str)) {
                    zzKh().zza(zzLW, "_dbg", Long.valueOf(1));
                    zzKh().zza(zzLW, "_r", Long.valueOf(1));
                }
                zzKU = zzKg().zzfv(str);
                if (zzKU > 0) {
                    zzKl().zzMa().zze("Data lost. Too many events stored on disk, deleted. appId", zzatx.zzfE(str), Long.valueOf(zzKU));
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqV, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrH, 0, zzLW);
                zzatn zzQ = zzKg().zzQ(str, com_google_android_gms_internal_zzatm.mName);
                if (zzQ == null) {
                    long zzfC = zzKg().zzfC(str);
                    zzKn().zzKT();
                    if (zzfC >= 500) {
                        zzKl().zzLY().zzd("Too many event names used, ignoring event. appId, name, supported count", zzatx.zzfE(str), com_google_android_gms_internal_zzatm.mName, Integer.valueOf(zzKn().zzKT()));
                        zzKh().zza(8, null, null, 0);
                        zzKg().endTransaction();
                        return;
                    }
                    com_google_android_gms_internal_zzatn = new zzatn(str, com_google_android_gms_internal_zzatm.mName, 0, 0, com_google_android_gms_internal_zzatm.zzaxb);
                } else {
                    com_google_android_gms_internal_zzatm = com_google_android_gms_internal_zzatm.zza(this, zzQ.zzbrC);
                    com_google_android_gms_internal_zzatn = zzQ.zzap(com_google_android_gms_internal_zzatm.zzaxb);
                }
                zzKg().zza(com_google_android_gms_internal_zzatn);
                zza(com_google_android_gms_internal_zzatm, com_google_android_gms_internal_zzatd);
                zzKg().setTransactionSuccessful();
                if (zzKl().zzak(2)) {
                    zzKl().zzMe().zzj("Event recorded", com_google_android_gms_internal_zzatm);
                }
                zzKg().endTransaction();
                zzMI();
                zzKl().zzMe().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
            } finally {
                zzKg().endTransaction();
            }
        }
    }

    @WorkerThread
    void zzc(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            if (com_google_android_gms_internal_zzatd.zzbqQ) {
                zzKl().zzMd().zzj("Removing user property", com_google_android_gms_internal_zzauq.name);
                zzKg().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzKg().zzR(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.name);
                    zzKg().setTransactionSuccessful();
                    zzKl().zzMd().zzj("User property removed", com_google_android_gms_internal_zzauq.name);
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
        zzb(new zzatq("_cd", new zzato(new Bundle()), "auto", j), com_google_android_gms_internal_zzatd);
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
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            if (com_google_android_gms_internal_zzatd.zzbqQ) {
                long currentTimeMillis = zznR().currentTimeMillis();
                zzKg().beginTransaction();
                try {
                    zza(com_google_android_gms_internal_zzatd, currentTimeMillis);
                    zzf(com_google_android_gms_internal_zzatd);
                    if (zzKg().zzQ(com_google_android_gms_internal_zzatd.packageName, "_f") == null) {
                        zzb(new zzauq("_fot", currentTimeMillis, Long.valueOf((1 + (currentTimeMillis / 3600000)) * 3600000), "auto"), com_google_android_gms_internal_zzatd);
                        zzb(com_google_android_gms_internal_zzatd, currentTimeMillis);
                        zzc(com_google_android_gms_internal_zzatd, currentTimeMillis);
                    } else if (com_google_android_gms_internal_zzatd.zzbqR) {
                        zzd(com_google_android_gms_internal_zzatd, currentTimeMillis);
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
            zzKl().zzMd().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKl().zzMa().zzj("App version does not match; dropping. appId", zzatx.zzfE(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), zzfu.zzKw(), null, zzfu.zzKx(), false, zzfu.zzKq(), zzfu.zzuW());
    }

    public String zzfP(final String str) {
        Object e;
        try {
            return (String) zzKk().zzd(new Callable<String>(this) {
                final /* synthetic */ zzaue zzbuF;

                public /* synthetic */ Object call() throws Exception {
                    return zzbY();
                }

                public String zzbY() throws Exception {
                    zzatc zzfu = this.zzbuF.zzKg().zzfu(str);
                    return zzfu == null ? null : zzfu.getAppInstanceId();
                }
            }).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzKl().zzLY().zze("Failed to get app instance id. appId", zzatx.zzfE(str), e);
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
            zzKl().zzLY().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzMB())) {
                zzKl().zzMe().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzKl().zzLY().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }
}
