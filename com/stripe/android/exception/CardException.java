package com.stripe.android.exception;

public class CardException extends StripeException {
    private String charge;
    private String code;
    private String declineCode;
    private String param;

    public CardException(String message, String requestId, String code, String param, String declineCode, String charge, Integer statusCode, Throwable e) {
        super(message, requestId, statusCode, e);
        this.code = code;
        this.param = param;
        this.declineCode = declineCode;
        this.charge = charge;
    }
}
