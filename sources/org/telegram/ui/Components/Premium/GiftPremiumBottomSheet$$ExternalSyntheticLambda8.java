package org.telegram.ui.Components.Premium;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentGiftPremium;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;

public final /* synthetic */ class GiftPremiumBottomSheet$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ GiftPremiumBottomSheet f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_inputStorePaymentGiftPremium f$2;
    public final /* synthetic */ GiftPremiumBottomSheet.GiftTier f$3;
    public final /* synthetic */ TLRPC$TL_error f$4;
    public final /* synthetic */ TLRPC$TL_payments_canPurchasePremium f$5;

    public /* synthetic */ GiftPremiumBottomSheet$$ExternalSyntheticLambda8(GiftPremiumBottomSheet giftPremiumBottomSheet, TLObject tLObject, TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium, GiftPremiumBottomSheet.GiftTier giftTier, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        this.f$0 = giftPremiumBottomSheet;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_inputStorePaymentGiftPremium;
        this.f$3 = giftTier;
        this.f$4 = tLRPC$TL_error;
        this.f$5 = tLRPC$TL_payments_canPurchasePremium;
    }

    public final void run() {
        this.f$0.lambda$onGiftPremium$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
