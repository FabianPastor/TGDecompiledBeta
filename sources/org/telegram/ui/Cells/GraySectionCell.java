package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GraySectionCell extends FrameLayout {
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatedTextView rightTextView;
    private TextView textView;

    public GraySectionCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GraySectionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        setBackgroundColor(getThemedColor("graySection"));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(getThemedColor("key_graySectionText"));
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        AnonymousClass1 r0 = new AnimatedTextView(getContext(), true, true, true) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        this.rightTextView = r0;
        r0.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        this.rightTextView.setAnimationProperties(1.0f, 0, 400, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.rightTextView.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.rightTextView.setTextColor(getThemedColor("key_graySectionText"));
        this.rightTextView.setGravity(LocaleController.isRTL ? 3 : 5);
        addView(this.rightTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        ViewCompat.setAccessibilityHeading(this, true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    public void setTextColor(String key) {
        int color = getThemedColor(key);
        this.textView.setTextColor(color);
        this.rightTextView.setTextColor(color);
    }

    public CharSequence getText() {
        return this.textView.getText();
    }

    public void setText(String text) {
        this.textView.setText(text);
        this.rightTextView.setVisibility(8);
        this.rightTextView.setOnClickListener((View.OnClickListener) null);
    }

    public void setText(String left, String right, View.OnClickListener onClickListener) {
        this.textView.setText(left);
        this.rightTextView.setText(right, false);
        this.rightTextView.setOnClickListener(onClickListener);
        this.rightTextView.setVisibility(0);
    }

    public void setRightText(String right) {
        setRightText(right, true);
    }

    public void setRightText(String right, boolean moveDown) {
        this.rightTextView.setText(right, true, moveDown);
        this.rightTextView.setVisibility(0);
    }

    public static void createThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView) {
        List<ThemeDescription> list = descriptions;
        Class<GraySectionCell> cls = GraySectionCell.class;
        list.add(new ThemeDescription((View) listView, 0, new Class[]{cls}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        list.add(new ThemeDescription((View) listView, 0, new Class[]{cls}, new String[]{"rightTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        list.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{cls}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
    }

    public TextView getTextView() {
        return this.textView;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
