package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private Factory<T> zzbOE;
    private SparseArray<zza> zzbOF;
    private int zzbOs;

    public static class Builder<T> {
        private MultiProcessor<T> zzbOG = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbOG.zzbOE = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbOG;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbOG.zzbOs = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        private Tracker<T> zzbOr;
        private int zzbOv;

        private zza(MultiProcessor multiProcessor) {
            this.zzbOv = 0;
        }
    }

    private MultiProcessor() {
        this.zzbOF = new SparseArray();
        this.zzbOs = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.zzbOF.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbOr = this.zzbOE.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOr.onNewItem(keyAt, valueAt);
                this.zzbOF.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.zzbOF.size(); i++) {
            int keyAt = this.zzbOF.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOF.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOv = com_google_android_gms_vision_MultiProcessor_zza.zzbOv + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.zzbOv >= this.zzbOs) {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOr.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOr.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.zzbOF.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOF.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbOv = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbOr.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbOF.size(); i++) {
            ((zza) this.zzbOF.valueAt(i)).zzbOr.onDone();
        }
        this.zzbOF.clear();
    }
}
