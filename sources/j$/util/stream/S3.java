package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.Arrays;

final class S3 extends R3 implements CLASSNAMEi3 {
    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    S3(long size) {
        super(size);
    }

    public CLASSNAMEo3 b() {
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

    public void accept(int i) {
        int i2 = this.b;
        int[] iArr = this.a;
        if (i2 < iArr.length) {
            this.b = i2 + 1;
            iArr[i2] = i;
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
        return String.format("IntFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
