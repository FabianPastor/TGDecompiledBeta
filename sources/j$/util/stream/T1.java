package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.y;

final class T1 extends D1 {
    T1(B1 b1, B1 b12) {
        super(b1, b12);
    }

    public void forEach(Consumer consumer) {
        this.a.forEach(consumer);
        this.b.forEach(consumer);
    }

    public void i(Object[] objArr, int i) {
        objArr.getClass();
        this.a.i(objArr, i);
        this.b.i(objArr, i + ((int) this.a.count()));
    }

    public Object[] q(m mVar) {
        long count = count();
        if (count < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public B1 r(long j, long j2, m mVar) {
        if (j == 0 && j2 == count()) {
            return this;
        }
        long count = this.a.count();
        if (j >= count) {
            return this.b.r(j - count, j2 - count, mVar);
        }
        if (j2 <= count) {
            return this.a.r(j, j2, mVar);
        }
        m mVar2 = mVar;
        return CLASSNAMEy2.i(CLASSNAMEf4.REFERENCE, this.a.r(j, count, mVar2), this.b.r(0, j2 - count, mVar2));
    }

    public y spliterator() {
        return new CLASSNAMEk2(this);
    }

    public String toString() {
        if (count() < 32) {
            return String.format("ConcNode[%s.%s]", new Object[]{this.a, this.b});
        }
        return String.format("ConcNode[size=%d]", new Object[]{Long.valueOf(count())});
    }
}
