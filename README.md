# GaiaTrace - Carbon Footprint Calculator

GaiaTrace is a modern, eco-friendly Android application built with Kotlin and Jetpack Compose. It helps users track their daily carbon footprint based on transportation, energy usage, and food habits, specifically tailored with scientific emission factors for the Indian context.

## 🌿 Features

- **Accurate Calculations**: Uses India-specific grid emission factors (0.82 kg CO₂/kWh) and IPCC standards.
- **Dynamic UI**: Input fields adapt based on the selected transport type (e.g., Petrol vs. Electric).
- **Detailed Breakdown**: Shows specific CO₂ contributions from Transport, Electricity, and Food.
- **Dark Mode Support**: A beautiful, eye-friendly dark theme that adapts to system settings.
- **Eco-Friendly Tips**: Provides dynamic suggestions based on your footprint level (Low, Medium, High).
- **Minimalist Design**: Clean, modern interface with a custom-drawn logo.

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Modern Android Development (MAD) practices
- **Icons & Graphics**: Custom Canvas-drawn logo and Material Design icons

## 📊 Scientific Factors Used

| Category | Type | Emission Factor |
| :--- | :--- | :--- |
| **Transport** | Petrol Bike | 0.134 kg CO₂ / km |
| **Transport** | Petrol/Diesel Car | 0.20 kg CO₂ / km |
| **Transport** | Public Transport | 0.05 kg CO₂ / km |
| **Energy** | Electricity (Grid) | 0.82 kg CO₂ / kWh |
| **Food** | Vegetarian | 1.5 kg / day |
| **Food** | Mixed | 2.5 kg / day |
| **Food** | Non-Vegetarian | 3.5 kg / day |

## 🚀 How to Run

1. Clone the repository: `git clone https://github.com/LimbuSoda-lep/GaiaTrace.git`
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync the Gradle files.
4. Run the app on an emulator or a physical device (Minimum SDK: 24).

## 📦 Generating the APK

To generate a shareable APK:
1. Go to **Build** menu in Android Studio.
2. Select **Build Bundle(s) / APK(s)** > **Build APK(s)**.
3. Once the build is complete, click **Locate** in the notification to find your `app-debug.apk`.

---
Developed for College Project & Viva Demonstration. 🌍
