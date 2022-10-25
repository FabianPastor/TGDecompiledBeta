package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
/* loaded from: classes3.dex */
public class TextDetailCell extends FrameLayout {
    private boolean contentDescriptionValueFirst;
    private final ImageView imageView;
    private boolean multiline;
    private boolean needDivider;
    private Theme.ResourcesProvider resourcesProvider;
    private final TextView textView;
    private final LinkSpanDrawable.LinksTextView valueTextView;

    public TextDetailCell(Context context) {
        this(context, null);
    }

    public TextDetailCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, resourcesProvider, false);
    }

    public TextDetailCell(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setImportantForAccessibility(2);
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 23.0f, 8.0f, 23.0f, 0.0f));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.valueTextView = linksTextView;
        linksTextView.setOnLinkLongPressListener(new LinkSpanDrawable.LinksTextView.OnLinkPress() { // from class: org.telegram.ui.Cells.TextDetailCell$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.LinkSpanDrawable.LinksTextView.OnLinkPress
            public final void run(ClickableSpan clickableSpan) {
                TextDetailCell.this.lambda$new$0(clickableSpan);
            }
        });
        this.multiline = z;
        if (z) {
            setMinimumHeight(AndroidUtilities.dp(60.0f));
        } else {
            linksTextView.setLines(1);
            linksTextView.setSingleLine(true);
        }
        linksTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider));
        linksTextView.setTextSize(1, 13.0f);
        linksTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linksTextView.setImportantForAccessibility(2);
        linksTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 23.0f, 33.0f, 23.0f, 10.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setImportantForAccessibility(2);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(imageView, LayoutHelper.createFrameRelatively(48.0f, 48.0f, 8388629, 0.0f, 0.0f, 12.0f, 0.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ClickableSpan clickableSpan) {
        if (clickableSpan != null) {
            try {
                performHapticFeedback(0, 1);
            } catch (Exception unused) {
            }
            clickableSpan.onClick(this.valueTextView);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.valueTextView.hit(((int) motionEvent.getX()) - this.valueTextView.getLeft(), ((int) motionEvent.getY()) - this.valueTextView.getTop()) != null) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM);
        if (!this.multiline) {
            i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f) + (this.needDivider ? 1 : 0), NUM);
        }
        super.onMeasure(makeMeasureSpec, i2);
    }

    public void setTextAndValue(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        this.textView.setText(charSequence);
        this.valueTextView.setText(charSequence2);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setImage(Drawable drawable) {
        setImage(drawable, null);
    }

    public void setImage(Drawable drawable, CharSequence charSequence) {
        ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).rightMargin = (LocaleController.isRTL || drawable == null) ? AndroidUtilities.dp(23.0f) : AndroidUtilities.dp(58.0f);
        this.imageView.setImageDrawable(drawable);
        int i = 0;
        this.imageView.setFocusable(drawable != null);
        this.imageView.setContentDescription(charSequence);
        if (drawable == null) {
            this.imageView.setBackground(null);
            this.imageView.setImportantForAccessibility(2);
        } else {
            this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(48.0f), 0, Theme.getColor("listSelectorSDK21", this.resourcesProvider)));
            this.imageView.setImportantForAccessibility(1);
        }
        int dp = AndroidUtilities.dp(23.0f);
        if (drawable != null) {
            i = AndroidUtilities.dp(48.0f);
        }
        int i2 = dp + i;
        if (LocaleController.isRTL) {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).leftMargin = i2;
        } else {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).rightMargin = i2;
        }
        this.textView.requestLayout();
    }

    public void setImageClickListener(View.OnClickListener onClickListener) {
        this.imageView.setOnClickListener(onClickListener);
    }

    public void setContentDescriptionValueFirst(boolean z) {
        this.contentDescriptionValueFirst = z;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.textView.invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CharSequence text = this.textView.getText();
        CharSequence text2 = this.valueTextView.getText();
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text2)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append((Object) (this.contentDescriptionValueFirst ? text2 : text));
        sb.append(": ");
        if (!this.contentDescriptionValueFirst) {
            text = text2;
        }
        sb.append((Object) text);
        accessibilityNodeInfo.setText(sb.toString());
    }
}
