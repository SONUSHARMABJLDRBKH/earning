package com.example.ui.screens.vip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MembershipPlanEntity
import com.example.data.local.MembershipSubscriptionEntity
import com.example.data.local.UserEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.ProductThumbnail
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldBadgeText
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueBright
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VipMembershipScreen(
    user: UserEntity?,
    plans: List<MembershipPlanEntity>,
    subscriptions: List<MembershipSubscriptionEntity>,
    onBack: () -> Unit,
    onActivatePlan: (String) -> Unit,
    onOpenRecharge: () -> Unit
) {
    val s = LocalStrings.current
    var selectedPlan by remember { mutableStateOf<MembershipPlanEntity?>(null) }

    Scaffold(
        topBar = { RoyalTopBar(title = s.vipMemberships, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(listOf(RoyalBlueDeep, RoyalBluePrimary, RoyalBlueBright)),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(34.dp)
                        )
                        Column {
                            Text(
                                text = "Luxury Car Fleet Showcase",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Thar • BMW • Ferrari • Mercedes • G-Wagon",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            items(plans, key = { it.id }) { plan ->
                val isOwned = (user?.vipTier ?: 0) >= plan.tierLevel
                RoyalCard {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(RoyalSkyLight)
                        ) {
                            ProductThumbnail(
                                imageKey = plan.imageKey,
                                contentDescription = plan.name,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = GoldBadgeBg,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${plan.validityDays} DAYS CYCLE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldBadgeText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            if (isOwned) {
                                Surface(
                                    color = SuccessGreen,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = RoyalBlueDeep,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatInr(plan.price),
                                style = MaterialTheme.typography.headlineMedium,
                                color = RoyalBluePrimary
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(RoyalSkyLight)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Daily Earning", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(
                                    formatInr(plan.dailyEarning),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SuccessGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Cycle", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(
                                    "${plan.validityDays} Days",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Revenue", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(
                                    formatInr(plan.totalRevenue),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlueDeep
                                )
                            }
                        }

                        Text(
                            text = plan.benefits,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        BlueGradientButton(
                            text = if (isOwned) "Active Plan" else "${s.activate} • ${formatInr(plan.price)}",
                            onClick = { if (!isOwned) selectedPlan = plan },
                            enabled = !isOwned,
                            testTag = "activate_vip_${plan.id}",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (subscriptions.isNotEmpty()) {
                item {
                    Text(
                        "My Fleet Purchases",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                items(subscriptions, key = { it.id }) { sub ->
                    RoyalCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sub.planName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Activated ${formatTimestamp(sub.activatedAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                            StatusPill(sub.status)
                        }
                    }
                }
            }
        }
    }

    selectedPlan?.let { plan ->
        val spendable = (user?.depositBalance ?: 0.0) + (user?.availableBalance ?: 0.0)
        val canAfford = spendable >= plan.price
        AlertDialog(
            onDismissRequest = { selectedPlan = null },
            title = { Text(plan.name, fontWeight = FontWeight.Bold, color = RoyalBlueDeep) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Price: ${formatInr(plan.price)}", style = MaterialTheme.typography.titleMedium, color = RoyalBluePrimary)
                    Text("Daily Earning: ${formatInr(plan.dailyEarning)} • Total: ${formatInr(plan.totalRevenue)}", color = SuccessGreen)
                    Text("Your Spendable Balance: ${formatInr(spendable)}", color = TextPrimary)
                    if (!canAfford) {
                        Text(
                            "Need ${formatInr(plan.price - spendable)} more in verified Deposit/Account balance to buy this plan.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            },
            confirmButton = {
                if (canAfford) {
                    BlueGradientButton(
                        text = "Confirm Buy",
                        onClick = {
                            onActivatePlan(plan.id)
                            selectedPlan = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BlueGradientButton(
                        text = "Recharge Now",
                        onClick = {
                            selectedPlan = null
                            onOpenRecharge()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlan = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
