package j$.util.concurrent;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes2.dex */
public final /* synthetic */ class a implements BiConsumer, BiFunction, Consumer {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ a(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = biConsumer;
        this.c = biConsumer2;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        ((Consumer) this.b).accept(obj);
        ((Consumer) this.c).accept(obj);
    }

    @Override // j$.util.function.BiFunction
    public BiFunction andThen(Function function) {
        function.getClass();
        return new a(this, function);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.function.BiFunction
    public Object apply(Object obj, Object obj2) {
        return ((Function) this.b).apply(((BiFunction) this.c).apply(obj, obj2));
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        switch (this.a) {
            case 0:
                biConsumer.getClass();
                return new a(this, biConsumer);
            default:
                biConsumer.getClass();
                return new a(this, biConsumer);
        }
    }

    public /* synthetic */ a(BiFunction biFunction, Function function) {
        this.c = biFunction;
        this.b = function;
    }

    @Override // j$.util.function.BiConsumer
    public void accept(Object obj, Object obj2) {
        switch (this.a) {
            case 0:
                ConcurrentMap concurrentMap = (ConcurrentMap) this.b;
                BiFunction biFunction = (BiFunction) this.c;
                while (!concurrentMap.replace(obj, obj2, biFunction.apply(obj, obj2)) && (obj2 = concurrentMap.get(obj)) != null) {
                }
                return;
            default:
                ((BiConsumer) this.b).accept(obj, obj2);
                ((BiConsumer) this.c).accept(obj, obj2);
                return;
        }
    }

    public /* synthetic */ a(Consumer consumer, Consumer consumer2) {
        this.b = consumer;
        this.c = consumer2;
    }

    public /* synthetic */ a(ConcurrentMap concurrentMap, BiFunction biFunction) {
        this.b = concurrentMap;
        this.c = biFunction;
    }
}
