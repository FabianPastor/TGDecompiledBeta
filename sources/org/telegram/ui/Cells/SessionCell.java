package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class SessionCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView detailExTextView;
    private TextView detailTextView;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;
    private TextView onlineTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SessionCell(Context context, int i) {
        super(context);
        Context context2 = context;
        LinearLayout linearLayout = new LinearLayout(context2);
        int i2 = 0;
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        int i3 = 15;
        int i4 = 5;
        if (i == 1) {
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 15 : 49), 11.0f, (float) (!LocaleController.isRTL ? 15 : 49), 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            this.avatarDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            this.imageView = new BackupImageView(context2);
            this.imageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            addView(this.imageView, LayoutHelper.createFrame(20, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 21), 13.0f, (float) (LocaleController.isRTL ? 21 : i2), 0.0f));
        } else {
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 15 : 21), 11.0f, (float) (LocaleController.isRTL ? 21 : i3), 0.0f));
        }
        this.nameTextView = new TextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.onlineTextView = new TextView(context2);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        if (LocaleController.isRTL) {
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 53, 10, 0, 0, 0));
        } else {
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 51, 0, 0, 10, 0));
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
        }
        this.detailTextView = new TextView(context2);
        this.detailTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 36.0f, 21.0f, 0.0f));
        this.detailExTextView = new TextView(context2);
        this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i4) | 48, 21.0f, 59.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setSession(TLObject tLObject, boolean z) {
        String str;
        this.needDivider = z;
        if (tLObject instanceof TLRPC.TL_authorization) {
            TLRPC.TL_authorization tL_authorization = (TLRPC.TL_authorization) tLObject;
            this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{tL_authorization.app_name, tL_authorization.app_version}));
            if ((tL_authorization.flags & 1) != 0) {
                setTag("windowBackgroundWhiteValueText");
                this.onlineTextView.setText(LocaleController.getString("Online", NUM));
                this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
            } else {
                setTag("windowBackgroundWhiteGrayText3");
                this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_authorization.date_active));
                this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            }
            StringBuilder sb = new StringBuilder();
            if (tL_authorization.ip.length() != 0) {
                sb.append(tL_authorization.ip);
            }
            if (tL_authorization.country.length() != 0) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append("— ");
                sb.append(tL_authorization.country);
            }
            this.detailExTextView.setText(sb);
            StringBuilder sb2 = new StringBuilder();
            if (tL_authorization.device_model.length() != 0) {
                if (sb2.length() != 0) {
                    sb2.append(", ");
                }
                sb2.append(tL_authorization.device_model);
            }
            if (!(tL_authorization.system_version.length() == 0 && tL_authorization.platform.length() == 0)) {
                if (sb2.length() != 0) {
                    sb2.append(", ");
                }
                if (tL_authorization.platform.length() != 0) {
                    sb2.append(tL_authorization.platform);
                }
                if (tL_authorization.system_version.length() != 0) {
                    if (tL_authorization.platform.length() != 0) {
                        sb2.append(" ");
                    }
                    sb2.append(tL_authorization.system_version);
                }
            }
            if (!tL_authorization.official_app) {
                if (sb2.length() != 0) {
                    sb2.append(", ");
                }
                sb2.append(LocaleController.getString("UnofficialApp", NUM));
                sb2.append(" (ID: ");
                sb2.append(tL_authorization.api_id);
                sb2.append(")");
            }
            this.detailTextView.setText(sb2);
        } else if (tLObject instanceof TLRPC.TL_webAuthorization) {
            TLRPC.TL_webAuthorization tL_webAuthorization = (TLRPC.TL_webAuthorization) tLObject;
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_webAuthorization.bot_id));
            this.nameTextView.setText(tL_webAuthorization.domain);
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                str = UserObject.getFirstName(user);
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            } else {
                str = "";
            }
            setTag("windowBackgroundWhiteGrayText3");
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            StringBuilder sb3 = new StringBuilder();
            if (tL_webAuthorization.ip.length() != 0) {
                sb3.append(tL_webAuthorization.ip);
            }
            if (tL_webAuthorization.region.length() != 0) {
                if (sb3.length() != 0) {
                    sb3.append(" ");
                }
                sb3.append("— ");
                sb3.append(tL_webAuthorization.region);
            }
            this.detailExTextView.setText(sb3);
            StringBuilder sb4 = new StringBuilder();
            if (!TextUtils.isEmpty(str)) {
                sb4.append(str);
            }
            if (tL_webAuthorization.browser.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tL_webAuthorization.browser);
            }
            if (tL_webAuthorization.platform.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tL_webAuthorization.platform);
            }
            this.detailTextView.setText(sb4);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
