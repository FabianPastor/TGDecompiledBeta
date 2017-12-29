package com.google.firebase.iid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.stats.zza;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class zzk implements ServiceConnection {
    int state;
    final Messenger zznzb;
    zzp zznzc;
    final Queue<zzr<?>> zznzd;
    final SparseArray<zzr<?>> zznze;
    final /* synthetic */ zzi zznzf;

    private zzk(zzi com_google_firebase_iid_zzi) {
        this.zznzf = com_google_firebase_iid_zzi;
        this.state = 0;
        this.zznzb = new Messenger(new Handler(Looper.getMainLooper(), new zzl(this)));
        this.zznzd = new ArrayDeque();
        this.zznze = new SparseArray();
    }

    private final void zza(zzs com_google_firebase_iid_zzs) {
        for (zzr zzb : this.zznzd) {
            zzb.zzb(com_google_firebase_iid_zzs);
        }
        this.zznzd.clear();
        for (int i = 0; i < this.zznze.size(); i++) {
            ((zzr) this.zznze.valueAt(i)).zzb(com_google_firebase_iid_zzs);
        }
        this.zznze.clear();
    }

    private final void zzcjb() {
        this.zznzf.zznyy.execute(new zzn(this));
    }

    public final synchronized void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (Log.isLoggable("MessengerIpcClient", 2)) {
            Log.v("MessengerIpcClient", "Service connected");
        }
        if (iBinder == null) {
            zzm(0, "Null service connection");
        } else {
            try {
                this.zznzc = new zzp(iBinder);
                this.state = 2;
                zzcjb();
            } catch (RemoteException e) {
                zzm(0, e.getMessage());
            }
        }
    }

    public final synchronized void onServiceDisconnected(ComponentName componentName) {
        if (Log.isLoggable("MessengerIpcClient", 2)) {
            Log.v("MessengerIpcClient", "Service disconnected");
        }
        zzm(2, "Service disconnected");
    }

    final synchronized boolean zzb(zzr com_google_firebase_iid_zzr) {
        boolean z = false;
        boolean z2 = true;
        synchronized (this) {
            switch (this.state) {
                case 0:
                    this.zznzd.add(com_google_firebase_iid_zzr);
                    if (this.state == 0) {
                        z = true;
                    }
                    zzbq.checkState(z);
                    if (Log.isLoggable("MessengerIpcClient", 2)) {
                        Log.v("MessengerIpcClient", "Starting bind to GmsCore");
                    }
                    this.state = 1;
                    Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
                    intent.setPackage("com.google.android.gms");
                    if (!zza.zzamc().zza(this.zznzf.zzair, intent, this, 1)) {
                        zzm(0, "Unable to bind to service");
                        break;
                    }
                    this.zznzf.zznyy.schedule(new zzm(this), 30, TimeUnit.SECONDS);
                    break;
                case 1:
                    this.zznzd.add(com_google_firebase_iid_zzr);
                    break;
                case 2:
                    this.zznzd.add(com_google_firebase_iid_zzr);
                    zzcjb();
                    break;
                case 3:
                case 4:
                    z2 = false;
                    break;
                default:
                    throw new IllegalStateException("Unknown state: " + this.state);
            }
        }
        return z2;
    }

    final synchronized void zzcjc() {
        if (this.state == 2 && this.zznzd.isEmpty() && this.zznze.size() == 0) {
            if (Log.isLoggable("MessengerIpcClient", 2)) {
                Log.v("MessengerIpcClient", "Finished handling requests, unbinding");
            }
            this.state = 3;
            zza.zzamc();
            this.zznzf.zzair.unbindService(this);
        }
    }

    final synchronized void zzcjd() {
        if (this.state == 1) {
            zzm(1, "Timed out while binding");
        }
    }

    final boolean zzd(Message message) {
        int i = message.arg1;
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            Log.d("MessengerIpcClient", "Received response to request: " + i);
        }
        synchronized (this) {
            zzr com_google_firebase_iid_zzr = (zzr) this.zznze.get(i);
            if (com_google_firebase_iid_zzr == null) {
                Log.w("MessengerIpcClient", "Received response for unknown request: " + i);
            } else {
                this.zznze.remove(i);
                zzcjc();
                Bundle data = message.getData();
                if (data.getBoolean("unsupported", false)) {
                    com_google_firebase_iid_zzr.zzb(new zzs(4, "Not supported by GmsCore"));
                } else {
                    com_google_firebase_iid_zzr.zzac(data);
                }
            }
        }
        return true;
    }

    final synchronized void zzic(int i) {
        zzr com_google_firebase_iid_zzr = (zzr) this.zznze.get(i);
        if (com_google_firebase_iid_zzr != null) {
            Log.w("MessengerIpcClient", "Timing out request: " + i);
            this.zznze.remove(i);
            com_google_firebase_iid_zzr.zzb(new zzs(3, "Timed out waiting for response"));
            zzcjc();
        }
    }

    final synchronized void zzm(int i, String str) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String str2 = "MessengerIpcClient";
            String str3 = "Disconnected: ";
            String valueOf = String.valueOf(str);
            Log.d(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        }
        switch (this.state) {
            case 0:
                throw new IllegalStateException();
            case 1:
            case 2:
                if (Log.isLoggable("MessengerIpcClient", 2)) {
                    Log.v("MessengerIpcClient", "Unbinding service");
                }
                this.state = 4;
                zza.zzamc();
                this.zznzf.zzair.unbindService(this);
                zza(new zzs(i, str));
                break;
            case 3:
                this.state = 4;
                break;
            case 4:
                break;
            default:
                throw new IllegalStateException("Unknown state: " + this.state);
        }
    }
}
