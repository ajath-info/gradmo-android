package com.app.hihlo.model.chat

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RecentChats(
    val friendId: String? = "",
    val friendImage: String? = "",
    val time: String? = "",
    val name: String? = "",
    val sender: String? = "",
    val message: String? = "",
    val person: String? = "",
    val status: String? = "",
    val pinned: String? = "",
    val archived: String? = "",
    val messageType: String? = "",
    val groupId: String? = "",
    val messageId: String? = "",
    val senderId: String? = "",
    val senderName: String? = "",
    val statusSent: String? = "",
    val groupName: String? = "",
    val group: String? = "",

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(friendId)
        parcel.writeString(friendImage)
        parcel.writeString(time)
        parcel.writeString(name)
        parcel.writeString(sender)
        parcel.writeString(message)
        parcel.writeString(person)
        parcel.writeString(status)
        parcel.writeString(pinned)
        parcel.writeString(archived)
        parcel.writeString(messageType)
        parcel.writeString(groupId)
        parcel.writeString(messageId)
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeString(statusSent)
        parcel.writeString(groupName)
        parcel.writeString(group)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentChats> {
        override fun createFromParcel(parcel: Parcel): RecentChats {
            return RecentChats(parcel)
        }

        override fun newArray(size: Int): Array<RecentChats?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class ChatList(
    val list: MutableList<RecentChats> = mutableListOf()
) : Parcelable