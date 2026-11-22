package com.hussein.jana.feature.home.tabs.courses.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val totalLessons: Int,
    val completedLessons: Int,
    val subjects: List<String>,
    /** True only once the backend has confirmed the student's subscription. */
    val isSubscribed: Boolean = false,
    /** Null means unknown; price UI is hidden. Null is never treated as free. */
    val price: Money? = null,
) {
    /**
     * A locked course can be bought only when it has a price. Unpriced locked courses stay
     * locked with no Subscribe action.
     * TODO: the backend should always send a price for purchasable locked courses.
     */
    val isPurchasable: Boolean get() = !isSubscribed && price != null
}
