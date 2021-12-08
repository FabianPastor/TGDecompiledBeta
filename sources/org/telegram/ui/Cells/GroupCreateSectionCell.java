package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateSectionCell extends FrameLayout {
    private Drawable drawable;
    private TextView textView;

    public GroupCreateSectionCell(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor("graySection"));
        Drawable drawable2 = getResources().getDrawable(NUM);
        this.drawable = drawable2;
        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("groupcreate_sectionShadow"), PorterDuff.Mode.MULTIPLY));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor("groupcreate_sectionText"));
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
        this.drawable.draw(canvas);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
