package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.BinderThread;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zze<T extends IInterface> {
    public static final String[] Bs = new String[]{"service_esmobile", "service_googleme"};
    private int Ba;
    private long Bb;
    private long Bc;
    private int Bd;
    private long Be;
    private final zzn Bf;
    private final Object Bg;
    private zzv Bh;
    private zzf Bi;
    private T Bj;
    private final ArrayList<zze<?>> Bk;
    private zzh Bl;
    private int Bm;
    private final zzb Bn;
    private final zzc Bo;
    private final int Bp;
    private final String Bq;
    protected AtomicInteger Br;
    private final Context mContext;
    final Handler mHandler;
    private final com.google.android.gms.common.zzc xn;
    private final Looper zzajn;
    private final Object zzakd;

    public interface zzb {
        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface zzc {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    final class zzd extends Handler {
        final /* synthetic */ zze Bu;

        public zzd(zze com_google_android_gms_common_internal_zze, Looper looper) {
            this.Bu = com_google_android_gms_common_internal_zze;
            super(looper);
        }

        private void zza(Message message) {
            zze com_google_android_gms_common_internal_zze_zze = (zze) message.obj;
            com_google_android_gms_common_internal_zze_zze.zzaub();
            com_google_android_gms_common_internal_zze_zze.unregister();
        }

        private boolean zzb(Message message) {
            return message.what == 2 || message.what == 1 || message.what == 5;
        }

        public void handleMessage(Message message) {
            PendingIntent pendingIntent = null;
            if (this.Bu.Br.get() != message.arg1) {
                if (zzb(message)) {
                    zza(message);
                }
            } else if ((message.what == 1 || message.what == 5) && !this.Bu.isConnecting()) {
                zza(message);
            } else if (message.what == 3) {
                if (message.obj instanceof PendingIntent) {
                    pendingIntent = (PendingIntent) message.obj;
                }
                ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
                this.Bu.Bi.zzh(connectionResult);
                this.Bu.onConnectionFailed(connectionResult);
            } else if (message.what == 4) {
                this.Bu.zzb(4, null);
                if (this.Bu.Bn != null) {
                    this.Bu.Bn.onConnectionSuspended(message.arg2);
                }
                this.Bu.onConnectionSuspended(message.arg2);
                this.Bu.zza(4, 1, null);
            } else if (message.what == 2 && !this.Bu.isConnected()) {
                zza(message);
            } else if (zzb(message)) {
                ((zze) message.obj).zzauc();
            } else {
                Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
            }
        }
    }

    protected abstract class zze<TListener> {
        final /* synthetic */ zze Bu;
        private boolean Bv = false;
        private TListener mListener;

        public zze(zze com_google_android_gms_common_internal_zze, TListener tListener) {
            this.Bu = com_google_android_gms_common_internal_zze;
            this.mListener = tListener;
        }

        public void unregister() {
            zzaud();
            synchronized (this.Bu.Bk) {
                this.Bu.Bk.remove(this);
            }
        }

        protected abstract void zzaub();

        public void zzauc() {
            synchronized (this) {
                Object obj = this.mListener;
                if (this.Bv) {
                    String valueOf = String.valueOf(this);
                    Log.w("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Callback proxy ").append(valueOf).append(" being reused. This is not safe.").toString());
                }
            }
            if (obj != null) {
                try {
                    zzv(obj);
                } catch (RuntimeException e) {
                    zzaub();
                    throw e;
                }
            }
            zzaub();
            synchronized (this) {
                this.Bv = true;
            }
            unregister();
        }

        public void zzaud() {
            synchronized (this) {
                this.mListener = null;
            }
        }

        protected abstract void zzv(TListener tListener);
    }

    public interface zzf {
        void zzh(@NonNull ConnectionResult connectionResult);
    }

    public final class zzh implements ServiceConnection {
        final /* synthetic */ zze Bu;
        private final int Bx;

        public zzh(zze com_google_android_gms_common_internal_zze, int i) {
            this.Bu = com_google_android_gms_common_internal_zze;
            this.Bx = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzac.zzb((Object) iBinder, (Object) "Expecting a valid IBinder");
            synchronized (this.Bu.Bg) {
                this.Bu.Bh = com.google.android.gms.common.internal.zzv.zza.zzdv(iBinder);
            }
            this.Bu.zza(0, null, this.Bx);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (this.Bu.Bg) {
                this.Bu.Bh = null;
            }
            this.Bu.mHandler.sendMessage(this.Bu.mHandler.obtainMessage(4, this.Bx, 1));
        }
    }

    private abstract class zza extends zze<Boolean> {
        public final Bundle Bt;
        final /* synthetic */ zze Bu;
        public final int statusCode;

        @BinderThread
        protected zza(zze com_google_android_gms_common_internal_zze, int i, Bundle bundle) {
            this.Bu = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, Boolean.valueOf(true));
            this.statusCode = i;
            this.Bt = bundle;
        }

        protected abstract boolean zzaua();

        protected void zzaub() {
        }

        protected void zzc(Boolean bool) {
            PendingIntent pendingIntent = null;
            if (bool == null) {
                this.Bu.zzb(1, null);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    if (!zzaua()) {
                        this.Bu.zzb(1, null);
                        zzm(new ConnectionResult(8, null));
                        return;
                    }
                    return;
                case 10:
                    this.Bu.zzb(1, null);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    this.Bu.zzb(1, null);
                    if (this.Bt != null) {
                        pendingIntent = (PendingIntent) this.Bt.getParcelable("pendingIntent");
                    }
                    zzm(new ConnectionResult(this.statusCode, pendingIntent));
                    return;
            }
        }

        protected abstract void zzm(ConnectionResult connectionResult);

        protected /* synthetic */ void zzv(Object obj) {
            zzc((Boolean) obj);
        }
    }

    protected class zzi implements zzf {
        final /* synthetic */ zze Bu;

        public zzi(zze com_google_android_gms_common_internal_zze) {
            this.Bu = com_google_android_gms_common_internal_zze;
        }

        public void zzh(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.Bu.zza(null, this.Bu.zzatz());
            } else if (this.Bu.Bo != null) {
                this.Bu.Bo.onConnectionFailed(connectionResult);
            }
        }
    }

    public static final class zzg extends com.google.android.gms.common.internal.zzu.zza {
        private zze Bw;
        private final int Bx;

        public zzg(@NonNull zze com_google_android_gms_common_internal_zze, int i) {
            this.Bw = com_google_android_gms_common_internal_zze;
            this.Bx = i;
        }

        private void zzaue() {
            this.Bw = null;
        }

        @BinderThread
        public void zza(int i, @NonNull IBinder iBinder, @Nullable Bundle bundle) {
            zzac.zzb(this.Bw, (Object) "onPostInitComplete can be called only once per call to getRemoteService");
            this.Bw.zza(i, iBinder, bundle, this.Bx);
            zzaue();
        }

        @BinderThread
        public void zzb(int i, @Nullable Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }
    }

    protected final class zzj extends zza {
        final /* synthetic */ zze Bu;
        public final IBinder By;

        @BinderThread
        public zzj(zze com_google_android_gms_common_internal_zze, int i, IBinder iBinder, Bundle bundle) {
            this.Bu = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, i, bundle);
            this.By = iBinder;
        }

        protected boolean zzaua() {
            try {
                String interfaceDescriptor = this.By.getInterfaceDescriptor();
                if (this.Bu.zziy().equals(interfaceDescriptor)) {
                    IInterface zzh = this.Bu.zzh(this.By);
                    if (zzh == null || !this.Bu.zza(2, 3, zzh)) {
                        return false;
                    }
                    Bundle zzaoe = this.Bu.zzaoe();
                    if (this.Bu.Bn != null) {
                        this.Bu.Bn.onConnected(zzaoe);
                    }
                    return true;
                }
                String valueOf = String.valueOf(this.Bu.zziy());
                Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
                return false;
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }

        protected void zzm(ConnectionResult connectionResult) {
            if (this.Bu.Bo != null) {
                this.Bu.Bo.onConnectionFailed(connectionResult);
            }
            this.Bu.onConnectionFailed(connectionResult);
        }
    }

    protected final class zzk extends zza {
        final /* synthetic */ zze Bu;

        @BinderThread
        public zzk(zze com_google_android_gms_common_internal_zze, int i, @Nullable Bundle bundle) {
            this.Bu = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, i, bundle);
        }

        protected boolean zzaua() {
            this.Bu.Bi.zzh(ConnectionResult.uJ);
            return true;
        }

        protected void zzm(ConnectionResult connectionResult) {
            this.Bu.Bi.zzh(connectionResult);
            this.Bu.onConnectionFailed(connectionResult);
        }
    }

    protected zze(Context context, Looper looper, int i, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc, String str) {
        this(context, looper, zzn.zzcf(context), com.google.android.gms.common.zzc.zzapd(), i, (zzb) zzac.zzy(com_google_android_gms_common_internal_zze_zzb), (zzc) zzac.zzy(com_google_android_gms_common_internal_zze_zzc), str);
    }

    protected zze(Context context, Looper looper, zzn com_google_android_gms_common_internal_zzn, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, int i, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc, String str) {
        this.zzakd = new Object();
        this.Bg = new Object();
        this.Bk = new ArrayList();
        this.Bm = 1;
        this.Br = new AtomicInteger(0);
        this.mContext = (Context) zzac.zzb((Object) context, (Object) "Context must not be null");
        this.zzajn = (Looper) zzac.zzb((Object) looper, (Object) "Looper must not be null");
        this.Bf = (zzn) zzac.zzb((Object) com_google_android_gms_common_internal_zzn, (Object) "Supervisor must not be null");
        this.xn = (com.google.android.gms.common.zzc) zzac.zzb((Object) com_google_android_gms_common_zzc, (Object) "API availability must not be null");
        this.mHandler = new zzd(this, looper);
        this.Bp = i;
        this.Bn = com_google_android_gms_common_internal_zze_zzb;
        this.Bo = com_google_android_gms_common_internal_zze_zzc;
        this.Bq = str;
    }

    private boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.zzakd) {
            if (this.Bm != i) {
                z = false;
            } else {
                zzb(i2, t);
                z = true;
            }
        }
        return z;
    }

    private void zzats() {
        if (this.Bl != null) {
            String valueOf = String.valueOf(zzix());
            String valueOf2 = String.valueOf(zzatq());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
            this.Bf.zzb(zzix(), zzatq(), this.Bl, zzatr());
            this.Br.incrementAndGet();
        }
        this.Bl = new zzh(this, this.Br.get());
        if (!this.Bf.zza(zzix(), zzatq(), this.Bl, zzatr())) {
            valueOf = String.valueOf(zzix());
            valueOf2 = String.valueOf(zzatq());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(valueOf2).length()).append("unable to connect to service: ").append(valueOf).append(" on ").append(valueOf2).toString());
            zza(16, null, this.Br.get());
        }
    }

    private void zzatt() {
        if (this.Bl != null) {
            this.Bf.zzb(zzix(), zzatq(), this.Bl, zzatr());
            this.Bl = null;
        }
    }

    private void zzb(int i, T t) {
        boolean z = true;
        if ((i == 3) != (t != null)) {
            z = false;
        }
        zzac.zzbs(z);
        synchronized (this.zzakd) {
            this.Bm = i;
            this.Bj = t;
            zzc(i, t);
            switch (i) {
                case 1:
                    zzatt();
                    break;
                case 2:
                    zzats();
                    break;
                case 3:
                    zza((IInterface) t);
                    break;
            }
        }
    }

    public void disconnect() {
        this.Br.incrementAndGet();
        synchronized (this.Bk) {
            int size = this.Bk.size();
            for (int i = 0; i < size; i++) {
                ((zze) this.Bk.get(i)).zzaud();
            }
            this.Bk.clear();
        }
        synchronized (this.Bg) {
            this.Bh = null;
        }
        zzb(1, null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.zzakd) {
            int i = this.Bm;
            IInterface iInterface = this.Bj;
        }
        printWriter.append(str).append("mConnectState=");
        switch (i) {
            case 1:
                printWriter.print("DISCONNECTED");
                break;
            case 2:
                printWriter.print("CONNECTING");
                break;
            case 3:
                printWriter.print("CONNECTED");
                break;
            case 4:
                printWriter.print("DISCONNECTING");
                break;
            default:
                printWriter.print("UNKNOWN");
                break;
        }
        printWriter.append(" mService=");
        if (iInterface == null) {
            printWriter.println("null");
        } else {
            printWriter.append(zziy()).append("@").println(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.Bc > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.Bc;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.Bc)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.Bb > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.Ba) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.Ba));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.Bb;
            valueOf = String.valueOf(simpleDateFormat.format(new Date(this.Bb)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.Be > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.Bd));
            append = printWriter.append(" lastFailedTime=");
            j = this.Be;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.Be)));
            append.println(new StringBuilder(String.valueOf(valueOf2).length() + 21).append(j).append(" ").append(valueOf2).toString());
        }
    }

    public Account getAccount() {
        return null;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Looper getLooper() {
        return this.zzajn;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzakd) {
            z = this.Bm == 3;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzakd) {
            z = this.Bm == 2;
        }
        return z;
    }

    @CallSuper
    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.Bd = connectionResult.getErrorCode();
        this.Be = System.currentTimeMillis();
    }

    @CallSuper
    protected void onConnectionSuspended(int i) {
        this.Ba = i;
        this.Bb = System.currentTimeMillis();
    }

    protected void zza(int i, @Nullable Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i2, -1, new zzk(this, i, bundle)));
    }

    @BinderThread
    protected void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, i2, -1, new zzj(this, i, iBinder, bundle)));
    }

    @CallSuper
    protected void zza(@NonNull T t) {
        this.Bc = System.currentTimeMillis();
    }

    public void zza(@NonNull zzf com_google_android_gms_common_internal_zze_zzf) {
        this.Bi = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zze_zzf, (Object) "Connection progress callbacks cannot be null.");
        zzb(2, null);
    }

    public void zza(zzf com_google_android_gms_common_internal_zze_zzf, ConnectionResult connectionResult) {
        this.Bi = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zze_zzf, (Object) "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.Br.get(), connectionResult.getErrorCode(), connectionResult.getResolution()));
    }

    @WorkerThread
    public void zza(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set) {
        try {
            GetServiceRequest zzo = new GetServiceRequest(this.Bp).zzht(this.mContext.getPackageName()).zzo(zzagl());
            if (set != null) {
                zzo.zzf(set);
            }
            if (zzahd()) {
                zzo.zzd(zzatv()).zzb(com_google_android_gms_common_internal_zzr);
            } else if (zzaty()) {
                zzo.zzd(getAccount());
            }
            synchronized (this.Bg) {
                if (this.Bh != null) {
                    this.Bh.zza(new zzg(this, this.Br.get()), zzo);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (DeadObjectException e) {
            Log.w("GmsClient", "service died");
            zzgl(1);
        } catch (Throwable e2) {
            Log.w("GmsClient", "Remote exception occurred", e2);
        }
    }

    protected Bundle zzagl() {
        return new Bundle();
    }

    public boolean zzahd() {
        return false;
    }

    public boolean zzahs() {
        return false;
    }

    public Intent zzaht() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    public Bundle zzaoe() {
        return null;
    }

    public boolean zzapr() {
        return true;
    }

    @Nullable
    public IBinder zzaps() {
        IBinder iBinder;
        synchronized (this.Bg) {
            if (this.Bh == null) {
                iBinder = null;
            } else {
                iBinder = this.Bh.asBinder();
            }
        }
        return iBinder;
    }

    protected String zzatq() {
        return "com.google.android.gms";
    }

    @Nullable
    protected final String zzatr() {
        return this.Bq == null ? this.mContext.getClass().getName() : this.Bq;
    }

    public void zzatu() {
        int isGooglePlayServicesAvailable = this.xn.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zzb(1, null);
            this.Bi = new zzi(this);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.Br.get(), isGooglePlayServicesAvailable));
            return;
        }
        zza(new zzi(this));
    }

    public final Account zzatv() {
        return getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
    }

    protected final void zzatw() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzatx() throws DeadObjectException {
        T t;
        synchronized (this.zzakd) {
            if (this.Bm == 4) {
                throw new DeadObjectException();
            }
            zzatw();
            zzac.zza(this.Bj != null, (Object) "Client is connected but service is null");
            t = this.Bj;
        }
        return t;
    }

    public boolean zzaty() {
        return false;
    }

    protected Set<Scope> zzatz() {
        return Collections.EMPTY_SET;
    }

    void zzc(int i, T t) {
    }

    public void zzgl(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.Br.get(), i));
    }

    @Nullable
    protected abstract T zzh(IBinder iBinder);

    @NonNull
    protected abstract String zzix();

    @NonNull
    protected abstract String zziy();
}
