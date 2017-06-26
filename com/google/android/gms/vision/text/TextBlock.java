package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.internal.fd;
import com.google.android.gms.internal.fj;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.telegram.tgnet.ConnectionsManager;

public class TextBlock implements Text {
    private Point[] cornerPoints;
    private fj[] zzbNO;
    private List<Line> zzbNP;
    private String zzbNQ;
    private Rect zzbNR;

    TextBlock(SparseArray<fj> sparseArray) {
        this.zzbNO = new fj[sparseArray.size()];
        for (int i = 0; i < this.zzbNO.length; i++) {
            this.zzbNO[i] = (fj) sparseArray.valueAt(i);
        }
    }

    private static Point[] zza(int i, int i2, int i3, int i4, fd fdVar) {
        int i5 = fdVar.left;
        int i6 = fdVar.top;
        double sin = Math.sin(Math.toRadians((double) fdVar.zzbNU));
        double cos = Math.cos(Math.toRadians((double) fdVar.zzbNU));
        Point[] pointArr = new Point[]{new Point(i, i2), new Point(i3, i2), new Point(i3, i4), new Point(i, i4)};
        for (int i7 = 0; i7 < 4; i7++) {
            int i8 = (int) ((((double) pointArr[i7].x) * sin) + (((double) pointArr[i7].y) * cos));
            pointArr[i7].x = (int) ((((double) pointArr[i7].x) * cos) - (((double) pointArr[i7].y) * sin));
            pointArr[i7].y = i8;
            pointArr[i7].offset(i5, i6);
        }
        return pointArr;
    }

    public Rect getBoundingBox() {
        if (this.zzbNR == null) {
            this.zzbNR = zzc.zza((Text) this);
        }
        return this.zzbNR;
    }

    public List<? extends Text> getComponents() {
        if (this.zzbNO.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbNP == null) {
            this.zzbNP = new ArrayList(this.zzbNO.length);
            for (fj line : this.zzbNO) {
                this.zzbNP.add(new Line(line));
            }
        }
        return this.zzbNP;
    }

    public Point[] getCornerPoints() {
        if (this.cornerPoints == null) {
            if (this.zzbNO.length == 0) {
                this.cornerPoints = new Point[0];
            } else {
                int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                int i2 = Integer.MIN_VALUE;
                int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                int i4 = Integer.MIN_VALUE;
                for (fj fjVar : this.zzbNO) {
                    fd fdVar = fjVar.zzbNW;
                    fd fdVar2 = this.zzbNO[0].zzbNW;
                    int i5 = -fdVar2.left;
                    int i6 = -fdVar2.top;
                    double sin = Math.sin(Math.toRadians((double) fdVar2.zzbNU));
                    double cos = Math.cos(Math.toRadians((double) fdVar2.zzbNU));
                    Point[] pointArr = new Point[4];
                    pointArr[0] = new Point(fdVar.left, fdVar.top);
                    pointArr[0].offset(i5, i6);
                    int i7 = (int) ((((double) pointArr[0].x) * cos) + (((double) pointArr[0].y) * sin));
                    i5 = (int) ((sin * ((double) (-pointArr[0].x))) + (cos * ((double) pointArr[0].y)));
                    pointArr[0].x = i7;
                    pointArr[0].y = i5;
                    pointArr[1] = new Point(fdVar.width + i7, i5);
                    pointArr[2] = new Point(fdVar.width + i7, fdVar.height + i5);
                    pointArr[3] = new Point(i7, fdVar.height + i5);
                    int i8 = 0;
                    while (i8 < 4) {
                        Point point = pointArr[i8];
                        i7 = Math.min(i, point.x);
                        i = Math.max(i2, point.x);
                        i2 = Math.min(i3, point.y);
                        i8++;
                        i4 = Math.max(i4, point.y);
                        i3 = i2;
                        i2 = i;
                        i = i7;
                    }
                }
                this.cornerPoints = zza(i, i3, i2, i4, this.zzbNO[0].zzbNW);
            }
        }
        return this.cornerPoints;
    }

    public String getLanguage() {
        if (this.zzbNQ != null) {
            return this.zzbNQ;
        }
        HashMap hashMap = new HashMap();
        for (fj fjVar : this.zzbNO) {
            hashMap.put(fjVar.zzbNQ, Integer.valueOf((hashMap.containsKey(fjVar.zzbNQ) ? ((Integer) hashMap.get(fjVar.zzbNQ)).intValue() : 0) + 1));
        }
        this.zzbNQ = (String) ((Entry) Collections.max(hashMap.entrySet(), new zza(this))).getKey();
        if (this.zzbNQ == null || this.zzbNQ.isEmpty()) {
            this.zzbNQ = "und";
        }
        return this.zzbNQ;
    }

    public String getValue() {
        if (this.zzbNO.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(this.zzbNO[0].zzbNZ);
        for (int i = 1; i < this.zzbNO.length; i++) {
            stringBuilder.append("\n");
            stringBuilder.append(this.zzbNO[i].zzbNZ);
        }
        return stringBuilder.toString();
    }
}
