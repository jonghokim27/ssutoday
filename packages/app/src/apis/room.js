import {post} from './common';

const list = async date => {
  return await post(
    'room/list',
    {
      date,
    },
    true,
  );
};

const get = async (date, roomNo) => {
  return await post(
    'room/get',
    {
      date,
      roomNo,
    },
    true,
  );
};

export {list, get};
