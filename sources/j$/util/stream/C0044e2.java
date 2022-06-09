package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

/* renamed from: j$.util.stream.e2  reason: case insensitive filesystem */
final class CLASSNAMEe2 extends W3 implements CLASSNAMEw1, CLASSNAMEq1 {
    CLASSNAMEe2() {
    }

    public u.a B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEo1.i(this, numArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEw1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.o(this, j, j2, mVar);
    }

    public A1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEw1 m576a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public void accept(int i) {
        super.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public CLASSNAMEz1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((int[]) obj, i);
    }

    public Object e() {
        return (int[]) super.e();
    }

    public void g(Object obj) {
        super.g((l) obj);
    }

    public void m() {
    }

    public void n(long j) {
        clear();
        x(j);
    }

    public /* synthetic */ boolean o() {
        return false;
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public /* synthetic */ Object[] q(m mVar) {
        return CLASSNAMEo1.g(this, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m577spliterator() {
        return super.spliterator();
    }

    public u spliterator() {
        return super.spliterator();
    }
}
