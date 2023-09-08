import React from 'react';
import {Component} from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Image} from 'react-native';
import size from '../constants/size';
import {AnimatedWelcomeText} from '../components/AnimatedWelcomeText';
import BottomSafe from '../components/BottomSafe';

class WelcomeScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {};
  }

  async componentDidMount() {}

  render() {
    return (
      <View style={styles.mainView}>
        <View style={styles.statusbarView} />
        <View style={styles.containerView}>
          <View style={{flexDirection: 'row'}}>
            <Text style={[styles.logoText, {color: '#1B8FD0'}]}>SSU</Text>
            <Text style={[styles.logoText, {color: '#4269CE'}]}>TODAY</Text>
          </View>
          <AnimatedWelcomeText text="스터디룸 예약과 공지사항 확인을 한번에!" />
        </View>
        <View style={styles.footerView}>
          <TouchableOpacity
            style={styles.buttonView}
            onPress={() => this.navigation.push('TermsScreen')}>
            <View style={styles.buttonLeftView}>
              <Image
                style={styles.buttonLogo}
                source={require('../../assets/images/logo.png')}
              />
            </View>
            <View style={styles.buttonDividerView} />
            <View style={styles.buttonRightView}>
              <Text style={styles.buttonText}>유세인트 로그인</Text>
            </View>
          </TouchableOpacity>
        </View>
        <BottomSafe />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  statusbarView: {
    height: size.STATUSBAR_HEIGHT,
    width: '100%',
  },
  containerView: {
    flex: 1,
    width: '100%',
    justifyContent: 'center',
    marginTop: -52,
    alignItems: 'center',
  },
  footerView: {
    paddingLeft: 25,
    paddingRight: 25,
    paddingTop: 0,
    paddingBottom: 25,
    width: '100%',
  },
  logoText: {
    fontFamily: 'Staatliches-Regular',
    fontSize: 40,
  },
  buttonView: {
    height: 52,
    width: '100%',
    borderColor: '#4269CE',
    borderWidth: 1.5,
    borderRadius: 8,
    flexDirection: 'row',
  },
  buttonLeftView: {
    height: '100%',
    paddingLeft: 18,
    paddingTop: 5,
    paddingBottom: 5,
    paddingRight: 18,
  },
  buttonLogo: {
    width: 40,
    height: 40,
  },
  buttonDividerView: {
    width: 1.5,
    backgroundColor: '#4269CE',
    height: 28,
    marginTop: 12,
  },
  buttonRightView: {
    flex: 1,
    paddingRight: 18,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#4269CE',
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
  },
});

export default WelcomeScreen;
