package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        MembershipPlanEntity::class,
        MembershipSubscriptionEntity::class,
        RechargeEntity::class,
        WithdrawalEntity::class,
        BankCardEntity::class,
        ReferralRecordEntity::class,
        LedgerEntryEntity::class,
        CompanyNewsEntity::class,
        SupportMessageEntity::class,
        AppConfigEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class RoyalBulletDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: RoyalBulletDatabase? = null

        fun getInstance(context: Context): RoyalBulletDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RoyalBulletDatabase::class.java,
                    "royal_bullet_350.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { seedInitialCatalog(it.appDao()) }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun hashPassword(password: String, salt: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }

        suspend fun seedInitialCatalog(dao: AppDao) {
            dao.upsertAppConfig(AppConfigEntity())

            // Seed default Admin account (0 fake balance) for administrative testing & verification
            val adminSalt = "rb350_admin_salt"
            dao.upsertUser(
                UserEntity(
                    id = "RB10001",
                    mobile = "9000000000",
                    loginPasswordHash = hashPassword("Admin@350", adminSalt),
                    withdrawalPasswordHash = hashPassword("123456", adminSalt),
                    salt = adminSalt,
                    inviteCode = "RBADMIN",
                    role = "ADMIN",
                    vipTier = 5,
                    welcomeBonusClaimed = true,
                    hasSeenWelcomePopup = true
                )
            )

            // Seed Royal Enfield products at exact ₹700, ₹1,500, ₹2,000, ₹3,000 (₹120/day on ₹700 proportional over 30 days)
            dao.insertProductsIfEmpty(
                listOf(
                    ProductEntity(
                        id = "prod_1",
                        name = "Royal Enfield Bullet 350 Black",
                        category = "Motorcycles",
                        price = 700.0,
                        dailyEarning = 120.0,
                        totalRevenue = 3600.0,
                        cycleDays = 30,
                        description = "Iconic Royal Enfield Bullet 350 Classic Black Edition cruiser motorcycle package with full 30-day rider plan.",
                        specifications = "Model: Bullet 350 Black • Engine: 349cc J-Series • Cycle: 30 Days • Daily Earning: ₹120",
                        imageKey = "re_black_bike",
                        fulfillmentNote = "Verified Royal Enfield showroom allocation & digital rider pass."
                    ),
                    ProductEntity(
                        id = "prod_2",
                        name = "Royal Enfield Classic Scooty",
                        category = "Scooters",
                        price = 1500.0,
                        dailyEarning = 240.0,
                        totalRevenue = 7200.0,
                        cycleDays = 30,
                        description = "Royal Enfield Classic Blue Touring Scooty edition built for effortless urban cruising and high daily returns.",
                        specifications = "Model: RE Touring Scooty • Finish: Royal Blue & Chrome • Cycle: 30 Days • Daily Earning: ₹240",
                        imageKey = "re_scooty",
                        fulfillmentNote = "Priority dispatch & dealership support included."
                    ),
                    ProductEntity(
                        id = "prod_3",
                        name = "Royal Enfield Hunter 350 Blue",
                        category = "Cruisers",
                        price = 2000.0,
                        dailyEarning = 340.0,
                        totalRevenue = 10200.0,
                        cycleDays = 30,
                        description = "Sporty Royal Enfield Hunter 350 in cobalt blue with agile chassis geometry and 30-day fleet revenue plan.",
                        specifications = "Model: Hunter 350 Blue • Dual-Channel ABS • Cycle: 30 Days • Daily Earning: ₹340",
                        imageKey = "re_hunter",
                        fulfillmentNote = "Authorized Royal Enfield dealer fulfillment with tracking."
                    ),
                    ProductEntity(
                        id = "prod_4",
                        name = "Royal Enfield Meteor 350 Flagship",
                        category = "Flagship",
                        price = 3000.0,
                        dailyEarning = 520.0,
                        totalRevenue = 15600.0,
                        cycleDays = 30,
                        description = "Flagship Royal Enfield Meteor 350 Cruiser edition featuring premium touring ergonomics and top monthly revenue.",
                        specifications = "Model: Meteor 350 Flagship • Tripper Nav • Cycle: 30 Days • Daily Earning: ₹520",
                        imageKey = "hero",
                        fulfillmentNote = "Flagship showroom kit & priority concierge activated."
                    )
                )
            )

            // Seed Luxury Car VIP Plans (Thar, BMW, Ferrari, Mercedes, G-Wagon)
            dao.insertPlansIfEmpty(
                listOf(
                    MembershipPlanEntity(
                        id = "vip_1",
                        tierLevel = 1,
                        name = "Mahindra Thar 4x4 Black",
                        price = 800.0,
                        dailyEarning = 140.0,
                        totalRevenue = 4200.0,
                        validityDays = 30,
                        benefits = "• Daily Earning: ₹140 for 30 days (₹4,200 Total)\n• Priority SUV fleet access & free express shipping\n• 2% extra cashback on verified orders",
                        eligibilityCondition = "Active 30 days from purchase. Uses verified deposit or available balance.",
                        orderDiscountPercent = 2,
                        imageKey = "car_thar"
                    ),
                    MembershipPlanEntity(
                        id = "vip_2",
                        tierLevel = 2,
                        name = "BMW M4 Sports Coupe",
                        price = 1200.0,
                        dailyEarning = 210.0,
                        totalRevenue = 6300.0,
                        validityDays = 30,
                        benefits = "• Daily Earning: ₹210 for 30 days (₹6,300 Total)\n• 3% cashback on verified showroom orders\n• Priority express customer concierge",
                        eligibilityCondition = "Active 30 days. Requires verified wallet balance.",
                        orderDiscountPercent = 3,
                        imageKey = "car_bmw"
                    ),
                    MembershipPlanEntity(
                        id = "vip_3",
                        tierLevel = 3,
                        name = "Ferrari SF90 Super",
                        price = 1500.0,
                        dailyEarning = 260.0,
                        totalRevenue = 7800.0,
                        validityDays = 30,
                        benefits = "• Daily Earning: ₹260 for 30 days (₹7,800 Total)\n• 4% cashback on verified orders\n• Dedicated roadside & showroom support",
                        eligibilityCondition = "Active 30 days from activation.",
                        orderDiscountPercent = 4,
                        imageKey = "car_ferrari"
                    ),
                    MembershipPlanEntity(
                        id = "vip_4",
                        tierLevel = 4,
                        name = "Mercedes-Benz AMG",
                        price = 2000.0,
                        dailyEarning = 350.0,
                        totalRevenue = 10500.0,
                        validityDays = 30,
                        benefits = "• Daily Earning: ₹350 for 30 days (₹10,500 Total)\n• 5% cashback on showroom purchases\n• Priority allocation & premium perks",
                        eligibilityCondition = "Active 30 days from activation.",
                        orderDiscountPercent = 5,
                        imageKey = "car_mercedes"
                    ),
                    MembershipPlanEntity(
                        id = "vip_5",
                        tierLevel = 5,
                        name = "G-Wagon G63 Gold",
                        price = 3000.0,
                        dailyEarning = 540.0,
                        totalRevenue = 16200.0,
                        validityDays = 30,
                        benefits = "• Daily Earning: ₹540 for 30 days (₹16,200 Total)\n• 6% flagship cashback on all verified orders\n• Unlimited priority access & free nationwide delivery",
                        eligibilityCondition = "Flagship 30-day Luxury Fleet plan.",
                        orderDiscountPercent = 6,
                        imageKey = "car_gwagon"
                    )
                )
            )

            // Seed initial Company News
            dao.insertNewsIfEmpty(
                listOf(
                    CompanyNewsEntity(
                        id = "news_1",
                        title = "Royal Enfield & Luxury Car Fleet Plans Now Live",
                        summary = "Explore Royal Enfield Bullet 350, Classic Scooty, Hunter 350 & Luxury Cars (Thar, BMW, Ferrari, Mercedes, G-Wagon).",
                        body = "Welcome to Royal Bullet 350! Choose from our upgraded Royal Enfield motorcycle lineup starting at ₹700 (₹120/day for 30 days) or our exclusive Luxury Car Showcase plans featuring Mahindra Thar, BMW M4, Ferrari SF90, Mercedes-Benz AMG, and G-Wagon G63.",
                        isPinned = true
                    ),
                    CompanyNewsEntity(
                        id = "news_2",
                        title = "Instant ₹120 Account Bonus & ₹85 Direct Referral",
                        summary = "Get ₹120 on signup in Account Balance and earn ₹85 per verified direct referral.",
                        body = "Every new member receives ₹120 directly in Account Balance on signup. Minimum withdrawal is ₹150. Invite friends and receive ₹85 once both you and your direct referral complete verified orders.",
                        isPinned = false
                    )
                )
            )
        }
    }
}
