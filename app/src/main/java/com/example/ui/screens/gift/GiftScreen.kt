package com.example.ui.screens.gift

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.UserEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.TransparentRuleBanner
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldBadgeText
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GiftScreen(
    user: UserEntity?,
    ledger: List<LedgerEntryEntity>,
    onClaimWelcomeBonus: () -> Unit,
    onOpenShop: () -> Unit
) {
    val s = LocalStrings.current

    val rewardEntries = remember(ledger) {
        ledger.filter {
            it.type == "WELCOME_PROMO" ||
                it.type == "CASHBACK" ||
                it.type == "REFERRAL_REWARD"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IceBackground)
    ) {
        RoyalTopBar(title = s.navGift)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Welcome Bonus Card (Credited directly to Account Balance)
            item {
                RoyalCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(color = GoldBadgeBg, shape = RoundedCornerShape(50)) {
                                    Text(
                                        "ACCOUNT BALANCE BONUS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldBadgeText,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("₹120 Welcome Bonus", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "${s.accountBalance}: ${formatInr(user?.availableBalance ?: 0.0)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = RoyalBluePrimary
                                )
                            }
                            Icon(
                                Icons.Filled.CardGiftcard,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        TransparentRuleBanner(
                            "One-time ₹120 welcome reward is credited directly to your Account Balance on signup. Minimum withdrawal limit is ₹150."
                        )

                        if (user?.welcomeBonusClaimed == false) {
                            BlueGradientButton(
                                text = s.claimReward,
                                onClick = onClaimWelcomeBonus,
                                testTag = "gift_claim_welcome_button",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                "✓ ₹120 One-Time Welcome Bonus Credited to Account Balance",
                                style = MaterialTheme.typography.labelLarge,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            // Special Offers
            item {
                RoyalCard(onClick = onOpenShop) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoyalSkyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = RoyalBluePrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.specialOffers, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(
                                "Explore genuine Royal Bullet 350 riding gear, service kits, and VIP rider memberships.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            item {
                Text(s.rewardHistory, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            if (rewardEntries.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Filled.CardGiftcard,
                        title = "No Rewards Recorded Yet",
                        subtitle = "Your ₹120 welcome bonus, cashback, and referral rewards will appear here."
                    )
                }
            } else {
                items(rewardEntries, key = { it.id }) { entry ->
                    RoyalCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp))
                                Text(entry.description, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextSecondary)
                                Text(formatTimestamp(entry.createdAt), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+${formatInr(entry.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
