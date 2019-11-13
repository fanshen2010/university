# encoding=utf-8
import json
import sys
import time
import financier
import investor
import myfun

def findStaffInfo(cursor,db):
    query = "select * from cicc_account where user_type='00'"
    cursor.execute(query)
    for row in cursor:
        print "userName: ", row['USER_NAME']
        print "balance: ", row['BALANCE']

def findLoanInfo(cursor,db):
    query = "select * from loan"
    cursor.execute(query)
    print "LoanList:"
    num=1
    for row in cursor:
        print "{0:<3} loanCode:{1:<14} name:{2:<20} amount:{3:<8} status:{4:<2}".format(num,row['LOAN_CODE'],row['LOAN_NAME'].encode('gbk'),row['LOAN_AMOUNT'],row['LOAN_STATUS'])
        num=num+1

def findLoanInfoByStatus(status,cursor,db):
    query = "select * from loan where loan_status='"+status+"'"
    cursor.execute(query)
    print "LoanList:"
    num=1
    for row in cursor:
        print "{0:<3} loanCode:{1:<14} name:{2:<20} amount:{3:<8} status:{4:<2}".format(num,row['LOAN_CODE'],row['LOAN_NAME'].encode('gbk'),row['LOAN_AMOUNT'],row['LOAN_STATUS'])
        num=num+1

def findLoanDetailInfo(cursor,db):
    sys.stdout.write("input loanCode:")
    loanCode = raw_input()
    loanInfo = db.loan_info.find_one({'loanCode':loanCode})
    print loanInfo

def findInvestInfo(cursor,db):
    query = "select * from invest"
    cursor.execute(query)
    print "InvestList:"
    num=1
    for row in cursor:
        print "{0:<4} investCode: {1:<10} amount: {2:<10} status: {3:<4}".format(num, row['INVEST_CODE'],row['INVESTMENT_AMOUNT'],row['STATUS'])
        num=num+1

def findPaymentInfo(cursor,db):
    query = "select * from payment_log"
    cursor.execute(query)
    print "paymentList:"
    num=1
    for row in cursor:
        print "{0:<4} user: {1:<20} amount: {2:<10} time: {3:<10}".format(num, row['USER_LOGIN'].encode('gbk'),row['PAYMENT_AMOUNT'],row['PAYMENT_TIME'])
        num=num+1



def doLoanEffect(cursor,db,cnx):
    sys.stdout.write("input loanCode:")
    loanCode = raw_input()

    loan = myfun.getLoanByLoanCode(loanCode,cursor,db)
    loanCode = loan['LOAN_CODE']
    loanName = loan['LOAN_NAME']
    platformFee = float(loan['LOAN_AMOUNT'])*0.1
    loanEffectAmount = float(loan['LOAN_AMOUNT'])*0.9
    loanInterest = float(loan['LOAN_AMOUNT'])*0.1
    userId = loan['APPLY_USER_ID']    
    loanJson = {"loanCode":loanCode,"loanName":loanName}
    
    #loan
    query = "update loan set AMOUNT='"+str(loanEffectAmount)+"',LOAN_INTEREST='"+str(loanInterest)+"',LOAN_STATUS='10' where loan_code = '"+loanCode+"'"
    cursor.execute(query)
    
    #invest
    query = "select * from invest where loan_code='"+loanCode+"'"
    cursor.execute(query)
    invests = cursor.fetchall()
    for row in invests:
        investCode  = row['INVEST_CODE']
        investPrincipal = float(row['INVESTMENT_AMOUNT'])
        investInterest  = float(row['INVESTMENT_AMOUNT'])*0.1
        query = "update invest set RECEIVABLE_PRINCIPAL='"+str(investPrincipal)+"',RECEIVABLE_INTEREST='"+str(investInterest)+"',STATUS='07' where invest_code = '"+investCode+"'"
        cursor.execute(query)

    #paymmentLog
    myfun.insertPaymentLog(userId,loanName,str(loanEffectAmount),json.dumps(loanJson),"",cursor,db)
    #account
    myfun.updatePaymentAccount(userId,str(loanEffectAmount),"+",cursor,db)
    myfun.updatePaymentAccount("00",str(platformFee),"+",cursor,db)
    
    cnx.commit()
    print("success")



def doLoanRepay(cursor,db,cnx):
    sys.stdout.write("input loanCode:")
    loanCode = raw_input()

    loan = myfun.getLoanByLoanCode(loanCode,cursor,db)    
    loanAmount = float(loan['LOAN_AMOUNT'])
    loanInterest = float(loan['LOAN_INTEREST'])
    loanRepayAmount = loanAmount+loanInterest
    userId = loan['APPLY_USER_ID']    
    loanCode = loan['LOAN_CODE']
    loanName = loan['LOAN_NAME']
    loanJson = {"loanCode":loanCode,"loanName":loanName}

    #loan
    query = "update loan set LOAN_STATUS='11' where loan_code = '"+loanCode+"'"
    cursor.execute(query)
    #paymmentLog
    myfun.insertPaymentLog(userId,loanName,str(loanRepayAmount),json.dumps(loanJson),"",cursor,db)
    #account    
    myfun.updatePaymentAccount(userId,str(loanRepayAmount),"-",cursor,db)
    
    #invest
    query = "select * from invest where loan_code='"+loanCode+"'"
    cursor.execute(query)
    invests = cursor.fetchall()
    for row in invests:
        investUserId  = row['USER_ID']
        investCode  = row['INVEST_CODE']
        investPrincipal = float(row['RECEIVABLE_PRINCIPAL'])
        investInterest  = float(row['RECEIVABLE_INTEREST'])
        investRepayAmount = investPrincipal+investInterest
	investJson = {"investCode":investCode,"investAmount":investPrincipal}
        query = "update invest set STATUS='09' where invest_code = '"+investCode+"'"
        cursor.execute(query)

        #paymmentLog
        myfun.insertPaymentLog(investUserId,loanName,str(investRepayAmount),json.dumps(loanJson),json.dumps(investJson),cursor,db)
        #account    
        myfun.updatePaymentAccount(investUserId,str(investRepayAmount),"+",cursor,db)

    cnx.commit()
    print("success")