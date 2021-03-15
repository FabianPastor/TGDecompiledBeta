package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.x;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEw1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.S1;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
class CLASSNAMEo1 extends CLASSNAMEy2.l<T, T> {

    /* renamed from: j$.util.stream.o1$a */
    class a extends A2.d<T, T> {
        boolean b;
        Object c;

        a(CLASSNAMEo1 o1Var, A2 a2) {
            super(a2);
        }

        public void accept(Object obj) {
            if (obj != null) {
                Object obj2 = this.c;
                if (obj2 == null || !obj.equals(obj2)) {
                    A2 a2 = this.var_a;
                    this.c = obj;
                    a2.accept(obj);
                }
            } else if (!this.b) {
                this.b = true;
                A2 a22 = this.var_a;
                this.c = null;
                a22.accept((Object) null);
            }
        }

        public void m() {
            this.b = false;
            this.c = null;
            this.var_a.m();
        }

        public void n(long j) {
            this.b = false;
            this.c = null;
            this.var_a.n(-1);
        }
    }

    /* renamed from: j$.util.stream.o1$b */
    class b extends A2.d<T, T> {
        Set b;

        b(CLASSNAMEo1 o1Var, A2 a2) {
            super(a2);
        }

        public void accept(Object obj) {
            if (!this.b.contains(obj)) {
                this.b.add(obj);
                this.var_a.accept(obj);
            }
        }

        public void m() {
            this.b = null;
            this.var_a.m();
        }

        public void n(long j) {
            this.b = new HashSet();
            this.var_a.n(-1);
        }
    }

    CLASSNAMEo1(CLASSNAMEh1 h1Var, U2 u2, int i) {
        super(h1Var, u2, i);
    }

    /* access modifiers changed from: package-private */
    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        if (T2.DISTINCT.d(t1.r0())) {
            return t1.o0(spliterator, false, xVar);
        }
        if (T2.ORDERED.d(t1.r0())) {
            return K0(t1, spliterator);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new CLASSNAMEw1.d(new CLASSNAMEm(atomicBoolean, concurrentHashMap), false).c(t1, spliterator);
        Set keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add((Object) null);
            keySet = hashSet;
        }
        return new S1.d(keySet);
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(T1 t1, Spliterator spliterator) {
        return T2.DISTINCT.d(t1.r0()) ? t1.v0(spliterator) : T2.ORDERED.d(t1.r0()) ? ((S1.d) K0(t1, spliterator)).spliterator() : new Y2(t1.v0(spliterator));
    }

    /* access modifiers changed from: package-private */
    public A2 G0(int i, A2 a2) {
        a2.getClass();
        if (T2.DISTINCT.d(i)) {
            return a2;
        }
        return T2.SORTED.d(i) ? new a(this, a2) : new b(this, a2);
    }

    /* access modifiers changed from: package-private */
    public R1 K0(T1 t1, Spliterator spliterator) {
        A a2 = A.var_a;
        M0 m0 = M0.var_a;
        return new S1.d((Collection) new CLASSNAMEi2(U2.REFERENCE, CLASSNAMEi.var_a, m0, a2).c(t1, spliterator));
    }
}
