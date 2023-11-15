from imports import *

LIST_URL = "http://aix.ssu.ac.kr/notice.html?&page="
MAX_PAGES = 17
# MAX_PAGES = 2
def aix():
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
                print("Error (subroutine/template/aix.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_table = list_soup.select_one("table")
            list_trs = list_table.select("tr")

            cnt = 0
            for list_tr in list_trs:
                cnt += 1

                if cnt == 1:
                    continue

                list_a = list_tr.select_one("a")

                article = {}

                article['url'] = "http://aix.ssu.ac.kr/" + list_a.attrs['href']
                article['article_id'] = article['url'].split("&idx=")[1]
                article['article_id'] = article['article_id'].split("&")[0]
                article['provider'] = constants.PROVIDER["AIX"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/aix.py) : Cannot GET " + article['url'])
                    continue

                if article_request.status_code != 200:
                    print("Error (subroutine/template/aix.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article_table = article_soup.select_one("table")

                article['title'] = article_table.select_one("h4").text
                article['date'] = article_table.select("tr")[1].text
                article['date'] = article['date'].split("작성일 : ")[1]
                article['date'] = article['date'].split(" \xa0")[0]
                article['date'] = article['date'].replace(".","-")

                article['content'] = article_table.select("tr")[2].text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/aix.py) : "+str(e))
