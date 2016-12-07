package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.internal.zzbhg;
import com.google.android.gms.internal.zzbhk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.telegram.tgnet.ConnectionsManager;

public class TextBlock implements Text {
    private Point[] cornerPoints;
    private zzbhk[] zzbNu;
    private List<Line> zzbNv;
    private String zzbNw;
    private Rect zzbNx;

    TextBlock(SparseArray<zzbhk> sparseArray) {
        this.zzbNu = new zzbhk[sparseArray.size()];
        for (int i = 0; i < this.zzbNu.length; i++) {
            this.zzbNu[i] = (zzbhk) sparseArray.valueAt(i);
        }
    }

    private static Point[] zza(int i, int i2, int i3, int i4, zzbhg com_google_android_gms_internal_zzbhg) {
        int i5 = com_google_android_gms_internal_zzbhg.left;
        int i6 = com_google_android_gms_internal_zzbhg.top;
        double sin = Math.sin(Math.toRadians((double) com_google_android_gms_internal_zzbhg.zzbNA));
        double cos = Math.cos(Math.toRadians((double) com_google_android_gms_internal_zzbhg.zzbNA));
        Point[] pointArr = new Point[]{new Point(i, i2), new Point(i3, i2), new Point(i3, i4), new Point(i, i4)};
        for (int i7 = 0; i7 < 4; i7++) {
            int i8 = (int) ((((double) pointArr[i7].x) * sin) + (((double) pointArr[i7].y) * cos));
            pointArr[i7].x = (int) ((((double) pointArr[i7].x) * cos) - (((double) pointArr[i7].y) * sin));
            pointArr[i7].y = i8;
            pointArr[i7].offset(i5, i6);
        }
        return pointArr;
    }

    private static Point[] zza(zzbhg com_google_android_gms_internal_zzbhg, zzbhg com_google_android_gms_internal_zzbhg2) {
        int i = -com_google_android_gms_internal_zzbhg2.left;
        int i2 = -com_google_android_gms_internal_zzbhg2.top;
        double sin = Math.sin(Math.toRadians((double) com_google_android_gms_internal_zzbhg2.zzbNA));
        double cos = Math.cos(Math.toRadians((double) com_google_android_gms_internal_zzbhg2.zzbNA));
        Point[] pointArr = new Point[4];
        pointArr[0] = new Point(com_google_android_gms_internal_zzbhg.left, com_google_android_gms_internal_zzbhg.top);
        pointArr[0].offset(i, i2);
        i = (int) ((((double) pointArr[0].x) * cos) + (((double) pointArr[0].y) * sin));
        i2 = (int) ((sin * ((double) (-pointArr[0].x))) + (cos * ((double) pointArr[0].y)));
        pointArr[0].x = i;
        pointArr[0].y = i2;
        pointArr[1] = new Point(com_google_android_gms_internal_zzbhg.width + i, i2);
        pointArr[2] = new Point(com_google_android_gms_internal_zzbhg.width + i, com_google_android_gms_internal_zzbhg.height + i2);
        pointArr[3] = new Point(i, i2 + com_google_android_gms_internal_zzbhg.height);
        return pointArr;
    }

    public Rect getBoundingBox() {
        if (this.zzbNx == null) {
            this.zzbNx = zza.zza((Text) this);
        }
        return this.zzbNx;
    }

    public List<? extends Text> getComponents() {
        return zzSt();
    }

    public Point[] getCornerPoints() {
        if (this.cornerPoints == null) {
            zzSs();
        }
        return this.cornerPoints;
    }

    public String getLanguage() {
        if (this.zzbNw != null) {
            return this.zzbNw;
        }
        HashMap hashMap = new HashMap();
        for (zzbhk com_google_android_gms_internal_zzbhk : this.zzbNu) {
            hashMap.put(com_google_android_gms_internal_zzbhk.zzbNw, Integer.valueOf((hashMap.containsKey(com_google_android_gms_internal_zzbhk.zzbNw) ? ((Integer) hashMap.get(com_google_android_gms_internal_zzbhk.zzbNw)).intValue() : 0) + 1));
        }
        this.zzbNw = (String) ((Entry) Collections.max(hashMap.entrySet(), new Comparator<Entry<String, Integer>>(this) {
            public /* synthetic */ int compare(Object obj, Object obj2) {
                return zza((Entry) obj, (Entry) obj2);
            }

            public int zza(Entry<String, Integer> entry, Entry<String, Integer> entry2) {
                return ((Integer) entry.getValue()).compareTo((Integer) entry2.getValue());
            }
        })).getKey();
        if (this.zzbNw == null || this.zzbNw.isEmpty()) {
            this.zzbNw = "und";
        }
        return this.zzbNw;
    }

    public String getValue() {
        if (this.zzbNu.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(this.zzbNu[0].zzbNF);
        for (int i = 1; i < this.zzbNu.length; i++) {
            stringBuilder.append("\n");
            stringBuilder.append(this.zzbNu[i].zzbNF);
        }
        return stringBuilder.toString();
    }

    void zzSs() {
        if (this.zzbNu.length == 0) {
            this.cornerPoints = new Point[0];
            return;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i2 = Integer.MIN_VALUE;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i4 = Integer.MIN_VALUE;
        for (zzbhk com_google_android_gms_internal_zzbhk : this.zzbNu) {
            Point[] zza = zza(com_google_android_gms_internal_zzbhk.zzbNC, this.zzbNu[0].zzbNC);
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
        this.cornerPoints = zza(i3, i, i2, i4, this.zzbNu[0].zzbNC);
    }

    List<Line> zzSt() {
        if (this.zzbNu.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbNv == null) {
            this.zzbNv = new ArrayList(this.zzbNu.length);
            for (zzbhk line : this.zzbNu) {
                this.zzbNv.add(new Line(line));
            }
        }
        return this.zzbNv;
    }
}
