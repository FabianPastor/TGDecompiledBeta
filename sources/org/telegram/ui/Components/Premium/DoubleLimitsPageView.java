package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class DoubleLimitsPageView extends FrameLayout implements PagerHeaderView {
    DoubledLimitsBottomSheet$Adapter adapter;
    final LinearLayoutManager layoutManager;
    final RecyclerListView recyclerListView;

    public DoubleLimitsPageView(Context context) {
        super(context);
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.recyclerListView = recyclerListView2;
        DoubledLimitsBottomSheet$Adapter doubledLimitsBottomSheet$Adapter = new DoubledLimitsBottomSheet$Adapter(UserConfig.selectedAccount, true);
        this.adapter = doubledLimitsBottomSheet$Adapter;
        recyclerListView2.setAdapter(doubledLimitsBottomSheet$Adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        recyclerListView2.setClipToPadding(false);
        this.adapter.containerView = this;
        addView(recyclerListView2, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.adapter.measureGradient(getContext(), getMeasuredWidth(), getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
    }

    public void setOffset(float f) {
        if (Math.abs(f / ((float) getMeasuredWidth())) == 1.0f) {
            this.recyclerListView.scrollToPosition(0);
        }
    }

    public void setTopOffset(int i) {
        this.recyclerListView.setPadding(0, i, 0, 0);
    }
}
