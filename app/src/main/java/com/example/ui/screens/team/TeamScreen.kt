package com.example.ui.screens.team

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReferralRecordEntity
import com.example.data.local.UserEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TransparentRuleBanner
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueBright
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private fun buildAppInstallShareUrl(baseInstallUrl: String, inviteCode: String): String {
    val cleanBase = baseInstallUrl.trim().ifBlank {
        "https://github.com/SONUSHARMABJLDRBKH/powerbank/releases/latest"
    }
    return if (cleanBase.contains("play.google.com", ignoreCase = true)) {
        val separator = if (cleanBase.contains("?")) "&" else "?"
        "${cleanBase}${separator}referrer=invite%3D$inviteCode"
    } else {
        cleanBase
    }
}

@Composable
fun TeamScreen(
    user: UserEntity?,
    referrals: List<ReferralRecordEntity>,
    appInstallUrl: String = "https://github.com/SONUSHARMABJLDRBKH/powerbank/releases/latest",
    onShowMessage: (String) -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val inviteCode = user?.inviteCode ?: "RB0000"
    val shareInstallLink = buildAppInstallShareUrl(appInstallUrl, inviteCode)
    val shareMessage =
        "ROYAL BULLET 350 ऐप डाउनलोड करें और ₹120 वेलकम बोनस पाएं!\n" +
            "Install App Link: $shareInstallLink\n" +
            "Referral Code: $inviteCode"

    val totalInvited = referrals.size
    val verifiedPurchasesCount = referrals.count { it.referredQualified && it.referrerQualified }
    val eligibleRewardsTotal = referrals.filter { it.status == "Reward Paid" }.sumOf { it.rewardAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IceBackground)
    ) {
        RoyalTopBar(title = "${s.navTeam} & Referrals")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(RoyalBlueDeep, RoyalBluePrimary, RoyalBlueBright)),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "App Install Link & Referral Code",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = inviteCode,
                                    style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 2.sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            Surface(
                                color = GoldAccent,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "₹120 Signup Bonus",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Visible App Install Link box
                        Surface(
                            color = Color.White.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(shareInstallLink))
                                        )
                                    }.onFailure {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("App Install Link", shareInstallLink))
                                        onShowMessage("App install link copied!")
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Link,
                                    contentDescription = "App Install Link",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tap to Open / Share Install Link",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = shareInstallLink,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.testTag("team_referral_link_text")
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "Open Install Page",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("App Install Link", shareInstallLink))
                                    onShowMessage("App install link copied!")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("team_copy_referral_button")
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(s.copyLink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                                        setPackage("com.whatsapp")
                                    }
                                    runCatching {
                                        context.startActivity(shareIntent)
                                    }.onFailure {
                                        val fallback = Intent.createChooser(
                                            Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                                            },
                                            s.shareWhatsapp
                                        )
                                        context.startActivity(fallback)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("team_share_whatsapp_button")
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", color = Color.White, maxLines = 1)
                            }
                        }

                        Button(
                            onClick = {
                                val chooser = Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Install ROYAL BULLET 350")
                                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    },
                                    "Share Royal Bullet 350 App Link"
                                )
                                context.startActivity(chooser)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("team_share_all_button")
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share App Install Link", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                RoyalCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TeamStatTile(s.totalInvited, totalInvited.toString(), RoyalBluePrimary, Modifier.weight(1f))
                        TeamStatTile(s.verifiedPurchases, verifiedPurchasesCount.toString(), RoyalBlueDeep, Modifier.weight(1f))
                        TeamStatTile(s.eligibleReferrals, formatInr(eligibleRewardsTotal), SuccessGreen, Modifier.weight(1f))
                    }
                }
            }

            item {
                TransparentRuleBanner(
                    "₹85 Direct Referral Reward Rules: Only 1 direct level. Both you (User A) and your invited friend (User B) must complete a paid & fulfilled qualifying product order. After verification, User A receives ₹85 one-time; User B receives ₹0 commission. Zero rewards for signups or deposits alone."
                )
            }

            item {
                Text("Direct Team Members (${referrals.size})", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            if (referrals.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Filled.Groups,
                        title = "No Direct Referrals Yet",
                        subtitle = "Share your code $inviteCode. You earn ₹85 once both you and your invited friend complete verified product orders."
                    )
                }
            } else {
                items(referrals, key = { it.id }) { ref ->
                    RoyalCard {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Member ${ref.referredMobileMasked}", style = MaterialTheme.typography.titleMedium)
                                StatusPill(ref.status)
                            }
                            Text(
                                "Your Order Qualified: ${if (ref.referrerQualified) "Yes" else "Pending"} • Friend Order Qualified: ${if (ref.referredQualified) "Yes" else "Pending"}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextSecondary
                            )
                            Text(
                                "Reward (User A only): ${formatInr(ref.rewardAmount)} • Joined ${formatTimestamp(ref.createdAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamStatTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = accent)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
