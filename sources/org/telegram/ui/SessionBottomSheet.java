package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Switch;

public class SessionBottomSheet extends BottomSheet {
    RLottieImageView imageView;
    BaseFragment parentFragment;
    TLRPC.TL_authorization session;

    public interface Callback {
        void onSessionTerminated(TLRPC.TL_authorization tL_authorization);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SessionBottomSheet(BaseFragment fragment, TLRPC.TL_authorization session2, boolean isCurrentSession, Callback callback) {
        super(fragment.getParentActivity(), false);
        String timeText;
        final BaseFragment baseFragment = fragment;
        final TLRPC.TL_authorization tL_authorization = session2;
        setOpenNoDelay(true);
        Context context = fragment.getParentActivity();
        this.session = tL_authorization;
        this.parentFragment = baseFragment;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!SessionBottomSheet.this.imageView.isPlaying() && SessionBottomSheet.this.imageView.getAnimatedDrawable() != null) {
                    SessionBottomSheet.this.imageView.getAnimatedDrawable().setCurrentFrame(40);
                    SessionBottomSheet.this.imageView.playAnimation();
                }
            }
        });
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(70, 70, 1, 0, 16, 0, 0));
        TextView nameView = new TextView(context);
        nameView.setTextSize(2, 20.0f);
        nameView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        nameView.setGravity(17);
        linearLayout.addView(nameView, LayoutHelper.createLinear(-1, -2, 1, 21, 12, 21, 0));
        TextView timeView = new TextView(context);
        timeView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        timeView.setTextSize(2, 13.0f);
        timeView.setGravity(17);
        linearLayout.addView(timeView, LayoutHelper.createLinear(-1, -2, 1, 21, 4, 21, 21));
        if ((tL_authorization.flags & 1) != 0) {
            timeText = LocaleController.getString("Online", NUM);
        } else {
            timeText = LocaleController.formatDateTime((long) tL_authorization.date_active);
        }
        timeView.setText(timeText);
        StringBuilder stringBuilder = new StringBuilder();
        if (tL_authorization.device_model.length() != 0) {
            stringBuilder.append(tL_authorization.device_model);
        }
        if (stringBuilder.length() == 0) {
            if (tL_authorization.platform.length() != 0) {
                stringBuilder.append(tL_authorization.platform);
            }
            if (tL_authorization.system_version.length() != 0) {
                if (tL_authorization.platform.length() != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(tL_authorization.system_version);
            }
        }
        nameView.setText(stringBuilder);
        setAnimation(tL_authorization, this.imageView);
        ItemView applicationItemView = new ItemView(context, false);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(tL_authorization.app_name);
        stringBuilder2.append(" ");
        stringBuilder2.append(tL_authorization.app_version);
        applicationItemView.valueText.setText(stringBuilder2);
        Drawable drawable = ContextCompat.getDrawable(context, NUM).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
        applicationItemView.iconView.setImageDrawable(drawable);
        applicationItemView.descriptionText.setText(LocaleController.getString("Application", NUM));
        linearLayout.addView(applicationItemView);
        ItemView prevItem = applicationItemView;
        if (tL_authorization.country.length() != 0) {
            ItemView locationItemView = new ItemView(context, false);
            TextView textView = nameView;
            locationItemView.valueText.setText(tL_authorization.country);
            Drawable drawable2 = ContextCompat.getDrawable(context, NUM).mutate();
            TextView textView2 = timeView;
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            locationItemView.iconView.setImageDrawable(drawable2);
            locationItemView.descriptionText.setText(LocaleController.getString("Location", NUM));
            locationItemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(tL_authorization.country);
                }
            });
            locationItemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(tL_authorization.country);
                    return true;
                }
            });
            locationItemView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            linearLayout.addView(locationItemView);
            prevItem.needDivider = true;
            prevItem = locationItemView;
        } else {
            TextView textView3 = timeView;
        }
        if (tL_authorization.ip.length() != 0) {
            ItemView locationItemView2 = new ItemView(context, false);
            locationItemView2.valueText.setText(tL_authorization.ip);
            Drawable drawable3 = ContextCompat.getDrawable(context, NUM).mutate();
            drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            locationItemView2.iconView.setImageDrawable(drawable3);
            locationItemView2.descriptionText.setText(LocaleController.getString("IpAddress", NUM));
            locationItemView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(tL_authorization.ip);
                }
            });
            locationItemView2.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(tL_authorization.country);
                    return true;
                }
            });
            locationItemView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            linearLayout.addView(locationItemView2);
            prevItem.needDivider = true;
            prevItem = locationItemView2;
        }
        if (secretChatsEnabled(tL_authorization)) {
            final ItemView acceptSecretChats = new ItemView(context, true);
            acceptSecretChats.valueText.setText(LocaleController.getString("AcceptSecretChats", NUM));
            Drawable drawable4 = ContextCompat.getDrawable(context, NUM).mutate();
            drawable4.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            acceptSecretChats.iconView.setImageDrawable(drawable4);
            acceptSecretChats.switchView.setChecked(!tL_authorization.encrypted_requests_disabled, false);
            acceptSecretChats.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7));
            acceptSecretChats.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    acceptSecretChats.switchView.setChecked(!acceptSecretChats.switchView.isChecked(), true);
                    tL_authorization.encrypted_requests_disabled = !acceptSecretChats.switchView.isChecked();
                    SessionBottomSheet.this.uploadSessionSettings();
                }
            });
            prevItem.needDivider = true;
            acceptSecretChats.descriptionText.setText(LocaleController.getString("AcceptSecretChatsDescription", NUM));
            linearLayout.addView(acceptSecretChats);
            prevItem = acceptSecretChats;
        }
        final ItemView acceptCalls = new ItemView(context, true);
        acceptCalls.valueText.setText(LocaleController.getString("AcceptCalls", NUM));
        Drawable drawable5 = ContextCompat.getDrawable(context, NUM).mutate();
        drawable5.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
        acceptCalls.iconView.setImageDrawable(drawable5);
        acceptCalls.switchView.setChecked(!tL_authorization.call_requests_disabled, false);
        acceptCalls.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7));
        acceptCalls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                acceptCalls.switchView.setChecked(!acceptCalls.switchView.isChecked(), true);
                tL_authorization.call_requests_disabled = !acceptCalls.switchView.isChecked();
                SessionBottomSheet.this.uploadSessionSettings();
            }
        });
        prevItem.needDivider = true;
        acceptCalls.descriptionText.setText(LocaleController.getString("AcceptCallsChatsDescription", NUM));
        linearLayout.addView(acceptCalls);
        if (!isCurrentSession) {
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            buttonTextView.setText(LocaleController.getString("TerminateSession", NUM));
            buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_attachAudioBackground"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            final Callback callback2 = callback;
            buttonTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) SessionBottomSheet.this.parentFragment.getParentActivity());
                    boolean[] zArr = new boolean[1];
                    builder.setMessage(LocaleController.getString("TerminateSessionText", NUM));
                    builder.setTitle(LocaleController.getString("AreYouSureSessionTitle", NUM));
                    builder.setPositiveButton(LocaleController.getString("Terminate", NUM), new SessionBottomSheet$8$$ExternalSyntheticLambda0(this, callback2, tL_authorization));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder.create();
                    baseFragment.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }

                /* renamed from: lambda$onClick$0$org-telegram-ui-SessionBottomSheet$8  reason: not valid java name */
                public /* synthetic */ void m3223lambda$onClick$0$orgtelegramuiSessionBottomSheet$8(Callback callback, TLRPC.TL_authorization session, DialogInterface dialogInterface, int option) {
                    callback.onSessionTerminated(session);
                    SessionBottomSheet.this.dismiss();
                }
            });
        } else {
            Callback callback3 = callback;
        }
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    private boolean secretChatsEnabled(TLRPC.TL_authorization session2) {
        if (session2.api_id == 2040 || session2.api_id == 2496) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void uploadSessionSettings() {
        TLRPC.TL_account_changeAuthorizationSettings req = new TLRPC.TL_account_changeAuthorizationSettings();
        req.encrypted_requests_disabled = this.session.encrypted_requests_disabled;
        req.call_requests_disabled = this.session.call_requests_disabled;
        req.flags = 3;
        req.hash = this.session.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, SessionBottomSheet$$ExternalSyntheticLambda1.INSTANCE);
    }

    static /* synthetic */ void lambda$uploadSessionSettings$0(TLObject response, TLRPC.TL_error error) {
    }

    /* access modifiers changed from: private */
    public void copyText(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new SessionBottomSheet$$ExternalSyntheticLambda0(this, text));
        builder.show();
    }

    /* renamed from: lambda$copyText$1$org-telegram-ui-SessionBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3222lambda$copyText$1$orgtelegramuiSessionBottomSheet(String text, DialogInterface dialogInterface, int i) {
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", text));
        BulletinFactory.of(getContainer(), (Theme.ResourcesProvider) null).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
    }

    private void setAnimation(TLRPC.TL_authorization session2, RLottieImageView imageView2) {
        String colorKey;
        int iconId;
        int iconId2;
        String platform = session2.platform.toLowerCase();
        if (platform.isEmpty()) {
            platform = session2.system_version.toLowerCase();
        }
        String deviceModel = session2.device_model.toLowerCase();
        boolean animation = true;
        if (deviceModel.contains("safari")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("edge")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("chrome")) {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        } else if (deviceModel.contains("opera") || deviceModel.contains("firefox") || deviceModel.contains("vivaldi")) {
            animation = false;
            if (deviceModel.contains("opera")) {
                iconId2 = NUM;
            } else if (deviceModel.contains("firefox") != 0) {
                iconId2 = NUM;
            } else {
                iconId2 = NUM;
            }
            colorKey = "avatar_backgroundPink";
        } else if (platform.contains("ubuntu")) {
            iconId = NUM;
            colorKey = "avatar_backgroundBlue";
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
            iconId = NUM;
            colorKey = "avatar_backgroundGreen";
        } else if (session2.app_name.toLowerCase().contains("desktop")) {
            iconId = NUM;
            colorKey = "avatar_backgroundCyan";
        } else {
            iconId = NUM;
            colorKey = "avatar_backgroundPink";
        }
        imageView2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(colorKey)));
        if (animation) {
            imageView2.setAnimation(iconId, 50, 50, new int[]{0, Theme.getColor(colorKey)});
        } else {
            imageView2.setImageDrawable(ContextCompat.getDrawable(getContext(), iconId));
        }
    }

    private static class ItemView extends FrameLayout {
        TextView descriptionText;
        ImageView iconView;
        boolean needDivider = false;
        Switch switchView;
        TextView valueText;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public ItemView(Context context, boolean needSwitch) {
            super(context);
            Context context2 = context;
            ImageView imageView = new ImageView(context2);
            this.iconView = imageView;
            addView(imageView, LayoutHelper.createFrame(28, 28.0f, 0, 16.0f, 8.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 64.0f, 4.0f, 0.0f, 4.0f));
            TextView textView = new TextView(context2);
            this.valueText = textView;
            textView.setTextSize(2, 16.0f);
            this.valueText.setGravity(3);
            this.valueText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            linearLayout.addView(this.valueText, LayoutHelper.createLinear(-1, -2, 0, 0, 0, needSwitch ? 46 : 0, 0));
            TextView textView2 = new TextView(context2);
            this.descriptionText = textView2;
            textView2.setTextSize(2, 13.0f);
            this.descriptionText.setGravity(3);
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-1, -2, 0, 0, 4, needSwitch ? 46 : 0, 0));
            setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            if (needSwitch) {
                Switch switchR = new Switch(context2);
                this.switchView = switchR;
                switchR.setDrawIconType(1);
                addView(this.switchView, LayoutHelper.createFrame(37, 40.0f, 21, 21.0f, 0.0f, 21.0f, 0.0f));
            }
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect((float) AndroidUtilities.dp(64.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dividerPaint);
            }
        }
    }

    public void show() {
        super.show();
        this.imageView.playAnimation();
    }
}
