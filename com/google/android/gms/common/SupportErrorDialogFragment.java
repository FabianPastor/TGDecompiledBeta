package com.google.android.gms.common;

import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import com.google.android.gms.common.internal.zzbq;

public class SupportErrorDialogFragment extends DialogFragment {
    private Dialog mDialog = null;
    private OnCancelListener zzfkt = null;

    public static SupportErrorDialogFragment newInstance(Dialog dialog, OnCancelListener onCancelListener) {
        SupportErrorDialogFragment supportErrorDialogFragment = new SupportErrorDialogFragment();
        Dialog dialog2 = (Dialog) zzbq.checkNotNull(dialog, "Cannot display null dialog");
        dialog2.setOnCancelListener(null);
        dialog2.setOnDismissListener(null);
        supportErrorDialogFragment.mDialog = dialog2;
        if (onCancelListener != null) {
            supportErrorDialogFragment.zzfkt = onCancelListener;
        }
        return supportErrorDialogFragment;
    }

    public void show(FragmentManager fragmentManager, String str) {
        super.show(fragmentManager, str);
    }
}
