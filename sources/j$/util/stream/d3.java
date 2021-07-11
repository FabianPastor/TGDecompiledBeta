package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.r;
import j$.util.stream.X2;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

abstract class d3<T, T_SPLITR extends Spliterator<T>> {
    protected final Spliterator a;
    protected final boolean b;
    private final long c;
    private final AtomicLong d;

    static final class a extends d<Double, q, X2.a, Spliterator.a> implements Spliterator.a, q {
        double e;

        a(Spliterator.a aVar, long j, long j2) {
            super(aVar, j, j2);
        }

        a(Spliterator.a aVar, a aVar2) {
            super(aVar, aVar2);
        }

        public void accept(double d) {
            this.e = d;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.e(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            r.a(this, consumer);
        }

        public q j(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        /* access modifiers changed from: protected */
        public Spliterator q(Spliterator spliterator) {
            return new a((Spliterator.a) spliterator, this);
        }

        /* access modifiers changed from: protected */
        public void s(Object obj) {
            ((q) obj).accept(this.e);
        }

        /* access modifiers changed from: protected */
        public X2.d t(int i) {
            return new X2.a(i);
        }
    }

    static final class b extends d<Integer, w, X2.b, Spliterator.b> implements Spliterator.b, w {
        int e;

        b(Spliterator.b bVar, long j, long j2) {
            super(bVar, j, j2);
        }

        b(Spliterator.b bVar, b bVar2) {
            super(bVar, bVar2);
        }

        public void accept(int i) {
            this.e = i;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.f(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            r.b(this, consumer);
        }

        public w k(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        /* access modifiers changed from: protected */
        public Spliterator q(Spliterator spliterator) {
            return new b((Spliterator.b) spliterator, this);
        }

        /* access modifiers changed from: protected */
        public void s(Object obj) {
            ((w) obj).accept(this.e);
        }

        /* access modifiers changed from: protected */
        public X2.d t(int i) {
            return new X2.b(i);
        }
    }

    static final class c extends d<Long, C, X2.c, Spliterator.c> implements Spliterator.c, C {
        long e;

        c(Spliterator.c cVar, long j, long j2) {
            super(cVar, j, j2);
        }

        c(Spliterator.c cVar, c cVar2) {
            super(cVar, cVar2);
        }

        public void accept(long j) {
            this.e = j;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.g(this, consumer);
        }

        public C f(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            r.c(this, consumer);
        }

        /* access modifiers changed from: protected */
        public Spliterator q(Spliterator spliterator) {
            return new c((Spliterator.c) spliterator, this);
        }

        /* access modifiers changed from: protected */
        public void s(Object obj) {
            ((C) obj).accept(this.e);
        }

        /* access modifiers changed from: protected */
        public X2.d t(int i) {
            return new X2.c(i);
        }
    }

    static abstract class d<T, T_CONS, T_BUFF extends X2.d<T_CONS>, T_SPLITR extends Spliterator.d<T, T_CONS, T_SPLITR>> extends d3<T, T_SPLITR> implements Spliterator.d<T, T_CONS, T_SPLITR> {
        d(Spliterator.d dVar, long j, long j2) {
            super(dVar, j, j2);
        }

        d(Spliterator.d dVar, d dVar2) {
            super(dVar, dVar2);
        }

        /* renamed from: forEachRemaining */
        public void e(Object obj) {
            obj.getClass();
            X2.d dVar = null;
            while (true) {
                f r = r();
                if (r == f.NO_MORE) {
                    return;
                }
                if (r == f.MAYBE_MORE) {
                    if (dVar == null) {
                        dVar = t(128);
                    } else {
                        dVar.b = 0;
                    }
                    long j = 0;
                    while (((Spliterator.d) this.a).tryAdvance(dVar)) {
                        j++;
                        if (j >= 128) {
                            break;
                        }
                    }
                    if (j != 0) {
                        dVar.b(obj, p(j));
                    } else {
                        return;
                    }
                } else {
                    ((Spliterator.d) this.a).forEachRemaining(obj);
                    return;
                }
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        /* access modifiers changed from: protected */
        public abstract void s(Object obj);

        /* access modifiers changed from: protected */
        public abstract X2.d t(int i);

        /* renamed from: tryAdvance */
        public boolean n(Object obj) {
            obj.getClass();
            while (r() != f.NO_MORE && ((Spliterator.d) this.a).tryAdvance(this)) {
                if (p(1) == 1) {
                    s(obj);
                    return true;
                }
            }
            return false;
        }
    }

    static final class e<T> extends d3<T, Spliterator<T>> implements Spliterator<T>, Consumer<T> {
        Object e;

        e(Spliterator spliterator, long j, long j2) {
            super(spliterator, j, j2);
        }

        e(Spliterator spliterator, e eVar) {
            super(spliterator, eVar);
        }

        public final void accept(Object obj) {
            this.e = obj;
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            while (r() != f.NO_MORE && this.a.b(this)) {
                if (p(1) == 1) {
                    consumer.accept(this.e);
                    this.e = null;
                    return true;
                }
            }
            return false;
        }

        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            X2.e eVar = null;
            while (true) {
                f r = r();
                if (r == f.NO_MORE) {
                    return;
                }
                if (r == f.MAYBE_MORE) {
                    if (eVar == null) {
                        eVar = new X2.e(128);
                    } else {
                        eVar.a = 0;
                    }
                    long j = 0;
                    while (this.a.b(eVar)) {
                        j++;
                        if (j >= 128) {
                            break;
                        }
                    }
                    if (j != 0) {
                        long p = p(j);
                        for (int i = 0; ((long) i) < p; i++) {
                            consumer.accept(eVar.b[i]);
                        }
                    } else {
                        return;
                    }
                } else {
                    this.a.forEachRemaining(consumer);
                    return;
                }
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        /* access modifiers changed from: protected */
        public Spliterator q(Spliterator spliterator) {
            return new e(spliterator, this);
        }
    }

    enum f {
        NO_MORE,
        MAYBE_MORE,
        UNLIMITED
    }

    d3(Spliterator spliterator, long j, long j2) {
        this.a = spliterator;
        long j3 = 0;
        int i = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
        this.b = i < 0;
        this.c = i >= 0 ? j2 : j3;
        this.d = new AtomicLong(i >= 0 ? j + j2 : j);
    }

    d3(Spliterator spliterator, d3 d3Var) {
        this.a = spliterator;
        this.b = d3Var.b;
        this.d = d3Var.d;
        this.c = d3Var.c;
    }

    public final int characteristics() {
        return this.a.characteristics() & -16465;
    }

    public final long estimateSize() {
        return this.a.estimateSize();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final long p(long r10) {
        /*
            r9 = this;
        L_0x0000:
            java.util.concurrent.atomic.AtomicLong r0 = r9.d
            long r0 = r0.get()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0013
            boolean r0 = r9.b
            if (r0 == 0) goto L_0x0011
            goto L_0x0012
        L_0x0011:
            r10 = r2
        L_0x0012:
            return r10
        L_0x0013:
            long r4 = java.lang.Math.min(r0, r10)
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0025
            java.util.concurrent.atomic.AtomicLong r6 = r9.d
            long r7 = r0 - r4
            boolean r6 = r6.compareAndSet(r0, r7)
            if (r6 == 0) goto L_0x0000
        L_0x0025:
            boolean r6 = r9.b
            if (r6 == 0) goto L_0x002f
            long r10 = r10 - r4
            long r10 = java.lang.Math.max(r10, r2)
            return r10
        L_0x002f:
            long r10 = r9.c
            int r6 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r6 <= 0) goto L_0x003c
            long r0 = r0 - r10
            long r4 = r4 - r0
            long r10 = java.lang.Math.max(r4, r2)
            return r10
        L_0x003c:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.d3.p(long):long");
    }

    /* access modifiers changed from: protected */
    public abstract Spliterator q(Spliterator spliterator);

    /* access modifiers changed from: protected */
    public final f r() {
        return this.d.get() > 0 ? f.MAYBE_MORE : this.b ? f.UNLIMITED : f.NO_MORE;
    }

    public final Spliterator trySplit() {
        Spliterator trySplit;
        if (this.d.get() == 0 || (trySplit = this.a.trySplit()) == null) {
            return null;
        }
        return q(trySplit);
    }
}
