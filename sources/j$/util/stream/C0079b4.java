package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import java.util.Arrays;

/* renamed from: j$.util.stream.b4  reason: case insensitive filesystem */
final class CLASSNAMEb4 extends CLASSNAMEa4 implements CLASSNAMEj3 {
    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    CLASSNAMEb4(long size) {
        super(size);
    }

    public CLASSNAMEq3 b() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
    }

    public void s(long size) {
        if (size == ((long) this.a.length)) {
            this.b = 0;
        } else {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(size), Integer.valueOf(this.a.length)}));
        }
    }

    public void accept(long i) {
        int i2 = this.b;
        long[] jArr = this.a;
        if (i2 < jArr.length) {
            this.b = i2 + 1;
            jArr[i2] = i;
            return;
        }
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.a.length)}));
    }

    public void r() {
        if (this.b < this.a.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
        }
    }

    public String toString() {
        return String.format("LongFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
