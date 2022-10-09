package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$RecentMeUrl extends TLObject {
    public long chat_id;
    public TLRPC$ChatInvite chat_invite;
    public TLRPC$StickerSetCovered set;
    public String url;
    public long user_id;

    public static TLRPC$RecentMeUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$RecentMeUrl tLRPC$RecentMeUrl;
        switch (i) {
            case -1294306862:
                tLRPC$RecentMeUrl = new TLRPC$RecentMeUrl() { // from class: org.telegram.tgnet.TLRPC$TL_recentMeUrlChat
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.url = abstractSerializedData2.readString(z2);
                        this.chat_id = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.url);
                        abstractSerializedData2.writeInt64(this.chat_id);
                    }
                };
                break;
            case -1188296222:
                tLRPC$RecentMeUrl = new TLRPC$RecentMeUrl() { // from class: org.telegram.tgnet.TLRPC$TL_recentMeUrlUser
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.url = abstractSerializedData2.readString(z2);
                        this.user_id = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.url);
                        abstractSerializedData2.writeInt64(this.user_id);
                    }
                };
                break;
            case -1140172836:
                tLRPC$RecentMeUrl = new TLRPC$RecentMeUrl() { // from class: org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.url = abstractSerializedData2.readString(z2);
                        this.set = TLRPC$StickerSetCovered.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.url);
                        this.set.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -347535331:
                tLRPC$RecentMeUrl = new TLRPC$RecentMeUrl() { // from class: org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.url = abstractSerializedData2.readString(z2);
                        this.chat_invite = TLRPC$ChatInvite.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.url);
                        this.chat_invite.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 1189204285:
                tLRPC$RecentMeUrl = new TLRPC$RecentMeUrl() { // from class: org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.url = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.url);
                    }
                };
                break;
            default:
                tLRPC$RecentMeUrl = null;
                break;
        }
        if (tLRPC$RecentMeUrl != null || !z) {
            if (tLRPC$RecentMeUrl != null) {
                tLRPC$RecentMeUrl.readParams(abstractSerializedData, z);
            }
            return tLRPC$RecentMeUrl;
        }
        throw new RuntimeException(String.format("can't parse magic %x in RecentMeUrl", Integer.valueOf(i)));
    }
}
