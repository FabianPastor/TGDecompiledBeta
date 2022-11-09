package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.p4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEp4 implements InterfaceCLASSNAMEk3 {
    public final /* synthetic */ int a = 0;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEp4(j$.util.function.l lVar) {
        this.b = lVar;
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

    public /* synthetic */ void b(Integer num) {
        switch (this.a) {
            case 0:
                AbstractCLASSNAMEo1.b(this, num);
                return;
            default:
                AbstractCLASSNAMEo1.b(this, num);
                return;
        }
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        switch (this.a) {
            case 0:
                lVar.getClass();
                return new j$.util.function.k(this, lVar);
            default:
                lVar.getClass();
                return new j$.util.function.k(this, lVar);
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

    public /* synthetic */ CLASSNAMEp4(W3 w3) {
        this.b = w3;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEk3, j$.util.stream.InterfaceCLASSNAMEm3
    public final void accept(int i) {
        switch (this.a) {
            case 0:
                ((j$.util.function.l) this.b).accept(i);
                return;
            default:
                ((W3) this.b).accept(i);
                return;
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
                b((Integer) obj);
                return;
            default:
                b((Integer) obj);
                return;
        }
    }
}
