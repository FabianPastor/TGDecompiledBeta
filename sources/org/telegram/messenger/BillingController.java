package org.telegram.messenger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.exoplayer2.util.Util;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputStorePaymentPurpose;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStorePaymentGiftPremium;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.ui.PremiumPreviewFragment;

public class BillingController implements PurchasesUpdatedListener, BillingClientStateListener {
    public static final QueryProductDetailsParams.Product PREMIUM_PRODUCT = QueryProductDetailsParams.Product.newBuilder().setProductType("subs").setProductId("telegram_premium").build();
    public static ProductDetails PREMIUM_PRODUCT_DETAILS = null;
    public static final String PREMIUM_PRODUCT_ID = "telegram_premium";
    private static BillingController instance;
    private BillingClient billingClient;
    private Map<String, Integer> currencyExpMap = new HashMap();
    private List<String> requestingTokens = new ArrayList();
    private Map<String, Consumer<BillingResult>> resultListeners = new HashMap();

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onPurchasesUpdated$3(BillingResult billingResult, String str) {
    }

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private BillingController(Context context) {
        this.billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
    }

    public String formatCurrency(long j, String str) {
        return formatCurrency(j, str, getCurrencyExp(str));
    }

    public String formatCurrency(long j, String str, int i) {
        if (str.isEmpty()) {
            return String.valueOf(j);
        }
        Currency instance2 = Currency.getInstance(str);
        if (instance2 != null) {
            NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
            currencyInstance.setCurrency(instance2);
            double d = (double) j;
            double pow = Math.pow(10.0d, (double) i);
            Double.isNaN(d);
            return currencyInstance.format(d / pow);
        }
        return j + " " + str;
    }

    public int getCurrencyExp(String str) {
        Integer num = this.currencyExpMap.get(str);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public void startConnection() {
        if (!isReady()) {
            try {
                InputStream open = ApplicationLoader.applicationContext.getAssets().open("currencies.json");
                parseCurrencies(new JSONObject(new String(Util.toByteArray(open), "UTF-8")));
                open.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (!BuildVars.useInvoiceBilling()) {
                this.billingClient.startConnection(this);
            }
        }
    }

    private void parseCurrencies(JSONObject jSONObject) {
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            this.currencyExpMap.put(next, Integer.valueOf(jSONObject.optJSONObject(next).optInt("exp")));
        }
    }

    public boolean isReady() {
        return this.billingClient.isReady();
    }

    public void queryProductDetails(List<QueryProductDetailsParams.Product> list, ProductDetailsResponseListener productDetailsResponseListener) {
        if (isReady()) {
            this.billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(list).build(), productDetailsResponseListener);
            return;
        }
        throw new IllegalStateException("Billing controller should be ready for this call!");
    }

    public void queryPurchases(String str, PurchasesResponseListener purchasesResponseListener) {
        this.billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(str).build(), purchasesResponseListener);
    }

    public boolean startManageSubscription(Context context, String str) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format("https://play.google.com/store/account/subscriptions?sku=%s&package=%s", new Object[]{str, context.getPackageName()}))));
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    public void addResultListener(String str, Consumer<BillingResult> consumer) {
        this.resultListeners.put(str, consumer);
    }

    public void launchBillingFlow(Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List<BillingFlowParams.ProductDetailsParams> list) {
        launchBillingFlow(activity, accountInstance, tLRPC$InputStorePaymentPurpose, list, false);
    }

    public void launchBillingFlow(Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List<BillingFlowParams.ProductDetailsParams> list, boolean z) {
        if (isReady() && activity != null) {
            if (!(tLRPC$InputStorePaymentPurpose instanceof TLRPC$TL_inputStorePaymentGiftPremium) || z) {
                if (this.billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setProductDetailsParamsList(list).build()).getResponseCode() == 0) {
                    for (BillingFlowParams.ProductDetailsParams zza : list) {
                        accountInstance.getUserConfig().billingPaymentPurpose = tLRPC$InputStorePaymentPurpose;
                        accountInstance.getUserConfig().awaitBillingProductIds.add(zza.zza().getProductId());
                    }
                    accountInstance.getUserConfig().saveConfig(false);
                    return;
                }
                return;
            }
            queryPurchases("inapp", new BillingController$$ExternalSyntheticLambda4(this, activity, accountInstance, tLRPC$InputStorePaymentPurpose, list));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$launchBillingFlow$2(Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List list, BillingResult billingResult, List list2) {
        if (billingResult.getResponseCode() == 0) {
            BillingController$$ExternalSyntheticLambda5 billingController$$ExternalSyntheticLambda5 = new BillingController$$ExternalSyntheticLambda5(this, activity, accountInstance, tLRPC$InputStorePaymentPurpose, list);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            ArrayList arrayList = new ArrayList();
            Iterator it = list2.iterator();
            while (it.hasNext()) {
                Purchase purchase = (Purchase) it.next();
                if (purchase.isAcknowledged()) {
                    Iterator it2 = list.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        String productId = ((BillingFlowParams.ProductDetailsParams) it2.next()).zza().getProductId();
                        if (purchase.getProducts().contains(productId)) {
                            atomicInteger.incrementAndGet();
                            this.billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new BillingController$$ExternalSyntheticLambda0(arrayList, productId, atomicInteger, billingController$$ExternalSyntheticLambda5));
                            break;
                        }
                    }
                } else {
                    onPurchasesUpdated(BillingResult.newBuilder().setResponseCode(0).build(), Collections.singletonList(purchase));
                    return;
                }
            }
            if (atomicInteger.get() == 0) {
                billingController$$ExternalSyntheticLambda5.run();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$launchBillingFlow$0(Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List list) {
        launchBillingFlow(activity, accountInstance, tLRPC$InputStorePaymentPurpose, list, true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$launchBillingFlow$1(List list, String str, AtomicInteger atomicInteger, Runnable runnable, BillingResult billingResult, String str2) {
        if (billingResult.getResponseCode() == 0) {
            list.add(str);
            if (atomicInteger.get() == list.size()) {
                runnable.run();
            }
        }
    }

    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        List<Purchase> list2 = list;
        FileLog.d("Billing purchases updated: " + billingResult + ", " + list2);
        int i = 4;
        int i2 = 1;
        if (billingResult.getResponseCode() != 0) {
            if (billingResult.getResponseCode() == 1) {
                PremiumPreviewFragment.sentPremiumBuyCanceled();
            }
            for (int i3 = 0; i3 < 4; i3++) {
                AccountInstance instance2 = AccountInstance.getInstance(i3);
                if (!instance2.getUserConfig().awaitBillingProductIds.isEmpty()) {
                    instance2.getUserConfig().awaitBillingProductIds.clear();
                    instance2.getUserConfig().billingPaymentPurpose = null;
                    instance2.getUserConfig().saveConfig(false);
                }
            }
        } else if (list2 != null) {
            for (Purchase next : list) {
                if (!this.requestingTokens.contains(next.getPurchaseToken())) {
                    int i4 = 0;
                    while (i4 < i) {
                        AccountInstance instance3 = AccountInstance.getInstance(i4);
                        if (instance3.getUserConfig().awaitBillingProductIds.containsAll(next.getProducts()) && next.getPurchaseState() != 2) {
                            instance3.getUserConfig().awaitBillingProductIds.removeAll(next.getProducts());
                            instance3.getUserConfig().saveConfig(false);
                            if (next.getPurchaseState() == i2 && !next.isAcknowledged()) {
                                this.requestingTokens.add(next.getPurchaseToken());
                                TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction = new TLRPC$TL_payments_assignPlayMarketTransaction();
                                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                                tLRPC$TL_payments_assignPlayMarketTransaction.receipt = tLRPC$TL_dataJSON;
                                tLRPC$TL_dataJSON.data = next.getOriginalJson();
                                tLRPC$TL_payments_assignPlayMarketTransaction.purpose = instance3.getUserConfig().billingPaymentPurpose;
                                BillingController$$ExternalSyntheticLambda7 billingController$$ExternalSyntheticLambda7 = r0;
                                ConnectionsManager connectionsManager = instance3.getConnectionsManager();
                                BillingController$$ExternalSyntheticLambda7 billingController$$ExternalSyntheticLambda72 = new BillingController$$ExternalSyntheticLambda7(this, instance3, next, billingResult, tLRPC$TL_payments_assignPlayMarketTransaction);
                                connectionsManager.sendRequest(tLRPC$TL_payments_assignPlayMarketTransaction, billingController$$ExternalSyntheticLambda7, 66);
                                instance3.getUserConfig().billingPaymentPurpose = null;
                                instance3.getUserConfig().saveConfig(false);
                            }
                        }
                        i4++;
                        i = 4;
                        i2 = 1;
                    }
                }
                i = 4;
                i2 = 1;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPurchasesUpdated$4(AccountInstance accountInstance, Purchase purchase, BillingResult billingResult, TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            this.requestingTokens.remove(purchase.getPurchaseToken());
            for (String remove : purchase.getProducts()) {
                Consumer remove2 = this.resultListeners.remove(remove);
                if (remove2 != null) {
                    remove2.accept(billingResult);
                }
            }
            if (tLRPC$TL_payments_assignPlayMarketTransaction.purpose instanceof TLRPC$TL_inputStorePaymentGiftPremium) {
                this.billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), BillingController$$ExternalSyntheticLambda1.INSTANCE);
            }
        }
    }

    public void onBillingServiceDisconnected() {
        FileLog.d("Billing service disconnected");
    }

    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), BillingController$$ExternalSyntheticLambda2.INSTANCE);
            queryPurchases("subs", new BillingController$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onBillingSetupFinished$6(BillingResult billingResult, List list) {
        if (billingResult.getResponseCode() == 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                ProductDetails productDetails = (ProductDetails) it.next();
                if (productDetails.getProductId().equals("telegram_premium")) {
                    PREMIUM_PRODUCT_DETAILS = productDetails;
                }
            }
            AndroidUtilities.runOnUIThread(BillingController$$ExternalSyntheticLambda6.INSTANCE);
        }
    }
}
