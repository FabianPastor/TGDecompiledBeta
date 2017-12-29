package com.stripe.android;

import android.os.AsyncTask;
import android.os.Build.VERSION;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.StripeException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.net.RequestOptions;
import com.stripe.android.net.StripeApiHandler;
import com.stripe.android.util.StripeNetworkUtils;
import java.util.concurrent.Executor;

public class Stripe {
    private String defaultPublishableKey;
    TokenCreator tokenCreator = new TokenCreator() {
        public void create(final Card card, final String publishableKey, Executor executor, final TokenCallback callback) {
            Stripe.this.executeTokenTask(executor, new AsyncTask<Void, Void, ResponseWrapper>() {
                protected ResponseWrapper doInBackground(Void... params) {
                    try {
                        return new ResponseWrapper(StripeApiHandler.createToken(StripeNetworkUtils.hashMapFromCard(card), RequestOptions.builder(publishableKey).build()), null);
                    } catch (StripeException e) {
                        return new ResponseWrapper(null, e);
                    }
                }

                protected void onPostExecute(ResponseWrapper result) {
                    Stripe.this.tokenTaskPostExecution(result, callback);
                }
            });
        }
    };

    private class ResponseWrapper {
        final Exception error;
        final Token token;

        private ResponseWrapper(Token token, Exception error) {
            this.error = error;
            this.token = token;
        }
    }

    interface TokenCreator {
        void create(Card card, String str, Executor executor, TokenCallback tokenCallback);
    }

    public Stripe(String publishableKey) throws AuthenticationException {
        setDefaultPublishableKey(publishableKey);
    }

    public void createToken(Card card, TokenCallback callback) {
        createToken(card, this.defaultPublishableKey, callback);
    }

    public void createToken(Card card, String publishableKey, TokenCallback callback) {
        createToken(card, publishableKey, null, callback);
    }

    public void createToken(Card card, String publishableKey, Executor executor, TokenCallback callback) {
        if (card == null) {
            try {
                throw new RuntimeException("Required Parameter: 'card' is required to create a token");
            } catch (AuthenticationException e) {
                callback.onError(e);
            }
        } else if (callback == null) {
            throw new RuntimeException("Required Parameter: 'callback' is required to use the created token and handle errors");
        } else {
            validateKey(publishableKey);
            this.tokenCreator.create(card, publishableKey, executor, callback);
        }
    }

    public void setDefaultPublishableKey(String publishableKey) throws AuthenticationException {
        validateKey(publishableKey);
        this.defaultPublishableKey = publishableKey;
    }

    private void validateKey(String publishableKey) throws AuthenticationException {
        if (publishableKey == null || publishableKey.length() == 0) {
            throw new AuthenticationException("Invalid Publishable Key: You must use a valid publishable key to create a token.  For more info, see https://stripe.com/docs/stripe.js.", null, Integer.valueOf(0));
        } else if (publishableKey.startsWith("sk_")) {
            throw new AuthenticationException("Invalid Publishable Key: You are using a secret key to create a token, instead of the publishable one. For more info, see https://stripe.com/docs/stripe.js", null, Integer.valueOf(0));
        }
    }

    private void tokenTaskPostExecution(ResponseWrapper result, TokenCallback callback) {
        if (result.token != null) {
            callback.onSuccess(result.token);
        } else if (result.error != null) {
            callback.onError(result.error);
        } else {
            callback.onError(new RuntimeException("Somehow got neither a token response or an error response"));
        }
    }

    private void executeTokenTask(Executor executor, AsyncTask<Void, Void, ResponseWrapper> task) {
        if (executor == null || VERSION.SDK_INT <= 11) {
            task.execute(new Void[0]);
        } else {
            task.executeOnExecutor(executor, new Void[0]);
        }
    }
}
