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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
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
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;

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
    /* access modifiers changed from: private */
    public TextView subtitleView;
    /* access modifiers changed from: private */
    public LinkSpanDrawable.LinksTextView titleView;
    int totalGradientHeight;
    TLRPC$User user;

    public PremiumPreviewBottomSheet(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User, Theme.ResourcesProvider resourcesProvider) {
        this(baseFragment, i, tLRPC$User, (GiftPremiumBottomSheet.GiftTier) null, resourcesProvider);
    }

    public PremiumPreviewBottomSheet(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User, GiftPremiumBottomSheet.GiftTier giftTier2, Theme.ResourcesProvider resourcesProvider) {
        super(baseFragment, false, false, resourcesProvider);
        this.premiumFeatures = new ArrayList<>();
        this.coords = new int[2];
        this.enterTransitionProgress = 0.0f;
        fixNavigationBar();
        this.fragment = baseFragment;
        this.topPadding = 0.26f;
        this.user = tLRPC$User;
        this.currentAccount = i;
        this.giftTier = giftTier2;
        this.dummyCell = new PremiumFeatureCell(getContext());
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, i);
        if (this.giftTier != null || UserConfig.getInstance(i).isPremium()) {
            this.buttonContainer.setVisibility(8);
        }
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools2;
        gradientTools2.exactly = true;
        gradientTools2.x1 = 0.0f;
        gradientTools2.y1 = 1.0f;
        gradientTools2.x2 = 0.0f;
        gradientTools2.y2 = 0.0f;
        gradientTools2.cx = 0.0f;
        gradientTools2.cy = 0.0f;
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
        if (!UserConfig.getInstance(i).isPremium() && giftTier2 == null) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.buttonRow = i4;
        }
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PremiumPreviewBottomSheet$$ExternalSyntheticLambda5(this, i, baseFragment));
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
        PremiumPreviewFragment.sentShowScreenStat("profile");
        FireworksOverlay fireworksOverlay2 = new FireworksOverlay(getContext());
        this.fireworksOverlay = fireworksOverlay2;
        this.container.addView(fireworksOverlay2, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.bulletinContainer = frameLayout;
        this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 140, 87));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, BaseFragment baseFragment, View view, int i2) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            PremiumPreviewFragment.sentShowFeaturePreview(i, premiumFeatureCell.data.type);
            int i3 = premiumFeatureCell.data.type;
            if (i3 == 0) {
                showDialog(new DoubledLimitsBottomSheet(baseFragment, i));
            } else {
                showDialog(new PremiumFeatureBottomSheet(baseFragment, i3, false));
            }
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
        dialog.setOnDismissListener(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda1(this));
        dialog.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$1(DialogInterface dialogInterface) {
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setDialogVisible(false);
        }
        this.starParticlesView.setPaused(false);
    }

    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        this.currentAccount = UserConfig.selectedAccount;
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount, (PremiumPreviewFragment.SubscriptionTier) null), new PremiumPreviewBottomSheet$$ExternalSyntheticLambda2(this));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$2(View view) {
        PremiumPreviewFragment.sentPremiumButtonClick();
        PremiumPreviewFragment.buyPremium(this.fragment, "profile");
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.container.getLocationOnScreen(this.coords);
    }

    public void setTitle() {
        TLRPC$Document tLRPC$Document;
        SpannableStringBuilder spannableStringBuilder;
        LinkSpanDrawable.LinksTextView linksTextView = this.titleView;
        if (linksTextView != null && this.subtitleView != null) {
            if (this.statusStickerSet != null) {
                int i = R.string.TelegramPremiumUserStatusDialogTitle;
                TLRPC$User tLRPC$User = this.user;
                CharSequence replaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.formatString(i, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name), "<STICKERSET>"), "windowBackgroundWhiteBlueButton", (Runnable) null);
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
                        spannableStringBuilder.setSpan(new AnimatedEmojiSpan(tLRPC$Document, this.titleView.getPaint().getFontMetricsInt()), 0, spannableStringBuilder.length(), 33);
                        if (!(stickerSet == null || stickerSet.set == null)) {
                            spannableStringBuilder.append(" ").append(stickerSet.set.title);
                        }
                    } else {
                        spannableStringBuilder = new SpannableStringBuilder("xxxxxx");
                        spannableStringBuilder.setSpan(new LoadingSpan(this.titleView, AndroidUtilities.dp(100.0f)), 0, spannableStringBuilder.length(), 33);
                    }
                    spannableStringBuilder2.replace(indexOf, indexOf + 12, spannableStringBuilder);
                    spannableStringBuilder2.setSpan(new ClickableSpan(this) {
                        public void onClick(View view) {
                        }

                        public void updateDrawState(TextPaint textPaint) {
                            super.updateDrawState(textPaint);
                            textPaint.setUnderlineText(false);
                        }
                    }, indexOf, spannableStringBuilder.length() + indexOf, 33);
                    this.titleView.setOnLinkPressListener(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda4(this));
                }
                this.titleView.setText(spannableStringBuilder2, (TextView.BufferType) null);
                this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserStatusDialogSubtitle)));
            } else if (this.isEmojiStatus) {
                int i3 = R.string.TelegramPremiumUserStatusDefaultDialogTitle;
                TLRPC$User tLRPC$User2 = this.user;
                linksTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(i3, ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name))));
                TextView textView = this.subtitleView;
                int i4 = R.string.TelegramPremiumUserStatusDialogSubtitle;
                TLRPC$User tLRPC$User3 = this.user;
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(i4, ContactsController.formatName(tLRPC$User3.first_name, tLRPC$User3.last_name))));
            } else {
                GiftPremiumBottomSheet.GiftTier giftTier2 = this.giftTier;
                if (giftTier2 != null) {
                    String str = "";
                    if (this.isOutboundGift) {
                        int i5 = R.string.TelegramPremiumUserGiftedPremiumOutboundDialogTitleWithPlural;
                        Object[] objArr = new Object[2];
                        TLRPC$User tLRPC$User4 = this.user;
                        objArr[0] = tLRPC$User4 != null ? tLRPC$User4.first_name : str;
                        objArr[1] = LocaleController.formatPluralString("GiftMonths", giftTier2.getMonths(), new Object[0]);
                        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i5, objArr), "windowBackgroundWhiteBlueButton", (Runnable) null));
                        TextView textView2 = this.subtitleView;
                        int i6 = R.string.TelegramPremiumUserGiftedPremiumOutboundDialogSubtitle;
                        Object[] objArr2 = new Object[1];
                        TLRPC$User tLRPC$User5 = this.user;
                        if (tLRPC$User5 != null) {
                            str = tLRPC$User5.first_name;
                        }
                        objArr2[0] = str;
                        textView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i6, objArr2), "windowBackgroundWhiteBlueButton", (Runnable) null));
                        return;
                    }
                    int i7 = R.string.TelegramPremiumUserGiftedPremiumDialogTitleWithPlural;
                    Object[] objArr3 = new Object[2];
                    TLRPC$User tLRPC$User6 = this.user;
                    if (tLRPC$User6 != null) {
                        str = tLRPC$User6.first_name;
                    }
                    objArr3[0] = str;
                    objArr3[1] = LocaleController.formatPluralString("GiftMonths", giftTier2.getMonths(), new Object[0]);
                    linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i7, objArr3), "windowBackgroundWhiteBlueButton", (Runnable) null));
                    this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserGiftedPremiumDialogSubtitle)));
                    return;
                }
                int i8 = R.string.TelegramPremiumUserDialogTitle;
                TLRPC$User tLRPC$User7 = this.user;
                linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(i8, ContactsController.formatName(tLRPC$User7.first_name, tLRPC$User7.last_name)), "windowBackgroundWhiteBlueButton", (Runnable) null));
                this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.TelegramPremiumUserDialogSubtitle)));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setTitle$3(ClickableSpan clickableSpan) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.statusStickerSet);
        AnonymousClass2 r2 = new BaseFragment() {
            public Activity getParentActivity() {
                BaseFragment baseFragment = PremiumPreviewBottomSheet.this.fragment;
                if (baseFragment == null) {
                    return null;
                }
                return baseFragment.getParentActivity();
            }

            public int getCurrentAccount() {
                return this.currentAccount;
            }

            public FrameLayout getLayoutContainer() {
                return PremiumPreviewBottomSheet.this.bulletinContainer;
            }

            public View getFragmentView() {
                return PremiumPreviewBottomSheet.this.containerView;
            }

            public Dialog showDialog(Dialog dialog) {
                dialog.show();
                return dialog;
            }
        };
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            r2.setParentFragment(baseFragment);
        }
        new EmojiPacksAlert(r2, getContext(), this.resourcesProvider, arrayList) {
            /* access modifiers changed from: protected */
            public void onCloseByLink() {
                PremiumPreviewBottomSheet.this.dismiss();
            }
        }.show();
    }

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        return LocaleController.getString("TelegramPremium", R.string.TelegramPremium);
    }

    /* access modifiers changed from: protected */
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new Adapter();
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 0) {
                AnonymousClass1 r14 = new LinearLayout(context) {
                    /* access modifiers changed from: protected */
                    public boolean drawChild(Canvas canvas, View view, long j) {
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        if (view != premiumPreviewBottomSheet.iconTextureView || !premiumPreviewBottomSheet.enterTransitionInProgress) {
                            return super.drawChild(canvas, view, j);
                        }
                        return true;
                    }
                };
                PremiumPreviewBottomSheet.this.iconContainer = r14;
                r14.setOrientation(1);
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                View view2 = premiumPreviewBottomSheet.overrideTitleIcon;
                if (view2 == null) {
                    premiumPreviewBottomSheet.iconTextureView = new GLIconTextureView(this, context, 1) {
                        /* access modifiers changed from: protected */
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            setPaused(false);
                        }

                        /* access modifiers changed from: protected */
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
                    r14.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                } else {
                    if (view2.getParent() != null) {
                        ((ViewGroup) PremiumPreviewBottomSheet.this.overrideTitleIcon.getParent()).removeView(PremiumPreviewBottomSheet.this.overrideTitleIcon);
                    }
                    r14.addView(PremiumPreviewBottomSheet.this.overrideTitleIcon, LayoutHelper.createLinear(140, 140, 1.0f, 17, 10, 10, 10, 10));
                }
                if (PremiumPreviewBottomSheet.this.titleView == null) {
                    final PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ColorUtils.setAlphaComponent(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"), 178), PorterDuff.Mode.MULTIPLY);
                    LinkSpanDrawable.LinksTextView unused = PremiumPreviewBottomSheet.this.titleView = new LinkSpanDrawable.LinksTextView(this, context, PremiumPreviewBottomSheet.this.resourcesProvider) {
                        private Layout lastLayout;
                        AnimatedEmojiSpan.EmojiGroupedSpans stack;

                        /* access modifiers changed from: protected */
                        public void onDetachedFromWindow() {
                            super.onDetachedFromWindow();
                            AnimatedEmojiSpan.release((View) this, this.stack);
                            this.lastLayout = null;
                        }

                        /* access modifiers changed from: protected */
                        public void dispatchDraw(Canvas canvas) {
                            super.dispatchDraw(canvas);
                            if (this.lastLayout != getLayout()) {
                                AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.stack;
                                Layout layout = getLayout();
                                this.lastLayout = layout;
                                this.stack = AnimatedEmojiSpan.update(3, (View) this, emojiGroupedSpans, layout);
                            }
                            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.stack, 0.0f, (List<SpoilerEffect>) null, 0.0f, 0.0f, 0.0f, 1.0f, porterDuffColorFilter);
                        }
                    };
                    PremiumPreviewBottomSheet.this.titleView.setTextSize(1, 16.0f);
                    PremiumPreviewBottomSheet.this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    PremiumPreviewBottomSheet.this.titleView.setGravity(1);
                    PremiumPreviewBottomSheet.this.titleView.setTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteBlackText"));
                    PremiumPreviewBottomSheet.this.titleView.setLinkTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"));
                }
                if (PremiumPreviewBottomSheet.this.titleView.getParent() != null) {
                    ((ViewGroup) PremiumPreviewBottomSheet.this.titleView.getParent()).removeView(PremiumPreviewBottomSheet.this.titleView);
                }
                r14.addView(PremiumPreviewBottomSheet.this.titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                if (PremiumPreviewBottomSheet.this.subtitleView == null) {
                    TextView unused2 = PremiumPreviewBottomSheet.this.subtitleView = new TextView(context);
                    PremiumPreviewBottomSheet.this.subtitleView.setTextSize(1, 14.0f);
                    PremiumPreviewBottomSheet.this.subtitleView.setGravity(1);
                    PremiumPreviewBottomSheet.this.subtitleView.setTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteBlackText"));
                    PremiumPreviewBottomSheet.this.subtitleView.setLinkTextColor(PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundWhiteLinkText"));
                }
                if (PremiumPreviewBottomSheet.this.subtitleView.getParent() != null) {
                    ((ViewGroup) PremiumPreviewBottomSheet.this.subtitleView.getParent()).removeView(PremiumPreviewBottomSheet.this.subtitleView);
                }
                r14.addView(PremiumPreviewBottomSheet.this.subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                PremiumPreviewBottomSheet.this.setTitle();
                PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(this, context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        this.drawable.rect2.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(52.0f)));
                    }
                };
                AnonymousClass5 r1 = new FrameLayout(context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        float f;
                        float top;
                        int measuredHeight;
                        super.onMeasure(i, i2);
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        GLIconTextureView gLIconTextureView = premiumPreviewBottomSheet.iconTextureView;
                        if (gLIconTextureView != null) {
                            top = (float) gLIconTextureView.getTop();
                            measuredHeight = PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight();
                        } else {
                            View view = premiumPreviewBottomSheet.overrideTitleIcon;
                            if (view != null) {
                                top = (float) view.getTop();
                                measuredHeight = PremiumPreviewBottomSheet.this.overrideTitleIcon.getMeasuredHeight();
                            } else {
                                f = 0.0f;
                                StarParticlesView starParticlesView = PremiumPreviewBottomSheet.this.starParticlesView;
                                starParticlesView.setTranslationY(f - (((float) starParticlesView.getMeasuredHeight()) / 2.0f));
                            }
                        }
                        f = top + (((float) measuredHeight) / 2.0f);
                        StarParticlesView starParticlesView2 = PremiumPreviewBottomSheet.this.starParticlesView;
                        starParticlesView2.setTranslationY(f - (((float) starParticlesView2.getMeasuredHeight()) / 2.0f));
                    }
                };
                r1.setClipChildren(false);
                r1.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                r1.addView(r14);
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
                view = r1;
            } else if (i == 2) {
                view = new ShadowSectionCell(context, 12, PremiumPreviewBottomSheet.this.getThemedColor("windowBackgroundGray"));
            } else if (i != 3) {
                view = i != 4 ? new PremiumFeatureCell(context) {
                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
                        PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, (float) (-this.data.yOffset));
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                        super.dispatchDraw(canvas);
                    }
                } : new AboutPremiumView(context);
            } else {
                view = new View(this, context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), NUM));
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            int i2 = premiumPreviewBottomSheet.featuresStartRow;
            if (i >= i2 && i < premiumPreviewBottomSheet.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder.itemView;
                PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = premiumPreviewBottomSheet.premiumFeatures.get(i - i2);
                boolean z = true;
                if (i == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            }
        }

        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

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
            if (i == premiumPreviewBottomSheet.helpUsRow) {
                return 4;
            }
            return super.getItemViewType(i);
        }

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

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
        if (this.animateConfetti) {
            AndroidUtilities.runOnUIThread(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda3(this), 200);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$show$4() {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        this.fireworksOverlay.start();
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.fireworksOverlay.isStarted()) {
            this.fireworksOverlay.animate().alpha(0.0f).setDuration(150).start();
        }
    }

    /* access modifiers changed from: protected */
    public void mainContainerDispatchDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        View view = this.overrideTitleIcon;
        if (view != null) {
            view.setVisibility(this.enterTransitionInProgress ? 4 : 0);
        }
        super.mainContainerDispatchDraw(canvas);
        if (this.startEnterFromView != null && this.enterTransitionInProgress) {
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
            float f = ((float) (-iArr[0])) + this.startEnterFromX1 + fArr[0];
            float f2 = ((float) (-iArr[1])) + this.startEnterFromY1 + fArr[1];
            float intrinsicWidth = this.startEnterFromScale * ((float) rightDrawable.getIntrinsicWidth());
            float measuredHeight = ((float) view3.getMeasuredHeight()) * 0.8f;
            float f3 = measuredHeight / intrinsicWidth;
            float f4 = intrinsicWidth / measuredHeight;
            float measuredWidth = ((float) view3.getMeasuredWidth()) / 2.0f;
            View view4 = view3;
            while (view4 != this.container && view4 != null) {
                measuredWidth += view4.getX();
                view4 = (View) view4.getParent();
            }
            float y = view3.getY() + 0.0f + ((View) view3.getParent()).getY() + ((View) view3.getParent().getParent()).getY() + (((float) view3.getMeasuredHeight()) / 2.0f);
            float lerp = AndroidUtilities.lerp(f, measuredWidth, CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.enterTransitionProgress));
            float lerp2 = AndroidUtilities.lerp(f2, y, this.enterTransitionProgress);
            float f5 = this.startEnterFromScale;
            float f6 = this.enterTransitionProgress;
            float f7 = (f5 * (1.0f - f6)) + (f3 * f6);
            canvas.save();
            canvas2.scale(f7, f7, lerp, lerp2);
            int i = (int) lerp;
            int i2 = (int) lerp2;
            rightDrawable.setBounds(i - (rightDrawable.getIntrinsicWidth() / 2), i2 - (rightDrawable.getIntrinsicHeight() / 2), i + (rightDrawable.getIntrinsicWidth() / 2), i2 + (rightDrawable.getIntrinsicHeight() / 2));
            rightDrawable.setAlpha((int) ((1.0f - Utilities.clamp(this.enterTransitionProgress, 1.0f, 0.0f)) * 255.0f));
            rightDrawable.draw(canvas2);
            rightDrawable.setAlpha(0);
            canvas.restore();
            float lerp3 = AndroidUtilities.lerp(f4, 1.0f, this.enterTransitionProgress);
            canvas2.scale(lerp3, lerp3, lerp, lerp2);
            canvas2.translate(lerp - (((float) view3.getMeasuredWidth()) / 2.0f), lerp2 - (((float) view3.getMeasuredHeight()) / 2.0f));
            view3.draw(canvas2);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        if (this.startEnterFromView == null) {
            return false;
        }
        this.enterAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.enterTransitionProgress = 0.0f;
        this.enterTransitionInProgress = true;
        this.iconContainer.invalidate();
        this.startEnterFromView.getRightDrawable().setAlpha(0);
        this.startEnterFromView.invalidate();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.startEnterAnimation(-360, 100);
        }
        this.enterAnimator.addUpdateListener(new PremiumPreviewBottomSheet$$ExternalSyntheticLambda0(this));
        this.enterAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                premiumPreviewBottomSheet.enterTransitionInProgress = false;
                premiumPreviewBottomSheet.enterTransitionProgress = 1.0f;
                premiumPreviewBottomSheet.iconContainer.invalidate();
                ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{0, 255});
                ofInt.addUpdateListener(new PremiumPreviewBottomSheet$4$$ExternalSyntheticLambda0(this, PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable()));
                ofInt.start();
                super.onAnimationEnd(animator);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onAnimationEnd$0(Drawable drawable, ValueAnimator valueAnimator) {
                drawable.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
                PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
            }
        });
        this.enterAnimator.setDuration(600);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomOpenAnimation$5(ValueAnimator valueAnimator) {
        this.enterTransitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.invalidate();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        if (i == NotificationCenter.groupStickersDidLoad && (tLRPC$InputStickerSet = this.statusStickerSet) != null && tLRPC$InputStickerSet.id == objArr[0].longValue()) {
            setTitle();
        }
    }
}
