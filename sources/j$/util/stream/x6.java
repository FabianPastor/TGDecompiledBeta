package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.F;
import java.util.Comparator;

abstract class x6 extends z6 implements F {
    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    x6(j$.util.F r13, long r14, long r16) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.x6.<init>(j$.util.F, long, long):void");
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
                if (j3 < j || ((F) this.c).estimateSize() + j3 > this.b) {
                    while (this.a > this.d) {
                        ((F) this.c).tryAdvance(f());
                        this.d++;
                    }
                    while (this.d < this.e) {
                        ((F) this.c).tryAdvance(obj);
                        this.d++;
                    }
                    return;
                }
                ((F) this.c).forEachRemaining(obj);
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

    /* renamed from: tryAdvance */
    public boolean o(Object obj) {
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
            ((F) this.c).tryAdvance(f());
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return ((F) this.c).tryAdvance(obj);
    }

    x6(F f, long j, long j2, long j3, long j4, CLASSNAMEi6 i6Var) {
        super(f, j, j2, j3, j4);
    }
}
