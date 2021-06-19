package com.programmers.kmooc.viewmodels

import android.widget.SimpleCursorAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository
import java.util.Collections.addAll

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    private var liveLectureList: MutableLiveData<LectureList> = MutableLiveData()

    fun initList() {
        repository.list { lectureList ->
            // 오류시 재 요청
//            if (LectureList.EMPTY == lectureList) {
//                initList()
//            }

            liveLectureList.postValue(lectureList)
        }
    }

    fun getLiveLectureList(): LiveData<LectureList> {
        return liveLectureList
    }

    fun next() {
        val currentLectureList: LectureList? = liveLectureList.value
        var param: LectureList

        if (currentLectureList == null) {
            param = LectureList.EMPTY
        } else {
            param = currentLectureList
        }

        // todo kbt : list가 로드되기 전에 next가 로드 되버리는 경우..?
        repository.next(param) { lectureList ->
            liveLectureList.postValue(lectureList)
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}