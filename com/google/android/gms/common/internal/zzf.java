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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zzf<T extends IInterface> {
    public static final String[] zzaFs = new String[]{"service_esmobile", "service_googleme"};
    private final Context mContext;
    final Handler mHandler;
    private final com.google.android.gms.common.zze zzaAQ;
    private int zzaFa;
    private long zzaFb;
    private long zzaFc;
    private int zzaFd;
    private long zzaFe;
    private final zzn zzaFf;
    private final Object zzaFg;
    private zzv zzaFh;
    protected zzf zzaFi;
    private T zzaFj;
    private final ArrayList<zze<?>> zzaFk;
    private zzh zzaFl;
    private int zzaFm;
    private final zzb zzaFn;
    private final zzc zzaFo;
    private final int zzaFp;
    private final String zzaFq;
    protected AtomicInteger zzaFr;
    private final Object zzrJ;
    private final Looper zzrs;

    public interface zzb {
        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface zzc {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    final class zzd extends Handler {
        final /* synthetic */ zzf zzaFu;

        public zzd(zzf com_google_android_gms_common_internal_zzf, Looper looper) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            super(looper);
        }

        private void zza(Message message) {
            ((zze) message.obj).unregister();
        }

        private boolean zzb(Message message) {
            return message.what == 2 || message.what == 1 || message.what == 5;
        }

        public void handleMessage(Message message) {
            PendingIntent pendingIntent = null;
            if (this.zzaFu.zzaFr.get() != message.arg1) {
                if (zzb(message)) {
                    zza(message);
                }
            } else if ((message.what == 1 || message.what == 5) && !this.zzaFu.isConnecting()) {
                zza(message);
            } else if (message.what == 3) {
                if (message.obj instanceof PendingIntent) {
                    pendingIntent = (PendingIntent) message.obj;
                }
                ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
                this.zzaFu.zzaFi.zzg(connectionResult);
                this.zzaFu.onConnectionFailed(connectionResult);
            } else if (message.what == 4) {
                this.zzaFu.zza(4, null);
                if (this.zzaFu.zzaFn != null) {
                    this.zzaFu.zzaFn.onConnectionSuspended(message.arg2);
                }
                this.zzaFu.onConnectionSuspended(message.arg2);
                this.zzaFu.zza(4, 1, null);
            } else if (message.what == 2 && !this.zzaFu.isConnected()) {
                zza(message);
            } else if (zzb(message)) {
                ((zze) message.obj).zzxH();
            } else {
                Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
            }
        }
    }

    protected abstract class zze<TListener> {
        private TListener mListener;
        final /* synthetic */ zzf zzaFu;
        private boolean zzaFv = false;

        public zze(zzf com_google_android_gms_common_internal_zzf, TListener tListener) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            this.mListener = tListener;
        }

        public void unregister() {
            zzxI();
            synchronized (this.zzaFu.zzaFk) {
                this.zzaFu.zzaFk.remove(this);
            }
        }

        protected abstract void zzu(TListener tListener);

        public void zzxH() {
            synchronized (this) {
                Object obj = this.mListener;
                if (this.zzaFv) {
                    String valueOf = String.valueOf(this);
                    Log.w("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Callback proxy ").append(valueOf).append(" being reused. This is not safe.").toString());
                }
            }
            if (obj != null) {
                try {
                    zzu(obj);
                } catch (RuntimeException e) {
                    throw e;
                }
            }
            synchronized (this) {
                this.zzaFv = true;
            }
            unregister();
        }

        public void zzxI() {
            synchronized (this) {
                this.mListener = null;
            }
        }
    }

    public interface zzf {
        void zzg(@NonNull ConnectionResult connectionResult);
    }

    public final class zzh implements ServiceConnection {
        final /* synthetic */ zzf zzaFu;
        private final int zzaFx;

        public zzh(zzf com_google_android_gms_common_internal_zzf, int i) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            this.zzaFx = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null) {
                this.zzaFu.zza(8, null, this.zzaFx);
                return;
            }
            synchronized (this.zzaFu.zzaFg) {
                this.zzaFu.zzaFh = com.google.android.gms.common.internal.zzv.zza.zzbu(iBinder);
            }
            this.zzaFu.zza(0, null, this.zzaFx);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (this.zzaFu.zzaFg) {
                this.zzaFu.zzaFh = null;
            }
            this.zzaFu.mHandler.sendMessage(this.zzaFu.mHandler.obtainMessage(4, this.zzaFx, 1));
        }
    }

    private abstract class zza extends zze<Boolean> {
        public final int statusCode;
        public final Bundle zzaFt;
        final /* synthetic */ zzf zzaFu;

        @BinderThread
        protected zza(zzf com_google_android_gms_common_internal_zzf, int i, Bundle bundle) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, Boolean.valueOf(true));
            this.statusCode = i;
            this.zzaFt = bundle;
        }

        protected void zzb(Boolean bool) {
            PendingIntent pendingIntent = null;
            if (bool == null) {
                this.zzaFu.zza(1, null);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    if (!zzxG()) {
                        this.zzaFu.zza(1, null);
                        zzm(new ConnectionResult(8, null));
                        return;
                    }
                    return;
                case 10:
                    this.zzaFu.zza(1, null);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    this.zzaFu.zza(1, null);
                    if (this.zzaFt != null) {
                        pendingIntent = (PendingIntent) this.zzaFt.getParcelable("pendingIntent");
                    }
                    zzm(new ConnectionResult(this.statusCode, pendingIntent));
                    return;
            }
        }

        protected abstract void zzm(ConnectionResult connectionResult);

        protected /* synthetic */ void zzu(Object obj) {
            zzb((Boolean) obj);
        }

        protected abstract boolean zzxG();
    }

    protected class zzi implements zzf {
        final /* synthetic */ zzf zzaFu;

        public zzi(zzf com_google_android_gms_common_internal_zzf) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.zzaFu.zza(null, this.zzaFu.zzxF());
            } else if (this.zzaFu.zzaFo != null) {
                this.zzaFu.zzaFo.onConnectionFailed(connectionResult);
            }
        }
    }

    public static final class zzg extends com.google.android.gms.common.internal.zzu.zza {
        private zzf zzaFw;
        private final int zzaFx;

        public zzg(@NonNull zzf com_google_android_gms_common_internal_zzf, int i) {
            this.zzaFw = com_google_android_gms_common_internal_zzf;
            this.zzaFx = i;
        }

        private void zzxJ() {
            this.zzaFw = null;
        }

        @BinderThread
        public void zza(int i, @NonNull IBinder iBinder, @Nullable Bundle bundle) {
            zzac.zzb(this.zzaFw, (Object) "onPostInitComplete can be called only once per call to getRemoteService");
            this.zzaFw.zza(i, iBinder, bundle, this.zzaFx);
            zzxJ();
        }

        @BinderThread
        public void zzb(int i, @Nullable Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }
    }

    protected final class zzj extends zza {
        final /* synthetic */ zzf zzaFu;
        public final IBinder zzaFy;

        @BinderThread
        public zzj(zzf com_google_android_gms_common_internal_zzf, int i, IBinder iBinder, Bundle bundle) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, i, bundle);
            this.zzaFy = iBinder;
        }

        protected void zzm(ConnectionResult connectionResult) {
            if (this.zzaFu.zzaFo != null) {
                this.zzaFu.zzaFo.onConnectionFailed(connectionResult);
            }
            this.zzaFu.onConnectionFailed(connectionResult);
        }

        protected boolean zzxG() {
            try {
                String interfaceDescriptor = this.zzaFy.getInterfaceDescriptor();
                if (this.zzaFu.zzeA().equals(interfaceDescriptor)) {
                    IInterface zzh = this.zzaFu.zzh(this.zzaFy);
                    if (zzh == null || !this.zzaFu.zza(2, 3, zzh)) {
                        return false;
                    }
                    Bundle zzuC = this.zzaFu.zzuC();
                    if (this.zzaFu.zzaFn != null) {
                        this.zzaFu.zzaFn.onConnected(zzuC);
                    }
                    return true;
                }
                String valueOf = String.valueOf(this.zzaFu.zzeA());
                Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
                return false;
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }
    }

    protected final class zzk extends zza {
        final /* synthetic */ zzf zzaFu;

        @BinderThread
        public zzk(zzf com_google_android_gms_common_internal_zzf, int i, @Nullable Bundle bundle) {
            this.zzaFu = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, i, bundle);
        }

        protected void zzm(ConnectionResult connectionResult) {
            this.zzaFu.zzaFi.zzg(connectionResult);
            this.zzaFu.onConnectionFailed(connectionResult);
        }

        protected boolean zzxG() {
            this.zzaFu.zzaFi.zzg(ConnectionResult.zzayj);
            return true;
        }
    }

    protected zzf(Context context, Looper looper, int i, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc, String str) {
        this(context, looper, zzn.zzaU(context), com.google.android.gms.common.zze.zzuY(), i, (zzb) zzac.zzw(com_google_android_gms_common_internal_zzf_zzb), (zzc) zzac.zzw(com_google_android_gms_common_internal_zzf_zzc), str);
    }

    protected zzf(Context context, Looper looper, zzn com_google_android_gms_common_internal_zzn, com.google.android.gms.common.zze com_google_android_gms_common_zze, int i, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc, String str) {
        this.zzrJ = new Object();
        this.zzaFg = new Object();
        this.zzaFk = new ArrayList();
        this.zzaFm = 1;
        this.zzaFr = new AtomicInteger(0);
        this.mContext = (Context) zzac.zzb((Object) context, (Object) "Context must not be null");
        this.zzrs = (Looper) zzac.zzb((Object) looper, (Object) "Looper must not be null");
        this.zzaFf = (zzn) zzac.zzb((Object) com_google_android_gms_common_internal_zzn, (Object) "Supervisor must not be null");
        this.zzaAQ = (com.google.android.gms.common.zze) zzac.zzb((Object) com_google_android_gms_common_zze, (Object) "API availability must not be null");
        this.mHandler = new zzd(this, looper);
        this.zzaFp = i;
        this.zzaFn = com_google_android_gms_common_internal_zzf_zzb;
        this.zzaFo = com_google_android_gms_common_internal_zzf_zzc;
        this.zzaFq = str;
    }

    private void zza(int i, T t) {
        boolean z = true;
        if ((i == 3) != (t != null)) {
            z = false;
        }
        zzac.zzax(z);
        synchronized (this.zzrJ) {
            this.zzaFm = i;
            this.zzaFj = t;
            switch (i) {
                case 1:
                    zzxy();
                    break;
                case 2:
                    zzxx();
                    break;
                case 3:
                    zza((IInterface) t);
                    break;
            }
        }
    }

    private boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.zzrJ) {
            if (this.zzaFm != i) {
                z = false;
            } else {
                zza(i2, (IInterface) t);
                z = true;
            }
        }
        return z;
    }

    private void zzxx() {
        if (this.zzaFl != null) {
            String valueOf = String.valueOf(zzez());
            String valueOf2 = String.valueOf(zzxv());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
            this.zzaFf.zzb(zzez(), zzxv(), this.zzaFl, zzxw());
            this.zzaFr.incrementAndGet();
        }
        this.zzaFl = new zzh(this, this.zzaFr.get());
        if (!this.zzaFf.zza(zzez(), zzxv(), this.zzaFl, zzxw())) {
            valueOf = String.valueOf(zzez());
            valueOf2 = String.valueOf(zzxv());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(valueOf2).length()).append("unable to connect to service: ").append(valueOf).append(" on ").append(valueOf2).toString());
            zza(16, null, this.zzaFr.get());
        }
    }

    private void zzxy() {
        if (this.zzaFl != null) {
            this.zzaFf.zzb(zzez(), zzxv(), this.zzaFl, zzxw());
            this.zzaFl = null;
        }
    }

    public void disconnect() {
        this.zzaFr.incrementAndGet();
        synchronized (this.zzaFk) {
            int size = this.zzaFk.size();
            for (int i = 0; i < size; i++) {
                ((zze) this.zzaFk.get(i)).zzxI();
            }
            this.zzaFk.clear();
        }
        synchronized (this.zzaFg) {
            this.zzaFh = null;
        }
        zza(1, null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.zzrJ) {
            int i = this.zzaFm;
            IInterface iInterface = this.zzaFj;
        }
        synchronized (this.zzaFg) {
            zzv com_google_android_gms_common_internal_zzv = this.zzaFh;
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
            printWriter.append("null");
        } else {
            printWriter.append(zzeA()).append("@").append(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        printWriter.append(" mServiceBroker=");
        if (com_google_android_gms_common_internal_zzv == null) {
            printWriter.println("null");
        } else {
            printWriter.append("IGmsServiceBroker@").println(Integer.toHexString(System.identityHashCode(com_google_android_gms_common_internal_zzv.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.zzaFc > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.zzaFc;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaFc)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaFb > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.zzaFa) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.zzaFa));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.zzaFb;
            valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaFb)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaFe > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.zzaFd));
            append = printWriter.append(" lastFailedTime=");
            j = this.zzaFe;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.zzaFe)));
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
        return this.zzrs;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzrJ) {
            z = this.zzaFm == 3;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzrJ) {
            z = this.zzaFm == 2;
        }
        return z;
    }

    @CallSuper
    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzaFd = connectionResult.getErrorCode();
        this.zzaFe = System.currentTimeMillis();
    }

    @CallSuper
    protected void onConnectionSuspended(int i) {
        this.zzaFa = i;
        this.zzaFb = System.currentTimeMillis();
    }

    protected void zza(int i, @Nullable Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i2, -1, new zzk(this, i, bundle)));
    }

    protected void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, i2, -1, new zzj(this, i, iBinder, bundle)));
    }

    @CallSuper
    protected void zza(@NonNull T t) {
        this.zzaFc = System.currentTimeMillis();
    }

    public void zza(@NonNull zzf com_google_android_gms_common_internal_zzf_zzf) {
        this.zzaFi = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zzf_zzf, (Object) "Connection progress callbacks cannot be null.");
        zza(2, null);
    }

    public void zza(@NonNull zzf com_google_android_gms_common_internal_zzf_zzf, int i, @Nullable PendingIntent pendingIntent) {
        this.zzaFi = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zzf_zzf, (Object) "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzaFr.get(), i, pendingIntent));
    }

    @WorkerThread
    public void zza(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set) {
        Throwable e;
        zzj zzp = new zzj(this.zzaFp).zzdm(this.mContext.getPackageName()).zzp(zzqL());
        if (set != null) {
            zzp.zzf((Collection) set);
        }
        if (zzrd()) {
            zzp.zzf(zzxB()).zzb(com_google_android_gms_common_internal_zzr);
        } else if (zzxE()) {
            zzp.zzf(getAccount());
        }
        zzp.zza(zzxA());
        try {
            synchronized (this.zzaFg) {
                if (this.zzaFh != null) {
                    this.zzaFh.zza(new zzg(this, this.zzaFr.get()), zzp);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (Throwable e2) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zzcS(1);
        } catch (SecurityException e3) {
            throw e3;
        } catch (RemoteException e4) {
            e2 = e4;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzaFr.get());
        } catch (RuntimeException e5) {
            e2 = e5;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzaFr.get());
        }
    }

    public void zzcS(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.zzaFr.get(), i));
    }

    @NonNull
    protected abstract String zzeA();

    @NonNull
    protected abstract String zzez();

    @Nullable
    protected abstract T zzh(IBinder iBinder);

    protected Bundle zzqL() {
        return new Bundle();
    }

    public boolean zzrd() {
        return false;
    }

    public boolean zzrr() {
        return false;
    }

    public Intent zzrs() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    public Bundle zzuC() {
        return null;
    }

    public boolean zzvh() {
        return true;
    }

    @Nullable
    public IBinder zzvi() {
        IBinder iBinder;
        synchronized (this.zzaFg) {
            if (this.zzaFh == null) {
                iBinder = null;
            } else {
                iBinder = this.zzaFh.asBinder();
            }
        }
        return iBinder;
    }

    public com.google.android.gms.common.zzc[] zzxA() {
        return new com.google.android.gms.common.zzc[0];
    }

    public final Account zzxB() {
        return getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
    }

    protected final void zzxC() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzxD() throws DeadObjectException {
        T t;
        synchronized (this.zzrJ) {
            if (this.zzaFm == 4) {
                throw new DeadObjectException();
            }
            zzxC();
            zzac.zza(this.zzaFj != null, (Object) "Client is connected but service is null");
            t = this.zzaFj;
        }
        return t;
    }

    public boolean zzxE() {
        return false;
    }

    protected Set<Scope> zzxF() {
        return Collections.EMPTY_SET;
    }

    protected String zzxv() {
        return "com.google.android.gms";
    }

    @Nullable
    protected final String zzxw() {
        return this.zzaFq == null ? this.mContext.getClass().getName() : this.zzaFq;
    }

    public void zzxz() {
        int isGooglePlayServicesAvailable = this.zzaAQ.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zza(1, null);
            zza(new zzi(this), isGooglePlayServicesAvailable, null);
            return;
        }
        zza(new zzi(this));
    }
}
