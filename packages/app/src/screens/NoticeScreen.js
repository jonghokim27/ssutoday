import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  ScrollView,
  RefreshControl,
  Alert,
  Pressable,
  AppState,
  TouchableWithoutFeedback,
  Keyboard,
} from 'react-native';

import size from '../constants/size';
import BottomSafe from '../components/BottomSafe';
import Loading from '../components/Loading';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {getLocaleDateTime, parseMajor} from '../constants/function';
import Header from '../components/Header';
import Footer from '../components/Footer';
import Search from '../../assets/svg/search.svg';
import Check from '../../assets/svg/check.svg';
import {list} from '../apis/article';
import Swal from '../components/Swal';
import Top from '../../assets/svg/top.svg';
import CheckCircle from '../../assets/svg/check-circle.svg';
import notifee, {AndroidImportance, EventType} from '@notifee/react-native';
import Null from '../../assets/svg/null.svg';
Text.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;

class NoticeScreen extends Component {
  constructor(props) {
    super(props);
    this.navigation = props.navigation;
    this.page = 0;
    this.totalPages = 0;
    this.moreLoading = false;
    this.isRendering = false;
    this.profile = {};
    this.state = {
      major: null,
      orderBy: 'DESC',
      search: '',
      provider: [],
      articles: [],
      refreshing: false,
      isTopButtonVisible: false,
    };
    this.appState = AppState.currentState;
  }

  // componentWillUnmount() {
  //   this.appStateSubscription.remove();
  // }

  async componentDidMount() {
    // await notifee.displayNotification({
    //   title: "테스트 메세지입니다.",
    //   body: "양해 부탁드립니다.",
    //   data: {
    //     link: "ssutoday://notice/8",
    //   },
    //   android: {
    //     channelId: 'default',
    //     importance: AndroidImportance.HIGH,
    //   },
    //   ios: {
    //     foregroundPresentationOptions: {
    //       badge: true,
    //       sound: true,
    //       banner: true,
    //       list: true,
    //     },
    //   },
    // });

    // this.appStateSubscription = AppState.addEventListener(
    //   "change",
    //   nextAppState => {
    //       if (
    //         this.appState.match(/inactive|background/) &&
    //         nextAppState === "active"
    //       ) {
    //         this.articleList();
    //       }
    //       this.appState = nextAppState;
    //     }
    // );

    await this.articleList();
  }

  async articleList() {
    this.swal.hide();
    await this.loading.show();

    let profile = await AsyncStorage.getItem('profile');
    if (profile == null) {
      this.loading.hide();
      this.swal.hide();
      this.navigation.navigate('MainScreen');
      return;
    }

    this.profile = JSON.parse(profile);

    let provider = await AsyncStorage.getItem('provider');
    if (provider == null) {
      provider = ['ssuCatch', 'stu', 'major'];
      await AsyncStorage.setItem('provider', JSON.stringify(provider));
    } else {
      provider = JSON.parse(provider);
    }

    let articleRes = await list(0, 'DESC', '', provider);
    let articles = [];
    if (articleRes.statusCode == 'SSU2060') {
      articles = articleRes.data.articles;
      this.totalPages = articleRes.data.totalPages;
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    await this.setState({
      major: parseMajor(this.profile.major),
      provider: provider,
      articles: articles,
    });

    await this.loading.hide();
  }

  async setOrderBy(orderBy) {
    Keyboard.dismiss();
    this.loading.show();
    this.setState({
      orderBy: orderBy,
    });

    this.page = 0;
    this.totalPages = 0;
    let articleRes = await list(
      0,
      orderBy,
      this.state.search,
      this.state.provider,
    );
    let articles = [];
    if (articleRes.statusCode == 'SSU2060') {
      articles = articleRes.data.articles;
      this.totalPages = articleRes.data.totalPages;
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.setState({
      articles: articles,
    });
    this.scrollView.scrollTo({y: 0, animated: false});
    this.loading.hide();
  }

  async setSearch(search) {
    this.setState({
      search: search,
    });
  }

  async toggleProvider(provider) {
    Keyboard.dismiss();
    this.loading.show();

    let providerArr = this.state.provider.slice(0);
    let index = providerArr.indexOf(provider);
    if (index != -1) {
      providerArr.splice(index, 1);
    } else {
      providerArr.push(provider);
    }
    await AsyncStorage.setItem('provider', JSON.stringify(providerArr));

    this.page = 0;
    this.totalPages = 0;
    let articleRes = await list(
      0,
      this.state.orderBy,
      this.state.search,
      providerArr,
    );
    let articles = [];
    if (articleRes.statusCode == 'SSU2060') {
      articles = articleRes.data.articles;
      this.totalPages = articleRes.data.totalPages;
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.setState({
      provider: providerArr,
      articles: articles,
    });
    this.scrollView.scrollTo({y: 0, animated: false});
    this.loading.hide();
  }

  async search() {
    Keyboard.dismiss();
    this.loading.show();

    this.page = 0;
    this.totalPages = 0;
    let articleRes = await list(
      0,
      this.state.orderBy,
      this.state.search,
      this.state.provider,
    );
    let articles = [];
    if (articleRes.statusCode == 'SSU2060') {
      articles = articleRes.data.articles;
      this.totalPages = articleRes.data.totalPages;
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.setState({
      articles: articles,
    });
    this.scrollView.scrollTo({y: 0, animated: false});
    this.loading.hide();
  }

  async refresh() {
    Keyboard.dismiss();
    this.setState({
      refreshing: true,
    });

    this.page = 0;
    this.totalPages = 0;
    let articleRes = await list(
      0,
      this.state.orderBy,
      this.state.search,
      this.state.provider,
    );
    let articles = this.state.articles;
    if (articleRes.statusCode == 'SSU2060') {
      articles = articleRes.data.articles;
      this.totalPages = articleRes.data.totalPages;
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }

    this.setState({
      articles: articles,
      refreshing: false,
    });
  }

  async nextPage() {
    if (this.moreLoading || this.isRendering) {
      return;
    }
    if (this.totalPages == this.page + 1) {
      return;
    }
    if (this.state.articles.length < 20) {
      return;
    }
    this.moreLoading = true;
    this.isRendering = true;
    this.isRenderingTimeout = setTimeout(
      () => (this.isRendering = false),
      1000,
    );
    this.loading.show();

    this.page = this.page + 1;
    let articleRes = await list(
      this.page,
      this.state.orderBy,
      this.state.search,
      this.state.provider,
    );
    let articles = this.state.articles.slice(0);
    if (articleRes.statusCode == 'SSU2060') {
      articles.push(...articleRes.data.articles);
      this.totalPages = articleRes.data.totalPages;
      this.setState({
        articles: articles,
      });
    } else if (articleRes.statusCode == 'SSU0000') {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    } else {
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          await this.swal.hide();
        },
      );
    }
    this.loading.hide();
    this.moreLoading = false;
  }

  parseTimestamp(timestamp) {
    return getLocaleDateTime(timestamp);
  }

  parseProviderHangul(provider) {
    if (provider == "ssucatch") {
      return 'SSU:Catch';
    } else if (provider == "stu") {
      return '총학생회';
    } else {
      return parseMajor(provider);
    }
  }

  isCloseToBottom = ({layoutMeasurement, contentOffset, contentSize}) => {
    const paddingToBottom = 50;

    this.setState({
      isTopButtonVisible: contentOffset.y >= 5,
    });

    return (
      layoutMeasurement.height + contentOffset.y >=
      contentSize.height - paddingToBottom
    );
  };

  render() {
    return (
      <Pressable style={styles.mainView} onPress={() => Keyboard.dismiss()}>
        <Loading init={true} ref={ref => (this.loading = ref)} />
        <Swal ref={ref => (this.swal = ref)} />
        <Header />
        <View style={styles.containerView}>
          <View style={styles.titleView}>
            <View style={styles.titleLeftView}>
              <Text style={styles.titleText}>공지사항</Text>
            </View>
            <View style={styles.titleRightView}>
              <View style={styles.titleRightInnerView}>
                <TouchableOpacity
                  onPress={() => this.setOrderBy('DESC')}
                  style={{flexDirection: 'row', justifyContent: 'center'}}>
                  {this.state.orderBy == 'DESC' && (
                    <View style={{justifyContent: 'center'}}>
                      <CheckCircle style={{marginRight: 3}} />
                    </View>
                  )}
                  <Text
                    style={
                      this.state.orderBy == 'DESC'
                        ? styles.titleRightSelectTextOn
                        : styles.titleRightSelectText
                    }>
                    최신순
                  </Text>
                </TouchableOpacity>
                <View style={styles.titleRightSelectDivider} />
                <TouchableOpacity
                  onPress={() => this.setOrderBy('ASC')}
                  style={{flexDirection: 'row', justifyContent: 'center'}}>
                  {this.state.orderBy == 'ASC' && (
                    <View style={{justifyContent: 'center'}}>
                      <CheckCircle style={{marginRight: 3}} />
                    </View>
                  )}
                  <Text
                    style={
                      this.state.orderBy == 'ASC'
                        ? styles.titleRightSelectTextOn
                        : styles.titleRightSelectText
                    }>
                    오래된순
                  </Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>

          <View style={styles.searchViewOuter}>
            <View style={styles.searchView}>
              <View style={styles.searchViewInner}>
                <TextInput
                  clearButtonMode={'while-editing'}
                  placeholder="검색어를 입력하세요"
                  style={styles.searchTextInput}
                  value={this.state.search}
                  onSubmitEditing={() => this.search()}
                  onChangeText={text => this.setSearch(text)}
                />
                <TouchableOpacity
                  style={{justifyContent: 'center'}}
                  onPress={() => this.search()}>
                  <Search />
                </TouchableOpacity>
              </View>
            </View>
          </View>

          <View style={styles.providerSelectScrollViewOuter}>
            <ScrollView
              style={styles.providerSelectScrollView}
              horizontal={true}
              showsHorizontalScrollIndicator={false}>
              {/* <View style={[styles.providerSelectBadgeView]}>
                <View style={styles.providerSelectBadgeInnerView}>
                  <Check></Check>
                  <Text style={styles.providerSelectBadgeText}>전체</Text>
                </View>
              </View> */}
              <TouchableOpacity
                style={
                  this.state.provider.includes('ssuCatch')
                    ? styles.providerSelectBadgeViewOn
                    : styles.providerSelectBadgeView
                }
                onPress={() => this.toggleProvider('ssuCatch')}>
                <View style={styles.providerSelectBadgeInnerView}>
                  <View style={{justifyContent: 'center'}}>
                    {this.state.provider.includes('ssuCatch') && <Check />}
                  </View>
                  <Text style={styles.providerSelectBadgeText}>SSU:Catch</Text>
                </View>
              </TouchableOpacity>
              <TouchableOpacity
                style={
                  this.state.provider.includes('stu')
                    ? styles.providerSelectBadgeViewOn
                    : styles.providerSelectBadgeView
                }
                onPress={() => this.toggleProvider('stu')}>
                <View style={styles.providerSelectBadgeInnerView}>
                  <View style={{justifyContent: 'center'}}>
                    {this.state.provider.includes('stu') && <Check />}
                  </View>
                  <Text style={styles.providerSelectBadgeText}>총학생회</Text>
                </View>
              </TouchableOpacity>
              <TouchableOpacity
                style={
                  this.state.provider.includes('major')
                    ? styles.providerSelectBadgeViewOn
                    : styles.providerSelectBadgeView
                }
                onPress={() => this.toggleProvider('major')}>
                <View style={styles.providerSelectBadgeInnerView}>
                  <View style={{justifyContent: 'center'}}>
                    {this.state.provider.includes('major') && <Check />}
                  </View>
                  <Text style={styles.providerSelectBadgeText}>
                    {this.state.major}
                  </Text>
                </View>
              </TouchableOpacity>
              <View style={{width: 40}} />
            </ScrollView>
          </View>

          <ScrollView
            style={styles.contentScrollView}
            ref={ref => (this.scrollView = ref)}
            refreshControl={
              <RefreshControl
                refreshing={this.state.refreshing}
                onRefresh={() => {
                  this.refresh();
                }}
              />
            }
            onScroll={async ({nativeEvent}) => {
              Keyboard.dismiss();
              if (this.isCloseToBottom(nativeEvent)) {
                await this.nextPage();
              }
            }}
            scrollEventThrottle={600}>
            {this.state.articles.map((val, index) => {
              if (val.content.length > 100) {
                val.content = val.content.slice(0, 100) + '...';
              }
              val.content = val.content.replace(/(\r\n|\n|\r)/gm, '');
              return (
                <Pressable
                  style={styles.contentItemView}
                  key={index}
                  onPress={() =>
                    this.navigation.push('NoticeWebViewScreen', {
                      idx: val.idx,
                    })
                  }>
                  <Text style={styles.contentItemTitleText}>{val.title}</Text>
                  <Text style={styles.contentItemBodyText}>{val.content}</Text>
                  <View style={styles.contentItemInfoView}>
                    <View style={styles.contentItemInfoLeftView}>
                      <Text style={styles.contentItemInfoText}>
                        {this.parseTimestamp(val.createdAt)}
                      </Text>
                    </View>
                    <View style={styles.contentItemInfoRightView}>
                      <Text style={styles.contentItemInfoText}>
                        {this.parseProviderHangul(val.provider)}
                      </Text>
                    </View>
                  </View>
                  {this.state.articles.length - 1 != index ? (
                    <View style={styles.contentItemDivider} />
                  ) : (
                    <View style={{height: 20}} />
                  )}
                </Pressable>
              );
            })}
            {
              this.state.articles.length == 0 && <View style={{alignItems: "center", marginTop: "30%"}}>
                <Null height={40} width={40}></Null>
                <Text style={styles.nullText}>검색 조건에 일치하는{"\n"}공지사항이 없어요.</Text>
              </View>
            }
          </ScrollView>
          {this.state.isTopButtonVisible && (
            <TouchableOpacity
              style={{position: 'absolute', bottom: 0, right: 0}}
              onPress={() => this.scrollView.scrollTo({y: 0, animated: true})}>
              <Top />
            </TouchableOpacity>
          )}
        </View>
        <Footer menu={1} navigation={this.navigation} />
      </Pressable>
    );
  }
}

const styles = StyleSheet.create({
  nullText: {
    textAlign: "center",
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: '#797979',
    marginTop: 5
  },
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  containerView: {
    flex: 1,
    width: '100%',
    paddingTop: 18,
  },
  titleView: {
    // borderWidth: 1,
    // borderColor: 'blue',
    flexDirection: 'row',
    marginBottom: 10,
    paddingLeft: 24,
    paddingRight: 24,
  },
  titleLeftView: {
    flex: 1,
    justifyContent: 'center',
  },
  titleRightView: {
    // borderWidth: 1,
    justifyContent: 'center',
  },
  titleRightInnerView: {
    // borderWidth: 1,
    // borderColor: 'red',
    flexDirection: 'row',
    justifyContent: 'center',
  },
  titleText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 24,
    color: 'black',
  },
  titleRightSelectTextOn: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 16,
    color: '#1B8FD0',
  },
  titleRightSelectText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 16,
    color: '#C5C5C5',
  },
  titleRightSelectDivider: {
    backgroundColor: '#C8C8C8',
    alignSelf: 'center',
    height: '85%',
    width: 2,
    marginLeft: 6,
    marginRight: 6,
  },
  searchViewOuter: {
    height: 47,
    width: '100%',
    paddingLeft: 24,
    paddingRight: 24,
    marginBottom: 12,
  },
  searchView: {
    height: 47,
    width: '100%',
    backgroundColor: '#EDEDED',
    borderRadius: 11,
    justifyContent: 'center',
    paddingLeft: 16,
    paddingRight: 16,
  },
  searchViewInner: {
    flexDirection: 'row',
  },
  searchTextInput: {
    flex: 1,
    fontFamily: 'Pretendard-Regular',
    fontSize: 18,
    color: '#BCBCBC',
    marginRight: 4,
  },
  providerSelectScrollViewOuter: {
    height: 24,
    marginBottom: 20,
  },
  providerSelectScrollView: {
    flexDirection: 'row',
    paddingLeft: 24,
    paddingRight: 24,
  },
  providerSelectBadgeView: {
    backgroundColor: '#D9D9D9',
    borderRadius: 6,
    height: 24,
    paddingLeft: 7,
    paddingRight: 7,
    justifyContent: 'center',
    marginRight: 6,
  },
  providerSelectBadgeViewOn: {
    backgroundColor: '#A9A1FF',
    borderRadius: 6,
    height: 24,
    paddingLeft: 7,
    paddingRight: 7,
    justifyContent: 'center',
    marginRight: 6,
  },
  providerSelectBadgeInnerView: {
    flexDirection: 'row',
  },
  providerSelectBadgeText: {
    marginLeft: 2,
    fontFamily: 'Pretendard-Regular',
    fontSize: 14,
    color: 'white',
  },
  contentScrollView: {
    flex: 1,
    paddingLeft: 26,
    paddingRight: 26,
  },
  contentItemView: {
    flexDirection: 'column',
  },
  contentItemTitleText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: '#4A4A4A',
    marginBottom: 8,
  },
  contentItemBodyText: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: '#4A4A4A',
    marginBottom: 8,
  },
  contentItemInfoView: {
    flexDirection: 'row',
  },
  contentItemInfoLeftView: {
    flex: 1,
  },
  contentItemInfoRightView: {},
  contentItemInfoText: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 12,
    color: '#B3B3B3',
  },
  contentItemDivider: {
    backgroundColor: '#E4E4E4',
    width: '100%',
    height: 1,
    marginTop: 14,
    marginBottom: 14,
  },
});

export default NoticeScreen;
