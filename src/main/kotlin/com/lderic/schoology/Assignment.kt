package com.lderic.schoology

data class Assignment(val course: String, val name: String, val due: String, val stamp:Long, val link: String) {
    override fun toString(): String {
        return "课程: $course, 作业名称: $name, 截止日期: $due, 链接: $link"
    }
}