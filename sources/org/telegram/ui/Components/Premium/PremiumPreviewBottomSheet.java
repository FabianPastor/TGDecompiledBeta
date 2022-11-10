package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class PremiumPreviewBottomSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    boolean animateConfetti;
    FrameLayout bulletinContainer;
    FrameLayout buttonContainer;
    int buttonRow;
    int[] coords;
    int currentAccount;
    PremiumFeatureCell dummyCell;
    ValueAnimator enterAnimator;
    boolean enterTransitionInProgress;
    float enterTransitionProgress;
    int featuresEndRow;
    int featuresStartRow;
    FireworksOverlay fireworksOverlay;
    BaseFragment fragment;
    GiftPremiumBottomSheet.GiftTier giftTier;
    PremiumGradient.GradientTools gradientTools;
    int helpUsRow;
    ViewGroup iconContainer;
    GLIconTextureView iconTextureView;
    public boolean isEmojiStatus;
    boolean isOutboundGift;
    public View overrideTitleIcon;
    int paddingRow;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures;
    int rowCount;
    int sectionRow;
    StarParticlesView starParticlesView;
    public float startEnterFromScale;
    public SimpleTextView startEnterFromView;
    public float startEnterFromX;
    public float startEnterFromX1;
    public float startEnterFromY;
    public float startEnterFromY1;
    public TLRPC$InputStickerSet statusStickerSet;
    private TextView subtitleView;
    private LinkSpanDrawable.LinksTextView[] titleView;
    private FrameLayout titleViewContainer;
    int totalGradientHeight;
    TLRPC$User user;

    public PremiumPreviewBottomSheet(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User, Theme.ResourcesProvider resourcesProvider) {
        this(baseFragment, i, tLRPC$User, null, resourcesProvider);
    }

    public PremiumPreviewBottomSheet(final BaseFragment baseFragment, final int i, TLRPC$User tLRPC$User, GiftPremiumBottomSheet.GiftTier giftTier, Theme.ResourcesProvider resourcesProvider) {
        super(baseFragment, false, false, resourcesProvider);
        this.premiumFeatures = new ArrayList<>();
        this.coords = new int[2];
        this.enterTransitionProgress = 0.0f;
        fixNavigationBar();
        this.fragment = baseFragment;
        this.topPadding = 0.26f;
        this.user = tLRPC$User;
        this.currentAccount = i;
        this.giftTier = giftTier;
        this.dummyCell = new PremiumFeatureCell(getContext());
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, i);
        if (this.giftTier != null || UserConfig.getInstance(i).isPremium()) {
            this.buttonContainer.setVisibility(8);
        }
        PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools;
        gradientTools.exactly = true;
        gradientTools.x1 = 0.0f;
        gradientTools.y1 = 1.0f;
        gradientTools.x2 = 0.0f;
        gradientTools.y2 = 0.0f;
        gradientTools.cx = 0.0f;
        gradientTools.cy = 0.0f;
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.paddingRow = i2;
        this.featuresStartRow = i3;
        int size = i3 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        this.rowCount = size + 1;
        this.sectionRow = size;
        if (!UserConfig.getInstance(i).isPremium() && giftTier == null) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.buttonRow = i4;
        }
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i5) {
                PremiumPreviewBottomSheet.this.lambda$new$0(i, baseFragment, view, i5);
            }
        });
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
        PremiumPreviewFragment.sentShowScreenStat("profile");
        FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
        this.fireworksOverlay = fireworksOverlay;
        this.container.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.bulletinContainer = frameLayout;
        this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 140, 87));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, BaseFragment baseFragment, View view, int i2) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            PremiumPreviewFragment.sentShowFeaturePreview(i, premiumFeatureCell.data.type);
            showDialog(new PremiumFeatureBottomSheet(baseFragment, premiumFeatureCell.data.type, false));
        }
    }

    public PremiumPreviewBottomSheet setOutboundGift(boolean z) {
        this.isOutboundGift = z;
        return this;
    }

    public PremiumPreviewBottomSheet setAnimateConfetti(boolean z) {
        this.animateConfetti = z;
        return this;
    }

    private void showDialog(Dialog dialog) {
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setDialogVisible(true);
        }
        this.starParticlesView.setPaused(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PremiumPreviewBottomSheet.this.lambda$showDialog$1(dialogInterface);
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$1(DialogInterface dialogInterface) {
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setDialogVisible(false);
        }
        this.starParticlesView.setPaused(false);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        this.currentAccount = UserConfig.selectedAccount;
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount, null), new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PremiumPreviewBottomSheet.this.lambda$onViewCreated$2(view);
            }
        });
        this.buttonContainer = new FrameLayout(getContext());
        View view = new View(getContext());
        view.setBackgroundColor(getThemedColor("divider"));
        this.buttonContainer.addView(view, LayoutHelper.createFrame(-1, 1.0f));
        view.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(view, true, 1.0f, false);
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.buttonContainer.addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
            frameLayout.addView(this.buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$2(View view) {
        PremiumPreviewFragment.sentPremiumButtonClick();
        PremiumPreviewFragment.buyPremium(this.fragment, "profile");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.container.getLocationOnScreen(this.coords);
    }

    private void titleLoaded(CharSequence charSequence, boolean z) {
        LinkSpanDrawable.LinksTextView[] linksTextViewArr = this.titleView;
        if (linksTextViewArr == null) {
            return;
        }
        linksTextViewArr[1].setText(charSequence);
        if (this.titleView[1].getVisibility() == 0) {
            return;
        }
        if (z) {
            this.titleView[1].setAlpha(0.0f);
            this.titleView[1].setVisibility(0);
            ViewPropertyAnimator alpha = this.titleView[1].animate().alpha(1.0f);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            alpha.setInterpolator(cubicBezierInterpolator).setDuration(200L).start();
            this.titleView[0].animate().alpha(0.0f).setInterpolator(cubicBezierInterpolator).setDuration(200L).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    PremiumPreviewBottomSheet.this.lambda$titleLoaded$3();
                }
            }).start();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PremiumPreviewBottomSheet.this.lambda$titleLoaded$4(valueAnimator);
                }
            });
            ofFloat.setInterpolator(cubicBezierInterpolator);
            ofFloat.setDuration(200L);
            ofFloat.start();
            return;
        }
        this.titleView[1].setAlpha(1.0f);
        this.titleView[1].setVisibility(0);
        this.titleView[0].setAlpha(0.0f);
        this.titleView[0].setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$titleLoaded$3() {
        this.titleView[0].setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$titleLoaded$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.titleViewContainer.getLayoutParams().height = AndroidUtilities.lerp(this.titleView[0].getHeight(), this.titleView[1].getHeight(), floatValue);
        this.titleViewContainer.requestLayout();
    }

    public void setTitle(boolean z) {
        TLRPC$Document tLRPC$Document;
        SpannableStringBuilder spannableStringBuilder;
        LinkSpanDrawable.LinksTextView[] linksTextViewArr = this.titleView;
        if (linksTextViewArr == null || this.subtitleView == null) {
            return;
        }
        if (this.statusStickerSet != null) {
            int i = R.string.TelegramPremiumUserStatusDialogTitle;
            TLRPC$User tLRPC$User = this.user;
            CharSequence replaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.formatString(i, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), "<STICKERSET>"), "windowBackgroundWhiteBlueButton", 0, null);
            SpannableStringBuilder spannableStringBuilder2 = replaceSingleTag instanceof SpannableStringBuilder ? (SpannableStringBuilder) replaceSingleTag : new SpannableStringBuilder(replaceSingleTag);
            int indexOf = replaceSingleTag.toString().indexOf("<STICKERSET>");
            if (indexOf >= 0) {
                TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.statusStickerSet, false);
                if (stickerSet == null || stickerSet.documents.isEmpty()) {
                    tLRPC$Document = null;
                } else {
                    tLRPC$Document = stickerSet.documents.get(0);
                    if (stickerSet.set != null) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= stickerSet.documents.size()) {
                                break;
                            } else if (stickerSet.documents.get(i2).id == stickerSet.set.thumb_document_id) {
                                tLRPC$Document = stickerSet.documents.get(i2);
                                break;
                            } else {
                                i2++;
                            }
                        }
                    }
                }
                if (tLRPC$Document != null) {
                    spannableStringBuilder = new SpannableStringBuilder("x");
                    spannableStringBuilder.setSpan(new AnimatedEmojiSpan(tLRPC$Document, this.titleView[0].getPaint().getFontMetricsInt()), 0, spannableStringBuilder.length(), 33);
                    if (stickerSet != null && stickerSet.set != null) {
                        spannableStringBuilder.append((CharSequence) " ").append((CharSequence) stickerSet.set.title);
                    }
                } else {
                    spannableStringBuilder = new SpannableStringBuilder("xxxxxx");
                    spannableStringBuilder.setSpan(new LoadingSpan(this.titleView[0], AndroidUtilities.dp(100.0f)), 0, spannableStringBuilder.length(), 33);
                }
                spannableStringBuilder2.replace(indexOf, indexOf + 12, (CharSequence) spannableStringBuilder);
                spannableStringBuilder2.setSpan(new ClickableSpan(this) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.1
                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                    }

                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(false);
                    }
                }, indexOf, spannableStringBuilder.length() + indexOf, 33);
                this.titleView[1].setOnLinkPressListener(new LinkSpanDrawable.LinksTextView.OnLinkPress() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda6
                    @Override // org.telegram.ui.Components.LinkSpanDrawable.LinksTextView.OnLinkPress
                    public final void run(ClickableSpan clickableSpan) {
                        PremiumPreviewBottomSheet.this.lambda$setTitle$5(clickableSpan);
                    }
                });
                if (tLRPC$Document != null) {
                    titleLoaded(spannableStringBuilder2, z);
                } else {
                    this.titleView[0].setText(spannableStringBuilder2, (TextView.BufferType) null);
                }
            }
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserStatusDialogSubtitle)));
        } else if (this.isEmojiStatus) {
            LinkSpanDrawable.LinksTextView linksTextView = linksTextViewArr[0];
            int i3 = R.string.TelegramPremiumUserStatusDefaultDialogTitle;
            TLRPC$User tLRPC$User2 = this.user;
            linksTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(i3, ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name))));
            TextView textView = this.subtitleView;
            int i4 = R.string.TelegramPremiumUserStatusDialogSubtitle;
            TLRPC$User tLRPC$User3 = this.user;
            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(i4, ContactsController.formatName(tLRPC$User3.first_name, tLRPC$User3.last_name))));
        } else {
            GiftPremiumBottomSheet.GiftTier giftTier = this.giftTier;
            if (giftTier != null) {
                String str = "";
                if (this.isOutboundGift) {
                    LinkSpanDrawable.LinksTextView linksTextView2 = linksTextViewArr[0];
                    int i5 = R.string.TelegramPremiumUserGiftedPremiumOutboundDialogTitleWithPlural;
                    Object[] objArr = new Object[2];
                    TLRPC$User tLRPC$User4 = this.user;
                    objArr[0] = tLRPC$User4 != null ? tLRPC$User4.first_name : str;
                    objArr[1] = LocaleController.formatPluralString("GiftMonths", giftTier.getMonths(), new Object[0]);
                    linksTextView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i5, objArr), "windowBackgroundWhiteBlueButton", 0, null));
                    TextView textView2 = this.subtitleView;
                    int i6 = R.string.TelegramPremiumUserGiftedPremiumOutboundDialogSubtitle;
                    Object[] objArr2 = new Object[1];
                    TLRPC$User tLRPC$User5 = this.user;
                    if (tLRPC$User5 != null) {
                        str = tLRPC$User5.first_name;
                    }
                    objArr2[0] = str;
                    textView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i6, objArr2), "windowBackgroundWhiteBlueButton", 0, null));
                    return;
                }
                LinkSpanDrawable.LinksTextView linksTextView3 = linksTextViewArr[0];
                int i7 = R.string.TelegramPremiumUserGiftedPremiumDialogTitleWithPlural;
                Object[] objArr3 = new Object[2];
                TLRPC$User tLRPC$User6 = this.user;
                if (tLRPC$User6 != null) {
                    str = tLRPC$User6.first_name;
                }
                objArr3[0] = str;
                objArr3[1] = LocaleController.formatPluralString("GiftMonths", giftTier.getMonths(), new Object[0]);
                linksTextView3.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i7, objArr3), "windowBackgroundWhiteBlueButton", 0, null));
                this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserGiftedPremiumDialogSubtitle)));
                return;
            }
            LinkSpanDrawable.LinksTextView linksTextView4 = linksTextViewArr[0];
            int i8 = R.string.TelegramPremiumUserDialogTitle;
            TLRPC$User tLRPC$User7 = this.user;
            linksTextView4.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i8, ContactsController.formatName(tLRPC$User7.first_name, tLRPC$User7.last_name)), "windowBackgroundWhiteBlueButton", 0, null));
            this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserDialogSubtitle)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTitle$5(ClickableSpan clickableSpan) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.statusStickerSet);
        BaseFragment baseFragment = new BaseFragment() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.2
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public Activity getParentActivity() {
                BaseFragment baseFragment2 = PremiumPreviewBottomSheet.this.fragment;
                if (baseFragment2 == null) {
                    return null;
                }
                return baseFragment2.getParentActivity();
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public int getCurrentAccount() {
                return this.currentAccount;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public FrameLayout getLayoutContainer() {
                return PremiumPreviewBottomSheet.this.bulletinContainer;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public View getFragmentView() {
                return ((BottomSheet) PremiumPreviewBottomSheet.this).containerView;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public Dialog showDialog(Dialog dialog) {
                dialog.show();
                return dialog;
            }
        };
        BaseFragment baseFragment2 = this.fragment;
        if (baseFragment2 != null) {
            baseFragment.setParentFragment(baseFragment2);
        }
        new EmojiPacksAlert(baseFragment, getContext(), this.resourcesProvider, arrayList) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.3
            @Override // org.telegram.ui.Components.EmojiPacksAlert
            protected void onCloseByLink() {
                PremiumPreviewBottomSheet.this.dismiss();
            }
        }.show();
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected CharSequence getTitle() {
        return LocaleController.getString("TelegramPremium", R.string.TelegramPremium);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected RecyclerListView.SelectionAdapter createAdapter() {
        return new Adapter();
    }

    /* loaded from: classes3.dex */
    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder moNUMonCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 0) {
                LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.1
                    @Override // android.view.ViewGroup
                    protected boolean drawChild(Canvas canvas, View view2, long j) {
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        if (view2 != premiumPreviewBottomSheet.iconTextureView || !premiumPreviewBottomSheet.enterTransitionInProgress) {
                            return super.drawChild(canvas, view2, j);
                        }
                        return true;
                    }
                };
                PremiumPreviewBottomSheet.this.iconContainer = linearLayout;
                linearLayout.setOrientation(1);
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                View view2 = premiumPreviewBottomSheet.overrideTitleIcon;
                if (view2 == null) {
                    premiumPreviewBottomSheet.iconTextureView = new GLIconTextureView(this, context, 1) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.2
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView, android.view.TextureView, android.view.View
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            setPaused(false);
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView, android.view.View
                        public void onDetachedFromWindow() {
                            super.onDetachedFromWindow();
                            setPaused(true);
                        }
                    };
                    Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    new Canvas(createBitmap).drawColor(ColorUtils.blendARGB(PremiumPreviewBottomSheet.this.getThemedColor("premiumGradient2"), PremiumPreviewBottomSheet.this.getThemedColor("dialogBackground"), 0.5f));
                    PremiumPreviewBottomSheet.this.iconTextureView.setBackgroundBitmap(createBitmap);
                    GLIconRenderer gLIconRenderer = PremiumPreviewBottomSheet.this.iconTextureView.mRenderer;
                    gLIconRenderer.colorKey1 = "premiumGradient2";
                    gLIconRenderer.colorKey2 = "premiumGradient1";
                    gLIconRenderer.updateColors();
                    linearLayout.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                } else {
                    if (view2.getParent() != null) {
                        ((ViewGroup) PremiumPreviewBottomSheet.this.overrideTitleIcon.getParent()).removeView(PremiumPreviewBottomSheet.this.overrideTitleIcon);
                    }
                    linearLayout.addView(PremiumPreviewBottomSheet.this.overrideTitleIcon, LayoutHelper.createLinear(140, 140, 1.0f, 17, 10, 10, 10, 10));
                }
                if (PremiumPreviewBottomSheet.this.titleViewContainer == null) {
                    PremiumPreviewBottomSheet.this.titleViewContainer = new FrameLayout(context);
                    PremiumPreviewBottomSheet.this.titleViewContainer.setClipChildren(false);
                    final PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ColorUtils.setAlphaComponent(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"), 178), PorterDuff.Mode.MULTIPLY);
                    PremiumPreviewBottomSheet.this.titleView = new LinkSpanDrawable.LinksTextView[2];
                    int i2 = 0;
                    while (i2 < 2) {
                        PremiumPreviewBottomSheet.this.titleView[i2] = new LinkSpanDrawable.LinksTextView(this, context, ((BottomSheet) PremiumPreviewBottomSheet.this).resourcesProvider) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.3
                            private Layout lastLayout;
                            AnimatedEmojiSpan.EmojiGroupedSpans stack;

                            @Override // android.view.View
                            protected void onDetachedFromWindow() {
                                super.onDetachedFromWindow();
                                AnimatedEmojiSpan.release(this, this.stack);
                                this.lastLayout = null;
                            }

                            @Override // android.view.View
                            protected void dispatchDraw(Canvas canvas) {
                                super.dispatchDraw(canvas);
                                if (this.lastLayout != getLayout()) {
                                    AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.stack;
                                    Layout layout = getLayout();
                                    this.lastLayout = layout;
                                    this.stack = AnimatedEmojiSpan.update(3, this, emojiGroupedSpans, layout);
                                }
                                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.stack, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f, porterDuffColorFilter);
                            }

                            @Override // android.widget.TextView, android.view.View
                            protected void onMeasure(int i3, int i4) {
                                super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(99999999, Integer.MIN_VALUE));
                            }
                        };
                        PremiumPreviewBottomSheet.this.titleView[i2].setVisibility(i2 == 0 ? 0 : 8);
                        PremiumPreviewBottomSheet.this.titleView[i2].setTextSize(1, 16.0f);
                        PremiumPreviewBottomSheet.this.titleView[i2].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        PremiumPreviewBottomSheet.this.titleView[i2].setGravity(1);
                        PremiumPreviewBottomSheet.this.titleView[i2].setTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteBlackText"));
                        PremiumPreviewBottomSheet.this.titleView[i2].setLinkTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"));
                        PremiumPreviewBottomSheet.this.titleViewContainer.addView(PremiumPreviewBottomSheet.this.titleView[i2], LayoutHelper.createFrame(-1, -2.0f));
                        i2++;
                    }
                }
                if (PremiumPreviewBottomSheet.this.titleViewContainer.getParent() != null) {
                    ((ViewGroup) PremiumPreviewBottomSheet.this.titleViewContainer.getParent()).removeView(PremiumPreviewBottomSheet.this.titleViewContainer);
                }
                linearLayout.addView(PremiumPreviewBottomSheet.this.titleViewContainer, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                if (PremiumPreviewBottomSheet.this.subtitleView == null) {
                    PremiumPreviewBottomSheet.this.subtitleView = new TextView(context);
                    PremiumPreviewBottomSheet.this.subtitleView.setTextSize(1, 14.0f);
                    PremiumPreviewBottomSheet.this.subtitleView.setGravity(1);
                    PremiumPreviewBottomSheet.this.subtitleView.setTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteBlackText"));
                    PremiumPreviewBottomSheet.this.subtitleView.setLinkTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"));
                }
                if (PremiumPreviewBottomSheet.this.subtitleView.getParent() != null) {
                    ((ViewGroup) PremiumPreviewBottomSheet.this.subtitleView.getParent()).removeView(PremiumPreviewBottomSheet.this.subtitleView);
                }
                linearLayout.addView(PremiumPreviewBottomSheet.this.subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                PremiumPreviewBottomSheet.this.setTitle(false);
                PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(this, context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.4
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // org.telegram.ui.Components.Premium.StarParticlesView, android.view.View
                    public void onMeasure(int i3, int i4) {
                        super.onMeasure(i3, i4);
                        this.drawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(52.0f));
                    }
                };
                FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.5
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int i3, int i4) {
                        float f;
                        float top;
                        int measuredHeight;
                        StarParticlesView starParticlesView;
                        super.onMeasure(i3, i4);
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet2 = PremiumPreviewBottomSheet.this;
                        GLIconTextureView gLIconTextureView = premiumPreviewBottomSheet2.iconTextureView;
                        if (gLIconTextureView != null) {
                            top = gLIconTextureView.getTop();
                            measuredHeight = PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight();
                        } else {
                            View view3 = premiumPreviewBottomSheet2.overrideTitleIcon;
                            if (view3 != null) {
                                top = view3.getTop();
                                measuredHeight = PremiumPreviewBottomSheet.this.overrideTitleIcon.getMeasuredHeight();
                            } else {
                                f = 0.0f;
                                PremiumPreviewBottomSheet.this.starParticlesView.setTranslationY(f - (starParticlesView.getMeasuredHeight() / 2.0f));
                            }
                        }
                        f = top + (measuredHeight / 2.0f);
                        PremiumPreviewBottomSheet.this.starParticlesView.setTranslationY(f - (starParticlesView.getMeasuredHeight() / 2.0f));
                    }
                };
                frameLayout.setClipChildren(false);
                frameLayout.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                frameLayout.addView(linearLayout);
                StarParticlesView.Drawable drawable = PremiumPreviewBottomSheet.this.starParticlesView.drawable;
                drawable.useGradient = true;
                drawable.useBlur = false;
                drawable.forceMaxAlpha = true;
                drawable.checkBounds = true;
                drawable.init();
                PremiumPreviewBottomSheet premiumPreviewBottomSheet2 = PremiumPreviewBottomSheet.this;
                GLIconTextureView gLIconTextureView = premiumPreviewBottomSheet2.iconTextureView;
                if (gLIconTextureView != null) {
                    gLIconTextureView.setStarParticlesView(premiumPreviewBottomSheet2.starParticlesView);
                }
                view = frameLayout;
            } else if (i == 2) {
                view = new ShadowSectionCell(context, 12, PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundGray"));
            } else if (i == 3) {
                view = new View(this, context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.7
                    @Override // android.view.View
                    protected void onMeasure(int i3, int i4) {
                        super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), NUM));
                    }
                };
            } else if (i != 4) {
                view = new PremiumFeatureCell(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.6
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // org.telegram.ui.PremiumFeatureCell, android.view.ViewGroup, android.view.View
                    public void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(this.imageView.getLeft(), this.imageView.getTop(), this.imageView.getRight(), this.imageView.getBottom());
                        PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, -this.data.yOffset);
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                        super.dispatchDraw(canvas);
                    }
                };
            } else {
                view = new AboutPremiumView(context);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            int i2 = premiumPreviewBottomSheet.featuresStartRow;
            if (i < i2 || i >= premiumPreviewBottomSheet.featuresEndRow) {
                return;
            }
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder.itemView;
            PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = premiumPreviewBottomSheet.premiumFeatures.get(i - i2);
            boolean z = true;
            if (i == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                z = false;
            }
            premiumFeatureCell.setData(premiumFeatureData, z);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            if (i == premiumPreviewBottomSheet.paddingRow) {
                return 0;
            }
            if (i >= premiumPreviewBottomSheet.featuresStartRow && i < premiumPreviewBottomSheet.featuresEndRow) {
                return 1;
            }
            if (i == premiumPreviewBottomSheet.sectionRow) {
                return 2;
            }
            if (i == premiumPreviewBottomSheet.buttonRow) {
                return 3;
            }
            if (i != premiumPreviewBottomSheet.helpUsRow) {
                return super.getItemViewType(i);
            }
            return 4;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }
    }

    private void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.premiumFeatures.size(); i4++) {
            this.dummyCell.setData(this.premiumFeatures.get(i4), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.premiumFeatures.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
        if (this.animateConfetti) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    PremiumPreviewBottomSheet.this.lambda$show$6();
                }
            }, 200L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$6() {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        this.fireworksOverlay.start();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.fireworksOverlay.isStarted()) {
            this.fireworksOverlay.animate().alpha(0.0f).setDuration(150L).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void mainContainerDispatchDraw(Canvas canvas) {
        View view = this.overrideTitleIcon;
        if (view != null) {
            view.setVisibility(this.enterTransitionInProgress ? 4 : 0);
        }
        super.mainContainerDispatchDraw(canvas);
        if (this.startEnterFromView == null || !this.enterTransitionInProgress) {
            return;
        }
        View view2 = this.overrideTitleIcon;
        View view3 = view2 == null ? this.iconTextureView : view2;
        if (view3 == view2) {
            view2.setVisibility(0);
        }
        canvas.save();
        float[] fArr = {this.startEnterFromX, this.startEnterFromY};
        this.startEnterFromView.getMatrix().mapPoints(fArr);
        Drawable rightDrawable = this.startEnterFromView.getRightDrawable();
        int[] iArr = this.coords;
        float f = (-iArr[0]) + this.startEnterFromX1 + fArr[0];
        float f2 = (-iArr[1]) + this.startEnterFromY1 + fArr[1];
        if (AndroidUtilities.isTablet()) {
            ViewGroup view4 = this.fragment.getParentLayout().getView();
            f += view4.getX() + view4.getPaddingLeft();
            f2 += view4.getY() + view4.getPaddingTop();
        }
        float intrinsicWidth = this.startEnterFromScale * rightDrawable.getIntrinsicWidth();
        float measuredHeight = view3.getMeasuredHeight() * 0.8f;
        float f3 = measuredHeight / intrinsicWidth;
        float f4 = intrinsicWidth / measuredHeight;
        float measuredWidth = view3.getMeasuredWidth() / 2.0f;
        for (View view5 = view3; view5 != this.container && view5 != null; view5 = (View) view5.getParent()) {
            measuredWidth += view5.getX();
        }
        float y = view3.getY() + 0.0f + ((View) view3.getParent()).getY() + ((View) view3.getParent().getParent()).getY() + (view3.getMeasuredHeight() / 2.0f);
        float lerp = AndroidUtilities.lerp(f, measuredWidth, CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.enterTransitionProgress));
        float lerp2 = AndroidUtilities.lerp(f2, y, this.enterTransitionProgress);
        float f5 = this.startEnterFromScale;
        float f6 = this.enterTransitionProgress;
        float f7 = (f5 * (1.0f - f6)) + (f3 * f6);
        canvas.save();
        canvas.scale(f7, f7, lerp, lerp2);
        int i = (int) lerp;
        int i2 = (int) lerp2;
        rightDrawable.setBounds(i - (rightDrawable.getIntrinsicWidth() / 2), i2 - (rightDrawable.getIntrinsicHeight() / 2), i + (rightDrawable.getIntrinsicWidth() / 2), i2 + (rightDrawable.getIntrinsicHeight() / 2));
        rightDrawable.setAlpha((int) ((1.0f - Utilities.clamp(this.enterTransitionProgress, 1.0f, 0.0f)) * 255.0f));
        rightDrawable.draw(canvas);
        rightDrawable.setAlpha(0);
        canvas.restore();
        float lerp3 = AndroidUtilities.lerp(f4, 1.0f, this.enterTransitionProgress);
        canvas.scale(lerp3, lerp3, lerp, lerp2);
        canvas.translate(lerp - (view3.getMeasuredWidth() / 2.0f), lerp2 - (view3.getMeasuredHeight() / 2.0f));
        view3.draw(canvas);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        if (this.startEnterFromView == null) {
            return false;
        }
        this.enterAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterTransitionProgress = 0.0f;
        this.enterTransitionInProgress = true;
        this.iconContainer.invalidate();
        this.startEnterFromView.getRightDrawable().setAlpha(0);
        this.startEnterFromView.invalidate();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.startEnterAnimation(-360, 100L);
        }
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PremiumPreviewBottomSheet.this.lambda$onCustomOpenAnimation$7(valueAnimator);
            }
        });
        this.enterAnimator.addListener(new AnonymousClass4());
        this.enterAnimator.setDuration(600L);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$7(ValueAnimator valueAnimator) {
        this.enterTransitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$4  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass4 extends AnimatorListenerAdapter {
        AnonymousClass4() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            premiumPreviewBottomSheet.enterTransitionInProgress = false;
            premiumPreviewBottomSheet.enterTransitionProgress = 1.0f;
            premiumPreviewBottomSheet.iconContainer.invalidate();
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 255);
            final Drawable rightDrawable = PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable();
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$4$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PremiumPreviewBottomSheet.AnonymousClass4.this.lambda$onAnimationEnd$0(rightDrawable, valueAnimator);
                }
            });
            ofInt.start();
            super.onAnimationEnd(animator);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0(Drawable drawable, ValueAnimator valueAnimator) {
            drawable.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
            PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
        }
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        if (i == NotificationCenter.groupStickersDidLoad && (tLRPC$InputStickerSet = this.statusStickerSet) != null && tLRPC$InputStickerSet.id == ((Long) objArr[0]).longValue()) {
            setTitle(true);
        }
    }
}
