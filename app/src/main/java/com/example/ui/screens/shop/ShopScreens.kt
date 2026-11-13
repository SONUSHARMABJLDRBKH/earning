package com.example.ui.screens.shop

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.ui.components.BlueGradientButton
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ProductThumbnail
import com.example.ui.screens.home.HomeProductCard
import com.example.ui.components.RoyalCard
import com.example.ui.components.RoyalTopBar
import com.example.ui.components.StatusPill
import com.example.ui.components.TransparentRuleBanner
import com.example.ui.components.formatInr
import com.example.ui.components.formatTimestamp
import com.example.ui.strings.LocalStrings
import com.example.ui.theme.IceBackground
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.RoyalSkyBorder
import com.example.ui.theme.RoyalSkyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.min

@Composable
fun ShopTabScreen(
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    onOpenProductDetail: (String) -> Unit,
    onOpenMyOrders: () -> Unit,
    onOpenVipPlans: () -> Unit
) {
    val s = LocalStrings.current
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = remember(products, selectedCategory) {
        if (selectedCategory == "All") products
        else products.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IceBackground)
    ) {
        RoyalTopBar(
            title = s.productShop,
            actions = {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clickable(onClick = onOpenMyOrders)
                        .testTag("shop_my_orders_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.ReceiptLong,
                            contentDescription = s.myOrders,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${s.myOrders} (${orders.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 108.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                RoyalCard(onClick = onOpenVipPlans) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VIP Luxury Car Fleet",
                                style = MaterialTheme.typography.titleMedium,
                                color = RoyalBlueDeep
                            )
                            Text(
                                text = "Thar, BMW, Ferrari, Mercedes & G-Wagon plans starting at ₹800",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = onOpenVipPlans) {
                            Text("View Cars")
                        }
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            items(filtered, key = { it.id }) { product ->
                val latestOrder = orders.firstOrNull { it.productId == product.id }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    HomeProductCard(
                        product = product,
                        buyLabel = s.buyNow,
                        onClick = { onOpenProductDetail(product.id) }
                    )
                    if (latestOrder != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Latest order ${latestOrder.id}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            StatusPill(latestOrder.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    product: ProductEntity?,
    user: UserEntity?,
    onBack: () -> Unit,
    onPlaceOrder: (productId: String, address: String, applyPromo: Boolean) -> Unit,
    onOpenRecharge: () -> Unit
) {
    var address by remember { mutableStateOf("") }
    var applyPromo by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            RoyalTopBar(title = "Product Details", onBack = onBack)
        },
        containerColor = IceBackground
    ) { padding ->
        if (product == null || user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Product unavailable.")
            }
            return@Scaffold
        }

        val maxPromo = product.price * 0.10
        val promoApplied = if (applyPromo && user.promotionalBalance > 0.0) {
            min(user.promotionalBalance, maxPromo)
        } else 0.0
        val netPayable = product.price - promoApplied
        val spendable = user.depositBalance + user.availableBalance

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RoyalCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    ProductThumbnail(
                        imageKey = product.imageKey,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = RoyalSkyLight,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = RoyalBluePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(product.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatInr(product.price),
                            style = MaterialTheme.typography.headlineMedium,
                            color = RoyalBluePrimary
                        )
                        Surface(color = RoyalSkyLight, shape = RoundedCornerShape(10.dp)) {
                            Text(
                                text = "Daily ${formatInr(product.dailyEarning)} • Total ${formatInr(product.totalRevenue)}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SuccessGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(product.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            RoyalCard {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Verified, contentDescription = null, tint = RoyalBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Specifications & Fulfillment", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(product.specifications, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(product.fulfillmentNote, style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                    }
                }
            }

            RoyalCard {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Order Checkout", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Delivery Address (Full Street, City, PIN)") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoyalSkyLight)
                            .border(1.dp, RoyalSkyBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = applyPromo,
                            onCheckedChange = { applyPromo = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBluePrimary)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Apply Promotional Credit (up to 10%)",
                                style = MaterialTheme.typography.labelLarge,
                                color = RoyalBlueDeep
                            )
                            Text(
                                "Available Promo: ${formatInr(user.promotionalBalance)} • Discount: -${formatInr(promoApplied)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Verified Spendable Balance", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(formatInr(spendable), style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Payable", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(formatInr(netPayable), style = MaterialTheme.typography.titleLarge, color = RoyalBluePrimary)
                    }

                    if (spendable < netPayable) {
                        TransparentRuleBanner(
                            "Insufficient verified Deposit/Available balance. Please Recharge ${formatInr(netPayable - spendable)} to place this order."
                        )
                        OutlinedButton(
                            onClick = onOpenRecharge,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Go to Recharge")
                        }
                    }

                    BlueGradientButton(
                        text = if (spendable >= netPayable) "Buy Now • ${formatInr(netPayable)}" else "Recharge Balance to Buy",
                        onClick = {
                            if (spendable >= netPayable) {
                                onPlaceOrder(product.id, address.ifBlank { "Verified Rider Delivery" }, applyPromo)
                            } else {
                                onOpenRecharge()
                            }
                        },
                        testTag = "confirm_place_order_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MyOrdersScreen(
    orders: List<OrderEntity>,
    onBack: () -> Unit
) {
    val s = LocalStrings.current
    Scaffold(
        topBar = { RoyalTopBar(title = s.myOrders, onBack = onBack) },
        containerColor = IceBackground
    ) { padding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                EmptyStateCard(
                    icon = Icons.Filled.ShoppingCart,
                    title = "No Orders Yet",
                    subtitle = "Purchase Royal Enfield bikes or scooty models from the Product Shop."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    RoyalCard {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(order.id, style = MaterialTheme.typography.labelLarge, color = RoyalBlueDeep)
                                StatusPill(order.status)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProductThumbnail(
                                    imageKey = order.productImageKey,
                                    contentDescription = order.productName,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(order.productName, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Paid: ${formatInr(order.paidFromBalance)} (Promo: -${formatInr(order.promoCreditApplied)})",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = TextSecondary
                                    )
                                    Text(
                                        "Tracking: ${order.trackingCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoyalBluePrimary
                                    )
                                }
                            }
                            Text(
                                "Ship to: ${order.shippingAddress}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = TextMuted
                            )
                            Text(
                                formatTimestamp(order.createdAt),
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
