import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Platform,
  Animated,
} from 'react-native';
import size from '../constants/size';
import Logo from '../../assets/svg/logo.svg';
class Header extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      animation: new Animated.Value(1),
      phase: 2,
    };
  }

  sleep(ms) {
    return new Promise(r => setTimeout(r, ms));
  }

  async componentDidMount() {
    // setInterval(() => this.animation(), 3000);
  }

  // async animation() {
  //   Animated.timing(this.state.animation, {
  //     toValue: 0,
  //     duration: 3000,
  //     useNativeDriver: true,
  //   }).start();
  //   await this.sleep(3000);
  //   // this.setState({phase: this.state.phase == 1 ? 2 : 1});
  //   Animated.timing(this.state.animation, {
  //     toValue: 1,
  //     duration: 3000,
  //     useNativeDriver: true,
  //   }).start();
  //   await this.sleep(3000);
  // }

  render() {
    return (
      <Animated.View
        style={[styles.headerView, {opacity: this.state.animation}]}>
        {this.state.phase == 1 && (
          <View style={{flexDirection: 'row'}}>
            <Text style={[styles.logoText, {color: '#1B8FD0'}]}>SSU</Text>
            <Text style={[styles.logoText, {color: '#4269CE'}]}>TODAY</Text>
          </View>
        )}
        {this.state.phase == 2 && (
          <View style={{flexDirection: 'row'}}>
            <View style={{flex: 1, justifyContent: 'center'}}>
              <Logo />
            </View>
            {this.props.rightBtn && (
              <TouchableOpacity
                style={{justifyContent: 'center'}}
                onPress={() => this.props.onRightBtnPressed()}>
                {this.props.rightBtn}
              </TouchableOpacity>
            )}
          </View>
        )}
      </Animated.View>
    );
  }
}

const styles = StyleSheet.create({
  headerView: {
    paddingTop:
      Platform.OS == 'android'
        ? size.STATUSBAR_HEIGHT / 2 + 10
        : size.STATUSBAR_HEIGHT + 10,
    paddingLeft: 24,
    paddingRight: 14,
    backgroundColor: '#F8F8FA',
    // borderWidth: 1
  },
  logoText: {
    fontFamily: 'Staatliches-Regular',
    fontSize: 35,
    height: 40,
  },
});

export default Header;
