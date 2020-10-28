package j$;

import j$.util.function.u;
import java.util.function.IntConsumer;

public final /* synthetic */ class V implements IntConsumer {
    final /* synthetic */ u a;

    private /* synthetic */ V(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ IntConsumer a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof U ? ((U) uVar).a : new V(uVar);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.l(U.b(intConsumer)));
    }
}
