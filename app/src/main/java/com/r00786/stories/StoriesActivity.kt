package com.nowwt.svr.stories

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.nowwt.svr.adapter.AdapterParentStories
import com.r00786.stories.Constants
import com.r00786.stories.Model.Result
import com.r00786.stories.Model.StoryResponse
import com.r00786.stories.R
import kotlinx.android.synthetic.main.activity_stories.*


/**
 * Created by Lucifer on 24-05-2018.
 */

class StoriesActivity : AppCompatActivity(), AdapterParentStories.AdapterParentStoriesCalllbacks {

    var items = ArrayList<StoryResponse>()


    override fun getVisibleItem(): Int {
        return currentItem
    }

    var currentItem = 0


    override fun requestPrevStory() {
        var prevIndex = ll.findFirstCompletelyVisibleItemPosition() - 1
        if (prevIndex >= 0 && adapter.getList()[prevIndex] != null) {
            currentItem = prevIndex
            ll.scrollToPosition(prevIndex)
            adapter.notifyItemChanged(prevIndex)
            adapter.resetVideoPlayerNo()


        }
    }


    override fun requestNextStory() {
        var nextIndex = ll.findFirstCompletelyVisibleItemPosition() + 1
        if (nextIndex < adapter.getList().size && adapter.getList()[nextIndex] != null) {
            currentItem = nextIndex
            ll.scrollToPosition(nextIndex)
            adapter.notifyItemChanged(nextIndex)
            adapter.resetVideoPlayerNo()
        }
    }

    var storiesLoaded = false
    lateinit var adapter: AdapterParentStories
    lateinit var ll: CustomLMM
    var list = ArrayList<StoryResponse>()
    lateinit var userName: String

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stories)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        adapter = AdapterParentStories(this)
        adapter.setCallbacks(this)
        ll = CustomLMM(this)
        rv_stories?.layoutManager = ll
        rv_stories?.adapter = adapter
        initData()
        adapter.addData(items)


    }

    class CustomLMM(context: Context) : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
        override fun canScrollHorizontally(): Boolean {
            return false
        }

        override fun canScrollVertically(): Boolean {
            return false
        }

    }


    override fun onBackPressed() {
        if (ll.findFirstVisibleItemPosition() >= 0) {

            adapter.releasePlayer(ll.findFirstVisibleItemPosition())
            super.onBackPressed()

        }

    }

    override fun onStop() {
        super.onStop()
        if (ll.findFirstVisibleItemPosition() >= 0)
            adapter.releasePlayer(ll.findFirstVisibleItemPosition())

    }

    override fun onPause() {
        super.onPause()
        if (ll.findFirstVisibleItemPosition() >= 0)
            adapter.StopPlayer(ll.findFirstVisibleItemPosition())
    }

    override fun onResume() {
        super.onResume()
        if (storiesLoaded) {
            if (ll.findFirstVisibleItemPosition() >= 0)
                adapter.resumePlayer(ll.findFirstVisibleItemPosition())
        }
        storiesLoaded = true
    }


    companion object {
        fun getViewHeight(view: View): Int {
            val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay

            val deviceWidth: Int

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                val size = Point()
                display.getSize(size)
                deviceWidth = size.x
            } else {
                deviceWidth = display.width
            }

            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthMeasureSpec, heightMeasureSpec)
            return view.measuredHeight //        view.getMeasuredWidth();
        }


        fun convertToSp(context: Context, px: Int): Float {
            return convertDpToPixel(px.toFloat(), context) / context.resources.displayMetrics.scaledDensity
        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }

    fun initData() {


        for (i in 1..5) {
            var list = ArrayList<Result>()

            for (i in 1..5) {

                var result = Result()
                if (i == 2) {

                    result.type = Constants.VIDEO
                    result.photo = "http://wge.wysaid.org/res/video/1.mp4"
                } else {
                    result.type = Constants.PHOTO
                    result.photo = "http://www.gstatic.com/webp/gallery/$i.jpg"
                }
                result.text = "Hello"

                list.add(result)

            }
            var obj = StoryResponse()
            obj.results = list
            items.add(obj)
        }

    }


}
