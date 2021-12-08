package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class DialogsEmptyCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private RLottieImageView imageView;

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(DialogsEmptyCell$$ExternalSyntheticLambda1.INSTANCE);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 52.0f, 4.0f, 52.0f, 0.0f));
        this.imageView.setOnClickListener(new DialogsEmptyCell$$ExternalSyntheticLambda0(this));
        TextView textView = new TextView(context);
        this.emptyTextView1 = textView;
        textView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
        this.emptyTextView1.setTextSize(1, 20.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setGravity(17);
        addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 10.0f, 52.0f, 0.0f));
        this.emptyTextView2 = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", NUM);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace(10, ' ');
        }
        this.emptyTextView2.setText(help);
        this.emptyTextView2.setTextColor(Theme.getColor("chats_message"));
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-DialogsEmptyCell  reason: not valid java name */
    public /* synthetic */ void m1531lambda$new$1$orgtelegramuiCellsDialogsEmptyCell(View v) {
        if (!this.imageView.isPlaying()) {
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }
    }

    public void setType(int value) {
        String help;
        int icon;
        if (this.currentType != value) {
            this.currentType = value;
            if (value == 0) {
                icon = 0;
                help = LocaleController.getString("NoChatsHelp", NUM);
                this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
            } else if (value == 1) {
                icon = 0;
                help = LocaleController.getString("NoChatsContactsHelp", NUM);
                this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
            } else if (value == 2) {
                this.imageView.setAutoRepeat(false);
                icon = NUM;
                help = LocaleController.getString("FilterNoChatsToDisplayInfo", NUM);
                this.emptyTextView1.setText(LocaleController.getString("FilterNoChatsToDisplay", NUM));
            } else {
                this.imageView.setAutoRepeat(true);
                icon = NUM;
                help = LocaleController.getString("FilterAddingChatsInfo", NUM);
                this.emptyTextView1.setText(LocaleController.getString("FilterAddingChats", NUM));
            }
            if (icon != 0) {
                this.imageView.setVisibility(0);
                this.imageView.setAnimation(icon, 100, 100);
                this.imageView.playAnimation();
            } else {
                this.imageView.setVisibility(8);
            }
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
            this.emptyTextView2.setText(help);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateLayout();
    }

    public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        updateLayout();
    }

    public void updateLayout() {
        if (getParent() instanceof View) {
            int i = this.currentType;
            if ((i == 2 || i == 3) && ((View) getParent()).getPaddingTop() != 0) {
                int offset = -(getTop() / 2);
                this.imageView.setTranslationY((float) offset);
                this.emptyTextView1.setTranslationY((float) offset);
                this.emptyTextView2.setTranslationY((float) offset);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            totalHeight = view.getMeasuredHeight();
            if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                totalHeight -= AndroidUtilities.statusBarHeight;
            }
        } else {
            totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        if (totalHeight == 0) {
            totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        int i = this.currentType;
        if (i == 0 || i == 2 || i == 3) {
            ArrayList<TLRPC.RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                totalHeight -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(totalHeight, NUM));
            return;
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), NUM));
    }
}
