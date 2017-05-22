package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbkd;
import com.google.android.gms.internal.zzbkh;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.telegram.tgnet.ConnectionsManager;

public class TextBlock implements Text {
    private Point[] cornerPoints;
    private zzbkh[] zzbPs;
    private List<Line> zzbPt;
    private String zzbPu;
    private Rect zzbPv;

    TextBlock(SparseArray<zzbkh> sparseArray) {
        this.zzbPs = new zzbkh[sparseArray.size()];
        for (int i = 0; i < this.zzbPs.length; i++) {
            this.zzbPs[i] = (zzbkh) sparseArray.valueAt(i);
        }
    }

    private static Point[] zza(int i, int i2, int i3, int i4, zzbkd com_google_android_gms_internal_zzbkd) {
        int i5 = com_google_android_gms_internal_zzbkd.left;
        int i6 = com_google_android_gms_internal_zzbkd.top;
        double sin = Math.sin(Math.toRadians((double) com_google_android_gms_internal_zzbkd.zzbPy));
        double cos = Math.cos(Math.toRadians((double) com_google_android_gms_internal_zzbkd.zzbPy));
        Point[] pointArr = new Point[]{new Point(i, i2), new Point(i3, i2), new Point(i3, i4), new Point(i, i4)};
        for (int i7 = 0; i7 < 4; i7++) {
            int i8 = (int) ((((double) pointArr[i7].x) * sin) + (((double) pointArr[i7].y) * cos));
            pointArr[i7].x = (int) ((((double) pointArr[i7].x) * cos) - (((double) pointArr[i7].y) * sin));
            pointArr[i7].y = i8;
            pointArr[i7].offset(i5, i6);
        }
        return pointArr;
    }

    private static Point[] zza(zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2) {
        int i = -com_google_android_gms_internal_zzbkd2.left;
        int i2 = -com_google_android_gms_internal_zzbkd2.top;
        double sin = Math.sin(Math.toRadians((double) com_google_android_gms_internal_zzbkd2.zzbPy));
        double cos = Math.cos(Math.toRadians((double) com_google_android_gms_internal_zzbkd2.zzbPy));
        Point[] pointArr = new Point[4];
        pointArr[0] = new Point(com_google_android_gms_internal_zzbkd.left, com_google_android_gms_internal_zzbkd.top);
        pointArr[0].offset(i, i2);
        i = (int) ((((double) pointArr[0].x) * cos) + (((double) pointArr[0].y) * sin));
        i2 = (int) ((sin * ((double) (-pointArr[0].x))) + (cos * ((double) pointArr[0].y)));
        pointArr[0].x = i;
        pointArr[0].y = i2;
        pointArr[1] = new Point(com_google_android_gms_internal_zzbkd.width + i, i2);
        pointArr[2] = new Point(com_google_android_gms_internal_zzbkd.width + i, com_google_android_gms_internal_zzbkd.height + i2);
        pointArr[3] = new Point(i, i2 + com_google_android_gms_internal_zzbkd.height);
        return pointArr;
    }

    public Rect getBoundingBox() {
        if (this.zzbPv == null) {
            this.zzbPv = zza.zza((Text) this);
        }
        return this.zzbPv;
    }

    public List<? extends Text> getComponents() {
        return zzTX();
    }

    public Point[] getCornerPoints() {
        if (this.cornerPoints == null) {
            zzTW();
        }
        return this.cornerPoints;
    }

    public String getLanguage() {
        if (this.zzbPu != null) {
            return this.zzbPu;
        }
        HashMap hashMap = new HashMap();
        for (zzbkh com_google_android_gms_internal_zzbkh : this.zzbPs) {
            hashMap.put(com_google_android_gms_internal_zzbkh.zzbPu, Integer.valueOf((hashMap.containsKey(com_google_android_gms_internal_zzbkh.zzbPu) ? ((Integer) hashMap.get(com_google_android_gms_internal_zzbkh.zzbPu)).intValue() : 0) + 1));
        }
        this.zzbPu = (String) ((Entry) Collections.max(hashMap.entrySet(), new Comparator<Entry<String, Integer>>(this) {
            public /* synthetic */ int compare(Object obj, Object obj2) {
                return zza((Entry) obj, (Entry) obj2);
            }

            public int zza(Entry<String, Integer> entry, Entry<String, Integer> entry2) {
                return ((Integer) entry.getValue()).compareTo((Integer) entry2.getValue());
            }
        })).getKey();
        if (this.zzbPu == null || this.zzbPu.isEmpty()) {
            this.zzbPu = "und";
        }
        return this.zzbPu;
    }

    public String getValue() {
        if (this.zzbPs.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(this.zzbPs[0].zzbPD);
        for (int i = 1; i < this.zzbPs.length; i++) {
            stringBuilder.append("\n");
            stringBuilder.append(this.zzbPs[i].zzbPD);
        }
        return stringBuilder.toString();
    }

    void zzTW() {
        if (this.zzbPs.length == 0) {
            this.cornerPoints = new Point[0];
            return;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i2 = Integer.MIN_VALUE;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i4 = Integer.MIN_VALUE;
        for (zzbkh com_google_android_gms_internal_zzbkh : this.zzbPs) {
            Point[] zza = zza(com_google_android_gms_internal_zzbkh.zzbPA, this.zzbPs[0].zzbPA);
            int i5 = 0;
            while (i5 < 4) {
                Point point = zza[i5];
                int min = Math.min(i3, point.x);
                i3 = Math.max(i2, point.x);
                i2 = Math.min(i, point.y);
                i5++;
                i4 = Math.max(i4, point.y);
                i = i2;
                i2 = i3;
                i3 = min;
            }
        }
        this.cornerPoints = zza(i3, i, i2, i4, this.zzbPs[0].zzbPA);
    }

    List<Line> zzTX() {
        if (this.zzbPs.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbPt == null) {
            this.zzbPt = new ArrayList(this.zzbPs.length);
            for (zzbkh line : this.zzbPs) {
                this.zzbPt.add(new Line(line));
            }
        }
        return this.zzbPt;
    }
}
