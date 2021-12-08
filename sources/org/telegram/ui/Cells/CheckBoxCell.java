package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    public static final int TYPE_CHECK_BOX_ROUND = 4;
    private View checkBox;
    private CheckBox2 checkBoxRound;
    private int checkBoxSize;
    private CheckBoxSquare checkBoxSquare;
    private int currentType;
    private boolean isMultiline;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int type) {
        this(context, type, 17, (Theme.ResourcesProvider) null);
    }

    public CheckBoxCell(Context context, int type, Theme.ResourcesProvider resourcesProvider2) {
        this(context, type, 17, resourcesProvider2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CheckBoxCell(Context context, int type, int padding, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        String str;
        String str2;
        Context context2 = context;
        int i = type;
        int i2 = padding;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.checkBoxSize = 18;
        this.resourcesProvider = resourcesProvider3;
        this.currentType = i;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        int i3 = 5;
        boolean z = true;
        textView2.setTextColor(getThemedColor((i == 1 || i == 5) ? "dialogTextBlack" : "windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(getThemedColor((i == 1 || i == 5) ? "dialogTextLink" : "windowBackgroundWhiteLinkText"));
        this.textView.setTag(Integer.valueOf(getThemedColor((i == 1 || i == 5) ? "dialogTextBlack" : str2)));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (i == 3) {
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 29.0f, 0.0f, 0.0f, 0.0f));
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        } else {
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            if (i == 2) {
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 29), 0.0f, (float) (LocaleController.isRTL ? 29 : 0), 0.0f));
            } else {
                int offset = i == 4 ? 56 : 46;
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? i2 : (i2 - 17) + offset), 0.0f, (float) (LocaleController.isRTL ? offset + (i2 - 17) : i2), 0.0f));
            }
        }
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(getThemedColor((i == 1 || i == 5) ? "dialogTextBlue" : "windowBackgroundWhiteValueText"));
        this.valueTextView.setTag((i == 1 || i == 5) ? "dialogTextBlue" : str);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, (float) i2, 0.0f, (float) i2, 0.0f));
        if (i == 4) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21, resourcesProvider3);
            this.checkBoxRound = checkBox2;
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(true);
            int i4 = 0;
            this.checkBoxRound.setChecked(true, false);
            this.checkBoxRound.setDrawBackgroundAsArc(10);
            this.checkBoxSize = 21;
            addView(this.checkBox, LayoutHelper.createFrame(21, (float) 21, (!LocaleController.isRTL ? 3 : i3) | 48, (float) (LocaleController.isRTL ? 0 : i2), 16.0f, (float) (LocaleController.isRTL ? i2 : i4), 0.0f));
            return;
        }
        int i5 = 0;
        if (!(i == 1 || i == 5)) {
            z = false;
        }
        CheckBoxSquare checkBoxSquare2 = new CheckBoxSquare(context2, z, resourcesProvider3);
        this.checkBoxSquare = checkBoxSquare2;
        this.checkBox = checkBoxSquare2;
        this.checkBoxSize = 18;
        if (i == 5) {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, (!LocaleController.isRTL ? 3 : i3) | 16, (float) (LocaleController.isRTL ? 0 : i2), 0.0f, (float) (LocaleController.isRTL ? i2 : i5), 0.0f));
        } else if (i == 3) {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, 51, 0.0f, 15.0f, 0.0f, 0.0f));
        } else if (i == 2) {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, (!LocaleController.isRTL ? 3 : i3) | 48, 0.0f, 15.0f, 0.0f, 0.0f));
        } else {
            addView(checkBoxSquare2, LayoutHelper.createFrame(18, (float) 18, (!LocaleController.isRTL ? 3 : i3) | 48, (float) (LocaleController.isRTL ? 0 : i2), 16.0f, (float) (LocaleController.isRTL ? i2 : i5), 0.0f));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentType == 3) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(34.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), NUM));
            setMeasuredDimension(this.textView.getMeasuredWidth() + AndroidUtilities.dp(29.0f), AndroidUtilities.dp(50.0f));
        } else if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
            int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.checkBoxSize), NUM));
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(CharSequence text, String value, boolean checked, boolean divider) {
        this.textView.setText(text);
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, false);
        } else {
            this.checkBoxSquare.setChecked(checked, false);
        }
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setNeedDivider(boolean needDivider2) {
        this.needDivider = needDivider2;
    }

    public void setMultiline(boolean value) {
        this.isMultiline = value;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.textView.setLines(0);
            this.textView.setMaxLines(0);
            this.textView.setSingleLine(false);
            this.textView.setEllipsize((TextUtils.TruncateAt) null);
            if (this.currentType != 5) {
                this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(5.0f));
                layoutParams.height = -2;
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
                layoutParams1.topMargin = AndroidUtilities.dp(12.0f);
            }
        } else {
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, 0, 0, 0);
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            layoutParams1.topMargin = AndroidUtilities.dp(15.0f);
        }
        this.textView.setLayoutParams(layoutParams);
        this.checkBox.setLayoutParams(layoutParams1);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        float f = 1.0f;
        this.textView.setAlpha(enabled ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(enabled ? 1.0f : 0.5f);
        View view = this.checkBox;
        if (!enabled) {
            f = 0.5f;
        }
        view.setAlpha(f);
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        } else {
            this.checkBoxSquare.setChecked(checked, animated);
        }
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return this.checkBoxSquare.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public View getCheckBoxView() {
        return this.checkBox;
    }

    public void setCheckBoxColor(String background, String background1, String check) {
        CheckBox2 checkBox2 = this.checkBoxRound;
        if (checkBox2 != null) {
            checkBox2.setColor(background, background, check);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            int offset = this.currentType == 4 ? 50 : 20;
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp((float) offset), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp((float) offset) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.CheckBox");
        info.setCheckable(true);
        info.setChecked(isChecked());
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
