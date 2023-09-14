import React from 'react';
import {Component} from 'react';
import {
  View,
  StyleSheet,
  ScrollView,
  Text,
  FlatList,
  TouchableOpacity,
  Pressable,
  Alert,
} from 'react-native';
import TimeBlock30 from '../../assets/svg/timeblock30.svg';
import TimeBlock60 from '../../assets/svg/timeblock60.svg';
import TimeBlock30My from '../../assets/svg/timeblock30my.svg';
import TimeBlock60My from '../../assets/svg/timeblock60my.svg';
import {opacity} from 'react-native-reanimated';
import moment from 'moment';

class TimeTableSelect extends Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.navigation = props.navigation;
    this.scrollLoaded = false;
    this.state = {
      startBlock: null,
      endBlock: null,
    };

    this.scrollTo = this.scrollTo.bind(this);
  }

  async scrollToToday() {
    if (!this.props.today) {
      await this.scrollView.scrollTo({
        x: 0,
        y: 0,
        animated: false,
      });
      return;
    }

    await this.scrollView.scrollTo({
      x:
        (new Date().getHours() - 6 >= 0 ? new Date().getHours() - 6 : 0) * 60 +
        (new Date().getHours() - 6 >= 0 ? new Date().getMinutes() : 0),
      y: 0,
      animated: false,
    });
  }

  scrollTo(x) {
    this.scrollView.scrollTo({x: x, animated: false});
  }

  selectBlock(block) {
    for (let reserve of this.props.reserves) {
      if (reserve.startBlock <= block && block <= reserve.endBlock) {
        return;
      }
    }

    let baseTime = new Date();
    baseTime.setHours(0);
    baseTime.setMinutes(0);
    baseTime.setSeconds(0);
    baseTime.setMilliseconds(0);

    if (
      this.props.today &&
      baseTime.getTime() + block * 30 * 60 * 1000 <= new Date().getTime() &&
      new Date().getMilliseconds() <=
        baseTime.getTime() + block * 30 * 60 * 1000 + 30 * 60 * 1000
    ) {
      this.props.openSwal(
        'warning',
        '안내',
        '이미 진행 중인 시간을 선택하셨습니다.\n예약하신 시간 중 일부만 사용하더라도,\n개인 당 하루 최대 예약 가능 시간은\n동일하게 차감됨을 알려드립니다.',
        '확인',
      );
    }

    if (this.state.startBlock == null) {
      this.setState({
        startBlock: block,
      });
      this.props.setStartBlock(block);
      return;
    }

    if (this.state.endBlock == null) {
      if (block < this.state.startBlock) {
        this.setState({
          startBlock: block,
        });
        this.props.setStartBlock(block);
        return;
      }
      if (block - this.state.startBlock > 1) {
        this.props.openSwal(
          'warning',
          '안내',
          '한 번에 최대 1시간까지만 예약이\n가능합니다. 추가 이용을 원하시면,\n현재 예약을 완료하신 뒤\n추가로 예약해주시기 바랍니다.',
          '확인',
        );
        return;
      }

      if (this.state.startBlock == block) {
        return;
      }

      this.setState({
        endBlock: block,
      });
      this.props.setEndBlock(block);
      return;
    }

    this.setState({
      startBlock: block,
      endBlock: null,
    });
    this.props.setStartBlock(block);
    this.props.setEndBlock(null);
  }

  render() {
    return (
      <ScrollView
        horizontal={true}
        showsHorizontalScrollIndicator={false}
        bounces={false}
        directionalLockEnabled={true}
        // decelerationRate={0}
        // snapToInterval={1}
        // disableIntervalMomentum={true}
        ref={ref => (this.scrollView = ref)}
        onLayout={async () => {
          await this.scrollToToday();
        }}
        scrollEventThrottle={5}>
        <View style={{height: '100%', flexDirection: 'column'}}>
          <View style={{height: '70%', flexDirection: 'row', marginBottom: 3}}>
            {[...Array(16)].map((val, hour) => {
              hour += 6;
              return (
                <View style={{flexDirection: 'row'}} key={hour}>
                  {hour == 6 && <View style={{width: 30}} />}
                  <Pressable
                    style={styles.container}
                    onPress={() => this.selectBlock(hour * 2)}>
                    <View style={styles.bar3} />
                  </Pressable>
                  <Pressable
                    style={styles.container}
                    onPress={() => this.selectBlock(hour * 2 + 1)}>
                    <View style={styles.bar2} />
                  </Pressable>
                  {hour == 21 && (
                    <Pressable style={[styles.container, {width: 30}]}>
                      <View style={styles.bar3} />
                    </Pressable>
                  )}
                </View>
              );
            })}

        

            <View
              style={[
                styles.hiddenBar,
                {
                  left: 30,
                  width: !this.props.today
                    ? (this.props.after
                      ? 0
                      : 16 * 60 + 2)
                    : ((new Date().getHours() - 6 > 16 ? 16 : new Date().getHours() - 6 ) * 60 +
                      (new Date().getHours() - 6 > 16 ? 2 : (new Date().getMinutes() >= 30 ? 31 : 1))),
                  height: '100%',
                },
              ]}
            />

            {this.props.reserves.map((item, index) => {
                    let opacity = 1;
                    if(this.props.today){
                      opacity = (item.endBlock + 1 - 12) * 30 < ((new Date().getHours() - 6) * 60 +
                      new Date().getMinutes()) ? 0.5 : 1;
                    }else if(this.props.after){
                      opacity = 1;
                    }else{
                      opacity = 0.5;
                    }

              return (
                <TouchableOpacity
                  key={index}
                  onPress={() => {
                    if(item.isMine){
                      this.navigation.push("ReserveListScreen");
                    }else{
                      this.props.showResvInfoModal(item, this.props.roomName);
                    }
                  }}
                  style={[
                    styles.redBox,
                    {left: 30 + (item.startBlock - 12) * 30 + 1},
                  ]}>
                  {item.endBlock - item.startBlock == 0 && !item.isMine && (
                    <TimeBlock30 style={{opacity: opacity}}/>
                  )}
                  {item.endBlock - item.startBlock == 1 && !item.isMine && (
                    <TimeBlock60 style={{opacity: opacity}}/>
                  )}
                  {item.endBlock - item.startBlock == 0 && item.isMine && (
                    <TimeBlock30My style={{opacity: opacity}}/>
                  )}
                  {item.endBlock - item.startBlock == 1 && item.isMine && (
                    <TimeBlock60My style={{opacity: opacity}}/>
                  )}
                </TouchableOpacity>
              );
            })}

            {this.props.today &&
              new Date().getHours() >= 6 &&
              new Date().getHours() <= 21 && (
                <View
                  style={[
                    styles.barTopNow,
                    {
                      left:
                        30 +
                        ((new Date().getHours() - 6) * 60 +
                          new Date().getMinutes()) -
                        1.7,
                    },
                  ]}
                />
              )}
            {this.props.today &&
              new Date().getHours() >= 6 &&
              new Date().getHours() <= 21 && (
                <View
                  style={[
                    styles.barNow,
                    {
                      left:
                        30 +
                        ((new Date().getHours() - 6) * 60 +
                          new Date().getMinutes()),
                    },
                  ]}
                />
              )}

            {this.state.startBlock != null && this.state.endBlock == null && (
              <View
                style={[
                  styles.redBox,
                  {left: 30 + (this.state.startBlock - 12) * 30 + 1},
                ]}>
                <TimeBlock30My />
              </View>
            )}

            {this.state.startBlock != null && this.state.endBlock != null && (
              <View
                style={[
                  styles.redBox,
                  {left: 30 + (this.state.startBlock - 12) * 30 + 1},
                ]}>
                <TimeBlock60My />
              </View>
            )}
          </View>
          <View style={{height: '30%', flexDirection: 'row'}}>
            {[...Array(17)].map((val, hour) => {
              hour += 6;
              return (
                <View style={{flexDirection: 'row'}} key={hour}>
                  {hour == 6 && <View style={{width: 22}} />}
                  <View style={{width: hour == 22 ? 25 : 60}}>
                    <Text style={styles.timeText}>
                      {hour < 10 ? '0' + hour : hour}:00
                    </Text>
                  </View>
                </View>
              );
            })}
          </View>
        </View>
      </ScrollView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    width: 30,
    height: '100%',
    flexDirection: 'column-reverse',
  },
  bar3: {
    width: 2,
    height: '100%',
    backgroundColor: '#CCCCCC',
  },
  bar2: {
    width: 2,
    height: '50%',
    backgroundColor: '#CCCCCC',
  },
  barNow: {
    backgroundColor: '#FF6A6A',
    width: 2,
    height: '100%',
    position: 'absolute',
  },
  barTopNow: {
    backgroundColor: '#FF6A6A',
    width: 5,
    height: 5,
    position: 'absolute',
    borderRadius: 5,
    zindex: 999,
    elevation: 999,
  },
  hiddenBar: {
    position: 'absolute',
    backgroundColor: '#EEEEEE',
    opacity: 0.8,
    zindex: 99,
    elevation: 99,
  },
  timeText: {
    color: '#6F6F6F',
    fontSize: 8,
    fontFamily: 'Pretendard-Light',
  },
  redBox: {
    position: 'absolute',
    alignSelf: 'flex-end',
    zindex: 9,
    elevation: 9,
  },
});

export default TimeTableSelect;
