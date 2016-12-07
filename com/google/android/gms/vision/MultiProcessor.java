package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private Factory<T> zzbMG;
    private SparseArray<zza> zzbMH;
    private int zzbMu;

    public static class Builder<T> {
        private MultiProcessor<T> zzbMI = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbMI.zzbMG = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbMI;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbMI.zzbMu = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        private Tracker<T> zzbMt;
        private int zzbMx;

        private zza(MultiProcessor multiProcessor) {
            this.zzbMx = 0;
        }
    }

    private MultiProcessor() {
        this.zzbMH = new SparseArray();
        this.zzbMu = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.zzbMH.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbMt = this.zzbMG.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbMt.onNewItem(keyAt, valueAt);
                this.zzbMH.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.zzbMH.size(); i++) {
            int keyAt = this.zzbMH.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbMH.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.zzbMx = com_google_android_gms_vision_MultiProcessor_zza.zzbMx + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.zzbMx >= this.zzbMu) {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbMt.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbMt.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.zzbMH.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbMH.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbMx = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbMt.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbMH.size(); i++) {
            ((zza) this.zzbMH.valueAt(i)).zzbMt.onDone();
        }
        this.zzbMH.clear();
    }
}
