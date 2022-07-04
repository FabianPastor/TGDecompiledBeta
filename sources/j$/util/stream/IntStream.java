package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.IntSummaryStatistics;
import j$.util.Iterator;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.IntBinaryOperator;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.IntPredicate;
import j$.util.function.IntSupplier;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.function.ObjIntConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;

public interface IntStream extends BaseStream<Integer, IntStream> {
    boolean allMatch(IntPredicate intPredicate);

    boolean anyMatch(IntPredicate intPredicate);

    DoubleStream asDoubleStream();

    LongStream asLongStream();

    OptionalDouble average();

    Stream<Integer> boxed();

    <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> objIntConsumer, BiConsumer<R, R> biConsumer);

    long count();

    IntStream distinct();

    IntStream filter(IntPredicate intPredicate);

    OptionalInt findAny();

    OptionalInt findFirst();

    IntStream flatMap(IntFunction<? extends IntStream> intFunction);

    void forEach(IntConsumer intConsumer);

    void forEachOrdered(IntConsumer intConsumer);

    PrimitiveIterator.OfInt iterator();

    IntStream limit(long j);

    IntStream map(IntUnaryOperator intUnaryOperator);

    DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction);

    LongStream mapToLong(IntToLongFunction intToLongFunction);

    <U> Stream<U> mapToObj(IntFunction<? extends U> intFunction);

    OptionalInt max();

    OptionalInt min();

    boolean noneMatch(IntPredicate intPredicate);

    IntStream parallel();

    IntStream peek(IntConsumer intConsumer);

    int reduce(int i, IntBinaryOperator intBinaryOperator);

    OptionalInt reduce(IntBinaryOperator intBinaryOperator);

    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    Spliterator.OfInt spliterator();

    int sum();

    IntSummaryStatistics summaryStatistics();

    int[] toArray();

    /* renamed from: j$.util.stream.IntStream$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.IntStreamBuilderImpl();
        }

        public static IntStream empty() {
            return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false);
        }

        public static IntStream of(int t) {
            return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(t), false);
        }

        public static IntStream of(int... values) {
            return DesugarArrays.stream(values);
        }

        public static IntStream iterate(int seed, IntUnaryOperator f) {
            f.getClass();
            return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(new Object(seed, f) {
                int t;
                final /* synthetic */ IntUnaryOperator val$f;
                final /* synthetic */ int val$seed;

                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                    PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, consumer);
                }

                public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                    PrimitiveIterator.OfInt.CC.$default$forEachRemaining((PrimitiveIterator.OfInt) this, intConsumer);
                }

                public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                    forEachRemaining((IntConsumer) obj);
                }

                public /* synthetic */ void remove() {
                    Iterator.CC.$default$remove(this);
                }

                {
                    this.val$seed = r1;
                    this.val$f = r2;
                    this.t = r1;
                }

                public boolean hasNext() {
                    return true;
                }

                public int nextInt() {
                    int v = this.t;
                    this.t = this.val$f.applyAsInt(this.t);
                    return v;
                }
            }, 1296), false);
        }

        public static IntStream generate(IntSupplier s) {
            s.getClass();
            return StreamSupport.intStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Long.MAX_VALUE, s), false);
        }

        public static IntStream range(int startInclusive, int endExclusive) {
            if (startInclusive >= endExclusive) {
                return empty();
            }
            return StreamSupport.intStream(new Streams.RangeIntSpliterator(startInclusive, endExclusive, false), false);
        }

        public static IntStream rangeClosed(int startInclusive, int endInclusive) {
            if (startInclusive > endInclusive) {
                return empty();
            }
            return StreamSupport.intStream(new Streams.RangeIntSpliterator(startInclusive, endInclusive, true), false);
        }

        public static IntStream concat(IntStream a, IntStream b) {
            a.getClass();
            b.getClass();
            return (IntStream) StreamSupport.intStream(new Streams.ConcatSpliterator.OfInt(a.spliterator(), b.spliterator()), a.isParallel() || b.isParallel()).onClose(Streams.composedClose(a, b));
        }
    }

    public interface Builder extends IntConsumer {
        void accept(int i);

        Builder add(int i);

        IntStream build();

        /* renamed from: j$.util.stream.IntStream$Builder$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, int t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
