package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.u;
import java.util.Comparator;

final class I4 extends J4 implements u, Consumer {
    Object e;

    I4(u uVar, long j, long j2) {
        super(uVar, j, j2);
    }

    I4(u uVar, I4 i4) {
        super(uVar, i4);
    }

    public final void accept(Object obj) {
        this.e = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        while (r() != 1 && this.a.b(this)) {
            if (p(1) == 1) {
                consumer.accept(this.e);
                this.e = null;
                return true;
            }
        }
        return false;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        CLASSNAMEk4 k4Var = null;
        while (true) {
            int r = r();
            if (r == 1) {
                return;
            }
            if (r == 2) {
                if (k4Var == null) {
                    k4Var = new CLASSNAMEk4(128);
                } else {
                    k4Var.a = 0;
                }
                long j = 0;
                while (this.a.b(k4Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    long p = p(j);
                    for (int i = 0; ((long) i) < p; i++) {
                        consumer.accept(k4Var.b[i]);
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
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    /* access modifiers changed from: protected */
    public u q(u uVar) {
        return new I4(uVar, this);
    }
}
