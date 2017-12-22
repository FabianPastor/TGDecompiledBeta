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

public class zzcgl {
    private static volatile zzcgl zzbsm;
    private final Context mContext;
    private final boolean zzafK;
    private final zzchz zzbsA;
    private final zzcid zzbsB;
    private final zzcet zzbsC;
    private final zzchl zzbsD;
    private final zzcfg zzbsE;
    private final zzcfu zzbsF;
    private final zzcjg zzbsG;
    private final zzcej zzbsH;
    private final zzcec zzbsI;
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
    private final long zzbsX = this.zzvw.currentTimeMillis();
    private final zzcem zzbsn = new zzcem(this);
    private final zzcfw zzbso;
    private final zzcfl zzbsp;
    private final zzcgg zzbsq;
    private final zzcja zzbsr;
    private final zzcgf zzbss;
    private final AppMeasurement zzbst;
    private final FirebaseAnalytics zzbsu;
    private final zzcjl zzbsv;
    private final zzcfj zzbsw;
    private final zzcen zzbsx;
    private final zzcfh zzbsy;
    private final zzcfp zzbsz;
    private final zze zzvw = zzi.zzrY();

    class zza implements zzcep {
        private /* synthetic */ zzcgl zzbsY;
        zzcjz zzbsZ;
        List<Long> zzbta;
        private long zzbtb;
        List<zzcjw> zztH;

        private zza(zzcgl com_google_android_gms_internal_zzcgl) {
            this.zzbsY = com_google_android_gms_internal_zzcgl;
        }

        private static long zza(zzcjw com_google_android_gms_internal_zzcjw) {
            return ((com_google_android_gms_internal_zzcjw.zzbvx.longValue() / 1000) / 60) / 60;
        }

        public final boolean zza(long j, zzcjw com_google_android_gms_internal_zzcjw) {
            zzbo.zzu(com_google_android_gms_internal_zzcjw);
            if (this.zztH == null) {
                this.zztH = new ArrayList();
            }
            if (this.zzbta == null) {
                this.zzbta = new ArrayList();
            }
            if (this.zztH.size() > 0 && zza((zzcjw) this.zztH.get(0)) != zza(com_google_android_gms_internal_zzcjw)) {
                return false;
            }
            long zzLV = this.zzbtb + ((long) com_google_android_gms_internal_zzcjw.zzLV());
            if (zzLV >= ((long) zzcem.zzxL())) {
                return false;
            }
            this.zzbtb = zzLV;
            this.zztH.add(com_google_android_gms_internal_zzcjw);
            this.zzbta.add(Long.valueOf(j));
            return this.zztH.size() < zzcem.zzxM();
        }

        public final void zzb(zzcjz com_google_android_gms_internal_zzcjz) {
            zzbo.zzu(com_google_android_gms_internal_zzcjz);
            this.zzbsZ = com_google_android_gms_internal_zzcjz;
        }
    }

    private zzcgl(zzchk com_google_android_gms_internal_zzchk) {
        zzcfn zzyB;
        zzbo.zzu(com_google_android_gms_internal_zzchk);
        this.mContext = com_google_android_gms_internal_zzchk.mContext;
        zzcfw com_google_android_gms_internal_zzcfw = new zzcfw(this);
        com_google_android_gms_internal_zzcfw.initialize();
        this.zzbso = com_google_android_gms_internal_zzcfw;
        zzcfl com_google_android_gms_internal_zzcfl = new zzcfl(this);
        com_google_android_gms_internal_zzcfl.initialize();
        this.zzbsp = com_google_android_gms_internal_zzcfl;
        zzwF().zzyB().zzj("App measurement is starting up, version", Long.valueOf(zzcem.zzwP()));
        zzcem.zzxE();
        zzwF().zzyB().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzcjl com_google_android_gms_internal_zzcjl = new zzcjl(this);
        com_google_android_gms_internal_zzcjl.initialize();
        this.zzbsv = com_google_android_gms_internal_zzcjl;
        zzcfj com_google_android_gms_internal_zzcfj = new zzcfj(this);
        com_google_android_gms_internal_zzcfj.initialize();
        this.zzbsw = com_google_android_gms_internal_zzcfj;
        zzcet com_google_android_gms_internal_zzcet = new zzcet(this);
        com_google_android_gms_internal_zzcet.initialize();
        this.zzbsC = com_google_android_gms_internal_zzcet;
        zzcfg com_google_android_gms_internal_zzcfg = new zzcfg(this);
        com_google_android_gms_internal_zzcfg.initialize();
        this.zzbsE = com_google_android_gms_internal_zzcfg;
        zzcem.zzxE();
        String zzhl = com_google_android_gms_internal_zzcfg.zzhl();
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
        zzcen com_google_android_gms_internal_zzcen = new zzcen(this);
        com_google_android_gms_internal_zzcen.initialize();
        this.zzbsx = com_google_android_gms_internal_zzcen;
        zzcfh com_google_android_gms_internal_zzcfh = new zzcfh(this);
        com_google_android_gms_internal_zzcfh.initialize();
        this.zzbsy = com_google_android_gms_internal_zzcfh;
        zzcej com_google_android_gms_internal_zzcej = new zzcej(this);
        com_google_android_gms_internal_zzcej.initialize();
        this.zzbsH = com_google_android_gms_internal_zzcej;
        this.zzbsI = new zzcec(this);
        zzcfp com_google_android_gms_internal_zzcfp = new zzcfp(this);
        com_google_android_gms_internal_zzcfp.initialize();
        this.zzbsz = com_google_android_gms_internal_zzcfp;
        zzchz com_google_android_gms_internal_zzchz = new zzchz(this);
        com_google_android_gms_internal_zzchz.initialize();
        this.zzbsA = com_google_android_gms_internal_zzchz;
        zzcid com_google_android_gms_internal_zzcid = new zzcid(this);
        com_google_android_gms_internal_zzcid.initialize();
        this.zzbsB = com_google_android_gms_internal_zzcid;
        zzchl com_google_android_gms_internal_zzchl = new zzchl(this);
        com_google_android_gms_internal_zzchl.initialize();
        this.zzbsD = com_google_android_gms_internal_zzchl;
        zzcjg com_google_android_gms_internal_zzcjg = new zzcjg(this);
        com_google_android_gms_internal_zzcjg.initialize();
        this.zzbsG = com_google_android_gms_internal_zzcjg;
        this.zzbsF = new zzcfu(this);
        this.zzbst = new AppMeasurement(this);
        this.zzbsu = new FirebaseAnalytics(this);
        zzcja com_google_android_gms_internal_zzcja = new zzcja(this);
        com_google_android_gms_internal_zzcja.initialize();
        this.zzbsr = com_google_android_gms_internal_zzcja;
        zzcgf com_google_android_gms_internal_zzcgf = new zzcgf(this);
        com_google_android_gms_internal_zzcgf.initialize();
        this.zzbss = com_google_android_gms_internal_zzcgf;
        zzcgg com_google_android_gms_internal_zzcgg = new zzcgg(this);
        com_google_android_gms_internal_zzcgg.initialize();
        this.zzbsq = com_google_android_gms_internal_zzcgg;
        if (this.zzbsQ != this.zzbsR) {
            zzwF().zzyx().zze("Not all components initialized", Integer.valueOf(this.zzbsQ), Integer.valueOf(this.zzbsR));
        }
        this.zzafK = true;
        zzcem.zzxE();
        if (this.mContext.getApplicationContext() instanceof Application) {
            zzchl zzwt = zzwt();
            if (zzwt.getContext().getApplicationContext() instanceof Application) {
                Application application = (Application) zzwt.getContext().getApplicationContext();
                if (zzwt.zzbto == null) {
                    zzwt.zzbto = new zzchy(zzwt);
                }
                application.unregisterActivityLifecycleCallbacks(zzwt.zzbto);
                application.registerActivityLifecycleCallbacks(zzwt.zzbto);
                zzwt.zzwF().zzyD().log("Registered activity lifecycle callback");
            }
        } else {
            zzwF().zzyz().log("Application context is not an Application");
        }
        this.zzbsq.zzj(new zzcgm(this));
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

    private final void zza(zzceu com_google_android_gms_internal_zzceu, zzceh com_google_android_gms_internal_zzceh) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzceu);
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzbo.zzcF(com_google_android_gms_internal_zzceu.mAppId);
        zzbo.zzaf(com_google_android_gms_internal_zzceu.mAppId.equals(com_google_android_gms_internal_zzceh.packageName));
        zzcjz com_google_android_gms_internal_zzcjz = new zzcjz();
        com_google_android_gms_internal_zzcjz.zzbvD = Integer.valueOf(1);
        com_google_android_gms_internal_zzcjz.zzbvL = "android";
        com_google_android_gms_internal_zzcjz.zzaH = com_google_android_gms_internal_zzceh.packageName;
        com_google_android_gms_internal_zzcjz.zzboR = com_google_android_gms_internal_zzceh.zzboR;
        com_google_android_gms_internal_zzcjz.zzbgW = com_google_android_gms_internal_zzceh.zzbgW;
        com_google_android_gms_internal_zzcjz.zzbvY = com_google_android_gms_internal_zzceh.zzboX == -2147483648L ? null : Integer.valueOf((int) com_google_android_gms_internal_zzceh.zzboX);
        com_google_android_gms_internal_zzcjz.zzbvP = Long.valueOf(com_google_android_gms_internal_zzceh.zzboS);
        com_google_android_gms_internal_zzcjz.zzboQ = com_google_android_gms_internal_zzceh.zzboQ;
        com_google_android_gms_internal_zzcjz.zzbvU = com_google_android_gms_internal_zzceh.zzboT == 0 ? null : Long.valueOf(com_google_android_gms_internal_zzceh.zzboT);
        Pair zzeb = zzwG().zzeb(com_google_android_gms_internal_zzceh.packageName);
        if (!(zzeb == null || TextUtils.isEmpty((CharSequence) zzeb.first))) {
            com_google_android_gms_internal_zzcjz.zzbvR = (String) zzeb.first;
            com_google_android_gms_internal_zzcjz.zzbvS = (Boolean) zzeb.second;
        }
        zzwv().zzkD();
        com_google_android_gms_internal_zzcjz.zzbvM = Build.MODEL;
        zzwv().zzkD();
        com_google_android_gms_internal_zzcjz.zzaY = VERSION.RELEASE;
        com_google_android_gms_internal_zzcjz.zzbvO = Integer.valueOf((int) zzwv().zzyq());
        com_google_android_gms_internal_zzcjz.zzbvN = zzwv().zzyr();
        com_google_android_gms_internal_zzcjz.zzbvQ = null;
        com_google_android_gms_internal_zzcjz.zzbvG = null;
        com_google_android_gms_internal_zzcjz.zzbvH = null;
        com_google_android_gms_internal_zzcjz.zzbvI = null;
        com_google_android_gms_internal_zzcjz.zzbwc = Long.valueOf(com_google_android_gms_internal_zzceh.zzboZ);
        if (isEnabled() && zzcem.zzyb()) {
            zzwu();
            com_google_android_gms_internal_zzcjz.zzbwd = null;
        }
        zzceg zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceh.packageName);
        if (zzdQ == null) {
            zzdQ = new zzceg(this, com_google_android_gms_internal_zzceh.packageName);
            zzdQ.zzdG(zzwu().zzyu());
            zzdQ.zzdJ(com_google_android_gms_internal_zzceh.zzboY);
            zzdQ.zzdH(com_google_android_gms_internal_zzceh.zzboQ);
            zzdQ.zzdI(zzwG().zzec(com_google_android_gms_internal_zzceh.packageName));
            zzdQ.zzQ(0);
            zzdQ.zzL(0);
            zzdQ.zzM(0);
            zzdQ.setAppVersion(com_google_android_gms_internal_zzceh.zzbgW);
            zzdQ.zzN(com_google_android_gms_internal_zzceh.zzboX);
            zzdQ.zzdK(com_google_android_gms_internal_zzceh.zzboR);
            zzdQ.zzO(com_google_android_gms_internal_zzceh.zzboS);
            zzdQ.zzP(com_google_android_gms_internal_zzceh.zzboT);
            zzdQ.setMeasurementEnabled(com_google_android_gms_internal_zzceh.zzboV);
            zzdQ.zzZ(com_google_android_gms_internal_zzceh.zzboZ);
            zzwz().zza(zzdQ);
        }
        com_google_android_gms_internal_zzcjz.zzbvT = zzdQ.getAppInstanceId();
        com_google_android_gms_internal_zzcjz.zzboY = zzdQ.zzwK();
        List zzdP = zzwz().zzdP(com_google_android_gms_internal_zzceh.packageName);
        com_google_android_gms_internal_zzcjz.zzbvF = new zzckb[zzdP.size()];
        for (int i = 0; i < zzdP.size(); i++) {
            zzckb com_google_android_gms_internal_zzckb = new zzckb();
            com_google_android_gms_internal_zzcjz.zzbvF[i] = com_google_android_gms_internal_zzckb;
            com_google_android_gms_internal_zzckb.name = ((zzcjk) zzdP.get(i)).mName;
            com_google_android_gms_internal_zzckb.zzbwh = Long.valueOf(((zzcjk) zzdP.get(i)).zzbuC);
            zzwB().zza(com_google_android_gms_internal_zzckb, ((zzcjk) zzdP.get(i)).mValue);
        }
        try {
            boolean z;
            long zza = zzwz().zza(com_google_android_gms_internal_zzcjz);
            zzcen zzwz = zzwz();
            if (com_google_android_gms_internal_zzceu.zzbpF != null) {
                Iterator it = com_google_android_gms_internal_zzceu.zzbpF.iterator();
                while (it.hasNext()) {
                    if ("_r".equals((String) it.next())) {
                        z = true;
                        break;
                    }
                }
                z = zzwC().zzO(com_google_android_gms_internal_zzceu.mAppId, com_google_android_gms_internal_zzceu.mName);
                zzceo zza2 = zzwz().zza(zzyZ(), com_google_android_gms_internal_zzceu.mAppId, false, false, false, false, false);
                if (z && zza2.zzbpy < ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzceu.mAppId))) {
                    z = true;
                    if (zzwz.zza(com_google_android_gms_internal_zzceu, zza, z)) {
                        this.zzbsT = 0;
                    }
                }
            }
            z = false;
            if (zzwz.zza(com_google_android_gms_internal_zzceu, zza, z)) {
                this.zzbsT = 0;
            }
        } catch (IOException e) {
            zzwF().zzyx().zze("Data loss. Failed to insert raw event metadata. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcjz.zzaH), e);
        }
    }

    private static void zza(zzchi com_google_android_gms_internal_zzchi) {
        if (com_google_android_gms_internal_zzchi == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private static void zza(zzchj com_google_android_gms_internal_zzchj) {
        if (com_google_android_gms_internal_zzchj == null) {
            throw new IllegalStateException("Component not created");
        } else if (!com_google_android_gms_internal_zzchj.isInitialized()) {
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

    private final zzcjv[] zza(String str, zzckb[] com_google_android_gms_internal_zzckbArr, zzcjw[] com_google_android_gms_internal_zzcjwArr) {
        zzbo.zzcF(str);
        return zzws().zza(str, com_google_android_gms_internal_zzcjwArr, com_google_android_gms_internal_zzckbArr);
    }

    @WorkerThread
    private final void zzb(zzceg com_google_android_gms_internal_zzceg) {
        Map map = null;
        zzwE().zzjC();
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzceg.getGmpAppId())) {
            zzb(com_google_android_gms_internal_zzceg.zzhl(), 204, null, null, null);
            return;
        }
        String gmpAppId = com_google_android_gms_internal_zzceg.getGmpAppId();
        String appInstanceId = com_google_android_gms_internal_zzceg.getAppInstanceId();
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzcfb.zzbpZ.get()).encodedAuthority((String) zzcfb.zzbqa.get());
        String str = "config/app/";
        String valueOf = String.valueOf(gmpAppId);
        encodedAuthority.path(valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).appendQueryParameter("app_instance_id", appInstanceId).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", "11020");
        valueOf = builder.build().toString();
        try {
            URL url = new URL(valueOf);
            zzwF().zzyD().zzj("Fetching remote configuration", com_google_android_gms_internal_zzceg.zzhl());
            zzcjt zzeh = zzwC().zzeh(com_google_android_gms_internal_zzceg.zzhl());
            CharSequence zzei = zzwC().zzei(com_google_android_gms_internal_zzceg.zzhl());
            if (!(zzeh == null || TextUtils.isEmpty(zzei))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzei);
            }
            this.zzbsU = true;
            zzyU().zza(com_google_android_gms_internal_zzceg.zzhl(), url, map, new zzcgp(this));
        } catch (MalformedURLException e) {
            zzwF().zzyx().zze("Failed to parse config URL. Not fetching. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceg.zzhl()), valueOf);
        }
    }

    public static zzcgl zzbj(Context context) {
        zzbo.zzu(context);
        zzbo.zzu(context.getApplicationContext());
        if (zzbsm == null) {
            synchronized (zzcgl.class) {
                if (zzbsm == null) {
                    zzbsm = new zzcgl(new zzchk(context));
                }
            }
        }
        return zzbsm;
    }

    @WorkerThread
    private final void zzc(zzcez com_google_android_gms_internal_zzcez, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzbo.zzcF(com_google_android_gms_internal_zzceh.packageName);
        long nanoTime = System.nanoTime();
        zzwE().zzjC();
        zzkD();
        String str = com_google_android_gms_internal_zzceh.packageName;
        zzwB();
        if (!zzcjl.zzd(com_google_android_gms_internal_zzcez, com_google_android_gms_internal_zzceh)) {
            return;
        }
        if (!com_google_android_gms_internal_zzceh.zzboV) {
            zzf(com_google_android_gms_internal_zzceh);
        } else if (zzwC().zzN(str, com_google_android_gms_internal_zzcez.name)) {
            zzwF().zzyz().zze("Dropping blacklisted event. appId", zzcfl.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzcez.name));
            Object obj = (zzwB().zzeA(str) || zzwB().zzeB(str)) ? 1 : null;
            if (obj == null && !"_err".equals(com_google_android_gms_internal_zzcez.name)) {
                zzwB().zza(str, 11, "_ev", com_google_android_gms_internal_zzcez.name, 0);
            }
            if (obj != null) {
                zzceg zzdQ = zzwz().zzdQ(str);
                if (zzdQ != null) {
                    if (Math.abs(this.zzvw.currentTimeMillis() - Math.max(zzdQ.zzwU(), zzdQ.zzwT())) > zzcem.zzxI()) {
                        zzwF().zzyC().log("Fetching config for blacklisted app");
                        zzb(zzdQ);
                    }
                }
            }
        } else {
            Bundle zzyt;
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Logging event", zzwA().zzb(com_google_android_gms_internal_zzcez));
            }
            zzwz().beginTransaction();
            zzcen zzwz;
            try {
                zzyt = com_google_android_gms_internal_zzcez.zzbpM.zzyt();
                zzf(com_google_android_gms_internal_zzceh);
                if ("_iap".equals(com_google_android_gms_internal_zzcez.name) || Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzcez.name)) {
                    long round;
                    Object string = zzyt.getString(Param.CURRENCY);
                    if (Event.ECOMMERCE_PURCHASE.equals(com_google_android_gms_internal_zzcez.name)) {
                        double d = zzyt.getDouble(Param.VALUE) * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzyt.getLong(Param.VALUE)) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzwF().zzyz().zze("Data lost. Currency value is too big. appId", zzcfl.zzdZ(str), Double.valueOf(d));
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
                            zzcjk zzG = zzwz().zzG(str, concat);
                            if (zzG == null || !(zzG.mValue instanceof Long)) {
                                zzwz = zzwz();
                                int zzb = this.zzbsn.zzb(str, zzcfb.zzbqz) - 1;
                                zzbo.zzcF(str);
                                zzwz.zzjC();
                                zzwz.zzkD();
                                zzwz.getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(zzb)});
                                zzG = new zzcjk(str, com_google_android_gms_internal_zzcez.zzbpc, concat, this.zzvw.currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzG = new zzcjk(str, com_google_android_gms_internal_zzcez.zzbpc, concat, this.zzvw.currentTimeMillis(), Long.valueOf(round + ((Long) zzG.mValue).longValue()));
                            }
                            if (!zzwz().zza(zzG)) {
                                zzwF().zzyx().zzd("Too many unique user properties are set. Ignoring user property. appId", zzcfl.zzdZ(str), zzwA().zzdY(zzG.mName), zzG.mValue);
                                zzwB().zza(str, 9, null, null, 0);
                            }
                        }
                    }
                }
            } catch (SQLiteException e) {
                zzwz.zzwF().zzyx().zze("Error pruning currencies. appId", zzcfl.zzdZ(str), e);
            } catch (Throwable th) {
                zzwz().endTransaction();
            }
            boolean zzeo = zzcjl.zzeo(com_google_android_gms_internal_zzcez.name);
            boolean equals = "_err".equals(com_google_android_gms_internal_zzcez.name);
            zzceo zza = zzwz().zza(zzyZ(), str, true, zzeo, false, equals, false);
            long zzxq = zza.zzbpv - zzcem.zzxq();
            if (zzxq > 0) {
                if (zzxq % 1000 == 1) {
                    zzwF().zzyx().zze("Data loss. Too many events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpv));
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzcev com_google_android_gms_internal_zzcev;
            if (zzeo) {
                zzxq = zza.zzbpu - zzcem.zzxr();
                if (zzxq > 0) {
                    if (zzxq % 1000 == 1) {
                        zzwF().zzyx().zze("Data loss. Too many public events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpu));
                    }
                    zzwB().zza(str, 16, "_ev", com_google_android_gms_internal_zzcez.name, 0);
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            if (equals) {
                zzxq = zza.zzbpx - ((long) Math.max(0, Math.min(1000000, this.zzbsn.zzb(com_google_android_gms_internal_zzceh.packageName, zzcfb.zzbqg))));
                if (zzxq > 0) {
                    if (zzxq == 1) {
                        zzwF().zzyx().zze("Too many error events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpx));
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            zzwB().zza(zzyt, "_o", com_google_android_gms_internal_zzcez.zzbpc);
            if (zzwB().zzey(str)) {
                zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                zzwB().zza(zzyt, "_r", Long.valueOf(1));
            }
            zzxq = zzwz().zzdR(str);
            if (zzxq > 0) {
                zzwF().zzyz().zze("Data lost. Too many events stored on disk, deleted. appId", zzcfl.zzdZ(str), Long.valueOf(zzxq));
            }
            zzceu com_google_android_gms_internal_zzceu = new zzceu(this, com_google_android_gms_internal_zzcez.zzbpc, str, com_google_android_gms_internal_zzcez.name, com_google_android_gms_internal_zzcez.zzbpN, 0, zzyt);
            zzcev zzE = zzwz().zzE(str, com_google_android_gms_internal_zzceu.mName);
            if (zzE == null) {
                long zzdU = zzwz().zzdU(str);
                zzcem.zzxp();
                if (zzdU >= 500) {
                    zzwF().zzyx().zzd("Too many event names used, ignoring event. appId, name, supported count", zzcfl.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzceu.mName), Integer.valueOf(zzcem.zzxp()));
                    zzwB().zza(str, 8, null, null, 0);
                    zzwz().endTransaction();
                    return;
                }
                com_google_android_gms_internal_zzcev = new zzcev(str, com_google_android_gms_internal_zzceu.mName, 0, 0, com_google_android_gms_internal_zzceu.zzayS);
            } else {
                com_google_android_gms_internal_zzceu = com_google_android_gms_internal_zzceu.zza(this, zzE.zzbpI);
                com_google_android_gms_internal_zzcev = zzE.zzab(com_google_android_gms_internal_zzceu.zzayS);
            }
            zzwz().zza(com_google_android_gms_internal_zzcev);
            zza(com_google_android_gms_internal_zzceu, com_google_android_gms_internal_zzceh);
            zzwz().setTransactionSuccessful();
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Event recorded", zzwA().zza(com_google_android_gms_internal_zzceu));
            }
            zzwz().endTransaction();
            zzzc();
            zzwF().zzyD().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / C.MICROS_PER_SECOND));
        }
    }

    @WorkerThread
    private final zzceh zzel(String str) {
        zzceg zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzbha.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping. appId", zzcfl.zzdZ(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzceh(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0);
    }

    @WorkerThread
    private final void zzf(zzceh com_google_android_gms_internal_zzceh) {
        Object obj = 1;
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzbo.zzcF(com_google_android_gms_internal_zzceh.packageName);
        zzceg zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceh.packageName);
        String zzec = zzwG().zzec(com_google_android_gms_internal_zzceh.packageName);
        Object obj2 = null;
        if (zzdQ == null) {
            zzceg com_google_android_gms_internal_zzceg = new zzceg(this, com_google_android_gms_internal_zzceh.packageName);
            com_google_android_gms_internal_zzceg.zzdG(zzwu().zzyu());
            com_google_android_gms_internal_zzceg.zzdI(zzec);
            zzdQ = com_google_android_gms_internal_zzceg;
            obj2 = 1;
        } else if (!zzec.equals(zzdQ.zzwJ())) {
            zzdQ.zzdI(zzec);
            zzdQ.zzdG(zzwu().zzyu());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ) || com_google_android_gms_internal_zzceh.zzboQ.equals(zzdQ.getGmpAppId()))) {
            zzdQ.zzdH(com_google_android_gms_internal_zzceh.zzboQ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboY) || com_google_android_gms_internal_zzceh.zzboY.equals(zzdQ.zzwK()))) {
            zzdQ.zzdJ(com_google_android_gms_internal_zzceh.zzboY);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzceh.zzboS == 0 || com_google_android_gms_internal_zzceh.zzboS == zzdQ.zzwP())) {
            zzdQ.zzO(com_google_android_gms_internal_zzceh.zzboS);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzbgW) || com_google_android_gms_internal_zzceh.zzbgW.equals(zzdQ.zzjH()))) {
            zzdQ.setAppVersion(com_google_android_gms_internal_zzceh.zzbgW);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceh.zzboX != zzdQ.zzwN()) {
            zzdQ.zzN(com_google_android_gms_internal_zzceh.zzboX);
            obj2 = 1;
        }
        if (!(com_google_android_gms_internal_zzceh.zzboR == null || com_google_android_gms_internal_zzceh.zzboR.equals(zzdQ.zzwO()))) {
            zzdQ.zzdK(com_google_android_gms_internal_zzceh.zzboR);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceh.zzboT != zzdQ.zzwQ()) {
            zzdQ.zzP(com_google_android_gms_internal_zzceh.zzboT);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceh.zzboV != zzdQ.zzwR()) {
            zzdQ.setMeasurementEnabled(com_google_android_gms_internal_zzceh.zzboV);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboU) || com_google_android_gms_internal_zzceh.zzboU.equals(zzdQ.zzxc()))) {
            zzdQ.zzdL(com_google_android_gms_internal_zzceh.zzboU);
            obj2 = 1;
        }
        if (com_google_android_gms_internal_zzceh.zzboZ != zzdQ.zzxe()) {
            zzdQ.zzZ(com_google_android_gms_internal_zzceh.zzboZ);
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
        String str2;
        boolean z;
        Object obj;
        zzcjx[] com_google_android_gms_internal_zzcjxArr;
        int length;
        zzcjt zzeh;
        Throwable th;
        zzwz().beginTransaction();
        zzcgl com_google_android_gms_internal_zzcgl = this;
        zza com_google_android_gms_internal_zzcgl_zza = new zza();
        zzcen zzwz = zzwz();
        String str3 = null;
        long j2 = this.zzbsS;
        zzbo.zzu(com_google_android_gms_internal_zzcgl_zza);
        zzwz.zzjC();
        zzwz.zzkD();
        Cursor cursor = null;
        Object obj2;
        zzcjz com_google_android_gms_internal_zzcjz;
        int i;
        int i2;
        boolean zzO;
        Object obj3;
        zzcjx[] com_google_android_gms_internal_zzcjxArr2;
        int length2;
        int i3;
        zzcjx com_google_android_gms_internal_zzcjx;
        zzcjx[] com_google_android_gms_internal_zzcjxArr3;
        zzcjx com_google_android_gms_internal_zzcjx2;
        zzcjw com_google_android_gms_internal_zzcjw;
        int i4;
        Object obj4;
        zzcjx com_google_android_gms_internal_zzcjx3;
        int i5;
        zzcjx com_google_android_gms_internal_zzcjx4;
        int i6;
        boolean z2;
        int i7;
        boolean z3;
        String str4;
        zzceg zzdQ;
        long zzwM;
        long zzwL;
        zzcen zzwz2;
        boolean z4;
        try {
            String[] strArr;
            String str5;
            String str6;
            Cursor cursor2;
            SQLiteDatabase writableDatabase = zzwz.getWritableDatabase();
            if (TextUtils.isEmpty(null)) {
                strArr = j2 != -1 ? new String[]{String.valueOf(j2), String.valueOf(j)} : new String[]{String.valueOf(j)};
                str5 = j2 != -1 ? "rowid <= ? and " : "";
                cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str5).length() + 148).append("select app_id, metadata_fingerprint from raw_events where ").append(str5).append("app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;").toString(), strArr);
                if (cursor.moveToFirst()) {
                    str3 = cursor.getString(0);
                    str5 = cursor.getString(1);
                    cursor.close();
                    str6 = str5;
                    cursor2 = cursor;
                    str2 = str3;
                } else {
                    if (cursor != null) {
                        cursor.close();
                    }
                    obj2 = (com_google_android_gms_internal_zzcgl_zza.zztH != null || com_google_android_gms_internal_zzcgl_zza.zztH.isEmpty()) ? 1 : null;
                    if (obj2 != null) {
                        z = false;
                        com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                if (!zzO) {
                                    zzwB();
                                }
                                obj3 = null;
                                obj = null;
                                if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                }
                                com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj3 = 1;
                                        obj2 = obj;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        obj2 = obj;
                                    } else {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                    }
                                    i3++;
                                    obj = obj2;
                                }
                                if (obj3 == null && zzO) {
                                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_c";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                }
                                if (obj == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_r";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                            i4++;
                                        } else {
                                            obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                            }
                                            if (i4 < obj.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                        }
                                    }
                                } else {
                                    z = true;
                                }
                                if (zzcjl.zzeo(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name) && zzO && zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, true, false, false).zzbpw > ((long) this.zzbsn.zzb(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, zzcfb.zzbqi))) {
                                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    obj4 = null;
                                    com_google_android_gms_internal_zzcjx3 = null;
                                    com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                    length = com_google_android_gms_internal_zzcjxArr.length;
                                    i5 = 0;
                                    while (i5 < length) {
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                            obj = obj4;
                                        } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                            obj = obj4;
                                        } else {
                                            com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                            i6 = 1;
                                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                        }
                                        i5++;
                                        obj4 = obj;
                                        com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                    }
                                    if (obj4 == null && com_google_android_gms_internal_zzcjx3 != null) {
                                        com_google_android_gms_internal_zzcjxArr = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                        i3 = 0;
                                        zzcjx[] com_google_android_gms_internal_zzcjxArr4 = com_google_android_gms_internal_zzcjw.zzbvw;
                                        int length3 = com_google_android_gms_internal_zzcjxArr4.length;
                                        i5 = 0;
                                        while (i5 < length3) {
                                            zzcjx com_google_android_gms_internal_zzcjx5 = com_google_android_gms_internal_zzcjxArr4[i5];
                                            if (com_google_android_gms_internal_zzcjx5 != com_google_android_gms_internal_zzcjx3) {
                                                i4 = i3 + 1;
                                                com_google_android_gms_internal_zzcjxArr[i3] = com_google_android_gms_internal_zzcjx5;
                                            } else {
                                                i4 = i3;
                                            }
                                            i5++;
                                            i3 = i4;
                                        }
                                        com_google_android_gms_internal_zzcjw.zzbvw = com_google_android_gms_internal_zzcjxArr;
                                        z2 = z;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                        i7 = i4;
                                        z3 = z2;
                                    } else if (com_google_android_gms_internal_zzcjx3 == null) {
                                        com_google_android_gms_internal_zzcjx3.name = "_err";
                                        com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                        z2 = z;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                        i7 = i4;
                                        z3 = z2;
                                    } else {
                                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    }
                                }
                                z2 = z;
                                i4 = i + 1;
                                com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                i7 = i4;
                                z3 = z2;
                            } else {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                obj2 = (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH) || zzwB().zzeB(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) ? 1 : null;
                                if (obj2 == null || "_err".equals(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                    i7 = i;
                                    z3 = z;
                                } else {
                                    zzwB().zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, 11, "_ev", ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name, 0);
                                    i7 = i;
                                    z3 = z;
                                }
                            }
                            i2++;
                            i = i7;
                            z = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                        com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjw com_google_android_gms_internal_zzcjw2 : com_google_android_gms_internal_zzcjz.zzbvE) {
                            if (com_google_android_gms_internal_zzcjw2.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw2.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjw2.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw2.zzbvx;
                            }
                        }
                        str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                        zzdQ = zzwz().zzdQ(str4);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzcem.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                            if (zzeh == null && zzeh.zzbvl != null) {
                                com_google_android_gms_internal_zzcjz.zzbwb = zzeh.zzbvl;
                            } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            } else {
                                com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                        zzwz2 = zzwz();
                        try {
                            zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                        } catch (SQLiteException e) {
                            zzwz2.zzwF().zzyx().zze("Failed to remove unused event metadata. appId", zzcfl.zzdZ(str4), e);
                        }
                        zzwz().setTransactionSuccessful();
                        z4 = com_google_android_gms_internal_zzcjz.zzbvE.length <= 0;
                        zzwz().endTransaction();
                        return z4;
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
            }
            strArr = j2 != -1 ? new String[]{null, String.valueOf(j2)} : new String[]{null};
            str5 = j2 != -1 ? " and rowid <= ?" : "";
            cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str5).length() + 84).append("select metadata_fingerprint from raw_events where app_id = ?").append(str5).append(" order by rowid limit 1;").toString(), strArr);
            if (cursor.moveToFirst()) {
                str5 = cursor.getString(0);
                cursor.close();
                str6 = str5;
                cursor2 = cursor;
                str2 = null;
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                }
                if (obj2 != null) {
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
                z = false;
                com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                i = 0;
                i2 = 0;
                while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                    if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                        zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                        if (zzO) {
                            zzwB();
                        }
                        obj3 = null;
                        obj = null;
                        if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                            ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                        }
                        com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                        length2 = com_google_android_gms_internal_zzcjxArr2.length;
                        i3 = 0;
                        while (i3 < length2) {
                            com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                            if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                obj3 = 1;
                                obj2 = obj;
                            } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                obj2 = obj;
                            } else {
                                com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                obj2 = 1;
                            }
                            i3++;
                            obj = obj2;
                        }
                        zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                        com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                        com_google_android_gms_internal_zzcjx2 = new zzcjx();
                        com_google_android_gms_internal_zzcjx2.name = "_c";
                        com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                        com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                        if (obj == null) {
                            zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                            com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                            com_google_android_gms_internal_zzcjx2 = new zzcjx();
                            com_google_android_gms_internal_zzcjx2.name = "_r";
                            com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                            com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                            ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                        }
                        if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                            z = true;
                        } else {
                            com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                            i4 = 0;
                            while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                    i4++;
                                } else {
                                    obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                    if (i4 > 0) {
                                        System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                    }
                                    if (i4 < obj.length) {
                                        System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                    }
                                    com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                }
                            }
                        }
                        zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                        obj4 = null;
                        com_google_android_gms_internal_zzcjx3 = null;
                        com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                        length = com_google_android_gms_internal_zzcjxArr.length;
                        i5 = 0;
                        while (i5 < length) {
                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                            if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                obj = obj4;
                            } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                obj = obj4;
                            } else {
                                com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                i6 = 1;
                                com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                            }
                            i5++;
                            obj4 = obj;
                            com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                        }
                        if (obj4 == null) {
                        }
                        if (com_google_android_gms_internal_zzcjx3 == null) {
                            zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            z2 = z;
                            i4 = i + 1;
                            com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                            i7 = i4;
                            z3 = z2;
                        } else {
                            com_google_android_gms_internal_zzcjx3.name = "_err";
                            com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                            z2 = z;
                            i4 = i + 1;
                            com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                            i7 = i4;
                            z3 = z2;
                        }
                    } else {
                        zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                        if (!zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                        }
                        if (obj2 == null) {
                        }
                        i7 = i;
                        z3 = z;
                    }
                    i2++;
                    i = i7;
                    z = z3;
                }
                if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                    com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                }
                com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                for (zzcjw com_google_android_gms_internal_zzcjw22 : com_google_android_gms_internal_zzcjz.zzbvE) {
                    if (com_google_android_gms_internal_zzcjw22.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                        com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw22.zzbvx;
                    }
                    if (com_google_android_gms_internal_zzcjw22.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                        com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw22.zzbvx;
                    }
                }
                str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                zzdQ = zzwz().zzdQ(str4);
                if (zzdQ != null) {
                    zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                    zzwM = zzdQ.zzwM();
                    if (zzwM == 0) {
                    }
                    com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                    zzwL = zzdQ.zzwL();
                    if (zzwL != 0) {
                        zzwM = zzwL;
                    }
                    if (zzwM == 0) {
                    }
                    com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                    zzdQ.zzwV();
                    com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                    zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                    zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                    com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                    zzwz().zza(zzdQ);
                }
                if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                    zzcem.zzxE();
                    zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                    if (zzeh == null) {
                    }
                    if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                        zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                    } else {
                        com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                    }
                    zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                }
                zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                zzwz2 = zzwz();
                zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                zzwz().setTransactionSuccessful();
                if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
                }
                zzwz().endTransaction();
                return z4;
            }
            try {
                cursor2 = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id = ? and metadata_fingerprint = ?", new String[]{str2, str6}, null, null, "rowid", "2");
                if (cursor2.moveToFirst()) {
                    byte[] blob = cursor2.getBlob(0);
                    adg zzb = adg.zzb(blob, 0, blob.length);
                    zzcjz com_google_android_gms_internal_zzcjz2 = new zzcjz();
                    try {
                        com_google_android_gms_internal_zzcjz2.zza(zzb);
                        if (cursor2.moveToNext()) {
                            zzwz.zzwF().zzyz().zzj("Get multiple raw event metadata records, expected one. appId", zzcfl.zzdZ(str2));
                        }
                        cursor2.close();
                        com_google_android_gms_internal_zzcgl_zza.zzb(com_google_android_gms_internal_zzcjz2);
                        if (j2 != -1) {
                            str5 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?";
                            strArr = new String[]{str2, str6, String.valueOf(j2)};
                        } else {
                            str5 = "app_id = ? and metadata_fingerprint = ?";
                            strArr = new String[]{str2, str6};
                        }
                        cursor = writableDatabase.query("raw_events", new String[]{"rowid", "name", AppMeasurement.Param.TIMESTAMP, "data"}, str5, strArr, null, null, "rowid", null);
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    zzwL = cursor.getLong(0);
                                    byte[] blob2 = cursor.getBlob(3);
                                    adg zzb2 = adg.zzb(blob2, 0, blob2.length);
                                    zzcjw com_google_android_gms_internal_zzcjw3 = new zzcjw();
                                    try {
                                        com_google_android_gms_internal_zzcjw3.zza(zzb2);
                                        com_google_android_gms_internal_zzcjw3.name = cursor.getString(1);
                                        com_google_android_gms_internal_zzcjw3.zzbvx = Long.valueOf(cursor.getLong(2));
                                        if (!com_google_android_gms_internal_zzcgl_zza.zza(zzwL, com_google_android_gms_internal_zzcjw3)) {
                                            if (cursor != null) {
                                                cursor.close();
                                            }
                                            if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                                            }
                                            if (obj2 != null) {
                                                z = false;
                                                com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                                                com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                                                i = 0;
                                                i2 = 0;
                                                while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                                                    if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                                        zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                                        if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                                                        }
                                                        if (obj2 == null) {
                                                        }
                                                        i7 = i;
                                                        z3 = z;
                                                    } else {
                                                        zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                                        if (zzO) {
                                                            zzwB();
                                                        }
                                                        obj3 = null;
                                                        obj = null;
                                                        if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                                            ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                                        }
                                                        com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                                        length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                                        i3 = 0;
                                                        while (i3 < length2) {
                                                            com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                                            if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                                                com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                                                obj3 = 1;
                                                                obj2 = obj;
                                                            } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                                                com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                                                obj2 = 1;
                                                            } else {
                                                                obj2 = obj;
                                                            }
                                                            i3++;
                                                            obj = obj2;
                                                        }
                                                        zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                                        com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                                        com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                                        com_google_android_gms_internal_zzcjx2.name = "_c";
                                                        com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                                        com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                                        if (obj == null) {
                                                            zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                                            com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                                            com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                                            com_google_android_gms_internal_zzcjx2.name = "_r";
                                                            com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                                            com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                                            ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                                        }
                                                        if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                                            com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                                            i4 = 0;
                                                            while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                                                if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                                                    obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                                                    if (i4 > 0) {
                                                                        System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                                                    }
                                                                    if (i4 < obj.length) {
                                                                        System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                                                    }
                                                                    com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                                                } else {
                                                                    i4++;
                                                                }
                                                            }
                                                        } else {
                                                            z = true;
                                                        }
                                                        zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                                        com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                                        obj4 = null;
                                                        com_google_android_gms_internal_zzcjx3 = null;
                                                        com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                                        length = com_google_android_gms_internal_zzcjxArr.length;
                                                        i5 = 0;
                                                        while (i5 < length) {
                                                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                                            if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                                                obj = obj4;
                                                            } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                                                com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                                                i6 = 1;
                                                                com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                                            } else {
                                                                com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                                                obj = obj4;
                                                            }
                                                            i5++;
                                                            obj4 = obj;
                                                            com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                                        }
                                                        if (obj4 == null) {
                                                        }
                                                        if (com_google_android_gms_internal_zzcjx3 == null) {
                                                            com_google_android_gms_internal_zzcjx3.name = "_err";
                                                            com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                                            z2 = z;
                                                            i4 = i + 1;
                                                            com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                                            i7 = i4;
                                                            z3 = z2;
                                                        } else {
                                                            zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                                            z2 = z;
                                                            i4 = i + 1;
                                                            com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                                            i7 = i4;
                                                            z3 = z2;
                                                        }
                                                    }
                                                    i2++;
                                                    i = i7;
                                                    z = z3;
                                                }
                                                if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                                                    com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                                                }
                                                com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                                                com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                                                com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                                                for (zzcjw com_google_android_gms_internal_zzcjw222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                                                    if (com_google_android_gms_internal_zzcjw222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                                        com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw222.zzbvx;
                                                    }
                                                    if (com_google_android_gms_internal_zzcjw222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                                        com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw222.zzbvx;
                                                    }
                                                }
                                                str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                                                zzdQ = zzwz().zzdQ(str4);
                                                if (zzdQ != null) {
                                                    zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                                } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                                                    zzwM = zzdQ.zzwM();
                                                    if (zzwM == 0) {
                                                    }
                                                    com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                                    zzwL = zzdQ.zzwL();
                                                    if (zzwL != 0) {
                                                        zzwM = zzwL;
                                                    }
                                                    if (zzwM == 0) {
                                                    }
                                                    com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                                    zzdQ.zzwV();
                                                    com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                                                    zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                                                    zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                                                    com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                                                    zzwz().zza(zzdQ);
                                                }
                                                if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                                                    zzcem.zzxE();
                                                    zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                                                    if (zzeh == null) {
                                                    }
                                                    if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                                        com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                                                    } else {
                                                        zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                                    }
                                                    zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                                                }
                                                zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                                                zzwz2 = zzwz();
                                                zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                                                zzwz().setTransactionSuccessful();
                                                if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
                                                }
                                                zzwz().endTransaction();
                                                return z4;
                                            }
                                            zzwz().setTransactionSuccessful();
                                            zzwz().endTransaction();
                                            return false;
                                        }
                                    } catch (IOException e2) {
                                        zzwz.zzwF().zzyx().zze("Data loss. Failed to merge raw event. appId", zzcfl.zzdZ(str2), e2);
                                    }
                                } catch (SQLiteException e3) {
                                    obj2 = e3;
                                    str3 = str2;
                                }
                            } while (cursor.moveToNext());
                            if (cursor != null) {
                                cursor.close();
                            }
                            if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                            }
                            if (obj2 != null) {
                                zzwz().setTransactionSuccessful();
                                zzwz().endTransaction();
                                return false;
                            }
                            z = false;
                            com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                            com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                            i = 0;
                            i2 = 0;
                            while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                                if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                    zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                    if (zzO) {
                                        zzwB();
                                    }
                                    obj3 = null;
                                    obj = null;
                                    if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                    }
                                    com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                    length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                    i3 = 0;
                                    while (i3 < length2) {
                                        com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                            com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                            obj3 = 1;
                                            obj2 = obj;
                                        } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                            obj2 = obj;
                                        } else {
                                            com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                            obj2 = 1;
                                        }
                                        i3++;
                                        obj = obj2;
                                    }
                                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_c";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                    if (obj == null) {
                                        zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                        com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                        com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                        com_google_android_gms_internal_zzcjx2.name = "_r";
                                        com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                        com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                    }
                                    if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                        z = true;
                                    } else {
                                        com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                        i4 = 0;
                                        while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                            if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                                i4++;
                                            } else {
                                                obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                                if (i4 > 0) {
                                                    System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                                }
                                                if (i4 < obj.length) {
                                                    System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                                }
                                                com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                            }
                                        }
                                    }
                                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    obj4 = null;
                                    com_google_android_gms_internal_zzcjx3 = null;
                                    com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                    length = com_google_android_gms_internal_zzcjxArr.length;
                                    i5 = 0;
                                    while (i5 < length) {
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                        if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                            obj = obj4;
                                        } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                            obj = obj4;
                                        } else {
                                            com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                            i6 = 1;
                                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                        }
                                        i5++;
                                        obj4 = obj;
                                        com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                    }
                                    if (obj4 == null) {
                                    }
                                    if (com_google_android_gms_internal_zzcjx3 == null) {
                                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                        z2 = z;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                        i7 = i4;
                                        z3 = z2;
                                    } else {
                                        com_google_android_gms_internal_zzcjx3.name = "_err";
                                        com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                        z2 = z;
                                        i4 = i + 1;
                                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                        i7 = i4;
                                        z3 = z2;
                                    }
                                } else {
                                    zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                                    }
                                    if (obj2 == null) {
                                    }
                                    i7 = i;
                                    z3 = z;
                                }
                                i2++;
                                i = i7;
                                z = z3;
                            }
                            if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                                com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                            }
                            com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                            com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                            com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                            for (zzcjw com_google_android_gms_internal_zzcjw2222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                                if (com_google_android_gms_internal_zzcjw2222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                    com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw2222.zzbvx;
                                }
                                if (com_google_android_gms_internal_zzcjw2222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                    com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw2222.zzbvx;
                                }
                            }
                            str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                            zzdQ = zzwz().zzdQ(str4);
                            if (zzdQ != null) {
                                zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                                zzwM = zzdQ.zzwM();
                                if (zzwM == 0) {
                                }
                                com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                zzwL = zzdQ.zzwL();
                                if (zzwL != 0) {
                                    zzwM = zzwL;
                                }
                                if (zzwM == 0) {
                                }
                                com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                                zzdQ.zzwV();
                                com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                                zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                                zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                                com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                                zzwz().zza(zzdQ);
                            }
                            if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                                zzcem.zzxE();
                                zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                                if (zzeh == null) {
                                }
                                if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                    zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                } else {
                                    com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                                }
                                zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                            }
                            zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                            zzwz2 = zzwz();
                            zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                            zzwz().setTransactionSuccessful();
                            if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
                            }
                            zzwz().endTransaction();
                            return z4;
                        }
                        zzwz.zzwF().zzyz().zzj("Raw event data disappeared while in transaction. appId", zzcfl.zzdZ(str2));
                        if (cursor != null) {
                            cursor.close();
                        }
                        if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                        }
                        if (obj2 != null) {
                            zzwz().setTransactionSuccessful();
                            zzwz().endTransaction();
                            return false;
                        }
                        z = false;
                        com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj3 = null;
                                obj = null;
                                if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                }
                                com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj3 = 1;
                                        obj2 = obj;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        obj2 = obj;
                                    } else {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                    }
                                    i3++;
                                    obj = obj2;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                com_google_android_gms_internal_zzcjx2.name = "_c";
                                com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                if (obj == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_r";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                    z = true;
                                } else {
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                            i4++;
                                        } else {
                                            obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                            }
                                            if (i4 < obj.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                        }
                                    }
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjx3 = null;
                                com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                length = com_google_android_gms_internal_zzcjxArr.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        obj = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                        obj = obj4;
                                    } else {
                                        com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                    }
                                    i5++;
                                    obj4 = obj;
                                    com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjx3 == null) {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                } else {
                                    com_google_android_gms_internal_zzcjx3.name = "_err";
                                    com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                }
                            } else {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                                }
                                if (obj2 == null) {
                                }
                                i7 = i;
                                z3 = z;
                            }
                            i2++;
                            i = i7;
                            z = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                        com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjw com_google_android_gms_internal_zzcjw22222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                            if (com_google_android_gms_internal_zzcjw22222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw22222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjw22222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw22222.zzbvx;
                            }
                        }
                        str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                        zzdQ = zzwz().zzdQ(str4);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzcem.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            } else {
                                com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                        zzwz2 = zzwz();
                        zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
                        }
                        zzwz().endTransaction();
                        return z4;
                    } catch (IOException e22) {
                        zzwz.zzwF().zzyx().zze("Data loss. Failed to merge raw event metadata. appId", zzcfl.zzdZ(str2), e22);
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                    } catch (Throwable th2) {
                        zzwz().endTransaction();
                    }
                } else {
                    zzwz.zzwF().zzyx().zzj("Raw event metadata record is missing. appId", zzcfl.zzdZ(str2));
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                    }
                    if (obj2 != null) {
                        z = false;
                        com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                                }
                                if (obj2 == null) {
                                }
                                i7 = i;
                                z3 = z;
                            } else {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj3 = null;
                                obj = null;
                                if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                }
                                com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj3 = 1;
                                        obj2 = obj;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                    } else {
                                        obj2 = obj;
                                    }
                                    i3++;
                                    obj = obj2;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                com_google_android_gms_internal_zzcjx2.name = "_c";
                                com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                if (obj == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_r";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                            obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                            }
                                            if (i4 < obj.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                        } else {
                                            i4++;
                                        }
                                    }
                                } else {
                                    z = true;
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjx3 = null;
                                com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                length = com_google_android_gms_internal_zzcjxArr.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        obj = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                    } else {
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                        obj = obj4;
                                    }
                                    i5++;
                                    obj4 = obj;
                                    com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjx3 == null) {
                                    com_google_android_gms_internal_zzcjx3.name = "_err";
                                    com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                } else {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                }
                            }
                            i2++;
                            i = i7;
                            z = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                        com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjw com_google_android_gms_internal_zzcjw222222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                            if (com_google_android_gms_internal_zzcjw222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw222222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjw222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw222222.zzbvx;
                            }
                        }
                        str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                        zzdQ = zzwz().zzdQ(str4);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzcem.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                            } else {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                        zzwz2 = zzwz();
                        zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
                        }
                        zzwz().endTransaction();
                        return z4;
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return false;
                }
            } catch (SQLiteException e4) {
                obj2 = e4;
                cursor = cursor2;
                str3 = str2;
                try {
                    zzwz.zzwF().zzyx().zze("Data loss. Error selecting raw event. appId", zzcfl.zzdZ(str3), obj2);
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
                    }
                    if (obj2 != null) {
                        z = false;
                        com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
                        com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
                        i = 0;
                        i2 = 0;
                        while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                                zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                                }
                                if (obj2 == null) {
                                }
                                i7 = i;
                                z3 = z;
                            } else {
                                zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                                if (zzO) {
                                    zzwB();
                                }
                                obj3 = null;
                                obj = null;
                                if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                                }
                                com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                                length2 = com_google_android_gms_internal_zzcjxArr2.length;
                                i3 = 0;
                                while (i3 < length2) {
                                    com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj3 = 1;
                                        obj2 = obj;
                                    } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                                        com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                                        obj2 = 1;
                                    } else {
                                        obj2 = obj;
                                    }
                                    i3++;
                                    obj = obj2;
                                }
                                zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                com_google_android_gms_internal_zzcjx2.name = "_c";
                                com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                if (obj == null) {
                                    zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                                    com_google_android_gms_internal_zzcjx2.name = "_r";
                                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                                }
                                if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i4 = 0;
                                    while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                                        if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                            obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                            if (i4 > 0) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                            }
                                            if (i4 < obj.length) {
                                                System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                            }
                                            com_google_android_gms_internal_zzcjw.zzbvw = obj;
                                        } else {
                                            i4++;
                                        }
                                    }
                                } else {
                                    z = true;
                                }
                                zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                obj4 = null;
                                com_google_android_gms_internal_zzcjx3 = null;
                                com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                                length = com_google_android_gms_internal_zzcjxArr.length;
                                i5 = 0;
                                while (i5 < length) {
                                    com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                                    if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        obj = obj4;
                                    } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                                        com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                                        i6 = 1;
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                                    } else {
                                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                                        obj = obj4;
                                    }
                                    i5++;
                                    obj4 = obj;
                                    com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                                }
                                if (obj4 == null) {
                                }
                                if (com_google_android_gms_internal_zzcjx3 == null) {
                                    com_google_android_gms_internal_zzcjx3.name = "_err";
                                    com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                } else {
                                    zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                                    z2 = z;
                                    i4 = i + 1;
                                    com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                                    i7 = i4;
                                    z3 = z2;
                                }
                            }
                            i2++;
                            i = i7;
                            z = z3;
                        }
                        if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                            com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
                        }
                        com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                        com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
                        com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
                        for (zzcjw com_google_android_gms_internal_zzcjw2222222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                            if (com_google_android_gms_internal_zzcjw2222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw2222222.zzbvx;
                            }
                            if (com_google_android_gms_internal_zzcjw2222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                                com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw2222222.zzbvx;
                            }
                        }
                        str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
                        zzdQ = zzwz().zzdQ(str4);
                        if (zzdQ != null) {
                            zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzwM = zzdQ.zzwM();
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzwL = zzdQ.zzwL();
                            if (zzwL != 0) {
                                zzwM = zzwL;
                            }
                            if (zzwM == 0) {
                            }
                            com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                            zzdQ.zzwV();
                            com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                            zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                            zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                            com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                            zzwz().zza(zzdQ);
                        }
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                            zzcem.zzxE();
                            zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                            if (zzeh == null) {
                            }
                            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                                com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                            } else {
                                zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                            }
                            zzwz().zza(com_google_android_gms_internal_zzcjz, z);
                        }
                        zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
                        zzwz2 = zzwz();
                        zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
                        zzwz().setTransactionSuccessful();
                        if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
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
            obj2 = e5;
            zzwz.zzwF().zzyx().zze("Data loss. Error selecting raw event. appId", zzcfl.zzdZ(str3), obj2);
            if (cursor != null) {
                cursor.close();
            }
            if (com_google_android_gms_internal_zzcgl_zza.zztH != null) {
            }
            if (obj2 != null) {
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return false;
            }
            z = false;
            com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcgl_zza.zzbsZ;
            com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[com_google_android_gms_internal_zzcgl_zza.zztH.size()];
            i = 0;
            i2 = 0;
            while (i2 < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                if (zzwC().zzN(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name)) {
                    zzO = zzwC().zzO(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name);
                    if (zzO) {
                        zzwB();
                    }
                    obj3 = null;
                    obj = null;
                    if (((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw == null) {
                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = new zzcjx[0];
                    }
                    com_google_android_gms_internal_zzcjxArr2 = ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw;
                    length2 = com_google_android_gms_internal_zzcjxArr2.length;
                    i3 = 0;
                    while (i3 < length2) {
                        com_google_android_gms_internal_zzcjx = com_google_android_gms_internal_zzcjxArr2[i3];
                        if (!"_c".equals(com_google_android_gms_internal_zzcjx.name)) {
                            com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                            obj3 = 1;
                            obj2 = obj;
                        } else if ("_r".equals(com_google_android_gms_internal_zzcjx.name)) {
                            obj2 = obj;
                        } else {
                            com_google_android_gms_internal_zzcjx.zzbvA = Long.valueOf(1);
                            obj2 = 1;
                        }
                        i3++;
                        obj = obj2;
                    }
                    zzwF().zzyD().zzj("Marking event as conversion", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                    com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                    com_google_android_gms_internal_zzcjx2 = new zzcjx();
                    com_google_android_gms_internal_zzcjx2.name = "_c";
                    com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                    com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                    ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                    if (obj == null) {
                        zzwF().zzyD().zzj("Marking event as real-time", zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                        com_google_android_gms_internal_zzcjxArr3 = (zzcjx[]) Arrays.copyOf(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw, ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw.length + 1);
                        com_google_android_gms_internal_zzcjx2 = new zzcjx();
                        com_google_android_gms_internal_zzcjx2.name = "_r";
                        com_google_android_gms_internal_zzcjx2.zzbvA = Long.valueOf(1);
                        com_google_android_gms_internal_zzcjxArr3[com_google_android_gms_internal_zzcjxArr3.length - 1] = com_google_android_gms_internal_zzcjx2;
                        ((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).zzbvw = com_google_android_gms_internal_zzcjxArr3;
                    }
                    if (zzwz().zza(zzyZ(), com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, false, false, false, false, true).zzbpy <= ((long) this.zzbsn.zzdM(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH))) {
                        z = true;
                    } else {
                        com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                        i4 = 0;
                        while (i4 < com_google_android_gms_internal_zzcjw.zzbvw.length) {
                            if ("_r".equals(com_google_android_gms_internal_zzcjw.zzbvw[i4].name)) {
                                i4++;
                            } else {
                                obj = new zzcjx[(com_google_android_gms_internal_zzcjw.zzbvw.length - 1)];
                                if (i4 > 0) {
                                    System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, 0, obj, 0, i4);
                                }
                                if (i4 < obj.length) {
                                    System.arraycopy(com_google_android_gms_internal_zzcjw.zzbvw, i4 + 1, obj, i4, obj.length - i4);
                                }
                                com_google_android_gms_internal_zzcjw.zzbvw = obj;
                            }
                        }
                    }
                    zzwF().zzyz().zzj("Too many conversions. Not logging as conversion. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                    com_google_android_gms_internal_zzcjw = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                    obj4 = null;
                    com_google_android_gms_internal_zzcjx3 = null;
                    com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                    length = com_google_android_gms_internal_zzcjxArr.length;
                    i5 = 0;
                    while (i5 < length) {
                        com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjxArr[i5];
                        if (!"_c".equals(com_google_android_gms_internal_zzcjx2.name)) {
                            obj = obj4;
                        } else if ("_err".equals(com_google_android_gms_internal_zzcjx2.name)) {
                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx3;
                            obj = obj4;
                        } else {
                            com_google_android_gms_internal_zzcjx4 = com_google_android_gms_internal_zzcjx3;
                            i6 = 1;
                            com_google_android_gms_internal_zzcjx2 = com_google_android_gms_internal_zzcjx4;
                        }
                        i5++;
                        obj4 = obj;
                        com_google_android_gms_internal_zzcjx3 = com_google_android_gms_internal_zzcjx2;
                    }
                    if (obj4 == null) {
                    }
                    if (com_google_android_gms_internal_zzcjx3 == null) {
                        zzwF().zzyx().zzj("Did not find conversion parameter. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                        z2 = z;
                        i4 = i + 1;
                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                        i7 = i4;
                        z3 = z2;
                    } else {
                        com_google_android_gms_internal_zzcjx3.name = "_err";
                        com_google_android_gms_internal_zzcjx3.zzbvA = Long.valueOf(10);
                        z2 = z;
                        i4 = i + 1;
                        com_google_android_gms_internal_zzcjz.zzbvE[i] = (zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2);
                        i7 = i4;
                        z3 = z2;
                    }
                } else {
                    zzwF().zzyz().zze("Dropping blacklisted raw event. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH), zzwA().zzdW(((zzcjw) com_google_android_gms_internal_zzcgl_zza.zztH.get(i2)).name));
                    if (zzwB().zzeA(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH)) {
                    }
                    if (obj2 == null) {
                    }
                    i7 = i;
                    z3 = z;
                }
                i2++;
                i = i7;
                z = z3;
            }
            if (i < com_google_android_gms_internal_zzcgl_zza.zztH.size()) {
                com_google_android_gms_internal_zzcjz.zzbvE = (zzcjw[]) Arrays.copyOf(com_google_android_gms_internal_zzcjz.zzbvE, i);
            }
            com_google_android_gms_internal_zzcjz.zzbvX = zza(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH, com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
            com_google_android_gms_internal_zzcjz.zzbvH = Long.valueOf(Long.MAX_VALUE);
            com_google_android_gms_internal_zzcjz.zzbvI = Long.valueOf(Long.MIN_VALUE);
            for (zzcjw com_google_android_gms_internal_zzcjw22222222 : com_google_android_gms_internal_zzcjz.zzbvE) {
                if (com_google_android_gms_internal_zzcjw22222222.zzbvx.longValue() < com_google_android_gms_internal_zzcjz.zzbvH.longValue()) {
                    com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw22222222.zzbvx;
                }
                if (com_google_android_gms_internal_zzcjw22222222.zzbvx.longValue() <= com_google_android_gms_internal_zzcjz.zzbvI.longValue()) {
                    com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw22222222.zzbvx;
                }
            }
            str4 = com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH;
            zzdQ = zzwz().zzdQ(str4);
            if (zzdQ != null) {
                zzwF().zzyx().zzj("Bundling raw events w/o app info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
            } else if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                zzwM = zzdQ.zzwM();
                if (zzwM == 0) {
                }
                com_google_android_gms_internal_zzcjz.zzbvK = zzwM == 0 ? Long.valueOf(zzwM) : null;
                zzwL = zzdQ.zzwL();
                if (zzwL != 0) {
                    zzwM = zzwL;
                }
                if (zzwM == 0) {
                }
                com_google_android_gms_internal_zzcjz.zzbvJ = zzwM == 0 ? Long.valueOf(zzwM) : null;
                zzdQ.zzwV();
                com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                com_google_android_gms_internal_zzcjz.zzboU = zzdQ.zzxd();
                zzwz().zza(zzdQ);
            }
            if (com_google_android_gms_internal_zzcjz.zzbvE.length > 0) {
                zzcem.zzxE();
                zzeh = zzwC().zzeh(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH);
                if (zzeh == null) {
                }
                if (TextUtils.isEmpty(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzboQ)) {
                    zzwF().zzyz().zzj("Did not find measurement config or missing version info. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzcgl_zza.zzbsZ.zzaH));
                } else {
                    com_google_android_gms_internal_zzcjz.zzbwb = Long.valueOf(-1);
                }
                zzwz().zza(com_google_android_gms_internal_zzcjz, z);
            }
            zzwz().zzG(com_google_android_gms_internal_zzcgl_zza.zzbta);
            zzwz2 = zzwz();
            zzwz2.getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str4, str4});
            zzwz().setTransactionSuccessful();
            if (com_google_android_gms_internal_zzcjz.zzbvE.length <= 0) {
            }
            zzwz().endTransaction();
            return z4;
        }
    }

    static void zzwo() {
        zzcem.zzxE();
        throw new IllegalStateException("Unexpected call on client side");
    }

    private final zzcfu zzyV() {
        if (this.zzbsF != null) {
            return this.zzbsF;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    private final zzcjg zzyW() {
        zza(this.zzbsG);
        return this.zzbsG;
    }

    @WorkerThread
    private final boolean zzyX() {
        zzwE().zzjC();
        try {
            this.zzbsN = new RandomAccessFile(new File(this.mContext.getFilesDir(), zzcem.zzxC()), "rw").getChannel();
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
        long currentTimeMillis = this.zzvw.currentTimeMillis();
        zzcfw zzwG = zzwG();
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
                abs = 3600000 - Math.abs(this.zzvw.elapsedRealtime() - this.zzbsT);
                if (abs > 0) {
                    zzwF().zzyD().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    zzyV().unregister();
                    zzyW().cancel();
                    return;
                }
                this.zzbsT = 0;
            }
            if (zzyP() && zzzb()) {
                long currentTimeMillis = this.zzvw.currentTimeMillis();
                long zzxX = zzcem.zzxX();
                Object obj = (zzwz().zzyi() || zzwz().zzyd()) ? 1 : null;
                if (obj != null) {
                    CharSequence zzya = this.zzbsn.zzya();
                    abs = (TextUtils.isEmpty(zzya) || ".none.".equals(zzya)) ? zzcem.zzxS() : zzcem.zzxT();
                } else {
                    abs = zzcem.zzxR();
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
                        for (int i = 0; i < zzcem.zzxZ(); i++) {
                            currentTimeMillis += ((long) (1 << i)) * zzcem.zzxY();
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
                    long zzxQ = zzcem.zzxQ();
                    if (!zzwB().zzf(currentTimeMillis, zzxQ)) {
                        abs = Math.max(abs, currentTimeMillis + zzxQ);
                    }
                    zzyV().unregister();
                    abs -= this.zzvw.currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzcem.zzxU();
                        zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
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
        } else if (!zzcem.zzqB()) {
            z = true;
        }
        return zzwG().zzal(z);
    }

    @WorkerThread
    protected final void start() {
        zzwE().zzjC();
        zzwz().zzye();
        if (zzwG().zzbrk.get() == 0) {
            zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
        }
        if (Long.valueOf(zzwG().zzbrp.get()).longValue() == 0) {
            zzwF().zzyD().zzj("Persisting first open", Long.valueOf(this.zzbsX));
            zzwG().zzbrp.set(this.zzbsX);
        }
        if (zzyP()) {
            zzcem.zzxE();
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
            zzcem.zzxE();
            if (!TextUtils.isEmpty(zzwu().getGmpAppId())) {
                zzchl zzwt = zzwt();
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
            zzcem.zzxE();
            if (!zzbha.zzaP(this.mContext).zzsl()) {
                if (!zzcgc.zzj(this.mContext, false)) {
                    zzwF().zzyx().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzciw.zzk(this.mContext, false)) {
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
                zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
                zzwG().zzbrl.set(0);
                zzzc();
                zzwF().zzyD().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzwz().beginTransaction();
                zzcen zzwz;
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
                this.zzbsT = this.zzvw.elapsedRealtime();
                zzwF().zzyD().zzj("Disable upload, time", Long.valueOf(this.zzbsT));
            }
        } else {
            zzwF().zzyD().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzwG().zzbrl.set(this.zzvw.currentTimeMillis());
            boolean z = i == 503 || i == 429;
            if (z) {
                zzwG().zzbrm.set(this.zzvw.currentTimeMillis());
            }
            zzzc();
        }
        this.zzbsV = false;
        zzzg();
    }

    @WorkerThread
    public final byte[] zza(@NonNull zzcez com_google_android_gms_internal_zzcez, @Size(min = 1) String str) {
        zzkD();
        zzwE().zzjC();
        zzwo();
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        zzbo.zzcF(str);
        zzcjy com_google_android_gms_internal_zzcjy = new zzcjy();
        zzwz().beginTransaction();
        try {
            zzceg zzdQ = zzwz().zzdQ(str);
            byte[] bArr;
            if (zzdQ == null) {
                zzwF().zzyC().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzdQ.zzwR()) {
                long j;
                zzcjz com_google_android_gms_internal_zzcjz = new zzcjz();
                com_google_android_gms_internal_zzcjy.zzbvB = new zzcjz[]{com_google_android_gms_internal_zzcjz};
                com_google_android_gms_internal_zzcjz.zzbvD = Integer.valueOf(1);
                com_google_android_gms_internal_zzcjz.zzbvL = "android";
                com_google_android_gms_internal_zzcjz.zzaH = zzdQ.zzhl();
                com_google_android_gms_internal_zzcjz.zzboR = zzdQ.zzwO();
                com_google_android_gms_internal_zzcjz.zzbgW = zzdQ.zzjH();
                long zzwN = zzdQ.zzwN();
                com_google_android_gms_internal_zzcjz.zzbvY = zzwN == -2147483648L ? null : Integer.valueOf((int) zzwN);
                com_google_android_gms_internal_zzcjz.zzbvP = Long.valueOf(zzdQ.zzwP());
                com_google_android_gms_internal_zzcjz.zzboQ = zzdQ.getGmpAppId();
                com_google_android_gms_internal_zzcjz.zzbvU = Long.valueOf(zzdQ.zzwQ());
                if (isEnabled() && zzcem.zzyb() && this.zzbsn.zzdO(com_google_android_gms_internal_zzcjz.zzaH)) {
                    zzwu();
                    com_google_android_gms_internal_zzcjz.zzbwd = null;
                }
                Pair zzeb = zzwG().zzeb(zzdQ.zzhl());
                if (!(zzeb == null || TextUtils.isEmpty((CharSequence) zzeb.first))) {
                    com_google_android_gms_internal_zzcjz.zzbvR = (String) zzeb.first;
                    com_google_android_gms_internal_zzcjz.zzbvS = (Boolean) zzeb.second;
                }
                zzwv().zzkD();
                com_google_android_gms_internal_zzcjz.zzbvM = Build.MODEL;
                zzwv().zzkD();
                com_google_android_gms_internal_zzcjz.zzaY = VERSION.RELEASE;
                com_google_android_gms_internal_zzcjz.zzbvO = Integer.valueOf((int) zzwv().zzyq());
                com_google_android_gms_internal_zzcjz.zzbvN = zzwv().zzyr();
                com_google_android_gms_internal_zzcjz.zzbvT = zzdQ.getAppInstanceId();
                com_google_android_gms_internal_zzcjz.zzboY = zzdQ.zzwK();
                List zzdP = zzwz().zzdP(zzdQ.zzhl());
                com_google_android_gms_internal_zzcjz.zzbvF = new zzckb[zzdP.size()];
                for (int i = 0; i < zzdP.size(); i++) {
                    zzckb com_google_android_gms_internal_zzckb = new zzckb();
                    com_google_android_gms_internal_zzcjz.zzbvF[i] = com_google_android_gms_internal_zzckb;
                    com_google_android_gms_internal_zzckb.name = ((zzcjk) zzdP.get(i)).mName;
                    com_google_android_gms_internal_zzckb.zzbwh = Long.valueOf(((zzcjk) zzdP.get(i)).zzbuC);
                    zzwB().zza(com_google_android_gms_internal_zzckb, ((zzcjk) zzdP.get(i)).mValue);
                }
                Bundle zzyt = com_google_android_gms_internal_zzcez.zzbpM.zzyt();
                if ("_iap".equals(com_google_android_gms_internal_zzcez.name)) {
                    zzyt.putLong("_c", 1);
                    zzwF().zzyC().log("Marking in-app purchase as real-time");
                    zzyt.putLong("_r", 1);
                }
                zzyt.putString("_o", com_google_android_gms_internal_zzcez.zzbpc);
                if (zzwB().zzey(com_google_android_gms_internal_zzcjz.zzaH)) {
                    zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                    zzwB().zza(zzyt, "_r", Long.valueOf(1));
                }
                zzcev zzE = zzwz().zzE(str, com_google_android_gms_internal_zzcez.name);
                if (zzE == null) {
                    zzwz().zza(new zzcev(str, com_google_android_gms_internal_zzcez.name, 1, 0, com_google_android_gms_internal_zzcez.zzbpN));
                    j = 0;
                } else {
                    j = zzE.zzbpI;
                    zzwz().zza(zzE.zzab(com_google_android_gms_internal_zzcez.zzbpN).zzys());
                }
                zzceu com_google_android_gms_internal_zzceu = new zzceu(this, com_google_android_gms_internal_zzcez.zzbpc, str, com_google_android_gms_internal_zzcez.name, com_google_android_gms_internal_zzcez.zzbpN, j, zzyt);
                zzcjw com_google_android_gms_internal_zzcjw = new zzcjw();
                com_google_android_gms_internal_zzcjz.zzbvE = new zzcjw[]{com_google_android_gms_internal_zzcjw};
                com_google_android_gms_internal_zzcjw.zzbvx = Long.valueOf(com_google_android_gms_internal_zzceu.zzayS);
                com_google_android_gms_internal_zzcjw.name = com_google_android_gms_internal_zzceu.mName;
                com_google_android_gms_internal_zzcjw.zzbvy = Long.valueOf(com_google_android_gms_internal_zzceu.zzbpE);
                com_google_android_gms_internal_zzcjw.zzbvw = new zzcjx[com_google_android_gms_internal_zzceu.zzbpF.size()];
                Iterator it = com_google_android_gms_internal_zzceu.zzbpF.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzcjx com_google_android_gms_internal_zzcjx = new zzcjx();
                    int i3 = i2 + 1;
                    com_google_android_gms_internal_zzcjw.zzbvw[i2] = com_google_android_gms_internal_zzcjx;
                    com_google_android_gms_internal_zzcjx.name = str2;
                    zzwB().zza(com_google_android_gms_internal_zzcjx, com_google_android_gms_internal_zzceu.zzbpF.get(str2));
                    i2 = i3;
                }
                com_google_android_gms_internal_zzcjz.zzbvX = zza(zzdQ.zzhl(), com_google_android_gms_internal_zzcjz.zzbvF, com_google_android_gms_internal_zzcjz.zzbvE);
                com_google_android_gms_internal_zzcjz.zzbvH = com_google_android_gms_internal_zzcjw.zzbvx;
                com_google_android_gms_internal_zzcjz.zzbvI = com_google_android_gms_internal_zzcjw.zzbvx;
                zzwN = zzdQ.zzwM();
                com_google_android_gms_internal_zzcjz.zzbvK = zzwN != 0 ? Long.valueOf(zzwN) : null;
                long zzwL = zzdQ.zzwL();
                if (zzwL != 0) {
                    zzwN = zzwL;
                }
                com_google_android_gms_internal_zzcjz.zzbvJ = zzwN != 0 ? Long.valueOf(zzwN) : null;
                zzdQ.zzwV();
                com_google_android_gms_internal_zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                com_google_android_gms_internal_zzcjz.zzbvQ = Long.valueOf(zzcem.zzwP());
                com_google_android_gms_internal_zzcjz.zzbvG = Long.valueOf(this.zzvw.currentTimeMillis());
                com_google_android_gms_internal_zzcjz.zzbvW = Boolean.TRUE;
                zzdQ.zzL(com_google_android_gms_internal_zzcjz.zzbvH.longValue());
                zzdQ.zzM(com_google_android_gms_internal_zzcjz.zzbvI.longValue());
                zzwz().zza(zzdQ);
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                try {
                    bArr = new byte[com_google_android_gms_internal_zzcjy.zzLV()];
                    adh zzc = adh.zzc(bArr, 0, bArr.length);
                    com_google_android_gms_internal_zzcjy.zza(zzc);
                    zzc.zzLM();
                    return zzwB().zzl(bArr);
                } catch (IOException e) {
                    zzwF().zzyx().zze("Data loss. Failed to bundle and serialize. appId", zzcfl.zzdZ(str), e);
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
    final void zzb(zzcek com_google_android_gms_internal_zzcek, zzceh com_google_android_gms_internal_zzceh) {
        boolean z = true;
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        zzbo.zzcF(com_google_android_gms_internal_zzcek.packageName);
        zzbo.zzu(com_google_android_gms_internal_zzcek.zzbpc);
        zzbo.zzu(com_google_android_gms_internal_zzcek.zzbpd);
        zzbo.zzcF(com_google_android_gms_internal_zzcek.zzbpd.name);
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            if (com_google_android_gms_internal_zzceh.zzboV) {
                zzcek com_google_android_gms_internal_zzcek2 = new zzcek(com_google_android_gms_internal_zzcek);
                com_google_android_gms_internal_zzcek2.zzbpf = false;
                zzwz().beginTransaction();
                try {
                    zzcek zzH = zzwz().zzH(com_google_android_gms_internal_zzcek2.packageName, com_google_android_gms_internal_zzcek2.zzbpd.name);
                    if (!(zzH == null || zzH.zzbpc.equals(com_google_android_gms_internal_zzcek2.zzbpc))) {
                        zzwF().zzyz().zzd("Updating a conditional user property with different origin. name, origin, origin (from DB)", zzwA().zzdY(com_google_android_gms_internal_zzcek2.zzbpd.name), com_google_android_gms_internal_zzcek2.zzbpc, zzH.zzbpc);
                    }
                    if (zzH != null && zzH.zzbpf) {
                        com_google_android_gms_internal_zzcek2.zzbpc = zzH.zzbpc;
                        com_google_android_gms_internal_zzcek2.zzbpe = zzH.zzbpe;
                        com_google_android_gms_internal_zzcek2.zzbpi = zzH.zzbpi;
                        com_google_android_gms_internal_zzcek2.zzbpg = zzH.zzbpg;
                        com_google_android_gms_internal_zzcek2.zzbpj = zzH.zzbpj;
                        com_google_android_gms_internal_zzcek2.zzbpf = zzH.zzbpf;
                        com_google_android_gms_internal_zzcek2.zzbpd = new zzcji(com_google_android_gms_internal_zzcek2.zzbpd.name, zzH.zzbpd.zzbuy, com_google_android_gms_internal_zzcek2.zzbpd.getValue(), zzH.zzbpd.zzbpc);
                        z = false;
                    } else if (TextUtils.isEmpty(com_google_android_gms_internal_zzcek2.zzbpg)) {
                        com_google_android_gms_internal_zzcek2.zzbpd = new zzcji(com_google_android_gms_internal_zzcek2.zzbpd.name, com_google_android_gms_internal_zzcek2.zzbpe, com_google_android_gms_internal_zzcek2.zzbpd.getValue(), com_google_android_gms_internal_zzcek2.zzbpd.zzbpc);
                        com_google_android_gms_internal_zzcek2.zzbpf = true;
                    } else {
                        z = false;
                    }
                    if (com_google_android_gms_internal_zzcek2.zzbpf) {
                        zzcji com_google_android_gms_internal_zzcji = com_google_android_gms_internal_zzcek2.zzbpd;
                        zzcjk com_google_android_gms_internal_zzcjk = new zzcjk(com_google_android_gms_internal_zzcek2.packageName, com_google_android_gms_internal_zzcek2.zzbpc, com_google_android_gms_internal_zzcji.name, com_google_android_gms_internal_zzcji.zzbuy, com_google_android_gms_internal_zzcji.getValue());
                        if (zzwz().zza(com_google_android_gms_internal_zzcjk)) {
                            zzwF().zzyC().zzd("User property updated immediately", com_google_android_gms_internal_zzcek2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zzd("(2)Too many active user properties, ignoring", zzcfl.zzdZ(com_google_android_gms_internal_zzcek2.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                        }
                        if (z && com_google_android_gms_internal_zzcek2.zzbpj != null) {
                            zzc(new zzcez(com_google_android_gms_internal_zzcek2.zzbpj, com_google_android_gms_internal_zzcek2.zzbpe), com_google_android_gms_internal_zzceh);
                        }
                    }
                    if (zzwz().zza(com_google_android_gms_internal_zzcek2)) {
                        zzwF().zzyC().zzd("Conditional property added", com_google_android_gms_internal_zzcek2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcek2.zzbpd.name), com_google_android_gms_internal_zzcek2.zzbpd.getValue());
                    } else {
                        zzwF().zzyx().zzd("Too many conditional properties, ignoring", zzcfl.zzdZ(com_google_android_gms_internal_zzcek2.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcek2.zzbpd.name), com_google_android_gms_internal_zzcek2.zzbpd.getValue());
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceh);
            }
        }
    }

    @WorkerThread
    final void zzb(zzcez com_google_android_gms_internal_zzcez, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzbo.zzcF(com_google_android_gms_internal_zzceh.packageName);
        zzwE().zzjC();
        zzkD();
        String str = com_google_android_gms_internal_zzceh.packageName;
        long j = com_google_android_gms_internal_zzcez.zzbpN;
        zzwB();
        if (!zzcjl.zzd(com_google_android_gms_internal_zzcez, com_google_android_gms_internal_zzceh)) {
            return;
        }
        if (com_google_android_gms_internal_zzceh.zzboV) {
            zzwz().beginTransaction();
            try {
                List emptyList;
                Object obj;
                zzcen zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying timed out conditional properties", zzcfl.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[]{str, String.valueOf(j)});
                }
                for (zzcek com_google_android_gms_internal_zzcek : r2) {
                    if (com_google_android_gms_internal_zzcek != null) {
                        zzwF().zzyC().zzd("User property timed out", com_google_android_gms_internal_zzcek.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcek.zzbpd.name), com_google_android_gms_internal_zzcek.zzbpd.getValue());
                        if (com_google_android_gms_internal_zzcek.zzbph != null) {
                            zzc(new zzcez(com_google_android_gms_internal_zzcek.zzbph, j), com_google_android_gms_internal_zzceh);
                        }
                        zzwz().zzI(str, com_google_android_gms_internal_zzcek.zzbpd.name);
                    }
                }
                zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying expired conditional properties", zzcfl.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[]{str, String.valueOf(j)});
                }
                List arrayList = new ArrayList(r2.size());
                for (zzcek com_google_android_gms_internal_zzcek2 : r2) {
                    if (com_google_android_gms_internal_zzcek2 != null) {
                        zzwF().zzyC().zzd("User property expired", com_google_android_gms_internal_zzcek2.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcek2.zzbpd.name), com_google_android_gms_internal_zzcek2.zzbpd.getValue());
                        zzwz().zzF(str, com_google_android_gms_internal_zzcek2.zzbpd.name);
                        if (com_google_android_gms_internal_zzcek2.zzbpl != null) {
                            arrayList.add(com_google_android_gms_internal_zzcek2.zzbpl);
                        }
                        zzwz().zzI(str, com_google_android_gms_internal_zzcek2.zzbpd.name);
                    }
                }
                ArrayList arrayList2 = (ArrayList) arrayList;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcez((zzcez) obj, j), com_google_android_gms_internal_zzceh);
                }
                zzwz = zzwz();
                String str2 = com_google_android_gms_internal_zzcez.name;
                zzbo.zzcF(str);
                zzbo.zzcF(str2);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zzd("Invalid time querying triggered conditional properties", zzcfl.zzdZ(str), zzwz.zzwA().zzdW(str2), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[]{str, str2, String.valueOf(j)});
                }
                List arrayList3 = new ArrayList(r2.size());
                for (zzcek com_google_android_gms_internal_zzcek3 : r2) {
                    if (com_google_android_gms_internal_zzcek3 != null) {
                        zzcji com_google_android_gms_internal_zzcji = com_google_android_gms_internal_zzcek3.zzbpd;
                        zzcjk com_google_android_gms_internal_zzcjk = new zzcjk(com_google_android_gms_internal_zzcek3.packageName, com_google_android_gms_internal_zzcek3.zzbpc, com_google_android_gms_internal_zzcji.name, j, com_google_android_gms_internal_zzcji.getValue());
                        if (zzwz().zza(com_google_android_gms_internal_zzcjk)) {
                            zzwF().zzyC().zzd("User property triggered", com_google_android_gms_internal_zzcek3.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zzd("Too many active user properties, ignoring", zzcfl.zzdZ(com_google_android_gms_internal_zzcek3.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                        }
                        if (com_google_android_gms_internal_zzcek3.zzbpj != null) {
                            arrayList3.add(com_google_android_gms_internal_zzcek3.zzbpj);
                        }
                        com_google_android_gms_internal_zzcek3.zzbpd = new zzcji(com_google_android_gms_internal_zzcjk);
                        com_google_android_gms_internal_zzcek3.zzbpf = true;
                        zzwz().zza(com_google_android_gms_internal_zzcek3);
                    }
                }
                zzc(com_google_android_gms_internal_zzcez, com_google_android_gms_internal_zzceh);
                arrayList2 = (ArrayList) arrayList3;
                int size2 = arrayList2.size();
                i = 0;
                while (i < size2) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcez((zzcez) obj, j), com_google_android_gms_internal_zzceh);
                }
                zzwz().setTransactionSuccessful();
            } finally {
                zzwz().endTransaction();
            }
        } else {
            zzf(com_google_android_gms_internal_zzceh);
        }
    }

    @WorkerThread
    final void zzb(zzcez com_google_android_gms_internal_zzcez, String str) {
        zzceg zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzbha.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping event. appId", zzcfl.zzdZ(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(com_google_android_gms_internal_zzcez.name)) {
                zzwF().zzyz().zzj("Could not find package. appId", zzcfl.zzdZ(str));
            }
        }
        zzcez com_google_android_gms_internal_zzcez2 = com_google_android_gms_internal_zzcez;
        zzb(com_google_android_gms_internal_zzcez2, new zzceh(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0));
    }

    final void zzb(zzchj com_google_android_gms_internal_zzchj) {
        this.zzbsQ++;
    }

    @WorkerThread
    final void zzb(zzcji com_google_android_gms_internal_zzcji, zzceh com_google_android_gms_internal_zzceh) {
        int i = 0;
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            if (com_google_android_gms_internal_zzceh.zzboV) {
                int zzes = zzwB().zzes(com_google_android_gms_internal_zzcji.name);
                String zza;
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjl.zza(com_google_android_gms_internal_zzcji.name, zzcem.zzxi(), true);
                    if (com_google_android_gms_internal_zzcji.name != null) {
                        i = com_google_android_gms_internal_zzcji.name.length();
                    }
                    zzwB().zza(com_google_android_gms_internal_zzceh.packageName, zzes, "_ev", zza, i);
                    return;
                }
                zzes = zzwB().zzl(com_google_android_gms_internal_zzcji.name, com_google_android_gms_internal_zzcji.getValue());
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjl.zza(com_google_android_gms_internal_zzcji.name, zzcem.zzxi(), true);
                    Object value = com_google_android_gms_internal_zzcji.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzwB().zza(com_google_android_gms_internal_zzceh.packageName, zzes, "_ev", zza, i);
                    return;
                }
                Object zzm = zzwB().zzm(com_google_android_gms_internal_zzcji.name, com_google_android_gms_internal_zzcji.getValue());
                if (zzm != null) {
                    zzcjk com_google_android_gms_internal_zzcjk = new zzcjk(com_google_android_gms_internal_zzceh.packageName, com_google_android_gms_internal_zzcji.zzbpc, com_google_android_gms_internal_zzcji.name, com_google_android_gms_internal_zzcji.zzbuy, zzm);
                    zzwF().zzyC().zze("Setting user property", zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), zzm);
                    zzwz().beginTransaction();
                    try {
                        zzf(com_google_android_gms_internal_zzceh);
                        boolean zza2 = zzwz().zza(com_google_android_gms_internal_zzcjk);
                        zzwz().setTransactionSuccessful();
                        if (zza2) {
                            zzwF().zzyC().zze("User property set", zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zze("Too many unique user properties are set. Ignoring user property", zzwA().zzdY(com_google_android_gms_internal_zzcjk.mName), com_google_android_gms_internal_zzcjk.mValue);
                            zzwB().zza(com_google_android_gms_internal_zzceh.packageName, 9, null, null, 0);
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
            zzf(com_google_android_gms_internal_zzceh);
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
        zzceg zzdQ = zzwz().zzdQ(str);
        boolean z2 = (i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null;
        if (zzdQ == null) {
            zzwF().zzyz().zzj("App does not exist in onConfigFetched. appId", zzcfl.zzdZ(str));
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
            zzdQ.zzR(this.zzvw.currentTimeMillis());
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
            zzdQ.zzS(this.zzvw.currentTimeMillis());
            zzwz().zza(zzdQ);
            zzwF().zzyD().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
            zzwC().zzej(str);
            zzwG().zzbrl.set(this.zzvw.currentTimeMillis());
            if (!(i == 503 || i == 429)) {
                z = false;
            }
            if (z) {
                zzwG().zzbrm.set(this.zzvw.currentTimeMillis());
            }
            zzzc();
        }
        zzwz().setTransactionSuccessful();
        zzwz().endTransaction();
        this.zzbsU = false;
        zzzg();
    }

    @WorkerThread
    final void zzc(zzcek com_google_android_gms_internal_zzcek, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        zzbo.zzcF(com_google_android_gms_internal_zzcek.packageName);
        zzbo.zzu(com_google_android_gms_internal_zzcek.zzbpd);
        zzbo.zzcF(com_google_android_gms_internal_zzcek.zzbpd.name);
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            if (com_google_android_gms_internal_zzceh.zzboV) {
                zzwz().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzceh);
                    zzcek zzH = zzwz().zzH(com_google_android_gms_internal_zzcek.packageName, com_google_android_gms_internal_zzcek.zzbpd.name);
                    if (zzH != null) {
                        zzwF().zzyC().zze("Removing conditional user property", com_google_android_gms_internal_zzcek.packageName, zzwA().zzdY(com_google_android_gms_internal_zzcek.zzbpd.name));
                        zzwz().zzI(com_google_android_gms_internal_zzcek.packageName, com_google_android_gms_internal_zzcek.zzbpd.name);
                        if (zzH.zzbpf) {
                            zzwz().zzF(com_google_android_gms_internal_zzcek.packageName, com_google_android_gms_internal_zzcek.zzbpd.name);
                        }
                        if (com_google_android_gms_internal_zzcek.zzbpl != null) {
                            Bundle bundle = null;
                            if (com_google_android_gms_internal_zzcek.zzbpl.zzbpM != null) {
                                bundle = com_google_android_gms_internal_zzcek.zzbpl.zzbpM.zzyt();
                            }
                            zzc(zzwB().zza(com_google_android_gms_internal_zzcek.zzbpl.name, bundle, zzH.zzbpc, com_google_android_gms_internal_zzcek.zzbpl.zzbpN, true, false), com_google_android_gms_internal_zzceh);
                        }
                    } else {
                        zzwF().zzyz().zze("Conditional user property doesn't exist", zzcfl.zzdZ(com_google_android_gms_internal_zzcek.packageName), zzwA().zzdY(com_google_android_gms_internal_zzcek.zzbpd.name));
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceh);
            }
        }
    }

    @WorkerThread
    final void zzc(zzcji com_google_android_gms_internal_zzcji, zzceh com_google_android_gms_internal_zzceh) {
        zzwE().zzjC();
        zzkD();
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            if (com_google_android_gms_internal_zzceh.zzboV) {
                zzwF().zzyC().zzj("Removing user property", zzwA().zzdY(com_google_android_gms_internal_zzcji.name));
                zzwz().beginTransaction();
                try {
                    zzf(com_google_android_gms_internal_zzceh);
                    zzwz().zzF(com_google_android_gms_internal_zzceh.packageName, com_google_android_gms_internal_zzcji.name);
                    zzwz().setTransactionSuccessful();
                    zzwF().zzyC().zzj("User property removed", zzwA().zzdY(com_google_android_gms_internal_zzcji.name));
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(com_google_android_gms_internal_zzceh);
            }
        }
    }

    final void zzd(zzceh com_google_android_gms_internal_zzceh) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzcF(com_google_android_gms_internal_zzceh.packageName);
        zzf(com_google_android_gms_internal_zzceh);
    }

    @WorkerThread
    final void zzd(zzcek com_google_android_gms_internal_zzcek) {
        zzceh zzel = zzel(com_google_android_gms_internal_zzcek.packageName);
        if (zzel != null) {
            zzb(com_google_android_gms_internal_zzcek, zzel);
        }
    }

    @WorkerThread
    public final void zze(zzceh com_google_android_gms_internal_zzceh) {
        zzwE().zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzbo.zzcF(com_google_android_gms_internal_zzceh.packageName);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            zzceg zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceh.packageName);
            if (!(zzdQ == null || !TextUtils.isEmpty(zzdQ.getGmpAppId()) || TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ))) {
                zzdQ.zzR(0);
                zzwz().zza(zzdQ);
                zzwC().zzek(com_google_android_gms_internal_zzceh.packageName);
            }
            if (com_google_android_gms_internal_zzceh.zzboV) {
                int i;
                Bundle bundle;
                long j = com_google_android_gms_internal_zzceh.zzbpa;
                if (j == 0) {
                    j = this.zzvw.currentTimeMillis();
                }
                int i2 = com_google_android_gms_internal_zzceh.zzbpb;
                if (i2 == 0 || i2 == 1) {
                    i = i2;
                } else {
                    zzwF().zzyz().zze("Incorrect app type, assuming installed app. appId, appType", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), Integer.valueOf(i2));
                    i = 0;
                }
                zzwz().beginTransaction();
                zzcen zzwz;
                String zzhl;
                try {
                    zzdQ = zzwz().zzdQ(com_google_android_gms_internal_zzceh.packageName);
                    if (!(zzdQ == null || zzdQ.getGmpAppId() == null || zzdQ.getGmpAppId().equals(com_google_android_gms_internal_zzceh.zzboQ))) {
                        zzwF().zzyz().zzj("New GMP App Id passed in. Removing cached database data. appId", zzcfl.zzdZ(zzdQ.zzhl()));
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
                    zzwz.zzwF().zzyx().zze("Error deleting application data. appId, error", zzcfl.zzdZ(zzhl), e);
                } catch (Throwable th) {
                    zzwz().endTransaction();
                }
                if (zzdQ != null) {
                    if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(com_google_android_gms_internal_zzceh.zzbgW))) {
                        bundle = new Bundle();
                        bundle.putString("_pv", zzdQ.zzjH());
                        zzb(new zzcez("_au", new zzcew(bundle), "auto", j), com_google_android_gms_internal_zzceh);
                    }
                }
                zzf(com_google_android_gms_internal_zzceh);
                zzcev com_google_android_gms_internal_zzcev = null;
                if (i == 0) {
                    com_google_android_gms_internal_zzcev = zzwz().zzE(com_google_android_gms_internal_zzceh.packageName, "_f");
                } else if (i == 1) {
                    com_google_android_gms_internal_zzcev = zzwz().zzE(com_google_android_gms_internal_zzceh.packageName, "_v");
                }
                if (com_google_android_gms_internal_zzcev == null) {
                    long j2 = (1 + (j / 3600000)) * 3600000;
                    if (i == 0) {
                        zzb(new zzcji("_fot", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzceh);
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
                            zzwF().zzyx().zzj("PackageManager is null, first open report might be inaccurate. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName));
                        } else {
                            ApplicationInfo applicationInfo;
                            PackageInfo packageInfo = null;
                            try {
                                packageInfo = zzbha.zzaP(this.mContext).getPackageInfo(com_google_android_gms_internal_zzceh.packageName, 0);
                            } catch (NameNotFoundException e2) {
                                zzwF().zzyx().zze("Package info is null, first open report might be inaccurate. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e2);
                            }
                            if (packageInfo != null) {
                                if (packageInfo.firstInstallTime != 0) {
                                    Object obj = null;
                                    if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) {
                                        bundle2.putLong("_uwa", 1);
                                    } else {
                                        obj = 1;
                                    }
                                    zzb(new zzcji("_fi", j, Long.valueOf(obj != null ? 1 : 0), "auto"), com_google_android_gms_internal_zzceh);
                                }
                            }
                            try {
                                applicationInfo = zzbha.zzaP(this.mContext).getApplicationInfo(com_google_android_gms_internal_zzceh.packageName, 0);
                            } catch (NameNotFoundException e22) {
                                zzwF().zzyx().zze("Application info is null, first open report might be inaccurate. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e22);
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
                        zzcen zzwz2 = zzwz();
                        String str = com_google_android_gms_internal_zzceh.packageName;
                        zzbo.zzcF(str);
                        zzwz2.zzjC();
                        zzwz2.zzkD();
                        j2 = zzwz2.zzL(str, "first_open_count");
                        if (j2 >= 0) {
                            bundle2.putLong("_pfo", j2);
                        }
                        zzb(new zzcez("_f", new zzcew(bundle2), "auto", j), com_google_android_gms_internal_zzceh);
                    } else if (i == 1) {
                        zzb(new zzcji("_fvt", j, Long.valueOf(j2), "auto"), com_google_android_gms_internal_zzceh);
                        zzwE().zzjC();
                        zzkD();
                        bundle = new Bundle();
                        bundle.putLong("_c", 1);
                        bundle.putLong("_r", 1);
                        zzb(new zzcez("_v", new zzcew(bundle), "auto", j), com_google_android_gms_internal_zzceh);
                    }
                    bundle = new Bundle();
                    bundle.putLong("_et", 1);
                    zzb(new zzcez("_e", new zzcew(bundle), "auto", j), com_google_android_gms_internal_zzceh);
                } else if (com_google_android_gms_internal_zzceh.zzboW) {
                    zzb(new zzcez("_cd", new zzcew(new Bundle()), "auto", j), com_google_android_gms_internal_zzceh);
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzf(com_google_android_gms_internal_zzceh);
        }
    }

    @WorkerThread
    final void zze(zzcek com_google_android_gms_internal_zzcek) {
        zzceh zzel = zzel(com_google_android_gms_internal_zzcek.packageName);
        if (zzel != null) {
            zzc(com_google_android_gms_internal_zzcek, zzel);
        }
    }

    public final String zzem(String str) {
        Object e;
        try {
            return (String) zzwE().zze(new zzcgn(this, str)).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzwF().zzyx().zze("Failed to get app instance id. appId", zzcfl.zzdZ(str), e);
        return null;
    }

    final void zzkD() {
        if (!this.zzafK) {
            throw new IllegalStateException("AppMeasurement is not initialized");
        }
    }

    public final zze zzkq() {
        return this.zzvw;
    }

    @WorkerThread
    final void zzl(Runnable runnable) {
        zzwE().zzjC();
        if (this.zzbsP == null) {
            this.zzbsP = new ArrayList();
        }
        this.zzbsP.add(runnable);
    }

    public final zzcfj zzwA() {
        zza(this.zzbsw);
        return this.zzbsw;
    }

    public final zzcjl zzwB() {
        zza(this.zzbsv);
        return this.zzbsv;
    }

    public final zzcgf zzwC() {
        zza(this.zzbss);
        return this.zzbss;
    }

    public final zzcja zzwD() {
        zza(this.zzbsr);
        return this.zzbsr;
    }

    public final zzcgg zzwE() {
        zza(this.zzbsq);
        return this.zzbsq;
    }

    public final zzcfl zzwF() {
        zza(this.zzbsp);
        return this.zzbsp;
    }

    public final zzcfw zzwG() {
        zza(this.zzbso);
        return this.zzbso;
    }

    public final zzcem zzwH() {
        return this.zzbsn;
    }

    public final zzcec zzwr() {
        zza(this.zzbsI);
        return this.zzbsI;
    }

    public final zzcej zzws() {
        zza(this.zzbsH);
        return this.zzbsH;
    }

    public final zzchl zzwt() {
        zza(this.zzbsD);
        return this.zzbsD;
    }

    public final zzcfg zzwu() {
        zza(this.zzbsE);
        return this.zzbsE;
    }

    public final zzcet zzwv() {
        zza(this.zzbsC);
        return this.zzbsC;
    }

    public final zzcid zzww() {
        zza(this.zzbsB);
        return this.zzbsB;
    }

    public final zzchz zzwx() {
        zza(this.zzbsA);
        return this.zzbsA;
    }

    public final zzcfh zzwy() {
        zza(this.zzbsy);
        return this.zzbsy;
    }

    public final zzcen zzwz() {
        zza(this.zzbsx);
        return this.zzbsx;
    }

    @WorkerThread
    protected final boolean zzyP() {
        boolean z = false;
        zzkD();
        zzwE().zzjC();
        if (this.zzbsK == null || this.zzbsL == 0 || !(this.zzbsK == null || this.zzbsK.booleanValue() || Math.abs(this.zzvw.elapsedRealtime() - this.zzbsL) <= 1000)) {
            this.zzbsL = this.zzvw.elapsedRealtime();
            zzcem.zzxE();
            if (zzwB().zzbv("android.permission.INTERNET") && zzwB().zzbv("android.permission.ACCESS_NETWORK_STATE") && (zzbha.zzaP(this.mContext).zzsl() || (zzcgc.zzj(this.mContext, false) && zzciw.zzk(this.mContext, false)))) {
                z = true;
            }
            this.zzbsK = Boolean.valueOf(z);
            if (this.zzbsK.booleanValue()) {
                this.zzbsK = Boolean.valueOf(zzwB().zzev(zzwu().getGmpAppId()));
            }
        }
        return this.zzbsK.booleanValue();
    }

    public final zzcfl zzyQ() {
        return (this.zzbsp == null || !this.zzbsp.isInitialized()) ? null : this.zzbsp;
    }

    final zzcgg zzyR() {
        return this.zzbsq;
    }

    public final AppMeasurement zzyS() {
        return this.zzbst;
    }

    public final FirebaseAnalytics zzyT() {
        return this.zzbsu;
    }

    public final zzcfp zzyU() {
        zza(this.zzbsz);
        return this.zzbsz;
    }

    final long zzyY() {
        Long valueOf = Long.valueOf(zzwG().zzbrp.get());
        return valueOf.longValue() == 0 ? this.zzbsX : Math.min(this.zzbsX, valueOf.longValue());
    }

    @WorkerThread
    public final void zzza() {
        String zzxO;
        boolean z = true;
        zzwE().zzjC();
        zzkD();
        this.zzbsW = true;
        String zzyc;
        try {
            zzcem.zzxE();
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
                    long currentTimeMillis = this.zzvw.currentTimeMillis();
                    zzg(null, currentTimeMillis - zzcem.zzxP());
                    long j = zzwG().zzbrk.get();
                    if (j != 0) {
                        zzwF().zzyC().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
                    }
                    zzyc = zzwz().zzyc();
                    Object zzaa;
                    if (TextUtils.isEmpty(zzyc)) {
                        this.zzbsS = -1;
                        zzaa = zzwz().zzaa(currentTimeMillis - zzcem.zzxP());
                        if (!TextUtils.isEmpty(zzaa)) {
                            zzceg zzdQ = zzwz().zzdQ(zzaa);
                            if (zzdQ != null) {
                                zzb(zzdQ);
                            }
                        }
                    } else {
                        if (this.zzbsS == -1) {
                            this.zzbsS = zzwz().zzyj();
                        }
                        List<Pair> zzl = zzwz().zzl(zzyc, this.zzbsn.zzb(zzyc, zzcfb.zzbqb), Math.max(0, this.zzbsn.zzb(zzyc, zzcfb.zzbqc)));
                        if (!zzl.isEmpty()) {
                            zzcjz com_google_android_gms_internal_zzcjz;
                            Object obj;
                            int i;
                            List subList;
                            for (Pair pair : zzl) {
                                com_google_android_gms_internal_zzcjz = (zzcjz) pair.first;
                                if (!TextUtils.isEmpty(com_google_android_gms_internal_zzcjz.zzbvR)) {
                                    obj = com_google_android_gms_internal_zzcjz.zzbvR;
                                    break;
                                }
                            }
                            obj = null;
                            if (obj != null) {
                                for (i = 0; i < zzl.size(); i++) {
                                    com_google_android_gms_internal_zzcjz = (zzcjz) ((Pair) zzl.get(i)).first;
                                    if (!TextUtils.isEmpty(com_google_android_gms_internal_zzcjz.zzbvR) && !com_google_android_gms_internal_zzcjz.zzbvR.equals(obj)) {
                                        subList = zzl.subList(0, i);
                                        break;
                                    }
                                }
                            }
                            subList = zzl;
                            zzcjy com_google_android_gms_internal_zzcjy = new zzcjy();
                            com_google_android_gms_internal_zzcjy.zzbvB = new zzcjz[subList.size()];
                            Collection arrayList = new ArrayList(subList.size());
                            boolean z2 = zzcem.zzyb() && this.zzbsn.zzdO(zzyc);
                            for (i = 0; i < com_google_android_gms_internal_zzcjy.zzbvB.length; i++) {
                                com_google_android_gms_internal_zzcjy.zzbvB[i] = (zzcjz) ((Pair) subList.get(i)).first;
                                arrayList.add((Long) ((Pair) subList.get(i)).second);
                                com_google_android_gms_internal_zzcjy.zzbvB[i].zzbvQ = Long.valueOf(zzcem.zzwP());
                                com_google_android_gms_internal_zzcjy.zzbvB[i].zzbvG = Long.valueOf(currentTimeMillis);
                                com_google_android_gms_internal_zzcjy.zzbvB[i].zzbvW = Boolean.valueOf(zzcem.zzxE());
                                if (!z2) {
                                    com_google_android_gms_internal_zzcjy.zzbvB[i].zzbwd = null;
                                }
                            }
                            Object zza = zzwF().zzz(2) ? zzwA().zza(com_google_android_gms_internal_zzcjy) : null;
                            byte[] zzb = zzwB().zzb(com_google_android_gms_internal_zzcjy);
                            zzxO = zzcem.zzxO();
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
                            if (com_google_android_gms_internal_zzcjy.zzbvB.length > 0) {
                                zzaa = com_google_android_gms_internal_zzcjy.zzbvB[0].zzaH;
                            }
                            zzwF().zzyD().zzd("Uploading data. app, uncompressed size, data", zzaa, Integer.valueOf(zzb.length), zza);
                            this.zzbsV = true;
                            zzyU().zza(zzyc, url, zzb, null, new zzcgo(this));
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
            zzwF().zzyx().zze("Failed to parse upload URL. Not uploading. appId", zzcfl.zzdZ(zzyc), zzxO);
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
