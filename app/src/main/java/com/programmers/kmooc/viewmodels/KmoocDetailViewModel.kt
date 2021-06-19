package com.programmers.kmooc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository


class KmoocDetailViewModel(private val repository: KmoocRepository) : ViewModel() {

    private var liveLecture: MutableLiveData<Lecture> = MutableLiveData()

    fun detail(courseId: String) {
        repository.detail(courseId) { lecture ->
            liveLecture.postValue(lecture)
        }
    }

    fun getLiveLecture(): LiveData<Lecture> {
        return liveLecture
    }
}

class KmoocDetailViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocDetailViewModel::class.java)) {
            return KmoocDetailViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}