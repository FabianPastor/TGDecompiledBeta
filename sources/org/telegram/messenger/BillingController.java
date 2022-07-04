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
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.exoplayer2.util.Util;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class BillingController implements PurchasesUpdatedListener, BillingClientStateListener {
    public static final QueryProductDetailsParams.Product PREMIUM_PRODUCT = QueryProductDetailsParams.Product.newBuilder().setProductType("subs").setProductId("telegram_premium").build();
    public static ProductDetails PREMIUM_PRODUCT_DETAILS = null;
    public static final String PREMIUM_PRODUCT_ID = "telegram_premium";
    private static BillingController instance;
    private BillingClient billingClient;
    private Map<String, Integer> currencyExpMap = new HashMap();
    private List<String> requestingTokens = new ArrayList();
    private Map<String, Consumer<BillingResult>> resultListeners = new HashMap();

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private BillingController(Context ctx) {
        this.billingClient = BillingClient.newBuilder(ctx).enablePendingPurchases().setListener(this).build();
    }

    public int getCurrencyExp(String currency) {
        Integer exp = this.currencyExpMap.get(currency);
        if (exp == null) {
            return 0;
        }
        return exp.intValue();
    }

    public void startConnection() {
        if (!isReady()) {
            if (BuildVars.useInvoiceBilling()) {
                try {
                    InputStream in = ApplicationLoader.applicationContext.getAssets().open("currencies.json");
                    parseCurrencies(new JSONObject(new String(Util.toByteArray(in), "UTF-8")));
                    in.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                this.billingClient.startConnection(this);
            }
        }
    }

    private void parseCurrencies(JSONObject obj) {
        Iterator<String> it = obj.keys();
        while (it.hasNext()) {
            String key = it.next();
            this.currencyExpMap.put(key, Integer.valueOf(obj.optJSONObject(key).optInt("exp")));
        }
    }

    public boolean isReady() {
        return this.billingClient.isReady();
    }

    public void queryProductDetails(List<QueryProductDetailsParams.Product> products, ProductDetailsResponseListener responseListener) {
        if (isReady()) {
            this.billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(products).build(), responseListener);
            return;
        }
        throw new IllegalStateException("Billing controller should be ready for this call!");
    }

    public void queryPurchases(String productType, PurchasesResponseListener responseListener) {
        this.billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(productType).build(), responseListener);
    }

    public boolean startManageSubscription(Context ctx, String productId) {
        try {
            ctx.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format("https://play.google.com/store/account/subscriptions?sku=%s&package=%s", new Object[]{productId, ctx.getPackageName()}))));
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void addResultListener(String productId, Consumer<BillingResult> listener) {
        this.resultListeners.put(productId, listener);
    }

    public boolean launchBillingFlow(Activity activity, List<BillingFlowParams.ProductDetailsParams> productDetails) {
        if (isReady() && this.billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetails).build()).getResponseCode() == 0) {
            return true;
        }
        return false;
    }

    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        FileLog.d("Billing purchases updated: " + billingResult + ", " + list);
        if (list != null) {
            for (Purchase purchase : list) {
                if (purchase.getPurchaseState() == 1 && !purchase.isAcknowledged() && !this.requestingTokens.contains(purchase.getPurchaseToken())) {
                    this.requestingTokens.add(purchase.getPurchaseToken());
                    TLRPC.TL_payments_assignPlayMarketTransaction req = new TLRPC.TL_payments_assignPlayMarketTransaction();
                    req.purchase_token = purchase.getPurchaseToken();
                    AccountInstance acc = AccountInstance.getInstance(UserConfig.selectedAccount);
                    acc.getConnectionsManager().sendRequest(req, new BillingController$$ExternalSyntheticLambda3(this, acc, purchase, billingResult));
                }
            }
        }
    }

    /* renamed from: lambda$onPurchasesUpdated$0$org-telegram-messenger-BillingController  reason: not valid java name */
    public /* synthetic */ void m1xaefb09cd(AccountInstance acc, Purchase purchase, BillingResult billingResult, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Updates) {
            acc.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            this.requestingTokens.remove(purchase.getPurchaseToken());
            for (String productId : purchase.getProducts()) {
                this.resultListeners.remove(productId).accept(billingResult);
            }
        }
    }

    public void onBillingServiceDisconnected() {
        FileLog.d("Billing service disconnected");
    }

    public void onBillingSetupFinished(BillingResult setupBillingResult) {
        if (setupBillingResult.getResponseCode() == 0) {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), BillingController$$ExternalSyntheticLambda0.INSTANCE);
            queryPurchases("subs", new BillingController$$ExternalSyntheticLambda1(this));
        }
    }

    static /* synthetic */ void lambda$onBillingSetupFinished$2(BillingResult billingResult, List list) {
        if (billingResult.getResponseCode() == 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                ProductDetails details = (ProductDetails) it.next();
                if (details.getProductId().equals("telegram_premium")) {
                    PREMIUM_PRODUCT_DETAILS = details;
                }
            }
            AndroidUtilities.runOnUIThread(BillingController$$ExternalSyntheticLambda2.INSTANCE);
        }
    }
}
