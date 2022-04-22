package j$.wrappers;

import j$.util.function.BiConsumer;
import j$.util.function.Function;
import j$.util.function.b;
import j$.util.function.y;
import java.util.Set;
import java.util.stream.Collector;

public final /* synthetic */ class J0 {
    final /* synthetic */ Collector a;

    private /* synthetic */ J0(Collector collector) {
        this.a = collector;
    }

    public static /* synthetic */ J0 d(Collector collector) {
        if (collector == null) {
            return null;
        }
        return collector instanceof K0 ? ((K0) collector).a : new J0(collector);
    }

    public BiConsumer a() {
        return CLASSNAMEq.a(this.a.accumulator());
    }

    public Set b() {
        return this.a.characteristics();
    }

    public b c() {
        return CLASSNAMEu.a(this.a.combiner());
    }

    public Function e() {
        return M.a(this.a.finisher());
    }

    public y f() {
        return z0.a(this.a.supplier());
    }
}
