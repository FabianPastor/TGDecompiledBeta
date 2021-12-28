package j$.util.stream;

import j$.lang.a;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.g;
import j$.util.function.m;
import j$.util.function.r;

class L extends CLASSNAMEe3 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(T t, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, g gVar) {
        super(cVar, f4Var, i);
        this.m = gVar;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        switch (this.l) {
            case 0:
                return new J(this, n3Var);
            case 1:
                return new F0(this, n3Var);
            case 2:
                return new CLASSNAMEa1(this, n3Var);
            case 3:
                return new Z2(this, n3Var);
            default:
                return new Z2(this, n3Var, (a) null);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(L0 l0, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, m mVar) {
        super(cVar, f4Var, i);
        this.m = mVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEe1 e1Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, r rVar) {
        super(cVar, f4Var, i);
        this.m = rVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEf3 f3Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, Consumer consumer) {
        super(cVar, f4Var, i);
        this.m = consumer;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public L(CLASSNAMEf3 f3Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, Predicate predicate) {
        super(cVar, f4Var, i);
        this.m = predicate;
    }
}
