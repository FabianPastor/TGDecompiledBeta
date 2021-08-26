package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;

public final /* synthetic */ class C implements CLASSNAMEb {
    public final /* synthetic */ int a;
    public final /* synthetic */ BiConsumer b;

    public /* synthetic */ C(BiConsumer biConsumer, int i) {
        this.a = i;
        if (i == 1) {
            this.b = biConsumer;
        } else if (i != 2) {
            this.b = biConsumer;
        } else {
            this.b = biConsumer;
        }
    }

    public final Object apply(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                this.b.accept(obj, obj2);
                return obj;
            case 1:
                this.b.accept(obj, obj2);
                return obj;
            default:
                this.b.accept(obj, obj2);
                return obj;
        }
    }

    public BiFunction b(Function function) {
        switch (this.a) {
            case 0:
                function.getClass();
                return new a((BiFunction) this, function);
            case 1:
                function.getClass();
                return new a((BiFunction) this, function);
            default:
                function.getClass();
                return new a((BiFunction) this, function);
        }
    }
}
