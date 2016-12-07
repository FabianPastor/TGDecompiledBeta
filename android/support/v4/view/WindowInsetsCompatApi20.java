package android.support.v4.view;

import android.view.WindowInsets;

class WindowInsetsCompatApi20 {
    WindowInsetsCompatApi20() {
    }

    public static Object consumeSystemWindowInsets(Object insets) {
        return ((WindowInsets) insets).consumeSystemWindowInsets();
    }

    public static int getSystemWindowInsetBottom(Object insets) {
        return ((WindowInsets) insets).getSystemWindowInsetBottom();
    }

    public static int getSystemWindowInsetLeft(Object insets) {
        return ((WindowInsets) insets).getSystemWindowInsetLeft();
    }

    public static int getSystemWindowInsetRight(Object insets) {
        return ((WindowInsets) insets).getSystemWindowInsetRight();
    }

    public static int getSystemWindowInsetTop(Object insets) {
        return ((WindowInsets) insets).getSystemWindowInsetTop();
    }

    public static boolean hasInsets(Object insets) {
        return ((WindowInsets) insets).hasInsets();
    }

    public static boolean hasSystemWindowInsets(Object insets) {
        return ((WindowInsets) insets).hasSystemWindowInsets();
    }

    public static boolean isRound(Object insets) {
        return ((WindowInsets) insets).isRound();
    }

    public static Object replaceSystemWindowInsets(Object insets, int left, int top, int right, int bottom) {
        return ((WindowInsets) insets).replaceSystemWindowInsets(left, top, right, bottom);
    }

    public static Object getSourceWindowInsets(Object src) {
        return new WindowInsets((WindowInsets) src);
    }
}
