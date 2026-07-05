# SpendWise

SpendWise is a modern, intuitive, and feature-rich personal finance tracker application for Android. It allows users to effortlessly track their daily income and expenses, organize transactions under standard or custom categories, and visualize spending habits through interactive analytical charts. Built with modern Android development practices, SpendWise ensures all data is saved locally on the device using a high-performance database with reactive updates.

---

## 🚀 Key Features

*   **Real-time Balance Dashboard:** Instantly view your total balance, total income, and total expenses.
*   **Interactive Visual Analytics:** Dynamic category-wise expense breakdown visualized using interactive pie charts (powered by `MPAndroidChart`).
*   **Transaction Filtering:** View your complete transaction history sorted by date and filter on-the-fly by *All*, *Income*, or *Expense* types.
*   **Transaction Details:** View comprehensive details of any transaction (amount, description, category, type, exact date, and time) in an elegant bottom-sheet view.
*   **Transaction Management:** Quickly add new transactions or delete existing ones with instant state updates.
*   **Custom Category Manager:** Add custom categories directly from the transaction dialogue or manage them in the profile section. Includes pre-populated default categories:
    *   *Income:* Salary, Freelance, Investments
    *   *Expense:* Food & Dining, Transportation, Shopping, Bills & Utilities, Healthcare
*   **Haptic Feedback & Micro-Interactions:** Premium, tactile app experience with haptic vibrations triggered on crucial actions.
*   **Edge-to-Edge Design:** Fully immersive system-wide dark and light mode UI matching Google's Material Design 3 specifications.

---

## 🛠 Tech Stack & Architecture

SpendWise is developed using standard Native Android components and follows the clean **MVVM (Model-View-ViewModel)** architectural pattern:

*   **Language:** 100% [Kotlin](https://kotlinlang.org/)
*   **Local Database:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite abstraction layer)
*   **Asynchronous Flow:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow/Flow](https://kotlinlang.org/docs/flow.html) for reactive data binding.
*   **UI Components:** ViewBinding, ViewPager2, BottomNavigationView, TabLayout, RecyclerView, and BottomSheetDialogFragment.
*   **Visualizations:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
*   **Design Framework:** [Material Design 3](https://m3.material.io/)
*   **Build System:** Kotlin DSL Gradle (`build.gradle.kts`) with Version Catalog (`libs.versions.toml`) and Google KSP (Kotlin Symbol Processing).

---

## 💻 Developer Installation Guide

Follow these steps to set up the development environment, build the project, and run the application on an emulator or a physical device.

### 📋 Prerequisites
Before you begin, ensure you have the following installed on your machine:
1.  **Android Studio:** Download and install the latest stable version of [Android Studio](https://developer.android.com/studio) (e.g., Ladybug, Koala, or newer).
2.  **Java Development Kit (JDK):** JDK 11 or higher is required. Android Studio comes bundled with a compatible JetBrains Runtime JDK.
3.  **Android SDK:** Android API Level 24 (Android 7.0 Nougat) to API Level 35/36.

---

### 📦 Setup & Import Project

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/KalpeshM13/SpendWise.git
    cd SpendWise
    ```
2.  **Import to Android Studio:**
    *   Open Android Studio.
    *   Click on **File > Open** or select **Open an Existing Project** on the welcome screen.
    *   Navigate to the directory where you cloned the project, select the root folder `SpendWise`, and click **OK**.
3.  **Sync Gradle:**
    *   Android Studio will automatically detect the build files and begin importing.
    *   Wait for the Gradle sync to finish successfully. You should see a green checkmark indicating a successful sync. If it fails, click **Sync Project with Gradle Files** in the top toolbar.

---

### 📱 Running the Application

You can run SpendWise using one of the three methods below. **Using a physical Android device is highly recommended** to fully experience the built-in haptic feedback.

#### Option A: Running on a Physical Device via USB Debugging (Recommended)

1.  **Enable Developer Options on your Phone:**
    *   Go to **Settings > About Phone**.
    *   Find the **Build Number** (sometimes located under *Software Information*) and tap it **7 times** until you see the toast message: *"You are now a developer!"*.
2.  **Enable USB Debugging:**
    *   Go back to **Settings** and find the new **Developer Options** menu.
    *   Scroll down and toggle the switch to turn on **USB Debugging**.
3.  **Connect Device to PC:**
    *   Connect your smartphone to your computer using a high-quality USB cable.
    *   A prompt will appear on your phone screen asking to *Allow USB debugging?*. Check **Always allow from this computer** and tap **Allow**.
4.  **Run the App in Android Studio:**
    *   In the top toolbar of Android Studio, locate the device dropdown (next to the green Run button).
    *   Select your physical smartphone name from the list.
    *   Click the green **Run (Play)** button (or press `Shift + F10` / `Ctrl + R`).
    *   Android Studio will compile the APK, install it on your device, and automatically launch the app.

---

#### Option B: Running on a Physical Device via Wireless Debugging (Alternative Recommended)

If you prefer a cable-free experience, Android 11+ supports debugging over Wi-Fi.

1.  **Requirements:**
    *   Ensure both your computer and your Android device are connected to the **same Wi-Fi network**.
2.  **Enable Wireless Debugging on your Phone:**
    *   Go to **Settings > Developer Options**.
    *   Scroll to find **Wireless Debugging** and toggle it **On**. (Agree to any dialogs asking to allow wireless debugging on this network).
3.  **Pair Your Device:**
    *   Tap **Wireless Debugging** to enter its settings screen.
    *   Tap **Pair device with QR code** (or *Pair device with pairing code*).
4.  **Connect via Android Studio:**
    *   In Android Studio, click the device dropdown menu in the top toolbar and select **Pair Devices Using Wi-Fi**.
    *   A popup will appear. Select either **Pair using QR Code** or **Pair using pairing code**.
    *   Scan the QR code displayed on your computer screen with your phone, or enter the pairing code.
5.  **Run the App:**
    *   Once paired, your phone will show up in the target device dropdown.
    *   Select your device and click the green **Run** button.

---

#### Option C: Running on the Android Studio Emulator (Virtual Device)

If you don't have a physical device, you can create a Virtual Device (AVD).

1.  **Open Device Manager:**
    *   In Android Studio, go to **Tools > Device Manager** or click the Device Manager icon in the right-side panel.
2.  **Create a Virtual Device:**
    *   Click **Create Device** (or the **+** button).
    *   Choose a hardware profile (e.g., *Pixel 8* or *Pixel 7*) and click **Next**.
    *   Select a system image. Download a modern Android version (API 33, 34, or 35 recommended) and click **Next**.
    *   Configure settings if needed, then click **Finish**.
3.  **Start the Emulator:**
    *   In the Device Manager, click the **Play** button next to your newly created virtual device to launch the emulator.
4.  **Run the App:**
    *   Once the emulator has booted up, go to the top toolbar in Android Studio.
    *   Select your emulator from the target device dropdown.
    *   Click the green **Run** button to build and deploy the app.

---

## 📁 Directory Structure

The project follows a standard Android package layout:

```text
app/src/main/java/dev/kalpeshmore/spendwise/
│
├── data/                       # Local Data Layer
│   ├── dao/                    # Room DAOs (TransactionDao, CategoryDao)
│   ├── database/               # Room DB Configuration & Type Converters
│   ├── models/                 # Data entities (Transaction, Category, enums)
│   └── repository/             # Repository layer for database abstractions
│
└── ui/                         # User Interface Layer
    ├── auth/                   # Password & Profile settings fragments
    ├── dashboard/              # Stats overview & analytical charts
    ├── dialogs/                # Modals (Add transactions dialog)
    ├── main/                   # MainActivity, ViewPager configuration
    ├── profile/                # Profile management & custom category configuration
    ├── transactions/           # Transaction listing list adapters & filters
    └── viewModel/              # Shared view models for state management
```

---

## 🤝 Contributing

Contributions are always welcome! If you'd like to improve the app, please feel free to fork the repository, make changes, and submit a Pull Request.

1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.
