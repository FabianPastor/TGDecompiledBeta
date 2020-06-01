package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GraySectionCell extends FrameLayout {
    private TextView rightTextView;
    private TextView textView;

    public GraySectionCell(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor("graySection"));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor("key_graySectionText"));
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        TextView textView3 = new TextView(getContext());
        this.rightTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.rightTextView.setTextColor(Theme.getColor("key_graySectionText"));
        this.rightTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.rightTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    public void setText(String str) {
        this.textView.setText(str);
        this.rightTextView.setVisibility(8);
    }

    public void setText(String str, String str2, View.OnClickListener onClickListener) {
        this.textView.setText(str);
        this.rightTextView.setText(str2);
        this.rightTextView.setOnClickListener(onClickListener);
        this.rightTextView.setVisibility(0);
    }

    public static void createThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView) {
        List<ThemeDescription> list2 = list;
        list2.add(new ThemeDescription((View) recyclerListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        list2.add(new ThemeDescription((View) recyclerListView, 0, new Class[]{GraySectionCell.class}, new String[]{"rightTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        list2.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
    }
}
