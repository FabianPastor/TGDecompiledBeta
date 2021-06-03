package j$.util.stream;

import j$.util.Optional;
import j$.util.function.C;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.o;
import j$.util.p;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.u1  reason: case insensitive filesystem */
abstract class CLASSNAMEu1<T, O> implements h3<T, O> {
    boolean a;
    Object b;

    /* renamed from: j$.util.stream.u1$a */
    static final class a extends CLASSNAMEu1<Double, o> implements A2.e {
        a() {
        }

        public void accept(double d) {
            accept((Object) Double.valueOf(d));
        }

        public Object get() {
            if (this.a) {
                return o.d(((Double) this.b).doubleValue());
            }
            return null;
        }

        public q j(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }
    }

    /* renamed from: j$.util.stream.u1$b */
    static final class b extends CLASSNAMEu1<Integer, p> implements A2.f {
        b() {
        }

        public void accept(int i) {
            accept((Object) Integer.valueOf(i));
        }

        public Object get() {
            if (this.a) {
                return p.d(((Integer) this.b).intValue());
            }
            return null;
        }

        public w k(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }
    }

    /* renamed from: j$.util.stream.u1$c */
    static final class c extends CLASSNAMEu1<Long, j$.util.q> implements A2.g {
        c() {
        }

        public void accept(long j) {
            accept((Object) Long.valueOf(j));
        }

        public C f(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public Object get() {
            if (this.a) {
                return j$.util.q.d(((Long) this.b).longValue());
            }
            return null;
        }
    }

    /* renamed from: j$.util.stream.u1$d */
    static final class d<T> extends CLASSNAMEu1<T, Optional<T>> {
        d() {
        }

        public Object get() {
            if (this.a) {
                return Optional.of(this.b);
            }
            return null;
        }
    }

    CLASSNAMEu1() {
    }

    public /* synthetic */ void accept(double d2) {
        j$.time.a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        j$.time.a.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        j$.time.a.b(this);
        throw null;
    }

    public void accept(Object obj) {
        if (!this.a) {
            this.a = true;
            this.b = obj;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public boolean o() {
        return this.a;
    }
}
