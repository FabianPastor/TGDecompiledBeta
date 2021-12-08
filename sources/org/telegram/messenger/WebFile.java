package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Locale;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class WebFile extends TLObject {
    public ArrayList<TLRPC.DocumentAttribute> attributes;
    public TLRPC.InputGeoPoint geo_point;
    public int h;
    public TLRPC.InputWebFileLocation location;
    public String mime_type;
    public int msg_id;
    public TLRPC.InputPeer peer;
    public int scale;
    public int size;
    public String url;
    public int w;
    public int zoom;

    public static WebFile createWithGeoPoint(TLRPC.GeoPoint point, int w2, int h2, int zoom2, int scale2) {
        return createWithGeoPoint(point.lat, point._long, point.access_hash, w2, h2, zoom2, scale2);
    }

    public static WebFile createWithGeoPoint(double lat, double _long, long access_hash, int w2, int h2, int zoom2, int scale2) {
        WebFile webFile = new WebFile();
        TLRPC.TL_inputWebFileGeoPointLocation location2 = new TLRPC.TL_inputWebFileGeoPointLocation();
        webFile.location = location2;
        TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
        webFile.geo_point = tL_inputGeoPoint;
        location2.geo_point = tL_inputGeoPoint;
        location2.access_hash = access_hash;
        webFile.geo_point.lat = lat;
        webFile.geo_point._long = _long;
        webFile.w = w2;
        location2.w = w2;
        webFile.h = h2;
        location2.h = h2;
        webFile.zoom = zoom2;
        location2.zoom = zoom2;
        webFile.scale = scale2;
        location2.scale = scale2;
        webFile.mime_type = "image/png";
        webFile.url = String.format(Locale.US, "maps_%.6f_%.6f_%d_%d_%d_%d.png", new Object[]{Double.valueOf(lat), Double.valueOf(_long), Integer.valueOf(w2), Integer.valueOf(h2), Integer.valueOf(zoom2), Integer.valueOf(scale2)});
        webFile.attributes = new ArrayList<>();
        return webFile;
    }

    public static WebFile createWithWebDocument(TLRPC.WebDocument webDocument) {
        if (!(webDocument instanceof TLRPC.TL_webDocument)) {
            return null;
        }
        WebFile webFile = new WebFile();
        TLRPC.TL_webDocument document = (TLRPC.TL_webDocument) webDocument;
        TLRPC.TL_inputWebFileLocation location2 = new TLRPC.TL_inputWebFileLocation();
        webFile.location = location2;
        String str = webDocument.url;
        webFile.url = str;
        location2.url = str;
        location2.access_hash = document.access_hash;
        webFile.size = document.size;
        webFile.mime_type = document.mime_type;
        webFile.attributes = document.attributes;
        return webFile;
    }
}
