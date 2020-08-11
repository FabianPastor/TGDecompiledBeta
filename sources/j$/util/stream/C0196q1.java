package j$.util.stream;

import java.util.HashSet;
import java.util.Set;

/* renamed from: j$.util.stream.q1  reason: case insensitive filesystem */
class CLASSNAMEq1 extends CLASSNAMEz5 {
    Set b;

    CLASSNAMEq1(CLASSNAMEr1 this$0, G5 downstream) {
        super(downstream);
    }

    public void s(long size) {
        this.b = new HashSet();
        this.a.s(-1);
    }

    public void r() {
        this.b = null;
        this.a.r();
    }

    public void accept(Object t) {
        if (!this.b.contains(t)) {
            this.b.add(t);
            this.a.accept(t);
        }
    }
}
