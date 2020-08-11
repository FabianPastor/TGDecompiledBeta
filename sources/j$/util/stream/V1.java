package j$.util.stream;

import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;

final class V1 extends Z1 implements B5 {
    final CLASSNAMEt b;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    V1(CLASSNAMEt consumer, boolean ordered) {
        super(ordered);
        this.b = consumer;
    }

    public void accept(double t) {
        this.b.accept(t);
    }
}
