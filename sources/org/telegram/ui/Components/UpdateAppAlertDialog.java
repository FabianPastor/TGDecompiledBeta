package org.telegram.ui.Components;

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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class UpdateAppAlertDialog extends AlertDialog implements NotificationCenterDelegate {
    private int accountNum;
    private TL_help_appUpdate appUpdate;
    private String fileName;
    private Activity parentActivity;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;

    public UpdateAppAlertDialog(Activity activity, TL_help_appUpdate tL_help_appUpdate, int i) {
        super(activity, 0);
        this.appUpdate = tL_help_appUpdate;
        this.accountNum = i;
        Document document = tL_help_appUpdate.document;
        if (document instanceof TL_document) {
            this.fileName = FileLoader.getAttachFileName(document);
        }
        this.parentActivity = activity;
        setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        setTopHeight(175);
        setMessage(this.appUpdate.text);
        Document document2 = this.appUpdate.document;
        if (document2 instanceof TL_document) {
            setSecondTitle(AndroidUtilities.formatFileSize((long) document2.size));
        }
        setDismissDialogByButtons(false);
        setTitle(LocaleController.getString("UpdateTelegram", NUM));
        setPositiveButton(LocaleController.getString("UpdateNow", NUM), new -$$Lambda$UpdateAppAlertDialog$voSGHXhYuACsnb2-x2STYrPYmkw(this));
        setNeutralButton(LocaleController.getString("Later", NUM), new -$$Lambda$UpdateAppAlertDialog$egnS6_js0sES87wcIflPDbL7olc(this));
        this.radialProgressView = new FrameLayout(this.parentActivity) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                i3 -= i;
                i4 -= i2;
                int dp = AndroidUtilities.dp(24.0f);
                i3 = (i3 - dp) / 2;
                i4 = ((i4 - dp) / 2) + AndroidUtilities.dp(2.0f);
                UpdateAppAlertDialog.this.radialProgress.setProgressRect(i3, i4, i3 + dp, dp + i4);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                UpdateAppAlertDialog.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        this.radialProgress = new RadialProgress(this.radialProgressView);
        this.radialProgress.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.radialProgress.setBackground(null, true, false);
        this.radialProgress.setProgressColor(Theme.getColor("dialogButton"));
    }

    public /* synthetic */ void lambda$new$0$UpdateAppAlertDialog(DialogInterface dialogInterface, int i) {
        if (BlockingUpdateView.checkApkInstallPermissions(getContext())) {
            TL_help_appUpdate tL_help_appUpdate = this.appUpdate;
            Document document = tL_help_appUpdate.document;
            if (document instanceof TL_document) {
                if (!BlockingUpdateView.openApkInstall(this.parentActivity, document)) {
                    FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
                    showProgress(true);
                }
            } else if (tL_help_appUpdate.url != null) {
                Browser.openUrl(getContext(), this.appUpdate.url);
                dialogInterface.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$new$1$UpdateAppAlertDialog(DialogInterface dialogInterface, int i) {
        if (this.appUpdate.document instanceof TL_document) {
            FileLoader.getInstance(this.accountNum).cancelLoadFile(this.appUpdate.document);
        }
        dialogInterface.dismiss();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        String str2;
        if (i == NotificationCenter.fileDidLoad) {
            str = (String) objArr[0];
            str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                showProgress(false);
                BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document);
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            str = (String) objArr[0];
            str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                showProgress(false);
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                this.radialProgress.setProgress(Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue())), true);
            }
        }
    }

    /* Access modifiers changed, original: protected */
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
        final View findViewWithTag = this.buttonsLayout.findViewWithTag(Integer.valueOf(-1));
        String str = "alpha";
        String str2 = "scaleY";
        String str3 = "scaleX";
        AnimatorSet animatorSet2;
        if (z2) {
            this.radialProgressView.setVisibility(0);
            findViewWithTag.setEnabled(false);
            animatorSet2 = this.progressAnimation;
            r8 = new Animator[6];
            r8[0] = ObjectAnimator.ofFloat(findViewWithTag, str3, new float[]{0.1f});
            r8[1] = ObjectAnimator.ofFloat(findViewWithTag, str2, new float[]{0.1f});
            r8[2] = ObjectAnimator.ofFloat(findViewWithTag, str, new float[]{0.0f});
            r8[3] = ObjectAnimator.ofFloat(this.radialProgressView, str3, new float[]{1.0f});
            r8[4] = ObjectAnimator.ofFloat(this.radialProgressView, str2, new float[]{1.0f});
            r8[5] = ObjectAnimator.ofFloat(this.radialProgressView, str, new float[]{1.0f});
            animatorSet2.playTogether(r8);
        } else {
            findViewWithTag.setVisibility(0);
            findViewWithTag.setEnabled(true);
            animatorSet2 = this.progressAnimation;
            Animator[] animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.radialProgressView, str3, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.radialProgressView, str2, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.radialProgressView, str, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(findViewWithTag, str3, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(findViewWithTag, str2, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(findViewWithTag, str, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animator)) {
                    if (z2) {
                        findViewWithTag.setVisibility(4);
                    } else {
                        UpdateAppAlertDialog.this.radialProgressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animator)) {
                    UpdateAppAlertDialog.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }
}
