package j$;

import j$.util.stream.CLASSNAMEn1;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final /* synthetic */ class N0 implements Collector {
    final /* synthetic */ CLASSNAMEn1 a;

    private /* synthetic */ N0(CLASSNAMEn1 n1Var) {
        this.a = n1Var;
    }

    public static /* synthetic */ Collector a(CLASSNAMEn1 n1Var) {
        if (n1Var == null) {
            return null;
        }
        return n1Var instanceof M0 ? ((M0) n1Var).a : new N0(n1Var);
    }

    public /* synthetic */ BiConsumer accumulator() {
        return CLASSNAMEu.a(this.a.accumulator());
    }

    public /* synthetic */ Set characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ BinaryOperator combiner() {
        return CLASSNAMEy.a(this.a.combiner());
    }

    public /* synthetic */ Function finisher() {
        return Q.a(this.a.finisher());
    }

    public /* synthetic */ Supplier supplier() {
        return D0.a(this.a.supplier());
    }
}
