package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.J;

final class e3<P_IN, P_OUT> extends W2<P_IN, P_OUT, S2<P_OUT>> {
    e3(T1 t1, Spliterator spliterator, boolean z) {
        super(t1, spliterator, z);
    }

    e3(T1 t1, J j, boolean z) {
        super(t1, j, z);
    }

    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            S2 s2 = (S2) this.h;
            long j = this.g;
            if (s2.c == 0) {
                if (j < ((long) s2.b)) {
                    obj = s2.e[(int) j];
                } else {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
            } else if (j < s2.count()) {
                int i = 0;
                while (i <= s2.c) {
                    long[] jArr = s2.d;
                    long j2 = jArr[i];
                    Object[][] objArr = s2.f;
                    if (j < j2 + ((long) objArr[i].length)) {
                        obj = objArr[i][(int) (j - jArr[i])];
                    } else {
                        i++;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                throw new IndexOutOfBoundsException(Long.toString(j));
            }
            consumer.accept(obj);
        }
        return a;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.h != null || this.i) {
            do {
            } while (b(consumer));
            return;
        }
        consumer.getClass();
        g();
        this.b.t0(new N0(consumer), this.d);
        this.i = true;
    }

    /* access modifiers changed from: package-private */
    public void i() {
        S2 s2 = new S2();
        this.h = s2;
        this.e = this.b.u0(new CLASSNAMEg(s2));
        this.f = new H0(this);
    }

    /* access modifiers changed from: package-private */
    public W2 k(Spliterator spliterator) {
        return new e3(this.b, spliterator, this.a);
    }
}
