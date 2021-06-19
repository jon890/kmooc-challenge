package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory

class KmoocListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKmookListBinding
    private lateinit var viewModel: KmoocListViewModel
    private lateinit var adapter: LecturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )

        binding = ActivityKmookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LecturesAdapter()
            .apply { onClick = this@KmoocListActivity::startDetailActivity }

        binding.lectureList.adapter = adapter

        setEvent()
        loadAndShow()
    }

    private fun setEvent() {
        binding.pullToRefresh.setOnRefreshListener {
            // swipe시 진행할 동작
            binding.progressBar.visibility = View.VISIBLE
            adapter.clear()
            viewModel.initList()

            // 변경이 끝났음을 알린다
            binding.pullToRefresh.isRefreshing = false
        }

        binding.lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 - 1

                // 스크롤이 끝에 도달했는지 확인
                if (lastVisibleItemPosition == itemTotalCount) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.next()
                }
            }
        })
    }

    private fun loadAndShow() {
        // 첫 리스트 데이터 요청
        binding.progressBar.visibility = View.VISIBLE
        viewModel.initList()

        // 리스트 데이터를 observing 하고 화면에 표시한다
        viewModel.getLiveLectureList().observe(this, Observer {
            Log.d("KBT", it.toString());
            adapter.addLectures(it.lectures)
            binding.progressBar.visibility = View.GONE
        })
    }

    private fun startDetailActivity(lecture: Lecture) {
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }
}
