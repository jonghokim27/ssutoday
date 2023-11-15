from imports import *
from subroutine.template.cse import *
from subroutine.template.sw import *
from subroutine.template.ssuCatch import *
from subroutine.template.stu import *
from subroutine.template.media import *
from subroutine.template.infocom import *
from subroutine.template.aix import *

import traceback

def website(DB):
    try:
        articles = []

        print("[INFO] Using SSUCatch Template")
        articles += ssuCatch()
        print("[INFO] Using STU Template")
        articles += stu()
        print("[INFO] Using CSE Template")
        articles += cse()
        print("[INFO] Using SW Template")
        articles += sw()
        print("[INFO] Using MEDIA Template")
        articles += media()
        print("[INFO] Using INFOCOM Template")
        articles += infocom()
        print("[INFO] Using AIX Template")
        articles += aix()
        
        print("[INFO] Connecting DB")
        db = DB()
        
        for article in articles:
            #중복 체크
            select_article_sql = "SELECT * FROM Article WHERE articleNo = %s AND provider = %s"
            db.cursor.execute(select_article_sql, (article['article_id'], article['provider']))
            select_article_result = db.cursor.fetchall()

            if len(select_article_result) == 0:
                #새로운 글 감지
                insert_article_sql = "INSERT INTO Article(provider, articleNo, title, content, url, createdAt) VALUES(%s, %s, %s, %s, %s, %s)"
                db.cursor.execute(insert_article_sql, (article['provider'], article['article_id'], article['title'], article['content'], article['url'], article['date']))
                db.conn.commit()
                article['idx'] = db.cursor.lastrowid
                functions.send_push(article, "created")
            else:
                #수정 여부 감지
                select_article_result = select_article_result[0]
                article['idx'] = select_article_result['idx']
                
                is_edited = False

                if select_article_result['title'] != article['title']:
                    is_edited = True
                if select_article_result['content'] != article['content']:
                    is_edited = True

                if is_edited:
                    edit_article_sql = "UPDATE Article SET title = %s, content = %s, updatedAt = NOW() WHERE idx = %s"
                    db.cursor.execute(edit_article_sql, (article['title'], article['content'], article['idx']))
                    db.conn.commit()

                    functions.send_push(article, "edited")

        print("[INFO] Disconnecting DB")
        db.close()

    except Exception as e:
        print(traceback.format_exc())
        print("Error (subroutine/website.py) : "+str(e))