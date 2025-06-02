package com.theoghn.mtgtreasury.ui.community

import kotlinx.serialization.Serializable


//@Serializable
//object CommunityScreenDestination


@Serializable
object CommunityRoot {
    @Serializable
    object CommunityScreenDestination

    @Serializable
    object ActiveChatsDestination
}
