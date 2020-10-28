package j$.util.stream;

import j$.util.E;
import j$.util.Spliterator;
import j$.util.function.v;

/* renamed from: j$.util.stream.z5  reason: case insensitive filesystem */
class CLASSNAMEz5 extends Q2 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEz5(CLASSNAMEh1 h1Var, CLASSNAMEh6 h6Var, int i, long j, long j2) {
        super(h1Var, h6Var, i);
        this.l = j;
        this.m = j2;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        long p0 = i4Var.p0(spliterator);
        if (p0 <= 0) {
            CLASSNAMEi4 i4Var2 = i4Var;
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            return CLASSNAMEh4.h(i4Var, D5.b(i4Var.q0(), spliterator, this.l, this.m), true);
        } else {
            CLASSNAMEi4 i4Var3 = i4Var;
        }
        if (!CLASSNAMEg6.ORDERED.d(i4Var.r0())) {
            return CLASSNAMEh4.h(this, M0((E) i4Var.v0(spliterator), this.l, this.m, p0), true);
        }
        return (CLASSNAMEl3) new C5(this, i4Var, spliterator, vVar, this.l, this.m).invoke();
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        long p0 = i4Var.p0(spliterator);
        if (p0 <= 0) {
            Spliterator spliterator2 = spliterator;
        } else if (spliterator.hasCharacteristics(16384)) {
            long j = this.l;
            return new w6((E) i4Var.v0(spliterator), j, D5.d(j, this.m));
        }
        if (!CLASSNAMEg6.ORDERED.d(i4Var.r0())) {
            return M0((E) i4Var.v0(spliterator), this.l, this.m, p0);
        }
        return ((CLASSNAMEl3) new C5(this, i4Var, spliterator, CLASSNAMEv0.a, this.l, this.m).invoke()).spliterator();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        return new CLASSNAMEy5(this, t5Var);
    }

    /* access modifiers changed from: package-private */
    public E M0(E e, long j, long j2, long j3) {
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
        return new C6(e, j5, j4);
    }
}
