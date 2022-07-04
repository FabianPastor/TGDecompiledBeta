package j$.wrappers;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$UnaryOperator$-WRP  reason: invalid class name */
/* compiled from: UnaryOperator */
public final /* synthetic */ class C$r8$wrapper$java$util$function$UnaryOperator$WRP implements UnaryOperator {
    final /* synthetic */ j$.util.function.UnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$UnaryOperator$WRP(j$.util.function.UnaryOperator unaryOperator) {
        this.wrappedValue = unaryOperator;
    }

    public static /* synthetic */ UnaryOperator convert(j$.util.function.UnaryOperator unaryOperator) {
        if (unaryOperator == null) {
            return null;
        }
        return unaryOperator instanceof C$r8$wrapper$java$util$function$UnaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$UnaryOperator$VWRP) unaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$UnaryOperator$WRP(unaryOperator);
    }

    public /* synthetic */ Function andThen(Function function) {
        return C$r8$wrapper$java$util$function$Function$WRP.convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.wrappedValue.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return C$r8$wrapper$java$util$function$Function$WRP.convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }
}
