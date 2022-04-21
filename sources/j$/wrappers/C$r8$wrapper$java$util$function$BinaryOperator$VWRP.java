package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BinaryOperator$-V-WRP  reason: invalid class name */
/* compiled from: BinaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BinaryOperator$VWRP implements BinaryOperator {
    final /* synthetic */ java.util.function.BinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BinaryOperator$VWRP(java.util.function.BinaryOperator binaryOperator) {
        this.wrappedValue = binaryOperator;
    }

    public static /* synthetic */ BinaryOperator convert(java.util.function.BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof C$r8$wrapper$java$util$function$BinaryOperator$WRP ? ((C$r8$wrapper$java$util$function$BinaryOperator$WRP) binaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$BinaryOperator$VWRP(binaryOperator);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.wrappedValue.apply(obj, obj2);
    }
}
