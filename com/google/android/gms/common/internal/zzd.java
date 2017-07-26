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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zzd<T extends IInterface> {
    private static String[] zzaHc = new String[]{"service_esmobile", "service_googleme"};
    private final Context mContext;
    final Handler mHandler;
    private final Object mLock;
    private final zze zzaCF;
    private int zzaGH;
    private long zzaGI;
    private long zzaGJ;
    private int zzaGK;
    private long zzaGL;
    private zzak zzaGM;
    private final zzae zzaGN;
    private final Object zzaGO;
    private zzaw zzaGP;
    protected zzj zzaGQ;
    private T zzaGR;
    private final ArrayList<zzi<?>> zzaGS;
    private zzl zzaGT;
    private int zzaGU;
    private final zzf zzaGV;
    private final zzg zzaGW;
    private final int zzaGX;
    private final String zzaGY;
    private ConnectionResult zzaGZ;
    private boolean zzaHa;
    protected AtomicInteger zzaHb;
    private final Looper zzrM;

    protected zzd(Context context, Looper looper, int i, zzf com_google_android_gms_common_internal_zzf, zzg com_google_android_gms_common_internal_zzg, String str) {
        this(context, looper, zzae.zzaC(context), zze.zzoW(), i, (zzf) zzbo.zzu(com_google_android_gms_common_internal_zzf), (zzg) zzbo.zzu(com_google_android_gms_common_internal_zzg), null);
    }

    protected zzd(Context context, Looper looper, zzae com_google_android_gms_common_internal_zzae, zze com_google_android_gms_common_zze, int i, zzf com_google_android_gms_common_internal_zzf, zzg com_google_android_gms_common_internal_zzg, String str) {
        this.mLock = new Object();
        this.zzaGO = new Object();
        this.zzaGS = new ArrayList();
        this.zzaGU = 1;
        this.zzaGZ = null;
        this.zzaHa = false;
        this.zzaHb = new AtomicInteger(0);
        this.mContext = (Context) zzbo.zzb((Object) context, (Object) "Context must not be null");
        this.zzrM = (Looper) zzbo.zzb((Object) looper, (Object) "Looper must not be null");
        this.zzaGN = (zzae) zzbo.zzb((Object) com_google_android_gms_common_internal_zzae, (Object) "Supervisor must not be null");
        this.zzaCF = (zze) zzbo.zzb((Object) com_google_android_gms_common_zze, (Object) "API availability must not be null");
        this.mHandler = new zzh(this, looper);
        this.zzaGX = i;
        this.zzaGV = com_google_android_gms_common_internal_zzf;
        this.zzaGW = com_google_android_gms_common_internal_zzg;
        this.zzaGY = str;
    }

    private final void zza(int i, T t) {
        boolean z = true;
        if ((i == 4) != (t != null)) {
            z = false;
        }
        zzbo.zzaf(z);
        synchronized (this.mLock) {
            this.zzaGU = i;
            this.zzaGR = t;
            switch (i) {
                case 1:
                    if (this.zzaGT != null) {
                        this.zzaGN.zza(zzdb(), zzqZ(), this.zzaGT, zzra());
                        this.zzaGT = null;
                        break;
                    }
                    break;
                case 2:
                case 3:
                    String valueOf;
                    String valueOf2;
                    if (!(this.zzaGT == null || this.zzaGM == null)) {
                        valueOf = String.valueOf(this.zzaGM.zzrE());
                        valueOf2 = String.valueOf(this.zzaGM.getPackageName());
                        Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("Calling connect() while still connected, missing disconnect() for ").append(valueOf).append(" on ").append(valueOf2).toString());
                        this.zzaGN.zza(this.zzaGM.zzrE(), this.zzaGM.getPackageName(), this.zzaGT, zzra());
                        this.zzaHb.incrementAndGet();
                    }
                    this.zzaGT = new zzl(this, this.zzaHb.get());
                    this.zzaGM = new zzak(zzqZ(), zzdb(), false);
                    if (!this.zzaGN.zza(new zzaf(this.zzaGM.zzrE(), this.zzaGM.getPackageName()), this.zzaGT, zzra())) {
                        valueOf = String.valueOf(this.zzaGM.zzrE());
                        valueOf2 = String.valueOf(this.zzaGM.getPackageName());
                        Log.e("GmsClient", new StringBuilder((String.valueOf(valueOf).length() + 34) + String.valueOf(valueOf2).length()).append("unable to connect to service: ").append(valueOf).append(" on ").append(valueOf2).toString());
                        zza(16, null, this.zzaHb.get());
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
            if (this.zzaGU != i) {
                z = false;
            } else {
                zza(i2, (IInterface) t);
                z = true;
            }
        }
        return z;
    }

    private final void zzaz(int i) {
        int i2;
        if (zzrc()) {
            i2 = 5;
            this.zzaHa = true;
        } else {
            i2 = 4;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(i2, this.zzaHb.get(), 16));
    }

    @Nullable
    private final String zzra() {
        return this.zzaGY == null ? this.mContext.getClass().getName() : this.zzaGY;
    }

    private final boolean zzrc() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzaGU == 3;
        }
        return z;
    }

    private final boolean zzri() {
        if (this.zzaHa || TextUtils.isEmpty(zzdc()) || TextUtils.isEmpty(null)) {
            return false;
        }
        try {
            Class.forName(zzdc());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void disconnect() {
        this.zzaHb.incrementAndGet();
        synchronized (this.zzaGS) {
            int size = this.zzaGS.size();
            for (int i = 0; i < size; i++) {
                ((zzi) this.zzaGS.get(i)).removeListener();
            }
            this.zzaGS.clear();
        }
        synchronized (this.zzaGO) {
            this.zzaGP = null;
        }
        zza(1, null);
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            int i = this.zzaGU;
            IInterface iInterface = this.zzaGR;
        }
        synchronized (this.zzaGO) {
            zzaw com_google_android_gms_common_internal_zzaw = this.zzaGP;
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
            printWriter.append(zzdc()).append("@").append(Integer.toHexString(System.identityHashCode(iInterface.asBinder())));
        }
        printWriter.append(" mServiceBroker=");
        if (com_google_android_gms_common_internal_zzaw == null) {
            printWriter.println("null");
        } else {
            printWriter.append("IGmsServiceBroker@").println(Integer.toHexString(System.identityHashCode(com_google_android_gms_common_internal_zzaw.asBinder())));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        if (this.zzaGJ > 0) {
            PrintWriter append = printWriter.append(str).append("lastConnectedTime=");
            long j = this.zzaGJ;
            String valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaGJ)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaGI > 0) {
            printWriter.append(str).append("lastSuspendedCause=");
            switch (this.zzaGH) {
                case 1:
                    printWriter.append("CAUSE_SERVICE_DISCONNECTED");
                    break;
                case 2:
                    printWriter.append("CAUSE_NETWORK_LOST");
                    break;
                default:
                    printWriter.append(String.valueOf(this.zzaGH));
                    break;
            }
            append = printWriter.append(" lastSuspendedTime=");
            j = this.zzaGI;
            valueOf = String.valueOf(simpleDateFormat.format(new Date(this.zzaGI)));
            append.println(new StringBuilder(String.valueOf(valueOf).length() + 21).append(j).append(" ").append(valueOf).toString());
        }
        if (this.zzaGL > 0) {
            printWriter.append(str).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.zzaGK));
            append = printWriter.append(" lastFailedTime=");
            j = this.zzaGL;
            String valueOf2 = String.valueOf(simpleDateFormat.format(new Date(this.zzaGL)));
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
        return this.zzrM;
    }

    public final boolean isConnected() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzaGU == 4;
        }
        return z;
    }

    public final boolean isConnecting() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzaGU == 2 || this.zzaGU == 3;
        }
        return z;
    }

    @CallSuper
    protected void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzaGK = connectionResult.getErrorCode();
        this.zzaGL = System.currentTimeMillis();
    }

    @CallSuper
    protected final void onConnectionSuspended(int i) {
        this.zzaGH = i;
        this.zzaGI = System.currentTimeMillis();
    }

    protected final void zza(int i, @Nullable Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, i2, -1, new zzo(this, i, null)));
    }

    protected void zza(int i, IBinder iBinder, Bundle bundle, int i2) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, i2, -1, new zzn(this, i, iBinder, bundle)));
    }

    @CallSuper
    protected void zza(@NonNull T t) {
        this.zzaGJ = System.currentTimeMillis();
    }

    @WorkerThread
    public final void zza(zzal com_google_android_gms_common_internal_zzal, Set<Scope> set) {
        Throwable e;
        Bundle zzmo = zzmo();
        zzx com_google_android_gms_common_internal_zzx = new zzx(this.zzaGX);
        com_google_android_gms_common_internal_zzx.zzaHw = this.mContext.getPackageName();
        com_google_android_gms_common_internal_zzx.zzaHz = zzmo;
        if (set != null) {
            com_google_android_gms_common_internal_zzx.zzaHy = (Scope[]) set.toArray(new Scope[set.size()]);
        }
        if (zzmv()) {
            com_google_android_gms_common_internal_zzx.zzaHA = getAccount() != null ? getAccount() : new Account("<<default account>>", "com.google");
            if (com_google_android_gms_common_internal_zzal != null) {
                com_google_android_gms_common_internal_zzx.zzaHx = com_google_android_gms_common_internal_zzal.asBinder();
            }
        } else if (zzrg()) {
            com_google_android_gms_common_internal_zzx.zzaHA = getAccount();
        }
        com_google_android_gms_common_internal_zzx.zzaHB = zzrd();
        try {
            synchronized (this.zzaGO) {
                if (this.zzaGP != null) {
                    this.zzaGP.zza(new zzk(this, this.zzaHb.get()), com_google_android_gms_common_internal_zzx);
                } else {
                    Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                }
            }
        } catch (Throwable e2) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zzay(1);
        } catch (SecurityException e3) {
            throw e3;
        } catch (RemoteException e4) {
            e2 = e4;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzaHb.get());
        } catch (RuntimeException e5) {
            e2 = e5;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e2);
            zza(8, null, null, this.zzaHb.get());
        }
    }

    public void zza(@NonNull zzj com_google_android_gms_common_internal_zzj) {
        this.zzaGQ = (zzj) zzbo.zzb((Object) com_google_android_gms_common_internal_zzj, (Object) "Connection progress callbacks cannot be null.");
        zza(2, null);
    }

    protected final void zza(@NonNull zzj com_google_android_gms_common_internal_zzj, int i, @Nullable PendingIntent pendingIntent) {
        this.zzaGQ = (zzj) zzbo.zzb((Object) com_google_android_gms_common_internal_zzj, (Object) "Connection progress callbacks cannot be null.");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.zzaHb.get(), i, pendingIntent));
    }

    public final void zzay(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, this.zzaHb.get(), i));
    }

    @Nullable
    protected abstract T zzd(IBinder iBinder);

    @NonNull
    protected abstract String zzdb();

    @NonNull
    protected abstract String zzdc();

    public boolean zzmG() {
        return false;
    }

    public Intent zzmH() {
        throw new UnsupportedOperationException("Not a sign in API");
    }

    protected Bundle zzmo() {
        return new Bundle();
    }

    public boolean zzmv() {
        return false;
    }

    public Bundle zzoC() {
        return null;
    }

    public boolean zzpe() {
        return true;
    }

    protected String zzqZ() {
        return "com.google.android.gms";
    }

    public final void zzrb() {
        int isGooglePlayServicesAvailable = this.zzaCF.isGooglePlayServicesAvailable(this.mContext);
        if (isGooglePlayServicesAvailable != 0) {
            zza(1, null);
            zza(new zzm(this), isGooglePlayServicesAvailable, null);
            return;
        }
        zza(new zzm(this));
    }

    public zzc[] zzrd() {
        return new zzc[0];
    }

    protected final void zzre() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public final T zzrf() throws DeadObjectException {
        T t;
        synchronized (this.mLock) {
            if (this.zzaGU == 5) {
                throw new DeadObjectException();
            }
            zzre();
            zzbo.zza(this.zzaGR != null, (Object) "Client is connected but service is null");
            t = this.zzaGR;
        }
        return t;
    }

    public boolean zzrg() {
        return false;
    }

    protected Set<Scope> zzrh() {
        return Collections.EMPTY_SET;
    }
}
