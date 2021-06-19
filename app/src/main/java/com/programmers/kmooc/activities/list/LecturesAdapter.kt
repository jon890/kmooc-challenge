package com.programmers.kmooc.activities.list

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SimpleCursorTreeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.R
import com.programmers.kmooc.databinding.ViewKmookListItemBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil

class LecturesAdapter : RecyclerView.Adapter<LectureViewHolder>() {

    private val lectures = mutableListOf<Lecture>()
    var onClick: (Lecture) -> Unit = {}

//    fun updateLectures(lectures: List<Lecture>) {
//        this.lectures.clear()
//        this.lectures.addAll(lectures)
//        notifyDataSetChanged()
//    }

    fun addLectures(lectures: List<Lecture>) {
        this.lectures.addAll(lectures)
        notifyDataSetChanged()
    }

    fun clear() {
        this.lectures.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_kmook_list_item, parent, false)
        val binding = ViewKmookListItemBinding.bind(view)
        return LectureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val lecture = lectures[position]
        holder.updateUI(lecture)
        holder.itemView.setOnClickListener { onClick(lecture) }
    }
}

class LectureViewHolder(binding: ViewKmookListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val binding: ViewKmookListItemBinding = binding

    fun updateUI(lecture: Lecture) {
        binding.lectureFrom.text = lecture.orgName
        binding.lectureTitle.text = lecture.name

        val duration = DateUtil.formatDate(lecture.start) +
                " ~ " +
                DateUtil.formatDate(lecture.end)
        binding.lectureDuration.text = duration

        // 이미지 로딩
        ImageLoader.loadImage(
            lecture.courseImage
        ) { bitmap: Bitmap? ->
            run {
                Log.d("KBT", "imageLoaded!!")
                Log.d("KBT", bitmap.toString())

                val handler = Handler(Looper.getMainLooper())
                handler.post { binding.lectureImage.setImageBitmap(bitmap) }
            }
        }
    }
}