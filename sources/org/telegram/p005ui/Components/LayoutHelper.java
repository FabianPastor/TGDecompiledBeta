package org.telegram.p005ui.Components;

import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.LayoutHelper */
public class LayoutHelper {
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    private static int getSize(float size) {
        if (size >= 0.0f) {
            size = (float) AndroidUtilities.m9dp(size);
        }
        return (int) size;
    }

    public static LayoutParams createScroll(int width, int height, int gravity) {
        return new LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), gravity);
    }

    public static LayoutParams createScroll(int width, int height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LayoutParams layoutParams = new LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), gravity);
        layoutParams.leftMargin = AndroidUtilities.m9dp(leftMargin);
        layoutParams.topMargin = AndroidUtilities.m9dp(topMargin);
        layoutParams.rightMargin = AndroidUtilities.m9dp(rightMargin);
        layoutParams.bottomMargin = AndroidUtilities.m9dp(bottomMargin);
        return layoutParams;
    }

    public static LayoutParams createFrame(int width, float height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LayoutParams layoutParams = new LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize(height), gravity);
        layoutParams.setMargins(AndroidUtilities.m9dp(leftMargin), AndroidUtilities.m9dp(topMargin), AndroidUtilities.m9dp(rightMargin), AndroidUtilities.m9dp(bottomMargin));
        return layoutParams;
    }

    public static LayoutParams createFrame(int width, int height, int gravity) {
        return new LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), gravity);
    }

    public static LayoutParams createFrame(int width, float height) {
        return new LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize(height));
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent, int alignRelative, int anchorRelative) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutHelper.getSize(width), LayoutHelper.getSize(height));
        if (alignParent >= 0) {
            layoutParams.addRule(alignParent);
        }
        if (alignRelative >= 0 && anchorRelative >= 0) {
            layoutParams.addRule(alignRelative, anchorRelative);
        }
        layoutParams.leftMargin = AndroidUtilities.m9dp((float) leftMargin);
        layoutParams.topMargin = AndroidUtilities.m9dp((float) topMargin);
        layoutParams.rightMargin = AndroidUtilities.m9dp((float) rightMargin);
        layoutParams.bottomMargin = AndroidUtilities.m9dp((float) bottomMargin);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return LayoutHelper.createRelative((float) width, (float) height, leftMargin, topMargin, rightMargin, bottomMargin, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent) {
        return LayoutHelper.createRelative((float) width, (float) height, leftMargin, topMargin, rightMargin, bottomMargin, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignRelative, int anchorRelative) {
        return LayoutHelper.createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent, int alignRelative, int anchorRelative) {
        return LayoutHelper.createRelative((float) width, (float) height, 0, 0, 0, 0, alignParent, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height) {
        return LayoutHelper.createRelative((float) width, (float) height, 0, 0, 0, 0, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent) {
        return LayoutHelper.createRelative((float) width, (float) height, 0, 0, 0, 0, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignRelative, int anchorRelative) {
        return LayoutHelper.createRelative((float) width, (float) height, 0, 0, 0, 0, -1, alignRelative, anchorRelative);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), weight);
        layoutParams.setMargins(AndroidUtilities.m9dp((float) leftMargin), AndroidUtilities.m9dp((float) topMargin), AndroidUtilities.m9dp((float) rightMargin), AndroidUtilities.m9dp((float) bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), weight);
        layoutParams.setMargins(AndroidUtilities.m9dp((float) leftMargin), AndroidUtilities.m9dp((float) topMargin), AndroidUtilities.m9dp((float) rightMargin), AndroidUtilities.m9dp((float) bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height));
        layoutParams.setMargins(AndroidUtilities.m9dp((float) leftMargin), AndroidUtilities.m9dp((float) topMargin), AndroidUtilities.m9dp((float) rightMargin), AndroidUtilities.m9dp((float) bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height));
        layoutParams.setMargins(AndroidUtilities.m9dp(leftMargin), AndroidUtilities.m9dp(topMargin), AndroidUtilities.m9dp(rightMargin), AndroidUtilities.m9dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), weight);
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height), weight);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height) {
        return new LinearLayout.LayoutParams(LayoutHelper.getSize((float) width), LayoutHelper.getSize((float) height));
    }
}
