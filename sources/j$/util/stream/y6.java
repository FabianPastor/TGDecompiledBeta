package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

final class y6 extends z6 implements Spliterator {
    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    y6(j$.util.Spliterator r13, long r14, long r16) {
        /*
            r12 = this;
            long r0 = r13.estimateSize()
            r6 = r16
            long r10 = java.lang.Math.min(r0, r6)
            r8 = 0
            r2 = r12
            r3 = r13
            r4 = r14
            r2.<init>(r3, r4, r6, r8, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.y6.<init>(j$.util.Spliterator, long, long):void");
    }

    private y6(Spliterator spliterator, long j, long j2, long j3, long j4) {
        super(spliterator, j, j2, j3, j4);
    }

    /* access modifiers changed from: protected */
    public Spliterator a(Spliterator spliterator, long j, long j2, long j3, long j4) {
        return new y6(spliterator, j, j2, j3, j4);
    }

    public boolean b(Consumer consumer) {
        long j;
        consumer.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            this.c.b(F0.a);
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return this.c.b(consumer);
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            long j3 = this.d;
            if (j3 < j2) {
                if (j3 < j || this.c.estimateSize() + j3 > this.b) {
                    while (this.a > this.d) {
                        this.c.b(G0.a);
                        this.d++;
                    }
                    while (this.d < this.e) {
                        this.c.b(consumer);
                        this.d++;
                    }
                    return;
                }
                this.c.forEachRemaining(consumer);
                this.d = this.e;
            }
        }
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }
}
