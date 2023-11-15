import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  Switch,
  PermissionsAndroid,
  Platform,
  Linking,
  TouchableOpacity,
  ScrollView,
  Alert,
} from 'react-native';

import Header from '../components/Header';
import Footer from '../components/Footer';
import Loading from '../components/Loading';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {getOption, register, unregister, updateOption} from '../apis/device';
import Swal from '../components/Swal';
import DeviceInfo from 'react-native-device-info';
import messaging from '@react-native-firebase/messaging';
import notifee, {AndroidImportance} from '@notifee/react-native';
import Right from '../../assets/svg/right.svg';
import Down from '../../assets/svg/down.svg';
import Up from '../../assets/svg/up.svg';
import {Pressable} from 'react-native';
import QSwal from '../components/QSwal';
import {logout} from '../apis/user';
import {APP_VERSION} from '../constants/setting';
import { parseMajor } from '../constants/function';

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

class MyScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      major: '',
      majorHangul: '',
      name: '',
      studentId: 0,
      notification: false,
      showDeveloper: false,
      showHelp: false,
      noticeNotification: false,
      reserveNotification: false,
      lmsNotification: false
    };
  }

  async componentDidMount() {
    this.loading.show();
    let profile = await AsyncStorage.getItem('profile');
    profile = JSON.parse(profile);

    let majorHangul = parseMajor(profile.major);

    let notification = await AsyncStorage.getItem('notificationEnabled');
    if (notification == null) {
      notification = false;
    } else if (notification == 'false') {
      notification = false;
    } else if (notification == 'true') {
      notification = true;
    }

    let noticeNotification = false;
    let reserveNotification = false;
    let lmsNotification = false;

    if(notification){
      let uuid = await DeviceInfo.getUniqueId();
      let getOptionRes = await getOption(Platform.OS, uuid);
      if (getOptionRes.statusCode == 'SSU0000') {
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            await this.swal.hide();
          },
        );
      } else if(getOptionRes.statusCode == 'SSU2170'){
        noticeNotification = getOptionRes.data.notice;
        reserveNotification = getOptionRes.data.reserve;
        lmsNotification = getOptionRes.data.lms;
      }
      else {
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          getOptionRes.statusCode,
          '확인',
          async () => {
            await this.swal.hide();
          },
        );
      }
    }

    this.setState({
      major: profile.major,
      majorHangul: majorHangul,
      name: profile.name,
      studentId: profile.studentId,
      notification: notification,
      noticeNotification: noticeNotification,
      reserveNotification: reserveNotification,
      lmsNotification: lmsNotification
    });

    this.loading.hide();
  }

  async setNotification(val) {
    this.loading.show();

    let uuid = await DeviceInfo.getUniqueId();

    if (val) {
      let pushToken = null;
      if (Platform.OS == 'ios') {
        const authStatus = await messaging().requestPermission();
        const enabled =
          authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
          authStatus === messaging.AuthorizationStatus.PROVISIONAL;

        if (enabled) {
          pushToken = await messaging().getToken();
        } else {
          this.loading.hide();
          this.swal.show(
            'warning',
            '알림 권한 필요',
            '알림 권한이 허용되어 있지 않아요.\n설정으로 이동하여 알림 권한을\n허용해주세요.',
            '설정으로 이동',
            () => {
              Linking.openURL('app-settings://notification/ssutoday');
              this.swal.hide();
            },
          );
          return;
        }
      } else if (Platform.OS == 'android') {
        if (Platform.Version >= 33) {
          let authStatus = await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
          );
          if (authStatus == 'granted') {
            pushToken = await messaging().getToken();
          } else {
            this.loading.hide();
            this.swal.show(
              'warning',
              '알림 권한 필요',
              '알림 권한이 허용되어 있지 않아요.\n설정으로 이동하여 알림 권한을\n허용해주세요.',
              '설정으로 이동',
              () => {
                Linking.openSettings();
                this.swal.hide();
              },
            );
            return;
          }
        } else {
          pushToken = await messaging().getToken();
        }
      }

      let registerRes = await register(Platform.OS, uuid, pushToken);
      if (registerRes.statusCode == 'SSU0000') {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            this.swal.hide();
          },
        );
        return;
      } else if (registerRes.statusCode == 'SSU2040') {
      } else {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
            registerRes.statusCode,
          '확인',
          async () => {
            this.swal.hide();
          },
        );
        return;
      }

      await messaging().subscribeToTopic('all');
      await messaging().subscribeToTopic(this.state.major);

      await notifee.requestPermission();
      if (Platform.OS == 'android') {
        await notifee.createChannel({
          id: 'default',
          name: '기본',
          importance: AndroidImportance.HIGH,
        });
      }
    } else {
      let unregisterRes = await unregister(Platform.OS, uuid);

      if (unregisterRes.statusCode == 'SSU0000') {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            this.swal.hide();
          },
        );
        return;
      }

      await messaging().unsubscribeFromTopic('all');
      await messaging().unsubscribeFromTopic(this.state.major);
    }

    let noticeNotification = false;
    let reserveNotification = false;
    let lmsNotification = false;
    if(val){
      noticeNotification = true;
      reserveNotification = true;
      lmsNotification = true;
    }

    this.loading.hide();
    this.setState({
      notification: val,
      noticeNotification: noticeNotification,
      reserveNotification: reserveNotification,
      lmsNotification: lmsNotification
    });

    await AsyncStorage.setItem('notificationEnabled', val ? 'true' : 'false');
  }

  async setNotificationOption(type, value){
    this.loading.show();

    let uuid = await DeviceInfo.getUniqueId();
    let updateOptionRes = await updateOption(Platform.OS, uuid, type, value);
    if (updateOptionRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
      this.loading.hide();
      return;
    } else if(updateOptionRes.statusCode != 'SSU2180'){
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
        updateOptionRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
      this.loading.hide();
      return;
    }

    if(type == "notice"){
      if(value){
        await messaging().subscribeToTopic('all');
        await messaging().subscribeToTopic(this.state.major);
      } else{
        await messaging().unsubscribeFromTopic('all');
        await messaging().unsubscribeFromTopic(this.state.major);
      }
    }

    this.setState({
      [type + "Notification"]: value
    })
    this.loading.hide();
  }

  async logoutAsk() {
    this.qswal.show(
      'warning',
      '확인 필요',
      '정말 로그아웃 하시겠어요?',
      '로그아웃',
      () => {
        this.qswal.hide();
        this.logout();
      },
      '취소',
      () => {
        this.qswal.hide();
      },
    );
  }

  async logout() {
    this.loading.show();

    if (this.state.notification) {
      let uuid = await DeviceInfo.getUniqueId();
      let unregisterRes = await unregister(Platform.OS, uuid);

      if (unregisterRes.statusCode == 'SSU0000') {
        this.loading.hide();
        this.swal.show(
          'error',
          '서버 연결 실패',
          '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
          '확인',
          async () => {
            this.swal.hide();
          },
        );
        return;
      }

      await messaging().unsubscribeFromTopic('all');
      await messaging().unsubscribeFromTopic(this.state.major);
    }

    let logoutRes = await logout();
    if (logoutRes.statusCode == 'SSU0000') {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (logoutRes.statusCode == 'SSU2030') {
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          logoutRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }

    let storageKeys = await AsyncStorage.getAllKeys();
    for await (let key of storageKeys) {
      await AsyncStorage.removeItem(key);
    }

    this.swal.show(
      'success',
      '로그아웃 성공',
      '성공적으로 로그아웃 되었어요.\n다음에 또 봬요!',
      '확인',
      () => {
        this.navigation.navigate('MainScreen');
      },
    );
    return;
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Loading init={true} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <QSwal ref={ref => (this.qswal = ref)} />
        <Header />
        <View style={styles.containerView}>
          <View style={styles.profileView}>
            <View>
              <Image
                source={require('../../assets/images/profile.png')}
                style={styles.profileImg}
              />
            </View>
            <View>
              <View style={{flexDirection: 'row'}}>
                <View style={[styles.majorView]}>
                  <Text style={styles.majorText}>{this.state.majorHangul}</Text>
                </View>
              </View>
              <Text style={styles.nameText}>{this.state.name}님</Text>
              <Text style={styles.infoText}>학번 / {this.state.studentId}</Text>
            </View>
          </View>

          <View style={styles.settingView}>
            <View style={[styles.settingInnerView, this.state.notification ? {} : {marginBottom: 0}]}>
              <View style={styles.settingLeftView}>
                <View style={{flexDirection: 'column'}}>
                  <Text style={styles.settingTitleView}>알림 설정</Text>
                  { !this.state.notification && <Text style={styles.settingTextView}>
                    알림을 수신하도록 설정하시면 공지사항, 예약,{"\n"}LMS와 같은 각종 내용을 보내드려요.
                  </Text>}
                </View>
              </View>
              <View style={styles.settingRightView}>
                <Switch
                  trackColor={{false: 'white', true: 'white'}}
                  thumbColor={this.state.notification ? '#7B70F0' : '#f4f3f4'}
                  ios_backgroundColor="white"
                  onValueChange={val => {
                    this.setNotification(val);
                  }}
                  value={this.state.notification}
                />
              </View>
            </View>
            { this.state.notification && 
            <>
            <View style={styles.settingInnerView}>
              <View style={styles.settingLeftView}>
                <View style={{flexDirection: 'column'}}>
                  <Text style={styles.settingTitleViewSub}>공지사항</Text>
                </View>
              </View>
              <View style={styles.settingRightView}>
                <Switch
                  trackColor={{false: 'white', true: 'white'}}
                  thumbColor={this.state.noticeNotification ? '#7B70F0' : '#f4f3f4'}
                  ios_backgroundColor="white"
                  onValueChange={val => {
                    this.setNotificationOption("notice", val);
                  }}
                  value={this.state.noticeNotification}
                />
              </View>
            </View>
            <View style={styles.settingInnerView}>
              <View style={styles.settingLeftView}>
                <View style={{flexDirection: 'column'}}>
                  <Text style={styles.settingTitleViewSub}>예약</Text>
                </View>
              </View>
              <View style={styles.settingRightView}>
                <Switch
                  trackColor={{false: 'white', true: 'white'}}
                  thumbColor={this.state.reserveNotification ? '#7B70F0' : '#f4f3f4'}
                  ios_backgroundColor="white"
                  onValueChange={val => {
                    this.setNotificationOption("reserve", val);
                  }}
                  value={this.state.reserveNotification}
                />
              </View>
            </View>
            <View style={[styles.settingInnerView, {marginBottom: 0}]}>
              <View style={styles.settingLeftView}>
                <View style={{flexDirection: 'column'}}>
                  <Text style={styles.settingTitleViewSub}>LMS</Text>
                </View>
              </View>
              <View style={styles.settingRightView}>
                <Switch
                  trackColor={{false: 'white', true: 'white'}}
                  thumbColor={this.state.lmsNotification ? '#7B70F0' : '#f4f3f4'}
                  ios_backgroundColor="white"
                  onValueChange={val => {
                    this.setNotificationOption("lms", val);
                  }}
                  value={this.state.lmsNotification}
                />
              </View>
            </View></>
          }
          </View>

          <ScrollView style={styles.linkView}>
            <Pressable
              style={styles.linkItem}
              onPress={() => this.setState({showHelp: !this.state.showHelp})}>
              <View style={styles.linkLeft}>
                <Text style={styles.settingTitleView}>지원</Text>
                {this.state.showHelp && (
                  <>
                    <Text style={styles.settingTextViewBold}>
                      컴퓨터학부 학생회
                    </Text>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://pf.kakao.com/_ZEjFd')
                      }>
                      <Text style={styles.settingTextView}>
                        카카오톡 채널: 숭실대학교 컴퓨터학부 학생회
                      </Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://instagram.com/ssu_cse')
                      }>
                      <Text style={styles.settingTextView}>
                        인스타그램: @ssu_cse
                      </Text>
                    </TouchableOpacity>
                    <Text style={styles.settingTextView} />
                    <Text style={styles.settingTextViewBold}>
                      소프트웨어학부 학생회
                    </Text>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://pf.kakao.com/_wkNxoxd')
                      }>
                      <Text style={styles.settingTextView}>
                        카카오톡 채널: 숭실대 소프트웨어학부 학생회
                      </Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://instagram.com/ssu_soft')
                      }>
                      <Text style={styles.settingTextView}>
                        인스타그램: @ssu_soft
                      </Text>
                    </TouchableOpacity>
                    <Text style={styles.settingTextView} />
                    <Text style={styles.settingTextViewBold}>
                      글로벌미디어학부 학생회
                    </Text>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://pf.kakao.com/_nwZHxl')
                      }>
                      <Text style={styles.settingTextView}>
                        카카오톡 채널: 숭실대학교 글로벌미디어학부
                      </Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL('https://instagram.com/ssu_globalmedia')
                      }>
                      <Text style={styles.settingTextView}>
                        인스타그램: @ssu_globalmedia
                      </Text>
                    </TouchableOpacity>
                  </>
                )}
              </View>
              <View style={styles.linkRight}>
                {!this.state.showHelp && <Down />}
                {this.state.showHelp && <Up />}
              </View>
            </Pressable>
            <View style={styles.dividerView}>
              <View style={styles.divider} />
            </View>
            <Pressable
              style={styles.linkItem}
              onPress={() =>
                this.setState({showDeveloper: !this.state.showDeveloper})
              }>
              <View style={styles.linkLeft}>
                <Text style={styles.settingTitleView}>개발자 정보</Text>
                {this.state.showDeveloper && (
                  <>
                    <Text style={styles.settingTextViewBold}>앱/서버</Text>
                    <Text style={styles.settingTextView}>
                      제27대 컴퓨터학부 부학생회장 김종호
                    </Text>
                    <Text style={styles.settingTextView} />
                    <Text style={styles.settingTextViewBold}>UI/UX</Text>
                    <Text style={styles.settingTextView}>
                      제20대 글로벌미디어학부 부학생회장 하선우
                    </Text>
                    <Text style={styles.settingTextView} />
                    <Text style={styles.settingTextViewBold}>기여</Text>
                    <TouchableOpacity
                      onPress={() =>
                        Linking.openURL(
                          'https://github.com/jonghokim27/ssutoday',
                        )
                      }>
                      <Text style={styles.settingTextView}>
                        https://github.com/jonghokim27/ssutoday
                      </Text>
                    </TouchableOpacity>
                  </>
                )}
              </View>
              <View style={styles.linkRight}>
                {!this.state.showDeveloper && <Down />}
                {this.state.showDeveloper && <Up />}
              </View>
            </Pressable>
            <View style={styles.dividerView}>
              <View style={styles.divider} />
            </View>
            <Pressable
              style={styles.linkItem}
              onPress={() => {
                this.logoutAsk();
              }}>
              <View style={styles.linkLeft}>
                <Text style={styles.settingTitleView}>로그아웃</Text>
              </View>
              <View style={styles.linkRight}>
                <Right />
              </View>
            </Pressable>
            <Text
              style={[
                styles.settingTextView,
                {paddingLeft: 18, paddingTop: 40, fontSize: 14},
              ]}>
              v{APP_VERSION}_{Platform.OS == 'android' ? '5030' : '51'}_2023.11.15{'\n'}ⓒ 슈투데이. 모든 권리 보유.
            </Text>
            <View style={{height: 20}} />
          </ScrollView>
        </View>
        <Footer menu={3} navigation={this.navigation} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    flex: 1,
    width: '100%',
    paddingTop: 37,
    paddingLeft: 24,
    paddingRight: 24,
  },
  profileView: {
    marginBottom: 38,
    flexDirection: 'row',
  },
  profileImg: {
    height: 81,
    width: 81,
    marginRight: 17,
  },
  majorView: {
    height: 24,
    borderRadius: 8,
    paddingLeft: 10,
    paddingRight: 10,
    alignItems: 'center',
    backgroundColor: '#9389FF',
    justifyContent: 'center',
    marginBottom: 6,
    marginLeft: 2,
  },
  majorText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 14,
    color: 'white',
  },
  nameText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 35,
    lineHeight: 35,
    marginBottom: 1,
    color: 'black',
  },
  infoText: {
    marginLeft: 4,
    fontFamily: 'Pretendard-Bold',
    fontSize: 12,
    lineHeight: 12,
    color: 'black',
  },
  settingView: {
    backgroundColor: '#F0F0F0',
    paddingLeft: 19,
    paddingRight: 19,
    paddingTop: 17,
    paddingBottom: 17,
    borderRadius: 20,
    marginBottom: 21,
  },
  settingInnerView: {
    flexDirection: 'row',
    // height: 77,
    width: '100%',
    marginBottom: 8,
  },
  settingLeftView: {
    // height: 77,
    flex: 1,
    justifyContent: 'center',
  },
  settingRightView: {
    justifyContent: 'center',
    // height: 77,
  },
  settingTitleView: {
    color: '#838383',
    fontSize: 20,
    fontFamily: 'Pretendard-Bold',
    marginBottom: 4,
  },
  settingTitleViewSub: {
    color: '#838383',
    fontSize: 16,
    fontFamily: 'Pretendard-Medium',
    marginBottom: 4,
  },
  settingTextView: {
    color: '#979797',
    fontSize: 12,
    fontFamily: 'Pretendard-Medium',
  },
  settingTextViewBold: {
    color: '#979797',
    fontSize: 12,
    fontFamily: 'Pretendard-Bold',
  },
  linkView: {
    flex: 1,
  },
  linkItem: {
    flexDirection: 'row',
    paddingLeft: 18,
    paddingRight: 18,
  },
  linkLeft: {
    flex: 1,
  },
  linkRight: {
    // justifyContent: "center"
  },
  divider: {
    height: 1,
    width: '100%',
    backgroundColor: '#E4E4E4',
    marginTop: 13,
    marginBottom: 13,
  },
  dividerView: {
    paddingLeft: 14,
    paddingRight: 14,
  },
});

export default MyScreen;
