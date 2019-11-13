import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


url = 'E:/657A_workspace/project/train_loanpredict.csv'
df = pd.read_csv(url)
# the front 10 item data
data = df.head(10)
print data

#count, mean, std, min, quartiles and max
data = df.describe()
print data

data = df['Property_Area'].value_counts()
data.plot(kind='bar')

f = plt.figure()
df['ApplicantIncome'].hist(bins=50)
f.show()

f = plt.figure()
df.boxplot(column='ApplicantIncome')
f.show()

df.boxplot(column='ApplicantIncome', by = 'Education')
df.boxplot(column='ApplicantIncome', by = 'Gender')

f = plt.figure()
df['LoanAmount'].hist(bins=50)
f.show()

f = plt.figure()
df.boxplot(column='LoanAmount')
f.show()

# show the Credit history and probility of getting loan in table type
temp1 = df['Credit_History'].value_counts(ascending=True)
temp2 = df.pivot_table(values='Loan_Status',index=['Credit_History'],aggfunc=lambda x: x.map({'Y':1,'N':0}).mean())
print 'Frequency Table for Credit History:'
print temp1

print '\nProbility of getting loan for each Credit History class:'
print temp2

# show the Credit history and probility of getting loan in graph type
fig = plt.figure(figsize=(8,4))
ax1 = fig.add_subplot(121)
ax1.set_xlabel('Credit_History')
ax1.set_ylabel('Count of Applicants')
ax1.set_title("Applicants by Credit_History")
temp1.plot(kind='bar')

ax2 = fig.add_subplot(122)
temp2.plot(kind = 'bar')
ax2.set_xlabel('Credit_History')
ax2.set_ylabel('Probability of getting loan')
ax2.set_title("Probability of getting loan by credit history")

temp3 = pd.crosstab(df['Credit_History'], df['Loan_Status'])
temp3.plot(kind='bar', stacked=True, color=['red','blue'], grid=False)

temp4 = pd.crosstab([df['Credit_History'], df['Gender']], df['Loan_Status'] )
temp4.plot(kind='bar', stacked=True, color=['red','blue'], grid=False )

temp5 = pd.crosstab([df['Credit_History'], df['Married']], df['Loan_Status'] )
temp5.plot(kind='bar', stacked=True, color=['red','blue'], grid=False )

temp6 = pd.crosstab([df['Credit_History'], df['Self_Employed']], df['Loan_Status'] )
temp6.plot(kind='bar', stacked=True, color=['red','blue'], grid=False )

temp7 = pd.crosstab([df['Credit_History'], df['Dependents']], df['Loan_Status'] )
temp7.plot(kind='bar', stacked=True, color=['red','blue'], grid=False )



# check misssing values
data = df.apply(lambda x: sum(x.isnull()), axis=0)
print data

df.boxplot(column='LoanAmount', by=['Education','Self_Employed'])

data = df['Self_Employed'].value_counts()
print data
df['Self_Employed'].fillna('No',inplace=True)
data = df['Credit_History'].value_counts()
print data
df['Credit_History'].fillna(1,inplace=True)
data = df['Gender'].value_counts()
print data
df['Gender'].fillna('Male',inplace=True)
data = df['Dependents'].value_counts()
print data
df['Dependents'].fillna('0',inplace=True)
data = df['Married'].value_counts()
print data
df['Married'].fillna('Yes',inplace=True)

# median values for all the groups of Self_Employed and Education
table = df.pivot_table(values='LoanAmount', index='Self_Employed' ,columns='Education', aggfunc=np.median)
# function to return median values for each group
def fage(x):
 return table.loc[x['Self_Employed'],x['Education']]

df['LoanAmount'].fillna(df[df['LoanAmount'].isnull()].apply(fage, axis=1), inplace=True)
df['LoanAmount_log'] = np.log(df['LoanAmount'])
f = plt.figure()
df['LoanAmount_log'].hist(bins=50)
f.show()

df['TotalIncome'] = df['ApplicantIncome'] + df['CoapplicantIncome']
df['TotalIncome_log'] = np.log(df['TotalIncome'])
f = plt.figure()
df['TotalIncome_log'].hist(bins=50)
f.show()

df['Loan_Amount_Term'].fillna(df['Loan_Amount_Term'].median(),inplace=True)
df['Loan_Amount_Term'].replace(0,df['Loan_Amount_Term'].median())

#after fill the missing values
data = df.apply(lambda x: sum(x.isnull()), axis=0)
print data

#convert variables into numeric before building model
from sklearn.preprocessing import LabelEncoder
var_mod = ['Gender','Married','Dependents','Education','Self_Employed','Property_Area','Loan_Status']
le = LabelEncoder()
for i in var_mod:
    df[i] = le.fit_transform(df[i])
df.dtypes

#after being numberic
data = df.head(10)
print data



from sklearn.linear_model import LogisticRegression
from sklearn.cross_validation import KFold
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier, export_graphviz
from sklearn import metrics


def classification_model(model, data, predictors, outcome):
    model.fit(data[predictors], data[outcome])
    predictions = model.predict(data[predictors])
    accuracy = metrics.accuracy_score(predictions, data[outcome])
    print "Accuracy : %s" % "{0:.3%}".format(accuracy)
    kf = KFold(data.shape[0], n_folds=5)
    error = []
    for train, test in kf:
        train_predictors = (data[predictors].iloc[train, :])
        train_target = data[outcome].iloc[train]
        model.fit(train_predictors, train_target)
        error.append(model.score(data[predictors].iloc[test, :], data[outcome].iloc[test]))

    print "Cross-Validation Score : %s" % "{0:.3%}".format(np.mean(error))
    model.fit(data[predictors], data[outcome])


#LogisticRegression
outcome_var = 'Loan_Status'
model = LogisticRegression()
predictor_var = ['Credit_History']
classification_model(model, df,predictor_var,outcome_var)

predictor_var = ['Credit_History','Education','Married','Self_Employed','Property_Area']
classification_model(model, df,predictor_var,outcome_var)

predictor_var = ['Credit_History','Gender','Married','Education','Dependents']
classification_model(model, df,predictor_var,outcome_var)


#DecisionTreeClassifier
model = DecisionTreeClassifier()
predictor_var = ['Credit_History','Gender','Married','Education','Dependents']
classification_model(model, df,predictor_var,outcome_var)

predictor_var = ['Credit_History','Loan_Amount_Term','LoanAmount_log']
classification_model(model, df,predictor_var,outcome_var)


#RandomForestClassifier
model = RandomForestClassifier(n_estimators=100)
predictor_var = ['Gender', 'Married', 'Dependents', 'Education',
       'Self_Employed', 'Loan_Amount_Term', 'Credit_History', 'Property_Area',
        'LoanAmount_log','TotalIncome_log']
classification_model(model, df,predictor_var,outcome_var)

#Create a series with feature importances, using top 5 variables for creating a model
featimp = pd.Series(model.feature_importances_, index=predictor_var).sort_values(ascending=False)
print featimp


#analyse the parameters of random forest to avoid overfitting
def overfitting(model, data, predictors, outcome):
    model.fit(data[predictors], data[outcome])
    predictions = model.predict(data[predictors])
    accuracy = metrics.accuracy_score(predictions, data[outcome])
    kf = KFold(data.shape[0], n_folds=5)
    error = []
    for train, test in kf:
        train_predictors = (data[predictors].iloc[train, :])
        train_target = data[outcome].iloc[train]
        model.fit(train_predictors, train_target)
        error.append(model.score(data[predictors].iloc[test, :], data[outcome].iloc[test]))
    return (round(accuracy,3),round(np.mean(error),3))

accuracys=[];
scores=[];
X=[1,5,10,15,20,25,30,40,60,80,100]
for i in X:
	model = RandomForestClassifier(n_estimators=i)
	predictor_var = ['TotalIncome_log','LoanAmount_log','Credit_History','Dependents','Property_Area']
	result = overfitting(model, df,predictor_var,outcome_var)
	accuracys.append(result[0]);
	scores.append(result[1]);
f = plt.figure()
plt.plot(X, accuracys, '-o', label='accuracy')
plt.plot(X, scores, '-o', label='cross validation')
plt.xlabel('Tree number')
plt.ylabel('Accuracy')
plt.legend()
f.show()

accuracys=[];
scores=[];
X=[10,15,20,25,30,40,60,80]
for i in X:
	model = RandomForestClassifier(min_samples_split=i)
	predictor_var = ['TotalIncome_log','LoanAmount_log','Credit_History','Dependents','Property_Area']
	result = overfitting(model, df,predictor_var,outcome_var)
	accuracys.append(result[0]);
	scores.append(result[1]);
f = plt.figure()
plt.plot(X, accuracys, '-o', label='accuracy')
plt.plot(X, scores, '-o', label='cross validation')
plt.xlabel('min_samples_split')
plt.ylabel('Accuracy')
plt.legend()
f.show()

accuracys=[];
scores=[];
X=[1,3,5,7,9,11,13,15,20,30,40,60,80]
for i in X:
	model = RandomForestClassifier(max_depth=i)
	predictor_var = ['TotalIncome_log','LoanAmount_log','Credit_History','Dependents','Property_Area']
	result = overfitting(model, df,predictor_var,outcome_var)
	accuracys.append(result[0]);
	scores.append(result[1]);
f = plt.figure()
plt.plot(X, accuracys, '-o', label='accuracy')
plt.plot(X, scores, '-o', label='cross validation')
plt.xlabel('max_depth')
plt.ylabel('Accuracy')
plt.legend()
f.show()

accuracys=[];
scores=[];
X=[1,2,3,4,5]
for i in X:
	model = RandomForestClassifier(max_features=i)
	predictor_var = ['TotalIncome_log','LoanAmount_log','Credit_History','Dependents','Property_Area']
	result = overfitting(model, df,predictor_var,outcome_var)
	accuracys.append(result[0]);
	scores.append(result[1]);
f = plt.figure()
plt.plot(X, accuracys, '-o', label='accuracy')
plt.plot(X, scores, '-o', label='cross validation')
plt.xlabel('max_features')
plt.ylabel('Accuracy')
plt.legend()
f.show()

#model
model = RandomForestClassifier(n_estimators=25, min_samples_split=20, max_depth=7)
predictor_var = ['TotalIncome_log','LoanAmount_log','Credit_History','Dependents','Property_Area']
classification_model(model, df,predictor_var,outcome_var)

#given an applicant's data, get the prediction of loan
LP001003=['6091.0','4.852030','1.0','1','0']#right predicted  Y-Y
LP001005=['3000.0','4.189655','1.0','0','2']#wrong predicted  N-Y
LP0010014=['5540.0','5.062595','0.0','3','1']#right predicted N-N
predictions = model.predict(LP001003)
print predictions

plt.show()