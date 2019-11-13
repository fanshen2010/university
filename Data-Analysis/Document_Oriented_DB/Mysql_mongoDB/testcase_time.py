import pymongo
import mysql.connector
import json
import timeit

def findLoanDetailInfo1(cursor):    
    query = "select * from loan_info_basic where loanCode ='201506010010'"
    cursor.execute(query)
    loanBasic = cursor.fetchone()
    query = "select * from loan_info_job where loanCode ='201506010010'"
    cursor.execute(query)
    loanJob = cursor.fetchone()
    query = "select * from loan_info_contact where loanCode ='201506010010'"
    cursor.execute(query)
    loanContact = cursor.fetchone()

def findLoanDetailInfo2(db):
    loanInfo = db.loan_info.find({'loanCode':'201506010010'})

## mysql
config = {
      'user': 'root',
      'password': '831022',
      'host': 'localhost',
      'database': 'ece656projtest',
      'raise_on_warnings': True,
    }
cnx = mysql.connector.connect(**config)
cursor = cnx.cursor()

## mongodb
from pymongo import MongoClient
client = MongoClient()
db = client.ECE656projtest

t1 = timeit.Timer(lambda:findLoanDetailInfo1(cursor))
print t1.timeit(1)

t2 = timeit.Timer(lambda:findLoanDetailInfo2(db))
print t2.timeit(1)

## mysql
cursor.close()
cnx.close()
