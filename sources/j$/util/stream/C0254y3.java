package j$.util.stream;

import j$.util.function.v;

/* renamed from: j$.util.stream.y3  reason: case insensitive filesystem */
abstract class CLASSNAMEy3 extends CLASSNAMEn3 implements CLASSNAMEk3 {
    CLASSNAMEy3(CLASSNAMEk3 k3Var, CLASSNAMEk3 k3Var2) {
        super(k3Var, k3Var2);
    }

    public void d(Object obj, int i) {
        ((CLASSNAMEk3) this.a).d(obj, i);
        ((CLASSNAMEk3) this.b).d(obj, i + ((int) ((CLASSNAMEk3) this.a).count()));
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

    public void h(Object obj) {
        ((CLASSNAMEk3) this.a).h(obj);
        ((CLASSNAMEk3) this.b).h(obj);
    }

    public /* synthetic */ Object[] q(v vVar) {
        return CLASSNAMEc3.d(this, vVar);
    }

    public String toString() {
        if (count() < 32) {
            return String.format("%s[%s.%s]", new Object[]{getClass().getName(), this.a, this.b});
        }
        return String.format("%s[size=%d]", new Object[]{getClass().getName(), Long.valueOf(count())});
    }
}
