package com.stripe.android.model;

import java.util.Date;

public class Token {
    private final Card mCard;
    private final Date mCreated;
    private final String mId;
    private final boolean mLivemode;
    private final String mType;
    private final boolean mUsed;

    public Token(String id, boolean livemode, Date created, Boolean used, Card card, String type) {
        this.mId = id;
        this.mType = type;
        this.mCreated = created;
        this.mLivemode = livemode;
        this.mCard = card;
        this.mUsed = used.booleanValue();
    }

    public String getId() {
        return this.mId;
    }

    public String getType() {
        return this.mType;
    }

    public Card getCard() {
        return this.mCard;
    }
}
