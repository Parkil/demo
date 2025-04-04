package com.linguatech.demo.repo

import com.linguatech.demo.entity.FeatureInfo
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureInfoRepo : JpaRepository<FeatureInfo, String> {
}