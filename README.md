Thanal - Autism Support Community
Thanal is a JavaFX desktop application designed to build a supportive community for parents, doctors, and supporters of children with autism. It provides a centralized platform for resource sharing, communication, and support, leveraging Google Firestore for backend data management.

Features
The application provides distinct dashboards and functionalities based on user roles:

1. Parent Dashboard
Behavior Tracking: Log daily behaviors and suspected triggers for your child.

Export Logs: Download the complete behavior history as a CSV file for analysis or sharing with professionals.

Financial Aid: View and apply for fictional government schemes and track application status.

Educational Games: Access links to external educational games for children.

Community Blogs: Read, comment on, and write new blog posts to share experiences and advice.

Q&A Forum: Ask questions to the community (including doctors) and answer questions from other parents.

Doctor Consultation: View a list of available doctors and send consultation requests.

2. Doctor Dashboard
Blog Management: Write, publish, and manage your own articles and blog posts.

Consultation Requests: View and manage incoming consultation requests from parents (Accept/Decline).

Q&A Forum: Answer questions posted by parents in the community forum.

Patient Reports: Access a list of approved patients (currently mock data).

3. Supporter Dashboard
Read Blogs: View and read all blogs posted by the community.

Rate Blogs: Provide ratings (1-5) on blogs.

Submit Recommendations: Suggest new features or improvements for the Thanal platform.

4. Admin Dashboard
User Approval: View and approve or reject new user registration requests.

User Management: View a list of all approved users and delete users from the system (both pending and approved).

Core Features
Secure Registration: A detailed registration process that collects role-specific information and documents (e.g., Aadhaar, medical license).

Role-Based Login: A login system that directs users to their specific dashboard.

Firebase Integration: All data (users, blogs, questions, logs) is stored and retrieved in real-time from Google Firestore.

Technologies Used
Frontend: JavaFX (using FXML for UI layout)

Backend: Google Firebase (Firestore)

Language: Java 17

Build System: Apache Maven


Prerequisites
Java JDK 17 or higher.

Apache Maven.

Google Firebase Project.

1. Firebase Setup
This application requires a Google Firebase project to function.

Go to the Firebase Console.

Create a new project.

In your project, go to Project Settings > Service accounts.

Click "Generate new private key" and save the downloaded JSON file.

Rename this file to serviceAccountKey.json.

Place this serviceAccountKey.json file into the project's src/main/resources/com/example/thanal/ directory.

2. Local File Storage Setup
The registration process saves uploaded documents to a local folder.

On your C: drive, create a new folder named Thanal_Uploads (path: C:/Thanal_Uploads/).

Ensure your user account has write permissions for this folder.

3. Build and Run
Open a terminal or command prompt in the root directory of the project (where pom.xml is located).

Build the project using Maven:

Bash

mvn clean install
(or ./mvnw clean install on Linux/macOS, mvnw.cmd clean install on Windows)

Run the application using the Maven JavaFX plugin:

Bash

mvn javafx:run
(or ./mvnw javafx:run / mvnw.cmd javafx:run)

4. Default Admin Login
A mock admin account is included for testing.

Role: Admin

Email: admin@thanal.com

Password: pass
