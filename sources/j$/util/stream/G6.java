package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;

abstract class G6 {
    protected final Spliterator a;
    protected final boolean b;
    private final long c;
    private final AtomicLong d;

    G6(Spliterator spliterator, long j, long j2) {
        this.a = spliterator;
        long j3 = 0;
        int i = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
        this.b = i < 0;
        this.c = i >= 0 ? j2 : j3;
        this.d = new AtomicLong(i >= 0 ? j + j2 : j);
    }

    G6(Spliterator spliterator, G6 g6) {
        this.a = spliterator;
        this.b = g6.b;
        this.d = g6.d;
        this.c = g6.c;
    }

    public final int characteristics() {
        return this.a.characteristics() & -16465;
    }

    public final long estimateSize() {
        return this.a.estimateSize();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final long q(long r10) {
        /*
            r9 = this;
        L_0x0000:
            java.util.concurrent.atomic.AtomicLong r0 = r9.d
            long r0 = r0.get()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0013
            boolean r0 = r9.b
            if (r0 == 0) goto L_0x0011
            goto L_0x0012
        L_0x0011:
            r10 = r2
        L_0x0012:
            return r10
        L_0x0013:
            long r4 = java.lang.Math.min(r0, r10)
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0025
            java.util.concurrent.atomic.AtomicLong r6 = r9.d
            long r7 = r0 - r4
            boolean r6 = r6.compareAndSet(r0, r7)
            if (r6 == 0) goto L_0x0000
        L_0x0025:
            boolean r6 = r9.b
            if (r6 == 0) goto L_0x002f
            long r10 = r10 - r4
            long r10 = java.lang.Math.max(r10, r2)
            return r10
        L_0x002f:
            long r10 = r9.c
            int r6 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r6 <= 0) goto L_0x003c
            long r0 = r0 - r10
            long r4 = r4 - r0
            long r10 = java.lang.Math.max(r4, r2)
            return r10
        L_0x003c:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.G6.q(long):long");
    }

    /* access modifiers changed from: protected */
    public abstract Spliterator r(Spliterator spliterator);

    /* access modifiers changed from: protected */
    public final F6 s() {
        return this.d.get() > 0 ? F6.MAYBE_MORE : this.b ? F6.UNLIMITED : F6.NO_MORE;
    }

    public final Spliterator trySplit() {
        Spliterator trySplit;
        if (this.d.get() == 0 || (trySplit = this.a.trySplit()) == null) {
            return null;
        }
        return r(trySplit);
    }
}
