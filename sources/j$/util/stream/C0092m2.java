package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;
import java.util.Arrays;

/* renamed from: j$.util.stream.m2  reason: case insensitive filesystem */
final class CLASSNAMEm2 extends CLASSNAMEl2 implements CLASSNAMEr1 {
    CLASSNAMEm2(long j) {
        super(j);
    }

    public CLASSNAMEy1 a() {
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

    public void accept(long j) {
        int i = this.b;
        long[] jArr = this.a;
        if (i < jArr.length) {
            this.b = i + 1;
            jArr[i] = j;
            return;
        }
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", new Object[]{Integer.valueOf(this.a.length)}));
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }

    /* renamed from: l */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEo1.c(this, l);
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
        return String.format("LongFixedNodeBuilder[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
