import pandas
import json
import re
import pickle
import sys
sys.setrecursionlimit(100) # 10000 is an example, try with different values


from collections import OrderedDict

global generic
generic = {}
global Form
Form = {}


'''
 to build a dict generic storing the metadatas have the same name
'''
def Generic(key):
    genericValue = []
    if generic.has_key(key) == 0:
        genericValue.append(key)
        value = key + "1"
        genericValue.append(value)
        generic[key] = genericValue
    else:
        genericValue = generic.get(key)
        count = len(genericValue)
        value = key +str(count)
        genericValue.append(value)
        generic[key] = genericValue

'''
 to check whether the value of a metadata is a dict or a list
'''
def nestedCheck(v):
    if isinstance(v,dict):
        return 1
    elif isinstance(v,list):
        return 2
    else:
        return 0

'''
 to build a dict Form storing the meta and its value
'''
def setHashmap(son,parent):
    if Form.has_key(son) == 0:
        Form[son] = parent

    else:
        Generic(son)
        #print generic.get(son)
        meta = generic.get(son)
        #print meta
        son = meta[(len(meta)-1)]
        #print son
        Form[son] =parent


'''
 to build two stacks,stack_k and stack_v,storing metadata and its value,for DFS.
'''
def set_stack(parentx,valuex,stack_k,stack_v):
    for keyx in valuex:
        setHashmap(keyx,parentx)
        stack_k.append(keyx)
        stack_v.append(valuex.get(keyx))



with open('1.json','r') as data_file:
    stack_v = []
    stack_k = []
    data = json.load(data_file)
    items = data[0]
    for key in items:
        setHashmap(key,0)
        stack_k.append(key)
        stack_v.append(items.get(key))
        while(len(stack_k) != 0):
            parent = stack_k.pop()
            value = stack_v.pop()
            if nestedCheck(value) == 1:
                set_stack(parent,value,stack_k,stack_v)
            elif nestedCheck(value) == 2:
                for i in range(len(value)):
                    valueT = value[i]
                    if nestedCheck(valueT) == 1:
                        parent1 = str(parent) + str([i])
                        #key2 = list(Form.keys())[list(Form.values()).index(parent)]
                        #Form[key2] = parent1
                        setHashmap(parent1,parent)
                        set_stack(parent1,valueT,stack_k,stack_v)



    '''
    export Form as filename.pickle,
    export generic as filename2.pickle
    '''
    with open('filename.pickle', 'wb') as handle:
        pickle.dump(Form, handle,pickle.HIGHEST_PROTOCOL)
    with open('filename2.pickle', 'wb') as f:
        pickle.dump(generic, f,pickle.HIGHEST_PROTOCOL)
    #with open('filename2.pickle', 'rb') as f:
        #b = pickle.load(f)
        #print b
    #with open('filename.pickle', 'rb') as handle:
        #b = pickle.load(handle)
        #print b







