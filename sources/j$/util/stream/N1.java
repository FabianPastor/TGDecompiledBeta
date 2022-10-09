package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
class N1 extends AbstractCLASSNAMEf {
    protected final AbstractCLASSNAMEy2 h;
    protected final j$.util.function.r i;
    protected final j$.util.function.b j;

    N1(N1 n1, j$.util.u uVar) {
        super(n1, uVar);
        this.h = n1.h;
        this.i = n1.i;
        this.j = n1.j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public N1(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.r rVar, j$.util.function.b bVar) {
        super(abstractCLASSNAMEy2, uVar);
        this.h = abstractCLASSNAMEy2;
        this.i = rVar;
        this.j = bVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object a() {
        InterfaceCLASSNAMEs1 interfaceCLASSNAMEs1 = (InterfaceCLASSNAMEs1) this.i.apply(this.h.q0(this.b));
        this.h.u0(interfaceCLASSNAMEs1, this.b);
        return interfaceCLASSNAMEs1.mo287a();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public AbstractCLASSNAMEf f(j$.util.u uVar) {
        return new N1(this, uVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((A1) this.j.apply((A1) ((N1) this.d).b(), (A1) ((N1) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
