import pandas
import json
import re
import sys
import re
import pickle


enterinput = raw_input()

output = []
metadata = enterinput

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

             #for item in reversed(output):
                 #print item

                  else:
                      if b.get(metadata) == 0:
                          metadata = b.get(metadata)
                          output.append(metadata)
                      else:                         #the metadata has the same value
                           metadata = metadata[:-3]
                           output.append(metadata)
                      #else:
                          #break

             for item in reversed(output):  #export the output reversely
                 print item







