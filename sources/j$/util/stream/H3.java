package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;

final class H3 extends CLASSNAMEv3 implements CLASSNAMEt3 {
    H3(CLASSNAMEt3 left, CLASSNAMEt3 right) {
        super(left, right);
    }

    public Spliterator spliterator() {
        return new Y3(this);
    }

    public void m(Object[] array, int offset) {
        array.getClass();
        this.a.m(array, offset);
        this.b.m(array, ((int) this.a.count()) + offset);
    }

    public Object[] x(C c) {
        long size = count();
        if (size < NUM) {
            T[] array = (Object[]) c.a((int) size);
            m(array, 0);
            return array;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void forEach(Consumer consumer) {
        this.a.forEach(consumer);
        this.b.forEach(consumer);
    }

    public CLASSNAMEt3 c(long from, long to, C c) {
        if (from == 0 && to == count()) {
            return this;
        }
        long leftCount = this.a.count();
        if (from >= leftCount) {
            return this.b.c(from - leftCount, to - leftCount, c);
        }
        if (to <= leftCount) {
            return this.a.c(from, to, c);
        }
        return CLASSNAMEp4.j(b(), this.a.c(from, leftCount, c), this.b.c(0, to - leftCount, c));
    }

    public String toString() {
        if (count() < 32) {
            return String.format("ConcNode[%s.%s]", new Object[]{this.a, this.b});
        }
        return String.format("ConcNode[size=%d]", new Object[]{Long.valueOf(count())});
    }
}
