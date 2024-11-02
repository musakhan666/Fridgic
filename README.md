Fridgic

Fridgic is an intuitive inventory management app designed to help users efficiently track and manage food items stored in their fridge, freezer, and pantry. The app reduces food waste by reminding users of expiration dates, allowing them to add items with details like location, quantity, and expiration, and enabling easy sorting and filtering.

Features

Add New Items: Add food items to your inventory, specifying details like name, location (fridge, freezer, pantry), quantity, and expiration date.
Image Upload: Optionally upload an image of the item to keep a visual record.
Expiration Tracking: Automatic reminders to track expiration dates with a default date set to seven days from the current date.
Filter & Sort: Easily filter items by storage location and sort by name, expiration date, or quantity.
User Account: Secure login and personalized inventory saved under each user's unique Firebase user ID.
Cloud Storage: Data and images are stored securely using Firebase Firestore and Firebase Storage.
Technology Stack

Kotlin: The primary language for Android development.
Jetpack Compose: For building declarative, modern UI.
Firebase Firestore: Cloud-based NoSQL database for storing inventory data.
Firebase Storage: For secure image storage.
Dagger Hilt: For dependency injection.
Coil: For image loading in Compose.
Setup

Prerequisites
Android Studio (latest version recommended)
Firebase account with Firestore and Storage configured
Installation
Clone this repository:
bash
Copy code
git clone https://github.com/musakhan666/Fridgic.git
Open the project in Android Studio.
Configure Firebase:
Add your google-services.json file to the app directory.
Ensure Firestore and Storage are set up with appropriate rules.
Build and run the app on your preferred device or emulator.
