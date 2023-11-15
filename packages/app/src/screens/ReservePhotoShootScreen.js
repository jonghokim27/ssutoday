import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Linking,
} from 'react-native';

import Loading from '../components/Loading';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

import size from '../constants/size';
import Back from '../../assets/svg/back.svg';
import { Camera, parsePhysicalDeviceTypes } from 'react-native-vision-camera';
import { uploadVerifyPhoto } from '../apis/reserve';
import CameraView from '../components/CameraView';

class ReservePhotoShootScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.idx = props.route.params.idx;
    this.state = {
      cameraReady: false,
      cameraActive: true,
      name: props.route.params.name,
      time: props.route.params.time,
      date: props.route.params.date
    };
  }

  async componentDidMount() {
    const newCameraPermission = await Camera.requestCameraPermission()
    if(newCameraPermission == 'denied'){
      this.swal.show(
        'warning',
        '카메라 권한 필요',
        '카메라 권한이 허용되어 있지 않아요.\n설정으로 이동하여 카메라 권한을\n허용해주세요.',
        '설정으로 이동',
        () => {
          Linking.openURL('app-settings://camera/ssutoday');
          this.swal.hide();
        },
      );
    }else{
      this.setState({
        cameraReady: true
      });
    }
  }

  getBase64(file) {
    const reader = new FileReader()
    return new Promise(resolve => {
      reader.onload = ev => {
        resolve(ev.target.result)
      }
      reader.readAsDataURL(file)
    })
  }

  async shoot(){
    let photo;
    try{
      photo = await this.camera.takePhoto({
        qualityPrioritization: 'speed',
        flash: 'off',
        enableShutterSound: true
      });
      this.setState({
        cameraActive: false
      });
    } catch(e){
      this.setState({
        cameraActive: true
      });
      this.swal.show(
        'error',
        '촬영 실패',
        '인증샷 촬영 중 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        () => {
          this.swal.hide();
        },
      );
      return;
    }

    this.loading.show();

    let mime = photo.path.split(".")[1];
    let fileName = new Date().getTime() + "." + mime;

    let uploadVerifyPhotoRes = await uploadVerifyPhoto(this.idx, "file://" + photo.path, fileName);

    if(uploadVerifyPhotoRes.statusCode == "SSU2200"){
      this.navigation.goBack();
      return;
    } else if(uploadVerifyPhotoRes.statusCode == "SSU4201" || uploadVerifyPhotoRes.statusCode == "SSU4202"){
      this.loading.hide();
      this.swal.show(
        'error',
        '업로드 실패',
        '이미 이용이 완료된 예약이에요.',
        '확인',
        async () => {
          this.swal.hide();
          this.navigation.goBack();
        },
      );
      return;
    } else if(uploadVerifyPhotoRes.statusCode == "SSU4203"){
      this.loading.hide();
      this.swal.show(
        'error',
        '업로드 실패',
        '아직 이용을 시작하지 않은 예약이에요.',
        '확인',
        async () => {
          this.swal.hide();
          this.navigation.goBack();
        },
      );
      return;
    } else if(uploadVerifyPhotoRes.statusCode == "SSU4204"){
      this.loading.hide();
      this.swal.show(
        'error',
        '업로드 실패',
        '촬영 기한이 경과했어요.',
        '확인',
        async () => {
          this.swal.hide();
          this.navigation.goBack();
        },
      );
      return;
    } else if (uploadVerifyPhotoRes.statusCode == 'SSU0000') {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          this.swal.hide();
          this.navigation.goBack();
        },
      );
      return;
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
        uploadVerifyPhotoRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
          this.navigation.goBack();
        },
      );
      return;
    }
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Loading init={false} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <QSwal ref={ref => (this.qswal = ref)} />

        <View style={styles.headerView}>
          <TouchableOpacity
            style={styles.backView}
            onPress={() => this.navigation.goBack()}>
            <Back width={24} height={24} />
          </TouchableOpacity>
          <Text style={styles.titleText}>인증샷 촬영</Text>
        </View>

        <View style={styles.containerView}>
          { this.state.cameraReady &&
          <CameraView ref={(ref) => this.camera = ref} isActive={this.state.cameraActive}></CameraView>
          }
          <View style={styles.cardView}>
            <View style={{flexDirection: 'row', marginBottom: 5, paddingLeft: 16, paddingRight: 16}}>
              <View style={{flex: 1}}>
                <Text style={styles.cardTitle}>
                  📒  {this.state.date}
                </Text>
              </View>
            </View>
            <View style={{flexDirection: 'row', paddingLeft: 16, paddingRight: 16}}>
              <View style={{flex: 1}}>
                <View style={{flexDirection: 'row'}}>
                  <View
                    style={{
                      flexDirection: 'row',
                      flex: 1,
                      alignItems: 'center',
                    }}>
                    <View
                      style={{justifyContent: 'center', marginBottom: 1, marginRight: 3}}>
                        <Text style={styles.cardSubtitle}>시설명</Text>
                    </View>
                    <Text style={[styles.cardSubtitle, {color: "black"}]}>{this.state.name}</Text>
                  </View>

                </View>
              </View>
            </View>
            <View style={{flexDirection: 'row', paddingLeft: 16, paddingRight: 16}}>
              <View style={{flex: 1}}>
                <View style={{flexDirection: 'row'}}>
                  <View
                    style={{
                      flexDirection: 'row',
                      flex: 1,
                      alignItems: 'center',
                    }}>
                    <View
                      style={{justifyContent: 'center', marginBottom: 1, marginRight: 3}}>
                        <Text style={styles.cardSubtitle}>이용 시간</Text>
                    </View>
                    <Text style={[styles.cardSubtitle, {color: "black"}]}>{this.state.time}</Text>
                  </View>

                </View>
              </View>
            </View>
          </View>
        </View>
        <View style={{width: '100%', paddingLeft: 16, paddingRight: 16, position: 'absolute', bottom: 25}}>
          <TouchableOpacity style={{backgroundColor: '#5962ff', width: '100%', height: 60, alignItems: "center", justifyContent: "center", borderRadius: 16}} onPress={() => this.shoot()}>
            <Text style={styles.shootBtnText}>촬영하기</Text>
            <Text style={styles.shootBtnTextSmall}>스터디룸 내부 전경이 잘 보이도록 촬영해주세요.</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  shootBtnText: {
    color: 'white',
    fontFamily: 'Pretendard-Medium',
    fontWeight: '600',
    fontSize: 17,
  },
  shootBtnTextSmall: {
    color: 'white',
    fontFamily: 'Pretendard-Medium',
    fontWeight: '600',
    fontSize: 12,
    textAlign: "center"
  },
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    backgroundColor: "#F5F6F8",
    paddingTop: 20,
    flex: 1,
    width: '100%',
    paddingLeft: 18,
    paddingRight: 18,
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

  titleText: {
    color: 'black',
    fontFamily: 'Pretendard-Medium',
    fontWeight: '600',
    fontSize: 17,
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
        cardView: {
          borderWidth: 1,
          borderColor: '#EEEEEE',
          backgroundColor: 'white',
          borderRadius: 17,
          paddingTop: 16,
          paddingBottom: 16,
          marginBottom: 10,
        },
        cardTitle: {
          color: 'black',
          fontFamily: 'Pretendard-Bold',
          fontWeight: "700",
          fontSize: 16,
        },
        cardDate: {
          color: '#ADADAD',
          fontFamily: 'Pretendard-Bold',
          fontSize: 14,
        },
        cardSubtitle: {
          color: '#A6A6A6',
          fontFamily: 'Pretendard-Medium',
          fontSize: 13,
          marginLeft: 3,
          flex: 1,
        },
});

export default ReservePhotoShootScreen;
