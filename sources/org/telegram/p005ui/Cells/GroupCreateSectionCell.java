package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.GroupCreateSectionCell */
public class GroupCreateSectionCell extends FrameLayout {
    private Drawable drawable = getResources().getDrawable(C0541R.drawable.shadowdown);
    private TextView textView;

    public GroupCreateSectionCell(Context context) {
        int i;
        int i2 = 5;
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_graySection));
        this.drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_groupcreate_sectionShadow), Mode.MULTIPLY));
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor(Theme.key_groupcreate_sectionText));
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 16);
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(40.0f), NUM));
    }

    protected void onDraw(Canvas canvas) {
        this.drawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.m10dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
        this.drawable.draw(canvas);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
