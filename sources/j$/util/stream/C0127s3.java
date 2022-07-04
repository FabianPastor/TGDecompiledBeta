package j$.util.stream;

import j$.util.function.m;
import j$.util.u;

/* renamed from: j$.util.stream.s3  reason: case insensitive filesystem */
class CLASSNAMEs3 extends J0 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEs3(CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i, long j, long j2) {
        super(cVar, e4Var, i);
        this.l = j;
        this.m = j2;
    }

    /* access modifiers changed from: package-private */
    public A1 E0(CLASSNAMEy2 y2Var, u uVar, m mVar) {
        long q0 = y2Var.q0(uVar);
        if (q0 <= 0) {
            CLASSNAMEy2 y2Var2 = y2Var;
            u uVar2 = uVar;
        } else if (uVar.hasCharacteristics(16384)) {
            return CLASSNAMEx2.g(y2Var, B3.b(y2Var.r0(), uVar, this.l, this.m), true);
        } else {
            CLASSNAMEy2 y2Var3 = y2Var;
        }
        if (!CLASSNAMEd4.ORDERED.d(y2Var.s0())) {
            return CLASSNAMEx2.g(this, N0((u.a) y2Var.w0(uVar), this.l, this.m, q0), true);
        }
        return (A1) new A3(this, y2Var, uVar, mVar, this.l, this.m).invoke();
    }

    /* access modifiers changed from: package-private */
    public u F0(CLASSNAMEy2 y2Var, u uVar) {
        long q0 = y2Var.q0(uVar);
        if (q0 <= 0) {
            u uVar2 = uVar;
        } else if (uVar.hasCharacteristics(16384)) {
            long j = this.l;
            return new w4((u.a) y2Var.w0(uVar), j, B3.d(j, this.m));
        }
        if (!CLASSNAMEd4.ORDERED.d(y2Var.s0())) {
            return N0((u.a) y2Var.w0(uVar), this.l, this.m, q0);
        }
        return ((A1) new A3(this, y2Var, uVar, CLASSNAMEq3.a, this.l, this.m).invoke()).spliterator();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        return new CLASSNAMEr3(this, m3Var);
    }

    /* access modifiers changed from: package-private */
    public u.a N0(u.a aVar, long j, long j2, long j3) {
        long j4;
        long j5;
        if (j <= j3) {
            long j6 = j3 - j;
            j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
            j5 = 0;
        } else {
            j5 = j;
            j4 = j2;
        }
        return new F4(aVar, j5, j4);
    }
}
