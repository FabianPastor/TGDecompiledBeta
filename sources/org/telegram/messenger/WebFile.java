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

    public static WebFile createWithGeoPoint(GeoPoint geoPoint, int i, int i2, int i3, int i4) {
        return createWithGeoPoint(geoPoint.lat, geoPoint._long, geoPoint.access_hash, i, i2, i3, i4);
    }

    public static WebFile createWithGeoPoint(double d, double d2, long j, int i, int i2, int i3, int i4) {
        WebFile webFile = new WebFile();
        TL_inputWebFileGeoPointLocation tL_inputWebFileGeoPointLocation = new TL_inputWebFileGeoPointLocation();
        webFile.location = tL_inputWebFileGeoPointLocation;
        TL_inputGeoPoint tL_inputGeoPoint = new TL_inputGeoPoint();
        webFile.geo_point = tL_inputGeoPoint;
        tL_inputWebFileGeoPointLocation.geo_point = tL_inputGeoPoint;
        tL_inputWebFileGeoPointLocation.access_hash = j;
        InputGeoPoint inputGeoPoint = webFile.geo_point;
        inputGeoPoint.lat = d;
        inputGeoPoint._long = d2;
        webFile.w = i;
        tL_inputWebFileGeoPointLocation.w = i;
        webFile.h = i2;
        tL_inputWebFileGeoPointLocation.h = i2;
        webFile.zoom = i3;
        tL_inputWebFileGeoPointLocation.zoom = i3;
        webFile.scale = i4;
        tL_inputWebFileGeoPointLocation.scale = i4;
        webFile.mime_type = "image/png";
        webFile.url = String.format(Locale.US, "maps_%.6f_%.6f_%d_%d_%d_%d.png", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        webFile.attributes = new ArrayList();
        return webFile;
    }

    public static WebFile createWithWebDocument(WebDocument webDocument) {
        WebFile webFile = new WebFile();
        TL_webDocument tL_webDocument = (TL_webDocument) webDocument;
        TL_inputWebFileLocation tL_inputWebFileLocation = new TL_inputWebFileLocation();
        webFile.location = tL_inputWebFileLocation;
        String str = webDocument.url;
        webFile.url = str;
        tL_inputWebFileLocation.url = str;
        tL_inputWebFileLocation.access_hash = tL_webDocument.access_hash;
        webFile.size = tL_webDocument.size;
        webFile.mime_type = tL_webDocument.mime_type;
        webFile.attributes = tL_webDocument.attributes;
        return webFile;
    }
}
