package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.F;
import java.util.Comparator;

abstract class D6 extends G6 implements F {
    D6(F f, long j, long j2) {
        super(f, j, j2);
    }

    D6(F f, D6 d6) {
        super(f, d6);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        obj.getClass();
        n6 n6Var = null;
        while (true) {
            F6 s = s();
            if (s == F6.NO_MORE) {
                return;
            }
            if (s == F6.MAYBE_MORE) {
                if (n6Var == null) {
                    n6Var = u(128);
                } else {
                    n6Var.b = 0;
                }
                long j = 0;
                while (((F) this.a).tryAdvance(n6Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    n6Var.b(obj, q(j));
                } else {
                    return;
                }
            } else {
                ((F) this.a).forEachRemaining(obj);
                return;
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

    /* access modifiers changed from: protected */
    public abstract void t(Object obj);

    /* renamed from: tryAdvance */
    public boolean o(Object obj) {
        obj.getClass();
        while (s() != F6.NO_MORE && ((F) this.a).tryAdvance(this)) {
            if (q(1) == 1) {
                t(obj);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract n6 u(int i);
}
