package org.telegram.tgnet;

public class TLRPC$TL_phoneCall extends TLRPC$PhoneCall {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.p2p_allowed = (readInt32 & 32) != 0;
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.date = abstractSerializedData.readInt32(z);
        this.admin_id = abstractSerializedData.readInt32(z);
        this.participant_id = abstractSerializedData.readInt32(z);
        this.g_a_or_b = abstractSerializedData.readByteArray(z);
        this.key_fingerprint = abstractSerializedData.readInt64(z);
        this.protocol = TLRPC$PhoneCallProtocol.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_phoneConnection TLdeserialize = TLRPC$TL_phoneConnection.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.connections.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.start_date = abstractSerializedData.readInt32(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.p2p_allowed ? this.flags | 32 : this.flags & -33;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.admin_id);
        abstractSerializedData.writeInt32(this.participant_id);
        abstractSerializedData.writeByteArray(this.g_a_or_b);
        abstractSerializedData.writeInt64(this.key_fingerprint);
        this.protocol.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.connections.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.connections.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.start_date);
    }
}
