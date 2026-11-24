package com.hussein.jana.feature.home.tabs.profile

import androidx.compose.runtime.Composable
import com.hussein.jana.feature.home.HomeTab
import com.hussein.jana.feature.home.tabs.profile.presentation.profile.ProfileScreen

/** Profile tab of Home. [onOpenTab] switches the selected Home tab. */
@Composable
fun ProfileTabScreen(onOpenTab: (HomeTab) -> Unit) {
    ProfileScreen(onOpenTeacherTab = { onOpenTab(HomeTab.MY_TEACHER_CHAT) })
}
