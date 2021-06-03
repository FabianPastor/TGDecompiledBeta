package j$.util.stream;

import j$.util.function.C;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;

interface A2<T> extends Consumer<T> {

    public static abstract class a<E_OUT> implements e {
        protected final A2 a;

        public a(A2 a2) {
            a2.getClass();
            this.a = a2;
        }

        public /* synthetic */ void accept(int i) {
            j$.time.a.a(this);
            throw null;
        }

        public /* synthetic */ void accept(long j) {
            j$.time.a.b(this);
            throw null;
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Double d) {
            Q1.a(this, d);
        }

        public q j(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        public void l() {
            this.a.l();
        }

        public void m(long j) {
            this.a.m(j);
        }

        public boolean o() {
            return this.a.o();
        }
    }

    public static abstract class b<E_OUT> implements f {
        protected final A2 a;

        public b(A2 a2) {
            a2.getClass();
            this.a = a2;
        }

        public /* synthetic */ void accept(double d) {
            j$.time.a.c(this);
            throw null;
        }

        public /* synthetic */ void accept(long j) {
            j$.time.a.b(this);
            throw null;
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Integer num) {
            Q1.b(this, num);
        }

        public w k(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        public void l() {
            this.a.l();
        }

        public void m(long j) {
            this.a.m(j);
        }

        public boolean o() {
            return this.a.o();
        }
    }

    public static abstract class c<E_OUT> implements g {
        protected final A2 a;

        public c(A2 a2) {
            a2.getClass();
            this.a = a2;
        }

        public /* synthetic */ void accept(double d) {
            j$.time.a.c(this);
            throw null;
        }

        public /* synthetic */ void accept(int i) {
            j$.time.a.a(this);
            throw null;
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        /* renamed from: b */
        public /* synthetic */ void accept(Long l) {
            Q1.c(this, l);
        }

        public C f(C c) {
            c.getClass();
            return new CLASSNAMEh(this, c);
        }

        public void l() {
            this.a.l();
        }

        public void m(long j) {
            this.a.m(j);
        }

        public boolean o() {
            return this.a.o();
        }
    }

    public static abstract class d<T, E_OUT> implements A2<T> {
        protected final A2 a;

        public d(A2 a2) {
            a2.getClass();
            this.a = a2;
        }

        public /* synthetic */ void accept(double d) {
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

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public void l() {
            this.a.l();
        }

        public void m(long j) {
            this.a.m(j);
        }

        public boolean o() {
            return this.a.o();
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

    void l();

    void m(long j);

    boolean o();
}
