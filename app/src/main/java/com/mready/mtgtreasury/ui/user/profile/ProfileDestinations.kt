package com.mready.mtgtreasury.ui.user.profile

import kotlinx.serialization.Serializable

@Serializable
object ProfileRoot {
    @Serializable
    class ProfileScreenDestination(val userId:String)
}

@Serializable
object SettingsScreenDestination

@Serializable
class ProfileUpdateScreenDestination(val updateType: String)