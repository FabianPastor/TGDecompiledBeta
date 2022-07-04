package j$.util.function;

import java.util.Comparator;

public interface BinaryOperator<T> extends BiFunction<T, T, T> {

    /* renamed from: j$.util.function.BinaryOperator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) {
            comparator.getClass();
            return new BinaryOperator$$ExternalSyntheticLambda1(comparator);
        }

        public static /* synthetic */ Object lambda$minBy$0(Comparator comparator, Object a, Object b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }

        public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {
            comparator.getClass();
            return new BinaryOperator$$ExternalSyntheticLambda0(comparator);
        }

        public static /* synthetic */ Object lambda$maxBy$1(Comparator comparator, Object a, Object b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }
}
