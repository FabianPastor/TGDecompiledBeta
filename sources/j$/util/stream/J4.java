package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.y;
import java.util.Comparator;

final class J4 extends K4 implements y, Consumer {
    Object e;

    J4(y yVar, long j, long j2) {
        super(yVar, j, j2);
    }

    J4(y yVar, J4 j4) {
        super(yVar, j4);
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
        CLASSNAMEl4 l4Var = null;
        while (true) {
            int r = r();
            if (r == 1) {
                return;
            }
            if (r == 2) {
                if (l4Var == null) {
                    l4Var = new CLASSNAMEl4(128);
                } else {
                    l4Var.a = 0;
                }
                long j = 0;
                while (this.a.b(l4Var)) {
                    j++;
                    if (j >= 128) {
                        break;
                    }
                }
                if (j != 0) {
                    long p = p(j);
                    for (int i = 0; ((long) i) < p; i++) {
                        consumer.accept(l4Var.b[i]);
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
    public y q(y yVar) {
        return new J4(yVar, this);
    }
}
