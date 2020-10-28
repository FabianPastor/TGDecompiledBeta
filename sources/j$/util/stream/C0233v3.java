package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.v3  reason: case insensitive filesystem */
final class CLASSNAMEv3 extends CLASSNAMEy3 implements CLASSNAMEh3 {
    CLASSNAMEv3(CLASSNAMEh3 h3Var, CLASSNAMEh3 h3Var2) {
        super(h3Var, h3Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void j(Double[] dArr, int i) {
        CLASSNAMEc3.e(this, dArr, i);
    }

    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.h(this, consumer);
    }

    /* renamed from: g */
    public /* synthetic */ CLASSNAMEh3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.k(this, j, j2, vVar);
    }

    public F spliterator() {
        return new M3(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m23spliterator() {
        return new M3(this);
    }
}
