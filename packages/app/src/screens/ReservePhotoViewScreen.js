import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
} from 'react-native';

import Loading from '../components/Loading';
import Swal from '../components/Swal';
import QSwal from '../components/QSwal';

Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

import size from '../constants/size';
import Back from '../../assets/svg/back.svg';
import { ImageZoom } from '@likashefqet/react-native-image-zoom';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

class ReservePhotoViewScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.state = {
      url: props.route.params.url,
      width: 0,
      height: 0
    };
  }

  async componentDidMount() {
    await Image.getSizeWithHeaders(this.state.url, {}, 
      (width, height) => {
          this.setState({
            width: width,
            height: height
          })
      });
  }

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
          <Text style={styles.titleText}>인증샷 보기</Text>
        </View>

        <View style={styles.containerView}>
          <GestureHandlerRootView style={{flex: 1, width: '100%'}}>
            <ImageZoom
              uri={this.state.url}
              minScale={0.5}
              renderLoader={() => <Loading init={true}></Loading>}
            />
          </GestureHandlerRootView>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    backgroundColor: "#F5F6F8",
    flex: 1,
    width: '100%',
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
});

export default ReservePhotoViewScreen;
