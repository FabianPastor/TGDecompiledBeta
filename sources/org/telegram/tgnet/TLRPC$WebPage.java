package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$WebPage extends TLObject {
    public ArrayList<TLRPC$TL_webPageAttributeTheme> attributes = new ArrayList<>();
    public String author;
    public TLRPC$Page cached_page;
    public int date;
    public String description;
    public String display_url;
    public TLRPC$Document document;
    public int duration;
    public int embed_height;
    public String embed_type;
    public String embed_url;
    public int embed_width;
    public int flags;
    public int hash;
    public long id;
    public TLRPC$Photo photo;
    public String site_name;
    public String title;
    public String type;
    public String url;

    public static TLRPC$WebPage TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$WebPage tLRPC$WebPage;
        switch (i) {
            case -2054908813:
                tLRPC$WebPage = new TLRPC$TL_webPageNotModified_layer110();
                break;
            case -1558273867:
                tLRPC$WebPage = new TLRPC$TL_webPage_old();
                break;
            case -981018084:
                tLRPC$WebPage = new TLRPC$TL_webPagePending();
                break;
            case -897446185:
                tLRPC$WebPage = new TLRPC$TL_webPage_layer58();
                break;
            case -736472729:
                tLRPC$WebPage = new TLRPC$TL_webPageUrlPending();
                break;
            case -392411726:
                tLRPC$WebPage = new TLRPC$TL_webPage();
                break;
            case -350980120:
                tLRPC$WebPage = new TLRPC$TL_webPageEmpty();
                break;
            case -94051982:
                tLRPC$WebPage = new TLRPC$TL_webPage_layer107();
                break;
            case 1594340540:
                tLRPC$WebPage = new TLRPC$TL_webPage_layer104();
                break;
            case 1930545681:
                tLRPC$WebPage = new TLRPC$TL_webPageNotModified();
                break;
            default:
                tLRPC$WebPage = null;
                break;
        }
        if (tLRPC$WebPage != null || !z) {
            if (tLRPC$WebPage != null) {
                tLRPC$WebPage.readParams(abstractSerializedData, z);
            }
            return tLRPC$WebPage;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WebPage", new Object[]{Integer.valueOf(i)}));
    }
}
