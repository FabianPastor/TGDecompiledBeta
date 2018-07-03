package org.telegram.messenger.exoplayer2.offline;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;

public abstract class DownloadHelper {

    public interface Callback {
        void onPrepareError(DownloadHelper downloadHelper, IOException iOException);

        void onPrepared(DownloadHelper downloadHelper);
    }

    public abstract DownloadAction getDownloadAction(byte[] bArr, List<TrackKey> list);

    public abstract int getPeriodCount();

    public abstract DownloadAction getRemoveAction(byte[] bArr);

    public abstract TrackGroupArray getTrackGroups(int i);

    protected abstract void prepareInternal() throws IOException;

    public void prepare(final Callback callback) {
        final Handler handler = new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper());
        new Thread() {

            /* renamed from: org.telegram.messenger.exoplayer2.offline.DownloadHelper$1$1 */
            class C06071 implements Runnable {
                C06071() {
                }

                public void run() {
                    callback.onPrepared(DownloadHelper.this);
                }
            }

            public void run() {
                try {
                    DownloadHelper.this.prepareInternal();
                    handler.post(new C06071());
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        public void run() {
                            callback.onPrepareError(DownloadHelper.this, e);
                        }
                    });
                }
            }
        }.start();
    }
}
