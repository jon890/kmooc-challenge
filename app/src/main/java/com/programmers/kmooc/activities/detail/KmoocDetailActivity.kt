package com.programmers.kmooc.activities.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAndShow()
    }

    private fun loadAndShow() {
        // 상세 데이터 요청 & 로딩창 표시
        val courseId = intent.getStringExtra(INTENT_PARAM_COURSE_ID)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.detail(courseId)
        
        viewModel.getLiveLecture().observe(this, {
            updateUI(it)
            binding.progressBar.visibility = View.GONE
        })
    }

    private fun updateUI(lecture: Lecture) {
        binding.lectureNumber.setDescription("강좌번호", lecture.number)
        binding.lectureType.setDescription("강좌분류", lecture.classfyName)
        binding.lectureOrg.setDescription("운영기관", lecture.orgName)

        val duration = DateUtil.formatDate(lecture.start) +
                " ~ " +
                DateUtil.formatDate(lecture.end)
        binding.lectureDue.setDescription("운영기간", duration)
        var teachers : String
        if (lecture.teachers != null) {
            teachers = lecture.teachers
        } else {
            teachers = "정보없음"
        }
        binding.lectureTeachers.setDescription("교수정보", teachers)

        // 웹뷰
        binding.webView.loadUrl(lecture.overview)

        // 이미지 로딩
        ImageLoader.loadImage(
            lecture.courseImageLarge
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