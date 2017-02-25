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
import com.google.android.gms.wallet.WalletConstants;
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
    private static volatile zzaue zzbud;
    private final Context mContext;
    private final boolean zzadP;
    private Boolean zzbuA;
    private long zzbuB;
    private FileLock zzbuC;
    private FileChannel zzbuD;
    private List<Long> zzbuE;
    private int zzbuF;
    private int zzbuG;
    private long zzbuH = -1;
    protected long zzbuI;
    private final zzati zzbue;
    private final zzaua zzbuf;
    private final zzatx zzbug;
    private final zzaud zzbuh;
    private final zzaun zzbui;
    private final zzauc zzbuj;
    private final AppMeasurement zzbuk;
    private final FirebaseAnalytics zzbul;
    private final zzaut zzbum;
    private final zzatj zzbun;
    private final zzatv zzbuo;
    private final zzaty zzbup;
    private final zzauk zzbuq;
    private final zzaul zzbur;
    private final zzatl zzbus;
    private final zzauj zzbut;
    private final zzatu zzbuu;
    private final zzatz zzbuv;
    private final zzaup zzbuw;
    private final zzatf zzbux;
    private final zzatb zzbuy;
    private boolean zzbuz;
    private final zze zzuP;

    private class zza implements zzb {
        final /* synthetic */ zzaue zzbuJ;
        zzauw.zze zzbuK;
        List<Long> zzbuL;
        long zzbuM;
        List<zzb> zzth;

        private zza(zzaue com_google_android_gms_internal_zzaue) {
            this.zzbuJ = com_google_android_gms_internal_zzaue;
        }

        private long zza(zzb com_google_android_gms_internal_zzauw_zzb) {
            return ((com_google_android_gms_internal_zzauw_zzb.zzbxd.longValue() / 1000) / 60) / 60;
        }

        boolean isEmpty() {
            return this.zzth == null || this.zzth.isEmpty();
        }

        public boolean zza(long j, zzb com_google_android_gms_internal_zzauw_zzb) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zzb);
            if (this.zzth == null) {
                this.zzth = new ArrayList();
            }
            if (this.zzbuL == null) {
                this.zzbuL = new ArrayList();
            }
            if (this.zzth.size() > 0 && zza((zzb) this.zzth.get(0)) != zza(com_google_android_gms_internal_zzauw_zzb)) {
                return false;
            }
            long zzaeS = this.zzbuM + ((long) com_google_android_gms_internal_zzauw_zzb.zzaeS());
            if (zzaeS >= ((long) this.zzbuJ.zzKm().zzLm())) {
                return false;
            }
            this.zzbuM = zzaeS;
            this.zzth.add(com_google_android_gms_internal_zzauw_zzb);
            this.zzbuL.add(Long.valueOf(j));
            return this.zzth.size() < this.zzbuJ.zzKm().zzLn();
        }

        public void zzb(zzauw.zze com_google_android_gms_internal_zzauw_zze) {
            zzac.zzw(com_google_android_gms_internal_zzauw_zze);
            this.zzbuK = com_google_android_gms_internal_zzauw_zze;
        }
    }

    zzaue(zzaui com_google_android_gms_internal_zzaui) {
        zzac.zzw(com_google_android_gms_internal_zzaui);
        this.mContext = com_google_android_gms_internal_zzaui.mContext;
        this.zzuP = com_google_android_gms_internal_zzaui.zzn(this);
        this.zzbue = com_google_android_gms_internal_zzaui.zza(this);
        zzaua zzb = com_google_android_gms_internal_zzaui.zzb(this);
        zzb.initialize();
        this.zzbuf = zzb;
        zzatx zzc = com_google_android_gms_internal_zzaui.zzc(this);
        zzc.initialize();
        this.zzbug = zzc;
        zzKk().zzMb().zzj("App measurement is starting up, version", Long.valueOf(zzKm().zzKu()));
        zzKm().zzLf();
        zzKk().zzMb().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzaut zzj = com_google_android_gms_internal_zzaui.zzj(this);
        zzj.initialize();
        this.zzbum = zzj;
        zzatl zzq = com_google_android_gms_internal_zzaui.zzq(this);
        zzq.initialize();
        this.zzbus = zzq;
        zzatu zzr = com_google_android_gms_internal_zzaui.zzr(this);
        zzr.initialize();
        this.zzbuu = zzr;
        zzKm().zzLf();
        String zzke = zzr.zzke();
        if (zzKg().zzge(zzke)) {
            zzKk().zzMb().log("Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.");
        } else {
            com.google.android.gms.internal.zzatx.zza zzMb = zzKk().zzMb();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzke = String.valueOf(zzke);
            zzMb.log(zzke.length() != 0 ? str.concat(zzke) : new String(str));
        }
        zzKk().zzMc().log("Debug-level message logging enabled");
        zzatj zzk = com_google_android_gms_internal_zzaui.zzk(this);
        zzk.initialize();
        this.zzbun = zzk;
        zzatv zzl = com_google_android_gms_internal_zzaui.zzl(this);
        zzl.initialize();
        this.zzbuo = zzl;
        zzatf zzu = com_google_android_gms_internal_zzaui.zzu(this);
        zzu.initialize();
        this.zzbux = zzu;
        this.zzbuy = com_google_android_gms_internal_zzaui.zzv(this);
        zzaty zzm = com_google_android_gms_internal_zzaui.zzm(this);
        zzm.initialize();
        this.zzbup = zzm;
        zzauk zzo = com_google_android_gms_internal_zzaui.zzo(this);
        zzo.initialize();
        this.zzbuq = zzo;
        zzaul zzp = com_google_android_gms_internal_zzaui.zzp(this);
        zzp.initialize();
        this.zzbur = zzp;
        zzauj zzi = com_google_android_gms_internal_zzaui.zzi(this);
        zzi.initialize();
        this.zzbut = zzi;
        zzaup zzt = com_google_android_gms_internal_zzaui.zzt(this);
        zzt.initialize();
        this.zzbuw = zzt;
        this.zzbuv = com_google_android_gms_internal_zzaui.zzs(this);
        this.zzbuk = com_google_android_gms_internal_zzaui.zzh(this);
        this.zzbul = com_google_android_gms_internal_zzaui.zzg(this);
        zzaun zze = com_google_android_gms_internal_zzaui.zze(this);
        zze.initialize();
        this.zzbui = zze;
        zzauc zzf = com_google_android_gms_internal_zzaui.zzf(this);
        zzf.initialize();
        this.zzbuj = zzf;
        zzaud zzd = com_google_android_gms_internal_zzaui.zzd(this);
        zzd.initialize();
        this.zzbuh = zzd;
        if (this.zzbuF != this.zzbuG) {
            zzKk().zzLX().zze("Not all components initialized", Integer.valueOf(this.zzbuF), Integer.valueOf(this.zzbuG));
        }
        this.zzadP = true;
        this.zzbue.zzLf();
        if (this.mContext.getApplicationContext() instanceof Application) {
            int i = VERSION.SDK_INT;
            zzJZ().zzMP();
        } else {
            zzKk().zzLZ().log("Application context is not an Application");
        }
        this.zzbuh.zzm(new Runnable(this) {
            final /* synthetic */ zzaue zzbuJ;

            {
                this.zzbuJ = r1;
            }

            public void run() {
                this.zzbuJ.start();
            }
        });
    }

    private boolean zzMG() {
        zzmR();
        zzob();
        return zzKf().zzLI() || !TextUtils.isEmpty(zzKf().zzLC());
    }

    @WorkerThread
    private void zzMH() {
        zzmR();
        zzob();
        if (zzML()) {
            long abs;
            if (this.zzbuI > 0) {
                abs = 3600000 - Math.abs(zznR().elapsedRealtime() - this.zzbuI);
                if (abs > 0) {
                    zzKk().zzMd().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    zzMy().unregister();
                    zzMz().cancel();
                    return;
                }
                this.zzbuI = 0;
            }
            if (zzMs() && zzMG()) {
                abs = zzMI();
                if (abs == 0) {
                    zzMy().unregister();
                    zzMz().cancel();
                    return;
                } else if (zzMx().zzqa()) {
                    long j = zzKl().zzbtf.get();
                    long zzLr = zzKm().zzLr();
                    if (!zzKg().zzh(j, zzLr)) {
                        abs = Math.max(abs, j + zzLr);
                    }
                    zzMy().unregister();
                    abs -= zznR().currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzKm().zzLv();
                        zzKl().zzbtd.set(zznR().currentTimeMillis());
                    }
                    zzKk().zzMd().zzj("Upload scheduled in approximately ms", Long.valueOf(abs));
                    zzMz().zzy(abs);
                    return;
                } else {
                    zzMy().zzpX();
                    zzMz().cancel();
                    return;
                }
            }
            zzMy().unregister();
            zzMz().cancel();
        }
    }

    private long zzMI() {
        long zzLt;
        long currentTimeMillis = zznR().currentTimeMillis();
        long zzLy = zzKm().zzLy();
        Object obj = (zzKf().zzLJ() || zzKf().zzLD()) ? 1 : null;
        if (obj != null) {
            CharSequence zzLB = zzKm().zzLB();
            zzLt = (TextUtils.isEmpty(zzLB) || ".none.".equals(zzLB)) ? zzKm().zzLt() : zzKm().zzLu();
        } else {
            zzLt = zzKm().zzLs();
        }
        long j = zzKl().zzbtd.get();
        long j2 = zzKl().zzbte.get();
        long max = Math.max(zzKf().zzLG(), zzKf().zzLH());
        if (max == 0) {
            return 0;
        }
        max = currentTimeMillis - Math.abs(max - currentTimeMillis);
        j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
        j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
        currentTimeMillis = max + zzLy;
        if (obj != null && j > 0) {
            currentTimeMillis = Math.min(max, j) + zzLt;
        }
        if (!zzKg().zzh(j, zzLt)) {
            currentTimeMillis = j + zzLt;
        }
        if (j2 == 0 || j2 < max) {
            return currentTimeMillis;
        }
        for (int i = 0; i < zzKm().zzLA(); i++) {
            currentTimeMillis += ((long) (1 << i)) * zzKm().zzLz();
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
        if (com_google_android_gms_internal_zzatm.zzbrD == null) {
            return false;
        }
        Iterator it = com_google_android_gms_internal_zzatm.zzbrD.iterator();
        while (it.hasNext()) {
            if ("_r".equals((String) it.next())) {
                return true;
            }
        }
        return zzKh().zzab(com_google_android_gms_internal_zzatm.mAppId, com_google_android_gms_internal_zzatm.mName) && zzKf().zza(zzMD(), com_google_android_gms_internal_zzatm.mAppId, false, false, false, false, false).zzbrv < ((long) zzKm().zzfl(com_google_android_gms_internal_zzatm.mAppId));
    }

    private com.google.android.gms.internal.zzauw.zza[] zza(String str, zzg[] com_google_android_gms_internal_zzauw_zzgArr, zzb[] com_google_android_gms_internal_zzauw_zzbArr) {
        zzac.zzdr(str);
        return zzJY().zza(str, com_google_android_gms_internal_zzauw_zzbArr, com_google_android_gms_internal_zzauw_zzgArr);
    }

    public static zzaue zzbM(Context context) {
        zzac.zzw(context);
        zzac.zzw(context.getApplicationContext());
        if (zzbud == null) {
            synchronized (zzaue.class) {
                if (zzbud == null) {
                    zzbud = new zzaui(context).zzMO();
                }
            }
        }
        return zzbud;
    }

    @WorkerThread
    private void zzf(zzatd com_google_android_gms_internal_zzatd) {
        Object obj = 1;
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzac.zzdr(com_google_android_gms_internal_zzatd.packageName);
        zzatc zzfu = zzKf().zzfu(com_google_android_gms_internal_zzatd.packageName);
        String zzfH = zzKl().zzfH(com_google_android_gms_internal_zzatd.packageName);
        Object obj2 = null;
        if (zzfu == null) {
            zzatc com_google_android_gms_internal_zzatc = new zzatc(this, com_google_android_gms_internal_zzatd.packageName);
            com_google_android_gms_internal_zzatc.zzfd(zzKl().zzMg());
            com_google_android_gms_internal_zzatc.zzff(zzfH);
            zzfu = com_google_android_gms_internal_zzatc;
            obj2 = 1;
        } else if (!zzfH.equals(zzfu.zzKo())) {
            zzfu.zzff(zzfH);
            zzfu.zzfd(zzKl().zzMg());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP) || com_google_android_gms_internal_zzatd.zzbqP.equals(zzfu.getGmpAppId()))) {
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqP);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqX) || com_google_android_gms_internal_zzatd.zzbqX.equals(zzfu.zzKp()))) {
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqX);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqR == 0 || com_google_android_gms_internal_zzatd.zzbqR == zzfu.zzKu())) {
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqR);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbhM) || com_google_android_gms_internal_zzatd.zzbhM.equals(zzfu.zzmZ()))) {
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhM);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqW != zzfu.zzKs()) {
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqW);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzatd.zzbqQ == null || com_google_android_gms_internal_zzatd.zzbqQ.equals(zzfu.zzKt()))) {
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqQ);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqS != zzfu.zzKv()) {
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqS);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqU != zzfu.zzKw()) {
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqU);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqT) || com_google_android_gms_internal_zzatd.zzbqT.equals(zzfu.zzKH()))) {
            zzfu.zzfi(com_google_android_gms_internal_zzatd.zzbqT);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzatd.zzbqY != zzfu.zzuW()) {
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqY);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzKf().zza(zzfu);
        }
    }

    private boolean zzl(String str, long j) {
        zzKf().beginTransaction();
        try {
            zzaue com_google_android_gms_internal_zzaue = this;
            zza com_google_android_gms_internal_zzaue_zza = new zza();
            zzKf().zza(str, j, this.zzbuH, com_google_android_gms_internal_zzaue_zza);
            if (com_google_android_gms_internal_zzaue_zza.isEmpty()) {
                zzKf().setTransactionSuccessful();
                zzKf().endTransaction();
                return false;
            }
            int i;
            boolean z = false;
            zzauw.zze com_google_android_gms_internal_zzauw_zze = com_google_android_gms_internal_zzaue_zza.zzbuK;
            com_google_android_gms_internal_zzauw_zze.zzbxk = new zzb[com_google_android_gms_internal_zzaue_zza.zzth.size()];
            int i2 = 0;
            int i3 = 0;
            while (i3 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                boolean z2;
                Object obj;
                if (zzKh().zzaa(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                    zzKk().zzLZ().zze("Dropping blacklisted raw event. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS), ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    obj = (zzKg().zzgg(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS) || zzKg().zzgh(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS)) ? 1 : null;
                    if (obj != null || "_err".equals(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                        i = i2;
                        z2 = z;
                    } else {
                        zzKg().zza(11, "_ev", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name, 0);
                        i = i2;
                        z2 = z;
                    }
                } else {
                    int i4;
                    boolean z3;
                    boolean zzab = zzKh().zzab(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                    if (zzab || zzKg().zzgi(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name)) {
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr;
                        zzc com_google_android_gms_internal_zzauw_zzc;
                        zzb com_google_android_gms_internal_zzauw_zzb;
                        Object obj2 = null;
                        Object obj3 = null;
                        if (((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc == null) {
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc = new zzc[0];
                        }
                        zzc[] com_google_android_gms_internal_zzauw_zzcArr2 = ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc;
                        int length = com_google_android_gms_internal_zzauw_zzcArr2.length;
                        int i5 = 0;
                        while (i5 < length) {
                            zzc com_google_android_gms_internal_zzauw_zzc2 = com_google_android_gms_internal_zzauw_zzcArr2[i5];
                            if ("_c".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxg = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzauw_zzc2.name)) {
                                com_google_android_gms_internal_zzauw_zzc2.zzbxg = Long.valueOf(1);
                                obj = 1;
                            } else {
                                obj = obj3;
                            }
                            i5++;
                            obj3 = obj;
                        }
                        if (obj2 == null && zzab) {
                            zzKk().zzMd().zzj("Marking event as conversion", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_c";
                            com_google_android_gms_internal_zzauw_zzc.zzbxg = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        if (obj3 == null) {
                            zzKk().zzMd().zzj("Marking event as real-time", ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name);
                            com_google_android_gms_internal_zzauw_zzcArr = (zzc[]) Arrays.copyOf(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc, ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc.length + 1);
                            com_google_android_gms_internal_zzauw_zzc = new zzc();
                            com_google_android_gms_internal_zzauw_zzc.name = "_r";
                            com_google_android_gms_internal_zzauw_zzc.zzbxg = Long.valueOf(1);
                            com_google_android_gms_internal_zzauw_zzcArr[com_google_android_gms_internal_zzauw_zzcArr.length - 1] = com_google_android_gms_internal_zzauw_zzc;
                            ((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).zzbxc = com_google_android_gms_internal_zzauw_zzcArr;
                        }
                        boolean z4 = true;
                        if (zzKf().zza(zzMD(), com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS, false, false, false, false, true).zzbrv > ((long) zzKm().zzfl(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS))) {
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzauw_zzb.zzbxc.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzauw_zzb.zzbxc[i4].name)) {
                                    obj3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbxc.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbxc, 0, obj3, 0, i4);
                                    }
                                    if (i4 < obj3.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzauw_zzb.zzbxc, i4 + 1, obj3, i4, obj3.length - i4);
                                    }
                                    com_google_android_gms_internal_zzauw_zzb.zzbxc = obj3;
                                    z4 = z;
                                } else {
                                    i4++;
                                }
                            }
                            z4 = z;
                        }
                        if (zzaut.zzfT(((zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3)).name) && zzab && zzKf().zza(zzMD(), com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS, false, false, true, false, false).zzbrt > ((long) zzKm().zzfk(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS))) {
                            zzKk().zzLZ().zzj("Too many conversions. Not logging as conversion. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS));
                            com_google_android_gms_internal_zzauw_zzb = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                            Object obj4 = null;
                            zzc com_google_android_gms_internal_zzauw_zzc3 = null;
                            zzc[] com_google_android_gms_internal_zzauw_zzcArr3 = com_google_android_gms_internal_zzauw_zzb.zzbxc;
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
                                com_google_android_gms_internal_zzauw_zzcArr3 = new zzc[(com_google_android_gms_internal_zzauw_zzb.zzbxc.length - 1)];
                                int i8 = 0;
                                zzc[] com_google_android_gms_internal_zzauw_zzcArr4 = com_google_android_gms_internal_zzauw_zzb.zzbxc;
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
                                com_google_android_gms_internal_zzauw_zzb.zzbxc = com_google_android_gms_internal_zzauw_zzcArr3;
                                z3 = z4;
                            } else if (com_google_android_gms_internal_zzauw_zzc3 != null) {
                                com_google_android_gms_internal_zzauw_zzc3.name = "_err";
                                com_google_android_gms_internal_zzauw_zzc3.zzbxg = Long.valueOf(10);
                                z3 = z4;
                            } else {
                                zzKk().zzLX().zzj("Did not find conversion parameter. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS));
                            }
                        }
                        z3 = z4;
                    } else {
                        z3 = z;
                    }
                    i4 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zze.zzbxk[i2] = (zzb) com_google_android_gms_internal_zzaue_zza.zzth.get(i3);
                    i = i4;
                    z2 = z3;
                }
                i3++;
                i2 = i;
                z = z2;
            }
            if (i2 < com_google_android_gms_internal_zzaue_zza.zzth.size()) {
                com_google_android_gms_internal_zzauw_zze.zzbxk = (zzb[]) Arrays.copyOf(com_google_android_gms_internal_zzauw_zze.zzbxk, i2);
            }
            com_google_android_gms_internal_zzauw_zze.zzbxD = zza(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS, com_google_android_gms_internal_zzaue_zza.zzbuK.zzbxl, com_google_android_gms_internal_zzauw_zze.zzbxk);
            com_google_android_gms_internal_zzauw_zze.zzbxn = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzauw_zze.zzbxo = Long.valueOf(Long.MIN_VALUE);
            for (zzb com_google_android_gms_internal_zzauw_zzb2 : com_google_android_gms_internal_zzauw_zze.zzbxk) {
                if (com_google_android_gms_internal_zzauw_zzb2.zzbxd.longValue() < com_google_android_gms_internal_zzauw_zze.zzbxn.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxn = com_google_android_gms_internal_zzauw_zzb2.zzbxd;
                }
                if (com_google_android_gms_internal_zzauw_zzb2.zzbxd.longValue() > com_google_android_gms_internal_zzauw_zze.zzbxo.longValue()) {
                    com_google_android_gms_internal_zzauw_zze.zzbxo = com_google_android_gms_internal_zzauw_zzb2.zzbxd;
                }
            }
            String str2 = com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS;
            zzatc zzfu = zzKf().zzfu(str2);
            if (zzfu == null) {
                zzKk().zzLX().zzj("Bundling raw events w/o app info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS));
            } else if (com_google_android_gms_internal_zzauw_zze.zzbxk.length > 0) {
                long zzKr = zzfu.zzKr();
                com_google_android_gms_internal_zzauw_zze.zzbxq = zzKr != 0 ? Long.valueOf(zzKr) : null;
                long zzKq = zzfu.zzKq();
                if (zzKq != 0) {
                    zzKr = zzKq;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxp = zzKr != 0 ? Long.valueOf(zzKr) : null;
                zzfu.zzKA();
                com_google_android_gms_internal_zzauw_zze.zzbxB = Integer.valueOf((int) zzfu.zzKx());
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxn.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxo.longValue());
                com_google_android_gms_internal_zzauw_zze.zzbqT = zzfu.zzKI();
                zzKf().zza(zzfu);
            }
            if (com_google_android_gms_internal_zzauw_zze.zzbxk.length > 0) {
                zzKm().zzLf();
                zzauv.zzb zzfL = zzKh().zzfL(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS);
                if (zzfL != null && zzfL.zzbwR != null) {
                    com_google_android_gms_internal_zzauw_zze.zzbxI = zzfL.zzbwR;
                } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzaue_zza.zzbuK.zzbqP)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxI = Long.valueOf(-1);
                } else {
                    zzKk().zzLZ().zzj("Did not find measurement config or missing version info. appId", zzatx.zzfE(com_google_android_gms_internal_zzaue_zza.zzbuK.zzaS));
                }
                zzKf().zza(com_google_android_gms_internal_zzauw_zze, z);
            }
            zzKf().zzJ(com_google_android_gms_internal_zzaue_zza.zzbuL);
            zzKf().zzfB(str2);
            zzKf().setTransactionSuccessful();
            boolean z5 = com_google_android_gms_internal_zzauw_zze.zzbxk.length > 0;
            zzKf().endTransaction();
            return z5;
        } catch (Throwable th) {
            zzKf().endTransaction();
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
        if (zzKm().zzLg()) {
            return false;
        }
        Boolean zzLh = zzKm().zzLh();
        if (zzLh != null) {
            z = zzLh.booleanValue();
        } else if (!zzKm().zzwR()) {
            z = true;
        }
        return zzKl().zzaL(z);
    }

    @WorkerThread
    protected void start() {
        zzmR();
        zzKf().zzLE();
        if (zzKl().zzbtd.get() == 0) {
            zzKl().zzbtd.set(zznR().currentTimeMillis());
        }
        if (zzMs()) {
            zzKm().zzLf();
            if (!TextUtils.isEmpty(zzKa().getGmpAppId())) {
                String zzMj = zzKl().zzMj();
                if (zzMj == null) {
                    zzKl().zzfI(zzKa().getGmpAppId());
                } else if (!zzMj.equals(zzKa().getGmpAppId())) {
                    zzKk().zzMb().log("Rechecking which service to use due to a GMP App Id change");
                    zzKl().zzMm();
                    this.zzbur.disconnect();
                    this.zzbur.zzoD();
                    zzKl().zzfI(zzKa().getGmpAppId());
                }
            }
            zzKm().zzLf();
            if (!TextUtils.isEmpty(zzKa().getGmpAppId())) {
                zzJZ().zzMQ();
            }
        } else if (isEnabled()) {
            if (!zzKg().zzbW("android.permission.INTERNET")) {
                zzKk().zzLX().log("App is missing INTERNET permission");
            }
            if (!zzKg().zzbW("android.permission.ACCESS_NETWORK_STATE")) {
                zzKk().zzLX().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzKm().zzLf();
            if (!zzadg.zzbi(getContext()).zzzw()) {
                if (!zzaub.zzi(getContext(), false)) {
                    zzKk().zzLX().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzaum.zzj(getContext(), false)) {
                    zzKk().zzLX().log("AppMeasurementService not registered/enabled");
                }
            }
            zzKk().zzLX().log("Uploading is not possible. App measurement disabled");
        }
        zzMH();
    }

    void zzJU() {
        zzKm().zzLf();
        throw new IllegalStateException("Unexpected call on client side");
    }

    void zzJV() {
        zzKm().zzLf();
    }

    public zzatb zzJX() {
        zza(this.zzbuy);
        return this.zzbuy;
    }

    public zzatf zzJY() {
        zza(this.zzbux);
        return this.zzbux;
    }

    public zzauj zzJZ() {
        zza(this.zzbut);
        return this.zzbut;
    }

    protected void zzK(List<Long> list) {
        zzac.zzax(!list.isEmpty());
        if (this.zzbuE != null) {
            zzKk().zzLX().log("Set uploading progress before finishing the previous upload");
        } else {
            this.zzbuE = new ArrayList(list);
        }
    }

    public zzatu zzKa() {
        zza(this.zzbuu);
        return this.zzbuu;
    }

    public zzatl zzKb() {
        zza(this.zzbus);
        return this.zzbus;
    }

    public zzaul zzKc() {
        zza(this.zzbur);
        return this.zzbur;
    }

    public zzauk zzKd() {
        zza(this.zzbuq);
        return this.zzbuq;
    }

    public zzatv zzKe() {
        zza(this.zzbuo);
        return this.zzbuo;
    }

    public zzatj zzKf() {
        zza(this.zzbun);
        return this.zzbun;
    }

    public zzaut zzKg() {
        zza(this.zzbum);
        return this.zzbum;
    }

    public zzauc zzKh() {
        zza(this.zzbuj);
        return this.zzbuj;
    }

    public zzaun zzKi() {
        zza(this.zzbui);
        return this.zzbui;
    }

    public zzaud zzKj() {
        zza(this.zzbuh);
        return this.zzbuh;
    }

    public zzatx zzKk() {
        zza(this.zzbug);
        return this.zzbug;
    }

    public zzaua zzKl() {
        zza(this.zzbuf);
        return this.zzbuf;
    }

    public zzati zzKm() {
        return this.zzbue;
    }

    FileChannel zzMA() {
        return this.zzbuD;
    }

    @WorkerThread
    void zzMB() {
        zzmR();
        zzob();
        if (zzML() && zzMC()) {
            zzy(zza(zzMA()), zzKa().zzLW());
        }
    }

    @WorkerThread
    boolean zzMC() {
        zzmR();
        try {
            this.zzbuD = new RandomAccessFile(new File(getContext().getFilesDir(), this.zzbun.zzow()), "rw").getChannel();
            this.zzbuC = this.zzbuD.tryLock();
            if (this.zzbuC != null) {
                zzKk().zzMd().log("Storage concurrent access okay");
                return true;
            }
            zzKk().zzLX().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzKk().zzLX().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzKk().zzLX().zzj("Failed to access storage lock file", e2);
        }
    }

    long zzMD() {
        return ((((zznR().currentTimeMillis() + zzKl().zzMh()) / 1000) / 60) / 60) / 24;
    }

    @WorkerThread
    protected boolean zzME() {
        zzmR();
        return this.zzbuE != null;
    }

    @WorkerThread
    public void zzMF() {
        int i = 0;
        zzmR();
        zzob();
        zzKm().zzLf();
        Boolean zzMl = zzKl().zzMl();
        if (zzMl == null) {
            zzKk().zzLZ().log("Upload data called on the client side before use of service was decided");
        } else if (zzMl.booleanValue()) {
            zzKk().zzLX().log("Upload called in the client side when service should be used");
        } else if (this.zzbuI > 0) {
            zzMH();
        } else if (zzME()) {
            zzKk().zzLZ().log("Uploading requested multiple times");
        } else if (zzMx().zzqa()) {
            long currentTimeMillis = zznR().currentTimeMillis();
            zzaq(currentTimeMillis - zzKm().zzLq());
            long j = zzKl().zzbtd.get();
            if (j != 0) {
                zzKk().zzMc().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
            }
            String zzLC = zzKf().zzLC();
            Object zzao;
            if (TextUtils.isEmpty(zzLC)) {
                this.zzbuH = -1;
                zzao = zzKf().zzao(currentTimeMillis - zzKm().zzLq());
                if (!TextUtils.isEmpty(zzao)) {
                    zzatc zzfu = zzKf().zzfu(zzao);
                    if (zzfu != null) {
                        zzb(zzfu);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.zzbuH == -1) {
                this.zzbuH = zzKf().zzLK();
            }
            List<Pair> zzn = zzKf().zzn(zzLC, zzKm().zzfq(zzLC), zzKm().zzfr(zzLC));
            if (!zzn.isEmpty()) {
                zzauw.zze com_google_android_gms_internal_zzauw_zze;
                Object obj;
                List subList;
                for (Pair pair : zzn) {
                    com_google_android_gms_internal_zzauw_zze = (zzauw.zze) pair.first;
                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxx)) {
                        obj = com_google_android_gms_internal_zzauw_zze.zzbxx;
                        break;
                    }
                }
                obj = null;
                if (obj != null) {
                    for (int i2 = 0; i2 < zzn.size(); i2++) {
                        com_google_android_gms_internal_zzauw_zze = (zzauw.zze) ((Pair) zzn.get(i2)).first;
                        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzauw_zze.zzbxx) && !com_google_android_gms_internal_zzauw_zze.zzbxx.equals(obj)) {
                            subList = zzn.subList(0, i2);
                            break;
                        }
                    }
                }
                subList = zzn;
                zzd com_google_android_gms_internal_zzauw_zzd = new zzd();
                com_google_android_gms_internal_zzauw_zzd.zzbxh = new zzauw.zze[subList.size()];
                List arrayList = new ArrayList(subList.size());
                while (i < com_google_android_gms_internal_zzauw_zzd.zzbxh.length) {
                    com_google_android_gms_internal_zzauw_zzd.zzbxh[i] = (zzauw.zze) ((Pair) subList.get(i)).first;
                    arrayList.add((Long) ((Pair) subList.get(i)).second);
                    com_google_android_gms_internal_zzauw_zzd.zzbxh[i].zzbxw = Long.valueOf(zzKm().zzKu());
                    com_google_android_gms_internal_zzauw_zzd.zzbxh[i].zzbxm = Long.valueOf(currentTimeMillis);
                    com_google_android_gms_internal_zzauw_zzd.zzbxh[i].zzbxC = Boolean.valueOf(zzKm().zzLf());
                    i++;
                }
                zzao = zzKk().zzak(2) ? zzaut.zzb(com_google_android_gms_internal_zzauw_zzd) : null;
                byte[] zza = zzKg().zza(com_google_android_gms_internal_zzauw_zzd);
                String zzLp = zzKm().zzLp();
                try {
                    URL url = new URL(zzLp);
                    zzK(arrayList);
                    zzKl().zzbte.set(currentTimeMillis);
                    Object obj2 = "?";
                    if (com_google_android_gms_internal_zzauw_zzd.zzbxh.length > 0) {
                        obj2 = com_google_android_gms_internal_zzauw_zzd.zzbxh[0].zzaS;
                    }
                    zzKk().zzMd().zzd("Uploading data. app, uncompressed size, data", obj2, Integer.valueOf(zza.length), zzao);
                    zzMx().zza(zzLC, url, zza, null, new zza(this) {
                        final /* synthetic */ zzaue zzbuJ;

                        {
                            this.zzbuJ = r1;
                        }

                        public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                            this.zzbuJ.zza(i, th, bArr);
                        }
                    });
                } catch (MalformedURLException e) {
                    zzKk().zzLX().zze("Failed to parse upload URL. Not uploading. appId", zzatx.zzfE(zzLC), zzLp);
                }
            }
        } else {
            zzKk().zzLZ().log("Network not connected, ignoring upload request");
            zzMH();
        }
    }

    void zzMJ() {
        this.zzbuG++;
    }

    @WorkerThread
    void zzMK() {
        zzmR();
        zzob();
        if (!this.zzbuz) {
            zzKk().zzMb().log("This instance being marked as an uploader");
            zzMB();
        }
        this.zzbuz = true;
    }

    @WorkerThread
    boolean zzML() {
        zzmR();
        zzob();
        return this.zzbuz;
    }

    @WorkerThread
    protected boolean zzMs() {
        boolean z = false;
        zzob();
        zzmR();
        if (this.zzbuA == null || this.zzbuB == 0 || !(this.zzbuA == null || this.zzbuA.booleanValue() || Math.abs(zznR().elapsedRealtime() - this.zzbuB) <= 1000)) {
            this.zzbuB = zznR().elapsedRealtime();
            zzKm().zzLf();
            if (zzKg().zzbW("android.permission.INTERNET") && zzKg().zzbW("android.permission.ACCESS_NETWORK_STATE") && (zzadg.zzbi(getContext()).zzzw() || (zzaub.zzi(getContext(), false) && zzaum.zzj(getContext(), false)))) {
                z = true;
            }
            this.zzbuA = Boolean.valueOf(z);
            if (this.zzbuA.booleanValue()) {
                this.zzbuA = Boolean.valueOf(zzKg().zzga(zzKa().getGmpAppId()));
            }
        }
        return this.zzbuA.booleanValue();
    }

    public zzatx zzMt() {
        return (this.zzbug == null || !this.zzbug.isInitialized()) ? null : this.zzbug;
    }

    zzaud zzMu() {
        return this.zzbuh;
    }

    public AppMeasurement zzMv() {
        return this.zzbuk;
    }

    public FirebaseAnalytics zzMw() {
        return this.zzbul;
    }

    public zzaty zzMx() {
        zza(this.zzbup);
        return this.zzbup;
    }

    public zzatz zzMy() {
        if (this.zzbuv != null) {
            return this.zzbuv;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    public zzaup zzMz() {
        zza(this.zzbuw);
        return this.zzbuw;
    }

    public void zzW(boolean z) {
        zzMH();
    }

    @WorkerThread
    int zza(FileChannel fileChannel) {
        int i = 0;
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKk().zzLX().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzKk().zzLZ().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzKk().zzLX().zzj("Failed to read from channel", e);
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
        List<Long> list = this.zzbuE;
        this.zzbuE = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            try {
                zzKl().zzbtd.set(zznR().currentTimeMillis());
                zzKl().zzbte.set(0);
                zzMH();
                zzKk().zzMd().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzKf().beginTransaction();
                for (Long longValue : list) {
                    zzKf().zzan(longValue.longValue());
                }
                zzKf().setTransactionSuccessful();
                zzKf().endTransaction();
                if (zzMx().zzqa() && zzMG()) {
                    zzMF();
                } else {
                    this.zzbuH = -1;
                    zzMH();
                }
                this.zzbuI = 0;
                return;
            } catch (SQLiteException e) {
                zzKk().zzLX().zzj("Database error while trying to delete uploaded bundles", e);
                this.zzbuI = zznR().elapsedRealtime();
                zzKk().zzMd().zzj("Disable upload, time", Long.valueOf(this.zzbuI));
                return;
            } catch (Throwable th2) {
                zzKf().endTransaction();
            }
        }
        zzKk().zzMd().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
        zzKl().zzbte.set(zznR().currentTimeMillis());
        if (i == 503 || i == 429) {
            i2 = 1;
        }
        if (i2 != 0) {
            zzKl().zzbtf.set(zznR().currentTimeMillis());
        }
        zzMH();
    }

    @WorkerThread
    void zza(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzatc zzfu = zzKf().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || zzfu.getGmpAppId() == null || zzfu.getGmpAppId().equals(com_google_android_gms_internal_zzatd.zzbqP))) {
            zzKk().zzLZ().zzj("New GMP App Id passed in. Removing cached database data. appId", zzatx.zzfE(zzfu.zzke()));
            zzKf().zzfz(zzfu.zzke());
            zzfu = null;
        }
        if (zzfu != null && zzfu.zzmZ() != null && !zzfu.zzmZ().equals(com_google_android_gms_internal_zzatd.zzbhM)) {
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
        com_google_android_gms_internal_zzauw_zze.zzbxj = Integer.valueOf(1);
        com_google_android_gms_internal_zzauw_zze.zzbxr = "android";
        com_google_android_gms_internal_zzauw_zze.zzaS = com_google_android_gms_internal_zzatd.packageName;
        com_google_android_gms_internal_zzauw_zze.zzbqQ = com_google_android_gms_internal_zzatd.zzbqQ;
        com_google_android_gms_internal_zzauw_zze.zzbhM = com_google_android_gms_internal_zzatd.zzbhM;
        com_google_android_gms_internal_zzauw_zze.zzbxE = Integer.valueOf((int) com_google_android_gms_internal_zzatd.zzbqW);
        com_google_android_gms_internal_zzauw_zze.zzbxv = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqR);
        com_google_android_gms_internal_zzauw_zze.zzbqP = com_google_android_gms_internal_zzatd.zzbqP;
        com_google_android_gms_internal_zzauw_zze.zzbxA = com_google_android_gms_internal_zzatd.zzbqS == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzatd.zzbqS);
        Pair zzfG = zzKl().zzfG(com_google_android_gms_internal_zzatd.packageName);
        if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
            com_google_android_gms_internal_zzauw_zze.zzbxx = (String) zzfG.first;
            com_google_android_gms_internal_zzauw_zze.zzbxy = (Boolean) zzfG.second;
        } else if (!zzKb().zzbL(this.mContext)) {
            String string = Secure.getString(this.mContext.getContentResolver(), "android_id");
            if (string == null) {
                zzKk().zzLZ().zzj("null secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
                string = "null";
            } else if (string.isEmpty()) {
                zzKk().zzLZ().zzj("empty secure ID. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
            }
            com_google_android_gms_internal_zzauw_zze.zzbxH = string;
        }
        com_google_android_gms_internal_zzauw_zze.zzbxs = zzKb().zzkN();
        com_google_android_gms_internal_zzauw_zze.zzbb = zzKb().zzLR();
        com_google_android_gms_internal_zzauw_zze.zzbxu = Integer.valueOf((int) zzKb().zzLS());
        com_google_android_gms_internal_zzauw_zze.zzbxt = zzKb().zzLT();
        com_google_android_gms_internal_zzauw_zze.zzbxw = null;
        com_google_android_gms_internal_zzauw_zze.zzbxm = null;
        com_google_android_gms_internal_zzauw_zze.zzbxn = null;
        com_google_android_gms_internal_zzauw_zze.zzbxo = null;
        com_google_android_gms_internal_zzauw_zze.zzbxJ = Long.valueOf(com_google_android_gms_internal_zzatd.zzbqY);
        zzatc zzfu = zzKf().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (zzfu == null) {
            zzfu = new zzatc(this, com_google_android_gms_internal_zzatd.packageName);
            zzfu.zzfd(zzKl().zzMg());
            zzfu.zzfg(com_google_android_gms_internal_zzatd.zzbqX);
            zzfu.zzfe(com_google_android_gms_internal_zzatd.zzbqP);
            zzfu.zzff(zzKl().zzfH(com_google_android_gms_internal_zzatd.packageName));
            zzfu.zzad(0);
            zzfu.zzY(0);
            zzfu.zzZ(0);
            zzfu.setAppVersion(com_google_android_gms_internal_zzatd.zzbhM);
            zzfu.zzaa(com_google_android_gms_internal_zzatd.zzbqW);
            zzfu.zzfh(com_google_android_gms_internal_zzatd.zzbqQ);
            zzfu.zzab(com_google_android_gms_internal_zzatd.zzbqR);
            zzfu.zzac(com_google_android_gms_internal_zzatd.zzbqS);
            zzfu.setMeasurementEnabled(com_google_android_gms_internal_zzatd.zzbqU);
            zzfu.zzam(com_google_android_gms_internal_zzatd.zzbqY);
            zzKf().zza(zzfu);
        }
        com_google_android_gms_internal_zzauw_zze.zzbxz = zzfu.getAppInstanceId();
        com_google_android_gms_internal_zzauw_zze.zzbqX = zzfu.zzKp();
        List zzft = zzKf().zzft(com_google_android_gms_internal_zzatd.packageName);
        com_google_android_gms_internal_zzauw_zze.zzbxl = new zzg[zzft.size()];
        for (int i = 0; i < zzft.size(); i++) {
            zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
            com_google_android_gms_internal_zzauw_zze.zzbxl[i] = com_google_android_gms_internal_zzauw_zzg;
            com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
            com_google_android_gms_internal_zzauw_zzg.zzbxN = Long.valueOf(((zzaus) zzft.get(i)).zzbwk);
            zzKg().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
        }
        try {
            if (zzKf().zza(com_google_android_gms_internal_zzatm, zzKf().zza(com_google_android_gms_internal_zzauw_zze), zza(com_google_android_gms_internal_zzatm))) {
                this.zzbuI = 0;
            }
        } catch (IOException e) {
            zzKk().zzLX().zze("Data loss. Failed to insert raw event metadata. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e);
        }
    }

    @WorkerThread
    boolean zza(int i, FileChannel fileChannel) {
        zzmR();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzKk().zzLX().log("Bad chanel to read from");
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
            zzKk().zzLX().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzKk().zzLX().zzj("Failed to write to channel", e);
            return false;
        }
    }

    @WorkerThread
    public byte[] zza(@NonNull zzatq com_google_android_gms_internal_zzatq, @Size(min = 1) String str) {
        zzob();
        zzmR();
        zzJU();
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzac.zzdr(str);
        zzd com_google_android_gms_internal_zzauw_zzd = new zzd();
        zzKf().beginTransaction();
        try {
            zzatc zzfu = zzKf().zzfu(str);
            byte[] bArr;
            if (zzfu == null) {
                zzKk().zzMc().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzfu.zzKw()) {
                long j;
                zzauw.zze com_google_android_gms_internal_zzauw_zze = new zzauw.zze();
                com_google_android_gms_internal_zzauw_zzd.zzbxh = new zzauw.zze[]{com_google_android_gms_internal_zzauw_zze};
                com_google_android_gms_internal_zzauw_zze.zzbxj = Integer.valueOf(1);
                com_google_android_gms_internal_zzauw_zze.zzbxr = "android";
                com_google_android_gms_internal_zzauw_zze.zzaS = zzfu.zzke();
                com_google_android_gms_internal_zzauw_zze.zzbqQ = zzfu.zzKt();
                com_google_android_gms_internal_zzauw_zze.zzbhM = zzfu.zzmZ();
                com_google_android_gms_internal_zzauw_zze.zzbxE = Integer.valueOf((int) zzfu.zzKs());
                com_google_android_gms_internal_zzauw_zze.zzbxv = Long.valueOf(zzfu.zzKu());
                com_google_android_gms_internal_zzauw_zze.zzbqP = zzfu.getGmpAppId();
                com_google_android_gms_internal_zzauw_zze.zzbxA = Long.valueOf(zzfu.zzKv());
                Pair zzfG = zzKl().zzfG(zzfu.zzke());
                if (!TextUtils.isEmpty((CharSequence) zzfG.first)) {
                    com_google_android_gms_internal_zzauw_zze.zzbxx = (String) zzfG.first;
                    com_google_android_gms_internal_zzauw_zze.zzbxy = (Boolean) zzfG.second;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxs = zzKb().zzkN();
                com_google_android_gms_internal_zzauw_zze.zzbb = zzKb().zzLR();
                com_google_android_gms_internal_zzauw_zze.zzbxu = Integer.valueOf((int) zzKb().zzLS());
                com_google_android_gms_internal_zzauw_zze.zzbxt = zzKb().zzLT();
                com_google_android_gms_internal_zzauw_zze.zzbxz = zzfu.getAppInstanceId();
                com_google_android_gms_internal_zzauw_zze.zzbqX = zzfu.zzKp();
                List zzft = zzKf().zzft(zzfu.zzke());
                com_google_android_gms_internal_zzauw_zze.zzbxl = new zzg[zzft.size()];
                for (int i = 0; i < zzft.size(); i++) {
                    zzg com_google_android_gms_internal_zzauw_zzg = new zzg();
                    com_google_android_gms_internal_zzauw_zze.zzbxl[i] = com_google_android_gms_internal_zzauw_zzg;
                    com_google_android_gms_internal_zzauw_zzg.name = ((zzaus) zzft.get(i)).mName;
                    com_google_android_gms_internal_zzauw_zzg.zzbxN = Long.valueOf(((zzaus) zzft.get(i)).zzbwk);
                    zzKg().zza(com_google_android_gms_internal_zzauw_zzg, ((zzaus) zzft.get(i)).mValue);
                }
                Bundle zzLV = com_google_android_gms_internal_zzatq.zzbrK.zzLV();
                if ("_iap".equals(com_google_android_gms_internal_zzatq.name)) {
                    zzLV.putLong("_c", 1);
                    zzKk().zzMc().log("Marking in-app purchase as real-time");
                    zzLV.putLong("_r", 1);
                }
                zzLV.putString("_o", com_google_android_gms_internal_zzatq.zzbqZ);
                if (zzKg().zzge(com_google_android_gms_internal_zzauw_zze.zzaS)) {
                    zzKg().zza(zzLV, "_dbg", Long.valueOf(1));
                    zzKg().zza(zzLV, "_r", Long.valueOf(1));
                }
                zzatn zzQ = zzKf().zzQ(str, com_google_android_gms_internal_zzatq.name);
                if (zzQ == null) {
                    zzKf().zza(new zzatn(str, com_google_android_gms_internal_zzatq.name, 1, 0, com_google_android_gms_internal_zzatq.zzbrL));
                    j = 0;
                } else {
                    j = zzQ.zzbrG;
                    zzKf().zza(zzQ.zzap(com_google_android_gms_internal_zzatq.zzbrL).zzLU());
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqZ, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrL, j, zzLV);
                zzb com_google_android_gms_internal_zzauw_zzb = new zzb();
                com_google_android_gms_internal_zzauw_zze.zzbxk = new zzb[]{com_google_android_gms_internal_zzauw_zzb};
                com_google_android_gms_internal_zzauw_zzb.zzbxd = Long.valueOf(com_google_android_gms_internal_zzatm.zzaxb);
                com_google_android_gms_internal_zzauw_zzb.name = com_google_android_gms_internal_zzatm.mName;
                com_google_android_gms_internal_zzauw_zzb.zzbxe = Long.valueOf(com_google_android_gms_internal_zzatm.zzbrC);
                com_google_android_gms_internal_zzauw_zzb.zzbxc = new zzc[com_google_android_gms_internal_zzatm.zzbrD.size()];
                Iterator it = com_google_android_gms_internal_zzatm.zzbrD.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzc com_google_android_gms_internal_zzauw_zzc = new zzc();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzauw_zzb.zzbxc[i2] = com_google_android_gms_internal_zzauw_zzc;
                    com_google_android_gms_internal_zzauw_zzc.name = str2;
                    zzKg().zza(com_google_android_gms_internal_zzauw_zzc, com_google_android_gms_internal_zzatm.zzbrD.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxD = zza(zzfu.zzke(), com_google_android_gms_internal_zzauw_zze.zzbxl, com_google_android_gms_internal_zzauw_zze.zzbxk);
                com_google_android_gms_internal_zzauw_zze.zzbxn = com_google_android_gms_internal_zzauw_zzb.zzbxd;
                com_google_android_gms_internal_zzauw_zze.zzbxo = com_google_android_gms_internal_zzauw_zzb.zzbxd;
                long zzKr = zzfu.zzKr();
                com_google_android_gms_internal_zzauw_zze.zzbxq = zzKr != 0 ? Long.valueOf(zzKr) : null;
                long zzKq = zzfu.zzKq();
                if (zzKq != 0) {
                    zzKr = zzKq;
                }
                com_google_android_gms_internal_zzauw_zze.zzbxp = zzKr != 0 ? Long.valueOf(zzKr) : null;
                zzfu.zzKA();
                com_google_android_gms_internal_zzauw_zze.zzbxB = Integer.valueOf((int) zzfu.zzKx());
                com_google_android_gms_internal_zzauw_zze.zzbxw = Long.valueOf(zzKm().zzKu());
                com_google_android_gms_internal_zzauw_zze.zzbxm = Long.valueOf(zznR().currentTimeMillis());
                com_google_android_gms_internal_zzauw_zze.zzbxC = Boolean.TRUE;
                zzfu.zzY(com_google_android_gms_internal_zzauw_zze.zzbxn.longValue());
                zzfu.zzZ(com_google_android_gms_internal_zzauw_zze.zzbxo.longValue());
                zzKf().zza(zzfu);
                zzKf().setTransactionSuccessful();
                zzKf().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzauw_zzd.zzaeS()];
                    zzbxm zzag = zzbxm.zzag(bArr);
                    com_google_android_gms_internal_zzauw_zzd.zza(zzag);
                    zzag.zzaeF();
                    return zzKg().zzk(bArr);
                } catch (IOException e) {
                    zzKk().zzLX().zze("Data loss. Failed to bundle and serialize. appId", zzatx.zzfE(str), e);
                    return null;
                }
            } else {
                zzKk().zzMc().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzKf().endTransaction();
                return bArr;
            }
        } finally {
            zzKf().endTransaction();
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
        String zzP = zzKm().zzP(com_google_android_gms_internal_zzatc.getGmpAppId(), com_google_android_gms_internal_zzatc.getAppInstanceId());
        try {
            URL url = new URL(zzP);
            zzKk().zzMd().zzj("Fetching remote configuration", com_google_android_gms_internal_zzatc.zzke());
            zzauv.zzb zzfL = zzKh().zzfL(com_google_android_gms_internal_zzatc.zzke());
            CharSequence zzfM = zzKh().zzfM(com_google_android_gms_internal_zzatc.zzke());
            if (!(zzfL == null || TextUtils.isEmpty(zzfM))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzfM);
            }
            zzMx().zza(com_google_android_gms_internal_zzatc.zzke(), url, map, new zza(this) {
                final /* synthetic */ zzaue zzbuJ;

                {
                    this.zzbuJ = r1;
                }

                public void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
                    this.zzbuJ.zzb(str, i, th, bArr, map);
                }
            });
        } catch (MalformedURLException e) {
            zzKk().zzLX().zze("Failed to parse config URL. Not fetching. appId", zzatx.zzfE(com_google_android_gms_internal_zzatc.zzke()), zzP);
        }
    }

    @WorkerThread
    void zzb(zzatd com_google_android_gms_internal_zzatd, long j) {
        zzmR();
        zzob();
        zzatc zzfu = zzKf().zzfu(com_google_android_gms_internal_zzatd.packageName);
        if (!(zzfu == null || !TextUtils.isEmpty(zzfu.getGmpAppId()) || com_google_android_gms_internal_zzatd == null || TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP))) {
            zzfu.zzae(0);
            zzKf().zza(zzfu);
        }
        Bundle bundle = new Bundle();
        bundle.putLong("_c", 1);
        bundle.putLong("_r", 1);
        bundle.putLong("_uwa", 0);
        bundle.putLong("_pfo", 0);
        bundle.putLong("_sys", 0);
        bundle.putLong("_sysu", 0);
        if (getContext().getPackageManager() == null) {
            zzKk().zzLX().zzj("PackageManager is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName));
        } else {
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo;
            try {
                packageInfo = zzadg.zzbi(getContext()).getPackageInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e) {
                zzKk().zzLX().zze("Package info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
                packageInfo = null;
            }
            if (!(packageInfo == null || packageInfo.firstInstallTime == 0 || packageInfo.firstInstallTime == packageInfo.lastUpdateTime)) {
                bundle.putLong("_uwa", 1);
            }
            try {
                applicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(com_google_android_gms_internal_zzatd.packageName, 0);
            } catch (NameNotFoundException e2) {
                zzKk().zzLX().zze("Application info is null, first open report might be inaccurate. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e2);
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
        long zzfA = zzKf().zzfA(com_google_android_gms_internal_zzatd.packageName);
        if (zzfA >= 0) {
            bundle.putLong("_pfo", zzfA);
        }
        zzb(new zzatq("_f", new zzato(bundle), "auto", j), com_google_android_gms_internal_zzatd);
    }

    @WorkerThread
    void zzb(zzatg com_google_android_gms_internal_zzatg, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzdr(com_google_android_gms_internal_zzatg.packageName);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbqZ);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbra);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbra.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP)) {
            if (com_google_android_gms_internal_zzatd.zzbqU) {
                zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
                zzKf().beginTransaction();
                try {
                    Object obj;
                    zzatg zzT = zzKf().zzT(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbra.name);
                    if (zzT != null && zzT.zzbrc) {
                        com_google_android_gms_internal_zzatg2.zzbqZ = zzT.zzbqZ;
                        com_google_android_gms_internal_zzatg2.zzbrb = zzT.zzbrb;
                        com_google_android_gms_internal_zzatg2.zzbrd = zzT.zzbrd;
                        com_google_android_gms_internal_zzatg2.zzbrg = zzT.zzbrg;
                        obj = null;
                    } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzatg2.zzbrd)) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg2.zzbra;
                        com_google_android_gms_internal_zzatg2.zzbra = new zzauq(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzatg2.zzbrb, com_google_android_gms_internal_zzauq.getValue(), com_google_android_gms_internal_zzauq.zzbqZ);
                        com_google_android_gms_internal_zzatg2.zzbrc = true;
                        int i = 1;
                    } else {
                        obj = null;
                    }
                    if (com_google_android_gms_internal_zzatg2.zzbrc) {
                        zzauq com_google_android_gms_internal_zzauq2 = com_google_android_gms_internal_zzatg2.zzbra;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbqZ, com_google_android_gms_internal_zzauq2.name, com_google_android_gms_internal_zzauq2.zzbwg, com_google_android_gms_internal_zzauq2.getValue());
                        if (zzKf().zza(com_google_android_gms_internal_zzaus)) {
                            zzKk().zzMc().zzd("User property updated immediately", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKk().zzLX().zzd("(2)Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (!(obj == null || com_google_android_gms_internal_zzatg2.zzbrg == null)) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg2.zzbrg, com_google_android_gms_internal_zzatg2.zzbrb), com_google_android_gms_internal_zzatd);
                        }
                    }
                    if (zzKf().zza(com_google_android_gms_internal_zzatg2)) {
                        zzKk().zzMc().zzd("Conditional property added", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbra.name, com_google_android_gms_internal_zzatg2.zzbra.getValue());
                    } else {
                        zzKk().zzLX().zzd("Too many conditional properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg2.packageName), com_google_android_gms_internal_zzatg2.zzbra.name, com_google_android_gms_internal_zzatg2.zzbra.getValue());
                    }
                    zzKf().setTransactionSuccessful();
                } finally {
                    zzKf().endTransaction();
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
        long j = com_google_android_gms_internal_zzatq.zzbrL;
        if (!zzKg().zzd(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd)) {
            return;
        }
        if (com_google_android_gms_internal_zzatd.zzbqU) {
            zzKf().beginTransaction();
            try {
                for (zzatg com_google_android_gms_internal_zzatg : zzKf().zzh(str, j)) {
                    if (com_google_android_gms_internal_zzatg != null) {
                        zzKk().zzMc().zzd("User property timed out", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbra.name, com_google_android_gms_internal_zzatg.zzbra.getValue());
                        if (com_google_android_gms_internal_zzatg.zzbre != null) {
                            zzc(new zzatq(com_google_android_gms_internal_zzatg.zzbre, j), com_google_android_gms_internal_zzatd);
                        }
                        zzKf().zzU(str, com_google_android_gms_internal_zzatg.zzbra.name);
                    }
                }
                List<zzatg> zzi = zzKf().zzi(str, j);
                List<zzatq> arrayList = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg2 : zzi) {
                    if (com_google_android_gms_internal_zzatg2 != null) {
                        zzKk().zzMc().zzd("User property expired", com_google_android_gms_internal_zzatg2.packageName, com_google_android_gms_internal_zzatg2.zzbra.name, com_google_android_gms_internal_zzatg2.zzbra.getValue());
                        zzKf().zzR(str, com_google_android_gms_internal_zzatg2.zzbra.name);
                        if (com_google_android_gms_internal_zzatg2.zzbri != null) {
                            arrayList.add(com_google_android_gms_internal_zzatg2.zzbri);
                        }
                        zzKf().zzU(str, com_google_android_gms_internal_zzatg2.zzbra.name);
                    }
                }
                for (zzatq com_google_android_gms_internal_zzatq2 : arrayList) {
                    zzc(new zzatq(com_google_android_gms_internal_zzatq2, j), com_google_android_gms_internal_zzatd);
                }
                zzi = zzKf().zzc(str, com_google_android_gms_internal_zzatq.name, j);
                List<zzatq> arrayList2 = new ArrayList(zzi.size());
                for (zzatg com_google_android_gms_internal_zzatg3 : zzi) {
                    if (com_google_android_gms_internal_zzatg3 != null) {
                        zzauq com_google_android_gms_internal_zzauq = com_google_android_gms_internal_zzatg3.zzbra;
                        zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzatg3.zzbqZ, com_google_android_gms_internal_zzauq.name, j, com_google_android_gms_internal_zzauq.getValue());
                        if (zzKf().zza(com_google_android_gms_internal_zzaus)) {
                            zzKk().zzMc().zzd("User property triggered", com_google_android_gms_internal_zzatg3.packageName, com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKk().zzLX().zzd("Too many active user properties, ignoring", zzatx.zzfE(com_google_android_gms_internal_zzatg3.packageName), com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        }
                        if (com_google_android_gms_internal_zzatg3.zzbrg != null) {
                            arrayList2.add(com_google_android_gms_internal_zzatg3.zzbrg);
                        }
                        com_google_android_gms_internal_zzatg3.zzbra = new zzauq(com_google_android_gms_internal_zzaus);
                        com_google_android_gms_internal_zzatg3.zzbrc = true;
                        zzKf().zza(com_google_android_gms_internal_zzatg3);
                    }
                }
                zzc(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd);
                for (zzatq com_google_android_gms_internal_zzatq22 : arrayList2) {
                    zzc(new zzatq(com_google_android_gms_internal_zzatq22, j), com_google_android_gms_internal_zzatd);
                }
                zzKf().setTransactionSuccessful();
            } finally {
                zzKf().endTransaction();
            }
        } else {
            zzf(com_google_android_gms_internal_zzatd);
        }
    }

    @WorkerThread
    void zzb(zzatq com_google_android_gms_internal_zzatq, String str) {
        zzatc zzfu = zzKf().zzfu(str);
        if (zzfu == null || TextUtils.isEmpty(zzfu.zzmZ())) {
            zzKk().zzMc().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKk().zzLZ().zzj("App version does not match; dropping event. appId", zzatx.zzfE(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKk().zzLZ().zzj("Could not find package. appId", zzatx.zzfE(str));
            }
        }
        zzatq com_google_android_gms_internal_zzatq2 = com_google_android_gms_internal_zzatq;
        zzb(com_google_android_gms_internal_zzatq2, new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKs(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), null, zzfu.zzKw(), false, zzfu.zzKp(), zzfu.zzuW()));
    }

    void zzb(zzauh com_google_android_gms_internal_zzauh) {
        this.zzbuF++;
    }

    @WorkerThread
    void zzb(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        int i = 0;
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP)) {
            if (com_google_android_gms_internal_zzatd.zzbqU) {
                int zzfX = zzKg().zzfX(com_google_android_gms_internal_zzauq.name);
                String zza;
                if (zzfX != 0) {
                    zza = zzKg().zza(com_google_android_gms_internal_zzauq.name, zzKm().zzKM(), true);
                    if (com_google_android_gms_internal_zzauq.name != null) {
                        i = com_google_android_gms_internal_zzauq.name.length();
                    }
                    zzKg().zza(zzfX, "_ev", zza, i);
                    return;
                }
                zzfX = zzKg().zzm(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzfX != 0) {
                    zza = zzKg().zza(com_google_android_gms_internal_zzauq.name, zzKm().zzKM(), true);
                    Object value = com_google_android_gms_internal_zzauq.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzKg().zza(zzfX, "_ev", zza, i);
                    return;
                }
                Object zzn = zzKg().zzn(com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.getValue());
                if (zzn != null) {
                    zzaus com_google_android_gms_internal_zzaus = new zzaus(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.zzbqZ, com_google_android_gms_internal_zzauq.name, com_google_android_gms_internal_zzauq.zzbwg, zzn);
                    zzKk().zzMc().zze("Setting user property", com_google_android_gms_internal_zzaus.mName, zzn);
                    zzKf().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzatd);
                        boolean zza2 = zzKf().zza(com_google_android_gms_internal_zzaus);
                        zzKf().setTransactionSuccessful();
                        if (zza2) {
                            zzKk().zzMc().zze("User property set", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                        } else {
                            zzKk().zzLX().zze("Too many unique user properties are set. Ignoring user property", com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.mValue);
                            zzKg().zza(9, null, null, 0);
                        }
                        zzKf().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzKf().endTransaction();
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
        zzKf().beginTransaction();
        try {
            zzatc zzfu = zzKf().zzfu(str);
            int i3 = ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null) ? 1 : 0;
            if (zzfu == null) {
                zzKk().zzLZ().zzj("App does not exist in onConfigFetched. appId", zzatx.zzfE(str));
            } else if (i3 != 0 || i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS) {
                List list = map != null ? (List) map.get("Last-Modified") : null;
                String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
                if (i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS || i == 304) {
                    if (zzKh().zzfL(str) == null && !zzKh().zzb(str, null, null)) {
                        zzKf().endTransaction();
                        return;
                    }
                } else if (!zzKh().zzb(str, bArr, str2)) {
                    zzKf().endTransaction();
                    return;
                }
                zzfu.zzae(zznR().currentTimeMillis());
                zzKf().zza(zzfu);
                if (i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS) {
                    zzKk().zzMa().zzj("Config not found. Using empty config. appId", str);
                } else {
                    zzKk().zzMd().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                }
                if (zzMx().zzqa() && zzMG()) {
                    zzMF();
                } else {
                    zzMH();
                }
            } else {
                zzfu.zzaf(zznR().currentTimeMillis());
                zzKf().zza(zzfu);
                zzKk().zzMd().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
                zzKh().zzfN(str);
                zzKl().zzbte.set(zznR().currentTimeMillis());
                if (i == 503 || i == 429) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    zzKl().zzbtf.set(zznR().currentTimeMillis());
                }
                zzMH();
            }
            zzKf().setTransactionSuccessful();
        } finally {
            zzKf().endTransaction();
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
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbra);
        zzac.zzdr(com_google_android_gms_internal_zzatg.zzbra.name);
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP)) {
            if (com_google_android_gms_internal_zzatd.zzbqU) {
                zzKf().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzatg zzT = zzKf().zzT(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbra.name);
                    if (zzT != null) {
                        zzKk().zzMc().zze("Removing conditional user property", com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbra.name);
                        zzKf().zzU(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbra.name);
                        if (zzT.zzbrc) {
                            zzKf().zzR(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbra.name);
                        }
                        if (com_google_android_gms_internal_zzatg.zzbri != null) {
                            Bundle bundle = null;
                            if (com_google_android_gms_internal_zzatg.zzbri.zzbrK != null) {
                                bundle = com_google_android_gms_internal_zzatg.zzbri.zzbrK.zzLV();
                            }
                            zzc(zzKg().zza(com_google_android_gms_internal_zzatg.zzbri.name, bundle, zzT.zzbqZ, com_google_android_gms_internal_zzatg.zzbri.zzbrL, true, false), com_google_android_gms_internal_zzatd);
                        }
                    } else {
                        zzKk().zzLZ().zze("Conditional user property doesn't exist", zzatx.zzfE(com_google_android_gms_internal_zzatg.packageName), com_google_android_gms_internal_zzatg.zzbra.name);
                    }
                    zzKf().setTransactionSuccessful();
                } finally {
                    zzKf().endTransaction();
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
        if (!zzKg().zzd(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd)) {
            return;
        }
        if (!com_google_android_gms_internal_zzatd.zzbqU) {
            zzf(com_google_android_gms_internal_zzatd);
        } else if (zzKh().zzaa(str, com_google_android_gms_internal_zzatq.name)) {
            zzKk().zzLZ().zze("Dropping blacklisted event. appId", zzatx.zzfE(str), com_google_android_gms_internal_zzatq.name);
            r2 = (zzKg().zzgg(str) || zzKg().zzgh(str)) ? 1 : null;
            if (r2 == null && !"_err".equals(com_google_android_gms_internal_zzatq.name)) {
                zzKg().zza(11, "_ev", com_google_android_gms_internal_zzatq.name, 0);
            }
            if (r2 != null) {
                zzatc zzfu = zzKf().zzfu(str);
                if (zzfu != null) {
                    if (Math.abs(zznR().currentTimeMillis() - Math.max(zzfu.zzKz(), zzfu.zzKy())) > zzKm().zzLk()) {
                        zzKk().zzMc().log("Fetching config for blacklisted app");
                        zzb(zzfu);
                    }
                }
            }
        } else {
            if (zzKk().zzak(2)) {
                zzKk().zzMd().zzj("Logging event", com_google_android_gms_internal_zzatq);
            }
            zzKf().beginTransaction();
            try {
                Bundle zzLV = com_google_android_gms_internal_zzatq.zzbrK.zzLV();
                zzf(com_google_android_gms_internal_zzatd);
                if ("_iap".equals(com_google_android_gms_internal_zzatq.name) || Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatq.name)) {
                    long round;
                    r2 = zzLV.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzatq.name)) {
                        double d = zzLV.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzLV.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzKk().zzLZ().zze("Data lost. Currency value is too big. appId", zzatx.zzfE(str), Double.valueOf(d));
                            zzKf().setTransactionSuccessful();
                            zzKf().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzLV.getLong(Param.VALUE);
                    }
                    if (!TextUtils.isEmpty(r2)) {
                        String toUpperCase = r2.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzaus zzS = zzKf().zzS(str, concat);
                            if (zzS == null || !(zzS.mValue instanceof Long)) {
                                zzKf().zzz(str, zzKm().zzfn(str) - 1);
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqZ, concat, zznR().currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzS = new zzaus(str, com_google_android_gms_internal_zzatq.zzbqZ, concat, zznR().currentTimeMillis(), Long.valueOf(round + ((Long) zzS.mValue).longValue()));
                            }
                            if (!zzKf().zza(zzS)) {
                                zzKk().zzLX().zzd("Too many unique user properties are set. Ignoring user property. appId", zzatx.zzfE(str), zzS.mName, zzS.mValue);
                                zzKg().zza(9, null, null, 0);
                            }
                        }
                    }
                }
                boolean zzfT = zzaut.zzfT(com_google_android_gms_internal_zzatq.name);
                boolean equals = "_err".equals(com_google_android_gms_internal_zzatq.name);
                com.google.android.gms.internal.zzatj.zza zza = zzKf().zza(zzMD(), str, true, zzfT, false, equals, false);
                long zzKT = zza.zzbrs - zzKm().zzKT();
                if (zzKT > 0) {
                    if (zzKT % 1000 == 1) {
                        zzKk().zzLX().zze("Data loss. Too many events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrs));
                    }
                    zzKg().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                    zzKf().setTransactionSuccessful();
                    return;
                }
                zzatn com_google_android_gms_internal_zzatn;
                if (zzfT) {
                    zzKT = zza.zzbrr - zzKm().zzKU();
                    if (zzKT > 0) {
                        if (zzKT % 1000 == 1) {
                            zzKk().zzLX().zze("Data loss. Too many public events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbrr));
                        }
                        zzKg().zza(16, "_ev", com_google_android_gms_internal_zzatq.name, 0);
                        zzKf().setTransactionSuccessful();
                        zzKf().endTransaction();
                        return;
                    }
                }
                if (equals) {
                    zzKT = zza.zzbru - ((long) zzKm().zzfj(com_google_android_gms_internal_zzatd.packageName));
                    if (zzKT > 0) {
                        if (zzKT == 1) {
                            zzKk().zzLX().zze("Too many error events logged. appId, count", zzatx.zzfE(str), Long.valueOf(zza.zzbru));
                        }
                        zzKf().setTransactionSuccessful();
                        zzKf().endTransaction();
                        return;
                    }
                }
                zzKg().zza(zzLV, "_o", com_google_android_gms_internal_zzatq.zzbqZ);
                if (zzKg().zzge(str)) {
                    zzKg().zza(zzLV, "_dbg", Long.valueOf(1));
                    zzKg().zza(zzLV, "_r", Long.valueOf(1));
                }
                zzKT = zzKf().zzfv(str);
                if (zzKT > 0) {
                    zzKk().zzLZ().zze("Data lost. Too many events stored on disk, deleted. appId", zzatx.zzfE(str), Long.valueOf(zzKT));
                }
                zzatm com_google_android_gms_internal_zzatm = new zzatm(this, com_google_android_gms_internal_zzatq.zzbqZ, str, com_google_android_gms_internal_zzatq.name, com_google_android_gms_internal_zzatq.zzbrL, 0, zzLV);
                zzatn zzQ = zzKf().zzQ(str, com_google_android_gms_internal_zzatm.mName);
                if (zzQ == null) {
                    long zzfC = zzKf().zzfC(str);
                    zzKm().zzKS();
                    if (zzfC >= 500) {
                        zzKk().zzLX().zzd("Too many event names used, ignoring event. appId, name, supported count", zzatx.zzfE(str), com_google_android_gms_internal_zzatm.mName, Integer.valueOf(zzKm().zzKS()));
                        zzKg().zza(8, null, null, 0);
                        zzKf().endTransaction();
                        return;
                    }
                    com_google_android_gms_internal_zzatn = new zzatn(str, com_google_android_gms_internal_zzatm.mName, 0, 0, com_google_android_gms_internal_zzatm.zzaxb);
                } else {
                    com_google_android_gms_internal_zzatm = com_google_android_gms_internal_zzatm.zza(this, zzQ.zzbrG);
                    com_google_android_gms_internal_zzatn = zzQ.zzap(com_google_android_gms_internal_zzatm.zzaxb);
                }
                zzKf().zza(com_google_android_gms_internal_zzatn);
                zza(com_google_android_gms_internal_zzatm, com_google_android_gms_internal_zzatd);
                zzKf().setTransactionSuccessful();
                if (zzKk().zzak(2)) {
                    zzKk().zzMd().zzj("Event recorded", com_google_android_gms_internal_zzatm);
                }
                zzKf().endTransaction();
                zzMH();
                zzKk().zzMd().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
            } finally {
                zzKf().endTransaction();
            }
        }
    }

    @WorkerThread
    void zzc(zzauq com_google_android_gms_internal_zzauq, zzatd com_google_android_gms_internal_zzatd) {
        zzmR();
        zzob();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP)) {
            if (com_google_android_gms_internal_zzatd.zzbqU) {
                zzKk().zzMc().zzj("Removing user property", com_google_android_gms_internal_zzauq.name);
                zzKf().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzatd);
                    zzKf().zzR(com_google_android_gms_internal_zzatd.packageName, com_google_android_gms_internal_zzauq.name);
                    zzKf().setTransactionSuccessful();
                    zzKk().zzMc().zzj("User property removed", com_google_android_gms_internal_zzauq.name);
                } finally {
                    zzKf().endTransaction();
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
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqP)) {
            if (com_google_android_gms_internal_zzatd.zzbqU) {
                long currentTimeMillis = zznR().currentTimeMillis();
                zzKf().beginTransaction();
                try {
                    zza(com_google_android_gms_internal_zzatd, currentTimeMillis);
                    zzf(com_google_android_gms_internal_zzatd);
                    if (zzKf().zzQ(com_google_android_gms_internal_zzatd.packageName, "_f") == null) {
                        zzb(new zzauq("_fot", currentTimeMillis, Long.valueOf((1 + (currentTimeMillis / 3600000)) * 3600000), "auto"), com_google_android_gms_internal_zzatd);
                        zzb(com_google_android_gms_internal_zzatd, currentTimeMillis);
                        zzc(com_google_android_gms_internal_zzatd, currentTimeMillis);
                    } else if (com_google_android_gms_internal_zzatd.zzbqV) {
                        zzd(com_google_android_gms_internal_zzatd, currentTimeMillis);
                    }
                    zzKf().setTransactionSuccessful();
                } finally {
                    zzKf().endTransaction();
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
        zzatc zzfu = zzKf().zzfu(str);
        if (zzfu == null || TextUtils.isEmpty(zzfu.zzmZ())) {
            zzKk().zzMc().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzadg.zzbi(getContext()).getPackageInfo(str, 0).versionName;
            if (!(zzfu.zzmZ() == null || zzfu.zzmZ().equals(str2))) {
                zzKk().zzLZ().zzj("App version does not match; dropping. appId", zzatx.zzfE(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzatd(str, zzfu.getGmpAppId(), zzfu.zzmZ(), zzfu.zzKs(), zzfu.zzKt(), zzfu.zzKu(), zzfu.zzKv(), null, zzfu.zzKw(), false, zzfu.zzKp(), zzfu.zzuW());
    }

    public String zzfP(final String str) {
        Object e;
        try {
            return (String) zzKj().zzd(new Callable<String>(this) {
                final /* synthetic */ zzaue zzbuJ;

                public /* synthetic */ Object call() throws Exception {
                    return zzbY();
                }

                public String zzbY() throws Exception {
                    zzatc zzfu = this.zzbuJ.zzKf().zzfu(str);
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
        zzKk().zzLX().zze("Failed to get app instance id. appId", zzatx.zzfE(str), e);
        return null;
    }

    @WorkerThread
    public void zzmR() {
        zzKj().zzmR();
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
            zzKk().zzLX().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            return false;
        }
        if (i < i2) {
            if (zza(i2, zzMA())) {
                zzKk().zzMd().zze("Storage version upgraded. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                zzKk().zzLX().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(i), Integer.valueOf(i2));
                return false;
            }
        }
        return true;
    }
}
