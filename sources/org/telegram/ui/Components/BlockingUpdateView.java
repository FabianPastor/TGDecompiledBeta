package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
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

    /* renamed from: org.telegram.ui.Components.BlockingUpdateView$1 */
    class C11341 implements OnClickListener {
        C11341() {
        }

        public void onClick(View v) {
            BlockingUpdateView.this.pressCount = BlockingUpdateView.this.pressCount + 1;
            if (BlockingUpdateView.this.pressCount >= 10) {
                BlockingUpdateView.this.setVisibility(8);
                UserConfig.getInstance(0).pendingAppUpdate = null;
                UserConfig.getInstance(0).saveConfig(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.BlockingUpdateView$2 */
    class C11352 implements OnClickListener {
        C11352() {
        }

        public void onClick(View view) {
            if (!BlockingUpdateView.checkApkInstallPermissions(BlockingUpdateView.this.getContext())) {
                return;
            }
            if (BlockingUpdateView.this.appUpdate.document instanceof TL_document) {
                if (!BlockingUpdateView.openApkInstall((Activity) BlockingUpdateView.this.getContext(), BlockingUpdateView.this.appUpdate.document)) {
                    FileLoader.getInstance(BlockingUpdateView.this.accountNum).loadFile(BlockingUpdateView.this.appUpdate.document, true, 1);
                    BlockingUpdateView.this.showProgress(true);
                }
            } else if (BlockingUpdateView.this.appUpdate.url != null) {
                Browser.openUrl(BlockingUpdateView.this.getContext(), BlockingUpdateView.this.appUpdate.url);
            }
        }
    }

    public BlockingUpdateView(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        FrameLayout view = new FrameLayout(context);
        view.setBackgroundColor(-11556378);
        addView(view, new LayoutParams(-1, (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(176.0f)));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.intro_tg_plane);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        view.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, (float) top, 0.0f, 0.0f));
        imageView.setOnClickListener(new C11341());
        ScrollView scrollView = new ScrollView(context);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        addView(scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, (float) (top + 206), 27.0f, 130.0f));
        FrameLayout container = new FrameLayout(context);
        scrollView.addView(container, LayoutHelper.createScroll(-1, -2, 17));
        TextView titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(1, 20.0f);
        titleTextView.setGravity(49);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setText(LocaleController.getString("UpdateTelegram", R.string.UpdateTelegram));
        container.addView(titleTextView, LayoutHelper.createFrame(-2, -2, 49));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        container.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        this.acceptButton = new FrameLayout(context);
        this.acceptButton.setBackgroundResource(R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.acceptButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.acceptButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.acceptButton.setStateListAnimator(animator);
        }
        this.acceptButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 56.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new C11352());
        this.acceptTextView = new TextView(context);
        this.acceptTextView.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 16.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        this.radialProgressView = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int width = right - left;
                int height = bottom - top;
                int w = AndroidUtilities.dp(36.0f);
                int l = (width - w) / 2;
                int t = (height - w) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(l, t, l + w, t + w);
            }

            protected void onDraw(Canvas canvas) {
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

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidLoaded) {
            location = args[0];
            if (this.fileName != null && this.fileName.equals(location)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
            }
        } else if (id == NotificationCenter.FileDidFailedLoad) {
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

    public static boolean checkApkInstallPermissions(final Context context) {
        if (VERSION.SDK_INT < 26 || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            return true;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ApkRestricted", R.string.ApkRestricted));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            @TargetApi(26)
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName())));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
        return false;
    }

    public static boolean openApkInstall(Activity activity, Document document) {
        boolean exists = false;
        try {
            String fileName = FileLoader.getAttachFileName(document);
            File f = FileLoader.getPathToAttach(document, true);
            exists = f.exists();
            if (exists) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        return exists;
    }

    private void showProgress(final boolean show) {
        if (this.progressAnimation != null) {
            this.progressAnimation.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            animatorSet = this.progressAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            animatorSet = this.progressAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    if (show) {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }

    public void show(int account, TL_help_appUpdate update) {
        this.pressCount = 0;
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(update.text);
        MessageObject.addEntitiesToText(builder, update.entities, false, 0, false, false, false);
        this.textView.setText(builder);
        if (update.document instanceof TL_document) {
            this.acceptTextView.setText(LocaleController.getString("Update", R.string.Update).toUpperCase() + String.format(Locale.US, " (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) update.document.size)}));
        } else {
            this.acceptTextView.setText(LocaleController.getString("Update", R.string.Update).toUpperCase());
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
    }
}
