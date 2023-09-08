from imports import *

LIST_URL = "https://sw.ssu.ac.kr/bbs/board.php?bo_table=sub6_1&page="
# MAX_PAGES = 71
MAX_PAGES = 2
def sw():
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
                print("Error (subroutine/template/sw.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_as = list_soup.select("table > tr > td > a")

            for list_a in list_as:
                article = {}

                article['article_id'] = list_a.attrs['href'].split("&wr_id=")[1]
                article['article_id'] = article['article_id'].split("&page=")[0]
                article['url'] = LIST_URL + "&wr_id=" + article['article_id']
                article['provider'] = constants.PROVIDER["SW"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/sw.py) : Cannot GET " + article['url'])
                    continue

                if article_request.status_code != 200:
                    print("Error (subroutine/template/sw.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')


                article['title'] = article_soup.select_one(".bo_view_1 > div").text.strip()
                article['date'] = str(article_soup.select_one(".bo_view_1 > table"))
                article['date'] = article['date'].split("<th>작성일")[1].split("<th>")[0].strip()
                article['date'] = article['date'].replace("<td>","").replace("</td>","")
                article['date'] = "20" + article['date'] + ":00"

                article['content'] = article_soup.select_one("#writeContents").text

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/sw.py) : "+str(e))
