import {post} from './common';

const register = async (osType, uuid, pushToken) => {
  return await post(
    'device/register',
    {
      osType,
      uuid,
      pushToken,
    },
    true,
  );
};

const unregister = async (osType, uuid) => {
  return await post(
    'device/unregister',
    {
      osType,
      uuid,
    },
    true,
  );
};

export {register, unregister};
