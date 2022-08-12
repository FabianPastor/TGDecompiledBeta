package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import java.util.Arrays;

/* renamed from: j$.util.stream.d2  reason: case insensitive filesystem */
final class CLASSNAMEd2 extends CLASSNAMEc2 implements CLASSNAMEq1 {
    CLASSNAMEd2(long j) {
        super(j);
    }

    public CLASSNAMEw1 a() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
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
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: k */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
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

    public /* synthetic */ boolean o() {
        return false;
    }

    public String toString() {
        return String.format("IntFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
