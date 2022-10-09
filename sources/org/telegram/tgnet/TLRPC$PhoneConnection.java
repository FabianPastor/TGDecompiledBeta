package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PhoneConnection extends TLObject {
    public int flags;
    public long id;
    public String ip;
    public String ipv6;
    public String password;
    public byte[] peer_tag;
    public int port;
    public boolean stun;
    public boolean tcp;
    public boolean turn;
    public String username;

    public static TLRPC$PhoneConnection TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhoneConnection tLRPC$PhoneConnection;
        if (i == -NUM) {
            tLRPC$PhoneConnection = new TLRPC$PhoneConnection() { // from class: org.telegram.tgnet.TLRPC$TL_phoneConnection
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    boolean z3 = true;
                    if ((readInt32 & 1) == 0) {
                        z3 = false;
                    }
                    this.tcp = z3;
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.ip = abstractSerializedData2.readString(z2);
                    this.ipv6 = abstractSerializedData2.readString(z2);
                    this.port = abstractSerializedData2.readInt32(z2);
                    this.peer_tag = abstractSerializedData2.readByteArray(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.tcp ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    abstractSerializedData2.writeInt32(i2);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeString(this.ip);
                    abstractSerializedData2.writeString(this.ipv6);
                    abstractSerializedData2.writeInt32(this.port);
                    abstractSerializedData2.writeByteArray(this.peer_tag);
                }
            };
        } else {
            tLRPC$PhoneConnection = i != NUM ? null : new TLRPC$PhoneConnection() { // from class: org.telegram.tgnet.TLRPC$TL_phoneConnectionWebrtc
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    boolean z3 = false;
                    this.turn = (readInt32 & 1) != 0;
                    if ((readInt32 & 2) != 0) {
                        z3 = true;
                    }
                    this.stun = z3;
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.ip = abstractSerializedData2.readString(z2);
                    this.ipv6 = abstractSerializedData2.readString(z2);
                    this.port = abstractSerializedData2.readInt32(z2);
                    this.username = abstractSerializedData2.readString(z2);
                    this.password = abstractSerializedData2.readString(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.turn ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    int i3 = this.stun ? i2 | 2 : i2 & (-3);
                    this.flags = i3;
                    abstractSerializedData2.writeInt32(i3);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeString(this.ip);
                    abstractSerializedData2.writeString(this.ipv6);
                    abstractSerializedData2.writeInt32(this.port);
                    abstractSerializedData2.writeString(this.username);
                    abstractSerializedData2.writeString(this.password);
                }
            };
        }
        if (tLRPC$PhoneConnection != null || !z) {
            if (tLRPC$PhoneConnection != null) {
                tLRPC$PhoneConnection.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhoneConnection;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhoneConnection", Integer.valueOf(i)));
    }
}
