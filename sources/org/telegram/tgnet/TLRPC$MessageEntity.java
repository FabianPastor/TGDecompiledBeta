package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$MessageEntity extends TLObject {
    public String language;
    public int length;
    public int offset;
    public String url;

    public static TLRPC$MessageEntity TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageEntity tLRPC$TL_messageEntityItalic;
        switch (i) {
            case -2106619040:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityItalic();
                break;
            case -1687559349:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityPhone
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case -1672577397:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityUnderline();
                break;
            case -1148011883:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityUnknown
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case -1117713463:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityBold();
                break;
            case -1090087980:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityStrike();
                break;
            case -925956616:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityCustomEmoji();
                break;
            case -595914432:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityMentionName();
                break;
            case -100378723:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityMention
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 34469328:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityBlockquote();
                break;
            case 546203849:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_inputMessageEntityMentionName();
                break;
            case 681706865:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityCode();
                break;
            case 852137487:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntitySpoiler();
                break;
            case 892193368:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityMentionName() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityMentionName_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageEntityMentionName, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                        this.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageEntityMentionName, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                    }
                };
                break;
            case 1280209983:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityCashtag
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1592721940:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityAnimatedEmoji
                    public static int constructor = NUM;
                    public int length;
                    public int offset;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1692693954:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityEmail
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1827637959:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityBotCommand
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1859134776:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityUrl();
                break;
            case 1868782349:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityHashtag
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1938967520:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityPre();
                break;
            case 1981704948:
                tLRPC$TL_messageEntityItalic = new TLRPC$MessageEntity() { // from class: org.telegram.tgnet.TLRPC$TL_messageEntityBankCard
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.offset = abstractSerializedData2.readInt32(z2);
                        this.length = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.offset);
                        abstractSerializedData2.writeInt32(this.length);
                    }
                };
                break;
            case 1990644519:
                tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityTextUrl();
                break;
            default:
                tLRPC$TL_messageEntityItalic = null;
                break;
        }
        if (tLRPC$TL_messageEntityItalic != null || !z) {
            if (tLRPC$TL_messageEntityItalic != null) {
                tLRPC$TL_messageEntityItalic.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messageEntityItalic;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageEntity", Integer.valueOf(i)));
    }
}
