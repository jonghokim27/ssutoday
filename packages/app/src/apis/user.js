import {post} from './common';

const profile = async () => {
  return await post('student/profile', {}, true);
};

const login = async (sToken, sIdno) => {
  return await post(
    'student/login',
    {
      sToken: sToken,
      sIdno: sIdno,
    },
    false,
  );
};

const logout = async () => {
  return await post('student/logout', {}, true);
};

const updateXnApiToken = async (xnApiToken) => {
  return await post('student/updateXnApiToken', {xnApiToken: xnApiToken}, true);
};

export {profile, login, logout, updateXnApiToken};
