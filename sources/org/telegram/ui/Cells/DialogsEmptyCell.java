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
import org.telegram.tgnet.TLRPC$RecentMeUrl;
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

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener($$Lambda$DialogsEmptyCell$EZBh9dIKAqFHQ518PFNPpE9QmAU.INSTANCE);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 52.0f, 4.0f, 52.0f, 0.0f));
        this.imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DialogsEmptyCell.this.lambda$new$1$DialogsEmptyCell(view);
            }
        });
        TextView textView = new TextView(context);
        this.emptyTextView1 = textView;
        textView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
        this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
        this.emptyTextView1.setTextSize(1, 20.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setGravity(17);
        addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 10.0f, 52.0f, 0.0f));
        this.emptyTextView2 = new TextView(context);
        String string = LocaleController.getString("NoChatsHelp", NUM);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$DialogsEmptyCell(View view) {
        if (!this.imageView.isPlaying()) {
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0090  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setType(int r6) {
        /*
            r5 = this;
            int r0 = r5.currentType
            if (r0 != r6) goto L_0x0005
            return
        L_0x0005:
            r5.currentType = r6
            r0 = 2131626240(0x7f0e0900, float:1.887971E38)
            java.lang.String r1 = "NoChats"
            r2 = 0
            if (r6 != 0) goto L_0x0024
            r6 = 2131626242(0x7f0e0902, float:1.8879715E38)
            java.lang.String r3 = "NoChatsHelp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r3, r6)
            android.widget.TextView r3 = r5.emptyTextView1
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setText(r0)
        L_0x0021:
            r0 = r6
            r6 = 0
            goto L_0x007c
        L_0x0024:
            r3 = 1
            if (r6 != r3) goto L_0x003a
            r6 = 2131626241(0x7f0e0901, float:1.8879713E38)
            java.lang.String r3 = "NoChatsContactsHelp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r3, r6)
            android.widget.TextView r3 = r5.emptyTextView1
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setText(r0)
            goto L_0x0021
        L_0x003a:
            r0 = 2
            if (r6 != r0) goto L_0x005d
            org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
            r6.setAutoRepeat(r2)
            r6 = 2131558428(0x7f0d001c, float:1.8742172E38)
            r0 = 2131625515(0x7f0e062b, float:1.887824E38)
            java.lang.String r1 = "FilterNoChatsToDisplayInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.widget.TextView r1 = r5.emptyTextView1
            r3 = 2131625514(0x7f0e062a, float:1.8878238E38)
            java.lang.String r4 = "FilterNoChatsToDisplay"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            goto L_0x007c
        L_0x005d:
            org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
            r6.setAutoRepeat(r3)
            r6 = 2131558427(0x7f0d001b, float:1.874217E38)
            r0 = 2131625470(0x7f0e05fe, float:1.8878149E38)
            java.lang.String r1 = "FilterAddingChatsInfo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.widget.TextView r1 = r5.emptyTextView1
            r3 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r4 = "FilterAddingChats"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
        L_0x007c:
            if (r6 == 0) goto L_0x0090
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            r1.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            r2 = 100
            r1.setAnimation(r6, r2, r2)
            org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
            r6.playAnimation()
            goto L_0x0097
        L_0x0090:
            org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
            r1 = 8
            r6.setVisibility(r1)
        L_0x0097:
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r6 == 0) goto L_0x00ab
            boolean r6 = org.telegram.messenger.AndroidUtilities.isSmallTablet()
            if (r6 != 0) goto L_0x00ab
            r6 = 10
            r1 = 32
            java.lang.String r0 = r0.replace(r6, r1)
        L_0x00ab:
            android.widget.TextView r6 = r5.emptyTextView2
            r6.setText(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogsEmptyCell.setType(int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateLayout();
    }

    public void offsetTopAndBottom(int i) {
        super.offsetTopAndBottom(i);
        updateLayout();
    }

    public void updateLayout() {
        if (getParent() instanceof View) {
            int i = this.currentType;
            if ((i == 2 || i == 3) && ((View) getParent()).getPaddingTop() != 0) {
                float f = (float) (-(getTop() / 2));
                this.imageView.setTranslationY(f);
                this.emptyTextView1.setTranslationY(f);
                this.emptyTextView2.setTranslationY(f);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            i3 = view.getMeasuredHeight();
            if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                i3 -= AndroidUtilities.statusBarHeight;
            }
        } else {
            i3 = View.MeasureSpec.getSize(i2);
        }
        if (i3 == 0) {
            i3 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        int i4 = this.currentType;
        if (i4 == 0 || i4 == 2 || i4 == 3) {
            ArrayList<TLRPC$RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                i3 -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(i3, NUM));
            return;
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), NUM));
    }
}
