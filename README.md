# Food Mania - Restaurant Review App

An Android app that enables users to rate and leave written reviews for restaurants they visit as well as view reviews left by others.

The architectural pattern used for this project is Model-View-ViewModel (MVVM) with the inclusion of LiveData and designated repositories for the retrieval of data.

## Features
* Create reviews through providing a rating, written review and an optional image
* Tag a location with the review using a map view
* All reviews left by the logged in user are viewable through a designated page
* Users can edit reviews they create
* Ability to favourite restaurants and view them via a favourites page
* Add new restaurants
* Narrow down restaurant results using a search filter
* User accounts (only logged in users can leave reviews, add and favourite restaurants)
* Guests (users without an account) are limited to browsing restaurants and reviews

## Technologies
The project was created using:
* Kotlin
* Android Studio
* Firebase Authentication
* Firebase Cloud Firestore (NoSQL database)
* Firebase Cloud Storage
* Glide Image Loading
* Material Design
* Lifecycle Libraries
* Google Maps API (Maps SDK for Android)

## API Reference

#### Google Maps API Key

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `google_maps_key` | `string` | Your Maps SDK API key |

## Usage
* Requires generation of a google-services.json Firebase config file which can be created through the 'Firebase Console'
* Move the config file into the app-level root directory of the app
* Set up Firebase Authentication, Firestore and Cloud Storage
* Requires an API key for 'Maps SDK for Android' via the 'Google Cloud Console'
