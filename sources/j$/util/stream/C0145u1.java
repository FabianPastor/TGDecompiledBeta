package j$.util.stream;

import j$.util.Optional;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;
import j$.util.p;
import j$.util.r;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.u1  reason: case insensitive filesystem */
abstract class CLASSNAMEu1<T, O> implements h3<T, O> {

    /* renamed from: a  reason: collision with root package name */
    boolean var_a;
    Object b;

    /* renamed from: j$.util.stream.u1$a */
    static final class a extends CLASSNAMEu1<Double, p> implements A2.e {
        a() {
        }

        public void accept(double d) {
            accept((Object) Double.valueOf(d));
        }

        public Object get() {
            if (this.var_a) {
                return p.d(((Double) this.b).doubleValue());
            }
            return null;
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }
    }

    /* renamed from: j$.util.stream.u1$b */
    static final class b extends CLASSNAMEu1<Integer, j$.util.q> implements A2.f {
        b() {
        }

        public void accept(int i) {
            accept((Object) Integer.valueOf(i));
        }

        public Object get() {
            if (this.var_a) {
                return j$.util.q.d(((Integer) this.b).intValue());
            }
            return null;
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }
    }

    /* renamed from: j$.util.stream.u1$c */
    static final class c extends CLASSNAMEu1<Long, r> implements A2.g {
        c() {
        }

        public void accept(long j) {
            accept((Object) Long.valueOf(j));
        }

        public C g(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public Object get() {
            if (this.var_a) {
                return r.d(((Long) this.b).longValue());
            }
            return null;
        }
    }

    /* renamed from: j$.util.stream.u1$d */
    static final class d<T> extends CLASSNAMEu1<T, Optional<T>> {
        d() {
        }

        public Object get() {
            if (this.var_a) {
                return Optional.of(this.b);
            }
            return null;
        }
    }

    CLASSNAMEu1() {
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

    public void accept(Object obj) {
        if (!this.var_a) {
            this.var_a = true;
            this.b = obj;
        }
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public boolean p() {
        return this.var_a;
    }
}
