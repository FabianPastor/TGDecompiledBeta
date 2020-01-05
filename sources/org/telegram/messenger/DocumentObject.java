package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_themeSettings;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

public class DocumentObject {

    public static class ThemeDocument extends TL_document {
        public ThemeAccent accent;
        public ThemeInfo baseTheme;
        public TL_themeSettings themeSettings;
        public Document wallpaper;

        public ThemeDocument(TL_themeSettings tL_themeSettings) {
            this.themeSettings = tL_themeSettings;
            this.baseTheme = Theme.getTheme(Theme.getBaseThemeKey(tL_themeSettings));
            this.accent = this.baseTheme.createNewAccent(tL_themeSettings);
            WallPaper wallPaper = this.themeSettings.wallpaper;
            if (wallPaper instanceof TL_wallPaper) {
                this.wallpaper = ((TL_wallPaper) wallPaper).document;
                Document document = this.wallpaper;
                this.id = document.id;
                this.access_hash = document.access_hash;
                this.file_reference = document.file_reference;
                this.user_id = document.user_id;
                this.date = document.date;
                this.file_name = document.file_name;
                this.mime_type = document.mime_type;
                this.size = document.size;
                this.thumbs = document.thumbs;
                this.version = document.version;
                this.dc_id = document.dc_id;
                this.key = document.key;
                this.iv = document.iv;
                this.attributes = document.attributes;
                return;
            }
            this.id = -2147483648L;
            this.dc_id = Integer.MIN_VALUE;
        }
    }
}
