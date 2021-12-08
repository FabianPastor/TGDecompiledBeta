package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.IntSummaryStatistics;
import j$.util.LongSummaryStatistics;
import j$.util.Map;
import j$.util.Objects;
import j$.util.Optional;
import j$.util.StringJoiner;
import j$.util.concurrent.ConcurrentMap;
import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.stream.Collector;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public final class Collectors {
    static final Set<Collector.Characteristics> CH_CONCURRENT_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_CONCURRENT_NOID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED));
    static final Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();
    static final Set<Collector.Characteristics> CH_UNORDERED_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));

    private Collectors() {
    }

    static /* synthetic */ Object lambda$throwingMerger$0(Object u, Object v) {
        throw new IllegalStateException(String.format("Duplicate key %s", new Object[]{u}));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return Collectors$$ExternalSyntheticLambda30.INSTANCE;
    }

    /* access modifiers changed from: private */
    public static <I, R> Function<I, R> castingIdentity() {
        return Collectors$$ExternalSyntheticLambda53.INSTANCE;
    }

    static /* synthetic */ Object lambda$castingIdentity$1(Object i) {
        return i;
    }

    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final BiConsumer<A, T> accumulator;
        private final Set<Collector.Characteristics> characteristics;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Supplier<A> supplier;

        CollectorImpl(Supplier<A> supplier2, BiConsumer<A, T> biConsumer, BinaryOperator<A> binaryOperator, Function<A, R> function, Set<Collector.Characteristics> characteristics2) {
            this.supplier = supplier2;
            this.accumulator = biConsumer;
            this.combiner = binaryOperator;
            this.finisher = function;
            this.characteristics = characteristics2;
        }

        CollectorImpl(Supplier<A> supplier2, BiConsumer<A, T> biConsumer, BinaryOperator<A> binaryOperator, Set<Collector.Characteristics> characteristics2) {
            this(supplier2, biConsumer, binaryOperator, Collectors.castingIdentity(), characteristics2);
        }

        public BiConsumer<A, T> accumulator() {
            return this.accumulator;
        }

        public Supplier<A> supplier() {
            return this.supplier;
        }

        public BinaryOperator<A> combiner() {
            return this.combiner;
        }

        public Function<A, R> finisher() {
            return this.finisher;
        }

        public Set<Collector.Characteristics> characteristics() {
            return this.characteristics;
        }
    }

    public static <T, C extends Collection<T>> Collector<T, ?, C> toCollection(Supplier<C> supplier) {
        return new CollectorImpl(supplier, Collectors$$ExternalSyntheticLambda19.INSTANCE, Collectors$$ExternalSyntheticLambda33.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, List<T>> toList() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda77.INSTANCE, Collectors$$ExternalSyntheticLambda20.INSTANCE, Collectors$$ExternalSyntheticLambda36.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, Set<T>> toSet() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda80.INSTANCE, Collectors$$ExternalSyntheticLambda21.INSTANCE, Collectors$$ExternalSyntheticLambda38.INSTANCE, CH_UNORDERED_ID);
    }

    public static Collector<CharSequence, ?, String> joining() {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda76.INSTANCE, Collectors$$ExternalSyntheticLambda18.INSTANCE, Collectors$$ExternalSyntheticLambda32.INSTANCE, Collectors$$ExternalSyntheticLambda55.INSTANCE, CH_NOID);
    }

    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, "", "");
    }

    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return new CollectorImpl(new Collectors$$ExternalSyntheticLambda66(delimiter, prefix, suffix), Collectors$$ExternalSyntheticLambda22.INSTANCE, Collectors$$ExternalSyntheticLambda39.INSTANCE, Collectors$$ExternalSyntheticLambda56.INSTANCE, CH_NOID);
    }

    static /* synthetic */ StringJoiner lambda$joining$6(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return new StringJoiner(delimiter, prefix, suffix);
    }

    private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> binaryOperator) {
        return new Collectors$$ExternalSyntheticLambda26(binaryOperator);
    }

    static /* synthetic */ Map lambda$mapMerger$7(BinaryOperator mergeFunction, Map m1, Map m2) {
        for (Map.Entry<K, V> e : m2.entrySet()) {
            Map.EL.merge(m1, e.getKey(), e.getValue(), mergeFunction);
        }
        return m1;
    }

    public static <T, U, A, R> Collector<T, ?, R> mapping(Function<? super T, ? extends U> function, Collector<? super U, A, R> collector) {
        return new CollectorImpl(collector.supplier(), new Collectors$$ExternalSyntheticLambda0(collector.accumulator(), function), collector.combiner(), collector.finisher(), collector.characteristics());
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [j$.util.stream.Collector, j$.util.stream.Collector<T, A, R>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T, A, R, RR> j$.util.stream.Collector<T, A, RR> collectingAndThen(j$.util.stream.Collector<T, A, R> r8, j$.util.function.Function<R, RR> r9) {
        /*
            java.util.Set r0 = r8.characteristics()
            j$.util.stream.Collector$Characteristics r1 = j$.util.stream.Collector.Characteristics.IDENTITY_FINISH
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0023
            int r1 = r0.size()
            r2 = 1
            if (r1 != r2) goto L_0x0016
            java.util.Set<j$.util.stream.Collector$Characteristics> r0 = CH_NOID
            goto L_0x0023
        L_0x0016:
            java.util.EnumSet r0 = java.util.EnumSet.copyOf(r0)
            j$.util.stream.Collector$Characteristics r1 = j$.util.stream.Collector.Characteristics.IDENTITY_FINISH
            r0.remove(r1)
            java.util.Set r0 = java.util.Collections.unmodifiableSet(r0)
        L_0x0023:
            j$.util.stream.Collectors$CollectorImpl r7 = new j$.util.stream.Collectors$CollectorImpl
            j$.util.function.Supplier r2 = r8.supplier()
            j$.util.function.BiConsumer r3 = r8.accumulator()
            j$.util.function.BinaryOperator r4 = r8.combiner()
            j$.util.function.Function r1 = r8.finisher()
            j$.util.function.Function r5 = r1.andThen(r9)
            r1 = r7
            r6 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.Collectors.collectingAndThen(j$.util.stream.Collector, j$.util.function.Function):j$.util.stream.Collector");
    }

    public static <T> Collector<T, ?, Long> counting() {
        return reducing(0L, Collectors$$ExternalSyntheticLambda54.INSTANCE, Collectors$$ExternalSyntheticLambda31.INSTANCE);
    }

    static /* synthetic */ Long lambda$counting$9(Object e) {
        return 1L;
    }

    public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> comparator) {
        return reducing(BinaryOperator.CC.minBy(comparator));
    }

    public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> comparator) {
        return reducing(BinaryOperator.CC.maxBy(comparator));
    }

    public static <T> Collector<T, ?, Integer> summingInt(ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda74.INSTANCE, new Collectors$$ExternalSyntheticLambda13(toIntFunction), Collectors$$ExternalSyntheticLambda43.INSTANCE, Collectors$$ExternalSyntheticLambda60.INSTANCE, CH_NOID);
    }

    static /* synthetic */ int[] lambda$summingInt$10() {
        return new int[1];
    }

    static /* synthetic */ void lambda$summingInt$11(ToIntFunction mapper, int[] a, Object t) {
        a[0] = a[0] + mapper.applyAsInt(t);
    }

    static /* synthetic */ int[] lambda$summingInt$12(int[] a, int[] b) {
        a[0] = a[0] + b[0];
        return a;
    }

    public static <T> Collector<T, ?, Long> summingLong(ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda75.INSTANCE, new Collectors$$ExternalSyntheticLambda17(toLongFunction), Collectors$$ExternalSyntheticLambda46.INSTANCE, Collectors$$ExternalSyntheticLambda63.INSTANCE, CH_NOID);
    }

    static /* synthetic */ long[] lambda$summingLong$14() {
        return new long[1];
    }

    static /* synthetic */ void lambda$summingLong$15(ToLongFunction mapper, long[] a, Object t) {
        a[0] = a[0] + mapper.applyAsLong(t);
    }

    static /* synthetic */ long[] lambda$summingLong$16(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        return a;
    }

    public static <T> Collector<T, ?, Double> summingDouble(ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda73.INSTANCE, new Collectors$$ExternalSyntheticLambda11(toDoubleFunction), Collectors$$ExternalSyntheticLambda42.INSTANCE, Collectors$$ExternalSyntheticLambda59.INSTANCE, CH_NOID);
    }

    static /* synthetic */ double[] lambda$summingDouble$18() {
        return new double[3];
    }

    static /* synthetic */ void lambda$summingDouble$19(ToDoubleFunction mapper, double[] a, Object t) {
        sumWithCompensation(a, mapper.applyAsDouble(t));
        a[2] = a[2] + mapper.applyAsDouble(t);
    }

    static /* synthetic */ double[] lambda$summingDouble$20(double[] a, double[] b) {
        sumWithCompensation(a, b[0]);
        a[2] = a[2] + b[2];
        return sumWithCompensation(a, b[1]);
    }

    static double[] sumWithCompensation(double[] intermediateSum, double value) {
        double tmp = value - intermediateSum[1];
        double sum = intermediateSum[0];
        double velvel = sum + tmp;
        intermediateSum[1] = (velvel - sum) - tmp;
        intermediateSum[0] = velvel;
        return intermediateSum;
    }

    static double computeFinalSum(double[] summands) {
        double tmp = summands[0] + summands[1];
        double simpleSum = summands[summands.length - 1];
        if (!Double.isNaN(tmp) || !Double.isInfinite(simpleSum)) {
            return tmp;
        }
        return simpleSum;
    }

    public static <T> Collector<T, ?, Double> averagingInt(ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda71.INSTANCE, new Collectors$$ExternalSyntheticLambda14(toIntFunction), Collectors$$ExternalSyntheticLambda44.INSTANCE, Collectors$$ExternalSyntheticLambda61.INSTANCE, CH_NOID);
    }

    static /* synthetic */ long[] lambda$averagingInt$22() {
        return new long[2];
    }

    static /* synthetic */ void lambda$averagingInt$23(ToIntFunction mapper, long[] a, Object t) {
        a[0] = a[0] + ((long) mapper.applyAsInt(t));
        a[1] = a[1] + 1;
    }

    static /* synthetic */ long[] lambda$averagingInt$24(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        a[1] = a[1] + b[1];
        return a;
    }

    static /* synthetic */ Double lambda$averagingInt$25(long[] a) {
        double d;
        if (a[1] == 0) {
            d = 0.0d;
        } else {
            double d2 = (double) a[0];
            double d3 = (double) a[1];
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        }
        return Double.valueOf(d);
    }

    public static <T> Collector<T, ?, Double> averagingLong(ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda72.INSTANCE, new Collectors$$ExternalSyntheticLambda16(toLongFunction), Collectors$$ExternalSyntheticLambda45.INSTANCE, Collectors$$ExternalSyntheticLambda62.INSTANCE, CH_NOID);
    }

    static /* synthetic */ long[] lambda$averagingLong$26() {
        return new long[2];
    }

    static /* synthetic */ void lambda$averagingLong$27(ToLongFunction mapper, long[] a, Object t) {
        a[0] = a[0] + mapper.applyAsLong(t);
        a[1] = a[1] + 1;
    }

    static /* synthetic */ long[] lambda$averagingLong$28(long[] a, long[] b) {
        a[0] = a[0] + b[0];
        a[1] = a[1] + b[1];
        return a;
    }

    static /* synthetic */ Double lambda$averagingLong$29(long[] a) {
        double d;
        if (a[1] == 0) {
            d = 0.0d;
        } else {
            double d2 = (double) a[0];
            double d3 = (double) a[1];
            Double.isNaN(d2);
            Double.isNaN(d3);
            d = d2 / d3;
        }
        return Double.valueOf(d);
    }

    public static <T> Collector<T, ?, Double> averagingDouble(ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda70.INSTANCE, new Collectors$$ExternalSyntheticLambda10(toDoubleFunction), Collectors$$ExternalSyntheticLambda41.INSTANCE, Collectors$$ExternalSyntheticLambda58.INSTANCE, CH_NOID);
    }

    static /* synthetic */ double[] lambda$averagingDouble$30() {
        return new double[4];
    }

    static /* synthetic */ void lambda$averagingDouble$31(ToDoubleFunction mapper, double[] a, Object t) {
        sumWithCompensation(a, mapper.applyAsDouble(t));
        a[2] = a[2] + 1.0d;
        a[3] = a[3] + mapper.applyAsDouble(t);
    }

    static /* synthetic */ double[] lambda$averagingDouble$32(double[] a, double[] b) {
        sumWithCompensation(a, b[0]);
        sumWithCompensation(a, b[1]);
        a[2] = a[2] + b[2];
        a[3] = a[3] + b[3];
        return a;
    }

    static /* synthetic */ Double lambda$averagingDouble$33(double[] a) {
        double d = 0.0d;
        if (a[2] != 0.0d) {
            d = computeFinalSum(a) / a[2];
        }
        return Double.valueOf(d);
    }

    public static <T> Collector<T, ?, T> reducing(T identity, BinaryOperator<T> binaryOperator) {
        return new CollectorImpl(boxSupplier(identity), new Collectors$$ExternalSyntheticLambda2(binaryOperator), new Collectors$$ExternalSyntheticLambda28(binaryOperator), Collectors$$ExternalSyntheticLambda64.INSTANCE, CH_NOID);
    }

    static /* synthetic */ void lambda$reducing$34(BinaryOperator op, Object[] a, Object t) {
        a[0] = op.apply(a[0], t);
    }

    static /* synthetic */ Object[] lambda$reducing$35(BinaryOperator op, Object[] a, Object[] b) {
        a[0] = op.apply(a[0], b[0]);
        return a;
    }

    static /* synthetic */ Object lambda$reducing$36(Object[] a) {
        return a[0];
    }

    private static <T> Supplier<T[]> boxSupplier(T identity) {
        return new Collectors$$ExternalSyntheticLambda67(identity);
    }

    static /* synthetic */ Object[] lambda$boxSupplier$37(Object identity) {
        return new Object[]{identity};
    }

    public static <T> Collector<T, ?, Optional<T>> reducing(BinaryOperator<T> binaryOperator) {
        return new CollectorImpl(new Collectors$$ExternalSyntheticLambda68(binaryOperator), Collectors$$ExternalSyntheticLambda23.INSTANCE, Collectors$$ExternalSyntheticLambda40.INSTANCE, Collectors$$ExternalSyntheticLambda57.INSTANCE, CH_NOID);
    }

    static /* synthetic */ AnonymousClass1OptionalBox lambda$reducing$38(final BinaryOperator op) {
        return new Consumer<T>() {
            boolean present = false;
            T value = null;

            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return Consumer.CC.$default$andThen(this, consumer);
            }

            public void accept(T t) {
                if (this.present) {
                    this.value = BinaryOperator.this.apply(this.value, t);
                    return;
                }
                this.value = t;
                this.present = true;
            }
        };
    }

    static /* synthetic */ AnonymousClass1OptionalBox lambda$reducing$39(AnonymousClass1OptionalBox a, AnonymousClass1OptionalBox b) {
        if (b.present) {
            a.accept(b.value);
        }
        return a;
    }

    public static <T, U> Collector<T, ?, U> reducing(U identity, Function<? super T, ? extends U> function, BinaryOperator<U> binaryOperator) {
        return new CollectorImpl(boxSupplier(identity), new Collectors$$ExternalSyntheticLambda3(binaryOperator, function), new Collectors$$ExternalSyntheticLambda29(binaryOperator), Collectors$$ExternalSyntheticLambda65.INSTANCE, CH_NOID);
    }

    static /* synthetic */ void lambda$reducing$41(BinaryOperator op, Function mapper, Object[] a, Object t) {
        a[0] = op.apply(a[0], mapper.apply(t));
    }

    static /* synthetic */ Object[] lambda$reducing$42(BinaryOperator op, Object[] a, Object[] b) {
        a[0] = op.apply(a[0], b[0]);
        return a;
    }

    static /* synthetic */ Object lambda$reducing$43(Object[] a) {
        return a[0];
    }

    public static <T, K> Collector<T, ?, java.util.Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> function) {
        return groupingBy(function, toList());
    }

    public static <T, K, A, D> Collector<T, ?, java.util.Map<K, D>> groupingBy(Function<? super T, ? extends K> function, Collector<? super T, A, D> collector) {
        return groupingBy(function, Collectors$$ExternalSyntheticLambda79.INSTANCE, collector);
    }

    public static <T, K, D, A, M extends java.util.Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> function, Supplier<M> supplier, Collector<? super T, A, D> collector) {
        Collectors$$ExternalSyntheticLambda6 collectors$$ExternalSyntheticLambda6 = new Collectors$$ExternalSyntheticLambda6(function, collector.supplier(), collector.accumulator());
        BinaryOperator<M> mapMerger = mapMerger(collector.combiner());
        Supplier<M> supplier2 = supplier;
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(supplier2, collectors$$ExternalSyntheticLambda6, mapMerger, CH_ID);
        }
        return new CollectorImpl(supplier2, collectors$$ExternalSyntheticLambda6, mapMerger, new Collectors$$ExternalSyntheticLambda47(collector.finisher()), CH_NOID);
    }

    public static <T, K> Collector<T, ?, ConcurrentMap<K, List<T>>> groupingByConcurrent(Function<? super T, ? extends K> function) {
        return groupingByConcurrent(function, Collectors$$ExternalSyntheticLambda83.INSTANCE, toList());
    }

    public static <T, K, A, D> Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(Function<? super T, ? extends K> function, Collector<? super T, A, D> collector) {
        return groupingByConcurrent(function, Collectors$$ExternalSyntheticLambda83.INSTANCE, collector);
    }

    public static <T, K, A, D, M extends ConcurrentMap<K, D>> Collector<T, ?, M> groupingByConcurrent(Function<? super T, ? extends K> function, Supplier<M> supplier, Collector<? super T, A, D> collector) {
        Collectors$$ExternalSyntheticLambda7 collectors$$ExternalSyntheticLambda7;
        Supplier<A> supplier2 = collector.supplier();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        BinaryOperator<M> mapMerger = mapMerger(collector.combiner());
        Supplier<M> supplier3 = supplier;
        if (collector.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
            collectors$$ExternalSyntheticLambda7 = new Collectors$$ExternalSyntheticLambda7(function, supplier2, accumulator);
        } else {
            collectors$$ExternalSyntheticLambda7 = new Collectors$$ExternalSyntheticLambda8(function, supplier2, accumulator);
        }
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(supplier3, collectors$$ExternalSyntheticLambda7, mapMerger, CH_CONCURRENT_ID);
        }
        return new CollectorImpl(supplier3, collectors$$ExternalSyntheticLambda7, mapMerger, new Collectors$$ExternalSyntheticLambda48(collector.finisher()), CH_CONCURRENT_NOID);
    }

    static /* synthetic */ void lambda$groupingByConcurrent$51(Function classifier, Supplier downstreamSupplier, BiConsumer downstreamAccumulator, ConcurrentMap m, Object t) {
        A resultContainer = ConcurrentMap.EL.computeIfAbsent(m, Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key"), new Collectors$$ExternalSyntheticLambda51(downstreamSupplier));
        synchronized (resultContainer) {
            downstreamAccumulator.accept(resultContainer, t);
        }
    }

    public static <T> Collector<T, ?, java.util.Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> predicate) {
        return partitioningBy(predicate, toList());
    }

    public static <T, D, A> Collector<T, ?, java.util.Map<Boolean, D>> partitioningBy(Predicate<? super T> predicate, Collector<? super T, A, D> collector) {
        Collectors$$ExternalSyntheticLambda1 collectors$$ExternalSyntheticLambda1 = new Collectors$$ExternalSyntheticLambda1(collector.accumulator(), predicate);
        Collectors$$ExternalSyntheticLambda27 collectors$$ExternalSyntheticLambda27 = new Collectors$$ExternalSyntheticLambda27(collector.combiner());
        Collectors$$ExternalSyntheticLambda69 collectors$$ExternalSyntheticLambda69 = new Collectors$$ExternalSyntheticLambda69(collector);
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl(collectors$$ExternalSyntheticLambda69, collectors$$ExternalSyntheticLambda1, collectors$$ExternalSyntheticLambda27, CH_ID);
        }
        return new CollectorImpl(collectors$$ExternalSyntheticLambda69, collectors$$ExternalSyntheticLambda1, collectors$$ExternalSyntheticLambda27, new Collectors$$ExternalSyntheticLambda52(collector), CH_NOID);
    }

    static /* synthetic */ void lambda$partitioningBy$54(BiConsumer downstreamAccumulator, Predicate predicate, Partition result, Object t) {
        downstreamAccumulator.accept(predicate.test(t) ? result.forTrue : result.forFalse, t);
    }

    static /* synthetic */ Partition lambda$partitioningBy$55(BinaryOperator op, Partition left, Partition right) {
        return new Partition(op.apply(left.forTrue, right.forTrue), op.apply(left.forFalse, right.forFalse));
    }

    static /* synthetic */ Partition lambda$partitioningBy$56(Collector downstream) {
        return new Partition(downstream.supplier().get(), downstream.supplier().get());
    }

    static /* synthetic */ java.util.Map lambda$partitioningBy$57(Collector downstream, Partition par) {
        return new Partition(downstream.finisher().apply(par.forTrue), downstream.finisher().apply(par.forFalse));
    }

    public static <T, K, U> Collector<T, ?, java.util.Map<K, U>> toMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2) {
        return toMap(function, function2, throwingMerger(), Collectors$$ExternalSyntheticLambda79.INSTANCE);
    }

    public static <T, K, U> Collector<T, ?, java.util.Map<K, U>> toMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator) {
        return toMap(function, function2, binaryOperator, Collectors$$ExternalSyntheticLambda79.INSTANCE);
    }

    public static <T, K, U, M extends java.util.Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator, Supplier<M> supplier) {
        return new CollectorImpl(supplier, new Collectors$$ExternalSyntheticLambda4(function, function2, binaryOperator), mapMerger(binaryOperator), CH_ID);
    }

    public static <T, K, U> Collector<T, ?, java.util.concurrent.ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2) {
        return toConcurrentMap(function, function2, throwingMerger(), Collectors$$ExternalSyntheticLambda83.INSTANCE);
    }

    public static <T, K, U> Collector<T, ?, java.util.concurrent.ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator) {
        return toConcurrentMap(function, function2, binaryOperator, Collectors$$ExternalSyntheticLambda83.INSTANCE);
    }

    public static <T, K, U, M extends java.util.concurrent.ConcurrentMap<K, U>> Collector<T, ?, M> toConcurrentMap(Function<? super T, ? extends K> function, Function<? super T, ? extends U> function2, BinaryOperator<U> binaryOperator, Supplier<M> supplier) {
        return new CollectorImpl(supplier, new Collectors$$ExternalSyntheticLambda5(function, function2, binaryOperator), mapMerger(binaryOperator), CH_CONCURRENT_ID);
    }

    public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda81.INSTANCE, new Collectors$$ExternalSyntheticLambda12(toIntFunction), Collectors$$ExternalSyntheticLambda35.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda82.INSTANCE, new Collectors$$ExternalSyntheticLambda15(toLongFunction), Collectors$$ExternalSyntheticLambda37.INSTANCE, CH_ID);
    }

    public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl(Collectors$$ExternalSyntheticLambda78.INSTANCE, new Collectors$$ExternalSyntheticLambda9(toDoubleFunction), Collectors$$ExternalSyntheticLambda34.INSTANCE, CH_ID);
    }

    private static final class Partition<T> extends AbstractMap<Boolean, T> implements java.util.Map<Boolean, T> {
        final T forFalse;
        final T forTrue;

        Partition(T forTrue2, T forFalse2) {
            this.forTrue = forTrue2;
            this.forFalse = forFalse2;
        }

        public Set<Map.Entry<Boolean, T>> entrySet() {
            return new AbstractSet<Map.Entry<Boolean, T>>() {
                public Iterator<Map.Entry<Boolean, T>> iterator() {
                    return Arrays.asList(new Map.Entry[]{new AbstractMap.SimpleImmutableEntry<>(false, Partition.this.forFalse), new AbstractMap.SimpleImmutableEntry<>(true, Partition.this.forTrue)}).iterator();
                }

                public int size() {
                    return 2;
                }
            };
        }
    }
}
