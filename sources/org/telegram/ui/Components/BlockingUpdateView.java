package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class BlockingUpdateView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout acceptButton;
    /* access modifiers changed from: private */
    public TextView acceptTextView;
    private int accountNum;
    private TLRPC.TL_help_appUpdate appUpdate;
    private String fileName;
    private int pressCount;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public RadialProgress radialProgress;
    /* access modifiers changed from: private */
    public FrameLayout radialProgressView;
    private TextView textView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public BlockingUpdateView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int i = Build.VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setBackgroundColor(-11556378);
        addView(frameLayout, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(176.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(NUM);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, (float) i, 0.0f, 0.0f));
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$0$BlockingUpdateView(view);
            }
        });
        ScrollView scrollView = new ScrollView(context2);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor("actionBarDefault"));
        addView(scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, (float) (i + 206), 27.0f, 130.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        scrollView.addView(frameLayout2, LayoutHelper.createScroll(-1, -2, 17));
        TextView textView2 = new TextView(context2);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView2.setTextSize(1, 20.0f);
        textView2.setGravity(49);
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView2.setText(LocaleController.getString("UpdateTelegram", NUM));
        frameLayout2.addView(textView2, LayoutHelper.createFrame(-2, -2, 49));
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        frameLayout2.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        this.acceptButton = new FrameLayout(context2);
        this.acceptButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        this.acceptButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 42.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$1$BlockingUpdateView(view);
            }
        });
        this.acceptTextView = new TextView(context2);
        this.acceptTextView.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        this.radialProgressView = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int dp = AndroidUtilities.dp(36.0f);
                int i5 = ((i3 - i) - dp) / 2;
                int i6 = ((i4 - i2) - dp) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(i5, i6, i5 + dp, dp + i6);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                BlockingUpdateView.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        this.radialProgress = new RadialProgress(this.radialProgressView);
        this.radialProgress.setBackground((Drawable) null, true, false);
        this.radialProgress.setProgressColor(-1);
        this.acceptButton.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36, 17));
    }

    public /* synthetic */ void lambda$new$0$BlockingUpdateView(View view) {
        this.pressCount++;
        if (this.pressCount >= 10) {
            setVisibility(8);
            UserConfig.getInstance(0).pendingAppUpdate = null;
            UserConfig.getInstance(0).saveConfig(false);
        }
    }

    public /* synthetic */ void lambda$new$1$BlockingUpdateView(View view) {
        if (checkApkInstallPermissions(getContext())) {
            TLRPC.TL_help_appUpdate tL_help_appUpdate = this.appUpdate;
            if (tL_help_appUpdate.document instanceof TLRPC.TL_document) {
                if (!openApkInstall((Activity) getContext(), this.appUpdate.document)) {
                    FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 2, 1);
                    showProgress(true);
                }
            } else if (tL_help_appUpdate.url != null) {
                Browser.openUrl(getContext(), this.appUpdate.url);
            }
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            String str = objArr[0];
            String str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
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

    public static boolean checkApkInstallPermissions(Context context) {
        if (Build.VERSION.SDK_INT < 26 || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ApkRestricted", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener(context) {
            private final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BlockingUpdateView.lambda$checkApkInstallPermissions$2(this.f$0, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
        return false;
    }

    static /* synthetic */ void lambda$checkApkInstallPermissions$2(Context context, DialogInterface dialogInterface, int i) {
        try {
            context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName())));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean openApkInstall(Activity activity, TLRPC.Document document) {
        boolean z = false;
        try {
            FileLoader.getAttachFileName(document);
            File pathToAttach = FileLoader.getPathToAttach(document, true);
            z = pathToAttach.exists();
            if (z) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", pathToAttach), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(pathToAttach), "application/vnd.android.package-archive");
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return z;
    }

    private void showProgress(final boolean z) {
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        if (z) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{1.0f})});
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    if (!z) {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    AnimatorSet unused = BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }

    public void show(int i, TLRPC.TL_help_appUpdate tL_help_appUpdate, boolean z) {
        this.pressCount = 0;
        this.appUpdate = tL_help_appUpdate;
        this.accountNum = i;
        TLRPC.Document document = tL_help_appUpdate.document;
        if (document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_help_appUpdate.text);
        MessageObject.addEntitiesToText(spannableStringBuilder, tL_help_appUpdate.entities, false, 0, false, false, false);
        this.textView.setText(spannableStringBuilder);
        if (tL_help_appUpdate.document instanceof TLRPC.TL_document) {
            TextView textView2 = this.acceptTextView;
            textView2.setText(LocaleController.getString("Update", NUM) + String.format(Locale.US, " (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) tL_help_appUpdate.document.size)}));
        } else {
            this.acceptTextView.setText(LocaleController.getString("Update", NUM));
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        if (z) {
            TLRPC.TL_help_getAppUpdate tL_help_getAppUpdate = new TLRPC.TL_help_getAppUpdate();
            try {
                tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tL_help_getAppUpdate.source == null) {
                tL_help_getAppUpdate.source = "";
            }
            ConnectionsManager.getInstance(this.accountNum).sendRequest(tL_help_getAppUpdate, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BlockingUpdateView.this.lambda$show$4$BlockingUpdateView(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$show$4$BlockingUpdateView(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BlockingUpdateView.this.lambda$null$3$BlockingUpdateView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$BlockingUpdateView(TLObject tLObject) {
        if ((tLObject instanceof TLRPC.TL_help_appUpdate) && !((TLRPC.TL_help_appUpdate) tLObject).can_not_skip) {
            setVisibility(8);
            UserConfig.getInstance(0).pendingAppUpdate = null;
            UserConfig.getInstance(0).saveConfig(false);
        }
    }
}
