package org.example.ktij31341

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface AccountModelRepository : CoroutineCrudRepository<AccountModel, UUID> {

    @Query(
        """
        SELECT
            acc.*
        FROM
            main.account AS acc
        WHERE
            acc.number = :accountNumber
            AND org.short_name = :issuerName
        """
    )
    suspend fun findByAccountNumberAndIssuerName(
        accountNumber: String,
        issuerName: Name,
    ): AccountModel?

}
