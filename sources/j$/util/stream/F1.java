package j$.util.stream;

import j$.CLASSNAMEl0;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;

class F1 extends CLASSNAMEp1.i<Long> {
    final /* synthetic */ CLASSNAMEl0 l;

    class a extends A2.c<Double> {
        a(A2 a2) {
            super(a2);
        }

        public void accept(long j) {
            this.a.accept(F1.this.l.a(j));
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    F1(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, CLASSNAMEl0 l0Var) {
        super(h1Var, u2, i);
        this.l = l0Var;
    }

    /* access modifiers changed from: package-private */
    public A2 G0(int i, A2 a2) {
        return new a(a2);
    }
}
