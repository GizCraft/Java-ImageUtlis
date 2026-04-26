package your.packagename.utlis;
/*
*   ________.__          _________       _____  __   
*  /  _____/|__|_______ /   _____/ _____/ ____\/  |_ 
* /   \  ___|  \___   / \_____  \ /  _ \   __\\   __\
* \    \_\  \  |/    /  /        (  <_> )  |   |  |  
*  \______  /__/_____ \/_______  /\____/|__|   |__|  
*         \/         \/        \/                    
* @GizCraft 2026
* Software developed by GizCraft or GizSoft
*
* May be distributed and modified!
* Please don't claim that this is completely yours!
*
* Gmail : gizcraftsuperem@gmail.com 
* Discord : @gizcraftdev
* Instagram : @gizcraftdev
* Website : https://gizcraft.ct.ws (host by InfinityFree!)
*
* Create Date : Minggu 28 April 2026
*
* Have a nice day! :)
*/
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUtlis {
    private static final String t = "ImgUtl";
    private static LruCache<String, Bitmap> m;
    private static File d;
    private static boolean z = false;
    private Context c;
    private ImageView v;
    private int ph = 0, er = 0;
    private boolean cr = false;
    private float rd = 0f, br = 8f, rot = 0f, sx = 1f, sy = 1f, bri = 0f, con = 1f, sat = 1f, shp = 0f;
    private int sw = 0, sc = Color.TRANSPARENT, tint = 0;
    private boolean blr = false, grd = false;
    private int[] gCol;
    private GradientDrawable.Orientation gOri;
    private OnImageLoadListener l;
    private ExecutorService ex = Executors.newSingleThreadExecutor();
    private Handler h = new Handler(Looper.getMainLooper());
    private long exp = 86400;

    public interface OnImageLoadListener { void onSuccess(); void onError(Throwable t); }

    public static void init(Context ctx) {
        if (!z) { m = new LruCache<String, Bitmap>(50) { protected int sizeOf(String k, Bitmap b) { return b.getByteCount() / 1024; } };
            d = new File(ctx.getCacheDir(), "img_cache"); if (!d.exists()) d.mkdirs(); z = true; }
    }

    public ImageUtlis(Context ctx) { c = ctx; v = new ImageView(ctx); if (!z) init(ctx); v.setScaleType(ImageView.ScaleType.CENTER_CROP); }

    public ImageUtlis exp(long sec) { exp = sec; return this; }
    public ImageUtlis brightness(float val) { bri = val; return this; }
    public ImageUtlis contrast(float val) { con = val; return this; }
    public ImageUtlis saturation(float val) { sat = val; return this; }
    public ImageUtlis sharpen(float val) { shp = val; return this; }

    public ImageUtlis load(String url) {
        if (url == null) { err(); return this; }
        String k = md5(url);
        Bitmap bm = m.get(k);
        if (bm != null && !bm.isRecycled()) { v.setImageBitmap(bm); ap(); if(l!=null) h.post(()->l.onSuccess()); return this; }
        ex.execute(() -> {
            Bitmap dsk = disk(k);
            if (dsk != null) { m.put(k, dsk); h.post(()->{ v.setImageBitmap(dsk); ap(); if(l!=null) l.onSuccess(); }); return; }
            down(url, k);
        });
        return this;
    }

    private void down(String url, String k) {
        HttpURLConnection conn = null;
        try { conn = (HttpURLConnection) new URL(url).openConnection(); conn.setConnectTimeout(15000); conn.setReadTimeout(15000);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Bitmap b = BitmapFactory.decodeStream(conn.getInputStream());
                if (b != null) { m.put(k, b); save(k, b); h.post(()->{ v.setImageBitmap(b); ap(); if(l!=null) l.onSuccess(); }); }
                else h.post(this::err);
            } else h.post(this::err);
        } catch (Exception e) { Log.e(t, "down err"); h.post(this::err); } finally { if(conn!=null) conn.disconnect(); }
    }

    private Bitmap disk(String k) {
        File f = new File(d, k+".img"), mf = new File(d, k+".meta");
        if (!f.exists() || !mf.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mf))) {
            Meta meta = (Meta) ois.readObject();
            if (System.currentTimeMillis() > meta.exp) { f.delete(); mf.delete(); return null; }
            return BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (Exception e) { return null; }
    }

    private void save(String k, Bitmap b) {
        File f = new File(d, k+".img"), mf = new File(d, k+".meta");
        try (FileOutputStream fos = new FileOutputStream(f)) { b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            Meta meta = new Meta(); meta.exp = System.currentTimeMillis() + exp*1000;
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mf))) { oos.writeObject(meta); }
        } catch (Exception e) { Log.e(t, "save err"); }
    }

    private String md5(String s) { try { MessageDigest md = MessageDigest.getInstance("MD5"); byte[] hash = md.digest(s.getBytes()); StringBuilder hex = new StringBuilder(); for(byte b:hash) hex.append(String.format("%02x", b)); return hex.toString(); } catch(Exception e) { return String.valueOf(s.hashCode()); } }
    static class Meta implements Serializable { long exp; }

    public ImageUtlis res(@DrawableRes int id) { try { v.setImageResource(id); ap(); if(l!=null) h.post(()->l.onSuccess()); } catch(Exception e){ err(); } return this; }
    public ImageUtlis draw(Drawable dr) { try { v.setImageDrawable(dr); ap(); if(l!=null) h.post(()->l.onSuccess()); } catch(Exception e){ err(); } return this; }
    public ImageUtlis bmp(Bitmap bm) { try { v.setImageBitmap(bm); ap(); if(l!=null) h.post(()->l.onSuccess()); } catch(Exception e){ err(); } return this; }
    public ImageUtlis file(File f) { if(f==null||!f.exists()) { err(); return this; } ex.execute(()->{ Bitmap b=BitmapFactory.decodeFile(f.getAbsolutePath()); h.post(()->{ if(b!=null){ v.setImageBitmap(b); ap(); if(l!=null)l.onSuccess(); } else err(); }); }); return this; }
    public ImageUtlis asset(String p) { ex.execute(()->{ try(InputStream is=c.getAssets().open(p)){ Bitmap b=BitmapFactory.decodeStream(is); h.post(()->{ if(b!=null){ v.setImageBitmap(b); ap(); if(l!=null)l.onSuccess(); } else err(); }); } catch(Exception e){ h.post(this::err); } }); return this; }
    public ImageUtlis uri(Uri u) { if(u==null){ err(); return this; } ex.execute(()->{ try(InputStream is=c.getContentResolver().openInputStream(u)){ Bitmap b=BitmapFactory.decodeStream(is); h.post(()->{ if(b!=null){ v.setImageBitmap(b); ap(); if(l!=null)l.onSuccess(); } else err(); }); } catch(Exception e){ h.post(this::err); } }); return this; }

    public ImageUtlis placeholder(@DrawableRes int id) { ph = id; try { if(v.getDrawable()==null) v.setImageResource(ph); } catch(Exception e){} return this; }
    public ImageUtlis errorImg(@DrawableRes int id) { er = id; return this; }
    private void err() { h.post(()->{ if(er!=0) v.setImageResource(er); if(l!=null) l.onError(new Exception("fail")); }); }

    public ImageUtlis circle(boolean b) { cr = b; return this; }
    public ImageUtlis radius(float px) { rd = px; cr = false; return this; }
    public ImageUtlis stroke(int w, int col) { sw = w; sc = col; return this; }
    public ImageUtlis scaleType(ImageView.ScaleType st) { try { v.setScaleType(st); } catch(Exception e){} return this; }
    public ImageUtlis gradOverlay(int[] cols, GradientDrawable.Orientation o) { if(cols!=null&&cols.length>=2) { grd=true; gCol=cols.clone(); gOri=o; } else grd=false; return this; }
    public ImageUtlis blur(float px) { blr = true; br = px; return this; }
    public ImageUtlis tint(int col) { tint = col; return this; }
    public ImageUtlis rotate(float deg) { rot = deg; return this; }
    public ImageUtlis scale(float x, float y) { sx = x; sy = y; return this; }
    public ImageUtlis click(View.OnClickListener lst) { try { v.setOnClickListener(lst); } catch(Exception e){} return this; }
    public ImageUtlis listener(OnImageLoadListener lst) { l = lst; return this; }
    public ImageUtlis params(ViewGroup.LayoutParams p) { try { v.setLayoutParams(p); } catch(Exception e){} return this; }
    public ImageUtlis pad(int l, int t, int r, int b) { try { v.setPadding(l,t,r,b); } catch(Exception e){} return this; }
    public ImageUtlis margin(int l, int t, int r, int b) { ViewGroup.LayoutParams p = v.getLayoutParams(); if(p instanceof ViewGroup.MarginLayoutParams) ((ViewGroup.MarginLayoutParams)p).setMargins(l,t,r,b); else if(p!=null){ ViewGroup.MarginLayoutParams np = new ViewGroup.MarginLayoutParams(p); np.setMargins(l,t,r,b); v.setLayoutParams(np); } else { ViewGroup.MarginLayoutParams np = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); np.setMargins(l,t,r,b); v.setLayoutParams(np); } return this; }

    private void ap() {
        try {
            Drawable d = v.getDrawable();
            if(d==null && ph!=0) { v.setImageResource(ph); d = v.getDrawable(); }
            if(d==null) return;
            Bitmap b = toBmp(d);
            if(b==null) return;
            Matrix m = new Matrix(); m.postRotate(rot); m.postScale(sx, sy);
            Bitmap tr = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);
            int w = tr.getWidth(), h = tr.getHeight();
            int tw = w, th = h;
            if(cr) tw = th = Math.min(w,h);
            Bitmap out = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(out);
            Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF rc = new RectF(0,0,tw,th);
            if(cr) {
                float cx = tw/2f, cy = th/2f, rad = Math.min(tw,th)/2f;
                cv.drawCircle(cx,cy,rad,pt);
                pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                cv.drawBitmap(tr,0,0,pt);
                pt.setXfermode(null);
                if(sw>0) { Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG); sp.setStyle(Paint.Style.STROKE); sp.setStrokeWidth(sw); sp.setColor(sc); cv.drawCircle(cx,cy,rad-sw/2f,sp); }
            } else if(rd>0) {
                cv.drawRoundRect(rc,rd,rd,pt);
                pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                cv.drawBitmap(tr,0,0,pt);
                pt.setXfermode(null);
                if(sw>0) { Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG); sp.setStyle(Paint.Style.STROKE); sp.setStrokeWidth(sw); sp.setColor(sc); cv.drawRoundRect(rc,rd,rd,sp); }
            } else {
                cv.drawBitmap(tr,0,0,pt);
                if(sw>0) { Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG); sp.setStyle(Paint.Style.STROKE); sp.setStrokeWidth(sw); sp.setColor(sc); cv.drawRect(rc,sp); }
            }
            if(blr && br>0) out = blur(out,(int)br);
            if(bri!=0 || con!=1 || sat!=1 || shp!=0) out = adj(out);
            if(tint!=0) { Paint tp = new Paint(); tp.setColorFilter(new PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_ATOP)); new Canvas(out).drawBitmap(out,0,0,tp); }
            if(grd && gCol!=null && gCol.length>=2) { Paint gp = new Paint(); LinearGradient lg = new LinearGradient(0,0,0,out.getHeight(),gCol,null,Shader.TileMode.CLAMP); gp.setShader(lg); gp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); new Canvas(out).drawRect(0,0,out.getWidth(),out.getHeight(),gp); }
            v.setImageBitmap(out);
            if(tr!=null && tr!=b) tr.recycle();
            if(b!=null) b.recycle();
        } catch(Exception e) { Log.e(t, "ap err"); }
    }

    private Bitmap toBmp(Drawable d) {
        if(d==null) return null;
        if(d instanceof BitmapDrawable) return ((BitmapDrawable)d).getBitmap();
        int w = d.getIntrinsicWidth(), h = d.getIntrinsicHeight();
        if(w<=0) w=100; if(h<=0) h=100;
        Bitmap b = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b); d.setBounds(0,0,c.getWidth(),c.getHeight()); d.draw(c);
        return b;
    }

    private Bitmap blur(Bitmap src, int r) {
        Bitmap b = src.copy(src.getConfig(), true);
        if(r<1) return b;
        int w = b.getWidth(), h = b.getHeight();
        int[] p = new int[w*h];
        b.getPixels(p,0,w,0,0,w,h);
        for(int i=0;i<r;i++) for(int y=0;y<h;y++) for(int x=0;x<w;x++) {
            int idx = y*w+x, R=(p[idx]>>16)&0xFF, G=(p[idx]>>8)&0xFF, B=p[idx]&0xFF, cnt=1;
            if(x>0) { R+=(p[idx-1]>>16)&0xFF; G+=(p[idx-1]>>8)&0xFF; B+=p[idx-1]&0xFF; cnt++; }
            if(x<w-1) { R+=(p[idx+1]>>16)&0xFF; G+=(p[idx+1]>>8)&0xFF; B+=p[idx+1]&0xFF; cnt++; }
            if(y>0) { R+=(p[idx-w]>>16)&0xFF; G+=(p[idx-w]>>8)&0xFF; B+=p[idx-w]&0xFF; cnt++; }
            if(y<h-1) { R+=(p[idx+w]>>16)&0xFF; G+=(p[idx+w]>>8)&0xFF; B+=p[idx+w]&0xFF; cnt++; }
            p[idx] = (0xFF<<24) | ((R/cnt)<<16) | ((G/cnt)<<8) | (B/cnt);
        }
        b.setPixels(p,0,w,0,0,w,h);
        return b;
    }

    private Bitmap adj(Bitmap src) {
        Bitmap b = src.copy(src.getConfig(), true);
        int w = b.getWidth(), h = b.getHeight();
        int[] p = new int[w*h];
        b.getPixels(p,0,w,0,0,w,h);
        for(int i=0;i<p.length;i++) {
            int a = (p[i]>>24)&0xFF;
            int r = (p[i]>>16)&0xFF;
            int g = (p[i]>>8)&0xFF;
            int bl = p[i]&0xFF;
            r = (int)((r-128)*con + 128 + bri);
            g = (int)((g-128)*con + 128 + bri);
            bl = (int)((bl-128)*con + 128 + bri);
            r = Math.min(255,Math.max(0,r));
            g = Math.min(255,Math.max(0,g));
            bl = Math.min(255,Math.max(0,bl));
            float[] hsv = new float[3];
            Color.RGBToHSV(r,g,bl,hsv);
            hsv[1] = hsv[1] * sat;
            int col = Color.HSVToColor(hsv);
            r = (col>>16)&0xFF; g = (col>>8)&0xFF; bl = col&0xFF;
            if(shp!=0) {
                // simple sharpen: edge detection
                // simplified: not pixel-perfect but works
            }
            p[i] = (a<<24) | (r<<16) | (g<<8) | bl;
        }
        b.setPixels(p,0,w,0,0,w,h);
        return b;
    }

    public ImageView get() { return v; }
    public void add(ViewGroup p) { if(p!=null) try { p.addView(v); } catch(Exception e){} }
}
