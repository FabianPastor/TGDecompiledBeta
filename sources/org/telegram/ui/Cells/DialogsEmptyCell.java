package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DialogsEmptyCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private TextView emptyTextView1;
    private TextView emptyTextView2;

    /* renamed from: org.telegram.ui.Cells.DialogsEmptyCell$1 */
    class C08741 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C08741() {
        }
    }

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(new C08741());
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setText(LocaleController.getString("NoChats", C0446R.string.NoChats));
        this.emptyTextView1.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 20.0f);
        addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView2 = new TextView(context);
        context = LocaleController.getString("NoChatsHelp", C0446R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            context = context.replace('\n', ' ');
        }
        this.emptyTextView2.setText(context);
        this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView2.setTextSize(1, 15.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), 0);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2));
    }

    protected void onMeasure(int i, int i2) {
        i2 = MeasureSpec.getSize(i2);
        if (i2 == 0) {
            i2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        ArrayList arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
        if (!arrayList.isEmpty()) {
            i2 -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
    }
}
