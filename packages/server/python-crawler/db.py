from imports import *

class DB:
    def __init__(self):
        try:
            self.conn = pymysql.connect(host=constants.MYSQL_HOST, user=constants.MYSQL_USER, password=constants.MYSQL_PASSWORD, db=constants.MYSQL_DB, charset='utf8mb4') 
            self.cursor = self.conn.cursor(pymysql.cursors.DictCursor)
        except Exception as e:
            print("Error (db.py) : "+str(e))

    def close(self):
        self.conn.close()