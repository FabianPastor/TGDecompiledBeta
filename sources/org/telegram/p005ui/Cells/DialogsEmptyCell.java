package org.telegram.p005ui.Cells;

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
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.TLRPC.RecentMeUrl;

/* renamed from: org.telegram.ui.Cells.DialogsEmptyCell */
public class DialogsEmptyCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private TextView emptyTextView1;
    private TextView emptyTextView2;

    /* renamed from: org.telegram.ui.Cells.DialogsEmptyCell$1 */
    class CLASSNAME implements OnTouchListener {
        CLASSNAME() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(new CLASSNAME());
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setText(LocaleController.getString("NoChats", CLASSNAMER.string.NoChats));
        this.emptyTextView1.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 20.0f);
        addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        this.emptyTextView2 = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", CLASSNAMER.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace(10, ' ');
        }
        this.emptyTextView2.setText(help);
        this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView2.setTextSize(1, 15.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(6.0f), AndroidUtilities.m9dp(8.0f), 0);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.m9dp(2.0f), 1.0f);
        addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, 20.0f));
    }

    public void setType(int value) {
        String help;
        this.currentType = value;
        if (this.currentType == 0) {
            help = LocaleController.getString("NoChatsHelp", CLASSNAMER.string.NoChatsHelp);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
        } else {
            help = LocaleController.getString("NoChatsContactsHelp", CLASSNAMER.string.NoChatsContactsHelp);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
        }
        this.emptyTextView2.setText(help);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (totalHeight == 0) {
            totalHeight = (AndroidUtilities.displaySize.y - CLASSNAMEActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        if (this.currentType == 0) {
            ArrayList<RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                totalHeight -= (((AndroidUtilities.m9dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.m9dp(50.0f);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(totalHeight, NUM));
            return;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(totalHeight, Integer.MIN_VALUE));
    }
}
