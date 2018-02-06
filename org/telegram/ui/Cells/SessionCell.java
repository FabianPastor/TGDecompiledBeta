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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
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

    public SessionCell(Context context, int type) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        if (type == 1) {
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 11 : 45), 11.0f, (float) (LocaleController.isRTL ? 45 : 11), 0.0f));
            this.avatarDrawable = new AvatarDrawable();
            this.avatarDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            addView(this.imageView, LayoutHelper.createFrame(20, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 17), 13.0f, (float) (LocaleController.isRTL ? 17 : 0), 0.0f));
        } else {
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 11 : 17), 11.0f, (float) (LocaleController.isRTL ? 17 : 11), 0.0f));
        }
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        if (LocaleController.isRTL) {
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 53, 10, 0, 0, 0));
        } else {
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 51, 0, 0, 10, 0));
            linearLayout.addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
        }
        this.detailTextView = new TextView(context);
        this.detailTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 36.0f, 17.0f, 0.0f));
        this.detailExTextView = new TextView(context);
        this.detailExTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 59.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(90.0f), NUM));
    }

    public void setSession(TLObject object, boolean divider) {
        this.needDivider = divider;
        StringBuilder stringBuilder;
        if (object instanceof TL_authorization) {
            TL_authorization session = (TL_authorization) object;
            this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{session.app_name, session.app_version}));
            if ((session.flags & 1) != 0) {
                setTag(Theme.key_windowBackgroundWhiteValueText);
                this.onlineTextView.setText(LocaleController.getString("Online", R.string.Online));
                this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            } else {
                setTag(Theme.key_windowBackgroundWhiteGrayText3);
                this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) session.date_active));
                this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            }
            stringBuilder = new StringBuilder();
            if (session.ip.length() != 0) {
                stringBuilder.append(session.ip);
            }
            if (session.country.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append("— ");
                stringBuilder.append(session.country);
            }
            this.detailExTextView.setText(stringBuilder);
            stringBuilder = new StringBuilder();
            if (session.device_model.length() != 0) {
                stringBuilder.append(session.device_model);
            }
            if (!(session.system_version.length() == 0 && session.platform.length() == 0)) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(", ");
                }
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
            if ((session.flags & 2) == 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(LocaleController.getString("UnofficialApp", R.string.UnofficialApp));
                stringBuilder.append(" (ID: ");
                stringBuilder.append(session.api_id);
                stringBuilder.append(")");
            }
            this.detailTextView.setText(stringBuilder);
        } else if (object instanceof TL_webAuthorization) {
            String name;
            TL_webAuthorization session2 = (TL_webAuthorization) object;
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(session2.bot_id));
            this.nameTextView.setText(session2.domain);
            if (user != null) {
                TLObject currentPhoto;
                this.avatarDrawable.setInfo(user);
                name = UserObject.getFirstName(user);
                if (user.photo != null) {
                    currentPhoto = user.photo.photo_small;
                } else {
                    currentPhoto = null;
                }
                this.imageView.setImage(currentPhoto, "50_50", this.avatarDrawable);
            } else {
                name = TtmlNode.ANONYMOUS_REGION_ID;
            }
            setTag(Theme.key_windowBackgroundWhiteGrayText3);
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) session2.date_active));
            this.onlineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            stringBuilder = new StringBuilder();
            if (session2.ip.length() != 0) {
                stringBuilder.append(session2.ip);
            }
            if (session2.region.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append("— ");
                stringBuilder.append(session2.region);
            }
            this.detailExTextView.setText(stringBuilder);
            stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(name)) {
                stringBuilder.append(name);
            }
            if (session2.browser.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(session2.browser);
            }
            if (session2.platform.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(session2.platform);
            }
            this.detailTextView.setText(stringBuilder);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
