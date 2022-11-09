package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.wrappers.CLASSNAMEj0;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.o  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEo implements Consumer, j$.util.function.y {
    public final /* synthetic */ int a = 5;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ CLASSNAMEo(BiConsumer biConsumer, Object obj) {
        this.b = biConsumer;
        this.c = obj;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.a) {
            case 0:
                AtomicBoolean atomicBoolean = (AtomicBoolean) this.b;
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) this.c;
                if (obj == null) {
                    atomicBoolean.set(true);
                    return;
                } else {
                    concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
                    return;
                }
            case 5:
                ((BiConsumer) this.b).accept(this.c, obj);
                return;
            default:
                ((CLASSNAMEm4) this.b).f((Consumer) this.c, obj);
                return;
        }
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return consumer.getClass();
            case 5:
                return consumer.getClass();
            default:
                return consumer.getClass();
        }
    }

    @Override // j$.util.function.y
    public Object get() {
        switch (this.a) {
            case 1:
                return new CLASSNAMEi1((EnumCLASSNAMEk1) this.b, (j$.wrappers.E) this.c);
            case 2:
                return new CLASSNAMEg1((EnumCLASSNAMEk1) this.b, (j$.wrappers.V) this.c);
            case 3:
                return new CLASSNAMEh1((EnumCLASSNAMEk1) this.b, (CLASSNAMEj0) this.c);
            default:
                return new CLASSNAMEf1((EnumCLASSNAMEk1) this.b, (Predicate) this.c);
        }
    }

    public /* synthetic */ CLASSNAMEo(EnumCLASSNAMEk1 enumCLASSNAMEk1, Predicate predicate) {
        this.b = enumCLASSNAMEk1;
        this.c = predicate;
    }

    public /* synthetic */ CLASSNAMEo(EnumCLASSNAMEk1 enumCLASSNAMEk1, j$.wrappers.E e) {
        this.b = enumCLASSNAMEk1;
        this.c = e;
    }

    public /* synthetic */ CLASSNAMEo(EnumCLASSNAMEk1 enumCLASSNAMEk1, j$.wrappers.V v) {
        this.b = enumCLASSNAMEk1;
        this.c = v;
    }

    public /* synthetic */ CLASSNAMEo(EnumCLASSNAMEk1 enumCLASSNAMEk1, CLASSNAMEj0 CLASSNAMEj0) {
        this.b = enumCLASSNAMEk1;
        this.c = CLASSNAMEj0;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEm4 CLASSNAMEm4, Consumer consumer) {
        this.b = CLASSNAMEm4;
        this.c = consumer;
    }

    public /* synthetic */ CLASSNAMEo(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.b = atomicBoolean;
        this.c = concurrentHashMap;
    }
}
