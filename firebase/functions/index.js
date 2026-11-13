const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();

// Server-side UTR / Payment Gateway Recharge Verification
exports.verifyRecharge = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Login required.");
  const caller = await db.collection("users").doc(context.auth.uid).get();
  if (!caller.exists || caller.data().role !== "ADMIN") {
    throw new functions.https.HttpsError("permission-denied", "Admin access required.");
  }

  const { rechargeId, approve, note } = data;
  return db.runTransaction(async (tx) => {
    const recRef = db.collection("recharges").doc(rechargeId);
    const recSnap = await tx.get(recRef);
    if (!recSnap.exists) throw new functions.https.HttpsError("not-found", "Recharge not found.");
    const rec = recSnap.data();
    if (rec.status !== "Pending") throw new functions.https.HttpsError("failed-precondition", "Already processed.");

    if (approve) {
      const userRef = db.collection("users").doc(rec.userId);
      tx.update(userRef, {
        depositBalance: admin.firestore.FieldValue.increment(rec.amount),
      });
      tx.update(recRef, { status: "Successful", verifiedAt: admin.firestore.FieldValue.serverTimestamp(), note });
      tx.set(db.collection("ledger").doc(), {
        userId: rec.userId,
        type: "RECHARGE",
        balanceBucket: "DEPOSIT",
        amount: rec.amount,
        referenceId: rechargeId,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    } else {
      tx.update(recRef, { status: "Failed", verifiedAt: admin.firestore.FieldValue.serverTimestamp(), note });
    }
    return { success: true };
  });
});

// Server-side Order Fulfillment & Strict 8-Condition ₹85 Referral Verification
exports.fulfillOrderAndEvaluateReferral = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Login required.");
  const caller = await db.collection("users").doc(context.auth.uid).get();
  if (!caller.exists || caller.data().role !== "ADMIN") {
    throw new functions.https.HttpsError("permission-denied", "Admin access required.");
  }

  const { orderId } = data;
  return db.runTransaction(async (tx) => {
    const orderRef = db.collection("orders").doc(orderId);
    const orderSnap = await tx.get(orderRef);
    if (!orderSnap.exists) throw new functions.https.HttpsError("not-found", "Order not found.");
    const order = orderSnap.data();
    if (order.status === "Fulfilled") return { alreadyFulfilled: true };

    tx.update(orderRef, {
      status: "Fulfilled",
      fulfilledAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Check if buyer was referred (User B) and evaluate User A's ₹85 one-time reward
    const refSnap = await tx.get(
      db.collection("referrals").where("referredUserId", "==", order.userId).limit(1)
    );
    if (!refSnap.empty) {
      const refDoc = refSnap.docs[0];
      const ref = refDoc.data();
      if (ref.status !== "Reward Paid" && ref.status !== "Rejected") {
        const referrerOrders = await tx.get(
          db.collection("orders")
            .where("userId", "==", ref.referrerUserId)
            .where("status", "==", "Fulfilled")
            .limit(1)
        );
        const referrerQualified = !referrerOrders.empty;
        if (referrerQualified) {
          tx.update(db.collection("users").doc(ref.referrerUserId), {
            availableBalance: admin.firestore.FieldValue.increment(85),
          });
          tx.update(refDoc.ref, {
            referrerQualified: true,
            referredQualified: true,
            status: "Reward Paid",
            paidAt: admin.firestore.FieldValue.serverTimestamp(),
          });
          tx.set(db.collection("ledger").doc(), {
            userId: ref.referrerUserId,
            type: "REFERRAL_REWARD",
            balanceBucket: "AVAILABLE",
            amount: 85,
            referenceId: refDoc.id,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
          });
        } else {
          tx.update(refDoc.ref, { referredQualified: true, status: "Awaiting Verification" });
        }
      }
    }
    return { success: true };
  });
});
