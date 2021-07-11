package j$;

import j$.util.stream.CLASSNAMEm1;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final /* synthetic */ class K0 implements Collector {
    final /* synthetic */ CLASSNAMEm1 a;

    private /* synthetic */ K0(CLASSNAMEm1 m1Var) {
        this.a = m1Var;
    }

    public static /* synthetic */ Collector a(CLASSNAMEm1 m1Var) {
        if (m1Var == null) {
            return null;
        }
        return m1Var instanceof J0 ? ((J0) m1Var).a : new K0(m1Var);
    }

    public /* synthetic */ BiConsumer accumulator() {
        return r.a(this.a.accumulator());
    }

    public /* synthetic */ Set characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ BinaryOperator combiner() {
        return CLASSNAMEv.a(this.a.combiner());
    }

    public /* synthetic */ Function finisher() {
        return N.a(this.a.finisher());
    }

    public /* synthetic */ Supplier supplier() {
        return A0.a(this.a.supplier());
    }
}
