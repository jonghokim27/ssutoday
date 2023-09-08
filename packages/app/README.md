# ssutoday-app
## Development environment
- Node.js 16.16.0
- XCode 14.2
- Android Studio Flamingo

## Project Dependencies
- @notifee/react-native: 7.8.0
- @react-native-async-storage/async-storage: 1.19.1
- @react-native-community/netinfo: 9.4.1
- @react-native-firebase/app: 18.3.1
- @react-native-firebase/messaging: 18.3.1
- @react-navigation/bottom-tabs: 6.5.8
- @react-navigation/native: 6.1.7
- @react-navigation/native-stack: 6.9.13
- @react-navigation/stack: 6.3.17
- axios: 1.4.0
- lottie-react-native: 6.2.0
- moment: 2.29.4
- react: 17.0.2
- react-native: 0.68.2
- react-native-date-picker: 4.2.14
- react-native-device-info: 10.8.0
- react-native-fast-image: 8.6.3
- react-native-gesture-handler: 2.12.1
- react-native-haptic-feedback: 2.0.3
- react-native-modal: 13.0.1
- react-native-reanimated: 3.4.1
- react-native-safe-area-context: 4.7.1
- react-native-safe-area-view: 1.1.1
- react-native-screens: 3.23.0
- react-native-shadow-2: 7.0.8
- react-native-svg: 13.13.0
- react-native-webview: 13.4.0
- <strike>react-native-wheely: 0.6.0</strike>


## Project setup
### 1. Set up the development environment (Both iOS, Android)
https://reactnative-archive-august-2023.netlify.app/docs/0.68/environment-setup
  
### 2. Install Dependencies
```
npm install
cd ios && pod install
```

- For Apple Silicon users
  ```
  cd ios && arch -x86_64 pod install
  ```

### 3. Run in Debug mode
- Android
  ```
  npx react-native run-android
  ```

- iOS
  ```
  npx react-native run-ios
  ```

### 4. Configure settings (Javascript)
- src/constants/settings.js
  ```
  import {Platform} from 'react-native';

  const API_BASE_URL = '<api base url>';

  const APP_VERSION = Platform.OS == 'android' ? '2.0.0' : '2.0.0';

  const CLIENT_KEY = require("<client key path>").key;

  export {API_BASE_URL, APP_VERSION, CLIENT_KEY};

  ```
