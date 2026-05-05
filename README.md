# LakeNote 📝

LakeNote is a modern, feature-rich Kotlin Multiplatform (KMP) note-taking and task management application. It follows Clean Architecture principles to ensure a scalable, testable, and maintainable codebase shared between Android and iOS.

## 🚀 Features

- **Global Navigation**: Persistent bottom navigation bar and a sliding drawer for quick access to all app sections.
- **Task Management**: Dedicated tasks screen with status tracking (Pending/Completed) and interactive checklists.
- **Note Management**: Create, edit, search, pin, and archive notes.
- **Dynamic Theming**: Premium design with full Dark/Light mode support, featuring a sleek Prussian Blue palette.
- **Priority System**: Organize your work with Urgent, High, Medium, and Low priority levels.
- **Clean Architecture**: Decoupled layers (Data, Domain, Presentation) for maximum code reuse and reliability.

## 🏗️ Architecture

The project is structured into several layers to enforce separation of concerns:

### 1. Domain Layer (`shared/commonMain`)
The heart of the application. It contains:
- **Models**: Pure data classes (e.g., `Note`, `Task`) that are framework-agnostic.
- **Use Cases**: Single-responsibility classes that encapsulate business logic (e.g., `SaveNoteUseCase`, `GetNotesUseCase`).
- **Repository Interfaces**: Contracts defined for data operations, ensuring the domain doesn't depend on implementation details.

### 2. Data Layer (`shared/commonMain`)
Handles data persistence and retrieval:
- **Repository Implementations**: Logic for managing data between local storage and the rest of the app.
- **SQLDelight**: Used for type-safe database operations shared across platforms.
- **Mappers**: Transform data models into domain models.

### 3. Presentation Layer (`androidApp`)
Platform-specific UI implementation:
- **Jetpack Compose**: Declarative UI for a modern Android experience.
- **MVI Pattern**: Uses `StateFlow` and `SharedFlow` to manage UI state and one-time events in a predictable way.
- **Koin DI**: Dependency injection to wire up ViewModels, Use Cases, and Repositories.

## 🛠️ Technology Stack

- **Language**: Kotlin 2.x
- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Jetpack Compose (Android) / SwiftUI (iOS)
- **DI**: Koin
- **Database**: SQLDelight
- **Date/Time**: Kotlinx-datetime
- **Navigation**: Jetpack Compose Navigation

## 📱 Getting Started

### Android
To build and run the Android app:
```bash
./gradlew :androidApp:assembleDebug
```

### iOS
1. Navigate to the `iosApp` directory.
2. Open the `.xcodeproj` or `.xcworkspace` in Xcode.
3. Run on a simulator or physical device.

## 🤖 CI/CD
The project includes a GitHub Action to automatically build the Android APK on every push or pull request to the `main` branch. You can find the built artifacts in the "Actions" tab of the repository.

---
*Built with ❤️ using Kotlin Multiplatform.*
