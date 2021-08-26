package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.Function;
import j$.util.function.f;
import j$.util.function.g;
import j$.util.function.z;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.E;
import j$.wrappers.X;

class K extends S {
    public final /* synthetic */ int l = 4;
    final /* synthetic */ Object m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(T t, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, f fVar) {
        super(cVar, f4Var, i);
        this.m = fVar;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        switch (this.l) {
            case 0:
                return new J(this, n3Var);
            case 1:
                return new J(this, n3Var, (a) null);
            case 2:
                return new J(this, n3Var, (b) null);
            case 3:
                return new J(this, n3Var, (c) null);
            case 4:
                return new F0(this, n3Var);
            case 5:
                return new CLASSNAMEa1(this, n3Var);
            case 6:
                return new Z2(this, n3Var);
            default:
                return new r(this, n3Var);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(T t, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, g gVar) {
        super(cVar, f4Var, i);
        this.m = gVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(T t, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, E e) {
        super(cVar, f4Var, i);
        this.m = e;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(T t, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, j$.wrappers.K k) {
        super(cVar, f4Var, i);
        this.m = k;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(L0 l0, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, X x) {
        super(cVar, f4Var, i);
        this.m = x;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(CLASSNAMEe1 e1Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, CLASSNAMEl0 l0Var) {
        super(cVar, f4Var, i);
        this.m = l0Var;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(CLASSNAMEf3 f3Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, Function function) {
        super(cVar, f4Var, i);
        this.m = function;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public K(CLASSNAMEf3 f3Var, CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, z zVar) {
        super(cVar, f4Var, i);
        this.m = zVar;
    }
}
