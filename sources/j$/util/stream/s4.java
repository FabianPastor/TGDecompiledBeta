package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;

public final /* synthetic */ class s4 implements CLASSNAMEm3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ s4(q qVar) {
        this.b = qVar;
    }

    public /* synthetic */ void accept(double d) {
        switch (this.a) {
            case 0:
                CLASSNAMEp1.f(this);
                throw null;
            default:
                CLASSNAMEp1.f(this);
                throw null;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return Consumer.CC.$default$andThen(this, consumer);
            default:
                return Consumer.CC.$default$andThen(this, consumer);
        }
    }

    public /* synthetic */ void b(Long l) {
        switch (this.a) {
            case 0:
                CLASSNAMEp1.c(this, l);
                return;
            default:
                CLASSNAMEp1.c(this, l);
                return;
        }
    }

    public q f(q qVar) {
        switch (this.a) {
            case 0:
                qVar.getClass();
                return new p(this, qVar);
            default:
                qVar.getClass();
                return new p(this, qVar);
        }
    }

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ s4(Z3 z3) {
        this.b = z3;
    }

    public /* synthetic */ void accept(int i) {
        switch (this.a) {
            case 0:
                CLASSNAMEp1.d(this);
                throw null;
            default:
                CLASSNAMEp1.d(this);
                throw null;
        }
    }

    public final void accept(long j) {
        switch (this.a) {
            case 0:
                ((q) this.b).accept(j);
                return;
            default:
                ((Z3) this.b).accept(j);
                return;
        }
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        switch (this.a) {
            case 0:
                b((Long) obj);
                return;
            default:
                b((Long) obj);
                return;
        }
    }
}
