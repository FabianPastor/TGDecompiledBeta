package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.n4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEn4 implements InterfaceCLASSNAMEj3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEn4(j$.util.function.f fVar) {
        this.b = fVar;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEj3, j$.util.stream.InterfaceCLASSNAMEm3
    public final void accept(double d) {
        switch (this.a) {
            case 0:
                ((j$.util.function.f) this.b).accept(d);
                return;
            default:
                ((U3) this.b).accept(d);
                return;
        }
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return consumer.getClass();
            default:
                return consumer.getClass();
        }
    }

    public /* synthetic */ void b(Double d) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.a(this, d);
                return;
            default:
                AbstractCLASSNAMEo1.a(this, d);
                return;
        }
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        switch (this.a) {
            case 0:
                fVar.getClass();
                return new j$.util.function.e(this, fVar);
            default:
                fVar.getClass();
                return new j$.util.function.e(this, fVar);
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ CLASSNAMEn4(U3 u3) {
        this.b = u3;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(int i) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.d(this);
                throw null;
            default:
                AbstractCLASSNAMEo1.d(this);
                throw null;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.e(this);
                throw null;
            default:
                AbstractCLASSNAMEo1.e(this);
                throw null;
        }
    }

    @Override // j$.util.function.Consumer
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
