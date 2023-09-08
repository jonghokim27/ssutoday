import os

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")
MYSQL_DB = os.getenv("MYSQL_DB")

KAFKA_HOST = os.getenv("KAFKA_HOST")

PROVIDER = {
	"SSUCATCH": 16,
	"STU": 8,
	"CSE": 4,
	"SW": 2,
	"MEDIA": 1
}