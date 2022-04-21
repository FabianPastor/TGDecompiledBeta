package j$.util;

import j$.util.Spliterator;
import j$.util.function.IntFunction;
import j$.util.function.IntToDoubleFunction;
import j$.util.function.IntToLongFunction;
import j$.util.function.IntUnaryOperator;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;
import j$.util.stream.StreamSupport;
import java.util.Arrays;

public class DesugarArrays {
    static final /* synthetic */ boolean $assertionsDisabled = true;

    private DesugarArrays() {
    }

    static boolean deepEquals0(Object e1, Object e2) {
        if (!$assertionsDisabled && e1 == null) {
            throw new AssertionError();
        } else if ((e1 instanceof Object[]) && (e2 instanceof Object[])) {
            return Arrays.deepEquals((Object[]) e1, (Object[]) e2);
        } else {
            if ((e1 instanceof byte[]) && (e2 instanceof byte[])) {
                return Arrays.equals((byte[]) e1, (byte[]) e2);
            }
            if ((e1 instanceof short[]) && (e2 instanceof short[])) {
                return Arrays.equals((short[]) e1, (short[]) e2);
            }
            if ((e1 instanceof int[]) && (e2 instanceof int[])) {
                return Arrays.equals((int[]) e1, (int[]) e2);
            }
            if ((e1 instanceof long[]) && (e2 instanceof long[])) {
                return Arrays.equals((long[]) e1, (long[]) e2);
            }
            if ((e1 instanceof char[]) && (e2 instanceof char[])) {
                return Arrays.equals((char[]) e1, (char[]) e2);
            }
            if ((e1 instanceof float[]) && (e2 instanceof float[])) {
                return Arrays.equals((float[]) e1, (float[]) e2);
            }
            if ((e1 instanceof double[]) && (e2 instanceof double[])) {
                return Arrays.equals((double[]) e1, (double[]) e2);
            }
            if (!(e1 instanceof boolean[]) || !(e2 instanceof boolean[])) {
                return e1.equals(e2);
            }
            return Arrays.equals((boolean[]) e1, (boolean[]) e2);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [j$.util.function.IntFunction<? extends T>, j$.util.function.IntFunction, java.lang.Object] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> void setAll(T[] r2, j$.util.function.IntFunction<? extends T> r3) {
        /*
            r3.getClass()
            r0 = 0
        L_0x0004:
            int r1 = r2.length
            if (r0 >= r1) goto L_0x0010
            java.lang.Object r1 = r3.apply(r0)
            r2[r0] = r1
            int r0 = r0 + 1
            goto L_0x0004
        L_0x0010:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarArrays.setAll(java.lang.Object[], j$.util.function.IntFunction):void");
    }

    public static <T> void parallelSetAll(T[] array, IntFunction<? extends T> intFunction) {
        intFunction.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new DesugarArrays$$ExternalSyntheticLambda3(array, intFunction));
    }

    static /* synthetic */ void lambda$parallelSetAll$0(Object[] array, IntFunction generator, int i) {
        array[i] = generator.apply(i);
    }

    public static void setAll(int[] array, IntUnaryOperator generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsInt(i);
        }
    }

    public static void parallelSetAll(int[] array, IntUnaryOperator generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new DesugarArrays$$ExternalSyntheticLambda1(array, generator));
    }

    static /* synthetic */ void lambda$parallelSetAll$1(int[] array, IntUnaryOperator generator, int i) {
        array[i] = generator.applyAsInt(i);
    }

    public static void setAll(long[] array, IntToLongFunction generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsLong(i);
        }
    }

    public static void parallelSetAll(long[] array, IntToLongFunction generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new DesugarArrays$$ExternalSyntheticLambda2(array, generator));
    }

    static /* synthetic */ void lambda$parallelSetAll$2(long[] array, IntToLongFunction generator, int i) {
        array[i] = generator.applyAsLong(i);
    }

    public static void setAll(double[] array, IntToDoubleFunction generator) {
        generator.getClass();
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.applyAsDouble(i);
        }
    }

    public static void parallelSetAll(double[] array, IntToDoubleFunction generator) {
        generator.getClass();
        IntStream.CC.range(0, array.length).parallel().forEach(new DesugarArrays$$ExternalSyntheticLambda0(array, generator));
    }

    static /* synthetic */ void lambda$parallelSetAll$3(double[] array, IntToDoubleFunction generator, int i) {
        array[i] = generator.applyAsDouble(i);
    }

    public static <T> Spliterator<T> spliterator(T[] array) {
        return Spliterators.spliterator((Object[]) array, 1040);
    }

    public static <T> Spliterator<T> spliterator(T[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator((Object[]) array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfInt spliterator(int[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfInt spliterator(int[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfLong spliterator(long[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfLong spliterator(long[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static Spliterator.OfDouble spliterator(double[] array) {
        return Spliterators.spliterator(array, 1040);
    }

    public static Spliterator.OfDouble spliterator(double[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, 1040);
    }

    public static <T> Stream<T> stream(T[] array) {
        return stream(array, 0, array.length);
    }

    public static <T> Stream<T> stream(T[] array, int startInclusive, int endExclusive) {
        return StreamSupport.stream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static IntStream stream(int[] array) {
        return stream(array, 0, array.length);
    }

    public static IntStream stream(int[] array, int startInclusive, int endExclusive) {
        return StreamSupport.intStream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static LongStream stream(long[] array) {
        return stream(array, 0, array.length);
    }

    public static LongStream stream(long[] array, int startInclusive, int endExclusive) {
        return StreamSupport.longStream(spliterator(array, startInclusive, endExclusive), false);
    }

    public static DoubleStream stream(double[] array) {
        return stream(array, 0, array.length);
    }

    public static DoubleStream stream(double[] array, int startInclusive, int endExclusive) {
        return StreamSupport.doubleStream(spliterator(array, startInclusive, endExclusive), false);
    }
}
