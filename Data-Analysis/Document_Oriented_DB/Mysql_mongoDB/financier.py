# encoding=utf-8
import json
import sys
import time
import investor
import staff
import myfun

def findLoanInfoByUserId(id,cursor,db):
    query = "select * from cicc_account where user_id='"+id+"'"
    cursor.execute(query)
    for row in cursor:
        print "userName: ", row['USER_NAME']
        print "balance: ", row['BALANCE']

    query = "select * from loan where apply_user_id='"+id+"'"
    cursor.execute(query)
    print "MyLoan:"
    num=1
    for row in cursor:
        print "{0:<4} name: {1:<20} amount: {2:<10} status: {3:<4}".format(num, row['LOAN_NAME'].encode('gbk'),row['LOAN_AMOUNT'],row['LOAN_STATUS'])
        num=num+1


def doLoanApplyByUserId(id,cursor,db,cnx):
    sys.stdout.write("input loanName:")
    loanName = raw_input()
    sys.stdout.write("input loanAmount:")
    loanAmount = raw_input()
    
    loanId = str(int(time.time()*10000))
    loanCode = loanId
    loan = myfun.getLoanByUserId(id,cursor,db)
    loanInfo = db.loan_info.find_one({'loanCode':loan['LOAN_CODE']})

    #loan
    loanInfo['loanCode']=loanCode
    del loanInfo['_id']
    db.loan_info.insert(loanInfo)
    
    query = "insert into loan (ID,LOAN_CODE,LOAN_NAME,LOAN_AMOUNT,LOAN_STATUS,APPLY_USER_ID) value ('"+loanId+"','"+loanCode+"','"+loanName+"','"+loanAmount+"','04','"+id+"')"
    cursor.execute(query)
    cnx.commit()
    print("success")