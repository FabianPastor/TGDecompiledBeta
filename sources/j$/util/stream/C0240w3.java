package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.w3  reason: case insensitive filesystem */
final class CLASSNAMEw3 extends CLASSNAMEy3 implements CLASSNAMEi3 {
    CLASSNAMEw3(CLASSNAMEi3 i3Var, CLASSNAMEi3 i3Var2) {
        super(i3Var, i3Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void j(Integer[] numArr, int i) {
        CLASSNAMEc3.f(this, numArr, i);
    }

    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.i(this, consumer);
    }

    /* renamed from: g */
    public /* synthetic */ CLASSNAMEi3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.l(this, j, j2, vVar);
    }

    public F spliterator() {
        return new N3(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m25spliterator() {
        return new N3(this);
    }
}
