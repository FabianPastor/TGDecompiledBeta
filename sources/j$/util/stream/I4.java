package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.x;
import java.util.Comparator;

abstract class I4 extends K4 implements x {
    I4(x xVar, long j, long j2) {
        super(xVar, j, j2);
    }

    I4(x xVar, I4 i4) {
        super(xVar, i4);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        obj.getClass();
        CLASSNAMEk4 k4Var = null;
        while (true) {
            int r = r();
            if (r == 1) {
                return;
            }
            if (r == 2) {
                if (k4Var == null) {
                    k4Var = t(128);
                } else {
                    k4Var.b = 0;
                }
                long j = 0;
                while (((x) this.a).tryAdvance(k4Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    k4Var.b(obj, p(j));
                } else {
                    return;
                }
            } else {
                ((x) this.a).forEachRemaining(obj);
                return;
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

    /* access modifiers changed from: protected */
    public abstract void s(Object obj);

    /* access modifiers changed from: protected */
    public abstract CLASSNAMEk4 t(int i);

    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        obj.getClass();
        while (r() != 1 && ((x) this.a).tryAdvance(this)) {
            if (p(1) == 1) {
                s(obj);
                return true;
            }
        }
        return false;
    }
}
