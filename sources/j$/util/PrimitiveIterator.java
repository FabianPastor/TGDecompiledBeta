package j$.util;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import java.util.Iterator;

public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {
    void forEachRemaining(T_CONS t_cons);

    public interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
        void forEachRemaining(Consumer<? super Integer> consumer);

        void forEachRemaining(IntConsumer intConsumer);

        Integer next();

        int nextInt();

        /* renamed from: j$.util.PrimitiveIterator$OfInt$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfInt _this, IntConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextInt());
                }
            }

            public static Integer $default$next(OfInt _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
                }
                return Integer.valueOf(_this.nextInt());
            }

            public static void $default$forEachRemaining(OfInt _this, Consumer consumer) {
                if (consumer instanceof IntConsumer) {
                    _this.forEachRemaining((IntConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((IntConsumer) new PrimitiveIterator$OfInt$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    public interface OfLong extends PrimitiveIterator<Long, LongConsumer> {
        void forEachRemaining(Consumer<? super Long> consumer);

        void forEachRemaining(LongConsumer longConsumer);

        Long next();

        long nextLong();

        /* renamed from: j$.util.PrimitiveIterator$OfLong$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfLong _this, LongConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextLong());
                }
            }

            public static Long $default$next(OfLong _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
                }
                return Long.valueOf(_this.nextLong());
            }

            public static void $default$forEachRemaining(OfLong _this, Consumer consumer) {
                if (consumer instanceof LongConsumer) {
                    _this.forEachRemaining((LongConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((LongConsumer) new PrimitiveIterator$OfLong$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    public interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {
        void forEachRemaining(Consumer<? super Double> consumer);

        void forEachRemaining(DoubleConsumer doubleConsumer);

        Double next();

        double nextDouble();

        /* renamed from: j$.util.PrimitiveIterator$OfDouble$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfDouble _this, DoubleConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextDouble());
                }
            }

            public static Double $default$next(OfDouble _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
                }
                return Double.valueOf(_this.nextDouble());
            }

            public static void $default$forEachRemaining(OfDouble _this, Consumer consumer) {
                if (consumer instanceof DoubleConsumer) {
                    _this.forEachRemaining((DoubleConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((DoubleConsumer) new PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0(consumer));
            }
        }
    }
}
