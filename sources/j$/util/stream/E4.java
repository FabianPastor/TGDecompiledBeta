package j$.util.stream;

import j$.util.y;

abstract class E4 {
    final long a;
    final long b;
    y c;
    long d;
    long e;

    E4(y yVar, long j, long j2, long j3, long j4) {
        this.c = yVar;
        this.a = j;
        this.b = j2;
        this.d = j3;
        this.e = j4;
    }

    /* access modifiers changed from: protected */
    public abstract y a(y yVar, long j, long j2, long j3, long j4);

    public int characteristics() {
        return this.c.characteristics();
    }

    public long estimateSize() {
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            return j2 - Math.max(j, this.d);
        }
        return 0;
    }

    public y trySplit() {
        long j = this.a;
        long j2 = this.e;
        if (j >= j2 || this.d >= j2) {
            return null;
        }
        while (true) {
            y trySplit = this.c.trySplit();
            if (trySplit == null) {
                return null;
            }
            long estimateSize = trySplit.estimateSize() + this.d;
            long min = Math.min(estimateSize, this.b);
            long j3 = this.a;
            if (j3 >= min) {
                this.d = min;
            } else {
                long j4 = this.b;
                if (min >= j4) {
                    this.c = trySplit;
                    this.e = min;
                } else {
                    long j5 = this.d;
                    if (j5 < j3 || estimateSize > j4) {
                        this.d = min;
                        return a(trySplit, j3, j4, j5, min);
                    }
                    this.d = min;
                    return trySplit;
                }
            }
        }
    }
}