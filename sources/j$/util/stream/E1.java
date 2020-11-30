package j$.util.stream;

import j$.CLASSNAMEq0;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEz1;

class E1 extends CLASSNAMEz1.k<Long> {
    final /* synthetic */ CLASSNAMEq0 l;

    class a extends A2.c<Integer> {
        a(A2 a2) {
            super(a2);
        }

        public void accept(long j) {
            this.a.accept(E1.this.l.a(j));
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    E1(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, CLASSNAMEq0 q0Var) {
        super(h1Var, u2, i);
        this.l = q0Var;
    }

    /* access modifiers changed from: package-private */
    public A2 G0(int i, A2 a2) {
        return new a(a2);
    }
}
