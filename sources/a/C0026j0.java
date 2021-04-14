package a;

import j$.util.function.C;
import java.util.function.LongConsumer;

/* renamed from: a.j0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj0 implements LongConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C var_a;

    private /* synthetic */ CLASSNAMEj0(C c) {
        this.var_a = c;
    }

    public static /* synthetic */ LongConsumer a(C c) {
        if (c == null) {
            return null;
        }
        return c instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) c).var_a : new CLASSNAMEj0(c);
    }

    public /* synthetic */ void accept(long j) {
        this.var_a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.var_a.g(CLASSNAMEi0.b(longConsumer)));
    }
}
