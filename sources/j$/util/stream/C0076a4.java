package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.a4  reason: case insensitive filesystem */
final class CLASSNAMEa4 extends CLASSNAMEd6 implements CLASSNAMEl3, CLASSNAMEg3 {
    private boolean g = false;

    CLASSNAMEa4() {
    }

    public CLASSNAMEl3 a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public void accept(Object obj) {
        super.accept(obj);
    }

    public CLASSNAMEl3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void forEach(Consumer consumer) {
        super.forEach(consumer);
    }

    public void j(Object[] objArr, int i) {
        super.j(objArr, i);
    }

    public void m() {
        this.g = false;
    }

    public void n(long j) {
        this.g = true;
        clear();
        u(j);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }

    public Object[] q(v vVar) {
        long count = count();
        if (count < NUM) {
            Object[] objArr = (Object[]) vVar.apply((int) count);
            j(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public /* synthetic */ CLASSNAMEl3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.n(this, j, j2, vVar);
    }

    public Spliterator spliterator() {
        return super.spliterator();
    }
}
