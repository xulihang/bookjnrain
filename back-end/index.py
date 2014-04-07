# -*- coding: utf-8 -*-  
import os
from bottle import route, run, template, request, static_file
import sqlite3
import json
import urllib

#登录
@route("/login",method="post")
def login():
    #global isbn
    #global lasttime
    username = request.forms.get("username")
    password = request.forms.get("password")
    isbn = request.forms.get("isbn")
    bookname = request.forms.get("bookname")
    lasttime = request.forms.get("time")
    print(username,password,isbn,lasttime)
    exist=1#默认插入
    print exist
    if username =='admin' and password =='admin':
        params = isbn
        
        #f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+str(params))
        #jresult=f.read()
        #jsonVal = json.loads(jresult)
        #bookcode=jsonVal["isbn13"]
        
        #判断是否已经存在
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        for isbncode in c.execute('select code from book'):
            if str("(u'"+isbn+"',)") == str(isbncode):
                exist=0
        print exist
        if exist==0:
            update(isbn,lasttime,bookname,username)
        else:
            insert(isbn,lasttime,bookname,username)
        

        return username+'登录成功'
    else :
        return username+'登录失败'

#用户登录页面的
@route("/index")
def index():
    
    return template("index")

#下载页面的
@route("/get")
def get():
    
    return static_file("book.db","./",download="book.db")

#下载页面2
@route("/get2")
def get2():
    
    return static_file("count.db",root='db',mimetype="*/*",download="count.db")

#下载页面2
@route("/getcomment/<isbn:path>")
def getcomment(isbn):
    
    return static_file(isbn+"-comment.db",root='db',mimetype="*/*",download=isbn+"-comment.db")



#生成xls文件并提供下载
@route("/getxls")
def getxls():
    os.system("python newxls.py")
    return static_file("test.xls","./",download="test.xls")

#手工输入
@route("/handlogin",method="post")
def reset():
    username = request.forms.get("username")
    password = request.forms.get("password")
    title = request.forms.get("title")
    price = request.forms.get("price")
    isbn = request.forms.get("isbn")
    pubdate=request.forms.get("pubdate")
    publisher=request.forms.get("publisher")
    lasttime = request.forms.get("time")
    print(username,password,isbn)
    exist=1#默认插入
    print exist
    if username =='admin' and password =='admin':
        #判断是否已经存在
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        for isbncode in c.execute('select code from book'):
            if str("(u'"+isbn+"',)") == str(isbncode):
                exist=0
        print exist
        if exist==0:
            #update()
            status="已更新原有记录。"
            conn = sqlite3.connect('book.db')
            c = conn.cursor()
            c.execute("update book set title='"+title+"' where code Like "+isbn+"")
            c.execute("update book set price='"+price+"' where code Like "+isbn+"")
            c.execute("update book set publisher='"+publisher+"' where code Like "+isbn+"")
            c.execute("update book set pubdate='"+pubdate+"' where code Like "+isbn+"")
            c.execute("update book set lasttime='"+lasttime+"' where code Like "+isbn+"")
            conn.commit()
            c.close()
            conn.close()
        else:
            #insert()
            status="已插入新的记录。"
            conn = sqlite3.connect('book.db')
            c = conn.cursor()
            c.execute("insert into book values ('"+title+"','"+price+"','"+publisher+"','"+isbn+"','"+pubdate+"','"+lasttime+"','0')")
            conn.commit()
            c.close()
            conn.close()
            
        return '登录成功：'+status
    else :
        return username+'登录失败'

#手工输入页面
@route("/hand")
def resetpage():
    return template("hand")  



#重置数据库
@route("/reset",method="post")
def reset():
    username = request.forms.get("username")
    password = request.forms.get("password")
    if username =='admin' and password =='admin':
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        c.execute("delete from book")
        conn.commit()
        c.close()
        conn.close()
        return '数据库已清空！'
    else:
        return '密码错误！'

#重置数据库登录
@route("/resetpage")
def resetpage():
    return template("reset")

#查询界面
@route("/query")
def query():
    os.system("python query.py")
    return static_file("out.html",root='static')       

@route('/static/<filepath:path>')
def server_static(filepath):
    return static_file(filepath, root='static')

#插入
def insert(isbn,lasttime,bookname,username):
    params = isbn
    f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+str(params))
    jresult=f.read()
    jsonVal = json.loads(jresult)
    bookcode=jsonVal["isbn13"]
    bookname=jsonVal["title"]
    bookprice=jsonVal["price"]
    bookpublisher=jsonVal["publisher"]
    bookpubdate=jsonVal["pubdate"]
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("insert into book values ('"+bookname+"','"+bookprice+"','"+bookpublisher+"','"+bookcode+"','"+bookpubdate+"','"+lasttime+"','0')")
    conn.commit()
    c.close()
    conn.close()
    createdb(isbn,lasttime,bookname,username)

def createdb(bookcode,lasttime,bookname,username):
    #新建立以isbn号为名的统计点赞的数据库
    if os.path.exists('db/'+bookcode+"-praise.db")==False:
        conn = sqlite3.connect('db/'+bookcode+'-praise.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (who, time, praise)")
        c.execute("insert into statics values ('admin','"+lasttime+"','0')")
        conn.commit()
        c.close()
        conn.close()
    #新建立以isbn号为名的统计评论的数据库
    if os.path.exists('db/'+bookcode+"-comment.db")==False:
        conn = sqlite3.connect('db/'+bookcode+'-comment.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (who, time, comment)")
        c.execute("insert into statics values ('admin','"+lasttime+"','0')")
        conn.commit()
        c.close()
        conn.close()
    #建立次数统计数据库,将作为前端加载对象
    if os.path.exists('db/count.db')==False:
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (isbn, praise, comment,bookname,username)")
        c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"')")
        conn.commit()
        c.close()
        conn.close()
    else:
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"')")
        conn.commit()
        c.close()
        conn.close()
#点赞
@route("/praise")
def praise():

    return template("praise")

#处理点赞
@route("/addpraise",method="post")
def addpraise():
    isbn = request.forms.get("isbn")
    username = request.forms.get("username")
    time = request.forms.get("time")
    praise = request.forms.get("praise")
    print praise,time

    #处理统计数据    
    if os.path.exists('db/count.db')==True:
        if getwhopraise(isbn,username)=="0":
            #没点过则加点赞数
            conn = sqlite3.connect('db/count.db')
            c = conn.cursor()
            c.execute("select praise from statics where isbn like "+isbn)
            num=c.fetchone()
            total=int(num[0])+1
            c.execute("update statics set praise='"+str(total)+"' where isbn Like "+isbn+"")
            conn.commit()
            c.close()
            conn.close()


        
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        if getwhopraise(isbn,username)=="0":
            #更新以isbn号为名的点赞数据库
            conn = sqlite3.connect('db/'+isbn+'-praise.db')
            c = conn.cursor()
            c.execute("insert into statics values ('"+username+"','"+time+"','"+praise+"')")
            conn.commit()
            c.close()
            conn.close()
            return "点赞成功"
        else:
            return "你点过赞了"

#获得点赞数-单次查询用
@route("/praise/<isbn:path>")
def getpraise(isbn):
    total=0
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        conn = sqlite3.connect('db/'+isbn+'-praise.db')
        c = conn.cursor()
        for praise in c.execute('select praise from statics'):
            print praise
            if praise==(u'null',):
                pass
            else:
                total=total+int(praise[0])
                print total
            #total=total+praise
        conn.commit()
        c.close()
        conn.close()
        return str(total)
    else:
        return "还没有此数据库！"

#有没有点过赞-单次查询用
@route("/whopraise/<isbn:path>/<name:path>")
def getwhopraise(isbn,name):
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        conn = sqlite3.connect('db/'+isbn+'-praise.db')
        c = conn.cursor()
        for who in c.execute('select who from statics'):
            print who
            if name==who[0]:
                return "1" #点过了
        conn.commit()
        c.close()
        conn.close()
        return "0"

#评论
@route("/comment")
def comment():

    return template("comment")

#处理评论
@route("/addcomment",method="post")
def addcomment():
    isbn = request.forms.get("isbn")
    username = request.forms.get("username")
    time = request.forms.get("time")
    comment = request.forms.get("comment")
    print comment,time

    #处理统计数据    
    if os.path.exists('db/count.db')==True:
        #加评论数
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        c.execute("select comment from statics where isbn like "+isbn)
        num=c.fetchone()
        total=int(num[0])+1
        c.execute("update statics set comment='"+str(total)+"' where isbn Like "+isbn+"")
        conn.commit()
        c.close()
        conn.close()


        
    if os.path.exists('db/'+isbn+"-comment.db")==True:
        #更新以isbn号为名的评论数据库
        conn = sqlite3.connect('db/'+isbn+'-comment.db')
        c = conn.cursor()
        c.execute("insert into statics values ('"+username+"','"+time+"','"+comment+"')")
        conn.commit()
        c.close()
        conn.close()

    return "评论成功！"



#修改
def update(isbn,lasttime,bookname, username):
    #print "Not changed"
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("update book set lasttime='"+lasttime+"' where code Like "+isbn+"")
    conn.commit()
    c.close()
    conn.close()
    createdb(isbn,lasttime,bookname, username)


def find():
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    for isbncode in c.execute('select code from book'):
        print isbncode

#@route('/hello/:name')
#def index(name='World'):
#    return '<b>Hello %s!</b>' % name


#默认端口  run(host='localhost', port=8080)
run(host='192.168.42.53', port=8080)
