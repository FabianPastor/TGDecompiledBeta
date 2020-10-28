package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.E;
import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.y;

final class U3 extends CLASSNAMEa6 implements CLASSNAMEj3, CLASSNAMEf3 {
    private boolean g = false;

    U3() {
    }

    public E B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    /* renamed from: D */
    public /* synthetic */ void j(Long[] lArr, int i) {
        CLASSNAMEc3.g(this, lArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEj3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.m(this, j, j2, vVar);
    }

    public CLASSNAMEj3 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEl3 m21a() {
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

    public void accept(long j) {
        super.accept(j);
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((long[]) obj, i);
    }

    public Object e() {
        return (long[]) super.e();
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void h(Object obj) {
        super.h((y) obj);
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
    public Spliterator m22spliterator() {
        return super.spliterator();
    }
}
