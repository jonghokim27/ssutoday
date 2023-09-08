import React from 'react';
import {Component} from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Platform} from 'react-native';
import BottomSafe from './BottomSafe';
import {Shadow} from 'react-native-shadow-2';
import HomeOn from '../../assets/svg/home_on.svg';
import HomeOff from '../../assets/svg/home_off.svg';
import DeskOn from '../../assets/svg/desk_on.svg';
import DeskOff from '../../assets/svg/desk_off.svg';
import MyOn from '../../assets/svg/my_on.svg';
import MyOff from '../../assets/svg/my_off.svg';
import BellOff from '../../assets/svg/bell_off.svg';
import BellOn from '../../assets/svg/bell_on.svg';

class Footer extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      menu: props.menu,
    };
  }

  async componentDidMount() {}

  render() {
    return (
      <>
        <Shadow
          containerStyle={styles.footerView}
          style={styles.shadowView}
          sides={{top: true, bottom: false}}
          distance={0.2}>
          <View style={styles.footerInnerView}>
            <TouchableOpacity
              style={styles.footerItemView}
              onPress={() => this.navigation.navigate('NoticeNavigator')}>
              {this.state.menu == 1 && <HomeOn height={32} width={32} />}
              {this.state.menu != 1 && <HomeOff height={32} width={32} />}
              <Text style={this.state.menu == 1 ? styles.text : styles.textOff}>
                홈
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.footerItemView}
              onPress={() => this.navigation.navigate('ReserveNavigator')}>
              {this.state.menu == 2 && <BellOn height={32} width={32} />}
              {this.state.menu != 2 && <BellOff height={32} width={32} />}
              <Text style={this.state.menu == 2 ? styles.text : styles.textOff}>
                예약
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.footerItemView}
              onPress={() => this.navigation.navigate('MyNavigator')}>
              {this.state.menu == 3 && <MyOn height={32} width={32} />}
              {this.state.menu != 3 && <MyOff height={32} width={32} />}
              <Text style={this.state.menu == 3 ? styles.text : styles.textOff}>
                마이
              </Text>
            </TouchableOpacity>
          </View>
        </Shadow>
        <BottomSafe />
      </>
    );
  }
}

const styles = StyleSheet.create({
  shadowView: {
    height: '100%',
    width: '100%',
  },
  footerView: {
    paddingLeft: '5%',
    paddingRight: '5%',
    paddingTop: 6,
    paddingBottom: 6,
    height: 65,
    width: '100%',
  },
  footerInnerView: {
    width: '100%',
    height: '100%',
    flexDirection: 'row',
  },
  footerItemView: {
    width: '33.3%',
    height: '100%',
    // borderWidth: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 12,
    color: '#878787',
  },
  textOff: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 12,
    color: '#B7B7B7',
  },
});

export default Footer;
