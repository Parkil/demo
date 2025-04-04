package com.linguatech.demo.config.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.servlet.HandlerInterceptor

@Component
class FeatureInterceptor(
    val transactionManager: PlatformTransactionManager
) : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    private val transactionStatusHolder: ThreadLocal<TransactionStatus> = ThreadLocal<TransactionStatus>()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Start a transaction
        val transactionStatus = transactionManager.getTransaction(DefaultTransactionDefinition())
        transactionStatusHolder.set(transactionStatus)

        // Bind transaction to the current thread
        TransactionSynchronizationManager.bindResource(transactionManager, transactionStatus)

        log.info("Transaction started: {}", transactionStatus)

        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val transactionStatus = transactionStatusHolder.get()
        if (transactionStatus != null) {
            try {
                if (ex == null) {
                    log.info("Committing transaction: {}", transactionStatus)
                    transactionManager.commit(transactionStatus)
                } else {
                    log.info("Rolling back transaction due to exception: {}", ex.message)
                    transactionManager.rollback(transactionStatus)
                }
            } finally {
                transactionStatusHolder.remove()
                TransactionSynchronizationManager.unbindResource(transactionManager)
            }
        }
    }
}
