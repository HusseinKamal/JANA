package com.hussein.jana.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntryDecorator

/**
 * Gives every Navigation 3 back-stack entry its own [ViewModelStore].
 *
 * Without it, `koinViewModel()` inside an entry is scoped to the Activity/window, so a
 * screen opened a second time reuses the first ViewModel and ignores its new route
 * arguments (e.g. `CourseDetail(courseId, openCheckout)`).
 *
 * The store is cleared when the entry is popped, and all stores are cleared when the
 * host leaves composition.
 *
 * Replace with `rememberViewModelStoreNavEntryDecorator()` from
 * `lifecycle-viewmodel-navigation3` once that artifact is available to the build.
 */
@Composable
fun <T : Any> rememberEntryViewModelStoreDecorator(): NavEntryDecorator<T> {
    val stores = remember { mutableMapOf<Any, ViewModelStore>() }

    DisposableEffect(stores) {
        onDispose {
            stores.values.forEach { it.clear() }
            stores.clear()
        }
    }

    return remember(stores) {
        NavEntryDecorator(
            onPop = { contentKey -> stores.remove(contentKey)?.clear() },
            decorate = { entry ->
                val store = stores.getOrPut(entry.contentKey) { ViewModelStore() }
                val owner = remember(store) {
                    object : ViewModelStoreOwner {
                        override val viewModelStore: ViewModelStore = store
                    }
                }
                CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                    entry.Content()
                }
            },
        )
    }
}
