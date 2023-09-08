import React, {useEffect} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import RootNavigator from './src/navigators/RootNavigator';
import {navigationRef} from './src/navigators/RootNavigation';
import {
  BackHandler,
  ToastAndroid,
  StatusBar,
  Text,
  Linking,
} from 'react-native';
import notifee, {AndroidImportance, EventType} from '@notifee/react-native';
import messaging from '@react-native-firebase/messaging';

let backHandlerClickCount = 0;

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

const App = () => {
  useEffect(() => {
    // back handle exit app
    BackHandler.addEventListener('hardwareBackPress', backButtonHandler);
    return () => {
      BackHandler.removeEventListener('hardwareBackPress', backButtonHandler);
    };
  }, []);

  useEffect(() => {
    const unsubscribe = messaging().onMessage(async remoteMessage => {
      await notifee.displayNotification({
        title: remoteMessage.notification.title,
        body: remoteMessage.notification.body,
        data: remoteMessage.data,
        android: {
          channelId: 'default',
          importance: AndroidImportance.HIGH,
        },
        ios: {
          foregroundPresentationOptions: {
            badge: true,
            sound: true,
            banner: true,
            list: true,
          },
        },
      });
    });

    return unsubscribe;
  }, []);

  const backButtonHandler = () => {
    if (
      navigationRef.current.canGoBack() &&
      navigationRef.current.getCurrentRoute().name != 'WelcomeScreen' &&
      navigationRef.current.getCurrentRoute().name != 'NoticeScreen'
    ) {
      navigationRef.current.goBack();
      return true;
    }

    backHandlerClickCount += 1;
    if (backHandlerClickCount < 2) {
      ToastAndroid.show('한번 더 누르시면 앱이 종료됩니다.', 1500);
      setTimeout(() => {
        backHandlerClickCount = 0;
      }, 1500);
      return true;
    } else {
      BackHandler.exitApp();
    }
  };

  // useEffect(() => {
  //   Linking.getInitialURL() // 최초 실행 시에 Universal link 또는 URL scheme요청이 있었을 때 여기서 찾을 수 있음
  //     .then(value => {
  //       if(!value)
  //         return;

  //       navigationRef.current.navigate("MainScreen", {screen: "LoginScreen"});
  //       console.log('최초 실행 ', value);
  //     });

  //   Linking.addEventListener('url', e => {
  //     // 앱이 실행되어있는 상태에서 요청이 왔을 때 처리하는 이벤트 등록
  //     console.log(navigationRef.current.getCurrentRoute().name);
  //     console.log('실행 중 ', e.url);
  //     const route = e.url.replace(/.*?:\/\//g, '');
  //   });

  //   return () => {
  //     Linking.removeEventListener('url', e => {});
  //   };
  // });

  useEffect(() => {
    // Assume a message-notification contains a "type" property in the data payload of the screen to open

    messaging().onNotificationOpenedApp(remoteMessage => {
      if (remoteMessage?.data.link) {
        Linking.openURL(remoteMessage.data.link);
      }
      // console.log(
      //   'Notification caused app to open from background state:',
      //   remoteMessage.notification,
      // );
    });

    // // Check whether an initial notification is available
    messaging()
      .getInitialNotification()
      .then(remoteMessage => {
        if (remoteMessage?.data.link) {
          Linking.openURL(remoteMessage.data.link);
        }
      });

    notifee.onForegroundEvent(e => {
      if (e.type != 3 && e.type != 0) {
        if (e?.detail?.notification?.data?.link) {
          Linking.openURL(e?.detail?.notification?.data?.link);
        }
      }
    });
  });

  const linking = {
    prefixes: ['ssutoday://'],
    config: {
      initialRouteName: 'MainScreen',
      screens: {
        NoticeNavigator: {
          initialRouteName: 'NoticeScreen',
          screens: {
            NoticeWebViewScreen: 'notice/:idx',
          },
        },
        ReserveNavigator: {
          initialRouteName: 'ReserveScreen',
          screens: {
            ReserveListScreen: 'reserve/list',
          },
        },
      },
    },
  };

  return (
    <NavigationContainer ref={navigationRef} linking={linking}>
      <StatusBar barStyle={Platform.OS == 'ios' ? 'dark-content' : ''} />
      <RootNavigator />
    </NavigationContainer>
  );
};

export default App;
