# encoding=utf-8
import json
import sys
import time
import financier
import staff
import myfun

def findInvestInfoByUserId(id,cursor,db):
    query = "select * from cicc_account where user_id='"+id+"'"
    cursor.execute(query)
    for row in cursor:
        print "userName: ", row['USER_NAME']
        print "balance: ", row['BALANCE']

    query = "select * from invest where user_id='"+id+"'"
    cursor.execute(query)
    print "MyInvest:"
    num=1
    for row in cursor:
        print "{0:<4} investCode: {1:<10} amount: {2:<10} status: {3:<4}".format(num, row['INVEST_CODE'].encode('gbk'),row['INVESTMENT_AMOUNT'],row['STATUS'])
        num=num+1

def doInvestApplyByUserId(id,cursor,db,cnx):
    sys.stdout.write("input loanCode:")
    loanCode = raw_input()
    sys.stdout.write("input investAmount:")
    investAmount = raw_input()

    loan = myfun.getLoanByLoanCode(loanCode,cursor,db)
    investId = str(int(time.time()*10000))
    investCode = investId
    loanName = loan['LOAN_NAME']
    loanJson = {"loanCode":loanCode,"loanName":loanName}
    investJson = {"investCode":investCode,"investAmount":investAmount}
    
    #invest
    query = "insert into invest (ID,INVEST_CODE,LOAN_CODE,INVESTMENT_AMOUNT,STATUS,USER_ID) value ('"+investId+"','"+investCode+"','"+loanCode+"','"+investAmount+"','02','"+id+"')"
    cursor.execute(query)

    #paymmentLog
    myfun.insertPaymentLog(id,loanName,investAmount,json.dumps(loanJson),json.dumps(investJson),cursor,db)
    #account
    myfun.updatePaymentAccount(id,investAmount,"-",cursor,db)
    
    cnx.commit()
    print("success")