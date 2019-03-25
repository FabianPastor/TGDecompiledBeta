package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
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

    public HeaderCell(Context context, int padding) {
        this(context, false, padding, 15, false);
    }

    public HeaderCell(Context context, boolean dialog, int padding, int topMargin, boolean text2) {
        int i;
        int i2 = 3;
        super(context);
        this.height = 40;
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setMinHeight(AndroidUtilities.dp((float) (this.height - topMargin)));
        if (dialog) {
            this.textView.setTextColor(Theme.getColor("dialogTextBlue2"));
        } else {
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
        }
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, (float) padding, (float) topMargin, (float) padding, 0.0f));
        if (text2) {
            this.textView2 = new SimpleTextView(getContext());
            this.textView2.setTextSize(13);
            SimpleTextView simpleTextView = this.textView2;
            if (LocaleController.isRTL) {
                i = 3;
            } else {
                i = 5;
            }
            simpleTextView.setGravity(i | 48);
            SimpleTextView simpleTextView2 = this.textView2;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(simpleTextView2, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, (float) padding, 21.0f, (float) padding, 0.0f));
        }
    }

    public void setHeight(int value) {
        this.textView.setMinHeight(AndroidUtilities.dp((float) this.height) - ((LayoutParams) this.textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        TextView textView;
        if (animators != null) {
            textView = this.textView;
            String str = "alpha";
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView, str, fArr));
            return;
        }
        textView = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView.setAlpha(f);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setText2(String text) {
        if (this.textView2 != null) {
            this.textView2.setText(text);
        }
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (VERSION.SDK_INT >= 19) {
            CollectionItemInfo collection = info.getCollectionItemInfo();
            if (collection != null) {
                info.setCollectionItemInfo(CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
            }
        }
    }
}
