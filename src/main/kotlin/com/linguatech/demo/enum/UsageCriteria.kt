package com.linguatech.demo.enum

enum class UsageCriteria(val formatStr: String, val componentName: String) {
    PER_ONE_USE("1회", "checkPerUse"), // 1회당
    PER_ONE_MONTH("1개월", "checkPerMonth")// 1달당
}