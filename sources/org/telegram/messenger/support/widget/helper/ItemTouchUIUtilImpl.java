package org.telegram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.view.View;
import org.telegram.messenger.support.widget.RecyclerView;

class ItemTouchUIUtilImpl {

    static class BaseImpl implements ItemTouchUIUtil {
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
        }

        public void onSelected(View view) {
        }

        BaseImpl() {
        }

        public void clearView(View view) {
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }
    }

    static class Api21Impl extends BaseImpl {
        Api21Impl() {
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            if (z && view.getTag() == null) {
                Float valueOf = Float.valueOf(ViewCompat.getElevation(view));
                ViewCompat.setElevation(view, 1.0f + findMaxElevation(recyclerView, view));
                view.setTag(valueOf);
            }
            super.onDraw(canvas, recyclerView, view, f, f2, i, z);
        }

        private float findMaxElevation(RecyclerView recyclerView, View view) {
            int childCount = recyclerView.getChildCount();
            float f = 0.0f;
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                if (childAt != view) {
                    float elevation = ViewCompat.getElevation(childAt);
                    if (elevation > f) {
                        f = elevation;
                    }
                }
            }
            return f;
        }

        public void clearView(View view) {
            Object tag = view.getTag();
            if (tag != null && (tag instanceof Float)) {
                ViewCompat.setElevation(view, ((Float) tag).floatValue());
            }
            view.setTag(null);
            super.clearView(view);
        }
    }

    ItemTouchUIUtilImpl() {
    }
}
