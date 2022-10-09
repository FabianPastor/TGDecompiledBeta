package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class r4 implements InterfaceCLASSNAMEl3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ r4(j$.util.function.q qVar) {
        this.b = qVar;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.f(this);
                throw null;
            default:
                AbstractCLASSNAMEo1.f(this);
                throw null;
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

    public /* synthetic */ void b(Long l) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.c(this, l);
                return;
            default:
                AbstractCLASSNAMEo1.c(this, l);
                return;
        }
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        switch (this.a) {
            case 0:
                qVar.getClass();
                return new j$.util.function.p(this, qVar);
            default:
                qVar.getClass();
                return new j$.util.function.p(this, qVar);
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

    public /* synthetic */ r4(Y3 y3) {
        this.b = y3;
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

    @Override // j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public final void accept(long j) {
        switch (this.a) {
            case 0:
                ((j$.util.function.q) this.b).accept(j);
                return;
            default:
                ((Y3) this.b).accept(j);
                return;
        }
    }

    @Override // j$.util.function.Consumer
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
