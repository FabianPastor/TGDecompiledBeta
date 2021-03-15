package j$.util.stream;

import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;

interface A2<T> extends Consumer<T> {

    public static abstract class a<E_OUT> implements e {

        /* renamed from: a  reason: collision with root package name */
        protected final A2 var_a;

        public a(A2 a2) {
            a2.getClass();
            this.var_a = a2;
        }

        public /* synthetic */ void accept(int i) {
            k.a(this);
            throw null;
        }

        public /* synthetic */ void accept(long j) {
            k.b(this);
            throw null;
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Double d) {
            Q1.a(this, d);
        }

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        public void m() {
            this.var_a.m();
        }

        public void n(long j) {
            this.var_a.n(j);
        }

        public boolean p() {
            return this.var_a.p();
        }
    }

    public static abstract class b<E_OUT> implements f {

        /* renamed from: a  reason: collision with root package name */
        protected final A2 var_a;

        public b(A2 a2) {
            a2.getClass();
            this.var_a = a2;
        }

        public /* synthetic */ void accept(double d) {
            k.c(this);
            throw null;
        }

        public /* synthetic */ void accept(long j) {
            k.b(this);
            throw null;
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Integer num) {
            Q1.b(this, num);
        }

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        public void m() {
            this.var_a.m();
        }

        public void n(long j) {
            this.var_a.n(j);
        }

        public boolean p() {
            return this.var_a.p();
        }
    }

    public static abstract class c<E_OUT> implements g {

        /* renamed from: a  reason: collision with root package name */
        protected final A2 var_a;

        public c(A2 a2) {
            a2.getClass();
            this.var_a = a2;
        }

        public /* synthetic */ void accept(double d) {
            k.c(this);
            throw null;
        }

        public /* synthetic */ void accept(int i) {
            k.a(this);
            throw null;
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Long l) {
            Q1.c(this, l);
        }

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }

        public C g(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public void m() {
            this.var_a.m();
        }

        public void n(long j) {
            this.var_a.n(j);
        }

        public boolean p() {
            return this.var_a.p();
        }
    }

    public static abstract class d<T, E_OUT> implements A2<T> {

        /* renamed from: a  reason: collision with root package name */
        protected final A2 var_a;

        public d(A2 a2) {
            a2.getClass();
            this.var_a = a2;
        }

        public /* synthetic */ void accept(double d) {
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

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }

        public void m() {
            this.var_a.m();
        }

        public void n(long j) {
            this.var_a.n(j);
        }

        public boolean p() {
            return this.var_a.p();
        }
    }

    public interface e extends A2<Double>, q {
        void accept(double d);
    }

    public interface f extends A2<Integer>, w {
        void accept(int i);
    }

    public interface g extends A2<Long>, C {
        void accept(long j);
    }

    void accept(double d2);

    void accept(int i);

    void accept(long j);

    void m();

    void n(long j);

    boolean p();
}
