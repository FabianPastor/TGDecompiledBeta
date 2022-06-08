package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$DialogFilter;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_dialogFilterSuggested;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFilter;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;

public class FiltersSetupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public int createFilterRow;
    /* access modifiers changed from: private */
    public int createSectionRow;
    /* access modifiers changed from: private */
    public int filterHelpRow;
    /* access modifiers changed from: private */
    public int filtersEndRow;
    /* access modifiers changed from: private */
    public int filtersHeaderRow;
    /* access modifiers changed from: private */
    public int filtersStartRow;
    /* access modifiers changed from: private */
    public boolean ignoreUpdates;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean orderChanged;
    /* access modifiers changed from: private */
    public int recommendedEndRow;
    /* access modifiers changed from: private */
    public int recommendedHeaderRow;
    /* access modifiers changed from: private */
    public int recommendedSectionRow;
    /* access modifiers changed from: private */
    public int recommendedStartRow;
    /* access modifiers changed from: private */
    public int rowCount = 0;
    /* access modifiers changed from: private */
    public boolean showAllChats;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static class TextCell extends FrameLayout {
        private ImageView imageView;
        private SimpleTextView textView;

        public TextCell(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
            this.textView.setTag("windowBackgroundWhiteBlueText2");
            addView(this.textView);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            setMeasuredDimension(size, AndroidUtilities.dp(50.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int textHeight = ((i4 - i2) - this.textView.getTextHeight()) / 2;
            float f = 64.0f;
            if (LocaleController.isRTL) {
                int measuredWidth = getMeasuredWidth() - this.textView.getMeasuredWidth();
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                i5 = measuredWidth - AndroidUtilities.dp(f);
            } else {
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                i5 = AndroidUtilities.dp(f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(i5, textHeight, simpleTextView.getMeasuredWidth() + i5, this.textView.getMeasuredHeight() + textHeight);
            int dp = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(dp, 0, imageView2.getMeasuredWidth() + dp, this.imageView.getMeasuredHeight());
        }

        public void setTextAndIcon(String str, Drawable drawable, boolean z) {
            this.textView.setText(str);
            this.imageView.setImageDrawable(drawable);
        }
    }

    public static class SuggestedFilterCell extends FrameLayout {
        private ProgressButton addButton;
        private boolean needDivider;
        private TLRPC$TL_dialogFilterSuggested suggestedFilter;
        private TextView textView;
        private TextView valueTextView;

        public SuggestedFilterCell(Context context) {
            super(context);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 10.0f, 22.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 35.0f, 22.0f, 0.0f));
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
            addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(64.0f));
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.addButton, i3, 0, i4, 0);
            measureChildWithMargins(this.textView, i3, this.addButton.getMeasuredWidth(), i4, 0);
            measureChildWithMargins(this.valueTextView, i3, this.addButton.getMeasuredWidth(), i4, 0);
        }

        public void setFilter(TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested, boolean z) {
            this.needDivider = z;
            this.suggestedFilter = tLRPC$TL_dialogFilterSuggested;
            setWillNotDraw(!z);
            this.textView.setText(tLRPC$TL_dialogFilterSuggested.filter.title);
            this.valueTextView.setText(tLRPC$TL_dialogFilterSuggested.description);
        }

        public TLRPC$TL_dialogFilterSuggested getSuggestedFilter() {
            return this.suggestedFilter;
        }

        public void setAddOnClickListener(View.OnClickListener onClickListener) {
            this.addButton.setOnClickListener(onClickListener);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setText(this.addButton.getText());
            accessibilityNodeInfo.setClassName("android.widget.Button");
        }
    }

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(NUM, 90, 90);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            this.imageView.setImportantForAccessibility(2);
            addView(this.imageView, LayoutHelper.createFrame(90, 90.0f, 49, 0.0f, 14.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new FiltersSetupActivity$HintInnerCell$$ExternalSyntheticLambda0(this));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CreateNewFilterInfo", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 40.0f, 121.0f, 40.0f, 24.0f));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
        }
    }

    public static class FilterCell extends FrameLayout {
        /* access modifiers changed from: private */
        public MessagesController.DialogFilter currentFilter;
        private ImageView moveImageView;
        private boolean needDivider;
        private ImageView optionsImageView;
        float progressToLock;
        private SimpleTextView textView;
        private TextView valueTextView;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public FilterCell(Context context) {
            super(context);
            Context context2 = context;
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context2);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.moveImageView.setContentDescription(LocaleController.getString("FilterReorder", NUM));
            this.moveImageView.setClickable(true);
            int i = 5;
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context2);
            this.textView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(16);
            this.textView.setMaxLines(1);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            Drawable drawable = ContextCompat.getDrawable(getContext(), NUM);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.textView.setRightDrawable(drawable);
            SimpleTextView simpleTextView2 = this.textView;
            boolean z = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 80.0f : 64.0f, 14.0f, z ? 64.0f : 80.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.valueTextView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            TextView textView3 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 80.0f : 64.0f, 35.0f, z2 ? 64.0f : 80.0f, 0.0f));
            this.valueTextView.setVisibility(8);
            ImageView imageView2 = new ImageView(context2);
            this.optionsImageView = imageView2;
            imageView2.setFocusable(false);
            this.optionsImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsImageView.setImageResource(NUM);
            this.optionsImageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            addView(this.optionsImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        /* JADX WARNING: Removed duplicated region for block: B:52:0x00da  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x00fb  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x010f  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0115  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0145  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x014d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setFilter(org.telegram.messenger.MessagesController.DialogFilter r10, boolean r11) {
            /*
                r9 = this;
                org.telegram.messenger.MessagesController$DialogFilter r0 = r9.currentFilter
                r1 = -1
                if (r0 != 0) goto L_0x0007
                r0 = -1
                goto L_0x0009
            L_0x0007:
                int r0 = r0.id
            L_0x0009:
                r9.currentFilter = r10
                if (r10 != 0) goto L_0x000e
                goto L_0x0010
            L_0x000e:
                int r1 = r10.id
            L_0x0010:
                r2 = 0
                if (r0 == r1) goto L_0x0015
                r0 = 1
                goto L_0x0016
            L_0x0015:
                r0 = 0
            L_0x0016:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                boolean r3 = r10.isDefault()
                r4 = 2131625821(0x7f0e075d, float:1.887886E38)
                java.lang.String r5 = "FilterAllChats"
                java.lang.String r6 = ", "
                if (r3 != 0) goto L_0x00bd
                int r3 = r10.flags
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS
                r8 = r3 & r7
                if (r8 != r7) goto L_0x0032
                goto L_0x00bd
            L_0x0032:
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CONTACTS
                r3 = r3 & r7
                if (r3 == 0) goto L_0x004c
                int r3 = r1.length()
                if (r3 == 0) goto L_0x0040
                r1.append(r6)
            L_0x0040:
                r3 = 2131625837(0x7f0e076d, float:1.8878893E38)
                java.lang.String r7 = "FilterContacts"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r1.append(r3)
            L_0x004c:
                int r3 = r10.flags
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS
                r3 = r3 & r7
                if (r3 == 0) goto L_0x0068
                int r3 = r1.length()
                if (r3 == 0) goto L_0x005c
                r1.append(r6)
            L_0x005c:
                r3 = 2131625867(0x7f0e078b, float:1.8878954E38)
                java.lang.String r7 = "FilterNonContacts"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r1.append(r3)
            L_0x0068:
                int r3 = r10.flags
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_GROUPS
                r3 = r3 & r7
                if (r3 == 0) goto L_0x0084
                int r3 = r1.length()
                if (r3 == 0) goto L_0x0078
                r1.append(r6)
            L_0x0078:
                r3 = 2131625854(0x7f0e077e, float:1.8878928E38)
                java.lang.String r7 = "FilterGroups"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r1.append(r3)
            L_0x0084:
                int r3 = r10.flags
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CHANNELS
                r3 = r3 & r7
                if (r3 == 0) goto L_0x00a0
                int r3 = r1.length()
                if (r3 == 0) goto L_0x0094
                r1.append(r6)
            L_0x0094:
                r3 = 2131625828(0x7f0e0764, float:1.8878875E38)
                java.lang.String r7 = "FilterChannels"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r1.append(r3)
            L_0x00a0:
                int r3 = r10.flags
                int r7 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_BOTS
                r3 = r3 & r7
                if (r3 == 0) goto L_0x00c4
                int r3 = r1.length()
                if (r3 == 0) goto L_0x00b0
                r1.append(r6)
            L_0x00b0:
                r3 = 2131625827(0x7f0e0763, float:1.8878873E38)
                java.lang.String r7 = "FilterBots"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                r1.append(r3)
                goto L_0x00c4
            L_0x00bd:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r1.append(r3)
            L_0x00c4:
                java.util.ArrayList<java.lang.Long> r3 = r10.alwaysShow
                boolean r3 = r3.isEmpty()
                if (r3 == 0) goto L_0x00d4
                java.util.ArrayList<java.lang.Long> r3 = r10.neverShow
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x00f5
            L_0x00d4:
                int r3 = r1.length()
                if (r3 == 0) goto L_0x00dd
                r1.append(r6)
            L_0x00dd:
                java.util.ArrayList<java.lang.Long> r3 = r10.alwaysShow
                int r3 = r3.size()
                java.util.ArrayList<java.lang.Long> r6 = r10.neverShow
                int r6 = r6.size()
                int r3 = r3 + r6
                java.lang.Object[] r6 = new java.lang.Object[r2]
                java.lang.String r7 = "Exception"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r7, r3, r6)
                r1.append(r3)
            L_0x00f5:
                int r3 = r1.length()
                if (r3 != 0) goto L_0x0107
                r3 = 2131625863(0x7f0e0787, float:1.8878946E38)
                java.lang.String r6 = "FilterNoChats"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
                r1.append(r3)
            L_0x0107:
                java.lang.String r3 = r10.name
                boolean r6 = r10.isDefault()
                if (r6 == 0) goto L_0x0113
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            L_0x0113:
                if (r0 != 0) goto L_0x0121
                org.telegram.messenger.MessagesController$DialogFilter r0 = r9.currentFilter
                boolean r0 = r0.locked
                if (r0 == 0) goto L_0x011e
                r0 = 1065353216(0x3var_, float:1.0)
                goto L_0x011f
            L_0x011e:
                r0 = 0
            L_0x011f:
                r9.progressToLock = r0
            L_0x0121:
                org.telegram.ui.ActionBar.SimpleTextView r0 = r9.textView
                android.graphics.Paint r4 = r0.getPaint()
                android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
                r5 = 1101004800(0x41a00000, float:20.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                java.lang.CharSequence r3 = org.telegram.messenger.Emoji.replaceEmoji(r3, r4, r5, r2)
                r0.setText(r3)
                android.widget.TextView r0 = r9.valueTextView
                r0.setText(r1)
                r9.needDivider = r11
                boolean r10 = r10.isDefault()
                if (r10 == 0) goto L_0x014d
                android.widget.ImageView r10 = r9.optionsImageView
                r11 = 8
                r10.setVisibility(r11)
                goto L_0x0152
            L_0x014d:
                android.widget.ImageView r10 = r9.optionsImageView
                r10.setVisibility(r2)
            L_0x0152:
                r9.invalidate()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FiltersSetupActivity.FilterCell.setFilter(org.telegram.messenger.MessagesController$DialogFilter, boolean):void");
        }

        public MessagesController.DialogFilter getCurrentFilter() {
            return this.currentFilter;
        }

        public void setOnOptionsClick(View.OnClickListener onClickListener) {
            this.optionsImageView.setOnClickListener(onClickListener);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(62.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(62.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
            MessagesController.DialogFilter dialogFilter = this.currentFilter;
            if (dialogFilter != null) {
                boolean z = dialogFilter.locked;
                if (z) {
                    float f = this.progressToLock;
                    if (f != 1.0f) {
                        this.progressToLock = f + 0.10666667f;
                        invalidate();
                    }
                }
                if (!z) {
                    float f2 = this.progressToLock;
                    if (f2 != 0.0f) {
                        this.progressToLock = f2 - 0.10666667f;
                        invalidate();
                    }
                }
            }
            float clamp = Utilities.clamp(this.progressToLock, 1.0f, 0.0f);
            this.progressToLock = clamp;
            this.textView.setRightDrawableScale(clamp);
            this.textView.invalidate();
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
            this.moveImageView.setOnTouchListener(onTouchListener);
        }
    }

    public boolean onFragmentCreate() {
        updateRows(true);
        getMessagesController().loadRemoteFilters(true);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (getMessagesController().suggestedFilters.isEmpty()) {
            getMessagesController().loadSuggestedFilters();
        }
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean z) {
        ListAdapter listAdapter;
        this.recommendedHeaderRow = -1;
        this.recommendedStartRow = -1;
        this.recommendedEndRow = -1;
        this.recommendedSectionRow = -1;
        ArrayList<TLRPC$TL_dialogFilterSuggested> arrayList = getMessagesController().suggestedFilters;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.filterHelpRow = 0;
        int size = getMessagesController().dialogFilters.size();
        if (!getUserConfig().isPremium()) {
            size--;
            this.showAllChats = false;
        } else {
            this.showAllChats = true;
        }
        if (!arrayList.isEmpty() && size < 10) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.recommendedHeaderRow = i;
            this.recommendedStartRow = i2;
            int size2 = i2 + arrayList.size();
            this.rowCount = size2;
            this.recommendedEndRow = size2;
            this.rowCount = size2 + 1;
            this.recommendedSectionRow = size2;
        }
        if (size != 0) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.filtersHeaderRow = i3;
            this.filtersStartRow = i4;
            int i5 = i4 + size;
            this.rowCount = i5;
            this.filtersEndRow = i5;
        } else {
            this.filtersHeaderRow = -1;
            this.filtersStartRow = -1;
            this.filtersEndRow = -1;
        }
        if (size < getMessagesController().dialogFiltersLimitPremium) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.createFilterRow = i6;
        } else {
            this.createFilterRow = -1;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.createSectionRow = i7;
        if (z && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onFragmentDestroy() {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.dialogFiltersUpdated;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().removeObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (this.orderChanged) {
            getNotificationCenter().postNotificationName(i, new Object[0]);
            getMessagesStorage().saveDialogFiltersOrder();
            TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i2).id));
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, FiltersSetupActivity$$ExternalSyntheticLambda0.INSTANCE);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Filters", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    FiltersSetupActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        AnonymousClass2 r2 = new RecyclerListView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    AndroidUtilities.runOnUIThread(new FiltersSetupActivity$2$$ExternalSyntheticLambda0(this), 250);
                }
                return super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onTouchEvent$0() {
                FiltersSetupActivity.this.getMessagesController().lockFiltersInternal();
            }
        };
        this.listView = r2;
        ((DefaultItemAnimator) r2.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new FiltersSetupActivity$$ExternalSyntheticLambda1(this, context));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(Context context, View view, int i, float f, float f2) {
        int i2 = this.filtersStartRow;
        if (i >= i2 && i < this.filtersEndRow) {
            int i3 = i - i2;
            if (!this.showAllChats) {
                i3++;
            }
            if (!getMessagesController().dialogFilters.get(i3).isDefault()) {
                if (getMessagesController().dialogFilters.get(i3).locked) {
                    showDialog(new LimitReachedBottomSheet(this, context, 3, this.currentAccount));
                } else {
                    presentFragment(new FilterCreateActivity(getMessagesController().dialogFilters.get(i3)));
                }
            }
        } else if (i != this.createFilterRow) {
        } else {
            if ((getMessagesController().dialogFilters.size() - 1 < getMessagesController().dialogFiltersLimitDefault || getUserConfig().isPremium()) && getMessagesController().dialogFilters.size() < getMessagesController().dialogFiltersLimitPremium) {
                presentFragment(new FilterCreateActivity());
            } else {
                showDialog(new LimitReachedBottomSheet(this, context, 3, this.currentAccount));
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogFiltersUpdated) {
            if (!this.ignoreUpdates) {
                int i3 = this.rowCount;
                updateRows(false);
                if (i3 != this.rowCount) {
                    this.adapter.notifyDataSetChanged();
                } else {
                    this.adapter.notifyItemRangeChanged(0, i3);
                }
            }
        } else if (i == NotificationCenter.suggestedFiltersLoaded) {
            updateRows(true);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 5 || itemViewType == 1) ? false : true;
        }

        public int getItemCount() {
            return FiltersSetupActivity.this.rowCount;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(FilterCell filterCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
            return false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$5(View view) {
            MessagesController.DialogFilter currentFilter = ((FilterCell) view.getParent()).getCurrentFilter();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
            TextPaint textPaint = new TextPaint(1);
            textPaint.setTextSize((float) AndroidUtilities.dp(20.0f));
            builder.setTitle(Emoji.replaceEmoji(currentFilter.name, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            builder.setItems(new CharSequence[]{LocaleController.getString("FilterEditItem", NUM), LocaleController.getString("FilterDeleteItem", NUM)}, new int[]{NUM, NUM}, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1(this, currentFilter));
            AlertDialog create = builder.create();
            FiltersSetupActivity.this.showDialog(create);
            create.setItemColor(1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$4(MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                if (dialogFilter.locked) {
                    FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
                    FiltersSetupActivity filtersSetupActivity2 = FiltersSetupActivity.this;
                    filtersSetupActivity.showDialog(new LimitReachedBottomSheet(filtersSetupActivity2, this.mContext, 3, filtersSetupActivity2.currentAccount));
                    return;
                }
                FiltersSetupActivity.this.presentFragment(new FilterCreateActivity(dialogFilter));
            } else if (i == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda0(this, dialogFilter));
                AlertDialog create = builder.create();
                FiltersSetupActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$3(MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            AlertDialog alertDialog;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                alertDialog = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                alertDialog.setCanCancel(false);
                alertDialog.show();
            } else {
                alertDialog = null;
            }
            TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
            tLRPC$TL_messages_updateDialogFilter.id = dialogFilter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda7(this, alertDialog, dialogFilter));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$2(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda6(this, alertDialog, dialogFilter));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$1(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            int indexOf = FiltersSetupActivity.this.getMessagesController().dialogFilters.indexOf(dialogFilter);
            if (indexOf >= 0) {
                indexOf += FiltersSetupActivity.this.filtersStartRow;
            }
            if (!FiltersSetupActivity.this.showAllChats) {
                indexOf--;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FiltersSetupActivity.this.getMessagesController().removeFilter(dialogFilter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
            boolean z = false;
            boolean unused2 = FiltersSetupActivity.this.ignoreUpdates = false;
            int access$700 = FiltersSetupActivity.this.createFilterRow;
            int access$200 = FiltersSetupActivity.this.recommendedHeaderRow;
            FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
            if (indexOf == -1) {
                z = true;
            }
            filtersSetupActivity.updateRows(z);
            if (indexOf != -1) {
                if (FiltersSetupActivity.this.filtersStartRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(indexOf - 1, 2);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(indexOf);
                }
                if (access$200 == -1 && FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeInserted(access$200, (FiltersSetupActivity.this.recommendedSectionRow - FiltersSetupActivity.this.recommendedHeaderRow) + 1);
                }
                if (access$700 == -1 && FiltersSetupActivity.this.createFilterRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.createFilterRow);
                }
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            SuggestedFilterCell suggestedFilterCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell = headerCell;
            } else if (i == 1) {
                HintInnerCell hintInnerCell = new HintInnerCell(this.mContext);
                hintInnerCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                suggestedFilterCell = hintInnerCell;
            } else if (i == 2) {
                FilterCell filterCell = new FilterCell(this.mContext);
                filterCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                filterCell.setOnReorderButtonTouchListener(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda4(this, filterCell));
                filterCell.setOnOptionsClick(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda2(this));
                suggestedFilterCell = filterCell;
            } else if (i == 3) {
                suggestedFilterCell = new ShadowSectionCell(this.mContext);
            } else if (i != 4) {
                SuggestedFilterCell suggestedFilterCell2 = new SuggestedFilterCell(this.mContext);
                suggestedFilterCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell2.setAddOnClickListener(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda3(this, suggestedFilterCell2));
                suggestedFilterCell = suggestedFilterCell2;
            } else {
                TextCell textCell = new TextCell(this.mContext);
                textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell = textCell;
            }
            return new RecyclerListView.Holder(suggestedFilterCell);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$7(SuggestedFilterCell suggestedFilterCell, View view) {
            TLRPC$TL_dialogFilterSuggested suggestedFilter = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter dialogFilter = new MessagesController.DialogFilter();
            dialogFilter.name = suggestedFilter.filter.title;
            dialogFilter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(dialogFilter.id) != null) {
                dialogFilter.id++;
            }
            dialogFilter.unreadCount = -1;
            dialogFilter.pendingUnreadCount = -1;
            int i = 0;
            while (i < 2) {
                TLRPC$DialogFilter tLRPC$DialogFilter = suggestedFilter.filter;
                ArrayList<TLRPC$InputPeer> arrayList = i == 0 ? tLRPC$DialogFilter.include_peers : tLRPC$DialogFilter.exclude_peers;
                ArrayList<Long> arrayList2 = i == 0 ? dialogFilter.alwaysShow : dialogFilter.neverShow;
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$InputPeer tLRPC$InputPeer = arrayList.get(i2);
                    long j = tLRPC$InputPeer.user_id;
                    if (j == 0) {
                        long j2 = tLRPC$InputPeer.chat_id;
                        if (j2 != 0) {
                            j = -j2;
                        } else {
                            j = -tLRPC$InputPeer.channel_id;
                        }
                    }
                    arrayList2.add(Long.valueOf(j));
                }
                i++;
            }
            TLRPC$DialogFilter tLRPC$DialogFilter2 = suggestedFilter.filter;
            if (tLRPC$DialogFilter2.groups) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (tLRPC$DialogFilter2.bots) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (tLRPC$DialogFilter2.contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (tLRPC$DialogFilter2.non_contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (tLRPC$DialogFilter2.broadcasts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (tLRPC$DialogFilter2.exclude_archived) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (tLRPC$DialogFilter2.exclude_read) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (tLRPC$DialogFilter2.exclude_muted) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, true, true, true, true, false, FiltersSetupActivity.this, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5(this, suggestedFilter));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$6(TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested) {
            FiltersSetupActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = false;
            ArrayList<TLRPC$TL_dialogFilterSuggested> arrayList = FiltersSetupActivity.this.getMessagesController().suggestedFilters;
            int indexOf = arrayList.indexOf(tLRPC$TL_dialogFilterSuggested);
            if (indexOf != -1) {
                boolean z = FiltersSetupActivity.this.filtersStartRow == -1;
                arrayList.remove(indexOf);
                int access$800 = indexOf + FiltersSetupActivity.this.recommendedStartRow;
                int access$700 = FiltersSetupActivity.this.createFilterRow;
                int access$200 = FiltersSetupActivity.this.recommendedHeaderRow;
                int access$1200 = FiltersSetupActivity.this.recommendedSectionRow;
                FiltersSetupActivity.this.updateRows(false);
                if (access$700 != -1 && FiltersSetupActivity.this.createFilterRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(access$700);
                }
                if (access$200 == -1 || FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(access$800);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(access$200, (access$1200 - access$200) + 1);
                }
                if (z) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersHeaderRow);
                }
                FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersStartRow);
                return;
            }
            FiltersSetupActivity.this.updateRows(true);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 2) {
                    FilterCell filterCell = (FilterCell) viewHolder.itemView;
                    int access$300 = i - FiltersSetupActivity.this.filtersStartRow;
                    if (!FiltersSetupActivity.this.showAllChats) {
                        access$300++;
                    }
                    filterCell.setFilter(FiltersSetupActivity.this.getMessagesController().dialogFilters.get(access$300), true);
                } else if (itemViewType != 3) {
                    if (itemViewType == 4) {
                        TextCell textCell = (TextCell) viewHolder.itemView;
                        MessagesController.getNotificationsSettings(FiltersSetupActivity.this.currentAccount);
                        if (i == FiltersSetupActivity.this.createFilterRow) {
                            Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                            Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                            textCell.setTextAndIcon(LocaleController.getString("CreateNewFilter", NUM), new CombinedDrawable(drawable, drawable2), false);
                        }
                    } else if (itemViewType == 5) {
                        SuggestedFilterCell suggestedFilterCell = (SuggestedFilterCell) viewHolder.itemView;
                        TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested = FiltersSetupActivity.this.getMessagesController().suggestedFilters.get(i - FiltersSetupActivity.this.recommendedStartRow);
                        if (FiltersSetupActivity.this.recommendedStartRow == FiltersSetupActivity.this.recommendedEndRow - 1) {
                            z = false;
                        }
                        suggestedFilterCell.setFilter(tLRPC$TL_dialogFilterSuggested, z);
                    }
                } else if (i == FiltersSetupActivity.this.createSectionRow) {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == FiltersSetupActivity.this.filtersHeaderRow) {
                    headerCell.setText(LocaleController.getString("Filters", NUM));
                } else if (i == FiltersSetupActivity.this.recommendedHeaderRow) {
                    headerCell.setText(LocaleController.getString("FilterRecommended", NUM));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == FiltersSetupActivity.this.filtersHeaderRow || i == FiltersSetupActivity.this.recommendedHeaderRow) {
                return 0;
            }
            if (i == FiltersSetupActivity.this.filterHelpRow) {
                return 1;
            }
            if (i >= FiltersSetupActivity.this.filtersStartRow && i < FiltersSetupActivity.this.filtersEndRow) {
                return 2;
            }
            if (i == FiltersSetupActivity.this.createSectionRow || i == FiltersSetupActivity.this.recommendedSectionRow) {
                return 3;
            }
            return i == FiltersSetupActivity.this.createFilterRow ? 4 : 5;
        }

        public void swapElements(int i, int i2) {
            int access$300 = i - FiltersSetupActivity.this.filtersStartRow;
            int access$3002 = i2 - FiltersSetupActivity.this.filtersStartRow;
            int access$1100 = FiltersSetupActivity.this.filtersEndRow - FiltersSetupActivity.this.filtersStartRow;
            if (!FiltersSetupActivity.this.showAllChats) {
                access$300++;
                access$3002++;
                access$1100++;
            }
            if (access$300 >= 0 && access$3002 >= 0 && access$300 < access$1100 && access$3002 < access$1100) {
                ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
                MessagesController.DialogFilter dialogFilter = arrayList.get(access$300);
                MessagesController.DialogFilter dialogFilter2 = arrayList.get(access$3002);
                int i3 = dialogFilter.order;
                dialogFilter.order = dialogFilter2.order;
                dialogFilter2.order = i3;
                arrayList.set(access$300, dialogFilter2);
                arrayList.set(access$3002, dialogFilter);
                boolean unused = FiltersSetupActivity.this.orderChanged = true;
                notifyItemMoved(i, i2);
            }
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            boolean z;
            if (!FiltersSetupActivity.this.getUserConfig().isPremium()) {
                View view = viewHolder.itemView;
                if ((view instanceof FilterCell) && ((FilterCell) view).currentFilter.isDefault()) {
                    z = false;
                    if (viewHolder.getItemViewType() == 2 || !z) {
                        return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
                    }
                    return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                }
            }
            z = true;
            if (viewHolder.getItemViewType() == 2) {
            }
            return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            boolean z;
            if (!FiltersSetupActivity.this.getUserConfig().isPremium()) {
                View view = viewHolder2.itemView;
                if ((view instanceof FilterCell) && ((FilterCell) view).currentFilter.isDefault()) {
                    z = false;
                    if (viewHolder.getItemViewType() == viewHolder2.getItemViewType() || !z) {
                        return false;
                    }
                    FiltersSetupActivity.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                    return true;
                }
            }
            z = true;
            if (viewHolder.getItemViewType() == viewHolder2.getItemViewType()) {
            }
            return false;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        return arrayList;
    }
}
