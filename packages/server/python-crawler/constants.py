import os

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")
MYSQL_DB = os.getenv("MYSQL_DB")

KAFKA_HOST = os.getenv("KAFKA_HOST")

PROVIDER = {
	"SSUCATCH": "ssucatch",
	"STU": "stu",
	"CSE": "cse",
	"SW": "sw",
	"MEDIA": "media",
	"INFOCOM": "infocom",
	"AIX": "aix"
}