# 🚀 YooSpace Android  
<p align="center">
  <img src="https://github.com/user-attachments/assets/9a3f809a-89a2-4a89-b3e1-8df3ee273d9d" width="200" alt="YooSpace Logo" />
</p>


*A modern social media Android app built with **Kotlin**, **Jetpack Compose**, and **Hilt**.  
Inspired by Instagram & Twitter — designed to connect users, share posts, chat, and create groups.*  

---

## ✨ Features  

The YooSpace Android app comes with a variety of features powered by a **Node.js + MongoDB backend** and modern **Android tools**:  

- 🔑 **Authentication** — Login, Register, and secure JWT-based sessions  
- 👤 **Profile Management** — Edit profile, view followers/following, manage account  
- 📝 **Your Content** — View your own posts and liked posts  
- 👥 **User Profiles** — Visit other users’ profiles, follow/unfollow, send messages, view their posts and followers  
- 📰 **Feed** — Browse posts, like, comment, and engage with content  
- 📸 **Post Creation** — Upload media (images via Cloudinary) or share text-only posts  
- 💬 **Messaging** — Real-time 1:1 and group chat using Socket.io  
- 👨‍👩‍👧‍👦 **Groups** — Create groups, join existing ones, and chat with members  
- 🔔 **Notifications** — Stay updated with likes, comments, follows, and group invites  
- 🌐 **Discover People** — Explore and follow new users  
- 🌙 **Dark & Light Mode** — Seamless theme switching for better user experience 
- ✨ **Shimmer Effects** — Beautiful loading animations for better UX  
- 🎨 **Modern UI & Animations** — Smooth transitions and engaging design  
---

## 📱 Screens  

The app is structured with clear modules under `ui/`.  
Below are screenshots representing each feature (you can add them in corresponding sections later).  

### 🔑 Auth  
- **Login Screen**  
- **Register Screen**  

### 📰 Feed  
- Main feed of posts  
- Like, comment, and view details  

### 👤 Profile  
- View and edit profile  
- Followers & Following list  
- User posts & liked posts  

### ✍️ Post  
- Upload a new post  
- Add captions, tags, media  
- Like & comment on posts  

### 💬 Message  
- 1:1 chat with friends  
- Group chat support  

### 👥 Discover People  
- Explore & follow new users  

### 🔔 Notifications  
- Real-time updates for likes, comments, follows  

### 🎨 Common & Theme  
- Custom UI components & styles  
- Consistent app-wide design system  

### ✨ Shimmer Components  
- Beautiful shimmer loading states for smooth UX  

---

## Installation

To install Yoo Notes, follow these steps:
1. Download the APK file from []().
2. Open the APK file on your Android device.
3. Follow the on-screen instructions to install the app.

## Usage
Once installed, you can start using Yoo Notes by:
1. Launching the app from your device's home screen.
2. Creating, organizing, and managing your notes seamlessly.
3. Utilizing the search and category features to quickly access your information.

---

## 🛠️ Tech Stack  

This project leverages modern **Android** and **Backend** technologies:

### **Frontend (Android App)**  
- ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)  
- ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)  
- ![Hilt](https://img.shields.io/badge/Hilt-D00000?style=for-the-badge&logo=dagger&logoColor=white)  
- ![Retrofit](https://img.shields.io/badge/Retrofit-009688?style=for-the-badge&logo=square&logoColor=white)  
- ![Coil](https://img.shields.io/badge/Coil-FF6F00?style=for-the-badge&logo=android&logoColor=white)  

### **Backend (API + Realtime)**  
- ![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)  
- ![Express.js](https://img.shields.io/badge/Express.js-000000?style=for-the-badge&logo=express&logoColor=white)  
- ![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)  
- **JWT** for authentication  
- **bcrypt** for password hashing  
- **zod** for validation  
- **Multer + Cloudinary** for media storage  
- **Socket.io** for realtime chat  

---

## ⚙️ Setup  

### Prerequisites  
- Android Studio (Latest version)  
- Node.js & npm/yarn  
- MongoDB  

### Clone & Run  

```bash
# Clone the repo
git clone https://github.com/YooAshu/yoospace-android.git
cd yoospace-android

# Open in Android Studio and run on emulator/device
```
### 🔗 Backend Setup if you want to update backend 

The **YooSpace Android app** requires the backend API to run.  
The full **Node.js + Express + MongoDB backend** (along with the web frontend) is available here:  

👉 [Yoo_Space Backend & Frontend Repository](https://github.com/YooAshu/Yoo_Space)  

Follow the instructions in that repo to:  

1. **Set up the backend server**  
   ```bash
   npm install
   npm run dev
   ```
2. Configure environment variables (.env) with:

MongoDB connection URI

JWT secrets and expiry times

Cloudinary credentials

3. Run the backend server and ensure it is active before launching the Android app.

4. **Update the Base URL** in the Android app to point to your backend instance (local or deployed).  
   You will need to change the API base URL in the following files:  

   - `data/api/RetrofitInstance.kt`  
   - `data/api/TokenAuthenticator.kt`  
   - `ui/auth/AuthViewModel.kt`  
   - `utils/AppContext.kt`  

---

## Feedback
Your feedback matters! If you encounter any issues or have ideas to enhance Yoo Notes, feel free to reach out.

## 🤝 Contribution

Contributions are welcome! 🚀
Feel free to fork, open issues, or submit PRs to improve YooSpace Android.

