package j$.util.stream;

import j$.util.DesugarArrays;
import j$.util.Iterator;
import j$.util.LongSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.OptionalLong;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.LongBinaryOperator;
import j$.util.function.LongConsumer;
import j$.util.function.LongFunction;
import j$.util.function.LongPredicate;
import j$.util.function.LongSupplier;
import j$.util.function.LongToDoubleFunction;
import j$.util.function.LongToIntFunction;
import j$.util.function.LongUnaryOperator;
import j$.util.function.ObjLongConsumer;
import j$.util.function.Supplier;
import j$.util.stream.StreamSpliterators;
import j$.util.stream.Streams;

public interface LongStream extends BaseStream<Long, LongStream> {
    boolean allMatch(LongPredicate longPredicate);

    boolean anyMatch(LongPredicate longPredicate);

    DoubleStream asDoubleStream();

    OptionalDouble average();

    Stream<Long> boxed();

    <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> objLongConsumer, BiConsumer<R, R> biConsumer);

    long count();

    LongStream distinct();

    LongStream filter(LongPredicate longPredicate);

    OptionalLong findAny();

    OptionalLong findFirst();

    LongStream flatMap(LongFunction<? extends LongStream> longFunction);

    void forEach(LongConsumer longConsumer);

    void forEachOrdered(LongConsumer longConsumer);

    PrimitiveIterator.OfLong iterator();

    LongStream limit(long j);

    LongStream map(LongUnaryOperator longUnaryOperator);

    DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction);

    IntStream mapToInt(LongToIntFunction longToIntFunction);

    <U> Stream<U> mapToObj(LongFunction<? extends U> longFunction);

    OptionalLong max();

    OptionalLong min();

    boolean noneMatch(LongPredicate longPredicate);

    LongStream parallel();

    LongStream peek(LongConsumer longConsumer);

    long reduce(long j, LongBinaryOperator longBinaryOperator);

    OptionalLong reduce(LongBinaryOperator longBinaryOperator);

    LongStream sequential();

    LongStream skip(long j);

    LongStream sorted();

    Spliterator.OfLong spliterator();

    long sum();

    LongSummaryStatistics summaryStatistics();

    long[] toArray();

    /* renamed from: j$.util.stream.LongStream$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Builder builder() {
            return new Streams.LongStreamBuilderImpl();
        }

        public static LongStream empty() {
            return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
        }

        public static LongStream of(long t) {
            return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(t), false);
        }

        public static LongStream of(long... values) {
            return DesugarArrays.stream(values);
        }

        public static LongStream iterate(long seed, LongUnaryOperator f) {
            f.getClass();
            return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(new Object(seed, f) {
                long t;
                final /* synthetic */ LongUnaryOperator val$f;
                final /* synthetic */ long val$seed;

                public /* synthetic */ void forEachRemaining(Consumer consumer) {
                    PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, consumer);
                }

                public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                    PrimitiveIterator.OfLong.CC.$default$forEachRemaining((PrimitiveIterator.OfLong) this, longConsumer);
                }

                public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
                    forEachRemaining((LongConsumer) obj);
                }

                public /* synthetic */ void remove() {
                    Iterator.CC.$default$remove(this);
                }

                {
                    this.val$seed = r1;
                    this.val$f = r3;
                    this.t = r1;
                }

                public boolean hasNext() {
                    return true;
                }

                public long nextLong() {
                    long v = this.t;
                    this.t = this.val$f.applyAsLong(this.t);
                    return v;
                }
            }, 1296), false);
        }

        public static LongStream generate(LongSupplier s) {
            s.getClass();
            return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, s), false);
        }

        public static LongStream range(long startInclusive, long endExclusive) {
            if (startInclusive >= endExclusive) {
                return empty();
            }
            if (endExclusive - startInclusive >= 0) {
                return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endExclusive, false), false);
            }
            long m = LongStream$$ExternalSyntheticBackport0.m(endExclusive - startInclusive, 2) + startInclusive + 1;
            return concat(range(startInclusive, m), range(m, endExclusive));
        }

        public static LongStream rangeClosed(long startInclusive, long endInclusive) {
            if (startInclusive > endInclusive) {
                return empty();
            }
            if ((endInclusive - startInclusive) + 1 > 0) {
                return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endInclusive, true), false);
            }
            long m = LongStream$$ExternalSyntheticBackport0.m(endInclusive - startInclusive, 2) + startInclusive + 1;
            return concat(range(startInclusive, m), rangeClosed(m, endInclusive));
        }

        public static LongStream concat(LongStream a, LongStream b) {
            a.getClass();
            b.getClass();
            return (LongStream) StreamSupport.longStream(new Streams.ConcatSpliterator.OfLong(a.spliterator(), b.spliterator()), a.isParallel() || b.isParallel()).onClose(Streams.composedClose(a, b));
        }
    }

    public interface Builder extends LongConsumer {
        void accept(long j);

        Builder add(long j);

        LongStream build();

        /* renamed from: j$.util.stream.LongStream$Builder$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static Builder $default$add(Builder _this, long t) {
                _this.accept(t);
                return _this;
            }
        }
    }
}
