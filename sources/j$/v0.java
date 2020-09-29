package j$;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;
import j$.util.function.Function;
import j$.util.function.V;
import j$.util.stream.CLASSNAMEn1;
import java.util.Set;
import java.util.stream.Collector;

public final /* synthetic */ class v0 implements CLASSNAMEn1 {
    final /* synthetic */ Collector a;

    private /* synthetic */ v0(Collector collector) {
        this.a = collector;
    }

    public static /* synthetic */ CLASSNAMEn1 e(Collector collector) {
        if (collector == null) {
            return null;
        }
        return new v0(collector);
    }

    public /* synthetic */ BiConsumer a() {
        return CLASSNAMEy.b(this.a.accumulator());
    }

    public /* synthetic */ CLASSNAMEo b() {
        return B.b(this.a.combiner());
    }

    public /* synthetic */ V c() {
        return q0.a(this.a.supplier());
    }

    public /* synthetic */ Set characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ Function d() {
        return O.c(this.a.finisher());
    }
}
