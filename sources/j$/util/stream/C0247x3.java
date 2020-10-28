package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.x3  reason: case insensitive filesystem */
final class CLASSNAMEx3 extends CLASSNAMEy3 implements CLASSNAMEj3 {
    CLASSNAMEx3(CLASSNAMEj3 j3Var, CLASSNAMEj3 j3Var2) {
        super(j3Var, j3Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void j(Long[] lArr, int i) {
        CLASSNAMEc3.g(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.j(this, consumer);
    }

    /* renamed from: g */
    public /* synthetic */ CLASSNAMEj3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.m(this, j, j2, vVar);
    }

    public F spliterator() {
        return new O3(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m26spliterator() {
        return new O3(this);
    }
}
