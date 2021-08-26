package j$.util.stream;

import j$.util.function.l;

abstract class S1 extends D1 implements A1 {
    S1(A1 a1, A1 a12) {
        super(a1, a12);
    }

    public void d(Object obj, int i) {
        ((A1) this.a).d(obj, i);
        ((A1) this.b).d(obj, i + ((int) ((A1) this.a).count()));
    }

    public Object e() {
        long count = count();
        if (count < NUM) {
            Object c = c((int) count);
            d(c, 0);
            return c;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void g(Object obj) {
        ((A1) this.a).g(obj);
        ((A1) this.b).g(obj);
    }

    public /* synthetic */ Object[] q(l lVar) {
        return CLASSNAMEp1.g(this, lVar);
    }

    public String toString() {
        if (count() < 32) {
            return String.format("%s[%s.%s]", new Object[]{getClass().getName(), this.a, this.b});
        }
        return String.format("%s[size=%d]", new Object[]{getClass().getName(), Long.valueOf(count())});
    }
}
