package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
final class CLASSNAMEt2 extends CLASSNAMEa4 implements A1, CLASSNAMEs1 {
    CLASSNAMEt2() {
    }

    public A1 a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public void accept(Object obj) {
        super.accept(obj);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void forEach(Consumer consumer) {
        super.forEach(consumer);
    }

    public void i(Object[] objArr, int i) {
        super.i(objArr, i);
    }

    public void m() {
    }

    public void n(long j) {
        clear();
        u(j);
    }

    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public Object[] q(m mVar) {
        long count = count();
        if (count < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public /* synthetic */ A1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.q(this, j, j2, mVar);
    }

    public u spliterator() {
        return super.spliterator();
    }
}