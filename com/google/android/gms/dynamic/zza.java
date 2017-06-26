package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.zzs;
import com.google.android.gms.common.zze;
import java.util.LinkedList;

public abstract class zza<T extends LifecycleDelegate> {
    private T zzaSr;
    private Bundle zzaSs;
    private LinkedList<zzi> zzaSt;
    private final zzo<T> zzaSu = new zzb(this);

    private final void zza(Bundle bundle, zzi com_google_android_gms_dynamic_zzi) {
        if (this.zzaSr != null) {
            com_google_android_gms_dynamic_zzi.zzb(this.zzaSr);
            return;
        }
        if (this.zzaSt == null) {
            this.zzaSt = new LinkedList();
        }
        this.zzaSt.add(com_google_android_gms_dynamic_zzi);
        if (bundle != null) {
            if (this.zzaSs == null) {
                this.zzaSs = (Bundle) bundle.clone();
            } else {
                this.zzaSs.putAll(bundle);
            }
        }
        zza(this.zzaSu);
    }

    private final void zzaR(int i) {
        while (!this.zzaSt.isEmpty() && ((zzi) this.zzaSt.getLast()).getState() >= i) {
            this.zzaSt.removeLast();
        }
    }

    public static void zzb(FrameLayout frameLayout) {
        GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = instance.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzs.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzs.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        Intent zza = zze.zza(context, isGooglePlayServicesAvailable, null);
        if (zza != null) {
            View button = new Button(context);
            button.setId(16908313);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzk);
            linearLayout.addView(button);
            button.setOnClickListener(new zzf(context, zza));
        }
    }

    public final void onCreate(Bundle bundle) {
        zza(bundle, new zzd(this, bundle));
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = new FrameLayout(layoutInflater.getContext());
        zza(bundle, new zze(this, frameLayout, layoutInflater, viewGroup, bundle));
        if (this.zzaSr == null) {
            zza(frameLayout);
        }
        return frameLayout;
    }

    public final void onDestroy() {
        if (this.zzaSr != null) {
            this.zzaSr.onDestroy();
        } else {
            zzaR(1);
        }
    }

    public final void onDestroyView() {
        if (this.zzaSr != null) {
            this.zzaSr.onDestroyView();
        } else {
            zzaR(2);
        }
    }

    public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
        zza(bundle2, new zzc(this, activity, bundle, bundle2));
    }

    public final void onLowMemory() {
        if (this.zzaSr != null) {
            this.zzaSr.onLowMemory();
        }
    }

    public final void onPause() {
        if (this.zzaSr != null) {
            this.zzaSr.onPause();
        } else {
            zzaR(5);
        }
    }

    public final void onResume() {
        zza(null, new zzh(this));
    }

    public final void onSaveInstanceState(Bundle bundle) {
        if (this.zzaSr != null) {
            this.zzaSr.onSaveInstanceState(bundle);
        } else if (this.zzaSs != null) {
            bundle.putAll(this.zzaSs);
        }
    }

    public final void onStart() {
        zza(null, new zzg(this));
    }

    public final void onStop() {
        if (this.zzaSr != null) {
            this.zzaSr.onStop();
        } else {
            zzaR(4);
        }
    }

    protected void zza(FrameLayout frameLayout) {
        GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = instance.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzs.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzs.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        Intent zza = zze.zza(context, isGooglePlayServicesAvailable, null);
        if (zza != null) {
            View button = new Button(context);
            button.setId(16908313);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzk);
            linearLayout.addView(button);
            button.setOnClickListener(new zzf(context, zza));
        }
    }

    protected abstract void zza(zzo<T> com_google_android_gms_dynamic_zzo_T);

    public final T zztx() {
        return this.zzaSr;
    }
}
