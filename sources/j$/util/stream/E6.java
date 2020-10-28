package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import java.util.Comparator;

final class E6 extends G6 implements Spliterator, Consumer {
    Object e;

    E6(Spliterator spliterator, long j, long j2) {
        super(spliterator, j, j2);
    }

    E6(Spliterator spliterator, E6 e6) {
        super(spliterator, e6);
    }

    public final void accept(Object obj) {
        this.e = obj;
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        while (s() != F6.NO_MORE && this.a.b(this)) {
            if (q(1) == 1) {
                consumer.accept(this.e);
                this.e = null;
                return true;
            }
        }
        return false;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        o6 o6Var = null;
        while (true) {
            F6 s = s();
            if (s == F6.NO_MORE) {
                return;
            }
            if (s == F6.MAYBE_MORE) {
                if (o6Var == null) {
                    o6Var = new o6(128);
                } else {
                    o6Var.a = 0;
                }
                long j = 0;
                while (this.a.b(o6Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    long q = q(j);
                    for (int i = 0; ((long) i) < q; i++) {
                        consumer.accept(o6Var.b[i]);
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
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    /* access modifiers changed from: protected */
    public Spliterator r(Spliterator spliterator) {
        return new E6(spliterator, this);
    }
}
