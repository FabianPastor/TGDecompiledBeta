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
        Context context2 = context;
        super(context);
        LinearLayout linearLayout = new LinearLayout(context2);
        int i = 0;
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        int i2 = 17;
        int i3 = 3;
        int i4;
        float f;
        if (type == 1) {
            i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i5 = 45;
            f = (float) (LocaleController.isRTL ? 11 : 45);
            if (!LocaleController.isRTL) {
                i5 = 11;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i4, f, 11.0f, (float) i5, 0.0f));
            r0.avatarDrawable = new AvatarDrawable();
            r0.avatarDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            r0.imageView = new BackupImageView(context2);
            r0.imageView.setRoundRadius(AndroidUtilities.dp(10.0f));
            View view = r0.imageView;
            i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 0 : 17);
            if (LocaleController.isRTL) {
                i = 17;
            }
            addView(view, LayoutHelper.createFrame(20, 20.0f, i4, f, 13.0f, (float) i, 0.0f));
        } else {
            i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            f = (float) (LocaleController.isRTL ? 11 : 17);
            if (!LocaleController.isRTL) {
                i2 = 11;
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, 30.0f, i4, f, 11.0f, (float) i2, 0.0f));
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
            i3 = 5;
        }
        addView(view2, LayoutHelper.createFrame(-1, -2.0f, i3 | 48, 17.0f, 59.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + this.needDivider, NUM));
    }

    public void setSession(TLObject object, boolean divider) {
        this.needDivider = divider;
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
            StringBuilder stringBuilder = new StringBuilder();
            if (session.ip.length() != 0) {
                stringBuilder.append(session.ip);
            }
            if (session.country.length() != 0) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append("\u2014 ");
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
            StringBuilder stringBuilder2 = new StringBuilder();
            if (session2.ip.length() != 0) {
                stringBuilder2.append(session2.ip);
            }
            if (session2.region.length() != 0) {
                if (stringBuilder2.length() != 0) {
                    stringBuilder2.append(" ");
                }
                stringBuilder2.append("\u2014 ");
                stringBuilder2.append(session2.region);
            }
            this.detailExTextView.setText(stringBuilder2);
            stringBuilder2 = new StringBuilder();
            if (!TextUtils.isEmpty(name)) {
                stringBuilder2.append(name);
            }
            if (session2.browser.length() != 0) {
                if (stringBuilder2.length() != 0) {
                    stringBuilder2.append(", ");
                }
                stringBuilder2.append(session2.browser);
            }
            if (session2.platform.length() != 0) {
                if (stringBuilder2.length() != 0) {
                    stringBuilder2.append(", ");
                }
                stringBuilder2.append(session2.platform);
            }
            this.detailTextView.setText(stringBuilder2);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
