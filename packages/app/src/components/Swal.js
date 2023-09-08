import React, {Component} from 'react';
import {View, StyleSheet, Animated, Text, TouchableOpacity} from 'react-native';
import Error from '../../assets/svg/error.svg';
import Success from '../../assets/svg/success.svg';
import Warning from '../../assets/svg/warning.svg';

class Swal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      type: '',
      title: '',
      text: '',
      buttonText: '',
      onPress: () => {},
      show: false,
      animation: new Animated.Value(0),
      shakeAnimation: new Animated.Value(0),
    };
  }

  show = async (type, title, text, buttonText, onPress) => {
    await this.setState({
      type: type,
      title: title,
      text: text,
      onPress: onPress,
      buttonText: buttonText,
      show: true,
      animation: new Animated.Value(0),
      shakeAnimation: new Animated.Value(0),
    });
    Animated.timing(this.state.animation, {
      toValue: 1,
      duration: 300,
      useNativeDriver: true,
    }).start();
    Animated.sequence([
      Animated.timing(this.state.shakeAnimation, {
        toValue: 10,
        duration: 70,
        useNativeDriver: true,
      }),
      Animated.timing(this.state.shakeAnimation, {
        toValue: -10,
        duration: 70,
        useNativeDriver: true,
      }),
      Animated.timing(this.state.shakeAnimation, {
        toValue: 10,
        duration: 70,
        useNativeDriver: true,
      }),
      Animated.timing(this.state.shakeAnimation, {
        toValue: 0,
        duration: 70,
        useNativeDriver: true,
      }),
    ]).start();
  };

  hide = () => {
    this.setState({show: false});
  };

  componentDidMount() {}

  render() {
    var modal;
    if (this.state.show) {
      modal = (
        <>
          <View style={styles.overlay} />
          <View style={styles.mainView}>
            <Animated.View
              style={[
                styles.modal_view,
                {
                  opacity: this.state.animation,
                  transform: [{translateX: this.state.shakeAnimation}],
                },
              ]}>
              {this.state.type == 'error' && <Error />}
              {this.state.type == 'success' && <Success />}
              {this.state.type == 'warning' && <Warning />}
              <Text style={styles.title}>{this.state.title}</Text>
              <Text style={styles.text}>{this.state.text}</Text>
              <View style={styles.divider} />
              <TouchableOpacity
                style={styles.buttonView}
                onPress={this.state.onPress}>
                <Text style={styles.buttonText}>{this.state.buttonText}</Text>
              </TouchableOpacity>
            </Animated.View>
          </View>
        </>
      );
    }

    return <>{modal}</>;
  }
}

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    left: 0,
    top: 0,
    width: '100%',
    height: '100%',
    zIndex: 99,
    backgroundColor: 'black',
    opacity: 0.5,
  },
  mainView: {
    position: 'absolute',
    justifyContent: 'center',
    alignItems: 'center',
    left: 0,
    top: 0,
    width: '100%',
    height: '100%',
    zIndex: 999,
  },
  modal_view: {
    width: 300,
    zIndex: 9999,
    backgroundColor: 'white',
    marginTop: '-5%',
    borderRadius: 15,
    paddingTop: 19,
    alignItems: 'center',
  },
  title: {
    color: '#5E5E5E',
    marginTop: 10,
    fontSize: 24,
    fontFamily: 'Pretendard-Bold',
  },
  text: {
    color: '#5E5E5E',
    marginTop: 10,
    fontSize: 17,
    fontFamily: 'Pretendard-Light',
    textAlign: 'center',
  },
  divider: {
    marginTop: 25,
    height: 1,
    backgroundColor: '#DEDEDE',
    width: '100%',
  },
  buttonView: {
    height: 55,
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: '#5E5E5E',
    fontSize: 18,
    fontFamily: 'Pretendard-Medium',
  },
});

export default Swal;
