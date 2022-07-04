package j$.util.stream;

import j$.lang.a;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.g;
import j$.util.function.m;
import j$.util.function.r;

class L extends CLASSNAMEd3 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(T t, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, g gVar) {
        super(cVar, e4Var, i);
        this.m = gVar;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        switch (this.l) {
            case 0:
                return new J(this, m3Var);
            case 1:
                return new F0(this, m3Var);
            case 2:
                return new Z0(this, m3Var);
            case 3:
                return new Y2(this, m3Var);
            default:
                return new Y2(this, m3Var, (a) null);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(L0 l0, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, m mVar) {
        super(cVar, e4Var, i);
        this.m = mVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEd1 d1Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, r rVar) {
        super(cVar, e4Var, i);
        this.m = rVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEe3 e3Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, Consumer consumer) {
        super(cVar, e4Var, i);
        this.m = consumer;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEe3 e3Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, Predicate predicate) {
        super(cVar, e4Var, i);
        this.m = predicate;
    }
}
