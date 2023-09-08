import {Platform} from 'react-native';

const API_BASE_URL = '<api base url>';

const APP_VERSION = Platform.OS == 'android' ? '2.0.0' : '2.0.0';

const CLIENT_KEY = require("<client key path>").key;

export {API_BASE_URL, APP_VERSION, CLIENT_KEY};
