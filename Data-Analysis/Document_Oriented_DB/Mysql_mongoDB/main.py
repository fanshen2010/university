# encoding=utf-8
## main
import pymongo
import mysql.connector
import json
import sys
import time
import financier
import investor
import staff
import myfun


def loginByUserName(name,type):
    if(type=='investor'):
        query = "select * from web_user where login='"+name+"'"
    elif(type=='financier'):
        query = "select * from web_user where login='"+name+"'"
    cursor.execute(query)
    user = cursor.fetchone()
    if user:
        return user['ID']
    else:
        exit()

        
def investor_login(name):
    id = loginByUserName(name,'investor')
    while(1):
        print("1:UserInfo");
        print("2:LoanInfo");
        print("3:LoanDetailInfo");
        print("4:InvestApply");
        print("e:logout");
        try:
            investor_input = raw_input()
        except EOFError:
            break
        if(investor_input=='1'):
            investor.findInvestInfoByUserId(id,cursor,db)
        elif(investor_input=='2'):
            staff.findLoanInfoByStatus('04',cursor,db)
        elif(investor_input=='3'):
            staff.findLoanDetailInfo(cursor,db)
        elif(investor_input=='4'):
            investor.doInvestApplyByUserId(id,cursor,db,cnx)
        elif(investor_input=='e'):
            break
    return

def financier_login(name):
    id = loginByUserName(name,'financier')
    while(1):
        print("1:UserInfo");
        print("2:LoanApply");
        print("e:logout");
        try:
            financier_input = raw_input()
        except EOFError:
            break
        if(financier_input=='1'):
            financier.findLoanInfoByUserId(id,cursor,db)
        elif(financier_input=='2'):
            financier.doLoanApplyByUserId(id,cursor,db,cnx)
        elif(financier_input=='e'):
            break
    return

def staff_login():
    while(1):
        print("1:staffInfo");
        print("2:LoanInfo");
        print("3:investInfo");
        print("4:paymentInfo");
        print("5:loanEffect");
        print("6:loanRepay");
        print("e:logout");
        try:
            staff_input = raw_input()
        except EOFError:
            break
        if(staff_input=='1'):
            staff.findStaffInfo(cursor,db)
        elif(staff_input=='2'):
            staff.findLoanInfo(cursor,db)
        elif(staff_input=='3'):
            staff.findInvestInfo(cursor,db)
        elif(staff_input=='4'):
            staff.findPaymentInfo(cursor,db)
        elif(staff_input=='5'):
            staff.doLoanEffect(cursor,db,cnx)
        elif(staff_input=='6'):
            staff.doLoanRepay(cursor,db,cnx)
        elif(staff_input=='e'):
            break
    return


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

while(1):
    print("1:investLogin");
    print("2:loanLogin");
    print("3:staffLogin");
    print("e:exit");
    
    try:
        login_input = raw_input()
    except EOFError:
        break

    if (login_input=='1'):
        name = raw_input()
        investor_login(name)

    elif(login_input=='2'):
        name = raw_input()
        financier_login(name)
    
    elif(login_input=='3'):
        staff_login()

    elif(login_input=='e'):
        ## mysql
        cursor.close()
        cnx.close()
        exit();
    
