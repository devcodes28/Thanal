<div align="center">
  <h1>🌟 Thanal</h1>
  <p><em>A Digital Platform for the Parents of Autistic Children</em></p>

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-4796CC?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
</div>

---

## 📖 About The Project

**Thanal** is a JavaFX desktop application designed to build a supportive, centralized community for parents, doctors, and supporters of children with autism. By leveraging Google Firestore for real-time backend data management, Thanal provides a safe space for resource sharing, behavioral tracking, and professional consultation.

---

## ✨ Features by Role

### 👨‍👩‍👦 For Parents
* **Behavior Tracking:** Log daily behaviors and suspected triggers to monitor patterns.
* **Exportable Reports:** Download complete behavior histories as CSV files for medical professionals.
* **Financial Aid Portal:** View and apply for government schemes and track application statuses.
* **Educational Resources:** Access curated external educational games for children.
* **Community Engagement:** Read, comment on, and author blog posts to share experiences.
* **Q&A Forum:** Ask questions directly to the community and verified doctors.
* **Doctor Consultations:** Browse available specialists and send direct consultation requests.

### 🩺 For Doctors
* **Blog Management:** Publish and manage informative articles for the community.
* **Consultation Management:** Review, accept, or decline incoming consultation requests.
* **Medical Q&A:** Provide verified answers to questions posted by parents in the forum.
* **Patient Overview:** Access approved patient reports and histories.

### 🤝 For Supporters
* **Knowledge Sharing:** Access and read all community blog posts.
* **Feedback System:** Rate blogs (1-5) to highlight helpful content.
* **Platform Improvement:** Submit recommendations and feature requests directly to admins.

### 🛡️ For Administrators
* **Access Control:** Review, approve, or reject new user registration requests.
* **User Management:** Oversee all approved users and maintain community safety guidelines.

---

## 🚀 Getting Started

### Prerequisites
Before running the application, ensure you have the following installed:
* **Java JDK 17** or higher
* **Apache Maven**
* A **Google Firebase** Project

### 1. Firebase Configuration
This application requires a Google Firebase project to handle real-time database operations.
1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Navigate to **Project Settings > Service accounts**.
3. Click **"Generate new private key"** and download the JSON file.
4. Rename the downloaded file to `serviceAccountKey.json`.
5. Place this file into the project directory: `src/main/resources/com/example/thanal/`

### 2. Local File Storage Setup
The registration process safely saves uploaded verification documents locally.
1. On your `C:` drive, create a new folder named exactly **`Thanal_Uploads`** (Path: `C:/Thanal_Uploads/`).
2. Ensure your user account has write permissions for this directory.

### 3. Build and Run
Open your terminal in the root directory (where `pom.xml` is located) and run:

```bash
# Clean and build the project
mvn clean install

# Run the application
mvn javafx:run