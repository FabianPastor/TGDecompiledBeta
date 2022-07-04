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
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;
import org.telegram.tgnet.TLRPC$Updates;

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

    private BillingController(Context context) {
        this.billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
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
            if (BuildVars.useInvoiceBilling()) {
                try {
                    InputStream open = ApplicationLoader.applicationContext.getAssets().open("currencies.json");
                    parseCurrencies(new JSONObject(new String(Util.toByteArray(open), "UTF-8")));
                    open.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
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

    public boolean launchBillingFlow(Activity activity, List<BillingFlowParams.ProductDetailsParams> list) {
        if (isReady() && this.billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setProductDetailsParamsList(list).build()).getResponseCode() == 0) {
            return true;
        }
        return false;
    }

    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        FileLog.d("Billing purchases updated: " + billingResult + ", " + list);
        if (list != null) {
            for (Purchase next : list) {
                if (next.getPurchaseState() == 1 && !next.isAcknowledged() && !this.requestingTokens.contains(next.getPurchaseToken())) {
                    this.requestingTokens.add(next.getPurchaseToken());
                    TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction = new TLRPC$TL_payments_assignPlayMarketTransaction();
                    tLRPC$TL_payments_assignPlayMarketTransaction.purchase_token = next.getPurchaseToken();
                    AccountInstance instance2 = AccountInstance.getInstance(UserConfig.selectedAccount);
                    instance2.getConnectionsManager().sendRequest(tLRPC$TL_payments_assignPlayMarketTransaction, new BillingController$$ExternalSyntheticLambda3(this, instance2, next, billingResult));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPurchasesUpdated$0(AccountInstance accountInstance, Purchase purchase, BillingResult billingResult, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            this.requestingTokens.remove(purchase.getPurchaseToken());
            for (String remove : purchase.getProducts()) {
                this.resultListeners.remove(remove).accept(billingResult);
            }
        }
    }

    public void onBillingServiceDisconnected() {
        FileLog.d("Billing service disconnected");
    }

    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            queryProductDetails(Collections.singletonList(PREMIUM_PRODUCT), BillingController$$ExternalSyntheticLambda0.INSTANCE);
            queryPurchases("subs", new BillingController$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onBillingSetupFinished$2(BillingResult billingResult, List list) {
        if (billingResult.getResponseCode() == 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                ProductDetails productDetails = (ProductDetails) it.next();
                if (productDetails.getProductId().equals("telegram_premium")) {
                    PREMIUM_PRODUCT_DETAILS = productDetails;
                }
            }
            AndroidUtilities.runOnUIThread(BillingController$$ExternalSyntheticLambda2.INSTANCE);
        }
    }
}
