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
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
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
    private PhotoViewerProvider provider = new C23521();
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
    class C16998 implements OnClickListener {
        C16998() {
        }

        public void onClick(View view) {
            view = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            if (view != null && view.photo != null && view.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
                PhotoViewer.getInstance().openPhoto(view.photo.photo_big, SettingsActivity.this.provider);
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$9 */
    class C17009 extends ViewOutlineProvider {
        C17009() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$2 */
    class C22872 implements AvatarUpdaterDelegate {

        /* renamed from: org.telegram.ui.SettingsActivity$2$1 */
        class C22861 implements RequestDelegate {

            /* renamed from: org.telegram.ui.SettingsActivity$2$1$1 */
            class C16891 implements Runnable {
                C16891() {
                }

                public void run() {
                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(true);
                }
            }

            C22861() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    tL_error = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                    if (tL_error == null) {
                        tL_error = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (tL_error != null) {
                            MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(tL_error, false);
                        } else {
                            return;
                        }
                    }
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).setCurrentUser(tL_error);
                    TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
                    ArrayList arrayList = tL_photos_photo.photo.sizes;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
                    tL_error.photo = new TL_userProfilePhoto();
                    tL_error.photo.photo_id = tL_photos_photo.photo.id;
                    if (closestPhotoSizeWithSize != null) {
                        tL_error.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    if (closestPhotoSizeWithSize2 != null) {
                        tL_error.photo.photo_big = closestPhotoSizeWithSize2.location;
                    } else if (closestPhotoSizeWithSize != null) {
                        tL_error.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearUserPhotos(tL_error.id);
                    tLObject = new ArrayList();
                    tLObject.add(tL_error);
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(tLObject, null, false, true);
                    AndroidUtilities.runOnUIThread(new C16891());
                }
            }
        }

        C22872() {
        }

        public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            photoSize = new TL_photos_uploadProfilePhoto();
            photoSize.file = inputFile;
            ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).sendRequest(photoSize, new C22861());
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$3 */
    class C22883 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.SettingsActivity$3$1 */
        class C16901 implements DialogInterface.OnClickListener {
            C16901() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(SettingsActivity.this.currentAccount).performLogout(1);
            }
        }

        C22883() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SettingsActivity.this.finishFragment();
            } else if (i == 1) {
                SettingsActivity.this.presentFragment(new ChangeNameActivity());
            } else if (i == 2 && SettingsActivity.this.getParentActivity() != 0) {
                i = new Builder(SettingsActivity.this.getParentActivity());
                i.setMessage(LocaleController.getString("AreYouSureLogout", C0446R.string.AreYouSureLogout));
                i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                i.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16901());
                i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                SettingsActivity.this.showDialog(i.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$6 */
    class C22906 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SettingsActivity$6$3 */
        class C16933 implements DialogInterface.OnClickListener {
            C16933() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.performAskAQuestion();
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity$6$4 */
        class C16944 implements DialogInterface.OnClickListener {
            C16944() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.pushAuthKey = null;
                SharedConfig.pushAuthKeyId = null;
                SharedConfig.saveConfig();
                ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).switchBackend();
            }
        }

        C22906() {
        }

        public void onItemClick(View view, final int i) {
            if (i == SettingsActivity.this.textSizeRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    view = new Builder(SettingsActivity.this.getParentActivity());
                    view.setTitle(LocaleController.getString("TextSize", C0446R.string.TextSize));
                    final View numberPicker = new NumberPicker(SettingsActivity.this.getParentActivity());
                    numberPicker.setMinValue(12);
                    numberPicker.setMaxValue(30);
                    numberPicker.setValue(SharedConfig.fontSize);
                    view.setView(numberPicker);
                    view.setNegativeButton(LocaleController.getString("Done", C0446R.string.Done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface = MessagesController.getGlobalMainSettings().edit();
                            dialogInterface.putInt("fons_size", numberPicker.getValue());
                            SharedConfig.fontSize = numberPicker.getValue();
                            dialogInterface.commit();
                            if (SettingsActivity.this.listAdapter != null) {
                                SettingsActivity.this.listAdapter.notifyItemChanged(i);
                            }
                        }
                    });
                    SettingsActivity.this.showDialog(view.create());
                }
            } else if (i == SettingsActivity.this.enableAnimationsRow) {
                i = MessagesController.getGlobalMainSettings();
                r0 = i.getBoolean("view_animations", true);
                i = i.edit();
                i.putBoolean("view_animations", r0 ^ 1);
                i.commit();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(r0 ^ 1);
                }
            } else if (i == SettingsActivity.this.notificationRow) {
                SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
            } else if (i == SettingsActivity.this.backgroundRow) {
                SettingsActivity.this.presentFragment(new WallpapersActivity());
            } else if (i == SettingsActivity.this.askQuestionRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    view = new TextView(SettingsActivity.this.getParentActivity());
                    i = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", C0446R.string.AskAQuestionInfo).replace("\n", "<br>")));
                    URLSpan[] uRLSpanArr = (URLSpan[]) i.getSpans(0, i.length(), URLSpan.class);
                    for (URLSpan uRLSpan : uRLSpanArr) {
                        int spanStart = i.getSpanStart(uRLSpan);
                        int spanEnd = i.getSpanEnd(uRLSpan);
                        i.removeSpan(uRLSpan);
                        i.setSpan(new URLSpanNoUnderline(uRLSpan.getURL()) {
                            public void onClick(View view) {
                                SettingsActivity.this.dismissCurrentDialig();
                                super.onClick(view);
                            }
                        }, spanStart, spanEnd, 0);
                    }
                    view.setText(i);
                    view.setTextSize(1, NUM);
                    view.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                    view.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                    view.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                    view.setMovementMethod(new LinkMovementMethodMy());
                    view.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    i = new Builder(SettingsActivity.this.getParentActivity());
                    i.setView(view);
                    i.setTitle(LocaleController.getString("AskAQuestion", C0446R.string.AskAQuestion));
                    i.setPositiveButton(LocaleController.getString("AskButton", C0446R.string.AskButton), new C16933());
                    i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    SettingsActivity.this.showDialog(i.create());
                }
            } else if (i == SettingsActivity.this.sendLogsRow) {
                SettingsActivity.this.sendLogs();
            } else if (i == SettingsActivity.this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (i == SettingsActivity.this.sendByEnterRow) {
                i = MessagesController.getGlobalMainSettings();
                r0 = i.getBoolean("send_by_enter", false);
                i = i.edit();
                i.putBoolean("send_by_enter", r0 ^ 1);
                i.commit();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(r0 ^ 1);
                }
            } else if (i == SettingsActivity.this.raiseToSpeakRow) {
                SharedConfig.toogleRaiseToSpeak();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
                }
            } else if (i == SettingsActivity.this.autoplayGifsRow) {
                SharedConfig.toggleAutoplayGifs();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                }
            } else if (i == SettingsActivity.this.saveToGalleryRow) {
                SharedConfig.toggleSaveToGallery();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
                }
            } else if (i == SettingsActivity.this.customTabsRow) {
                SharedConfig.toggleCustomTabs();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
                }
            } else if (i == SettingsActivity.this.directShareRow) {
                SharedConfig.toggleDirectShare();
                if ((view instanceof TextCheckCell) != 0) {
                    ((TextCheckCell) view).setChecked(SharedConfig.directShare);
                }
            } else if (i == SettingsActivity.this.privacyRow) {
                SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
            } else if (i == SettingsActivity.this.dataRow) {
                SettingsActivity.this.presentFragment(new DataSettingsActivity());
            } else if (i == SettingsActivity.this.languageRow) {
                SettingsActivity.this.presentFragment(new LanguageSelectActivity());
            } else if (i == SettingsActivity.this.themeRow) {
                SettingsActivity.this.presentFragment(new ThemeActivity(0));
            } else if (i == SettingsActivity.this.switchBackendButtonRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    view = new Builder(SettingsActivity.this.getParentActivity());
                    view.setMessage(LocaleController.getString("AreYouSure", C0446R.string.AreYouSure));
                    view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16944());
                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    SettingsActivity.this.showDialog(view.create());
                }
            } else if (i == SettingsActivity.this.telegramFaqRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", C0446R.string.TelegramFaqUrl));
            } else if (i == SettingsActivity.this.privacyPolicyRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", C0446R.string.PrivacyPolicyUrl));
            } else if (i != SettingsActivity.this.contactsReimportRow) {
                if (i == SettingsActivity.this.contactsSortRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        view = new Builder(SettingsActivity.this.getParentActivity());
                        view.setTitle(LocaleController.getString("SortBy", C0446R.string.SortBy));
                        view.setItems(new CharSequence[]{LocaleController.getString("Default", C0446R.string.Default), LocaleController.getString("SortFirstName", C0446R.string.SortFirstName), LocaleController.getString("SortLastName", C0446R.string.SortLastName)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface = MessagesController.getGlobalMainSettings().edit();
                                dialogInterface.putInt("sortContactsBy", i);
                                dialogInterface.commit();
                                if (SettingsActivity.this.listAdapter != null) {
                                    SettingsActivity.this.listAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                        SettingsActivity.this.showDialog(view.create());
                    }
                } else if (i == SettingsActivity.this.usernameRow) {
                    SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                } else if (i == SettingsActivity.this.bioRow) {
                    if (MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()) != null) {
                        SettingsActivity.this.presentFragment(new ChangeBioActivity());
                    }
                } else if (i == SettingsActivity.this.numberRow) {
                    SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                } else if (i == SettingsActivity.this.stickersRow) {
                    SettingsActivity.this.presentFragment(new StickersActivity(0));
                } else if (i == SettingsActivity.this.emojiRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        view = new boolean[2];
                        BottomSheet.Builder builder = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                        builder.setApplyTopPadding(false);
                        builder.setApplyBottomPadding(false);
                        View linearLayout = new LinearLayout(SettingsActivity.this.getParentActivity());
                        linearLayout.setOrientation(1);
                        int i2 = 0;
                        while (true) {
                            if (i2 >= (VERSION.SDK_INT >= 19 ? 2 : true)) {
                                break;
                            }
                            String string;
                            if (i2 == 0) {
                                view[i2] = SharedConfig.allowBigEmoji;
                                string = LocaleController.getString("EmojiBigSize", C0446R.string.EmojiBigSize);
                            } else if (i2 == 1) {
                                view[i2] = SharedConfig.useSystemEmoji;
                                string = LocaleController.getString("EmojiUseDefault", C0446R.string.EmojiUseDefault);
                            } else {
                                string = null;
                            }
                            View checkBoxCell = new CheckBoxCell(SettingsActivity.this.getParentActivity(), 1);
                            checkBoxCell.setTag(Integer.valueOf(i2));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(string, TtmlNode.ANONYMOUS_REGION_ID, view[i2], true);
                            checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            checkBoxCell.setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                    int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                                    view[intValue] = view[intValue] ^ true;
                                    checkBoxCell.setChecked(view[intValue], true);
                                }
                            });
                            i2++;
                        }
                        View bottomSheetCell = new BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
                        bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0446R.string.Save).toUpperCase(), 0);
                        bottomSheetCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                        bottomSheetCell.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                try {
                                    if (SettingsActivity.this.visibleDialog != null) {
                                        SettingsActivity.this.visibleDialog.dismiss();
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                view = MessagesController.getGlobalMainSettings().edit();
                                boolean z = view[0];
                                SharedConfig.allowBigEmoji = z;
                                view.putBoolean("allowBigEmoji", z);
                                z = view[1];
                                SharedConfig.useSystemEmoji = z;
                                view.putBoolean("useSystemEmoji", z);
                                view.commit();
                                if (SettingsActivity.this.listAdapter != null) {
                                    SettingsActivity.this.listAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                        builder.setCustomView(linearLayout);
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == SettingsActivity.this.dumpCallStatsRow) {
                    i = MessagesController.getGlobalMainSettings();
                    r0 = i.getBoolean("dbg_dump_call_stats", false);
                    i = i.edit();
                    i.putBoolean("dbg_dump_call_stats", r0 ^ 1);
                    i.commit();
                    if ((view instanceof TextCheckCell) != 0) {
                        ((TextCheckCell) view).setChecked(r0 ^ 1);
                    }
                } else if (i == SettingsActivity.this.forceTcpInCallsRow) {
                    i = MessagesController.getGlobalMainSettings();
                    r0 = i.getBoolean("dbg_force_tcp_in_calls", false);
                    i = i.edit();
                    i.putBoolean("dbg_force_tcp_in_calls", r0 ^ 1);
                    i.commit();
                    if ((view instanceof TextCheckCell) != 0) {
                        ((TextCheckCell) view).setChecked(r0 ^ 1);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$7 */
    class C22917 implements OnItemLongClickListener {
        private int pressCount = null;

        /* renamed from: org.telegram.ui.SettingsActivity$7$1 */
        class C16981 implements DialogInterface.OnClickListener {
            C16981() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
                } else if (i == 1) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
                } else if (i == 2) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
                } else if (i == 3) {
                    MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
                } else if (i == 4) {
                    BuildVars.LOGS_ENABLED = 1 ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                } else if (i == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (i == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                } else if (i == 7) {
                    SharedConfig.toggleRoundCamera16to9();
                }
            }
        }

        C22917() {
        }

        public boolean onItemClick(View view, int i) {
            if (i != SettingsActivity.this.versionRow) {
                return false;
            }
            String str;
            int i2;
            this.pressCount += 1;
            if (this.pressCount < 2) {
                if (BuildVars.DEBUG_PRIVATE_VERSION == null) {
                    try {
                        Toast.makeText(SettingsActivity.this.getParentActivity(), "\u00af\\_(\u30c4)_/\u00af", 0).show();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    return true;
                }
            }
            view = new Builder(SettingsActivity.this.getParentActivity());
            view.setTitle(LocaleController.getString("DebugMenu", C0446R.string.DebugMenu));
            CharSequence[] charSequenceArr = new CharSequence[7];
            charSequenceArr[0] = LocaleController.getString("DebugMenuImportContacts", C0446R.string.DebugMenuImportContacts);
            charSequenceArr[1] = LocaleController.getString("DebugMenuReloadContacts", C0446R.string.DebugMenuReloadContacts);
            charSequenceArr[2] = LocaleController.getString("DebugMenuResetContacts", C0446R.string.DebugMenuResetContacts);
            charSequenceArr[3] = LocaleController.getString("DebugMenuResetDialogs", C0446R.string.DebugMenuResetDialogs);
            if (BuildVars.LOGS_ENABLED) {
                str = "DebugMenuDisableLogs";
                i2 = C0446R.string.DebugMenuDisableLogs;
            } else {
                str = "DebugMenuEnableLogs";
                i2 = C0446R.string.DebugMenuEnableLogs;
            }
            charSequenceArr[4] = LocaleController.getString(str, i2);
            if (SharedConfig.inappCamera) {
                str = "DebugMenuDisableCamera";
                i2 = C0446R.string.DebugMenuDisableCamera;
            } else {
                str = "DebugMenuEnableCamera";
                i2 = C0446R.string.DebugMenuEnableCamera;
            }
            charSequenceArr[5] = LocaleController.getString(str, i2);
            charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", C0446R.string.DebugMenuClearMediaCache);
            view.setItems(charSequenceArr, new C16981());
            view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            SettingsActivity.this.showDialog(view.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$1 */
    class C23521 extends EmptyPhotoViewerProvider {
        C23521() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            if (fileLocation == null) {
                return null;
            }
            i = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            if (!(i == 0 || i.photo == null || i.photo.photo_big == null)) {
                i = i.photo.photo_big;
                if (i.local_id == fileLocation.local_id && i.volume_id == fileLocation.volume_id && i.dc_id == fileLocation.dc_id) {
                    messageObject = new int[2];
                    SettingsActivity.this.avatarImage.getLocationInWindow(messageObject);
                    fileLocation = new PlaceProviderObject();
                    i = 0;
                    fileLocation.viewX = messageObject[0];
                    messageObject = messageObject[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    fileLocation.viewY = messageObject - i;
                    fileLocation.parentView = SettingsActivity.this.avatarImage;
                    fileLocation.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
                    fileLocation.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
                    fileLocation.thumb = fileLocation.imageReceiver.getBitmapSafe();
                    fileLocation.size = -1;
                    fileLocation.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    fileLocation.scale = SettingsActivity.this.avatarImage.getScaleX();
                    return fileLocation;
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

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 6) {
                    switch (itemViewType) {
                        case 2:
                            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                            if (i == SettingsActivity.this.textSizeRow) {
                                i = MessagesController.getGlobalMainSettings().getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                                textSettingsCell.setTextAndValue(LocaleController.getString("TextSize", C0446R.string.TextSize), String.format("%d", new Object[]{Integer.valueOf(i)}), true);
                                return;
                            } else if (i == SettingsActivity.this.languageRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Language", C0446R.string.Language), LocaleController.getCurrentLanguageName(), true);
                                return;
                            } else if (i == SettingsActivity.this.themeRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("Theme", C0446R.string.Theme), Theme.getCurrentThemeName(), true);
                                return;
                            } else if (i == SettingsActivity.this.contactsSortRow) {
                                i = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                                if (i == 0) {
                                    i = LocaleController.getString("Default", C0446R.string.Default);
                                } else if (i == 1) {
                                    i = LocaleController.getString("FirstName", C0446R.string.SortFirstName);
                                } else {
                                    i = LocaleController.getString("LastName", C0446R.string.SortLastName);
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", C0446R.string.SortBy), i, true);
                                return;
                            } else if (i == SettingsActivity.this.notificationRow) {
                                textSettingsCell.setText(LocaleController.getString("NotificationsAndSounds", C0446R.string.NotificationsAndSounds), true);
                                return;
                            } else if (i == SettingsActivity.this.backgroundRow) {
                                textSettingsCell.setText(LocaleController.getString("ChatBackground", C0446R.string.ChatBackground), true);
                                return;
                            } else if (i == SettingsActivity.this.sendLogsRow) {
                                textSettingsCell.setText(LocaleController.getString("DebugSendLogs", C0446R.string.DebugSendLogs), true);
                                return;
                            } else if (i == SettingsActivity.this.clearLogsRow) {
                                textSettingsCell.setText(LocaleController.getString("DebugClearLogs", C0446R.string.DebugClearLogs), true);
                                return;
                            } else if (i == SettingsActivity.this.askQuestionRow) {
                                textSettingsCell.setText(LocaleController.getString("AskAQuestion", C0446R.string.AskAQuestion), true);
                                return;
                            } else if (i == SettingsActivity.this.privacyRow) {
                                textSettingsCell.setText(LocaleController.getString("PrivacySettings", C0446R.string.PrivacySettings), true);
                                return;
                            } else if (i == SettingsActivity.this.dataRow) {
                                textSettingsCell.setText(LocaleController.getString("DataSettings", C0446R.string.DataSettings), true);
                                return;
                            } else if (i == SettingsActivity.this.switchBackendButtonRow) {
                                textSettingsCell.setText("Switch Backend", true);
                                return;
                            } else if (i == SettingsActivity.this.telegramFaqRow) {
                                textSettingsCell.setText(LocaleController.getString("TelegramFAQ", C0446R.string.TelegramFAQ), true);
                                return;
                            } else if (i == SettingsActivity.this.contactsReimportRow) {
                                textSettingsCell.setText(LocaleController.getString("ImportContacts", C0446R.string.ImportContacts), true);
                                return;
                            } else if (i == SettingsActivity.this.stickersRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("StickersName", C0446R.string.StickersName), DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size() != 0 ? String.format("%d", new Object[]{Integer.valueOf(DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size())}) : TtmlNode.ANONYMOUS_REGION_ID, true);
                                return;
                            } else if (i == SettingsActivity.this.privacyPolicyRow) {
                                textSettingsCell.setText(LocaleController.getString("PrivacyPolicy", C0446R.string.PrivacyPolicy), true);
                                return;
                            } else if (i == SettingsActivity.this.emojiRow) {
                                textSettingsCell.setText(LocaleController.getString("Emoji", C0446R.string.Emoji), true);
                                return;
                            } else {
                                return;
                            }
                        case 3:
                            TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                            viewHolder = MessagesController.getGlobalMainSettings();
                            if (i == SettingsActivity.this.enableAnimationsRow) {
                                textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", C0446R.string.EnableAnimations), viewHolder.getBoolean("view_animations", true), false);
                                return;
                            } else if (i == SettingsActivity.this.sendByEnterRow) {
                                textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", C0446R.string.SendByEnter), viewHolder.getBoolean("send_by_enter", false), true);
                                return;
                            } else if (i == SettingsActivity.this.saveToGalleryRow) {
                                textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", C0446R.string.SaveToGallerySettings), SharedConfig.saveToGallery, false);
                                return;
                            } else if (i == SettingsActivity.this.autoplayGifsRow) {
                                textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayGifs", C0446R.string.AutoplayGifs), SharedConfig.autoplayGifs, true);
                                return;
                            } else if (i == SettingsActivity.this.raiseToSpeakRow) {
                                textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", C0446R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                                return;
                            } else if (i == SettingsActivity.this.customTabsRow) {
                                textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", C0446R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", C0446R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                                return;
                            } else if (i == SettingsActivity.this.directShareRow) {
                                textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", C0446R.string.DirectShare), LocaleController.getString("DirectShareInfo", C0446R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                                return;
                            } else if (i == SettingsActivity.this.dumpCallStatsRow) {
                                textCheckCell.setTextAndCheck("Dump detailed call stats", viewHolder.getBoolean("dbg_dump_call_stats", false), true);
                                return;
                            } else if (i == SettingsActivity.this.forceTcpInCallsRow) {
                                textCheckCell.setTextAndValueAndCheck("Force TCP in calls", "This disables UDP", viewHolder.getBoolean("dbg_force_tcp_in_calls", false), false, true);
                                return;
                            } else {
                                return;
                            }
                        case 4:
                            if (i == SettingsActivity.this.settingsSectionRow2) {
                                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SETTINGS", C0446R.string.SETTINGS));
                                return;
                            } else if (i == SettingsActivity.this.supportSectionRow2) {
                                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Support", C0446R.string.Support));
                                return;
                            } else if (i == SettingsActivity.this.messagesSectionRow2) {
                                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("MessagesSettings", C0446R.string.MessagesSettings));
                                return;
                            } else if (i == SettingsActivity.this.numberSectionRow) {
                                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Info", C0446R.string.Info));
                                return;
                            } else {
                                return;
                            }
                        default:
                            return;
                    }
                }
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                if (i == SettingsActivity.this.numberRow) {
                    i = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    if (i == 0 || i.phone == null || i.phone.length() == 0) {
                        i = LocaleController.getString("NumberUnknown", C0446R.string.NumberUnknown);
                    } else {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("+");
                        stringBuilder.append(i.phone);
                        i = instance.format(stringBuilder.toString());
                    }
                    textDetailSettingsCell.setTextAndValue(i, LocaleController.getString("Phone", C0446R.string.Phone), true);
                } else if (i == SettingsActivity.this.usernameRow) {
                    i = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    if (i == 0 || TextUtils.isEmpty(i.username)) {
                        i = LocaleController.getString("UsernameEmpty", C0446R.string.UsernameEmpty);
                    } else {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("@");
                        stringBuilder2.append(i.username);
                        i = stringBuilder2.toString();
                    }
                    textDetailSettingsCell.setTextAndValue(i, LocaleController.getString("Username", C0446R.string.Username), true);
                } else if (i == SettingsActivity.this.bioRow) {
                    i = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId());
                    if (i == 0) {
                        i = LocaleController.getString("Loading", C0446R.string.Loading);
                    } else if (TextUtils.isEmpty(i.about)) {
                        i = LocaleController.getString("UserBioEmpty", C0446R.string.UserBioEmpty);
                    } else {
                        i = i.about;
                    }
                    textDetailSettingsCell.setTextWithEmojiAndValue(i, LocaleController.getString("UserBio", C0446R.string.UserBio), false);
                }
            } else if (i == SettingsActivity.this.overscrollRow) {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(NUM));
            } else {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(NUM));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (!(viewHolder == SettingsActivity.this.textSizeRow || viewHolder == SettingsActivity.this.enableAnimationsRow || viewHolder == SettingsActivity.this.notificationRow || viewHolder == SettingsActivity.this.backgroundRow || viewHolder == SettingsActivity.this.numberRow || viewHolder == SettingsActivity.this.askQuestionRow || viewHolder == SettingsActivity.this.sendLogsRow || viewHolder == SettingsActivity.this.sendByEnterRow || viewHolder == SettingsActivity.this.autoplayGifsRow || viewHolder == SettingsActivity.this.privacyRow || viewHolder == SettingsActivity.this.clearLogsRow || viewHolder == SettingsActivity.this.languageRow || viewHolder == SettingsActivity.this.usernameRow || viewHolder == SettingsActivity.this.bioRow || viewHolder == SettingsActivity.this.switchBackendButtonRow || viewHolder == SettingsActivity.this.telegramFaqRow || viewHolder == SettingsActivity.this.contactsSortRow || viewHolder == SettingsActivity.this.contactsReimportRow || viewHolder == SettingsActivity.this.saveToGalleryRow || viewHolder == SettingsActivity.this.stickersRow || viewHolder == SettingsActivity.this.raiseToSpeakRow || viewHolder == SettingsActivity.this.privacyPolicyRow || viewHolder == SettingsActivity.this.customTabsRow || viewHolder == SettingsActivity.this.directShareRow || viewHolder == SettingsActivity.this.versionRow || viewHolder == SettingsActivity.this.emojiRow || viewHolder == SettingsActivity.this.dataRow || viewHolder == SettingsActivity.this.themeRow || viewHolder == SettingsActivity.this.dumpCallStatsRow)) {
                if (viewHolder != SettingsActivity.this.forceTcpInCallsRow) {
                    return null;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new EmptyCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    viewGroup = new TextInfoCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    try {
                        i = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i2 = i.versionCode / 10;
                        String str = TtmlNode.ANONYMOUS_REGION_ID;
                        switch (i.versionCode % 10) {
                            case 0:
                            case 9:
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("universal ");
                                stringBuilder.append(Build.CPU_ABI);
                                stringBuilder.append(" ");
                                stringBuilder.append(Build.CPU_ABI2);
                                str = stringBuilder.toString();
                                break;
                            case 1:
                            case 3:
                                str = "arm-v7a";
                                break;
                            case 2:
                            case 4:
                                str = "x86";
                                break;
                            case 5:
                            case 7:
                                str = "arm64-v8a";
                                break;
                            case 6:
                            case 8:
                                str = "x86_64";
                                break;
                            default:
                                break;
                        }
                        TextInfoCell textInfoCell = (TextInfoCell) viewGroup;
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{i.versionName, Integer.valueOf(i2), str});
                        textInfoCell.setText(LocaleController.formatString("TelegramVersion", C0446R.string.TelegramVersion, objArr));
                        break;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        break;
                    }
                case 6:
                    viewGroup = new TextDetailSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (i != SettingsActivity.this.emptyRow) {
                if (i != SettingsActivity.this.overscrollRow) {
                    if (!(i == SettingsActivity.this.settingsSectionRow || i == SettingsActivity.this.supportSectionRow || i == SettingsActivity.this.messagesSectionRow)) {
                        if (i != SettingsActivity.this.contactsSectionRow) {
                            if (!(i == SettingsActivity.this.enableAnimationsRow || i == SettingsActivity.this.sendByEnterRow || i == SettingsActivity.this.saveToGalleryRow || i == SettingsActivity.this.autoplayGifsRow || i == SettingsActivity.this.raiseToSpeakRow || i == SettingsActivity.this.customTabsRow || i == SettingsActivity.this.directShareRow || i == SettingsActivity.this.dumpCallStatsRow)) {
                                if (i != SettingsActivity.this.forceTcpInCallsRow) {
                                    if (!(i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.themeRow || i == SettingsActivity.this.backgroundRow || i == SettingsActivity.this.askQuestionRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.switchBackendButtonRow || i == SettingsActivity.this.telegramFaqRow || i == SettingsActivity.this.contactsReimportRow || i == SettingsActivity.this.textSizeRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.contactsSortRow || i == SettingsActivity.this.stickersRow || i == SettingsActivity.this.privacyPolicyRow || i == SettingsActivity.this.emojiRow)) {
                                        if (i != SettingsActivity.this.dataRow) {
                                            if (i == SettingsActivity.this.versionRow) {
                                                return 5;
                                            }
                                            if (!(i == SettingsActivity.this.numberRow || i == SettingsActivity.this.usernameRow)) {
                                                if (i != SettingsActivity.this.bioRow) {
                                                    if (!(i == SettingsActivity.this.settingsSectionRow2 || i == SettingsActivity.this.messagesSectionRow2 || i == SettingsActivity.this.supportSectionRow2)) {
                                                        if (i != SettingsActivity.this.numberSectionRow) {
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
        this.avatarUpdater.delegate = new C22872();
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            r0.actionBar.setOccupyStatusBar(false);
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22883());
        ActionBarMenuItem addItem = r0.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_other);
        addItem.addSubItem(1, LocaleController.getString("EditName", C0446R.string.EditName));
        addItem.addSubItem(2, LocaleController.getString("LogOut", C0446R.string.LogOut));
        r0.listAdapter = new ListAdapter(context2);
        r0.fragmentView = new FrameLayout(context2) {
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view != SettingsActivity.this.listView) {
                    return super.drawChild(canvas, view, j);
                }
                j = super.drawChild(canvas, view, j);
                if (SettingsActivity.this.parentLayout != null) {
                    int childCount = getChildCount();
                    int i = 0;
                    for (int i2 = 0; i2 < childCount; i2++) {
                        View childAt = getChildAt(i2);
                        if (childAt != view) {
                            if ((childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                                if (((ActionBar) childAt).getCastShadows() != null) {
                                    i = childAt.getMeasuredHeight();
                                }
                                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                            }
                        }
                    }
                    SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                }
                return j;
            }
        };
        r0.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) r0.fragmentView;
        r0.listView = new RecyclerListView(context2);
        r0.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager c23535 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager = c23535;
        recyclerListView.setLayoutManager(c23535);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setItemAnimator(null);
        r0.listView.setLayoutAnimation(null);
        r0.listView.setOnItemClickListener(new C22906());
        r0.listView.setOnItemLongClickListener(new C22917());
        frameLayout.addView(r0.actionBar);
        r0.extraHeightView = new View(context2);
        r0.extraHeightView.setPivotY(0.0f);
        r0.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(r0.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        r0.shadowView = new View(context2);
        r0.shadowView.setBackgroundResource(C0446R.drawable.header_shadow);
        frameLayout.addView(r0.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        r0.avatarImage = new BackupImageView(context2);
        r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        r0.avatarImage.setPivotX(0.0f);
        r0.avatarImage.setPivotY(0.0f);
        frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        r0.avatarImage.setOnClickListener(new C16998());
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
        float f = 56.0f;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        r0.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        r0.writeButton.setImageResource(C0446R.drawable.floating_camera);
        r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        r0.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.writeButton.setStateListAnimator(stateListAnimator);
            r0.writeButton.setOutlineProvider(new C17009());
        }
        View view = r0.writeButton;
        int i = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT < 21) {
            f = 60.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(i, f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        r0.writeButton.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.SettingsActivity$10$1 */
            class C16861 implements DialogInterface.OnClickListener {
                C16861() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        SettingsActivity.this.avatarUpdater.openCamera();
                    } else if (i == 1) {
                        SettingsActivity.this.avatarUpdater.openGallery();
                    } else if (i == 2) {
                        MessagesController.getInstance(SettingsActivity.this.currentAccount).deleteUserPhoto(0);
                    }
                }
            }

            public void onClick(View view) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    view = new Builder(SettingsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    }
                    if (user != null) {
                        CharSequence[] charSequenceArr = (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) ? new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley)} : new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("DeletePhoto", C0446R.string.DeletePhoto)};
                        view.setItems(charSequenceArr, new C16861());
                        SettingsActivity.this.showDialog(view.create());
                    }
                }
            }
        });
        needLayout();
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    i = 0;
                    recyclerView = recyclerView.getChildAt(0);
                    if (recyclerView != null) {
                        if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            i2 = AndroidUtilities.dp(NUM);
                            if (recyclerView.getTop() < 0) {
                                i = recyclerView.getTop();
                            }
                            i += i2;
                        }
                        if (SettingsActivity.this.extraHeight != i) {
                            SettingsActivity.this.extraHeight = i;
                            SettingsActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        return r0.fragmentView;
    }

    private void performAskAQuestion() {
        final SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
        int i = mainSettings.getInt("support_id", 0);
        User user = null;
        if (i != 0) {
            User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user2 == null) {
                String string = mainSettings.getString("support_user", null);
                if (string != null) {
                    try {
                        byte[] decode = Base64.decode(string, 0);
                        if (decode != null) {
                            AbstractSerializedData serializedData = new SerializedData(decode);
                            User TLdeserialize = User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                            if (TLdeserialize != null && TLdeserialize.id == 333000) {
                                TLdeserialize = null;
                            }
                            serializedData.cleanup();
                            user = TLdeserialize;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }
            user = user2;
        }
        if (user == null) {
            final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 1);
            alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getSupport(), new RequestDelegate() {

                /* renamed from: org.telegram.ui.SettingsActivity$12$2 */
                class C16882 implements Runnable {
                    C16882() {
                    }

                    public void run() {
                        try {
                            alertDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        final TL_help_support tL_help_support = (TL_help_support) tLObject;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                Editor edit = mainSettings.edit();
                                edit.putInt("support_id", tL_help_support.user.id);
                                AbstractSerializedData serializedData = new SerializedData();
                                tL_help_support.user.serializeToStream(serializedData);
                                edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
                                edit.commit();
                                serializedData.cleanup();
                                try {
                                    alertDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(tL_help_support.user);
                                MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(arrayList, null, true, true);
                                MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(tL_help_support.user, false);
                                Bundle bundle = new Bundle();
                                bundle.putInt("user_id", tL_help_support.user.id);
                                SettingsActivity.this.presentFragment(new ChatActivity(bundle));
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C16882());
                }
            });
            return;
        }
        MessagesController.getInstance(this.currentAccount).putUser(user, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user.id);
        presentFragment(new ChatActivity(bundle));
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0) {
                updateUserData();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.listAdapter != 0) {
                this.listAdapter.notifyItemChanged(this.stickersRow);
            }
        } else if (i == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) objArr[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != 0) {
                this.listAdapter.notifyItemChanged(this.bioRow);
            }
        } else if (i == NotificationCenter.emojiDidLoaded && this.listView != 0) {
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    private void needLayout() {
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) currentActionBarHeight);
            }
        }
        if (this.avatarImage != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(dp);
            this.shadowView.setTranslationY((float) (currentActionBarHeight + this.extraHeight));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
            final boolean z = dp > 0.2f;
            if (z != (this.writeButton.getTag() == null)) {
                if (z) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                if (this.writeButtonAnimation != null) {
                    AnimatorSet animatorSet = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    animatorSet.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                Animator[] animatorArr;
                if (z) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet animatorSet2 = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    AnimatorSet animatorSet3 = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                    animatorSet3.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animator) != null) {
                            SettingsActivity.this.writeButton.setVisibility(z ? 0 : 8);
                            SettingsActivity.this.writeButtonAnimation = null;
                        }
                    }
                });
                this.writeButtonAnimation.start();
            }
            float f = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarImage.setScaleX(f);
            this.avatarImage.setScaleY(f);
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float currentActionBarHeight2 = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + dp))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * dp);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarImage.setTranslationY((float) Math.ceil(d));
            this.nameTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
            this.nameTextView.setTranslationY((((float) Math.floor(d)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * dp))));
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * dp);
            this.onlineTextView.setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * dp));
            float f2 = 1.0f + (0.12f * dp);
            this.nameTextView.setScaleX(f2);
            this.nameTextView.setScaleY(f2);
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
        FileLocation fileLocation;
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        TLObject tLObject = null;
        if (user.photo != null) {
            tLObject = user.photo.photo_small;
            fileLocation = user.photo.photo_big;
        } else {
            fileLocation = null;
        }
        this.avatarDrawable = new AvatarDrawable(user, true);
        this.avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        if (this.avatarImage != null) {
            this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable);
            this.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation) ^ true, false);
            this.nameTextView.setText(UserObject.getUserName(user));
            this.onlineTextView.setText(LocaleController.getString("Online", C0446R.string.Online));
            this.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation) ^ true, false);
        }
    }

    private void sendLogs() {
        try {
            ArrayList arrayList = new ArrayList();
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(externalFilesDir.getAbsolutePath());
            stringBuilder.append("/logs");
            for (File file : new File(stringBuilder.toString()).listFiles()) {
                if (VERSION.SDK_INT >= 24) {
                    arrayList.add(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", file));
                } else {
                    arrayList.add(Uri.fromFile(file));
                }
            }
            if (!arrayList.isEmpty()) {
                Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                if (VERSION.SDK_INT >= 24) {
                    intent.addFlags(1);
                }
                intent.setType("message/rfc822");
                intent.putExtra("android.intent.extra.EMAIL", TtmlNode.ANONYMOUS_REGION_ID);
                intent.putExtra("android.intent.extra.SUBJECT", "last logs");
                intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
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
