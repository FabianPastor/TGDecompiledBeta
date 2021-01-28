package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.w1  reason: case insensitive filesystem */
abstract class CLASSNAMEw1<T> implements g3<T, Void>, h3<T, Void> {
    private final boolean a;

    /* renamed from: j$.util.stream.w1$a */
    static final class a extends CLASSNAMEw1<Double> implements A2.e {
        final q b;

        a(q qVar, boolean z) {
            super(z);
            this.b = qVar;
        }

        public void accept(double d) {
            this.b.accept(d);
        }

        /* renamed from: e */
        public /* synthetic */ void accept(Double d) {
            Q1.a(this, d);
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }
    }

    /* renamed from: j$.util.stream.w1$b */
    static final class b extends CLASSNAMEw1<Integer> implements A2.f {
        final w b;

        b(w wVar, boolean z) {
            super(z);
            this.b = wVar;
        }

        public void accept(int i) {
            this.b.accept(i);
        }

        /* renamed from: e */
        public /* synthetic */ void accept(Integer num) {
            Q1.b(this, num);
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }
    }

    /* renamed from: j$.util.stream.w1$c */
    static final class c extends CLASSNAMEw1<Long> implements A2.g {
        final C b;

        c(C c, boolean z) {
            super(z);
            this.b = c;
        }

        public void accept(long j) {
            this.b.accept(j);
        }

        /* renamed from: e */
        public /* synthetic */ void accept(Long l) {
            Q1.c(this, l);
        }

        public C g(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }
    }

    /* renamed from: j$.util.stream.w1$d */
    static final class d<T> extends CLASSNAMEw1<T> {
        final Consumer b;

        d(Consumer consumer, boolean z) {
            super(z);
            this.b = consumer;
        }

        public void accept(Object obj) {
            this.b.accept(obj);
        }
    }

    protected CLASSNAMEw1(boolean z) {
        this.a = z;
    }

    public /* synthetic */ void accept(double d2) {
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    public int b() {
        if (this.a) {
            return 0;
        }
        return T2.r;
    }

    public Object c(T1 t1, Spliterator spliterator) {
        (this.a ? new CLASSNAMEx1(t1, spliterator, (A2) this) : new CLASSNAMEy1(t1, spliterator, t1.u0(this))).invoke();
        return null;
    }

    public Object d(T1 t1, Spliterator spliterator) {
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
        h1Var.m0(h1Var.u0(this), spliterator);
        return null;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
