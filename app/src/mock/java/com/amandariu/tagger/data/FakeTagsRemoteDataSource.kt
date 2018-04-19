package com.amandariu.tagger.data

import android.support.annotation.VisibleForTesting
import android.util.Log
import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.data.Tag
import com.amandariu.tagger.demo.data.source.ISourceBase
import com.amandariu.tagger.demo.data.source.ITagsDataSource
import com.bluelinelabs.logansquare.LoganSquare
import com.google.common.collect.Lists
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class FakeTagsRemoteDataSource : ITagsDataSource {
    companion object {
        private var sInstance: ITagsDataSource? = null
        private val TAGS_DATA_SERVICE = arrayListOf<Tag>()

        @JvmStatic
        fun getInstance(): ITagsDataSource {
            if (sInstance == null) {
                sInstance = FakeTagsRemoteDataSource()
            }
            return sInstance!!
        }
    }
    init {
        addTags()
    }

    override fun saveTags(tags: List<ITag>, callback: ITagsDataSource.ISaveTagsCallback) {
        // todo
    }

    override fun getTags(callback: ISourceBase.ILoadTagsCallback) {
        callback.onTagsLoaded(Lists.newArrayList(TAGS_DATA_SERVICE))
    }

    override fun shutdown() {
        // todo
    }

    @VisibleForTesting
    private fun addTags() {
        val tagJson = getStringFromResourceFile("sample_tags.json")
        val tags = LoganSquare.parseList(tagJson, Tag::class.java)
        for (tag in tags) {
            TAGS_DATA_SERVICE.add(tag)
        }
    }

    private fun getStringFromResourceFile(filename: String): String? {
        try {
            val inStream = this.javaClass.classLoader.getResourceAsStream(filename)
            val bufferedReader = BufferedReader(InputStreamReader(inStream, "UTF-8"))

            val buffer = StringBuilder()
            var lineString: String? = null

            while ({ lineString = bufferedReader.readLine(); lineString }() != null) {
                buffer.append(lineString)
            }

            bufferedReader.close()
            return buffer.toString()
        } catch (e: IOException) {
            Log.d("AMANDA-TEST", "Could not load contents of json file")
            return null
        }
    }
}
