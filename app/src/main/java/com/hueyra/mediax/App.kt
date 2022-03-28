package com.hueyra.mediax

import android.app.Application
import android.content.Context
import com.github.hueyra.mediax.app.IMediaXApp
import com.github.hueyra.mediax.app.MediaXAppInitializer
import com.github.hueyra.mediax.engine.ImageEngine
import com.github.hueyra.mediax.engine.PictureSelectorEngine
import com.github.hueyra.mediax.engineimpl.GlideEngine
import com.github.hueyra.mediax.entity.LocalMedia
import com.github.hueyra.mediax.listener.OnResultCallbackListener

/**
 * Created by zhujun
 * Date : 2022-03-25
 * Desc : _
 */
class App : Application(), IMediaXApp {

    override fun onCreate() {
        super.onCreate()
        MediaXAppInitializer.getInstance().setup(this)
    }

    override fun getAppContext(): Context {
        return this
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine {
        return object : PictureSelectorEngine {
            override fun createEngine(): ImageEngine {
                return GlideEngine.createGlideEngine();
            }


            override fun getResultCallbackListener(): OnResultCallbackListener<LocalMedia> {
                return object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                    }

                    override fun onCancel() {
                    }
                }
            }
        }
    }
}