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

    public HeaderCell(Context context, int padding) {
        this(context, "windowBackgroundWhiteBlueHeader", padding, 15, false, (Theme.ResourcesProvider) null);
    }

    public HeaderCell(Context context, String textColorKey, int padding, int topMargin, boolean text2) {
        this(context, textColorKey, padding, topMargin, text2, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HeaderCell(Context context, String textColorKey, int padding, int topMargin, boolean text2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        String str = textColorKey;
        int i = padding;
        int i2 = topMargin;
        this.height = 40;
        this.resourcesProvider = resourcesProvider2;
        TextView textView3 = new TextView(getContext());
        this.textView = textView3;
        textView3.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i3 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setMinHeight(AndroidUtilities.dp((float) (this.height - i2)));
        this.textView.setTextColor(getThemedColor(str));
        this.textView.setTag(str);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) i, (float) i2, (float) i, 0.0f));
        if (text2) {
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 3 : i3) | 48, (float) i, 21.0f, (float) i, 0.0f));
        }
        ViewCompat.setAccessibilityHeading(this, true);
    }

    public void setHeight(int value) {
        TextView textView3 = this.textView;
        this.height = value;
        textView3.setMinHeight(AndroidUtilities.dp((float) value) - ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        if (animators != null) {
            TextView textView3 = this.textView;
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr));
            return;
        }
        TextView textView4 = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView4.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTextSize(float dip) {
        this.textView.setTextSize(1, dip);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(CharSequence text) {
        this.textView.setText(text);
    }

    public void setText2(CharSequence text) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(text);
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo.CollectionItemInfo collection;
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= 19 && (collection = info.getCollectionItemInfo()) != null) {
            info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
