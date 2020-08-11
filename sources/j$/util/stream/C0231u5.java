package j$.util.stream;

import j$.util.CLASSNAMEz;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.C;
import j$.util.function.CLASSNAMEn;
import j$.util.function.CLASSNAMEo;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.V;
import j$.util.k0;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.u5  reason: case insensitive filesystem */
abstract class CLASSNAMEu5 extends CLASSNAMEh1 implements Stream {
    CLASSNAMEu5(Spliterator spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    CLASSNAMEu5(CLASSNAMEh1 upstream, int opFlags) {
        super(upstream, opFlags);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEv6 A0() {
        return CLASSNAMEv6.REFERENCE;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 y0(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree, C c) {
        return CLASSNAMEp4.f(helper, spliterator, flattenTree, c);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator M0(CLASSNAMEq4 ph, V v, boolean isParallel) {
        return new a7(ph, v, isParallel);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator F0(V v) {
        return new I6(v);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r2, j$.util.stream.G5 r3) {
        /*
            r1 = this;
        L_0x0000:
            boolean r0 = r3.u()
            if (r0 != 0) goto L_0x000c
            boolean r0 = r2.a(r3)
            if (r0 != 0) goto L_0x0000
        L_0x000c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEu5.z0(j$.util.Spliterator, j$.util.stream.G5):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEk3 s0(long exactSizeIfKnown, C c) {
        return CLASSNAMEp4.e(exactSizeIfKnown, c);
    }

    public final Iterator iterator() {
        return k0.i(spliterator());
    }

    /* renamed from: Q0 */
    public Stream unordered() {
        if (!B0()) {
            return this;
        }
        return new CLASSNAMEa5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.w);
    }

    public final Stream R(Predicate predicate) {
        predicate.getClass();
        return new CLASSNAMEc5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.y, predicate);
    }

    public final Stream p(Function function) {
        function.getClass();
        return new CLASSNAMEe5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s, function);
    }

    public final A2 o(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new CLASSNAMEg5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s, toIntFunction);
    }

    public final W2 h0(ToLongFunction toLongFunction) {
        toLongFunction.getClass();
        return new CLASSNAMEi5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s, toLongFunction);
    }

    public final M1 k0(ToDoubleFunction toDoubleFunction) {
        toDoubleFunction.getClass();
        return new CLASSNAMEk5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s, toDoubleFunction);
    }

    public final Stream r(Function function) {
        function.getClass();
        return new CLASSNAMEm5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, function);
    }

    public final A2 h(Function function) {
        function.getClass();
        return new CLASSNAMEo5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, function);
    }

    public final M1 C(Function function) {
        function.getClass();
        return new CLASSNAMEq5(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, function);
    }

    public final W2 Y(Function function) {
        function.getClass();
        return new X4(this, this, CLASSNAMEv6.REFERENCE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, function);
    }

    public final Stream U(Consumer consumer) {
        consumer.getClass();
        return new Z4(this, this, CLASSNAMEv6.REFERENCE, 0, consumer);
    }

    public final Stream distinct() {
        return CLASSNAMEs1.a(this);
    }

    public final Stream sorted() {
        return CLASSNAMEh6.d(this);
    }

    public final Stream sorted(Comparator comparator) {
        return CLASSNAMEh6.e(this, comparator);
    }

    public final Stream limit(long maxSize) {
        if (maxSize >= 0) {
            return Q5.m(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final Stream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return Q5.m(this, n, -1);
        }
    }

    public void forEach(Consumer consumer) {
        w0(CLASSNAMEc2.d(consumer, false));
    }

    public void j(Consumer consumer) {
        w0(CLASSNAMEc2.d(consumer, true));
    }

    public final Object[] toArray(C c) {
        C rawGenerator = c;
        return CLASSNAMEp4.n(x0(rawGenerator), rawGenerator).x(rawGenerator);
    }

    static /* synthetic */ Object[] P0(int x$0) {
        return new Object[x$0];
    }

    public final Object[] toArray() {
        return toArray(CLASSNAMEq0.a);
    }

    public final boolean a(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEf3.h(predicate, CLASSNAMEc3.ANY))).booleanValue();
    }

    public final boolean X(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEf3.h(predicate, CLASSNAMEc3.ALL))).booleanValue();
    }

    public final boolean f0(Predicate predicate) {
        return ((Boolean) w0(CLASSNAMEf3.h(predicate, CLASSNAMEc3.NONE))).booleanValue();
    }

    public final CLASSNAMEz findFirst() {
        return (CLASSNAMEz) w0(U1.d(true));
    }

    public final CLASSNAMEz findAny() {
        return (CLASSNAMEz) w0(U1.d(false));
    }

    public final Object o0(Object identity, CLASSNAMEo oVar) {
        return w0(V4.m(identity, oVar, oVar));
    }

    public final CLASSNAMEz u(CLASSNAMEo oVar) {
        return (CLASSNAMEz) w0(V4.j(oVar));
    }

    public final Object A(Object identity, BiFunction biFunction, CLASSNAMEo oVar) {
        return w0(V4.m(identity, biFunction, oVar));
    }

    public final Object W(CLASSNAMEn1 n1Var) {
        A container;
        if (!isParallel() || !n1Var.characteristics().contains(CLASSNAMEm1.CONCURRENT) || (B0() && !n1Var.characteristics().contains(CLASSNAMEm1.UNORDERED))) {
            container = w0(V4.l(n1Var));
        } else {
            container = n1Var.c().get();
            forEach(new CLASSNAMEo0(n1Var.a(), container));
        }
        if (n1Var.characteristics().contains(CLASSNAMEm1.IDENTITY_FINISH)) {
            return container;
        }
        return n1Var.d().apply(container);
    }

    public final Object m(V v, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return w0(V4.k(v, biConsumer, biConsumer2));
    }

    public final CLASSNAMEz max(Comparator comparator) {
        return u(CLASSNAMEn.c(comparator));
    }

    public final CLASSNAMEz min(Comparator comparator) {
        return u(CLASSNAMEn.d(comparator));
    }

    static /* synthetic */ long O0() {
        return 1;
    }

    public final long count() {
        return ((V2) h0(CLASSNAMEp0.a)).sum();
    }
}
