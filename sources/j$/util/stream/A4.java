package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.x;
import java.util.Comparator;

abstract class A4 extends E4 implements x {
    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    A4(j$.util.x r13, long r14, long r16) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.A4.<init>(j$.util.x, long, long):void");
    }

    /* access modifiers changed from: protected */
    public abstract Object f();

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        obj.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            long j3 = this.d;
            if (j3 < j2) {
                if (j3 < j || ((x) this.c).estimateSize() + j3 > this.b) {
                    while (this.a > this.d) {
                        ((x) this.c).tryAdvance(f());
                        this.d++;
                    }
                    while (this.d < this.e) {
                        ((x) this.c).tryAdvance(obj);
                        this.d++;
                    }
                    return;
                }
                ((x) this.c).forEachRemaining(obj);
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

    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        long j;
        obj.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            ((x) this.c).tryAdvance(f());
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return ((x) this.c).tryAdvance(obj);
    }

    A4(x xVar, long j, long j2, long j3, long j4, CLASSNAMEp1 p1Var) {
        super(xVar, j, j2, j3, j4);
    }
}
