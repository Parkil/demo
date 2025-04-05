package com.linguatech.demo.repo

import com.linguatech.demo.entity.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepo : JpaRepository<Company, Long>