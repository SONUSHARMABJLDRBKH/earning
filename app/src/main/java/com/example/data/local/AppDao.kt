package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?

    @Query("SELECT * FROM users WHERE inviteCode = :inviteCode LIMIT 1")
    suspend fun getUserByInviteCode(inviteCode: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    // Products
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY price ASC")
    fun observeActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY price ASC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProductsIfEmpty(products: List<ProductEntity>)

    // Orders
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserOrders(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId AND status = 'Fulfilled'")
    suspend fun countFulfilledOrdersForUser(userId: String): Int

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: OrderEntity)

    // Memberships
    @Query("SELECT * FROM membership_plans WHERE isActive = 1 ORDER BY price ASC")
    fun observeMembershipPlans(): Flow<List<MembershipPlanEntity>>

    @Query("SELECT * FROM membership_plans ORDER BY price ASC")
    fun observeAllMembershipPlans(): Flow<List<MembershipPlanEntity>>

    @Query("SELECT * FROM membership_plans WHERE id = :planId LIMIT 1")
    suspend fun getMembershipPlanById(planId: String): MembershipPlanEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlansIfEmpty(plans: List<MembershipPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlan(plan: MembershipPlanEntity)

    @Query("SELECT * FROM membership_subscriptions WHERE userId = :userId ORDER BY activatedAt DESC")
    fun observeUserSubscriptions(userId: String): Flow<List<MembershipSubscriptionEntity>>

    @Query("SELECT * FROM membership_subscriptions ORDER BY activatedAt DESC")
    fun observeAllSubscriptions(): Flow<List<MembershipSubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubscription(subscription: MembershipSubscriptionEntity)

    // Recharges
    @Query("SELECT * FROM recharges WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserRecharges(userId: String): Flow<List<RechargeEntity>>

    @Query("SELECT * FROM recharges ORDER BY createdAt DESC")
    fun observeAllRecharges(): Flow<List<RechargeEntity>>

    @Query("SELECT * FROM recharges WHERE referenceId = :referenceId LIMIT 1")
    suspend fun getRechargeByReference(referenceId: String): RechargeEntity?

    @Query("SELECT * FROM recharges WHERE id = :id LIMIT 1")
    suspend fun getRechargeById(id: String): RechargeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecharge(recharge: RechargeEntity)

    // Withdrawals
    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC")
    fun observeAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalById(id: String): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWithdrawal(withdrawal: WithdrawalEntity)

    // Bank cards
    @Query("SELECT * FROM bank_cards WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserBankCards(userId: String): Flow<List<BankCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBankCard(bankCard: BankCardEntity)

    @Query("DELETE FROM bank_cards WHERE id = :id AND userId = :userId")
    suspend fun deleteBankCard(id: String, userId: String)

    // Referrals
    @Query("SELECT * FROM referral_records WHERE referrerUserId = :userId ORDER BY createdAt DESC")
    fun observeUserReferrals(userId: String): Flow<List<ReferralRecordEntity>>

    @Query("SELECT * FROM referral_records ORDER BY createdAt DESC")
    fun observeAllReferrals(): Flow<List<ReferralRecordEntity>>

    @Query("SELECT * FROM referral_records WHERE referredUserId = :referredUserId LIMIT 1")
    suspend fun getReferralByReferredUser(referredUserId: String): ReferralRecordEntity?

    @Query("SELECT * FROM referral_records WHERE referrerUserId = :referrerUserId")
    suspend fun getReferralsForReferrer(referrerUserId: String): List<ReferralRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReferral(referral: ReferralRecordEntity)

    // Ledger
    @Query("SELECT * FROM ledger_entries WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserLedger(userId: String): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM ledger_entries ORDER BY createdAt DESC")
    fun observeAllLedger(): Flow<List<LedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity)

    // Company News
    @Query("SELECT * FROM company_news ORDER BY isPinned DESC, publishedAt DESC")
    fun observeCompanyNews(): Flow<List<CompanyNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsIfEmpty(news: List<CompanyNewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNews(news: CompanyNewsEntity)

    // Support Messages
    @Query("SELECT * FROM support_messages WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserSupportMessages(userId: String): Flow<List<SupportMessageEntity>>

    @Query("SELECT * FROM support_messages ORDER BY createdAt DESC")
    fun observeAllSupportMessages(): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSupportMessage(message: SupportMessageEntity)

    // App Config
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun observeAppConfig(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    suspend fun getAppConfig(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppConfig(config: AppConfigEntity)
}
