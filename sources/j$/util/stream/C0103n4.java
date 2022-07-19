package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.n4  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn4 implements CLASSNAMEj3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEn4(f fVar) {
        this.b = fVar;
    }

    public final void accept(double d) {
        switch (this.a) {
            case 0:
                ((f) this.b).accept(d);
                return;
            default:
                ((U3) this.b).accept(d);
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
                CLASSNAMEo1.a(this, d);
                return;
            default:
                CLASSNAMEo1.a(this, d);
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

    public /* synthetic */ CLASSNAMEn4(U3 u3) {
        this.b = u3;
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
