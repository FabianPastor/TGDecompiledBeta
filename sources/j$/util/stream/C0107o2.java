package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.function.q;
import j$.util.w;
import j$.util.x;
import j$.util.y;

/* renamed from: j$.util.stream.o2  reason: case insensitive filesystem */
final class CLASSNAMEo2 extends Z3 implements CLASSNAMEz1, CLASSNAMEs1 {
    CLASSNAMEo2() {
    }

    public w B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEp1.j(this, lArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEz1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.p(this, j, j2, mVar);
    }

    public B1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEz1 m539a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public void accept(long j) {
        super.accept(j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((long[]) obj, i);
    }

    public Object e() {
        return (long[]) super.e();
    }

    public void g(Object obj) {
        super.g((q) obj);
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
        return CLASSNAMEp1.g(this, mVar);
    }

    public x spliterator() {
        return super.spliterator();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m540spliterator() {
        return super.spliterator();
    }
}
