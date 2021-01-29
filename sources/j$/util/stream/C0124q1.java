package j$.util.stream;

import j$.J;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEz1;

/* renamed from: j$.util.stream.q1  reason: case insensitive filesystem */
class CLASSNAMEq1 extends CLASSNAMEz1.k<Double> {
    final /* synthetic */ J l;

    /* renamed from: j$.util.stream.q1$a */
    class a extends A2.a<Integer> {
        a(A2 a2) {
            super(a2);
        }

        public void accept(double d) {
            this.a.accept(CLASSNAMEq1.this.l.a(d));
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEq1(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, J j) {
        super(h1Var, u2, i);
        this.l = j;
    }

    /* access modifiers changed from: package-private */
    public A2 G0(int i, A2 a2) {
        return new a(a2);
    }
}
