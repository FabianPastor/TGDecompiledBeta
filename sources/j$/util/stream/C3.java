package j$.util.stream;

import j$.util.C;
import j$.util.CLASSNAMEk;
import j$.util.F;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.v;

final class C3 extends W5 implements CLASSNAMEh3, CLASSNAMEd3 {
    private boolean g = false;

    C3() {
    }

    public C B() {
        return super.spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEc3.a(this, d);
    }

    /* renamed from: D */
    public /* synthetic */ void j(Double[] dArr, int i) {
        CLASSNAMEc3.e(this, dArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ CLASSNAMEh3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.k(this, j, j2, vVar);
    }

    public CLASSNAMEh3 a() {
        return this;
    }

    /* renamed from: a  reason: collision with other method in class */
    public CLASSNAMEl3 m10a() {
        return this;
    }

    public void accept(double d) {
        super.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public void d(Object obj, int i) {
        super.d((double[]) obj, i);
    }

    public Object e() {
        return (double[]) super.e();
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void h(Object obj) {
        super.h((q) obj);
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
    public Spliterator m11spliterator() {
        return super.spliterator();
    }
}
