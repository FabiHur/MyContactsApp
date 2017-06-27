package com.fabhurtado.mycontacts.view;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.caverock.androidsvg.SVG;
import com.fabhurtado.mycontacts.view.svg.SvgDecoder;
import com.fabhurtado.mycontacts.view.svg.SvgDrawableTranscoder;

import java.io.InputStream;

/**
 * This class is required by Glide  library.
 *
 * @author fabHurtado
 */

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Registry registry) {
        registry.register(SVG.class, PictureDrawable.class, new SvgDrawableTranscoder())
                .append(InputStream.class, SVG.class, new SvgDecoder());
    }

    // Disable manifest parsing to avoid adding similar modules twice.
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
