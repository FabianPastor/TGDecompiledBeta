package j$;

import j$.util.function.BiConsumer;
import j$.util.function.Function;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.stream.CLASSNAMEm1;
import java.util.Set;
import java.util.stream.Collector;

public final /* synthetic */ class J0 implements CLASSNAMEm1 {
    final /* synthetic */ Collector a;

    private /* synthetic */ J0(Collector collector) {
        this.a = collector;
    }

    public static /* synthetic */ CLASSNAMEm1 a(Collector collector) {
        if (collector == null) {
            return null;
        }
        return collector instanceof K0 ? ((K0) collector).a : new J0(collector);
    }

    public /* synthetic */ BiConsumer accumulator() {
        return CLASSNAMEq.b(this.a.accumulator());
    }

    public /* synthetic */ Set characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ n combiner() {
        return CLASSNAMEu.b(this.a.combiner());
    }

    public /* synthetic */ Function finisher() {
        return M.c(this.a.finisher());
    }

    public /* synthetic */ J supplier() {
        return z0.a(this.a.supplier());
    }
}
