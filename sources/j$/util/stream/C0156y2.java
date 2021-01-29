package j$.util.stream;

import j$.util.Optional;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.C;
import j$.util.function.CLASSNAMEc;
import j$.util.function.CLASSNAMEd;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.J;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.n;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEm1;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.CLASSNAMEw1;
import j$.util.stream.CLASSNAMEz1;
import j$.util.stream.D1;
import j$.util.stream.R1;
import j$.util.v;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.util.stream.y2  reason: case insensitive filesystem */
abstract class CLASSNAMEy2<P_IN, P_OUT> extends CLASSNAMEh1<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {

    /* renamed from: j$.util.stream.y2$a */
    class a extends D1.i<P_OUT> {
        final /* synthetic */ Function l;

        /* renamed from: j$.util.stream.y2$a$a  reason: collision with other inner class name */
        class CLASSNAMEa extends A2.d<P_OUT, Long> {
            C b;

            CLASSNAMEa(A2 a2) {
                super(a2);
                this.b = new K0(a2);
            }

            public void accept(Object obj) {
                H1 h1 = (H1) a.this.l.apply(obj);
                if (h1 != null) {
                    try {
                        h1.sequential().f(this.b);
                    } catch (Throwable unused) {
                    }
                }
                if (h1 != null) {
                    h1.close();
                    return;
                }
                return;
                throw th;
            }

            public void n(long j) {
                this.a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        a(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Function function) {
            super(h1Var, u2, i);
            this.l = function;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new CLASSNAMEa(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$b */
    class b extends m<P_OUT, P_OUT> {
        final /* synthetic */ Consumer l;

        /* renamed from: j$.util.stream.y2$b$a */
        class a extends A2.d<P_OUT, P_OUT> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                b.this.l.accept(obj);
                this.a.accept(obj);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        b(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Consumer consumer) {
            super(h1Var, u2, i);
            this.l = consumer;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$c */
    class c extends m<P_OUT, P_OUT> {
        final /* synthetic */ Predicate l;

        /* renamed from: j$.util.stream.y2$c$a */
        class a extends A2.d<P_OUT, P_OUT> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                if (c.this.l.test(obj)) {
                    this.a.accept(obj);
                }
            }

            public void n(long j) {
                this.a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        c(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Predicate predicate) {
            super(h1Var, u2, i);
            this.l = predicate;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$d */
    class d extends m<P_OUT, R> {
        final /* synthetic */ Function l;

        /* renamed from: j$.util.stream.y2$d$a */
        class a extends A2.d<P_OUT, R> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                this.a.accept(d.this.l.apply(obj));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        d(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Function function) {
            super(h1Var, u2, i);
            this.l = function;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$e */
    class e extends CLASSNAMEz1.k<P_OUT> {
        final /* synthetic */ ToIntFunction l;

        /* renamed from: j$.util.stream.y2$e$a */
        class a extends A2.d<P_OUT, Integer> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                this.a.accept(e.this.l.applyAsInt(obj));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        e(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, ToIntFunction toIntFunction) {
            super(h1Var, u2, i);
            this.l = toIntFunction;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$f */
    class f extends D1.i<P_OUT> {
        final /* synthetic */ ToLongFunction l;

        /* renamed from: j$.util.stream.y2$f$a */
        class a extends A2.d<P_OUT, Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                this.a.accept(f.this.l.applyAsLong(obj));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        f(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, ToLongFunction toLongFunction) {
            super(h1Var, u2, i);
            this.l = toLongFunction;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$g */
    class g extends CLASSNAMEp1.i<P_OUT> {
        final /* synthetic */ ToDoubleFunction l;

        /* renamed from: j$.util.stream.y2$g$a */
        class a extends A2.d<P_OUT, Double> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                this.a.accept(g.this.l.applyAsDouble(obj));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        g(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, ToDoubleFunction toDoubleFunction) {
            super(h1Var, u2, i);
            this.l = toDoubleFunction;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$h */
    class h extends m<P_OUT, R> {
        final /* synthetic */ Function l;

        /* renamed from: j$.util.stream.y2$h$a */
        class a extends A2.d<P_OUT, R> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(Object obj) {
                Stream stream = (Stream) h.this.l.apply(obj);
                if (stream != null) {
                    try {
                        ((Stream) stream.sequential()).forEach(this.a);
                    } catch (Throwable unused) {
                    }
                }
                if (stream != null) {
                    stream.close();
                    return;
                }
                return;
                throw th;
            }

            public void n(long j) {
                this.a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        h(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Function function) {
            super(h1Var, u2, i);
            this.l = function;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$i */
    class i extends CLASSNAMEz1.k<P_OUT> {
        final /* synthetic */ Function l;

        /* renamed from: j$.util.stream.y2$i$a */
        class a extends A2.d<P_OUT, Integer> {
            w b;

            a(A2 a2) {
                super(a2);
                this.b = new CLASSNAMEc(a2);
            }

            public void accept(Object obj) {
                C1 c1 = (C1) i.this.l.apply(obj);
                if (c1 != null) {
                    try {
                        c1.sequential().Q(this.b);
                    } catch (Throwable unused) {
                    }
                }
                if (c1 != null) {
                    c1.close();
                    return;
                }
                return;
                throw th;
            }

            public void n(long j) {
                this.a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        i(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Function function) {
            super(h1Var, u2, i);
            this.l = function;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$j */
    class j extends CLASSNAMEp1.i<P_OUT> {
        final /* synthetic */ Function l;

        /* renamed from: j$.util.stream.y2$j$a */
        class a extends A2.d<P_OUT, Double> {
            q b;

            a(A2 a2) {
                super(a2);
                this.b = new N(a2);
            }

            public void accept(Object obj) {
                CLASSNAMEs1 s1Var = (CLASSNAMEs1) j.this.l.apply(obj);
                if (s1Var != null) {
                    try {
                        s1Var.sequential().l(this.b);
                    } catch (Throwable unused) {
                    }
                }
                if (s1Var != null) {
                    s1Var.close();
                    return;
                }
                return;
                throw th;
            }

            public void n(long j) {
                this.a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        j(CLASSNAMEy2 y2Var, CLASSNAMEh1 h1Var, U2 u2, int i, Function function) {
            super(h1Var, u2, i);
            this.l = function;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.y2$k */
    static class k<E_IN, E_OUT> extends CLASSNAMEy2<E_IN, E_OUT> {
        k(Spliterator spliterator, int i, boolean z) {
            super(spliterator, i, z);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final A2 G0(int i, A2 a2) {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer consumer) {
            if (!isParallel()) {
                I0().forEachRemaining(consumer);
            } else {
                CLASSNAMEy2.super.forEach(consumer);
            }
        }

        public void g(Consumer consumer) {
            if (!isParallel()) {
                I0().forEachRemaining(consumer);
                return;
            }
            consumer.getClass();
            w0(new CLASSNAMEw1.d(consumer, true));
        }
    }

    /* renamed from: j$.util.stream.y2$l */
    static abstract class l<E_IN, E_OUT> extends CLASSNAMEy2<E_IN, E_OUT> {
        l(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return true;
        }
    }

    /* renamed from: j$.util.stream.y2$m */
    static abstract class m<E_IN, E_OUT> extends CLASSNAMEy2<E_IN, E_OUT> {
        m(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return false;
        }
    }

    CLASSNAMEy2(Spliterator spliterator, int i2, boolean z) {
        super(spliterator, i2, z);
    }

    CLASSNAMEy2(CLASSNAMEh1 h1Var, int i2) {
        super(h1Var, i2);
    }

    /* access modifiers changed from: package-private */
    public final U2 A0() {
        return U2.REFERENCE;
    }

    public final CLASSNAMEs1 B(Function function) {
        function.getClass();
        return new j(this, this, U2.REFERENCE, T2.p | T2.n | T2.t, function);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(T1 t1, J j2, boolean z) {
        return new e3(t1, j2, z);
    }

    public final Stream P(Predicate predicate) {
        predicate.getClass();
        return new c(this, this, U2.REFERENCE, T2.t, predicate);
    }

    public final Stream S(Consumer consumer) {
        consumer.getClass();
        return new b(this, this, U2.REFERENCE, 0, consumer);
    }

    public final Object T(CLASSNAMEm1 m1Var) {
        Object obj;
        if (!isParallel() || !m1Var.characteristics().contains(CLASSNAMEm1.a.CONCURRENT) || (B0() && !m1Var.characteristics().contains(CLASSNAMEm1.a.UNORDERED))) {
            m1Var.getClass();
            J supplier = m1Var.supplier();
            obj = w0(new CLASSNAMEg2(U2.REFERENCE, m1Var.combiner(), m1Var.accumulator(), supplier, m1Var));
        } else {
            obj = m1Var.supplier().get();
            forEach(new CLASSNAMEs0(m1Var.accumulator(), obj));
        }
        return m1Var.characteristics().contains(CLASSNAMEm1.a.IDENTITY_FINISH) ? obj : m1Var.finisher().apply(obj);
    }

    public final boolean U(Predicate predicate) {
        return ((Boolean) w0(Q1.u(predicate, N1.ALL))).booleanValue();
    }

    public final H1 V(Function function) {
        function.getClass();
        return new a(this, this, U2.REFERENCE, T2.p | T2.n | T2.t, function);
    }

    public final boolean a(Predicate predicate) {
        return ((Boolean) w0(Q1.u(predicate, N1.ANY))).booleanValue();
    }

    public final boolean c0(Predicate predicate) {
        return ((Boolean) w0(Q1.u(predicate, N1.NONE))).booleanValue();
    }

    public final long count() {
        return ((D1) e0(CLASSNAMEq0.a)).sum();
    }

    public final Stream distinct() {
        return new CLASSNAMEo1(this, U2.REFERENCE, T2.m | T2.t);
    }

    public final C1 e(Function function) {
        function.getClass();
        return new i(this, this, U2.REFERENCE, T2.p | T2.n | T2.t, function);
    }

    public final H1 e0(ToLongFunction toLongFunction) {
        toLongFunction.getClass();
        return new f(this, this, U2.REFERENCE, T2.p | T2.n, toLongFunction);
    }

    public final Optional findAny() {
        return (Optional) w0(new CLASSNAMEt1(false, U2.REFERENCE, Optional.empty(), CLASSNAMEg1.a, W0.a));
    }

    public final Optional findFirst() {
        return (Optional) w0(new CLASSNAMEt1(true, U2.REFERENCE, Optional.empty(), CLASSNAMEg1.a, W0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        w0(new CLASSNAMEw1.d(consumer, false));
    }

    public void g(Consumer consumer) {
        consumer.getClass();
        w0(new CLASSNAMEw1.d(consumer, true));
    }

    public final CLASSNAMEs1 h0(ToDoubleFunction toDoubleFunction) {
        toDoubleFunction.getClass();
        return new g(this, this, U2.REFERENCE, T2.p | T2.n, toDoubleFunction);
    }

    public final Iterator iterator() {
        return v.i(spliterator());
    }

    public final Object k(J j2, BiConsumer biConsumer, BiConsumer biConsumer2) {
        j2.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return w0(new CLASSNAMEi2(U2.REFERENCE, biConsumer2, biConsumer, j2));
    }

    public final Object l0(Object obj, n nVar) {
        nVar.getClass();
        return w0(new CLASSNAMEc2(U2.REFERENCE, nVar, nVar, obj));
    }

    public final Stream limit(long j2) {
        if (j2 >= 0) {
            return B2.i(this, 0, j2);
        }
        throw new IllegalArgumentException(Long.toString(j2));
    }

    public final C1 m(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new e(this, this, U2.REFERENCE, T2.p | T2.n, toIntFunction);
    }

    public final Optional max(Comparator comparator) {
        comparator.getClass();
        return s(new CLASSNAMEd(comparator));
    }

    public final Optional min(Comparator comparator) {
        comparator.getClass();
        return s(new CLASSNAMEc(comparator));
    }

    public final Stream n(Function function) {
        function.getClass();
        return new d(this, this, U2.REFERENCE, T2.p | T2.n, function);
    }

    public final Stream p(Function function) {
        function.getClass();
        return new h(this, this, U2.REFERENCE, T2.p | T2.n | T2.t, function);
    }

    public final Optional s(n nVar) {
        nVar.getClass();
        return (Optional) w0(new CLASSNAMEe2(U2.REFERENCE, nVar));
    }

    /* access modifiers changed from: package-private */
    public final R1.a s0(long j2, x xVar) {
        return S1.d(j2, xVar);
    }

    public final Stream skip(long j2) {
        int i2 = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
        if (i2 >= 0) {
            return i2 == 0 ? this : B2.i(this, j2, -1);
        }
        throw new IllegalArgumentException(Long.toString(j2));
    }

    public final Stream sorted() {
        return new M2(this);
    }

    public final Object[] toArray() {
        CLASSNAMEr0 r0Var = CLASSNAMEr0.a;
        return S1.l(x0(r0Var), r0Var).q(r0Var);
    }

    public final Object[] toArray(x xVar) {
        return S1.l(x0(xVar), xVar).q(xVar);
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new CLASSNAMEz2(this, this, U2.REFERENCE, T2.r);
    }

    /* access modifiers changed from: package-private */
    public final R1 y0(T1 t1, Spliterator spliterator, boolean z, x xVar) {
        return S1.e(t1, spliterator, z, xVar);
    }

    public final Object z(Object obj, BiFunction biFunction, n nVar) {
        biFunction.getClass();
        nVar.getClass();
        return w0(new CLASSNAMEc2(U2.REFERENCE, nVar, biFunction, obj));
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r2, j$.util.stream.A2 r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEy2.z0(j$.util.Spliterator, j$.util.stream.A2):void");
    }

    public final Stream sorted(Comparator comparator) {
        return new M2(this, comparator);
    }
}
