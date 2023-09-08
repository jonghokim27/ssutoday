import axios from 'axios';
import {API_BASE_URL, CLIENT_KEY} from '../constants/setting';
import AsyncStorage from '@react-native-async-storage/async-storage';

const makeHeaders = async (authRequired) => {
  if(authRequired){
    let accessToken = await AsyncStorage.getItem('accessToken');
    let refreshToken = await AsyncStorage.getItem('refreshToken');

    if (!accessToken) {
      accessToken = '';
    }
    if (!refreshToken) {
      refreshToken = '';
    }

    return {
      Authorization: 'Bearer ' + accessToken,
      'Refresh-Token': refreshToken,
      'Client-Key': CLIENT_KEY
    };
  } else {
    return {
      'Client-Key': CLIENT_KEY
    };
  }
};

const refreshToken = async headers => {
  let accessToken = headers['access-token'];
  let refreshToken = headers['refresh-token'];

  if (accessToken && refreshToken) {
    await AsyncStorage.setItem('accessToken', accessToken);
    await AsyncStorage.setItem('refreshToken', refreshToken);
  }
};

const post = async (uri, body, authRequired) => {
  try {
    let headers = await makeHeaders(authRequired);

    let response = await axios.post(API_BASE_URL + uri, body, {
      headers: headers,
      timeout: 5000,
    });
    if (authRequired) {
      await refreshToken(response.headers);
    }

    return response.data;
  } catch (e) {
    if (e.response?.status) {
      if (e.response?.status == 521) {
        return {
          statusCode: 'SSU0000',
          data: null,
          message: 'Failed to connect to server',
        };
      }
    }

    if (e.response?.data) {
      if (authRequired) {
        await refreshToken(e.response.headers);
      }

      return e.response.data;
    }

    return {
      statusCode: 'SSU0000',
      data: null,
      message: 'Failed to connect to server',
    };
  }
};

export {post};
