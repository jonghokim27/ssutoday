import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  Pressable,
  ScrollView,
  Image,
  TouchableOpacity,
  Linking,
} from 'react-native';

import Header from '../components/Header';
import Footer from '../components/Footer';
import Loading from '../components/Loading';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';
import Down from '../../assets/svg/down.svg';
Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;
import Refresh from '../../assets/svg/refresh.svg';
import UserSmall from '../../assets/svg/user-small.svg';
import LocationSmall from '../../assets/svg/location-small.svg';
import TimeTable from '../components/TimeTable';
import {list} from '../apis/room';
import {dayOfWeekHan} from '../constants/function';
import DatePicker from 'react-native-date-picker';
import Modal from 'react-native-modal';
import BottomSafe from '../components/BottomSafe';
import DownWhite from '../../assets/svg/downwhite.svg';
import moment from 'moment';
import FastImage from 'react-native-fast-image';
import Right from '../../assets/svg/right.svg';
import List from '../../assets/svg/list.svg';
import {generateToken} from '../apis/sso';
import Null from '../../assets/svg/null.svg';

class ReserveScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      typeModalOpen: false,
      rooms: [],
      date: new Date(),
      dateModalOpen: false,
    };

    this.timetableRefs = null;
    this.scrollIndex = null;
    this.childScrollEvent = this.childScrollEvent.bind(this);
    this.childGetScrollIndex = this.childGetScrollIndex.bind(this);
    this.childSetScrollIndex = this.childSetScrollIndex.bind(this);
    this.showResvInfoModal = this.showResvInfoModal.bind(this);
  }

  async componentDidMount() {
    await this.loadRoomList();
    this.willFocusSubscription = this.navigation.addListener('focus', () => {
      this.loadRoomList();
    });
  }

  async setDate(val) {
    await this.setState({
      date: val,
      dateModalOpen: false,
    });
    await this.loadRoomList();
  }

  async loadRoomList() {
    this.loading.show();
    let listRes = await list(moment(this.state.date).format('YYYY-MM-DD'));
    if (listRes.statusCode == 'SSU0000') {
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
    } else if (listRes.statusCode == 'SSU2110') {
      this.loading.hide();
      this.timetableRefs = new Array(listRes.data.rooms.length);
      this.isScrolling = new Array(listRes.data.rooms.length).fill(false);
      this.setState({
        rooms: listRes.data.rooms,
      });
      this.scrollToToday();
      return;
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          listRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }
  }

  childScrollEvent(e, index) {
    for (let refIndex of this.timetableRefs.keys()) {
      if (refIndex == index) {
        continue;
      }
      this.timetableRefs[refIndex].scrollTo(e.nativeEvent.contentOffset.x);
    }
  }

  childGetScrollIndex() {
    // console.log("get scroll index: ", this.scrollIndex);
    return this.scrollIndex;
  }

  childSetScrollIndex(index) {
    // console.log("set scroll index : ", index);
    this.scrollIndex = index;
  }

  scrollToToday() {
    for (let refIndex of this.timetableRefs.keys()) {
      this.timetableRefs[refIndex].scrollToToday();
    }
  }

  showResvInfoModal(reserve, roomName) {
    let startTime = reserve.startBlock * 30;
    let startHour = Math.floor(startTime / 60);
    let startMin = startTime % 60;
    let endTime = (reserve.endBlock + 1) * 30;
    let endHour = Math.floor(endTime / 60);
    let endMin = endTime % 60;

    let selectedTime =
      (startHour < 10 ? '0' : '') +
      startHour +
      ':' +
      (startMin < 10 ? '0' : '') +
      startMin +
      ' ~ ' +
      (endHour < 10 ? '0' : '') +
      endHour +
      ':' +
      (endMin < 10 ? '0' : '') +
      endMin +
      '';

      if (reserve.endBlock - reserve.startBlock == 0) {
        selectedTime += ' (30분)';
      } else if(reserve.endBlock - reserve.startBlock == 1){
        selectedTime += ' (1시간)';
      } else if(reserve.endBlock - reserve.startBlock == 2){
        selectedTime += ' (1시간 30분)';
      } else if(reserve.endBlock - reserve.startBlock == 3){
        selectedTime += ' (2시간)';
      }

    let reportLink =
      'https://docs.google.com/forms/d/e/1FAIpQLSeCYo0oiuoK-3KNzKFnFLPFP43Bp4fRZq7ulTmxgoMUWGWz8g/viewform?usp=pp_url';
    reportLink +=
      '&entry.284506795=' + encodeURIComponent(roomName).replace('%20', '+');
    reportLink +=
      '&entry.46856824=' +
      encodeURIComponent(
        moment(this.state.date).format('YYYY년 M월 D일') +
          '(' +
          dayOfWeekHan(this.state.date.getDay()) +
          ')',
      ).replace('%20', '+');
    reportLink +=
      '&entry.573216846=' +
      encodeURIComponent(selectedTime).replace('%20', '+');
    this.qswal.show(
      'info',
      '예약 정보',
      '시설명: ' +
        roomName +
        '\n날짜: ' +
        moment(this.state.date).format('YYYY년 M월 D일') +
        '(' +
        dayOfWeekHan(this.state.date.getDay()) +
        ')' +
        '\n시간: ' +
        selectedTime +
        '\n예약자: ' +
        reserve.studentInfo,
      '닫기',
      () => this.qswal.hide(),
      '신고하기',
      () => {
        this.qswal.hide();
        this.navigation.push('ReserveReportScreen', {
          url: reportLink,
        });
      },
    );
  }

  async loadLocker() {
    this.setState({
      typeModalOpen: false,
    });
    this.loading.show();

    let generateTokenRes = await generateToken('itlocker');
    if (generateTokenRes.statusCode == 'SSU0000') {
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
    } else if (generateTokenRes.statusCode == 'SSU2150') {
      this.loading.hide();
      let ssoToken = generateTokenRes.data.ssoToken;
      let callback = generateTokenRes.data.callbackUrl + ssoToken;
      this.navigation.push('ReserveLockerScreen', {
        url: callback,
      });
      return;
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          generateTokenRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Modal
          onBackdropPress={() => this.setState({typeModalOpen: false})}
          isVisible={this.state.typeModalOpen}
          hideModalContentWhileAnimating={true}
          useNativeDriver={true}
          backdropOpacity={0.5}
          style={{justifyContent: 'flex-end', margin: 0}}>
          <View style={styles.modalView}>
            <Text style={styles.modalTitle}>예약할 시설을 선택하세요</Text>
            <View style={{marginTop: 30}}>
              <TouchableOpacity
                style={{flexDirection: 'row', marginBottom: 20}}
                onPress={() => this.setState({typeModalOpen: false})}>
                <View style={{flex: 1}}>
                  <View style={{flexDirection: 'column'}}>
                    <Text style={styles.modalItemTitle}>스터디룸</Text>
                    <Text style={styles.modalItemText}>정보과학관 2-5층</Text>
                  </View>
                </View>
                <View style={{justifyContent: 'center'}}>
                  <Text style={styles.modalItemSubText}>선택됨</Text>
                </View>
              </TouchableOpacity>
              <TouchableOpacity
                style={{flexDirection: 'row'}}
                onPress={() => {
                  this.loadLocker();
                }}>
                <View style={{flex: 1}}>
                  <View style={{flexDirection: 'column'}}>
                    <Text style={styles.modalItemTitle}>사물함</Text>
                    <Text style={styles.modalItemText}>정보과학관 B1-5층</Text>
                  </View>
                </View>
                <View style={{justifyContent: 'center'}}>
                  {/* <Text style={styles.modalItemSubText}>선택됨</Text> */}
                </View>
              </TouchableOpacity>
            </View>
            <BottomSafe />
          </View>
        </Modal>
        <Loading init={false} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <QSwal ref={ref => (this.qswal = ref)} />
        <Header
          rightBtn={
            <View style={{padding: 10}}>
              <List height={30} width={30} />
            </View>
          }
          onRightBtnPressed={() => {
            this.navigation.push('ReserveListScreen');
          }}
        />
        <View style={styles.containerView}>
          <View style={styles.titleView}>
            <View style={{flexDirection: 'column'}}>
              <View style={{flexDirection: 'row'}}>
                <TouchableOpacity
                  style={{flexDirection: 'row'}}
                  onPress={() => this.setState({typeModalOpen: true})}>
                  <Text style={styles.titleText}>스터디룸</Text>
                  <View style={{justifyContent: 'center'}}>
                    <Down height={20} width={20} />
                  </View>
                </TouchableOpacity>
                <View style={{flex: 1}} />
                {/* <TouchableOpacity style={{justifyContent: 'center', alignItems: 'flex-end'}} onPress={() => this.navigation.push("ReserveListScreen")}>
                <List height={30} width={30}></List>
              </TouchableOpacity> */}
              </View>
              <Text style={styles.subText}>예약하기</Text>
            </View>
          </View>

          <View style={styles.refreshView}>
            <View style={{flex: 1, alignItems: 'flex-start'}}>
              <TouchableOpacity
                style={styles.dateView}
                onPress={() => this.setState({dateModalOpen: true})}>
                <Text style={styles.dateText}>
                  {moment(this.state.date).format('YYYY년 M월 D일') +
                    '(' +
                    dayOfWeekHan(this.state.date.getDay()) +
                    ')'}
                </Text>
                <View style={{justifyContent: 'center', marginTop: 2}}>
                  <DownWhite />
                </View>
              </TouchableOpacity>
            </View>
            <View style={{alignItems: 'flex-end', justifyContent: 'center'}}>
              <TouchableOpacity
                style={styles.refreshInnerView}
                onPress={() => this.loadRoomList()}>
                <Text style={styles.refreshText}>새로고침</Text>
                <View style={{justifyContent: 'center'}}>
                  <Refresh />
                </View>
              </TouchableOpacity>
            </View>
          </View>

          <ScrollView style={styles.roomScrollView} bounces={true}>
            {this.state.rooms.map((item, index) => {
              return (
                <View style={styles.roomItemView} key={index}>
                  <TouchableOpacity
                    style={styles.roomItemTopView}
                    onPress={() =>
                      this.navigation.push('ReserveRoomScreen', {
                        date: this.state.date,
                        roomNo: item.no,
                      })
                    }>
                    <View style={styles.roomItemLeftView}>
                      <FastImage
                        style={styles.roomItemImage}
                        source={{uri: item.image}}
                      />
                    </View>
                    <View style={styles.roomItemRightView}>
                      <View style={styles.roomItemInfoView}>
                        <View style={styles.roomItemInfoItemView}>
                          <View style={styles.roomItemInfoItemIcon}>
                            <UserSmall />
                          </View>
                          <Text style={styles.roomItemInfoItemText}>
                            {item.capacity}인실
                          </Text>
                        </View>
                        <View style={styles.roomItemInfoItemView}>
                          <View style={styles.roomItemInfoItemIcon}>
                            <LocationSmall />
                          </View>
                          <Text style={styles.roomItemInfoItemText}>
                            {item.location}
                          </Text>
                        </View>
                      </View>
                      <Text style={styles.roomItemTitleText}>{item.name}</Text>
                    </View>
                    <View
                      style={{
                        justifyContent: 'center',
                        alignItems: 'flex-end',
                        flex: 1,
                      }}>
                      <Right />
                    </View>
                  </TouchableOpacity>
                  <View style={styles.roomItemBottomView}>
                    <TimeTable
                      index={index}
                      ref={ref => (this.timetableRefs[index] = ref)}
                      today={moment(this.state.date).isSame(new Date(), 'day')}
                      after={moment(this.state.date).isAfter(new Date(), 'day')}
                      childScrollEvent={this.childScrollEvent}
                      childGetScrollIndex={this.childGetScrollIndex}
                      childSetScrollIndex={this.childSetScrollIndex}
                      reserves={item.reserves}
                      roomName={item.name}
                      navigation={this.navigation}
                      showResvInfoModal={this.showResvInfoModal}
                    />
                  </View>
                </View>
              );
            })}
          {
              this.state.rooms.length == 0 && <View style={{alignItems: "center", marginTop: "30%"}}>
                <Null height={40} width={40}></Null>
                <Text style={styles.nullText}>예약할 수 있는{"\n"}스터디룸이 없어요.</Text>
              </View>
            }
          </ScrollView>
        </View>
        <Footer menu={2} navigation={this.navigation} />
        <DatePicker
          modal
          title="날짜를 선택하세요"
          confirmText="완료"
          cancelText="취소"
          onCancel={() => this.setState({dateModalOpen: false})}
          onConfirm={val => this.setDate(val)}
          open={this.state.dateModalOpen}
          mode="date"
          androidVariant="nativeAndroid"
          locale="kor"
          date={this.state.date}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  nullText: {
    textAlign: "center",
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: '#797979',
    marginTop: 5
  },
  modalItemTitle: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
    color: '#3F3F3F',
  },
  modalItemText: {
    color: '#767676',
  },
  modalItemSubText: {
    color: '#767676',
  },
  modalTitle: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: '#000000',
  },
  dateView: {
    flexDirection: 'row',
    backgroundColor: '#8075F8',
    paddingLeft: 12,
    paddingRight: 12,
    paddingTop: 3,
    paddingBottom: 3,
    borderRadius: 9,
  },
  dateText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: 'white',
    marginRight: 6,
  },
  modalView: {
    backgroundColor: 'white',
    paddingTop: 30,
    paddingBottom: 30,
    paddingLeft: 22,
    paddingRight: 22,
    borderRadius: 0,
    borderColor: 'rgba(0, 0, 0, 0.1)',
  },
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    flex: 1,
    width: '100%',
    paddingTop: 16,
    // paddingLeft: 24,
    // paddingRight: 24,
  },
  titleView: {
    paddingLeft: 28,
    paddingRight: 28,
  },
  titleText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 30,
    color: '#7267E7',
  },
  subText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 21,
    color: '#3F3F3F',
  },
  pickerView: {
    marginTop: 10,
    alignItems: 'center',
    width: '100%',
  },
  pickerItemText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
    color: '#000000',
  },
  refreshView: {
    flexDirection: 'row',
    paddingLeft: 20,
    paddingRight: 24,
    paddingTop: 20,
  },
  refreshInnerView: {
    flexDirection: 'row',
    justifyContent: 'center',
  },
  refreshText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: '#767676',
    marginRight: 4,
  },
  roomScrollView: {
    paddingLeft: 20,
    paddingRight: 20,
    flex: 1,
    marginTop: 14,
  },
  roomItemView: {
    width: '100%',
    borderRadius: 8,
    overflow: 'hidden',
    borderWidth: 0.6,
    borderColor: '#DFDFDF',
    marginBottom: 13,
  },
  roomItemTopView: {
    width: '100%',
    backgroundColor: '#white',
    paddingLeft: 16,
    paddingRight: 16,
    paddingTop: 14,
    paddingBottom: 8,
    flexDirection: 'row',
  },
  roomItemBottomView: {
    // borderWidth: 1,
    height: 90,
    width: '100%',
    // paddingLeft: 10,
    // paddingRight: 10,
    paddingTop: 8,
    paddingBottom: 0,
  },
  roomItemLeftView: {
    marginRight: 16,
  },
  roomItemImage: {
    height: 50,
    width: 50,
    borderRadius: 10,
  },
  roomItemRightView: {
    // borderWidth: 1,
    justifyContent: 'center',
  },
  roomItemTitleText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
    color: '#000000',
  },
  roomItemInfoView: {
    flexDirection: 'row',
    marginBottom: 6,
  },
  roomItemInfoItemView: {
    borderRadius: 6,
    backgroundColor: '#E7E7E7',
    flexDirection: 'row',
    paddingTop: 3,
    paddingBottom: 3,
    paddingLeft: 6,
    paddingRight: 6,
    marginRight: 5,
  },
  roomItemInfoItemIcon: {
    justifyContent: 'center',
    marginRight: 3,
  },
  roomItemInfoItemText: {
    fontFamily: 'Pretendard-Medium',
    fontWeight: '600',
    fontSize: 12,
    color: '#343434',
  },
});

export default ReserveScreen;
