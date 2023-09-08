from imports import *

LIST_URL = "http://stu.ssu.ac.kr/bbs/board.php?bo_table=bd_0201&page="
# MAX_PAGES = 21
MAX_PAGES = 2
def stu():
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
                print("Error (subroutine/template/stu.py) : Cannot GET " + LIST_URL)
                continue

            list_html = list_request.text

            list_soup = BeautifulSoup(list_html, 'html.parser')
            list_as = list_soup.select(".gall_li > a")


            for list_a in list_as:
                article = {}

                article['url'] = list_a.attrs['href']
                article['article_id'] = article['url'].split("&wr_id=")[1]
                article['article_id'] = article['article_id'].split("&page=")[0]
                article['provider'] = constants.PROVIDER["STU"]

                try:
                    article_request = requests.get(article['url'])
                except Exception as e:
                    print("Error (subroutine/template/stu.py) : Cannot GET " + article['url'])
                    continue

                if article_request.status_code != 200:
                    print("Error (subroutine/template/stu.py) : Cannot GET " + article['url'])
                    continue

                article_html = article_request.text
                article_soup = BeautifulSoup(article_html, 'html.parser')

                article['title'] = article_soup.select_one("span.bo_v_tit").text.strip()
                article['date'] = str(article_soup.select_one("section#bo_v_info"))
                article['date'] = article['date'].split('<i aria-hidden="true" class="fa fa-clock-o"></i> ')[1]
                article['date'] = "20" + article['date'].split('<span class="sound_only">')[0].strip() + ":00"

                article['content'] = article_soup.select_one("div#bo_v_con").text
                article['content'] = article['content'].replace(u'\xa0', " ")

                articles.append(article)

        return articles

    except Exception as e:
        print("Error (subroutine/template/stu.py) : "+str(e))
