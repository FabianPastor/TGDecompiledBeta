package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.E;

final class H6 extends CLASSNAMEj6 {
    H6(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        super(i4Var, spliterator, z);
    }

    H6(CLASSNAMEi4 i4Var, E e, boolean z) {
        super(i4Var, e, z);
    }

    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            CLASSNAMEd6 d6Var = (CLASSNAMEd6) this.h;
            long j = this.g;
            if (d6Var.c == 0) {
                if (j < ((long) d6Var.b)) {
                    obj = d6Var.e[(int) j];
                } else {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
            } else if (j < d6Var.count()) {
                int i = 0;
                while (i <= d6Var.c) {
                    long[] jArr = d6Var.d;
                    long j2 = jArr[i];
                    Object[][] objArr = d6Var.f;
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
        CLASSNAMEd6 d6Var = new CLASSNAMEd6();
        this.h = d6Var;
        this.e = this.b.u0(new CLASSNAMEg(d6Var));
        this.f = new H0(this);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEj6 k(Spliterator spliterator) {
        return new H6(this.b, spliterator, this.a);
    }
}
