# AstroLearn – AI Space Explorer 🌌🚀

AstroLearn is an AI-powered Android app designed to help students explore space science in a fun, interactive way. It simplifies complex astronomy topics like black holes, galaxies, and exoplanets using natural language generation from AI models via OpenRouter.

---

## 📱 Features
- 🔍 Ask questions and get AI-generated explanations (powered by OpenRouter)
- 🌍 Browse curated space topics (e.g., planets, stars, space missions)
- 🧠 Take simple quizzes to reinforce learning
- ⭐ Bookmark responses and access them offline
- ⚡ No login required, fast and lightweight

---

## 🔧 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Local Storage:** Room Database
- **Networking:** Ktor Client (if present)
- **AI Integration:** OpenRouter API (API key required)
- **Media Handling:** Coil for image loading, Lottie for animations

---

## 🚀 Getting Started

### Prerequisites
- Android Studio
- Android SDK

### Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone https://github.com/MrThe-KotlinDroid/AstroLearn.git
   ```
2. **Add your OpenRouter API key:**
   - Create or edit the `local.properties` file in the project root.
   - Add the following line:
     ```
     OPENROUTER_API_KEY=your_api_key_here
     ```
   - **Note:** Do NOT commit `local.properties` to version control. It contains sensitive information.
3. **Open the project in Android Studio and build/run.**

---

## 👨‍💻 Team Members
- **Md. Abrar Hamim** – Lead Developer
- **Nure Salma Nuri** – Collaborator

---

## 🔗 Project Link
[https://github.com/MrThe-KotlinDroid/AstroLearn](https://github.com/MrThe-KotlinDroid/AstroLearn)