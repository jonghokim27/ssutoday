import {Platform} from 'react-native';
import {post} from './common';
import {APP_VERSION} from '../constants/setting';

const checkVersion = async () => {
  return await post(
    'device/checkVersion',
    {
      osType: Platform.OS,
      version: APP_VERSION,
    },
    false,
  );
};

export {checkVersion};
