package com.programmers.kmooc.repositories

import android.util.Log
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.Constants
import com.programmers.kmooc.utils.DateUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey = Constants.API_KEY

    fun list(completed: (LectureList) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("serviceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                try {
                    completed(parseLectureList(JSONObject(it)))
                } catch (e: Exception) {
                    // todo kbt : 에러 처리 방식을 좀더 추상화 해야할듯
                    Log.d("KBT", e.toString())
                    completed(LectureList.EMPTY)
                }
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {
        //TODO: JSONObject -> LectureList 를 구현하세요
        // todo kbt : home lecture, detail lecture 분리
        val paginationJsonObject: JSONObject = jsonObject.get("pagination") as JSONObject
        val count: Int = paginationJsonObject.getInt("count")
        val previous: String = paginationJsonObject.getString("previous")
        val numPages: Int = paginationJsonObject.getInt("num_pages")
        val next: String = paginationJsonObject.getString("next")

        val resultJsonArray: JSONArray = jsonObject.getJSONArray("results")
        val lectureList = mutableListOf<Lecture>()
        for (i: Int in 0 until resultJsonArray.length()) {
            val lectureJsonObject = resultJsonArray.getJSONObject(i);
            val parsed = parseLecture(lectureJsonObject);
            lectureList.add(parsed)
        }

        return LectureList(
            count = count,
            numPages = numPages,
            previous = previous,
            next = next,
            lectures = lectureList
        )
    }

    private fun parseLecture(jsonObject: JSONObject): Lecture {
        //TODO: JSONObject -> Lecture 를 구현하세요
        // todo kbt : home lecture, detail lecture 분리
        val id: String = jsonObject.getString("id")
        val number: String = jsonObject.getString("number")
        val name: String = jsonObject.getString("name")
        val classfyName: String = jsonObject.getString("classfy_name")
        val middleClassfyName: String = jsonObject.getString("middle_classfy_name")
        val shortDescription: String = jsonObject.getString("short_description")
        val orgName: String = jsonObject.getString("org_name")
        val start: Date = DateUtil.parseDate(jsonObject.getString("start"))
        val end: Date = DateUtil.parseDate(jsonObject.getString("end"))
        val teachers: String? = jsonObject.getString("teachers")

        var overview: String?
        try {
            overview = jsonObject.getString("overview")
        } catch (e: JSONException) {
            overview = "";
        }

        val mediaObject = jsonObject.getJSONObject("media");
        val thumbnailObject = mediaObject.getJSONObject("course_image")
        val courseImage: String = thumbnailObject.getString("uri")

        val imageObject = mediaObject.getJSONObject("image");
        val courseImageLarge: String = imageObject.getString("large")

        return Lecture(
            id = id,
            number = number,
            name = name,
            classfyName = classfyName,
            middleClassfyName = middleClassfyName,
            shortDescription = shortDescription,
            orgName = orgName,
            start = start,
            end = end,
            teachers = teachers,
            courseImage = courseImage,
            courseImageLarge = courseImageLarge,
            overview = overview,
        )
    }
}