package a;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

/* renamed from: a.u0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu0 implements G {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ObjDoubleConsumer var_a;

    private /* synthetic */ CLASSNAMEu0(ObjDoubleConsumer objDoubleConsumer) {
        this.var_a = objDoubleConsumer;
    }

    public static /* synthetic */ G a(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof CLASSNAMEv0 ? ((CLASSNAMEv0) objDoubleConsumer).var_a : new CLASSNAMEu0(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.var_a.accept(obj, d);
    }
}
