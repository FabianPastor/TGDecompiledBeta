package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private Factory<T> zzbOB;
    private SparseArray<zza> zzbOC;
    private int zzbOp;

    public static class Builder<T> {
        private MultiProcessor<T> zzbOD = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbOD.zzbOB = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbOD;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbOD.zzbOp = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    private class zza {
        private Tracker<T> zzbOo;
        private int zzbOs;

        private zza(MultiProcessor multiProcessor) {
            this.zzbOs = 0;
        }
    }

    private MultiProcessor() {
        this.zzbOC = new SparseArray();
        this.zzbOp = 3;
    }

    private void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            if (this.zzbOC.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbOo = this.zzbOB.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOo.onNewItem(keyAt, valueAt);
                this.zzbOC.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
    }

    private void zzb(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        for (int i = 0; i < this.zzbOC.size(); i++) {
            int keyAt = this.zzbOC.keyAt(i);
            if (detectedItems.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOC.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza.zzbOs = com_google_android_gms_vision_MultiProcessor_zza.zzbOs + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza.zzbOs >= this.zzbOp) {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOo.onDone();
                    hashSet.add(Integer.valueOf(keyAt));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza.zzbOo.onMissing(detections);
                }
            }
        }
        for (Integer intValue : hashSet) {
            this.zzbOC.delete(intValue.intValue());
        }
    }

    private void zzc(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbOC.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbOs = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbOo.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        zza(detections);
        zzb(detections);
        zzc(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbOC.size(); i++) {
            ((zza) this.zzbOC.valueAt(i)).zzbOo.onDone();
        }
        this.zzbOC.clear();
    }
}
