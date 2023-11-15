import React, {Component} from 'react';
import {View, StyleSheet, Animated, Text} from 'react-native';
import LottieView from 'lottie-react-native';

class Loading extends Component {
  constructor(props) {
    super(props);
    this.state = {
      show: this.props.init ? this.props.init : false,
      message: '',
    };
  }

  show = () => {
    this.setState({show: true, message: ''});
  };

  hide = () => {
    this.setState({show: false});
  };

  message = message => {
    this.setState({
      message: message,
    });
  };

  componentDidMount() {
    // this.lottie.play();
    // Animated.timing(this.state.progress, {
    //   toValue: 1,
    //   duration: 5000,
    //   easing: Easing.linear,
    // }).start();
    // Animated.loop(
    //     Animated.sequence([
    //         Animated.timing(this.state.progress, {
    //             toValue: 1,
    //             duration: 2000,
    //             useNativeDriver: true
    //         }, () => {
    //             Animated.timing(this.state.progress, {
    //                 toValue: 0,
    //                 duration: 2000,
    //                 useNativeDriver: true
    //             })
    //         }),
    //     ])
    // ).start();
  }

  render() {
    var modal;
    if (this.state.show) {
      modal = (
        <View style={styles.overlay}>
          <View style={styles.modal_view}>
            <LottieView
              style={{height: 50, width: 50}}
              speed={2}
              source={require('../../assets/lottie/animation_llteq9t0.json')}
              autoPlay
              loop
              ref={instance => (this.lottie = instance)}
            />
            {this.state.message != '' && (
              <Text style={styles.text}>{this.state.message}</Text>
            )}
          </View>
        </View>
      );
    }

    return <>{modal}</>;
  }
}

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    justifyContent: 'center',
    alignItems: 'center',
    left: 0,
    top: 0,
    width: '100%',
    height: '100%',
    zIndex: 999,
    backgroundColor: 'white',
    opacity: 0.9,
  },
  modal_view: {
    height: 200,
    width: '100%',
    zIndex: 9999,
    marginTop: '-25%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    marginTop: 10,
    color: '#1B8FD0',
    fontSize: 15,
    fontFamily: 'Pretendard-Medium',
    textAlign: 'center',
  },
});

export default Loading;
