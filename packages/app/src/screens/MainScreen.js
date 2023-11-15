import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Linking,
  Platform,
  AppState,
  Alert,
} from 'react-native';
import DeviceInfo from 'react-native-device-info';
import size from '../constants/size';
import Loading from '../components/Loading';
import BottomSafe from '../components/BottomSafe';
import NetInfo from '@react-native-community/netinfo';
import Swal from '../components/Swal';
import {checkVersion} from '../apis/version';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {profile} from '../apis/user';
import messaging from '@react-native-firebase/messaging';
import {PermissionsAndroid} from 'react-native';
import {register} from '../apis/device';
import notifee, {AndroidImportance} from '@notifee/react-native';
import CodePush from 'react-native-code-push';

class MainScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      loadingMessage: '',
    };
    // this.appState = AppState.currentState;
  }

  async checkInternet() {
    await this.loading.show();
    this.loading.message('인터넷 연결을 확인하고 있어요.');

    let netInfo = await NetInfo.fetch();
    // console.log(netInfo);
    if (!netInfo.isConnected) {
      // if(0){
      this.loading.hide();
      this.swal.show(
        'warning',
        '인터넷 연결 없음',
        '인터넷에 연결되어 있지 않아요.\n인터넷 연결을 확인해 주세요.',
        '재시도',
        async () => {
          await this.swal.hide();
          await this.checkInternet();
        },
      );
      return;
    }
    await this.checkVersion();
  }

  async checkVersion() {
    await this.loading.show();
    this.loading.message('앱이 최신 상태인지 확인하고 있어요.');
    let checkVersionRes = await checkVersion();

    if (checkVersionRes.statusCode == 'SSU0000') {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '재시도',
        async () => {
          await this.swal.hide();
          await this.checkVersion();
        },
      );
      return;
    } else if (checkVersionRes.statusCode == 'SSU2071') {
      this.loading.hide();
      this.swal.show(
        'warning',
        '업데이트 필요',
        '원활한 서비스 제공을 위해\n앱을 최신 버전으로 업데이트해 주세요.',
        '업데이트',
        async () => {
          if (Platform.OS == 'ios') {
            Linking.openURL(
              'https://apps.apple.com/kr/app/ssutoday-%EC%8A%88%ED%88%AC%EB%8D%B0%EC%9D%B4/id1641227854',
            );
          } else {
            Linking.openURL(
              'https://play.google.com/store/apps/details?id=com.ssutoday',
            );
          }
        },
      );
      return;
    } else if (checkVersionRes.statusCode != 'SSU2070') {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          checkVersionRes.statusCode,
        '재시도',
        async () => {
          await this.swal.hide();
          await this.checkVersion();
        },
      );
      return;
    }

    let update;
    try{
      update = await CodePush.checkForUpdate();
    } catch(e){
      await this.checkUser();
      return;
    }

   	if (update) {
      this.loading.message("새로운 업데이트가 발견되었어요.");

      let newPackage = await update.download((progress) => {
        this.loading.message("업데이트를 다운로드하고 있어요.\n잠시만 기다려주세요.\n" + Math.floor(progress.receivedBytes / 1024) + "/" + Math.floor(progress.totalBytes / 1024) + "KB");
      });
      
      this.loading.message("업데이트를 적용하고 있어요.");
      await newPackage.install();
      await CodePush.notifyAppReady();
      await CodePush.restartApp();
      return;
   	}

    await this.checkUser();
  }

  async checkUser() {
    await this.loading.show();
    this.loading.message('인증 상태를 점검하고 있어요.');
    let uuid = await DeviceInfo.getUniqueId();
    let pushToken = null;
    if (Platform.OS == 'ios') {
      const authStatus = await messaging().requestPermission();
      const enabled =
        authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
        authStatus === messaging.AuthorizationStatus.PROVISIONAL;

      if (enabled) {
        pushToken = await messaging().getToken();
      }
    } else if (Platform.OS == 'android') {
      if (Platform.Version >= 33) {
        let authStatus = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
        );
        if (authStatus == 'granted') {
          pushToken = await messaging().getToken();
        }
      } else {
        pushToken = await messaging().getToken();
      }
    }

    await notifee.requestPermission();
    if (Platform.OS == 'android') {
      await notifee.createChannel({
        id: 'default',
        name: '기본',
        importance: AndroidImportance.HIGH,
      });
    }
    let accessToken = await AsyncStorage.getItem('accessToken');
    let refreshToken = await AsyncStorage.getItem('refreshToken');
    if (accessToken && refreshToken) {
      let profileRes = await profile();
      if (profileRes.statusCode == 'SSU0000') {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '재시도',
          async () => {
            await this.swal.hide();
            await this.checkUser();
          },
        );
        return;
      } else if (profileRes.statusCode == 'SSU2020') {
        //인증됨
        let notificationEnabled = await AsyncStorage.getItem(
          'notificationEnabled',
        );
        if (
          (notificationEnabled == null || notificationEnabled == 'true') &&
          pushToken != null
        ) {
          await register(Platform.OS, uuid, pushToken);
        }

        await AsyncStorage.setItem('profile', JSON.stringify(profileRes.data));
        this.navigation.navigate('NoticeNavigator');
      } else if (profileRes.statusCode == 'SSU4001') {
        //인증 만료
        await AsyncStorage.removeItem('accessToken');
        await AsyncStorage.removeItem('refreshToken');
        await AsyncStorage.removeItem('profile');
        this.loading.hide();
        this.swal.show(
          'warning',
          '인증 만료',
          '장기간 앱을 이용하지 않았거나,\n학적 정보에 변동이 발생하여\n인증이 만료되었어요.\n다시 로그인해주세요.',
          '확인',
          async () => {
            await this.swal.hide();
            this.navigation.navigate('LoginNavigator');
          },
        );
        return;
      } else {
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
            profileRes.statusCode,
          '재시도',
          async () => {
            await this.swal.hide();
            await this.checkUser();
          },
        );
        return;
      }
    } else {
      this.navigation.navigate('LoginNavigator');
    }
  }

  async componentDidMount() {
    this.checkInternet();
  }

  componentWillUnmount() {
    // this.appStateSubscription.remove();
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Text style={{position: 'absolute', left: 30, top: 100, zIndex: 99999}}>
          {this.state.loadingMessage}
        </Text>
        <Loading init={true} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <View style={styles.statusbarView} />
        <View style={styles.containerView}>
          <View style={{flexDirection: 'row'}}>
            <Text style={[styles.logoText, {color: '#1B8FD0'}]}>SSU</Text>
            <Text style={[styles.logoText, {color: '#4269CE'}]}>TODAY</Text>
          </View>
          <Text style={styles.subText}>
            스터디룸 예약과 공지사항 확인을 한번에!
          </Text>
        </View>
        <View style={styles.footerView} />
        <BottomSafe />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  statusbarView: {
    height: size.STATUSBAR_HEIGHT,
    width: '100%',
  },
  containerView: {
    flex: 1,
    width: '100%',
    justifyContent: 'center',
    marginTop: -(52 + 25),
    alignItems: 'center',
  },
  footerView: {
    paddingLeft: 25,
    paddingRight: 25,
    paddingTop: 0,
    paddingBottom: 25,
    width: '100%',
    height: 52 + 25,
  },
  logoText: {
    fontFamily: 'Staatliches-Regular',
    fontSize: 40,
  },
  subText: {
    color: '#1B8FD0',
    fontSize: 15,
    fontFamily: 'Pretendard-Bold',
  },
});

export default MainScreen;
