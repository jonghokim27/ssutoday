from imports import *

LIST_URL = "http://cse.ssu.ac.kr/03_sub/01_sub.htm?page="
# MAX_PAGES = 39
MAX_PAGES = 2
def cse():
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
                print("Error (subroutine/template/cse.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_as = list_soup.select("a.blue")

            for list_a in list_as:
                article = {}

                article['url'] = "http://cse.ssu.ac.kr/03_sub/01_sub.htm" + list_a.attrs['href']
                article['article_id'] = article['url'].split("?no=")[1]
                article['article_id'] = article['article_id'].split("&")[0]
                article['provider'] = constants.PROVIDER["CSE"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/cse.py) : Cannot GET " + article['url'])
                    continue

                if article_request.status_code != 200:
                    print("Error (subroutine/template/cse.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article['title'] = article_soup.select_one(".subject").text
                article['date'] = article_soup.select_one(".name")
                article['date'] = article['date'].select("dd")[1].text
                article['date'] = article['date'].split("   ")[1]
                article['date'] = article['date'].split(" (")[0]
                article['date'] = article['date'].replace(".","-")

                article['content'] = article_soup.select_one(".smartOutput").text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/cse.py) : "+str(e))
