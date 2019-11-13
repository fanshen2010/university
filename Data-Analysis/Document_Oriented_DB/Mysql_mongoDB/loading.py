# loading from mysql to python
# encoding=utf-8
import pymongo
import mysql.connector
import json
import re



def initData(jsonData,delColumnNameArray):
    for delElem in delColumnNameArray:
        del jsonData[delElem]

    for key in jsonData:
        x = re.match(r'^.*{.*}.*$',json.dumps(jsonData[key]))
        if x:
            jsonData[key]= json.loads(jsonData[key])

    return jsonData



## mysql
config = {
    'user': 'root',
    'password': '831022',
    'host': 'localhost',
    'database': 'ece656projtest',
    'raise_on_warnings': True,
    'charset':'utf8'
}
cnx = mysql.connector.connect(**config)
cursor = cnx.cursor(dictionary=True)


## mongodb
from pymongo import MongoClient
client = MongoClient()
db = client.ECE656projtest

query = 'select * from loan_info_basic'
cursor.execute(query)
for row in cursor:
    loanInfo =  db.loan_info.find_one({'loanCode':row['loanCode']})
    if loanInfo:
        print "loading failed"
        exit()

loanInfoNew = []
query = 'select * from loan_info_basic'
cursor.execute(query)
for row in cursor:
    loanInfo={}
    loanInfo['loanCode']=row['loanCode']
    loanInfo['basicInfo']= initData(row,['id','loanCode'])    
    loanInfoNew.append(loanInfo)
db.loan_info.insert(loanInfoNew)


query = 'select * from loan_info_job'
cursor.execute(query)
for row in cursor:
    loanInfo =  db.loan_info.find_one({'loanCode':row['loanCode']})
    if loanInfo:
        print "have"
        loanInfo['jobInfo']= initData(row,['id','loanCode'])
        db.loan_info.update({'loanCode':loanInfo['loanCode']},loanInfo)
    else:
        print "not have"
        loanInfo={}
        loanInfo['loanCode']=row['loanCode']
        loanInfo['jobInfo']= initData(row,['id','loanCode'])
        db.loan_info.insert(loanInfo)


query = 'select distinct(loanCode) from loan_info_contact'
cursor.execute(query)
loanCodes = cursor.fetchall()
for loanCode in loanCodes:
    loanInfoContacts = []
    query = 'select * from loan_info_contact where loanCode="'+loanCode['loanCode']+'"'
    cursor.execute(query)
    for row in cursor:
        loanInfoContacts.append(initData(row,['id','loanCode']))
    
    loanInfo =  db.loan_info.find_one({'loanCode':loanCode['loanCode']})
    if loanInfo:
        print "have"
        loanInfo['contactInfo']= loanInfoContacts
        db.loan_info.update({'loanCode':loanInfo['loanCode']},loanInfo)
    else:
        print "not have"
        loanInfo={}
        loanInfo['loanCode']=row['loanCode']
        loanInfo['contactInfo']= loanInfoContacts
        db.loan_info.insert(loanInfo)
