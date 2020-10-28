package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.v;

/* renamed from: j$.util.stream.v5  reason: case insensitive filesystem */
class CLASSNAMEv5 extends CLASSNAMEj5 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEv5(CLASSNAMEh1 h1Var, CLASSNAMEh6 h6Var, int i, long j, long j2) {
        super(h1Var, h6Var, i);
        this.l = j;
        this.m = j2;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        v vVar2 = vVar;
        long p0 = i4Var.p0(spliterator);
        if (p0 <= 0) {
            CLASSNAMEi4 i4Var2 = i4Var;
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            return CLASSNAMEh4.e(i4Var, D5.b(i4Var.q0(), spliterator, this.l, this.m), true, vVar2);
        } else {
            CLASSNAMEi4 i4Var3 = i4Var;
        }
        if (!CLASSNAMEg6.ORDERED.d(i4Var.r0())) {
            return CLASSNAMEh4.e(this, K0(i4Var.v0(spliterator), this.l, this.m, p0), true, vVar2);
        }
        return (CLASSNAMEl3) new C5(this, i4Var, spliterator, vVar, this.l, this.m).invoke();
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        long p0 = i4Var.p0(spliterator);
        if (p0 <= 0) {
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            Spliterator v0 = i4Var.v0(spliterator);
            long j = this.l;
            return new y6(v0, j, D5.d(j, this.m));
        }
        if (!CLASSNAMEg6.ORDERED.d(i4Var.r0())) {
            return K0(i4Var.v0(spliterator), this.l, this.m, p0);
        }
        return ((CLASSNAMEl3) new C5(this, i4Var, spliterator, CLASSNAMEu0.a, this.l, this.m).invoke()).spliterator();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        return new CLASSNAMEu5(this, t5Var);
    }

    /* access modifiers changed from: package-private */
    public Spliterator K0(Spliterator spliterator, long j, long j2, long j3) {
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
        return new E6(spliterator, j5, j4);
    }
}
