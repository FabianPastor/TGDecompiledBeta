package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.k0  reason: case insensitive filesystem */
final class CLASSNAMEk0 extends CLASSNAMEo0 implements CLASSNAMEk3 {
    final f b;

    CLASSNAMEk0(f fVar, boolean z) {
        super(z);
        this.b = fVar;
    }

    public void accept(double d) {
        this.b.accept(d);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEp1.a(this, d);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
