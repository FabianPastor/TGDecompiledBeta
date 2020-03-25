package org.telegram.tgnet;

public abstract class TLRPC$PageBlock extends TLObject {
    public boolean bottom;
    public boolean first;
    public int groupId;
    public int level;
    public int mid;
    public TLRPC$PhotoSize thumb;
    public TLObject thumbObject;

    public static TLRPC$PageBlock TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PageBlock tLRPC$PageBlock;
        switch (i) {
            case -2143067670:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockAudio();
                break;
            case -1879401953:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockSubtitle();
                break;
            case -1702174239:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockOrderedList();
                break;
            case -1538310410:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockMap();
                break;
            case -1468953147:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockEmbed();
                break;
            case -1162877472:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockAuthorDate();
                break;
            case -1085412734:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockTable();
                break;
            case -1076861716:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockHeader();
                break;
            case -1066346178:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockPreformatted();
                break;
            case -840826671:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockEmbed_layer82();
                break;
            case -837994576:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockAnchor();
                break;
            case -650782469:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockEmbed_layer60();
                break;
            case -640214938:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockVideo_layer82();
                break;
            case -618614392:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockDivider();
                break;
            case -454524911:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockList();
                break;
            case -372860542:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockPhoto_layer82();
                break;
            case -283684427:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockChannel();
                break;
            case -248793375:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockSubheader();
                break;
            case -229005301:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockEmbedPost();
                break;
            case 52401552:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockSlideshow();
                break;
            case 145955919:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockCollage_layer82();
                break;
            case 319588707:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockSlideshow_layer82();
                break;
            case 324435594:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockUnsupported();
                break;
            case 370236054:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockRelatedArticles();
                break;
            case 391759200:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockPhoto();
                break;
            case 504660880:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockKicker();
                break;
            case 641563686:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockBlockquote();
                break;
            case 690781161:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockEmbedPost_layer82();
                break;
            case 834148991:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockAudio_layer82();
                break;
            case 972174080:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockCover();
                break;
            case 978896884:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockList_layer82();
                break;
            case 1029399794:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockAuthorDate_layer60();
                break;
            case 1182402406:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockParagraph();
                break;
            case 1216809369:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockFooter();
                break;
            case 1329878739:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockPullquote();
                break;
            case 1705048653:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockCollage();
                break;
            case 1890305021:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockTitle();
                break;
            case 1987480557:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockDetails();
                break;
            case 2089805750:
                tLRPC$PageBlock = new TLRPC$TL_pageBlockVideo();
                break;
            default:
                tLRPC$PageBlock = null;
                break;
        }
        if (tLRPC$PageBlock != null || !z) {
            if (tLRPC$PageBlock != null) {
                tLRPC$PageBlock.readParams(abstractSerializedData, z);
            }
            return tLRPC$PageBlock;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PageBlock", new Object[]{Integer.valueOf(i)}));
    }
}
