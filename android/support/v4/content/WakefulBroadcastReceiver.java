package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

@Deprecated
public abstract class WakefulBroadcastReceiver extends BroadcastReceiver {
    private static int mNextId = 1;
    private static final SparseArray<WakeLock> sActiveWakeLocks = new SparseArray();

    public static ComponentName startWakefulService(Context context, Intent intent) {
        ComponentName comp;
        synchronized (sActiveWakeLocks) {
            int id = mNextId;
            mNextId++;
            if (mNextId <= 0) {
                mNextId = 1;
            }
            intent.putExtra("android.support.content.wakelockid", id);
            comp = context.startService(intent);
            if (comp == null) {
                comp = null;
            } else {
                WakeLock wl = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "wake:" + comp.flattenToShortString());
                wl.setReferenceCounted(false);
                wl.acquire(ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
                sActiveWakeLocks.put(id, wl);
            }
        }
        return comp;
    }

    public static boolean completeWakefulIntent(Intent intent) {
        int id = intent.getIntExtra("android.support.content.wakelockid", 0);
        if (id == 0) {
            return false;
        }
        synchronized (sActiveWakeLocks) {
            WakeLock wl = (WakeLock) sActiveWakeLocks.get(id);
            if (wl != null) {
                wl.release();
                sActiveWakeLocks.remove(id);
                return true;
            }
            Log.w("WakefulBroadcastReceiv.", "No active wake lock id #" + id);
            return true;
        }
    }
}
