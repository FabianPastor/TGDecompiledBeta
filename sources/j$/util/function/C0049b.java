package j$.util.function;

/* renamed from: j$.util.function.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements BiFunction {
    public final /* synthetic */ BiFunction a;
    public final /* synthetic */ Function b;

    public /* synthetic */ CLASSNAMEb(BiFunction biFunction, Function function) {
        this.a = biFunction;
        this.b = function;
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return this.b.apply(this.a.apply(obj, obj2));
    }
}
