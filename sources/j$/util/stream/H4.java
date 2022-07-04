package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.w;
import java.util.Comparator;

abstract class H4 extends J4 implements w {
    H4(w wVar, long j, long j2) {
        super(wVar, j, j2);
    }

    H4(w wVar, H4 h4) {
        super(wVar, h4);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        obj.getClass();
        CLASSNAMEj4 j4Var = null;
        while (true) {
            int r = r();
            if (r == 1) {
                return;
            }
            if (r == 2) {
                if (j4Var == null) {
                    j4Var = t(128);
                } else {
                    j4Var.b = 0;
                }
                long j = 0;
                while (((w) this.a).tryAdvance(j4Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    j4Var.b(obj, p(j));
                } else {
                    return;
                }
            } else {
                ((w) this.a).forEachRemaining(obj);
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
    public abstract CLASSNAMEj4 t(int i);

    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        obj.getClass();
        while (r() != 1 && ((w) this.a).tryAdvance(this)) {
            if (p(1) == 1) {
                s(obj);
                return true;
            }
        }
        return false;
    }
}
