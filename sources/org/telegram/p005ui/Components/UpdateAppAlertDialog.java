package org.telegram.p005ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.browser.Browser;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;

/* renamed from: org.telegram.ui.Components.UpdateAppAlertDialog */
public class UpdateAppAlertDialog extends AlertDialog implements NotificationCenterDelegate {
    private int accountNum;
    private TL_help_appUpdate appUpdate;
    private String fileName;
    private Activity parentActivity;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;

    public UpdateAppAlertDialog(Activity activity, TL_help_appUpdate update, int account) {
        super(activity, 0);
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        this.parentActivity = activity;
        setTopImage((int) CLASSNAMER.drawable.update, Theme.getColor(Theme.key_dialogTopBackground));
        setTopHeight(175);
        setMessage(this.appUpdate.text);
        if (this.appUpdate.document instanceof TL_document) {
            setSecondTitle(AndroidUtilities.formatFileSize((long) this.appUpdate.document.size));
        }
        setDismissDialogByButtons(false);
        setTitle(LocaleController.getString("UpdateTelegram", CLASSNAMER.string.UpdateTelegram));
        setPositiveButton(LocaleController.getString("UpdateNow", CLASSNAMER.string.UpdateNow), new UpdateAppAlertDialog$$Lambda$0(this));
        setNeutralButton(LocaleController.getString("Later", CLASSNAMER.string.Later), new UpdateAppAlertDialog$$Lambda$1(this));
        this.radialProgressView = new FrameLayout(this.parentActivity) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int width = right - left;
                int height = bottom - top;
                int w = AndroidUtilities.m9dp(24.0f);
                int l = (width - w) / 2;
                int t = ((height - w) / 2) + AndroidUtilities.m9dp(2.0f);
                UpdateAppAlertDialog.this.radialProgress.setProgressRect(l, t, l + w, t + w);
            }

            protected void onDraw(Canvas canvas) {
                UpdateAppAlertDialog.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        this.radialProgress = new RadialProgress(this.radialProgressView);
        this.radialProgress.setStrokeWidth(AndroidUtilities.m9dp(2.0f));
        this.radialProgress.setBackground(null, true, false);
        this.radialProgress.setProgressColor(Theme.getColor(Theme.key_dialogButton));
    }

    final /* synthetic */ void lambda$new$0$UpdateAppAlertDialog(DialogInterface dialog, int which) {
        if (!BlockingUpdateView.checkApkInstallPermissions(getContext())) {
            return;
        }
        if (this.appUpdate.document instanceof TL_document) {
            if (!BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document)) {
                FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
                showProgress(true);
            }
        } else if (this.appUpdate.url != null) {
            Browser.openUrl(getContext(), this.appUpdate.url);
            dialog.dismiss();
        }
    }

    final /* synthetic */ void lambda$new$1$UpdateAppAlertDialog(DialogInterface dialog, int which) {
        if (this.appUpdate.document instanceof TL_document) {
            FileLoader.getInstance(this.accountNum).cancelLoadFile(this.appUpdate.document);
        }
        dialog.dismiss();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        if (id == NotificationCenter.fileDidLoad) {
            location = args[0];
            if (this.fileName != null && this.fileName.equals(location)) {
                showProgress(false);
                BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document);
            }
        } else if (id == NotificationCenter.fileDidFailedLoad) {
            location = (String) args[0];
            if (this.fileName != null && this.fileName.equals(location)) {
                showProgress(false);
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[0];
            if (this.fileName != null && this.fileName.equals(location)) {
                this.radialProgress.setProgress(args[1].floatValue(), true);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        this.buttonsLayout.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36.0f));
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
    }

    private void showProgress(final boolean show) {
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        final View textButton = this.buttonsLayout.findViewWithTag(Integer.valueOf(-1));
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.radialProgressView.setVisibility(0);
            textButton.setEnabled(false);
            animatorSet = this.progressAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(textButton, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(textButton, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(textButton, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            textButton.setVisibility(0);
            textButton.setEnabled(true);
            animatorSet = this.progressAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(textButton, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(textButton, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(textButton, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animation)) {
                    if (show) {
                        textButton.setVisibility(4);
                    } else {
                        UpdateAppAlertDialog.this.radialProgressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animation)) {
                    UpdateAppAlertDialog.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }
}
