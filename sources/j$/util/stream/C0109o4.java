package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.o4  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo4 implements CLASSNAMEk3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEo4(f fVar) {
        this.b = fVar;
    }

    public final void accept(double d) {
        switch (this.a) {
            case 0:
                ((f) this.b).accept(d);
                return;
            default:
                ((V3) this.b).accept(d);
                return;
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

    public /* synthetic */ void b(Double d) {
        switch (this.a) {
            case 0:
                CLASSNAMEp1.a(this, d);
                return;
            default:
                CLASSNAMEp1.a(this, d);
                return;
        }
    }

    public f j(f fVar) {
        switch (this.a) {
            case 0:
                fVar.getClass();
                return new e(this, fVar);
            default:
                fVar.getClass();
                return new e(this, fVar);
        }
    }

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ CLASSNAMEo4(V3 v3) {
        this.b = v3;
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

    public /* synthetic */ void accept(long j) {
        switch (this.a) {
            case 0:
                CLASSNAMEp1.e(this);
                throw null;
            default:
                CLASSNAMEp1.e(this);
                throw null;
        }
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        switch (this.a) {
            case 0:
                b((Double) obj);
                return;
            default:
                b((Double) obj);
                return;
        }
    }
}
