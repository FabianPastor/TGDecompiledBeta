package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.function.Supplier;
import j$.util.stream.Collectors;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public interface Collector<T, A, R> {

    public enum Characteristics {
        CONCURRENT,
        UNORDERED,
        IDENTITY_FINISH
    }

    BiConsumer<A, T> accumulator();

    Set<Characteristics> characteristics();

    BinaryOperator<A> combiner();

    Function<A, R> finisher();

    Supplier<A> supplier();

    /* renamed from: j$.util.stream.Collector$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <T, R> Collector<T, R, R> of(Supplier<R> supplier, BiConsumer<R, T> biConsumer, BinaryOperator<R> binaryOperator, Characteristics... characteristics) {
            Set<Characteristics> set;
            supplier.getClass();
            biConsumer.getClass();
            binaryOperator.getClass();
            characteristics.getClass();
            if (characteristics.length == 0) {
                set = Collectors.CH_ID;
            } else {
                set = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, characteristics));
            }
            return new Collectors.CollectorImpl(supplier, biConsumer, binaryOperator, set);
        }

        public static <T, A, R> Collector<T, A, R> of(Supplier<A> supplier, BiConsumer<A, T> biConsumer, BinaryOperator<A> binaryOperator, Function<A, R> function, Characteristics... characteristics) {
            supplier.getClass();
            biConsumer.getClass();
            binaryOperator.getClass();
            function.getClass();
            characteristics.getClass();
            Set<Characteristics> set = Collectors.CH_NOID;
            if (characteristics.length > 0) {
                EnumSet noneOf = EnumSet.noneOf(Characteristics.class);
                Collections.addAll(noneOf, characteristics);
                set = Collections.unmodifiableSet(noneOf);
            }
            return new Collectors.CollectorImpl(supplier, biConsumer, binaryOperator, function, set);
        }
    }
}
