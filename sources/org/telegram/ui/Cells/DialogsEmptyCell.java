package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DialogsEmptyCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private TextView emptyTextView1;
    private TextView emptyTextView2;

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(-$$Lambda$DialogsEmptyCell$7lLGhZthID2bSlrXEXwZZGk1ZsM.INSTANCE);
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
        this.emptyTextView1.setTextSize(1, 20.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setGravity(17);
        addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 4.0f, 52.0f, 0.0f));
        this.emptyTextView2 = new TextView(context);
        CharSequence string = LocaleController.getString("NoChatsHelp", NUM);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            string = string.replace(10, ' ');
        }
        this.emptyTextView2.setText(string);
        this.emptyTextView2.setTextColor(Theme.getColor("chats_message"));
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    public void setType(int i) {
        CharSequence string;
        this.currentType = i;
        if (this.currentType == 0) {
            string = LocaleController.getString("NoChatsHelp", NUM);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                string = string.replace(10, ' ');
            }
        } else {
            string = LocaleController.getString("NoChatsContactsHelp", NUM);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                string = string.replace(10, ' ');
            }
        }
        this.emptyTextView2.setText(string);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i2 = MeasureSpec.getSize(i2);
        if (i2 == 0) {
            i2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        if (this.currentType == 0) {
            ArrayList arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                i2 -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            return;
        }
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), NUM));
    }
}
