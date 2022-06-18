package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.m;
import j$.util.t;
import j$.util.u;
import j$.util.w;

final class V1 extends U3 implements CLASSNAMEu1, CLASSNAMEp1 {
    V1() {
    }

    public t B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEo1.a(this, d);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEo1.h(this, dArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEu1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.n(this, j, j2, mVar);
    }

    public A1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEu1 m6a() {
        return this;
    }

    public void accept(double d) {
        super.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
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
        return CLASSNAMEo1.g(this, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m7spliterator() {
        return super.spliterator();
    }

    public u spliterator() {
        return super.spliterator();
    }
}
