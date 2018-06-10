package com.r00786.stories


import android.content.Context
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

class CacheDataSourceCustom(private val context: Context, private val maxCacheSize: Long, private val maxFileSize: Long) : DataSource.Factory {
    private val defaultDatasourceFactory: DefaultDataSourceFactory

    init {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val bandwidthMeter = DefaultBandwidthMeter()
        defaultDatasourceFactory = DefaultDataSourceFactory(this.context,
                bandwidthMeter,
                DefaultHttpDataSourceFactory(userAgent, bandwidthMeter))
    }

    override fun createDataSource(): DataSource {
        val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
        val simpleCache = SimpleCache(File(context.cacheDir, "media"), evictor)
        return CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(),
                FileDataSource(), CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null)
    }
}