package com.mready.mtgtreasury.ui.user.profile

import kotlinx.serialization.Serializable

@Serializable
object ProfileRoot {
    @Serializable
    object SettingsScreenDestination

    @Serializable
    object ProfileScreenDestination

    @Serializable
    class ProfileUpdateScreenDestination(val updateType: String)
}