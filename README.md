# 📍 Weather Forecast & Location Portal

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![WebGL](https://img.shields.io/badge/WebGL-Three.js-blue.svg)](https://threejs.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

An interactive, high-performance **Location & Weather Forecast Portal** built with a lightweight native Java backend (`com.sun.net.httpserver`) and a modern glassmorphic WebGL frontend powered by **Three.js**.

---

## ✨ Features

- ✨ **Interactive 3D WebGL Background**: Dynamic particle starfield background with mouse-parallax interaction using Three.js.
- 📍 **Hierarchical Cascading Location Selector**: Dynamic selection flows from **Country ➔ State ➔ City**, pre-populated with major global regions (India, United States, United Kingdom, Canada, Japan, Germany, France).
- 🛰️ **Auto-GPS Geolocation**: Detect user coordinates automatically via Browser Geolocation API and reverse-geocode to nearest city/region.
- 💎 **Modern Glassmorphic UI**: Ultra-sleek glass panel design with dynamic 3D card tilt effects, custom scrollbars, and responsive typography (Outfit & Inter fonts).
- ⚡ **Zero-Dependency Java Backend**: Uses Java's built-in `HttpServer` with RESTful API endpoints, CORS support, and static asset serving.
- 🚀 **One-Click Windows Launcher**: Pre-configured `run.bat` script for compiling and starting the application server instantly.

---

## 🛠️ Technology Stack

| Layer | Technologies / Libraries |
| :--- | :--- |
| **Backend** | Java 21+ (`com.sun.net.httpserver`), REST JSON Endpoints |
| **Frontend UI** | HTML5, Modern CSS3 (Vanilla CSS with CSS Variables & Glassmorphism) |
| **3D Graphics** | Three.js (r128 WebGL Engine) |
| **Icons & Fonts** | FontAwesome 6, Google Fonts (`Outfit`, `Inter`) |
| **Automation** | Batch Script (`run.bat`) |

---

## 📁 Project Architecture

```
Wheather-forecast-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/locationapp/
│       │       ├── LocationLoginServer.java  # HTTP Server setup, routing & static server
│       │       ├── LocationService.java      # Location dataset provider (Countries/States/Cities)
│       │       └── AuthService.java          # Authentication & token verification handler
│       └── resources/
│           └── public/
│               ├── css/
│               │   └── styles.css           # Modern Glassmorphic styling & layout engine
│               ├── js/
│               │   ├── 3d-engine.js         # Three.js starfield background canvas controller
│               │   └── app.js               # Cascading dropdown logic, GPS & API handlers
│               └── index.html               # Main viewport & interactive portal UI
├── bin/                                     # Compiled Java bytecodes (generated)
├── run.bat                                  # Windows automated compile & run script
└── README.md                                # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher (JDK 21 recommended).
- **Web Browser**: Any modern WebGL-enabled browser (Chrome, Edge, Firefox, Safari).

### Quick Start (Windows)

Simply double-click or run `run.bat` in your terminal:

```cmd
.\run.bat
```

The script will:
1. Compile all Java source files into the `bin` directory.
2. Launch the native Java Desktop GUI Application window.

### Manual Build & Run

If you prefer building manually from terminal:

1. **Compile Java Files**:
   ```bash
   mkdir bin
   javac -d bin src/main/java/com/locationapp/*.java
   ```

2. **Run Desktop GUI App**:
   ```bash
   java -cp bin com.locationapp.LocationAppGUI
   ```

---

## 🔌 API Endpoints

| Method | Endpoint | Description | Response Format |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/locations` | Fetches full dataset of countries, states, and cities | JSON |
| `POST` | `/api/login` | Validates location selection and returns access session token | JSON |
| `GET` | `/*` | Serves static frontend assets (`index.html`, CSS, JS, Images) | Static Files |

### Sample Login Payload (`POST /api/login`)

```json
{
  "username": "user",
  "password": "password",
  "country": "India",
  "state": "Maharashtra",
  "city": "Chh. Sambhajinagar"
}
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).