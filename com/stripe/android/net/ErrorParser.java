package com.stripe.android.net;

import com.stripe.android.util.StripeJsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

class ErrorParser {

    static class StripeError {
        public String charge;
        public String code;
        public String decline_code;
        public String message;
        public String param;
        public String type;

        StripeError() {
        }
    }

    static StripeError parseError(String rawError) {
        StripeError stripeError = new StripeError();
        try {
            JSONObject errorObject = new JSONObject(rawError).getJSONObject("error");
            stripeError.charge = StripeJsonUtils.optString(errorObject, "charge");
            stripeError.code = StripeJsonUtils.optString(errorObject, "code");
            stripeError.decline_code = StripeJsonUtils.optString(errorObject, "decline_code");
            stripeError.message = StripeJsonUtils.optString(errorObject, "message");
            stripeError.param = StripeJsonUtils.optString(errorObject, "param");
            stripeError.type = StripeJsonUtils.optString(errorObject, "type");
        } catch (JSONException e) {
            stripeError.message = "An improperly formatted error response was found.";
        }
        return stripeError;
    }
}
