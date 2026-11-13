# Royal Bullet 350 — Firebase Configuration & Deployment Guide

Because Firebase provisioning was declined in the interactive setup prompt, the Android app currently persists all data locally via Room (`royal_bullet_350.db`) and DataStore (`royal_bullet_session`) and exposes a clearly marked **Firebase & Payment Config** screen in the Profile tab.

## Steps to Connect Live Firebase Backend

1. **Create Firebase Project**:
   - Open the Firebase Console and create a project (e.g., `royal-bullet-350-prod`).
   - Add an Android app with package name `com.aistudio.royalbullet350.kxmpzq`.
   - Download `google-services.json` and place it in `app/google-services.json`.

2. **Enable Required Services**:
   - **Firebase Authentication**: Enable Phone (+91) / Google Sign-In providers.
   - **Cloud Firestore**: Create database and deploy `/firebase/firestore.rules`:
     ```bash
     firebase deploy --only firestore:rules
     ```
   - **Firebase Storage**: Enable default bucket for Admin product image uploads.
   - **Firebase Cloud Functions**: Deploy `/firebase/functions/index.js` so all wallet credits, UTR recharge verifications, order fulfillments, ₹85 referral evaluations, and withdrawals execute strictly server-side:
     ```bash
     cd firebase/functions && npm install && cd ..
     firebase deploy --only functions
     ```

3. **Enable Android Firebase Dependencies**:
   - Uncomment `firebase-bom`, `firebase-firestore`, `firebase-auth`, `androidx-credentials`, `androidx-credentials-play-services`, and `googleid` in `app/build.gradle.kts`.
   - Swap `RoyalBulletRepository` calls to delegate writes to Firebase Cloud Functions and listen to Firestore snapshots.

4. **Default Admin Credentials (Local Evaluation)**:
   - Mobile: `9000000000`
   - Login Password: `Admin@350`
   - Withdrawal Password: `123456`
