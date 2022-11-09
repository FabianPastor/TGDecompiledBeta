package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class V2 extends AbstractCLASSNAMEf {
    private final U2 h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public V2(U2 u2, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        super(abstractCLASSNAMEy2, uVar);
        this.h = u2;
    }

    V2(V2 v2, j$.util.u uVar) {
        super(v2, uVar);
        this.h = v2.h;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object a() {
        AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = this.a;
        S2 a = this.h.a();
        abstractCLASSNAMEy2.u0(a, this.b);
        return a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public AbstractCLASSNAMEf f(j$.util.u uVar) {
        return new V2(this, uVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            S2 s2 = (S2) ((V2) this.d).b();
            s2.h((S2) ((V2) this.e).b());
            g(s2);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
