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

const getOption = async (osType, uuid) => {
  return await post(
    'device/getOption',
    {
      osType,
      uuid,
    },
    true,
  )
}

const updateOption = async (osType, uuid, option, value) => {
  return await post(
    'device/updateOption',
    {
      osType,
      uuid,
      option,
      value
    },
    true,
  )
}

export {register, unregister, getOption, updateOption};
