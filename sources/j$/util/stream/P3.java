package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import java.util.Deque;

abstract class P3 extends R3 implements F {
    P3(CLASSNAMEk3 k3Var) {
        super(k3Var);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        if (this.a != null) {
            if (this.d == null) {
                Spliterator spliterator = this.c;
                if (spliterator == null) {
                    Deque f = f();
                    while (true) {
                        CLASSNAMEk3 k3Var = (CLASSNAMEk3) a(f);
                        if (k3Var != null) {
                            k3Var.h(obj);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    ((F) spliterator).forEachRemaining(obj);
                }
            } else {
                do {
                } while (o(obj));
            }
        }
    }

    /* renamed from: tryAdvance */
    public boolean o(Object obj) {
        CLASSNAMEk3 k3Var;
        if (!g()) {
            return false;
        }
        boolean tryAdvance = ((F) this.d).tryAdvance(obj);
        if (!tryAdvance) {
            if (this.c != null || (k3Var = (CLASSNAMEk3) a(this.e)) == null) {
                this.a = null;
            } else {
                F spliterator = k3Var.spliterator();
                this.d = spliterator;
                return spliterator.tryAdvance(obj);
            }
        }
        return tryAdvance;
    }
}
