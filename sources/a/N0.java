package a;

import j$.util.stream.CLASSNAMEm1;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final /* synthetic */ class N0 implements Collector {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CLASSNAMEm1 var_a;

    private /* synthetic */ N0(CLASSNAMEm1 m1Var) {
        this.var_a = m1Var;
    }

    public static /* synthetic */ Collector a(CLASSNAMEm1 m1Var) {
        if (m1Var == null) {
            return null;
        }
        return m1Var instanceof M0 ? ((M0) m1Var).var_a : new N0(m1Var);
    }

    public /* synthetic */ BiConsumer accumulator() {
        return CLASSNAMEu.a(this.var_a.accumulator());
    }

    public /* synthetic */ Set characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ BinaryOperator combiner() {
        return CLASSNAMEy.a(this.var_a.combiner());
    }

    public /* synthetic */ Function finisher() {
        return Q.a(this.var_a.finisher());
    }

    public /* synthetic */ Supplier supplier() {
        return D0.a(this.var_a.supplier());
    }
}
