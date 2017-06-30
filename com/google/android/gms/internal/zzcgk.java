package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzi;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzcgk {
    private static volatile zzcgk zzbsm;
    private final Context mContext;
    private final boolean zzafK;
    private final zzchy zzbsA;
    private final zzcic zzbsB;
    private final zzces zzbsC;
    private final zzchk zzbsD;
    private final zzcff zzbsE;
    private final zzcft zzbsF;
    private final zzcjf zzbsG;
    private final zzcei zzbsH;
    private final zzceb zzbsI;
    private boolean zzbsJ;
    private Boolean zzbsK;
    private long zzbsL;
    private FileLock zzbsM;
    private FileChannel zzbsN;
    private List<Long> zzbsO;
    private List<Runnable> zzbsP;
    private int zzbsQ;
    private int zzbsR;
    private long zzbsS = -1;
    private long zzbsT;
    private boolean zzbsU;
    private boolean zzbsV;
    private boolean zzbsW;
    private final long zzbsX = this.zzvy.currentTimeMillis();
    private final zzcel zzbsn = new zzcel(this);
    private final zzcfv zzbso;
    private final zzcfk zzbsp;
    private final zzcgf zzbsq;
    private final zzciz zzbsr;
    private final zzcge zzbss;
    private final AppMeasurement zzbst;
    private final FirebaseAnalytics zzbsu;
    private final zzcjk zzbsv;
    private final zzcfi zzbsw;
    private final zzcem zzbsx;
    private final zzcfg zzbsy;
    private final zzcfo zzbsz;
    private final zze zzvy = zzi.zzrY();

    class zza implements zzceo {
        private /* synthetic */ zzcgk zzbsY;
        zzcjy zzbsZ;
        List<Long> zzbta;
        private long zzbtb;
        List<zzcjv> zztJ;

        private zza(zzcgk com_google_android_gms_internal_zzcgk) {
            this.zzbsY = com_google_android_gms_internal_zzcgk;
        }

        private static long zza(zzcjv com_google_android_gms_internal_zzcjv) {
            return ((com_google_android_gms_internal_zzcjv.zzbvx.longValue() / 1000) / 60) / 60;
        }

        public final boolean zza(long j, zzcjv com_google_android_gms_internal_zzcjv) {
            zzbo.zzu(com_google_android_gms_internal_zzcjv);
            if (this.zztJ == null) {
                this.zztJ = new ArrayList();
            }
            if (this.zzbta == null) {
                this.zzbta = new ArrayList();
            }
            if (this.zztJ.size() > 0 && zza((zzcjv) this.zztJ.get(0)) != zza(com_google_android_gms_internal_zzcjv)) {
                return false;
            }
            long zzLT = this.zzbtb + ((long) com_google_android_gms_internal_zzcjv.zzLT());
            if (zzLT >= ((long) zzcel.zzxL())) {
                return false;
            }
            this.zzbtb = zzLT;
            this.zztJ.add(com_google_android_gms_internal_zzcjv);
            this.zzbta.add(Long.valueOf(j));
            return this.zztJ.size() < zzcel.zzxM();
        }

        public final void zzb(zzcjy com_google_android_gms_internal_zzcjy) {
            zzbo.zzu(com_google_android_gms_internal_zzcjy);
            this.zzbsZ = com_google_android_gms_internal_zzcjy;
        }
    }

    private zzcgk(zzchj com_google_android_gms_internal_zzchj) {
        zzcfm zzyB;
        zzbo.zzu(com_google_android_gms_internal_zzchj);
        this.mContext = com_google_android_gms_internal_zzchj.mContext;
        zzcfv com_google_android_gms_internal_zzcfv = new zzcfv(this);
        com_google_android_gms_internal_zzcfv.initialize();
        this.zzbso = com_google_android_gms_internal_zzcfv;
        zzcfk com_google_android_gms_internal_zzcfk = new zzcfk(this);
        com_google_android_gms_internal_zzcfk.initialize();
        this.zzbsp = com_google_android_gms_internal_zzcfk;
        zzwF().zzyB().zzj("App measurement is starting up, version", Long.valueOf(zzcel.zzwP()));
        zzcel.zzxE();
        zzwF().zzyB().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzcjk com_google_android_gms_internal_zzcjk = new zzcjk(this);
        com_google_android_gms_internal_zzcjk.initialize();
        this.zzbsv = com_google_android_gms_internal_zzcjk;
        zzcfi com_google_android_gms_internal_zzcfi = new zzcfi(this);
        com_google_android_gms_internal_zzcfi.initialize();
        this.zzbsw = com_google_android_gms_internal_zzcfi;
        zzces com_google_android_gms_internal_zzces = new zzces(this);
        com_google_android_gms_internal_zzces.initialize();
        this.zzbsC = com_google_android_gms_internal_zzces;
        zzcff com_google_android_gms_internal_zzcff = new zzcff(this);
        com_google_android_gms_internal_zzcff.initialize();
        this.zzbsE = com_google_android_gms_internal_zzcff;
        zzcel.zzxE();
        String zzhl = com_google_android_gms_internal_zzcff.zzhl();
        if (zzwB().zzey(zzhl)) {
            zzyB = zzwF().zzyB();
            zzhl = "Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.";
        } else {
            zzyB = zzwF().zzyB();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzhl = String.valueOf(zzhl);
            zzhl = zzhl.length() != 0 ? str.concat(zzhl) : new String(str);
        }
        zzyB.log(zzhl);
        zzwF().zzyC().log("Debug-level message logging enabled");
        zzcem com_google_android_gms_internal_zzcem = new zzcem(this);
        com_google_android_gms_internal_zzcem.initialize();
        this.zzbsx = com_google_android_gms_internal_zzcem;
        zzcfg com_google_android_gms_internal_zzcfg = new zzcfg(this);
        com_google_android_gms_internal_zzcfg.initialize();
        this.zzbsy = com_google_android_gms_internal_zzcfg;
        zzcei com_google_android_gms_internal_zzcei = new zzcei(this);
        com_google_android_gms_internal_zzcei.initialize();
        this.zzbsH = com_google_android_gms_internal_zzcei;
        this.zzbsI = new zzceb(this);
        zzcfo com_google_android_gms_internal_zzcfo = new zzcfo(this);
        com_google_android_gms_internal_zzcfo.initialize();
        this.zzbsz = com_google_android_gms_internal_zzcfo;
        zzchy com_google_android_gms_internal_zzchy = new zzchy(this);
        com_google_android_gms_internal_zzchy.initialize();
        this.zzbsA = com_google_android_gms_internal_zzchy;
        zzcic com_google_android_gms_internal_zzcic = new zzcic(this);
        com_google_android_gms_internal_zzcic.initialize();
        this.zzbsB = com_google_android_gms_internal_zzcic;
        zzchk com_google_android_gms_internal_zzchk = new zzchk(this);
        com_google_android_gms_internal_zzchk.initialize();
        this.zzbsD = com_google_android_gms_internal_zzchk;
        zzcjf com_google_android_gms_internal_zzcjf = new zzcjf(this);
        com_google_android_gms_internal_zzcjf.initialize();
        this.zzbsG = com_google_android_gms_internal_zzcjf;
        this.zzbsF = new zzcft(this);
        this.zzbst = new AppMeasurement(this);
        this.zzbsu = new FirebaseAnalytics(this);
        zzciz com_google_android_gms_internal_zzciz = new zzciz(this);
        com_google_android_gms_internal_zzciz.initialize();
        this.zzbsr = com_google_android_gms_internal_zzciz;
        zzcge com_google_android_gms_internal_zzcge = new zzcge(this);
        com_google_android_gms_internal_zzcge.initialize();
        this.zzbss = com_google_android_gms_internal_zzcge;
        zzcgf com_google_android_gms_internal_zzcgf = new zzcgf(this);
        com_google_android_gms_internal_zzcgf.initialize();
        this.zzbsq = com_google_android_gms_internal_zzcgf;
        if (this.zzbsQ != this.zzbsR) {
            zzwF().zzyx().zze("Not all components initialized", Integer.valueOf(this.zzbsQ), Integer.valueOf(this.zzbsR));
        }
        this.zzafK = true;
        zzcel.zzxE();
        if (this.mContext.getApplicationContext() instanceof Application) {
            zzchk zzwt = zzwt();
            if (zzwt.getContext().getApplicationContext() instanceof Application) {
                Application application = (Application) zzwt.getContext().getApplicationContext();
                if (zzwt.zzbto == null) {
                    zzwt.zzbto = new zzchx(zzwt);
                }
                application.unregisterActivityLifecycleCallbacks(zzwt.zzbto);
                application.registerActivityLifecycleCallbacks(zzwt.zzbto);
                zzwt.zzwF().zzyD().log("Registered activity lifecycle callback");
            }
        } else {
            zzwF().zzyz().log("Application context is not an Application");
        }
        this.zzbsq.zzj(new zzcgl(this));
    }

    @WorkerThread
    private final int zza(FileChannel fileChannel) {
        int i = 0;
        zzwE().zzjC();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzwF().zzyx().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzwF().zzyz().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzwF().zzyx().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    private final void zza(zzcet com_google_android_gms_internal_zzcet, zzceg com_google_android_gms_internal_zzceg) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzcet);
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzbo.zzcF(com_google_android_gms_internal_zzcet.mAppId);
        zzbo.zzaf(com_google_android_gms_internal_zzcet.mAppId.equals(com_google_android_gms_internal_zzceg.packageName));
        zzcjy com_google_android_gms_internal_zzcjy = new zzcjy();
        com_google_android_gms_internal_zzcjy.zzbvD = Integer.valueOf(1);
        com_google_android_gms_internal_zzcjy.zzbvL = "android";
        com_google_android_gms_internal_zzcjy.zzaJ = com_google_android_gms_internal_zzceg.packageName;
        com_google_android_gms_internal_zzcjy.zzboR = com_google_android_gms_internal_zzceg.zzboR;
        com_google_android_gms_internal_zzcjy.zzbgW = com_google_android_gms_internal_zzceg.zzbgW;
        com_google_android_gms_internal_zzcjy.zzbvY = com_google_android_gms_internal_zzceg.zzboX == -2147483648L ? null : Integer.valueOf((int) com_google_android_gms_internal_zzceg.zzboX);
        com_google_android_gms_internal_zzcjy.zzbvP = Long.valueOf(com_google_android_gms_internal_zzceg.zzboS);
        com_google_android_gms_internal_zzcjy.zzboQ = com_google_android_gms_internal_zzceg.zzboQ;
        com_google_android_gms_internal_zzcjy.zzbvU = com_google_android_gms_internal_zzceg.zzboT == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzceg.zzboT);
        Pair zzeb = zzwG().zzeb(com_google_android_gms_internal_zzceg.packageName);
        if (!(zzeb == null || TextUtils.isEmpty((CharSequence) zzeb.first))) {
            com_google_android_gms_internal_zzcjy.zzbvR = (String) zzeb.first;
            com_google_android_gms_internal_zzcjy.zzbvS = (Boolean) zzeb.second;
        }
        zzwv().zzkD();
        com_google_android_gms_internal_zzcjy.zzbvM = Build.MODEL;
        zzwv().zzkD();
        com_google_android_gms_internal_zzcjy.zzba = VERSION.RELEASE;
        com_google_android_gms_internal_zzcjy.zzbvO = Integer.valueOf((int) zzwv().zzyq());
        com_google_android_gms_internal_zzcjy.zzbvN = zzwv().zzyr();
        com_google_android_gms_internal_zzcjy.zzbvQ = null;
        com_google_android_gms_internal_zzcjy.zzbvG = null;
        com_google_android_gms_internal_zzcjy.zzbvH = null;
        com_google_android_gms_internal_zzcjy.zzbvI = null;
        com_google_android_gms_internal_zzcjy.zzbwc = Long.valueOf(com_google_android_gms_internal_zzceg.zzboZ);
        if (isEnabled() && zzcel.zzyb()) {
            zzwu();
            com_google_android_gms_internal_zzcjy.zzbwd = null;
        }
        zzcef zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceg.packageName);
        if (zzdQ == null) {
            zzdQ = new zzcef(this, com_google_android_gms_internal_zzceg.packageName);
            zzdQ.zzdG(zzwu().zzyu());
            zzdQ.zzdJ(com_google_android_gms_internal_zzceg.zzboY);
            zzdQ.zzdH(com_google_android_gms_internal_zzceg.zzboQ);
            zzdQ.zzdI(zzwG().zzec(com_google_android_gms_internal_zzceg.packageName));
            zzdQ.zzQ(0);
            zzdQ.zzL(0);
            zzdQ.zzM(0);
            zzdQ.setAppVersion(com_google_android_gms_internal_zzceg.zzbgW);
            zzdQ.zzN(com_google_android_gms_internal_zzceg.zzboX);
            zzdQ.zzdK(com_google_android_gms_internal_zzceg.zzboR);
            zzdQ.zzO(com_google_android_gms_internal_zzceg.zzboS);
            zzdQ.zzP(com_google_android_gms_internal_zzceg.zzboT);
            zzdQ.setMeasurementEnabled(com_google_android_gms_internal_zzceg.zzboV);
            zzdQ.zzZ(com_google_android_gms_internal_zzceg.zzboZ);
            zzwz().zza(zzdQ);
        }
        com_google_android_gms_internal_zzcjy.zzbvT = zzdQ.getAppInstanceId();
        com_google_android_gms_internal_zzcjy.zzboY = zzdQ.zzwK();
        List zzdP = zzwz().zzdP(com_google_android_gms_internal_zzceg.packageName);
        com_google_android_gms_internal_zzcjy.zzbvF = new zzcka[zzdP.size()];
        for (int i = 0; i < zzdP.size(); i++) {
            zzcka com_google_android_gms_internal_zzcka = new zzcka();
            com_google_android_gms_internal_zzcjy.zzbvF[i] = com_google_android_gms_internal_zzcka;
            com_google_android_gms_internal_zzcka.name = ((zzcjj) zzdP.get(i)).mName;
            com_google_android_gms_internal_zzcka.zzbwh = Long.valueOf(((zzcjj) zzdP.get(i)).zzbuC);
            zzwB().zza(com_google_android_gms_internal_zzcka, ((zzcjj) zzdP.get(i)).mValue);
        }
        try {
            boolean z;
            long zza = zzwz().zza(com_google_android_gms_internal_zzcjy);
            zzcem zzwz = zzwz();
            if (com_google_android_gms_internal_zzcet.zzbpF != null) {
                Iterator it = com_google_android_gms_internal_zzcet.zzbpF.iterator();
                while (it.hasNext()) {
                    if ("_r".equals((String) it.next())) {
                        z = true;
                        break;
                    }
                }
                z = zzwC().zzO(com_google_android_gms_internal_zzcet.mAppId, com_google_android_gms_internal_zzcet.mName);
                zzcen zza2 = zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcet.mAppId, false, false, false, false, false);
                if (z && zza2.zzbpy < ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcet.mAppId))) {
                    z = true;
                    if (zzwz.zza(com_google_android_gms_internal_zzcet, zza, z)) {
                        this.zzbsT = 0;
                    }
                }
            }
            z = false;
            if (zzwz.zza(com_google_android_gms_internal_zzcet, zza, z)) {
                this.zzbsT = 0;
            }
        } catch (IOException e) {
            zzwF().zzyx().zze("Data loss. Failed to insert raw event metadata. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), e);
        }
    }

    private static void zza(zzchh com_google_android_gms_internal_zzchh) {
        if (com_google_android_gms_internal_zzchh == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private static void zza(zzchi com_google_android_gms_internal_zzchi) {
        if (com_google_android_gms_internal_zzchi == null) {
            throw new IllegalStateException("Component not created");
        } else if (!com_google_android_gms_internal_zzchi.isInitialized()) {
            throw new IllegalStateException("Component not initialized");
        }
    }

    @WorkerThread
    private final boolean zza(int i, FileChannel fileChannel) {
        zzwE().zzjC();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzwF().zzyx().log("Bad chanel to read from");
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
            zzwF().zzyx().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzwF().zzyx().zzj("Failed to write to channel", e);
            return false;
        }
    }

    private final zzcju[] zza(String str, zzcka[] com_google_android_gms_internal_zzckaArr, zzcjv[] com_google_android_gms_internal_zzcjvArr) {
        zzbo.zzcF(str);
        return zzws().zza(str, com_google_android_gms_internal_zzcjvArr, com_google_android_gms_internal_zzckaArr);
    }

    @WorkerThread
    private final void zzb(zzcef com_google_android_gms_internal_zzcef) {
        Map map = null;
        zzwE().zzjC();
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzcef.getGmpAppId())) {
            zzb(com_google_android_gms_internal_zzcef.zzhl(), 204, null, null, null);
            return;
        }
        String gmpAppId = com_google_android_gms_internal_zzcef.getGmpAppId();
        String appInstanceId = com_google_android_gms_internal_zzcef.getAppInstanceId();
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzcfa.zzbpZ.get()).encodedAuthority((String) zzcfa.zzbqa.get());
        String str = "config/app/";
        String valueOf = String.valueOf(gmpAppId);
        encodedAuthority.path(valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).appendQueryParameter("app_instance_id", appInstanceId).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", "11011");
        valueOf = builder.build().toString();
        try {
            URL url = new URL(valueOf);
            zzwF().zzyD().zzj("Fetching remote configuration", com_google_android_gms_internal_zzcef.zzhl());
            zzcjs zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcef.zzhl());
            CharSequence zzei = zzwC().zzei(com_google_android_gms_internal_zzcef.zzhl());
            if (!(zzeh == null || TextUtils.isEmpty(zzei))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzei);
            }
            this.zzbsU = true;
            zzyU().zza(com_google_android_gms_internal_zzcef.zzhl(), url, map, new zzcgo(this));
        } catch (MalformedURLException e) {
            zzwF().zzyx().zze("Failed to parse config URL. Not fetching. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcef.zzhl()), valueOf);
        }
    }

    public static zzcgk zzbj(Context context) {
        zzbo.zzu(context);
        zzbo.zzu(context.getApplicationContext());
        if (zzbsm == null) {
            synchronized (zzcgk.class) {
                if (zzbsm == null) {
                    zzbsm = new zzcgk(new zzchj(context));
                }
            }
        }
        return zzbsm;
    }

    @WorkerThread
    private final void zzc(zzcey com_google_android_gms_internal_zzcey, zzceg com_google_android_gms_internal_zzceg) {
        zzcem zzwz;
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzbo.zzcF(com_google_android_gms_internal_zzceg.packageName);
        long nanoTime = System.nanoTime();
        zzwE().zzjC();
        zzkD();
        String str = com_google_android_gms_internal_zzceg.packageName;
        zzwB();
        if (!zzcjk.zzd(com_google_android_gms_internal_zzcey, com_google_android_gms_internal_zzceg)) {
            return;
        }
        if (!com_google_android_gms_internal_zzceg.zzboV) {
            zzf(com_google_android_gms_internal_zzceg);
        } else if (zzwC().zzN(str, com_google_android_gms_internal_zzcey.name)) {
            zzwF().zzyz().zze("Dropping blacklisted event. appId", zzcfk.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzcey.name));
            Object obj = (zzwB().zzeA(str) || zzwB().zzeB(str)) ? 1 : null;
            if (obj == null && !"_err".equals(com_google_android_gms_internal_zzcey.name)) {
                zzwB().zza(str, 11, "_ev", com_google_android_gms_internal_zzcey.name, 0);
            }
            if (obj != null) {
                zzcef zzdQ = zzwz().zzdQ(str);
                if (zzdQ != null) {
                    if (Math.abs(this.zzvy.currentTimeMillis() - Math.max(zzdQ.zzwU(), zzdQ.zzwT())) > zzcel.zzxI()) {
                        zzwF().zzyC().log("Fetching config for blacklisted app");
                        zzb(zzdQ);
                    }
                }
            }
        } else {
            Bundle zzyt;
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Logging event", zzwA().zzb(com_google_android_gms_internal_zzcey));
            }
            zzwz().beginTransaction();
            try {
                zzyt = com_google_android_gms_internal_zzcey.zzbpM.zzyt();
                zzf(com_google_android_gms_internal_zzceg);
                if ("_iap".equals(com_google_android_gms_internal_zzcey.name) || Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzcey.name)) {
                    long round;
                    Object string = zzyt.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzcey.name)) {
                        double d = zzyt.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzyt.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzwF().zzyz().zze("Data lost. Currency value is too big. appId", zzcfk.zzdZ(str), Double.valueOf(d));
                            zzwz().setTransactionSuccessful();
                            zzwz().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzyt.getLong(Param.VALUE);
                    }
                    if (!TextUtils.isEmpty(string)) {
                        String toUpperCase = string.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzcjj zzG = zzwz().zzG(str, concat);
                            if (zzG == null || !(zzG.mValue instanceof Long)) {
                                zzwz = zzwz();
                                int zzb = this.zzbsn.zzb(str, zzcfa.zzbqz) - 1;
                                zzbo.zzcF(str);
                                zzwz.zzjC();
                                zzwz.zzkD();
                                zzwz.getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(zzb)});
                                zzG = new zzcjj(str, com_google_android_gms_internal_zzcey.zzbpc, concat, this.zzvy.currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzG = new zzcjj(str, com_google_android_gms_internal_zzcey.zzbpc, concat, this.zzvy.currentTimeMillis(), Long.valueOf(round + ((Long) zzG.mValue).longValue()));
                            }
                            if (!zzwz().zza(zzG)) {
                                zzwF().zzyx().zzd("Too many unique user properties are set. Ignoring user property. appId", zzcfk.zzdZ(str), zzwA().zzdY(zzG.mName), zzG.mValue);
                                zzwB().zza(str, 9, null, null, 0);
                            }
                        }
                    }
                }
            } catch (SQLiteException e) {
                zzwz.zzwF().zzyx().zze("Error pruning currencies. appId", zzcfk.zzdZ(str), e);
            } catch (Throwable th) {
                zzwz().endTransaction();
            }
            boolean zzeo = zzcjk.zzeo(com_google_android_gms_internal_zzcey.name);
            boolean equals = "_err".equals(com_google_android_gms_internal_zzcey.name);
            zzcen zza = zzwz().zza(zzyZ(), str, true, zzeo, false, equals, false);
            long zzxq = zza.zzbpv - zzcel.zzxq();
            if (zzxq > 0) {
                if (zzxq % 1000 == 1) {
                    zzwF().zzyx().zze("Data loss. Too many events logged. appId, count", zzcfk.zzdZ(str), Long.valueOf(zza.zzbpv));
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzceu com_google_android_gms_internal_zzceu;
            if (zzeo) {
                zzxq = zza.zzbpu - zzcel.zzxr();
                if (zzxq > 0) {
                    if (zzxq % 1000 == 1) {
                        zzwF().zzyx().zze("Data loss. Too many public events logged. appId, count", zzcfk.zzdZ(str), Long.valueOf(zza.zzbpu));
                    }
                    zzwB().zza(str, 16, "_ev", com_google_android_gms_internal_zzcey.name, 0);
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            if (equals) {
                zzxq = zza.zzbpx - ((long) Math.max(0, Math.min(1000000, this.zzbsn.zzb(com_google_android_gms_internal_zzceg.packageName, zzcfa.zzbqg))));
                if (zzxq > 0) {
                    if (zzxq == 1) {
                        zzwF().zzyx().zze("Too many error events logged. appId, count", zzcfk.zzdZ(str), Long.valueOf(zza.zzbpx));
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            zzwB().zza(zzyt, "_o", com_google_android_gms_internal_zzcey.zzbpc);
            if (zzwB().zzey(str)) {
                zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                zzwB().zza(zzyt, "_r", Long.valueOf(1));
            }
            zzxq = zzwz().zzdR(str);
            if (zzxq > 0) {
                zzwF().zzyz().zze("Data lost. Too many events stored on disk, deleted. appId", zzcfk.zzdZ(str), Long.valueOf(zzxq));
            }
            zzcet com_google_android_gms_internal_zzcet = new zzcet(this, com_google_android_gms_internal_zzcey.zzbpc, str, com_google_android_gms_internal_zzcey.name, com_google_android_gms_internal_zzcey.zzbpN, 0, zzyt);
            zzceu zzE = zzwz().zzE(str, com_google_android_gms_internal_zzcet.mName);
            if (zzE == null) {
                long zzdU = zzwz().zzdU(str);
                zzcel.zzxp();
                if (zzdU >= 500) {
                    zzwF().zzyx().zzd("Too many event names used, ignoring event. appId, name, supported count", zzcfk.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzcet.mName), Integer.valueOf(zzcel.zzxp()));
                    zzwB().zza(str, 8, null, null, 0);
                    zzwz().endTransaction();
                    return;
                }
                com_google_android_gms_internal_zzceu = new zzceu(str, com_google_android_gms_internal_zzcet.mName, 0, 0, com_google_android_gms_internal_zzcet.zzayS);
            } else {
                com_google_android_gms_internal_zzcet = com_google_android_gms_internal_zzcet.zza(this, zzE.zzbpI);
                com_google_android_gms_internal_zzceu = zzE.zzab(com_google_android_gms_internal_zzcet.zzayS);
            }
            zzwz().zza(com_google_android_gms_internal_zzceu);
            zza(com_google_android_gms_internal_zzcet, com_google_android_gms_internal_zzceg);
            zzwz().setTransactionSuccessful();
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Event recorded", zzwA().zza(com_google_android_gms_internal_zzcet));
            }
            zzwz().endTransaction();
            zzzc();
            zzwF().zzyD().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
        }
    }

    @WorkerThread
    private final zzceg zzel(String str) {
        zzcef zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzbgz.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping. appId", zzcfk.zzdZ(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzceg(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0);
    }

    @WorkerThread
    private final void zzf(zzceg com_google_android_gms_internal_zzceg) {
        Object obj = 1;
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzbo.zzcF(com_google_android_gms_internal_zzceg.packageName);
        zzcef zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceg.packageName);
        String zzec = zzwG().zzec(com_google_android_gms_internal_zzceg.packageName);
        Object obj2 = null;
        if (zzdQ == null) {
            zzcef com_google_android_gms_internal_zzcef = new zzcef(this, com_google_android_gms_internal_zzceg.packageName);
            com_google_android_gms_internal_zzcef.zzdG(zzwu().zzyu());
            com_google_android_gms_internal_zzcef.zzdI(zzec);
            zzdQ = com_google_android_gms_internal_zzcef;
            obj2 = 1;
        } else if (!zzec.equals(zzdQ.zzwJ())) {
            zzdQ.zzdI(zzec);
            zzdQ.zzdG(zzwu().zzyu());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ) || com_google_android_gms_internal_zzceg.zzboQ.equals(zzdQ.getGmpAppId()))) {
            zzdQ.zzdH(com_google_android_gms_internal_zzceg.zzboQ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboY) || com_google_android_gms_internal_zzceg.zzboY.equals(zzdQ.zzwK()))) {
            zzdQ.zzdJ(com_google_android_gms_internal_zzceg.zzboY);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzceg.zzboS == 0 || com_google_android_gms_internal_zzceg.zzboS == zzdQ.zzwP())) {
            zzdQ.zzO(com_google_android_gms_internal_zzceg.zzboS);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzbgW) || com_google_android_gms_internal_zzceg.zzbgW.equals(zzdQ.zzjH()))) {
            zzdQ.setAppVersion(com_google_android_gms_internal_zzceg.zzbgW);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceg.zzboX != zzdQ.zzwN()) {
            zzdQ.zzN(com_google_android_gms_internal_zzceg.zzboX);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzceg.zzboR == null || com_google_android_gms_internal_zzceg.zzboR.equals(zzdQ.zzwO()))) {
            zzdQ.zzdK(com_google_android_gms_internal_zzceg.zzboR);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceg.zzboT != zzdQ.zzwQ()) {
            zzdQ.zzP(com_google_android_gms_internal_zzceg.zzboT);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceg.zzboV != zzdQ.zzwR()) {
            zzdQ.setMeasurementEnabled(com_google_android_gms_internal_zzceg.zzboV);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboU) || com_google_android_gms_internal_zzceg.zzboU.equals(zzdQ.zzxc()))) {
            zzdQ.zzdL(com_google_android_gms_internal_zzceg.zzboU);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceg.zzboZ != zzdQ.zzxe()) {
            zzdQ.zzZ(com_google_android_gms_internal_zzceg.zzboZ);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzwz().zza(zzdQ);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean zzg(String str, long j) {
        int length;
        boolean z;
        String str2;
        long zzwM;
        long zzwL;
        zzcem zzwz;
        Throwable th;
        zzwz().beginTransaction();
        zzcgk com_google_android_gms_internal_zzcgk = this;
        zza com_google_android_gms_internal_zzcgk_zza = new zza();
        zzcem zzwz2 = zzwz();
        String str3 = null;
        long j2 = this.zzbsS;
        zzbo.zzu(com_google_android_gms_internal_zzcgk_zza);
        zzwz2.zzjC();
        zzwz2.zzkD();
        Cursor cursor = null;
        Object obj;
        boolean z2;
        zzcjy com_google_android_gms_internal_zzcjy;
        int i;
        int i2;
        boolean zzO;
        Object obj2;
        Object obj3;
        zzcjw[] com_google_android_gms_internal_zzcjwArr;
        int length2;
        int i3;
        zzcjw com_google_android_gms_internal_zzcjw;
        zzcjw[] com_google_android_gms_internal_zzcjwArr2;
        zzcjw com_google_android_gms_internal_zzcjw2;
        zzcjv com_google_android_gms_internal_zzcjv;
        int i4;
        Object obj4;
        zzcjw com_google_android_gms_internal_zzcjw3;
        zzcjw[] com_google_android_gms_internal_zzcjwArr3;
        int i5;
        zzcjw com_google_android_gms_internal_zzcjw4;
        int i6;
        int i7;
        boolean z3;
        zzcef zzdQ;
        zzcjs zzeh;
        boolean z4;
        try {
            String[] strArr;
            String str4;
            String str5;
            String str6;
            SQLiteDatabase writableDatabase = zzwz2.getWritableDatabase();
            if (TextUtils.isEmpty(null)) {
                strArr = j2 != -1 ? new String[]{String.valueOf(j2), String.valueOf(j)} : new String[]{String.valueOf(j)};
                str4 = j2 != -1 ? "rowid <= ? and " : "";
                cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str4).length() + 148).append("select app_id, metadata_fingerprint from raw_events where ").append(str4).append("app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;").toString(), strArr);
                if (cursor.moveToFirst()) {
                    str3 = cursor.getString(0);
                    str4 = cursor.getString(1);
                    cursor.close();
                    str5 = str4;
                    r11 = cursor;
                    str6 = str3;
                } else {
                    if (cursor != null) {
                        cursor.close();
                    }
                    obj = (com_google_android_gms_internal_zzcgk_zza.zztJ != null || com_google_android_gms_internal_zzcgk_zza.zztJ.isEmpty()) ? 1 : null;
                    if (obj != null) {
                        z2 = false;
                        com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                if (!zzO) {
                                    zzwB();
                                }
                                obj2 = null;
                                obj3 = null;
                                if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                }
                                com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjwArr.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                        obj = obj3;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        obj = obj3;
                                    } else {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj = 1;
                                    }
                                    i3++;
                                    obj3 = obj;
                                }
                                if (obj2 == null && zzO) {
                                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_c";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                }
                                if (obj3 == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_r";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                            i4++;
                                        } else {
                                            obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                            }
                                            if (i4 < obj3.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                        }
                                    }
                                } else {
                                    z2 = true;
                                }
                                if (zzcjk.zzeo(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name) && zzO && zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, true, false, false).zzbpw > ((long) this.zzbsn.zzb(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, zzcfa.zzbqi))) {
                                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    obj4 = null;
                                    com_google_android_gms_internal_zzcjw3 = null;
                                    com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                    length = com_google_android_gms_internal_zzcjwArr3.length;
                                    i5 = 0;
                                    while (i5 < length) {
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                            obj3 = obj4;
                                        } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                            obj3 = obj4;
                                        } else {
                                            com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                            i6 = 1;
                                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                        }
                                        i5++;
                                        obj4 = obj3;
                                        com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                    }
                                    if (obj4 == null && com_google_android_gms_internal_zzcjw3 != null) {
                                        com_google_android_gms_internal_zzcjwArr3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                        i3 = 0;
                                        zzcjw[] com_google_android_gms_internal_zzcjwArr4 = com_google_android_gms_internal_zzcjv.zzbvw;
                                        int length3 = com_google_android_gms_internal_zzcjwArr4.length;
                                        i5 = 0;
                                        while (i5 < length3) {
                                            zzcjw com_google_android_gms_internal_zzcjw5 = com_google_android_gms_internal_zzcjwArr4[i5];
                                            if (com_google_android_gms_internal_zzcjw5 != com_google_android_gms_internal_zzcjw3) {
                                                i4 = i3 + 1;
                                                com_google_android_gms_internal_zzcjwArr3[i3] = com_google_android_gms_internal_zzcjw5;
                                            } else {
                                                i4 = i3;
                                            }
                                            i5++;
                                            i3 = i4;
                                        }
                                        com_google_android_gms_internal_zzcjv.zzbvw = com_google_android_gms_internal_zzcjwArr3;
                                        z = z2;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                        i7 = i4;
                                        z3 = z;
                                    } else if (com_google_android_gms_internal_zzcjw3 == null) {
                                        com_google_android_gms_internal_zzcjw3.name = "_err";
                                        com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                        z = z2;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                        i7 = i4;
                                        z3 = z;
                                    } else {
                                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    }
                                }
                                z = z2;
                                i4 = i + 1;
                                com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                i7 = i4;
                                z3 = z;
                            } else {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                obj = (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ) || zzwB().zzeB(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) ? 1 : null;
                                if (obj == null || "_err".equals(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                    i7 = i;
                                    z3 = z2;
                                } else {
                                    zzwB().zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, 11, "_ev", ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name, 0);
                                    i7 = i;
                                    z3 = z2;
                                }
                            }
                            i2++;
                            i = i7;
                            z2 = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                        com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjv com_google_android_gms_internal_zzcjv2 : com_google_android_gms_internal_zzcjy.zzbvE) {
                            if (com_google_android_gms_internal_zzcjv2.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv2.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjv2.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv2.zzbvx;
                            }
                        }
                        str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                        zzdQ = zzwz().zzdQ(str2);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzcel.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                            if (zzeh == null && zzeh.zzbvl != null) {
                                com_google_android_gms_internal_zzcjy.zzbwb = zzeh.zzbvl;
                            } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            } else {
                                com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                        zzwz = zzwz();
                        try {
                            zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                        } catch (SQLiteException e) {
                            zzwz.zzwF().zzyx().zze("Failed to remove unused event metadata. appId", zzcfk.zzdZ(str2), e);
                        }
                        zzwz().setTransactionSuccessful();
                        z4 = com_google_android_gms_internal_zzcjy.zzbvE.length <= 0;
                        zzwz().endTransaction();
                        return z4;
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
            }
            strArr = j2 != -1 ? new String[]{null, String.valueOf(j2)} : new String[]{null};
            str4 = j2 != -1 ? " and rowid <= ?" : "";
            cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str4).length() + 84).append("select metadata_fingerprint from raw_events where app_id = ?").append(str4).append(" order by rowid limit 1;").toString(), strArr);
            if (cursor.moveToFirst()) {
                str4 = cursor.getString(0);
                cursor.close();
                str5 = str4;
                r11 = cursor;
                str6 = null;
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                }
                if (obj != null) {
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
                z2 = false;
                com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                i = 0;
                i2 = 0;
                while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                    if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                        zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                        if (zzO) {
                            zzwB();
                        }
                        obj2 = null;
                        obj3 = null;
                        if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                            ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                        }
                        com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                        length2 = com_google_android_gms_internal_zzcjwArr.length;
                        i3 = 0;
                        while (i3 < length2) {
                            com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                            if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                obj2 = 1;
                                obj = obj3;
                            } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                obj = obj3;
                            } else {
                                com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                obj = 1;
                            }
                            i3++;
                            obj3 = obj;
                        }
                        zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                        com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                        com_google_android_gms_internal_zzcjw2 = new zzcjw();
                        com_google_android_gms_internal_zzcjw2.name = "_c";
                        com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                        com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                        if (obj3 == null) {
                            zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                            com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                            com_google_android_gms_internal_zzcjw2 = new zzcjw();
                            com_google_android_gms_internal_zzcjw2.name = "_r";
                            com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                            com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                            ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                        }
                        if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                            z2 = true;
                        } else {
                            com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                    i4++;
                                } else {
                                    obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                    }
                                    if (i4 < obj3.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                    }
                                    com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                }
                            }
                        }
                        zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                        obj4 = null;
                        com_google_android_gms_internal_zzcjw3 = null;
                        com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                        length = com_google_android_gms_internal_zzcjwArr3.length;
                        i5 = 0;
                        while (i5 < length) {
                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                            if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                obj3 = obj4;
                            } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                obj3 = obj4;
                            } else {
                                com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                i6 = 1;
                                com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                            }
                            i5++;
                            obj4 = obj3;
                            com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                        }
                        if (obj4 == null) {
                        }
                        if (com_google_android_gms_internal_zzcjw3 == null) {
                            zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            z = z2;
                            i4 = i + 1;
                            com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                            i7 = i4;
                            z3 = z;
                        } else {
                            com_google_android_gms_internal_zzcjw3.name = "_err";
                            com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                            z = z2;
                            i4 = i + 1;
                            com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                            i7 = i4;
                            z3 = z;
                        }
                    } else {
                        zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                        if (!zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                        }
                        if (obj == null) {
                        }
                        i7 = i;
                        z3 = z2;
                    }
                    i2++;
                    i = i7;
                    z2 = z3;
                }
                if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                    com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                }
                com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                for (zzcjv com_google_android_gms_internal_zzcjv22 : com_google_android_gms_internal_zzcjy.zzbvE) {
                    if (com_google_android_gms_internal_zzcjv22.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                        com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv22.zzbvx;
                    }
                    if (com_google_android_gms_internal_zzcjv22.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                        com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv22.zzbvx;
                    }
                }
                str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                zzdQ = zzwz().zzdQ(str2);
                if (zzdQ != null) {
                    zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                    zzwM = zzdQ.zzwM();
                    if (zzwM == 0) {
                    }
                    com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                    zzwL = zzdQ.zzwL();
                    if (zzwL != 0) {
                        zzwM = zzwL;
                    }
                    if (zzwM == 0) {
                    }
                    com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                    zzdQ.zzwV();
                    com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                    zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                    zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                    com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                    zzwz().zza(zzdQ);
                }
                if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                    zzcel.zzxE();
                    zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                    if (zzeh == null) {
                    }
                    if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                        zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                    } else {
                        com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                    }
                    zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                }
                zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                zzwz = zzwz();
                zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                zzwz().setTransactionSuccessful();
                if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                }
                zzwz().endTransaction();
                return z4;
            }
            try {
                r11 = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id = ? and metadata_fingerprint = ?", new String[]{str6, str5}, null, null, "rowid", "2");
                if (r11.moveToFirst()) {
                    byte[] blob = r11.getBlob(0);
                    acx zzb = acx.zzb(blob, 0, blob.length);
                    zzcjy com_google_android_gms_internal_zzcjy2 = new zzcjy();
                    try {
                        com_google_android_gms_internal_zzcjy2.zza(zzb);
                        if (r11.moveToNext()) {
                            zzwz2.zzwF().zzyz().zzj("Get multiple raw event metadata records, expected one. appId", zzcfk.zzdZ(str6));
                        }
                        r11.close();
                        com_google_android_gms_internal_zzcgk_zza.zzb(com_google_android_gms_internal_zzcjy2);
                        if (j2 != -1) {
                            str4 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?";
                            strArr = new String[]{str6, str5, String.valueOf(j2)};
                        } else {
                            str4 = "app_id = ? and metadata_fingerprint = ?";
                            strArr = new String[]{str6, str5};
                        }
                        cursor = writableDatabase.query("raw_events", new String[]{"rowid", "name", AppMeasurement.Param.TIMESTAMP, "data"}, str4, strArr, null, null, "rowid", null);
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    zzwL = cursor.getLong(0);
                                    byte[] blob2 = cursor.getBlob(3);
                                    acx zzb2 = acx.zzb(blob2, 0, blob2.length);
                                    zzcjv com_google_android_gms_internal_zzcjv3 = new zzcjv();
                                    try {
                                        com_google_android_gms_internal_zzcjv3.zza(zzb2);
                                        com_google_android_gms_internal_zzcjv3.name = cursor.getString(1);
                                        com_google_android_gms_internal_zzcjv3.zzbvx = Long.valueOf(cursor.getLong(2));
                                        if (!com_google_android_gms_internal_zzcgk_zza.zza(zzwL, com_google_android_gms_internal_zzcjv3)) {
                                            if (cursor != null) {
                                                cursor.close();
                                            }
                                            if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                                            }
                                            if (obj != null) {
                                                z2 = false;
                                                com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                                                com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                                                i = 0;
                                                i2 = 0;
                                                while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                                                    if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                                        zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                                        if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                                                        }
                                                        if (obj == null) {
                                                        }
                                                        i7 = i;
                                                        z3 = z2;
                                                    } else {
                                                        zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                                        if (zzO) {
                                                            zzwB();
                                                        }
                                                        obj2 = null;
                                                        obj3 = null;
                                                        if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                                            ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                                        }
                                                        com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                                        length2 = com_google_android_gms_internal_zzcjwArr.length;
                                                        i3 = 0;
                                                        while (i3 < length2) {
                                                            com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                                            if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                                                com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                                                obj2 = 1;
                                                                obj = obj3;
                                                            } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                                                com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                                                obj = 1;
                                                            } else {
                                                                obj = obj3;
                                                            }
                                                            i3++;
                                                            obj3 = obj;
                                                        }
                                                        zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                                        com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                                        com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                                        com_google_android_gms_internal_zzcjw2.name = "_c";
                                                        com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                                        com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                                        if (obj3 == null) {
                                                            zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                                            com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                                            com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                                            com_google_android_gms_internal_zzcjw2.name = "_r";
                                                            com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                                            com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                                            ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                                        }
                                                        if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                                            com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                                            i4 = 0;
                                                            while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                                                if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                                                    obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                                                    if (i4 > 0) {
                                                                        System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                                                    }
                                                                    if (i4 < obj3.length) {
                                                                        System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                                                    }
                                                                    com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                                                } else {
                                                                    i4++;
                                                                }
                                                            }
                                                        } else {
                                                            z2 = true;
                                                        }
                                                        zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                                        com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                                        obj4 = null;
                                                        com_google_android_gms_internal_zzcjw3 = null;
                                                        com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                                        length = com_google_android_gms_internal_zzcjwArr3.length;
                                                        i5 = 0;
                                                        while (i5 < length) {
                                                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                                            if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                                                obj3 = obj4;
                                                            } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                                                com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                                                i6 = 1;
                                                                com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                                            } else {
                                                                com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                                                obj3 = obj4;
                                                            }
                                                            i5++;
                                                            obj4 = obj3;
                                                            com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                                        }
                                                        if (obj4 == null) {
                                                        }
                                                        if (com_google_android_gms_internal_zzcjw3 == null) {
                                                            com_google_android_gms_internal_zzcjw3.name = "_err";
                                                            com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                                            z = z2;
                                                            i4 = i + 1;
                                                            com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                                            i7 = i4;
                                                            z3 = z;
                                                        } else {
                                                            zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                                            z = z2;
                                                            i4 = i + 1;
                                                            com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                                            i7 = i4;
                                                            z3 = z;
                                                        }
                                                    }
                                                    i2++;
                                                    i = i7;
                                                    z2 = z3;
                                                }
                                                if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                                                    com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                                                }
                                                com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                                                com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                                                com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                                                for (zzcjv com_google_android_gms_internal_zzcjv222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                                                    if (com_google_android_gms_internal_zzcjv222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                                        com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv222.zzbvx;
                                                    }
                                                    if (com_google_android_gms_internal_zzcjv222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                                        com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv222.zzbvx;
                                                    }
                                                }
                                                str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                                                zzdQ = zzwz().zzdQ(str2);
                                                if (zzdQ != null) {
                                                    zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                                } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                                                    zzwM = zzdQ.zzwM();
                                                    if (zzwM == 0) {
                                                    }
                                                    com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                                    zzwL = zzdQ.zzwL();
                                                    if (zzwL != 0) {
                                                        zzwM = zzwL;
                                                    }
                                                    if (zzwM == 0) {
                                                    }
                                                    com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                                    zzdQ.zzwV();
                                                    com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                                                    zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                                                    zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                                                    com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                                                    zzwz().zza(zzdQ);
                                                }
                                                if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                                                    zzcel.zzxE();
                                                    zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                                                    if (zzeh == null) {
                                                    }
                                                    if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                                        com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                                                    } else {
                                                        zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                                    }
                                                    zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                                                }
                                                zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                                                zzwz = zzwz();
                                                zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                                                zzwz().setTransactionSuccessful();
                                                if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                                                }
                                                zzwz().endTransaction();
                                                return z4;
                                            }
                                            zzwz().setTransactionSuccessful();
                                            zzwz().endTransaction();
                                            return false;
                                        }
                                    } catch (IOException e2) {
                                        zzwz2.zzwF().zzyx().zze("Data loss. Failed to merge raw event. appId", zzcfk.zzdZ(str6), e2);
                                    }
                                } catch (SQLiteException e3) {
                                    obj = e3;
                                    str3 = str6;
                                }
                            } while (cursor.moveToNext());
                            if (cursor != null) {
                                cursor.close();
                            }
                            if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                            }
                            if (obj != null) {
                                zzwz().setTransactionSuccessful();
                                zzwz().endTransaction();
                                return false;
                            }
                            z2 = false;
                            com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                            com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                            i = 0;
                            i2 = 0;
                            while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                                if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                    zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                    if (zzO) {
                                        zzwB();
                                    }
                                    obj2 = null;
                                    obj3 = null;
                                    if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                    }
                                    com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                    length2 = com_google_android_gms_internal_zzcjwArr.length;
                                    i3 = 0;
                                    while (i3 < length2) {
                                        com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                            com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                            obj2 = 1;
                                            obj = obj3;
                                        } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                            obj = obj3;
                                        } else {
                                            com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                            obj = 1;
                                        }
                                        i3++;
                                        obj3 = obj;
                                    }
                                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_c";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                    if (obj3 == null) {
                                        zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                        com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                        com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                        com_google_android_gms_internal_zzcjw2.name = "_r";
                                        com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                        com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                    }
                                    if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                        z2 = true;
                                    } else {
                                        com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                        i4 = 0;
                                        while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                            if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                                i4++;
                                            } else {
                                                obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                                if (i4 > 0) {
                                                    System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                                }
                                                if (i4 < obj3.length) {
                                                    System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                                }
                                                com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                            }
                                        }
                                    }
                                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    obj4 = null;
                                    com_google_android_gms_internal_zzcjw3 = null;
                                    com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                    length = com_google_android_gms_internal_zzcjwArr3.length;
                                    i5 = 0;
                                    while (i5 < length) {
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                            obj3 = obj4;
                                        } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                            obj3 = obj4;
                                        } else {
                                            com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                            i6 = 1;
                                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                        }
                                        i5++;
                                        obj4 = obj3;
                                        com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                    }
                                    if (obj4 == null) {
                                    }
                                    if (com_google_android_gms_internal_zzcjw3 == null) {
                                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                        z = z2;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                        i7 = i4;
                                        z3 = z;
                                    } else {
                                        com_google_android_gms_internal_zzcjw3.name = "_err";
                                        com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                        z = z2;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                        i7 = i4;
                                        z3 = z;
                                    }
                                } else {
                                    zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                                    }
                                    if (obj == null) {
                                    }
                                    i7 = i;
                                    z3 = z2;
                                }
                                i2++;
                                i = i7;
                                z2 = z3;
                            }
                            if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                                com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                            }
                            com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                            com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                            com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                            for (zzcjv com_google_android_gms_internal_zzcjv2222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                                if (com_google_android_gms_internal_zzcjv2222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                    com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv2222.zzbvx;
                                }
                                if (com_google_android_gms_internal_zzcjv2222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                    com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv2222.zzbvx;
                                }
                            }
                            str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                            zzdQ = zzwz().zzdQ(str2);
                            if (zzdQ != null) {
                                zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                                zzwM = zzdQ.zzwM();
                                if (zzwM == 0) {
                                }
                                com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                zzwL = zzdQ.zzwL();
                                if (zzwL != 0) {
                                    zzwM = zzwL;
                                }
                                if (zzwM == 0) {
                                }
                                com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                zzdQ.zzwV();
                                com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                                zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                                zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                                com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                                zzwz().zza(zzdQ);
                            }
                            if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                                zzcel.zzxE();
                                zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                                if (zzeh == null) {
                                }
                                if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                    zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                } else {
                                    com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                                }
                                zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                            }
                            zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                            zzwz = zzwz();
                            zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                            zzwz().setTransactionSuccessful();
                            if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                            }
                            zzwz().endTransaction();
                            return z4;
                        }
                        zzwz2.zzwF().zzyz().zzj("Raw event data disappeared while in transaction. appId", zzcfk.zzdZ(str6));
                        if (cursor != null) {
                            cursor.close();
                        }
                        if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                        }
                        if (obj != null) {
                            zzwz().setTransactionSuccessful();
                            zzwz().endTransaction();
                            return false;
                        }
                        z2 = false;
                        com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj2 = null;
                                obj3 = null;
                                if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                }
                                com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjwArr.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                        obj = obj3;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        obj = obj3;
                                    } else {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj = 1;
                                    }
                                    i3++;
                                    obj3 = obj;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                com_google_android_gms_internal_zzcjw2.name = "_c";
                                com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                if (obj3 == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_r";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                    z2 = true;
                                } else {
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                            i4++;
                                        } else {
                                            obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                            }
                                            if (i4 < obj3.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                        }
                                    }
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjw3 = null;
                                com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                length = com_google_android_gms_internal_zzcjwArr3.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        obj3 = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                        obj3 = obj4;
                                    } else {
                                        com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                    }
                                    i5++;
                                    obj4 = obj3;
                                    com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjw3 == null) {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                } else {
                                    com_google_android_gms_internal_zzcjw3.name = "_err";
                                    com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                }
                            } else {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                                }
                                if (obj == null) {
                                }
                                i7 = i;
                                z3 = z2;
                            }
                            i2++;
                            i = i7;
                            z2 = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                        com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjv com_google_android_gms_internal_zzcjv22222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                            if (com_google_android_gms_internal_zzcjv22222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv22222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjv22222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv22222.zzbvx;
                            }
                        }
                        str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                        zzdQ = zzwz().zzdQ(str2);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzcel.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            } else {
                                com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                        zzwz = zzwz();
                        zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                        }
                        zzwz().endTransaction();
                        return z4;
                    } catch (IOException e22) {
                        zzwz2.zzwF().zzyx().zze("Data loss. Failed to merge raw event metadata. appId", zzcfk.zzdZ(str6), e22);
                        Cursor cursor2;
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                    } catch (Throwable th2) {
                        zzwz().endTransaction();
                    }
                } else {
                    zzwz2.zzwF().zzyx().zzj("Raw event metadata record is missing. appId", zzcfk.zzdZ(str6));
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                    }
                    if (obj != null) {
                        z2 = false;
                        com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                                }
                                if (obj == null) {
                                }
                                i7 = i;
                                z3 = z2;
                            } else {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj2 = null;
                                obj3 = null;
                                if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                }
                                com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjwArr.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                        obj = obj3;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj = 1;
                                    } else {
                                        obj = obj3;
                                    }
                                    i3++;
                                    obj3 = obj;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                com_google_android_gms_internal_zzcjw2.name = "_c";
                                com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                if (obj3 == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_r";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                            obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                            }
                                            if (i4 < obj3.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                        } else {
                                            i4++;
                                        }
                                    }
                                } else {
                                    z2 = true;
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjw3 = null;
                                com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                length = com_google_android_gms_internal_zzcjwArr3.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        obj3 = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                    } else {
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                        obj3 = obj4;
                                    }
                                    i5++;
                                    obj4 = obj3;
                                    com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjw3 == null) {
                                    com_google_android_gms_internal_zzcjw3.name = "_err";
                                    com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                } else {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                }
                            }
                            i2++;
                            i = i7;
                            z2 = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                        com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjv com_google_android_gms_internal_zzcjv222222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                            if (com_google_android_gms_internal_zzcjv222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv222222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjv222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv222222.zzbvx;
                            }
                        }
                        str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                        zzdQ = zzwz().zzdQ(str2);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzcel.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                            } else {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                        zzwz = zzwz();
                        zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                        }
                        zzwz().endTransaction();
                        return z4;
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
            } catch (SQLiteException e4) {
                obj = e4;
                cursor = cursor2;
                str3 = str6;
                try {
                    zzwz2.zzwF().zzyx().zze("Data loss. Error selecting raw event. appId", zzcfk.zzdZ(str3), obj);
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
                    }
                    if (obj != null) {
                        z2 = false;
                        com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                                }
                                if (obj == null) {
                                }
                                i7 = i;
                                z3 = z2;
                            } else {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj2 = null;
                                obj3 = null;
                                if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                                }
                                com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjwArr.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                        obj = obj3;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                                        com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                                        obj = 1;
                                    } else {
                                        obj = obj3;
                                    }
                                    i3++;
                                    obj3 = obj;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                com_google_android_gms_internal_zzcjw2.name = "_c";
                                com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                if (obj3 == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                                    com_google_android_gms_internal_zzcjw2.name = "_r";
                                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                            obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                            }
                                            if (i4 < obj3.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                                        } else {
                                            i4++;
                                        }
                                    }
                                } else {
                                    z2 = true;
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjw3 = null;
                                com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                                length = com_google_android_gms_internal_zzcjwArr3.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        obj3 = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                                        com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                                    } else {
                                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                                        obj3 = obj4;
                                    }
                                    i5++;
                                    obj4 = obj3;
                                    com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjw3 == null) {
                                    com_google_android_gms_internal_zzcjw3.name = "_err";
                                    com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                } else {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                                    z = z2;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                                    i7 = i4;
                                    z3 = z;
                                }
                            }
                            i2++;
                            i = i7;
                            z2 = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                            com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                        com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjv com_google_android_gms_internal_zzcjv2222222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                            if (com_google_android_gms_internal_zzcjv2222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv2222222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjv2222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv2222222.zzbvx;
                            }
                        }
                        str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
                        zzdQ = zzwz().zzdQ(str2);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                            zzcel.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                                com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                            } else {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
                        zzwz = zzwz();
                        zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
                        }
                        zzwz().endTransaction();
                        return z4;
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                cursor = cursor2;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (SQLiteException e5) {
            obj = e5;
            zzwz2.zzwF().zzyx().zze("Data loss. Error selecting raw event. appId", zzcfk.zzdZ(str3), obj);
            if (cursor != null) {
                cursor.close();
            }
            if (com_google_android_gms_internal_zzcgk_zza.zztJ != null) {
            }
            if (obj != null) {
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return false;
            }
            z2 = false;
            com_google_android_gms_internal_zzcjy = com_google_android_gms_internal_zzcgk_zza.zzbsZ;
            com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[com_google_android_gms_internal_zzcgk_zza.zztJ.size()];
            i = 0;
            i2 = 0;
            while (i2 < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                if (zzwC().zzN(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name)) {
                    zzO = zzwC().zzO(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name);
                    if (zzO) {
                        zzwB();
                    }
                    obj2 = null;
                    obj3 = null;
                    if (((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw == null) {
                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = new zzcjw[0];
                    }
                    com_google_android_gms_internal_zzcjwArr = ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw;
                    length2 = com_google_android_gms_internal_zzcjwArr.length;
                    i3 = 0;
                    while (i3 < length2) {
                        com_google_android_gms_internal_zzcjw = com_google_android_gms_internal_zzcjwArr[i3];
                        if (!"_c".equals(com_google_android_gms_internal_zzcjw.name)) {
                            com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                            obj2 = 1;
                            obj = obj3;
                        } else if ("_r".equals(com_google_android_gms_internal_zzcjw.name)) {
                            obj = obj3;
                        } else {
                            com_google_android_gms_internal_zzcjw.zzbvA = Long.valueOf(1);
                            obj = 1;
                        }
                        i3++;
                        obj3 = obj;
                    }
                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                    com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                    com_google_android_gms_internal_zzcjw2 = new zzcjw();
                    com_google_android_gms_internal_zzcjw2.name = "_c";
                    com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                    com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                    ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                    if (obj3 == null) {
                        zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                        com_google_android_gms_internal_zzcjwArr2 = (zzcjw[]) Arrays.copyOf(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw, ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw.length + 1);
                        com_google_android_gms_internal_zzcjw2 = new zzcjw();
                        com_google_android_gms_internal_zzcjw2.name = "_r";
                        com_google_android_gms_internal_zzcjw2.zzbvA = Long.valueOf(1);
                        com_google_android_gms_internal_zzcjwArr2[com_google_android_gms_internal_zzcjwArr2.length - 1] = com_google_android_gms_internal_zzcjw2;
                        ((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).zzbvw = com_google_android_gms_internal_zzcjwArr2;
                    }
                    if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ))) {
                        z2 = true;
                    } else {
                        com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                        i4 = 0;
                        while (i4 < com_google_android_gms_internal_zzcjv.zzbvw.length) {
                            if ("_r".equals(com_google_android_gms_internal_zzcjv.zzbvw[i4].name)) {
                                i4++;
                            } else {
                                obj3 = new zzcjw[(com_google_android_gms_internal_zzcjv.zzbvw.length - 1)];
                                if (i4 > 0) {
                                    System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, 0, obj3, 0, i4);
                                }
                                if (i4 < obj3.length) {
                                    System.arraycopy(com_google_android_gms_internal_zzcjv.zzbvw, i4 + 1, obj3, i4, obj3.length - i4);
                                }
                                com_google_android_gms_internal_zzcjv.zzbvw = obj3;
                            }
                        }
                    }
                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                    com_google_android_gms_internal_zzcjv = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                    obj4 = null;
                    com_google_android_gms_internal_zzcjw3 = null;
                    com_google_android_gms_internal_zzcjwArr3 = com_google_android_gms_internal_zzcjv.zzbvw;
                    length = com_google_android_gms_internal_zzcjwArr3.length;
                    i5 = 0;
                    while (i5 < length) {
                        com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjwArr3[i5];
                        if (!"_c".equals(com_google_android_gms_internal_zzcjw2.name)) {
                            obj3 = obj4;
                        } else if ("_err".equals(com_google_android_gms_internal_zzcjw2.name)) {
                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw3;
                            obj3 = obj4;
                        } else {
                            com_google_android_gms_internal_zzcjw4 = com_google_android_gms_internal_zzcjw3;
                            i6 = 1;
                            com_google_android_gms_internal_zzcjw2 = com_google_android_gms_internal_zzcjw4;
                        }
                        i5++;
                        obj4 = obj3;
                        com_google_android_gms_internal_zzcjw3 = com_google_android_gms_internal_zzcjw2;
                    }
                    if (obj4 == null) {
                    }
                    if (com_google_android_gms_internal_zzcjw3 == null) {
                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                        z = z2;
                        i4 = i + 1;
                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                        i7 = i4;
                        z3 = z;
                    } else {
                        com_google_android_gms_internal_zzcjw3.name = "_err";
                        com_google_android_gms_internal_zzcjw3.zzbvA = Long.valueOf(10);
                        z = z2;
                        i4 = i + 1;
                        com_google_android_gms_internal_zzcjy.zzbvE[i] = (zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2);
                        i7 = i4;
                        z3 = z;
                    }
                } else {
                    zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ), zzwA().zzdW(((zzcjv) com_google_android_gms_internal_zzcgk_zza.zztJ.get(i2)).name));
                    if (zzwB().zzeA(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ)) {
                    }
                    if (obj == null) {
                    }
                    i7 = i;
                    z3 = z2;
                }
                i2++;
                i = i7;
                z2 = z3;
            }
            if (i < com_google_android_gms_internal_zzcgk_zza.zztJ.size()) {
                com_google_android_gms_internal_zzcjy.zzbvE = (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjy.zzbvE, i);
            }
            com_google_android_gms_internal_zzcjy.zzbvX = zza(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ, com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
            com_google_android_gms_internal_zzcjy.zzbvH = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzcjy.zzbvI = Long.valueOf(Long.MIN_VALUE);
            for (zzcjv com_google_android_gms_internal_zzcjv22222222 : com_google_android_gms_internal_zzcjy.zzbvE) {
                if (com_google_android_gms_internal_zzcjv22222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjy.zzbvH.longValue()) {
                    com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv22222222.zzbvx;
                }
                if (com_google_android_gms_internal_zzcjv22222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjy.zzbvI.longValue()) {
                    com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv22222222.zzbvx;
                }
            }
            str2 = com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ;
            zzdQ = zzwz().zzdQ(str2);
            if (zzdQ != null) {
                zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
            } else if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                zzwM = zzdQ.zzwM();
                if (zzwM == 0) {
                }
                com_google_android_gms_internal_zzcjy.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                zzwL = zzdQ.zzwL();
                if (zzwL != 0) {
                    zzwM = zzwL;
                }
                if (zzwM == 0) {
                }
                com_google_android_gms_internal_zzcjy.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                zzdQ.zzwV();
                com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                com_google_android_gms_internal_zzcjy.zzboU = zzdQ.zzxd();
                zzwz().zza(zzdQ);
            }
            if (com_google_android_gms_internal_zzcjy.zzbvE.length > 0) {
                zzcel.zzxE();
                zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ);
                if (zzeh == null) {
                }
                if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzboQ)) {
                    zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcgk_zza.zzbsZ.zzaJ));
                } else {
                    com_google_android_gms_internal_zzcjy.zzbwb = Long.valueOf(-1);
                }
                zzwz().zza(com_google_android_gms_internal_zzcjy, z2);
            }
            zzwz().zzG(com_google_android_gms_internal_zzcgk_zza.zzbta);
            zzwz = zzwz();
            zzwz.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str2, str2});
            zzwz().setTransactionSuccessful();
            if (com_google_android_gms_internal_zzcjy.zzbvE.length <= 0) {
            }
            zzwz().endTransaction();
            return z4;
        }
    }

    static void zzwo() {
        zzcel.zzxE();
        throw new IllegalStateException("Unexpected call on client side");
    }

    private final zzcft zzyV() {
        if (this.zzbsF != null) {
            return this.zzbsF;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    private final zzcjf zzyW() {
        zza(this.zzbsG);
        return this.zzbsG;
    }

    @WorkerThread
    private final boolean zzyX() {
        zzwE().zzjC();
        try {
            this.zzbsN = new RandomAccessFile(new File(this.mContext.getFilesDir(), zzcel.zzxC()), "rw").getChannel();
            this.zzbsM = this.zzbsN.tryLock();
            if (this.zzbsM != null) {
                zzwF().zzyD().log("Storage concurrent access okay");
                return true;
            }
            zzwF().zzyx().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzwF().zzyx().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzwF().zzyx().zzj("Failed to access storage lock file", e2);
        }
    }

    private final long zzyZ() {
        long currentTimeMillis = this.zzvy.currentTimeMillis();
        zzcfv zzwG = zzwG();
        zzwG.zzkD();
        zzwG.zzjC();
        long j = zzwG.zzbro.get();
        if (j == 0) {
            j = (long) (zzwG.zzwB().zzzt().nextInt(86400000) + 1);
            zzwG.zzbro.set(j);
        }
        return ((((j + currentTimeMillis) / 1000) / 60) / 60) / 24;
    }

    private final boolean zzzb() {
        zzwE().zzjC();
        zzkD();
        return zzwz().zzyh() || !TextUtils.isEmpty(zzwz().zzyc());
    }

    @WorkerThread
    private final void zzzc() {
        zzwE().zzjC();
        zzkD();
        if (zzzf()) {
            long abs;
            if (this.zzbsT > 0) {
                abs = 3600000 - Math.abs(this.zzvy.elapsedRealtime() - this.zzbsT);
                if (abs > 0) {
                    zzwF().zzyD().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    zzyV().unregister();
                    zzyW().cancel();
                    return;
                }
                this.zzbsT = 0;
            }
            if (zzyP() && zzzb()) {
                long currentTimeMillis = this.zzvy.currentTimeMillis();
                long zzxX = zzcel.zzxX();
                Object obj = (zzwz().zzyi() || zzwz().zzyd()) ? 1 : null;
                if (obj != null) {
                    CharSequence zzya = this.zzbsn.zzya();
                    abs = (TextUtils.isEmpty(zzya) || ".none.".equals(zzya)) ? zzcel.zzxS() : zzcel.zzxT();
                } else {
                    abs = zzcel.zzxR();
                }
                long j = zzwG().zzbrk.get();
                long j2 = zzwG().zzbrl.get();
                long max = Math.max(zzwz().zzyf(), zzwz().zzyg());
                if (max == 0) {
                    abs = 0;
                } else {
                    max = currentTimeMillis - Math.abs(max - currentTimeMillis);
                    j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
                    j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
                    currentTimeMillis = max + zzxX;
                    if (obj != null && j > 0) {
                        currentTimeMillis = Math.min(max, j) + abs;
                    }
                    if (!zzwB().zzf(j, abs)) {
                        currentTimeMillis = j + abs;
                    }
                    if (j2 == 0 || j2 < max) {
                        abs = currentTimeMillis;
                    } else {
                        for (int i = 0; i < zzcel.zzxZ(); i++) {
                            currentTimeMillis += ((long) (1 << i)) * zzcel.zzxY();
                            if (currentTimeMillis > j2) {
                                abs = currentTimeMillis;
                                break;
                            }
                        }
                        abs = 0;
                    }
                }
                if (abs == 0) {
                    zzwF().zzyD().log("Next upload time is 0");
                    zzyV().unregister();
                    zzyW().cancel();
                    return;
                } else if (zzyU().zzlQ()) {
                    currentTimeMillis = zzwG().zzbrm.get();
                    long zzxQ = zzcel.zzxQ();
                    if (!zzwB().zzf(currentTimeMillis, zzxQ)) {
                        abs = Math.max(abs, currentTimeMillis + zzxQ);
                    }
                    zzyV().unregister();
                    abs -= this.zzvy.currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzcel.zzxU();
                        zzwG().zzbrk.set(this.zzvy.currentTimeMillis());
                    }
                    zzwF().zzyD().zzj("Upload scheduled in approximately ms", Long.valueOf(abs));
                    zzyW().zzs(abs);
                    return;
                } else {
                    zzwF().zzyD().log("No network");
                    zzyV().zzlN();
                    zzyW().cancel();
                    return;
                }
            }
            zzwF().zzyD().log("Nothing to upload or uploading impossible");
            zzyV().unregister();
            zzyW().cancel();
        }
    }

    @WorkerThread
    private final boolean zzzf() {
        zzwE().zzjC();
        zzkD();
        return this.zzbsJ;
    }

    @WorkerThread
    private final void zzzg() {
        zzwE().zzjC();
        if (this.zzbsU || this.zzbsV || this.zzbsW) {
            zzwF().zzyD().zzd("Not stopping services. fetch, network, upload", Boolean.valueOf(this.zzbsU), Boolean.valueOf(this.zzbsV), Boolean.valueOf(this.zzbsW));
            return;
        }
        zzwF().zzyD().log("Stopping uploading service(s)");
        if (this.zzbsP != null) {
            for (Runnable run : this.zzbsP) {
                run.run();
            }
            this.zzbsP.clear();
        }
    }

    public final Context getContext() {
        return this.mContext;
    }

    @WorkerThread
    public final boolean isEnabled() {
        boolean z = false;
        zzwE().zzjC();
        zzkD();
        if (this.zzbsn.zzxF()) {
            return false;
        }
        Boolean zzdN = this.zzbsn.zzdN("firebase_analytics_collection_enabled");
        if (zzdN != null) {
            z = zzdN.booleanValue();
        } else if (!zzcel.zzqB()) {
            z = true;
        }
        return zzwG().zzal(z);
    }

    @WorkerThread
    protected final void start() {
        zzwE().zzjC();
        zzwz().zzye();
        if (zzwG().zzbrk.get() == 0) {
            zzwG().zzbrk.set(this.zzvy.currentTimeMillis());
        }
        if (Long.valueOf(zzwG().zzbrp.get()).longValue() == 0) {
            zzwF().zzyD().zzj("Persisting first open", Long.valueOf(this.zzbsX));
            zzwG().zzbrp.set(this.zzbsX);
        }
        if (zzyP()) {
            zzcel.zzxE();
            if (!TextUtils.isEmpty(zzwu().getGmpAppId())) {
                String zzyG = zzwG().zzyG();
                if (zzyG == null) {
                    zzwG().zzed(zzwu().getGmpAppId());
                } else if (!zzyG.equals(zzwu().getGmpAppId())) {
                    zzwF().zzyB().log("Rechecking which service to use due to a GMP App Id change");
                    zzwG().zzyJ();
                    this.zzbsB.disconnect();
                    this.zzbsB.zzla();
                    zzwG().zzed(zzwu().getGmpAppId());
                    zzwG().zzbrp.set(this.zzbsX);
                    zzwG().zzbrq.zzef(null);
                }
            }
            zzwt().zzee(zzwG().zzbrq.zzyL());
            zzcel.zzxE();
            if (!TextUtils.isEmpty(zzwu().getGmpAppId())) {
                zzchk zzwt = zzwt();
                zzwt.zzjC();
                zzwt.zzwp();
                zzwt.zzkD();
                if (zzwt.zzboe.zzyP()) {
                    zzwt.zzww().zzzk();
                    String zzyK = zzwt.zzwG().zzyK();
                    if (!TextUtils.isEmpty(zzyK)) {
                        zzwt.zzwv().zzkD();
                        if (!zzyK.equals(VERSION.RELEASE)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("_po", zzyK);
                            zzwt.zzd("auto", "_ou", bundle);
                        }
                    }
                }
                zzww().zza(new AtomicReference());
            }
        } else if (isEnabled()) {
            if (!zzwB().zzbv("android.permission.INTERNET")) {
                zzwF().zzyx().log("App is missing INTERNET permission");
            }
            if (!zzwB().zzbv("android.permission.ACCESS_NETWORK_STATE")) {
                zzwF().zzyx().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzcel.zzxE();
            if (!zzbgz.zzaP(this.mContext).zzsl()) {
                if (!zzcgb.zzj(this.mContext, false)) {
                    zzwF().zzyx().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzciv.zzk(this.mContext, false)) {
                    zzwF().zzyx().log("AppMeasurementService not registered/enabled");
                }
            }
            zzwF().zzyx().log("Uploading is not possible. App measurement disabled");
        }
        zzzc();
    }

    @WorkerThread
    protected final void zza(int i, Throwable th, byte[] bArr) {
        zzwE().zzjC();
        zzkD();
        if (bArr == null) {
            try {
                bArr = new byte[0];
            } catch (Throwable th2) {
                this.zzbsV = false;
                zzzg();
            }
        }
        List<Long> list = this.zzbsO;
        this.zzbsO = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            try {
                zzwG().zzbrk.set(this.zzvy.currentTimeMillis());
                zzwG().zzbrl.set(0);
                zzzc();
                zzwF().zzyD().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzwz().beginTransaction();
                zzcem zzwz;
                try {
                    for (Long l : list) {
                        zzwz = zzwz();
                        long longValue = l.longValue();
                        zzwz.zzjC();
                        zzwz.zzkD();
                        if (zzwz.getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(longValue)}) != 1) {
                            throw new SQLiteException("Deleted fewer rows from queue than expected");
                        }
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    if (zzyU().zzlQ() && zzzb()) {
                        zzza();
                    } else {
                        this.zzbsS = -1;
                        zzzc();
                    }
                    this.zzbsT = 0;
                } catch (SQLiteException e) {
                    zzwz.zzwF().zzyx().zzj("Failed to delete a bundle in a queue table", e);
                    throw e;
                } catch (Throwable th3) {
                    zzwz().endTransaction();
                }
            } catch (SQLiteException e2) {
                zzwF().zzyx().zzj("Database error while trying to delete uploaded bundles", e2);
                this.zzbsT = this.zzvy.elapsedRealtime();
                zzwF().zzyD().zzj("Disable upload, time", Long.valueOf(this.zzbsT));
            }
        } else {
            zzwF().zzyD().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzwG().zzbrl.set(this.zzvy.currentTimeMillis());
            boolean z = i == 503 || i == 429;
            if (z) {
                zzwG().zzbrm.set(this.zzvy.currentTimeMillis());
            }
            zzzc();
        }
        this.zzbsV = false;
        zzzg();
    }

    @WorkerThread
    public final byte[] zza(@NonNull zzcey com_google_android_gms_internal_zzcey, @Size(min = 1) String str) {
        zzkD();
        zzwE().zzjC();
        zzwo();
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        zzbo.zzcF(str);
        zzcjx com_google_android_gms_internal_zzcjx = new zzcjx();
        zzwz().beginTransaction();
        try {
            zzcef zzdQ = zzwz().zzdQ(str);
            byte[] bArr;
            if (zzdQ == null) {
                zzwF().zzyC().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzdQ.zzwR()) {
                long j;
                zzcjy com_google_android_gms_internal_zzcjy = new zzcjy();
                com_google_android_gms_internal_zzcjx.zzbvB = new zzcjy[]{com_google_android_gms_internal_zzcjy};
                com_google_android_gms_internal_zzcjy.zzbvD = Integer.valueOf(1);
                com_google_android_gms_internal_zzcjy.zzbvL = "android";
                com_google_android_gms_internal_zzcjy.zzaJ = zzdQ.zzhl();
                com_google_android_gms_internal_zzcjy.zzboR = zzdQ.zzwO();
                com_google_android_gms_internal_zzcjy.zzbgW = zzdQ.zzjH();
                long zzwN = zzdQ.zzwN();
                com_google_android_gms_internal_zzcjy.zzbvY = zzwN == -2147483648L ? null : Integer.valueOf((int) zzwN);
                com_google_android_gms_internal_zzcjy.zzbvP = Long.valueOf(zzdQ.zzwP());
                com_google_android_gms_internal_zzcjy.zzboQ = zzdQ.getGmpAppId();
                com_google_android_gms_internal_zzcjy.zzbvU = Long.valueOf(zzdQ.zzwQ());
                if (isEnabled() && zzcel.zzyb() && this.zzbsn.zzdO(com_google_android_gms_internal_zzcjy.zzaJ)) {
                    zzwu();
                    com_google_android_gms_internal_zzcjy.zzbwd = null;
                }
                Pair zzeb = zzwG().zzeb(zzdQ.zzhl());
                if (!(zzeb == null || TextUtils.isEmpty((CharSequence) zzeb.first))) {
                    com_google_android_gms_internal_zzcjy.zzbvR = (String) zzeb.first;
                    com_google_android_gms_internal_zzcjy.zzbvS = (Boolean) zzeb.second;
                }
                zzwv().zzkD();
                com_google_android_gms_internal_zzcjy.zzbvM = Build.MODEL;
                zzwv().zzkD();
                com_google_android_gms_internal_zzcjy.zzba = VERSION.RELEASE;
                com_google_android_gms_internal_zzcjy.zzbvO = Integer.valueOf((int) zzwv().zzyq());
                com_google_android_gms_internal_zzcjy.zzbvN = zzwv().zzyr();
                com_google_android_gms_internal_zzcjy.zzbvT = zzdQ.getAppInstanceId();
                com_google_android_gms_internal_zzcjy.zzboY = zzdQ.zzwK();
                List zzdP = zzwz().zzdP(zzdQ.zzhl());
                com_google_android_gms_internal_zzcjy.zzbvF = new zzcka[zzdP.size()];
                for (int i = 0; i < zzdP.size(); i++) {
                    zzcka com_google_android_gms_internal_zzcka = new zzcka();
                    com_google_android_gms_internal_zzcjy.zzbvF[i] = com_google_android_gms_internal_zzcka;
                    com_google_android_gms_internal_zzcka.name = ((zzcjj) zzdP.get(i)).mName;
                    com_google_android_gms_internal_zzcka.zzbwh = Long.valueOf(((zzcjj) zzdP.get(i)).zzbuC);
                    zzwB().zza(com_google_android_gms_internal_zzcka, ((zzcjj) zzdP.get(i)).mValue);
                }
                Bundle zzyt = com_google_android_gms_internal_zzcey.zzbpM.zzyt();
                if ("_iap".equals(com_google_android_gms_internal_zzcey.name)) {
                    zzyt.putLong("_c", 1);
                    zzwF().zzyC().log("Marking in-app purchase as real-time");
                    zzyt.putLong("_r", 1);
                }
                zzyt.putString("_o", com_google_android_gms_internal_zzcey.zzbpc);
                if (zzwB().zzey(com_google_android_gms_internal_zzcjy.zzaJ)) {
                    zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                    zzwB().zza(zzyt, "_r", Long.valueOf(1));
                }
                zzceu zzE = zzwz().zzE(str, com_google_android_gms_internal_zzcey.name);
                if (zzE == null) {
                    zzwz().zza(new zzceu(str, com_google_android_gms_internal_zzcey.name, 1, 0, com_google_android_gms_internal_zzcey.zzbpN));
                    j = 0;
                } else {
                    j = zzE.zzbpI;
                    zzwz().zza(zzE.zzab(com_google_android_gms_internal_zzcey.zzbpN).zzys());
                }
                zzcet com_google_android_gms_internal_zzcet = new zzcet(this, com_google_android_gms_internal_zzcey.zzbpc, str, com_google_android_gms_internal_zzcey.name, com_google_android_gms_internal_zzcey.zzbpN, j, zzyt);
                zzcjv com_google_android_gms_internal_zzcjv = new zzcjv();
                com_google_android_gms_internal_zzcjy.zzbvE = new zzcjv[]{com_google_android_gms_internal_zzcjv};
                com_google_android_gms_internal_zzcjv.zzbvx = Long.valueOf(com_google_android_gms_internal_zzcet.zzayS);
                com_google_android_gms_internal_zzcjv.name = com_google_android_gms_internal_zzcet.mName;
                com_google_android_gms_internal_zzcjv.zzbvy = Long.valueOf(com_google_android_gms_internal_zzcet.zzbpE);
                com_google_android_gms_internal_zzcjv.zzbvw = new zzcjw[com_google_android_gms_internal_zzcet.zzbpF.size()];
                Iterator it = com_google_android_gms_internal_zzcet.zzbpF.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzcjw com_google_android_gms_internal_zzcjw = new zzcjw();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzcjv.zzbvw[i2] = com_google_android_gms_internal_zzcjw;
                    com_google_android_gms_internal_zzcjw.name = str2;
                    zzwB().zza(com_google_android_gms_internal_zzcjw, com_google_android_gms_internal_zzcet.zzbpF.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzcjy.zzbvX = zza(zzdQ.zzhl(), com_google_android_gms_internal_zzcjy.zzbvF, com_google_android_gms_internal_zzcjy.zzbvE);
                com_google_android_gms_internal_zzcjy.zzbvH = com_google_android_gms_internal_zzcjv.zzbvx;
                com_google_android_gms_internal_zzcjy.zzbvI = com_google_android_gms_internal_zzcjv.zzbvx;
                zzwN = zzdQ.zzwM();
                com_google_android_gms_internal_zzcjy.zzbvK = zzwN != 0 ? Long.valueOf(zzwN) : null;
                long zzwL = zzdQ.zzwL();
                if (zzwL != 0) {
                    zzwN = zzwL;
                }
                com_google_android_gms_internal_zzcjy.zzbvJ = zzwN != 0 ? Long.valueOf(zzwN) : null;
                zzdQ.zzwV();
                com_google_android_gms_internal_zzcjy.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                com_google_android_gms_internal_zzcjy.zzbvQ = Long.valueOf(zzcel.zzwP());
                com_google_android_gms_internal_zzcjy.zzbvG = Long.valueOf(this.zzvy.currentTimeMillis());
                com_google_android_gms_internal_zzcjy.zzbvW = Boolean.TRUE;
                zzdQ.zzL(com_google_android_gms_internal_zzcjy.zzbvH.longValue());
                zzdQ.zzM(com_google_android_gms_internal_zzcjy.zzbvI.longValue());
                zzwz().zza(zzdQ);
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzcjx.zzLT()];
                    acy zzc = acy.zzc(bArr, 0, bArr.length);
                    com_google_android_gms_internal_zzcjx.zza(zzc);
                    zzc.zzLK();
                    return zzwB().zzl(bArr);
                } catch (IOException e) {
                    zzwF().zzyx().zze("Data loss. Failed to bundle and serialize. appId", zzcfk.zzdZ(str), e);
                    return null;
                }
            } else {
                zzwF().zzyC().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzwz().endTransaction();
                return bArr;
            }
        } finally {
            zzwz().endTransaction();
        }
    }

    public final void zzam(boolean z) {
        zzzc();
    }

    @WorkerThread
    final void zzb(zzcej com_google_android_gms_internal_zzcej, zzceg com_google_android_gms_internal_zzceg) {
        boolean z = true;
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        zzbo.zzcF(com_google_android_gms_internal_zzcej.packageName);
        zzbo.zzu(com_google_android_gms_internal_zzcej.zzbpc);
        zzbo.zzu(com_google_android_gms_internal_zzcej.zzbpd);
        zzbo.zzcF(com_google_android_gms_internal_zzcej.zzbpd.name);
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ)) {
            if (com_google_android_gms_internal_zzceg.zzboV) {
                zzcej com_google_android_gms_internal_zzcej2 = new zzcej(com_google_android_gms_internal_zzcej);
                com_google_android_gms_internal_zzcej2.zzbpf = false;
                zzwz().beginTransaction();
                try {
                    zzcej zzH = zzwz().zzH(com_google_android_gms_internal_zzcej2.packageName, com_google_android_gms_internal_zzcej2.zzbpd.name);
                    if (!(zzH == null || zzH.zzbpc.equals(com_google_android_gms_internal_zzcej2.zzbpc))) {
                        zzwF().zzyz().zzd("Updating a conditional user property with different origin. name, origin, origin (from DB)", zzwA().zzdY(com_google_android_gms_internal_zzcej2.zzbpd.name), com_google_android_gms_internal_zzcej2.zzbpc, zzH.zzbpc);
                    }
                    if (zzH != null && zzH.zzbpf) {
                        com_google_android_gms_internal_zzcej2.zzbpc = zzH.zzbpc;
                        com_google_android_gms_internal_zzcej2.zzbpe = zzH.zzbpe;
                        com_google_android_gms_internal_zzcej2.zzbpi = zzH.zzbpi;
                        com_google_android_gms_internal_zzcej2.zzbpg = zzH.zzbpg;
                        com_google_android_gms_internal_zzcej2.zzbpj = zzH.zzbpj;
                        com_google_android_gms_internal_zzcej2.zzbpf = zzH.zzbpf;
                        com_google_android_gms_internal_zzcej2.zzbpd = new zzcjh(com_google_android_gms_internal_zzcej2.zzbpd.name, zzH.zzbpd.zzbuy, com_google_android_gms_internal_zzcej2.zzbpd.getValue(), zzH.zzbpd.zzbpc);
                        z = false;
                    } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzcej2.zzbpg)) {
                        com_google_android_gms_internal_zzcej2.zzbpd = new zzcjh(com_google_android_gms_internal_zzcej2.zzbpd.name, com_google_android_gms_internal_zzcej2.zzbpe, com_google_android_gms_internal_zzcej2.zzbpd.getValue(), com_google_android_gms_internal_zzcej2.zzbpd.zzbpc);
                        com_google_android_gms_internal_zzcej2.zzbpf = true;
                    } else {
                        z = false;
                    }
                    if (com_google_android_gms_internal_zzcej2.zzbpf) {
                        zzcjh com_google_android_gms_internal_zzcjh = com_google_android_gms_internal_zzcej2.zzbpd;
                        zzcjj com_google_android_gms_internal_zzcjj = new zzcjj(com_google_android_gms_internal_zzcej2.packageName, com_google_android_gms_internal_zzcej2.zzbpc, com_google_android_gms_internal_zzcjh.name, com_google_android_gms_internal_zzcjh.zzbuy, com_google_android_gms_internal_zzcjh.getValue());
                        if (zzwz().zza(com_google_android_gms_internal_zzcjj)) {
                            zzwF().zzyC().zzd("User property updated immediately", com_google_android_gms_internal_zzcej2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                        } else {
                            zzwF().zzyx().zzd("(2)Too many active user properties, ignoring", zzcfk.zzdZ(com_google_android_gms_internal_zzcej2.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                        }
                        if (z && com_google_android_gms_internal_zzcej2.zzbpj != null) {
                            zzc(new zzcey(com_google_android_gms_internal_zzcej2.zzbpj, com_google_android_gms_internal_zzcej2.zzbpe), com_google_android_gms_internal_zzceg);
                        }
                    }
                    if (zzwz().zza(com_google_android_gms_internal_zzcej2)) {
                        zzwF().zzyC().zzd("Conditional property added", com_google_android_gms_internal_zzcej2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcej2.zzbpd.name), com_google_android_gms_internal_zzcej2.zzbpd.getValue());
                    } else {
                        zzwF().zzyx().zzd("Too many conditional properties, ignoring", zzcfk.zzdZ(com_google_android_gms_internal_zzcej2.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcej2.zzbpd.name), com_google_android_gms_internal_zzcej2.zzbpd.getValue());
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceg);
            }
        }
    }

    @WorkerThread
    final void zzb(zzcey com_google_android_gms_internal_zzcey, zzceg com_google_android_gms_internal_zzceg) {
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzbo.zzcF(com_google_android_gms_internal_zzceg.packageName);
        zzwE().zzjC();
        zzkD();
        String str = com_google_android_gms_internal_zzceg.packageName;
        long j = com_google_android_gms_internal_zzcey.zzbpN;
        zzwB();
        if (!zzcjk.zzd(com_google_android_gms_internal_zzcey, com_google_android_gms_internal_zzceg)) {
            return;
        }
        if (com_google_android_gms_internal_zzceg.zzboV) {
            zzwz().beginTransaction();
            try {
                List emptyList;
                Object obj;
                zzcem zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying timed out conditional properties", zzcfk.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[]{str, String.valueOf(j)});
                }
                for (zzcej com_google_android_gms_internal_zzcej : r2) {
                    if (com_google_android_gms_internal_zzcej != null) {
                        zzwF().zzyC().zzd("User property timed out", com_google_android_gms_internal_zzcej.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcej.zzbpd.name), com_google_android_gms_internal_zzcej.zzbpd.getValue());
                        if (com_google_android_gms_internal_zzcej.zzbph != null) {
                            zzc(new zzcey(com_google_android_gms_internal_zzcej.zzbph, j), com_google_android_gms_internal_zzceg);
                        }
                        zzwz().zzI(str, com_google_android_gms_internal_zzcej.zzbpd.name);
                    }
                }
                zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying expired conditional properties", zzcfk.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[]{str, String.valueOf(j)});
                }
                List arrayList = new ArrayList(r2.size());
                for (zzcej com_google_android_gms_internal_zzcej2 : r2) {
                    if (com_google_android_gms_internal_zzcej2 != null) {
                        zzwF().zzyC().zzd("User property expired", com_google_android_gms_internal_zzcej2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcej2.zzbpd.name), com_google_android_gms_internal_zzcej2.zzbpd.getValue());
                        zzwz().zzF(str, com_google_android_gms_internal_zzcej2.zzbpd.name);
                        if (com_google_android_gms_internal_zzcej2.zzbpl != null) {
                            arrayList.add(com_google_android_gms_internal_zzcej2.zzbpl);
                        }
                        zzwz().zzI(str, com_google_android_gms_internal_zzcej2.zzbpd.name);
                    }
                }
                ArrayList arrayList2 = (ArrayList) arrayList;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcey((zzcey) obj, j), com_google_android_gms_internal_zzceg);
                }
                zzwz = zzwz();
                String str2 = com_google_android_gms_internal_zzcey.name;
                zzbo.zzcF(str);
                zzbo.zzcF(str2);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zzd("Invalid time querying triggered conditional properties", zzcfk.zzdZ(str), zzwz.zzwA().zzdW(str2), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[]{str, str2, String.valueOf(j)});
                }
                List arrayList3 = new ArrayList(r2.size());
                for (zzcej com_google_android_gms_internal_zzcej3 : r2) {
                    if (com_google_android_gms_internal_zzcej3 != null) {
                        zzcjh com_google_android_gms_internal_zzcjh = com_google_android_gms_internal_zzcej3.zzbpd;
                        zzcjj com_google_android_gms_internal_zzcjj = new zzcjj(com_google_android_gms_internal_zzcej3.packageName, com_google_android_gms_internal_zzcej3.zzbpc, com_google_android_gms_internal_zzcjh.name, j, com_google_android_gms_internal_zzcjh.getValue());
                        if (zzwz().zza(com_google_android_gms_internal_zzcjj)) {
                            zzwF().zzyC().zzd("User property triggered", com_google_android_gms_internal_zzcej3.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                        } else {
                            zzwF().zzyx().zzd("Too many active user properties, ignoring", zzcfk.zzdZ(com_google_android_gms_internal_zzcej3.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                        }
                        if (com_google_android_gms_internal_zzcej3.zzbpj != null) {
                            arrayList3.add(com_google_android_gms_internal_zzcej3.zzbpj);
                        }
                        com_google_android_gms_internal_zzcej3.zzbpd = new zzcjh(com_google_android_gms_internal_zzcjj);
                        com_google_android_gms_internal_zzcej3.zzbpf = true;
                        zzwz().zza(com_google_android_gms_internal_zzcej3);
                    }
                }
                zzc(com_google_android_gms_internal_zzcey, com_google_android_gms_internal_zzceg);
                arrayList2 = (ArrayList) arrayList3;
                int size2 = arrayList2.size();
                i = 0;
                while (i < size2) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcey((zzcey) obj, j), com_google_android_gms_internal_zzceg);
                }
                zzwz().setTransactionSuccessful();
            } finally {
                zzwz().endTransaction();
            }
        } else {
            zzf(com_google_android_gms_internal_zzceg);
        }
    }

    @WorkerThread
    final void zzb(zzcey com_google_android_gms_internal_zzcey, String str) {
        zzcef zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzbgz.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping event. appId", zzcfk.zzdZ(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzcey.name)) {
                zzwF().zzyz().zzj("Could not find package. appId", zzcfk.zzdZ(str));
            }
        }
        zzcey com_google_android_gms_internal_zzcey2 = com_google_android_gms_internal_zzcey;
        zzb(com_google_android_gms_internal_zzcey2, new zzceg(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0));
    }

    final void zzb(zzchi com_google_android_gms_internal_zzchi) {
        this.zzbsQ++;
    }

    @WorkerThread
    final void zzb(zzcjh com_google_android_gms_internal_zzcjh, zzceg com_google_android_gms_internal_zzceg) {
        int i = 0;
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ)) {
            if (com_google_android_gms_internal_zzceg.zzboV) {
                int zzes = zzwB().zzes(com_google_android_gms_internal_zzcjh.name);
                String zza;
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjk.zza(com_google_android_gms_internal_zzcjh.name, zzcel.zzxi(), true);
                    if (com_google_android_gms_internal_zzcjh.name != null) {
                        i = com_google_android_gms_internal_zzcjh.name.length();
                    }
                    zzwB().zza(com_google_android_gms_internal_zzceg.packageName, zzes, "_ev", zza, i);
                    return;
                }
                zzes = zzwB().zzl(com_google_android_gms_internal_zzcjh.name, com_google_android_gms_internal_zzcjh.getValue());
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjk.zza(com_google_android_gms_internal_zzcjh.name, zzcel.zzxi(), true);
                    Object value = com_google_android_gms_internal_zzcjh.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzwB().zza(com_google_android_gms_internal_zzceg.packageName, zzes, "_ev", zza, i);
                    return;
                }
                Object zzm = zzwB().zzm(com_google_android_gms_internal_zzcjh.name, com_google_android_gms_internal_zzcjh.getValue());
                if (zzm != null) {
                    zzcjj com_google_android_gms_internal_zzcjj = new zzcjj(com_google_android_gms_internal_zzceg.packageName, com_google_android_gms_internal_zzcjh.zzbpc, com_google_android_gms_internal_zzcjh.name, com_google_android_gms_internal_zzcjh.zzbuy, zzm);
                    zzwF().zzyC().zze("Setting user property", zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), zzm);
                    zzwz().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzceg);
                        boolean zza2 = zzwz().zza(com_google_android_gms_internal_zzcjj);
                        zzwz().setTransactionSuccessful();
                        if (zza2) {
                            zzwF().zzyC().zze("User property set", zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                        } else {
                            zzwF().zzyx().zze("Too many unique user properties are set. Ignoring user property", zzwA().zzdY(com_google_android_gms_internal_zzcjj.mName), com_google_android_gms_internal_zzcjj.mValue);
                            zzwB().zza(com_google_android_gms_internal_zzceg.packageName, 9, null, null, 0);
                        }
                        zzwz().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzwz().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zzf(com_google_android_gms_internal_zzceg);
        }
    }

    @WorkerThread
    final void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        boolean z = true;
        zzwE().zzjC();
        zzkD();
        zzbo.zzcF(str);
        if (bArr == null) {
            try {
                bArr = new byte[0];
            } catch (Throwable th2) {
                this.zzbsU = false;
                zzzg();
            }
        }
        zzwF().zzyD().zzj("onConfigFetched. Response size", Integer.valueOf(bArr.length));
        zzwz().beginTransaction();
        zzcef zzdQ = zzwz().zzdQ(str);
        boolean z2 = (i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null;
        if (zzdQ == null) {
            zzwF().zzyz().zzj("App does not exist in onConfigFetched. appId", zzcfk.zzdZ(str));
        } else if (z2 || i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS) {
            List list = map != null ? (List) map.get("Last-Modified") : null;
            String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
            if (i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS || i == 304) {
                if (zzwC().zzeh(str) == null && !zzwC().zzb(str, null, null)) {
                    zzwz().endTransaction();
                    this.zzbsU = false;
                    zzzg();
                    return;
                }
            } else if (!zzwC().zzb(str, bArr, str2)) {
                zzwz().endTransaction();
                this.zzbsU = false;
                zzzg();
                return;
            }
            zzdQ.zzR(this.zzvy.currentTimeMillis());
            zzwz().zza(zzdQ);
            if (i == WalletConstants.ERROR_CODE_INVALID_PARAMETERS) {
                zzwF().zzyA().zzj("Config not found. Using empty config. appId", str);
            } else {
                zzwF().zzyD().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
            }
            if (zzyU().zzlQ() && zzzb()) {
                zzza();
            } else {
                zzzc();
            }
        } else {
            zzdQ.zzS(this.zzvy.currentTimeMillis());
            zzwz().zza(zzdQ);
            zzwF().zzyD().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
            zzwC().zzej(str);
            zzwG().zzbrl.set(this.zzvy.currentTimeMillis());
            if (!(i == 503 || i == 429)) {
                z = false;
            }
            if (z) {
                zzwG().zzbrm.set(this.zzvy.currentTimeMillis());
            }
            zzzc();
        }
        zzwz().setTransactionSuccessful();
        zzwz().endTransaction();
        this.zzbsU = false;
        zzzg();
    }

    @WorkerThread
    final void zzc(zzcej com_google_android_gms_internal_zzcej, zzceg com_google_android_gms_internal_zzceg) {
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        zzbo.zzcF(com_google_android_gms_internal_zzcej.packageName);
        zzbo.zzu(com_google_android_gms_internal_zzcej.zzbpd);
        zzbo.zzcF(com_google_android_gms_internal_zzcej.zzbpd.name);
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ)) {
            if (com_google_android_gms_internal_zzceg.zzboV) {
                zzwz().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzceg);
                    zzcej zzH = zzwz().zzH(com_google_android_gms_internal_zzcej.packageName, com_google_android_gms_internal_zzcej.zzbpd.name);
                    if (zzH != null) {
                        zzwF().zzyC().zze("Removing conditional user property", com_google_android_gms_internal_zzcej.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcej.zzbpd.name));
                        zzwz().zzI(com_google_android_gms_internal_zzcej.packageName, com_google_android_gms_internal_zzcej.zzbpd.name);
                        if (zzH.zzbpf) {
                            zzwz().zzF(com_google_android_gms_internal_zzcej.packageName, com_google_android_gms_internal_zzcej.zzbpd.name);
                        }
                        if (com_google_android_gms_internal_zzcej.zzbpl != null) {
                            Bundle bundle = null;
                            if (com_google_android_gms_internal_zzcej.zzbpl.zzbpM != null) {
                                bundle = com_google_android_gms_internal_zzcej.zzbpl.zzbpM.zzyt();
                            }
                            zzc(zzwB().zza(com_google_android_gms_internal_zzcej.zzbpl.name, bundle, zzH.zzbpc, com_google_android_gms_internal_zzcej.zzbpl.zzbpN, true, false), com_google_android_gms_internal_zzceg);
                        }
                    } else {
                        zzwF().zzyz().zze("Conditional user property doesn't exist", zzcfk.zzdZ(com_google_android_gms_internal_zzcej.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcej.zzbpd.name));
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceg);
            }
        }
    }

    @WorkerThread
    final void zzc(zzcjh com_google_android_gms_internal_zzcjh, zzceg com_google_android_gms_internal_zzceg) {
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ)) {
            if (com_google_android_gms_internal_zzceg.zzboV) {
                zzwF().zzyC().zzj("Removing user property", zzwA().zzdY(com_google_android_gms_internal_zzcjh.name));
                zzwz().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzceg);
                    zzwz().zzF(com_google_android_gms_internal_zzceg.packageName, com_google_android_gms_internal_zzcjh.name);
                    zzwz().setTransactionSuccessful();
                    zzwF().zzyC().zzj("User property removed", zzwA().zzdY(com_google_android_gms_internal_zzcjh.name));
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceg);
            }
        }
    }

    final void zzd(zzceg com_google_android_gms_internal_zzceg) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzcF(com_google_android_gms_internal_zzceg.packageName);
        zzf(com_google_android_gms_internal_zzceg);
    }

    @WorkerThread
    final void zzd(zzcej com_google_android_gms_internal_zzcej) {
        zzceg zzel = zzel(com_google_android_gms_internal_zzcej.packageName);
        if (zzel != null) {
            zzb(com_google_android_gms_internal_zzcej, zzel);
        }
    }

    @WorkerThread
    public final void zze(zzceg com_google_android_gms_internal_zzceg) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzbo.zzcF(com_google_android_gms_internal_zzceg.packageName);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ)) {
            zzcef zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceg.packageName);
            if (!(zzdQ == null || !TextUtils.isEmpty(zzdQ.getGmpAppId()) || TextUtils.isEmpty(com_google_android_gms_internal_zzceg.zzboQ))) {
                zzdQ.zzR(0);
                zzwz().zza(zzdQ);
                zzwC().zzek(com_google_android_gms_internal_zzceg.packageName);
            }
            if (com_google_android_gms_internal_zzceg.zzboV) {
                int i;
                Bundle bundle;
                long j = com_google_android_gms_internal_zzceg.zzbpa;
                if (j == 0) {
                    j = this.zzvy.currentTimeMillis();
                }
                int i2 = com_google_android_gms_internal_zzceg.zzbpb;
                if (i2 == 0 || i2 == 1) {
                    i = i2;
                } else {
                    zzwF().zzyz().zze("Incorrect app type, assuming installed app. appId, appType", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), Integer.valueOf(i2));
                    i = 0;
                }
                zzwz().beginTransaction();
                zzcem zzwz;
                String zzhl;
                try {
                    zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceg.packageName);
                    if (!(zzdQ == null || zzdQ.getGmpAppId() == null || zzdQ.getGmpAppId().equals(com_google_android_gms_internal_zzceg.zzboQ))) {
                        zzwF().zzyz().zzj("New GMP App Id passed in. Removing cached database data. appId", zzcfk.zzdZ(zzdQ.zzhl()));
                        zzwz = zzwz();
                        zzhl = zzdQ.zzhl();
                        zzwz.zzkD();
                        zzwz.zzjC();
                        zzbo.zzcF(zzhl);
                        SQLiteDatabase writableDatabase = zzwz.getWritableDatabase();
                        String[] strArr = new String[]{zzhl};
                        i2 = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + ((((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("conditional_properties", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
                        if (i2 > 0) {
                            zzwz.zzwF().zzyD().zze("Deleted application data. app, records", zzhl, Integer.valueOf(i2));
                        }
                        zzdQ = null;
                    }
                } catch (SQLiteException e) {
                    zzwz.zzwF().zzyx().zze("Error deleting application data. appId, error", zzcfk.zzdZ(zzhl), e);
                } catch (Throwable th) {
                    zzwz().endTransaction();
                }
                if (zzdQ != null) {
                    if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(com_google_android_gms_internal_zzceg.zzbgW))) {
                        bundle = new Bundle();
                        bundle.putString("_pv", zzdQ.zzjH());
                        zzb(new zzcey("_au", new zzcev(bundle), "auto", j), com_google_android_gms_internal_zzceg);
                    }
                }
                zzf(com_google_android_gms_internal_zzceg);
                zzceu com_google_android_gms_internal_zzceu = null;
                if (i == 0) {
                    com_google_android_gms_internal_zzceu = zzwz().zzE(com_google_android_gms_internal_zzceg.packageName, "_f");
                } else if (i == 1) {
                    com_google_android_gms_internal_zzceu = zzwz().zzE(com_google_android_gms_internal_zzceg.packageName, "_v");
                }
                if (com_google_android_gms_internal_zzceu == null) {
                    long j2 = (1 + (j / 3600000)) * 3600000;
                    if (i == 0) {
                        zzb(new zzcjh("_fot", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzceg);
                        zzwE().zzjC();
                        zzkD();
                        Bundle bundle2 = new Bundle();
                        bundle2.putLong("_c", 1);
                        bundle2.putLong("_r", 1);
                        bundle2.putLong("_uwa", 0);
                        bundle2.putLong("_pfo", 0);
                        bundle2.putLong("_sys", 0);
                        bundle2.putLong("_sysu", 0);
                        if (this.mContext.getPackageManager() == null) {
                            zzwF().zzyx().zzj("PackageManager is null, first open report might be inaccurate. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName));
                        } else {
                            ApplicationInfo applicationInfo;
                            PackageInfo packageInfo = null;
                            try {
                                packageInfo = zzbgz.zzaP(this.mContext).getPackageInfo(com_google_android_gms_internal_zzceg.packageName, 0);
                            } catch (NameNotFoundException e2) {
                                zzwF().zzyx().zze("Package info is null, first open report might be inaccurate. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e2);
                            }
                            if (packageInfo != null) {
                                if (packageInfo.firstInstallTime != 0) {
                                    Object obj = null;
                                    if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) {
                                        bundle2.putLong("_uwa", 1);
                                    } else {
                                        obj = 1;
                                    }
                                    zzb(new zzcjh("_fi", j, Long.valueOf(obj != null ? 1 : 0), "auto"), com_google_android_gms_internal_zzceg);
                                }
                            }
                            try {
                                applicationInfo = zzbgz.zzaP(this.mContext).getApplicationInfo(com_google_android_gms_internal_zzceg.packageName, 0);
                            } catch (NameNotFoundException e22) {
                                zzwF().zzyx().zze("Application info is null, first open report might be inaccurate. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e22);
                                applicationInfo = null;
                            }
                            if (applicationInfo != null) {
                                if ((applicationInfo.flags & 1) != 0) {
                                    bundle2.putLong("_sys", 1);
                                }
                                if ((applicationInfo.flags & 128) != 0) {
                                    bundle2.putLong("_sysu", 1);
                                }
                            }
                        }
                        zzcem zzwz2 = zzwz();
                        String str = com_google_android_gms_internal_zzceg.packageName;
                        zzbo.zzcF(str);
                        zzwz2.zzjC();
                        zzwz2.zzkD();
                        j2 = zzwz2.zzL(str, "first_open_count");
                        if (j2 >= 0) {
                            bundle2.putLong("_pfo", j2);
                        }
                        zzb(new zzcey("_f", new zzcev(bundle2), "auto", j), com_google_android_gms_internal_zzceg);
                    } else if (i == 1) {
                        zzb(new zzcjh("_fvt", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzceg);
                        zzwE().zzjC();
                        zzkD();
                        bundle = new Bundle();
                        bundle.putLong("_c", 1);
                        bundle.putLong("_r", 1);
                        zzb(new zzcey("_v", new zzcev(bundle), "auto", j), com_google_android_gms_internal_zzceg);
                    }
                    bundle = new Bundle();
                    bundle.putLong("_et", 1);
                    zzb(new zzcey("_e", new zzcev(bundle), "auto", j), com_google_android_gms_internal_zzceg);
                } else if (com_google_android_gms_internal_zzceg.zzboW) {
                    zzb(new zzcey("_cd", new zzcev(new Bundle()), "auto", j), com_google_android_gms_internal_zzceg);
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzf(com_google_android_gms_internal_zzceg);
        }
    }

    @WorkerThread
    final void zze(zzcej com_google_android_gms_internal_zzcej) {
        zzceg zzel = zzel(com_google_android_gms_internal_zzcej.packageName);
        if (zzel != null) {
            zzc(com_google_android_gms_internal_zzcej, zzel);
        }
    }

    public final String zzem(String str) {
        Object e;
        try {
            return (String) zzwE().zze(new zzcgm(this, str)).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzwF().zzyx().zze("Failed to get app instance id. appId", zzcfk.zzdZ(str), e);
        return null;
    }

    final void zzkD() {
        if (!this.zzafK) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    public final zze zzkq() {
        return this.zzvy;
    }

    @WorkerThread
    final void zzl(Runnable runnable) {
        zzwE().zzjC();
        if (this.zzbsP == null) {
            this.zzbsP = new ArrayList();
        }
        this.zzbsP.add(runnable);
    }

    public final zzcfi zzwA() {
        zza(this.zzbsw);
        return this.zzbsw;
    }

    public final zzcjk zzwB() {
        zza(this.zzbsv);
        return this.zzbsv;
    }

    public final zzcge zzwC() {
        zza(this.zzbss);
        return this.zzbss;
    }

    public final zzciz zzwD() {
        zza(this.zzbsr);
        return this.zzbsr;
    }

    public final zzcgf zzwE() {
        zza(this.zzbsq);
        return this.zzbsq;
    }

    public final zzcfk zzwF() {
        zza(this.zzbsp);
        return this.zzbsp;
    }

    public final zzcfv zzwG() {
        zza(this.zzbso);
        return this.zzbso;
    }

    public final zzcel zzwH() {
        return this.zzbsn;
    }

    public final zzceb zzwr() {
        zza(this.zzbsI);
        return this.zzbsI;
    }

    public final zzcei zzws() {
        zza(this.zzbsH);
        return this.zzbsH;
    }

    public final zzchk zzwt() {
        zza(this.zzbsD);
        return this.zzbsD;
    }

    public final zzcff zzwu() {
        zza(this.zzbsE);
        return this.zzbsE;
    }

    public final zzces zzwv() {
        zza(this.zzbsC);
        return this.zzbsC;
    }

    public final zzcic zzww() {
        zza(this.zzbsB);
        return this.zzbsB;
    }

    public final zzchy zzwx() {
        zza(this.zzbsA);
        return this.zzbsA;
    }

    public final zzcfg zzwy() {
        zza(this.zzbsy);
        return this.zzbsy;
    }

    public final zzcem zzwz() {
        zza(this.zzbsx);
        return this.zzbsx;
    }

    @WorkerThread
    protected final boolean zzyP() {
        boolean z = false;
        zzkD();
        zzwE().zzjC();
        if (this.zzbsK == null || this.zzbsL == 0 || !(this.zzbsK == null || this.zzbsK.booleanValue() || Math.abs(this.zzvy.elapsedRealtime() - this.zzbsL) <= 1000)) {
            this.zzbsL = this.zzvy.elapsedRealtime();
            zzcel.zzxE();
            if (zzwB().zzbv("android.permission.INTERNET") && zzwB().zzbv("android.permission.ACCESS_NETWORK_STATE") && (zzbgz.zzaP(this.mContext).zzsl() || (zzcgb.zzj(this.mContext, false) && zzciv.zzk(this.mContext, false)))) {
                z = true;
            }
            this.zzbsK = Boolean.valueOf(z);
            if (this.zzbsK.booleanValue()) {
                this.zzbsK = Boolean.valueOf(zzwB().zzev(zzwu().getGmpAppId()));
            }
        }
        return this.zzbsK.booleanValue();
    }

    public final zzcfk zzyQ() {
        return (this.zzbsp == null || !this.zzbsp.isInitialized()) ? null : this.zzbsp;
    }

    final zzcgf zzyR() {
        return this.zzbsq;
    }

    public final AppMeasurement zzyS() {
        return this.zzbst;
    }

    public final FirebaseAnalytics zzyT() {
        return this.zzbsu;
    }

    public final zzcfo zzyU() {
        zza(this.zzbsz);
        return this.zzbsz;
    }

    final long zzyY() {
        Long valueOf = Long.valueOf(zzwG().zzbrp.get());
        return valueOf.longValue() == 0 ? this.zzbsX : Math.min(this.zzbsX, valueOf.longValue());
    }

    @WorkerThread
    public final void zzza() {
        boolean z = true;
        zzwE().zzjC();
        zzkD();
        this.zzbsW = true;
        String zzyc;
        String zzxO;
        try {
            zzcel.zzxE();
            Boolean zzyI = zzwG().zzyI();
            if (zzyI == null) {
                zzwF().zzyz().log("Upload data called on the client side before use of service was decided");
                this.zzbsW = false;
                zzzg();
            } else if (zzyI.booleanValue()) {
                zzwF().zzyx().log("Upload called in the client side when service should be used");
                this.zzbsW = false;
                zzzg();
            } else if (this.zzbsT > 0) {
                zzzc();
                this.zzbsW = false;
                zzzg();
            } else {
                zzwE().zzjC();
                if (this.zzbsO != null) {
                    zzwF().zzyD().log("Uploading requested multiple times");
                    this.zzbsW = false;
                    zzzg();
                } else if (zzyU().zzlQ()) {
                    long currentTimeMillis = this.zzvy.currentTimeMillis();
                    zzg(null, currentTimeMillis - zzcel.zzxP());
                    long j = zzwG().zzbrk.get();
                    if (j != 0) {
                        zzwF().zzyC().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
                    }
                    zzyc = zzwz().zzyc();
                    Object zzaa;
                    if (TextUtils.isEmpty(zzyc)) {
                        this.zzbsS = -1;
                        zzaa = zzwz().zzaa(currentTimeMillis - zzcel.zzxP());
                        if (!TextUtils.isEmpty(zzaa)) {
                            zzcef zzdQ = zzwz().zzdQ(zzaa);
                            if (zzdQ != null) {
                                zzb(zzdQ);
                            }
                        }
                    } else {
                        if (this.zzbsS == -1) {
                            this.zzbsS = zzwz().zzyj();
                        }
                        List<Pair> zzl = zzwz().zzl(zzyc, this.zzbsn.zzb(zzyc, zzcfa.zzbqb), Math.max(0, this.zzbsn.zzb(zzyc, zzcfa.zzbqc)));
                        if (!zzl.isEmpty()) {
                            zzcjy com_google_android_gms_internal_zzcjy;
                            Object obj;
                            int i;
                            List subList;
                            for (Pair pair : zzl) {
                                com_google_android_gms_internal_zzcjy = (zzcjy) pair.first;
                                if (!TextUtils.isEmpty(com_google_android_gms_internal_zzcjy.zzbvR)) {
                                    obj = com_google_android_gms_internal_zzcjy.zzbvR;
                                    break;
                                }
                            }
                            obj = null;
                            if (obj != null) {
                                for (i = 0; i < zzl.size(); i++) {
                                    com_google_android_gms_internal_zzcjy = (zzcjy) ((Pair) zzl.get(i)).first;
                                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzcjy.zzbvR) && !com_google_android_gms_internal_zzcjy.zzbvR.equals(obj)) {
                                        subList = zzl.subList(0, i);
                                        break;
                                    }
                                }
                            }
                            subList = zzl;
                            zzcjx com_google_android_gms_internal_zzcjx = new zzcjx();
                            com_google_android_gms_internal_zzcjx.zzbvB = new zzcjy[subList.size()];
                            Collection arrayList = new ArrayList(subList.size());
                            boolean z2 = zzcel.zzyb() && this.zzbsn.zzdO(zzyc);
                            for (i = 0; i < com_google_android_gms_internal_zzcjx.zzbvB.length; i++) {
                                com_google_android_gms_internal_zzcjx.zzbvB[i] = (zzcjy) ((Pair) subList.get(i)).first;
                                arrayList.add((Long) ((Pair) subList.get(i)).second);
                                com_google_android_gms_internal_zzcjx.zzbvB[i].zzbvQ = Long.valueOf(zzcel.zzwP());
                                com_google_android_gms_internal_zzcjx.zzbvB[i].zzbvG = Long.valueOf(currentTimeMillis);
                                com_google_android_gms_internal_zzcjx.zzbvB[i].zzbvW = Boolean.valueOf(zzcel.zzxE());
                                if (!z2) {
                                    com_google_android_gms_internal_zzcjx.zzbvB[i].zzbwd = null;
                                }
                            }
                            Object zza = zzwF().zzz(2) ? zzwA().zza(com_google_android_gms_internal_zzcjx) : null;
                            byte[] zzb = zzwB().zzb(com_google_android_gms_internal_zzcjx);
                            zzxO = zzcel.zzxO();
                            URL url = new URL(zzxO);
                            if (arrayList.isEmpty()) {
                                z = false;
                            }
                            zzbo.zzaf(z);
                            if (this.zzbsO != null) {
                                zzwF().zzyx().log("Set uploading progress before finishing the previous upload");
                            } else {
                                this.zzbsO = new ArrayList(arrayList);
                            }
                            zzwG().zzbrl.set(currentTimeMillis);
                            zzaa = "?";
                            if (com_google_android_gms_internal_zzcjx.zzbvB.length > 0) {
                                zzaa = com_google_android_gms_internal_zzcjx.zzbvB[0].zzaJ;
                            }
                            zzwF().zzyD().zzd("Uploading data. app, uncompressed size, data", zzaa, Integer.valueOf(zzb.length), zza);
                            this.zzbsV = true;
                            zzyU().zza(zzyc, url, zzb, null, new zzcgn(this));
                        }
                    }
                    this.zzbsW = false;
                    zzzg();
                } else {
                    zzwF().zzyD().log("Network not connected, ignoring upload request");
                    zzzc();
                    this.zzbsW = false;
                    zzzg();
                }
            }
        } catch (MalformedURLException e) {
            zzwF().zzyx().zze("Failed to parse upload URL. Not uploading. appId", zzcfk.zzdZ(zzyc), zzxO);
        } catch (Throwable th) {
            this.zzbsW = false;
            zzzg();
        }
    }

    final void zzzd() {
        this.zzbsR++;
    }

    @WorkerThread
    final void zzze() {
        zzwE().zzjC();
        zzkD();
        if (!this.zzbsJ) {
            zzwF().zzyB().log("This instance being marked as an uploader");
            zzwE().zzjC();
            zzkD();
            if (zzzf() && zzyX()) {
                int zza = zza(this.zzbsN);
                int zzyv = zzwu().zzyv();
                zzwE().zzjC();
                if (zza > zzyv) {
                    zzwF().zzyx().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                } else if (zza < zzyv) {
                    if (zza(zzyv, this.zzbsN)) {
                        zzwF().zzyD().zze("Storage version upgraded. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                    } else {
                        zzwF().zzyx().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                    }
                }
            }
            this.zzbsJ = true;
            zzzc();
        }
    }
}
