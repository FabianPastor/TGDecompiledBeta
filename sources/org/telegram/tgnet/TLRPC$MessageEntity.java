package org.telegram.tgnet;

public abstract class TLRPC$MessageEntity extends TLObject {
    public String language;
    public int length;
    public int offset;
    public String url;

    public static TLRPC$MessageEntity TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageEntity tLRPC$MessageEntity;
        switch (i) {
            case -2106619040:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityItalic();
                break;
            case -1687559349:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityPhone();
                break;
            case -1672577397:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityUnderline();
                break;
            case -1148011883:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityUnknown();
                break;
            case -1117713463:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityBold();
                break;
            case -1090087980:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityStrike();
                break;
            case -925956616:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityCustomEmoji();
                break;
            case -595914432:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityMentionName();
                break;
            case -100378723:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityMention();
                break;
            case 34469328:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityBlockquote();
                break;
            case 546203849:
                tLRPC$MessageEntity = new TLRPC$TL_inputMessageEntityMentionName();
                break;
            case 681706865:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityCode();
                break;
            case 852137487:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntitySpoiler();
                break;
            case 892193368:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityMentionName_layer131();
                break;
            case 1280209983:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityCashtag();
                break;
            case 1592721940:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityAnimatedEmoji();
                break;
            case 1692693954:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityEmail();
                break;
            case 1827637959:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityBotCommand();
                break;
            case 1859134776:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityUrl();
                break;
            case 1868782349:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityHashtag();
                break;
            case 1938967520:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityPre();
                break;
            case 1981704948:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityBankCard();
                break;
            case 1990644519:
                tLRPC$MessageEntity = new TLRPC$TL_messageEntityTextUrl();
                break;
            default:
                tLRPC$MessageEntity = null;
                break;
        }
        if (tLRPC$MessageEntity != null || !z) {
            if (tLRPC$MessageEntity != null) {
                tLRPC$MessageEntity.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageEntity;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageEntity", new Object[]{Integer.valueOf(i)}));
    }
}
