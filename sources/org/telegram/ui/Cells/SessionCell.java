package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
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
        LinearLayout linearLayout = new LinearLayout(context2);
        int i2 = 0;
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        int i3 = 15;
        int i4 = 5;
        int i5;
        float f;
        if (i == 1) {
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i6 = 49;
            f = (float) (LocaleController.isRTL ? 15 : 49);
            if (!LocaleController.isRTL) {
                i6 = 15;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i5, f, 11.0f, (float) i6, 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            this.avatarDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            this.imageView = new BackupImageView(context2);
            this.imageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            BackupImageView backupImageView = this.imageView;
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 0 : 21);
            if (LocaleController.isRTL) {
                i2 = 21;
            }
            addView(backupImageView, LayoutHelper.createFrame(20, 20.0f, i5, f, 13.0f, (float) i2, 0.0f));
        } else {
            i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 15 : 21);
            if (LocaleController.isRTL) {
                i3 = 21;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i5, f, 11.0f, (float) i3, 0.0f));
        }
        this.nameTextView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        this.nameTextView.setTextColor(Theme.getColor(str));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
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
        this.detailTextView.setTextColor(Theme.getColor(str));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 36.0f, 21.0f, 0.0f));
        this.detailExTextView = new TextView(context2);
        this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i4 = 3;
        }
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, i4 | 48, 21.0f, 59.0f, 21.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + this.needDivider, NUM));
    }

    public void setSession(TLObject tLObject, boolean z) {
        this.needDivider = z;
        String str = "— ";
        String str2 = " ";
        String str3 = "windowBackgroundWhiteGrayText3";
        String str4 = ", ";
        StringBuilder stringBuilder;
        if (tLObject instanceof TL_authorization) {
            TL_authorization tL_authorization = (TL_authorization) tLObject;
            this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{tL_authorization.app_name, tL_authorization.app_version}));
            if ((tL_authorization.flags & 1) != 0) {
                String str5 = "windowBackgroundWhiteValueText";
                setTag(str5);
                this.onlineTextView.setText(LocaleController.getString("Online", NUM));
                this.onlineTextView.setTextColor(Theme.getColor(str5));
            } else {
                setTag(str3);
                this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_authorization.date_active));
                this.onlineTextView.setTextColor(Theme.getColor(str3));
            }
            stringBuilder = new StringBuilder();
            if (tL_authorization.ip.length() != 0) {
                stringBuilder.append(tL_authorization.ip);
            }
            if (tL_authorization.country.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str2);
                }
                stringBuilder.append(str);
                stringBuilder.append(tL_authorization.country);
            }
            this.detailExTextView.setText(stringBuilder);
            stringBuilder = new StringBuilder();
            if (tL_authorization.device_model.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str4);
                }
                stringBuilder.append(tL_authorization.device_model);
            }
            if (!(tL_authorization.system_version.length() == 0 && tL_authorization.platform.length() == 0)) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str4);
                }
                if (tL_authorization.platform.length() != 0) {
                    stringBuilder.append(tL_authorization.platform);
                }
                if (tL_authorization.system_version.length() != 0) {
                    if (tL_authorization.platform.length() != 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append(tL_authorization.system_version);
                }
            }
            if (!tL_authorization.official_app) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str4);
                }
                stringBuilder.append(LocaleController.getString("UnofficialApp", NUM));
                stringBuilder.append(" (ID: ");
                stringBuilder.append(tL_authorization.api_id);
                stringBuilder.append(")");
            }
            this.detailTextView.setText(stringBuilder);
        } else if (tLObject instanceof TL_webAuthorization) {
            CharSequence firstName;
            TL_webAuthorization tL_webAuthorization = (TL_webAuthorization) tLObject;
            Object user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tL_webAuthorization.bot_id));
            this.nameTextView.setText(tL_webAuthorization.domain);
            if (user != null) {
                this.avatarDrawable.setInfo((User) user);
                firstName = UserObject.getFirstName(user);
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", this.avatarDrawable, user);
            } else {
                firstName = "";
            }
            setTag(str3);
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor(str3));
            stringBuilder = new StringBuilder();
            if (tL_webAuthorization.ip.length() != 0) {
                stringBuilder.append(tL_webAuthorization.ip);
            }
            if (tL_webAuthorization.region.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str2);
                }
                stringBuilder.append(str);
                stringBuilder.append(tL_webAuthorization.region);
            }
            this.detailExTextView.setText(stringBuilder);
            stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(firstName)) {
                stringBuilder.append(firstName);
            }
            if (tL_webAuthorization.browser.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str4);
                }
                stringBuilder.append(tL_webAuthorization.browser);
            }
            if (tL_webAuthorization.platform.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str4);
                }
                stringBuilder.append(tL_webAuthorization.platform);
            }
            this.detailTextView.setText(stringBuilder);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
