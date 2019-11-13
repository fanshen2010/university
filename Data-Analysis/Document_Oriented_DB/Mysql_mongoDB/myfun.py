# encoding=utf-8
import json
import sys
import time
import financier
import investor
import staff

def getLoanByLoanCode(loanCode,cursor,db):
    query = "select * from loan where loan_code='"+loanCode+"'"
    cursor.execute(query)
    loan= cursor.fetchone()
    return loan

def getLoanByUserId(userId,cursor,db):
    query = "select * from loan where apply_user_id='"+userId+"'"
    cursor.execute(query)
    loan =""
    for row in cursor:
	loan=row
    return loan

def insertPaymentLog(userId,loanName,amount,loanJson,investJson,cursor,db):
    paymentId = str(int(time.time()*10000))
    query = "insert into payment_log (ID,USER_LOGIN,PROJECT_NAME,PAYMENT_AMOUNT,LOAN_JSON,INVEST_JSON) value ('"+paymentId+"','"+userId+"','"+loanName+"','"+amount+"','"+loanJson+"','"+investJson+"')"
    cursor.execute(query)

def updatePaymentAccount(userId,amount,oper,cursor,db):
    query = "select * from cicc_account where user_id='"+userId+"'"
    cursor.execute(query)
    ciccAcount= cursor.fetchone()
    balance = float(ciccAcount['BALANCE'])
    amount = float(amount)

    if oper=='+':
	balance = balance + amount
    elif oper=='-':
	balance = balance - amount
    balance = str(balance)
    query = "update cicc_account set BALANCE= '"+balance+"' where user_id = '"+userId+"'"       
    cursor.execute(query)
