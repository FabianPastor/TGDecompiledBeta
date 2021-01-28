package j$.util.stream;

import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;

abstract class X2 {
    int a;

    static final class a extends d<q> implements q {
        final double[] c;

        a(int i) {
            this.c = new double[i];
        }

        public void accept(double d) {
            double[] dArr = this.c;
            int i = this.b;
            this.b = i + 1;
            dArr[i] = d;
        }

        /* access modifiers changed from: package-private */
        public void b(Object obj, long j) {
            q qVar = (q) obj;
            for (int i = 0; ((long) i) < j; i++) {
                qVar.accept(this.c[i]);
            }
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }
    }

    static final class b extends d<w> implements w {
        final int[] c;

        b(int i) {
            this.c = new int[i];
        }

        public void accept(int i) {
            int[] iArr = this.c;
            int i2 = this.b;
            this.b = i2 + 1;
            iArr[i2] = i;
        }

        public void b(Object obj, long j) {
            w wVar = (w) obj;
            for (int i = 0; ((long) i) < j; i++) {
                wVar.accept(this.c[i]);
            }
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }
    }

    static final class c extends d<C> implements C {
        final long[] c;

        c(int i) {
            this.c = new long[i];
        }

        public void accept(long j) {
            long[] jArr = this.c;
            int i = this.b;
            this.b = i + 1;
            jArr[i] = j;
        }

        public void b(Object obj, long j) {
            C c2 = (C) obj;
            for (int i = 0; ((long) i) < j; i++) {
                c2.accept(this.c[i]);
            }
        }

        public C g(C c2) {
            c2.getClass();
            return new CLASSNAMEh(this, c2);
        }
    }

    static abstract class d<T_CONS> extends X2 {
        int b;

        d() {
        }

        /* access modifiers changed from: package-private */
        public abstract void b(Object obj, long j);
    }

    static final class e<T> extends X2 implements Consumer<T> {
        final Object[] b;

        e(int i) {
            this.b = new Object[i];
        }

        public void accept(Object obj) {
            Object[] objArr = this.b;
            int i = this.a;
            this.a = i + 1;
            objArr[i] = obj;
        }

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }
    }

    X2() {
    }
}
