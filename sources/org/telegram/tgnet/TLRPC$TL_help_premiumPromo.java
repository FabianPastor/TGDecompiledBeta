package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_help_premiumPromo extends TLObject {
    public static int constructor = -NUM;
    public String currency;
    public long monthly_amount;
    public ArrayList<TLRPC$MessageEntity> status_entities = new ArrayList<>();
    public String status_text;
    public ArrayList<TLRPC$User> users = new ArrayList<>();
    public ArrayList<String> video_sections = new ArrayList<>();
    public ArrayList<TLRPC$Document> videos = new ArrayList<>();

    public static TLRPC$TL_help_premiumPromo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo;
        if (i != -NUM) {
            tLRPC$TL_help_premiumPromo = i != -NUM ? null : new TLRPC$TL_help_premiumPromo_layer140();
        } else {
            tLRPC$TL_help_premiumPromo = new TLRPC$TL_help_premiumPromo();
        }
        if (tLRPC$TL_help_premiumPromo != null || !z) {
            if (tLRPC$TL_help_premiumPromo != null) {
                tLRPC$TL_help_premiumPromo.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_help_premiumPromo;
        }
        throw new RuntimeException(String.format("can't parse magic %x in TL_help_premiumPromo", new Object[]{Integer.valueOf(i)}));
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.status_text = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt322) {
                TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.status_entities.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                for (int i3 = 0; i3 < readInt324; i3++) {
                    this.video_sections.add(abstractSerializedData.readString(z));
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                if (readInt325 == NUM) {
                    int readInt326 = abstractSerializedData.readInt32(z);
                    int i4 = 0;
                    while (i4 < readInt326) {
                        TLRPC$Document TLdeserialize2 = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize2 != null) {
                            this.videos.add(TLdeserialize2);
                            i4++;
                        } else {
                            return;
                        }
                    }
                    this.currency = abstractSerializedData.readString(z);
                    this.monthly_amount = abstractSerializedData.readInt64(z);
                    int readInt327 = abstractSerializedData.readInt32(z);
                    if (readInt327 == NUM) {
                        int readInt328 = abstractSerializedData.readInt32(z);
                        while (i < readInt328) {
                            TLRPC$User TLdeserialize3 = TLRPC$User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                            if (TLdeserialize3 != null) {
                                this.users.add(TLdeserialize3);
                                i++;
                            } else {
                                return;
                            }
                        }
                    } else if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt327)}));
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt325)}));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.status_text);
        abstractSerializedData.writeInt32(NUM);
        int size = this.status_entities.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.status_entities.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.video_sections.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            abstractSerializedData.writeString(this.video_sections.get(i2));
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.videos.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            this.videos.get(i3).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.currency);
        abstractSerializedData.writeInt64(this.monthly_amount);
        abstractSerializedData.writeInt32(NUM);
        int size4 = this.users.size();
        abstractSerializedData.writeInt32(size4);
        for (int i4 = 0; i4 < size4; i4++) {
            this.users.get(i4).serializeToStream(abstractSerializedData);
        }
    }
}
