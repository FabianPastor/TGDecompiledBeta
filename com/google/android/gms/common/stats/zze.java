package com.google.android.gms.common.stats;

import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public class zze {
    private final long Ev;
    private final int Ew;
    private final SimpleArrayMap<String, Long> Ex;

    public zze() {
        this.Ev = HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS;
        this.Ew = 10;
        this.Ex = new SimpleArrayMap(10);
    }

    public zze(int i, long j) {
        this.Ev = j;
        this.Ew = i;
        this.Ex = new SimpleArrayMap();
    }

    private void zze(long j, long j2) {
        for (int size = this.Ex.size() - 1; size >= 0; size--) {
            if (j2 - ((Long) this.Ex.valueAt(size)).longValue() > j) {
                this.Ex.removeAt(size);
            }
        }
    }

    public Long zzif(String str) {
        Long l;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.Ev;
        synchronized (this) {
            while (this.Ex.size() >= this.Ew) {
                zze(j, elapsedRealtime);
                j /= 2;
                Log.w("ConnectionTracker", "The max capacity " + this.Ew + " is not enough. Current durationThreshold is: " + j);
            }
            l = (Long) this.Ex.put(str, Long.valueOf(elapsedRealtime));
        }
        return l;
    }

    public boolean zzig(String str) {
        boolean z;
        synchronized (this) {
            z = this.Ex.remove(str) != null;
        }
        return z;
    }
}
