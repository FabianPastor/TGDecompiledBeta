package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zzf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public abstract class zzd<T extends IInterface> {
    private static String[] zzfyy = new String[]{"service_esmobile", "service_googleme"};
    private final Context mContext;
    final Handler mHandler;
    private final Object mLock;
    private final Looper zzall;
    private final zzf zzfqc;
    private int zzfyd;
    private long zzfye;
    private long zzfyf;
    private int zzfyg;
    private long zzfyh;
    private zzam zzfyi;
    private final zzag zzfyj;
    private final Object zzfyk;
    private zzay zzfyl;
    protected zzj zzfym;
    private T zzfyn;
    private final ArrayList<zzi<?>> zzfyo;
    private zzl zzfyp;
    private int zzfyq;
    private final zzf zzfyr;
    private final zzg zzfys;
    private final int zzfyt;
    private final String zzfyu;
    private ConnectionResult zzfyv;
    private boolean zzfyw;
    protected AtomicInteger zzfyx;

    protected zzd(Context context, Looper looper, int i, zzf com_google_android_gms_common_internal_zzf, zzg com_google_android_gms_common_internal_zzg, String str) {
        this(context, looper, zzag.zzco(context), zzf.zzafy(), i, (zzf) zzbq.checkNotNull(com_google_android_gms_common_internal_zzf), (zzg) zzbq.checkNotNull(com_google_android_gms_common_internal_zzg), null);
    }

    protected zzd(Context context, Looper looper, zzag com_google_android_gms_common_internal_zzag, zzf com_google_android_gms_common_zzf, int i, zzf com_google_android_gms_common_internal_zzf, zzg com_google_android_gms_common_internal_zzg, String str) {
        this.mLock = new Object();
        this.zzfyk = new Object();
        this.zzfyo = new ArrayList();
        this.zzfyq = 1;
        this.zzfyv = null;
        this.zzfyw = false;
        this.zzfyx = new AtomicInteger(0);
        this.mContext = (Context) zzbq.checkNotNull(context, "Context must not be null");
        this.zzall = (Looper) zzbq.checkNotNull(looper, "Looper must not be null");
        this.zzfyj = (zzag) zzbq.checkNotNull(com_google_android_gms_common_internal_zzag, "Supervisor must not be null");
        this.zzfqc = (zzf) zzbq.checkNotNull(com_google_android_gms_common_zzf, "API availability must not be null");
        this.mHandler = new zzh(this, looper);
        this.zzfyt = i;
        this.zzfyr = com_google_android_gms_common_internal_zzf;
        this.zzfys = com_google_android_gms_common_internal_zzg;
        this.zzfyu = str;
    }

    private final void zza(int i, T t) {
        boolean z = true;
        if ((i == 4) != (t != null)) {
            z = false;
        }
        zzbq.checkArgument(z);
        synchronized (this.mLock) {
            this.zzfyq = i;
            this.zzfyn = t;
            switch (i) {
                case 1:
                    if (this.zzfyp != null) {
                        this.zzfyj.zza(zzhi(), zzakh(), TsExtractor.TS_STREAM_TYPE_AC3, this.zzfyp, zzaki());
                        this.zzfyp = null;
                        break;
                    }
                    break;
                case 2:
                case 3:
                    String zzalo;
                    String packageName;
                    if (!(this.zzfyp == null || this.zzfyi == null)) {
                        zzalo = this.zzfyi.zzalo();
                        packageName = this.zzfyi.getPackageName();
                        Log.e("GmsClient", new StringBuilder((String.valueOf(zzalo).length() + 70) + String.valueOf(packageName).length()).append("Calling connect() while still connected, missing disconnect() for ").append(zzalo).append(" on ").append(packageName).toString());
                        this.zzfyj.zza(this.zzfyi.zzalo(), this.zzfyi.getPackageName(), this.zzfyi.zzalk(), this.zzfyp, zzaki());
                        this.zzfyx.incrementAndGet();
                    }
                    this.zzfyp = new zzl(this, this.zzfyx.get());
                    this.zzfyi = new zzam(zzakh(), zzhi(), false, TsExtractor.TS_STREAM_TYPE_AC3);
                    if (!this.zzfyj.zza(new zzah(this.zzfyi.zzalo(), this.zzfyi.getPackageName(), this.zzfyi.zzalk()), this.zzfyp, zzaki())) {
                        zzalo = this.zzfyi.zzalo();
                        packageName = this.zzfyi.getPackageName();
                        Log.e("GmsClient", new StringBuilder((String.valueOf(zzalo).length() + 34) + String.valueOf(packageName).length()).append("unable to connect to service: ").append(zzalo).append(" on ").append(packageName).toString());
                        zza(16, null, this.zzfyx.get());
                        break;
                    }
                    break;
                case 4:
                    zza((IInterface) t);
                    break;
            }
        }
    }

    private final boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.mLock) {
            if (this.zzfyq != i) {
                z = false;
            } else {
                zza(i2, (IInterface) t);
                z = true;
            }
        }
        return z;
    }

    private final String zzaki() {
        return this.zzfyu == null ? this.mContext.getClass().getName() : this.zzfyu;
    }

    private final boolean zzakk() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzfyq == 3;
        }
        return z;
    }

    private final boolean zzakq() {
        if (this.zzfyw || TextUtils.isEmpty(zzhj()) || TextUtils.isEmpty(null)) {
            return false;
        }
        try {
            Class.forName(zzhj());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private final void zzcf(int i) {
        int i2;
        if (zzakk()) {
            i2 = 5;
            this.zzfyw = true;
        } else {
            i2 = 4;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(i2, this.zzfyx.get(), 16));
    }

    public void disconnect() {
        this.zzfyx.incrementAndGet();
        synchronized (this.zzfyo) {
            int size = this.zzfyo.size();
            for (int i = 0; i < size; i++) {
                ((zzi) this.zzfyo.get(i)).removeListener();
            }
            this.zzfyo.clear();
        }
        synchronized (this.zzfyk) {
            this.zzfyl = null;
        }
        zza(1, null);
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            int i = this.zzfyq;
            IInterface iInterface = this.zzfyn;
        }
        synchronized (this.zzfyk) {
            zzay com_google_android_gms_common_internal_zzay = this.zzfyl;
        }
        printWriter.append(str).append("mConnectState=");
        switch (i) {
            case 1:
                printWriter.print("DISCONNECTED");
                break;
            case 2:
                printWriter.print("REMOTE_CONNECTING");
                break;
            case 3:
                printWriter.print("LOCAL_CONNECTING");
                break;
            case 4:
                printWriter.print("CONNECTED");
                break;
            case 5:
                printWriter.print("DISCONNECTING");
                break;
            default:
                printWriter.print("UNKNOWN");
                break;
        }
        printWriter.append(" mService=");
        if (iInterface == null) {
            printWriter.append("null");
        } else {
            printWriter.append(zzhj()).append("@").append(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        printWriter.append(" mServiceBroker=");
        if (com_google_android_gms_common_internal_zzay == null) {
            printWriter.println("null");
        } else {
            printWriter.append("IGmsServiceBroker@").println(Integer.toHexString(System.identityHashCode(com_google_android_gms_common_internal_zzay.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.zzfyf > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.zzfyf;
            String format = simpleDateFormat.format(new Date(this.zzfyf));
            append.println(new StringBuilder(String.valueOf(format).length() + 21).append(j).append(" ").append(format).toString());
        }
        if (this.zzfye > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.zzfyd) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.zzfyd));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.zzfye;
            format = simpleDateFormat.format(new Date(this.zzfye));
            append.println(new StringBuilder(String.valueOf(format).length() + 21).append(j).append(" ").append(format).toString());
        }
        if (this.zzfyh > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.zzfyg));
            append = printWriter.append(" lastFailedTime=");
            j = this.zzfyh;
            String format2 = simpleDateFormat.format(new Date(this.zzfyh));
            append.println(new StringBuilder(String.valueOf(format2).length() + 21).append(j).append(" ").append(format2).toString());
        }
    }

    public Account getAccount() {
        return null;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public Intent getSignInIntent() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    public final boolean isConnected() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzfyq == 4;
        }
        return z;
    }

    public final boolean isConnecting() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzfyq == 2 || this.zzfyq == 3;
        }
        return z;
    }

    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzfyg = connectionResult.getErrorCode();
        this.zzfyh = System.currentTimeMillis();
    }

    protected void onConnectionSuspended(int i) {
        this.zzfyd = i;
        this.zzfye = System.currentTimeMillis();
    }

    protected final void zza(int i, Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, i2, -1, new zzo(this, i, null)));
    }

    protected void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, i2, -1, new zzn(this, i, iBinder, bundle)));
    }

    protected void zza(T t) {
        this.zzfyf = System.currentTimeMillis();
    }

    public final void zza(zzan com_google_android_gms_common_internal_zzan, Set<Scope> set) {
        Throwable e;
        Bundle zzaap = zzaap();
        zzz com_google_android_gms_common_internal_zzz = new zzz(this.zzfyt);
        com_google_android_gms_common_internal_zzz.zzfzt = this.mContext.getPackageName();
        com_google_android_gms_common_internal_zzz.zzfzw = zzaap;
        if (set != null) {
            com_google_android_gms_common_internal_zzz.zzfzv = (Scope[]) set.toArray(new Scope[set.size()]);
        }
        if (zzaay()) {
            com_google_android_gms_common_internal_zzz.zzfzx = getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
            if (com_google_android_gms_common_internal_zzan != null) {
                com_google_android_gms_common_internal_zzz.zzfzu = com_google_android_gms_common_internal_zzan.asBinder();
            }
        } else if (zzako()) {
            com_google_android_gms_common_internal_zzz.zzfzx = getAccount();
        }
        com_google_android_gms_common_internal_zzz.zzfzy = zzakl();
        try {
            synchronized (this.zzfyk) {
                if (this.zzfyl != null) {
                    this.zzfyl.zza(new zzk(this, this.zzfyx.get()), com_google_android_gms_common_internal_zzz);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (Throwable e2) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zzce(1);
        } catch (SecurityException e3) {
            throw e3;
        } catch (RemoteException e4) {
            e2 = e4;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzfyx.get());
        } catch (RuntimeException e5) {
            e2 = e5;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzfyx.get());
        }
    }

    public void zza(zzj com_google_android_gms_common_internal_zzj) {
        this.zzfym = (zzj) zzbq.checkNotNull(com_google_android_gms_common_internal_zzj, "Connection progress callbacks cannot be null.");
        zza(2, null);
    }

    protected final void zza(zzj com_google_android_gms_common_internal_zzj, int i, PendingIntent pendingIntent) {
        this.zzfym = (zzj) zzbq.checkNotNull(com_google_android_gms_common_internal_zzj, "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzfyx.get(), i, pendingIntent));
    }

    public void zza(zzp com_google_android_gms_common_internal_zzp) {
        com_google_android_gms_common_internal_zzp.zzajf();
    }

    protected Bundle zzaap() {
        return new Bundle();
    }

    public boolean zzaay() {
        return false;
    }

    public boolean zzabj() {
        return false;
    }

    public Bundle zzafi() {
        return null;
    }

    public boolean zzagg() {
        return true;
    }

    public final IBinder zzagh() {
        IBinder iBinder;
        synchronized (this.zzfyk) {
            if (this.zzfyl == null) {
                iBinder = null;
            } else {
                iBinder = this.zzfyl.asBinder();
            }
        }
        return iBinder;
    }

    public final String zzagi() {
        if (isConnected() && this.zzfyi != null) {
            return this.zzfyi.getPackageName();
        }
        throw new RuntimeException("Failed to connect when checking package");
    }

    protected String zzakh() {
        return "com.google.android.gms";
    }

    public final void zzakj() {
        int isGooglePlayServicesAvailable = this.zzfqc.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zza(1, null);
            zza(new zzm(this), isGooglePlayServicesAvailable, null);
            return;
        }
        zza(new zzm(this));
    }

    public zzc[] zzakl() {
        return new zzc[0];
    }

    protected final void zzakm() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzakn() throws DeadObjectException {
        T t;
        synchronized (this.mLock) {
            if (this.zzfyq == 5) {
                throw new DeadObjectException();
            }
            zzakm();
            zzbq.zza(this.zzfyn != null, "Client is connected but service is null");
            t = this.zzfyn;
        }
        return t;
    }

    public boolean zzako() {
        return false;
    }

    protected Set<Scope> zzakp() {
        return Collections.EMPTY_SET;
    }

    public final void zzce(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, this.zzfyx.get(), i));
    }

    protected abstract T zzd(IBinder iBinder);

    protected abstract String zzhi();

    protected abstract String zzhj();
}
