package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.ui.Components.LayoutHelper;

public class SessionCell extends FrameLayout {
    private static Paint paint;
    private TextView detailExTextView;
    private TextView detailTextView;
    private TextView nameTextView;
    boolean needDivider;
    private TextView onlineTextView;

    public SessionCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(1.0f);
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        addView(linearLayout, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_ORANGE, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 11.0f, 11.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-14606047);
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
        this.detailTextView.setTextColor(-14606047);
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLines(1);
        this.detailTextView.setMaxLines(1);
        this.detailTextView.setSingleLine(true);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 36.0f, 17.0f, 0.0f));
        this.detailExTextView = new TextView(context);
        this.detailExTextView.setTextColor(-6710887);
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 59.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(90.0f), NUM));
    }

    public void setSession(TL_authorization session, boolean divider) {
        this.needDivider = divider;
        this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[]{session.app_name, session.app_version}));
        if ((session.flags & 1) != 0) {
            this.onlineTextView.setText(LocaleController.getString("Online", R.string.Online));
            this.onlineTextView.setTextColor(-13660983);
        } else {
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) session.date_active));
            this.onlineTextView.setTextColor(-6710887);
        }
        StringBuilder stringBuilder = new StringBuilder();
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
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }
}
