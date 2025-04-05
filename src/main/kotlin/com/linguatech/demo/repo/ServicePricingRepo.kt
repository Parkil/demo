package com.linguatech.demo.repo

import com.linguatech.demo.entity.ServicePricing
import org.springframework.data.jpa.repository.JpaRepository

interface ServicePricingRepo : JpaRepository<ServicePricing, Long>