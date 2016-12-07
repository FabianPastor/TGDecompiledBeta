package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;

class AccessibilityNodeInfoCompatApi24 {
    AccessibilityNodeInfoCompatApi24() {
    }

    public static Object getActionSetProgress() {
        return AccessibilityAction.ACTION_SET_PROGRESS;
    }

    public static int getDrawingOrder(Object info) {
        return ((AccessibilityNodeInfo) info).getDrawingOrder();
    }

    public static void setDrawingOrder(Object info, int drawingOrderInParent) {
        ((AccessibilityNodeInfo) info).setDrawingOrder(drawingOrderInParent);
    }

    public static boolean isImportantForAccessibility(Object info) {
        return ((AccessibilityNodeInfo) info).isImportantForAccessibility();
    }

    public static void setImportantForAccessibility(Object info, boolean importantForAccessibility) {
        ((AccessibilityNodeInfo) info).setImportantForAccessibility(importantForAccessibility);
    }
}
