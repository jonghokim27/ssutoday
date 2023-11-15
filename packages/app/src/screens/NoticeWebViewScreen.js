import React from 'react';
import {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  Platform,
  Share,
  AppState,
} from 'react-native';
import size from '../constants/size';
import WebView from 'react-native-webview';
import Loading from '../components/Loading';
import Back from '../../assets/svg/back.svg';
import ShareIcon from '../../assets/svg/share.svg';
import Swal from '../components/Swal';
import {get} from '../apis/article';

class NoticeWebViewScreen extends Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.navigation = props.navigation;
    this.idx = props.route.params.idx;
    this.state = {
      url: '',
      appState: AppState.currentState,
    };
  }

  componentWillUnmount() {
    this.appStateSubscription.remove();
  }

  async componentDidMount() {
    this.appStateSubscription = AppState.addEventListener(
      'change',
      nextAppState => {
        if (
          this.state.appState.match(/inactive|background/) &&
          nextAppState === 'active'
        ) {
          this.idx = this.props.route.params.idx;
          this.loadWebView();
        }
        this.setState({appState: nextAppState});
      },
    );

    this.loadWebView();
  }

  async loadWebView() {
    this.loading.show();
    let articleRes = await get(this.idx);
    if (articleRes.statusCode == 'SSU0000') {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '서버에 연결할 수 없어요.\n잠시 후 다시 시도해 주세요.',
        '확인',
        async () => {
          this.navigation.goBack();
        },
      );
      return;
    } else if (articleRes.statusCode == 'SSU4080') {
      this.loading.hide();
      this.swal.show(
        'warning',
        '게시글 없음',
        '요청하신 게시글을 찾을 수 없어요.\n이동되었거나 삭제된 게시글일 수 있어요.',
        '확인',
        async () => {
          this.navigation.goBack();
        },
      );
      return;
    } else if (articleRes.statusCode == 'SSU2080') {
      this.loading.hide();
      this.setState({
        url: decodeURIComponent(articleRes.data.article.url),
      });
      this.title = articleRes.data.article.title;
      this.content = articleRes.data.article.content;
      this.content = this.content.replace(/(\r\n|\n|\r)/gm, '');
      if (this.content.length > 100) {
        this.content = this.content.slice(0, 100) + '...';
      }
      return;
    } else {
      this.loading.hide();
      this.swal.show(
        'error',
        '서버 연결 실패',
        '알 수 없는 오류가 발생했어요.\n잠시 후 다시 시도해 주세요.\n\n오류 코드: ' +
          articleRes.statusCode,
        '확인',
        async () => {
          this.navigation.goBack();
        },
      );
      return;
    }
  }

  render() {
    return (
      <View style={styles.mainView}>
        <Loading ref={ref => (this.loading = ref)} init={true} />
        <Swal ref={ref => (this.swal = ref)} />
        <View style={styles.headerView}>
          <TouchableOpacity
            style={styles.backView}
            onPress={() => this.navigation.goBack()}>
            <Back width={24} height={24} />
          </TouchableOpacity>
          <Text style={styles.titleText}>공지사항 보기</Text>
          <TouchableOpacity
            style={styles.shareView}
            onPress={() =>
              Share.share(
                {
                  message:
                    '[' +
                    this.title +
                    ']\n\n' +
                    this.content +
                    '\n\n자세히 보기: ' +
                    decodeURIComponent(this.state.url) +
                    '\n\n해당 게시글은 슈투데이에서 공유되었어요.',
                  // url: "ssutoday://notice/" + this.idx,
                  title: this.title,
                },
                {
                  dialogTitle: '게시글을 공유하세요.',
                  subject: this.title,
                },
              )
            }>
            <ShareIcon width={24} height={24} />
          </TouchableOpacity>
        </View>
        {this.state.url != '' && (
          <WebView
            ref={ref => (this.webView = ref)}
            style={styles.webView}
            startInLoadingState={true}
            renderLoading={() => <Loading init={true} />}
            source={{
              uri: this.state.url,
            }}
          />
        )}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainView: {
    flex: 1,
    backgroundColor: 'white',
  },
  headerView:
    Platform.OS == 'ios'
      ? {
          paddingTop: size.STATUSBAR_HEIGHT,
          height: size.STATUSBAR_HEIGHT + 50,
          backgroundColor: '#FAFAFA',
          width: '100%',
          justifyContent: 'center',
          alignItems: 'center',
        }
      : {
          height: size.STATUSBAR_HEIGHT / 2 + 50,
          backgroundColor: '#FAFAFA',
          width: '100%',
          justifyContent: 'center',
          alignItems: 'center',
        },
  webView: {
    flex: 1,
    width: '100%',
  },
  titleText: {
    color: 'black',
    fontFamily: 'Pretendard-Medium',
    fontWeight: '600',
    fontSize: 17,
  },
  subText: {
    color: 'black',
    fontFamily: 'Pretendard-Bold',
    fontSize: 12,
  },
  backView:
    Platform.OS == 'ios'
      ? {
          position: 'absolute',
          height: '100%',
          left: 0,
          top: size.STATUSBAR_HEIGHT,
          padding: 16,
          justifyContent: 'center',
        }
      : {
          position: 'absolute',
          height: '100%',
          left: 0,
          top: 0,
          padding: 16,
          justifyContent: 'center',
        },
  shareView:
    Platform.OS == 'ios'
      ? {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: size.STATUSBAR_HEIGHT,
          padding: 16,
          justifyContent: 'center',
        }
      : {
          position: 'absolute',
          height: '100%',
          right: 0,
          top: 0,
          padding: 16,
          justifyContent: 'center',
        },
  backButton: {
    height: 35,
    width: 20,
  },
});

export default NoticeWebViewScreen;
