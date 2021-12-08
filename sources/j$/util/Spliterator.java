package j$.util;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import java.util.Comparator;

public interface Spliterator<T> {
    public static final int CONCURRENT = 4096;
    public static final int DISTINCT = 1;
    public static final int IMMUTABLE = 1024;
    public static final int NONNULL = 256;
    public static final int ORDERED = 16;
    public static final int SIZED = 64;
    public static final int SORTED = 4;
    public static final int SUBSIZED = 16384;

    int characteristics();

    long estimateSize();

    void forEachRemaining(Consumer<? super T> consumer);

    Comparator<? super T> getComparator();

    long getExactSizeIfKnown();

    boolean hasCharacteristics(int i);

    boolean tryAdvance(Consumer<? super T> consumer);

    Spliterator<T> trySplit();

    /* renamed from: j$.util.Spliterator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$forEachRemaining(Spliterator _this, Consumer consumer) {
            do {
            } while (_this.tryAdvance(consumer));
        }

        public static long $default$getExactSizeIfKnown(Spliterator _this) {
            if ((_this.characteristics() & 64) == 0) {
                return -1;
            }
            return _this.estimateSize();
        }

        public static boolean $default$hasCharacteristics(Spliterator _this, int characteristics) {
            return (_this.characteristics() & characteristics) == characteristics;
        }

        public static Comparator $default$getComparator(Spliterator _this) {
            throw new IllegalStateException();
        }
    }

    public interface OfPrimitive<T, T_CONS, T_SPLITR extends OfPrimitive<T, T_CONS, T_SPLITR>> extends Spliterator<T> {
        void forEachRemaining(T_CONS t_cons);

        boolean tryAdvance(T_CONS t_cons);

        T_SPLITR trySplit();

        /* renamed from: j$.util.Spliterator$OfPrimitive$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfPrimitive _this, Object action) {
                do {
                } while (_this.tryAdvance(action));
            }
        }
    }

    public interface OfInt extends OfPrimitive<Integer, IntConsumer, OfInt> {
        void forEachRemaining(Consumer<? super Integer> consumer);

        void forEachRemaining(IntConsumer intConsumer);

        boolean tryAdvance(Consumer<? super Integer> consumer);

        boolean tryAdvance(IntConsumer intConsumer);

        OfInt trySplit();

        /* renamed from: j$.util.Spliterator$OfInt$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfInt _this, IntConsumer action) {
                do {
                } while (_this.tryAdvance(action));
            }

            public static boolean $default$tryAdvance(OfInt _this, Consumer consumer) {
                if (consumer instanceof IntConsumer) {
                    return _this.tryAdvance((IntConsumer) consumer);
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
                }
                consumer.getClass();
                return _this.tryAdvance((IntConsumer) new PrimitiveIterator$OfInt$$ExternalSyntheticLambda0(consumer));
            }

            public static void $default$forEachRemaining(OfInt _this, Consumer consumer) {
                if (consumer instanceof IntConsumer) {
                    _this.forEachRemaining((IntConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((IntConsumer) new PrimitiveIterator$OfInt$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    public interface OfLong extends OfPrimitive<Long, LongConsumer, OfLong> {
        void forEachRemaining(Consumer<? super Long> consumer);

        void forEachRemaining(LongConsumer longConsumer);

        boolean tryAdvance(Consumer<? super Long> consumer);

        boolean tryAdvance(LongConsumer longConsumer);

        OfLong trySplit();

        /* renamed from: j$.util.Spliterator$OfLong$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfLong _this, LongConsumer action) {
                do {
                } while (_this.tryAdvance(action));
            }

            public static boolean $default$tryAdvance(OfLong _this, Consumer consumer) {
                if (consumer instanceof LongConsumer) {
                    return _this.tryAdvance((LongConsumer) consumer);
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
                }
                consumer.getClass();
                return _this.tryAdvance((LongConsumer) new PrimitiveIterator$OfLong$$ExternalSyntheticLambda0(consumer));
            }

            public static void $default$forEachRemaining(OfLong _this, Consumer consumer) {
                if (consumer instanceof LongConsumer) {
                    _this.forEachRemaining((LongConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((LongConsumer) new PrimitiveIterator$OfLong$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    public interface OfDouble extends OfPrimitive<Double, DoubleConsumer, OfDouble> {
        void forEachRemaining(Consumer<? super Double> consumer);

        void forEachRemaining(DoubleConsumer doubleConsumer);

        boolean tryAdvance(Consumer<? super Double> consumer);

        boolean tryAdvance(DoubleConsumer doubleConsumer);

        OfDouble trySplit();

        /* renamed from: j$.util.Spliterator$OfDouble$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfDouble _this, DoubleConsumer action) {
                do {
                } while (_this.tryAdvance(action));
            }

            public static boolean $default$tryAdvance(OfDouble _this, Consumer consumer) {
                if (consumer instanceof DoubleConsumer) {
                    return _this.tryAdvance((DoubleConsumer) consumer);
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
                }
                consumer.getClass();
                return _this.tryAdvance((DoubleConsumer) new PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0(consumer));
            }

            public static void $default$forEachRemaining(OfDouble _this, Consumer consumer) {
                if (consumer instanceof DoubleConsumer) {
                    _this.forEachRemaining((DoubleConsumer) consumer);
                    return;
                }
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((DoubleConsumer) new PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0(consumer));
            }
        }
    }
}
