package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
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
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
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
    private boolean showStub;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SessionCell(Context context, int i) {
        super(context);
        Context context2 = context;
        int i2 = i;
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        int i3 = 0;
        linearLayout2.setOrientation(0);
        this.linearLayout.setWeightSum(1.0f);
        int i4 = 15;
        int i5 = 5;
        if (i2 == 1) {
            LinearLayout linearLayout3 = this.linearLayout;
            boolean z = LocaleController.isRTL;
            addView(linearLayout3, LayoutHelper.createFrame(-1, 30.0f, (z ? 5 : 3) | 48, (float) (z ? 15 : 49), 11.0f, (float) (z ? 49 : i4), 0.0f));
            AvatarDrawable avatarDrawable2 = new AvatarDrawable();
            this.avatarDrawable = avatarDrawable2;
            avatarDrawable2.setTextSize(AndroidUtilities.dp(10.0f));
            BackupImageView backupImageView = new BackupImageView(context2);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            BackupImageView backupImageView2 = this.imageView;
            boolean z2 = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(20, 20.0f, (z2 ? 5 : 3) | 48, (float) (z2 ? 0 : 21), 13.0f, (float) (z2 ? 21 : i3), 0.0f));
        } else {
            BackupImageView backupImageView3 = new BackupImageView(context2);
            this.imageView = backupImageView3;
            backupImageView3.setRoundRadius(AndroidUtilities.dp(10.0f));
            BackupImageView backupImageView4 = this.imageView;
            boolean z3 = LocaleController.isRTL;
            addView(backupImageView4, LayoutHelper.createFrame(42, 42.0f, (z3 ? 5 : 3) | 48, (float) (z3 ? 0 : 16), 13.0f, (float) (z3 ? 16 : i3), 0.0f));
            LinearLayout linearLayout4 = this.linearLayout;
            boolean z4 = LocaleController.isRTL;
            addView(linearLayout4, LayoutHelper.createFrame(-1, 30.0f, (z4 ? 5 : 3) | 48, (float) (z4 ? 15 : 72), 11.0f, (float) (z4 ? 72 : i4), 0.0f));
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
        TextView textView3 = new TextView(context2);
        this.detailTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, i2 == 0 ? 72.0f : 21.0f, 36.0f, 21.0f, 0.0f));
        TextView textView4 = new TextView(context2);
        this.detailExTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i5) | 48, i2 == 0 ? 72.0f : 21.0f, 59.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setSession(TLObject tLObject, boolean z) {
        String str;
        String str2;
        this.needDivider = z;
        if (tLObject instanceof TLRPC$TL_authorization) {
            TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) tLObject;
            this.imageView.setImageDrawable(createDrawable(tLRPC$TL_authorization));
            StringBuilder sb = new StringBuilder();
            if (tLRPC$TL_authorization.device_model.length() != 0) {
                sb.append(tLRPC$TL_authorization.device_model);
            }
            if (sb.length() == 0) {
                if (tLRPC$TL_authorization.platform.length() != 0) {
                    sb.append(tLRPC$TL_authorization.platform);
                }
                if (tLRPC$TL_authorization.system_version.length() != 0) {
                    if (tLRPC$TL_authorization.platform.length() != 0) {
                        sb.append(" ");
                    }
                    sb.append(tLRPC$TL_authorization.system_version);
                }
            }
            this.nameTextView.setText(sb);
            if ((tLRPC$TL_authorization.flags & 1) != 0) {
                setTag("windowBackgroundWhiteValueText");
                str2 = LocaleController.getString("Online", NUM);
            } else {
                setTag("windowBackgroundWhiteGrayText3");
                str2 = LocaleController.stringForMessageListDate((long) tLRPC$TL_authorization.date_active);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (tLRPC$TL_authorization.country.length() != 0) {
                spannableStringBuilder.append(tLRPC$TL_authorization.country);
            }
            if (spannableStringBuilder.length() != 0) {
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder.append(" . ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 0);
            }
            spannableStringBuilder.append(str2);
            this.detailExTextView.setText(spannableStringBuilder);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tLRPC$TL_authorization.app_name);
            sb2.append(" ");
            sb2.append(tLRPC$TL_authorization.app_version);
            this.detailTextView.setText(sb2);
        } else if (tLObject instanceof TLRPC$TL_webAuthorization) {
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) tLObject;
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_webAuthorization.bot_id));
            this.nameTextView.setText(tLRPC$TL_webAuthorization.domain);
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                str = UserObject.getFirstName(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                str = "";
            }
            setTag("windowBackgroundWhiteGrayText3");
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tLRPC$TL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            StringBuilder sb3 = new StringBuilder();
            if (tLRPC$TL_webAuthorization.ip.length() != 0) {
                sb3.append(tLRPC$TL_webAuthorization.ip);
            }
            if (tLRPC$TL_webAuthorization.region.length() != 0) {
                if (sb3.length() != 0) {
                    sb3.append(" ");
                }
                sb3.append("— ");
                sb3.append(tLRPC$TL_webAuthorization.region);
            }
            this.detailExTextView.setText(sb3);
            StringBuilder sb4 = new StringBuilder();
            if (!TextUtils.isEmpty(str)) {
                sb4.append(str);
            }
            if (tLRPC$TL_webAuthorization.browser.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.browser);
            }
            if (tLRPC$TL_webAuthorization.platform.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.platform);
            }
            this.detailTextView.setText(sb4);
        }
        if (this.showStub) {
            this.showStub = false;
            invalidate();
        }
    }

    public static Drawable createDrawable(TLRPC$TL_authorization tLRPC$TL_authorization) {
        int i;
        int i2;
        String lowerCase = tLRPC$TL_authorization.platform.toLowerCase();
        if (lowerCase.isEmpty()) {
            lowerCase = tLRPC$TL_authorization.system_version.toLowerCase();
        }
        String lowerCase2 = tLRPC$TL_authorization.device_model.toLowerCase();
        String str = "avatar_backgroundCyan";
        if (lowerCase2.contains("safari")) {
            i2 = NUM;
        } else if (lowerCase2.contains("edge")) {
            i2 = NUM;
        } else if (lowerCase2.contains("chrome")) {
            i2 = NUM;
        } else {
            if (lowerCase.contains("ios")) {
                i = lowerCase2.contains("ipad") ? NUM : NUM;
                str = "avatar_backgroundBlue";
            } else if (lowerCase.contains("windows")) {
                i = NUM;
            } else if (lowerCase.contains("macos")) {
                i = NUM;
            } else if (lowerCase.contains("android")) {
                i = lowerCase2.contains("tab") ? NUM : NUM;
                str = "avatar_backgroundGreen";
            } else if (tLRPC$TL_authorization.app_name.toLowerCase().contains("desktop")) {
                i = NUM;
            } else {
                i2 = NUM;
            }
            Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
            return new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(str)), mutate);
        }
        str = "avatar_backgroundPink";
        Drawable mutate2 = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        return new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(str)), mutate2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        FlickerLoadingView flickerLoadingView;
        if (this.showStub && (flickerLoadingView = this.globalGradient) != null) {
            flickerLoadingView.updateColors();
            this.globalGradient.updateGradient();
            if (getParent() != null) {
                View view = (View) getParent();
                this.globalGradient.setParentSize(view.getMeasuredWidth(), view.getMeasuredHeight(), -getX());
            }
            float top = (float) (this.linearLayout.getTop() + this.nameTextView.getTop() + AndroidUtilities.dp(12.0f));
            float x = this.linearLayout.getX();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(x, top - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.2f) + x, top + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float top2 = (float) ((this.linearLayout.getTop() + this.detailTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x2 = this.linearLayout.getX();
            rectF.set(x2, top2 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.4f) + x2, top2 + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float top3 = (float) ((this.linearLayout.getTop() + this.detailExTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x3 = this.linearLayout.getX();
            rectF.set(x3, top3 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.3f) + x3, top3 + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            invalidate();
        }
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void showStub(FlickerLoadingView flickerLoadingView) {
        this.globalGradient = flickerLoadingView;
        this.showStub = true;
        Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, AndroidUtilities.isTablet() ? NUM : NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        this.imageView.setImageDrawable(new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor("avatar_backgroundGreen")), mutate));
        invalidate();
    }

    public boolean isStub() {
        return this.showStub;
    }
}
