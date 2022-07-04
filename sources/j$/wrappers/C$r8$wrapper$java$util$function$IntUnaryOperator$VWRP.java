package j$.wrappers;

import j$.util.function.IntUnaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntUnaryOperator$-V-WRP  reason: invalid class name */
/* compiled from: IntUnaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP implements IntUnaryOperator {
    final /* synthetic */ java.util.function.IntUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP(java.util.function.IntUnaryOperator intUnaryOperator) {
        this.wrappedValue = intUnaryOperator;
    }

    public static /* synthetic */ IntUnaryOperator convert(java.util.function.IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof C$r8$wrapper$java$util$function$IntUnaryOperator$WRP ? ((C$r8$wrapper$java$util$function$IntUnaryOperator$WRP) intUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP(intUnaryOperator);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$IntUnaryOperator$WRP.convert(intUnaryOperator)));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return this.wrappedValue.applyAsInt(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$IntUnaryOperator$WRP.convert(intUnaryOperator)));
    }
}
