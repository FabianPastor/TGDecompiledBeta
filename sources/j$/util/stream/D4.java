package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.y;
import java.util.Comparator;

final class D4 extends E4 implements y {
    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    D4(j$.util.y r13, long r14, long r16) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.D4.<init>(j$.util.y, long, long):void");
    }

    private D4(y yVar, long j, long j2, long j3, long j4) {
        super(yVar, j, j2, j3, j4);
    }

    /* access modifiers changed from: protected */
    public y a(y yVar, long j, long j2, long j3, long j4) {
        return new D4(yVar, j, j2, j3, j4);
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
            this.c.b(C4.a);
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
                        this.c.b(B4.a);
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
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }
}
