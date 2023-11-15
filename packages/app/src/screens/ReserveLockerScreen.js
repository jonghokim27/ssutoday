import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Platform,
  Share,
  AppState,
} from 'react-native';
import size from '../constants/size';
import WebView from 'react-native-webview';
import Loading from '../components/Loading';
import Back from '../../assets/svg/back.svg';
import ShareIcon from '../../assets/svg/share.svg';
import Swal from '../components/Swal';

class ReserveLockerScreen extends Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.navigation = props.navigation;
    this.state = {
      url: props.route.params.url,
    };
  }

  componentWillUnmount() {}

  async componentDidMount() {}

  render() {
    return (
      <View style={styles.mainView}>
        <Swal ref={ref => (this.swal = ref)} />
        <View style={styles.headerView}>
          <TouchableOpacity
            style={styles.backView}
            onPress={() => this.navigation.goBack()}>
            <Back width={24} height={24} />
          </TouchableOpacity>
          <Text style={styles.titleText}>사물함 예약</Text>
        </View>
        <WebView
          ref={ref => (this.webView = ref)}
          style={styles.webView}
          startInLoadingState={true}
          renderLoading={() => <Loading init={true} />}
          source={{
            uri: this.state.url,
          }}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
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
  webView: {
    flex: 1,
    width: '100%',
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
  shareView:
    Platform.OS == 'ios'
      ? {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: size.STATUSBAR_HEIGHT,
          padding: 16,
          justifyContent: 'center',
        }
      : {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: 0,
          padding: 16,
          justifyContent: 'center',
        },
  backButton: {
    height: 35,
    width: 20,
  },
});

export default ReserveLockerScreen;
