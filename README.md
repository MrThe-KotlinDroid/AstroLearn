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
- OpenRouter API key (sign up at https://openrouter.ai/)

### Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone https://github.com/MrThe-KotlinDroid/AstroLearn.git
   ```

2. **Configure API Key (REQUIRED):**
   - Create or edit the `local.properties` file in the project root
   - Add your OpenRouter API key:
     ```properties
     OPENROUTER_API_KEY=your_api_key_here
     ```
   - **⚠️ SECURITY NOTE:** 
     - Never commit `local.properties` to Git (it's already in `.gitignore`)
     - Never hardcode API keys in source code
     - The app uses `BuildConfig.OPENROUTER_API_KEY` to access the key securely

3. **Build Configuration:**
   - The app automatically reads the API key from `local.properties`
   - It's injected as a `BuildConfig` field at compile time
   - Usage in code: `private val apiKey = BuildConfig.OPENROUTER_API_KEY`

4. **Open the project in Android Studio and build/run.**

### 🔑 API Key Management
- **For Developers:** Add `OPENROUTER_API_KEY=your_key_here` to `local.properties`
- **For Contributors:** Get your own API key from OpenRouter - don't share keys
- **For Production:** Use secure environment variables or secret management

---

## 👨‍💻 Team Members
- **Md. Abrar Hamim** – Lead Developer
- **Nure Salma Nuri** – Collaborator

---

## 🔗 Project Link
[https://github.com/MrThe-KotlinDroid/AstroLearn](https://github.com/MrThe-KotlinDroid/AstroLearn)