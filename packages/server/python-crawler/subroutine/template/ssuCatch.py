from imports import *

LIST_URL = "https://scatch.ssu.ac.kr/%ea%b3%b5%ec%a7%80%ec%82%ac%ed%95%ad/page/"
# MAX_PAGES = 605
MAX_PAGES = 2

def ssuCatch():
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
                print("Error (subroutine/template/ssuCatch.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_as = list_soup.select(".notice_col3 > a")

            for list_a in list_as:
                article = {}

                article['url'] = list_a.attrs['href']
                article['article_id'] = article['url'].split("&slug=")[1]
                article['article_id'] = article['article_id'].split("&")[0]
                article['article_id'] = urllib.parse.unquote(article['article_id'])
                article['provider'] = constants.PROVIDER["SSUCATCH"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/ssuCatch.py) : Cannot GET " + article['url'])
                    continue
                
                if article_request.status_code != 200:
                    print("Error (subroutine/template/ssuCatch.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article['title'] = article_soup.select_one("h2.font-weight-light.mb-3").text
                article['date'] = article_soup.select_one("div.float-left.mr-4").text.strip()
                article['date'] = article['date'].replace("년 ", "-")
                article['date'] = article['date'].replace("월 ", "-")
                article['date'] = article['date'].replace("일", "")
                article['date'] = article['date'].split("-")[0] + "-" + ("0" + article['date'].split("-")[1] if int(article['date'].split("-")[1]) < 10 else article['date'].split("-")[1]) + "-" + ("0" + article['date'].split("-")[2] if int(article['date'].split("-")[2]) < 10 else article['date'].split("-")[2]) + " 00:00:00"

                article['content'] = article_soup.select_one("div.bg-white.p-4.mb-5 > div:not(.clearfix)").text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/ssuCatch.py) : "+str(e))
