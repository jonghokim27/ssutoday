import React from 'react';
import {Component} from 'react';
import {
  View,
  StyleSheet,
  ScrollView,
  Text,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import TimeBlock30 from '../../assets/svg/timeblock30.svg';
import TimeBlock60 from '../../assets/svg/timeblock60.svg';
import TimeBlock90 from '../../assets/svg/timeblock90.svg';
import TimeBlock120 from '../../assets/svg/timeblock120.svg';
import TimeBlock30My from '../../assets/svg/timeblock30my.svg';
import TimeBlock60My from '../../assets/svg/timeblock60my.svg';
import TimeBlock90My from '../../assets/svg/timeblock90my.svg';
import TimeBlock120My from '../../assets/svg/timeblock120my.svg';
import {opacity} from 'react-native-reanimated';
import moment from 'moment';

class TimeTable extends Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.navigation = props.navigation;
    this.scrollLoaded = false;

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
          this.props.childSetScrollIndex(null);
        }}
        onScroll={e => {
          if (this.props.childGetScrollIndex() == this.props.index) {
            // console.log(this.props.index + "is scrolling");
            this.props.childScrollEvent(e, this.props.index);
          }
        }}
        onScrollBeginDrag={e => {
          // console.log("scroll start!!");
          this.props.childSetScrollIndex(this.props.index);
        }}
        onScrollAnimationEnd={e => {
          // console.log("scroll end!!");
          this.props.childSetScrollIndex(null);
        }}
        // onScrollEndDrag={(e) => {
        //   console.log("scroll end!!");
        //   this.props.childSetScrollIndex(null);
        // }}
        scrollEventThrottle={5}>
        <View style={{height: '100%', flexDirection: 'column'}}>
          <View style={{height: '70%', flexDirection: 'row', marginBottom: 3}}>
            {[...Array(16)].map((val, hour) => {
              hour += 6;
              return (
                <View style={{flexDirection: 'row'}} key={hour}>
                  {hour == 6 && <View style={{width: 30}} />}
                  <View style={styles.container}>
                    <View style={styles.bar3} />
                  </View>
                  <View style={styles.container}>
                    <View style={styles.bar2} />
                  </View>
                  {hour == 21 && (
                    <View style={[styles.container, {width: 30}]}>
                      <View style={styles.bar3} />
                    </View>
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
                    ? this.props.after
                      ? 0
                      : 16 * 60 + 2
                    : (new Date().getHours() - 6 >= 16
                        ? 16
                        : new Date().getHours() - 6) *
                        60 +
                      (new Date().getHours() - 6 >= 16
                        ? 2
                        : new Date().getMinutes() >= 15
                        ? (new Date().getMinutes() >= 45 ? 62 : 31)
                        : 1),
                  height: '100%',
                },
              ]}
            />

            {this.props.reserves.map((item, index) => {
              let opacity = 1;
              if (this.props.today) {
                opacity =
                  (item.endBlock + 1 - 12) * 30 <
                  (new Date().getHours() - 6) * 60 + new Date().getMinutes()
                    ? 0.5
                    : 1;
              } else if (this.props.after) {
                opacity = 1;
              } else {
                opacity = 0.5;
              }

              return (
                <TouchableOpacity
                  key={index}
                  onPress={() => {
                    if (item.isMine) {
                      this.navigation.push('ReserveListScreen');
                    } else {
                      this.props.showResvInfoModal(item, this.props.roomName);
                    }
                  }}
                  style={[
                    styles.redBox,
                    {left: 30 + (item.startBlock - 12) * 30 + 1},
                  ]}>
                  {item.endBlock - item.startBlock == 0 && !item.isMine && (
                    <TimeBlock30 style={{opacity: opacity, marginBottom: 1}} />
                  )}
                  {item.endBlock - item.startBlock == 1 && !item.isMine && (
                    <TimeBlock60 style={{opacity: opacity}} />
                  )}
                  {item.endBlock - item.startBlock == 2 && !item.isMine && (
                    <TimeBlock90 style={{opacity: opacity, marginBottom: 1}} />
                  )}
                  {item.endBlock - item.startBlock == 3 && !item.isMine && (
                    <TimeBlock120 style={{opacity: opacity, marginBottom: 1}} />
                  )}
                  {item.endBlock - item.startBlock == 0 && item.isMine && (
                    <TimeBlock30My
                      style={{opacity: opacity, marginBottom: 1}}
                    />
                  )}
                  {item.endBlock - item.startBlock == 1 && item.isMine && (
                    <TimeBlock60My style={{opacity: opacity}} />
                  )}
                  {item.endBlock - item.startBlock == 2 && item.isMine && (
                    <TimeBlock90My style={{opacity: opacity, marginBottom: 1}} />
                  )}
                  {item.endBlock - item.startBlock == 3 && item.isMine && (
                    <TimeBlock120My style={{opacity: opacity, marginBottom: 1}} />
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

export default TimeTable;
