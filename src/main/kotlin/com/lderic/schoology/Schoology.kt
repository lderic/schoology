package com.lderic.schoology

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.mamoe.mirai.utils.MiraiLogger
import java.util.*

class Schoology(email: String, pass: String) {
    private val mail: String
    private val pass: String

    var isLogin = false

    init {
        this.mail = email
        this.pass = pass
    }

    val domain = "https://schoology.justin-siena.org"
    val JSSchoologyLoginUrl = "$domain/login?school=20609933"
    val JSSchoologyHomeUrl = "$domain/home"
    val JSCalender = "$domain/calendar"
    val cookies = mutableListOf<Cookie>()
    val client = HttpClient() {
        install(HttpCookies) {
            storage = object : CookiesStorage {

                override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                    cookies.add(cookie)
                }

                override fun close() {

                }

                override suspend fun get(requestUrl: Url): List<Cookie> {
                    return cookies
                }

            }
        }
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        BrowserUserAgent()
    }

    private suspend fun getBuildId(): String {
        val response = client.get<String>(JSSchoologyLoginUrl)
        return response.split("form_build_id\" id=\"")[1].split("\"")[0]
    }

    suspend fun login() {
        val id = getBuildId()
        client.submitForm<HttpResponse>(
            url = JSSchoologyLoginUrl,
            formParameters = Parameters.build {
                append("mail", mail)
                append("pass", pass)
                append("form_build_id", id)
                append("form_id", "s_user_login_form")
            }
        )
        isLogin = true
    }

    suspend fun upcoming(): MutableList<Assignment> {
        if (!isLogin) {
            logger.info("User create: $mail")
            login()
        }
        var result = client.get<String>("https://schoology.justin-siena.org/home/upcoming_ajax")
            .split("{\"html\":\"")[1].split("\"}")[0]

        result = redo(result, "\\/", "/")

        val assignmentList = mutableListOf<Assignment>()
        result.split("upcoming-event course-event").forEachIndexed() { i, it ->
            if (i == 0) {
                return@forEachIndexed
            }
            it.split("\\u003C/div\\u003E")[0].let { inner ->
                val due = Date(inner.split("data-start=\\u0022")[1].split("\\u0022")[0].toLong() * 1000).toString()
                var str = inner.split("aria-label=\\u0027")[1].split("\\u003C/span\\u003E\\u0026nbsp;")[0]
                val course = str.split("\\u0027\\u003E")[0]
                str = str.split("href=\\u0022")[1]
                val link = domain + str.split("\\u0022\\u003E")[0]
                val name = str.split("\\u0022\\u003E")[1].split("\\u003C/a\\u003E")[0]
                assignmentList.add(
                    Assignment(
                        course,
                        name,
                        due,
                        inner.split("data-start=\\u0022")[1].split("\\u0022")[0].toLong() * 1000,
                        link
                    )
                )
            }
        }
        return assignmentList
    }

    fun redo(target: String, before: String, after: String): String {
        val arr = target.split(before)
        if (arr.size == 1) {
            return target
        }
        val sb = StringBuilder()
        for (i in arr.indices - 1) {
            sb.append(arr[i]).append(after)
        }
        sb.append(arr[arr.lastIndex])
        return sb.toString()
    }


    companion object {
        val logger = MiraiLogger.Factory.INSTANCE.create(Schoology::class)
    }
}