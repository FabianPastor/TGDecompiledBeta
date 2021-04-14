package a;

import j$.util.function.C;
import java.util.function.LongConsumer;

/* renamed from: a.i0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi0 implements C {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongConsumer var_a;

    private /* synthetic */ CLASSNAMEi0(LongConsumer longConsumer) {
        this.var_a = longConsumer;
    }

    public static /* synthetic */ C b(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof CLASSNAMEj0 ? ((CLASSNAMEj0) longConsumer).var_a : new CLASSNAMEi0(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.var_a.accept(j);
    }

    public /* synthetic */ C g(C c) {
        return b(this.var_a.andThen(CLASSNAMEj0.a(c)));
    }
}
