package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class HeaderCell extends FrameLayout {
    private int height;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private SimpleTextView textView2;

    public HeaderCell(Context context) {
        this(context, "windowBackgroundWhiteBlueHeader", 21, 15, false, (Theme.ResourcesProvider) null);
    }

    public HeaderCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, "windowBackgroundWhiteBlueHeader", 21, 15, false, resourcesProvider2);
    }

    public HeaderCell(Context context, int i) {
        this(context, "windowBackgroundWhiteBlueHeader", i, 15, false, (Theme.ResourcesProvider) null);
    }

    public HeaderCell(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        this(context, "windowBackgroundWhiteBlueHeader", i, 15, false, resourcesProvider2);
    }

    public HeaderCell(Context context, String str, int i, int i2, boolean z) {
        this(context, str, i, i2, z, (Theme.ResourcesProvider) null);
    }

    public HeaderCell(Context context, String str, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        this(context, str, i, i2, 0, z, resourcesProvider2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HeaderCell(Context context, String str, int i, int i2, int i3, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        String str2 = str;
        int i4 = i2;
        int i5 = i3;
        this.height = 40;
        this.resourcesProvider = resourcesProvider2;
        TextView textView3 = new TextView(getContext());
        this.textView = textView3;
        textView3.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i6 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setMinHeight(AndroidUtilities.dp((float) (this.height - i4)));
        this.textView.setTextColor(getThemedColor(str2));
        this.textView.setTag(str2);
        float f = (float) i;
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, (float) i4, f, z ? 0.0f : (float) i5));
        if (z) {
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 3 : i6) | 48, f, 21.0f, f, (float) i5));
        }
        ViewCompat.setAccessibilityHeading(this, true);
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
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!z) {
                f = 0.5f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(textView3, property, fArr));
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

    public void setTextSize(float f) {
        this.textView.setTextSize(1, f);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setText2(CharSequence charSequence) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(charSequence);
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        int i = Build.VERSION.SDK_INT;
        if (i >= 28) {
            accessibilityNodeInfo.setHeading(true);
        } else if (i >= 19 && (collectionItemInfo = accessibilityNodeInfo.getCollectionItemInfo()) != null) {
            accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), true));
        }
        accessibilityNodeInfo.setEnabled(true);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
