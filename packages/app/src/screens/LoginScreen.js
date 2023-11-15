import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Platform,
  Keyboard,
} from 'react-native';
import size from '../constants/size';
import WebView from 'react-native-webview';
import Loading from '../components/Loading';
import Back from '../../assets/svg/back.svg';
import {login} from '../apis/user';
import Swal from '../components/Swal';
import AsyncStorage from '@react-native-async-storage/async-storage';
import messaging from '@react-native-firebase/messaging';
import {register} from '../apis/device';
import DeviceInfo from 'react-native-device-info';

class LoginScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {};
  }

  async componentDidMount() {}

  async onLoadStartHandler(url) {
    if (url.startsWith('https://saint.ssu.ac.kr/webSSO/sso.jsp')) {
      Keyboard.dismiss();
      await this.webView.stopLoading();
      await this.loading.show();
      this.loading.message(
        '유세인트로 인증하고 있어요.\n잠시만 기다려 주세요.',
      );
      let queryString = url.split('?')[1];
      let sToken = queryString.split('sToken=')[1].split('&')[0];
      let sIdno = queryString.split('sIdno=')[1];

      let loginRes = await login(sToken, sIdno);
      if (loginRes.statusCode == 'SSU0000') {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            this.navigation.goBack();
          },
        );
        return;
      } else if (loginRes.statusCode == 'SSU4010') {
        //인증 실패
        this.loading.hide();
        this.swal.show(
          'error',
          '인증 실패',
          '유세인트 인증에 실패했어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            this.navigation.goBack();
          },
        );
        return;
      } else if (loginRes.statusCode == 'SSU4011') {
        //미지원 학부
        this.loading.hide();
        this.swal.show(
          'warning',
          '인증 실패',
          '지원하지 않는 학과(부)에요.',
          '확인',
          async () => {
            this.navigation.goBack();
          },
        );
        return;
      } else if (loginRes.statusCode == 'SSU2010') {
        let accessToken = loginRes.data.accessToken;
        let refreshToken = loginRes.data.refreshToken;
        await AsyncStorage.setItem('accessToken', accessToken);
        await AsyncStorage.setItem('refreshToken', refreshToken);

        let studentId = loginRes.data.studentId;
        let name = loginRes.data.name;
        let major = loginRes.data.major;
        let profile = {
          studentId,
          name,
          major,
        };

        await AsyncStorage.setItem('profile', JSON.stringify(profile));

        let uuid = await DeviceInfo.getUniqueId();
        let pushToken = await messaging().getToken();
        if (pushToken != null) {
          await register(Platform.OS, uuid, pushToken);
          await messaging().subscribeToTopic('all');
          await messaging().subscribeToTopic(major);
          await AsyncStorage.setItem('notificationEnabled', 'true');
        } else {
          await AsyncStorage.setItem('notificationEnabled', 'false');
        }

        this.loading.hide();
        await this.navigation.popToTop();
        await this.navigation.navigate('NoticeNavigator');
        return;
      } else {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
            loginRes.statusCode,
          '확인',
          async () => {
            this.navigation.goBack();
          },
        );
        return;
      }
    }
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Loading ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <View style={styles.headerView}>
          <TouchableOpacity
            style={styles.backView}
            onPress={() => this.navigation.goBack()}>
            <Back width={24} height={24} />
          </TouchableOpacity>
          <Text style={styles.titleText}>유세인트 로그인</Text>
        </View>
        <WebView
          ref={ref => (this.webView = ref)}
          style={styles.webView}
          startInLoadingState={true}
          sharedCookiesEnabled={true}
          onLoadStart={async e =>
            await this.onLoadStartHandler(e.nativeEvent.url)
          }
          renderLoading={() => <Loading init={true} />}
          source={{
            uri: 'https://smartid.ssu.ac.kr/Symtra_sso/smln.asp?apiReturnUrl=https%3A%2F%2Fsaint.ssu.ac.kr%2FwebSSO%2Fsso.jsp',
          }}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  headerView:
    Platform.OS == 'ios'
      ? {
          paddingTop: size.STATUSBAR_HEIGHT,
          height: size.STATUSBAR_HEIGHT + 50,
          backgroundColor: '#FAFAFA',
          width: '100%',
          justifyContent: 'center',
          alignItems: 'center',
          marginBottom: 10,
        }
      : {
          height: size.STATUSBAR_HEIGHT / 2 + 50,
          backgroundColor: '#FAFAFA',
          width: '100%',
          justifyContent: 'center',
          alignItems: 'center',
          marginBottom: 10,
        },
  webView: {
    flex: 1,
    width: '100%',
  },
  titleText: {
    color: 'black',
    fontFamily: 'Pretendard-Medium',
    fontSize: 17,
  },
  subText: {
    color: 'black',
    fontFamily: 'Pretendard-Bold',
    fontSize: 12,
  },
  backView:
    Platform.OS == 'ios'
      ? {
          position: 'absolute',
          height: '100%',
          left: 0,
          top: size.STATUSBAR_HEIGHT,
          padding: 16,
          justifyContent: 'center',
        }
      : {
          position: 'absolute',
          height: '100%',
          left: 0,
          top: 0,
          padding: 16,
          justifyContent: 'center',
        },
  backButton: {
    height: 35,
    width: 20,
  },
});

export default LoginScreen;
