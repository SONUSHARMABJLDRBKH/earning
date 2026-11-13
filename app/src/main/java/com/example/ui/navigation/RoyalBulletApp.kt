package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.components.FirstLoginBonusDialog
import com.example.ui.components.RoyalBottomNavBar
import com.example.ui.components.WelcomeOverviewDialog
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.gift.GiftScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.secondary.BackendConfigScreen
import com.example.ui.screens.secondary.CompanyNewsScreen
import com.example.ui.screens.secondary.CustomerSupportScreen
import com.example.ui.screens.secondary.LegalInfoScreen
import com.example.ui.screens.secondary.RecordsScreen
import com.example.ui.screens.shop.MyOrdersScreen
import com.example.ui.screens.shop.ProductDetailScreen
import com.example.ui.screens.shop.ShopTabScreen
import com.example.ui.screens.team.TeamScreen
import com.example.ui.screens.vip.VipMembershipScreen
import com.example.ui.screens.wallet.BankCardScreen
import com.example.ui.screens.wallet.RechargeScreen
import com.example.ui.screens.wallet.WithdrawalScreen
import com.example.ui.strings.LocalStrings
import com.example.ui.strings.stringsFor

object Routes {
    const val AUTH = "auth"
    const val MAIN_TABS = "main_tabs"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val MY_ORDERS = "my_orders"
    const val VIP_PLANS = "vip_plans"
    const val RECHARGE = "recharge"
    const val WITHDRAWAL = "withdrawal"
    const val BANK_CARDS = "bank_cards"
    const val COMPANY_NEWS = "company_news"
    const val RECORDS = "records/{mode}"
    const val SUPPORT = "support"
    const val ABOUT = "about"
    const val TERMS = "terms"
    const val PRIVACY = "privacy"
    const val ADMIN = "admin"
    const val BACKEND_CONFIG = "backend_config"

    fun productDetail(id: String) = "product_detail/$id"
    fun records(mode: String) = "records/$mode"
}

@Composable
fun RoyalBulletApp(
    initialInviteCode: String = "",
    viewModel: MainViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthBusy by viewModel.isAuthBusy.collectAsStateWithLifecycle()

    val products by viewModel.activeProducts.collectAsStateWithLifecycle()
    val membershipPlans by viewModel.membershipPlans.collectAsStateWithLifecycle()
    val appConfig by viewModel.appConfig.collectAsStateWithLifecycle()
    val companyNews by viewModel.companyNews.collectAsStateWithLifecycle()

    val orders by viewModel.userOrders.collectAsStateWithLifecycle()
    val subscriptions by viewModel.userSubscriptions.collectAsStateWithLifecycle()
    val recharges by viewModel.userRecharges.collectAsStateWithLifecycle()
    val withdrawals by viewModel.userWithdrawals.collectAsStateWithLifecycle()
    val bankCards by viewModel.userBankCards.collectAsStateWithLifecycle()
    val referrals by viewModel.userReferrals.collectAsStateWithLifecycle()
    val ledger by viewModel.userLedger.collectAsStateWithLifecycle()
    val supportMessages by viewModel.userSupportMessages.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var localNotice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.feedback.collect { fb ->
            snackbarHostState.showSnackbar(fb.message)
        }
    }
    LaunchedEffect(localNotice) {
        localNotice?.let {
            snackbarHostState.showSnackbar(it)
            localNotice = null
        }
    }

    val strings = stringsFor(language)

    CompositionLocalProvider(LocalStrings provides strings) {
        val navController = rememberNavController()
        var currentTab by rememberSaveable { mutableIntStateOf(0) }
        var showBonusDialogForSession by remember(currentUser?.id) { mutableStateOf(true) }
        var showWelcomeDialogForSession by remember(currentUser?.id) { mutableStateOf(true) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (currentUser == null) {
                NavHost(navController = navController, startDestination = Routes.AUTH) {
                    composable(Routes.AUTH) {
                        AuthScreen(
                            isBusy = isAuthBusy,
                            currentLanguage = language,
                            initialInviteCode = initialInviteCode,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onLogin = { m, p -> viewModel.login(m, p) },
                            onRegister = { m, lp, wp, ic, terms ->
                                viewModel.register(m, lp, wp, ic, terms)
                            },
                            onResetPassword = { m, wp, np, done ->
                                viewModel.resetPassword(m, wp, np, done)
                            },
                            onOpenTerms = { navController.navigate(Routes.TERMS) }
                        )
                    }
                    composable(Routes.TERMS) {
                        LegalInfoScreen(
                            title = strings.termsAndConditions,
                            bodyText = TERMS_COPY,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            } else {
                val user = currentUser!!
                NavHost(navController = navController, startDestination = Routes.MAIN_TABS) {
                    composable(Routes.MAIN_TABS) {
                        Scaffold(
                            bottomBar = {
                                RoyalBottomNavBar(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it }
                                )
                            }
                        ) { _ ->
                            when (currentTab) {
                                0 -> HomeScreen(
                                    user = user,
                                    products = products,
                                    membershipPlans = membershipPlans,
                                    ledger = ledger,
                                    language = language,
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onOpenMyOrders = { navController.navigate(Routes.MY_ORDERS) },
                                    onOpenRecharge = { navController.navigate(Routes.RECHARGE) },
                                    onOpenWithdraw = { navController.navigate(Routes.WITHDRAWAL) },
                                    onOpenInvite = { currentTab = 3 },
                                    onOpenChat = { navController.navigate(Routes.SUPPORT) },
                                    onOpenNews = { navController.navigate(Routes.COMPANY_NEWS) },
                                    onOpenProductDetail = { pid -> navController.navigate(Routes.productDetail(pid)) },
                                    onOpenVipScreen = { navController.navigate(Routes.VIP_PLANS) },
                                    onActivateMembership = { planId -> viewModel.activateMembership(planId) },
                                    onOpenRecords = { navController.navigate(Routes.records("ALL")) }
                                )
                                1 -> ShopTabScreen(
                                    products = products,
                                    orders = orders,
                                    onOpenProductDetail = { pid -> navController.navigate(Routes.productDetail(pid)) },
                                    onOpenMyOrders = { navController.navigate(Routes.MY_ORDERS) },
                                    onOpenVipPlans = { navController.navigate(Routes.VIP_PLANS) }
                                )
                                2 -> GiftScreen(
                                    user = user,
                                    ledger = ledger,
                                    onClaimWelcomeBonus = { viewModel.claimWelcomeBonus() },
                                    onOpenShop = { currentTab = 1 }
                                )
                                3 -> TeamScreen(
                                    user = user,
                                    referrals = referrals,
                                    appInstallUrl = appConfig.appInstallUrl,
                                    onShowMessage = { localNotice = it }
                                )
                                else -> ProfileScreen(
                                    user = user,
                                    language = language,
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onOpenRecharge = { navController.navigate(Routes.RECHARGE) },
                                    onOpenWithdraw = { navController.navigate(Routes.WITHDRAWAL) },
                                    onOpenMyOrders = { navController.navigate(Routes.MY_ORDERS) },
                                    onOpenBankCard = { navController.navigate(Routes.BANK_CARDS) },
                                    onOpenVip = { navController.navigate(Routes.VIP_PLANS) },
                                    onOpenNews = { navController.navigate(Routes.COMPANY_NEWS) },
                                    onOpenIncomeRecords = { navController.navigate(Routes.records("INCOME")) },
                                    onOpenWithdrawalRecords = { navController.navigate(Routes.records("WITHDRAWAL")) },
                                    onOpenRechargeRecords = { navController.navigate(Routes.records("RECHARGE")) },
                                    onOpenReferralRecords = { navController.navigate(Routes.records("REFERRAL")) },
                                    onOpenSupport = { navController.navigate(Routes.SUPPORT) },
                                    onOpenAbout = { navController.navigate(Routes.ABOUT) },
                                    onOpenTerms = { navController.navigate(Routes.TERMS) },
                                    onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                                    onOpenAdmin = { navController.navigate(Routes.ADMIN) },
                                    onOpenBackendConfig = { navController.navigate(Routes.BACKEND_CONFIG) },
                                    onLogout = { viewModel.logout() },
                                    onShowMessage = { localNotice = it }
                                )
                            }
                        }
                    }

                    composable(
                        route = Routes.PRODUCT_DETAIL,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val pid = backStackEntry.arguments?.getString("productId")
                        val product = products.firstOrNull { it.id == pid }
                        ProductDetailScreen(
                            product = product,
                            user = user,
                            onBack = { navController.popBackStack() },
                            onPlaceOrder = { id, address, applyPromo ->
                                viewModel.placeOrder(id, address, applyPromo) {
                                    navController.navigate(Routes.MY_ORDERS) {
                                        popUpTo(Routes.MAIN_TABS)
                                    }
                                }
                            },
                            onOpenRecharge = { navController.navigate(Routes.RECHARGE) }
                        )
                    }

                    composable(Routes.MY_ORDERS) {
                        MyOrdersScreen(
                            orders = orders,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.VIP_PLANS) {
                        VipMembershipScreen(
                            user = user,
                            plans = membershipPlans,
                            subscriptions = subscriptions,
                            onBack = { navController.popBackStack() },
                            onActivatePlan = { planId -> viewModel.activateMembership(planId) },
                            onOpenRecharge = { navController.navigate(Routes.RECHARGE) }
                        )
                    }

                    composable(Routes.RECHARGE) {
                        RechargeScreen(
                            user = user,
                            config = appConfig,
                            recharges = recharges,
                            onBack = { navController.popBackStack() },
                            onSubmitRecharge = { amt, method, ref ->
                                viewModel.submitRecharge(amt, method, ref) {}
                            },
                            onShowMessage = { localNotice = it }
                        )
                    }

                    composable(Routes.WITHDRAWAL) {
                        WithdrawalScreen(
                            user = user,
                            bankCards = bankCards,
                            withdrawals = withdrawals,
                            onBack = { navController.popBackStack() },
                            onOpenBankCards = { navController.navigate(Routes.BANK_CARDS) },
                            onSubmitWithdrawal = { amt, method, upi, bankAcc, holder, ifsc, wPass ->
                                viewModel.submitWithdrawal(amt, method, upi, bankAcc, holder, ifsc, wPass) {}
                            }
                        )
                    }

                    composable(Routes.BANK_CARDS) {
                        BankCardScreen(
                            bankCards = bankCards,
                            onBack = { navController.popBackStack() },
                            onSaveCard = { holder, bank, acc, ifsc, upi ->
                                viewModel.saveBankCard(holder, bank, acc, ifsc, upi) {}
                            },
                            onDeleteCard = { id -> viewModel.deleteBankCard(id) }
                        )
                    }

                    composable(Routes.COMPANY_NEWS) {
                        CompanyNewsScreen(
                            news = companyNews,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Routes.RECORDS,
                        arguments = listOf(navArgument("mode") { type = NavType.StringType })
                    ) { entry ->
                        val mode = entry.arguments?.getString("mode") ?: "ALL"
                        val title = when (mode) {
                            "INCOME" -> strings.incomeRecords
                            "WITHDRAWAL" -> strings.withdrawalRecords
                            "RECHARGE" -> strings.rechargeRecords
                            "REFERRAL" -> strings.referralRecords
                            else -> strings.transactionSummary
                        }
                        RecordsScreen(
                            title = title,
                            mode = mode,
                            ledger = ledger,
                            recharges = recharges,
                            withdrawals = withdrawals,
                            referrals = referrals,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.SUPPORT) {
                        CustomerSupportScreen(
                            config = appConfig,
                            messages = supportMessages,
                            onBack = { navController.popBackStack() },
                            onSubmitTicket = { sub, msg -> viewModel.submitSupport(sub, msg) {} }
                        )
                    }

                    composable(Routes.ABOUT) {
                        LegalInfoScreen(
                            title = strings.aboutUs,
                            bodyText = ABOUT_COPY,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.TERMS) {
                        LegalInfoScreen(
                            title = strings.termsAndConditions,
                            bodyText = TERMS_COPY,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.PRIVACY) {
                        LegalInfoScreen(
                            title = strings.privacyPolicy,
                            bodyText = PRIVACY_COPY,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.BACKEND_CONFIG) {
                        BackendConfigScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Routes.ADMIN) {
                        val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
                        val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
                        val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
                        val allRecharges by viewModel.allRecharges.collectAsStateWithLifecycle()
                        val allWithdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
                        val allReferrals by viewModel.allReferrals.collectAsStateWithLifecycle()
                        val allSubscriptions by viewModel.allSubscriptions.collectAsStateWithLifecycle()
                        val allLedger by viewModel.allLedger.collectAsStateWithLifecycle()
                        val allSupport by viewModel.allSupportMessages.collectAsStateWithLifecycle()

                        AdminDashboardScreen(
                            users = allUsers,
                            products = allProducts,
                            orders = allOrders,
                            recharges = allRecharges,
                            withdrawals = allWithdrawals,
                            referrals = allReferrals,
                            subscriptions = allSubscriptions,
                            ledger = allLedger,
                            supportMessages = allSupport,
                            config = appConfig,
                            onBack = { navController.popBackStack() },
                            onVerifyRecharge = { id, approve, note -> viewModel.adminVerifyRecharge(id, approve, note) },
                            onFulfillOrder = { orderId -> viewModel.adminFulfillOrder(orderId) },
                            onProcessWithdrawal = { id, status, note -> viewModel.adminProcessWithdrawal(id, status, note) },
                            onSaveProduct = { prod -> viewModel.adminSaveProduct(prod) },
                            onToggleBlockUser = { u -> viewModel.adminToggleBlockUser(u) },
                            onRejectReferral = { ref -> viewModel.adminRejectReferral(ref) },
                            onUpdateConfig = { tg, upi, installUrl -> viewModel.adminUpdateConfig(tg, upi, installUrl) },
                            onPublishNews = { t, sum, body, pin -> viewModel.adminPublishNews(t, sum, body, pin) },
                            onReplySupport = { msg, reply -> viewModel.adminReplySupport(msg, reply) }
                        )
                    }
                }

                // Popups on first login / welcome
                if (!user.welcomeBonusClaimed && showBonusDialogForSession) {
                    FirstLoginBonusDialog(
                        onClaim = {
                            viewModel.claimWelcomeBonus()
                            showBonusDialogForSession = false
                        },
                        onDismiss = { showBonusDialogForSession = false }
                    )
                } else if (!user.hasSeenWelcomePopup && showWelcomeDialogForSession) {
                    WelcomeOverviewDialog(
                        onDismiss = {
                            viewModel.dismissWelcomePopup()
                            showWelcomeDialogForSession = false
                        }
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 86.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

private const val ABOUT_COPY =
    "Royal Bullet 350 is a dedicated rider commerce and service membership platform providing genuine cruiser riding gear, periodic maintenance kits, touring luggage, and transparent workshop membership perks across India."

private const val TERMS_COPY =
    "1. Genuine Commerce: All shop products represent physical goods or authorized workshop service packages fulfilled with real tracking.\n\n" +
        "2. Welcome Bonus: A one-time ₹120 welcome reward is credited to your Account Balance upon signup.\n\n" +
        "3. VIP Memberships: Membership fees purchase service and retail perks (shipping waivers, workshop slots, and verified order cashback). No guaranteed daily, hourly, or deposit-based profits are offered.\n\n" +
        "4. Direct Referral Program: ₹85 one-time reward is credited solely to User A after BOTH User A and User B complete paid and fulfilled qualifying product orders. Single direct level only; duplicate or self-referral accounts are disqualified.\n\n" +
        "5. Recharges & Withdrawals: Deposits require verified UTR/gateway confirmation. Withdrawals (min ₹150) apply to eligible Account Balance and are subject to 2% processing fee and verification."

private const val PRIVACY_COPY =
    "Royal Bullet 350 stores only essential account identifiers, masked contact details, delivery addresses, and financial ledger records required to fulfill orders and process verified payouts. Passwords are stored using salted SHA-256 cryptographic digests. We never sell personal data to third parties."
