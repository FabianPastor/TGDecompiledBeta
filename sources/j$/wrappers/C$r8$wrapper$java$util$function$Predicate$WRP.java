package j$.wrappers;

import java.util.function.Predicate;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Predicate$-WRP  reason: invalid class name */
/* compiled from: Predicate */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Predicate$WRP implements Predicate {
    final /* synthetic */ j$.util.function.Predicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Predicate$WRP(j$.util.function.Predicate predicate) {
        this.wrappedValue = predicate;
    }

    public static /* synthetic */ Predicate convert(j$.util.function.Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof C$r8$wrapper$java$util$function$Predicate$VWRP ? ((C$r8$wrapper$java$util$function$Predicate$VWRP) predicate).wrappedValue : new C$r8$wrapper$java$util$function$Predicate$WRP(predicate);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return convert(this.wrappedValue.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.wrappedValue.test(obj);
    }
}
