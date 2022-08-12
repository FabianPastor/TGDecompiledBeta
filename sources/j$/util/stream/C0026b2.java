package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import java.util.Arrays;

/* renamed from: j$.util.stream.b2  reason: case insensitive filesystem */
final class CLASSNAMEb2 extends D1 implements CLASSNAMEs1 {
    CLASSNAMEb2(long j, m mVar) {
        super(j, mVar);
    }

    public A1 a() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", new Object[]{Integer.valueOf(this.b), Integer.valueOf(this.a.length)}));
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public void accept(Object obj) {
        int i = this.b;
        Object[] objArr = this.a;
        if (i < objArr.length) {
            this.b = i + 1;
            objArr[i] = obj;
            return;
        }
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.a.length)}));
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
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
        return String.format("FixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
