package com.codifyr.receipttracker.domain.model

enum class Category(val displayName: String, val icon: String) {
    ELECTRONICS("إلكترونيات", "🔌"),
    HOME_APPLIANCES("أجهزة منزلية", "🏠"),
    CLOTHING("ملابس", "👕"),
    FURNITURE("أثاث", "🪑"),
    GROCERY("مشتريات", "🛒"),
    HEALTH("صحة", "💊"),
    AUTOMOTIVE("سيارات", "🚗"),
    OTHER("أخرى", "📦");

    companion object {
        fun fromString(value: String): Category {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: OTHER
        }
    }
}
