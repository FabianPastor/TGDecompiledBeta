package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class HeaderCell extends FrameLayout {
    private int height;
    private TextView textView;
    private SimpleTextView textView2;

    public HeaderCell(Context context) {
        this(context, false, 21, 15, false);
    }

    public HeaderCell(Context context, int i) {
        this(context, false, i, 15, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HeaderCell(Context context, boolean z, int i, int i2, boolean z2) {
        super(context);
        int i3 = i2;
        this.height = 40;
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i4 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setMinHeight(AndroidUtilities.dp((float) (this.height - i3)));
        if (z) {
            this.textView.setTextColor(Theme.getColor("dialogTextBlue2"));
        } else {
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
        }
        float f = (float) i;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, (float) i3, f, 0.0f));
        if (z2) {
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 3 : i4) | 48, f, 21.0f, f, 0.0f));
        }
    }

    public void setHeight(int i) {
        TextView textView3 = this.textView;
        this.height = i;
        textView3.setMinHeight(AndroidUtilities.dp((float) i) - ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean z, ArrayList<Animator> arrayList) {
        float f = 1.0f;
        if (arrayList != null) {
            TextView textView3 = this.textView;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr));
            return;
        }
        TextView textView4 = this.textView;
        if (!z) {
            f = 0.5f;
        }
        textView4.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setText2(String str) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(str);
        }
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (Build.VERSION.SDK_INT >= 19 && (collectionItemInfo = accessibilityNodeInfo.getCollectionItemInfo()) != null) {
            accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), true));
        }
    }
}
