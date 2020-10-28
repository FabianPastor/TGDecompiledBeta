package j$.util.stream;

import j$.util.Comparator;
import j$.util.Spliterator;
import j$.util.function.v;
import java.util.Arrays;
import java.util.Comparator;

final class O5 extends CLASSNAMEj5 {
    private final boolean l;
    private final Comparator m;

    O5(CLASSNAMEh1 h1Var) {
        super(h1Var, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.v | CLASSNAMEg6.t);
        this.l = true;
        this.m = Comparator.CC.b();
    }

    O5(CLASSNAMEh1 h1Var, java.util.Comparator comparator) {
        super(h1Var, CLASSNAMEh6.REFERENCE, CLASSNAMEg6.v | CLASSNAMEg6.u);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        if (CLASSNAMEg6.SORTED.d(i4Var.r0()) && this.l) {
            return i4Var.o0(spliterator, false, vVar);
        }
        Object[] q = i4Var.o0(spliterator, true, vVar).q(vVar);
        Arrays.sort(q, this.m);
        return new CLASSNAMEo3(q);
    }

    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        if (!CLASSNAMEg6.SORTED.d(i) || !this.l) {
            return CLASSNAMEg6.SIZED.d(i) ? new T5(t5Var, this.m) : new P5(t5Var, this.m);
        }
        return t5Var;
    }
}
