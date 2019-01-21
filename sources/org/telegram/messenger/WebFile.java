package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Locale;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.InputGeoPoint;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputWebFileGeoPointLocation;
import org.telegram.tgnet.TLRPC.TL_inputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.WebDocument;

public class WebFile extends TLObject {
    public ArrayList<DocumentAttribute> attributes;
    public InputGeoPoint geo_point;
    public int h;
    public InputWebFileLocation location;
    public String mime_type;
    public int msg_id;
    public InputPeer peer;
    public int scale;
    public int size;
    public String url;
    public int w;
    public int zoom;

    public static WebFile createWithGeoPoint(GeoPoint point, int w, int h, int zoom, int scale) {
        return createWithGeoPoint(point.lat, point._long, point.access_hash, w, h, zoom, scale);
    }

    public static WebFile createWithGeoPoint(double lat, double _long, long access_hash, int w, int h, int zoom, int scale) {
        WebFile webFile = new WebFile();
        TL_inputWebFileGeoPointLocation location = new TL_inputWebFileGeoPointLocation();
        webFile.location = location;
        InputGeoPoint tL_inputGeoPoint = new TL_inputGeoPoint();
        webFile.geo_point = tL_inputGeoPoint;
        location.geo_point = tL_inputGeoPoint;
        location.access_hash = access_hash;
        webFile.geo_point.lat = lat;
        webFile.geo_point._long = _long;
        webFile.w = w;
        location.w = w;
        webFile.h = h;
        location.h = h;
        webFile.zoom = zoom;
        location.zoom = zoom;
        webFile.scale = scale;
        location.scale = scale;
        webFile.mime_type = "image/png";
        webFile.url = String.format(Locale.US, "maps_%.6f_%.6f_%d_%d_%d_%d.png", new Object[]{Double.valueOf(lat), Double.valueOf(_long), Integer.valueOf(w), Integer.valueOf(h), Integer.valueOf(zoom), Integer.valueOf(scale)});
        webFile.attributes = new ArrayList();
        return webFile;
    }

    public static WebFile createWithWebDocument(WebDocument webDocument) {
        WebFile webFile = new WebFile();
        TL_webDocument document = (TL_webDocument) webDocument;
        TL_inputWebFileLocation location = new TL_inputWebFileLocation();
        webFile.location = location;
        String str = webDocument.url;
        webFile.url = str;
        location.url = str;
        location.access_hash = document.access_hash;
        webFile.size = document.size;
        webFile.mime_type = document.mime_type;
        webFile.attributes = document.attributes;
        return webFile;
    }
}
