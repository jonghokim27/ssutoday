import moment from 'moment';

export const parseMajor = major => {
  if (major == 'cse') {
    return '컴퓨터학부';
  } else if (major == 'sw') {
    return '소프트웨어학부';
  } else if (major == 'media') {
    return '글로벌미디어학부';
  }
};

export const getLocaleDateTime = datetime => {
  return moment(datetime).format('YYYY년 M월 D일 HH시 mm분');
  return datetime;
};

export const dayOfWeekHan = day => {
  switch (day) {
    case 0:
      return '일';
    case 1:
      return '월';
    case 2:
      return '화';
    case 3:
      return '수';
    case 4:
      return '목';
    case 5:
      return '금';
    case 6:
      return '토';
    default:
      return '';
  }
};
