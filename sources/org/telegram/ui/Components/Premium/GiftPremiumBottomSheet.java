package org.telegram.ui.Components.Premium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.QueryProductDetailsParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentGiftPremium;
import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;

public class GiftPremiumBottomSheet extends BottomSheetWithRecyclerListView {
    /* access modifiers changed from: private */
    public int buttonRow;
    private PremiumGiftTierCell dummyCell;
    /* access modifiers changed from: private */
    public int footerRow;
    /* access modifiers changed from: private */
    public List<GiftTier> giftTiers = new ArrayList();
    /* access modifiers changed from: private */
    public PremiumGradient.GradientTools gradientTools;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public PremiumGradient.GradientTools outlineGradient;
    private PremiumButtonView premiumButtonView;
    /* access modifiers changed from: private */
    public int rowsCount;
    /* access modifiers changed from: private */
    public int selectedTierIndex = 0;
    /* access modifiers changed from: private */
    public int tiersEndRow;
    /* access modifiers changed from: private */
    public int tiersStartRow;
    /* access modifiers changed from: private */
    public int totalGradientHeight;
    /* access modifiers changed from: private */
    public TLRPC$User user;

    @SuppressLint({"NotifyDataSetChanged"})
    public GiftPremiumBottomSheet(BaseFragment baseFragment, TLRPC$User tLRPC$User) {
        super(baseFragment, false, true);
        this.user = tLRPC$User;
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", (String) null, (String) null);
        this.gradientTools = gradientTools2;
        gradientTools2.exactly = true;
        gradientTools2.x1 = 0.0f;
        gradientTools2.y1 = 0.0f;
        gradientTools2.x2 = 0.0f;
        gradientTools2.y2 = 1.0f;
        gradientTools2.cx = 0.0f;
        gradientTools2.cy = 0.0f;
        PremiumGradient.GradientTools gradientTools3 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.outlineGradient = gradientTools3;
        gradientTools3.paint.setStyle(Paint.Style.STROKE);
        this.outlineGradient.paint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        this.dummyCell = new PremiumGiftTierCell(getContext());
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(tLRPC$User.id);
        if (userFull != null) {
            ArrayList arrayList = new ArrayList();
            Iterator<TLRPC$TL_premiumGiftOption> it = userFull.premium_gifts.iterator();
            while (it.hasNext()) {
                GiftTier giftTier = new GiftTier(it.next());
                this.giftTiers.add(giftTier);
                if (!BuildVars.useInvoiceBilling() && giftTier.giftOption.store_product != null) {
                    arrayList.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(giftTier.giftOption.store_product).build());
                }
            }
            if (!arrayList.isEmpty()) {
                BillingController.getInstance().queryProductDetails(arrayList, new GiftPremiumBottomSheet$$ExternalSyntheticLambda3(this));
            }
        }
        if (!this.giftTiers.isEmpty()) {
            this.selectedTierIndex = 0;
            updateButtonText(false);
        }
        int i = this.rowsCount;
        int i2 = i + 1;
        this.rowsCount = i2;
        this.headerRow = i;
        this.tiersStartRow = i2;
        int size = i2 + this.giftTiers.size();
        this.rowsCount = size;
        this.tiersEndRow = size;
        int i3 = size + 1;
        this.rowsCount = i3;
        this.footerRow = size;
        this.rowsCount = i3 + 1;
        this.buttonRow = i3;
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GiftPremiumBottomSheet$$ExternalSyntheticLambda6(this));
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setSelectorTransformer(new GiftPremiumBottomSheet$$ExternalSyntheticLambda2(this, new Path()));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(BillingResult billingResult, List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ProductDetails productDetails = (ProductDetails) it.next();
            Iterator<GiftTier> it2 = this.giftTiers.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                GiftTier next = it2.next();
                String str = next.giftOption.store_product;
                if (str != null && str.equals(productDetails.getProductId())) {
                    next.setGooglePlayProductDetails(productDetails);
                    break;
                }
            }
        }
        AndroidUtilities.runOnUIThread(new GiftPremiumBottomSheet$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
        updateButtonText(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view, int i) {
        if (view instanceof PremiumGiftTierCell) {
            PremiumGiftTierCell premiumGiftTierCell = (PremiumGiftTierCell) view;
            this.selectedTierIndex = this.giftTiers.indexOf(premiumGiftTierCell.tier);
            updateButtonText(true);
            premiumGiftTierCell.setChecked(true, true);
            for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
                View childAt = this.recyclerListView.getChildAt(i2);
                if (childAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell2 = (PremiumGiftTierCell) childAt;
                    if (premiumGiftTierCell2.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell2.setChecked(false, true);
                    }
                }
            }
            for (int i3 = 0; i3 < this.recyclerListView.getHiddenChildCount(); i3++) {
                View hiddenChildAt = this.recyclerListView.getHiddenChildAt(i3);
                if (hiddenChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell3 = (PremiumGiftTierCell) hiddenChildAt;
                    if (premiumGiftTierCell3.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell3.setChecked(false, true);
                    }
                }
            }
            for (int i4 = 0; i4 < this.recyclerListView.getCachedChildCount(); i4++) {
                View cachedChildAt = this.recyclerListView.getCachedChildAt(i4);
                if (cachedChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell4 = (PremiumGiftTierCell) cachedChildAt;
                    if (premiumGiftTierCell4.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell4.setChecked(false, true);
                    }
                }
            }
            for (int i5 = 0; i5 < this.recyclerListView.getAttachedScrapChildCount(); i5++) {
                View attachedScrapChildAt = this.recyclerListView.getAttachedScrapChildAt(i5);
                if (attachedScrapChildAt instanceof PremiumGiftTierCell) {
                    PremiumGiftTierCell premiumGiftTierCell5 = (PremiumGiftTierCell) attachedScrapChildAt;
                    if (premiumGiftTierCell5.tier != premiumGiftTierCell.tier) {
                        premiumGiftTierCell5.setChecked(false, true);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(Path path, Canvas canvas) {
        path.rewind();
        Rect selectorRect = this.recyclerListView.getSelectorRect();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set((float) (selectorRect.left + AndroidUtilities.dp(20.0f)), (float) (selectorRect.top + AndroidUtilities.dp(3.0f)), (float) (selectorRect.right - AndroidUtilities.dp(20.0f)), (float) (selectorRect.bottom - AndroidUtilities.dp(3.0f)));
        path.addRoundRect(rectF, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), Path.Direction.CW);
        canvas.clipPath(path);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButtonText$4(View view) {
        onGiftPremium();
    }

    private void updateButtonText(boolean z) {
        this.premiumButtonView.setButton(LocaleController.formatString(NUM, this.giftTiers.get(this.selectedTierIndex).getFormattedPrice()), new GiftPremiumBottomSheet$$ExternalSyntheticLambda0(this), z);
    }

    private void onGiftPremium() {
        GiftTier giftTier = this.giftTiers.get(this.selectedTierIndex);
        if (BuildVars.useInvoiceBilling()) {
            if (getBaseFragment().getParentActivity() instanceof LaunchActivity) {
                Uri parse = Uri.parse(giftTier.giftOption.bot_url);
                if (parse.getHost().equals("t.me") && !parse.getPath().startsWith("/$")) {
                    ((LaunchActivity) getBaseFragment().getParentActivity()).setNavigateToPremiumBot(true);
                }
                Browser.openUrl((Context) getBaseFragment().getParentActivity(), giftTier.giftOption.bot_url);
            }
        } else if (BillingController.getInstance().isReady() && giftTier.googlePlayProductDetails != null) {
            TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium = new TLRPC$TL_inputStorePaymentGiftPremium();
            tLRPC$TL_inputStorePaymentGiftPremium.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user);
            ProductDetails.OneTimePurchaseOfferDetails oneTimePurchaseOfferDetails = giftTier.googlePlayProductDetails.getOneTimePurchaseOfferDetails();
            tLRPC$TL_inputStorePaymentGiftPremium.currency = oneTimePurchaseOfferDetails.getPriceCurrencyCode();
            double priceAmountMicros = (double) oneTimePurchaseOfferDetails.getPriceAmountMicros();
            double pow = Math.pow(10.0d, 6.0d);
            Double.isNaN(priceAmountMicros);
            tLRPC$TL_inputStorePaymentGiftPremium.amount = (long) ((priceAmountMicros / pow) * Math.pow(10.0d, (double) BillingController.getInstance().getCurrencyExp(tLRPC$TL_inputStorePaymentGiftPremium.currency)));
            BillingController.getInstance().addResultListener(giftTier.giftOption.store_product, new GiftPremiumBottomSheet$$ExternalSyntheticLambda1(this));
            BillingController.getInstance().launchBillingFlow(getBaseFragment().getParentActivity(), AccountInstance.getInstance(this.currentAccount), tLRPC$TL_inputStorePaymentGiftPremium, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(giftTier.googlePlayProductDetails).build()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onGiftPremium$6(BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(new GiftPremiumBottomSheet$$ExternalSyntheticLambda4(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onGiftPremium$5() {
        if (getBaseFragment() != null) {
            getBaseFragment().finishFragment();
        }
    }

    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        this.premiumButtonView = new PremiumButtonView(getContext(), true);
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        frameLayout2.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        frameLayout2.setBackgroundColor(getThemedColor("dialogBackground"));
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 68, 80));
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    private void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.giftTiers.size(); i4++) {
            this.dummyCell.bind(this.giftTiers.get(i4));
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.giftTiers.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        return LocaleController.getString(NUM);
    }

    /* access modifiers changed from: protected */
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 1;
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$1} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$1} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$1} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
                /*
                    r7 = this;
                    r8 = 1
                    if (r9 == r8) goto L_0x009b
                    r0 = 2
                    if (r9 == r0) goto L_0x0023
                    r8 = 3
                    if (r9 == r8) goto L_0x0016
                    org.telegram.ui.Components.Premium.PremiumGiftHeaderCell r8 = new org.telegram.ui.Components.Premium.PremiumGiftHeaderCell
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet r9 = org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.this
                    android.content.Context r9 = r9.getContext()
                    r8.<init>(r9)
                    goto L_0x00c1
                L_0x0016:
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$2 r8 = new org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$2
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet r9 = org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.this
                    android.content.Context r9 = r9.getContext()
                    r8.<init>(r7, r9)
                    goto L_0x00c1
                L_0x0023:
                    org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet r0 = org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.this
                    android.content.Context r0 = r0.getContext()
                    r9.<init>(r0)
                    r0 = 28
                    r9.setTopPadding(r0)
                    android.widget.TextView r0 = r9.getTextView()
                    r0.setGravity(r8)
                    r0 = 2131626094(0x7f0e086e, float:1.8879414E38)
                    java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
                    r1 = 42
                    int r2 = r0.indexOf(r1)
                    int r1 = r0.lastIndexOf(r1)
                    r3 = -1
                    r4 = 0
                    if (r2 == r3) goto L_0x008a
                    if (r1 == r3) goto L_0x008a
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r5 = r0.substring(r4, r2)
                    r3.append(r5)
                    int r5 = r2 + 1
                    java.lang.String r5 = r0.substring(r5, r1)
                    r3.append(r5)
                    int r5 = r1 + 1
                    java.lang.String r0 = r0.substring(r5)
                    r3.append(r0)
                    java.lang.String r0 = r3.toString()
                    android.text.SpannableString r3 = new android.text.SpannableString
                    r3.<init>(r0)
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$LinkSpan r0 = new org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$LinkSpan
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet r5 = org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.this
                    r6 = 0
                    r0.<init>()
                    int r1 = r1 - r8
                    r8 = 33
                    r3.setSpan(r0, r2, r1, r8)
                    r9.setText(r3)
                    goto L_0x008d
                L_0x008a:
                    r9.setText(r0)
                L_0x008d:
                    r8 = 1101529088(0x41a80000, float:21.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    r9.setPadding(r0, r4, r8, r4)
                    goto L_0x00c0
                L_0x009b:
                    java.util.concurrent.atomic.AtomicReference r8 = new java.util.concurrent.atomic.AtomicReference
                    r9 = 0
                    java.lang.Float r9 = java.lang.Float.valueOf(r9)
                    r8.<init>(r9)
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$1 r9 = new org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$1
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet r0 = org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.this
                    android.content.Context r0 = r0.getContext()
                    r9.<init>(r0, r8)
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda0
                    r0.<init>(r7, r9)
                    r9.setCirclePaintProvider(r0)
                    org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$1$$ExternalSyntheticLambda1
                    r0.<init>(r8, r9)
                    r9.setProgressDelegate(r0)
                L_0x00c0:
                    r8 = r9
                L_0x00c1:
                    org.telegram.ui.Components.RecyclerListView$Holder r9 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r9.<init>(r8)
                    return r9
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.GiftPremiumBottomSheet.AnonymousClass1.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ Paint lambda$onCreateViewHolder$0(PremiumGiftTierCell premiumGiftTierCell, Void voidR) {
                GiftPremiumBottomSheet.this.gradientTools.gradientMatrix(0, 0, premiumGiftTierCell.getMeasuredWidth(), GiftPremiumBottomSheet.this.totalGradientHeight, 0.0f, (float) (-premiumGiftTierCell.tier.yOffset));
                return GiftPremiumBottomSheet.this.gradientTools.paint;
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onCreateViewHolder$1(AtomicReference atomicReference, PremiumGiftTierCell premiumGiftTierCell, float f) {
                atomicReference.set(Float.valueOf(f));
                premiumGiftTierCell.invalidate();
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (i == GiftPremiumBottomSheet.this.headerRow) {
                    ((PremiumGiftHeaderCell) viewHolder.itemView).bind(GiftPremiumBottomSheet.this.user);
                } else if (i >= GiftPremiumBottomSheet.this.tiersStartRow && i < GiftPremiumBottomSheet.this.tiersEndRow) {
                    PremiumGiftTierCell premiumGiftTierCell = (PremiumGiftTierCell) viewHolder.itemView;
                    premiumGiftTierCell.bind((GiftTier) GiftPremiumBottomSheet.this.giftTiers.get(i - GiftPremiumBottomSheet.this.tiersStartRow));
                    premiumGiftTierCell.setChecked(i - GiftPremiumBottomSheet.this.tiersStartRow == GiftPremiumBottomSheet.this.selectedTierIndex, false);
                }
            }

            public int getItemViewType(int i) {
                if (i == GiftPremiumBottomSheet.this.headerRow) {
                    return 0;
                }
                if (i >= GiftPremiumBottomSheet.this.tiersStartRow && i < GiftPremiumBottomSheet.this.tiersEndRow) {
                    return 1;
                }
                if (i == GiftPremiumBottomSheet.this.footerRow) {
                    return 2;
                }
                if (i == GiftPremiumBottomSheet.this.buttonRow) {
                    return 3;
                }
                return 0;
            }

            public int getItemCount() {
                return GiftPremiumBottomSheet.this.rowsCount;
            }
        };
    }

    private final class LinkSpan extends ClickableSpan {
        private LinkSpan() {
        }

        public void onClick(View view) {
            GiftPremiumBottomSheet.this.getBaseFragment().presentFragment(new PremiumPreviewFragment("profile"));
            GiftPremiumBottomSheet.this.dismiss();
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }
    }

    public static final class GiftTier {
        private int discount;
        public final TLRPC$TL_premiumGiftOption giftOption;
        /* access modifiers changed from: private */
        public ProductDetails googlePlayProductDetails;
        private long pricePerMonth;
        public int yOffset;

        public GiftTier(TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption) {
            this.giftOption = tLRPC$TL_premiumGiftOption;
        }

        public void setGooglePlayProductDetails(ProductDetails productDetails) {
            this.googlePlayProductDetails = productDetails;
        }

        public int getMonths() {
            return this.giftOption.months;
        }

        public int getDiscount() {
            long j;
            if (this.discount == 0) {
                if (getPricePerMonth() == 0) {
                    return 0;
                }
                if (BuildVars.useInvoiceBilling()) {
                    MediaDataController.getInstance(UserConfig.selectedAccount).getPremiumPromo();
                    j = 499;
                } else {
                    j = 0;
                }
                ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
                if (productDetails != null) {
                    List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
                    if (!subscriptionOfferDetails.isEmpty()) {
                        Iterator<ProductDetails.PricingPhase> it = subscriptionOfferDetails.get(0).getPricingPhases().getPricingPhaseList().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            ProductDetails.PricingPhase next = it.next();
                            if (next.getBillingPeriod().equals("P1M")) {
                                j = next.getPriceAmountMicros();
                                break;
                            }
                        }
                    }
                }
                if (j != 0) {
                    double pricePerMonth2 = (double) getPricePerMonth();
                    double d = (double) j;
                    Double.isNaN(pricePerMonth2);
                    Double.isNaN(d);
                    this.discount = (int) ((1.0d - (pricePerMonth2 / d)) * 100.0d);
                }
            }
            return this.discount;
        }

        public long getPricePerMonth() {
            if (this.pricePerMonth == 0) {
                long price = getPrice();
                if (price != 0) {
                    this.pricePerMonth = price / ((long) this.giftOption.months);
                }
            }
            return this.pricePerMonth;
        }

        public String getFormattedPricePerMonth() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency(), 6);
        }

        public String getFormattedPrice() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return BillingController.getInstance().formatCurrency(getPrice(), getCurrency());
            }
            return this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPrice(), getCurrency(), 6);
        }

        public long getPrice() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.amount;
            }
            ProductDetails productDetails = this.googlePlayProductDetails;
            if (productDetails == null) {
                return 0;
            }
            return productDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
        }

        public String getCurrency() {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.currency;
            }
            ProductDetails productDetails = this.googlePlayProductDetails;
            return productDetails == null ? "" : productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
        }
    }
}
