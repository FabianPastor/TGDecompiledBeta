package org.telegram.tgnet;

public abstract class TLRPC$GroupCall extends TLObject {
    public long access_hash;
    public int admin_id;
    public int channel_id;
    public TLRPC$TL_groupCallConnection connection;
    public int duration;
    public byte[] encryption_key;
    public int flags;
    public long id;
    public long key_fingerprint;
    public int participants_count;
    public TLRPC$PhoneCallProtocol protocol;
    public byte[] reflector_group_tag;
    public byte[] reflector_self_secret;
    public byte[] reflector_self_tag;

    public static TLRPC$GroupCall TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$GroupCall tLRPC$GroupCall;
        if (i == NUM) {
            tLRPC$GroupCall = new TLRPC$TL_groupCall();
        } else if (i != NUM) {
            tLRPC$GroupCall = i != NUM ? null : new TLRPC$TL_groupCallDiscarded();
        } else {
            tLRPC$GroupCall = new TLRPC$TL_groupCallPrivate();
        }
        if (tLRPC$GroupCall != null || !z) {
            if (tLRPC$GroupCall != null) {
                tLRPC$GroupCall.readParams(abstractSerializedData, z);
            }
            return tLRPC$GroupCall;
        }
        throw new RuntimeException(String.format("can't parse magic %x in GroupCall", new Object[]{Integer.valueOf(i)}));
    }
}
