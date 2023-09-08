import {NativeModules, Dimensions} from 'react-native';
const {StatusBarManager} = NativeModules;

const SCREEN_HEIGHT = Dimensions.get('window').height;
const SCREEN_WIDTH = Dimensions.get('window').width;

const STATUSBAR_HEIGHT = StatusBarManager.HEIGHT;

export default {
  SCREEN_HEIGHT,
  SCREEN_WIDTH,
  STATUSBAR_HEIGHT,
};
