package org.telegram.messenger;

import android.graphics.Paint;
import android.graphics.Path;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class DocumentObject {

    public static class ThemeDocument extends TLRPC.TL_document {
        public Theme.ThemeAccent accent;
        public Theme.ThemeInfo baseTheme;
        public TLRPC.ThemeSettings themeSettings;
        public TLRPC.Document wallpaper;

        public ThemeDocument(TLRPC.ThemeSettings settings) {
            this.themeSettings = settings;
            Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(settings));
            this.baseTheme = theme;
            this.accent = theme.createNewAccent(settings);
            if (this.themeSettings.wallpaper instanceof TLRPC.TL_wallPaper) {
                TLRPC.Document document = ((TLRPC.TL_wallPaper) this.themeSettings.wallpaper).document;
                this.wallpaper = document;
                this.id = document.id;
                this.access_hash = this.wallpaper.access_hash;
                this.file_reference = this.wallpaper.file_reference;
                this.user_id = this.wallpaper.user_id;
                this.date = this.wallpaper.date;
                this.file_name = this.wallpaper.file_name;
                this.mime_type = this.wallpaper.mime_type;
                this.size = this.wallpaper.size;
                this.thumbs = this.wallpaper.thumbs;
                this.version = this.wallpaper.version;
                this.dc_id = this.wallpaper.dc_id;
                this.key = this.wallpaper.key;
                this.iv = this.wallpaper.iv;
                this.attributes = this.wallpaper.attributes;
                return;
            }
            this.id = -2147483648L;
            this.dc_id = Integer.MIN_VALUE;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.tgnet.TLRPC$TL_photoPathSize} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.messenger.SvgHelper.SvgDrawable getSvgThumb(java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize> r7, java.lang.String r8, float r9) {
        /*
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            int r4 = r7.size()
        L_0x0008:
            if (r3 >= r4) goto L_0x0035
            java.lang.Object r5 = r7.get(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoPathSize
            if (r6 == 0) goto L_0x0018
            r2 = r5
            org.telegram.tgnet.TLRPC$TL_photoPathSize r2 = (org.telegram.tgnet.TLRPC.TL_photoPathSize) r2
            goto L_0x001c
        L_0x0018:
            int r0 = r5.w
            int r1 = r5.h
        L_0x001c:
            if (r2 == 0) goto L_0x0032
            if (r0 == 0) goto L_0x0032
            if (r1 == 0) goto L_0x0032
            byte[] r6 = r2.bytes
            java.lang.String r6 = org.telegram.messenger.SvgHelper.decompress(r6)
            org.telegram.messenger.SvgHelper$SvgDrawable r6 = org.telegram.messenger.SvgHelper.getDrawableByPath(r6, r0, r1)
            if (r6 == 0) goto L_0x0031
            r6.setupGradient(r8, r9)
        L_0x0031:
            return r6
        L_0x0032:
            int r3 = r3 + 1
            goto L_0x0008
        L_0x0035:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DocumentObject.getSvgThumb(java.util.ArrayList, java.lang.String, float):org.telegram.messenger.SvgHelper$SvgDrawable");
    }

    public static SvgHelper.SvgDrawable getSvgThumb(TLRPC.Document document, String colorKey, float alpha) {
        return getSvgThumb(document, colorKey, alpha, 1.0f);
    }

    public static SvgHelper.SvgDrawable getSvgRectThumb(String colorKey, float alpha) {
        Path path = new Path();
        path.addRect(0.0f, 0.0f, 512.0f, 512.0f, Path.Direction.CW);
        path.close();
        SvgHelper.SvgDrawable drawable = new SvgHelper.SvgDrawable();
        drawable.commands.add(path);
        drawable.paints.put(path, new Paint(1));
        drawable.width = 512;
        drawable.height = 512;
        drawable.setupGradient(colorKey, alpha);
        return drawable;
    }

    public static SvgHelper.SvgDrawable getSvgThumb(TLRPC.Document document, String colorKey, float alpha, float zoom) {
        if (document == null) {
            return null;
        }
        SvgHelper.SvgDrawable pathThumb = null;
        int b = 0;
        int N2 = document.thumbs.size();
        while (true) {
            if (b >= N2) {
                break;
            }
            TLRPC.PhotoSize size = document.thumbs.get(b);
            if (size instanceof TLRPC.TL_photoPathSize) {
                int w = 512;
                int h = 512;
                int a = 0;
                int N = document.attributes.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        w = attribute.w;
                        h = attribute.h;
                        break;
                    }
                    a++;
                }
                if (w != 0 && h != 0 && (pathThumb = SvgHelper.getDrawableByPath(SvgHelper.decompress(size.bytes), (int) (((float) w) * zoom), (int) (((float) h) * zoom))) != null) {
                    pathThumb.setupGradient(colorKey, alpha);
                }
            } else {
                b++;
            }
        }
        return pathThumb;
    }
}
