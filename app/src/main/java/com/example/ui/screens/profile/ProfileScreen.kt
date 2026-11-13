package com.example.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.ui.components.RoyalBulletLogoBadge
import com.example.ui.components.RoyalCard
import com.example.ui.components.formatInr
import com.example.ui.strings.AppLanguage
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldBadgeText
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueBright
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyBorder
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    user: UserEntity,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onOpenRecharge: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenMyOrders: () -> Unit,
    onOpenBankCard: () -> Unit,
    onOpenVip: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenIncomeRecords: () -> Unit,
    onOpenWithdrawalRecords: () -> Unit,
    onOpenRechargeRecords: () -> Unit,
    onOpenReferralRecords: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenBackendConfig: () -> Unit,
    onLogout: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val maskedMobile = if (user.mobile.length == 10) {
        "${user.mobile.take(3)}***${user.mobile.takeLast(4)}"
    } else user.mobile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(IceBackground),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. Deep Blue Header with Motorcycle Backdrop + Overlapping Profile Card (matching Screenshot 1)
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(255.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(RoyalBlueDeep, RoyalBluePrimary, RoyalBlueBright)
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.18f),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = s.profileTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.CenterEnd)
                                    .clickable(onClick = onOpenSupport)
                                    .testTag("profile_support_top_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.HeadsetMic,
                                        contentDescription = stringResource(id = R.string.cd_support_button),
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = s.availableBalance,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                        Text(
                            text = formatInr(user.availableBalance),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                            ) {
                                Text(
                                    text = "${s.depositBalance} ${formatInr(user.depositBalance)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                            ) {
                                Text(
                                    text = "${s.promotionalBalance} ${formatInr(user.promotionalBalance)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Overlapping White User Info Card (matching Screenshot 1)
                RoyalCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 205.dp),
                    onClick = onOpenVip
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoyalBulletLogoBadge(size = 56.dp, showLabel = false)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = maskedMobile,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ID: ${user.id}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("User ID", user.id))
                                        onShowMessage("User ID ${user.id} copied.")
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = stringResource(id = R.string.cd_copy_id),
                                        tint = RoyalBluePrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                            Surface(
                                color = GoldBadgeBg,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(GoldAccent)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (user.vipTier > 0) "VIP${user.vipTier}" else "Standard",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBadgeText
                                    )
                                }
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = RoyalBluePrimary
                        )
                    }
                }
            }
        }

        // 2. 4 Quick Action Buttons Card (Recharge, Withdraw, My Orders, Bank Card)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            RoyalCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileQuickTile(
                        label = s.recharge,
                        icon = Icons.Filled.AccountBalanceWallet,
                        tint = RoyalBluePrimary,
                        testTag = "profile_quick_recharge",
                        onClick = onOpenRecharge,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileQuickTile(
                        label = s.withdraw,
                        icon = Icons.Filled.Payments,
                        tint = Color(0xFF0284C7),
                        testTag = "profile_quick_withdraw",
                        onClick = onOpenWithdraw,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileQuickTile(
                        label = s.myOrders,
                        icon = Icons.Filled.ShoppingCart,
                        tint = RoyalBlueDeep,
                        testTag = "profile_quick_orders",
                        onClick = onOpenMyOrders,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileQuickTile(
                        label = s.bankCard,
                        icon = Icons.Filled.AccountBalance,
                        tint = Color(0xFF4F46E5),
                        testTag = "profile_quick_bank",
                        onClick = onOpenBankCard,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Additional Options Header + List
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = s.additionalOptions,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileOptionRow(s.companyNews, Icons.Filled.Campaign, Color(0xFF0284C7), "opt_news", onOpenNews)
                ProfileOptionRow(s.incomeRecords, Icons.Filled.History, Color(0xFF16A34A), "opt_income", onOpenIncomeRecords)
                ProfileOptionRow(s.withdrawalRecords, Icons.Filled.Payments, Color(0xFF0D9488), "opt_withdraw_records", onOpenWithdrawalRecords)
                ProfileOptionRow(s.rechargeRecords, Icons.Filled.ReceiptLong, RoyalBluePrimary, "opt_recharge_records", onOpenRechargeRecords)
                ProfileOptionRow(s.referralRecords, Icons.Filled.Group, Color(0xFF4F46E5), "opt_referral_records", onOpenReferralRecords)
                ProfileOptionRow(s.customerSupport, Icons.Filled.HeadsetMic, Color(0xFF2563EB), "opt_support", onOpenSupport)
                ProfileOptionRow(s.aboutUs, Icons.Filled.Info, Color(0xFF0EA5E9), "opt_about", onOpenAbout)
                ProfileOptionRow(s.termsAndConditions, Icons.Filled.Description, RoyalBlueDeep, "opt_terms", onOpenTerms)
                ProfileOptionRow(s.privacyPolicy, Icons.Filled.PrivacyTip, Color(0xFF6366F1), "opt_privacy", onOpenPrivacy)
                ProfileOptionRow(
                    title = "${s.languageSetting} (${language.label})",
                    icon = Icons.Filled.Language,
                    tint = Color(0xFF0891B2),
                    testTag = "opt_language",
                    onClick = onToggleLanguage
                )
                ProfileOptionRow(
                    title = s.backendConfig,
                    icon = Icons.Filled.CloudQueue,
                    tint = Color(0xFFD97706),
                    testTag = "opt_backend_config",
                    onClick = onOpenBackendConfig
                )
                if (user.role == "ADMIN") {
                    ProfileOptionRow(
                        title = s.adminPanel,
                        icon = Icons.Filled.AdminPanelSettings,
                        tint = RoyalBlueDeep,
                        testTag = "opt_admin_panel",
                        onClick = onOpenAdmin
                    )
                }
                ProfileOptionRow(
                    title = s.logout,
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    tint = ErrorRed,
                    testTag = "opt_logout",
                    onClick = onLogout
                )
            }
        }
    }
}

@Composable
private fun ProfileQuickTile(
    label: String,
    icon: ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RoyalSkyLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileOptionRow(
    title: String,
    icon: ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    RoyalCard(
        modifier = Modifier.testTag(testTag),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(19.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RoyalBluePrimary
            )
        }
    }
}
