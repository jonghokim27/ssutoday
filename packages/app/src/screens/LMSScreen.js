import React from 'react';
import {Component} from 'react';
import {View, Text, StyleSheet, Platform, TouchableOpacity} from 'react-native';

import Footer from '../components/Footer';
import Loading from '../components/Loading';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';
import WebView from 'react-native-webview';
import axios from 'axios';
import CookieManager from '@react-native-cookies/cookies';
import size from '../constants/size';
import Refresh from '../../assets/svg/refresh-black.svg';
import Lock from '../../assets/svg/lock.svg';

import { updateXnApiToken } from '../apis/user';

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

class LMSScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      url: '',
      progress: 0,
      webViewReady: true
    };

    this.clientId = null;
    this.apiKey = null;
    this.clientSecret = null;
  }

  async componentDidMount() {
    let lmsAgree = await AsyncStorage.getItem("lmsAgree");
    if(lmsAgree == null){
      this.swal.show("warning",
      "확인 필요",
      "슈투데이의 LMS 기능을 이용하시려면\n아래 내용에 동의가 필요해요.\n\n1. 슈투데이는 유세인트 아이디와\n비밀번호를 수집하지 않아요.\n\n2. 슈투데이는 동영상, 과제, 퀴즈의\n마감일 알림을 보내드리기 위해\nLMS API 토큰을 수집해요.\n\n위 내용에 동의하신다면\nLMS 로그인을 진행해주세요.", "확인", async () => {
        await AsyncStorage.setItem("lmsAgree", "true");
        this.swal.hide();
      });
    }

    // await AsyncStorage.removeItem('canvasToken');
    await CookieManager.clearAll(true);
    await CookieManager.clearAll(false);

    await this.launch();
  }

  async launch(){
    // Phase 3 : SessionLess Launch
    let accessToken = await AsyncStorage.getItem('canvasToken');
    if (accessToken != null) {
      let sessionLessLaunchUrl =
        'https://canvas.ssu.ac.kr/api/v1/accounts/1/external_tools/sessionless_launch?id=67&launch_type=global_navigation';
      let sessionLessLaunchRes;
      try {
        sessionLessLaunchRes = await axios.get(sessionLessLaunchUrl, {
          headers: {
            Authorization: 'Bearer ' + accessToken,
          },
        });
      } catch (e) {
        await AsyncStorage.removeItem('canvasToken');
        this.swal.show(
          'warning',
          '인증 만료',
          '장기간 앱을 이용하지 않았거나,\n학적 정보에 변동이 발생하여\n인증이 만료되었어요.\n다시 로그인해주세요.',
          '확인',
          async () => {
            await this.swal.hide();
            await this.loadLogin();
          },
        );
        return;
      }

      let sessionTokenUrl =
        'https://canvas.ssu.ac.kr/login/session_token?return_to=' +
        encodeURIComponent(sessionLessLaunchRes.data.url);
      let sessionTokenRes;
      try {
        sessionTokenRes = await axios.get(sessionTokenUrl, {
          headers: {
            Authorization: 'Bearer ' + accessToken,
          },
        });
      } catch (e) {
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            await this.swal.hide();
            await this.loadLogin();
          },
        );
        return;
      }

      this.setState({
        url: sessionTokenRes.data.session_url,
      });
      return;
    }

    await this.loadLogin();
  }

  async loadLogin() {
    // Phase 1 : Fetch API Key & Open WebView

    let apiKeyUrl =
      Platform.OS == 'android'
        ? 'https://canvas.ssu.ac.kr/learningx/api/v1/mobileverify?app_name=LearningX Student&platform=android&canvas_url=canvas.ssu.ac.kr'
        : 'https://canvas.ssu.ac.kr/learningx/api/v1/mobileverify?app_name=LearningX Student&platform=ios&canvas_url=canvas.ssu.ac.kr';

    let apiKeyRes;
    try {
      apiKeyRes = await axios.get(apiKeyUrl);
    } catch (e) {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
          await this.loadLogin();
        },
      );
      return;
    }

    if (apiKeyRes.data.authorized != true) {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
          await this.loadLogin();
        },
      );
      return;
    }

    this.clientId = apiKeyRes.data.client_id;
    this.apiKey = apiKeyRes.data.api_key;
    this.clientSecret = apiKeyRes.data.client_secret;

    let url =
      'https://canvas.ssu.ac.kr/login/oauth2/auth?client_id=' +
      this.clientId +
      '&response_type=code&mobile=1&purpose=&redirect_uri=https://canvas.ssu.ac.kr/login/oauth2/auth';
    
    await this.setState({
      webViewReady: false
    });

    await this.setState({
      url: url,
      webViewReady: true
    });
  }

  async onLoadStartHandler(url) {
    if (url.startsWith('https://canvas.ssu.ac.kr/login/oauth2/auth?code=')) {
      this.loading.show();

      // Phase 2 : Fetch Canvas access token
      let code = url.split(
        'https://canvas.ssu.ac.kr/login/oauth2/auth?code=',
      )[1];
      let tokenUrl =
        'https://canvas.ssu.ac.kr/login/oauth2/token?client_id=' +
        this.clientId +
        '&client_secret=' +
        this.clientSecret +
        '&code=' +
        code +
        '&redirect_uri=urn:ietf:wg:oauth:2.0:oob';

      let tokenRes;
      let accessToken;
      try {
        tokenRes = await axios.post(tokenUrl);
        accessToken = tokenRes.data.access_token;
      } catch (e) {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            await this.swal.hide();
            await this.loadLogin();
          },
        );
        return;
      }

      this.loading.hide();
      await AsyncStorage.setItem('canvasToken', accessToken);
      this.setState({
        url: 'https://canvas.ssu.ac.kr/learningx/dashboard?locale=ko',
      });
    }

  }

  async onLoadEndHandler(url) {
    if(url.startsWith("https://canvas.ssu.ac.kr/learningx/lti/dashboard") || url.startsWith("https://canvas.ssu.ac.kr/learningx/dashboard")){
      this.loading.show();
      let xnApiTokenFound = false;
      let cookies = await CookieManager.get("https://canvas.ssu.ac.kr", true);
      for await (let [key, value] of Object.entries(cookies)) 
      {   
        if(key == "xn_api_token" && value.value){
          xnApiTokenFound = true;
          let updateXnApiTokenRes = await updateXnApiToken(value.value);
          if (updateXnApiTokenRes.statusCode != 'SSU2190') {
            this.swal.show(
              'error',
              '토큰 등록 실패',
              '서버에 API 토큰을 등록할 수 없었어요.\n마감일 알림 기능이\n제공되지 않을 수 있어요.',
              '확인',
              async () => {
                await this.swal.hide();
              },
            );
            this.loading.hide();
            return;
          }
        }
      }

      if(!xnApiTokenFound){
        let cookies2 = await CookieManager.get("https://canvas.ssu.ac.kr", false);
        for await (let [key, value] of Object.entries(cookies2)) 
        {   
          if(key == "xn_api_token" && value.value){
            xnApiTokenFound = true;
            let updateXnApiTokenRes = await updateXnApiToken(value.value);
            if (updateXnApiTokenRes.statusCode != 'SSU2190') {
              this.swal.show(
                'error',
                '토큰 등록 실패',
                '서버에 API 토큰을 등록할 수 없었어요.\n마감일 알림 기능이\n제공되지 않을 수 있어요.',
                '확인',
                async () => {
                  await this.swal.hide();
                },
              );
              this.loading.hide();
              return;
            }
          }
        }
      }

      if(!xnApiTokenFound){
        this.swal.show(
          'error',
          '토큰 등록 실패',
          '서버에 API 토큰을 등록할 수 없었어요.\n마감일 알림 기능이\n제공되지 않을 수 있어요.',
          '확인',
          async () => {
            await this.swal.hide();
          },
        );
        this.loading.hide();
        return;
      }
      this.loading.hide();
    }
  }

  clearAllAsk(){
    this.qswal.show(
      'warning',
      '확인 필요',
      '정말 LMS에서 로그아웃 하시겠어요?\n마감일 알림 기능이 더 이상\n제공되지 않아요.',
      '로그아웃',
      () => {
        this.qswal.hide();
        this.clearAll();
      },
      '취소',
      () => {
        this.qswal.hide();
      },
    );
  }

  async clearAll(){
    await AsyncStorage.removeItem('canvasToken');
    await CookieManager.clearAll(true);
    await CookieManager.clearAll(false);
    this.loading.show();
    let updateXnApiTokenRes = await updateXnApiToken(null);
    if (updateXnApiTokenRes.statusCode != 'SSU2190') {
      this.swal.show(
        'error',
        '토큰 삭제 실패',
        '서버에서 API 토큰을 삭제할 수 없었어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }
    this.loading.hide();
    await this.loadLogin();
  }



  render() {
    return (
      <View style={styles.mainView}>
        <Loading init={false} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <QSwal ref={ref => (this.qswal = ref)} />
        <View style={styles.headerView}>
          <TouchableOpacity
            style={styles.clearView}
            onPress={() => this.clearAllAsk()}>
            <Lock width={21} height={21}/>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.refreshView}
            onPress={() => this.launch()}>
            <Refresh width={20} height={20}/>
          </TouchableOpacity>
          <Text style={styles.titleText}>LMS</Text>
        </View>
        <View style={styles.containerView}>
          {this.state.webViewReady && <WebView
            ref={ref => (this.webView = ref)}
            style={styles.webView}
            nestedScrollEnabled={false}
            startInLoadingState={true}
            sharedCookiesEnabled={true}
            onLoadStart={async e =>
              await this.onLoadStartHandler(e.nativeEvent.url)
            }
            onLoadEnd={async e =>
              await this.onLoadEndHandler(e.nativeEvent.url)
            }
            renderLoading={() => <Loading init={true} />}
            source={{
              uri: this.state.url,
            }}
          />}
        </View>
        <Footer menu={4} navigation={this.navigation} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: '#F8F8FA',
  },
  webView: {
    flex: 1,
    width: '100%',
  },
  containerView: {
    flex: 1,
    width: '100%',
  },
  titleText: {
    color: 'black',
    fontFamily: 'Pretendard-Medium',
    fontSize: 17,
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
        }
      : {
          height: size.STATUSBAR_HEIGHT / 2 + 50,
          backgroundColor: '#FAFAFA',
          width: '100%',
          justifyContent: 'center',
          alignItems: 'center',
        },
  refreshView:
    Platform.OS == 'ios'
      ? {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: size.STATUSBAR_HEIGHT,
          padding: 16,
          justifyContent: 'center',
        }
      : {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: 0,
          padding: 16,
          justifyContent: 'center',
        },
  clearView:
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
});

export default LMSScreen;
