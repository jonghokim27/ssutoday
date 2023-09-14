import {post} from './common';

const generateToken = async (clientId) => {
  return await post(
    'sso/generateToken',
    {
      clientId
    },
    true,
  );
};

export {generateToken};
