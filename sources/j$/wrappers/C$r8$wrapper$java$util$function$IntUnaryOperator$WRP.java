package j$.wrappers;

import java.util.function.IntUnaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntUnaryOperator$-WRP  reason: invalid class name */
/* compiled from: IntUnaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntUnaryOperator$WRP implements IntUnaryOperator {
    final /* synthetic */ j$.util.function.IntUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntUnaryOperator$WRP(j$.util.function.IntUnaryOperator intUnaryOperator) {
        this.wrappedValue = intUnaryOperator;
    }

    public static /* synthetic */ IntUnaryOperator convert(j$.util.function.IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP) intUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$IntUnaryOperator$WRP(intUnaryOperator);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP.convert(intUnaryOperator)));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return this.wrappedValue.applyAsInt(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP.convert(intUnaryOperator)));
    }
}
