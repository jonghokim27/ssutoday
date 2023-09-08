from imports import *

LIST_URL = "http://media.ssu.ac.kr/sub.php?code=XxH00AXY&category=1&page="
# MAX_PAGES = 34
MAX_PAGES = 2
def media():
    try:
        articles = []
        for i in range(MAX_PAGES, 0, -1):
            print(LIST_URL + str(i))

            try:
                list_request = requests.get(LIST_URL + str(i))
            except Exception as e:
                print(e)
                continue

            if list_request.status_code != 200:
                print("Error (subroutine/template/media.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_trs = list_soup.select("table > tbody > tr")

            for list_tr in list_trs:
                article = {}

                list_a = list_tr.select_one("a")
                article['article_id'] = list_a.attrs['onclick']
                article['article_id'] = article['article_id'].split("viewData('")[1]
                article['article_id'] = article['article_id'].split("');")[0]
                article['url'] = LIST_URL + "&mode=view&board_num=" + article['article_id']
                article['provider'] = constants.PROVIDER["MEDIA"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/media.py) : Cannot GET " + article['url'])
                    continue
                
                if article_request.status_code != 200:
                    print("Error (subroutine/template/media.py) : Cannot GET " + article['url'])
                    continue
                article_request.encoding = 'UTF-8'

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article['title'] = article_soup.select_one("#boardSkin_s_default_view > thead").text.strip()

                viewBody1Table = article_soup.select_one(".s_default_view_body_1 > table")
                viewBody1Tr = viewBody1Table.select("tr")[0]
                viewBody1Td = viewBody1Tr.select("td")[1]

                article['date'] = viewBody1Td.text
                article['date'] = article['date'].split("작성일 :  ")[1].strip()

                article['content'] = article_soup.select_one(".s_default_view_body_2").text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/media.py) : "+str(e))
