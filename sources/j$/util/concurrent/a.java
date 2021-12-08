package j$.util.concurrent;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class a implements BiConsumer, BiFunction, Consumer {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ a(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = biConsumer;
        this.c = biConsumer2;
    }

    public void accept(Object obj) {
        ((Consumer) this.b).accept(obj);
        ((Consumer) this.c).accept(obj);
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public Object apply(Object obj, Object obj2) {
        return ((Function) this.b).apply(((BiFunction) this.c).apply(obj, obj2));
    }

    public BiConsumer b(BiConsumer biConsumer) {
        switch (this.a) {
            case 0:
                biConsumer.getClass();
                return new a((BiConsumer) this, biConsumer);
            default:
                biConsumer.getClass();
                return new a((BiConsumer) this, biConsumer);
        }
    }

    public /* synthetic */ a(BiFunction biFunction, Function function) {
        this.c = biFunction;
        this.b = function;
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x000e A[LOOP:0: B:3:0x000e->B:6:0x001c, LOOP_START, PHI: r5 
      PHI: (r5v1 java.lang.Object) = (r5v0 java.lang.Object), (r5v3 java.lang.Object) binds: [B:2:0x0006, B:6:0x001c] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void accept(java.lang.Object r4, java.lang.Object r5) {
        /*
            r3 = this;
            int r0 = r3.a
            switch(r0) {
                case 0: goto L_0x0006;
                default: goto L_0x0005;
            }
        L_0x0005:
            goto L_0x001f
        L_0x0006:
            java.lang.Object r0 = r3.b
            java.util.concurrent.ConcurrentMap r0 = (java.util.concurrent.ConcurrentMap) r0
            java.lang.Object r1 = r3.c
            j$.util.function.BiFunction r1 = (j$.util.function.BiFunction) r1
        L_0x000e:
            java.lang.Object r2 = r1.apply(r4, r5)
            boolean r5 = r0.replace(r4, r5, r2)
            if (r5 != 0) goto L_0x001e
            java.lang.Object r5 = r0.get(r4)
            if (r5 != 0) goto L_0x000e
        L_0x001e:
            return
        L_0x001f:
            java.lang.Object r0 = r3.b
            j$.util.function.BiConsumer r0 = (j$.util.function.BiConsumer) r0
            java.lang.Object r1 = r3.c
            j$.util.function.BiConsumer r1 = (j$.util.function.BiConsumer) r1
            r0.accept(r4, r5)
            r1.accept(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.a.accept(java.lang.Object, java.lang.Object):void");
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
