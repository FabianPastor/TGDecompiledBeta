package j$.util.stream;

import java.util.HashSet;
import java.util.Set;

/* renamed from: j$.util.stream.q1  reason: case insensitive filesystem */
class CLASSNAMEq1 extends CLASSNAMEp5 {
    Set b;

    CLASSNAMEq1(CLASSNAMEr1 r1Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
    }

    public void accept(Object obj) {
        if (!this.b.contains(obj)) {
            this.b.add(obj);
            this.a.accept(obj);
        }
    }

    public void m() {
        this.b = null;
        this.a.m();
    }

    public void n(long j) {
        this.b = new HashSet();
        this.a.n(-1);
    }
}
