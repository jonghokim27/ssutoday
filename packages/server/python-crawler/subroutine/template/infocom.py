from imports import *

LIST_URL = "http://infocom.ssu.ac.kr/kor/notice/undergraduate.php?code=notice&pNo="
# MAX_PAGES = 36
MAX_PAGES = 2
def infocom():
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
                print("Error (subroutine/template/infocom.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_as = list_soup.select(".con_box")

            for list_a in list_as:
                article = {}

                article['url'] = "http://infocom.ssu.ac.kr" + list_a.attrs['href']
                article['article_id'] = article['url'].split("&idx=")[1]
                article['article_id'] = article['article_id'].split("&")[0]
                article['provider'] = constants.PROVIDER["INFOCOM"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/infocom.py) : Cannot GET " + article['url'])
                    continue

                if article_request.status_code != 200:
                    print("Error (subroutine/template/infocom.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article['title'] = article_soup.select_one(".subject").text
                article['date'] = article_soup.select_one(".date").text
                article['date'] = article['date'].replace(". ","-")

                article['content'] = article_soup.select_one(".con").text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/infocom.py) : "+str(e))
