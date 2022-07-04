package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public class ReadAllMentionsMenu {
    public static final int TYPE_MENTIONS = 1;
    public static final int TYPE_REACTIONS = 0;

    public static ActionBarPopupWindow show(int type, Activity activity, FrameLayout contentView, View mentionButton, Theme.ResourcesProvider resourcesProvider, Runnable onRead) {
        String str;
        int i;
        ActionBarPopupWindow.ActionBarPopupWindowLayout popupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(activity);
        popupWindowLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem((Context) activity, true, true, resourcesProvider);
        cell.setMinimumWidth(AndroidUtilities.dp(200.0f));
        if (type == 0) {
            i = NUM;
            str = "ReadAllReactions";
        } else {
            i = NUM;
            str = "ReadAllMentions";
        }
        cell.setTextAndIcon(LocaleController.getString(str, i), NUM);
        cell.setOnClickListener(new ReadAllMentionsMenu$$ExternalSyntheticLambda0(onRead));
        popupWindowLayout.addView(cell);
        ActionBarPopupWindow scrimPopupWindow = new ActionBarPopupWindow(popupWindowLayout, -2, -2);
        scrimPopupWindow.setPauseNotifications(true);
        scrimPopupWindow.setDismissAnimationDuration(220);
        scrimPopupWindow.setOutsideTouchable(true);
        scrimPopupWindow.setClippingEnabled(true);
        scrimPopupWindow.setAnimationStyle(NUM);
        scrimPopupWindow.setFocusable(true);
        popupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        scrimPopupWindow.setInputMethodMode(2);
        scrimPopupWindow.setSoftInputMode(0);
        scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
        scrimPopupWindow.showAtLocation(contentView, 51, (int) (((mentionButton.getX() + ((float) mentionButton.getWidth())) - ((float) popupWindowLayout.getMeasuredWidth())) + ((float) AndroidUtilities.dp(8.0f))), (int) (mentionButton.getY() - ((float) popupWindowLayout.getMeasuredHeight())));
        return scrimPopupWindow;
    }

    static /* synthetic */ void lambda$show$0(Runnable onRead, View view) {
        if (onRead != null) {
            onRead.run();
        }
    }
}
