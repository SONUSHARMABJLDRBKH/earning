package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.withTransaction
import com.example.data.local.AppConfigEntity
import com.example.data.local.BankCardEntity
import com.example.data.local.CompanyNewsEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.MembershipPlanEntity
import com.example.data.local.MembershipSubscriptionEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.RechargeEntity
import com.example.data.local.ReferralRecordEntity
import com.example.data.local.RoyalBulletDatabase
import com.example.data.local.SupportMessageEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import java.util.UUID
import kotlin.math.min
import kotlin.random.Random

private val Context.sessionDataStore by preferencesDataStore(name = "royal_bullet_session")

class RoyalBulletRepository(private val context: Context) {
    private val db = RoyalBulletDatabase.getInstance(context)
    private val dao = db.appDao()

    companion object {
        private val KEY_CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val KEY_LANGUAGE_CODE = stringPreferencesKey("language_code")
        const val MIN_RECHARGE = 700.0
        const val MAX_RECHARGE = 100000.0
        const val MIN_WITHDRAWAL = 150.0
        const val WITHDRAWAL_FEE_PERCENT = 0.02
        const val WELCOME_PROMO_AMOUNT = 120.0
        const val REFERRAL_REWARD_AMOUNT = 85.0
    }

    val currentUserIdFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_CURRENT_USER_ID]
    }

    val languageCodeFlow: Flow<String> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE_CODE] ?: "en"
    }

    suspend fun setLanguageCode(code: String) {
        context.sessionDataStore.edit { it[KEY_LANGUAGE_CODE] = code }
    }

    fun observeUser(userId: String): Flow<UserEntity?> = dao.observeUser(userId)
    fun observeActiveProducts(): Flow<List<ProductEntity>> = dao.observeActiveProducts()
    fun observeAllProducts(): Flow<List<ProductEntity>> = dao.observeAllProducts()
    fun observeUserOrders(userId: String): Flow<List<OrderEntity>> = dao.observeUserOrders(userId)
    fun observeAllOrders(): Flow<List<OrderEntity>> = dao.observeAllOrders()
    fun observeMembershipPlans(): Flow<List<MembershipPlanEntity>> = dao.observeMembershipPlans()
    fun observeUserSubscriptions(userId: String): Flow<List<MembershipSubscriptionEntity>> =
        dao.observeUserSubscriptions(userId)

    fun observeAllSubscriptions(): Flow<List<MembershipSubscriptionEntity>> =
        dao.observeAllSubscriptions()

    fun observeUserRecharges(userId: String): Flow<List<RechargeEntity>> =
        dao.observeUserRecharges(userId)

    fun observeAllRecharges(): Flow<List<RechargeEntity>> = dao.observeAllRecharges()
    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalEntity>> =
        dao.observeUserWithdrawals(userId)

    fun observeAllWithdrawals(): Flow<List<WithdrawalEntity>> = dao.observeAllWithdrawals()
    fun observeUserBankCards(userId: String): Flow<List<BankCardEntity>> =
        dao.observeUserBankCards(userId)

    fun observeUserReferrals(userId: String): Flow<List<ReferralRecordEntity>> =
        dao.observeUserReferrals(userId)

    fun observeAllReferrals(): Flow<List<ReferralRecordEntity>> = dao.observeAllReferrals()
    fun observeUserLedger(userId: String): Flow<List<LedgerEntryEntity>> =
        dao.observeUserLedger(userId)

    fun observeAllLedger(): Flow<List<LedgerEntryEntity>> = dao.observeAllLedger()
    fun observeCompanyNews(): Flow<List<CompanyNewsEntity>> = dao.observeCompanyNews()
    fun observeUserSupportMessages(userId: String): Flow<List<SupportMessageEntity>> =
        dao.observeUserSupportMessages(userId)

    fun observeAllSupportMessages(): Flow<List<SupportMessageEntity>> =
        dao.observeAllSupportMessages()

    fun observeAllUsers(): Flow<List<UserEntity>> = dao.observeAllUsers()
    fun observeAppConfig(): Flow<AppConfigEntity?> = dao.observeAppConfig()

    suspend fun ensureSeeded() {
        if (dao.getAppConfig() == null) {
            RoyalBulletDatabase.seedInitialCatalog(dao)
        }
    }

    private fun normalizeMobile(raw: String): String =
        raw.filter { it.isDigit() }.takeLast(10)

    private fun maskMobile(mobile: String): String {
        val clean = normalizeMobile(mobile)
        return if (clean.length == 10) {
            "${clean.take(3)}***${clean.takeLast(4)}"
        } else clean
    }

    private fun newId(prefix: String): String =
        "${prefix}_${UUID.randomUUID().toString().take(8)}"

    suspend fun registerUser(
        mobileInput: String,
        loginPassword: String,
        withdrawalPassword: String,
        inviteCodeInput: String
    ): Result<UserEntity> {
        ensureSeeded()
        val mobile = normalizeMobile(mobileInput)
        if (mobile.length != 10) {
            return Result.failure(IllegalArgumentException("Enter a valid 10-digit +91 mobile number."))
        }
        if (loginPassword.length < 6) {
            return Result.failure(IllegalArgumentException("Login password must be at least 6 characters."))
        }
        if (withdrawalPassword.length < 4) {
            return Result.failure(IllegalArgumentException("Withdrawal password must be at least 4 characters."))
        }
        if (dao.getUserByMobile(mobile) != null) {
            return Result.failure(IllegalArgumentException("An account with +91 $mobile already exists."))
        }

        val cleanInvite = inviteCodeInput.trim().uppercase(Locale.US)
        var referrer: UserEntity? = null
        if (cleanInvite.isNotEmpty()) {
            referrer = dao.getUserByInviteCode(cleanInvite)
                ?: return Result.failure(IllegalArgumentException("Invitation code '$cleanInvite' was not found."))
        }

        val salt = UUID.randomUUID().toString().take(12)
        val numericId = Random.nextInt(10000, 99999)
        val userId = "RB$numericId"
        val userInviteCode = "RB${UUID.randomUUID().toString().take(4).uppercase(Locale.US)}"

        val newUser = UserEntity(
            id = userId,
            mobile = mobile,
            loginPasswordHash = RoyalBulletDatabase.hashPassword(loginPassword, salt),
            withdrawalPasswordHash = RoyalBulletDatabase.hashPassword(withdrawalPassword, salt),
            salt = salt,
            inviteCode = userInviteCode,
            referredByUserId = referrer?.id,
            referredByCode = referrer?.inviteCode,
            role = "USER",
            availableBalance = WELCOME_PROMO_AMOUNT,
            welcomeBonusClaimed = true
        )

        db.withTransaction {
            dao.upsertUser(newUser)
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = newUser.id,
                    type = "WELCOME_PROMO",
                    balanceBucket = "AVAILABLE",
                    amount = WELCOME_PROMO_AMOUNT,
                    title = "₹120 Welcome Bonus",
                    description = "Instant ₹120 welcome bonus credited to your Account Balance.",
                    referenceId = "WELCOME_${newUser.id}"
                )
            )
            if (referrer != null) {
                dao.upsertReferral(
                    ReferralRecordEntity(
                        id = newId("ref"),
                        referrerUserId = referrer.id,
                        referredUserId = newUser.id,
                        referredMobileMasked = maskMobile(newUser.mobile),
                        referrerQualified = dao.countFulfilledOrdersForUser(referrer.id) > 0,
                        referredQualified = false,
                        rewardAmount = REFERRAL_REWARD_AMOUNT,
                        status = "Registered"
                    )
                )
            }
        }

        context.sessionDataStore.edit { it[KEY_CURRENT_USER_ID] = newUser.id }
        return Result.success(newUser)
    }

    suspend fun loginUser(mobileInput: String, password: String): Result<UserEntity> {
        ensureSeeded()
        val mobile = normalizeMobile(mobileInput)
        val user = dao.getUserByMobile(mobile)
            ?: return Result.failure(IllegalArgumentException("No account found for +91 $mobile."))
        if (user.isBlocked) {
            return Result.failure(IllegalStateException("This account is restricted. Contact Customer Support."))
        }
        val expectedHash = RoyalBulletDatabase.hashPassword(password, user.salt)
        if (expectedHash != user.loginPasswordHash) {
            return Result.failure(IllegalArgumentException("Incorrect login password."))
        }
        var signedInUser = user
        if (user.role != "ADMIN" && (!user.welcomeBonusClaimed || user.promotionalBalance > 0.0)) {
            db.withTransaction {
                val fresh = dao.getUserById(user.id) ?: return@withTransaction
                val bonusToMove = fresh.promotionalBalance + if (!fresh.welcomeBonusClaimed) WELCOME_PROMO_AMOUNT else 0.0
                if (bonusToMove > 0.0 || !fresh.welcomeBonusClaimed) {
                    signedInUser = fresh.copy(
                        availableBalance = fresh.availableBalance + bonusToMove,
                        promotionalBalance = 0.0,
                        welcomeBonusClaimed = true
                    )
                    dao.upsertUser(signedInUser)
                    if (!fresh.welcomeBonusClaimed) {
                        dao.insertLedgerEntry(
                            LedgerEntryEntity(
                                id = newId("led"),
                                userId = fresh.id,
                                type = "WELCOME_PROMO",
                                balanceBucket = "AVAILABLE",
                                amount = WELCOME_PROMO_AMOUNT,
                                title = "₹120 Welcome Bonus",
                                description = "₹120 welcome bonus credited to your Account Balance.",
                                referenceId = "WELCOME_${fresh.id}"
                            )
                        )
                    }
                }
            }
        }
        context.sessionDataStore.edit { it[KEY_CURRENT_USER_ID] = signedInUser.id }
        return Result.success(signedInUser)
    }

    suspend fun resetPassword(
        mobileInput: String,
        withdrawalPassword: String,
        newLoginPassword: String
    ): Result<Unit> {
        val mobile = normalizeMobile(mobileInput)
        val user = dao.getUserByMobile(mobile)
            ?: return Result.failure(IllegalArgumentException("No account found for +91 $mobile."))
        val withdrawHash = RoyalBulletDatabase.hashPassword(withdrawalPassword, user.salt)
        if (withdrawHash != user.withdrawalPasswordHash) {
            return Result.failure(IllegalArgumentException("Withdrawal password verification failed."))
        }
        if (newLoginPassword.length < 6) {
            return Result.failure(IllegalArgumentException("New password must be at least 6 characters."))
        }
        dao.upsertUser(
            user.copy(
                loginPasswordHash = RoyalBulletDatabase.hashPassword(newLoginPassword, user.salt)
            )
        )
        return Result.success(Unit)
    }

    suspend fun logout() {
        context.sessionDataStore.edit { it.remove(KEY_CURRENT_USER_ID) }
    }

    suspend fun claimWelcomeBonus(userId: String): Result<Double> {
        return db.withTransaction {
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            if (user.welcomeBonusClaimed) {
                return@withTransaction Result.failure(IllegalStateException("Welcome bonus already claimed."))
            }
            val updated = user.copy(
                availableBalance = user.availableBalance + WELCOME_PROMO_AMOUNT,
                welcomeBonusClaimed = true
            )
            dao.upsertUser(updated)
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = user.id,
                    type = "WELCOME_PROMO",
                    balanceBucket = "AVAILABLE",
                    amount = WELCOME_PROMO_AMOUNT,
                    title = "₹120 Welcome Bonus",
                    description = "One-time ₹120 welcome bonus credited to your Account Balance.",
                    referenceId = "WELCOME_${user.id}"
                )
            )
            Result.success(WELCOME_PROMO_AMOUNT)
        }
    }

    suspend fun markWelcomePopupSeen(userId: String) {
        val user = dao.getUserById(userId) ?: return
        if (!user.hasSeenWelcomePopup) {
            dao.upsertUser(user.copy(hasSeenWelcomePopup = true))
        }
    }

    suspend fun placeProductOrder(
        userId: String,
        productId: String,
        shippingAddress: String,
        applyPromoCredit: Boolean
    ): Result<OrderEntity> {
        if (shippingAddress.trim().length < 8) {
            return Result.failure(IllegalArgumentException("Please enter a complete delivery address."))
        }
        return db.withTransaction {
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val product = dao.getProductById(productId)
                ?: return@withTransaction Result.failure(IllegalStateException("Product not found"))

            val maxPromoDiscount = product.price * 0.10 // Transparent 10% promotional credit cap per order
            val promoUsed = if (applyPromoCredit && user.promotionalBalance > 0.0) {
                min(user.promotionalBalance, maxPromoDiscount)
            } else 0.0
            val netPayable = product.price - promoUsed

            val spendableTotal = user.depositBalance + user.availableBalance
            if (spendableTotal < netPayable) {
                return@withTransaction Result.failure(
                    IllegalStateException(
                        "Insufficient verified balance (Need ₹${"%.2f".format(netPayable)}). Please Recharge first."
                    )
                )
            }

            val fromDeposit = min(user.depositBalance, netPayable)
            val fromAvailable = netPayable - fromDeposit

            dao.upsertUser(
                user.copy(
                    depositBalance = user.depositBalance - fromDeposit,
                    availableBalance = user.availableBalance - fromAvailable,
                    promotionalBalance = user.promotionalBalance - promoUsed
                )
            )

            val order = OrderEntity(
                id = newId("ORD"),
                userId = user.id,
                productId = product.id,
                productName = product.name,
                productImageKey = product.imageKey,
                productPrice = product.price,
                promoCreditApplied = promoUsed,
                paidFromBalance = netPayable,
                shippingAddress = shippingAddress.trim(),
                status = "Paid",
                trackingCode = "RB-TRK-${Random.nextInt(100000, 999999)}"
            )
            dao.upsertOrder(order)

            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = user.id,
                    type = "ORDER_PAYMENT",
                    balanceBucket = if (fromDeposit > 0) "DEPOSIT" else "AVAILABLE",
                    amount = -netPayable,
                    title = "Order: ${product.name}",
                    description = "Paid ₹${"%.2f".format(netPayable)} (Promo credit applied: ₹${"%.2f".format(promoUsed)})",
                    referenceId = order.id
                )
            )

            Result.success(order)
        }
    }

    suspend fun activateMembership(userId: String, planId: String): Result<MembershipSubscriptionEntity> {
        return db.withTransaction {
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val plan = dao.getMembershipPlanById(planId)
                ?: return@withTransaction Result.failure(IllegalStateException("Membership plan not found"))

            val spendable = user.depositBalance + user.availableBalance
            if (spendable < plan.price) {
                return@withTransaction Result.failure(
                    IllegalStateException("Insufficient verified balance (₹${"%.2f".format(plan.price)} required). Please Recharge.")
                )
            }
            val fromDeposit = min(user.depositBalance, plan.price)
            val fromAvailable = plan.price - fromDeposit

            val now = System.currentTimeMillis()
            val expires = now + plan.validityDays * 24L * 60L * 60L * 1000L

            dao.upsertUser(
                user.copy(
                    depositBalance = user.depositBalance - fromDeposit,
                    availableBalance = user.availableBalance - fromAvailable,
                    vipTier = maxOf(user.vipTier, plan.tierLevel)
                )
            )

            val sub = MembershipSubscriptionEntity(
                id = newId("VIP"),
                userId = user.id,
                planId = plan.id,
                planName = plan.name,
                tierLevel = plan.tierLevel,
                price = plan.price,
                status = "Active",
                activatedAt = now,
                expiresAt = expires
            )
            dao.upsertSubscription(sub)

            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = user.id,
                    type = "MEMBERSHIP",
                    balanceBucket = if (fromDeposit > 0) "DEPOSIT" else "AVAILABLE",
                    amount = -plan.price,
                    title = "Activated ${plan.name}",
                    description = "Valid for ${plan.validityDays} days (${plan.orderDiscountPercent}% cashback on fulfilled product orders).",
                    referenceId = sub.id
                )
            )

            Result.success(sub)
        }
    }

    suspend fun submitRechargeRequest(
        userId: String,
        amount: Double,
        method: String,
        referenceId: String
    ): Result<RechargeEntity> {
        if (amount < MIN_RECHARGE || amount > MAX_RECHARGE) {
            return Result.failure(
                IllegalArgumentException("Recharge amount must be between ₹${MIN_RECHARGE.toInt()} and ₹${MAX_RECHARGE.toInt()}.")
            )
        }
        val cleanRef = referenceId.trim()
        if (cleanRef.length < 6) {
            return Result.failure(IllegalArgumentException("Enter a valid payment UTR / Gateway Reference ID (min 6 chars)."))
        }
        if (dao.getRechargeByReference(cleanRef) != null) {
            return Result.failure(IllegalArgumentException("Duplicate payment reference ID detected."))
        }
        val user = dao.getUserById(userId)
            ?: return Result.failure(IllegalStateException("User not found"))

        val recharge = RechargeEntity(
            id = newId("RCH"),
            userId = user.id,
            userMobile = user.mobile,
            amount = amount,
            method = method,
            referenceId = cleanRef,
            status = "Pending",
            note = "Awaiting server-side / admin UTR verification"
        )
        dao.upsertRecharge(recharge)
        return Result.success(recharge)
    }

    suspend fun submitWithdrawalRequest(
        userId: String,
        amount: Double,
        payoutMethod: String,
        upiId: String,
        bankAccountNumber: String,
        accountHolderName: String,
        ifscCode: String,
        withdrawalPassword: String
    ): Result<WithdrawalEntity> {
        if (amount < MIN_WITHDRAWAL) {
            return Result.failure(IllegalArgumentException("Minimum withdrawal amount is ₹${MIN_WITHDRAWAL.toInt()}."))
        }
        if (accountHolderName.isBlank()) {
            return Result.failure(IllegalArgumentException("Account holder name is required."))
        }
        if (payoutMethod == "UPI" && !upiId.contains("@")) {
            return Result.failure(IllegalArgumentException("Enter a valid UPI ID (e.g. name@upi)."))
        }
        if (payoutMethod == "Bank" && (bankAccountNumber.length < 9 || ifscCode.length < 8)) {
            return Result.failure(IllegalArgumentException("Enter a valid bank account number and IFSC code."))
        }

        return db.withTransaction {
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))

            val passHash = RoyalBulletDatabase.hashPassword(withdrawalPassword, user.salt)
            if (passHash != user.withdrawalPasswordHash) {
                return@withTransaction Result.failure(IllegalArgumentException("Incorrect withdrawal password."))
            }

            // Only legitimately earned Available Balance can be withdrawn (NOT promotional or unspent deposit)
            if (user.availableBalance < amount) {
                return@withTransaction Result.failure(
                    IllegalStateException(
                        "Insufficient eligible Available Balance (₹${"%.2f".format(user.availableBalance)}). Promotional credit cannot be withdrawn."
                    )
                )
            }

            val fee = (amount * WITHDRAWAL_FEE_PERCENT * 100).toInt() / 100.0
            val net = amount - fee

            dao.upsertUser(user.copy(availableBalance = user.availableBalance - amount))

            val withdrawal = WithdrawalEntity(
                id = newId("WDR"),
                userId = user.id,
                userMobile = user.mobile,
                amount = amount,
                feeAmount = fee,
                netAmount = net,
                payoutMethod = payoutMethod,
                upiId = upiId.trim(),
                bankAccountNumber = bankAccountNumber.trim(),
                accountHolderName = accountHolderName.trim(),
                ifscCode = ifscCode.trim().uppercase(Locale.US),
                status = "Pending"
            )
            dao.upsertWithdrawal(withdrawal)

            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = user.id,
                    type = "WITHDRAWAL",
                    balanceBucket = "AVAILABLE",
                    amount = -amount,
                    title = "Withdrawal Request (${payoutMethod})",
                    description = "Requested ₹${"%.2f".format(amount)} • Fee ₹${"%.2f".format(fee)} • Net ₹${"%.2f".format(net)} (Pending review)",
                    referenceId = withdrawal.id
                )
            )

            Result.success(withdrawal)
        }
    }

    suspend fun saveBankCard(
        userId: String,
        holderName: String,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        upiId: String
    ): Result<Unit> {
        if (holderName.isBlank() || (accountNumber.isBlank() && upiId.isBlank())) {
            return Result.failure(IllegalArgumentException("Provide account holder name and bank or UPI details."))
        }
        dao.upsertBankCard(
            BankCardEntity(
                id = newId("BNK"),
                userId = userId,
                accountHolderName = holderName.trim(),
                bankName = bankName.trim(),
                accountNumber = accountNumber.trim(),
                ifscCode = ifscCode.trim().uppercase(Locale.US),
                upiId = upiId.trim()
            )
        )
        return Result.success(Unit)
    }

    suspend fun deleteBankCard(cardId: String, userId: String) {
        dao.deleteBankCard(cardId, userId)
    }

    suspend fun submitSupportMessage(userId: String, subject: String, message: String): Result<Unit> {
        if (subject.isBlank() || message.isBlank()) {
            return Result.failure(IllegalArgumentException("Subject and message cannot be empty."))
        }
        val user = dao.getUserById(userId)
            ?: return Result.failure(IllegalStateException("User not found"))
        dao.upsertSupportMessage(
            SupportMessageEntity(
                id = newId("SUP"),
                userId = user.id,
                userMobile = user.mobile,
                subject = subject.trim(),
                message = message.trim()
            )
        )
        return Result.success(Unit)
    }

    // ==================== ADMIN OPERATIONS ====================

    suspend fun adminVerifyRecharge(rechargeId: String, approve: Boolean, note: String): Result<Unit> {
        return db.withTransaction {
            val recharge = dao.getRechargeById(rechargeId)
                ?: return@withTransaction Result.failure(IllegalStateException("Recharge record not found"))
            if (recharge.status != "Pending") {
                return@withTransaction Result.failure(IllegalStateException("Recharge already processed."))
            }
            val now = System.currentTimeMillis()
            if (approve) {
                val user = dao.getUserById(recharge.userId)
                    ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
                dao.upsertUser(user.copy(depositBalance = user.depositBalance + recharge.amount))
                dao.upsertRecharge(
                    recharge.copy(
                        status = "Successful",
                        verifiedAt = now,
                        note = note.ifBlank { "Verified by Admin" }
                    )
                )
                dao.insertLedgerEntry(
                    LedgerEntryEntity(
                        id = newId("led"),
                        userId = user.id,
                        type = "RECHARGE",
                        balanceBucket = "DEPOSIT",
                        amount = recharge.amount,
                        title = "Verified Deposit (${recharge.method})",
                        description = "UTR ${recharge.referenceId} verified and credited to Deposit Balance.",
                        referenceId = recharge.id
                    )
                )
            } else {
                dao.upsertRecharge(
                    recharge.copy(
                        status = "Failed",
                        verifiedAt = now,
                        note = note.ifBlank { "Payment could not be verified" }
                    )
                )
            }
            Result.success(Unit)
        }
    }

    suspend fun adminFulfillOrder(orderId: String): Result<Unit> {
        return db.withTransaction {
            val order = dao.getOrderById(orderId)
                ?: return@withTransaction Result.failure(IllegalStateException("Order not found"))
            if (order.status == "Fulfilled") {
                return@withTransaction Result.success(Unit)
            }
            val now = System.currentTimeMillis()
            dao.upsertOrder(order.copy(status = "Fulfilled", fulfilledAt = now))

            val buyer = dao.getUserById(order.userId)
            if (buyer != null && buyer.vipTier > 0) {
                // Credit transparent VIP order cashback on fulfilled purchase (2% - 6% depending on tier)
                val cashbackRate = when (buyer.vipTier) {
                    1 -> 0.02
                    2 -> 0.03
                    3 -> 0.04
                    4 -> 0.05
                    else -> 0.06
                }
                val cashback = (order.paidFromBalance * cashbackRate * 100).toInt() / 100.0
                if (cashback > 0) {
                    dao.upsertUser(buyer.copy(availableBalance = buyer.availableBalance + cashback))
                    dao.insertLedgerEntry(
                        LedgerEntryEntity(
                            id = newId("led"),
                            userId = buyer.id,
                            type = "CASHBACK",
                            balanceBucket = "AVAILABLE",
                            amount = cashback,
                            title = "VIP${buyer.vipTier} Order Cashback",
                            description = "${(cashbackRate * 100).toInt()}% cashback on fulfilled order ${order.id}.",
                            referenceId = order.id
                        )
                    )
                }
            }

            // Evaluate strict 8-rule referral eligibility:
            // 1. If buyer is User B (referred by User A), update referral record referredQualified = true
            val asReferred = dao.getReferralByReferredUser(order.userId)
            if (asReferred != null && asReferred.status != "Reward Paid" && asReferred.status != "Rejected") {
                val referrerFulfilled = dao.countFulfilledOrdersForUser(asReferred.referrerUserId) > 0
                val updatedRef = asReferred.copy(
                    referredQualified = true,
                    referrerQualified = referrerFulfilled
                )
                evaluateAndPayReferralIfEligible(updatedRef, now)
            }

            // 2. If buyer is User A (referrer of User B), mark referrerQualified = true on pending referrals
            val asReferrerList = dao.getReferralsForReferrer(order.userId)
            for (ref in asReferrerList) {
                if (ref.status != "Reward Paid" && ref.status != "Rejected") {
                    val referredFulfilled = dao.countFulfilledOrdersForUser(ref.referredUserId) > 0
                    val updatedRef = ref.copy(
                        referrerQualified = true,
                        referredQualified = referredFulfilled
                    )
                    evaluateAndPayReferralIfEligible(updatedRef, now)
                }
            }

            Result.success(Unit)
        }
    }

    private suspend fun evaluateAndPayReferralIfEligible(ref: ReferralRecordEntity, now: Long) {
        if (ref.referrerQualified && ref.referredQualified && ref.status != "Reward Paid") {
            val referrer = dao.getUserById(ref.referrerUserId) ?: return
            // Anti-fraud: never reward same user or blocked user
            if (referrer.id == ref.referredUserId || referrer.isBlocked) {
                dao.upsertReferral(ref.copy(status = "Rejected"))
                return
            }
            // Credit ₹85 ONE-TIME to User A only (User B receives ₹0 referral commission)
            dao.upsertUser(
                referrer.copy(availableBalance = referrer.availableBalance + REFERRAL_REWARD_AMOUNT)
            )
            dao.upsertReferral(
                ref.copy(
                    status = "Reward Paid",
                    paidAt = now
                )
            )
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    id = newId("led"),
                    userId = referrer.id,
                    type = "REFERRAL_REWARD",
                    balanceBucket = "AVAILABLE",
                    amount = REFERRAL_REWARD_AMOUNT,
                    title = "Verified Direct Referral Reward (₹85)",
                    description = "Both you and ${ref.referredMobileMasked} completed paid & fulfilled qualifying product orders.",
                    referenceId = ref.id
                )
            )
        } else {
            dao.upsertReferral(
                ref.copy(
                    status = if (ref.referrerQualified || ref.referredQualified) {
                        "Awaiting Verification"
                    } else "Registered"
                )
            )
        }
    }

    suspend fun adminProcessWithdrawal(
        withdrawalId: String,
        newStatus: String,
        adminNote: String
    ): Result<Unit> {
        return db.withTransaction {
            val withdrawal = dao.getWithdrawalById(withdrawalId)
                ?: return@withTransaction Result.failure(IllegalStateException("Withdrawal not found"))
            val now = System.currentTimeMillis()
            if (newStatus == "Rejected" && withdrawal.status != "Rejected" && withdrawal.status != "Paid") {
                val user = dao.getUserById(withdrawal.userId)
                if (user != null) {
                    dao.upsertUser(user.copy(availableBalance = user.availableBalance + withdrawal.amount))
                    dao.insertLedgerEntry(
                        LedgerEntryEntity(
                            id = newId("led"),
                            userId = user.id,
                            type = "WITHDRAWAL_REFUND",
                            balanceBucket = "AVAILABLE",
                            amount = withdrawal.amount,
                            title = "Withdrawal Refunded (${withdrawal.id})",
                            description = adminNote.ifBlank { "Withdrawal rejected; amount returned to Available Balance." },
                            referenceId = withdrawal.id
                        )
                    )
                }
            }
            dao.upsertWithdrawal(
                withdrawal.copy(
                    status = newStatus,
                    adminNote = adminNote,
                    processedAt = now
                )
            )
            Result.success(Unit)
        }
    }

    suspend fun adminSaveProduct(product: ProductEntity) {
        dao.upsertProduct(product)
    }

    suspend fun adminUpdateConfig(
        telegramUrl: String,
        upiId: String,
        appInstallUrl: String = ""
    ) {
        val current = dao.getAppConfig() ?: AppConfigEntity()
        dao.upsertAppConfig(
            current.copy(
                telegramUrl = telegramUrl.trim().ifBlank { current.telegramUrl },
                upiMerchantId = upiId.trim().ifBlank { current.upiMerchantId },
                appInstallUrl = appInstallUrl.trim().ifBlank { current.appInstallUrl }
            )
        )
    }

    suspend fun adminPublishNews(title: String, summary: String, body: String, pinned: Boolean) {
        if (title.isBlank() || body.isBlank()) return
        dao.upsertNews(
            CompanyNewsEntity(
                id = newId("news"),
                title = title.trim(),
                summary = summary.trim().ifBlank { body.take(90) },
                body = body.trim(),
                isPinned = pinned
            )
        )
    }

    suspend fun adminUpdateSupportMessage(message: SupportMessageEntity) {
        dao.upsertSupportMessage(message)
    }

    suspend fun adminToggleBlockUser(user: UserEntity) {
        if (user.role == "ADMIN") return
        dao.upsertUser(user.copy(isBlocked = !user.isBlocked))
    }

    suspend fun adminRejectReferral(ref: ReferralRecordEntity) {
        if (ref.status == "Reward Paid") return
        dao.upsertReferral(ref.copy(status = "Rejected"))
    }
}
