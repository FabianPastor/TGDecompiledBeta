package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.tgnet.TLRPC.User;
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

    public SessionCell(Context context, int i) {
        Context context2 = context;
        super(context);
        View linearLayout = new LinearLayout(context2);
        int i2 = 0;
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        int i3 = 17;
        int i4 = 3;
        int i5;
        float f;
        if (i == 1) {
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i6 = 45;
            f = (float) (LocaleController.isRTL ? 11 : 45);
            if (!LocaleController.isRTL) {
                i6 = 11;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i5, f, 11.0f, (float) i6, 0.0f));
            r0.avatarDrawable = new AvatarDrawable();
            r0.avatarDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            r0.imageView = new BackupImageView(context2);
            r0.imageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            View view = r0.imageView;
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 0 : 17);
            if (LocaleController.isRTL) {
                i2 = 17;
            }
            addView(view, LayoutHelper.createFrame(20, 20.0f, i5, f, 13.0f, (float) i2, 0.0f));
        } else {
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 11 : 17);
            if (!LocaleController.isRTL) {
                i3 = 11;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i5, f, 11.0f, (float) i3, 0.0f));
        }
        r0.nameTextView = new TextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(1, 16.0f);
        r0.nameTextView.setLines(1);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        r0.onlineTextView = new TextView(context2);
        r0.onlineTextView.setTextSize(1, 14.0f);
        r0.onlineTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        if (LocaleController.isRTL) {
            linearLayout.addView(r0.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
            linearLayout.addView(r0.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 53, 10, 0, 0, 0));
        } else {
            linearLayout.addView(r0.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 51, 0, 0, 10, 0));
            linearLayout.addView(r0.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
        }
        r0.detailTextView = new TextView(context2);
        r0.detailTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.detailTextView.setTextSize(1, 14.0f);
        r0.detailTextView.setLines(1);
        r0.detailTextView.setMaxLines(1);
        r0.detailTextView.setSingleLine(true);
        r0.detailTextView.setEllipsize(TruncateAt.END);
        r0.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 36.0f, 17.0f, 0.0f));
        r0.detailExTextView = new TextView(context2);
        r0.detailExTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.detailExTextView.setTextSize(1, 14.0f);
        r0.detailExTextView.setLines(1);
        r0.detailExTextView.setMaxLines(1);
        r0.detailExTextView.setSingleLine(true);
        r0.detailExTextView.setEllipsize(TruncateAt.END);
        r0.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view2 = r0.detailExTextView;
        if (LocaleController.isRTL) {
            i4 = 5;
        }
        addView(view2, LayoutHelper.createFrame(-1, -2.0f, i4 | 48, 17.0f, 59.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + this.needDivider, NUM));
    }

    public void setSession(TLObject tLObject, boolean z) {
        this.needDivider = z;
        if (tLObject instanceof TL_authorization) {
            TL_authorization tL_authorization = (TL_authorization) tLObject;
            this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{tL_authorization.app_name, tL_authorization.app_version}));
            if (tL_authorization.flags & true) {
                setTag(Theme.key_windowBackgroundWhiteValueText);
                this.onlineTextView.setText(LocaleController.getString("Online", C0446R.string.Online));
                this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            } else {
                setTag(Theme.key_windowBackgroundWhiteGrayText3);
                this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_authorization.date_active));
                this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            }
            z = new StringBuilder();
            if (tL_authorization.ip.length() != 0) {
                z.append(tL_authorization.ip);
            }
            if (tL_authorization.country.length() != 0) {
                if (z.length() != 0) {
                    z.append(" ");
                }
                z.append("\u2014 ");
                z.append(tL_authorization.country);
            }
            this.detailExTextView.setText(z);
            z = new StringBuilder();
            if (tL_authorization.device_model.length() != 0) {
                z.append(tL_authorization.device_model);
            }
            if (!(tL_authorization.system_version.length() == 0 && tL_authorization.platform.length() == 0)) {
                if (z.length() != 0) {
                    z.append(", ");
                }
                if (tL_authorization.platform.length() != 0) {
                    z.append(tL_authorization.platform);
                }
                if (tL_authorization.system_version.length() != 0) {
                    if (tL_authorization.platform.length() != 0) {
                        z.append(" ");
                    }
                    z.append(tL_authorization.system_version);
                }
            }
            if ((tL_authorization.flags & 2) == 0) {
                if (z.length() != 0) {
                    z.append(", ");
                }
                z.append(LocaleController.getString("UnofficialApp", C0446R.string.UnofficialApp));
                z.append(" (ID: ");
                z.append(tL_authorization.api_id);
                z.append(")");
            }
            this.detailTextView.setText(z);
        } else if (tLObject instanceof TL_webAuthorization) {
            Object firstName;
            TL_webAuthorization tL_webAuthorization = (TL_webAuthorization) tLObject;
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_webAuthorization.bot_id));
            this.nameTextView.setText(tL_webAuthorization.domain);
            if (user == true) {
                this.avatarDrawable.setInfo(user);
                firstName = UserObject.getFirstName(user);
                this.imageView.setImage(user.photo != null ? user.photo.photo_small : false, "50_50", this.avatarDrawable);
            } else {
                firstName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            setTag(Theme.key_windowBackgroundWhiteGrayText3);
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            z = new StringBuilder();
            if (tL_webAuthorization.ip.length() != 0) {
                z.append(tL_webAuthorization.ip);
            }
            if (tL_webAuthorization.region.length() != 0) {
                if (z.length() != 0) {
                    z.append(" ");
                }
                z.append("\u2014 ");
                z.append(tL_webAuthorization.region);
            }
            this.detailExTextView.setText(z);
            z = new StringBuilder();
            if (!TextUtils.isEmpty(firstName)) {
                z.append(firstName);
            }
            if (tL_webAuthorization.browser.length() != 0) {
                if (z.length() != 0) {
                    z.append(", ");
                }
                z.append(tL_webAuthorization.browser);
            }
            if (tL_webAuthorization.platform.length() != 0) {
                if (z.length() != 0) {
                    z.append(", ");
                }
                z.append(tL_webAuthorization.platform);
            }
            this.detailTextView.setText(z);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
