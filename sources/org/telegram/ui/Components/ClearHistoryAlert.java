package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
/* loaded from: classes3.dex */
public class ClearHistoryAlert extends BottomSheet {
    private boolean autoDeleteOnly;
    private CheckBoxCell cell;
    private int currentTimer;
    private ClearHistoryAlertDelegate delegate;
    private boolean dismissedDelayed;
    private LinearLayout linearLayout;
    private int[] location;
    private int newTimer;
    private int scrollOffsetY;
    private BottomSheetCell setTimerButton;
    private Drawable shadowDrawable;

    /* loaded from: classes3.dex */
    public interface ClearHistoryAlertDelegate {

        /* renamed from: org.telegram.ui.Components.ClearHistoryAlert$ClearHistoryAlertDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onClearHistory(ClearHistoryAlertDelegate clearHistoryAlertDelegate, boolean z) {
            }
        }

        void onAutoDeleteHistory(int i, int i2);

        void onClearHistory(boolean z);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    /* loaded from: classes3.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:132:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x045d  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x046b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ClearHistoryAlert(android.content.Context r28, org.telegram.tgnet.TLRPC$User r29, org.telegram.tgnet.TLRPC$Chat r30, boolean r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        /*
            Method dump skipped, instructions count: 1188
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ClearHistoryAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public static /* synthetic */ void lambda$new$0(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    public /* synthetic */ void lambda$new$1(View view) {
        if (this.dismissedDelayed) {
            return;
        }
        ClearHistoryAlertDelegate clearHistoryAlertDelegate = this.delegate;
        CheckBoxCell checkBoxCell = this.cell;
        clearHistoryAlertDelegate.onClearHistory(checkBoxCell != null && checkBoxCell.isChecked());
        dismiss();
    }

    public /* synthetic */ void lambda$new$2(View view) {
        int i;
        if (this.dismissedDelayed) {
            return;
        }
        int i2 = this.newTimer;
        if (i2 != this.currentTimer) {
            this.dismissedDelayed = true;
            int i3 = 70;
            if (i2 == 3) {
                i = 2678400;
            } else if (i2 == 2) {
                i = 604800;
            } else if (i2 == 1) {
                i = 86400;
            } else {
                i = 0;
                i3 = 71;
            }
            this.delegate.onAutoDeleteHistory(i, i3);
        }
        if (this.dismissedDelayed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ClearHistoryAlert.this.dismiss();
                }
            }, 200L);
        } else {
            dismiss();
        }
    }

    public void updateTimerButton(boolean z) {
        if (this.currentTimer != this.newTimer || this.autoDeleteOnly) {
            this.setTimerButton.setVisibility(0);
            if (z) {
                this.setTimerButton.animate().alpha(1.0f).setDuration(180L).start();
            } else {
                this.setTimerButton.setAlpha(1.0f);
            }
        } else if (z) {
            this.setTimerButton.animate().alpha(0.0f).setDuration(180L).start();
        } else {
            this.setTimerButton.setVisibility(4);
            this.setTimerButton.setAlpha(0.0f);
        }
    }

    public void updateLayout() {
        this.linearLayout.getChildAt(0).getLocationInWindow(this.location);
        int max = Math.max(this.location[1] - AndroidUtilities.dp(this.autoDeleteOnly ? 6.0f : 19.0f), 0);
        if (this.scrollOffsetY != max) {
            this.scrollOffsetY = max;
            this.containerView.invalidate();
        }
    }

    public void setDelegate(ClearHistoryAlertDelegate clearHistoryAlertDelegate) {
        this.delegate = clearHistoryAlertDelegate;
    }
}
