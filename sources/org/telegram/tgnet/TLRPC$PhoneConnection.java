package org.telegram.tgnet;

public abstract class TLRPC$PhoneConnection extends TLObject {
    public int flags;
    public long id;
    public String ip;
    public String ipv6;
    public String password;
    public byte[] peer_tag;
    public int port;
    public boolean stun;
    public boolean turn;
    public String username;

    public static TLRPC$PhoneConnection TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhoneConnection tLRPC$PhoneConnection;
        if (i != -NUM) {
            tLRPC$PhoneConnection = i != NUM ? null : new TLRPC$TL_phoneConnectionWebrtc();
        } else {
            tLRPC$PhoneConnection = new TLRPC$TL_phoneConnection();
        }
        if (tLRPC$PhoneConnection != null || !z) {
            if (tLRPC$PhoneConnection != null) {
                tLRPC$PhoneConnection.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhoneConnection;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhoneConnection", new Object[]{Integer.valueOf(i)}));
    }
}
