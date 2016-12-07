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
    public static final String[] DB = new String[]{"service_esmobile", "service_googleme"};
    protected AtomicInteger DA;
    private int Dj;
    private long Dk;
    private long Dl;
    private int Dm;
    private long Dn;
    private final zzl Do;
    private final Object Dp;
    private zzt Dq;
    protected zzf Dr;
    private T Ds;
    private final ArrayList<zze<?>> Dt;
    private zzh Du;
    private int Dv;
    private final zzb Dw;
    private final zzc Dx;
    private final int Dy;
    private final String Dz;
    private final Context mContext;
    final Handler mHandler;
    private final com.google.android.gms.common.zzc zm;
    private final Looper zzajy;
    private final Object zzako;

    public interface zzb {
        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface zzc {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    final class zzd extends Handler {
        final /* synthetic */ zze DD;

        public zzd(zze com_google_android_gms_common_internal_zze, Looper looper) {
            this.DD = com_google_android_gms_common_internal_zze;
            super(looper);
        }

        private void zza(Message message) {
            zze com_google_android_gms_common_internal_zze_zze = (zze) message.obj;
            com_google_android_gms_common_internal_zze_zze.zzavk();
            com_google_android_gms_common_internal_zze_zze.unregister();
        }

        private boolean zzb(Message message) {
            return message.what == 2 || message.what == 1 || message.what == 5;
        }

        public void handleMessage(Message message) {
            PendingIntent pendingIntent = null;
            if (this.DD.DA.get() != message.arg1) {
                if (zzb(message)) {
                    zza(message);
                }
            } else if ((message.what == 1 || message.what == 5) && !this.DD.isConnecting()) {
                zza(message);
            } else if (message.what == 3) {
                if (message.obj instanceof PendingIntent) {
                    pendingIntent = (PendingIntent) message.obj;
                }
                ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
                this.DD.Dr.zzg(connectionResult);
                this.DD.onConnectionFailed(connectionResult);
            } else if (message.what == 4) {
                this.DD.zzb(4, null);
                if (this.DD.Dw != null) {
                    this.DD.Dw.onConnectionSuspended(message.arg2);
                }
                this.DD.onConnectionSuspended(message.arg2);
                this.DD.zza(4, 1, null);
            } else if (message.what == 2 && !this.DD.isConnected()) {
                zza(message);
            } else if (zzb(message)) {
                ((zze) message.obj).zzavl();
            } else {
                Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
            }
        }
    }

    protected abstract class zze<TListener> {
        final /* synthetic */ zze DD;
        private boolean DE = false;
        private TListener mListener;

        public zze(zze com_google_android_gms_common_internal_zze, TListener tListener) {
            this.DD = com_google_android_gms_common_internal_zze;
            this.mListener = tListener;
        }

        public void unregister() {
            zzavm();
            synchronized (this.DD.Dt) {
                this.DD.Dt.remove(this);
            }
        }

        protected abstract void zzavk();

        public void zzavl() {
            synchronized (this) {
                Object obj = this.mListener;
                if (this.DE) {
                    String valueOf = String.valueOf(this);
                    Log.w("GmsClient", new StringBuilder(String.valueOf(valueOf).length() + 47).append("Callback proxy ").append(valueOf).append(" being reused. This is not safe.").toString());
                }
            }
            if (obj != null) {
                try {
                    zzv(obj);
                } catch (RuntimeException e) {
                    zzavk();
                    throw e;
                }
            }
            zzavk();
            synchronized (this) {
                this.DE = true;
            }
            unregister();
        }

        public void zzavm() {
            synchronized (this) {
                this.mListener = null;
            }
        }

        protected abstract void zzv(TListener tListener);
    }

    public interface zzf {
        void zzg(@NonNull ConnectionResult connectionResult);
    }

    public final class zzh implements ServiceConnection {
        final /* synthetic */ zze DD;
        private final int DG;

        public zzh(zze com_google_android_gms_common_internal_zze, int i) {
            this.DD = com_google_android_gms_common_internal_zze;
            this.DG = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null) {
                this.DD.zzl(new ConnectionResult(8, null, "ServiceBroker IBinder is null"));
                return;
            }
            synchronized (this.DD.Dp) {
                this.DD.Dq = com.google.android.gms.common.internal.zzt.zza.zzdu(iBinder);
            }
            this.DD.zza(0, null, this.DG);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (this.DD.Dp) {
                this.DD.Dq = null;
            }
            this.DD.mHandler.sendMessage(this.DD.mHandler.obtainMessage(4, this.DG, 1));
        }
    }

    private abstract class zza extends zze<Boolean> {
        public final Bundle DC;
        final /* synthetic */ zze DD;
        public final int statusCode;

        @BinderThread
        protected zza(zze com_google_android_gms_common_internal_zze, int i, Bundle bundle) {
            this.DD = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, Boolean.valueOf(true));
            this.statusCode = i;
            this.DC = bundle;
        }

        protected abstract boolean zzavj();

        protected void zzavk() {
        }

        protected void zzc(Boolean bool) {
            PendingIntent pendingIntent = null;
            if (bool == null) {
                this.DD.zzb(1, null);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    if (!zzavj()) {
                        this.DD.zzb(1, null);
                        zzm(new ConnectionResult(8, null));
                        return;
                    }
                    return;
                case 10:
                    this.DD.zzb(1, null);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    this.DD.zzb(1, null);
                    if (this.DC != null) {
                        pendingIntent = (PendingIntent) this.DC.getParcelable("pendingIntent");
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
        final /* synthetic */ zze DD;

        public zzi(zze com_google_android_gms_common_internal_zze) {
            this.DD = com_google_android_gms_common_internal_zze;
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.DD.zza(null, this.DD.zzavi());
            } else if (this.DD.Dx != null) {
                this.DD.Dx.onConnectionFailed(connectionResult);
            }
        }
    }

    public static final class zzg extends com.google.android.gms.common.internal.zzs.zza {
        private zze DF;
        private final int DG;

        public zzg(@NonNull zze com_google_android_gms_common_internal_zze, int i) {
            this.DF = com_google_android_gms_common_internal_zze;
            this.DG = i;
        }

        private void zzavn() {
            this.DF = null;
        }

        @BinderThread
        public void zza(int i, @NonNull IBinder iBinder, @Nullable Bundle bundle) {
            zzaa.zzb(this.DF, (Object) "onPostInitComplete can be called only once per call to getRemoteService");
            this.DF.zza(i, iBinder, bundle, this.DG);
            zzavn();
        }

        @BinderThread
        public void zzb(int i, @Nullable Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }
    }

    protected final class zzj extends zza {
        final /* synthetic */ zze DD;
        public final IBinder DH;

        @BinderThread
        public zzj(zze com_google_android_gms_common_internal_zze, int i, IBinder iBinder, Bundle bundle) {
            this.DD = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, i, bundle);
            this.DH = iBinder;
        }

        protected boolean zzavj() {
            try {
                String interfaceDescriptor = this.DH.getInterfaceDescriptor();
                if (this.DD.zzjy().equals(interfaceDescriptor)) {
                    IInterface zzh = this.DD.zzh(this.DH);
                    if (zzh == null || !this.DD.zza(2, 3, zzh)) {
                        return false;
                    }
                    Bundle zzapn = this.DD.zzapn();
                    if (this.DD.Dw != null) {
                        this.DD.Dw.onConnected(zzapn);
                    }
                    return true;
                }
                String valueOf = String.valueOf(this.DD.zzjy());
                Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
                return false;
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }

        protected void zzm(ConnectionResult connectionResult) {
            if (this.DD.Dx != null) {
                this.DD.Dx.onConnectionFailed(connectionResult);
            }
            this.DD.onConnectionFailed(connectionResult);
        }
    }

    protected final class zzk extends zza {
        final /* synthetic */ zze DD;

        @BinderThread
        public zzk(zze com_google_android_gms_common_internal_zze, int i, @Nullable Bundle bundle) {
            this.DD = com_google_android_gms_common_internal_zze;
            super(com_google_android_gms_common_internal_zze, i, bundle);
        }

        protected boolean zzavj() {
            this.DD.Dr.zzg(ConnectionResult.wO);
            return true;
        }

        protected void zzm(ConnectionResult connectionResult) {
            this.DD.Dr.zzg(connectionResult);
            this.DD.onConnectionFailed(connectionResult);
        }
    }

    protected zze(Context context, Looper looper, int i, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc, String str) {
        this(context, looper, zzl.zzcc(context), com.google.android.gms.common.zzc.zzaql(), i, (zzb) zzaa.zzy(com_google_android_gms_common_internal_zze_zzb), (zzc) zzaa.zzy(com_google_android_gms_common_internal_zze_zzc), str);
    }

    protected zze(Context context, Looper looper, zzl com_google_android_gms_common_internal_zzl, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, int i, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc, String str) {
        this.zzako = new Object();
        this.Dp = new Object();
        this.Dt = new ArrayList();
        this.Dv = 1;
        this.DA = new AtomicInteger(0);
        this.mContext = (Context) zzaa.zzb((Object) context, (Object) "Context must not be null");
        this.zzajy = (Looper) zzaa.zzb((Object) looper, (Object) "Looper must not be null");
        this.Do = (zzl) zzaa.zzb((Object) com_google_android_gms_common_internal_zzl, (Object) "Supervisor must not be null");
        this.zm = (com.google.android.gms.common.zzc) zzaa.zzb((Object) com_google_android_gms_common_zzc, (Object) "API availability must not be null");
        this.mHandler = new zzd(this, looper);
        this.Dy = i;
        this.Dw = com_google_android_gms_common_internal_zze_zzb;
        this.Dx = com_google_android_gms_common_internal_zze_zzc;
        this.Dz = str;
    }

    private boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.zzako) {
            if (this.Dv != i) {
                z = false;
            } else {
                zzb(i2, t);
                z = true;
            }
        }
        return z;
    }

    private void zzavb() {
        if (this.Du != null) {
            String valueOf = String.valueOf(zzjx());
            String valueOf2 = String.valueOf(zzauz());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
            this.Do.zzb(zzjx(), zzauz(), this.Du, zzava());
            this.DA.incrementAndGet();
        }
        this.Du = new zzh(this, this.DA.get());
        if (!this.Do.zza(zzjx(), zzauz(), this.Du, zzava())) {
            valueOf = String.valueOf(zzjx());
            valueOf2 = String.valueOf(zzauz());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(valueOf2).length()).append("unable to connect to service: ").append(valueOf).append(" on ").append(valueOf2).toString());
            zza(16, null, this.DA.get());
        }
    }

    private void zzavc() {
        if (this.Du != null) {
            this.Do.zzb(zzjx(), zzauz(), this.Du, zzava());
            this.Du = null;
        }
    }

    private void zzb(int i, T t) {
        boolean z = true;
        if ((i == 3) != (t != null)) {
            z = false;
        }
        zzaa.zzbt(z);
        synchronized (this.zzako) {
            this.Dv = i;
            this.Ds = t;
            zzc(i, t);
            switch (i) {
                case 1:
                    zzavc();
                    break;
                case 2:
                    zzavb();
                    break;
                case 3:
                    zza((IInterface) t);
                    break;
            }
        }
    }

    private void zzl(ConnectionResult connectionResult) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.DA.get(), connectionResult.getErrorCode(), connectionResult.getResolution()));
    }

    public void disconnect() {
        this.DA.incrementAndGet();
        synchronized (this.Dt) {
            int size = this.Dt.size();
            for (int i = 0; i < size; i++) {
                ((zze) this.Dt.get(i)).zzavm();
            }
            this.Dt.clear();
        }
        synchronized (this.Dp) {
            this.Dq = null;
        }
        zzb(1, null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.zzako) {
            int i = this.Dv;
            IInterface iInterface = this.Ds;
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
            printWriter.append(zzjy()).append("@").println(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.Dl > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.Dl;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.Dl)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.Dk > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.Dj) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.Dj));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.Dk;
            valueOf = String.valueOf(simpleDateFormat.format(new Date(this.Dk)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.Dn > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.Dm));
            append = printWriter.append(" lastFailedTime=");
            j = this.Dn;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.Dn)));
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
        return this.zzajy;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzako) {
            z = this.Dv == 3;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzako) {
            z = this.Dv == 2;
        }
        return z;
    }

    @CallSuper
    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.Dm = connectionResult.getErrorCode();
        this.Dn = System.currentTimeMillis();
    }

    @CallSuper
    protected void onConnectionSuspended(int i) {
        this.Dj = i;
        this.Dk = System.currentTimeMillis();
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
        this.Dl = System.currentTimeMillis();
    }

    public void zza(@NonNull zzf com_google_android_gms_common_internal_zze_zzf) {
        this.Dr = (zzf) zzaa.zzb((Object) com_google_android_gms_common_internal_zze_zzf, (Object) "Connection progress callbacks cannot be null.");
        zzb(2, null);
    }

    public void zza(zzf com_google_android_gms_common_internal_zze_zzf, ConnectionResult connectionResult) {
        this.Dr = (zzf) zzaa.zzb((Object) com_google_android_gms_common_internal_zze_zzf, (Object) "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.DA.get(), connectionResult.getErrorCode(), connectionResult.getResolution()));
    }

    @WorkerThread
    public void zza(zzp com_google_android_gms_common_internal_zzp, Set<Scope> set) {
        GetServiceRequest zzo = new GetServiceRequest(this.Dy).zzhv(this.mContext.getPackageName()).zzo(zzahv());
        if (set != null) {
            zzo.zzf(set);
        }
        if (zzain()) {
            zzo.zze(zzave()).zzb(com_google_android_gms_common_internal_zzp);
        } else if (zzavh()) {
            zzo.zze(getAccount());
        }
        try {
            synchronized (this.Dp) {
                if (this.Dq != null) {
                    this.Dq.zza(new zzg(this, this.DA.get()), zzo);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (DeadObjectException e) {
            Log.w("GmsClient", "service died");
            zzgk(1);
        } catch (Throwable e2) {
            Log.w("GmsClient", "Remote exception occurred", e2);
        } catch (SecurityException e3) {
            throw e3;
        } catch (Throwable e22) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e22);
            zzl(new ConnectionResult(8, null, "IGmsServiceBroker.getService failed."));
        }
    }

    protected Bundle zzahv() {
        return new Bundle();
    }

    public boolean zzain() {
        return false;
    }

    public boolean zzajc() {
        return false;
    }

    public Intent zzajd() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    public Bundle zzapn() {
        return null;
    }

    public boolean zzaqx() {
        return true;
    }

    @Nullable
    public IBinder zzaqy() {
        IBinder iBinder;
        synchronized (this.Dp) {
            if (this.Dq == null) {
                iBinder = null;
            } else {
                iBinder = this.Dq.asBinder();
            }
        }
        return iBinder;
    }

    protected String zzauz() {
        return "com.google.android.gms";
    }

    @Nullable
    protected final String zzava() {
        return this.Dz == null ? this.mContext.getClass().getName() : this.Dz;
    }

    public void zzavd() {
        int isGooglePlayServicesAvailable = this.zm.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zzb(1, null);
            this.Dr = new zzi(this);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.DA.get(), isGooglePlayServicesAvailable));
            return;
        }
        zza(new zzi(this));
    }

    public final Account zzave() {
        return getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
    }

    protected final void zzavf() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzavg() throws DeadObjectException {
        T t;
        synchronized (this.zzako) {
            if (this.Dv == 4) {
                throw new DeadObjectException();
            }
            zzavf();
            zzaa.zza(this.Ds != null, (Object) "Client is connected but service is null");
            t = this.Ds;
        }
        return t;
    }

    public boolean zzavh() {
        return false;
    }

    protected Set<Scope> zzavi() {
        return Collections.EMPTY_SET;
    }

    void zzc(int i, T t) {
    }

    public void zzgk(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.DA.get(), i));
    }

    @Nullable
    protected abstract T zzh(IBinder iBinder);

    @NonNull
    protected abstract String zzjx();

    @NonNull
    protected abstract String zzjy();
}
