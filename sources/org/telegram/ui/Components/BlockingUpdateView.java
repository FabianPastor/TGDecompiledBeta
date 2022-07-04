package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class BlockingUpdateView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout acceptButton;
    /* access modifiers changed from: private */
    public TextView acceptTextView;
    private int accountNum;
    private TLRPC.TL_help_appUpdate appUpdate;
    private String fileName;
    Drawable gradientDrawableBottom = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Theme.getColor("windowBackgroundWhite"), 0});
    Drawable gradientDrawableTop = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Theme.getColor("windowBackgroundWhite"), 0});
    private int pressCount;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public RadialProgress radialProgress;
    /* access modifiers changed from: private */
    public FrameLayout radialProgressView;
    private ScrollView scrollView;
    private TextView textView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public BlockingUpdateView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int top = Build.VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        FrameLayout view = new FrameLayout(context2);
        addView(view, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(176.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        RLottieImageView imageView = new RLottieImageView(context2);
        imageView.setAnimation(NUM, 108, 108);
        imageView.playAnimation();
        imageView.getAnimatedDrawable().setAutoRepeat(1);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        view.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, (float) top, 0.0f, 0.0f));
        imageView.setOnClickListener(new BlockingUpdateView$$ExternalSyntheticLambda0(this));
        FrameLayout container = new FrameLayout(context2);
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView2, Theme.getColor("actionBarDefault"));
        this.scrollView.setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f));
        this.scrollView.setClipToPadding(false);
        addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, (float) (top + 178), 27.0f, 130.0f));
        this.scrollView.addView(container);
        TextView titleTextView = new TextView(context2);
        titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        titleTextView.setTextSize(1, 20.0f);
        titleTextView.setGravity(49);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setText(LocaleController.getString("UpdateTelegram", NUM));
        container.addView(titleTextView, LayoutHelper.createFrame(-2, -2, 49));
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        container.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        AnonymousClass1 r11 = new FrameLayout(context2) {
            CellFlickerDrawable cellFlickerDrawable;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable2 = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable2;
                    cellFlickerDrawable2.drawFrame = false;
                    this.cellFlickerDrawable.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (View) null);
                invalidate();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (View.MeasureSpec.getSize(widthMeasureSpec) > AndroidUtilities.dp(260.0f)) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), NUM), heightMeasureSpec);
                } else {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        this.acceptButton = r11;
        r11.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.acceptButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
        this.acceptButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 46.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new BlockingUpdateView$$ExternalSyntheticLambda1(this));
        TextView textView3 = new TextView(context2);
        this.acceptTextView = textView3;
        textView3.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        AnonymousClass2 r6 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int w = AndroidUtilities.dp(36.0f);
                int l = ((right - left) - w) / 2;
                int t = ((bottom - top) - w) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(l, t, l + w, t + w);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                BlockingUpdateView.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView = r6;
        r6.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        RadialProgress radialProgress2 = new RadialProgress(this.radialProgressView);
        this.radialProgress = radialProgress2;
        radialProgress2.setBackground((Drawable) null, true, false);
        this.radialProgress.setProgressColor(-1);
        this.acceptButton.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36, 17));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-BlockingUpdateView  reason: not valid java name */
    public /* synthetic */ void m568lambda$new$0$orgtelegramuiComponentsBlockingUpdateView(View v) {
        int i = this.pressCount + 1;
        this.pressCount = i;
        if (i >= 10) {
            setVisibility(8);
            SharedConfig.pendingAppUpdate = null;
            SharedConfig.saveConfig();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-BlockingUpdateView  reason: not valid java name */
    public /* synthetic */ void m569lambda$new$1$orgtelegramuiComponentsBlockingUpdateView(View view1) {
        if (checkApkInstallPermissions(getContext())) {
            if (this.appUpdate.document instanceof TLRPC.TL_document) {
                if (!openApkInstall((Activity) getContext(), this.appUpdate.document)) {
                    FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 2, 1);
                    showProgress(true);
                }
            } else if (this.appUpdate.url != null) {
                Browser.openUrl(getContext(), this.appUpdate.url);
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String location = args[0];
            String str = this.fileName;
            if (str != null && str.equals(location)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
            }
        } else if (id == NotificationCenter.fileLoadFailed) {
            String location2 = args[0];
            String str2 = this.fileName;
            if (str2 != null && str2.equals(location2)) {
                showProgress(false);
            }
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            String location3 = args[0];
            String str3 = this.fileName;
            if (str3 != null && str3.equals(location3)) {
                this.radialProgress.setProgress(Math.min(1.0f, ((float) args[1].longValue()) / ((float) args[2].longValue())), true);
            }
        }
    }

    public static boolean checkApkInstallPermissions(Context context) {
        if (Build.VERSION.SDK_INT < 26 || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            return true;
        }
        AlertsCreator.createApkRestrictedDialog(context, (Theme.ResourcesProvider) null).show();
        return false;
    }

    public static boolean openApkInstall(Activity activity, TLRPC.Document document) {
        boolean exists = false;
        try {
            String attachFileName = FileLoader.getAttachFileName(document);
            File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
            boolean exists2 = f.exists();
            exists = exists2;
            if (exists2) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
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
        return exists;
    }

    private void showProgress(final boolean show) {
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        if (show) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{1.0f})});
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    if (!show) {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    AnimatorSet unused = BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }

    public void show(int account, TLRPC.TL_help_appUpdate update, boolean check) {
        this.pressCount = 0;
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(update.text);
        MessageObject.addEntitiesToText(builder, update.entities, false, false, false, false);
        this.textView.setText(builder);
        if (update.document instanceof TLRPC.TL_document) {
            TextView textView2 = this.acceptTextView;
            textView2.setText(LocaleController.getString("Update", NUM) + String.format(Locale.US, " (%1$s)", new Object[]{AndroidUtilities.formatFileSize(update.document.size)}));
        } else {
            this.acceptTextView.setText(LocaleController.getString("Update", NUM));
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        if (check) {
            TLRPC.TL_help_getAppUpdate req = new TLRPC.TL_help_getAppUpdate();
            try {
                req.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception e) {
            }
            if (req.source == null) {
                req.source = "";
            }
            ConnectionsManager.getInstance(this.accountNum).sendRequest(req, new BlockingUpdateView$$ExternalSyntheticLambda3(this));
        }
    }

    /* renamed from: lambda$show$3$org-telegram-ui-Components-BlockingUpdateView  reason: not valid java name */
    public /* synthetic */ void m571lambda$show$3$orgtelegramuiComponentsBlockingUpdateView(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new BlockingUpdateView$$ExternalSyntheticLambda2(this, response));
    }

    /* renamed from: lambda$show$2$org-telegram-ui-Components-BlockingUpdateView  reason: not valid java name */
    public /* synthetic */ void m570lambda$show$2$orgtelegramuiComponentsBlockingUpdateView(TLObject response) {
        if ((response instanceof TLRPC.TL_help_appUpdate) && !((TLRPC.TL_help_appUpdate) response).can_not_skip) {
            setVisibility(8);
            SharedConfig.pendingAppUpdate = null;
            SharedConfig.saveConfig();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.gradientDrawableTop.setBounds(this.scrollView.getLeft(), this.scrollView.getTop(), this.scrollView.getRight(), this.scrollView.getTop() + AndroidUtilities.dp(16.0f));
        this.gradientDrawableTop.draw(canvas);
        this.gradientDrawableBottom.setBounds(this.scrollView.getLeft(), this.scrollView.getBottom() - AndroidUtilities.dp(18.0f), this.scrollView.getRight(), this.scrollView.getBottom());
        this.gradientDrawableBottom.draw(canvas);
    }
}
