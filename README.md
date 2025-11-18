# 🛡️ Clash Upgrade Tracker

A precision timing application designed specifically for **Clash of Clans** players. Stop guessing when your builders will be free—get exact alerts, visual progress tracking, and manage multiple villages in one app.



## ✨ Features

* **🎯 Exact Timing:** Calculates the precise date and time an upgrade will finish based on the duration (Days/Hours/Minutes).
* **⚔️ Clash Themed UI:** A beautiful dark "stone" interface with orange accents, matching the game's aesthetic.
* **📊 Visual Progress Bars:** Watch your upgrades progress with color-coded bars (Orange -> Green).
* **👥 Multi-Account Support:** seamlessly switch between accounts (e.g., *Main Village* vs. *Mini Account*) without losing data.
* **🔔 Smart Alerts:**
    * **High Priority Alarm:** Wakes up the screen with a full-screen pop-up when an upgrade finishes.
    * **Pre-Alert:** Get a notification **5 minutes before** completion to get ready.
    * **Persistent Status:** A sticky notification bar shows your next upcoming upgrade at a glance.
* **✨ Auto-Complete:** Smart suggestions for buildings (Town Hall, X-Bow, Monolith, etc.) to save typing.
* **⚙️ Dynamic Management:** Add, Rename, or Delete village profiles easily.

## 🛠️ Tech Stack

* **Language:** Java
* **Platform:** Android (Min SDK 26, Target SDK 34)
* **Storage:** SharedPreferences & GSON (JSON Parsing)
* **UI:** XML Layouts, Material Design Components, Custom Drawables
* **System Services:** `AlarmManager` (Exact Alarms), `NotificationManager`, `BroadcastReceiver`

## 🚀 Getting Started

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/tharinduxd0/Clash-Upgrade-Tracker
    ```
2.  **Open in Android Studio:**
    File -> Open -> Select the project folder.
3.  **Build & Run:**
    Connect your Android device or start an emulator and hit **Run**.

## 📝 Usage

1.  **Select Village:** Use the dropdown at the top to select or create a new account (e.g., "TharinduXD").
2.  **Enter Time:** Input the upgrade duration (e.g., 14 Days, 12 Hours).
3.  **Description:** Type the building name (Auto-complete will help you!).
4.  **Start:** Click "Start Timer".
5.  **Relax:** The app will notify you 5 minutes before it's done, and ring a loud alarm when the builder is free.

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features (like Hero Equipment tracking or Pet timers), feel free to open an issue or submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---
*This project is a fan-made tool and is not affiliated with, endorsed, sponsored, or specifically approved by Supercell.*
