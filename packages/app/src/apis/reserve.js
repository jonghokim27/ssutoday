import {post} from './common';

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

export {request, status, list, cancel};
