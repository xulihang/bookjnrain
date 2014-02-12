# -*- coding: utf-8 -*-  
import os
from bottle import route, run, template, request, static_file
import sqlite3
import json
import urllib

#登录
@route("/login",method="post")
def login():
    global isbn
    global lasttime
    username = request.forms.get("username");
    password = request.forms.get("password");
    isbn = request.forms.get("isbn");
    lasttime = request.forms.get("time");
    print(username,password,isbn,lasttime)
    exist=1#默认插入
    print exist
    if username =='admin' and password =='admin':
        params = isbn
        f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+str(params))
        jresult=f.read()
        jsonVal = json.loads(jresult)
        bookcode=jsonVal["isbn13"]
        #判断是否已经存在
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        for isbncode in c.execute('select code from book'):
            if str("(u'"+bookcode+"',)") == str(isbncode):
                exist=0
        print exist
        if exist==0:
            update()
        else:
            insert()
        

        return username+'登录成功';
    else :
        return username+'登录失败';

#用户登录页面的
@route("/index")
def index():
    
    return template("index")

#下载页面的
@route("/get")
def get():
    
    return static_file("book.db","./",download="book.db")

#生成xls文件并提供下载
@route("/getxls")
def getxls():
    os.system("python newxls.py")
    return static_file("test.xls","./",download="test.xls")

  
#重置数据库
@route("/reset",method="post")
def reset():
    username = request.forms.get("username");
    password = request.forms.get("password");
    if username =='admin' and password =='admin':
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        c.execute("delete from book")
        conn.commit()
        c.close()
        conn.close()
        return '数据库已清空！';

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
def insert():
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

#修改
def update():
    #print "Not changed"
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("update book set lasttime='"+lasttime+"' where code Like "+isbn+"")
    conn.commit()


def find():
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    for isbncode in c.execute('select code from book'):
        print isbncode

#@route('/hello/:name')
#def index(name='World'):
#    return '<b>Hello %s!</b>' % name


#默认端口  run(host='localhost', port=8080)
run(host='192.168.1.103', port=8080)
