package app.sen.musics

import android.app.Application
import android.util.Log
import app.sen.musics.module.log.L


/**
 * 说明:
 * @author wangshengxing  07.24 2020
 */
class MyApplication: Application() {
    val TAG = "MyApplication"

    override fun onCreate() {
        super.onCreate()
        L.init(Log.DEBUG)
        L.i(TAG, { "onCreate: " })
    }

}