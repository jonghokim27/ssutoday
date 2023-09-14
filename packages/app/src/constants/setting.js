import {Platform} from 'react-native';

const API_BASE_URL = '<api base url>';

const APP_VERSION = Platform.OS == 'android' ? '2.1.2' : '2.1.2';

const CLIENT_KEY = require("<client key path>").key;

export {API_BASE_URL, APP_VERSION, CLIENT_KEY};
