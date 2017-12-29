package com.stripe.android.net;

import com.stripe.android.model.Card;
import com.stripe.android.util.StripeJsonUtils;
import com.stripe.android.util.StripeTextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CardParser {
    public static Card parseCard(JSONObject objectCard) throws JSONException {
        return new Card(null, Integer.valueOf(objectCard.getInt("exp_month")), Integer.valueOf(objectCard.getInt("exp_year")), null, StripeJsonUtils.optString(objectCard, "name"), StripeJsonUtils.optString(objectCard, "address_line1"), StripeJsonUtils.optString(objectCard, "address_line2"), StripeJsonUtils.optString(objectCard, "address_city"), StripeJsonUtils.optString(objectCard, "address_state"), StripeJsonUtils.optString(objectCard, "address_zip"), StripeJsonUtils.optString(objectCard, "address_country"), StripeTextUtils.asCardBrand(StripeJsonUtils.optString(objectCard, "brand")), StripeJsonUtils.optString(objectCard, "last4"), StripeJsonUtils.optString(objectCard, "fingerprint"), StripeTextUtils.asFundingType(StripeJsonUtils.optString(objectCard, "funding")), StripeJsonUtils.optString(objectCard, "country"), StripeJsonUtils.optString(objectCard, "currency"));
    }
}
