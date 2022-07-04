package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailCell extends FrameLayout {
    private boolean contentDescriptionValueFirst;
    private final ImageView imageView;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private final TextView showMoreTextView;
    private final TextView textView;
    private final TextView valueTextView;

    public TextDetailCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public TextDetailCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.showMoreTextView = null;
        this.resourcesProvider = resourcesProvider2;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider2));
        textView2.setTextSize(1, 16.0f);
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setImportantForAccessibility(2);
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 23.0f, 8.0f, 23.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider2));
        textView3.setTextSize(1, 13.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
        textView3.setImportantForAccessibility(2);
        addView(textView3, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 23.0f, 33.0f, 23.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setImportantForAccessibility(2);
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        addView(imageView2, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388629, 0.0f, 0.0f, 12.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setImage(Drawable drawable) {
        setImage(drawable, (CharSequence) null);
    }

    public void setImage(Drawable drawable, CharSequence imageContentDescription) {
        this.imageView.setImageDrawable(drawable);
        int i = 0;
        this.imageView.setFocusable(drawable != null);
        this.imageView.setContentDescription(imageContentDescription);
        if (drawable == null) {
            this.imageView.setBackground((Drawable) null);
            this.imageView.setImportantForAccessibility(2);
        } else {
            this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(48.0f), 0, Theme.getColor("listSelectorSDK21", this.resourcesProvider)));
            this.imageView.setImportantForAccessibility(1);
        }
        int dp = AndroidUtilities.dp(23.0f);
        if (drawable != null) {
            i = AndroidUtilities.dp(48.0f);
        }
        int margin = dp + i;
        if (LocaleController.isRTL) {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).leftMargin = margin;
        } else {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).rightMargin = margin;
        }
        this.textView.requestLayout();
    }

    public void setImageClickListener(View.OnClickListener clickListener) {
        this.imageView.setOnClickListener(clickListener);
    }

    public void setTextWithEmojiAndValue(CharSequence text, CharSequence value, boolean divider) {
        TextView textView2 = this.textView;
        textView2.setText(Emoji.replaceEmoji(text, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setContentDescriptionValueFirst(boolean contentDescriptionValueFirst2) {
        this.contentDescriptionValueFirst = contentDescriptionValueFirst2;
    }

    public void invalidate() {
        super.invalidate();
        this.textView.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CharSequence text = this.textView.getText();
        CharSequence valueText = this.valueTextView.getText();
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(valueText)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.contentDescriptionValueFirst ? valueText : text);
            sb.append(": ");
            sb.append(this.contentDescriptionValueFirst ? text : valueText);
            info.setText(sb.toString());
        }
    }
}
