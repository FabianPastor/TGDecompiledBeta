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

public abstract class zzf<T extends IInterface> {
    public static final String[] zzaDT = new String[]{"service_esmobile", "service_googleme"};
    private final Context mContext;
    final Handler mHandler;
    private int zzaDB;
    private long zzaDC;
    private long zzaDD;
    private int zzaDE;
    private long zzaDF;
    private final zzn zzaDG;
    private final Object zzaDH;
    private zzv zzaDI;
    protected zzf zzaDJ;
    private T zzaDK;
    private final ArrayList<zze<?>> zzaDL;
    private zzh zzaDM;
    private int zzaDN;
    private final zzb zzaDO;
    private final zzc zzaDP;
    private final int zzaDQ;
    private final String zzaDR;
    protected AtomicInteger zzaDS;
    private final com.google.android.gms.common.zzc zzazw;
    private final Object zzrN;
    private final Looper zzrx;

    public interface zzb {
        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface zzc {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    final class zzd extends Handler {
        final /* synthetic */ zzf zzaDV;

        public zzd(zzf com_google_android_gms_common_internal_zzf, Looper looper) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
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
            if (this.zzaDV.zzaDS.get() != message.arg1) {
                if (zzb(message)) {
                    zza(message);
                }
            } else if ((message.what == 1 || message.what == 5) && !this.zzaDV.isConnecting()) {
                zza(message);
            } else if (message.what == 3) {
                if (message.obj instanceof PendingIntent) {
                    pendingIntent = (PendingIntent) message.obj;
                }
                ConnectionResult connectionResult = new ConnectionResult(message.arg2, pendingIntent);
                this.zzaDV.zzaDJ.zzg(connectionResult);
                this.zzaDV.onConnectionFailed(connectionResult);
            } else if (message.what == 4) {
                this.zzaDV.zza(4, null);
                if (this.zzaDV.zzaDO != null) {
                    this.zzaDV.zzaDO.onConnectionSuspended(message.arg2);
                }
                this.zzaDV.onConnectionSuspended(message.arg2);
                this.zzaDV.zza(4, 1, null);
            } else if (message.what == 2 && !this.zzaDV.isConnected()) {
                zza(message);
            } else if (zzb(message)) {
                ((zze) message.obj).zzxa();
            } else {
                Log.wtf("GmsClient", "Don't know how to handle message: " + message.what, new Exception());
            }
        }
    }

    protected abstract class zze<TListener> {
        private TListener mListener;
        final /* synthetic */ zzf zzaDV;
        private boolean zzaDW = false;

        public zze(zzf com_google_android_gms_common_internal_zzf, TListener tListener) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
            this.mListener = tListener;
        }

        public void unregister() {
            zzxb();
            synchronized (this.zzaDV.zzaDL) {
                this.zzaDV.zzaDL.remove(this);
            }
        }

        protected abstract void zzu(TListener tListener);

        public void zzxa() {
            synchronized (this) {
                Object obj = this.mListener;
                if (this.zzaDW) {
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
                this.zzaDW = true;
            }
            unregister();
        }

        public void zzxb() {
            synchronized (this) {
                this.mListener = null;
            }
        }
    }

    public interface zzf {
        void zzg(@NonNull ConnectionResult connectionResult);
    }

    public final class zzh implements ServiceConnection {
        final /* synthetic */ zzf zzaDV;
        private final int zzaDY;

        public zzh(zzf com_google_android_gms_common_internal_zzf, int i) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
            this.zzaDY = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null) {
                this.zzaDV.zzm(new ConnectionResult(8, null, "ServiceBroker IBinder is null"));
                return;
            }
            synchronized (this.zzaDV.zzaDH) {
                this.zzaDV.zzaDI = com.google.android.gms.common.internal.zzv.zza.zzbu(iBinder);
            }
            this.zzaDV.zza(0, null, this.zzaDY);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (this.zzaDV.zzaDH) {
                this.zzaDV.zzaDI = null;
            }
            this.zzaDV.mHandler.sendMessage(this.zzaDV.mHandler.obtainMessage(4, this.zzaDY, 1));
        }
    }

    private abstract class zza extends zze<Boolean> {
        public final int statusCode;
        public final Bundle zzaDU;
        final /* synthetic */ zzf zzaDV;

        @BinderThread
        protected zza(zzf com_google_android_gms_common_internal_zzf, int i, Bundle bundle) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, Boolean.valueOf(true));
            this.statusCode = i;
            this.zzaDU = bundle;
        }

        protected void zzc(Boolean bool) {
            PendingIntent pendingIntent = null;
            if (bool == null) {
                this.zzaDV.zza(1, null);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    if (!zzwZ()) {
                        this.zzaDV.zza(1, null);
                        zzn(new ConnectionResult(8, null));
                        return;
                    }
                    return;
                case 10:
                    this.zzaDV.zza(1, null);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    this.zzaDV.zza(1, null);
                    if (this.zzaDU != null) {
                        pendingIntent = (PendingIntent) this.zzaDU.getParcelable("pendingIntent");
                    }
                    zzn(new ConnectionResult(this.statusCode, pendingIntent));
                    return;
            }
        }

        protected abstract void zzn(ConnectionResult connectionResult);

        protected /* synthetic */ void zzu(Object obj) {
            zzc((Boolean) obj);
        }

        protected abstract boolean zzwZ();
    }

    protected class zzi implements zzf {
        final /* synthetic */ zzf zzaDV;

        public zzi(zzf com_google_android_gms_common_internal_zzf) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.zzaDV.zza(null, this.zzaDV.zzwY());
            } else if (this.zzaDV.zzaDP != null) {
                this.zzaDV.zzaDP.onConnectionFailed(connectionResult);
            }
        }
    }

    public static final class zzg extends com.google.android.gms.common.internal.zzu.zza {
        private zzf zzaDX;
        private final int zzaDY;

        public zzg(@NonNull zzf com_google_android_gms_common_internal_zzf, int i) {
            this.zzaDX = com_google_android_gms_common_internal_zzf;
            this.zzaDY = i;
        }

        private void zzxc() {
            this.zzaDX = null;
        }

        @BinderThread
        public void zza(int i, @NonNull IBinder iBinder, @Nullable Bundle bundle) {
            zzac.zzb(this.zzaDX, (Object) "onPostInitComplete can be called only once per call to getRemoteService");
            this.zzaDX.zza(i, iBinder, bundle, this.zzaDY);
            zzxc();
        }

        @BinderThread
        public void zzb(int i, @Nullable Bundle bundle) {
            Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
        }
    }

    protected final class zzj extends zza {
        final /* synthetic */ zzf zzaDV;
        public final IBinder zzaDZ;

        @BinderThread
        public zzj(zzf com_google_android_gms_common_internal_zzf, int i, IBinder iBinder, Bundle bundle) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, i, bundle);
            this.zzaDZ = iBinder;
        }

        protected void zzn(ConnectionResult connectionResult) {
            if (this.zzaDV.zzaDP != null) {
                this.zzaDV.zzaDP.onConnectionFailed(connectionResult);
            }
            this.zzaDV.onConnectionFailed(connectionResult);
        }

        protected boolean zzwZ() {
            try {
                String interfaceDescriptor = this.zzaDZ.getInterfaceDescriptor();
                if (this.zzaDV.zzev().equals(interfaceDescriptor)) {
                    IInterface zzh = this.zzaDV.zzh(this.zzaDZ);
                    if (zzh == null || !this.zzaDV.zza(2, 3, zzh)) {
                        return false;
                    }
                    Bundle zzud = this.zzaDV.zzud();
                    if (this.zzaDV.zzaDO != null) {
                        this.zzaDV.zzaDO.onConnected(zzud);
                    }
                    return true;
                }
                String valueOf = String.valueOf(this.zzaDV.zzev());
                Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(interfaceDescriptor).length()).append("service descriptor mismatch: ").append(valueOf).append(" vs. ").append(interfaceDescriptor).toString());
                return false;
            } catch (RemoteException e) {
                Log.w("GmsClient", "service probably died");
                return false;
            }
        }
    }

    protected final class zzk extends zza {
        final /* synthetic */ zzf zzaDV;

        @BinderThread
        public zzk(zzf com_google_android_gms_common_internal_zzf, int i, @Nullable Bundle bundle) {
            this.zzaDV = com_google_android_gms_common_internal_zzf;
            super(com_google_android_gms_common_internal_zzf, i, bundle);
        }

        protected void zzn(ConnectionResult connectionResult) {
            this.zzaDV.zzaDJ.zzg(connectionResult);
            this.zzaDV.onConnectionFailed(connectionResult);
        }

        protected boolean zzwZ() {
            this.zzaDV.zzaDJ.zzg(ConnectionResult.zzawX);
            return true;
        }
    }

    protected zzf(Context context, Looper looper, int i, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc, String str) {
        this(context, looper, zzn.zzaC(context), com.google.android.gms.common.zzc.zzuz(), i, (zzb) zzac.zzw(com_google_android_gms_common_internal_zzf_zzb), (zzc) zzac.zzw(com_google_android_gms_common_internal_zzf_zzc), str);
    }

    protected zzf(Context context, Looper looper, zzn com_google_android_gms_common_internal_zzn, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, int i, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc, String str) {
        this.zzrN = new Object();
        this.zzaDH = new Object();
        this.zzaDL = new ArrayList();
        this.zzaDN = 1;
        this.zzaDS = new AtomicInteger(0);
        this.mContext = (Context) zzac.zzb((Object) context, (Object) "Context must not be null");
        this.zzrx = (Looper) zzac.zzb((Object) looper, (Object) "Looper must not be null");
        this.zzaDG = (zzn) zzac.zzb((Object) com_google_android_gms_common_internal_zzn, (Object) "Supervisor must not be null");
        this.zzazw = (com.google.android.gms.common.zzc) zzac.zzb((Object) com_google_android_gms_common_zzc, (Object) "API availability must not be null");
        this.mHandler = new zzd(this, looper);
        this.zzaDQ = i;
        this.zzaDO = com_google_android_gms_common_internal_zzf_zzb;
        this.zzaDP = com_google_android_gms_common_internal_zzf_zzc;
        this.zzaDR = str;
    }

    private void zza(int i, T t) {
        boolean z = true;
        if ((i == 3) != (t != null)) {
            z = false;
        }
        zzac.zzas(z);
        synchronized (this.zzrN) {
            this.zzaDN = i;
            this.zzaDK = t;
            switch (i) {
                case 1:
                    zzwS();
                    break;
                case 2:
                    zzwR();
                    break;
                case 3:
                    zza((IInterface) t);
                    break;
            }
        }
    }

    private boolean zza(int i, int i2, T t) {
        boolean z;
        synchronized (this.zzrN) {
            if (this.zzaDN != i) {
                z = false;
            } else {
                zza(i2, (IInterface) t);
                z = true;
            }
        }
        return z;
    }

    private void zzm(ConnectionResult connectionResult) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzaDS.get(), connectionResult.getErrorCode(), connectionResult.getResolution()));
    }

    private void zzwR() {
        if (this.zzaDM != null) {
            String valueOf = String.valueOf(zzeu());
            String valueOf2 = String.valueOf(zzwP());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
            this.zzaDG.zzb(zzeu(), zzwP(), this.zzaDM, zzwQ());
            this.zzaDS.incrementAndGet();
        }
        this.zzaDM = new zzh(this, this.zzaDS.get());
        if (!this.zzaDG.zza(zzeu(), zzwP(), this.zzaDM, zzwQ())) {
            valueOf = String.valueOf(zzeu());
            valueOf2 = String.valueOf(zzwP());
            Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(valueOf2).length()).append("unable to connect to service: ").append(valueOf).append(" on ").append(valueOf2).toString());
            zza(16, null, this.zzaDS.get());
        }
    }

    private void zzwS() {
        if (this.zzaDM != null) {
            this.zzaDG.zzb(zzeu(), zzwP(), this.zzaDM, zzwQ());
            this.zzaDM = null;
        }
    }

    public void disconnect() {
        this.zzaDS.incrementAndGet();
        synchronized (this.zzaDL) {
            int size = this.zzaDL.size();
            for (int i = 0; i < size; i++) {
                ((zze) this.zzaDL.get(i)).zzxb();
            }
            this.zzaDL.clear();
        }
        synchronized (this.zzaDH) {
            this.zzaDI = null;
        }
        zza(1, null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.zzrN) {
            int i = this.zzaDN;
            IInterface iInterface = this.zzaDK;
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
            printWriter.append(zzev()).append("@").println(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.zzaDD > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.zzaDD;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaDD)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaDC > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.zzaDB) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.zzaDB));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.zzaDC;
            valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaDC)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaDF > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.zzaDE));
            append = printWriter.append(" lastFailedTime=");
            j = this.zzaDF;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.zzaDF)));
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
        return this.zzrx;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.zzrN) {
            z = this.zzaDN == 3;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.zzrN) {
            z = this.zzaDN == 2;
        }
        return z;
    }

    @CallSuper
    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzaDE = connectionResult.getErrorCode();
        this.zzaDF = System.currentTimeMillis();
    }

    @CallSuper
    protected void onConnectionSuspended(int i) {
        this.zzaDB = i;
        this.zzaDC = System.currentTimeMillis();
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
        this.zzaDD = System.currentTimeMillis();
    }

    public void zza(@NonNull zzf com_google_android_gms_common_internal_zzf_zzf) {
        this.zzaDJ = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zzf_zzf, (Object) "Connection progress callbacks cannot be null.");
        zza(2, null);
    }

    public void zza(zzf com_google_android_gms_common_internal_zzf_zzf, ConnectionResult connectionResult) {
        this.zzaDJ = (zzf) zzac.zzb((Object) com_google_android_gms_common_internal_zzf_zzf, (Object) "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzaDS.get(), connectionResult.getErrorCode(), connectionResult.getResolution()));
    }

    @WorkerThread
    public void zza(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set) {
        zzj zzp = new zzj(this.zzaDQ).zzdq(this.mContext.getPackageName()).zzp(zzql());
        if (set != null) {
            zzp.zzf(set);
        }
        if (zzqD()) {
            zzp.zze(zzwU()).zzb(com_google_android_gms_common_internal_zzr);
        } else if (zzwX()) {
            zzp.zze(getAccount());
        }
        try {
            synchronized (this.zzaDH) {
                if (this.zzaDI != null) {
                    this.zzaDI.zza(new zzg(this, this.zzaDS.get()), zzp);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (DeadObjectException e) {
            Log.w("GmsClient", "service died");
            zzcM(1);
        } catch (Throwable e2) {
            Log.w("GmsClient", "Remote exception occurred", e2);
        } catch (SecurityException e3) {
            throw e3;
        } catch (Throwable e22) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e22);
            zzm(new ConnectionResult(8, null, "IGmsServiceBroker.getService failed."));
        }
    }

    public void zzcM(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.zzaDS.get(), i));
    }

    @NonNull
    protected abstract String zzeu();

    @NonNull
    protected abstract String zzev();

    @Nullable
    protected abstract T zzh(IBinder iBinder);

    public boolean zzqD() {
        return false;
    }

    public boolean zzqS() {
        return false;
    }

    public Intent zzqT() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    protected Bundle zzql() {
        return new Bundle();
    }

    public boolean zzuI() {
        return true;
    }

    @Nullable
    public IBinder zzuJ() {
        IBinder iBinder;
        synchronized (this.zzaDH) {
            if (this.zzaDI == null) {
                iBinder = null;
            } else {
                iBinder = this.zzaDI.asBinder();
            }
        }
        return iBinder;
    }

    public Bundle zzud() {
        return null;
    }

    protected String zzwP() {
        return "com.google.android.gms";
    }

    @Nullable
    protected final String zzwQ() {
        return this.zzaDR == null ? this.mContext.getClass().getName() : this.zzaDR;
    }

    public void zzwT() {
        int isGooglePlayServicesAvailable = this.zzazw.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zza(1, null);
            this.zzaDJ = new zzi(this);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzaDS.get(), isGooglePlayServicesAvailable));
            return;
        }
        zza(new zzi(this));
    }

    public final Account zzwU() {
        return getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
    }

    protected final void zzwV() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzwW() throws DeadObjectException {
        T t;
        synchronized (this.zzrN) {
            if (this.zzaDN == 4) {
                throw new DeadObjectException();
            }
            zzwV();
            zzac.zza(this.zzaDK != null, (Object) "Client is connected but service is null");
            t = this.zzaDK;
        }
        return t;
    }

    public boolean zzwX() {
        return false;
    }

    protected Set<Scope> zzwY() {
        return Collections.EMPTY_SET;
    }
}
