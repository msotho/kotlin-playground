package org.example.ktij31341

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID


@Table("main.account")
data class AccountModel(
    @Id var id: UUID? = null,
    val number: String,
    val name: Name,
    val productId: UUID,
    val issuerId: UUID,
    val updatedBy: String
)
