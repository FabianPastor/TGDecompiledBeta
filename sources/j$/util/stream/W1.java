package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.m;
import j$.util.u;
import j$.util.x;
import j$.util.y;

final class W1 extends V3 implements CLASSNAMEv1, CLASSNAMEq1 {
    W1() {
    }

    public u B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEp1.a(this, d);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEp1.h(this, dArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEv1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.n(this, j, j2, mVar);
    }

    public B1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEv1 m533a() {
        return this;
    }

    public void accept(double d) {
        super.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((double[]) obj, i);
    }

    public Object e() {
        return (double[]) super.e();
    }

    public void g(Object obj) {
        super.g((f) obj);
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
    public y m534spliterator() {
        return super.spliterator();
    }
}
