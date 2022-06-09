package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.function.q;
import j$.util.u;
import j$.util.v;
import j$.util.w;

/* renamed from: j$.util.stream.n2  reason: case insensitive filesystem */
final class CLASSNAMEn2 extends Y3 implements CLASSNAMEy1, CLASSNAMEr1 {
    CLASSNAMEn2() {
    }

    public v B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEo1.c(this, l);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEo1.j(this, lArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEy1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.p(this, j, j2, mVar);
    }

    public A1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEy1 m579a() {
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

    public void accept(long j) {
        super.accept(j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public CLASSNAMEz1 b(int i) {
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
        return CLASSNAMEo1.g(this, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m580spliterator() {
        return super.spliterator();
    }

    public u spliterator() {
        return super.spliterator();
    }
}
