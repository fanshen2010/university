#!/usr/bin/python

import re
import sys

def show(output):
##    V=output['V']
##    E=output['E']
##    
##    print 'V={'
##    for V_key in V:
##        print "%s:(%.2f,%.2f)" %(V_key,V[V_key][0],V[V_key][1])
##    print '}'
##    print ' '
##    print 'E={'
##    for i in range(len(E)):
##        if(i<len(E)-1):
##            print "<"+str(E[i][0])+","+str(E[i][1])+">,"
##        else:
##            print "<"+str(E[i][0])+","+str(E[i][1])+">"
##    print '}'

    V=output['V']
    E=output['E']

    V_copy = V.copy()
    E_copy = []
    count =0
    for V_key in V:
        V_copy[V_key]=count;
        count=count+1

    for E_elem in E:
        E_copy.append((V_copy[E_elem[0]],V_copy[E_elem[1]]))
    
    print ('V '+str(len(V)))
    sys.stdout.write("E {")
    for i in range(len(E_copy)):
        if(i<len(E_copy)-1):
            sys.stdout.write("<"+str(E_copy[i][0])+","+str(E_copy[i][1])+">,")
        else:
            sys.stdout.write("<"+str(E_copy[i][0])+","+str(E_copy[i][1])+">")
    print ("}")
##
##    sys.stderr.write('V '+str(len(V))+"\n")
##    sys.stderr.write("E {")
##    for i in range(len(E_copy)):
##        if(i<len(E_copy)-1):
##            sys.stderr.write("<"+str(E_copy[i][0])+","+str(E_copy[i][1])+">,")
##        else:
##            sys.stderr.write("<"+str(E_copy[i][0])+","+str(E_copy[i][1])+">")
##    sys.stderr.write("}\n")

    sys.stdout.flush()
    

def intersection(p1,p2,p3,p4):
    tl=[p1,p2,p3,p4]
    ts=set(tl)
    if (len(ts)==2):
        return "same"
    elif (len(ts)==3):
        crossing=(0.0,0.0)
        for elem in tl:
            if(tl.count(elem)==2):
                crossing=elem
        ts.remove(crossing)
        vl=list(ts)
        
        ping = 0
        if(p1[0] == p2[0] == p3[0] == p4[0]):
            ping = 1
        elif(p1[0] != p2[0] and p3[0] != p4[0]):
            a = (p1[1] - p2[1])/(p1[0] - p2[0])
            c = (p3[1] - p4[1])/(p3[0] - p4[0])
            if(a==c):
                ping = 1

        if(ping==1):
            if((crossing[0]>vl[0][1] and crossing[0]>vl[1][1])
               or (crossing[0]<vl[0][1] and crossing[0]<vl[1][1])):
                return "same"
        else:
            returnX = crossing[0]
            returnY = crossing[1]
            return (returnX,returnY)
            
    else:
        if(p1[0] == p2[0] and p3[0] == p4[0] and p2[0]!=p3[0]):
            return 0
        elif(p1[0] == p2[0] == p3[0] == p4[0]):
            if(p1[1]<p3[1]<p2[1] or p2[1]<p3[1]<p1[1] or p1[1]<p4[1]<p2[1] or p2[1]<p4[1]<p1[1]):
                return "same"
            else:
                return 0
        elif(p1[0] == p2[0]):
            returnX = p1[0]
            c = (p3[1] - p4[1])/(p3[0] - p4[0])
            d = p3[1] - (p3[1] - p4[1])/(p3[0] - p4[0]) * p3[0]
            returnY = c * returnX + d             
        elif(p3[0] == p4[0]):        
            returnX = p3[0]
            a = (p1[1] - p2[1])/(p1[0] - p2[0])
            b = p1[1] - (p1[1] - p2[1])/(p1[0] - p2[0]) * p1[0]
            returnY = a * returnX + b
        else:
            a = (p1[1] - p2[1])/(p1[0] - p2[0])
            b = p1[1] - (p1[1] - p2[1])/(p1[0] - p2[0]) * p1[0]
            c = (p3[1] - p4[1])/(p3[0] - p4[0])
            d = p3[1] - (p3[1] - p4[1])/(p3[0] - p4[0]) * p3[0]
            if(a - c == 0):
                if((p3[1]==a*p3[0]+b and p1[1]<p3[1]<p2[1])
                    or (p3[1]==a*p3[0]+b and p2[1]<p3[1]<p1[1])
                    or (p3[1]==a*p3[0]+b and p1[1]<p4[1]<p2[1])
                    or (p3[1]==a*p3[0]+b and p2[1]<p4[1]<p1[1])):
                    return "same"
                else:
                    return 0
            else:
                returnX = (d - b)/(a - c)
                returnY = (a * d - c * b)/(a - c)
                

        if( (returnX > p1[0] and returnX > p2[0])
            or (returnX < p1[0] and returnX < p2[0])
            or (returnX > p3[0] and returnX > p4[0])
            or (returnX < p3[0] and returnX < p4[0])
            or (returnY > p1[1] and returnY > p2[1])
            or (returnY < p1[1] and returnY < p2[1])
            or (returnY > p3[1] and returnY > p4[1])
            or (returnY < p3[1] and returnY < p4[1])):
            return 0
        else:
            return (returnX,returnY)


def check_street(output,sname,sline):
    S=output['S']
    s_input=[]
    for i in range(0,len(sline)-1,2):
        s_input.append((float(sline[i]),float(sline[i+1])))
        
    for i in range(len(s_input)-1):
        if(s_input[i]==s_input[i+1]):
            return "same_next_point"

    for i in range(len(s_input)-1):
        j=i+1
        while (j<(len(s_input)-1)):
            x=intersection(s_input[i],s_input[i+1],s_input[j],s_input[j+1])
            if(x=="same"):
                return "cover_itself"
##            elif(x!=0 and x not in s_input):
##                return "cover_itself"
            j=j+1
            
    for i in range(len(s_input)-1):
        for S_key in S:
            if(S_key != sname):
                for j in range(len(S[S_key])-1):
                    x=intersection(s_input[i],s_input[i+1],S[S_key][j],S[S_key][j+1])
                    if(x=="same"):
                        return "cover_other"

def sadd(output,sname,sline):
    s=output['S']
    s[sname]=[]
    for i in range(0,len(sline)-1,2):
        s[sname].append((float(sline[i]),float(sline[i+1])))

    output['S']=s

    return output
                    

def add(output):

##    s=output['S']
##    V=output['V']
##    E=output['E']
    S=output['S']
    s={}
    for S_key in S:
        s[S_key]=S[S_key][:]
    V={}
    E=[]

##    s[sname]=[]
##    for i in range(0,len(sline)-1,2):
##        s[sname].append((float(sline[i]),float(sline[i+1])))

    E_crossing_set=set()
##    for E_elem in E:
##        E_crossing_set.add((V[E_elem[0]],V[E_elem[1]]))
    
    V_near_crossing_set=set()
    comp=[]

    for s_key1 in s:
        for s_key2 in s:
            if(s_key1 != s_key2 and  set([s_key1,s_key2]) not in comp ):
                comp.append(set([s_key1,s_key2]))
                i=0
                j=0
                while(i<len(s[s_key1])-1):
                    while(j<len(s[s_key2])-1):
                        V_intersection = intersection(s[s_key1][i],s[s_key1][i+1],s[s_key2][j],s[s_key2][j+1])
                        if(V_intersection != 0 and V_intersection!="same"):
                            V_near_crossing_set.update([s[s_key1][i],s[s_key1][i+1],s[s_key2][j],s[s_key2][j+1],V_intersection])
                            if(V_intersection not in s[s_key1] and V_intersection not in s[s_key2]):
                                E_crossing_set.update([(s[s_key1][i],V_intersection),(V_intersection,s[s_key1][i+1]),(s[s_key2][j],V_intersection),(V_intersection,s[s_key2][j+1])])
                                if((s[s_key1][i],s[s_key1][i+1]) in E_crossing_set):
                                    E_crossing_set.remove((s[s_key1][i],s[s_key1][i+1]))
                                if((s[s_key2][j],s[s_key2][j+1]) in E_crossing_set):
                                    E_crossing_set.remove((s[s_key2][j],s[s_key2][j+1]))
                                s[s_key1].insert(i+1,V_intersection)
                                s[s_key2].insert(j+1,V_intersection)
                                j=j+1
                            elif(V_intersection in s[s_key1] and V_intersection not in s[s_key2]):
                                E_crossing_set.update([(s[s_key2][j],V_intersection),(V_intersection,s[s_key2][j+1])])
                                if((s[s_key2][j],s[s_key2][j+1]) in E_crossing_set):
                                    E_crossing_set.remove((s[s_key2][j],s[s_key2][j+1]))
                                s[s_key2].insert(j+1,V_intersection)
                                j=j+1
                            elif(V_intersection in s[s_key2] and V_intersection not in s[s_key1]):
                                E_crossing_set.update([(s[s_key1][i],V_intersection),(V_intersection,s[s_key1][i+1])])
                                if((s[s_key1][i],s[s_key1][i+1]) in E_crossing_set):
                                    E_crossing_set.remove((s[s_key1][i],s[s_key1][i+1]))
                                s[s_key1].insert(i+1,V_intersection)
                                j=j+1
                            elif(V_intersection in s[s_key1] and V_intersection in s[s_key2]):                                
                                E_crossing_set.update([(s[s_key1][i],s[s_key1][i+1]),(s[s_key2][j],s[s_key2][j+1])])
                        j=j+1
                    j=0
                    i=i+1

##    if(len(V)==0):
##        V_new_list = list(V_near_crossing_set)
##        count=0
##    else:
##        V_values_set = set(V.values())
##        V_new_set = V_near_crossing_set-V_values_set
##        V_new_list=list(V_new_set)
##        V_key_list=V.keys()
##        count=V_key_list[len(V_key_list)-1]
    V_new_list = list(V_near_crossing_set)
    count=0    
    for V_elem in V_new_list:
        count=count+1
        V[count]=V_elem


    E_list = list(E_crossing_set)
    E=[]
    for E_elem in E_list:
        for V_key in V:
            if(E_elem[0]==V[V_key]):
                p1=V_key
            if(E_elem[1]==V[V_key]):
                p2=V_key
        E.append((p1,p2))
    
##    output['S']=s
    output['V']=V
    output['E']=E

    return output


def delete(output,sname):
    S=output['S']
##    V=output['V']
##    E=output['E']
##    s=S[sname]
##
##    V_id_du={}
##    V_id_1=[]
##    V_id_3_4=[]
##    V_id_2_m4=[]
##    s_V_id=[]
##    V_del_set=set()
##    
##    for E_elem in E:
##        if(E_elem[0] not in V_id_du.keys()):
##            V_id_du[E_elem[0]]=1
##        else:
##            V_id_du[E_elem[0]]=V_id_du[E_elem[0]]+1
##        if(E_elem[1] not in V_id_du.keys()):
##            V_id_du[E_elem[1]]=1
##        else:
##            V_id_du[E_elem[1]]=V_id_du[E_elem[1]]+1
##
##    for V_id_du_key in V_id_du:
##        if(V_id_du[V_id_du_key]==1):
##            V_id_1.append(V_id_du_key)
##        elif(3<=V_id_du[V_id_du_key]<=4):
##            V_id_3_4.append(V_id_du_key)
##        else:
##            V_id_2_m4.append(V_id_du_key)
##
##    for i in range(len(s)):
##        for V_key in V:
##            if(s[i]==V[V_key]):
##                s_V_id.append(V_key)
##
##    for i in range(len(s_V_id)):
##        if(i!=len(s_V_id)-1):
##            if((s_V_id[i],s_V_id[i+1]) in E):
##                E.remove((s_V_id[i],s_V_id[i+1]))
##            elif((s_V_id[i+1],s_V_id[i]) in E):
##                E.remove((s_V_id[i+1],s_V_id[i]))
##        if(s_V_id[i] in V_id_1):
##            del V[s_V_id[i]]
##        elif(s_V_id[i] in V_id_3_4):                        
##            merge={0:0,1:0}
##            E_output = E[:]
##            for E_elem in E:
##                if(s_V_id[i] == E_elem[0]):
##                    merge[1]=E_elem[1]
##                elif(s_V_id[i] == E_elem[1]):
##                    merge[0]=E_elem[0]
##                if((s_V_id[i],merge[1]) in E_output):
##                    E_output.remove((s_V_id[i],merge[1]))
##                elif((merge[0],s_V_id[i]) in E_output):
##                    E_output.remove((merge[0],s_V_id[i]))
##            E=E_output[:]
##            E.append((merge[0],merge[1]))
##            V_del_set.add(V[s_V_id[i]])
##            del V[s_V_id[i]]
##    
##    E_output = E[:]
##    for elem in E:
##        if(elem[0] in V_id_1 and elem[1] in V_id_1):
##            E_output.remove(elem);
##            del V[elem[0]]
##            del V[elem[1]]
##    E = E_output[:]
        
    del S[sname]

##    for S_key in S:
##        for elem in S[S_key]:
##            if (elem in V_del_set):
##                S[S_key].remove(elem)

    output['S']=S
##    output['V']=V
##    output['E']=E

    return output


output={'V':{},'E':[],'S':{}}
while(1):
    S=output['S']
    try:
        input_x = raw_input()
    except EOFError:
        break
##    sys.stderr.write(input_x+"\n")
        
    x = re.match(r'^\s*(\w)\s*"(.*)"(.*)$', input_x)
    x_g = re.match(r'^\s*(g)\s*$', input_x)
    if x:
        cmd = x.group(1)
        scmd = re.match(r'[acrg]$',cmd)
        if not scmd:
            sys.stderr.write("Error: input does not start with 'a' or 'c' or 'r' or 'g' \n")
            continue
        
        if(cmd=='a' or cmd=='c' or cmd=='r'):
            sname = re.match(r'^\s*$',x.group(2))
            if sname:
                sys.stderr.write("Error: street name is null \n")
                continue
            if(cmd=='a' and (x.group(2) in S.keys())):
                sys.stderr.write("Error: 'a' specified for a street that already exists \n")
                continue
            if(cmd=='c' and (x.group(2) not in S.keys())):
                sys.stderr.write("Error: 'c' specified for a street that does not exist \n")
                continue
            if(cmd=='r' and (x.group(2) not in S.keys())):
                sys.stderr.write("Error: 'r' specified for a street that does not exist \n")
                continue
                        
        if(cmd=='a' or cmd=='c'):
            sline = re.match(r'(\s*\(\s*-?\d+\s*,\s*-?\d+\s*\)\s*)*$',x.group(3))
            if sline:
                p = re.compile(r'-?\d+')
                sline_list = p.findall(x.group(3))
                if(len(sline_list)==0):
                    sys.stderr.write("Error: Incomplete coordinates in a \""+x.group(2)+"\",no point \n")
                    continue
                if(len(sline_list)==2):
                    sys.stderr.write("Error: Incomplete coordinates in a \""+x.group(2)+"\",no end point \n")
                    continue
##                check = check_street(output,x.group(2),sline_list)
##                if(check=="same_next_point"):
##                    sys.stderr.write("Error: two neighboring point in \""+x.group(2)+"\" are same ,can not be made into a segment \n")
##                    continue
##                if(check=="cover_itself"):
##                    sys.stderr.write("Error: the part of \""+x.group(2)+"\" covers another part of itself \n")
##                    continue
##                if(check=="cover_other"):
##                    sys.stderr.write("Error: the part of \""+x.group(2)+"\" covers another part of other streets \n")
##                    continue
            else:
                sys.stderr.write("Error: Incomplete coordinates in a \""+x.group(2)+"\",format error \n")
                continue
            
        if(cmd=='r'):
            sline = re.match(r'^\s*$',x.group(3))
            if not sline:
                sys.stderr.write("Error: 'r' specified for a street that defines coordinates again \n")
                continue
                
        
        if(cmd=='a'):
            output=sadd(output,x.group(2),sline_list)
            output=add(output)
        elif(cmd=='c'):
            output=delete(output,x.group(2))
            output=sadd(output,x.group(2),sline_list)
            output=add(output)
        elif(cmd=='r'):
            output=delete(output,x.group(2))
            output=add(output)
    elif x_g:
        show(output)
    else:
        sys.stderr.write("Error: input is not valid \n")
