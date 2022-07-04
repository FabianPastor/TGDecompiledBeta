package j$.wrappers;

import j$.util.function.Function;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Function$-V-WRP  reason: invalid class name */
/* compiled from: Function */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Function$VWRP implements Function {
    final /* synthetic */ java.util.function.Function wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Function$VWRP(java.util.function.Function function) {
        this.wrappedValue = function;
    }

    public static /* synthetic */ Function convert(java.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof C$r8$wrapper$java$util$function$Function$WRP ? ((C$r8$wrapper$java$util$function$Function$WRP) function).wrappedValue : new C$r8$wrapper$java$util$function$Function$VWRP(function);
    }

    public /* synthetic */ Function andThen(Function function) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.wrappedValue.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }
}
