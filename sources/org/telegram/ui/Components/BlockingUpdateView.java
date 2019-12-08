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
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_getAppUpdate;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;

public class BlockingUpdateView extends FrameLayout implements NotificationCenterDelegate {
    private FrameLayout acceptButton;
    private TextView acceptTextView;
    private int accountNum;
    private TL_help_appUpdate appUpdate;
    private String fileName;
    private int pressCount;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;
    private TextView textView;

    public BlockingUpdateView(Context context) {
        Context context2 = context;
        super(context);
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int i = VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setBackgroundColor(-11556378);
        addView(frameLayout, new LayoutParams(-1, AndroidUtilities.dp(176.0f) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(NUM);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, (float) i, 0.0f, 0.0f));
        imageView.setOnClickListener(new -$$Lambda$BlockingUpdateView$ninaHPZEm5ZV8HP69-4gylboxn4(this));
        ScrollView scrollView = new ScrollView(context2);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor("actionBarDefault"));
        addView(scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, (float) (i + 206), 27.0f, 130.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        scrollView.addView(frameLayout2, LayoutHelper.createScroll(-1, -2, 17));
        TextView textView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        textView.setTextColor(Theme.getColor(str));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(49);
        String str2 = "fonts/rmedium.ttf";
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        textView.setText(LocaleController.getString("UpdateTelegram", NUM));
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2, 49));
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        frameLayout2.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        this.acceptButton = new FrameLayout(context2);
        this.acceptButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        this.acceptButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 42.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new -$$Lambda$BlockingUpdateView$58XjGl4nF8-_CcazfB65zVJEC_c(this));
        this.acceptTextView = new TextView(context2);
        this.acceptTextView.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        this.radialProgressView = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                i3 -= i;
                i4 -= i2;
                int dp = AndroidUtilities.dp(36.0f);
                i3 = (i3 - dp) / 2;
                i4 = (i4 - dp) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(i3, i4, i3 + dp, dp + i4);
            }

            /* Access modifiers changed, original: protected */
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
        this.radialProgress.setBackground(null, true, false);
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
            TL_help_appUpdate tL_help_appUpdate = this.appUpdate;
            if (tL_help_appUpdate.document instanceof TL_document) {
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
        String str;
        String str2;
        if (i == NotificationCenter.fileDidLoad) {
            str = (String) objArr[0];
            str2 = this.fileName;
            if (str2 != null && str2.equals(str)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
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
                this.radialProgress.setProgress(((Float) objArr[1]).floatValue(), true);
            }
        }
    }

    public static boolean checkApkInstallPermissions(Context context) {
        if (VERSION.SDK_INT < 26 || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            return true;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ApkRestricted", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$BlockingUpdateView$ftz1vauUG0mCLASSNAMEY32crTPmAh74(context));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.show();
        return false;
    }

    static /* synthetic */ void lambda$checkApkInstallPermissions$2(Context context, DialogInterface dialogInterface, int i) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse(stringBuilder.toString())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static boolean openApkInstall(Activity activity, Document document) {
        boolean z = false;
        try {
            FileLoader.getAttachFileName(document);
            File pathToAttach = FileLoader.getPathToAttach(document, true);
            z = pathToAttach.exists();
            if (z) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                String str = "application/vnd.android.package-archive";
                if (VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.provider", pathToAttach), str);
                } else {
                    intent.setDataAndType(Uri.fromFile(pathToAttach), str);
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return z;
    }

    private void showProgress(boolean z) {
        final boolean z2 = z;
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        String str = "alpha";
        String str2 = "scaleY";
        String str3 = "scaleX";
        if (z2) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            AnimatorSet animatorSet2 = this.progressAnimation;
            Animator[] animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.acceptTextView, str3, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.acceptTextView, str2, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.acceptTextView, str, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.radialProgressView, str3, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.radialProgressView, str2, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.radialProgressView, str, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            animatorSet = this.progressAnimation;
            Animator[] animatorArr2 = new Animator[6];
            animatorArr2[0] = ObjectAnimator.ofFloat(this.radialProgressView, str3, new float[]{0.1f});
            animatorArr2[1] = ObjectAnimator.ofFloat(this.radialProgressView, str2, new float[]{0.1f});
            animatorArr2[2] = ObjectAnimator.ofFloat(this.radialProgressView, str, new float[]{0.0f});
            animatorArr2[3] = ObjectAnimator.ofFloat(this.acceptTextView, str3, new float[]{1.0f});
            animatorArr2[4] = ObjectAnimator.ofFloat(this.acceptTextView, str2, new float[]{1.0f});
            animatorArr2[5] = ObjectAnimator.ofFloat(this.acceptTextView, str, new float[]{1.0f});
            animatorSet.playTogether(animatorArr2);
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    if (z2) {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }

    public void show(int i, TL_help_appUpdate tL_help_appUpdate, boolean z) {
        this.pressCount = 0;
        this.appUpdate = tL_help_appUpdate;
        this.accountNum = i;
        Document document = tL_help_appUpdate.document;
        if (document instanceof TL_document) {
            this.fileName = FileLoader.getAttachFileName(document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_help_appUpdate.text);
        MessageObject.addEntitiesToText(spannableStringBuilder, tL_help_appUpdate.entities, false, 0, false, false, false);
        this.textView.setText(spannableStringBuilder);
        String str = "Update";
        if (tL_help_appUpdate.document instanceof TL_document) {
            TextView textView = this.acceptTextView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString(str, NUM));
            stringBuilder.append(String.format(Locale.US, " (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) tL_help_appUpdate.document.size)}));
            textView.setText(stringBuilder.toString());
        } else {
            this.acceptTextView.setText(LocaleController.getString(str, NUM));
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        if (z) {
            TL_help_getAppUpdate tL_help_getAppUpdate = new TL_help_getAppUpdate();
            try {
                tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tL_help_getAppUpdate.source == null) {
                tL_help_getAppUpdate.source = "";
            }
            ConnectionsManager.getInstance(this.accountNum).sendRequest(tL_help_getAppUpdate, new -$$Lambda$BlockingUpdateView$IMGtOo6ZHOPONCRx2yC4X_1QmSM(this));
        }
    }

    public /* synthetic */ void lambda$show$4$BlockingUpdateView(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$BlockingUpdateView$tW9xygtx1eZK95aajS8E6pWLkkc(this, tLObject));
    }

    public /* synthetic */ void lambda$null$3$BlockingUpdateView(TLObject tLObject) {
        if ((tLObject instanceof TL_help_appUpdate) && !((TL_help_appUpdate) tLObject).can_not_skip) {
            setVisibility(8);
            UserConfig.getInstance(0).pendingAppUpdate = null;
            UserConfig.getInstance(0).saveConfig(false);
        }
    }
}
