package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
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

public class DialogsEmptyCell extends FrameLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private DialogsEmptyCellContent currentContent = null;
    private int currentType = -1;
    private DialogsEmptyCellContent lastContent = null;
    private int offset = 0;

    private static class DialogsEmptyCellContent extends LinearLayout {
        private int currentType = 0;
        private TextView emptyTextView1;
        private TextView emptyTextView2;
        private RLottieImageView imageView;

        /* access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
            return true;
        }

        public DialogsEmptyCellContent(Context context) {
            super(context);
            setGravity(17);
            setOrientation(1);
            setOnTouchListener(DialogsEmptyCell$DialogsEmptyCellContent$$ExternalSyntheticLambda1.INSTANCE);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createLinear(100, 100, 17, 52, 4, 52, 0));
            this.imageView.setOnClickListener(new DialogsEmptyCell$DialogsEmptyCellContent$$ExternalSyntheticLambda0(this));
            TextView textView = new TextView(context);
            this.emptyTextView1 = textView;
            textView.setTextColor(Theme.getColor("chats_nameMessage_threeLines"));
            this.emptyTextView1.setText(LocaleController.getString("NoChats", NUM));
            this.emptyTextView1.setTextSize(1, 20.0f);
            this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.emptyTextView1.setGravity(17);
            addView(this.emptyTextView1, LayoutHelper.createLinear(-1, -2, 51, 52, 10, 52, 0));
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
            addView(this.emptyTextView2, LayoutHelper.createLinear(-1, -2, 51, 52, 7, 52, 0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x009b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setType(int r6) {
            /*
                r5 = this;
                int r0 = r5.currentType
                if (r0 != r6) goto L_0x0005
                return
            L_0x0005:
                r5.currentType = r6
                r0 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
                java.lang.String r1 = "NoChats"
                r2 = 0
                if (r6 != 0) goto L_0x0025
                r6 = 2131626526(0x7f0e0a1e, float:1.888029E38)
                java.lang.String r3 = "NoChatsHelp"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r3, r6)
                android.widget.TextView r3 = r5.emptyTextView1
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r3.setText(r0)
            L_0x0021:
                r0 = r6
                r6 = 0
            L_0x0023:
                r1 = 0
                goto L_0x0083
            L_0x0025:
                r3 = 1
                if (r6 != r3) goto L_0x003b
                r6 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
                java.lang.String r3 = "NoChatsContactsHelp"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r3, r6)
                android.widget.TextView r3 = r5.emptyTextView1
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r3.setText(r0)
                goto L_0x0021
            L_0x003b:
                r0 = 2
                if (r6 != r0) goto L_0x005e
                org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
                r6.setAutoRepeat(r2)
                r6 = 2131558437(0x7f0d0025, float:1.874219E38)
                r0 = 2131625661(0x7f0e06bd, float:1.8878536E38)
                java.lang.String r1 = "FilterNoChatsToDisplayInfo"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                android.widget.TextView r1 = r5.emptyTextView1
                r3 = 2131625660(0x7f0e06bc, float:1.8878534E38)
                java.lang.String r4 = "FilterNoChatsToDisplay"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setText(r3)
                goto L_0x0023
            L_0x005e:
                org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
                r6.setAutoRepeat(r3)
                r6 = 2131558436(0x7f0d0024, float:1.8742188E38)
                r0 = 2131625616(0x7f0e0690, float:1.8878445E38)
                java.lang.String r1 = "FilterAddingChatsInfo"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                android.widget.TextView r1 = r5.emptyTextView1
                r3 = 2131625615(0x7f0e068f, float:1.8878443E38)
                java.lang.String r4 = "FilterAddingChats"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setText(r3)
                r1 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            L_0x0083:
                float r1 = (float) r1
                r5.setTranslationY(r1)
                if (r6 == 0) goto L_0x009b
                org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
                r1.setVisibility(r2)
                org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
                r2 = 100
                r1.setAnimation(r6, r2, r2)
                org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
                r6.playAnimation()
                goto L_0x00a2
            L_0x009b:
                org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
                r1 = 8
                r6.setVisibility(r1)
            L_0x00a2:
                boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
                if (r6 == 0) goto L_0x00b6
                boolean r6 = org.telegram.messenger.AndroidUtilities.isSmallTablet()
                if (r6 != 0) goto L_0x00b6
                r6 = 10
                r1 = 32
                java.lang.String r0 = r0.replace(r6, r1)
            L_0x00b6:
                android.widget.TextView r6 = r5.emptyTextView2
                r6.setText(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogsEmptyCell.DialogsEmptyCellContent.setType(int):void");
        }

        public void setOffset(int i) {
            int i2 = this.currentType;
            if (i2 == 2 || i2 == 3) {
                float f = (float) i;
                this.imageView.setTranslationY(f);
                this.emptyTextView1.setTranslationY(f);
                this.emptyTextView2.setTranslationY(f);
            }
        }
    }

    public DialogsEmptyCell(Context context) {
        super(context);
    }

    public void setType(int i) {
        setType(i, true);
    }

    public void setType(int i, boolean z) {
        if (this.currentType != i) {
            this.currentType = i;
            if (this.currentContent == null) {
                DialogsEmptyCellContent dialogsEmptyCellContent = new DialogsEmptyCellContent(getContext());
                this.currentContent = dialogsEmptyCellContent;
                addView(dialogsEmptyCellContent, LayoutHelper.createFrame(-1, -1, 119));
            }
            this.currentContent.setType(i);
            this.currentContent.setOffset(this.offset);
        }
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

    private void updateLayout() {
        if ((getParent() instanceof View) && ((View) getParent()).getPaddingTop() != 0) {
            int i = -(getTop() / 2);
            this.offset = i;
            DialogsEmptyCellContent dialogsEmptyCellContent = this.currentContent;
            if (dialogsEmptyCellContent != null) {
                dialogsEmptyCellContent.setOffset(i);
            }
            DialogsEmptyCellContent dialogsEmptyCellContent2 = this.lastContent;
            if (dialogsEmptyCellContent2 != null) {
                dialogsEmptyCellContent2.setOffset(this.offset);
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
