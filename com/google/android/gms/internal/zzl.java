package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class zzl {
    private AtomicInteger zzax;
    private final Map<String, Queue<zzk<?>>> zzay;
    private final Set<zzk<?>> zzaz;
    private final PriorityBlockingQueue<zzk<?>> zzba;
    private final PriorityBlockingQueue<zzk<?>> zzbb;
    private zzg[] zzbc;
    private zzc zzbd;
    private List<zza> zzbe;
    private final zzb zzi;
    private final zzn zzj;
    private final zzf zzy;

    public interface zza<T> {
        void zzg(zzk<T> com_google_android_gms_internal_zzk_T);
    }

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzf, 4);
    }

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf, int i) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzf, i, new zze(new Handler(Looper.getMainLooper())));
    }

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf, int i, zzn com_google_android_gms_internal_zzn) {
        this.zzax = new AtomicInteger();
        this.zzay = new HashMap();
        this.zzaz = new HashSet();
        this.zzba = new PriorityBlockingQueue();
        this.zzbb = new PriorityBlockingQueue();
        this.zzbe = new ArrayList();
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzy = com_google_android_gms_internal_zzf;
        this.zzbc = new zzg[i];
        this.zzj = com_google_android_gms_internal_zzn;
    }

    public int getSequenceNumber() {
        return this.zzax.incrementAndGet();
    }

    public void start() {
        stop();
        this.zzbd = new zzc(this.zzba, this.zzbb, this.zzi, this.zzj);
        this.zzbd.start();
        for (int i = 0; i < this.zzbc.length; i++) {
            zzg com_google_android_gms_internal_zzg = new zzg(this.zzbb, this.zzy, this.zzi, this.zzj);
            this.zzbc[i] = com_google_android_gms_internal_zzg;
            com_google_android_gms_internal_zzg.start();
        }
    }

    public void stop() {
        if (this.zzbd != null) {
            this.zzbd.quit();
        }
        for (int i = 0; i < this.zzbc.length; i++) {
            if (this.zzbc[i] != null) {
                this.zzbc[i].quit();
            }
        }
    }

    public <T> zzk<T> zze(zzk<T> com_google_android_gms_internal_zzk_T) {
        com_google_android_gms_internal_zzk_T.zza(this);
        synchronized (this.zzaz) {
            this.zzaz.add(com_google_android_gms_internal_zzk_T);
        }
        com_google_android_gms_internal_zzk_T.zza(getSequenceNumber());
        com_google_android_gms_internal_zzk_T.zzc("add-to-queue");
        if (com_google_android_gms_internal_zzk_T.zzq()) {
            synchronized (this.zzay) {
                String zzg = com_google_android_gms_internal_zzk_T.zzg();
                if (this.zzay.containsKey(zzg)) {
                    Queue queue = (Queue) this.zzay.get(zzg);
                    if (queue == null) {
                        queue = new LinkedList();
                    }
                    queue.add(com_google_android_gms_internal_zzk_T);
                    this.zzay.put(zzg, queue);
                    if (zzs.DEBUG) {
                        zzs.zza("Request for cacheKey=%s is in flight, putting on hold.", zzg);
                    }
                } else {
                    this.zzay.put(zzg, null);
                    this.zzba.add(com_google_android_gms_internal_zzk_T);
                }
            }
        } else {
            this.zzbb.add(com_google_android_gms_internal_zzk_T);
        }
        return com_google_android_gms_internal_zzk_T;
    }

    <T> void zzf(zzk<T> com_google_android_gms_internal_zzk_T) {
        synchronized (this.zzaz) {
            this.zzaz.remove(com_google_android_gms_internal_zzk_T);
        }
        synchronized (this.zzbe) {
            for (zza zzg : this.zzbe) {
                zzg.zzg(com_google_android_gms_internal_zzk_T);
            }
        }
        if (com_google_android_gms_internal_zzk_T.zzq()) {
            synchronized (this.zzay) {
                Queue queue = (Queue) this.zzay.remove(com_google_android_gms_internal_zzk_T.zzg());
                if (queue != null) {
                    if (zzs.DEBUG) {
                        zzs.zza("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(queue.size()), r2);
                    }
                    this.zzba.addAll(queue);
                }
            }
        }
    }
}
