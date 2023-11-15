import { API_BASE_URL } from '../constants/setting';
import {makeHeaders, post} from './common';

const request = async (roomNo, date, startBlock, endBlock) => {
  return await post(
    'reserve/request',
    {
      roomNo,
      date,
      startBlock,
      endBlock,
    },
    true,
  );
};

const status = async idx => {
  return await post(
    'reserve/status',
    {
      idx,
    },
    true,
  );
};

const list = async (type, page) => {
  return await post(
    'reserve/list',
    {
      type,
      page,
    },
    true,
  );
};

const cancel = async idx => {
  return await post(
    'reserve/cancel',
    {
      idx,
    },
    true,
  );
};

const uploadVerifyPhoto = async (idx, file, filename) => {
  let formData = new FormData();
  formData.append("file", {
    name: filename,
    type: "image/jpeg",
    uri: file
  });
  formData.append("idx", idx);

  let headers = await makeHeaders(true);
  headers['Content-Type'] = "multipart/form-data";

  let response;
  try{
    response = await fetch(API_BASE_URL + "reserve/verifyPhoto/upload", { headers: headers, method: "POST", body: formData });
  } catch(e){
    return {
      statusCode: 'SSU0000',
      data: null,
      message: 'Failed to connect to server',
    };
  }

  let accessToken = response.headers.get("access-token");
  let refreshToken = response.headers.get("refresh-token");
  if (accessToken && refreshToken) {
    await AsyncStorage.setItem('accessToken', accessToken);
    await AsyncStorage.setItem('refreshToken', refreshToken);
  }

  return await response.json();
};

export {request, status, list, cancel, uploadVerifyPhoto};
