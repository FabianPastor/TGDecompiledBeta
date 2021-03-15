package j$.util.stream;

import a.CLASSNAMEa0;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;

class A1 extends CLASSNAMEp1.i<Integer> {
    final /* synthetic */ CLASSNAMEa0 l;

    class a extends A2.b<Double> {
        a(A2 a2) {
            super(a2);
        }

        public void accept(int i) {
            this.var_a.accept(A1.this.l.a(i));
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A1(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, CLASSNAMEa0 a0Var) {
        super(h1Var, u2, i);
        this.l = a0Var;
    }

    /* access modifiers changed from: package-private */
    public A2 G0(int i, A2 a2) {
        return new a(a2);
    }
}
