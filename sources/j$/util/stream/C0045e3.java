package j$.util.stream;

import j$.util.L;
import j$.util.Optional;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
import j$.util.function.b;
import j$.util.function.m;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.u;
import j$.wrappers.J0;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.e3  reason: case insensitive filesystem */
abstract class CLASSNAMEe3 extends CLASSNAMEc implements Stream {
    CLASSNAMEe3(CLASSNAMEc cVar, int i) {
        super(cVar, i);
    }

    CLASSNAMEe3(u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void A0(j$.util.u r2, j$.util.stream.CLASSNAMEm3 r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEe3.A0(j$.util.u, j$.util.stream.m3):void");
    }

    public final Object B(Object obj, BiFunction biFunction, b bVar) {
        biFunction.getClass();
        bVar.getClass();
        return x0(new CLASSNAMEz2(CLASSNAMEe4.REFERENCE, bVar, biFunction, obj));
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEe4 B0() {
        return CLASSNAMEe4.REFERENCE;
    }

    public final U E(Function function) {
        function.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, function);
    }

    /* access modifiers changed from: package-private */
    public final u K0(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        return new L4(y2Var, yVar, z);
    }

    public final Stream T(Predicate predicate) {
        predicate.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.t, predicate);
    }

    public final Stream V(Consumer consumer) {
        consumer.getClass();
        return new L(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, 0, consumer);
    }

    public final boolean W(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEo1.x(predicate, CLASSNAMEk1.ALL))).booleanValue();
    }

    public final CLASSNAMEe1 X(Function function) {
        function.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, function);
    }

    public final boolean a(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEo1.x(predicate, CLASSNAMEk1.ANY))).booleanValue();
    }

    public final Object b0(J0 j0) {
        Object obj;
        if (!isParallel() || !j0.b().contains(CLASSNAMEh.CONCURRENT) || (C0() && !j0.b().contains(CLASSNAMEh.UNORDERED))) {
            j0.getClass();
            y f = j0.f();
            obj = x0(new I2(CLASSNAMEe4.REFERENCE, j0.c(), j0.a(), f, j0));
        } else {
            obj = j0.f().get();
            forEach(new CLASSNAMEo(j0.a(), obj));
        }
        return j0.b().contains(CLASSNAMEh.IDENTITY_FINISH) ? obj : j0.e().apply(obj);
    }

    public final IntStream c(Function function) {
        function.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, function);
    }

    public final long count() {
        return ((CLASSNAMEd1) g0(X2.a)).sum();
    }

    public final boolean d0(Predicate predicate) {
        return ((Boolean) x0(CLASSNAMEo1.x(predicate, CLASSNAMEk1.NONE))).booleanValue();
    }

    public final Stream distinct() {
        return new CLASSNAMEs(this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.m | CLASSNAMEd4.t);
    }

    public void e(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, true));
    }

    public final Optional findAny() {
        return (Optional) x0(new CLASSNAMEd0(false, CLASSNAMEe4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    public final Optional findFirst() {
        return (Optional) x0(new CLASSNAMEd0(true, CLASSNAMEe4.REFERENCE, Optional.empty(), V.a, CLASSNAMEc0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        x0(new CLASSNAMEn0(consumer, false));
    }

    public final CLASSNAMEe1 g0(A a) {
        a.getClass();
        return new N(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n, a);
    }

    public final Object i(y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        yVar.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return x0(new CLASSNAMEz2(CLASSNAMEe4.REFERENCE, biConsumer2, biConsumer, yVar));
    }

    public final Iterator iterator() {
        return L.i(spliterator());
    }

    public final U j0(z zVar) {
        zVar.getClass();
        return new K(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n, zVar);
    }

    public final Object[] l(m mVar) {
        return CLASSNAMEx2.l(y0(mVar), mVar).q(mVar);
    }

    public final Stream limit(long j) {
        if (j >= 0) {
            return B3.i(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final IntStream m(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new M(this, (CLASSNAMEc) this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n, toIntFunction);
    }

    public final Object m0(Object obj, b bVar) {
        bVar.getClass();
        return x0(new CLASSNAMEz2(CLASSNAMEe4.REFERENCE, bVar, (BiFunction) bVar, obj));
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
        return new CLASSNAMEa3(this, this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n, function, 0);
    }

    public final Stream o(Function function) {
        function.getClass();
        return new CLASSNAMEa3(this, this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.p | CLASSNAMEd4.n | CLASSNAMEd4.t, function, 1);
    }

    public final Stream skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.i(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final Stream sorted() {
        return new M3(this);
    }

    public final Optional t(b bVar) {
        bVar.getClass();
        return (Optional) x0(new D2(CLASSNAMEe4.REFERENCE, bVar));
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEs1 t0(long j, m mVar) {
        return CLASSNAMEx2.d(j, mVar);
    }

    public final Object[] toArray() {
        W2 w2 = W2.a;
        return CLASSNAMEx2.l(y0(w2), w2).q(w2);
    }

    public CLASSNAMEg unordered() {
        return !C0() ? this : new Z2(this, this, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.r);
    }

    /* access modifiers changed from: package-private */
    public final A1 z0(CLASSNAMEy2 y2Var, u uVar, boolean z, m mVar) {
        return CLASSNAMEx2.e(y2Var, uVar, z, mVar);
    }

    public final Stream sorted(Comparator comparator) {
        return new M3(this, comparator);
    }
}
