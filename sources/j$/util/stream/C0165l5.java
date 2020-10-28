package j$.util.stream;

import j$.util.Optional;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEc;
import j$.util.function.CLASSNAMEd;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.n;
import j$.util.function.v;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.l5  reason: case insensitive filesystem */
abstract class CLASSNAMEl5 extends CLASSNAMEh1 implements Stream {
    CLASSNAMEl5(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    CLASSNAMEl5(CLASSNAMEh1 h1Var, int i) {
        super(h1Var, i);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEh6 A0() {
        return CLASSNAMEh6.REFERENCE;
    }

    public final L1 B(Function function) {
        function.getClass();
        return new CLASSNAMEh5(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, function);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(CLASSNAMEi4 i4Var, E e, boolean z) {
        return new H6(i4Var, e, z);
    }

    public final Stream P(Predicate predicate) {
        predicate.getClass();
        return new T4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.y, predicate);
    }

    public final Stream R(Consumer consumer) {
        consumer.getClass();
        return new Q4(this, this, CLASSNAMEh6.REFERENCE, 0, consumer);
    }

    public final Object S(CLASSNAMEn1 n1Var) {
        Object obj;
        if (!isParallel() || !n1Var.characteristics().contains(CLASSNAMEm1.CONCURRENT) || (B0() && !n1Var.characteristics().contains(CLASSNAMEm1.UNORDERED))) {
            n1Var.getClass();
            E supplier = n1Var.supplier();
            obj = w0(new CLASSNAMEv4(CLASSNAMEh6.REFERENCE, n1Var.combiner(), n1Var.accumulator(), supplier, n1Var));
        } else {
            obj = n1Var.supplier().get();
            forEach(new CLASSNAMEs0(n1Var.accumulator(), obj));
        }
        return n1Var.characteristics().contains(CLASSNAMEm1.IDENTITY_FINISH) ? obj : n1Var.finisher().apply(obj);
    }

    public final boolean T(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEc3.u(predicate, Z2.ALL))).booleanValue();
    }

    public final T2 U(Function function) {
        function.getClass();
        return new O4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, function);
    }

    public final boolean a(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEc3.u(predicate, Z2.ANY))).booleanValue();
    }

    public final boolean b0(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEc3.u(predicate, Z2.NONE))).booleanValue();
    }

    public final long count() {
        return ((S2) d0(CLASSNAMEq0.a)).sum();
    }

    public final CLASSNAMEx2 d(Function function) {
        function.getClass();
        return new CLASSNAMEf5(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, function);
    }

    public final T2 d0(ToLongFunction toLongFunction) {
        toLongFunction.getClass();
        return new Z4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s, toLongFunction);
    }

    public final Stream distinct() {
        return new CLASSNAMEr1(this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.r | CLASSNAMEg6.y);
    }

    public void f(Consumer consumer) {
        consumer.getClass();
        w0(new W1(consumer, true));
    }

    public final L1 f0(ToDoubleFunction toDoubleFunction) {
        toDoubleFunction.getClass();
        return new CLASSNAMEb5(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s, toDoubleFunction);
    }

    public final Optional findAny() {
        return (Optional) w0(new M1(false, CLASSNAMEh6.REFERENCE, Optional.empty(), CLASSNAMEg1.a, W0.a));
    }

    public final Optional findFirst() {
        return (Optional) w0(new M1(true, CLASSNAMEh6.REFERENCE, Optional.empty(), CLASSNAMEg1.a, W0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        w0(new W1(consumer, false));
    }

    public final Iterator iterator() {
        return V.i(spliterator());
    }

    public final Object j(E e, BiConsumer biConsumer, BiConsumer biConsumer2) {
        e.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return w0(new CLASSNAMEx4(CLASSNAMEh6.REFERENCE, biConsumer2, biConsumer, e));
    }

    public final Object l0(Object obj, n nVar) {
        nVar.getClass();
        return w0(new CLASSNAMEr4(CLASSNAMEh6.REFERENCE, nVar, nVar, obj));
    }

    public final Stream limit(long j) {
        if (j >= 0) {
            return D5.i(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEx2 m(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new X4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s, toIntFunction);
    }

    public final Optional max(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEd(comparator));
    }

    public final Optional min(Comparator comparator) {
        comparator.getClass();
        return t(new CLASSNAMEc(comparator));
    }

    public final Stream n(Function function) {
        function.getClass();
        return new V4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s, function);
    }

    public final Stream p(Function function) {
        function.getClass();
        return new CLASSNAMEd5(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, function);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEg3 s0(long j, v vVar) {
        return CLASSNAMEh4.d(j, vVar);
    }

    public final Stream skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : D5.i(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final Stream sorted() {
        return new O5(this);
    }

    public final Optional t(n nVar) {
        nVar.getClass();
        return (Optional) w0(new CLASSNAMEt4(CLASSNAMEh6.REFERENCE, nVar));
    }

    public final Object[] toArray() {
        CLASSNAMEr0 r0Var = CLASSNAMEr0.a;
        return CLASSNAMEh4.l(x0(r0Var), r0Var).q(r0Var);
    }

    public final Object[] toArray(v vVar) {
        return CLASSNAMEh4.l(x0(vVar), vVar).q(vVar);
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new R4(this, this, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.w);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 y0(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar) {
        return CLASSNAMEh4.e(i4Var, spliterator, z, vVar);
    }

    public final Object z(Object obj, BiFunction biFunction, n nVar) {
        biFunction.getClass();
        nVar.getClass();
        return w0(new CLASSNAMEr4(CLASSNAMEh6.REFERENCE, nVar, biFunction, obj));
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r2, j$.util.stream.CLASSNAMEt5 r3) {
        /*
            r1 = this;
        L_0x0000:
            boolean r0 = r3.p()
            if (r0 != 0) goto L_0x000c
            boolean r0 = r2.b(r3)
            if (r0 != 0) goto L_0x0000
        L_0x000c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEl5.z0(j$.util.Spliterator, j$.util.stream.t5):void");
    }

    public final Stream sorted(Comparator comparator) {
        return new O5(this, comparator);
    }
}
