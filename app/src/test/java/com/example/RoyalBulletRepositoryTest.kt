package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RoyalBulletDatabase
import com.example.data.repository.RoyalBulletRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoyalBulletRepositoryTest {

    @Test
    fun `full wallet order referral and withdrawal verification rules`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = RoyalBulletDatabase.getInstance(context)
        val dao = db.appDao()
        val repo = RoyalBulletRepository(context)
        repo.ensureSeeded()

        // 1. Register User A -> ₹120 Welcome Bonus credited immediately to Account Balance (availableBalance)
        val regA = repo.registerUser("9876543210", "Pass@123", "1122", "")
        assertTrue(regA.isSuccess)
        val userA = regA.getOrThrow()
        assertEquals(120.0, userA.availableBalance, 0.01)
        assertTrue(userA.welcomeBonusClaimed)

        // Cannot claim welcome bonus twice
        assertTrue(repo.claimWelcomeBonus(userA.id).isFailure)

        // 2. Register User B with User A's invite code -> User B gets ₹120; User A gets ₹0 referral reward before purchases
        val regB = repo.registerUser("9876543211", "Pass@123", "3344", userA.inviteCode)
        assertTrue(regB.isSuccess)
        val userB = regB.getOrThrow()
        assertEquals(120.0, userB.availableBalance, 0.01)
        assertEquals(120.0, dao.getUserById(userA.id)!!.availableBalance, 0.01)

        // 3. Withdrawal below ₹150 fails, and ₹150 fails when balance is only ₹120
        assertTrue(
            repo.submitWithdrawalRequest(userA.id, 100.0, "UPI", "rider@upi", "", "Rider A", "", "1122").isFailure
        )
        assertTrue(
            repo.submitWithdrawalRequest(userA.id, 150.0, "UPI", "rider@upi", "", "Rider A", "", "1122").isFailure
        )

        // 4. Recharge stays Pending until verified; then credits depositBalance
        val recA = repo.submitRechargeRequest(userA.id, 1000.0, "UPI", "UTR100001").getOrThrow()
        assertEquals("Pending", recA.status)
        assertEquals(0.0, dao.getUserById(userA.id)!!.depositBalance, 0.01)

        assertTrue(repo.adminVerifyRecharge(recA.id, true, "OK").isSuccess)
        assertEquals(1000.0, dao.getUserById(userA.id)!!.depositBalance, 0.01)

        val recB = repo.submitRechargeRequest(userB.id, 1000.0, "PhonePe", "UTR100002").getOrThrow()
        assertTrue(repo.adminVerifyRecharge(recB.id, true, "OK").isSuccess)

        // 5. Both User A and User B order Product 1 (₹700) and Admin fulfills both
        val orderA = repo.placeProductOrder(userA.id, "prod_1", "12 MG Road, Pune 411001", false).getOrThrow()
        assertTrue(repo.adminFulfillOrder(orderA.id).isSuccess)
        // User B hasn't fulfilled yet -> still ₹120 (₹0 referral reward)
        assertEquals(120.0, dao.getUserById(userA.id)!!.availableBalance, 0.01)

        val orderB = repo.placeProductOrder(userB.id, "prod_1", "44 Residency Rd, Bengaluru 560025", false).getOrThrow()
        assertTrue(repo.adminFulfillOrder(orderB.id).isSuccess)

        // Now User A receives ₹85 one-time (120 + 85 = 205); User B receives ₹0 referral commission (stays 120)
        assertEquals(205.0, dao.getUserById(userA.id)!!.availableBalance, 0.01)
        assertEquals(120.0, dao.getUserById(userB.id)!!.availableBalance, 0.01)

        // 6. Now User A can withdraw ₹150 (>= min ₹150)
        val wSuccess = repo.submitWithdrawalRequest(
            userA.id, 150.0, "UPI", "rider@upi", "", "Rider A", "", "1122"
        )
        assertTrue(wSuccess.isSuccess)
        assertEquals(55.0, dao.getUserById(userA.id)!!.availableBalance, 0.01)
    }
}
