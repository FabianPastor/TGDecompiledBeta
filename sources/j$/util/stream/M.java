package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.Function;
import j$.util.function.ToIntFunction;
import j$.util.function.l;
import j$.util.function.m;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.CLASSNAMEn0;
import j$.wrappers.G;
import j$.wrappers.V;

class M extends K0 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(T t, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, G g) {
        super(cVar, e4Var, i);
        this.m = g;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        switch (this.l) {
            case 0:
                return new J(this, m3Var);
            case 1:
                return new F0(this, m3Var);
            case 2:
                return new F0(this, m3Var, (a) null);
            case 3:
                return new F0(this, m3Var, (b) null);
            case 4:
                return new F0(this, m3Var, (c) null);
            case 5:
                return new Z0(this, m3Var);
            case 6:
                return new Y2(this, m3Var);
            default:
                return new r(this, m3Var);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(L0 l0, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, l lVar) {
        super(cVar, e4Var, i);
        this.m = lVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(L0 l0, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, m mVar) {
        super(cVar, e4Var, i);
        this.m = mVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(L0 l0, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, V v) {
        super(cVar, e4Var, i);
        this.m = v;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(L0 l0, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, CLASSNAMEb0 b0Var) {
        super(cVar, e4Var, i);
        this.m = b0Var;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(CLASSNAMEd1 d1Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, CLASSNAMEn0 n0Var) {
        super(cVar, e4Var, i);
        this.m = n0Var;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(CLASSNAMEe3 e3Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, Function function) {
        super(cVar, e4Var, i);
        this.m = function;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public M(CLASSNAMEe3 e3Var, CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, ToIntFunction toIntFunction) {
        super(cVar, e4Var, i);
        this.m = toIntFunction;
    }
}
