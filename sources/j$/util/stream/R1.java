package j$.util.stream;

import j$.util.function.m;

abstract class R1 extends C1 implements CLASSNAMEz1 {
    R1(CLASSNAMEz1 z1Var, CLASSNAMEz1 z1Var2) {
        super(z1Var, z1Var2);
    }

    public void d(Object obj, int i) {
        ((CLASSNAMEz1) this.a).d(obj, i);
        ((CLASSNAMEz1) this.b).d(obj, i + ((int) ((CLASSNAMEz1) this.a).count()));
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
        ((CLASSNAMEz1) this.a).g(obj);
        ((CLASSNAMEz1) this.b).g(obj);
    }

    public /* synthetic */ Object[] q(m mVar) {
        return CLASSNAMEo1.g(this, mVar);
    }

    public String toString() {
        if (count() < 32) {
            return String.format("%s[%s.%s]", new Object[]{getClass().getName(), this.a, this.b});
        }
        return String.format("%s[size=%d]", new Object[]{getClass().getName(), Long.valueOf(count())});
    }
}
