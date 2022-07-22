package org.telegram.ui.Components.Premium;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentGiftPremium;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;

public final /* synthetic */ class GiftPremiumBottomSheet$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ GiftPremiumBottomSheet f$0;
    public final /* synthetic */ TLRPC$TL_inputStorePaymentGiftPremium f$1;
    public final /* synthetic */ GiftPremiumBottomSheet.GiftTier f$2;
    public final /* synthetic */ TLRPC$TL_payments_canPurchasePremium f$3;

    public /* synthetic */ GiftPremiumBottomSheet$$ExternalSyntheticLambda8(GiftPremiumBottomSheet giftPremiumBottomSheet, TLRPC$TL_inputStorePaymentGiftPremium tLRPC$TL_inputStorePaymentGiftPremium, GiftPremiumBottomSheet.GiftTier giftTier, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        this.f$0 = giftPremiumBottomSheet;
        this.f$1 = tLRPC$TL_inputStorePaymentGiftPremium;
        this.f$2 = giftTier;
        this.f$3 = tLRPC$TL_payments_canPurchasePremium;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onGiftPremium$9(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
