package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val mobile: String,
    val loginPasswordHash: String,
    val withdrawalPasswordHash: String,
    val salt: String,
    val inviteCode: String,
    val referredByUserId: String? = null,
    val referredByCode: String? = null,
    val role: String = "USER", // "USER" or "ADMIN"
    val vipTier: Int = 0, // 0 = Standard, 1..5 = VIP1..VIP5
    val availableBalance: Double = 0.0, // Eligible withdrawable earnings (e.g. verified ₹85 referral rewards, cashback)
    val promotionalBalance: Double = 0.0, // Promotional credits (₹120 welcome + check-in), product-discount eligible only
    val depositBalance: Double = 0.0, // Verified recharge deposits used for orders & VIP plans
    val welcomeBonusClaimed: Boolean = false,
    val hasSeenWelcomePopup: Boolean = false,
    val lastCheckInDate: String? = null, // YYYY-MM-DD
    val checkInStreak: Int = 0,
    val isBlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val dailyEarning: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val cycleDays: Int = 30,
    val description: String,
    val specifications: String,
    val imageKey: String, // "re_black_bike", "re_scooty", "re_hunter", "hero", or content:// URI
    val fulfillmentNote: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val productImageKey: String,
    val productPrice: Double,
    val promoCreditApplied: Double,
    val paidFromBalance: Double,
    val shippingAddress: String,
    val status: String, // "Pending Verification", "Paid", "Fulfilled", "Cancelled"
    val trackingCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fulfilledAt: Long? = null
)

@Entity(tableName = "membership_plans")
data class MembershipPlanEntity(
    @PrimaryKey val id: String,
    val tierLevel: Int,
    val name: String,
    val price: Double,
    val dailyEarning: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val validityDays: Int,
    val benefits: String,
    val eligibilityCondition: String,
    val orderDiscountPercent: Int,
    val imageKey: String = "car_thar",
    val isActive: Boolean = true
)

@Entity(tableName = "membership_subscriptions")
data class MembershipSubscriptionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val planId: String,
    val planName: String,
    val tierLevel: Int,
    val price: Double,
    val status: String, // "Active", "Expired", "Cancelled"
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long
)

@Entity(tableName = "recharges")
data class RechargeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userMobile: String,
    val amount: Double,
    val method: String, // "UPI", "PhonePe", "Google Pay", "QR Code", "Bank Transfer"
    val referenceId: String,
    val status: String, // "Pending", "Successful", "Failed"
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userMobile: String,
    val amount: Double,
    val feeAmount: Double,
    val netAmount: Double,
    val payoutMethod: String, // "UPI" or "Bank"
    val upiId: String,
    val bankAccountNumber: String,
    val accountHolderName: String,
    val ifscCode: String,
    val status: String, // "Pending", "Approved", "Rejected", "Paid"
    val adminNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "bank_cards")
data class BankCardEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val accountHolderName: String,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val upiId: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "referral_records")
data class ReferralRecordEntity(
    @PrimaryKey val id: String,
    val referrerUserId: String,
    val referredUserId: String,
    val referredMobileMasked: String,
    val referrerQualified: Boolean = false,
    val referredQualified: Boolean = false,
    val rewardAmount: Double = 85.0,
    val status: String, // "Registered", "Awaiting Verification", "Reward Paid", "Rejected"
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)

@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // "WELCOME_PROMO", "CHECK_IN_PROMO", "RECHARGE", "ORDER_PAYMENT", "MEMBERSHIP", "REFERRAL_REWARD", "CASHBACK", "WITHDRAWAL", "WITHDRAWAL_REFUND"
    val balanceBucket: String, // "AVAILABLE", "PROMOTIONAL", "DEPOSIT"
    val amount: Double,
    val title: String,
    val description: String,
    val referenceId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "company_news")
data class CompanyNewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val isPinned: Boolean = false,
    val publishedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userMobile: String,
    val subject: String,
    val message: String,
    val adminReply: String = "",
    val status: String = "Open", // "Open", "Replied", "Resolved"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val telegramUrl: String = "https://t.me/royalbullet350official",
    val upiMerchantId: String = "royalbullet350@upi",
    val bankBeneficiaryName: String = "Royal Bullet 350 Motors Pvt Ltd",
    val bankAccountNumber: String = "50200084910350",
    val bankIfscCode: String = "HDFC0001350",
    val supportHours: String = "Mon-Sat, 9:00 AM - 8:00 PM IST",
    val firebaseConfigured: Boolean = true,
    val appInstallUrl: String = "https://github.com/SONUSHARMABJLDRBKH/powerbank/releases/latest"
)
