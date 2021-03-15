package a;

import j$.util.function.D;
import java.util.function.LongFunction;

/* renamed from: a.k0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk0 implements D {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongFunction var_a;

    private /* synthetic */ CLASSNAMEk0(LongFunction longFunction) {
        this.var_a = longFunction;
    }

    public static /* synthetic */ D a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof CLASSNAMEl0 ? ((CLASSNAMEl0) longFunction).var_a : new CLASSNAMEk0(longFunction);
    }

    public /* synthetic */ Object apply(long j) {
        return this.var_a.apply(j);
    }
}
