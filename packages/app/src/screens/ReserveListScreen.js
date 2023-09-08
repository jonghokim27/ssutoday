import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  ScrollView,
  RefreshControl,
} from 'react-native';

import Loading from '../components/Loading';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

import size from '../constants/size';
import Back from '../../assets/svg/back.svg';
import ClockTime from '../../assets/svg/clocktime.svg';
import {cancel, list} from '../apis/reserve';
import moment from 'moment';
import {dayOfWeekHan} from '../constants/function';
import BottomSafe from '../components/BottomSafe';
import Top from '../../assets/svg/top.svg';


class ReserveListScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.page = 0;
    this.totalPages = 0;
    this.moreLoading = false;
    this.isRendering = false;
    this.state = {
      menu: 1,
      reserves: [],
      refreshing: false,
      isTopButtonVisible: false,
    };
  }

  async setMenu(val) {
    this.setState({
      menu: val,
      reserves: [],
    });

    await this.getList();
  }

  async getList() {
    this.swal.hide();
    await this.loading.show();

    let listRes = await list(this.state.menu, this.page);
    let reserves = [];
    if (listRes.statusCode == 'SSU2130') {
      reserves = listRes.data.reserves;
      this.totalPages = listRes.data.totalPages;
    } else if (listRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          listRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    await this.setState({
      reserves: reserves,
    });

    await this.loading.hide();
  }

  async refresh() {
    this.setState({
      refreshing: true,
    });

    this.page = 0;
    this.totalPages = 0;

    let listRes = await list(this.state.menu, this.page);
    let reserves = this.state.reserves;
    if (listRes.statusCode == 'SSU2130') {
      reserves = listRes.data.reserves;
      this.totalPages = listRes.data.totalPages;
    } else if (listRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          listRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.setState({
      reserves: reserves,
      refreshing: false,
    });
  }

  async nextPage() {
    if (this.moreLoading || this.isRendering) {
      return;
    }
    if (this.totalPages == this.page + 1) {
      return;
    }
    if (this.state.reserves.length < 10) {
      return;
    }
    this.moreLoading = true;
    this.isRendering = true;
    this.isRenderingTimeout = setTimeout(() => this.isRendering = false, 1000);
    this.loading.show();

    this.page = this.page + 1;

    let listRes = await list(this.state.menu, this.page);
    let reserves = this.state.reserves.slice(0);
    if (listRes.statusCode == 'SSU2130') {
      this.setState({
        reserves: []
      });

      reserves.push(...listRes.data.reserves);
      this.totalPages = listRes.data.totalPages;

      this.setState({
        reserves: reserves,
      });
    } else if (listRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          listRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.loading.hide();
    this.moreLoading = false;
  }

  async componentDidMount() {
    await this.getList();
  }

  async cancel(idx, index) {
    this.loading.show();

    let cancelRes = await cancel(idx);
    if (cancelRes.statusCode == 'SSU2140'){
      let reserves = this.state.reserves;
      reserves[index].deletedAt = new Date();
      this.setState({
        reserves: reserves,
      });

      this.loading.hide();
      this.swal.show(
        'success',
        '취소 성공',
        '성공적으로 예약을 취소했습니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (cancelRes.statusCode == 'SSU4141') {
      this.loading.hide();
      this.swal.show(
        'error',
        '취소 실패',
        '이미 지난 날짜입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (cancelRes.statusCode == 'SSU4142') {
      this.loading.hide();
      this.swal.show(
        'error',
        '취소 실패',
        '현재 이용중인 예약입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (cancelRes.statusCode == 'SSU4143') {
      this.loading.hide();
      this.swal.show(
        'error',
        '취소 실패',
        '이미 이용이 완료된 예약입니다.',
        '확인',
        async () => {
          this.swal.hide();
        },
      );
      return;
    } else if (cancelRes.statusCode == 'SSU0000') {
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
  }

  cancelAsk(idx, index) {
    let selectedTime = '';

    let startTime = this.state.reserves[index].startBlock * 30;
    let startHour = Math.floor(startTime / 60);
    let startMin = startTime % 60;
    let endTime = (this.state.reserves[index].endBlock + 1) * 30;
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
      ' ';

    if (
      this.state.reserves[index].endBlock -
        this.state.reserves[index].startBlock ==
      1
    )
      selectedTime += '(1시간)';
    else selectedTime += '(30분)';

    let date = new Date(this.state.reserves[index].date);
    let dateText =
      moment(date).format('YYYY년 M월 D일') +
      '(' +
      dayOfWeekHan(date.getDay()) +
      ')';

    this.qswal.show('warning', '확인 필요', '정말 아래 내용의 예약을\n취소하시겠습니까?\n\n시설명: '+this.state.reserves[index].roomByRoomNo.name + "\n날짜: " + dateText + "\n시간: " + selectedTime, '확인', () => {this.qswal.hide(); this.cancel(idx, index);}, '취소', () => this.qswal.hide());
  }

  isCloseToBottom = ({layoutMeasurement, contentOffset, contentSize}) => {
    const paddingToBottom = 20;

    this.setState({
      isTopButtonVisible: contentOffset.y >= 5,
    });

    return (
      layoutMeasurement.height + contentOffset.y >=
      contentSize.height - paddingToBottom
    );
  };

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
          <Text style={styles.titleText}>예약 내역</Text>
        </View>

        <View style={styles.selectView}>
          <TouchableOpacity
            style={
              this.state.menu == 1 ? styles.selectItemOn : styles.selectItem
            }
            onPress={() => this.setMenu(1)}>
            <Text
              style={
                this.state.menu == 1
                  ? styles.selectItemTextOn
                  : styles.selectItemText
              }>
              이용중 / 이용 대기
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={
              this.state.menu == 0 ? styles.selectItemOn : styles.selectItem
            }
            onPress={() => this.setMenu(0)}>
            <Text
              style={
                this.state.menu == 0
                  ? styles.selectItemTextOn
                  : styles.selectItemText
              }>
              이용 완료
            </Text>
          </TouchableOpacity>
        </View>

        <ScrollView
          style={styles.containerView}
          ref={ref => (this.scrollView = ref)}
          refreshControl={
            <RefreshControl
              refreshing={this.state.refreshing}
              onRefresh={() => {
                this.refresh();
              }}
            />
          }
          onScroll={async ({nativeEvent}) => {
            if (this.isCloseToBottom(nativeEvent)) {
              await this.nextPage();
            }
          }}
          scrollEventThrottle={600}>
          {this.state.reserves.map((item, index) => {
            console.log(item.idx);
            
            let startTime = item.startBlock * 30;
            let startHour = Math.floor(startTime / 60);
            let startMin = startTime % 60;
            let endTime = (item.endBlock + 1) * 30;
            let endHour = Math.floor(endTime / 60);
            let endMin = endTime % 60;

            let time =
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

            if (item.endBlock - item.startBlock == 1)
              {time += " (1시간)";}
            else time += ' (30분)';

            let date = new Date(item.date);
            let dateText =
              moment(date).format('YYYY년 M월 D일') +
              '(' +
              dayOfWeekHan(date.getDay()) + ')';

            let start = new Date(item.date);
            start.setHours(startHour);
            start.setMinutes(startMin);

            let end = new Date(item.date);
            end.setHours(endHour);
            end.setMinutes(endMin);

            let now = new Date();

            let type;

            //취소됨
            if (item.deletedAt != null){
              type = -1;
            }
            //이용 전
            else if (moment(now).isBefore(start)){
              type = 0;
            }
            //이용 중
            else if (moment(now).isBefore(end)){
              type = 1;
            }
            //이용 후
            else if (moment(now).isAfter(end)){
              type = 2;
            }

            return (
              <View style={styles.cardView} key={item.idx}>
                <View style={{flexDirection: 'row', marginBottom: 5}}>
                  <View style={{flex: 1}}>
                    <Text style={styles.cardTitle}>
                      {item.roomByRoomNo.name}
                    </Text>
                  </View>
                  <View style={{alignItems: 'flex-end'}}>
                    <Text style={styles.cardDate}>{dateText}</Text>
                  </View>
                </View>
                <View style={{flexDirection: 'row'}}>
                  <View style={{flex: 1}}>
                    <View style={{flexDirection: 'row'}}>
                      <View
                        style={{
                          flexDirection: 'row',
                          flex: 1,
                          alignItems: 'center',
                        }}>
                        <View
                          style={{justifyContent: 'center', marginBottom: 1}}>
                          <ClockTime />
                        </View>
                        <Text style={styles.cardSubtitle}>{time}</Text>
                      </View>
                      <View style={{alignItems: 'flex-end'}}>
                        {type == -1 && (
                          <View style={styles.cancelDoneBtnView}>
                            <Text style={styles.cancelDoneBtnText}>취소됨</Text>
                          </View>
                        )}
                        {type == 0 && (
                          <TouchableOpacity
                            style={styles.cancelBtnView}
                            onPress={() => this.cancelAsk(item.idx, index)}>
                            <Text style={styles.btnText}>취소</Text>
                          </TouchableOpacity>
                        )}
                        {type == 1 && (
                          <View style={styles.primaryBtnView}>
                            <Text style={styles.btnText}>이용중</Text>
                          </View>
                        )}
                        {type == 2 && (
                          <View style={styles.secondaryBtnView}>
                            <Text style={styles.btnText}>이용완료</Text>
                          </View>
                        )}
                      </View>
                    </View>
                  </View>
                </View>
              </View>
            );
          })}
          <View style={{height: 30}} />
          <BottomSafe />
        </ScrollView>
        {this.state.isTopButtonVisible && (
            <TouchableOpacity
              style={{position: 'absolute', bottom: 30, right: 0}}
              onPress={() => this.scrollView.scrollTo({y: 0, animated: true})}>
              <Top />
            </TouchableOpacity>
          )}
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
    paddingTop: 20,
    flex: 1,
    width: '100%',
    paddingLeft: 24,
    paddingRight: 24,
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
  selectView: {flexDirection: 'row'},
  selectItemOn: {
    width: '50%',
    alignItems: 'center',
    justifyContent: 'center',
    height: 45,
    borderBottomWidth: 3,
    backgroundColor: '#FAFAFA',
  },
  selectItem: {
    width: '50%',
    alignItems: 'center',
    justifyContent: 'center',
    height: 45,
    backgroundColor: '#FAFAFA',
  },
  selectItemTextOn: {
    color: 'black',
    fontFamily: 'Pretendard-Bold',
    fontSize: 15,
  },
  selectItemText: {
    color: 'black',
    fontFamily: 'Pretendard-Medium',
    fontSize: 15,
  },
  cardView: {
    borderWidth: 1,
    borderColor: '#EEEEEE',
    backgroundColor: 'white',
    borderRadius: 17,
    paddingTop: 16,
    paddingBottom: 16,
    paddingLeft: 20,
    paddingRight: 20,
    marginBottom: 10,
  },
  cardTitle: {
    color: 'black',
    fontFamily: 'Pretendard-Bold',
    fontSize: 21,
  },
  cardDate: {
    color: '#ADADAD',
    fontFamily: 'Pretendard-Bold',
    fontSize: 14,
  },
  cardSubtitle: {
    color: '#A6A6A6',
    fontFamily: 'Pretendard-Bold',
    fontSize: 15,
    marginLeft: 3,
    flex: 1,
  },
  cancelBtnView: {
    backgroundColor: '#FF5151',
    paddingLeft: 20,
    paddingRight: 20,
    paddingTop: 7,
    paddingBottom: 7,
    borderRadius: 7,
  },
  cancelDoneBtnView: {
    borderColor: '#FF5151',
    backgroundColor: 'white',
    borderWidth: 2,
    paddingLeft: 11.5,
    paddingRight: 11.5,
    paddingTop: 6,
    paddingBottom: 6,
    borderRadius: 7,
  },
  primaryBtnView: {
    backgroundColor: '#7267E7',
    paddingLeft: 14,
    paddingRight: 14,
    paddingTop: 7,
    paddingBottom: 7,
    borderRadius: 7,
  },
  secondaryBtnView: {
    backgroundColor: '#ECECEC',
    paddingLeft: 10,
    paddingRight: 10,
    paddingTop: 7,
    paddingBottom: 7,
    borderRadius: 7,
  },
  btnText: {
    color: 'white',
    fontFamily: 'Pretendard-Bold',
    fontSize: 14,
  },
  cancelDoneBtnText: {
    color: '#FF5151',
    fontFamily: 'Pretendard-Bold',
    fontSize: 14,
  },
});

export default ReserveListScreen;
