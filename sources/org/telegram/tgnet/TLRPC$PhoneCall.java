package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$PhoneCall extends TLObject {
    public long access_hash;
    public int admin_id;
    public ArrayList<TLRPC$TL_phoneConnection> connections = new ArrayList<>();
    public int date;
    public int duration;
    public int flags;
    public byte[] g_a_hash;
    public byte[] g_a_or_b;
    public byte[] g_b;
    public long id;
    public long key_fingerprint;
    public boolean need_debug;
    public boolean need_rating;
    public boolean p2p_allowed;
    public int participant_id;
    public TLRPC$PhoneCallProtocol protocol;
    public TLRPC$PhoneCallDiscardReason reason;
    public int receive_date;
    public int start_date;
    public boolean video;

    public static TLRPC$PhoneCall TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhoneCall tLRPC$PhoneCall;
        switch (i) {
            case -2025673089:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCall();
                break;
            case -2014659757:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCallRequested();
                break;
            case -1719909046:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCallAccepted();
                break;
            case 462375633:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCallWaiting();
                break;
            case 1355435489:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCallDiscarded();
                break;
            case 1399245077:
                tLRPC$PhoneCall = new TLRPC$TL_phoneCallEmpty();
                break;
            default:
                tLRPC$PhoneCall = null;
                break;
        }
        if (tLRPC$PhoneCall != null || !z) {
            if (tLRPC$PhoneCall != null) {
                tLRPC$PhoneCall.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhoneCall;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhoneCall", new Object[]{Integer.valueOf(i)}));
    }
}
