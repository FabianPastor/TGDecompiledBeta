package j$.util.stream;

import j$.util.u;
import j$.util.w;
import java.util.Deque;

/* renamed from: j$.util.stream.i2  reason: case insensitive filesystem */
abstract class CLASSNAMEi2 extends CLASSNAMEk2 implements w {
    CLASSNAMEi2(CLASSNAMEz1 z1Var) {
        super(z1Var);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        if (this.a != null) {
            if (this.d == null) {
                u uVar = this.c;
                if (uVar == null) {
                    Deque f = f();
                    while (true) {
                        CLASSNAMEz1 z1Var = (CLASSNAMEz1) a(f);
                        if (z1Var != null) {
                            z1Var.g(obj);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    ((w) uVar).forEachRemaining(obj);
                }
            } else {
                do {
                } while (k(obj));
            }
        }
    }

    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        CLASSNAMEz1 z1Var;
        if (!h()) {
            return false;
        }
        boolean tryAdvance = ((w) this.d).tryAdvance(obj);
        if (!tryAdvance) {
            if (this.c != null || (z1Var = (CLASSNAMEz1) a(this.e)) == null) {
                this.a = null;
            } else {
                w spliterator = z1Var.spliterator();
                this.d = spliterator;
                return spliterator.tryAdvance(obj);
            }
        }
        return tryAdvance;
    }
}
