import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  ScrollView,
} from 'react-native';

import Header from '../components/Header';
import Loading from '../components/Loading';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';
import Down from '../../assets/svg/down.svg';
Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;
import {get} from '../apis/room';
import DatePicker from 'react-native-date-picker';
import moment from 'moment';
import size from '../constants/size';
import Back from '../../assets/svg/back.svg';
import FastImage from 'react-native-fast-image';
import UserSmall from '../../assets/svg/user-small.svg';
import LocationSmall from '../../assets/svg/location-small.svg';
import {dayOfWeekHan} from '../constants/function';
import RefreshFill from '../../assets/svg/refresh-fill.svg';
import TimeTableSelect from '../components/TimeTableSelect';
import Time from '../../assets/svg/time.svg';
import Reset from '../../assets/svg/reset.svg';
import ClockPurple from '../../assets/svg/clock-purple.svg';
import {request, status as getStatus} from '../apis/reserve';
import BottomSafe from '../components/BottomSafe';
import Exclamation from '../../assets/svg/exclamation-mark.svg';

class ReserveRoomScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      roomNo: props.route.params.roomNo,
      date: props.route.params.date,
      dateModalOpen: false,
      room: {
        no: '',
        name: '',
        capacity: 0,
        location: '',
        tags: '',
        image: '',
        bigImage: '',
        reserves: [],
      },
      startBlock: null,
      endBlock: null,
    };

    this.idx = null;
    this.openSwal = this.openSwal.bind(this);
    this.setStartBlock = this.setStartBlock.bind(this);
    this.setEndBlock = this.setEndBlock.bind(this);
  }

  async componentDidMount() {
    await this.loadRoom();
  }

  async setDate(val) {
    await this.setState({
      date: val,
      dateModalOpen: false,
    });
    this.reset();
    await this.loadRoom();
  }

  async loadRoom() {
    this.loading.show();
    let getRes = await get(
      moment(this.state.date).format('YYYY-MM-DD'),
      this.state.roomNo,
    );
    if (getRes.statusCode == 'SSU0000') {
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
    } else if (getRes.statusCode == 'SSU2100') {
      this.loading.hide();
      this.setState({
        room: getRes.data.room,
      });
      this.timetable.scrollToToday();
      return;
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          getRes.statusCode,
        '확인',
        async () => {
          this.navigation.goBack();
        },
      );
      return;
    }
  }

  openSwal(type, title, message, confirmText) {
    this.swal.show(type, title, message, confirmText, async () => {
      this.swal.hide();
    });
  }

  setStartBlock(block) {
    this.setState({
      startBlock: block,
    });
  }

  setEndBlock(block) {
    this.setState({
      endBlock: block,
    });
  }

  reset() {
    this.setState({
      startBlock: null,
      endBlock: null,
    });
    this.timetable.setState({
      startBlock: null,
      endBlock: null,
    });
  }

  async reserve() {
    this.loading.show();
    this.loading.message('대기열에 요청을 추가하고 있어요.');
    let requestRes = await request(
      this.state.roomNo,
      moment(this.state.date).format('YYYY-MM-DD'),
      this.state.startBlock,
      this.state.endBlock == null ? this.state.startBlock : this.state.endBlock,
    );
    if (requestRes.statusCode == 'SSU0000') {
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
    } else if (requestRes.statusCode == 'SSU5091') {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '현재 일시적으로 예약이 불가능합니다.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (requestRes.statusCode == 'SSU2090') {
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          requestRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }
    this.loading.message('요청이 처리되기를 기다리고 있어요.');
    this.idx = requestRes.data.idx;
    this.statusInterval = setInterval(() => this.reserveStatus(), 500);
  }

  async reserveStatus() {
    let statusRes = await getStatus(this.idx);
    if (statusRes.statusCode == 'SSU0000') {
      clearInterval(this.statusInterval);
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
    } else if (statusRes.statusCode == 'SSU2120') {
    } else {
      clearInterval(this.statusInterval);
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          statusRes.statusCode,
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }

    let status = statusRes.data.status;
    if (status == 0) {
      return;
    }

    clearInterval(this.statusInterval);
    if (status == 1) {
      this.loading.hide();
      this.swal.show(
        'success',
        '예약 성공',
        '성공적으로 스터디룸을 예약했습니다.',
        '확인',
        async () => {
          this.navigation.replace('ReserveListScreen');
        },
      );
      return;
    } else if (status == 2) {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '이미 지난 날짜입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (status == 3) {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '이미 지나간 시간입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (status == 4) {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '전날 07:00 부터 예약이 가능합니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (status == 5) {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '이미 예약된 시간입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (status == 6) {
      this.loading.hide();
      this.swal.show(
        'error',
        '예약 실패',
        '하루 최대 예약 가능 시간을 초과했습니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    }
  }

  reserveAsk() {
    let selectedTime = '';
    if (this.state.startBlock) {
      let startTime = this.state.startBlock * 30;
      let startHour = Math.floor(startTime / 60);
      let startMin = startTime % 60;
      let endTime = (this.state.startBlock + 1) * 30;
      let endHour = Math.floor(endTime / 60);
      let endMin = endTime % 60;
      if (this.state.endBlock == null) {
        selectedTime =
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
          ' (30분)';
      } else {
        let endTime = (this.state.endBlock + 1) * 30;
        let endHour = Math.floor(endTime / 60);
        let endMin = endTime % 60;
        selectedTime =
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
          ' (1시간)';
      }
    }

    let dateText =
      moment(this.state.date).format('YYYY년 M월 D일') +
      '(' +
      dayOfWeekHan(this.state.date.getDay()) +
      ')';

    this.qswal.show(
      'warning',
      '확인 필요',
      '정말 아래 내용으로\n예약을 진행하시겠습니까?\n\n시설명: ' +
        this.state.room.name +
        '\n날짜: ' +
        dateText +
        '\n시간: ' +
        selectedTime,
      '예약',
      () => {
        this.qswal.hide();
        this.reserve();
      },
      '취소',
      () => this.qswal.hide(),
    );
  }

  render() {
    let tags = this.state.room.tags.split(',');

    let selectedTime = '';
    if (this.state.startBlock) {
      let startTime = this.state.startBlock * 30;
      let startHour = Math.floor(startTime / 60);
      let startMin = startTime % 60;
      let endTime = (this.state.startBlock + 1) * 30;
      let endHour = Math.floor(endTime / 60);
      let endMin = endTime % 60;
      if (this.state.endBlock == null) {
        selectedTime =
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
          ' (30분)';
      } else {
        let endTime = (this.state.endBlock + 1) * 30;
        let endHour = Math.floor(endTime / 60);
        let endMin = endTime % 60;
        selectedTime =
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
          ' (1시간)';
      }
    }

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
          <Text style={styles.titleText}>{this.state.room.name}</Text>
        </View>

        <ScrollView
          style={styles.containerView}
          bounces={false}
          directionalLockEnabled={true}>
          <FastImage
            source={{uri: this.state.room.bigImage}}
            style={{width: '100%', height: 200}}
          />
          <View style={styles.roomItemInfoView}>
            <View style={styles.roomItemInfoItemView}>
              <View style={styles.roomItemInfoItemIcon}>
                <UserSmall />
              </View>
              <Text style={styles.roomItemInfoItemText}>
                {this.state.room.capacity}인실
              </Text>
            </View>
            <View style={styles.roomItemInfoItemView}>
              <View style={styles.roomItemInfoItemIcon}>
                <LocationSmall />
              </View>
              <Text style={styles.roomItemInfoItemText}>
                {this.state.room.location}
              </Text>
            </View>
            {tags.map(item => {
              return (
                <View
                  style={[
                    styles.roomItemInfoItemView,
                    {paddingLeft: 8, paddingRight: 8},
                  ]}>
                  <Text style={styles.roomItemInfoItemText}>{item}</Text>
                </View>
              );
            })}
          </View>
          <View style={styles.dateView}>
            <TouchableOpacity
              onPress={() => this.setState({dateModalOpen: true})}>
              <Text style={styles.dateViewText}>
                {moment(this.state.date).format('YYYY년 M월 D일') +
                  '(' +
                  dayOfWeekHan(this.state.date.getDay()) +
                  ')'}
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.dateRefreshView}
              onPress={() => this.loadRoom()}>
              <RefreshFill />
            </TouchableOpacity>
          </View>

          <View style={styles.timetableView}>
            <TimeTableSelect
              today={moment(this.state.date).isSame(new Date(), 'day')}
              after={moment(this.state.date).isAfter(new Date(), 'day')}
              ref={ref => (this.timetable = ref)}
              openSwal={this.openSwal}
              reserves={this.state.room.reserves}
              setStartBlock={this.setStartBlock}
              setEndBlock={this.setEndBlock}
            />
          </View>

          {this.state.startBlock == null && (
            <View style={[styles.noSelectView]}>
              <View style={{alignItems: 'center', marginTop: '12%'}}>
                <Time />
                <Text style={styles.noSelectViewText}>선택된 시간 없음</Text>
              </View>
            </View>
          )}

          {this.state.startBlock != null && (
            <View style={styles.selectedView}>
              <View style={{flexDirection: 'row'}}>
                <View style={styles.selectedLeftView}>
                  <View style={{flexDirection: 'row', marginLeft: 4}}>
                    <View style={{justifyContent: 'center', marginBottom: 1}}>
                      <ClockPurple />
                    </View>
                    <View style={{justifyContent: 'center'}}>
                      <Text style={styles.selectedText}>선택됨</Text>
                    </View>
                  </View>
                  <View style={{alignItems: 'center'}}>
                    <Text style={styles.selectedTitleText}>{selectedTime}</Text>
                  </View>
                </View>
                <TouchableOpacity
                  style={styles.selectedRightView}
                  onPress={() => this.reset()}>
                  <Reset />
                  <Text style={styles.selectedClearText}>초기화</Text>
                </TouchableOpacity>
              </View>

              <View style={{flexDirection: 'row', marginTop: 20}}>
                <View style={[styles.selectedLeftView]}>
                  <View style={{flexDirection: 'row'}}>
                    <View style={{justifyContent: 'center'}}>
                      <Exclamation />
                    </View>
                    <View style={{justifyContent: 'center', marginLeft: 4}}>
                      <Text style={[styles.selectedText, {color: '#F85050'}]}>
                        이용 규칙
                      </Text>
                    </View>
                  </View>
                  <View style={{alignItems: 'flex-start', marginTop: 8}}>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 스터디룸 이용 인원은 최소 3명입니다.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 스터디룸을 타 학부 학생과 함께 이용하는 행위는 금지되어
                      있습니다.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 예약 취소는 이용 시작 전에만 가능합니다.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 예약한 시간에 스터디룸 이용이 불가능하다면, 다른
                      이용자를 위해 이용 시작 전에 예약을 취소해주세요.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 다음 이용자를 위해 스터디룸 이용이 종료되기 5분 전부터
                      자리를 정리하고 퇴실해주세요.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 예약자 본인은 예약하신 시간에 스터디룸에 재실해야
                      합니다.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 이용 규칙을 지키지 않으실 경우, 추후 스터디룸 예약 시
                      불이익이 있을 수 있습니다.
                    </Text>
                    <Text
                      style={[
                        styles.selectedTitleText,
                        {fontSize: 14, fontFamily: 'Pretendard-Bold'},
                      ]}>
                      ● 예약을 진행하는 것은 위 이용 규칙의 모든 내용에
                      동의한다는 것으로 간주합니다.
                    </Text>
                  </View>
                </View>
              </View>

              <TouchableOpacity
                style={styles.reserveView}
                onPress={() => this.reserveAsk()}>
                <Text style={styles.reserveText}>에약하기</Text>
              </TouchableOpacity>
            </View>
          )}
          <BottomSafe />
        </ScrollView>
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
  dateText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: 'white',
    marginRight: 6,
  },
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    flex: 1,
    width: '100%',
    // paddingLeft: 24,
    // paddingRight: 24,
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
  roomItemInfoView: {
    flexDirection: 'row',
    marginTop: 2,
    paddingLeft: 16,
    paddingRight: 16,
  },
  roomItemInfoItemView: {
    marginTop: 14,
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
    fontSize: 12,
    fontWeight: '600',
    color: '#343434',
  },
  dateView: {
    marginTop: 27,
    paddingLeft: 16,
    paddingRight: 16,
    flexDirection: 'row',
  },
  dateViewText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: '#222222',
  },
  dateRefreshView: {
    justifyContent: 'center',
    marginLeft: 8,
    marginTop: 1,
  },
  timetableView: {
    marginTop: 14,
    paddingTop: 14,
    backgroundColor: '#F3F3F3',
    height: 90,
    width: '100%',
  },
  noSelectView: {flex: 1, justifyContent: 'center', alignItems: 'center'},
  noSelectViewText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: '#BBBBBB',
  },
  selectedView: {flex: 1, marginTop: 30, paddingLeft: 30, paddingRight: 30},
  selectedLeftView: {
    flex: 1,
    marginRight: 10,
    borderWidth: 1,
    borderColor: '#DBDBDB',
    borderRadius: 17,
    paddingTop: 15,
    paddingBottom: 15,
    paddingLeft: 16,
    paddingRight: 16,
  },
  selectedText: {fontFamily: 'Pretendard-Bold', fontSize: 13, color: '#7288FF'},
  selectedTitleText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 15,
    // textAlign: 'center',
    color: '#636363',
  },
  selectedRightView: {
    flexDirection: 'column',
    alignItems: 'center',
    paddingTop: 20,
    paddingBottom: 20,
    paddingLeft: 26,
    paddingRight: 26,
    borderWidth: 1,
    borderColor: '#DBDBDB',
    borderRadius: 17,
  },
  selectedClearText: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 10,
    color: '#333333',
    marginTop: 1,
  },
  reserveView: {
    marginTop: 20,
    width: '100%',
    backgroundColor: '#8094FF',
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    paddingTop: 18,
    paddingBottom: 18,
    paddingLeft: 16,
    paddingRight: 12,
    marginBottom: 20,
  },
  reserveText: {fontFamily: 'Pretendard-Bold', fontSize: 18, color: 'white'},
});

export default ReserveRoomScreen;
