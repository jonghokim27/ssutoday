import {post} from './common';

const list = async (page, orderBy, search, provider) => {
  return await post(
    'article/list',
    {
      page,
      orderBy,
      search,
      provider,
    },
    true,
  );
};

const get = async idx => {
  return await post(
    'article/get',
    {
      idx,
    },
    true,
  );
};

export {list, get};
