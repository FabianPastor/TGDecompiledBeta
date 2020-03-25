package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class UpdateAppAlertDialog extends AlertDialog implements NotificationCenter.NotificationCenterDelegate {
    private int accountNum;
    private TLRPC$TL_help_appUpdate appUpdate;
    private String fileName;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public RadialProgress radialProgress;
    /* access modifiers changed from: private */
    public FrameLayout radialProgressView;

    public UpdateAppAlertDialog(Activity activity, TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate, int i) {
        super(activity, 0);
        this.appUpdate = tLRPC$TL_help_appUpdate;
        this.accountNum = i;
        TLRPC$Document tLRPC$Document = tLRPC$TL_help_appUpdate.document;
        if (tLRPC$Document instanceof TLRPC$TL_document) {
            this.fileName = FileLoader.getAttachFileName(tLRPC$Document);
        }
        this.parentActivity = activity;
        setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        setTopHeight(175);
        setMessage(this.appUpdate.text);
        TLRPC$Document tLRPC$Document2 = this.appUpdate.document;
        if (tLRPC$Document2 instanceof TLRPC$TL_document) {
            setSecondTitle(AndroidUtilities.formatFileSize((long) tLRPC$Document2.size));
        }
        setDismissDialogByButtons(false);
        setTitle(LocaleController.getString("UpdateTelegram", NUM));
        setPositiveButton(LocaleController.getString("UpdateNow", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                UpdateAppAlertDialog.this.lambda$new$0$UpdateAppAlertDialog(dialogInterface, i);
            }
        });
        setNeutralButton(LocaleController.getString("Later", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                UpdateAppAlertDialog.this.lambda$new$1$UpdateAppAlertDialog(dialogInterface, i);
            }
        });
        AnonymousClass1 r2 = new FrameLayout(this.parentActivity) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int dp = AndroidUtilities.dp(24.0f);
                int i5 = ((i3 - i) - dp) / 2;
                int dp2 = (((i4 - i2) - dp) / 2) + AndroidUtilities.dp(2.0f);
                UpdateAppAlertDialog.this.radialProgress.setProgressRect(i5, dp2, i5 + dp, dp + dp2);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                UpdateAppAlertDialog.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView = r2;
        r2.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        RadialProgress radialProgress2 = new RadialProgress(this.radialProgressView);
        this.radialProgress = radialProgress2;
        radialProgress2.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.radialProgress.setBackground((Drawable) null, true, false);
        this.radialProgress.setProgressColor(Theme.getColor("dialogButton"));
    }

    public /* synthetic */ void lambda$new$0$UpdateAppAlertDialog(DialogInterface dialogInterface, int i) {
        if (BlockingUpdateView.checkApkInstallPermissions(getContext())) {
            TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = this.appUpdate;
            TLRPC$Document tLRPC$Document = tLRPC$TL_help_appUpdate.document;
            if (tLRPC$Document instanceof TLRPC$TL_document) {
                if (!BlockingUpdateView.openApkInstall(this.parentActivity, tLRPC$Document)) {
                    FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
                    showProgress(true);
                }
            } else if (tLRPC$TL_help_appUpdate.url != null) {
                Browser.openUrl(getContext(), this.appUpdate.url);
                dialogInterface.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$new$1$UpdateAppAlertDialog(DialogInterface dialogInterface, int i) {
        if (this.appUpdate.document instanceof TLRPC$TL_document) {
            FileLoader.getInstance(this.accountNum).cancelLoadFile(this.appUpdate.document);
        }
        dialogInterface.dismiss();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            String str = objArr[0];
            String str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                showProgress(false);
                BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document);
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            String str3 = objArr[0];
            String str4 = this.fileName;
            if (str4 != null && str4.equals(str3)) {
                showProgress(false);
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            String str5 = objArr[0];
            String str6 = this.fileName;
            if (str6 != null && str6.equals(str5)) {
                this.radialProgress.setProgress(Math.min(1.0f, ((float) objArr[1].longValue()) / ((float) objArr[2].longValue())), true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        this.buttonsLayout.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36.0f));
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
    }

    private void showProgress(boolean z) {
        final boolean z2 = z;
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        final View findViewWithTag = this.buttonsLayout.findViewWithTag(-1);
        if (z2) {
            this.radialProgressView.setVisibility(0);
            findViewWithTag.setEnabled(false);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(findViewWithTag, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(findViewWithTag, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(findViewWithTag, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{1.0f})});
        } else {
            findViewWithTag.setVisibility(0);
            findViewWithTag.setEnabled(true);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(findViewWithTag, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(findViewWithTag, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(findViewWithTag, "alpha", new float[]{1.0f})});
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animator)) {
                    if (!z2) {
                        UpdateAppAlertDialog.this.radialProgressView.setVisibility(4);
                    } else {
                        findViewWithTag.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animator)) {
                    AnimatorSet unused = UpdateAppAlertDialog.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }
}
