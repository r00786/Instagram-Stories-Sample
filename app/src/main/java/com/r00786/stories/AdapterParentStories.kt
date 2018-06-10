package com.nowwt.svr.adapter

import android.content.Context
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nowwt.svr.stories.StoriesActivity
import com.r00786.stories.Constants
import com.r00786.stories.Model.Result
import com.r00786.stories.Model.StoryResponse
import com.r00786.stories.R
import com.r00786.stories.StoryStatusView
import kotlinx.android.synthetic.main.layout_rv_parent.view.*
import kotlinx.android.synthetic.main.pb_common.view.*


/**
 * Created by Lucifer on 24-05-2018.
 */

class AdapterParentStories(context: Context) : RecyclerView.Adapter<AdapterParentStories.ViewHolder>() {

    interface AdapterParentStoriesCalllbacks {
        fun requestNextStory()
        fun requestPrevStory()
        fun getVisibleItem(): Int

    }

    lateinit var holderGlobal: ViewHolder
    var adapterParentStories: AdapterParentStoriesCalllbacks? = null
    var listItems = ArrayList<StoryResponse>()
    val context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_rv_parent, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == adapterParentStories?.getVisibleItem())
            holderGlobal = holder

        holder.bindViews(listItems[position])
    }

    override fun getItemCount(): Int {
        return if (listItems != null) {
            listItems.size
        } else {
            0
        }
    }

    var videoPlayerNo = 0
    var storiesloaded = false

    lateinit var adapter: AdapterChildStories;


    fun setCallbacks(adapterParentStoriesCalllbacks: AdapterParentStoriesCalllbacks) {
        this.adapterParentStories = adapterParentStoriesCalllbacks
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            AdapterChildStories.StoryLoaderCallbacks, StoryStatusView.UserInteractionListener {


        override fun getVideoPlayerIndex(): Int {
            return videoPlayerNo
        }

        var playStories: StoryStatusView = itemView.storiesStatusParent

        override fun setVideoDuration(time: Long, position: Int) {
            playStories.setCustomDuration(time, position)


        }

        override fun onNext() {
//            if (!skipped) {
//                ll.scrollToPosition(ll.findFirstVisibleItemPosition() + 1)
////                playStories.pause()
//            }
        }

        override fun onPrev() {

        }

        override fun onComplete() {
            //Here we have to move to next user

        }

        override fun getVisibleItem(): Int {
            return ll.findFirstVisibleItemPosition()
        }

        var skipped = false

        override fun isStoryLoading() {
            if (!skipped) {
                if (!storiesloaded) {
                    playStories.playStories()
                    storiesloaded = true
                }
                playStories.pause()
            }
            itemView.pb_loading.visibility = View.VISIBLE
        }

        override fun isStoryLoaded() {
            if (!storiesloaded) {
                playStories.playStories()
                storiesloaded = true
            }
            playStories.resume()
            itemView.pb_loading.visibility = View.GONE

        }

        fun startState(obj: StoryResponse) {

            itemView.pb_loading.visibility = View.VISIBLE
            playStories.clear()
            playStories.setStoriesCount(obj.results.size)
            playStories.setStoryDuration(10000L)
            adapter = AdapterChildStories(context)
            itemView.rv_child_stories.layoutManager = ll
            itemView.rv_child_stories.adapter = adapter
            playStories.setUserInteractionListener(this)
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(null)
            itemView.rv_child_stories.clearOnScrollListeners()
            itemView.rv_child_stories.onFlingListener = null
            snapHelper.attachToRecyclerView(itemView.rv_child_stories)
            adapter.addData(obj.results)
            adapter.setCallBacksStoryCallBacks(this)
        }

        var ll = StoriesActivity.CustomLMM(context)
        fun bindViews(obj: StoryResponse) {
            startState(obj)
            var clickNext = false
            var clickPrev = false

            itemView.skipParent.setOnClickListener {
                skipped = true
                var pos = ll.findFirstCompletelyVisibleItemPosition()

                if (!clickNext) {
                    clickNext = true
                    if ((pos) < obj.results.size - 1) {
                        ll.scrollToPosition(pos + 1)
                        videoPlayerNo = pos + 1
                        if (obj.results[pos].type == Constants.VIDEO) {
                            adapter.pausePlayer(pos)
                        }
                        itemView.skipParent.post {
                            playStories.skip()
                            adapter.notifyItemChanged(pos + 1)
                        }
                    } else {
                        if (obj.results[obj.results.size - 1].type == Constants.VIDEO) {
                            adapter.pausePlayer(obj.results.size - 1)
                        }
                        getNextNowwt()
                    }
                    itemView.postDelayed({
                        clickNext = false
                    }, 100)
                }
                skipped = false
            }

            itemView.reverseparent.setOnClickListener {
                if (!clickPrev) {
                    clickPrev = true
                    var pos = ll.findFirstCompletelyVisibleItemPosition()
                    if ((pos) > 0) {
                        videoPlayerNo = pos - 1
                        if (obj.results[pos].type == Constants.VIDEO) {
                            adapter.pausePlayer(pos)
                        }
                        playStories.reverse()
                        ll.scrollToPosition(pos - 1)
                        adapter.notifyItemChanged(pos - 1)
                    } else {
                        if (obj.results[0].type == Constants.VIDEO) {
                            adapter.pausePlayer(0)
                        }
                        getPreviousNowwt()
                    }
                    itemView.postDelayed({
                        clickPrev = false
                    }, 100)
                }
            }
        }

        fun getNextNowwt() {

            adapterParentStories?.requestNextStory()
        }

        fun getPreviousNowwt() {

            adapterParentStories?.requestPrevStory()
        }

    }

    fun addData(listItems: ArrayList<StoryResponse>) {
        var size = this.listItems.size
        this.listItems.addAll(listItems)
        var sizeNew = this.listItems.size
        notifyItemRangeChanged(size, sizeNew)
    }

    fun addDataPrev(listItems: ArrayList<StoryResponse>) {
        var size = this.listItems.size
        this.listItems.addAll(0, listItems)
        notifyItemRangeChanged(0, size)
    }

    fun StopPlayer(position: Int) {

        var player = listItems[position].results[videoPlayerNo].simpleExoPlayer
        if (player != null) {
            player.playWhenReady = false
        }

    }

    fun resumePlayer(position: Int) {
        var player = listItems[position].results[videoPlayerNo].simpleExoPlayer
        if (player != null) {
            player.playWhenReady = true
        }
    }

    fun releasePlayer(position: Int) {
        var player = listItems[position].results
        for (obj in player) {
            if (obj.type == Constants.VIDEO) {
                if (obj.simpleExoPlayer != null) {
                    if (obj.simpleExoPlayer.playWhenReady) {
                        obj.simpleExoPlayer.playWhenReady = false
                        obj.simpleExoPlayer.release()
                    }
                }
            }
        }

    }

    fun getList(): ArrayList<StoryResponse> {
        return listItems
    }

    fun resetVideoPlayerNo() {
        videoPlayerNo = 0
        storiesloaded = false
    }



    fun showReplies(results: ArrayList<Result>) {
        //Pending

    }

}
