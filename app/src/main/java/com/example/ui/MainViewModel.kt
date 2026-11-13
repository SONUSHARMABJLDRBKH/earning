package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.local.SupportMessageEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalEntity
import com.example.data.repository.RoyalBulletRepository
import com.example.ui.strings.AppLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiFeedback(val message: String, val isError: Boolean = false)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = RoyalBulletRepository(application)

    private val _feedback = MutableSharedFlow<UiFeedback>(extraBufferCapacity = 8)
    val feedback: SharedFlow<UiFeedback> = _feedback.asSharedFlow()

    private val _isAuthBusy = MutableStateFlow(false)
    val isAuthBusy: StateFlow<Boolean> = _isAuthBusy

    val language: StateFlow<AppLanguage> = repo.languageCodeFlow
        .map { code -> if (code == "hi") AppLanguage.HINDI else AppLanguage.ENGLISH }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val currentUserId: StateFlow<String?> = repo.currentUserIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUser: StateFlow<UserEntity?> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(null) else repo.observeUser(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeProducts: StateFlow<List<ProductEntity>> = repo.observeActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val membershipPlans: StateFlow<List<MembershipPlanEntity>> = repo.observeMembershipPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appConfig: StateFlow<AppConfigEntity> = repo.observeAppConfig()
        .map { it ?: AppConfigEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    val companyNews: StateFlow<List<CompanyNewsEntity>> = repo.observeCompanyNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserOrders(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSubscriptions: StateFlow<List<MembershipSubscriptionEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserSubscriptions(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRecharges: StateFlow<List<RechargeEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserRecharges(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWithdrawals: StateFlow<List<WithdrawalEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserWithdrawals(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userBankCards: StateFlow<List<BankCardEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserBankCards(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userReferrals: StateFlow<List<ReferralRecordEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserReferrals(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userLedger: StateFlow<List<LedgerEntryEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserLedger(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSupportMessages: StateFlow<List<SupportMessageEntity>> = currentUserId
        .flatMapLatest { uid -> if (uid == null) flowOf(emptyList()) else repo.observeUserSupportMessages(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin streams (active when signed in)
    val allUsers: StateFlow<List<UserEntity>> = repo.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allProducts: StateFlow<List<ProductEntity>> = repo.observeAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allOrders: StateFlow<List<OrderEntity>> = repo.observeAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRecharges: StateFlow<List<RechargeEntity>> = repo.observeAllRecharges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allWithdrawals: StateFlow<List<WithdrawalEntity>> = repo.observeAllWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allReferrals: StateFlow<List<ReferralRecordEntity>> = repo.observeAllReferrals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLedger: StateFlow<List<LedgerEntryEntity>> = repo.observeAllLedger()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSupportMessages: StateFlow<List<SupportMessageEntity>> = repo.observeAllSupportMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSubscriptions: StateFlow<List<MembershipSubscriptionEntity>> = repo.observeAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
        }
    }

    private fun emitMsg(msg: String, isError: Boolean = false) {
        _feedback.tryEmit(UiFeedback(msg, isError))
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val next = if (language.value == AppLanguage.ENGLISH) "hi" else "en"
            repo.setLanguageCode(next)
        }
    }

    fun register(
        mobile: String,
        loginPassword: String,
        withdrawalPassword: String,
        inviteCode: String,
        termsAccepted: Boolean
    ) {
        if (!termsAccepted) {
            emitMsg("Please accept the Terms & Conditions to register.", isError = true)
            return
        }
        viewModelScope.launch {
            _isAuthBusy.value = true
            repo.registerUser(mobile, loginPassword, withdrawalPassword, inviteCode)
                .onSuccess { emitMsg("Welcome to Royal Bullet 350!") }
                .onFailure { emitMsg(it.message ?: "Registration failed.", isError = true) }
            _isAuthBusy.value = false
        }
    }

    fun login(mobile: String, password: String) {
        viewModelScope.launch {
            _isAuthBusy.value = true
            repo.loginUser(mobile, password)
                .onSuccess { emitMsg("Signed in successfully.") }
                .onFailure { emitMsg(it.message ?: "Login failed.", isError = true) }
            _isAuthBusy.value = false
        }
    }

    fun resetPassword(mobile: String, withdrawalPassword: String, newLoginPassword: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.resetPassword(mobile, withdrawalPassword, newLoginPassword)
                .onSuccess {
                    emitMsg("Password updated. You can now log in.")
                    onDone()
                }
                .onFailure { emitMsg(it.message ?: "Reset failed.", isError = true) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }

    fun claimWelcomeBonus() {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.claimWelcomeBonus(uid)
                .onSuccess { emitMsg("₹120 Welcome Bonus credited to your Account Balance!") }
                .onFailure { emitMsg(it.message ?: "Unable to claim bonus.", isError = true) }
        }
    }

    fun dismissWelcomePopup() {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch { repo.markWelcomePopupSeen(uid) }
    }

    fun placeOrder(
        productId: String,
        shippingAddress: String,
        applyPromoCredit: Boolean,
        onSuccess: (OrderEntity) -> Unit
    ) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.placeProductOrder(uid, productId, shippingAddress, applyPromoCredit)
                .onSuccess { order ->
                    emitMsg("Order ${order.id} placed! Awaiting dispatch & fulfillment.")
                    onSuccess(order)
                }
                .onFailure { emitMsg(it.message ?: "Order failed.", isError = true) }
        }
    }

    fun activateMembership(planId: String) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.activateMembership(uid, planId)
                .onSuccess { sub -> emitMsg("${sub.planName} activated successfully!") }
                .onFailure { emitMsg(it.message ?: "Activation failed.", isError = true) }
        }
    }

    fun submitRecharge(amount: Double, method: String, referenceId: String, onSuccess: () -> Unit) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.submitRechargeRequest(uid, amount, method, referenceId)
                .onSuccess { rec ->
                    emitMsg("Recharge ${rec.id} submitted as Pending Verification. Balance credits after server/admin UTR check.")
                    onSuccess()
                }
                .onFailure { emitMsg(it.message ?: "Recharge submission failed.", isError = true) }
        }
    }

    fun submitWithdrawal(
        amount: Double,
        payoutMethod: String,
        upiId: String,
        bankAccountNumber: String,
        accountHolderName: String,
        ifscCode: String,
        withdrawalPassword: String,
        onSuccess: () -> Unit
    ) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.submitWithdrawalRequest(
                uid, amount, payoutMethod, upiId, bankAccountNumber,
                accountHolderName, ifscCode, withdrawalPassword
            )
                .onSuccess { w ->
                    emitMsg("Withdrawal ${w.id} submitted (Pending review).")
                    onSuccess()
                }
                .onFailure { emitMsg(it.message ?: "Withdrawal failed.", isError = true) }
        }
    }

    fun saveBankCard(
        holderName: String,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        upiId: String,
        onSuccess: () -> Unit
    ) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.saveBankCard(uid, holderName, bankName, accountNumber, ifscCode, upiId)
                .onSuccess {
                    emitMsg("Bank / UPI details saved.")
                    onSuccess()
                }
                .onFailure { emitMsg(it.message ?: "Failed to save card.", isError = true) }
        }
    }

    fun deleteBankCard(cardId: String) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch { repo.deleteBankCard(cardId, uid) }
    }

    fun submitSupport(subject: String, message: String, onSuccess: () -> Unit) {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.submitSupportMessage(uid, subject, message)
                .onSuccess {
                    emitMsg("Support ticket sent.")
                    onSuccess()
                }
                .onFailure { emitMsg(it.message ?: "Failed to send message.", isError = true) }
        }
    }

    // Admin actions
    fun adminVerifyRecharge(rechargeId: String, approve: Boolean, note: String = "") {
        viewModelScope.launch {
            repo.adminVerifyRecharge(rechargeId, approve, note)
                .onSuccess {
                    emitMsg(if (approve) "Recharge verified & credited." else "Recharge marked Failed.")
                }
                .onFailure { emitMsg(it.message ?: "Action failed.", isError = true) }
        }
    }

    fun adminFulfillOrder(orderId: String) {
        viewModelScope.launch {
            repo.adminFulfillOrder(orderId)
                .onSuccess { emitMsg("Order fulfilled & referral eligibility evaluated.") }
                .onFailure { emitMsg(it.message ?: "Action failed.", isError = true) }
        }
    }

    fun adminProcessWithdrawal(withdrawalId: String, status: String, note: String = "") {
        viewModelScope.launch {
            repo.adminProcessWithdrawal(withdrawalId, status, note)
                .onSuccess { emitMsg("Withdrawal updated to $status.") }
                .onFailure { emitMsg(it.message ?: "Action failed.", isError = true) }
        }
    }

    fun adminSaveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repo.adminSaveProduct(product)
            emitMsg("Product '${product.name}' saved.")
        }
    }

    fun adminUpdateConfig(telegramUrl: String, upiId: String, appInstallUrl: String = "") {
        viewModelScope.launch {
            repo.adminUpdateConfig(telegramUrl, upiId, appInstallUrl)
            emitMsg("Configuration updated.")
        }
    }

    fun adminPublishNews(title: String, summary: String, body: String, pinned: Boolean) {
        viewModelScope.launch {
            repo.adminPublishNews(title, summary, body, pinned)
            emitMsg("Company news published.")
        }
    }

    fun adminReplySupport(message: SupportMessageEntity, reply: String) {
        viewModelScope.launch {
            repo.adminUpdateSupportMessage(
                message.copy(adminReply = reply.trim(), status = "Replied")
            )
            emitMsg("Support reply sent.")
        }
    }

    fun adminToggleBlockUser(user: UserEntity) {
        viewModelScope.launch {
            repo.adminToggleBlockUser(user)
            emitMsg(if (user.isBlocked) "User unblocked." else "User blocked.")
        }
    }

    fun adminRejectReferral(ref: ReferralRecordEntity) {
        viewModelScope.launch {
            repo.adminRejectReferral(ref)
            emitMsg("Referral marked rejected.")
        }
    }
}
