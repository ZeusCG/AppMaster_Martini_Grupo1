package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ProfileDao
import com.example.appmaster_martini_grupo1.data.model.Profile
import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val profileDao: ProfileDao
) {

    suspend fun getByUserId(userId: Long): Profile? = profileDao.getByUserId(userId)

    fun observeByUserId(userId: Long): Flow<Profile?> = profileDao.observeByUserId(userId)

    suspend fun save(profile: Profile) = profileDao.upsert(profile)
}
