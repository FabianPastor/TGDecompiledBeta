package org.telegram.ui.Cells;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;

public class FeaturedStickerSetCell2 extends FrameLayout {
    /* access modifiers changed from: private */
    public final ProgressButton addButton;
    private final int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public final TextView delButton;
    private final BackupImageView imageView;
    /* access modifiers changed from: private */
    public boolean isInstalled;
    /* access modifiers changed from: private */
    public boolean isLocked;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC$StickerSetCovered stickersSet;
    private final TextView textView;
    /* access modifiers changed from: private */
    public final PremiumButtonView unlockButton;
    private final TextView valueTextView;

    /* access modifiers changed from: protected */
    public void onPremiumButtonClick() {
    }

    public FeaturedStickerSetCell2(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView2.setTextSize(1, 16.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        boolean z = LocaleController.isRTL;
        float f = 71.0f;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 22.0f : 71.0f, 10.0f, z ? 71.0f : 22.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        textView3.setTextSize(1, 13.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
        boolean z2 = LocaleController.isRTL;
        addView(textView3, LayoutHelper.createFrame(-2, -2.0f, z2 ? 5 : 3, z2 ? 100.0f : 71.0f, 35.0f, !z2 ? 100.0f : f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        boolean z3 = LocaleController.isRTL;
        addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (!z3 ? 3 : i) | 48, z3 ? 0.0f : 12.0f, 8.0f, z3 ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", R.string.Add));
        progressButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.delButton = textView4;
        textView4.setGravity(17);
        textView4.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
        textView4.setTextSize(1, 14.0f);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
        addView(textView4, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(4.0f), false);
        this.unlockButton = premiumButtonView;
        premiumButtonView.setIcon(R.raw.unlock_icon);
        premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new FeaturedStickerSetCell2$$ExternalSyntheticLambda0(this));
        premiumButtonView.setVisibility(8);
        try {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) premiumButtonView.getIconView().getLayoutParams();
            marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
            marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
            int dp = AndroidUtilities.dp(20.0f);
            marginLayoutParams.height = dp;
            marginLayoutParams.width = dp;
            ((ViewGroup.MarginLayoutParams) premiumButtonView.getTextView().getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
            premiumButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        } catch (Exception unused) {
        }
        addView(this.unlockButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 10.0f, 0.0f));
        updateColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onPremiumButtonClick();
    }

    public TextView getTextView() {
        return this.textView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        int measuredWidth = this.addButton.getMeasuredWidth();
        int measuredWidth2 = this.delButton.getMeasuredWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
        if (measuredWidth2 < measuredWidth) {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((measuredWidth - measuredWidth2) / 2);
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
        }
        measureChildWithMargins(this.textView, i, measuredWidth, i2, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:131:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01a2  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01c4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStickersSet(org.telegram.tgnet.TLRPC$StickerSetCovered r14, boolean r15, boolean r16, boolean r17, boolean r18) {
        /*
            r13 = this;
            r0 = r13
            r8 = r14
            r1 = r15
            android.animation.AnimatorSet r2 = r0.currentAnimation
            r3 = 0
            if (r2 == 0) goto L_0x000d
            r2.cancel()
            r0.currentAnimation = r3
        L_0x000d:
            r0.needDivider = r1
            r0.stickersSet = r8
            r9 = 1
            r1 = r1 ^ r9
            r13.setWillNotDraw(r1)
            android.widget.TextView r1 = r0.textView
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = r0.stickersSet
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            java.lang.String r2 = r2.title
            r1.setText(r2)
            r10 = 0
            if (r16 == 0) goto L_0x003a
            org.telegram.ui.Cells.FeaturedStickerSetCell2$1 r1 = new org.telegram.ui.Cells.FeaturedStickerSetCell2$1
            r1.<init>(r13)
            android.widget.TextView r2 = r0.textView
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0031
            r5 = r3
            goto L_0x0032
        L_0x0031:
            r5 = r1
        L_0x0032:
            if (r4 == 0) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            r1 = r3
        L_0x0036:
            r2.setCompoundDrawablesWithIntrinsicBounds(r5, r3, r1, r3)
            goto L_0x003f
        L_0x003a:
            android.widget.TextView r1 = r0.textView
            r1.setCompoundDrawablesWithIntrinsicBounds(r10, r10, r10, r10)
        L_0x003f:
            android.widget.TextView r1 = r0.valueTextView
            org.telegram.tgnet.TLRPC$StickerSet r2 = r8.set
            boolean r4 = r2.emojis
            if (r4 == 0) goto L_0x004a
            java.lang.String r4 = "EmojiCount"
            goto L_0x004c
        L_0x004a:
            java.lang.String r4 = "Stickers"
        L_0x004c:
            int r2 = r2.count
            java.lang.Object[] r5 = new java.lang.Object[r10]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2, r5)
            r1.setText(r2)
            org.telegram.tgnet.TLRPC$Document r1 = r8.cover
            if (r1 == 0) goto L_0x005e
        L_0x005b:
            r3 = r1
            goto L_0x00ce
        L_0x005e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r8.covers
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0095
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r8.covers
            java.lang.Object r1 = r1.get(r10)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            r2 = 0
        L_0x006f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r8.covers
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x005b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r8.covers
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC$Document) r3
            long r3 = r3.id
            org.telegram.tgnet.TLRPC$StickerSet r5 = r8.set
            long r5 = r5.thumb_document_id
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x0092
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r8.covers
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x005b
        L_0x0092:
            int r2 = r2 + 1
            goto L_0x006f
        L_0x0095:
            boolean r1 = r8 instanceof org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered
            if (r1 == 0) goto L_0x00ce
            r1 = r8
            org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered r1 = (org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered) r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r1.documents
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x00ce
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r1.documents
            java.lang.Object r2 = r1.get(r10)
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
            r3 = 0
        L_0x00ad:
            int r4 = r1.size()
            if (r3 >= r4) goto L_0x00cd
            java.lang.Object r4 = r1.get(r3)
            org.telegram.tgnet.TLRPC$Document r4 = (org.telegram.tgnet.TLRPC$Document) r4
            long r4 = r4.id
            org.telegram.tgnet.TLRPC$StickerSet r6 = r8.set
            long r6 = r6.thumb_document_id
            int r11 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r11 != 0) goto L_0x00ca
            java.lang.Object r1 = r1.get(r3)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x005b
        L_0x00ca:
            int r3 = r3 + 1
            goto L_0x00ad
        L_0x00cd:
            r3 = r2
        L_0x00ce:
            r11 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x017f
            boolean r1 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r3)
            r2 = 90
            if (r1 == 0) goto L_0x0153
            org.telegram.tgnet.TLRPC$StickerSet r1 = r8.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r2)
            if (r1 != 0) goto L_0x00e5
            r1 = r3
        L_0x00e5:
            org.telegram.tgnet.TLRPC$StickerSet r4 = r8.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.thumbs
            java.lang.String r5 = "windowBackgroundGray"
            org.telegram.messenger.SvgHelper$SvgDrawable r5 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r4, (java.lang.String) r5, (float) r11)
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Document
            if (r4 == 0) goto L_0x00fe
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r2)
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r1, (org.telegram.tgnet.TLRPC$Document) r3)
            goto L_0x0108
        L_0x00fe:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC$PhotoSize) r1
            org.telegram.tgnet.TLRPC$StickerSet r2 = r8.set
            int r2 = r2.thumb_version
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForSticker(r1, r3, r2)
        L_0x0108:
            r6 = r1
            if (r4 == 0) goto L_0x0135
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r9)
            if (r1 == 0) goto L_0x0135
            if (r5 == 0) goto L_0x0124
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r6 = 0
            java.lang.String r3 = "50_50"
            r4 = r5
            r5 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (android.graphics.drawable.Drawable) r4, (int) r5, (java.lang.Object) r6)
            goto L_0x018a
        L_0x0124:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r5 = 0
            r7 = 0
            java.lang.String r3 = "50_50"
            r4 = r6
            r6 = r7
            r7 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (int) r6, (java.lang.Object) r7)
            goto L_0x018a
        L_0x0135:
            if (r6 == 0) goto L_0x0147
            int r1 = r6.imageType
            if (r1 != r9) goto L_0x0147
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            java.lang.String r3 = "50_50"
            java.lang.String r4 = "tgs"
            r2 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x018a
        L_0x0147:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            java.lang.String r3 = "50_50"
            java.lang.String r4 = "webp"
            r2 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x018a
        L_0x0153:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r2)
            if (r1 == 0) goto L_0x016f
            org.telegram.ui.Components.BackupImageView r2 = r0.imageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r1, (org.telegram.tgnet.TLRPC$Document) r3)
            r5 = 0
            java.lang.String r4 = "50_50"
            java.lang.String r6 = "webp"
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r6
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x018a
        L_0x016f:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            r5 = 0
            java.lang.String r3 = "50_50"
            java.lang.String r4 = "webp"
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
            goto L_0x018a
        L_0x017f:
            org.telegram.ui.Components.BackupImageView r1 = r0.imageView
            r2 = 0
            r3 = 0
            r5 = 0
            java.lang.String r4 = "webp"
            r6 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6)
        L_0x018a:
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r10)
            if (r17 != 0) goto L_0x01a4
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            org.telegram.tgnet.TLRPC$StickerSet r2 = r8.set
            long r2 = r2.id
            boolean r1 = r1.isStickerPackInstalled((long) r2)
            if (r1 == 0) goto L_0x01a2
            goto L_0x01a4
        L_0x01a2:
            r1 = 0
            goto L_0x01a5
        L_0x01a4:
            r1 = 1
        L_0x01a5:
            r0.isInstalled = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isPremium()
            if (r1 != 0) goto L_0x01bb
            boolean r1 = org.telegram.messenger.MessageObject.isPremiumEmojiPack((org.telegram.tgnet.TLRPC$StickerSetCovered) r14)
            if (r1 == 0) goto L_0x01bb
            r1 = 1
            goto L_0x01bc
        L_0x01bb:
            r1 = 0
        L_0x01bc:
            r0.isLocked = r1
            r2 = 8
            r3 = 4
            r4 = 0
            if (r18 == 0) goto L_0x02e3
            if (r1 == 0) goto L_0x01d6
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setVisibility(r10)
            android.widget.TextView r1 = r0.delButton
            r1.setVisibility(r10)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r10)
            goto L_0x01ea
        L_0x01d6:
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setVisibility(r10)
            boolean r1 = r0.isInstalled
            if (r1 == 0) goto L_0x01e5
            android.widget.TextView r1 = r0.delButton
            r1.setVisibility(r10)
            goto L_0x01ea
        L_0x01e5:
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r10)
        L_0x01ea:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.currentAnimation = r1
            r5 = 250(0xfa, double:1.235E-321)
            r1.setDuration(r5)
            android.animation.AnimatorSet r1 = r0.currentAnimation
            android.animation.Animator[] r2 = new android.animation.Animator[r2]
            android.widget.TextView r5 = r0.delButton
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r9]
            boolean r8 = r0.isInstalled
            if (r8 == 0) goto L_0x020b
            boolean r8 = r0.isLocked
            if (r8 != 0) goto L_0x020b
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x020c
        L_0x020b:
            r8 = 0
        L_0x020c:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r10] = r5
            android.widget.TextView r5 = r0.delButton
            android.util.Property r6 = android.view.View.SCALE_X
            float[] r7 = new float[r9]
            boolean r8 = r0.isInstalled
            if (r8 == 0) goto L_0x0225
            boolean r8 = r0.isLocked
            if (r8 != 0) goto L_0x0225
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x0226
        L_0x0225:
            r8 = 0
        L_0x0226:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r9] = r5
            r5 = 2
            android.widget.TextView r6 = r0.delButton
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r9]
            boolean r12 = r0.isInstalled
            if (r12 == 0) goto L_0x0240
            boolean r12 = r0.isLocked
            if (r12 != 0) goto L_0x0240
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x0241
        L_0x0240:
            r12 = 0
        L_0x0241:
            r8[r10] = r12
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r2[r5] = r6
            r5 = 3
            org.telegram.ui.Components.ProgressButton r6 = r0.addButton
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r9]
            boolean r12 = r0.isInstalled
            if (r12 != 0) goto L_0x025c
            boolean r12 = r0.isLocked
            if (r12 == 0) goto L_0x0259
            goto L_0x025c
        L_0x0259:
            r12 = 1065353216(0x3var_, float:1.0)
            goto L_0x025d
        L_0x025c:
            r12 = 0
        L_0x025d:
            r8[r10] = r12
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r2[r5] = r6
            org.telegram.ui.Components.ProgressButton r5 = r0.addButton
            android.util.Property r6 = android.view.View.SCALE_X
            float[] r7 = new float[r9]
            boolean r8 = r0.isInstalled
            if (r8 != 0) goto L_0x0277
            boolean r8 = r0.isLocked
            if (r8 == 0) goto L_0x0274
            goto L_0x0277
        L_0x0274:
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x0278
        L_0x0277:
            r8 = 0
        L_0x0278:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r3] = r5
            r3 = 5
            org.telegram.ui.Components.Premium.PremiumButtonView r5 = r0.unlockButton
            android.util.Property r6 = android.view.View.SCALE_Y
            float[] r7 = new float[r9]
            boolean r8 = r0.isLocked
            if (r8 != 0) goto L_0x028d
            r8 = 0
            goto L_0x028f
        L_0x028d:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x028f:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r3] = r5
            r3 = 6
            org.telegram.ui.Components.Premium.PremiumButtonView r5 = r0.unlockButton
            android.util.Property r6 = android.view.View.SCALE_X
            float[] r7 = new float[r9]
            boolean r8 = r0.isLocked
            if (r8 != 0) goto L_0x02a4
            r8 = 0
            goto L_0x02a6
        L_0x02a4:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x02a6:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r3] = r5
            r3 = 7
            org.telegram.ui.Components.Premium.PremiumButtonView r5 = r0.unlockButton
            android.util.Property r6 = android.view.View.SCALE_Y
            float[] r7 = new float[r9]
            boolean r8 = r0.isLocked
            if (r8 != 0) goto L_0x02ba
            r11 = 0
        L_0x02ba:
            r7[r10] = r11
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r2[r3] = r4
            r1.playTogether(r2)
            android.animation.AnimatorSet r1 = r0.currentAnimation
            org.telegram.ui.Cells.FeaturedStickerSetCell2$2 r2 = new org.telegram.ui.Cells.FeaturedStickerSetCell2$2
            r2.<init>()
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r0.currentAnimation
            android.view.animation.OvershootInterpolator r2 = new android.view.animation.OvershootInterpolator
            r3 = 1065520988(0x3var_f5c, float:1.02)
            r2.<init>(r3)
            r1.setInterpolator(r2)
            android.animation.AnimatorSet r1 = r0.currentAnimation
            r1.start()
            goto L_0x038b
        L_0x02e3:
            if (r1 == 0) goto L_0x0322
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setVisibility(r10)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setAlpha(r11)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setScaleX(r11)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setScaleY(r11)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r3)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setAlpha(r4)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleX(r4)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleY(r4)
            android.widget.TextView r1 = r0.delButton
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.delButton
            r1.setAlpha(r4)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleX(r4)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleY(r4)
            goto L_0x038b
        L_0x0322:
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setVisibility(r2)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setAlpha(r4)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setScaleX(r4)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.unlockButton
            r1.setScaleY(r4)
            boolean r1 = r0.isInstalled
            if (r1 == 0) goto L_0x0363
            android.widget.TextView r1 = r0.delButton
            r1.setVisibility(r10)
            android.widget.TextView r1 = r0.delButton
            r1.setAlpha(r11)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleX(r11)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleY(r11)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r3)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setAlpha(r4)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleX(r4)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleY(r4)
            goto L_0x038b
        L_0x0363:
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setVisibility(r10)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setAlpha(r11)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleX(r11)
            org.telegram.ui.Components.ProgressButton r1 = r0.addButton
            r1.setScaleY(r11)
            android.widget.TextView r1 = r0.delButton
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.delButton
            r1.setAlpha(r4)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleX(r4)
            android.widget.TextView r1 = r0.delButton
            r1.setScaleY(r4)
        L_0x038b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.FeaturedStickerSetCell2.setStickersSet(org.telegram.tgnet.TLRPC$StickerSetCovered, boolean, boolean, boolean, boolean):void");
    }

    public TLRPC$StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
        this.delButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean z, boolean z2) {
        this.addButton.setDrawProgress(z, z2);
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(71.0f), (float) (getHeight() - 1), (float) (getWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void updateColors() {
        this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
    }

    public static void createThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        List<ThemeDescription> list2 = list;
        list2.add(new ThemeDescription((View) recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        RecyclerListView recyclerListView2 = recyclerListView;
        list2.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        RecyclerListView recyclerListView3 = recyclerListView;
        list2.add(new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        list2.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_removeButtonText"));
        list2.add(new ThemeDescription(recyclerListView3, 0, new Class[]{FeaturedStickerSetCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        list2.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_buttonProgress"));
        list2.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_addButtonPressed"));
    }
}
