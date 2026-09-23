package com.rohitchauhan.hiichat.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto

@Entity(tableName = "current_user")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val profileImg: String,
    val lastSeen: Long,
    val fcmToken: String
) {
    fun toUserDto(): UserDto {
        return UserDto(
            id = id,
            name = name,
            email = email,
            profileImg = profileImg,
            lastSeen = lastSeen,
            fcmToken = fcmToken
        )
    }

    companion object {
        fun fromUserDto(dto: UserDto): UserEntity {
            return UserEntity(
                id = dto.id,
                name = dto.name,
                email = dto.email,
                profileImg = dto.profileImg,
                lastSeen = dto.lastSeen,
                fcmToken = dto.fcmToken
            )
        }
    }
}
