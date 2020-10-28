package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;

/* renamed from: j$.util.stream.z3  reason: case insensitive filesystem */
final class CLASSNAMEz3 extends CLASSNAMEn3 implements CLASSNAMEl3 {
    CLASSNAMEz3(CLASSNAMEl3 l3Var, CLASSNAMEl3 l3Var2) {
        super(l3Var, l3Var2);
    }

    public void forEach(Consumer consumer) {
        this.a.forEach(consumer);
        this.b.forEach(consumer);
    }

    public void j(Object[] objArr, int i) {
        objArr.getClass();
        this.a.j(objArr, i);
        this.b.j(objArr, i + ((int) this.a.count()));
    }

    public Object[] q(v vVar) {
        long count = count();
        if (count < NUM) {
            Object[] objArr = (Object[]) vVar.apply((int) count);
            j(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public CLASSNAMEl3 r(long j, long j2, v vVar) {
        if (j == 0 && j2 == count()) {
            return this;
        }
        long count = this.a.count();
        if (j >= count) {
            return this.b.r(j - count, j2 - count, vVar);
        }
        if (j2 <= count) {
            return this.a.r(j, j2, vVar);
        }
        v vVar2 = vVar;
        return CLASSNAMEh4.i(CLASSNAMEh6.REFERENCE, this.a.r(j, count, vVar2), this.b.r(0, j2 - count, vVar2));
    }

    public Spliterator spliterator() {
        return new Q3(this);
    }

    public String toString() {
        if (count() < 32) {
            return String.format("ConcNode[%s.%s]", new Object[]{this.a, this.b});
        }
        return String.format("ConcNode[size=%d]", new Object[]{Long.valueOf(count())});
    }
}
