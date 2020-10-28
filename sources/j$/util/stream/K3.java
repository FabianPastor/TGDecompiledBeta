package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;
import java.util.Arrays;

final class K3 extends J3 implements CLASSNAMEe3 {
    K3(long j) {
        super(j);
    }

    public CLASSNAMEi3 a() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
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

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    public void m() {
        if (this.b < this.a.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
        }
    }

    public void n(long j) {
        if (j == ((long) this.a.length)) {
            this.b = 0;
        } else {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", new Object[]{Long.valueOf(j), Integer.valueOf(this.a.length)}));
        }
    }

    public /* synthetic */ boolean p() {
        return false;
    }

    /* renamed from: s */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public String toString() {
        return String.format("IntFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
