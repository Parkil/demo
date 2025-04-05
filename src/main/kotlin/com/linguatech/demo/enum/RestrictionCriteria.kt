package com.linguatech.demo.enum

enum class RestrictionCriteria(val formatStr: String) {
    NUMBER_OF_CHAR("자"), // 글자수 제한
    NUMBER_OF_USE("회") // 사용 회수 제한
}