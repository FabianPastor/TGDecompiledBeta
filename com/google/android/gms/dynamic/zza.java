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
import com.google.android.gms.common.internal.zzu;
import com.google.android.gms.common.zzf;
import java.util.LinkedList;

public abstract class zza<T extends LifecycleDelegate> {
    private T zzgwd;
    private Bundle zzgwe;
    private LinkedList<zzi> zzgwf;
    private final zzo<T> zzgwg = new zzb(this);

    private final void zza(Bundle bundle, zzi com_google_android_gms_dynamic_zzi) {
        if (this.zzgwd != null) {
            com_google_android_gms_dynamic_zzi.zzb(this.zzgwd);
            return;
        }
        if (this.zzgwf == null) {
            this.zzgwf = new LinkedList();
        }
        this.zzgwf.add(com_google_android_gms_dynamic_zzi);
        if (bundle != null) {
            if (this.zzgwe == null) {
                this.zzgwe = (Bundle) bundle.clone();
            } else {
                this.zzgwe.putAll(bundle);
            }
        }
        zza(this.zzgwg);
    }

    public static void zzb(FrameLayout frameLayout) {
        zzf instance = GoogleApiAvailability.getInstance();
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = instance.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzu.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzu.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        Intent zza = zzf.zza(context, isGooglePlayServicesAvailable, null);
        if (zza != null) {
            View button = new Button(context);
            button.setId(16908313);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzk);
            linearLayout.addView(button);
            button.setOnClickListener(new zzf(context, zza));
        }
    }

    private final void zzcz(int i) {
        while (!this.zzgwf.isEmpty() && ((zzi) this.zzgwf.getLast()).getState() >= i) {
            this.zzgwf.removeLast();
        }
    }

    public final void onCreate(Bundle bundle) {
        zza(bundle, new zzd(this, bundle));
    }

    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = new FrameLayout(layoutInflater.getContext());
        zza(bundle, new zze(this, frameLayout, layoutInflater, viewGroup, bundle));
        if (this.zzgwd == null) {
            zza(frameLayout);
        }
        return frameLayout;
    }

    public final void onDestroy() {
        if (this.zzgwd != null) {
            this.zzgwd.onDestroy();
        } else {
            zzcz(1);
        }
    }

    public final void onInflate(Activity activity, Bundle bundle, Bundle bundle2) {
        zza(bundle2, new zzc(this, activity, bundle, bundle2));
    }

    public final void onLowMemory() {
        if (this.zzgwd != null) {
            this.zzgwd.onLowMemory();
        }
    }

    public final void onPause() {
        if (this.zzgwd != null) {
            this.zzgwd.onPause();
        } else {
            zzcz(5);
        }
    }

    public final void onResume() {
        zza(null, new zzh(this));
    }

    public final void onSaveInstanceState(Bundle bundle) {
        if (this.zzgwd != null) {
            this.zzgwd.onSaveInstanceState(bundle);
        } else if (this.zzgwe != null) {
            bundle.putAll(this.zzgwe);
        }
    }

    public final void onStart() {
        zza(null, new zzg(this));
    }

    public final void onStop() {
        if (this.zzgwd != null) {
            this.zzgwd.onStop();
        } else {
            zzcz(4);
        }
    }

    protected void zza(FrameLayout frameLayout) {
        zzf instance = GoogleApiAvailability.getInstance();
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = instance.isGooglePlayServicesAvailable(context);
        CharSequence zzi = zzu.zzi(context, isGooglePlayServicesAvailable);
        CharSequence zzk = zzu.zzk(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzi);
        linearLayout.addView(textView);
        Intent zza = zzf.zza(context, isGooglePlayServicesAvailable, null);
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

    public final T zzapw() {
        return this.zzgwd;
    }
}
