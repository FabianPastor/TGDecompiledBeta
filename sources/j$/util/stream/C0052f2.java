package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.v;
import j$.util.x;
import j$.util.y;

/* renamed from: j$.util.stream.f2  reason: case insensitive filesystem */
final class CLASSNAMEf2 extends X3 implements CLASSNAMEx1, CLASSNAMEr1 {
    CLASSNAMEf2() {
    }

    public v B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEp1.b(this, num);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEp1.i(this, numArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEx1 r(long j, long j2, l lVar) {
        return CLASSNAMEp1.o(this, j, j2, lVar);
    }

    public B1 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEx1 m13a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public void accept(int i) {
        super.accept(i);
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
        super.d((int[]) obj, i);
    }

    public Object e() {
        return (int[]) super.e();
    }

    public void g(Object obj) {
        super.g((k) obj);
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

    public /* synthetic */ Object[] q(l lVar) {
        return CLASSNAMEp1.g(this, lVar);
    }

    public x spliterator() {
        return super.spliterator();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m14spliterator() {
        return super.spliterator();
    }
}
