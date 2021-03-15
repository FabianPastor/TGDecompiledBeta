package a;

import j$.util.function.z;
import java.util.function.IntToLongFunction;

/* renamed from: a.c0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc0 implements z {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntToLongFunction var_a;

    private /* synthetic */ CLASSNAMEc0(IntToLongFunction intToLongFunction) {
        this.var_a = intToLongFunction;
    }

    public static /* synthetic */ z a(IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return intToLongFunction instanceof CLASSNAMEd0 ? ((CLASSNAMEd0) intToLongFunction).var_a : new CLASSNAMEc0(intToLongFunction);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.var_a.applyAsLong(i);
    }
}
