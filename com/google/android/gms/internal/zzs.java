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

public final class zzs {
    private AtomicInteger zzW;
    private final Map<String, Queue<zzp<?>>> zzX;
    private final Set<zzp<?>> zzY;
    private final PriorityBlockingQueue<zzp<?>> zzZ;
    private final PriorityBlockingQueue<zzp<?>> zzaa;
    private zzl[] zzab;
    private zzd zzac;
    private List<Object> zzad;
    private final zzb zzi;
    private final zzw zzj;
    private final zzk zzx;

    public zzs(zzb com_google_android_gms_internal_zzb, zzk com_google_android_gms_internal_zzk) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzk, 4);
    }

    private zzs(zzb com_google_android_gms_internal_zzb, zzk com_google_android_gms_internal_zzk, int i) {
        this(com_google_android_gms_internal_zzb, com_google_android_gms_internal_zzk, 4, new zzh(new Handler(Looper.getMainLooper())));
    }

    private zzs(zzb com_google_android_gms_internal_zzb, zzk com_google_android_gms_internal_zzk, int i, zzw com_google_android_gms_internal_zzw) {
        this.zzW = new AtomicInteger();
        this.zzX = new HashMap();
        this.zzY = new HashSet();
        this.zzZ = new PriorityBlockingQueue();
        this.zzaa = new PriorityBlockingQueue();
        this.zzad = new ArrayList();
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzx = com_google_android_gms_internal_zzk;
        this.zzab = new zzl[4];
        this.zzj = com_google_android_gms_internal_zzw;
    }

    public final void start() {
        int i = 0;
        if (this.zzac != null) {
            this.zzac.quit();
        }
        for (int i2 = 0; i2 < this.zzab.length; i2++) {
            if (this.zzab[i2] != null) {
                this.zzab[i2].quit();
            }
        }
        this.zzac = new zzd(this.zzZ, this.zzaa, this.zzi, this.zzj);
        this.zzac.start();
        while (i < this.zzab.length) {
            zzl com_google_android_gms_internal_zzl = new zzl(this.zzaa, this.zzx, this.zzi, this.zzj);
            this.zzab[i] = com_google_android_gms_internal_zzl;
            com_google_android_gms_internal_zzl.start();
            i++;
        }
    }

    public final <T> zzp<T> zzc(zzp<T> com_google_android_gms_internal_zzp_T) {
        com_google_android_gms_internal_zzp_T.zza(this);
        synchronized (this.zzY) {
            this.zzY.add(com_google_android_gms_internal_zzp_T);
        }
        com_google_android_gms_internal_zzp_T.zza(this.zzW.incrementAndGet());
        com_google_android_gms_internal_zzp_T.zzb("add-to-queue");
        if (com_google_android_gms_internal_zzp_T.zzh()) {
            synchronized (this.zzX) {
                String zzd = com_google_android_gms_internal_zzp_T.zzd();
                if (this.zzX.containsKey(zzd)) {
                    Queue queue = (Queue) this.zzX.get(zzd);
                    if (queue == null) {
                        queue = new LinkedList();
                    }
                    queue.add(com_google_android_gms_internal_zzp_T);
                    this.zzX.put(zzd, queue);
                    if (zzab.DEBUG) {
                        zzab.zza("Request for cacheKey=%s is in flight, putting on hold.", zzd);
                    }
                } else {
                    this.zzX.put(zzd, null);
                    this.zzZ.add(com_google_android_gms_internal_zzp_T);
                }
            }
        } else {
            this.zzaa.add(com_google_android_gms_internal_zzp_T);
        }
        return com_google_android_gms_internal_zzp_T;
    }

    final <T> void zzd(zzp<T> com_google_android_gms_internal_zzp_T) {
        synchronized (this.zzY) {
            this.zzY.remove(com_google_android_gms_internal_zzp_T);
        }
        synchronized (this.zzad) {
            Iterator it = this.zzad.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
        if (com_google_android_gms_internal_zzp_T.zzh()) {
            synchronized (this.zzX) {
                Queue queue = (Queue) this.zzX.remove(com_google_android_gms_internal_zzp_T.zzd());
                if (queue != null) {
                    if (zzab.DEBUG) {
                        zzab.zza("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(queue.size()), r2);
                    }
                    this.zzZ.addAll(queue);
                }
            }
        }
    }
}
