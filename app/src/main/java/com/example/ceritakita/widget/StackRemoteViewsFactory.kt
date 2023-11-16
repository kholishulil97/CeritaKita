package com.example.ceritakita.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.ceritakita.R
import com.example.ceritakita.data.pref.UserPreference
import com.example.ceritakita.data.pref.dataStore
import com.example.ceritakita.data.remote.retrofit.ApiConfig
import com.example.ceritakita.utils.Constanta
import com.example.ceritakita.utils.bitmapFromURL
import com.example.ceritakita.utils.resizeBitmap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        runBlocking {
            try {
                val pref = UserPreference.getInstance(mContext.dataStore)
                val user = runBlocking { pref.getSession().first() }
                val service = ApiConfig.getApiService(user.token)

                val response = service.getStoryListWidget(10).body()
                val stories = response?.listStory
                if (stories != null) {
                    mWidgetItems.clear()
                    for (story in stories) {
                        val bitmap = bitmapFromURL(mContext, story.photoUrl)
                        val newBitmap = resizeBitmap(bitmap, 500, 500)
                        mWidgetItems.add(newBitmap)
                    }
                } else {
                    Log.i(Constanta.TAG_WIDGET, "Empty Stories")
                }
            } catch (exception: Exception) {
                Handler(mContext.mainLooper).post {
                    Toast.makeText(
                        mContext,

                        StringBuilder("Failed")
                            .append(" : ")
                            .append(exception.message),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(Constanta.TAG_WIDGET, "Failed fetch data : ${exception.message}")
                    exception.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}