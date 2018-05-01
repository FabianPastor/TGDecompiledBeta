package org.telegram.messenger.exoplayer2.source;

import android.os.Handler;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public interface MediaSourceEventListener {

    public static final class EventDispatcher {
        private final Handler handler;
        private final MediaSourceEventListener listener;
        private final long mediaTimeOffsetMs;

        public EventDispatcher(Handler handler, MediaSourceEventListener mediaSourceEventListener) {
            this(handler, mediaSourceEventListener, 0);
        }

        public EventDispatcher(Handler handler, MediaSourceEventListener mediaSourceEventListener, long j) {
            this.handler = mediaSourceEventListener != null ? (Handler) Assertions.checkNotNull(handler) : null;
            this.listener = mediaSourceEventListener;
            this.mediaTimeOffsetMs = j;
        }

        public EventDispatcher copyWithMediaTimeOffsetMs(long j) {
            return new EventDispatcher(this.handler, this.listener, j);
        }

        public void loadStarted(DataSpec dataSpec, int i, long j) {
            loadStarted(dataSpec, i, -1, null, 0, null, C0542C.TIME_UNSET, C0542C.TIME_UNSET, j);
        }

        public void loadStarted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3) {
            if (this.listener != null && r14.handler != null) {
                Handler handler = r14.handler;
                final DataSpec dataSpec2 = dataSpec;
                final int i4 = i;
                final int i5 = i2;
                final Format format2 = format;
                final int i6 = i3;
                final Object obj2 = obj;
                final long j4 = j;
                final long j5 = j2;
                C05961 c05961 = r0;
                final long j6 = j3;
                C05961 c059612 = new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onLoadStarted(dataSpec2, i4, i5, format2, i6, obj2, EventDispatcher.this.adjustMediaTime(j4), EventDispatcher.this.adjustMediaTime(j5), j6);
                    }
                };
                handler.post(c05961);
            }
        }

        public void loadCompleted(DataSpec dataSpec, int i, long j, long j2, long j3) {
            loadCompleted(dataSpec, i, -1, null, 0, null, C0542C.TIME_UNSET, C0542C.TIME_UNSET, j, j2, j3);
        }

        public void loadCompleted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
            if (this.listener != null && r14.handler != null) {
                final DataSpec dataSpec2 = dataSpec;
                final int i4 = i;
                final int i5 = i2;
                final Format format2 = format;
                final int i6 = i3;
                final Object obj2 = obj;
                final long j6 = j;
                final long j7 = j2;
                C05972 c05972 = r0;
                final long j8 = j3;
                Handler handler = r14.handler;
                final long j9 = j4;
                final long j10 = j5;
                C05972 c059722 = new Runnable() {
                    public void run() {
                        MediaSourceEventListener access$100 = EventDispatcher.this.listener;
                        DataSpec dataSpec = dataSpec2;
                        int i = i4;
                        int i2 = i5;
                        Format format = format2;
                        int i3 = i6;
                        Object obj = obj2;
                        long access$000 = EventDispatcher.this.adjustMediaTime(j6);
                        long access$0002 = EventDispatcher.this.adjustMediaTime(j7);
                        long j = j8;
                        long j2 = j;
                        access$100.onLoadCompleted(dataSpec, i, i2, format, i3, obj, access$000, access$0002, j2, j9, j10);
                    }
                };
                handler.post(c05972);
            }
        }

        public void loadCanceled(DataSpec dataSpec, int i, long j, long j2, long j3) {
            loadCanceled(dataSpec, i, -1, null, 0, null, C0542C.TIME_UNSET, C0542C.TIME_UNSET, j, j2, j3);
        }

        public void loadCanceled(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5) {
            if (this.listener != null && r14.handler != null) {
                final DataSpec dataSpec2 = dataSpec;
                final int i4 = i;
                final int i5 = i2;
                final Format format2 = format;
                final int i6 = i3;
                final Object obj2 = obj;
                final long j6 = j;
                final long j7 = j2;
                C05983 c05983 = r0;
                final long j8 = j3;
                Handler handler = r14.handler;
                final long j9 = j4;
                final long j10 = j5;
                C05983 c059832 = new Runnable() {
                    public void run() {
                        MediaSourceEventListener access$100 = EventDispatcher.this.listener;
                        DataSpec dataSpec = dataSpec2;
                        int i = i4;
                        int i2 = i5;
                        Format format = format2;
                        int i3 = i6;
                        Object obj = obj2;
                        long access$000 = EventDispatcher.this.adjustMediaTime(j6);
                        long access$0002 = EventDispatcher.this.adjustMediaTime(j7);
                        long j = j8;
                        long j2 = j;
                        access$100.onLoadCanceled(dataSpec, i, i2, format, i3, obj, access$000, access$0002, j2, j9, j10);
                    }
                };
                handler.post(c05983);
            }
        }

        public void loadError(DataSpec dataSpec, int i, long j, long j2, long j3, IOException iOException, boolean z) {
            loadError(dataSpec, i, -1, null, 0, null, C0542C.TIME_UNSET, C0542C.TIME_UNSET, j, j2, j3, iOException, z);
        }

        public void loadError(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5, IOException iOException, boolean z) {
            if (this.listener != null && r14.handler != null) {
                final DataSpec dataSpec2 = dataSpec;
                final int i4 = i;
                final int i5 = i2;
                final Format format2 = format;
                final int i6 = i3;
                final Object obj2 = obj;
                final long j6 = j;
                final long j7 = j2;
                C05994 c05994 = r0;
                final long j8 = j3;
                Handler handler = r14.handler;
                final long j9 = j4;
                final long j10 = j5;
                final IOException iOException2 = iOException;
                final boolean z2 = z;
                C05994 c059942 = new Runnable() {
                    public void run() {
                        MediaSourceEventListener access$100 = EventDispatcher.this.listener;
                        DataSpec dataSpec = dataSpec2;
                        int i = i4;
                        int i2 = i5;
                        Format format = format2;
                        int i3 = i6;
                        Object obj = obj2;
                        long access$000 = EventDispatcher.this.adjustMediaTime(j6);
                        long access$0002 = EventDispatcher.this.adjustMediaTime(j7);
                        long j = j8;
                        long j2 = j9;
                        long j3 = j10;
                        long j4 = j3;
                        access$100.onLoadError(dataSpec, i, i2, format, i3, obj, access$000, access$0002, j, j2, j4, iOException2, z2);
                    }
                };
                handler.post(c05994);
            }
        }

        public void upstreamDiscarded(int i, long j, long j2) {
            if (this.listener != null && this.handler != null) {
                final int i2 = i;
                final long j3 = j;
                final long j4 = j2;
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onUpstreamDiscarded(i2, EventDispatcher.this.adjustMediaTime(j3), EventDispatcher.this.adjustMediaTime(j4));
                    }
                });
            }
        }

        public void downstreamFormatChanged(int i, Format format, int i2, Object obj, long j) {
            if (this.listener != null && r8.handler != null) {
                final int i3 = i;
                final Format format2 = format;
                final int i4 = i2;
                final Object obj2 = obj;
                final long j2 = j;
                r8.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onDownstreamFormatChanged(i3, format2, i4, obj2, EventDispatcher.this.adjustMediaTime(j2));
                    }
                });
            }
        }

        private long adjustMediaTime(long j) {
            j = C0542C.usToMs(j);
            if (j == C0542C.TIME_UNSET) {
                return C0542C.TIME_UNSET;
            }
            return this.mediaTimeOffsetMs + j;
        }
    }

    void onDownstreamFormatChanged(int i, Format format, int i2, Object obj, long j);

    void onLoadCanceled(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5);

    void onLoadCompleted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5);

    void onLoadError(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3, long j4, long j5, IOException iOException, boolean z);

    void onLoadStarted(DataSpec dataSpec, int i, int i2, Format format, int i3, Object obj, long j, long j2, long j3);

    void onUpstreamDiscarded(int i, long j, long j2);
}
