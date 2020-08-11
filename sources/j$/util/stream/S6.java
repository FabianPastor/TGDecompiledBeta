package j$.util.stream;

import j$.util.Spliterator;

abstract class S6 {
    final long a;
    final long b;
    Spliterator c;
    long d;
    long e;

    /* access modifiers changed from: protected */
    public abstract Spliterator b(Spliterator spliterator, long j, long j2, long j3, long j4);

    S6(Spliterator s, long sliceOrigin, long sliceFence, long origin, long fence) {
        this.c = s;
        this.a = sliceOrigin;
        this.b = sliceFence;
        this.d = origin;
        this.e = fence;
    }

    public Spliterator trySplit() {
        long j = this.a;
        long j2 = this.e;
        if (j >= j2 || this.d >= j2) {
            return null;
        }
        while (true) {
            T_SPLITR leftSplit = this.c.trySplit();
            if (leftSplit == null) {
                return null;
            }
            long leftSplitFenceUnbounded = this.d + leftSplit.estimateSize();
            long leftSplitFence = Math.min(leftSplitFenceUnbounded, this.b);
            long j3 = this.a;
            if (j3 >= leftSplitFence) {
                this.d = leftSplitFence;
            } else {
                long j4 = this.b;
                if (leftSplitFence >= j4) {
                    this.c = leftSplit;
                    this.e = leftSplitFence;
                } else if (this.d < j3 || leftSplitFenceUnbounded > j4) {
                    long j5 = this.a;
                    long j6 = this.b;
                    long j7 = this.d;
                    this.d = leftSplitFence;
                    return b(leftSplit, j5, j6, j7, leftSplitFence);
                } else {
                    this.d = leftSplitFence;
                    return leftSplit;
                }
            }
        }
    }

    public long estimateSize() {
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            return j2 - Math.max(j, this.d);
        }
        return 0;
    }

    public int characteristics() {
        return this.c.characteristics();
    }
}
