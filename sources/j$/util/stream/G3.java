package j$.util.stream;

import j$.util.function.C;

abstract class G3 extends CLASSNAMEv3 implements CLASSNAMEs3 {
    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    public /* bridge */ /* synthetic */ CLASSNAMEs3 d(int i) {
        return (CLASSNAMEs3) super.d(i);
    }

    G3(CLASSNAMEs3 left, CLASSNAMEs3 right) {
        super(left, right);
    }

    public void j(Object consumer) {
        ((CLASSNAMEs3) this.a).j(consumer);
        ((CLASSNAMEs3) this.b).j(consumer);
    }

    public void f(Object array, int offset) {
        ((CLASSNAMEs3) this.a).f(array, offset);
        ((CLASSNAMEs3) this.b).f(array, ((int) ((CLASSNAMEs3) this.a).count()) + offset);
    }

    public Object i() {
        long size = count();
        if (size < NUM) {
            T_ARR array = a((int) size);
            f(array, 0);
            return array;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public String toString() {
        if (count() < 32) {
            return String.format("%s[%s.%s]", new Object[]{getClass().getName(), this.a, this.b});
        }
        return String.format("%s[size=%d]", new Object[]{getClass().getName(), Long.valueOf(count())});
    }
}
