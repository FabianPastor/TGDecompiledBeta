package j$;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;
import j$.util.function.Function;
import j$.util.function.V;
import j$.util.stream.CLASSNAMEn1;
import java.util.Set;
import java.util.stream.Collector;

public final /* synthetic */ class k0 implements CLASSNAMEn1 {
    final /* synthetic */ Collector a;

    private /* synthetic */ k0(Collector collector) {
        this.a = collector;
    }

    public static /* synthetic */ CLASSNAMEn1 e(Collector collector) {
        if (collector == null) {
            return null;
        }
        return new k0(collector);
    }

    public /* synthetic */ BiConsumer a() {
        return CLASSNAMEn.b(this.a.accumulator());
    }

    public /* synthetic */ CLASSNAMEo b() {
        return CLASSNAMEq.b(this.a.combiner());
    }

    public /* synthetic */ V c() {
        return f0.a(this.a.supplier());
    }

    public /* synthetic */ Set characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ Function d() {
        return D.c(this.a.finisher());
    }
}
