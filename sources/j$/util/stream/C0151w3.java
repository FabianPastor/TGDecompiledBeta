package j$.util.stream;

import j$.util.function.m;
import j$.util.w;
import j$.util.y;

/* renamed from: j$.util.stream.w3  reason: case insensitive filesystem */
class CLASSNAMEw3 extends CLASSNAMEc1 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEw3(CLASSNAMEc cVar, CLASSNAMEf4 f4Var, int i, long j, long j2) {
        super(cVar, f4Var, i);
        this.l = j;
        this.m = j2;
    }

    /* access modifiers changed from: package-private */
    public B1 E0(CLASSNAMEz2 z2Var, y yVar, m mVar) {
        long q0 = z2Var.q0(yVar);
        if (q0 <= 0) {
            CLASSNAMEz2 z2Var2 = z2Var;
            y yVar2 = yVar;
        } else if (yVar.hasCharacteristics(16384)) {
            return CLASSNAMEy2.h(z2Var, C3.b(z2Var.r0(), yVar, this.l, this.m), true);
        } else {
            CLASSNAMEz2 z2Var3 = z2Var;
        }
        if (!CLASSNAMEe4.ORDERED.d(z2Var.s0())) {
            return CLASSNAMEy2.h(this, N0((w) z2Var.w0(yVar), this.l, this.m, q0), true);
        }
        return (B1) new B3(this, z2Var, yVar, mVar, this.l, this.m).invoke();
    }

    /* access modifiers changed from: package-private */
    public y F0(CLASSNAMEz2 z2Var, y yVar) {
        long q0 = z2Var.q0(yVar);
        if (q0 <= 0) {
            y yVar2 = yVar;
        } else if (yVar.hasCharacteristics(16384)) {
            long j = this.l;
            return new z4((w) z2Var.w0(yVar), j, C3.d(j, this.m));
        }
        if (!CLASSNAMEe4.ORDERED.d(z2Var.s0())) {
            return N0((w) z2Var.w0(yVar), this.l, this.m, q0);
        }
        return ((B1) new B3(this, z2Var, yVar, CLASSNAMEu3.a, this.l, this.m).invoke()).spliterator();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        return new CLASSNAMEv3(this, n3Var);
    }

    /* access modifiers changed from: package-private */
    public w N0(w wVar, long j, long j2, long j3) {
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
        return new H4(wVar, j5, j4);
    }
}