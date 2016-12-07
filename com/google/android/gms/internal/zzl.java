package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class zzl {
    private AtomicInteger zzW;
    private final Map<String, Queue<zzk<?>>> zzX;
    private final Set<zzk<?>> zzY;
    private final PriorityBlockingQueue<zzk<?>> zzZ;
    private final PriorityBlockingQueue<zzk<?>> zzaa;
    private zzg[] zzab;
    private zzc zzac;
    private List<Object> zzad;
    private final zzb zzi;
    private final zzn zzj;
    private final zzf zzx;

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzf, 4);
    }

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf, int i) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzf, i, new zze(new Handler(Looper.getMainLooper())));
    }

    public zzl(zzb com_google_android_gms_internal_zzb, zzf com_google_android_gms_internal_zzf, int i, zzn com_google_android_gms_internal_zzn) {
        this.zzW = new AtomicInteger();
        this.zzX = new HashMap();
        this.zzY = new HashSet();
        this.zzZ = new PriorityBlockingQueue();
        this.zzaa = new PriorityBlockingQueue();
        this.zzad = new ArrayList();
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzx = com_google_android_gms_internal_zzf;
        this.zzab = new zzg[i];
        this.zzj = com_google_android_gms_internal_zzn;
    }

    public int getSequenceNumber() {
        return this.zzW.incrementAndGet();
    }

    public void start() {
        stop();
        this.zzac = new zzc(this.zzZ, this.zzaa, this.zzi, this.zzj);
        this.zzac.start();
        for (int i = 0; i < this.zzab.length; i++) {
            zzg com_google_android_gms_internal_zzg = new zzg(this.zzaa, this.zzx, this.zzi, this.zzj);
            this.zzab[i] = com_google_android_gms_internal_zzg;
            com_google_android_gms_internal_zzg.start();
        }
    }

    public void stop() {
        if (this.zzac != null) {
            this.zzac.quit();
        }
        for (int i = 0; i < this.zzab.length; i++) {
            if (this.zzab[i] != null) {
                this.zzab[i].quit();
            }
        }
    }

    public <T> zzk<T> zze(zzk<T> com_google_android_gms_internal_zzk_T) {
        com_google_android_gms_internal_zzk_T.zza(this);
        synchronized (this.zzY) {
            this.zzY.add(com_google_android_gms_internal_zzk_T);
        }
        com_google_android_gms_internal_zzk_T.zza(getSequenceNumber());
        com_google_android_gms_internal_zzk_T.zzc("add-to-queue");
        if (com_google_android_gms_internal_zzk_T.zzn()) {
            synchronized (this.zzX) {
                String zzg = com_google_android_gms_internal_zzk_T.zzg();
                if (this.zzX.containsKey(zzg)) {
                    Queue queue = (Queue) this.zzX.get(zzg);
                    if (queue == null) {
                        queue = new LinkedList();
                    }
                    queue.add(com_google_android_gms_internal_zzk_T);
                    this.zzX.put(zzg, queue);
                    if (zzs.DEBUG) {
                        zzs.zza("Request for cacheKey=%s is in flight, putting on hold.", zzg);
                    }
                } else {
                    this.zzX.put(zzg, null);
                    this.zzZ.add(com_google_android_gms_internal_zzk_T);
                }
            }
        } else {
            this.zzaa.add(com_google_android_gms_internal_zzk_T);
        }
        return com_google_android_gms_internal_zzk_T;
    }

    <T> void zzf(zzk<T> com_google_android_gms_internal_zzk_T) {
        synchronized (this.zzY) {
            this.zzY.remove(com_google_android_gms_internal_zzk_T);
        }
        synchronized (this.zzad) {
            Iterator it = this.zzad.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
        if (com_google_android_gms_internal_zzk_T.zzn()) {
            synchronized (this.zzX) {
                Queue queue = (Queue) this.zzX.remove(com_google_android_gms_internal_zzk_T.zzg());
                if (queue != null) {
                    if (zzs.DEBUG) {
                        zzs.zza("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(queue.size()), r2);
                    }
                    this.zzZ.addAll(queue);
                }
            }
        }
    }
}
