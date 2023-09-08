import React from 'react';
import {Component} from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import size from '../constants/size';
import BottomSafe from '../components/BottomSafe';
import WebView from 'react-native-webview';
import Loading from '../components/Loading';

class TermsScreen extends Component {
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
          <View style={{flexDirection: 'column'}}>
            <Text style={[styles.mainText, {color: '#555555'}]}>
              슈투데이를 이용하기 위해
            </Text>
            <Text style={[styles.mainText, {color: '#4269CE'}]}>
              이용약관 및 개인정보취급방침에
            </Text>
            <Text style={[styles.mainText, {color: '#4269CE'}]}>
              동의해주세요
            </Text>
          </View>
          <WebView
            source={{
              uri: 'https://sulky-growth-977.notion.site/a94bbf04a93f4f3c9a1282b0e4e6305e?pvs=4',
            }}
            startInLoadingState={true}
            renderLoading={() => <Loading init={true} />}
            style={{
              height: 200,
              width: '100%',
              marginBottom: 20,
              marginTop: 20,
            }}
          />
        </View>
        <View style={styles.footerView}>
          <TouchableOpacity
            style={styles.buttonView}
            onPress={() => this.navigation.replace('LoginScreen')}>
            <View style={styles.buttonRightView}>
              <Text style={styles.buttonText}>동의하기</Text>
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
    paddingLeft: 32,
    paddingRight: 32,
    paddingTop: 31,
  },
  mainText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 21,
  },
  footerView: {
    paddingLeft: 25,
    paddingRight: 25,
    paddingTop: 0,
    paddingBottom: 25,
    width: '100%',
  },
  buttonView: {
    height: 52,
    width: '100%',
    borderColor: '#4269CE',
    borderWidth: 1.5,
    borderRadius: 8,
    flexDirection: 'row',
  },
  buttonRightView: {
    flex: 1,
    paddingRight: 18,
    paddingLeft: 18,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#4269CE',
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
  },
});

export default TermsScreen;
