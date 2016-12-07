package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.BoundingBoxParcel;
import org.telegram.tgnet.ConnectionsManager;

final class zza {
    static Rect zza(Text text) {
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i2 = Integer.MIN_VALUE;
        int i3 = Integer.MIN_VALUE;
        int i4 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (Point point : text.getCornerPoints()) {
            i4 = Math.min(i4, point.x);
            i3 = Math.max(i3, point.x);
            i = Math.min(i, point.y);
            i2 = Math.max(i2, point.y);
        }
        return new Rect(i4, i, i3, i2);
    }

    static Point[] zza(BoundingBoxParcel boundingBoxParcel) {
        r0 = new Point[4];
        double sin = Math.sin(Math.toRadians((double) boundingBoxParcel.aOP));
        double cos = Math.cos(Math.toRadians((double) boundingBoxParcel.aOP));
        r0[0] = new Point(boundingBoxParcel.left, boundingBoxParcel.top);
        r0[1] = new Point((int) (((double) boundingBoxParcel.left) + (((double) boundingBoxParcel.width) * cos)), (int) (((double) boundingBoxParcel.top) + (((double) boundingBoxParcel.width) * sin)));
        r0[2] = new Point((int) (((double) r0[1].x) - (sin * ((double) boundingBoxParcel.height))), (int) ((cos * ((double) boundingBoxParcel.height)) + ((double) r0[1].y)));
        r0[3] = new Point(r0[0].x + (r0[2].x - r0[1].x), r0[0].y + (r0[2].y - r0[1].y));
        return r0;
    }
}
