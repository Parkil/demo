package com.linguatech.demo.repo

import com.linguatech.demo.entity.UsageLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface UsageLogRepo : JpaRepository<UsageLog, Long>, JpaSpecificationExecutor<UsageLog> {
    @Query("SELECT count(1) FROM UsageLog a " +
            "WHERE a.companyId=:companyId" +
            " AND a.featureCode=:featureCode" +
            " AND a.regDtm >= :baseDateTime")
    fun findUsageLogCount(companyId: Long, featureCode: String, baseDateTime: LocalDateTime): Int

    @Query("SELECT count(1) FROM UsageLog a " +
            "WHERE a.companyId=:companyId" +
            " AND a.featureCode=:featureCode")
    fun findUsageLogCount(companyId: Long, featureCode: String): Int
}