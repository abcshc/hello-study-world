package com.flab.hsw.core.jdbc.content.repository

import com.flab.hsw.core.domain.content.command.CreateContentCommand
import com.flab.hsw.core.domain.content.query.Content
import com.flab.hsw.core.domain.content.repository.ContentRepository
import com.flab.hsw.core.domain.user.exception.UserByIdNotFoundException
import com.flab.hsw.core.jdbc.content.ContentEntity
import com.flab.hsw.core.jdbc.content.dao.ContentEntityDao
import com.flab.hsw.core.jdbc.user.UserEntity
import com.flab.hsw.core.jdbc.user.dao.UserEntityDao
import org.springframework.stereotype.Service
import java.util.*

@Service
internal class ContentRepositoryImpl(
        private val contentEntityDao: ContentEntityDao,
        private val userEntityDao: UserEntityDao
) : ContentRepository {
    override fun create(createContent: CreateContentCommand): Content {
        val provider = userEntityDao.selectByUuid(createContent.providerUserId)
                ?: throw UserByIdNotFoundException(createContent.providerUserId)

        return contentEntityDao.insert(ContentEntity.from(createContent, provider.seq))
                .toContent(provider)
    }

    private fun ContentEntity.toContent(providerUserEntity: UserEntity): Content {
        return Content.create(
                id = seq,
                url = url,
                description = description,
                provider = providerUserEntity.toUserProfile(),
                registeredAt = registeredAt,
                lastUpdateAt = lastActiveAt,
        )
    }

    override fun findByUuid(uuid: UUID): Content? {
        TODO("Not yet implemented")
    }
}
