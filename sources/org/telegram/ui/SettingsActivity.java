package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private int askQuestionRow;
    private int autoplayGifsRow;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater = new AvatarUpdater();
    private int backgroundRow;
    private int bioRow;
    private int clearLogsRow;
    private int contactsReimportRow;
    private int contactsSectionRow;
    private int contactsSortRow;
    private int customTabsRow;
    private int dataRow;
    private int directShareRow;
    private int dumpCallStatsRow;
    private int emojiRow;
    private int emptyRow;
    private int enableAnimationsRow;
    private int extraHeight;
    private View extraHeightView;
    private int forceTcpInCallsRow;
    private int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int messagesSectionRow;
    private int messagesSectionRow2;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private int overscrollRow;
    private int privacyPolicyRow;
    private int privacyRow;
    private PhotoViewerProvider provider = new C23461();
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int sendByEnterRow;
    private int sendLogsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private int stickersRow;
    private int supportSectionRow;
    private int supportSectionRow2;
    private int switchBackendButtonRow;
    private int telegramFaqRow;
    private int textSizeRow;
    private int themeRow;
    private int usernameRow;
    private int versionRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.SettingsActivity$8 */
    class C16938 implements OnClickListener {
        C16938() {
        }

        public void onClick(View v) {
            User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, SettingsActivity.this.provider);
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$9 */
    class C16949 extends ViewOutlineProvider {
        C16949() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.m3e(e);
                return false;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$2 */
    class C22812 implements AvatarUpdaterDelegate {

        /* renamed from: org.telegram.ui.SettingsActivity$2$1 */
        class C22801 implements RequestDelegate {

            /* renamed from: org.telegram.ui.SettingsActivity$2$1$1 */
            class C16831 implements Runnable {
                C16831() {
                }

                public void run() {
                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(true);
                }
            }

            C22801() {
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user != null) {
                            MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(user, false);
                        } else {
                            return;
                        }
                    }
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).setCurrentUser(user);
                    TL_photos_photo photo = (TL_photos_photo) response;
                    ArrayList<PhotoSize> sizes = photo.photo.sizes;
                    PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                    PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = photo.photo.id;
                    if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    if (bigSize != null) {
                        user.photo.photo_big = bigSize.location;
                    } else if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearUserPhotos(user.id);
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(users, null, false, true);
                    AndroidUtilities.runOnUIThread(new C16831());
                }
            }
        }

        C22812() {
        }

        public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
            TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).sendRequest(req, new C22801());
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$3 */
    class C22823 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.SettingsActivity$3$1 */
        class C16841 implements DialogInterface.OnClickListener {
            C16841() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(SettingsActivity.this.currentAccount).performLogout(true);
            }
        }

        C22823() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                SettingsActivity.this.finishFragment();
            } else if (id == 1) {
                SettingsActivity.this.presentFragment(new ChangeNameActivity());
            } else if (id == 2 && SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16841());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$6 */
    class C22846 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SettingsActivity$6$3 */
        class C16873 implements DialogInterface.OnClickListener {
            C16873() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.performAskAQuestion();
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity$6$4 */
        class C16884 implements DialogInterface.OnClickListener {
            C16884() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.pushAuthKey = null;
                SharedConfig.pushAuthKeyId = null;
                SharedConfig.saveConfig();
                ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).switchBackend();
            }
        }

        C22846() {
        }

        public void onItemClick(View view, final int position) {
            Builder builder;
            if (position == SettingsActivity.this.textSizeRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("TextSize", R.string.TextSize));
                    final NumberPicker numberPicker = new NumberPicker(SettingsActivity.this.getParentActivity());
                    numberPicker.setMinValue(12);
                    numberPicker.setMaxValue(30);
                    numberPicker.setValue(SharedConfig.fontSize);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Editor editor = MessagesController.getGlobalMainSettings().edit();
                            editor.putInt("fons_size", numberPicker.getValue());
                            SharedConfig.fontSize = numberPicker.getValue();
                            editor.commit();
                            if (SettingsActivity.this.listAdapter != null) {
                                SettingsActivity.this.listAdapter.notifyItemChanged(position);
                            }
                        }
                    });
                    SettingsActivity.this.showDialog(builder.create());
                }
            } else if (position == SettingsActivity.this.enableAnimationsRow) {
                preferences = MessagesController.getGlobalMainSettings();
                animations = preferences.getBoolean("view_animations", true);
                editor = preferences.edit();
                editor.putBoolean("view_animations", animations ^ 1);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(animations ^ 1);
                }
            } else if (position == SettingsActivity.this.notificationRow) {
                SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
            } else if (position == SettingsActivity.this.backgroundRow) {
                SettingsActivity.this.presentFragment(new WallpapersActivity());
            } else if (position == SettingsActivity.this.askQuestionRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    TextView message = new TextView(SettingsActivity.this.getParentActivity());
                    Spannable spanned = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo).replace("\n", "<br>")));
                    URLSpan[] spans = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
                    for (URLSpan span : spans) {
                        int start = spanned.getSpanStart(span);
                        int end = spanned.getSpanEnd(span);
                        spanned.removeSpan(span);
                        spanned.setSpan(new URLSpanNoUnderline(span.getURL()) {
                            public void onClick(View widget) {
                                SettingsActivity.this.dismissCurrentDialig();
                                super.onClick(widget);
                            }
                        }, start, end, 0);
                    }
                    message.setText(spanned);
                    message.setTextSize(1, 16.0f);
                    message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                    message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                    message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                    message.setMovementMethod(new LinkMovementMethodMy());
                    message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    Builder builder2 = new Builder(SettingsActivity.this.getParentActivity());
                    builder2.setView(message);
                    builder2.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));
                    builder2.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new C16873());
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder2.create());
                }
            } else if (position == SettingsActivity.this.sendLogsRow) {
                SettingsActivity.this.sendLogs();
            } else if (position == SettingsActivity.this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (position == SettingsActivity.this.sendByEnterRow) {
                preferences = MessagesController.getGlobalMainSettings();
                animations = preferences.getBoolean("send_by_enter", false);
                editor = preferences.edit();
                editor.putBoolean("send_by_enter", animations ^ 1);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(animations ^ 1);
                }
            } else if (position == SettingsActivity.this.raiseToSpeakRow) {
                SharedConfig.toogleRaiseToSpeak();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
                }
            } else if (position == SettingsActivity.this.autoplayGifsRow) {
                SharedConfig.toggleAutoplayGifs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                }
            } else if (position == SettingsActivity.this.saveToGalleryRow) {
                SharedConfig.toggleSaveToGallery();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
                }
            } else if (position == SettingsActivity.this.customTabsRow) {
                SharedConfig.toggleCustomTabs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
                }
            } else if (position == SettingsActivity.this.directShareRow) {
                SharedConfig.toggleDirectShare();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.directShare);
                }
            } else if (position == SettingsActivity.this.privacyRow) {
                SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
            } else if (position == SettingsActivity.this.dataRow) {
                SettingsActivity.this.presentFragment(new DataSettingsActivity());
            } else if (position == SettingsActivity.this.languageRow) {
                SettingsActivity.this.presentFragment(new LanguageSelectActivity());
            } else if (position == SettingsActivity.this.themeRow) {
                SettingsActivity.this.presentFragment(new ThemeActivity(0));
            } else if (position == SettingsActivity.this.switchBackendButtonRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16884());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                }
            } else if (position == SettingsActivity.this.telegramFaqRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
            } else if (position == SettingsActivity.this.privacyPolicyRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
            } else if (position != SettingsActivity.this.contactsReimportRow) {
                if (position == SettingsActivity.this.contactsSortRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Editor editor = MessagesController.getGlobalMainSettings().edit();
                                editor.putInt("sortContactsBy", which);
                                editor.commit();
                                if (SettingsActivity.this.listAdapter != null) {
                                    SettingsActivity.this.listAdapter.notifyItemChanged(position);
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == SettingsActivity.this.usernameRow) {
                    SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                } else if (position == SettingsActivity.this.bioRow) {
                    if (MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()) != null) {
                        SettingsActivity.this.presentFragment(new ChangeBioActivity());
                    }
                } else if (position == SettingsActivity.this.numberRow) {
                    SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                } else if (position == SettingsActivity.this.stickersRow) {
                    SettingsActivity.this.presentFragment(new StickersActivity(0));
                } else if (position == SettingsActivity.this.emojiRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        final boolean[] maskValues = new boolean[2];
                        BottomSheet.Builder builder3 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                        builder3.setApplyTopPadding(false);
                        builder3.setApplyBottomPadding(false);
                        LinearLayout linearLayout = new LinearLayout(SettingsActivity.this.getParentActivity());
                        linearLayout.setOrientation(1);
                        int a = 0;
                        while (true) {
                            if (a >= (VERSION.SDK_INT >= 19 ? 2 : true)) {
                                break;
                            }
                            String name = null;
                            if (a == 0) {
                                maskValues[a] = SharedConfig.allowBigEmoji;
                                name = LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize);
                            } else if (a == 1) {
                                maskValues[a] = SharedConfig.useSystemEmoji;
                                name = LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault);
                            }
                            CheckBoxCell checkBoxCell = new CheckBoxCell(SettingsActivity.this.getParentActivity(), 1);
                            checkBoxCell.setTag(Integer.valueOf(a));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(name, TtmlNode.ANONYMOUS_REGION_ID, maskValues[a], true);
                            checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            checkBoxCell.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    CheckBoxCell cell = (CheckBoxCell) v;
                                    int num = ((Integer) cell.getTag()).intValue();
                                    maskValues[num] = maskValues[num] ^ true;
                                    cell.setChecked(maskValues[num], true);
                                }
                            });
                            a++;
                        }
                        BottomSheetCell cell = new BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
                        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                        cell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                        cell.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                try {
                                    if (SettingsActivity.this.visibleDialog != null) {
                                        SettingsActivity.this.visibleDialog.dismiss();
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                Editor editor = MessagesController.getGlobalMainSettings().edit();
                                boolean z = maskValues[0];
                                SharedConfig.allowBigEmoji = z;
                                editor.putBoolean("allowBigEmoji", z);
                                z = maskValues[1];
                                SharedConfig.useSystemEmoji = z;
                                editor.putBoolean("useSystemEmoji", z);
                                editor.commit();
                                if (SettingsActivity.this.listAdapter != null) {
                                    SettingsActivity.this.listAdapter.notifyItemChanged(position);
                                }
                            }
                        });
                        linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                        builder3.setCustomView(linearLayout);
                        SettingsActivity.this.showDialog(builder3.create());
                    }
                } else if (position == SettingsActivity.this.dumpCallStatsRow) {
                    preferences = MessagesController.getGlobalMainSettings();
                    animations = preferences.getBoolean("dbg_dump_call_stats", false);
                    editor = preferences.edit();
                    editor.putBoolean("dbg_dump_call_stats", animations ^ 1);
                    editor.commit();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(animations ^ 1);
                    }
                } else if (position == SettingsActivity.this.forceTcpInCallsRow) {
                    preferences = MessagesController.getGlobalMainSettings();
                    animations = preferences.getBoolean("dbg_force_tcp_in_calls", false);
                    editor = preferences.edit();
                    editor.putBoolean("dbg_force_tcp_in_calls", animations ^ 1);
                    editor.commit();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(animations ^ 1);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$7 */
    class C22857 implements OnItemLongClickListener {
        private int pressCount = 0;

        /* renamed from: org.telegram.ui.SettingsActivity$7$1 */
        class C16921 implements DialogInterface.OnClickListener {
            C16921() {
            }

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
                } else if (which == 1) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
                } else if (which == 2) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
                } else if (which == 3) {
                    MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
                } else if (which == 4) {
                    BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                } else if (which == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (which == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                } else if (which == 7) {
                    SharedConfig.toggleRoundCamera16to9();
                }
            }
        }

        C22857() {
        }

        public boolean onItemClick(View view, int position) {
            if (position != SettingsActivity.this.versionRow) {
                return false;
            }
            String str;
            int i;
            this.pressCount++;
            if (this.pressCount < 2) {
                if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                    try {
                        Toast.makeText(SettingsActivity.this.getParentActivity(), "\u00af\\_(\u30c4)_/\u00af", 0).show();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    return true;
                }
            }
            Builder builder = new Builder(SettingsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
            CharSequence[] charSequenceArr = new CharSequence[7];
            charSequenceArr[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
            charSequenceArr[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
            charSequenceArr[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
            charSequenceArr[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
            if (BuildVars.LOGS_ENABLED) {
                str = "DebugMenuDisableLogs";
                i = R.string.DebugMenuDisableLogs;
            } else {
                str = "DebugMenuEnableLogs";
                i = R.string.DebugMenuEnableLogs;
            }
            charSequenceArr[4] = LocaleController.getString(str, i);
            if (SharedConfig.inappCamera) {
                str = "DebugMenuDisableCamera";
                i = R.string.DebugMenuDisableCamera;
            } else {
                str = "DebugMenuEnableCamera";
                i = R.string.DebugMenuEnableCamera;
            }
            charSequenceArr[5] = LocaleController.getString(str, i);
            charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
            builder.setItems(charSequenceArr, new C16921());
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            SettingsActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$1 */
    class C23461 extends EmptyPhotoViewerProvider {
        C23461() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            if (fileLocation == null) {
                return null;
            }
            User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                FileLocation photoBig = user.photo.photo_big;
                if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                    int[] coords = new int[2];
                    SettingsActivity.this.avatarImage.getLocationInWindow(coords);
                    PlaceProviderObject object = new PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = SettingsActivity.this.avatarImage;
                    object.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
                    object.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.size = -1;
                    object.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    object.scale = SettingsActivity.this.avatarImage.getScaleX();
                    return object;
                }
            }
            return null;
        }

        public void willHidePhotoViewer() {
            SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return SettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 6) {
                    switch (itemViewType) {
                        case 2:
                            TextSettingsCell textCell = holder.itemView;
                            if (position == SettingsActivity.this.textSizeRow) {
                                int size = MessagesController.getGlobalMainSettings().getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                                textCell.setTextAndValue(LocaleController.getString("TextSize", R.string.TextSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                                return;
                            } else if (position == SettingsActivity.this.languageRow) {
                                textCell.setTextAndValue(LocaleController.getString("Language", R.string.Language), LocaleController.getCurrentLanguageName(), true);
                                return;
                            } else if (position == SettingsActivity.this.themeRow) {
                                textCell.setTextAndValue(LocaleController.getString("Theme", R.string.Theme), Theme.getCurrentThemeName(), true);
                                return;
                            } else if (position == SettingsActivity.this.contactsSortRow) {
                                String value;
                                int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                                if (sort == 0) {
                                    value = LocaleController.getString("Default", R.string.Default);
                                } else if (sort == 1) {
                                    value = LocaleController.getString("FirstName", R.string.SortFirstName);
                                } else {
                                    value = LocaleController.getString("LastName", R.string.SortLastName);
                                    textCell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value, true);
                                    return;
                                }
                                textCell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value, true);
                                return;
                            } else if (position == SettingsActivity.this.notificationRow) {
                                textCell.setText(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), true);
                                return;
                            } else if (position == SettingsActivity.this.backgroundRow) {
                                textCell.setText(LocaleController.getString("ChatBackground", R.string.ChatBackground), true);
                                return;
                            } else if (position == SettingsActivity.this.sendLogsRow) {
                                textCell.setText(LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs), true);
                                return;
                            } else if (position == SettingsActivity.this.clearLogsRow) {
                                textCell.setText(LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs), true);
                                return;
                            } else if (position == SettingsActivity.this.askQuestionRow) {
                                textCell.setText(LocaleController.getString("AskAQuestion", R.string.AskAQuestion), true);
                                return;
                            } else if (position == SettingsActivity.this.privacyRow) {
                                textCell.setText(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), true);
                                return;
                            } else if (position == SettingsActivity.this.dataRow) {
                                textCell.setText(LocaleController.getString("DataSettings", R.string.DataSettings), true);
                                return;
                            } else if (position == SettingsActivity.this.switchBackendButtonRow) {
                                textCell.setText("Switch Backend", true);
                                return;
                            } else if (position == SettingsActivity.this.telegramFaqRow) {
                                textCell.setText(LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), true);
                                return;
                            } else if (position == SettingsActivity.this.contactsReimportRow) {
                                textCell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                                return;
                            } else if (position == SettingsActivity.this.stickersRow) {
                                textCell.setTextAndValue(LocaleController.getString("StickersName", R.string.StickersName), DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size() != 0 ? String.format("%d", new Object[]{Integer.valueOf(DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size())}) : TtmlNode.ANONYMOUS_REGION_ID, true);
                                return;
                            } else if (position == SettingsActivity.this.privacyPolicyRow) {
                                textCell.setText(LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), true);
                                return;
                            } else if (position == SettingsActivity.this.emojiRow) {
                                textCell.setText(LocaleController.getString("Emoji", R.string.Emoji), true);
                                return;
                            } else {
                                return;
                            }
                        case 3:
                            TextCheckCell textCell2 = holder.itemView;
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (position == SettingsActivity.this.enableAnimationsRow) {
                                textCell2.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), preferences.getBoolean("view_animations", true), false);
                                return;
                            } else if (position == SettingsActivity.this.sendByEnterRow) {
                                textCell2.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), preferences.getBoolean("send_by_enter", false), true);
                                return;
                            } else if (position == SettingsActivity.this.saveToGalleryRow) {
                                textCell2.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), SharedConfig.saveToGallery, false);
                                return;
                            } else if (position == SettingsActivity.this.autoplayGifsRow) {
                                textCell2.setTextAndCheck(LocaleController.getString("AutoplayGifs", R.string.AutoplayGifs), SharedConfig.autoplayGifs, true);
                                return;
                            } else if (position == SettingsActivity.this.raiseToSpeakRow) {
                                textCell2.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                                return;
                            } else if (position == SettingsActivity.this.customTabsRow) {
                                textCell2.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                                return;
                            } else if (position == SettingsActivity.this.directShareRow) {
                                textCell2.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                                return;
                            } else if (position == SettingsActivity.this.dumpCallStatsRow) {
                                textCell2.setTextAndCheck("Dump detailed call stats", preferences.getBoolean("dbg_dump_call_stats", false), true);
                                return;
                            } else if (position == SettingsActivity.this.forceTcpInCallsRow) {
                                textCell2.setTextAndValueAndCheck("Force TCP in calls", "This disables UDP", preferences.getBoolean("dbg_force_tcp_in_calls", false), false, true);
                                return;
                            } else {
                                return;
                            }
                        case 4:
                            if (position == SettingsActivity.this.settingsSectionRow2) {
                                ((HeaderCell) holder.itemView).setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                                return;
                            } else if (position == SettingsActivity.this.supportSectionRow2) {
                                ((HeaderCell) holder.itemView).setText(LocaleController.getString("Support", R.string.Support));
                                return;
                            } else if (position == SettingsActivity.this.messagesSectionRow2) {
                                ((HeaderCell) holder.itemView).setText(LocaleController.getString("MessagesSettings", R.string.MessagesSettings));
                                return;
                            } else if (position == SettingsActivity.this.numberSectionRow) {
                                ((HeaderCell) holder.itemView).setText(LocaleController.getString("Info", R.string.Info));
                                return;
                            } else {
                                return;
                            }
                        default:
                            return;
                    }
                }
                TextDetailSettingsCell textCell3 = holder.itemView;
                User user;
                String value2;
                if (position == SettingsActivity.this.numberRow) {
                    user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    if (user == null || user.phone == null || user.phone.length() == 0) {
                        value2 = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                    } else {
                        value2 = PhoneFormat.getInstance();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("+");
                        stringBuilder.append(user.phone);
                        value2 = value2.format(stringBuilder.toString());
                    }
                    textCell3.setTextAndValue(value2, LocaleController.getString("Phone", R.string.Phone), true);
                } else if (position == SettingsActivity.this.usernameRow) {
                    user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    if (user == null || TextUtils.isEmpty(user.username)) {
                        value2 = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                    } else {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("@");
                        stringBuilder2.append(user.username);
                        value2 = stringBuilder2.toString();
                    }
                    textCell3.setTextAndValue(value2, LocaleController.getString("Username", R.string.Username), true);
                } else if (position == SettingsActivity.this.bioRow) {
                    String value3;
                    TL_userFull userFull = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId());
                    if (userFull == null) {
                        value3 = LocaleController.getString("Loading", R.string.Loading);
                    } else if (TextUtils.isEmpty(userFull.about)) {
                        value3 = LocaleController.getString("UserBioEmpty", R.string.UserBioEmpty);
                        textCell3.setTextWithEmojiAndValue(value3, LocaleController.getString("UserBio", R.string.UserBio), false);
                    } else {
                        value3 = userFull.about;
                    }
                    textCell3.setTextWithEmojiAndValue(value3, LocaleController.getString("UserBio", R.string.UserBio), false);
                }
            } else if (position == SettingsActivity.this.overscrollRow) {
                ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
            } else {
                ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(16.0f));
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (!(position == SettingsActivity.this.textSizeRow || position == SettingsActivity.this.enableAnimationsRow || position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.backgroundRow || position == SettingsActivity.this.numberRow || position == SettingsActivity.this.askQuestionRow || position == SettingsActivity.this.sendLogsRow || position == SettingsActivity.this.sendByEnterRow || position == SettingsActivity.this.autoplayGifsRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.clearLogsRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow || position == SettingsActivity.this.switchBackendButtonRow || position == SettingsActivity.this.telegramFaqRow || position == SettingsActivity.this.contactsSortRow || position == SettingsActivity.this.contactsReimportRow || position == SettingsActivity.this.saveToGalleryRow || position == SettingsActivity.this.stickersRow || position == SettingsActivity.this.raiseToSpeakRow || position == SettingsActivity.this.privacyPolicyRow || position == SettingsActivity.this.customTabsRow || position == SettingsActivity.this.directShareRow || position == SettingsActivity.this.versionRow || position == SettingsActivity.this.emojiRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.themeRow || position == SettingsActivity.this.dumpCallStatsRow)) {
                if (position != SettingsActivity.this.forceTcpInCallsRow) {
                    return false;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListAdapter listAdapter = this;
            View view = null;
            switch (viewType) {
                case 0:
                    view = new EmptyCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ShadowSectionCell(listAdapter.mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new TextInfoCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int code = pInfo.versionCode / 10;
                        String abi = TtmlNode.ANONYMOUS_REGION_ID;
                        switch (pInfo.versionCode % 10) {
                            case 0:
                            case 9:
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("universal ");
                                stringBuilder.append(Build.CPU_ABI);
                                stringBuilder.append(" ");
                                stringBuilder.append(Build.CPU_ABI2);
                                abi = stringBuilder.toString();
                                break;
                            case 1:
                            case 3:
                                abi = "arm-v7a";
                                break;
                            case 2:
                            case 4:
                                abi = "x86";
                                break;
                            case 5:
                            case 7:
                                abi = "arm64-v8a";
                                break;
                            case 6:
                            case 8:
                                abi = "x86_64";
                                break;
                            default:
                                break;
                        }
                        TextInfoCell textInfoCell = (TextInfoCell) view;
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi});
                        textInfoCell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, objArr));
                        break;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        break;
                    }
                case 6:
                    view = new TextDetailSettingsCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position != SettingsActivity.this.emptyRow) {
                if (position != SettingsActivity.this.overscrollRow) {
                    if (!(position == SettingsActivity.this.settingsSectionRow || position == SettingsActivity.this.supportSectionRow || position == SettingsActivity.this.messagesSectionRow)) {
                        if (position != SettingsActivity.this.contactsSectionRow) {
                            if (!(position == SettingsActivity.this.enableAnimationsRow || position == SettingsActivity.this.sendByEnterRow || position == SettingsActivity.this.saveToGalleryRow || position == SettingsActivity.this.autoplayGifsRow || position == SettingsActivity.this.raiseToSpeakRow || position == SettingsActivity.this.customTabsRow || position == SettingsActivity.this.directShareRow || position == SettingsActivity.this.dumpCallStatsRow)) {
                                if (position != SettingsActivity.this.forceTcpInCallsRow) {
                                    if (!(position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.themeRow || position == SettingsActivity.this.backgroundRow || position == SettingsActivity.this.askQuestionRow || position == SettingsActivity.this.sendLogsRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.clearLogsRow || position == SettingsActivity.this.switchBackendButtonRow || position == SettingsActivity.this.telegramFaqRow || position == SettingsActivity.this.contactsReimportRow || position == SettingsActivity.this.textSizeRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.contactsSortRow || position == SettingsActivity.this.stickersRow || position == SettingsActivity.this.privacyPolicyRow || position == SettingsActivity.this.emojiRow)) {
                                        if (position != SettingsActivity.this.dataRow) {
                                            if (position == SettingsActivity.this.versionRow) {
                                                return 5;
                                            }
                                            if (!(position == SettingsActivity.this.numberRow || position == SettingsActivity.this.usernameRow)) {
                                                if (position != SettingsActivity.this.bioRow) {
                                                    if (!(position == SettingsActivity.this.settingsSectionRow2 || position == SettingsActivity.this.messagesSectionRow2 || position == SettingsActivity.this.supportSectionRow2)) {
                                                        if (position != SettingsActivity.this.numberSectionRow) {
                                                            return 2;
                                                        }
                                                    }
                                                    return 4;
                                                }
                                            }
                                            return 6;
                                        }
                                    }
                                    return 2;
                                }
                            }
                            return 3;
                        }
                    }
                    return 1;
                }
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = new C22812();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emptyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.numberSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.numberRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usernameRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.bioRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dataRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.backgroundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableAnimationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.customTabsRow = i;
        if (VERSION.SDK_INT >= 23) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.directShareRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.stickersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.raiseToSpeakRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendByEnterRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.saveToGalleryRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.supportSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.supportSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.askQuestionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.telegramFaqRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacyPolicyRow = i;
        if (BuildVars.LOGS_ENABLED) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.clearLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.dumpCallStatsRow = i;
        } else {
            this.sendLogsRow = -1;
            this.clearLogsRow = -1;
            this.dumpCallStatsRow = -1;
        }
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.forceTcpInCallsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.switchBackendButtonRow = i;
        } else {
            this.switchBackendButtonRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionRow = i;
        DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
        MessagesController.getInstance(this.currentAccount).loadFullUser(UserConfig.getInstance(this.currentAccount).getCurrentUser(), this.classGuid, true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarImage != null) {
            this.avatarImage.setImageDrawable(null);
        }
        MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(UserConfig.getInstance(this.currentAccount).getClientUserId());
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        this.avatarUpdater.clear();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            r0.actionBar.setOccupyStatusBar(false);
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22823());
        ActionBarMenuItem item = r0.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other);
        item.addSubItem(1, LocaleController.getString("EditName", R.string.EditName));
        item.addSubItem(2, LocaleController.getString("LogOut", R.string.LogOut));
        r0.listAdapter = new ListAdapter(context2);
        r0.fragmentView = new FrameLayout(context2) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != SettingsActivity.this.listView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (SettingsActivity.this.parentLayout != null) {
                    int actionBarHeight = 0;
                    int childCount = getChildCount();
                    for (int a = 0; a < childCount; a++) {
                        View view = getChildAt(a);
                        if (view != child) {
                            if ((view instanceof ActionBar) && view.getVisibility() == 0) {
                                if (((ActionBar) view).getCastShadows()) {
                                    actionBarHeight = view.getMeasuredHeight();
                                }
                                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                            }
                        }
                    }
                    SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                }
                return result;
            }
        };
        r0.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = r0.fragmentView;
        r0.listView = new RecyclerListView(context2);
        r0.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager c23475 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager = c23475;
        recyclerListView.setLayoutManager(c23475);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setItemAnimator(null);
        r0.listView.setLayoutAnimation(null);
        r0.listView.setOnItemClickListener(new C22846());
        r0.listView.setOnItemLongClickListener(new C22857());
        frameLayout.addView(r0.actionBar);
        r0.extraHeightView = new View(context2);
        r0.extraHeightView.setPivotY(0.0f);
        r0.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(r0.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        r0.shadowView = new View(context2);
        r0.shadowView.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(r0.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        r0.avatarImage = new BackupImageView(context2);
        r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        r0.avatarImage.setPivotX(0.0f);
        r0.avatarImage.setPivotY(0.0f);
        frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        r0.avatarImage.setOnClickListener(new C16938());
        r0.nameTextView = new TextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_profile_title));
        r0.nameTextView.setTextSize(1, 18.0f);
        r0.nameTextView.setLines(1);
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setGravity(3);
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setPivotX(0.0f);
        r0.nameTextView.setPivotY(0.0f);
        frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        r0.onlineTextView = new TextView(context2);
        r0.onlineTextView.setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
        r0.onlineTextView.setTextSize(1, 14.0f);
        r0.onlineTextView.setLines(1);
        r0.onlineTextView.setMaxLines(1);
        r0.onlineTextView.setSingleLine(true);
        r0.onlineTextView.setEllipsize(TruncateAt.END);
        r0.onlineTextView.setGravity(3);
        frameLayout.addView(r0.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        r0.writeButton = new ImageView(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        r0.writeButton.setBackgroundDrawable(drawable);
        r0.writeButton.setImageResource(R.drawable.floating_camera);
        r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        r0.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.writeButton.setStateListAnimator(animator);
            r0.writeButton.setOutlineProvider(new C16949());
        }
        frameLayout.addView(r0.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        r0.writeButton.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.SettingsActivity$10$1 */
            class C16801 implements DialogInterface.OnClickListener {
                C16801() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        SettingsActivity.this.avatarUpdater.openCamera();
                    } else if (i == 1) {
                        SettingsActivity.this.avatarUpdater.openGallery();
                    } else if (i == 2) {
                        MessagesController.getInstance(SettingsActivity.this.currentAccount).deleteUserPhoto(null);
                    }
                }
            }

            public void onClick(View v) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    }
                    if (user != null) {
                        CharSequence[] items;
                        boolean fullMenu = false;
                        if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) {
                            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)};
                        } else {
                            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)};
                            fullMenu = true;
                        }
                        boolean full = fullMenu;
                        builder.setItems(items, new C16801());
                        SettingsActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        needLayout();
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    int height = 0;
                    int i = 0;
                    View child = recyclerView.getChildAt(0);
                    if (child != null) {
                        if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            int height2 = AndroidUtilities.dp(88.0f);
                            if (child.getTop() < 0) {
                                i = child.getTop();
                            }
                            height = height2 + i;
                        }
                        if (SettingsActivity.this.extraHeight != height) {
                            SettingsActivity.this.extraHeight = height;
                            SettingsActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        return r0.fragmentView;
    }

    private void performAskAQuestion() {
        final SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        int uid = preferences.getInt("support_id", 0);
        User supportUser = null;
        if (uid != 0) {
            supportUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            if (supportUser == null) {
                String userString = preferences.getString("support_user", null);
                if (userString != null) {
                    try {
                        byte[] datacentersBytes = Base64.decode(userString, 0);
                        if (datacentersBytes != null) {
                            SerializedData data = new SerializedData(datacentersBytes);
                            supportUser = User.TLdeserialize(data, data.readInt32(false), false);
                            if (supportUser != null && supportUser.id == 333000) {
                                supportUser = null;
                            }
                            data.cleanup();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        supportUser = null;
                    }
                }
            }
        }
        if (supportUser == null) {
            final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 1);
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getSupport(), new RequestDelegate() {

                /* renamed from: org.telegram.ui.SettingsActivity$12$2 */
                class C16822 implements Runnable {
                    C16822() {
                    }

                    public void run() {
                        try {
                            progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        final TL_help_support res = (TL_help_support) response;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                Editor editor = preferences.edit();
                                editor.putInt("support_id", res.user.id);
                                SerializedData data = new SerializedData();
                                res.user.serializeToStream(data);
                                editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
                                editor.commit();
                                data.cleanup();
                                try {
                                    progressDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                ArrayList<User> users = new ArrayList();
                                users.add(res.user);
                                MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(users, null, true, true);
                                MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(res.user, false);
                                Bundle args = new Bundle();
                                args.putInt("user_id", res.user.id);
                                SettingsActivity.this.presentFragment(new ChatActivity(args));
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C16822());
                }
            });
            return;
        }
        MessagesController.getInstance(this.currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        presentFragment(new ChatActivity(args));
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0)) {
                updateUserData();
            }
        } else if (id == NotificationCenter.featuredStickersDidLoaded) {
            if (this.listAdapter != null) {
                this.listAdapter.notifyItemChanged(this.stickersRow);
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            if (args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != null) {
                this.listAdapter.notifyItemChanged(this.bioRow);
            }
        } else if (id == NotificationCenter.emojiDidLoaded && this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void needLayout() {
        int i = 0;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
            final boolean setVisible = diff > 0.2f;
            if (setVisible != (this.writeButton.getTag() == null)) {
                if (setVisible) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                if (this.writeButtonAnimation != null) {
                    AnimatorSet old = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    old.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                Animator[] animatorArr;
                if (setVisible) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    AnimatorSet animatorSet2 = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animation)) {
                            SettingsActivity.this.writeButton.setVisibility(setVisible ? 0 : 8);
                            SettingsActivity.this.writeButtonAnimation = null;
                        }
                    }
                });
                this.writeButtonAnimation.start();
            }
            this.avatarImage.setScaleX(((18.0f * diff) + 42.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * diff) + 42.0f) / 42.0f);
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float avatarY = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationX((AndroidUtilities.density * -21.0f) * diff);
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.onlineTextView.setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
            this.nameTextView.setScaleX((0.12f * diff) + 1.0f);
            this.nameTextView.setScaleY(1.0f + (0.12f * diff));
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView != null) {
                        SettingsActivity.this.needLayout();
                        SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void updateUserData() {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        TLObject photo = null;
        FileLocation photoBig = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
            photoBig = user.photo.photo_big;
        }
        this.avatarDrawable = new AvatarDrawable(user, true);
        this.avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        if (this.avatarImage != null) {
            this.avatarImage.setImage(photo, "50_50", this.avatarDrawable);
            this.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(photoBig) ^ true, false);
            this.nameTextView.setText(UserObject.getUserName(user));
            this.onlineTextView.setText(LocaleController.getString("Online", R.string.Online));
            this.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig), false);
        }
    }

    private void sendLogs() {
        try {
            ArrayList<Uri> uris = new ArrayList();
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sdCard.getAbsolutePath());
            stringBuilder.append("/logs");
            for (File file : new File(stringBuilder.toString()).listFiles()) {
                if (VERSION.SDK_INT >= 24) {
                    uris.add(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file));
                } else {
                    uris.add(Uri.fromFile(file));
                }
            }
            if (!uris.isEmpty()) {
                Intent i = new Intent("android.intent.action.SEND_MULTIPLE");
                if (VERSION.SDK_INT >= 24) {
                    i.addFlags(1);
                }
                i.setType("message/rfc822");
                i.putExtra("android.intent.extra.EMAIL", TtmlNode.ANONYMOUS_REGION_ID);
                i.putExtra("android.intent.extra.SUBJECT", "last logs");
                i.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
                getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[32];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextInfoCell.class, TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r1[4] = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        r1[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_profile_title);
        r1[9] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileBlue);
        r1[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        r1[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r1[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r1[23] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[24] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[25] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[26] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText5);
        r1[27] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r1[28] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        r1[29] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r1[30] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r1[31] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        return r1;
    }
}
