package org.telegram.ui.Components;

import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class LayoutHelper {
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    private static int getSize(float size) {
        return (int) (size < 0.0f ? size : (float) AndroidUtilities.dp(size));
    }

    private static int getAbsoluteGravity(int gravity) {
        return Gravity.getAbsoluteGravity(gravity, LocaleController.isRTL ? 1 : 0);
    }

    public static int getAbsoluteGravityStart() {
        return LocaleController.isRTL ? 5 : 3;
    }

    public static int getAbsoluteGravityEnd() {
        return LocaleController.isRTL ? 3 : 5;
    }

    public static FrameLayout.LayoutParams createScroll(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize((float) width), getSize((float) height), gravity);
    }

    public static FrameLayout.LayoutParams createScroll(int width, int height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize((float) width), getSize((float) height), gravity);
        layoutParams.leftMargin = AndroidUtilities.dp(leftMargin);
        layoutParams.topMargin = AndroidUtilities.dp(topMargin);
        layoutParams.rightMargin = AndroidUtilities.dp(rightMargin);
        layoutParams.bottomMargin = AndroidUtilities.dp(bottomMargin);
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize((float) width), getSize(height), gravity);
        layoutParams.setMargins(AndroidUtilities.dp(leftMargin), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(rightMargin), AndroidUtilities.dp(bottomMargin));
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrame(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize((float) width), getSize((float) height), gravity);
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height) {
        return new FrameLayout.LayoutParams(getSize((float) width), getSize(height));
    }

    public static FrameLayout.LayoutParams createFrame(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? endMargin : startMargin);
        layoutParams.topMargin = AndroidUtilities.dp(topMargin);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = AndroidUtilities.dp(bottomMargin);
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent, int alignRelative, int anchorRelative) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getSize(width), getSize(height));
        if (alignParent >= 0) {
            layoutParams.addRule(alignParent);
        }
        if (alignRelative >= 0 && anchorRelative >= 0) {
            layoutParams.addRule(alignRelative, anchorRelative);
        }
        layoutParams.leftMargin = AndroidUtilities.dp((float) leftMargin);
        layoutParams.topMargin = AndroidUtilities.dp((float) topMargin);
        layoutParams.rightMargin = AndroidUtilities.dp((float) rightMargin);
        layoutParams.bottomMargin = AndroidUtilities.dp((float) bottomMargin);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return createRelative((float) width, (float) height, leftMargin, topMargin, rightMargin, bottomMargin, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent) {
        return createRelative((float) width, (float) height, leftMargin, topMargin, rightMargin, bottomMargin, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignRelative, int anchorRelative) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent, int alignRelative, int anchorRelative) {
        return createRelative((float) width, (float) height, 0, 0, 0, 0, alignParent, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height) {
        return createRelative((float) width, (float) height, 0, 0, 0, 0, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent) {
        return createRelative((float) width, (float) height, 0, 0, 0, 0, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignRelative, int anchorRelative) {
        return createRelative((float) width, (float) height, 0, 0, 0, 0, -1, alignRelative, anchorRelative);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height), weight);
        layoutParams.setMargins(AndroidUtilities.dp((float) leftMargin), AndroidUtilities.dp((float) topMargin), AndroidUtilities.dp((float) rightMargin), AndroidUtilities.dp((float) bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height), weight);
        layoutParams.setMargins(AndroidUtilities.dp((float) leftMargin), AndroidUtilities.dp((float) topMargin), AndroidUtilities.dp((float) rightMargin), AndroidUtilities.dp((float) bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height));
        layoutParams.setMargins(AndroidUtilities.dp((float) leftMargin), AndroidUtilities.dp((float) topMargin), AndroidUtilities.dp((float) rightMargin), AndroidUtilities.dp((float) bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height));
        layoutParams.setMargins(AndroidUtilities.dp(leftMargin), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(rightMargin), AndroidUtilities.dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height), weight);
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height), weight);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height) {
        return new LinearLayout.LayoutParams(getSize((float) width), getSize((float) height));
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), (float) getAbsoluteGravity(gravity));
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? endMargin : startMargin);
        layoutParams.topMargin = AndroidUtilities.dp(topMargin);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = AndroidUtilities.dp(bottomMargin);
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), (float) getAbsoluteGravity(gravity));
    }
}
