package org.telegram.tgnet;

public abstract class TLRPC$PhoneCallDiscardReason extends TLObject {
    public byte[] encrypted_key;

    public static TLRPC$PhoneCallDiscardReason TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason;
        switch (i) {
            case -2048646399:
                tLRPC$PhoneCallDiscardReason = new TLRPC$TL_phoneCallDiscardReasonMissed();
                break;
            case -1344096199:
                tLRPC$PhoneCallDiscardReason = new TLRPC$TL_phoneCallDiscardReasonAllowGroupCall();
                break;
            case -527056480:
                tLRPC$PhoneCallDiscardReason = new TLRPC$TL_phoneCallDiscardReasonDisconnect();
                break;
            case -84416311:
                tLRPC$PhoneCallDiscardReason = new TLRPC$TL_phoneCallDiscardReasonBusy();
                break;
            case 1471006352:
                tLRPC$PhoneCallDiscardReason = new TLRPC$TL_phoneCallDiscardReasonHangup();
                break;
            default:
                tLRPC$PhoneCallDiscardReason = null;
                break;
        }
        if (tLRPC$PhoneCallDiscardReason != null || !z) {
            if (tLRPC$PhoneCallDiscardReason != null) {
                tLRPC$PhoneCallDiscardReason.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhoneCallDiscardReason;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhoneCallDiscardReason", new Object[]{Integer.valueOf(i)}));
    }
}
