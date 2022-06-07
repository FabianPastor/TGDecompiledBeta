package j$.util.stream;

import j$.util.function.Consumer;

public final /* synthetic */ class K4 implements CLASSNAMEm3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ K4(Consumer consumer) {
        this.b = consumer;
    }

    public /* synthetic */ void accept(double d) {
        switch (this.a) {
            case 0:
                CLASSNAMEo1.f(this);
                throw null;
            default:
                CLASSNAMEo1.f(this);
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

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ K4(CLASSNAMEa4 a4Var) {
        this.b = a4Var;
    }

    public /* synthetic */ void accept(int i) {
        switch (this.a) {
            case 0:
                CLASSNAMEo1.d(this);
                throw null;
            default:
                CLASSNAMEo1.d(this);
                throw null;
        }
    }

    public /* synthetic */ void accept(long j) {
        switch (this.a) {
            case 0:
                CLASSNAMEo1.e(this);
                throw null;
            default:
                CLASSNAMEo1.e(this);
                throw null;
        }
    }

    public final void accept(Object obj) {
        switch (this.a) {
            case 0:
                ((Consumer) this.b).accept(obj);
                return;
            default:
                ((CLASSNAMEa4) this.b).accept(obj);
                return;
        }
    }
}
