import React, {useEffect, useRef} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import RootNavigator from './src/navigators/RootNavigator';
import {navigationRef} from './src/navigators/RootNavigation';
import analytics from '@react-native-firebase/analytics';
import CodePush from 'react-native-code-push';
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
  const routeNameRef = useRef();

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
          sound: "default",
          channelId: 'default',
          importance: AndroidImportance.HIGH,
        },
        ios: {
          sound: "default",
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
      navigationRef.current.getCurrentRoute().name != 'NoticeScreen' &&
      navigationRef.current.getCurrentRoute().name != 'ReserveScreen' &&
      navigationRef.current.getCurrentRoute().name != 'LMSScreen' &&
      navigationRef.current.getCurrentRoute().name != 'MyScreen'
    ) {
      navigationRef.current.goBack();
      return true;
    }

    backHandlerClickCount += 1;
    if (backHandlerClickCount < 2) {
      ToastAndroid.show('한번 더 누르시면 앱이 종료돼요.', 1500);
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
        LMSNavigator: {
          initialRouteName: 'LMSScreen',
          screens: {
            LMSScreen: 'lms'
          }
        }
      },
    },
  };

  return (
    <NavigationContainer 
      ref={navigationRef}
      linking={linking} 
      onReady={() => {
        routeNameRef.current = navigationRef.current.getCurrentRoute().name;
      }}
      onStateChange={async () => {
          const previousRouteName = routeNameRef.current;
          const currentRouteName = navigationRef.current.getCurrentRoute().name;

          if (previousRouteName !== currentRouteName) {
            routeNameRef.current = currentRouteName;
            await analytics().logScreenView({
              screen_name: currentRouteName,
              screen_class: currentRouteName,
            });
          }
      }}>
      <StatusBar barStyle={Platform.OS == 'ios' ? 'dark-content' : ''} />
      <RootNavigator />
    </NavigationContainer>
  );
};

export default CodePush({
  checkFrequency: CodePush.CheckFrequency.MANUAL
})(App);