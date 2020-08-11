package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;

abstract class Z6 {
    protected final Spliterator a;
    protected final boolean b;
    private final long c;
    private final AtomicLong d;

    /* access modifiers changed from: protected */
    public abstract Spliterator m(Spliterator spliterator);

    Z6(Spliterator s, long skip, long limit) {
        this.a = s;
        this.b = limit < 0;
        this.c = limit >= 0 ? limit : 0;
        this.d = new AtomicLong(limit >= 0 ? skip + limit : skip);
    }

    Z6(Spliterator s, Z6 parent) {
        this.a = s;
        this.b = parent.b;
        this.d = parent.d;
        this.c = parent.c;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x000d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0013  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final long k(long r10) {
        /*
            r9 = this;
        L_0x0001:
            java.util.concurrent.atomic.AtomicLong r0 = r9.d
            long r0 = r0.get()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0013
            boolean r4 = r9.b
            if (r4 == 0) goto L_0x0012
            r2 = r10
        L_0x0012:
            return r2
        L_0x0013:
            long r4 = java.lang.Math.min(r0, r10)
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0025
            java.util.concurrent.atomic.AtomicLong r6 = r9.d
            long r7 = r0 - r4
            boolean r6 = r6.compareAndSet(r0, r7)
            if (r6 == 0) goto L_0x0001
        L_0x0025:
            boolean r6 = r9.b
            if (r6 == 0) goto L_0x0030
            long r6 = r10 - r4
            long r2 = java.lang.Math.max(r6, r2)
            return r2
        L_0x0030:
            long r6 = r9.c
            int r8 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x003f
            long r6 = r0 - r6
            long r6 = r4 - r6
            long r2 = java.lang.Math.max(r6, r2)
            return r2
        L_0x003f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.Z6.k(long):long");
    }

    /* access modifiers changed from: protected */
    public final Y6 o() {
        if (this.d.get() > 0) {
            return Y6.MAYBE_MORE;
        }
        return this.b ? Y6.UNLIMITED : Y6.NO_MORE;
    }

    public final Spliterator trySplit() {
        T_SPLITR split;
        if (this.d.get() == 0 || (split = this.a.trySplit()) == null) {
            return null;
        }
        return m(split);
    }

    public final long estimateSize() {
        return this.a.estimateSize();
    }

    public final int characteristics() {
        return this.a.characteristics() & -16465;
    }
}
