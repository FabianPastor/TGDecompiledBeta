package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.D;
import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.u;
import j$.util.function.v;

final class L3 extends Y5 implements CLASSNAMEi3, CLASSNAMEe3 {
    private boolean g = false;

    L3() {
    }

    public D B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    /* renamed from: D */
    public /* synthetic */ void j(Integer[] numArr, int i) {
        CLASSNAMEc3.f(this, numArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEi3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.l(this, j, j2, vVar);
    }

    public CLASSNAMEi3 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEl3 m17a() {
        return this;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public void accept(int i) {
        super.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((int[]) obj, i);
    }

    public Object e() {
        return (int[]) super.e();
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void h(Object obj) {
        super.h((u) obj);
    }

    public void m() {
        this.g = false;
    }

    public void n(long j) {
        this.g = true;
        clear();
        x(j);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }

    public /* synthetic */ Object[] q(v vVar) {
        return CLASSNAMEc3.d(this, vVar);
    }

    public F spliterator() {
        return super.spliterator();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m18spliterator() {
        return super.spliterator();
    }
}
