package j$.util.stream;

import j$.util.N;
import j$.util.Optional;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEa;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.m;
import j$.util.function.z;
import j$.util.y;
import j$.wrappers.J0;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.f3  reason: case insensitive filesystem */
abstract class CLASSNAMEf3 extends CLASSNAMEc implements Stream {
    CLASSNAMEf3(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    CLASSNAMEf3(y yVar, int i, boolean z) {
        super(yVar, i, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.y r2, j$.util.stream.CLASSNAMEn3 r3) {
        /*
            r1 = this;
        L_0x0000:
            boolean r0 = r3.o()
            if (r0 != 0) goto L_0x000c
            boolean r0 = r2.b(r3)
            if (r0 != 0) goto L_0x0000
        L_0x000c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEf3.A0(j$.util.y, j$.util.stream.n3):void");
    }

    public final Object B(Object obj, BiFunction biFunction, CLASSNAMEb bVar) {
        biFunction.getClass();
        bVar.getClass();
        return x0(new A2(CLASSNAMEf4.REFERENCE, bVar, biFunction, obj));
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEf4 B0() {
        return CLASSNAMEf4.REFERENCE;
    }

    public final U E(Function function) {
        function.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, function);
    }

    /* access modifiers changed from: package-private */
    public final y K0(CLASSNAMEz2 z2Var, j$.util.function.y yVar, boolean z) {
        return new M4(z2Var, yVar, z);
    }

    public final Stream T(Predicate predicate) {
        predicate.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.t, predicate);
    }

    public final Stream V(Consumer consumer) {
        consumer.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, 0, consumer);
    }

    public final boolean W(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEp1.x(predicate, CLASSNAMEl1.ALL))).booleanValue();
    }

    public final CLASSNAMEf1 X(Function function) {
        function.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, function);
    }

    public final boolean a(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEp1.x(predicate, CLASSNAMEl1.ANY))).booleanValue();
    }

    public final Object b0(J0 j0) {
        Object obj;
        if (!isParallel() || !j0.b().contains(CLASSNAMEh.CONCURRENT) || (C0() && !j0.b().contains(CLASSNAMEh.UNORDERED))) {
            j0.getClass();
            j$.util.function.y f = j0.f();
            obj = x0(new J2(CLASSNAMEf4.REFERENCE, j0.c(), j0.a(), f, j0));
        } else {
            obj = j0.f().get();
            forEach(new CLASSNAMEo(j0.a(), obj));
        }
        return j0.b().contains(CLASSNAMEh.IDENTITY_FINISH) ? obj : j0.e().apply(obj);
    }

    public final M0 c(Function function) {
        function.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, function);
    }

    public final long count() {
        return ((CLASSNAMEe1) g0(Y2.a)).sum();
    }

    public final boolean d0(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEp1.x(predicate, CLASSNAMEl1.NONE))).booleanValue();
    }

    public final Stream distinct() {
        return new CLASSNAMEs(this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.m | CLASSNAMEe4.t);
    }

    public void e(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, true));
    }

    public final Optional findAny() {
        return (Optional) x0(new CLASSNAMEd0(false, CLASSNAMEf4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    public final Optional findFirst() {
        return (Optional) x0(new CLASSNAMEd0(true, CLASSNAMEf4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, false));
    }

    public final CLASSNAMEf1 g0(B b) {
        b.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n, b);
    }

    public final Object i(j$.util.function.y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        yVar.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return x0(new A2(CLASSNAMEf4.REFERENCE, biConsumer2, biConsumer, yVar));
    }

    public final Iterator iterator() {
        return N.i(spliterator());
    }

    public final U j0(z zVar) {
        zVar.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n, zVar);
    }

    public final Object[] l(m mVar) {
        return CLASSNAMEy2.l(y0(mVar), mVar).q(mVar);
    }

    public final Stream limit(long j) {
        if (j >= 0) {
            return C3.i(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final M0 m(A a) {
        a.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n, a);
    }

    public final Object m0(Object obj, CLASSNAMEb bVar) {
        bVar.getClass();
        return x0(new A2(CLASSNAMEf4.REFERENCE, bVar, (BiFunction) bVar, obj));
    }

    public final Optional max(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEa(comparator, 0));
    }

    public final Optional min(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEa(comparator, 1));
    }

    public final Stream n(Function function) {
        function.getClass();
        return new CLASSNAMEb3(this, this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n, function, 0);
    }

    public final Stream o(Function function) {
        function.getClass();
        return new CLASSNAMEb3(this, this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.p | CLASSNAMEe4.n | CLASSNAMEe4.t, function, 1);
    }

    public final Stream skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : C3.i(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final Stream sorted() {
        return new N3(this);
    }

    public final Optional t(CLASSNAMEb bVar) {
        bVar.getClass();
        return (Optional) x0(new E2(CLASSNAMEf4.REFERENCE, bVar));
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt1 t0(long j, m mVar) {
        return CLASSNAMEy2.d(j, mVar);
    }

    public final Object[] toArray() {
        X2 x2 = X2.a;
        return CLASSNAMEy2.l(y0(x2), x2).q(x2);
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new CLASSNAMEa3(this, this, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.r);
    }

    /* access modifiers changed from: package-private */
    public final B1 z0(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar) {
        return CLASSNAMEy2.e(z2Var, yVar, z, mVar);
    }

    public final Stream sorted(Comparator comparator) {
        return new N3(this, comparator);
    }
}
