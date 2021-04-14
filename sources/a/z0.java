package a;

import j$.util.function.I;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class z0 implements ObjLongConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ I var_a;

    private /* synthetic */ z0(I i) {
        this.var_a = i;
    }

    public static /* synthetic */ ObjLongConsumer a(I i) {
        if (i == null) {
            return null;
        }
        return i instanceof y0 ? ((y0) i).var_a : new z0(i);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.var_a.accept(obj, j);
    }
}
