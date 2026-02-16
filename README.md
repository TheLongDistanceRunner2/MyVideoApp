# MyVideoApp
Android app leveraging Vonage Video API for video calls.

<img src="https://github.com/user-attachments/assets/1d59950b-88e8-450b-8bbc-092be50ecddb" width="180" />
<img src="https://github.com/user-attachments/assets/c5d50d3f-e8e7-4f91-8e76-16c95909299d" width="180" />
<img src="https://github.com/user-attachments/assets/8f809a16-3446-4862-a14b-9577f5d45ccb" width="180" />
<img src="https://github.com/user-attachments/assets/0e74a9f4-2c7b-4639-8a30-573192121867" width="180" />
<img src="https://github.com/user-attachments/assets/a60cdb01-258a-43fa-976c-0c42c3c69439" width="180" />

## Setup instructions
This project supports Android devices with SDK v24 and higher.

To enable all features, **ensure the configuration file contains the required data**.

The configuration file is located at: `app/src/main/java/com/example/myvideoapp/data/VideoApiConfig.kt`

Provide the following credentials generated in your account (log in to the API Dashboard and access the Video API Playground at https://tools.vonage.com/video/playground):
- APP_ID
- SESSION_ID
- TOKEN

## Assumptions and design decisions
In this project, I decided to utilize the **MVI (Model-View-Intent)** architecture. This approach allows for a clean separation of business logic from core Android functionality, such as views and navigation. Additionally, it ensures the project follows the most recent recommended architecture patterns for Android development.

The project is divided into two main packages:
- data: Contains the data sources and logic (including `VonageVideoRepository.kt`).
- ui: The layer responsible for presenting data and handling user interactions.

The core logic related to the Vonage Video API is encapsulated in: `app/src/main/java/com/example/myvideoapp/data/repository/VonageVideoRepository.kt`

This centralized approach makes it extremely easy to understand the concepts and possibilities provided by the API, while also simplifying the process of adding further functionality to the application.

## Time spent on the project
I spent approximately 4 hours developing this project. My primary focus was to **demonstrate the crucial functionality required to enable video calls**. While the UI could certainly be further refined, it is currently sufficient to showcase the app's core features and capabilities.

## Known limitations and issues
Since the time spent on the project was relatively short, the app requires several improvements:
- Moving all hardcoded strings to the strings.xml file.
- Enhancing the UI/UX.
- Handling all edge cases.
- Dependency Injection (e.g., for injecting `VonageVideoRepository` into `VideoViewModel`) to ensure modularity and ease of testing.

There are also some known issues that require fixing:
- Handling single-permission grant scenarios (currently not working as intended).
- A display issue with the video stream of the second person joining the session (this does not occur if the other user is already present in the session).
Moreover, the problem is connected only with video - the voice is received without any problems.
