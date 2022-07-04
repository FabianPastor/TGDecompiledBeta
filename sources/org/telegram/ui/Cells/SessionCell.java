package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;

public class SessionCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView detailExTextView;
    private TextView detailTextView;
    FlickerLoadingView globalGradient;
    private BackupImageView imageView;
    LinearLayout linearLayout;
    private TextView nameTextView;
    private boolean needDivider;
    private TextView onlineTextView;
    private BackupImageView placeholderImageView;
    private boolean showStub;
    private AnimatedFloat showStubValue = new AnimatedFloat(this);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SessionCell(Context context, int type) {
        super(context);
        int leftMargin;
        int rightMargin;
        Context context2 = context;
        int i = type;
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        int i2 = 0;
        linearLayout2.setOrientation(0);
        this.linearLayout.setWeightSum(1.0f);
        int i3 = 15;
        int i4 = 5;
        if (i == 1) {
            addView(this.linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 15 : 49), 11.0f, (float) (LocaleController.isRTL ? 49 : i3), 0.0f));
            AvatarDrawable avatarDrawable2 = new AvatarDrawable();
            this.avatarDrawable = avatarDrawable2;
            avatarDrawable2.setTextSize(AndroidUtilities.dp(10.0f));
            BackupImageView backupImageView = new BackupImageView(context2);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            addView(this.imageView, LayoutHelper.createFrame(20, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 21), 13.0f, (float) (LocaleController.isRTL ? 21 : i2), 0.0f));
        } else {
            BackupImageView backupImageView2 = new BackupImageView(context2);
            this.placeholderImageView = backupImageView2;
            backupImageView2.setRoundRadius(AndroidUtilities.dp(10.0f));
            addView(this.placeholderImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 16), 13.0f, (float) (LocaleController.isRTL ? 16 : 0), 0.0f));
            BackupImageView backupImageView3 = new BackupImageView(context2);
            this.imageView = backupImageView3;
            backupImageView3.setRoundRadius(AndroidUtilities.dp(10.0f));
            addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 16), 13.0f, (float) (LocaleController.isRTL ? 16 : i2), 0.0f));
            addView(this.linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 15 : 72), 11.0f, (float) (LocaleController.isRTL ? 72 : i3), 0.0f));
        }
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView2 = new TextView(context2);
        this.onlineTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.onlineTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        if (LocaleController.isRTL) {
            this.linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
            this.linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 53, 10, 0, 0, 0));
        } else {
            this.linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 51, 0, 0, 10, 0));
            this.linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
        }
        if (LocaleController.isRTL) {
            rightMargin = i == 0 ? 72 : 21;
            leftMargin = 21;
        } else {
            leftMargin = i == 0 ? 72 : 21;
            rightMargin = 21;
        }
        TextView textView3 = new TextView(context2);
        this.detailTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) leftMargin, 36.0f, (float) rightMargin, 0.0f));
        TextView textView4 = new TextView(context2);
        this.detailExTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i4) | 48, (float) leftMargin, 59.0f, (float) rightMargin, 0.0f));
    }

    private void setContentAlpha(float alpha) {
        TextView textView = this.detailExTextView;
        if (textView != null) {
            textView.setAlpha(alpha);
        }
        TextView textView2 = this.detailTextView;
        if (textView2 != null) {
            textView2.setAlpha(alpha);
        }
        TextView textView3 = this.nameTextView;
        if (textView3 != null) {
            textView3.setAlpha(alpha);
        }
        TextView textView4 = this.onlineTextView;
        if (textView4 != null) {
            textView4.setAlpha(alpha);
        }
        BackupImageView backupImageView = this.imageView;
        if (backupImageView != null) {
            backupImageView.setAlpha(alpha);
        }
        BackupImageView backupImageView2 = this.placeholderImageView;
        if (backupImageView2 != null) {
            backupImageView2.setAlpha(1.0f - alpha);
        }
        LinearLayout linearLayout2 = this.linearLayout;
        if (linearLayout2 != null) {
            linearLayout2.setAlpha(alpha);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setSession(TLObject object, boolean divider) {
        String name;
        String timeText;
        this.needDivider = divider;
        if (object instanceof TLRPC.TL_authorization) {
            TLRPC.TL_authorization session = (TLRPC.TL_authorization) object;
            this.imageView.setImageDrawable(createDrawable(session));
            StringBuilder stringBuilder = new StringBuilder();
            if (session.device_model.length() != 0) {
                stringBuilder.append(session.device_model);
            }
            if (stringBuilder.length() == 0) {
                if (session.platform.length() != 0) {
                    stringBuilder.append(session.platform);
                }
                if (session.system_version.length() != 0) {
                    if (session.platform.length() != 0) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append(session.system_version);
                }
            }
            this.nameTextView.setText(stringBuilder);
            if ((session.flags & 1) != 0) {
                setTag("windowBackgroundWhiteValueText");
                timeText = LocaleController.getString("Online", NUM);
            } else {
                setTag("windowBackgroundWhiteGrayText3");
                timeText = LocaleController.stringForMessageListDate((long) session.date_active);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (session.country.length() != 0) {
                spannableStringBuilder.append(session.country);
            }
            if (spannableStringBuilder.length() != 0) {
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder.append(" . ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 0);
            }
            spannableStringBuilder.append(timeText);
            this.detailExTextView.setText(spannableStringBuilder);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(session.app_name);
            stringBuilder2.append(" ");
            stringBuilder2.append(session.app_version);
            this.detailTextView.setText(stringBuilder2);
        } else if (object instanceof TLRPC.TL_webAuthorization) {
            TLRPC.TL_webAuthorization session2 = (TLRPC.TL_webAuthorization) object;
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(session2.bot_id));
            this.nameTextView.setText(session2.domain);
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                name = UserObject.getFirstName(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                name = "";
            }
            setTag("windowBackgroundWhiteGrayText3");
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) session2.date_active));
            this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            StringBuilder stringBuilder3 = new StringBuilder();
            if (session2.ip.length() != 0) {
                stringBuilder3.append(session2.ip);
            }
            if (session2.region.length() != 0) {
                if (stringBuilder3.length() != 0) {
                    stringBuilder3.append(" ");
                }
                stringBuilder3.append("— ");
                stringBuilder3.append(session2.region);
            }
            this.detailExTextView.setText(stringBuilder3);
            StringBuilder stringBuilder4 = new StringBuilder();
            if (!TextUtils.isEmpty(name)) {
                stringBuilder4.append(name);
            }
            if (session2.browser.length() != 0) {
                if (stringBuilder4.length() != 0) {
                    stringBuilder4.append(", ");
                }
                stringBuilder4.append(session2.browser);
            }
            if (session2.platform.length() != 0) {
                if (stringBuilder4.length() != 0) {
                    stringBuilder4.append(", ");
                }
                stringBuilder4.append(session2.platform);
            }
            this.detailTextView.setText(stringBuilder4);
        }
        if (this.showStub) {
            this.showStub = false;
            invalidate();
        }
    }

    public static Drawable createDrawable(TLRPC.TL_authorization session) {
        String colorKey;
        int iconId;
        String platform = session.platform.toLowerCase();
        if (platform.isEmpty()) {
            platform = session.system_version.toLowerCase();
        }
        String deviceModel = session.device_model.toLowerCase();
        if (deviceModel.contains("safari")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("edge")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("chrome")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("opera")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("firefox")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("vivaldi")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (platform.contains("ios")) {
            iconId = deviceModel.contains("ipad") ? NUM : NUM;
            colorKey = "avatar_backgroundBlue";
        } else if (platform.contains("windows")) {
            iconId = NUM;
            colorKey = "avatar_backgroundCyan";
        } else if (platform.contains("macos")) {
            iconId = NUM;
            colorKey = "avatar_backgroundCyan";
        } else if (platform.contains("android")) {
            iconId = deviceModel.contains("tab") ? NUM : NUM;
            colorKey = "avatar_backgroundGreen";
        } else if (session.app_name.toLowerCase().contains("desktop")) {
            iconId = NUM;
            colorKey = "avatar_backgroundCyan";
        } else {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        }
        Drawable iconDrawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, iconId).mutate();
        iconDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        return new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(colorKey)), iconDrawable);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float stubAlpha = this.showStubValue.set(this.showStub ? 1.0f : 0.0f);
        setContentAlpha(1.0f - stubAlpha);
        if (stubAlpha > 0.0f && this.globalGradient != null) {
            if (stubAlpha < 1.0f) {
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (255.0f * stubAlpha), 31);
            }
            this.globalGradient.updateColors();
            this.globalGradient.updateGradient();
            if (getParent() != null) {
                View parent = (View) getParent();
                this.globalGradient.setParentSize(parent.getMeasuredWidth(), parent.getMeasuredHeight(), -getX());
            }
            float y = (float) (this.linearLayout.getTop() + this.nameTextView.getTop() + AndroidUtilities.dp(12.0f));
            float x = this.linearLayout.getX();
            AndroidUtilities.rectTmp.set(x, y - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.2f) + x, ((float) AndroidUtilities.dp(4.0f)) + y);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float y2 = (float) ((this.linearLayout.getTop() + this.detailTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x2 = this.linearLayout.getX();
            AndroidUtilities.rectTmp.set(x2, y2 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.4f) + x2, ((float) AndroidUtilities.dp(4.0f)) + y2);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float y3 = (float) ((this.linearLayout.getTop() + this.detailExTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x3 = this.linearLayout.getX();
            AndroidUtilities.rectTmp.set(x3, y3 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.3f) + x3, ((float) AndroidUtilities.dp(4.0f)) + y3);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            invalidate();
            if (stubAlpha < 1.0f) {
                canvas.restore();
            }
        }
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void showStub(FlickerLoadingView globalGradient2) {
        this.globalGradient = globalGradient2;
        this.showStub = true;
        Drawable iconDrawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, AndroidUtilities.isTablet() ? NUM : NUM).mutate();
        iconDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor("avatar_backgroundGreen")), iconDrawable);
        BackupImageView backupImageView = this.placeholderImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(combinedDrawable);
        } else {
            this.imageView.setImageDrawable(combinedDrawable);
        }
        invalidate();
    }

    public boolean isStub() {
        return this.showStub;
    }
}
