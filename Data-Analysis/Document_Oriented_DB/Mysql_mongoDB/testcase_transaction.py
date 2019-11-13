import pymongo
import mysql.connector
import json
import timeit
import time

def insertPayment1(cursor,cnx):
    for i in range(0,50000):
	print "mysql",i
	paymentId = str(i)
	query = "insert into payment_log (ID,PROJECT_NAME,PAYMENT_AMOUNT) value ('"+paymentId+"','123',123)"
	cursor.execute(query)
    print("inserting")
    cnx.commit()

def insertPayment2(db):
    payments=[]
    for i in range(0,50000):
	print "mongodb",i
	payment={}
	payment['ID'] = str(i)
	payment['PROJECT_NAME'] = "123"
	payment['PAYMENT_AMOUNT'] = 123
	payments.append(payment)
    print("inserting")
    db.payment_log.insert(payments)


## mysql
config = {
      'user': 'root',
      'password': '831022',
      'host': 'localhost',
      'database': 'ece656proj',
      'raise_on_warnings': True,
    }
cnx = mysql.connector.connect(**config)
cursor = cnx.cursor()

## mongodb
from pymongo import MongoClient
client = MongoClient()
db = client.ECE656proj

insertPayment1(cursor,cnx)

#insertPayment2(db)

## mysql
cursor.close()
cnx.close()
