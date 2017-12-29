package com.stripe.android.net;

import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.util.StripeJsonUtils;
import com.stripe.android.util.StripeTextUtils;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class TokenParser {
    public static Token parseToken(String jsonToken) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonToken);
        String tokenId = StripeJsonUtils.getString(jsonObject, TtmlNode.ATTR_ID);
        Long createdTimeStamp = Long.valueOf(jsonObject.getLong("created"));
        Boolean liveMode = Boolean.valueOf(jsonObject.getBoolean("livemode"));
        String tokenType = StripeTextUtils.asTokenType(StripeJsonUtils.getString(jsonObject, "type"));
        Boolean used = Boolean.valueOf(jsonObject.getBoolean("used"));
        Card card = CardParser.parseCard(jsonObject.getJSONObject("card"));
        return new Token(tokenId, liveMode.booleanValue(), new Date(createdTimeStamp.longValue() * 1000), used, card, tokenType);
    }
}
