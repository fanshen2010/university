import pandas
import json, ast
import re
import sys
import re
import pickle
import pymongo
from bson.json_util import dumps
from pymongo import MongoClient
client = MongoClient() #connect mongodb
db = client.test
post = db.test


enterinput = raw_input()
enterinput = eval(enterinput)
output = []
metadata = enterinput[0]
metadataValue = enterinput[1]

with open('filename.pickle', 'rb') as handle:
    b = pickle.load(handle)   #import filename.pickle as b,referencing to Form
    #print b
    with open('filename2.pickle', 'rb') as f:
        d = pickle.load(f)    #import filename2.pickle as b,referencing to generic



        if b.has_key(metadata) == 0:
             print "error:this is not such key."
        else:
             output.append(metadata)
             #print output

             while metadata != 0: # to check the metadata reach to outer
                  if b.has_key(b.get(metadata)) == 1:
                      metadata = b.get(metadata)
                      output.append(metadata)

                  else:
                      if b.get(metadata) == 0:
                          metadata = b.get(metadata)
                          output.append(metadata)
                      else:                         #the metadata has the same value
                           metadata = metadata[:-3]
                           output.append(metadata)


             ##MongoDB export
             length = len(output)
             output = output[::-1]
             Str = ""
             for items in output:
                 Str = Str + str(items)
             m =re.search(r"\[|\]", Str) #judge whether output include array



             string = ""
             i = 1
             if  m == None:  #the normal condition without nest
                 while(i != length): #concatenate the string
                      item = output[i]
                      string =string + str(item) + "."
                      i = i+1
                 normal = string[:-1]
                 value = post.find_one({normal : metadataValue})
             else: # the condition with nest
                 while(i != (length-2)): #concatenate the string
                      item = output[i]
                      string =string + str(item) + "."
                      i = i+1
                 nest = string[:-1]
                 value = post.find_one({nest : {"$elemMatch" : {output[-1] : metadataValue}}})

             print dumps(value)  #convert bson to json






